#!/bin/python

import sys
import select
import hashlib
import signal
import socket

IP = "127.0.0.1"
PORT = int(sys.argv[1])
FORMAT = "utf-8"

# Use this variable for your loop
daemon_quit = False


# Handles message receiving
def receive_message(client_socket):
    message = client_socket.recv(1024).decode(FORMAT).strip()
    if not message:
        return False
    # Return an object of message header and message data
    return message



def username_wrapper(username):
    #     this function wrap the username in format: USERNAME@ADDRESS:PORT, if username
    #     is invalid, will return false
    if "@" in username or ":" in username:
        return False
    else:
        return username + "@" + IP + ":" + str(PORT)


def channel_wrapper(channel):
    # this function return the channel in format: CHANNEL@ADDRESS:PORT, if CHANNEL
    # is invalid, will return false
    if "@" in channel or ":" in channel:
        return False
    else:
        return channel + ":" + IP + ":" + str(PORT)


def username_read(username_with_port):
    #     this function return the username in format: USERNAME@ADDRESS:PORT, if username
    #     address and port is not in local server, will return false

    if username_with_port.partition("@")[2].split(":")[0] == IP and \
            int(username_with_port.partition("@")[2].split(":")[1]) == PORT:
        return username_with_port.partition("@")[0]
    else:
        return False


def channel_read(channel_with_port):
    #     this function return the username in format: USERNAME@ADDRESS:PORT, if username
    #     address and port is not in local server, will return false
    if channel_with_port.split(":")[1] == IP and \
            int(channel_with_port.split(":")[2]) == PORT:
        return channel_with_port.split(":")[0]
    else:
        return False


def login_command(client, message_split):
    username = username_wrapper(message_split[1])
    password = message_split[2].encode('utf-8')
    password_hash = (hashlib.sha256(password)).hexdigest()
    if username not in track_detail.keys() or username in clients.values() or client in \
            clients.keys():
        # if username has not been registered or username
        return 0
    else:
        hexed = (track_detail.get(username)).hexdigest()
        if hexed == password_hash:
            clients[client] = username
            return 1
        else:
            return 0


def register_command(message_split):
    username = username_wrapper(message_split[1])
    password = message_split[2].encode('utf-8')
    password_hash = hashlib.sha256(password)
    if username in track_detail.keys():
        return 0
    else:
        track_detail[username] = password_hash
        return 1


def create_channel(client_socket, message_split):
    if client_socket in clients.keys():
        # if socket has a username logged in
        channel_name = channel_wrapper(message_split[1])
        if channel_name in channel_detail.keys():
            return 0
        else:
            channel_detail[channel_name] = []
            return 1
    else:
        return 0


def join_channel(client_socket, message_split):
    channel_name = message_split[1]
    if ":" not in (message_split[1]):
        channel_name = channel_wrapper(message_split[1])

    if client_socket in clients.keys() and channel_name in channel_detail.keys():
        #         if user already in the channel
        if not channel_read(channel_name):
            # join outside channel
            fed_join(UDP_server.getsockname(), clients.get(client_socket), channel_name)
            # switch to UDP listener, it needs confirmation from all other federate
            # channel to
            return None

        else:
            if clients.get(client_socket) not in channel_detail[channel_name]:
                channel_detail[channel_name].append(clients.get(client_socket))
                return 1
            else:
                return 0
    else:
        return 0


def say_msg(client_socket, message_split):
    channel_name, final_msg = message_split[1], \
                              " ".join(message_split[2:])
    if ":" not in channel_name:
        channel_name = channel_wrapper(channel_name)

    if client_socket in clients.keys() and channel_name in channel_detail.keys():
        # if client is a valid client (local), and channel is valid
        if not channel_read(channel_name):
            # if channel is outside
            fed_say(UDP_server.getsockname(), clients.get(client_socket), channel_name,
                    final_msg)
            return

        elif clients.get(client_socket) in channel_detail[channel_name]:
            #         if user already in the channel
            for each_name in channel_detail[channel_name]:
                if not username_read(each_name):
                    #     if user_name is not in local channel
                    fed_recv(UDP_server.getsockname(), each_name, clients.get(
                        client_socket), channel_name, final_msg)
                else:
                    each_socket = list(clients.keys())[list(clients.values()).index(
                        each_name)]

                    sent_msg = f"RECV " \
                               f"{username_read(clients.get(client_socket))} " \
                               f"{channel_read(channel_name)}" \
                               f" {final_msg}\n"
                    each_socket.send(sent_msg.encode(FORMAT))
            return

    err_msg = "User has not logged in or not joined channel\n"
    client_socket.send(err_msg.encode(FORMAT))


def channels(client_socket):
    display_channels = ""
    sorted_list = sorted(channel_detail.keys())
    i = 1
    length = len(channel_detail.keys())
    if length != 0:
        for channel_name in sorted_list:
            if not channel_read(channel_name):
                display_name = channel_name
            else:
                display_name = channel_read(channel_name)
            if i == length:
                display_channels += display_name
            else:
                display_channels += f"{display_name}, "
            i += 1

    msg = f"RESULT CHANNELS {display_channels}\n"
    client_socket.send(msg.encode(FORMAT))


# Do not modify or remove this handler
def quit_gracefully(signum, frame):
    global daemon_quit
    daemon_quit = True


def federation_connect():
    server_list = []
    if len(sys.argv) > 2:
        f = open(sys.argv[2], "r").readlines()
        for server in f:
            server_list.append(server.strip().split(":"))
        msg = "FEDERATE-OUT".encode(FORMAT)

        for server in server_list:
            UDP_server.sendto(msg, (server[0], int(server[1])))


def fed_channels_broadcast():
    channel_list = "FEDCHANNELS " + ",".join(channel_detail.keys())
    for server in federate_server:
        msg = channel_list.encode(
            FORMAT)
        UDP_server.sendto(msg, server)


def fed_new(channel):
    msg = f"FEDNEW {channel}".encode(FORMAT)
    for server in federate_server:
        UDP_server.sendto(msg, server)


def fed_join(address, username, channel_name):
    msg = f"FEDJOIN {username} {channel_name}".encode(FORMAT)
    for server in federate_server:
        if address != server:
            UDP_server.sendto(msg, server)


def fed_say(address, username, channel_name, msg):
    broadcast = f"FEDSAY {username} {channel_name} {msg}".encode(FORMAT)
    for server in federate_server:
        if address != server:
            # it will not broadcast to the message sender
            UDP_server.sendto(broadcast, server)

def channel_broadcast(user_name, channel_name, final_message, message_split):
    try:
        if user_name in channel_detail[channel_name]:
            for each_name in channel_detail[channel_name]:
                if not username_read(each_name):
                    #     if the receiver is not local user
                    fed_recv(UDP_server.getsockname(), each_name,
                             user_name,
                             channel_name,
                             final_message)
                else:
                    # if the receiver is a local user
                    each_socket = list(clients.keys())[
                        list(clients.values()).index(
                            each_name)]
                    tmp_list.append(each_socket)
                    each_socket.send(f"RECV {user_name} "
                                     f"{channel_read(channel_name)} "
                                     f"{final_message}\n".encode(FORMAT))
                    tmp_list.pop()
        else:
            # if the user is not in the chanel
            fed_result(UDP_server.getsockname(), user_name,
                       f"user had not joined {message_split[2]}", 0)
    except ValueError:
        pass

def fed_recv(address, to_username, from_username, channel_name, msg):
    broadcast = f"FEDRECV {to_username} {from_username} {channel_name} {msg}".encode(
        FORMAT)
    for server in federate_server:
        if address != server:
            UDP_server.sendto(broadcast, server)


def fed_result(address, user_name, result_type, result):
    msg = f"FEDRESULT {user_name} {result_type} {result}".encode(FORMAT)
    for server in federate_server:
        if address != server:
            UDP_server.sendto(msg, server)


def fed_command(address, message):
    message_split = message.split(" ")
    if address not in federate_server:
        federate_server.append(address)
        msg = "FEDERATE-CONFIRM".encode(FORMAT)
        UDP_server.sendto(msg, (address[0], address[1]))

    if message_split[0] == "FEDERATE-CONFIRM":
        fed_channels_broadcast()

    elif message_split[0] == "FEDCHANNELS":
        new_channels = False
        if len(message_split) > 1:
            all_channel = message_split[1].split(",")
            if all_channel[-1] == "":
                all_channel.pop()
            for channel in all_channel:

                if channel not in channel_detail.keys():
                    new_channels = True
                    channel_detail[channel] = []
            if new_channels:
                fed_channels_broadcast()

    elif message_split[0] == "FEDNEW":
        channel_name = message_split[1]
        if channel_name not in channel_detail.keys():
            channel_detail[channel_name] = []
            # create a shadow channel in local user
            fed_new(channel_name)
            # because user fednew is always allowed

            # fed_result(address, None, f"RESULT CREATE {channel_name}", 1)

    elif message_split[0] == "FEDJOIN":
        user_name, channel_name = message_split[1], message_split[2]
        if not channel_read(channel_name):
            # if it is not a local channel, forward the message to all its
            # federate server, except for its sender
            fed_join(address, user_name, channel_name)
        else:
            # if the channel is a local channel
            if user_name not in channel_detail[channel_name]:
                channel_detail[channel_name].append(user_name)
                fed_result(UDP_server.getsockname(), user_name,
                           f"JOIN {channel_name}", 1)
            #     result give 1
            else:
                fed_result(UDP_server.getsockname(), user_name,
                           f"JOIN {channel_name}", 0)
                #     result give 0

    elif message_split[0] == "FEDSAY":
        user_name, channel_name, final_message = message_split[1], \
                                                 message_split[2], \
                                                 " ".join(message_split[3:])

        if not channel_read(channel_name):
            # if it is not a local channel, forward the message to all its
            # federate server
            fed_say(address, user_name, channel_name, final_message)

        else:
            #         if the channel is a local channel
            channel_broadcast(user_name, channel_name, final_message, message_split)

    elif message_split[0] == "FEDRECV":
        to_user_name, from_user_name, channel_name, final_message = \
            message_split[1], message_split[2], message_split[3], \
            " ".join(message_split[4:])

        if not username_read(to_user_name):
            # if receiver is not local user in this server, pass out the msg
            fed_recv(address, to_user_name, from_user_name,
                     channel_name, final_message)

        else:
            # if the TOUSER is in local user list
            recipient_socket = list(clients.keys())[
                list(clients.values()).index(
                    to_user_name)]
            if username_read(from_user_name):
                from_user_name = username_read(from_user_name)
            if channel_read(channel_name):
                channel_read(channel_name)

            sent_msg = f"RECV " \
                       f"{from_user_name} " \
                       f"{channel_name}" \
                       f" {final_message}\n"
            recipient_socket.send(sent_msg.encode(FORMAT))

    elif message_split[0] == "FEDRESULT":
        user_name = message_split[1]
        result_type = " ".join(message_split[2:-1])
        result = message_split[-1]
        if not username_read(user_name):
            fed_result(address, user_name, result_type, result)

        else:
            # if user is in local server
            recipient_socket = list(clients.keys())[list(clients.values()).index(
                user_name)]
            msg = f"RESULT {result_type} {result}\n"
            tmp_list.append(recipient_socket)
            recipient_socket.send(msg.encode(FORMAT))
            tmp_list.pop()


def local_command(message, notified_socket):
    message_split = message.split(" ")

    # Receive message
    if message_split[0] == "LOGIN":
        result = login_command(notified_socket, message_split)
        msg = f"RESULT LOGIN {result}\n".encode(FORMAT)
        notified_socket.send(msg)

    elif message_split[0] == "REGISTER":
        result = register_command(message_split)
        msg = f"RESULT REGISTER {result}\n".encode(FORMAT)
        notified_socket.send(msg)

    elif message_split[0] == "CREATE":
        result = create_channel(notified_socket, message_split)
        msg = f"RESULT CREATE {message_split[1]} {result}\n".encode(FORMAT)
        notified_socket.send(msg)

        fed_new(channel_wrapper(message_split[1]))

    elif message_split[0] == "JOIN":
        result = join_channel(notified_socket, message_split)
        if result is not None:
            msg = f"RESULT JOIN {message_split[1]} {result}\n".encode(FORMAT)
            notified_socket.send(msg)

    elif message_split[0] == "SAY":
        say_msg(notified_socket, message_split)

    elif message_split[0] == "CHANNELS":
        channels(notified_socket)

    else:
        msg = (f"Error Command {' '.join(message_split[0:])}\n").encode(
            FORMAT)
        notified_socket.send(msg)


def run():
    # Do not modify or remove this function call

    signal.signal(signal.SIGINT, quit_gracefully)

    read_sockets, write_list, exception_sockets = select.select(sockets_list, tmp_list,
                                                                sockets_list, 1)
    # Iterate over notified sockets
    for notified_socket in read_sockets:

        # If notified socket is a server socket - new connection, accept it
        if notified_socket == TCP_server:

            client_socket, client_address = TCP_server.accept()
            # Add accepted socket to select.select() list
            sockets_list.append(client_socket)

            # print('Accepted new connection from {}:{}, username: unknown'.format(
            # *client_address))

        # Else existing socket is sending a message
        elif notified_socket == UDP_server:

            data, address = UDP_server.recvfrom(1024)
            message = data.decode(FORMAT)
            fed_command(address, message)

        else:
            message = receive_message(notified_socket)
            if message is False:
                # Remove from list for socket.socket()
                sockets_list.remove(notified_socket)
                if notified_socket in clients:
                    del clients[notified_socket]
                # Remove from our list of users
                continue
            local_command(message, notified_socket)

    for notified_socket in exception_sockets:
        if notified_socket in clients:
            # Remove from our list of users
            del clients[notified_socket]


if __name__ == '__main__':

    TCP_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    TCP_server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    TCP_server.bind((IP, PORT))
    UDP_server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    UDP_server.bind((IP, PORT))
    federation_connect()

    # This makes server listen to new connections
    TCP_server.listen()
    # List of sockets for select.select()
    federate_server = []
    sockets_list = [TCP_server, UDP_server]
    tmp_list = []
    # List of connected clients - socket as a key, user header and name as data
    clients = {}
    # clients as key, username as value, username@IP:port
    track_detail = {}
    # username as key, password as value
    channel_detail = {}
    # channel as key, clients as value, channel:IP:port
    # print(f'Listening for connections on {IP}:{PORT}...')
    while not daemon_quit:
        run()
    TCP_server.close()
    UDP_server.close()
    sys.exit()

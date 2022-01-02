import socket
import select
import errno
import sys

IP = "127.0.0.1"
PORT = int(sys.argv[1])
FORMAT = "utf-8"

# Create a socket
# socket.AF_INET - address family, IPv4, some otehr possible are AF_INET6, AF_BLUETOOTH, AF_UNIX
# socket.SOCK_STREAM - TCP, conection-based, socket.SOCK_DGRAM - UDP, connectionless, datagrams, socket.SOCK_RAW - raw IP packets
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect to a given ip and port
client_socket.connect((IP, PORT))

# Set connection to non-blocking state, so .recv() call won't block, just return some exception we'll handle
client_socket.setblocking(False)

# We need to encode username to bytes, then count number of bytes and prepare header of fixed size, that we encode to bytes as well

while True:
    i, o, e = select.select([sys.stdin], [], [], 0)
    if i:
        # if there is something in input
        msg = sys.stdin.readline().strip()
        # send the msg to the server
        if msg == "LOGOUT":
            client_socket.close()
            sys.exit()
        else:
            client_socket.send(msg.encode(FORMAT))
    else:
        try:
            message = client_socket.recv(1024).decode(FORMAT)

            if message:
                print(message, end= '')
                # print message
            else:
                sys.exit()
        except IOError as e:
            # This is normal on non blocking connections - when there are no incoming data error is going to be raised
            # Some operating systems will indicate that using AGAIN, and some using WOULDBLOCK error code
            # We are going to check for both - if one of them - that's expected, means no incoming data, continue as normal
            # If we got different error code - something happened
            if e.errno != errno.EAGAIN and e.errno != errno.EWOULDBLOCK:
                print('Reading error: {}'.format(str(e)))
                sys.exit()

            # We just did not receive anything
            continue

        except Exception as e:
            # Any other exception - something happened, exit
            print('Reading error: '.format(str(e)))
            sys.exit()
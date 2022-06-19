from website import create_app, socketio

app = create_app()
context = ('webChat2222.crt','webChat2222.key')
if __name__ == '__main__':
    # dbug = True will restart the server everytime we make changes
    # socketio.run(app, debug=True, keyfile='webChat2222.key',certfile='webChat2222.crt')
    # host and port remian default if delete (port 5000)
    socketio.run(app, host="127.0.0.1", port=8000,  debug=True)
    # change host to local IPV4 address to run it in local network,
    # socketio.run(app, host="192.168.0.75", port=8000, debug=True,
    #              keyfile='webChat2222.key',
    #              certfile='webChat2222.crt')

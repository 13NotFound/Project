from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from os import path
from flask_login import LoginManager
from flask_socketio import SocketIO, send, emit


db = SQLAlchemy()
DB_NAME = "database.db"
socketio = SocketIO()

def create_app():
    app = Flask(__name__)
    # suppress FSADeprecationWarning
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    # need to figure out
    app.config['SECRET_KEY'] = 'hjshjhdjah kjshkjdhjs'
    # saying that my SQL database is stored under this name sqlite:///{DB_NAME}
    app.config['SQLALCHEMY_DATABASE_URI'] = f'sqlite:///{DB_NAME}'
    # tell database that the app is what we are going to use
    db.init_app(app)
    # initialise the socket
    socketio.init_app(app)

    from .chats import chats
    from .auth import auth
    from .discussion import discussion
    from .resource import resource

    app.register_blueprint(auth, url_prefix='/')
    app.register_blueprint(chats, url_prefix='/')
    app.register_blueprint(discussion, url_prefix='/')
    app.register_blueprint(resource, url_prefix='/')

    from .models import User, Message
    # we load this only because we want to make sure User and Note been ran

    create_database(app)

    # if the user closes the browser tab, log out the user
    login_manager = LoginManager()
    login_manager.login_view = 'auth.login'
    login_manager.init_app(app)

    @login_manager.user_loader
    def load_user(id):
        return User.query.get(int(id))

    return app


def create_database(app):
    if not path.exists('website/' + DB_NAME):
        db.create_all(app=app)
        print('Created Database!')


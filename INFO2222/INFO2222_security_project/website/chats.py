from flask import Blueprint, render_template, request, flash, jsonify, url_for
from flask_login import login_required, current_user
from flask_socketio import emit
from werkzeug.utils import redirect

from .models import Message, User
from . import db, socketio
import json

chats = Blueprint('chats', __name__)

@socketio.on('message')
def message(data):
    from .models import Message
    print(data.get('msg'))
    new_msg = Message(owner=data.get('msg').get('owner'),
                      receiver=data.get('msg').get('receiver'),
                      sender=data.get('msg').get('sender'),
                      signature=data.get('msg').get('signature'),
                      message=data.get('msg').get('data'))
    db.session.add(new_msg)
    new_history = Message(owner=data.get('history').get('owner'),
                          sender=data.get('history').get('sender'),
                          receiver=data.get('history').get('receiver'),
                          signature=data.get('history').get('signature'),
                          message=data.get('history').get('data'))
    db.session.add(new_history)
    db.session.commit()
    emit('some-event', 'this is a custom event message')

@socketio.on('friend-request')
def send_back_public_key(data):
    friend = User.query.filter_by(user_name=data.get('friend_name')).first()
    if friend:
        emit('request-confirmed',
             {'friend_name':data.get('friend_name'),
              'friend_public_key':friend.public_key,
             'friend_public_sign_key':friend.public_sign_key} )

    else:

        emit('friend-name-not-exist',{'friend_name':data.get('friend_name')})


@chats.route('/chat_rooms/<name>', methods=['GET','POST'])
def chat_rooms(name):
    if not current_user.is_authenticated:
        flash('You are not logged in yet :( please log in in this page ',
              category='error')
        return redirect(url_for('auth.login'))
    return render_template('chat_rooms.html', current_user=current_user, friend=name)


@chats.route('/', methods=['GET','POST'])
def friend_list():
    if not current_user.is_authenticated:
        flash('You are not logged in yet :( please log in in this page ',
              category='error')
        return redirect(url_for('auth.login'))
    if request.method == 'POST':
        name = request.form.get('friend_name')
        return redirect(url_for('chats.chat_rooms', name=name))
    return render_template('friend_list.html', user=current_user)

@chats.route('/user_list', methods=['GET','POST'])
@login_required
def user_list():
    if current_user.user_role in ["admin","root"]:
        users = User.query.all()
        return render_template('user_list.html',users=users, user=current_user)
    else:
        flash('You are not admin or root, oh you have no right',
              category='error')
        return render_template('friend_list.html', user=current_user)
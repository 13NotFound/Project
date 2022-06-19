from flask import Blueprint, render_template, request, flash, redirect, url_for
from .models import User
from werkzeug.security import generate_password_hash, check_password_hash
from . import db
from flask_login import login_user, login_required, logout_user, current_user
from . import socketio

auth = Blueprint('auth', __name__)

# by default it can only receive get request, now it can receive post request
# get the form, post the result
@auth.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        # request can access the url, all the information the url sends
        user_name = request.form.get('user_name')
        password = request.form.get('password')
        # return the user object of user matching this email
        user = User.query.filter_by(user_name=user_name).first()
        if user:
            if check_password_hash(user.password, password):
                flash('Logged in successfully!', category='success')
                login_user(user, remember=True)
                return redirect(url_for('chats.friend_list'))
            else:
                flash('Incorrect password, try again.', category='error')
        else:
            # same error msg for user name not exists
            flash('Incorrect password, try again.', category='error')
    return render_template("login.html", user=current_user)

@auth.route('/edit_profile', methods=['GET', 'POST'])
@login_required
def profile_page():
    if request.method == 'POST':
        # request can access the url, all the information the url sends
        new_user_name = request.form.get('new_name')
        new_pwd = request.form.get('new_pwd')
        confirm_new_pwd = request.form.get('c_new_pwd')

        # return the user object of user matching this email
        new_user = User.query.filter_by(user_name=new_user_name).first()
        password_msg = password_strength(new_user_name, new_pwd,confirm_new_pwd)

        if not new_user or len(new_pwd) > 1:
            # if the new user name does not exist
            if password_msg is not None:
                db.session.query(User).filter(User.user_name ==
                                             current_user.user_name).update({
                    'user_name': new_user_name, 'password': generate_password_hash(
                password_msg[0],method='pbkdf2:sha256', salt_length=8)})
                db.session.commit()
            else:
                flash('Incorrect password, try again.', category='error')
        else:
            # same error msg for user name not exists
            flash('User namae already exist, try to be creative',
                  category='error')
    return render_template("profile.html", user=current_user)


@auth.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('auth.login'))

def password_strength(user_name, password_1, password_2):
    if password_1 != password_2:
        flash('Check your typing! Passwords don\'t match.', category='error')
    elif user_name == password_1:
        flash('Password can\'t be user name, come on!', category='error')
    elif len(user_name) < 2:
        flash('That can\'t be your name. Relax We won\'t stalk on you (username needs to be at least 2 characters)',
              category='error')
    elif len(password_1) < 7:
        flash('Password needs to be at least 7 characters, nice and long',
              category='error')
    elif len(password_1) >= 7:
        if password_1.lower() == password_1 or password_1.upper() == password_1 or \
                password_1.isalnum() == password_1:
            return (password_1, 'weak')
        elif password_1.lower() == password_1 and password_1.upper() == password_1 or \
                password_1.isalnum() == password_1:
            return (password_1, 'med')

        else:
            return (password_1, 'strong')
    return None


@auth.route('/sign-up', methods=['GET', 'POST'])
def sign_up():
    if request.method == 'POST':
        user_name = request.form.get('user_name')
        password_1 = request.form.get('password_1')
        password_2 = request.form.get('password_2')
        public_key = request.form.get('public_key')
        public_sign_key = request.form.get('public_key')
        user = User.query.filter_by(user_name=user_name).first()
        password_msg = password_strength(user_name, password_1,password_2)
        if user:
            flash('Username already exists, try to be inventive!', category='error')
        elif password_msg is not None:
            if request.form.get('user_role') == "on":
                if user_name == "root":
                    user_role = "root"
                else:
                    user_role = "admin"
            else:
                user_role = "normal"
            # salted hash
            new_user = User(user_name=user_name, password=generate_password_hash(
                password_msg[0],method='pbkdf2:sha256', salt_length=8), public_key =
            public_key, public_sign_key = public_sign_key, user_role=user_role)
            # adds the user to database
            db.session.add(new_user)
            # commit the user
            db.session.commit()
            login_user(new_user, remember=True)
            flash(f'Account created! Your password strength is {password_msg[1]}',
                  category='success')
            return redirect(url_for('chats.friend_list'))

    return render_template("sign_up.html", user=current_user)



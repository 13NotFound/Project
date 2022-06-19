from flask import Blueprint, render_template, request, flash, jsonify, url_for
from flask_login import login_required, current_user
from flask_socketio import emit
from werkzeug.utils import redirect

from .models import User,Post, Comment, Like, Dislike

from . import db
import json

discussion = Blueprint('discussion', __name__)


@discussion.route('/discussion/<post_category>', methods=['GET','POST'])
def home(post_category):
    print("16")
    if not current_user.is_authenticated:
        flash('You are not logged in yet :( please log in in this page ',
              category='error')
        return redirect(url_for('auth.login'))
    post_category= post_category.split("-")

    if current_user.user_role == "normal":
        if len(post_category) == 1:
            posts = Post.query.filter_by(adminOnly=False)
        else:
            posts = Post.query.filter_by(course=post_category[0], topic=post_category[1])

    else:
        if len(post_category) == 1:
            posts = Post.query.all()
        else:
            posts = Post.query.filter_by(course=post_category[0], topic=post_category[1])



    return render_template('discussion.html', user=current_user, posts=posts)

@discussion.route("/create-post", methods=['GET', 'POST'])
@login_required
def create_post():
    if request.method == "POST":
        if not current_user.mute:
            text = json.loads(request.data)["text"]
            anon = json.loads(request.data)["anon"]
            adminOnly = json.loads(request.data)["adminOnly"]
            course = json.loads(request.data)["course"]
            topic = json.loads(request.data)["topic"]

            if len(text) == 0:
                flash('Post cannot be empty, please input something', category='error')
            else:
                if anon == "1":
                    anon = True
                else:
                    anon = False
                if adminOnly == "1":
                    adminOnly = True
                else:
                    adminOnly = False

                post = Post(text=text, author=current_user.id, anon=anon,course=course,
                            topic=topic,adminOnly=adminOnly)
                db.session.add(post)
                db.session.commit()
                flash('Post created!', category='success')
        else:
            flash('Oops you got muted, ask admin for help', category='error')

    return render_template('create_post.html', user=current_user)


@discussion.route("/delete-post/<id>")
@login_required
def delete_post(id):
    post = Post.query.filter_by(id=id).first()

    if not post:
        flash("Post does not exist.", category='error')
    elif current_user.id == post.id \
            or current_user.user_role == "root" \
            or current_user.user_role == "admin":
        db.session.delete(post)
        db.session.commit()
        flash('Post deleted.', category='success')
    else:
        flash('You do not have permission to delete this post.', category='error')

    if current_user.user_role == "normal":
        posts = Post.query.filter_by(adminOnly=False)

    else:
        posts = Post.query.all()
    return render_template('discussion.html', user=current_user, posts=posts)


@discussion.route("/posts/<user_name>")
@login_required
def posts(user_name):
    user = User.query.filter_by(user_name=user_name).first()

    if not user:
        flash('No user with that user_name exists.', category='error')
        return redirect(url_for('discussion.home'))
    if current_user.user_role != "normal":
        posts = Post.query.filter_by(author=user.id, anon=False).all()
    else:
        posts = Post.query.filter_by(author=user.id, anon=False, adminOnly=False).all()

    if current_user.user_role == "normal":
        posts = Post.query.filter_by(adminOnly=False)

    else:
        posts = Post.query.all()

    return render_template("posts.html", user=current_user, posts=posts,
                           user_name=user_name)


@discussion.route("/create-comment/<post_id>", methods=['POST'])
@login_required
def create_comment(post_id):
    text = request.form.get('text')
    if not current_user.mute:
        if not text:
            flash('Comment cannot be empty.', category='error')
        else:
            post = Post.query.filter_by(id=post_id)
            if post:
                comment = Comment(
                    text=text, author=current_user.id, post_id=post_id)
                db.session.add(comment)
                db.session.commit()
            else:
                flash('Post does not exist.', category='error')
    else:
        flash('Oops you got muted, ask admin for help', category='error')
    posts = Post.query.all()

    if current_user.user_role == "normal":
        posts = Post.query.filter_by(adminOnly=False)

    else:
        posts = Post.query.all()

    return render_template('discussion.html', user=current_user, posts=posts)


@discussion.route("/delete-comment/<comment_id>")
@login_required
def delete_comment(comment_id):
    comment = Comment.query.filter_by(id=comment_id).first()
    print(current_user.user_role)
    if not comment:
        flash('Comment does not exist.', category='error')
    elif (current_user.id == comment.author and current_user.id == comment.post.author)\
       or current_user.user_role == "root"\
       or current_user.user_role == "admin":
        db.session.delete(comment)
        db.session.commit()
    else:
        flash('You do not have permission to delete this comment.', category='error')

    if current_user.user_role == "normal":
        posts = Post.query.filter_by(adminOnly=False)

    else:
        posts = Post.query.all()

    return render_template('discussion.html', user=current_user, posts=posts)


@discussion.route("/like-post/<post_id>", methods=['POST'])
@login_required
def like(post_id):
    post = Post.query.filter_by(id=post_id).first()
    like = Like.query.filter_by(
    author=current_user.id, post_id=post_id).first()

    if not post:
        return jsonify({'error': 'Post does not exist.'}, 400)
    elif like:
        db.session.delete(like)
        db.session.commit()
    else:
        like = Like(author=current_user.id, post_id=post_id)
        db.session.add(like)
        db.session.commit()

    return jsonify({"likes": len(post.likes), "liked": current_user.id in map(lambda x: x.author, post.likes)})

@discussion.route("/dislike-post/<post_id>", methods=['POST'])
@login_required
def dislike(post_id):
    print("here")
    post = Post.query.filter_by(id=post_id).first()
    dislike = Dislike.query.filter_by(author=current_user.id, post_id=post_id).first()

    if not post:
        return jsonify({'error': 'Post does not exist.'}, 400)
    elif dislike:
        db.session.delete(dislike)
        db.session.commit()
    else:
        dislike = Dislike(author=current_user.id, post_id=post_id)
        db.session.add(dislike)
        db.session.commit()

    return jsonify({"dislikes": len(post.dislikes), "disliked": current_user.id in map(lambda x: x.author, post.dislikes)})

def mute_power(admin: 'User',user: 'User'):
    # mute power root>admin>normal
    if admin.user_role == "normal":
        return False
    elif admin.user_role == "admin" and user.user_role == "admin":
        return False
    elif user.user_role == "root":
        return False
    else:
        return True

@discussion.route("/mute-user/<user_id>", methods=['POST'])
@login_required
def mute(user_id):
    user = User.query.filter_by(id=user_id).first()

    if mute_power(current_user,user):
        db.session.query(User).filter(User.id == user.id).update({
            'mute': True })
        db.session.commit()
        return jsonify({'error': 'User does not exist.'}, 400)
    else:
        flash('O you do not have the right.', category='error')

@discussion.route("/unmute-user/<user_id>", methods=['POST'])
@login_required
def unmute(user_id):
    user = User.query.filter_by(id=user_id).first()
    if mute_power(current_user,user):
        db.session.query(User).filter(User.id == user.id).update({
            'mute': False, })
        db.session.commit()
        return jsonify({'error': 'User does not exist.'}, 400)
    else:
        flash('O you do not have the right.', category='error')

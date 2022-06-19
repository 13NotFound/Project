from flask import Blueprint, render_template, request, flash, jsonify, url_for
from flask_login import login_required, current_user
from flask_socketio import emit
from werkzeug.utils import redirect

from .models import Message, User
from . import db, socketio
import json

resource = Blueprint('resource', __name__)


@resource.route('/resource', methods=['GET','POST'])
def resource_page():
    if not current_user.is_authenticated:
        flash('You are not logged in yet :( please log in in this page ',
              category='error')
        return redirect(url_for('auth.login'))
    return render_template('resource.html', user=current_user)

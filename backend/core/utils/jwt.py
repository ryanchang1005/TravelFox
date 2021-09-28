

import jwt

from core.settings import JWT_ACCESS_TOKEN_LIFETIME, SECRET_KEY
from core.utils.time import get_timestamp
from users.models import User


def get_jwt_token_for_user(user):
    exp = int(get_timestamp() + JWT_ACCESS_TOKEN_LIFETIME.total_seconds() * 1000)
    data = {
        'user_id': user.pub_id,
        'exp': exp,
    }
    return jwt.encode(data, SECRET_KEY, algorithm='HS256')


def jwt_decode(jwt_token):
    try:
        data = jwt.decode(jwt_token, SECRET_KEY, algorithms=['HS256'])

        # check time expire
        if get_timestamp() > data['exp']:
            return None

        # check user valid
        if User.objects.filter(pub_id=data['user_id']).exists() is False:
            return None
        return data
    except Exception as e:
        return None

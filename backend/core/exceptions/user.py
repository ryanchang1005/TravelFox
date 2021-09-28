from rest_framework import status

from core.exceptions.base import MyException

from django.utils.translation import ugettext_lazy as _


class LoginFailed(MyException):
    http_code = status.HTTP_401_UNAUTHORIZED
    code = 'login_failed'
    msg = _('login_failed')


class InvalidLoginPlatform(MyException):
    http_code = status.HTTP_400_BAD_REQUEST
    code = 'invalid_login_platform'
    msg = _('invalid_login_platform')

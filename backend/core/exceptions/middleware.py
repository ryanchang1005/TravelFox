from rest_framework import status

from .base import MyException

from django.utils.translation import ugettext_lazy as _


class InvalidToken(MyException):
    http_code = status.HTTP_403_FORBIDDEN
    code = 'invalid_token'
    msg = _('invalid_token')


class InvalidUts(MyException):
    http_code = status.HTTP_403_FORBIDDEN
    code = 'invalid_uts'
    msg = _('invalid_uts')


class ExceedRateLimit(MyException):
    http_code = status.HTTP_429_TOO_MANY_REQUESTS
    code = 'exceed_rate_limit'
    msg = _('exceed_rate_limit')


class InvalidSignature(MyException):
    http_code = status.HTTP_403_FORBIDDEN
    code = 'invalid_signature'
    msg = _('invalid_signature')


class InvalidIP(MyException):
    http_code = status.HTTP_403_FORBIDDEN
    code = 'invalid_ip'
    msg = _('invalid_ip')

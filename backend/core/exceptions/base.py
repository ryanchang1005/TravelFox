from rest_framework import status

from django.utils.translation import ugettext_lazy as _


class MyException(Exception):
    # default is 400
    http_code = status.HTTP_400_BAD_REQUEST

    @property
    def code(self):
        raise NotImplementedError

    @property
    def msg(self):
        raise NotImplementedError

    def __init__(self, remark=None):
        if remark and hasattr(self, 'msg'):
            self.msg = f'{self.msg}({remark})'


class NotFound(MyException):
    http_code = status.HTTP_404_NOT_FOUND
    code = 'not_found'
    msg = _('not_found')


class InvalidValue(MyException):
    http_code = status.HTTP_400_BAD_REQUEST
    code = 'invalid_value'
    msg = _('invalid_value')

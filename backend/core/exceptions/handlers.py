from django.core.exceptions import PermissionDenied
from django.http import Http404
from rest_framework import exceptions
from rest_framework.response import Response

from core.exceptions.base import MyException


def my_exception_handler(exc, context):
    if isinstance(exc, MyException):
        return Response(
            data={
                'code': exc.code,
                'message': exc.msg
            },
            status=exc.http_code
        )
    elif isinstance(exc, Http404):
        exc = exceptions.NotFound()
    elif isinstance(exc, PermissionDenied):
        exc = exceptions.PermissionDenied()

    if isinstance(exc, exceptions.APIException):
        if isinstance(exc.detail, dict):
            key = list(exc.detail.keys())[0]
            data = {
                'code': 'field error',
                'message': f'[{key}], {str(exc.detail[key][0])}'
            }
        else:
            data = {
                'code': exc.detail.code,
                'message': exc.detail
            }
        return Response(
            data,
            status=exc.status_code
        )
    return None

from functools import wraps

from django.contrib import messages
from django.shortcuts import redirect
from django.urls import reverse

from citadel.exceptions import CitadelException


def citadel_exception_handler(redirect_to):
    """
    citadel相關錯誤捕捉處理
    發生錯誤 > messages > redirect('redirect_to')
    """

    def decorator(func):
        @wraps(func)
        def inner(request, *args, **kwargs):
            try:
                return func(request, *args, **kwargs)
            except CitadelException as e:
                msg = e.args[0]
                messages.error(request, msg)
                return redirect(reverse(redirect_to))

        return inner

    return decorator

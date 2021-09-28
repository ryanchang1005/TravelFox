import logging

from django.contrib import auth
from django.contrib.auth.decorators import login_required
from django.shortcuts import redirect, render
from django.urls import reverse

from citadel.decorators import citadel_exception_handler
from citadel.serializers.common import LoginSerializer
from core.utils.mfa import is_mfa_code_ok

logger = logging.getLogger(__name__)


@citadel_exception_handler('citadel:login')
def login_view(request):
    if request.method == 'GET':
        return render(request, 'citadel/common/login.html')
    else:
        serializer = LoginSerializer(data=request.POST)
        serializer.is_valid()
        data = serializer.validated_data
        username = data['username']
        password = data['password']
        mfa_code = data['mfa_code']

        user = auth.authenticate(request, username=username, password=password)

        if user is None:
            print('user is None')
            return redirect(reverse('citadel:login'))

        if user.is_superuser is False:
            print('user.is_superuser is False')
            return redirect(reverse('citadel:login'))

        if user.mfa_secret_key is None:
            print('user.mfa_secret_key is None')
            return redirect(reverse('citadel:login'))

        if is_mfa_code_ok(user.get_mfa_secret_key(), mfa_code) is False:
            print('is_mfa_code_ok(user.get_mfa_secret_key(), mfa_code) is False')
            return redirect(reverse('citadel:login'))

        # 登入
        auth.login(request, user)
        return redirect(reverse('citadel:dashboard'))


@login_required
@citadel_exception_handler('citadel:login')
def logout_view(request):
    auth.logout(request)
    return redirect(reverse('citadel:login'))


@citadel_exception_handler('citadel:login')
def router_view(request):
    # 已登入
    if request.user.is_authenticated:
        return redirect(reverse('citadel:dashboard'))
    return redirect(reverse('citadel:login'))

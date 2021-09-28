from django.contrib.auth.models import AbstractUser
from django.db import models
from django.utils.translation import ugettext_lazy as _

from core.models import AutoPubIDField
from core.utils.rsa import *


class User(AbstractUser):
    # 公開ID
    pub_id = AutoPubIDField(
        _('pub_id')
    )

    # 登入平台
    LOGIN_PLATFORM_GOOGLE = 'GOOGLE'
    LOGIN_PLATFORM_FB = 'FACEBOOK'
    LOGIN_PLATFORM_APPLE = 'APPLE'
    LOGIN_PLATFORM_CHOICE = (
        (LOGIN_PLATFORM_GOOGLE, _('google')),
        (LOGIN_PLATFORM_FB, _('fb')),
        (LOGIN_PLATFORM_APPLE, _('apple')),
    )
    login_platform = models.CharField(
        _('login_platform'),
        max_length=32
    )

    # 頭像URL
    avatar_url = models.CharField(
        _('avatar_url'),
        max_length=256,
        null=True,
    )

    # 顯示用名稱
    display_name = models.CharField(
        _('display_name'),
        max_length=128,
        null=True,
    )

    # MFA多重驗證 secret key
    mfa_secret_key = models.CharField(
        _('mfa_secret_key'),
        max_length=512,
        null=True,
    )

    def set_mfa_secret_key(self, value):
        self.mfa_secret_key = en(server_sign_pub_key, value)

    def get_mfa_secret_key(self):
        return de(server_sign_pri_key, self.mfa_secret_key)

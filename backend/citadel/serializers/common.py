from rest_framework import serializers

from citadel.exceptions import CitadelException
from citadel.serializers.base import BaseSerializer


class LoginSerializer(BaseSerializer):
    username = serializers.CharField(allow_blank=True)
    password = serializers.CharField(allow_blank=True)
    mfa_code = serializers.CharField(allow_blank=True)

    def validate_username(self, value):
        if self.is_empty(value):
            raise CitadelException('username is_empty')
        return value

    def validate_password(self, value):
        if self.is_empty(value):
            raise CitadelException('password is_empty')
        return value

    def validate_mfa_code(self, value):
        if self.is_empty(value):
            raise CitadelException('password is_empty')
        return value

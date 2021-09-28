import secrets

from rest_framework import serializers

from core.exceptions.user import LoginFailed
from core.services.google_auth import google_sign_in_verify
from core.utils.jwt import get_jwt_token_for_user
from core.utils.time import get_now_datetime_obj, to_iso8601_utc_string
from users.models import User


class UserSimpleDisplaySerializer(serializers.Serializer):
    def to_representation(self, instance):
        return {
            'id': instance.pub_id,
            'name': instance.display_name,
            'avatar_url': instance.avatar_url,
        }


class UserDetailSerializer(serializers.Serializer):

    def to_representation(self, instance):
        user = instance
        return {
            'user_id': user.pub_id,
            'display_name': user.display_name,
            'login_platform': user.login_platform,
            'avatar_url': user.avatar_url,
        }


class LoginSerializer(serializers.Serializer):
    login_platform = serializers.CharField()
    token = serializers.CharField()

    def create(self, validated_data):
        if validated_data['login_platform'].upper() == User.LOGIN_PLATFORM_GOOGLE:  # Google Sign in
            # Ask google token is valid and return some information
            google_sign_in_rsp = google_sign_in_verify(validated_data['token'])
            email = google_sign_in_rsp.get('email')
            picture = google_sign_in_rsp.get('picture')
            name = google_sign_in_rsp.get('name')
            login_platform = User.LOGIN_PLATFORM_GOOGLE

            # Check user exists
            user = User.objects.filter(login_platform=login_platform, email=email).first()
            if user is None:
                # Not exists, create one
                user = User.objects.create_user(username=secrets.token_urlsafe(10), password=secrets.token_urlsafe(10))
                user.email = email
                user.login_platform = login_platform
                user.avatar_url = picture
                user.display_name = name
                user.last_login = get_now_datetime_obj()
                user.save(update_fields=['email', 'login_platform', 'avatar_url', 'display_name', 'last_login'])
            else:
                # Only change login time
                user.last_login = get_now_datetime_obj()
                user.save(update_fields=['last_login'])
        else:
            raise LoginFailed

        return user

    def to_representation(self, instance):
        return {
            'user_id': instance.pub_id,
            'access_token': get_jwt_token_for_user(instance),
        }

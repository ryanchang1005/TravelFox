import json
import secrets

from django.test import TestCase

from core.services.init import *
from core.tests.clients import ApiClient
from core.utils.jwt import get_jwt_token_for_user
from core.utils.time import get_now_datetime_obj
from users.models import User


class BaseAPITestCase(TestCase):

    @classmethod
    def setUpClass(cls):
        super().setUpClass()

        # Run some initial script
        create_default_place_tags()
        create_default_place()

        # Create testing user
        cls.api_client_user = user = cls.create_user_for_testing()
        access_token = get_jwt_token_for_user(user)
        cls.api_client = ApiClient(user_id=user.pub_id, jwt=access_token)

    @classmethod
    def tearDownClass(cls):
        super().tearDownClass()

    @classmethod
    def create_user_for_testing(cls):
        user = User.objects.create_user(username=secrets.token_urlsafe(10), password=secrets.token_urlsafe(10))
        name = secrets.token_urlsafe(10)
        user.email = '{}@example.com'.format(name)
        user.login_platform = User.LOGIN_PLATFORM_GOOGLE
        user.display_name = name
        user.last_login = get_now_datetime_obj()
        user.save(update_fields=['email', 'login_platform', 'display_name', 'last_login'])
        return user


class MockResponse:
    def __init__(self, json_data, status_code):
        self.json_data = json_data
        self.status_code = status_code
        self.content = json.dumps(json_data).encode()

    def json(self):
        return self.json_data

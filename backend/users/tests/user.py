import secrets
from unittest.mock import patch

from django.test import TestCase
from core.tests.clients import ApiClient
from travels.tests import BaseTravelTestCase
from users.models import User


class UserLoginTestCase(TestCase):

    @patch('users.serializers.user.google_sign_in_verify')
    def test_login_success(self, mock_google_sign_in_verify):
        # mock
        google_sign_in_return_data = {
            'name': 'ryan',
            'email': 'ryan@example.com',
            'picture': 'https://avatars.githubusercontent.com/u/15250400?v=4',
        }
        mock_google_sign_in_verify.side_effect = lambda token: google_sign_in_return_data

        # ready
        login_platform = User.LOGIN_PLATFORM_GOOGLE
        token = secrets.token_hex(20)
        data = {
            'login_platform': login_platform,
            'token': token,
        }
        rsp = ApiClient().post('/api/user/login/', data=data)
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('user_id' in return_data)
        self.assertTrue('access_token' in return_data)

        # assert data valid
        user = User.objects.get(pub_id=return_data['user_id'])
        self.assertIsNotNone(user)  # User exist
        self.assertEqual(user.login_platform, login_platform)
        self.assertEqual(user.display_name, google_sign_in_return_data['name'])
        self.assertEqual(user.email, google_sign_in_return_data['email'])
        self.assertEqual(user.avatar_url, google_sign_in_return_data['picture'])


class GetUserDetailTestCase(BaseTravelTestCase):

    def test_get_detail(self):
        # execute
        rsp = self.api_client.get('/api/user/{}/'.format(self.api_client_user.pub_id))
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('user_id' in return_data)
        self.assertTrue('display_name' in return_data)
        self.assertTrue('login_platform' in return_data)
        self.assertTrue('avatar_url' in return_data)

        # assert data valid
        self.assertEqual(self.api_client_user.pub_id, return_data['user_id'])
        self.assertEqual(self.api_client_user.display_name, return_data['display_name'])
        self.assertEqual(self.api_client_user.login_platform, return_data['login_platform'])
        self.assertEqual(self.api_client_user.avatar_url, return_data['avatar_url'])

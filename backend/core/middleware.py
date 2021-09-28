from django.http import JsonResponse
from django.utils.deprecation import MiddlewareMixin
from rest_framework import status

from core.services.rate_limit import touch_by_token_rate_limit_middleware
from core.settings import ADMIN_URL
from core.utils.hash import sha256
from core.utils.jwt import jwt_decode
from users.models import User

InvalidUTSResponse = JsonResponse(
    data={'code': 'invalid_uts', 'message': 'invalid_uts'},
    status=status.HTTP_400_BAD_REQUEST
)
InvalidTokenResponse = JsonResponse(
    data={'code': 'invalid_token', 'message': 'invalid_token'},
    status=status.HTTP_401_UNAUTHORIZED
)
InvalidTokenRateLimitResponse = JsonResponse(
    data={'code': 'invalid_token_rate_limit', 'message': 'invalid_token_rate_limit'},
    status=status.HTTP_429_TOO_MANY_REQUESTS
)


def is_skip(request):
    if request.path.startswith('/api/user/login'):
        return True
    if request.path.startswith('/api/schedule/'):
        return True
    if request.path.startswith(f'/{ADMIN_URL}'):
        return True
    return False


class CheckUTSMiddleware(MiddlewareMixin):

    def process_request(self, request):
        if is_skip(request) is True:
            return

        try:
            uts = request.META['HTTP_X_TP_UTS']
            length = len(uts)
            if length != 13:
                raise Exception('length != 13, {}'.format(length))
        except Exception:
            return InvalidUTSResponse


class CheckJWTMiddleware(MiddlewareMixin):

    def process_request(self, request):
        if is_skip(request) is True:
            return

        try:
            token = request.META['HTTP_X_TP_TOKEN'].split()[1]
            data = jwt_decode(token)
            request.user_object = User.objects.get(pub_id=data['user_id'])
            request.token = token
        except Exception as e:
            return InvalidTokenResponse


class TokenRateLimitMiddleware(MiddlewareMixin):

    def process_request(self, request):
        if is_skip(request) is True:
            return

        # 檢查token
        if not hasattr(request, 'token') or request.token is None:
            return InvalidTokenResponse

        try:
            if not touch_by_token_rate_limit_middleware(sha256(request.token)):
                return InvalidTokenRateLimitResponse
        except Exception:
            return InvalidTokenResponse


class LogMiddleware(MiddlewareMixin):

    def process_request(self, request):
        pass

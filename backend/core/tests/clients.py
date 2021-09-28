
from django.test import Client

from core.utils.time import get_timestamp


class ApiClient(Client):

    def __init__(self, user_id=None, jwt=None, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.user_id = user_id
        self.jwt = jwt

    def _update_headers(self, kwargs):
        kwargs.update({
            'content_type': 'application/json',
            'HTTP_X_TP_UTS': str(get_timestamp()),
        })

        if self.jwt:
            kwargs.update({
                'HTTP_X_TP_TOKEN': f'Bearer {self.jwt}'
            })

    def get(self, *args, **kwargs):
        self._update_headers(kwargs)
        rsp = super().get(*args, **kwargs)
        print(rsp.content.decode())
        return rsp

    def post(self, *args, **kwargs):
        self._update_headers(kwargs)
        rsp = super().post(*args, **kwargs)
        print(rsp.content.decode())
        return rsp

    def put(self, *args, **kwargs):
        self._update_headers(kwargs)
        rsp = super().put(*args, **kwargs)
        print(rsp.content.decode())
        return rsp

    def patch(self, *args, **kwargs):
        self._update_headers(kwargs)
        rsp = super().patch(*args, **kwargs)
        print(rsp.content.decode())
        return rsp

    def delete(self, *args, **kwargs):
        self._update_headers(kwargs)
        rsp = super().delete(*args, **kwargs)
        print(rsp.content.decode())
        return rsp

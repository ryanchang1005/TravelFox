from django.shortcuts import get_object_or_404
from rest_framework import status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.viewsets import mixins, GenericViewSet

from core.exceptions.base import NotFound
from users.models import User
from users.serializers.user import LoginSerializer, UserDetailSerializer


class UserViewSet(
    mixins.RetrieveModelMixin,
    GenericViewSet
):
    lookup_field = 'pub_id'

    def get_serializer_class(self):
        if self.action == 'retrieve':
            return UserDetailSerializer
        elif self.action == 'login':
            return LoginSerializer

    def get_queryset(self):
        qs = User.objects.all()
        if self.action == 'retrieve':
            return qs.filter(pub_id=self.kwargs['pub_id'])
        raise NotImplementedError

    def retrieve(self, request, *args, **kwargs):
        user = self.get_queryset().first()

        if user.id != request.user_object.id:
            return NotFound

        serializer = self.get_serializer(instance=user)
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    @action(methods=['post'], detail=False, url_path='login')
    def login(self, request):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(data=serializer.data, status=status.HTTP_200_OK)

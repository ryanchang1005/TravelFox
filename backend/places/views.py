from django.contrib.gis.measure import D
from django.db.models import Q
from rest_framework import status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.viewsets import mixins, GenericViewSet

from places.models import Place
from places.serializers.place import PlaceDetailSerializer
from places.services.geo import create_geo_position
from users.models import User
from users.serializers.user import LoginSerializer, UserDetailSerializer


class PlaceViewSet(
    mixins.RetrieveModelMixin,
    mixins.ListModelMixin,
    GenericViewSet
):
    lookup_field = 'pub_id'

    def get_serializer_class(self):
        if self.action == 'retrieve':
            return PlaceDetailSerializer
        raise NotImplementedError

    def get_queryset(self):
        qs = Place.objects.all()
        if self.action == 'retrieve':
            return qs.filter(pub_id=self.kwargs['pub_id'])
        elif self.action == 'list':
            return qs.filter()
        raise NotImplementedError

    def retrieve(self, request, *args, **kwargs):
        plan = self.get_queryset().first()
        serializer = self.get_serializer(instance=plan)
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    def list(self, request, *args, **kwargs):
        keyword = request.GET.get('keyword')
        tag = request.GET.get('tag')
        lat = request.GET.get('lat')
        lng = request.GET.get('lng')
        distance = request.GET.get('distance')  # meter

        qs = self.get_queryset()

        if keyword:
            qs = qs.filter(Q(name__contains=keyword) | Q(address__contains=keyword))

        if tag:
            qs = qs.filter(tags__name=tag)

        if lat and lng and distance:
            qs = qs.filter(position__distance_lte=(create_geo_position(lat, lng), D(m=distance)))

        qs = qs[:50]

        return Response(
            data={'results': PlaceDetailSerializer(instance=qs, many=True).data},
            status=status.HTTP_200_OK
        )

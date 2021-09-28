from rest_framework import status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework.viewsets import mixins, GenericViewSet

from travels.models import Travel, TravelPlace
from travels.serializers.travel import TravelListDisplaySerializer, TravelDetailSerializer, CreateTravelSerializer, \
    CreateTravelPlaceSerializer, UpdateTravelNameSerializer, UpdateTravelTimeSerializer, UpdateTravelRemarksSerializer, \
    UpdateTravelIsPublicSerializer, UpdateTravelPlaceRemarksSerializer, UpdateTravelPlaceExpenseSerializer, \
    UpdateTravelPlaceOrderingSerializer


class TravelViewSet(
    mixins.RetrieveModelMixin,
    mixins.ListModelMixin,
    mixins.CreateModelMixin,
    mixins.DestroyModelMixin,
    GenericViewSet
):
    lookup_field = 'pub_id'

    def get_serializer_class(self):
        if self.action == 'create':
            return CreateTravelSerializer
        elif self.action == 'retrieve':
            return TravelDetailSerializer
        elif self.action == 'list':
            return TravelListDisplaySerializer
        elif self.action == 'public':
            return TravelListDisplaySerializer
        elif self.action == 'create_travel_place':
            return CreateTravelPlaceSerializer
        elif self.action == 'update_travel_place_remarks':
            return UpdateTravelPlaceRemarksSerializer
        elif self.action == 'update_travel_place_expense':
            return UpdateTravelPlaceExpenseSerializer
        elif self.action == 'update_travel_place_ordering':
            return UpdateTravelPlaceOrderingSerializer
        elif self.action == 'update_name':
            return UpdateTravelNameSerializer
        elif self.action == 'update_time':
            return UpdateTravelTimeSerializer
        elif self.action == 'update_remarks':
            return UpdateTravelRemarksSerializer
        elif self.action == 'update_is_public':
            return UpdateTravelIsPublicSerializer

        raise NotImplementedError

    def get_queryset(self):
        if self.action in [
            'retrieve',
            'create_travel_place',
            'update_travel_place_remarks',
            'update_travel_place_expense',
            'update_travel_place_ordering',
            'delete_travel_place',
            'update_name',
            'update_time',
            'update_remarks',
            'update_is_public',
        ]:
            return Travel.objects.filter(pub_id=self.kwargs['pub_id'], creator=self.request.user_object)
        elif self.action == 'list':
            return Travel.objects.filter(creator=self.request.user_object)
        elif self.action == 'public':
            return Travel.objects.filter(is_public=True)

        raise NotImplementedError

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            data=serializer.data,
            status=status.HTTP_201_CREATED
        )

    def retrieve(self, request, *args, **kwargs):
        travel = self.get_queryset().first()
        serializer = self.get_serializer(instance=travel)
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    def list(self, request, *args, **kwargs):
        qs = self.get_queryset()[:10]
        return Response(
            data={'results': self.get_serializer(instance=qs, many=True).data},
            status=status.HTTP_200_OK
        )

    @action(methods=['get'], detail=False, url_path='public')
    def public(self, request):
        qs = self.get_queryset()[:10]
        return Response(
            data={'results': self.get_serializer(instance=qs, many=True).data},
            status=status.HTTP_200_OK
        )

    @action(methods=['post', 'delete'], detail=True, url_path='place')
    def create_travel_place(self, request, pub_id):
        travel = self.get_queryset().first()
        if request.method == 'POST':
            serializer = self.get_serializer(data=request.data, instance=travel)
            serializer.is_valid(raise_exception=True)
            serializer.save()
            return Response(
                data=serializer.data,
                status=status.HTTP_201_CREATED
            )
        elif request.method == 'DELETE':
            place_id = request.GET.get('place_id')
            travel.travelplace_set.filter(place__pub_id=place_id).delete()
            return Response(status=status.HTTP_204_NO_CONTENT)

    @action(methods=['patch'], detail=True, url_path='place_remarks')
    def update_travel_place_remarks(self, request, pub_id):
        travel = self.get_queryset().first()
        serializer = self.get_serializer(data=request.data, instance=travel)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    @action(methods=['patch'], detail=True, url_path='place_expense')
    def update_travel_place_expense(self, request, pub_id):
        travel = self.get_queryset().first()
        serializer = self.get_serializer(data=request.data, instance=travel)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    @action(methods=['patch'], detail=True, url_path='place_ordering')
    def update_travel_place_ordering(self, request, pub_id):
        travel = self.get_queryset().first()
        serializer = self.get_serializer(data=request.data, instance=travel)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    @action(methods=['patch'], detail=True, url_path='name')
    def update_name(self, request, pub_id):
        travel = self.get_queryset().first()
        serializer = self.get_serializer(data=request.data, instance=travel)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    @action(methods=['patch'], detail=True, url_path='time')
    def update_time(self, request, pub_id):
        travel = self.get_queryset().first()
        serializer = self.get_serializer(data=request.data, instance=travel)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    @action(methods=['patch'], detail=True, url_path='remarks')
    def update_remarks(self, request, pub_id):
        travel = self.get_queryset().first()
        serializer = self.get_serializer(data=request.data, instance=travel)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

    @action(methods=['patch'], detail=True, url_path='is_public')
    def update_is_public(self, request, pub_id):
        travel = self.get_queryset().first()
        serializer = self.get_serializer(data=request.data, instance=travel)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            data=serializer.data,
            status=status.HTTP_200_OK
        )

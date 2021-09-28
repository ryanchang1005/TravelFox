from django.urls import path, include

from rest_framework import routers

from core.settings import ADMIN_URL
from places.views import PlaceViewSet
from travels.views import TravelViewSet
from users.views import UserViewSet

api_router = routers.SimpleRouter()
api_router.register('user', UserViewSet, base_name='user')
api_router.register('place', PlaceViewSet, base_name='place')
api_router.register('travel', TravelViewSet, base_name='travel')

urlpatterns = [

    # api
    path('api/', include(api_router.urls)),

    # admin
    path(f'{ADMIN_URL}/', include('citadel.urls')),
]

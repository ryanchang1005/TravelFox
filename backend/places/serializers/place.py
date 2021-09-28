from rest_framework import serializers

from places.services.geo import get_lat_lng


class PlaceDetailSerializer(serializers.Serializer):

    def to_representation(self, instance):
        lat, lng = get_lat_lng(instance.position)
        return {
            'id': instance.pub_id,
            'name': instance.name,
            'lat': lat,
            'lng': lng,
            'address': instance.address,
            'tags': [],
        }

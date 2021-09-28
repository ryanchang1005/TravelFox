from django.db import transaction
from rest_framework import serializers

from citadel.serializers.base import BaseSerializer
from places.models import Place, PlaceTag
from places.services.geo import create_geo_position


class CreatePlaceSerializer(BaseSerializer):
    name = serializers.CharField()
    address = serializers.CharField()
    lat = serializers.CharField()
    lng = serializers.CharField()
    tags = serializers.ListField()
    is_audited = serializers.BooleanField()

    def create(self, validated_data):
        with transaction.atomic():
            place = Place.objects.create(
                name=validated_data['name'],
                address=validated_data['address'],
                position=create_geo_position(validated_data['lat'], validated_data['lng']),
                is_audited=validated_data['is_audited'],
                creator=self.context['request'].user,
            )
            place.tags.all().delete()
            place.tags.add(*list(PlaceTag.objects.filter(id__in=validated_data['tags'])))
            return place


class AuditPlaceSerializer(BaseSerializer):
    audit_ids = serializers.ListField()

    def create(self, validated_data):
        print(validated_data['audit_ids'])
        Place.objects.filter(pub_id__in=validated_data['audit_ids']).update(is_audited=True)
        return Place.objects.get(pub_id=validated_data['audit_ids'][0])


class EditPlaceSerializer(BaseSerializer):
    name = serializers.CharField()
    address = serializers.CharField()
    lat = serializers.CharField()
    lng = serializers.CharField()
    tags = serializers.ListField()
    is_audited = serializers.BooleanField()

    def update(self, instance, validated_data):
        with transaction.atomic():
            instance.name = validated_data['name']
            instance.address = validated_data['address']
            instance.position = create_geo_position(validated_data['lat'], validated_data['lng'])
            instance.is_audited = validated_data['is_audited']
            instance.save(update_fields=['name', 'address', 'position', 'is_audited'])

            instance.tags.remove(*list(instance.tags.all()))
            instance.tags.add(*list(PlaceTag.objects.filter(id__in=validated_data['tags'])))
            return instance

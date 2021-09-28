from rest_framework import serializers

from citadel.serializers.base import BaseSerializer


class EditUserSerializer(BaseSerializer):
    is_active = serializers.BooleanField()

    def update(self, instance, validated_data):
        instance.is_active = validated_data['is_active']
        instance.save(update_fields=['is_active'])
        return instance

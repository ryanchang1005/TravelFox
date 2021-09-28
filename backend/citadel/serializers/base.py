from rest_framework import serializers


class BaseSerializer(serializers.Serializer):

    def is_empty(self, value):
        if isinstance(value, str) and len(value) > 0:
            return False
        return True

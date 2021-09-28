from rest_framework import serializers

from core.utils.time import to_iso8601_utc_string
from places.models import Place
from places.services.geo import get_lat_lng
from places.utils.tags import *
from travels.models import Travel, TravelPlace
from users.serializers.user import UserSimpleDisplaySerializer


class CreateTravelSerializer(serializers.Serializer):
    name = serializers.CharField()
    from_time = serializers.CharField()
    to_time = serializers.CharField()

    def create(self, validated_data):
        user = self.context['request'].user_object
        travel = Travel.objects.create(
            name=validated_data['name'],
            from_time=validated_data['from_time'],
            to_time=validated_data['to_time'],
            creator=user,
        )
        return travel

    def to_representation(self, instance):
        # performance up(related)
        instance = Travel.objects \
            .select_related('creator') \
            .prefetch_related('travelplace_set__creator') \
            .prefetch_related('travelmember_set__member') \
            .get(id=instance.id)

        # TravelMember
        member_list = [{
            'id': travel_member.member.pub_id,
            'name': travel_member.member.display_name,
            'role': travel_member.role,
        } for travel_member in instance.travelmember_set.all()]

        return {
            'id': instance.id,
            'name': instance.name,
            'from_time': to_iso8601_utc_string(instance.from_time),
            'to_time': to_iso8601_utc_string(instance.to_time),
            'creator': UserSimpleDisplaySerializer(instance=instance.creator).data,
            'live_list': [],
            'food_list': [],
            'attraction_list': [],
            'o_mi_ya_ga_list': [],
            'traffic_list': [],
            'member_list': member_list,
            'remarks': instance.remarks,
            'is_public': instance.is_public,
            'is_archived': instance.is_archived,
            'likers': [],
        }


class TravelDetailSerializer(serializers.Serializer):

    def to_representation(self, instance):
        # performance up(related)
        instance = Travel.objects \
            .select_related('creator') \
            .prefetch_related('travelplace_set__creator') \
            .prefetch_related('travelmember_set__member') \
            .get(id=instance.id)

        # TravelPlace
        live_list = []
        o_mi_ya_ga_list = []
        attraction_list = []
        food_list = []
        traffic_list = []
        tag_mapping_list = {
            TAG_LIVE: live_list,
            TAG_O_MI_YA_GA: o_mi_ya_ga_list,
            TAG_ATTRACTION: attraction_list,
            TAG_FOOD: food_list,
            TAG_TRAFFIC: traffic_list,
        }
        for travel_place in instance.travelplace_set.all():
            lat, lng = get_lat_lng(travel_place.place.position)
            tag_mapping_list[travel_place.tag].append({
                'id': travel_place.place.pub_id,
                'name': travel_place.place.name,
                'lat': lat,
                'lng': lng,
                'address': travel_place.place.address,
                'order': travel_place.order,
                'remarks': travel_place.remarks,
                'expense': travel_place.expense,
                'tag': travel_place.tag,
                'creator': UserSimpleDisplaySerializer(instance=travel_place.creator).data,
            })
        # sort
        for v in tag_mapping_list.values():
            v.sort(key=lambda it: it['order'])

        # TravelMember
        member_list = [{
            'id': travel_member.member.pub_id,
            'name': travel_member.member.display_name,
            'role': travel_member.role,
        } for travel_member in instance.travelmember_set.all()]

        return {
            'id': instance.pub_id,
            'name': instance.name,
            'from_time': to_iso8601_utc_string(instance.from_time),
            'to_time': to_iso8601_utc_string(instance.to_time),
            'creator': UserSimpleDisplaySerializer(instance=instance.creator).data,
            'live_list': live_list,
            'food_list': food_list,
            'attraction_list': attraction_list,
            'o_mi_ya_ga_list': o_mi_ya_ga_list,
            'traffic_list': traffic_list,
            'member_list': member_list,
            'remarks': instance.remarks,
            'is_public': instance.is_public,
            'is_archived': instance.is_archived,
            'likers': [],
        }


class TravelListDisplaySerializer(serializers.Serializer):

    def to_representation(self, instance):
        return {
            'id': instance.pub_id,
            'name': instance.name,
            'creator': UserSimpleDisplaySerializer(instance=instance.creator).data,
            'is_liked': False,
            'comment_count': 0,
            'like_count': 0,
        }


class TravelPlaceOrderSerializer(serializers.Serializer):
    id = serializers.CharField()
    order = serializers.IntegerField()


class CreateTravelPlaceSerializer(serializers.Serializer):
    place_id = serializers.CharField()
    tag = serializers.CharField()

    def validate_tag(self, value):
        value = value.lower()
        if value not in [it[0] for it in TAG_CHOICE]:
            raise Exception('Invalid tag, {}'.format(value))
        return value

    def update(self, instance, validated_data):
        # 新增旅程景點
        place = Place.objects.get(pub_id=validated_data['place_id'])
        user = self.context['request'].user_object
        travel_place = TravelPlace.objects.create(
            travel=self.instance,
            place=place,
            order=0,
            tag=validated_data['tag'],
            creator=user,
        )

        # 後推原旅程景點排序(全+1)
        qs = TravelPlace.objects.filter(travel=instance).order_by('order')
        travel_place_list = [it for it in qs]
        travel_place_list.insert(0, travel_place)
        for i in range(len(travel_place_list)):
            travel_place_list[i].order = i
        TravelPlace.objects.bulk_update(travel_place_list, ['order'])

        return instance

    def to_representation(self, instance):
        travel_place = TravelPlace.objects.select_related('place', 'creator').get(
            travel=instance, place__pub_id=self.validated_data['place_id']
        )
        lat, lng = get_lat_lng(travel_place.place.position)
        return {
            'id': travel_place.place.pub_id,
            'name': travel_place.place.name,
            'address': travel_place.place.address,
            'lat': lat,
            'lng': lng,
            'tag': travel_place.tag,
            'order': travel_place.order,
            'remarks': travel_place.remarks,
            'expense': travel_place.expense,
            'creator': UserSimpleDisplaySerializer(instance=travel_place.creator).data,
        }


class UpdateTravelPlaceRemarksSerializer(serializers.Serializer):
    place_id = serializers.CharField()
    remarks = serializers.CharField()

    def update(self, instance, validated_data):
        self.travel_place = TravelPlace.objects.get(
            travel=instance, place__pub_id=validated_data['place_id']
        )
        self.travel_place.remarks = validated_data['remarks']
        self.travel_place.save(update_fields=['remarks'])
        return instance

    def to_representation(self, instance):
        return {
            'remarks': self.travel_place.remarks
        }


class UpdateTravelPlaceExpenseSerializer(serializers.Serializer):
    place_id = serializers.CharField()
    expense = serializers.CharField()

    def update(self, instance, validated_data):
        self.travel_place = TravelPlace.objects.get(
            travel=instance, place__pub_id=validated_data['place_id']
        )
        self.travel_place.expense = int(validated_data['expense'])
        self.travel_place.save(update_fields=['expense'])
        return instance

    def to_representation(self, instance):
        return {
            'expense': self.travel_place.expense
        }


class UpdateTravelPlaceOrderingSerializer(serializers.Serializer):
    ordering = serializers.ListField()

    def update(self, instance, validated_data):
        """
        為了要設定新的順序
        ['A', 'B'] : id A 排序為0, id B 排序為1
        先做個mapping, id : TravelPlace
        藉由index更新TravelPlace.order
        最後bulk_update
        """
        pub_id_mapping_travel_place_object = {
            travel_place.place.pub_id: travel_place
            for travel_place in TravelPlace.objects.select_related('place').filter(travel=instance)
        }
        for i, pub_id in enumerate(validated_data['ordering']):
            pub_id_mapping_travel_place_object[pub_id].order = i
        TravelPlace.objects.bulk_update(list(pub_id_mapping_travel_place_object.values()), ['order'])
        return instance

    def to_representation(self, instance):
        return {
            'ordering': self.validated_data['ordering']
        }


class UpdateTravelNameSerializer(serializers.Serializer):
    name = serializers.CharField()

    def update(self, instance, validated_data):
        instance.name = validated_data['name']
        instance.save(update_fields=['name'])
        return instance

    def to_representation(self, instance):
        return {
            'name': instance.name
        }


class UpdateTravelTimeSerializer(serializers.Serializer):
    from_time = serializers.CharField()
    to_time = serializers.CharField()

    def update(self, instance, validated_data):
        instance.from_time = validated_data['from_time']
        instance.to_time = validated_data['to_time']
        instance.save(update_fields=['from_time', 'to_time'])
        return instance

    def to_representation(self, instance):
        instance = Travel.objects.get(id=instance.id)
        return {
            'from_time': instance.from_time,
            'to_time': instance.to_time,
        }


class UpdateTravelRemarksSerializer(serializers.Serializer):
    remarks = serializers.CharField()

    def update(self, instance, validated_data):
        instance.remarks = validated_data['remarks']
        instance.save(update_fields=['remarks'])
        return instance

    def to_representation(self, instance):
        return {
            'remarks': instance.remarks,
        }


class UpdateTravelIsPublicSerializer(serializers.Serializer):
    is_public = serializers.BooleanField()

    def update(self, instance, validated_data):
        instance.is_public = validated_data['is_public']
        instance.save(update_fields=['is_public'])
        return instance

    def to_representation(self, instance):
        return {
            'is_public': instance.is_public,
        }

import logging
from datetime import timedelta

from django.core.paginator import Paginator
from django.db.models import Q
from django.shortcuts import render, redirect
from django.urls import reverse
from rest_framework.generics import get_object_or_404

from citadel.decorators import citadel_exception_handler
from citadel.serializers.place import CreatePlaceSerializer, EditPlaceSerializer, AuditPlaceSerializer
from core.utils.time import to_local_display_time, get_now_datetime_obj
from places.models import Place
from places.services.geo import get_lat_lng
from places.services.place_tag import get_place_tag_option, get_translate_place_tag_name

logger = logging.getLogger(__name__)


def get_query_set(qs, keyword=None, is_audited=None):
    if keyword:
        qs = qs.filter(Q(name__contains=keyword) | Q(address__contains=keyword))

    if is_audited is not None:
        qs = qs.filter(is_audited=is_audited)

    return qs


@citadel_exception_handler('citadel:login')
def place_list_view(request):
    keyword = request.GET.get('keyword')

    place_list = []
    qs = get_query_set(
        qs=Place.objects.prefetch_related('tags'),
        keyword=keyword,
    ).order_by('-create_at')

    page_obj = Paginator(qs, 20).get_page(request.GET.get('page', default=1))

    for place in page_obj.object_list:
        # ex : "美食, 伴手禮"
        tags = ', '.join([get_translate_place_tag_name(place_tag.name) for place_tag in place.tags.all()])

        # lat, lng
        lat, lng = get_lat_lng(place.position)

        place_list.append({
            'id': place.pub_id,
            'name': place.name,
            'address': place.address,
            'lat': lat,
            'lng': lng,
            'is_audited': place.is_audited,
            'tags': tags,
            'create_at': to_local_display_time(place.create_at),
            'is_deleted': place.is_deleted,
        })

    # statistic
    all_place_count = Place.objects.count()
    not_audit_place_count = Place.objects.filter(is_audited=False).count()

    return render(request, 'citadel/place/list.html', {
        'place_list': place_list,
        'page_obj': page_obj,

        # search
        'keyword': keyword if keyword else '',

        # statistic
        'all_place_count': all_place_count,
        'not_audit_place_count': not_audit_place_count,
    })


@citadel_exception_handler('citadel:place_list')
def place_create_view(request):
    if request.method == 'GET':
        return render(request, 'citadel/place/create.html', {
            'place_tag_option': get_place_tag_option(),
        })
    else:
        serializer = CreatePlaceSerializer(context={'request': request}, data=request.POST)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return redirect(reverse('citadel:place_list'))


@citadel_exception_handler('citadel:place_list')
def place_audit_view(request):
    serializer = AuditPlaceSerializer(data=request.POST)
    serializer.is_valid(raise_exception=True)
    serializer.save()
    return redirect(reverse('citadel:not_audit_place_list'))


@citadel_exception_handler('citadel:place_list')
def not_audit_place_list_view(request):
    keyword = request.GET.get('keyword')

    place_list = []
    qs = get_query_set(
        qs=Place.objects.select_related('creator'),
        keyword=keyword,
        is_audited=False,
    ).order_by('-create_at')

    page_obj = Paginator(qs, 20).get_page(request.GET.get('page', default=1))

    for place in page_obj.object_list:
        # lat, lng
        lat, lng = get_lat_lng(place.position)

        place_list.append({
            'id': place.pub_id,
            'name': place.name,
            'address': place.address,
            'latlng': '{},{}'.format(lat, lng),
            'create_at': to_local_display_time(place.create_at),
            'creator': {
                'id': place.creator.pub_id,
                'display_name': place.creator.display_name if place.creator.display_name else place.creator.username,
            } if place.creator else None,
        })

    # statistic
    not_audit_place_count = Place.objects.filter(is_audited=False).count()
    last_24_hours_not_audit_place_count = Place.objects.filter(
        is_audited=False,
        create_at__gte=get_now_datetime_obj() - timedelta(hours=24)
    ).count()

    return render(request, 'citadel/place/not_audit_list.html', {
        'place_list': place_list,
        'page_obj': page_obj,

        # search
        'keyword': keyword if keyword else '',

        # statistic
        'not_audit_place_count': not_audit_place_count,
        'last_24_hours_not_audit_place_count': last_24_hours_not_audit_place_count,
    })


@citadel_exception_handler('citadel:place_list')
def place_edit_view(request, pub_id):
    place = get_object_or_404(Place.objects.prefetch_related('tags'), pub_id=pub_id)
    if request.method == 'GET':
        # PlaceTag selected
        tag_ids = [place_tag.id for place_tag in place.tags.all()]
        place_tag_option = get_place_tag_option()

        for option in place_tag_option:
            option['selected'] = option['id'] in tag_ids

        # lat, lng
        lat, lng = get_lat_lng(place.position)

        return render(request, 'citadel/place/edit.html', {
            'id': place.pub_id,
            'name': place.name,
            'address': place.address,
            'lat': lat,
            'lng': lng,
            'is_audited': place.is_audited,
            'place_tag_option': place_tag_option,
        })
    else:
        serializer = EditPlaceSerializer(data=request.POST, instance=place)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        return redirect(reverse('citadel:place_list'))

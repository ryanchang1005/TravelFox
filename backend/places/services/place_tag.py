from places.models import PlaceTag


def get_place_tag_option():
    return [{
        'id': place_tag.id,
        'name': get_translate_place_tag_name(place_tag.name),
    } for place_tag in PlaceTag.objects.all()]


def get_translate_place_tag_name(name):
    return {
        'food': '美食',
        'o_mi_ya_ga': '伴手禮',
        'live': '住宿',
        'attraction': '景點',
        'traffic': '交通',
    }[name]

from places.models import PlaceTag
from places.services.geo import create_geo_position


def create_default_place_tags():
    from places.models import PlaceTag
    from places.utils.tags import TAG_CHOICE

    for it in TAG_CHOICE:
        if PlaceTag.objects.filter(name=it[0]).exists() is False:
            PlaceTag.objects.create(name=it[0])
            print('Create {}'.format(it[0]))


def create_default_place():
    from places.models import Place
    from places.services.geo import create_geo_position
    from places.utils.tags import TAG_TRAFFIC, TAG_ATTRACTION

    if Place.objects.exists() is True:
        print('Place is exists, create nothing.')
        return

    p1 = Place()
    p1.name = '台北車站'
    p1.position = create_geo_position(25.0477022, 121.5173748)
    p1.address = '台北市中正區北平西路3號100臺灣'

    p2 = Place()
    p2.name = '士林夜市'
    p2.position = create_geo_position(25.0879113, 121.5241765)
    p2.address = '111台北市士林區基河路101號'

    p3 = Place()
    p3.name = '基隆廟口夜市'
    p3.position = create_geo_position(25.1282477, 121.743618)
    p3.address = '200基隆市仁愛區愛四路20號'
    places = [p1, p2, p3]
    Place.objects.bulk_create(places)

    p1.tags.add(PlaceTag.objects.get(name=TAG_TRAFFIC))
    p2.tags.add(PlaceTag.objects.get(name=TAG_ATTRACTION))
    p3.tags.add(PlaceTag.objects.get(name=TAG_ATTRACTION))

    print('Create default Place, {}'.format([place.name for place in places]))


def import_place_data():
    from places.models import Place, PlaceTag
    from django.db import transaction

    # Check tags
    create_default_place_tags()

    # Mapping tag
    mapping = {}
    for tag in PlaceTag.objects.all():
        mapping[tag.name] = tag

    # import from csv
    with open(
            '/travelfox/place_data/place1.csv',
            encoding='utf8'
    ) as f:
        line = f.readline()
        while line:
            # Check len
            if len(line) < 20:
                print('len too short')
                line = f.readline()
                continue

            # Replace "
            line = line.replace('"', '')
            line = line.replace('\n', '')
            line = line.strip()

            # split item column
            items = line.split(',')
            name = items[0]
            lat = items[1].replace(' ', '')
            lng = items[2].replace(' ', '')
            tag_name = items[3]
            address = items[4]

            # Check tag
            if tag_name not in mapping:
                print('tag not exists, {}'.format(tag_name))
                line = f.readline()
                continue

            # Check address
            if Place.objects.filter(address=address).exists() is True:
                print('address exists, {}'.format(address))
                line = f.readline()
                continue

            try:
                with transaction.atomic():
                    # Create Place & tag
                    p1 = Place()
                    p1.name = name
                    p1.position = create_geo_position(lat, lng)
                    p1.address = address
                    p1.save()
                    p1.tags.add(mapping[tag_name])
            except Exception as e:
                print('{}, {}'.format(str(e), name))

            # next
            line = f.readline()


"""
from core.services.init import *
create_default_place_tags()
create_default_place()
import_place_data()
"""

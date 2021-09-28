from django.contrib.gis.geos import GEOSGeometry


def create_geo_position(lat, lng):
    return GEOSGeometry(f'POINT({lat} {lng})', 4326)


def get_lat_lng(position):
    return str(position.coords[0]), str(position.coords[1])

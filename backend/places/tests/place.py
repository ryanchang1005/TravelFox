import random
import secrets

from core.tests.base import BaseAPITestCase
from places.models import Place
from places.services.geo import create_geo_position


class BasePlaceTestCase(BaseAPITestCase):

    def create_place(self):
        lat = 24.9661930 + (25.1014923 - 24.9661930) * random.random()
        lng = 121.4130998 + (121.6142014 - 121.4130998) * random.random()
        return Place.objects.create(
            name=secrets.token_hex(10),
            address=secrets.token_hex(10),
            position=create_geo_position(lat, lng)
        )


class GetPlaceListTestCase(BaseAPITestCase):

    def test_get_place_list_by_nothing(self):
        rsp = self.api_client.get('/api/place/')
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('results' in return_data)
        self.assertTrue(len(return_data['results']) > 0)
        for it in return_data['results']:
            self.assertTrue('id' in it)
            self.assertTrue('name' in it)
            self.assertTrue('lat' in it)
            self.assertTrue('lng' in it)
            self.assertTrue('address' in it)
            self.assertTrue('tags' in it)

    def test_get_place_list_by_keyword(self):
        keyword = '車站'
        rsp = self.api_client.get('/api/place/?keyword={}'.format(keyword))
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('results' in return_data)
        self.assertTrue(len(return_data['results']) > 0)
        for it in return_data['results']:
            self.assertTrue(keyword in it['name'] or keyword in it['address'])

import secrets

from core.tests.base import BaseAPITestCase
from travels.models import Travel


class BaseTravelTestCase(BaseAPITestCase):
    """
    This class is for create default 'Travel'
    """

    def create_public_travel(self):
        user = self.api_client_user
        return Travel.objects.create(
            name=secrets.token_hex(10),
            from_time='2021-09-17T16:00:00Z',
            to_time='2021-09-17T16:00:00Z',
            creator=user,
            is_public=True,
        )

    def create_travel(self):
        user = self.api_client_user
        return Travel.objects.create(
            name=secrets.token_hex(10),
            from_time='2021-09-17T16:00:00Z',
            to_time='2021-09-17T16:00:00Z',
            creator=user,
        )


class GetPublicTravelListTestCase(BaseTravelTestCase):

    def test_get_public_list_by_nothing(self):

        # ready
        self.create_public_travel()

        # execute
        rsp = self.api_client.get('/api/travel/public/')
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('results' in return_data)
        self.assertTrue(len(return_data['results']) > 0)
        for it in return_data['results']:
            self.assertTrue('id' in it)
            self.assertTrue('name' in it)
            self.assertTrue('creator' in it)
            self.assertTrue('id' in it['creator'])
            self.assertTrue('name' in it['creator'])
            self.assertTrue('is_liked' in it)
            self.assertTrue('comment_count' in it)
            self.assertTrue('like_count' in it)

        # assert travel is 'public'
        for it in return_data['results']:
            self.assertTrue(Travel.objects.get(pub_id=it['id']).is_public)


class GetSelfTravelListTestCase(BaseTravelTestCase):

    def test_get_list(self):

        # ready
        self.create_travel()

        # execute
        rsp = self.api_client.get('/api/travel/')
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('results' in return_data)
        self.assertTrue(len(return_data['results']) > 0)
        for it in return_data['results']:
            self.assertTrue('id' in it)
            self.assertTrue('name' in it)
            self.assertTrue('creator' in it)
            self.assertTrue('id' in it['creator'])
            self.assertTrue('name' in it['creator'])
            self.assertTrue('avatar_url' in it['creator'])
            self.assertTrue('is_liked' in it)
            self.assertTrue('comment_count' in it)
            self.assertTrue('like_count' in it)

        # assert travel creator is self
        for it in return_data['results']:
            self.assertEqual(it['creator']['id'], self.api_client_user.pub_id)


class CreateTravelTestCase(BaseTravelTestCase):

    def test_create_success(self):
        # ready
        data = {
            'name': secrets.token_hex(10),
            'from_time': '2021-09-17T16:00:00Z',
            'to_time': '2021-09-20T16:00:00Z',
        }

        # execute
        rsp = self.api_client.post('/api/travel/', data=data)
        self.assertEqual(rsp.status_code, 201)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('id' in return_data)
        self.assertTrue('name' in return_data)
        self.assertTrue('from_time' in return_data)
        self.assertTrue('to_time' in return_data)
        self.assertTrue('creator' in return_data)
        self.assertTrue('id' in return_data['creator'])
        self.assertTrue('name' in return_data['creator'])
        self.assertTrue('avatar_url' in return_data['creator'])
        self.assertTrue('live_list' in return_data)
        self.assertTrue('food_list' in return_data)
        self.assertTrue('attraction_list' in return_data)
        self.assertTrue('o_mi_ya_ga_list' in return_data)
        self.assertTrue('traffic_list' in return_data)
        self.assertTrue('member_list' in return_data)
        self.assertTrue('remarks' in return_data)
        self.assertTrue('is_public' in return_data)
        self.assertTrue('is_archived' in return_data)
        self.assertTrue('likers' in return_data)


class GetTravelDetailTestCase(BaseTravelTestCase):

    def test_get_list(self):
        # ready
        travel = self.create_travel()

        # execute
        rsp = self.api_client.get(f'/api/travel/{travel.pub_id}/')
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('id' in return_data)
        self.assertTrue('name' in return_data)
        self.assertTrue('from_time' in return_data)
        self.assertTrue('to_time' in return_data)
        self.assertTrue('creator' in return_data)
        self.assertTrue('id' in return_data['creator'])
        self.assertTrue('name' in return_data['creator'])
        self.assertTrue('avatar_url' in return_data['creator'])
        self.assertTrue('live_list' in return_data)
        self.assertTrue('food_list' in return_data)
        self.assertTrue('attraction_list' in return_data)
        self.assertTrue('o_mi_ya_ga_list' in return_data)
        self.assertTrue('traffic_list' in return_data)
        self.assertTrue('member_list' in return_data)
        self.assertTrue('remarks' in return_data)
        self.assertTrue('is_public' in return_data)
        self.assertTrue('is_archived' in return_data)
        self.assertTrue('likers' in return_data)


class UpdateTravelDetailTestCase(BaseTravelTestCase):

    def test_update_name(self):
        # ready
        travel = self.create_travel()
        name = secrets.token_hex(10)
        data = {
            'name': name
        }

        # execute
        rsp = self.api_client.patch(f'/api/travel/{travel.pub_id}/name/', data=data)
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('name' in return_data)

    def test_update_time(self):
        # ready
        travel = self.create_travel()
        data = {
            'from_time': '2021-09-20T16:00:00Z',
            'to_time': '2021-09-23T16:00:00Z',
        }

        # execute
        rsp = self.api_client.patch(f'/api/travel/{travel.pub_id}/time/', data=data)
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('from_time' in return_data)
        self.assertTrue('to_time' in return_data)

    def test_update_remarks(self):
        # ready
        travel = self.create_travel()
        data = {
            'remarks': secrets.token_hex(10),
        }

        # execute
        rsp = self.api_client.patch(f'/api/travel/{travel.pub_id}/remarks/', data=data)
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('remarks' in return_data)

    def test_update_is_public(self):
        # ready
        travel = self.create_travel()
        data = {
            'is_public': True,
        }

        # execute
        rsp = self.api_client.patch(f'/api/travel/{travel.pub_id}/is_public/', data=data)
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('is_public' in return_data)

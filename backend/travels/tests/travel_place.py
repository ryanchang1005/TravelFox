import secrets

from places.tests import BasePlaceTestCase
from places.utils.tags import TAG_FOOD
from travels.models import TravelPlace
from travels.tests import BaseTravelTestCase


class BaseTravelPlaceTestCase(BaseTravelTestCase, BasePlaceTestCase):

    def create_travel_and_travel_place(self):
        travel = self.create_travel()
        place = self.create_place()
        TravelPlace.objects.create(
            travel=travel,
            place=place,
            creator=self.api_client_user,
            order=0,
        )
        return travel, place


class CreateTravelPlaceTestCase(BaseTravelPlaceTestCase):

    def test_create_success(self):
        # ready
        travel = self.create_travel()
        place = self.create_place()
        data = {
            'place_id': place.pub_id,
            'tag': TAG_FOOD,
        }

        # execute
        rsp = self.api_client.post(f'/api/travel/{travel.pub_id}/place/', data=data)
        self.assertEqual(rsp.status_code, 201)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('id' in return_data)
        self.assertTrue('name' in return_data)
        self.assertTrue('address' in return_data)
        self.assertTrue('lat' in return_data)
        self.assertTrue('lng' in return_data)
        self.assertTrue('tag' in return_data)
        self.assertTrue('order' in return_data)
        self.assertTrue('remarks' in return_data)
        self.assertTrue('expense' in return_data)
        self.assertTrue('creator' in return_data)
        self.assertTrue('id' in return_data['creator'])
        self.assertTrue('name' in return_data['creator'])
        self.assertTrue('avatar_url' in return_data['creator'])


class UpdateTravelPlaceTestCase(BaseTravelPlaceTestCase):

    def test_update_remarks_success(self):
        # ready
        travel, place = self.create_travel_and_travel_place()
        remarks = secrets.token_hex(10)
        data = {
            'place_id': place.pub_id,
            'remarks': remarks,
        }

        # execute
        rsp = self.api_client.patch(f'/api/travel/{travel.pub_id}/place_remarks/', data=data)
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('remarks' in return_data)
        self.assertEqual(return_data['remarks'], remarks)

    def test_update_expense_success(self):
        # ready
        travel, place = self.create_travel_and_travel_place()
        expense = 999
        data = {
            'place_id': place.pub_id,
            'expense': expense,
        }

        # execute
        rsp = self.api_client.patch(f'/api/travel/{travel.pub_id}/place_expense/', data=data)
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('expense' in return_data)
        self.assertEqual(return_data['expense'], expense)

    def test_update_ordering(self):
        # ready
        # create 2 place
        travel, place1 = self.create_travel_and_travel_place()
        place2 = self.create_place()  # second Place
        TravelPlace.objects.create(
            travel=travel,
            place=place2,
            creator=self.api_client_user,
            order=1,
        )
        # assert origin TravelPlace order
        qs = TravelPlace.objects.select_related('place').filter(travel=travel).order_by('order')
        self.assertEqual(qs[0].place.pub_id, place1.pub_id)
        self.assertEqual(qs[1].place.pub_id, place2.pub_id)

        data = {
            'ordering': [place2.pub_id, place1.pub_id],
        }

        # execute
        rsp = self.api_client.patch(f'/api/travel/{travel.pub_id}/place_ordering/', data=data)
        self.assertEqual(rsp.status_code, 200)
        return_data = rsp.json()

        # assert response json key in return_data
        self.assertTrue('ordering' in return_data)
        self.assertEqual(return_data['ordering'], data['ordering'])


class DeleteTravelPlaceTestCase(BaseTravelPlaceTestCase):

    def test_delete_success(self):
        # ready
        travel, place = self.create_travel_and_travel_place()

        # execute
        place_id = place.pub_id
        rsp = self.api_client.delete(f'/api/travel/{travel.pub_id}/place/?place_id={place_id}')
        self.assertEqual(rsp.status_code, 204)

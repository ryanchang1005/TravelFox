from django.contrib.gis.db import models
from django.utils.translation import ugettext_lazy as _

from core.models import AutoPubIDField
from places.utils.tags import TAG_CHOICE
from users.models import User


class PlaceTag(models.Model):
    """
    標籤
    提供景點標籤, 多對多
    """

    # 標籤名稱, ex : 吃, 伴手禮, 交通, 景點, 住宿
    name = models.CharField(
        _('name'),
        max_length=32,
        choices=TAG_CHOICE,
    )


class Place(models.Model):
    """
    景點
    用於地圖搜尋, Travel關連, User收藏, PlaceComment景點評論, PlacePicture景點圖片
    """
    # 公開ID
    pub_id = AutoPubIDField(
        _('pub_id')
    )

    # 景點名稱, ex : OO夜市, XX雞排
    name = models.CharField(
        _('name'),
        max_length=128,
    )

    # 經緯度, ex : 24.123456,121.123456
    position = models.GeometryField()

    # 地址, ex : 111台北市士林區基河路101號
    address = models.CharField(
        _('address'),
        max_length=128,
    )

    # 標籤, ex : [Tag('吃'), Tag('伴手禮')]
    tags = models.ManyToManyField(PlaceTag)

    # 是否審核
    is_audited = models.BooleanField(
        _('is_audited'),
        default=False,
    )

    # 建立時間
    create_at = models.DateTimeField(
        _('create_at'),
        auto_now_add=True,
    )

    # 建立者
    creator = models.ForeignKey(
        User,
        on_delete=models.PROTECT,
        null=True,
    )

    # 是否刪除
    is_deleted = models.BooleanField(
        _('is_deleted'),
        default=False,
    )

    def __str__(self):
        return self.name


class PlaceComment(models.Model):
    """
    景點評論
    使用者於景點下方評論
    """

    # 公開ID
    pub_id = AutoPubIDField(
        _('pub_id')
    )

    # 景點
    place = models.ForeignKey(Place, on_delete=models.PROTECT)

    # 評論內容
    comment = models.CharField(
        _('comment'),
        max_length=128,
    )

    # 評論者
    creator = models.ForeignKey(User, on_delete=models.PROTECT)

    # 評論時間
    create_at = models.DateTimeField(
        _('create_at'),
        auto_now_add=True,
    )


class PlaceImage(models.Model):
    """
    景點圖片
    """

    # 公開ID
    pub_id = AutoPubIDField(
        _('pub_id')
    )

    # 景點
    place = models.ForeignKey(Place, on_delete=models.PROTECT)

    # 圖片連結
    image_url = models.CharField(
        _('image_url'),
        max_length=256,
    )

    # 圖片上傳者
    creator = models.ForeignKey(User, on_delete=models.PROTECT)

    # 圖片上傳時間
    create_at = models.DateTimeField(
        _('create_at'),
        auto_now_add=True,
    )


class PlaceSaveBucket(models.Model):
    """
    景點收藏籃子
    使用者建立籃子, 籃子內儲存景點, ex : 台南-吃(OO小吃, XX雞排), 台中景點(XX步道, OO海灘)
    """

    # 籃子名稱
    name = models.CharField(
        _('name_of_bucket'),
        max_length=32,
    )

    # 籃子創建者
    creator = models.ForeignKey(User, on_delete=models.PROTECT)

    # 收藏的景點, [Place('OO雞排'), Place('XX小吃')]
    places = models.ManyToManyField(Place)


class PlaceSaved(models.Model):
    """
    被收藏的景點
    籃子, 景點, 哪時, 備註/心得, 順序
    """
    # 籃子
    bucket = models.ForeignKey(PlaceSaveBucket, on_delete=models.PROTECT)

    # 景點
    place = models.ForeignKey(Place, on_delete=models.PROTECT)

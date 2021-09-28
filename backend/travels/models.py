from django.contrib.gis.db import models
from django.utils.translation import ugettext_lazy as _

from core.models import AutoPubIDField
from places.models import Place
from places.utils.tags import TAG_CHOICE
from users.models import User


class Travel(models.Model):
    """
    旅程
    """
    # 公開ID
    pub_id = AutoPubIDField(
        _('pub_id')
    )

    # 旅程名稱, ex : OOXX三日行
    name = models.CharField(
        _('name'),
        max_length=32,
    )

    # 旅程開始時間
    from_time = models.DateTimeField(
        _('from_time')
    )

    # 旅程結束時間
    to_time = models.DateTimeField(
        _('to_time')
    )

    # 旅程創建者
    creator = models.ForeignKey(User, on_delete=models.PROTECT)

    # 旅程創建時間
    create_at = models.DateTimeField(
        _('create_at'),
        auto_now_add=True,
    )

    # 備註/心得
    remarks = models.CharField(
        _('remarks'),
        max_length=128,
        null=True,
    )

    # 是否公開
    is_public = models.BooleanField(
        _('is_public'),
        default=False,
    )

    # 是否封存
    is_archived = models.BooleanField(
        _('is_archived'),
        default=False,
    )

    # 按讚的使用者
    likes = models.ManyToManyField(User, related_name='travel_of_like')


class TravelPlace(models.Model):
    """
    在旅程裡面的景點
    """
    # 旅程
    travel = models.ForeignKey(Travel, on_delete=models.PROTECT)

    # 景點
    place = models.ForeignKey(Place, on_delete=models.PROTECT)

    # 新增者
    creator = models.ForeignKey(User, on_delete=models.PROTECT)

    # 順序
    order = models.PositiveSmallIntegerField(
        _('order_of_travel_place')
    )

    # 標籤, ex : 吃, 伴手禮, 交通, 景點, 住宿
    tag = models.CharField(
        _('name'),
        max_length=32,
        choices=TAG_CHOICE,
        null=True,
    )

    # 備註/心得
    remarks = models.CharField(
        _('remarks'),
        max_length=32,
        null=True,
    )

    # 花費
    expense = models.PositiveIntegerField(
        _('expense'),
        default=0,
    )

    class Meta:
        unique_together = [('travel', 'place', 'tag')]


class TravelMember(models.Model):
    """
    在旅程中共筆的成員
    """
    # 旅程
    travel = models.ForeignKey(Travel, on_delete=models.PROTECT)

    # 共筆者
    member = models.ForeignKey(User, on_delete=models.PROTECT)

    # 角色
    ROLE_MEMBER = 'member'  # 僅供查看旅程
    ROLE_OWNER = 'owner'  # 旅程的建立者, 全權限 : 查看/編輯/邀請(member/planner)
    ROLE_PLANNER = 'planner'  # 旅程的規劃者, 全權限 : 查看/編輯
    ROLE_CHOICE = (
        (ROLE_MEMBER, _('member')),
        (ROLE_OWNER, _('owner')),
        (ROLE_PLANNER, _('planner')),
    )
    role = models.CharField(
        _('role'),
        max_length=16,
    )

    class Meta:
        unique_together = [('travel', 'member')]

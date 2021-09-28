import logging

from django.core.paginator import Paginator
from django.shortcuts import render, redirect
from rest_framework.generics import get_object_or_404
from rest_framework.reverse import reverse

from citadel.decorators import citadel_exception_handler
from citadel.serializers.user import EditUserSerializer
from core.utils.time import to_local_display_time
from users.models import User

logger = logging.getLogger(__name__)


def get_query_set(qs, email=None):
    if email:
        qs = qs.filter(email=email)
    return qs


@citadel_exception_handler('citadel:dashboard')
def user_list_view(request):
    email = request.GET.get('email')

    qs = get_query_set(
        qs=User.objects.filter(is_superuser=False),
        email=email,
    ).order_by('-last_login')

    page_obj = Paginator(qs, 20).get_page(request.GET.get('page', default=1))

    user_list = []
    for it in page_obj.object_list:
        user_list.append({
            'id': it.pub_id,
            'login_platform': it.login_platform,
            'display_name': it.display_name,
            'email': it.email,
            'last_login': to_local_display_time(it.last_login) if it.last_login else '',
            'is_active': it.is_active,
        })

    # statistic
    all_user_count = 0
    last_24_hours_user_count = 0

    return render(request, 'citadel/user/list.html', {
        'user_list': user_list,
        'page_obj': page_obj,

        # search
        'email': email if email else '',

        # statistic
        'all_user_count': all_user_count,
        'last_24_hours_user_count': last_24_hours_user_count,
    })


@citadel_exception_handler('citadel:user_list')
def user_edit_view(request, pub_id):
    user = get_object_or_404(User, pub_id=pub_id, is_superuser=False)

    if request.method == 'GET':
        return render(request, 'citadel/user/edit.html', {
            'id': user.pub_id,
            'login_platform': user.login_platform,
            'display_name': user.display_name if user.display_name else 'N/A',
            'email': user.email,
            'last_login': to_local_display_time(user.last_login) if user.last_login else 'N/A',
            'is_active': user.is_active,
        })
    else:
        serializer = EditUserSerializer(data=request.POST, instance=user)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        return redirect(reverse('citadel:user_list'))

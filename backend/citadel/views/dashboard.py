import logging

from django.shortcuts import render

from citadel.decorators import citadel_exception_handler
from core.utils.time import get_today_midnight

logger = logging.getLogger(__name__)


@citadel_exception_handler('citadel:login')
def dashboard_view(request):


    return render(request, 'citadel/dashboard/index.html', {

    })

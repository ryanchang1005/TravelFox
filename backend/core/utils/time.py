import pytz

from datetime import datetime
from django.utils import timezone


def get_timestamp():
    return int(datetime.now().timestamp() * 1000)


def get_now_datetime_obj():
    return timezone.now()


def to_iso8601_utc_string(datetime_obj):
    utc_datetime = datetime_obj.astimezone(pytz.utc)
    return utc_datetime.strftime('%Y-%m-%dT%H:%M:%SZ')


def iso8601_string_to_datetime_obj(value):
    return get_now_datetime_obj().strptime(value, '%Y-%m-%dT%H:%M:%SZ').astimezone(pytz.utc)


def get_today_midnight():
    return datetime.now().replace(hour=0, minute=0, second=0, microsecond=0).astimezone(pytz.utc)


def to_local_display_time(datetime_obj):
    if not datetime_obj:
        return ''
    locale_datetime_obj = datetime_obj.astimezone(timezone.get_current_timezone())
    return locale_datetime_obj.strftime('%Y/%m/%d %H:%M:%S')

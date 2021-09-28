import os
from datetime import timedelta

from celery import Celery
from celery.schedules import crontab
from django.conf import settings

# 設置環境變量 DJANGO_SETTINGS_MODULE

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'core.settings')

# 創建實例
app = Celery('core')
app.config_from_object('django.conf:settings')

# 查找在 INSTALLED_APPS 設置的異步任務
app.autodiscover_tasks(lambda: settings.INSTALLED_APPS)

# ================== 排程任務[Start] ==================
app.conf.beat_schedule = {
    # 'get_quotes_from_api': {
    #     'task': 'core.celery.get_quotes_from_api',
    #     'schedule': crontab(hour=14, minute=0),  # 每天下午2點
    # },
}


@app.task(bind=True)
def get_quotes_from_api(self):
    from core.services.schedule import get_quotes_from_api
    get_quotes_from_api()

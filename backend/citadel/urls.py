from django.urls import path

from citadel import views

app_name = 'citadel'

urlpatterns = [
    path('', views.router_view, name='router_view'),
    path('login/', views.login_view, name='login'),
    path('logout/', views.logout_view, name='logout'),

    # dashboard
    path('dashboard/', views.dashboard_view, name='dashboard'),

    # user
    path('user/list/', views.user_list_view, name='user_list'),
    path('user/<slug:pub_id>/edit/', views.user_edit_view, name='user_edit'),

    # place
    path('place/list/', views.place_list_view, name='place_list'),
    path('place/create/', views.place_create_view, name='place_create'),
    path('place/audit/', views.place_audit_view, name='place_audit'),
    path('place/not_audit_list/', views.not_audit_place_list_view, name='not_audit_place_list'),
    path('place/<slug:pub_id>/edit/', views.place_edit_view, name='place_edit'),

]

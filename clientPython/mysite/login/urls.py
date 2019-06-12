from django.urls import path
from . import views as weather_views
urlpatterns = [
    path('login', weather_views.index, name = "login")
]

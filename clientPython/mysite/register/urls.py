from django.urls import path
from . import views as weather_views
urlpatterns = [
    path('register', weather_views.index, name = "register1")
]

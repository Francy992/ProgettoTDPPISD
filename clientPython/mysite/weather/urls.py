from django.urls import path
from . import views as weather_views
urlpatterns = [
    path('weather', weather_views.index, name = "index")
]

from django.urls import path
from . import views as weather_views
urlpatterns = [
    path('ciao', weather_views.index, name = "index")
]

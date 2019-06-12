from django.contrib import admin
'''
# Register your models here.
from .models import Weather


class WeatherAdmin(admin.ModelAdmin):
    list_display = ["titolo", "data"]
    list_filter = ["data"]
    search_fields = ["titolo", "data"]

    class Meta:
        model = Weather

admin.site.register (Weather, WeatherAdmin)'''
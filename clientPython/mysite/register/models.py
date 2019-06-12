from django.db import models
'''
# Create your models here.
class Weather (models.Model):
    titolo = models.CharField(max_length = 150)
    contenuto = models.TextField()
    data = models.DateTimeField(auto_now = False, auto_now_add=True)
'''
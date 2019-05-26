from django.shortcuts import render
from . models import Weather
# Create your views here.
def index(request):
    model = Weather()
    model.contenuto = "Contenuto"
    model.titolo = "AAA"
    provv = ""
    provv = request.POST.get("name_field", "")
    STATIC_URL = '/static/'
    '''
    Creare con la chiamata dell'altro file l'oggetto weather restituito dal server java e inviarlo alla view.
    Nella view gestire la stampa in un qualche modo.
    Ancora gestire l'invio di form dal template ad un controller/view'''
    return render(
        request,
        'index.html',
        locals(),
        {'model': model, 'request': provv},
    )

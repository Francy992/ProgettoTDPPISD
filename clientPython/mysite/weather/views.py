from django.shortcuts import render
from . models import Weather
import socket
import json
# Create your views here.
def index(request):
    city = ""
    city = request.POST.get("city", "")
    #city = "Floridia"
    if (city != ""): 
        '''In questo caso è stata cercata una città dal modulo find.'''
        try:
            y = contactServer(city)
            cityRequired = city
        except Exception as msg:
             y = json.loads('{"error":"True", "messageError":"Server contact error."}')
    else:
        y = json.loads('{"error":"True", "messageError":"Unknown error."}')
        cityRequired ="No city required"
    STATIC_URL = '/static/'
    return render(
        request,
        'index.html',
        locals(),
        {'request': city, 'recv': y, 'cityRequired': cityRequired},
    )


def getJson(choose, city):
    data = {"choose": choose, "city": city}
    print(data)
    return json.dumps(data)

def contactServer(city):
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect(("localhost", 8888))
        client_socket.send((getJson(1, city) + "\n").encode())
        recv = client_socket.recv(1024*20).decode()
        return json.loads(recv)
    except Exception as msg:
        return json.loads('{"error":"True", "messageError":"Server contact error."}')

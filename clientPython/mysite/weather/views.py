from django.shortcuts import render
#dfrom . models import Weather
import socket
import json
# Create your views here.
def index(request):
    city = ""
    city = request.POST.get("city", "")
    #city = "Floridia"
    print("Sono dentro index")
    if (city != ""): 
        '''In questo caso è stata cercata una città dal modulo find.'''
        try:
            y = contactServer(1, city)
            cityRequired = city
        except Exception as msg:
             y = json.loads('{"error":"True", "messageError":"Server contact error."}')
    else:
        y = json.loads('{"error":"True", "messageError":"Unknown error."}')
        cityRequired ="No city required"

    userSession = inizializeJson("Nessuna città preferita salvata.")
    if(request.session.get('cityPrefer') != None):
        userSession = getPreferCity(request.session.get('cityPrefer'))

    STATIC_URL = '/static/'
    return render(
        request,
        'index.html',
        locals(),
        {'request': city, 'recv': y, 'cityRequired': cityRequired, "userSession": userSession},
    )


def getJson(choose, city):
    data = {"choose": choose, "city": city}
    print(data)
    return json.dumps(data)

def contactServer(choose, city):
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect(("localhost", 8888))
        client_socket.send((getJson(choose, city) + "\n").encode())
        recv = client_socket.recv(1024*20).decode()
        print("Ho ricevuto, non entro + qui.")
        return json.loads(recv)
    except Exception as msg:
        return json.loads('{"error":"True", "messageError":"Server contact error."}')

def getPreferCity(request):
    print("aaaaaaaaaaaaaaaaa", request)
    if request != None:
        return request

def inizializeJson(message):
    data = {"error":"True", "messageError":message}
    return json.loads(data)
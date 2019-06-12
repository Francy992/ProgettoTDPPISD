from django.shortcuts import render
#from . models import Weather
import socket
import json
# Create your views here.
def index(request):
    '''city = ""
    city = request.POST.get("city", "")
    #city = "Floridia"
    print("Sono dentro index")
    if (city != ""): 
        In questo caso è stata cercata una città dal modulo find.
        try:
            y = contactServer(city)
            cityRequired = city
        except Exception as msg:
             y = json.loads('{"error":"True", "messageError":"Server contact error."}')
    else:
        y = json.loads('{"error":"True", "messageError":"Unknown error."}')
        cityRequired ="No city required"
    '''
    username = request.POST.get("username", "")
    password = request.POST.get("password", "")
    response = ""
    if(username != "" and password != ""):
        response = contactServer(username, password)

    STATIC_URL = '/static/'
    return render(
        request,
        'register.html',
        locals(),
        {"response": response},
    )


def getJson(choose, username, password):
    data = {"choose": choose, "username": username, "password": password}
    print(data)
    return json.dumps(data)

def contactServer(username, password):
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect(("localhost", 8888))
        client_socket.send((getJson(1, username, password) + "\n").encode())
        recv = client_socket.recv(1024*20).decode()
        print("Ho ricevuto, non entro + qui.")
        return json.loads(recv)
    except Exception as msg:
        return json.loads('{"error":"True", "messageError":"Server contact error."}')

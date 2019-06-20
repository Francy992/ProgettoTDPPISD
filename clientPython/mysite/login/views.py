from django.shortcuts import render, redirect
#from . models import Weather
import socket
import json
# Create your views here.
def index(request):
    username = request.POST.get("username", "")
    password = request.POST.get("password", "")
    response = None
    if(username != "" and password != ""):
        response = contactServer(username, password)
        print("Response --> ", response)
        if response['error'] == False:
            request.session['cityPrefer'] = response
            request.session['loggato'] = True
            request.session['username'] = username
            request.session['password'] = password
            return redirect('/weather')
    
    userDate = json.loads('{"user": "False"}')
    if('username' in request.session and 'password' in request.session):
        data = json.dumps({"user": "True", "username": request.session['username'], "password": request.session['password']})
        userDate = json.loads(data)
        print(userDate)  

    STATIC_URL = '/static/'
    return render(
        request,
        'login.html',
        locals(),
        {"response": response, 'userDate': userDate},
    )


def getJson(choose, username, password):
    data = {"choose": choose, "username": username, "password": password}
    print(data)
    return json.dumps(data)

def contactServer(username, password):
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect(("localhost", 8888))
        client_socket.send((getJson(2, username, password) + "\n").encode())
        recv = client_socket.recv(1024*20).decode()
        print("Recv-->",recv)
        return json.loads(recv)
    except Exception as msg:
        return json.loads('{"error":"True", "messageError":"Server contact error."}')

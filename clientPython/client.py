'''from kivy.app import App
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.lang import Builder
        
class ScreenManagement(ScreenManager):
    pass

class LoginPage(Screen):
    def verify(self):
        user=self.ids["login"].text
        pwd=self.ids["password"].text
        if  user== "user" and  pwd== "passwd":
             print('login successful')
             

class LoginApp(App):
    def builder(self):
        return kv_file

kv_file = Builder.load_file('login.kv')

if __name__ == '__main__':
    LoginApp().run()'''

'''import socket
import sys

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
server_address = ('localhost', 8888)
print (sys.stderr, 'connecting to %s port %s' % server_address)
sock.connect(server_address)
try:
    
    # Send data
    message = 'This is the message.  It will be repeated.'
    print (sys.stderr, 'sending "%s"' % message)
    sock.sendall(message)

    # Look for the response
    amount_received = 0
    amount_expected = len(message)
    
    while amount_received < amount_expected:
        data = sock.recv(16)
        amount_received += len(data)
        print (sys.stderr, 'received "%s"' % data)

finally:
    print (sys.stderr, 'closing socket')
    sock.close()'''

def printPossibleRequest():
    while(1):
        print("******************************")
        print("Puoi scegliere tra le seguenti operazioni:")
        print("1--> Current Weather")
        print("2--> Tomorrow's Weather")
        print("3--> Next five days of weather")
        print("4--> For exit")
        print("Insert your choose:")
        try:
            choose = int(input())  
            if choose != 1 and choose != 2 and choose != 3 and choose != 4:
                raise ValueError    
            if choose == 4:
                return choose, ""
            print("Insert city:")
            city = input() 
            return choose, city
        except ValueError:
            print("Wrong choose, retry.")
            continue

import socket
import json

def getJson(choose, city):
    data = {"choose": choose, "city": city}
    print(data)
    return json.dumps(data)
    
HOST = "localhost"
PORT = 8888
 
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect(("localhost", 8888))

while 1:    
    choose, city = printPossibleRequest()
    if choose == 4:
        print("Goodbay!")
        break    
    else:
        client_socket.send((getJson(choose, city) + "\n").encode())
        recv = client_socket.recv(1024*20).decode()
        y = json.loads(recv)
        print("Ho ricevuto: ", y)
        for i in y['details']:
            print(i)
print("Sono uscito")





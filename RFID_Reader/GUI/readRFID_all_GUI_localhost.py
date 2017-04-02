# Support install library of python with terminal
# sudo easy_install pip
# more Serial info: http://pyserial.readthedocs.io/en/latest/pyserial.html
# more appjar info: http://appjar.info
import requests
import json
import serial # python -m pip install pyserial
import os
from appJar import gui # sudo pip3 install appjar
import threading
import subprocess

# Apis 
def sendAddRequest(data):
    dataString = data[1:-1]
    print str(dataString)
    url = 'http://localhost:8080/librarian/add/copy'
    data = {
        "ibeaconId" : "1",
        "rfid" : dataString
    }
    headers = {'Content-Type': 'application/json'}
    response = requests.post(url, data=json.dumps(data), headers=headers)
    print response.text
    if response.json()['soundMessage'] != "":
        os.system('say {}'.format(response.json()['soundMessage']))

def senReturnRequest(data):
    dataString = data[1:-1]
    print str(dataString)
    url = 'http://localhost:8080/librarian/add/return'
    data = {
        "librarianId" : "1",
        "rfid" : dataString
    }
    headers = {'Content-Type': 'application/json'}
    response = requests.post(url, data=json.dumps(data), headers=headers)
    print response.text
    if response.json()['soundMessage'] != "":
        os.system('say {}'.format(response.json()['soundMessage']))

def sendUserBorrowRequest(data):
    dataString = data[1:-1]
    print str(dataString)
    url = 'http://localhost:8080/add/copy'
    data = {
        "ibeaconId" : "1",
        "rfid" : dataString
    }
    headers = {'Content-Type': 'application/json'}
    response = requests.post(url, data=json.dumps(data), headers=headers)
    print response.text
    if response.json()['code'] == "200" or response.json()['code'] == "400" and response.json()['soundMessage'] != "":
        subprocess.call(["afplay", response.json()['soundMessage']])
    else:
        subprocess.call(["afplay", "alarm.mp3"])

# Global variable used in thread for choosing apis.
option = 1

def callApi():
    ser = serial.Serial('/dev/tty.usbserial-A702RZ3Y')  # open serial port
    print(ser.name)         # check which port was really used
    while True:
        print option
        x = ser.read(12)
        print "/"+x+"/"
        if option == 1:
            sendAddRequest(x)
        if option == 2:
            senReturnRequest(x)
        if option == 3:
            sendUserBorrowRequest(x)

thread = threading.Thread(target=callApi) # if not use thread, serial.read will block the UI
thread.daemon = True # daemon thread is thread that will end if app closes and no non-daemon thread is running
thread.start()

# UI functions
def onChooseRb(rb):
    global option
    if app.getRadioButton("api") == "Librarian Borrow Book":
        option = 1
    elif app.getRadioButton("api") == "User Borrow Book":
        option = 3
    else:
        option = 2

def onStop(btn):
    app.stop()

# UI
app = gui("Just Walk Out Library RFID Reader", "300x200")
app.setResizable(canResize=False)
app.setLabelFont(30, font="Arial")
app.setButtonFont(20, font="Arial")
app.addRadioButton("api", "Librarian Borrow Book")
app.addRadioButton("api", "Return Book")
app.addRadioButton("api", "User Borrow Book")
app.setRadioButtonFunction("api", onChooseRb)
app.addButton("Stop", onStop, 4, 0)
app.go()

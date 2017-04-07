import requests
import json
import serial
import os
import subprocess
from appJar import gui
import threading

host = 'localhost'
process = ""
def sendPostRequest(data):
    global process

    dataString = data[1:-1]
    print str(dataString)
    url = 'http://' + host + ':8080/add/copy'
    data = {
        "ibeaconId" : "1",
        "rfid" : dataString
    }
    headers = {'Content-Type': 'application/json'}
    response = requests.post(url, data=json.dumps(data), headers=headers)
    print response.text
    if response.json()['code'] == "200" or response.json()['code'] == "400" and response.json()['soundMessage'] != "":
        process = subprocess.Popen(['afplay', response.json()['soundMessage']])
    else:
        subprocess.Popen(["afplay", "alarm.mp3"])

# list cac device dang cam vao may
# ls /dev/tty.*
def callApi():
    ser = serial.Serial('/dev/tty.usbserial-A702RZ3Y')  # open serial port
    print(ser.name)         # check which port was really used
    while True:
        x = ser.read(12)        
        print "/"+x+"/"
        sendPostRequest(x)
    ser.close()             # close port

thread = threading.Thread(target=callApi) # if not use thread, serial.read will block the UI
thread.daemon = True # daemon thread is thread that will end if app closes and no non-daemon thread is running
thread.start()

# UI functions
def onClickFunctions(btn):
    if btn == "Change Host":
        onChangeHost(btn)
    elif btn == "Stop Alarm":
        onStopAlarm(btn)

def onChangeHost(btn):
    global host
    newHost = app.getEntry("host")
    if newHost != "":
        host = newHost
    print("new host: " + host)
    sendPostRequest("R1")

def onStopAlarm(btn):
    global process
    if process != "":
        process.kill()

def onStop(btn):
    app.stop()

# UI
app = gui("Just Walk Out Library RFID Reader", "300x200")
app.setResizable(canResize=False)
app.setLabelFont(30, font="Arial")
app.setButtonFont(20, font="Arial")
app.addEntry("host")
app.setEntryDefault("host", "http://localhost")
app.addButtons(["Change Host", "Stop Alarm"], onClickFunctions, 1, 0, 2)
app.addButton("Stop", onStop, 2, 0)
app.go()

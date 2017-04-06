import requests
import json
import serial
import os
import subprocess

def senPostRequest(data):
    dataString = data[2:-3]
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

# list cac device dang cam vao may
# ls /dev/tty.*
ser = serial.Serial('/dev/tty.usbserial-A702R566')  # open serial port
print(ser.name)         # check which port was really used
while True:
    x = ser.read(15)        
    print "/"+x+"/"
    senPostRequest(x)
ser.close()             # close port
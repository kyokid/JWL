import requests
import json
import serial

def senPostRequest(data):
    print data
    url = 'https://jwl-api-v0.herokuapp.com/add/copy'
    data = {
        "ibeaconId": "D8:CF:F3:6B:8E:01",
        "rfid": data
    }
    headers = {'Content-Type': 'application/json'}
    response = requests.post(url, data=json.dumps(data), headers=headers)
    print response.text

# list cac device dang cam vao may
# ls /dev/tty.*
ser = serial.Serial('/dev/tty.usbserial-A702RZ3Y')  # open serial port
print(ser.name)         # check which port was really used
while True:
    x = ser.read(12)
    print "/"+x+"/"
    senPostRequest(x)
ser.close()             # close port

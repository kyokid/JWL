import requests
import json

def senPostRequest(data):
    print data
    url = 'https://jwl-api-v0.herokuapp.com/add/copies'
    data = {
        "ibeaconId": 123,
        "rfids": [data]
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

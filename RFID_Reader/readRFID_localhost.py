import requests
import json
import serial

def senPostRequest(data):
    dataString = data[1:-1]
    print str(dataString)
    url = 'http://192.168.43.207:8080/add/copy'
    data = {
        "ibeaconId" : "1",
        "rfid" : dataString
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
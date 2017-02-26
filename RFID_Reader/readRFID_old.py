import serial
import urllib
import urllib2


def sendPostRequest(data):
    url = 'http://reduxblog.herokuapp.com/api/posts?key=45678'
    values ={'title' : data, 'categories' : 'hello',
          'location' : 'Vietnam',
          'content' : data}
    data = urllib.urlencode(values)
    req = urllib2.Request(url, data)
    response = urllib2.urlopen(req)
    print response.read()

# list cac device dang cam vao may
# ls /dev/tty.*
ser = serial.Serial('/dev/tty.usbserial-A702RZ3Y')  # open serial port
print(ser.name)         # check which port was really used
while True:
    x = ser.read(12)
    print "/"+x+"/"
    sendPostRequest(x)
ser.close()             # close port

import requests
import json
import serial
import time

# list cac device dang cam vao may
# ls /dev/tty.*
ser = serial.Serial('/dev/tty.usbserial-A702RZ3Y')  # open serial port
print(ser.name)         # check which port was really used
while True:
	message = "Data Test!"
 	ser.write("A")
 	ser.write("U")
 	data = []
 	while len(data < 1000):
 		data.append(ser.readLine())

 	ser.write("D")
 	x = ser.read(12)
 	print x
ser.close()             # close port

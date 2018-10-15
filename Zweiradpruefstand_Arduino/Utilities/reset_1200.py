#!/usr/bin/env python

#
# embedXcode
# ----------------------------------
# Embedded Computing on Xcode
#
# Copyright © Rei VILO, 2010-2017
# http://embedxcode.weebly.com
# All rights reserved
#
# Last update: Apr 18, 2016 release 4.4.8
#

import serial
import sys
import time

if len(sys.argv) < 2:
    print "Missing serial port"
    sys.exit()

print 'Setting %s at 1200' % sys.argv[1]

ser = serial.Serial(sys.argv[1], baudrate=1200)

time.sleep(1)

ser.close()

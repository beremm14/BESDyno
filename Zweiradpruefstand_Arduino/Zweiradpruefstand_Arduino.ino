//
// Zweiradpruefstand_Arduino
//
// Description of the project
// Developed with [embedXcode](http://embedXcode.weebly.com)
//
// Author 		Emil Berger
// 				DA-Team (BES)
//
// Date			15.10.18 15:34
// Version		<#version#>
//
// Copyright	© Emil Berger, 2018
// Licence		<#licence#>
//
// See         ReadMe.txt for references
//


// Core library for code-sense - IDE-based
// !!! Help: http://bit.ly/2AdU7cu
#if defined(ENERGIA) // LaunchPad specific
#include "Energia.h"
#elif defined(TEENSYDUINO) // Teensy specific
#include "Arduino.h"
#elif defined(ESP8266) // ESP8266 specific
#include "Arduino.h"
#elif defined(ARDUINO) // Arduino 1.8 specific
#include "Arduino.h"
#else // error
#error Platform not defined
#endif // end IDE

#include <Adafruit_BMP085.h>
#include <Wire.h>

//-Definations----------------------------------------------------------//
//Analog Devices
int engTempPin = A0;
int exhTempPin = A1;

//Digital Devices
int rearRPMPin = 2;
int engRPMPin = 1;

//EnvTemp and EnvPress at TWI-Interface (BMP180)

//-Declarations----------------------------------------------------------//
//Devices
Adafruit_BMP085 bmp;

//Environment
double envTemp;
double envPress;

//-Functions------------------------------------------------------------//
void readEnvironment () {
    if (!bmp.begin()) {
        Serial.println("Error 180: Environment-Sensor not found");
        return;
    }
    envTemp = bmp.readTemperature();
    envPress = bmp.readPressure();
}

void readSparkplug() {

}

void readRearWheel() {

}

void readEngineTemp() {

}

void readExhaustTemp() {

}


void setup() {
    Serial.begin(57600);
}

void loop() {


}

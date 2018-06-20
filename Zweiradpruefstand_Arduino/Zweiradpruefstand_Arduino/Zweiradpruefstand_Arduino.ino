//
// Zweiradpruefstand_Arduino
//
// Software for Ehmann's Hardware
// Developed with [embedXcode](http://embedXcode.weebly.com)
//
// Author 		Emil Berger
// 				    DA-Team (BES)
//
// Date			20.06.18 08:28
// Version		0.1
//
// Copyright	© Emil Berger, 2018
// Licence		GNU General Public Licence
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


// Set parameters


// Include application, user and local libraries


// Define structures and classes


// Define variables and constants
#define engPin A0;
#define fumePin A1;
#define envPin A2;
#define schleppPin 0;
#define rpmPin 1;


double engTemp;
double fumeTemp;
double envTemp;
int rpm;
int wss;
int measTime;

// Prototypes
// !!! Help: http://bit.ly/2l0ZhTa


// Utilities


// Functions
void serialInit() {
  String serialRead;
  Serial.begin(57600);
  
  do {
    serialRead = Serial.read();
  } while(serialRead.equals("START"));

  engTemp = analogRead(engPin);
  fumeTemp = analogRead(fumePin);
  envTemp = analogRead(envPin);

  String response = engTemp + "#" + fumeTemp + "#" + envTemp;
  Serial.println(response);
  Serial.flush();
}

// Add setup code
void setup() {
  serialInit();
  
}

// Add loop code
void loop() {
    
}

#include <Adafruit_BMP085.h>
#include <Wire.h>

//-Definations----------------------------------------------------------//
//Analog Devices
int engTempPin = A0;
int exhTempPin = A1;

//Digital Devices
int rearRPMPin = 0;
int engRPMPin = 1;
int resetPin = 2;

//Protocol
enum Protocol {
  INIT, START, ENGINE, MEASURE, RESET, PROBLEM
};

//-Declarations----------------------------------------------------------//
//Devices
Adafruit_BMP085 bmp;

//Environment
float envTemp;
float envPress;
float envAlt;

//Thermos
float engTemp;
float exhTemp;

//-Functions------------------------------------------------------------//
enum Protocol convertIncomingString() {
  if (Serial.read() == 's') {
    return START;
  } else if (Serial.read() == 'e') {
    return ENGINE;
  } else if (Serial.read() == 'm') {
    return MEASURE;
  } else if (Serial.read() == 'r') {
    return RESET;
  } else if (Serial.read() == 'i') {
    return INIT;
  } else {
    return PROBLEM;
  }
}

void readEnvironment () {
    if (!bmp.begin()) {
        Serial.println("Error 180: Environment-Sensor not found");
        return;
    }
    envTemp = bmp.readTemperature();
    envPress = bmp.readPressure();
    envAlt = bmp.readAltitude();
}

void sendEnvironment() {
  Serial.print(envTemp);
  
}

void readSparkplug() {
                                                                                                     
}

void readRearWheel() {

}

void readThermos() {
    //ENGINE
    float u_eng = (analogRead(A0)*5.0)/1024;
    engTemp = (u_eng-1.248)/0.005 + 2;

    //EXHAUST
    float u_exh = (analogRead(A1)*5.0)/1024;
    exhTemp = (u_exh-1.248)/0.005;
}

void setup() {
    Serial.begin(57600);
    pinMode(resetPin, OUTPUT);
    analogReference(EXTERNAL);
}

void loop() {
  switch (convertIncomingString()) {
    case INIT:
      Serial.println("I'm here!");
      Serial.flush();
      break;
    case START:
      readEnvironment();
      String environment = String(envTemp) + "#" + String(envPress) + "#" + String(envAlt);
      Serial.println(environment);
      Serial.flush();
      break;
    case ENGINE:
      readThermos();
      break;
    case MEASURE:
      readSparkplug();
      readRearWheel();
      break;
    case RESET:
      digitalWrite(resetPin, HIGH);
      break;
    case PROBLEM:
      break;
  }
}

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
  START, ENGINE, MEASURE, RESET, PROBLEM
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
}

void loop() {
  /*switch (convertIncomingString()) {
    case START:
      readEnvironment();
      Serial.print(envTemp);
      Serial.print("\t");
      Serial.print(envPress);
      Serial.print("\t");
      Serial.print(envAlt);
      Serial.print("\n");
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
      Serial.println("PROBLEM at Communication!");
  }*/

  readEnvironment();
  readThermos();
  Serial.print("BMP Temperatur: ");
  Serial.println(envTemp);
  Serial.print("BMP Luftdruck: ");
  Serial.println(envPress);
  Serial.print("BMP Höhenmeter: ");
  Serial.println(envAlt);
  Serial.print("Thermo Rot A0: ");
  Serial.println(engTemp);
  Serial.println(analogRead(A0));
  Serial.print("Thermo Gruen A1: ");
  Serial.println(exhTemp);
  Serial.println(analogRead(A1));
}

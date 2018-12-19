#include <Adafruit_BMP085.h>
#include <Wire.h>

//-Definations----------------------------------------------------------//
//Analog Devices
int engTempPin = A0;
int exhTempPin = A1;

//Digital Devices
int rearRPMPin = 3;
int engRPMPin = 2;
int statusPinF = 4;
int statusPinW = 5;
int statusPinS = 6;
int resetPin = 12;

//Protocol
enum Protocol {
  INIT, START, ENGINE, MEASURE, RESET, PROBLEM, FINE, WARNING, SEVERE
};

enum Protocol currentState = PROBLEM;

//-Declarations----------------------------------------------------------//
void serialEvent();

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
    char req = Serial.read();
    if (req == 's') {
      return START;
    } else if (req == 'e') {
      return ENGINE;
    } else if (req == 'm') {
      return MEASURE;
    } else if (req == 'r') {
      return RESET;
    } else if (req == 'i') {
      return INIT;
    } else if (req == 'f') {
      return FINE;
    } else if (req == 'w') {
      return WARNING;
    } else if (req == 'v') {
      return SEVERE;
    } else {
      return PROBLEM;
    }
}

void readEnvironment () {
    if (!bmp.begin()) {
        setStatusSevere();
        return;
    }
    envTemp = bmp.readTemperature();
    envPress = bmp.readPressure();
    envAlt = bmp.readAltitude();
    if (envTemp == 0 || envPress == 0 || envAlt == 0) {
      setStatusWarning();
    }
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
    float u_eng = (analogRead(A0)*4.94)/1024;
    engTemp = (u_eng-1.248)/0.005 + 2;

    //EXHAUST
    float u_exh = (analogRead(A1)*4.94)/1024;
    exhTemp = (u_exh-1.248)/0.005;
}

void testTemps() {
  readEnvironment();
  readThermos();
  Serial.println("---");
  Serial.println(envTemp);
  Serial.println(engTemp);
  Serial.println(exhTemp);
  Serial.flush();
}

void setStatusFine() {
  digitalWrite(statusPinF, LOW);
  digitalWrite(statusPinW, HIGH);
  digitalWrite(statusPinS, HIGH);
}

void setStatusWarning() {
  digitalWrite(statusPinF, HIGH);
  digitalWrite(statusPinW, LOW);
  digitalWrite(statusPinS, HIGH);
}

void setStatusSevere() {
  digitalWrite(statusPinF, HIGH);
  digitalWrite(statusPinW, HIGH);
  digitalWrite(statusPinS, LOW);
}

void setStatusMaxProblem() {
  digitalWrite(statusPinF, HIGH);
  digitalWrite(statusPinW, LOW);
  digitalWrite(statusPinS, LOW);
}


void setup() {
    Serial.begin(57600, SERIAL_8N1);
    Serial.println("BES Dyno started");

    pinMode(resetPin, OUTPUT);
    pinMode(statusPinF, OUTPUT);
    pinMode(statusPinW, OUTPUT);
    pinMode(statusPinS, OUTPUT);
    
    digitalWrite(resetPin, HIGH);
    digitalWrite(statusPinF, LOW);
    digitalWrite(statusPinW, HIGH);
    digitalWrite(statusPinS, HIGH);
    
    analogReference(EXTERNAL);
}

void loop() {
  switch (currentState) {
    case INIT:
      Serial.println(":BES Dyno is ready!;");
      Serial.flush();
      setStatusFine();
      break;
    case START:
      readEnvironment();
      String environment = ':' + String(envTemp) + '#' + String(envPress) + '#' + String(envAlt) + ';';
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
      Serial.println("RESET");
      Serial.flush();
      digitalWrite(resetPin, LOW);
      delay(200);
      break;
    case PROBLEM:
      break;
    case FINE:
      setStatusFine();
      break;
    case WARNING:
      setStatusWarning();
      break;
    case SEVERE:
      setStatusSevere();
      break;
  }

}


void serialEvent() {
  while(Serial.available()) {
    currentState = convertIncomingString();
  }
}


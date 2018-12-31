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

void readEnvironment () {
    if (!bmp.begin()) {
        setStatusSevere();
        return;
    } else {
      envTemp = bmp.readTemperature();
      envPress = bmp.readPressure();
      envAlt = bmp.readAltitude();
      if (envTemp == 0 || envPress == 0 || envAlt == 0) {
        setStatusWarning();
      }
   }
}

void readThermos() {
    //ENGINE
    float u_eng = (analogRead(A0)*4.94)/1024;
    engTemp = (u_eng-1.248)/0.005 + 2;

    //EXHAUST
    float u_exh = (analogRead(A1)*4.94)/1024;
    exhTemp = (u_exh-1.248)/0.005;

    if (engTemp <= 0 || exhTemp <= 0) {
      setStatusWarning();
    }
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

void setStatusMaxProblems() {
  digitalWrite(statusPinF, HIGH);
  digitalWrite(statusPinW, LOW);
  digitalWrite(statusPinS, LOW);
}

void visualizeInitialization() {
  setStatusFine();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusSevere();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusFine();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusSevere();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusFine();
  delay(50);
  setStatusWarning();
  delay(50);
  setStatusSevere();
  delay(50);
  setStatusWarning();
}

void visualizeInitComplete() {
  setStatusFine();
  delay(200);
  setStatusWarning();
  delay(200);
  setStatusSevere();
  delay(200);
  setStatusWarning();
  delay(200);
  setStatusFine();
}


//Initialize BESDyno

void setup() {
    Serial.begin(57600, SERIAL_8N1);

    pinMode(resetPin, OUTPUT);
    pinMode(statusPinF, OUTPUT);
    pinMode(statusPinW, OUTPUT);
    pinMode(statusPinS, OUTPUT);
    
    digitalWrite(resetPin, HIGH);

    visualizeInitialization();
    
    analogReference(EXTERNAL);
}


//Main

void loop() {
}


//ISR for Communication

//INIT:        :BESDyno;
//START:       :envTemp#envPress#envAlt;
//ENGINE:      :engTemp#exhTemp;
//MEASURE:     :;
//MEASURENO:   :;
//FINE:        :FINE;
//WARNING:     :WARNING;
//SEVERE:      :SEVERE;
//MAXPROBLEMS: :MAXPROBLEMS;

void serialEvent() {
  while(Serial.available()) {
    char req = (char)Serial.read();
    
    if(req == 'i') {
      Serial.println(":BESDyno;");
      Serial.flush();
      visualizeInitialization();
      delay(50);
      visualizeInitComplete();
      
    } else if (req == 's') {
      setStatusFine();
      readEnvironment();
      String environment = ':' + String(envTemp) + '#' + String(envPress) + '#' + String(envAlt) + ';';
      Serial.println(environment);
      Serial.flush();
      
    } else if (req == 'e') {
      setStatusFine();
      readThermos();
      String thermos = ':' + String(engTemp) + '#' + String(exhTemp) + ';';
      Serial.println(thermos);
      Serial.flush();
      
    } else if (req == 'm') {
      Serial.println(":;");
      Serial.flush();
      
    } else if (req == 'n') {
      Serial.println(":;");
      Serial.flush();
      
    } else if (req == 'f') {
      setStatusFine();
      Serial.println(":FINE;");
      Serial.flush();
      
    } else if (req == 'w') {
      setStatusWarning();
      Serial.println(":WARNING;");
      Serial.flush();
      
    } else if (req == 'v') {
      setStatusSevere();
      Serial.println(":SEVERE;");
      Serial.flush();
      
    } else if (req == 'x') {
      setStatusMaxProblems();
      Serial.println(":MAXPROBLEMS;");
      Serial.flush();
    }
  }
}


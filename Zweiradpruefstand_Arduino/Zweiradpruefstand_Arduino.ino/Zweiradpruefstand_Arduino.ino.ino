#include <Adafruit_BMP085.h>
#include <Wire.h>
#include <avr/pgmspace.h>

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

PROGMEM const uint32_t crc_table[16] = {
   0x00000000, 0x1db71064, 0x3b6e20c8, 0x26d930ac,
   0x76dc4190, 0x6b6b51f4, 0x4db26158, 0x5005713c,
   0xedb88320, 0xf00f9344, 0xd6d6a3e8, 0xcb61b38c,
   0x9b64c2b0, 0x86d3d2d4, 0xa00ae278, 0xbdbdf21c
};

unsigned long crc_update(unsigned long crc, byte data)
{
   byte tbl_idx;
   tbl_idx = crc ^ (data >> (0 * 4));
   crc = pgm_read_dword_near(crc_table + (tbl_idx & 0x0f)) ^ (crc >> 4);
   tbl_idx = crc ^ (data >> (1 * 4));
   crc = pgm_read_dword_near(crc_table + (tbl_idx & 0x0f)) ^ (crc >> 4);
   return crc;
}

unsigned long crc_string(char *s)
{
 unsigned long crc = ~0L;
 while (*s)
   crc = crc_update(crc, *s++);
 crc = ~crc;
 
 return crc;
}

char* string2char(String command){
    if(command.length()!=0){
        char *p = const_cast<char*>(command.c_str());
        return p;
    }
}

String createCRC(String msg) {
  return String(crc_string(string2char(msg)));
}

String createTelegram(String msg) {
  return ':' + msg + '>' + createCRC(msg) + ';';
}

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
  float u_eng = (analogRead(A0) * 4.94) / 1024;
  engTemp = (u_eng - 1.248) / 0.005 + 2;

  //EXHAUST
  float u_exh = (analogRead(A1) * 4.94) / 1024;
  exhTemp = (u_exh - 1.248) / 0.005;

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

void setNoStatus() {
  digitalWrite(statusPinF, LOW);
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
  delay(200);
  setStatusWarning();
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
  setNoStatus();

  analogReference(EXTERNAL);
}


//Main

void loop() {
}


//ISR for Communication

//INIT:        :BESDyno>crc;
//START:       :envTemp#envPress#envAlt>crc;
//ENGINE:      :engTemp#exhTemp>crc;
//MEASURE:     :;
//MEASURENO:   :;
//FINE:        :FINE>crc;
//WARNING:     :WARNING>crc;
//SEVERE:      :SEVERE>crc;
//MAXPROBLEMS: :MAXPROBLEMS>crc;

//CRC is calculated without ':' and ';'
//Every Response: :Message>Checksum;

void serialEvent() {
  while (Serial.available()) {
    char req = (char)Serial.read();

    if (req == 'i') {
      visualizeInitialization();
      String init = "BESDyno";
      Serial.println(createTelegram(init));
      Serial.flush();
      delay(50);
      visualizeInitComplete();

    } else if (req == 's') {
      setStatusFine();
      readEnvironment();
      String environment = String(envTemp) + '#' + String(envPress) + '#' + String(envAlt);
      Serial.println(createTelegram(environment));
      Serial.flush();

    } else if (req == 'e') {
      setStatusFine();
      readThermos();
      String thermos = String(engTemp) + '#' + String(exhTemp);
      Serial.println(createTelegram(thermos));
      Serial.flush();

    } else if (req == 'm') {
      setStatusMaxProblems();
      Serial.println(":;");
      Serial.flush();

    } else if (req == 'n') {
      setStatusMaxProblems();
      Serial.println(":;");
      Serial.flush();

    } else if (req == 'f') {
      setStatusFine();
      Serial.println(createTelegram("FINE"));
      Serial.flush();

    } else if (req == 'w') {
      setStatusWarning();
      Serial.println(createTelegram("WARNING"));
      Serial.flush();

    } else if (req == 'v') {
      setStatusSevere();
      Serial.println(createTelegram("SEVERE"));
      Serial.flush();

    } else if (req == 'x') {
      setStatusMaxProblems();
      Serial.println(createTelegram("MAXPROBLEMS"));
      Serial.flush();
    }
  }
}


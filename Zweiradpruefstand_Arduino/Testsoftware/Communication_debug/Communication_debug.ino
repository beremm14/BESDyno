int statusPinF = 4;
int statusPinW = 5;
int statusPinS = 6;

void serialEvent();

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
  
  pinMode(statusPinF, OUTPUT);
  pinMode(statusPinW, OUTPUT);
  pinMode(statusPinS, OUTPUT);

  setStatusWarning();
}

void loop() {
}

void serialEvent() {
  while(Serial.available()) {
    char req = Serial.read();
    if(req == 'i') {
      setStatusFine();
      Serial.println(":BESDyno;");
      Serial.flush();
    }
  }
}


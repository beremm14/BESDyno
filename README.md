# BESDyno (Zweiradprüfstand)

## Team
* **Berger Emil**: Software
* **Ehmann Julian**: Elektronik
* **Sammer Daniel**: Maschinenbau

## Projekt
Dieses Repository beinhaltet die Software für einen [Arduino](https://www.arduino.cc) UNO, an den die Sensoren des Prüfstands angeschlossen sind und das PC-Interface, das die vom Arduino empfangenen Daten auswertet und grafisch darstellt.

## Installation
### PC-Interface
Um das PC-Interface ausführen zu können, muss die [Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) installiert werden. Wenn dies geschehen ist, kann das Programm einfach über BESDyno.jar gestartet werden, es sind keine weiteren Installationsschritte erforderlich.

### Arduino-Software
  Um den Arduino-Code auf einen Arduino zu programmieren, wird [Xcode](https://developer.apple.com/xcode/) (macOS) und das Plug-In [embedXcode](https://embedxcode.com/site/) benötigt. Alternativ kann auch die .ino-Datei in der [Arduino-IDE](https://www.arduino.cc/en/Main/Software) eingelesen werden, dann muss aber der obere Header (generierte Code des Plug-Ins) gelöscht werden.  
  Der Arduino muss nur angeschlossen werden und der Name des Geräts in der Make-File geändert werden, bei neueren macOS-Versionen cu.usb*, bei älteren tty.usb* - einfach ausprobieren :)  
  
## For Developer
  **/Zweiradpruefstand_GUI**: Das PC-Interface wurde in NetBeans geschrieben, es wird die IDE benötigt, sowie [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html).  
  **/Zweiradpruefstand_Arduino**: Protoyp-Software, die zur Demonstration der Funktionsfähigkeit sowie für erste Messungen im Rahmen der Diplomarbeit angefertigt wird.  

# Dokumentation
## Wichtigste Funktionen
  * Einlesen der Sensordaten  
  * Übertragung der Daten per UART  
  * Auswertung der Daten
  * Live-Ansicht von Drehzahl und Geschwindigkeit  
  * Berechnung von Leistung und Drehmoment über die Drehzahlen  

## Arten von Daten und deren Erfassung
  * Umgebungstemperatur und -luftdruck: BMP180 (I2C)  
  * Motor- und Abgastemperatur: Thermoelemente (OPV-Verstärker, AnalogIn)  
  * Walzendrehzahl: Induktiver Näherungsschalter (InterruptPin)  
  * Motordrehzahl: Strommesszange am Zündkabel (Schmitt-Trigger, InterruptPin)  

## Protokoll
Das Protokoll ist verbindungslos und Request-Response- (Master-Slave) -orientiert. Die Datenübertragung erfolgt textuell, die CRC32-Prüfsumme wird mit einer im Arduino gespeicherte Tabelle realisiert und nur über die Daten berechnet (nicht über `:`, `;`, `>`), der Hash `#` wird aber berücksichtigt und sieht sich als Teil der Daten.

### Aufbau:
| Doppelpunkt `:` | Daten, bei mehreren Werten mit Hash `#` getrennt | Bigger than `>` | CRC32-Prüfsumme | Semicolon `;` |
| --------------- | ------------------------------------------------ | ----------- | --------------- | ------------- |

### Requests (mit Response-Beispielen)
| Request | Request-String Rx | Response | Response-String Tx |
| ------- | ------------- | -------- | ------------------ |
| INIT | `i` | Initialisierungs-Antwort | `:BESDyno>CRC;` |
| VERSION | `p` | Version der Arduino-Software | `:Version>CRC;` |
| START | `s` | Umweltdaten (Temperatur, Luftdruck, Seehöhe) | `:Temperatur#Luftdruck#Seehöhe>CRC;` oder `:BMP-ERROR>CRC;` |
| ENGINE | `e` | Motor- & Abgastemperatur | `:Motortemp.#Abgastemp.>CRC;` |
| KILL | `k` | *setzt RPM-Counter auf 0* | `:KILL>CRC;` |
| ALL | `a` | Motor- & Walzendrehzahl + Motor- & Abgastemperatur | `:Motor-Zeit#Walzen-Zeit#Motortemp.#Abgastemp.#Zeit>CRC;` |
| MEASURE | `m` | Motor- & Walzendrehzahl | `:Motor-Zeit#Walzen-Zeit#Zeit>CRC;` |
| MEASURENO | `n` | Nur Walzendrehzahl | `:Walzen-Zeit#Zeit>CRC;` |
| FINE | `f` | *setzt grüne LED* | `:FINE>CRC;` |
| WARNING | `w` | *setzt gelbe LED* | `:WARNING>CRC;` |
| SEVERE | `v` | *setzt rote LED* | `:SEVERE>CRC;` |
| MAXPROBLEMS | `x` | *setzt gelbe & rote LED* | `:MAXPROBLEMS>CRC;` |
| DEBUG *(nicht Teil des Protokolls)* | `d` | *gibt am Terminal eine Übersicht über alle aktuellen Messwerte* | *nur zum Debuggen, für den Menschen lesbar* |

  **DEBUG** gibt am Terminal bzw. Monitor eine Liste mit den aktuellen Messwerten aus. Diese Response ist nicht vom Java-Programm decodierbar und wird deshalb auch nicht von jenem angefordert. Das `d` wird in die Eingabezeile des Monitors eingegeben, ein spezielles Zeilenende ist wie auch für die anderen Requests nicht erforderlich.  
  
### Idle-RQ
  Das Protokoll verzichtet auf weitere Sicherungsvorkehrungen, wie zum Beispiel *Idle-RQ*, neben der CRC-Prüfsumme.  
  Die Datenübertragung würde mit so einem System viel langsamer vonstattengehen, es wird aber vorausgesetzt, dass so viele Messwerte wie möglich eingelesen werden. Im Endeffekt würde *Idle-RQ* auch keinen Sinn machen, weil fehlerhafte/fehlende Daten - deren Chance aufzutreten im unteren Prozentbereich liegen - verworfen, bzw nicht auffallen würden, da das Diagramm am Ende über die erhaltenen Daten interpoliert wird.  

## CommLog-Files
  Im Entwicklunsmodus wird beim Schließen des Hauptfensters die gesamte Kommunikation als .log-Datei in das Verzeichnis `User/Bike-Files/Service_Files` gespeichert - wie auch das Logging-Protokoll.  
  
  Diese Datei ist so zu lesen:  
### REQUESTS
`Zeitstempel : Name des Requests`
### RESPONSES
`Zeitstempel : Response CRC: *empfangener CRC* <-> *berechneter CRC*`

## Messvorgang
  Im Einstellungs-Dialogfenster werden u.A. folgende Parameter eingegeben:  
  * Hysterese Zeitspanne (hysteresisTime)  
  * Geschwindigkeit wenn bereit (idleVelocity)  
  * Hysterese Geschwindigkeit ± (hysteresisVelocity)  
  * Startgeschwindigkeit (startVelocity)  
  * Stoppgeschwindigkeit (stopVelocity)  
  * Motordrehzahl wenn bereit (idleRPM)  
  * Hysterese Motordrehzahl ± (hysteresisRPM)  
  * Start-Motordrehazhl (startRPM)  
  * Stop-Motordrehzahl (stopRPM)  
  
  Es gibt zwei Modi:
  * **DROP-RPM**: Messen bis zur Höchstdrehzahl, Abfallen lassen und Messung bei Erreichen der Startdrehzahl stoppen  
    * *Diese Methode bietet die Möglichkeit um mit Hilfe der Schleppleistung - die beim Abfallen der Drehzahl gemessen wird - auf die reale Motorleistung zurückschließen zu können.*  
  * **Start-Stop**: Messen bis zu einer eingegeben Enddrehzahl  
    * *Mit dieser Methode kann nur die Hinterradleistung gemessen werden, sie ist aber für den Motor schonender (für Zweitaktmotoren wird diese Methode ausdrücklich empfohlen!).*  
  
  Der Messvorgang besteht aus folgenden Zuständen:  
  
  | SHIFT_UP | WAIT | READY | MEASURE | FINISH |
  | -------- | ---- | ----- | ------- | ------ |
  
  | Zustand | Wechsel wenn: | User-Eingabe | Programmmodus | Messmethodik |
  | ------- | ------------- | ------------ | ------------- | ------------ |
  | SHIFT_UP | Bei **einmaligem** Erreichen (**≤**) von **startRPM** | Hochschalten in den letzten Gang und Abfallen lassen bis zur Startgeschwindigkeit | x | *beide* |
  | WAIT | Wenn sich die Drehzahl innerhalb der Hysterese Zeitspanne bei **idleRPM ± hysteresisRPM** eingependelt hat | Warten... | x | *beide* |
  | READY | Bei **einmaligem** Erreichen (**≥**) von **startRPM** | Gas geben! | x | *beide* |
  | MEASURE | Bei **einmaligem** Erreichen (**≤**) von **startRPM** | Bei der Höchstdrehzahl abfallen lassen | MESSEN | **DROP-RPM** |
  | MEASURE | Bei **einmaligem** Erreichen (**≥**) von **stopRPM** | Nach eigenem Ermessen (schonend) runterschalten | MESSEN | **Start-Stop** |
  | FINISH | Nach dem Ende der Berechnung -> Ende des Messvorgangs | Warten... | BERECHNEN | *beide* |
  
  *Anmerkungen*: Wenn die Motordrehzahl nicht gemessen wird, erfolgt das Switchen der States sowie das Einpendeln über die Geschwindigkeit der Walze. Wenn ein Zweirad mit Automatik-Getriebe gemessen wird, ist der erste State *SHIFT_UP* nicht notwendig und wird übersprungen.  

## Numerische Simulation
  *Variablen und Einheiten:*  
  * Leistung **P** (W)  
  * Moment **M** (Nm)  
  * Trägheitsmoment **I** (kgm^2)  
  * Drehzahl **n** (U/min)  
  * Winkelgeschwindigkeit **ω** (rad/s)  
  * Winkelbeschleunigung **α** (rad/s^2)  
  * Bahngeschwindigkeit **v** (m/s)  
  * Zeit **t** (µs)  
  * Periodendauer **t_motor** & **t_walze** (µs)  
  * Radius **r** (m)  
  * Luftdruck **p** (hPa)  
  * Umgebungstemperatur **T** (°C)  
  
| Wert | Formel |
| ---- | ------ |
| n_motor = | 60,000,000 / t_motor |
| n_walze = | 1,000,000 / (26 \* t_walze) |
| ω_motor = | (2π/60) \* n_motor |
| ω_walze = | (2π/60) \* n_walze |
| v_walze = | ω_walze \* r_walze |
| Δω = | ω_walze(i) - ω_walze(i-1) |
| α_walze = | Δω / t(i) |
| P_Rad = | I_Walze \* α_walze \* ω_walze |
| P_Motor = | P_Rad + P_Schlepp |
| P_Norm = | (1013 / p_Luft) \* sqrt((T_Luft+273.15)/293.15) \* P_Motor |
| M_Motor = | P_Norm / ω_motor |

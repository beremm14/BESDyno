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

## Dokumentation
### Protokoll
Das Protokoll ist verbindungslos und Request-Response- (Master-Slave) -orientiert.
#### Aufbau:
| Doppelpunkt `:` | Daten, bei mehreren Werten mit Hash `#` getrennt | Bigger than `>` | CRC32-Prüfsumme | Semicolon `;` |
| --------------- | ------------------------------------------------ | ----------- | --------------- | ------------- |

#### Requests (mit Response-Beispielen)
| Request | Request-String Rx | Response | Response-String Tx |
| ------- | ------------- | -------- | ------------------ |
| INIT | `i` | Initialisierungs-Antwort | `:BESDyno>CRC;` |
| VERSION | `p` | Version der Arduino-Software | `:Version>CRC;` |
| START | `s` | Umweltdaten (Temperatur, Luftdruck, Seehöhe) | `:Temperatur#Luftdruck#Seehöhe>CRC;` oder `:BMP-ERROR>CRC;` |
| ENGINE | `e` | Motor- & Abgastemperatur | `:Motortemp.#Abgastemp.>CRC;` |
| KILL | `k` | *setzt RPM-Counter auf 0* | `:KILL>CRC;` |
| MEASURE | `m` | Motor- & Walzendrehzahl | `:Motor-Counts#Motorzeit#Walzen-Counts#Walzenzeit>CRC;` |
| MEASURENO | `n` | Nur Walzendrehzahl | `:Walzen-Counts#Zeit>CRC;` |
| FINE | `f` | *setzt grüne LED* | `:FINE>CRC;` |
| WARNING | `w` | *setzt gelbe LED* | `:WARNING>CRC;` |
| SEVERE | `v` | *setzt rote LED* | `:SEVERE>CRC;` |
| MAXPROBLEMS | `x` | *setzt gelbe & rote LED* | `:MAXPROBLEMS>CRC;` |

### CommLog-Files
  Im Entwicklunsmodus wird beim Schließen des Hauptfensters die gesamte Kommunikation als .log-Datei in das Verzeichnis `User/Bike-Files/Service_Files` gespeichert - wie auch das Logging-Protokoll.  
  
  Diese Datei ist so zu lesen:  
#### REQUESTS
`Zeitstempel : Name des Requests`
#### RESPONSES
`Zeitstempel : Response CRC: *empfangener CRC* <-> *berechneter CRC*`

### Messvorgang
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
  * **Start-Stop**: Messen bis zu einer eingegeben Enddrehzahl  
  *Die zweite Methode wurde eingeführt, weil es für Zweitaktmotoren problematisch sein kann die Drehzahl im letzten Gang so weit abfallen zu lassen.*  
  
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
  
  

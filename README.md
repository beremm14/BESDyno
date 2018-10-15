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
  **/Zweiradpruefstand_AVR**: Software, die zur Endauslieferung des Produktes verwendet wird.  
  **/Zweiradpruefstand_Arduino**: Protoyp-Software, die zur Demonstration der Funktionsfähigkeit sowie für erste Messungen im Rahmen der Diplomarbeit angefertigt wird.  

# BESDyno (Zweiradprüfstand)

## Team
* **Berger Emil**: Software
* **Ehmann Julian**: Elektronik
* **Sammer Daniel**: Mechanik

## Projekt
Dieses Repository beinhaltet die Software für einen [Arduino](https://www.arduino.cc) UNO, an den die Sensoren des Prüfstands angeschlossen sind und das PC-Interface, das die vom Arduino empfangenen Daten auswertet und grafisch darstellt.

## Installation
### PC-Interface
Um das PC-Interface ausführen zu können, muss die [Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) installiert werden. Wenn dies geschehen ist, kann das Programm einfach über Zweiradpruefstand.jar gestartet werden, es sind keine weiteren Installationsschritte erforderlich.

### Arduino-Software
  Um den AVR-Code auf einen Arduino zu programmieren, wird [Xcode](https://developer.apple.com/xcode/) (macOS) benötigt.
  Der Arduino muss nur angeschlossen werden und der Name des Geräts in der Make-File geändert werden.
  
## For Developer
  **/Zweiradpruefstand_GUI**: Das PC-Interface wurde in NetBeans geschrieben, es wird die IDE benötigt, sowie [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html).  
  **/Zweiradpruefstand_AVR**: Die Arduino-Software wurde in Xcode mit dem Plug-In [xAVR](https://github.com/jawher/xavr) geschrieben.

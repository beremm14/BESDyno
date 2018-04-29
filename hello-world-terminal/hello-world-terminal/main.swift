//
//  main.swift
//  hello-world-terminal
//
//  Created by Emil Berger on 21.03.18.
//  Copyright © 2018 Emil Berger. All rights reserved.
//

import Foundation
/*
 Foundation entspricht der stdio.h und stdlib.h in C
 Foundation-Klassen beginnen üblicherweise mit NS (NSArray, NSError, NSString)
 Foundation wird für Terminal-Programme verwendet, Cocoa und UIKit für GUI-Anwendungen auf macOS und iOS
*/

print("Hello, World!")


//Funktionen:
func sayHello (name: String) {
    print("Hello, \(name)!")
}

//den Namen des Users ermitteln:
var username = NSFullUserName() //Emil Berger als String

//Funktion sayHello aufrufen:
sayHello(name: username)


let s = """
    Das
    ist
    eine
    mehrzeilige
    Zeichenkette!
    """
//oder:
let s2 = "Das\nist\neine\nmehrzeilige\nZeichenkette!"


//Verzweigung
let m = 3
if m == 3 {
    print("\n")
    print(s)
    print("\n")
    print(s2)
    print("\n")
    print("m=3")
}


//Arrays
var data = [1, 2, 3, 4]         //Array
var newData = data.map() {$0+3} //Anonyme Funktion, zählt zu jedem Feld 3 dazu
print(data)                     //[1, 2, 3, 4]
print(newData)                  //[4, 5, 6, 7]

print("\n")


//Variablen und Datentypen
var a = 3 //Variable
let b = 5 //Konstante, wie final in Java

//Datentypen werden automatisch generiert
//a = 2.5 //geht nicht weil durch die Definition von a = 3 der Datentyp Integer für a verwendet wurde

//Datentypen umwandeln
let i = 2
let sq = sqrt(Double(i)) //Mittels Typecast wird eine Variable umgewandelt

let x1 = 3
let x2 = 0.2
let x3 = Double(x1) + x2

//Datentypen festlegen
var d1: Double = 3
var i1: Int16 = 5
var i2: Int32 = 9
var s1: String = "Hallo"

print("\(d1) \(i1) \(i2) \(s1)") //Ausgabe von allem

print("\n")

//Ausdrücke
var result = 3*7
print("Resultat = \(result)") //Die Variable wird in die Zeichenkette mit \() eingefügt, Datentyp wird automatisch gewählt, wie %d oder %f bei Java


//Optionals
var opt: Int? = 3 //Optionals sind Variablen, die den Zustand nil (NULL) annehmen können
opt = nil
//Beispiel:
var nmb = Int("123") //123
nmb = Int("abc")     //nil, weil abc ist nicht int
if nmb != nil {      //Plausi-Check
    print (nmb!)     //Macht aus Int? --> Int
}


//Tupel
let tupe1 = (1, 2, 3, "abc", true) //Aufzählungen mehrerer Werte, unterschiedlicher Datentypen

let coord3D = (3.3, 2.4, 0.7)
let (x,y,z) = coord3D //Über Tupel kann nicht iteriert werden, aber man kann sie in mehreren Variablen speichern
print("\nTupel:")
print("\(x) \(y) \(z)")


print("\n")


//Zweidimensionales Integer-Array
var chessboard = Array(repeating:
    Array(repeating: 0, count: 8), count: 8)


//Enum-Arrays
var colorcodes = ["red": 0xff0000,
                  "green": 0x00ff00,
                  "blue": 0x0000ff]
colorcodes ["white"] = 0xffffff //Element hinzufügen

for (cname, cval) in colorcodes { //Schleife über alle Elemente
    let hex = NSString(format: "%06X", cval)
    print ("Farbcode \(hex) = Farbe \(cname)")
}
print("\n")


//Schleifen

//For-Each
print("For-Each:")
for i in data {
    print(i)
}
print("\n")

//While
print("While:")
var cnt = 1
while cnt<4 {
    print(cnt)
    cnt+=1
}
print("\n")

//Repeat-While
print("Repeat-While:")
cnt = 1
repeat {
    print(cnt)
    cnt += 1
} while cnt<4
print("\n")


//Verzweigungen
var rand = Int(arc4random()) //Zufallszahl

if x<0 {
    print ("x ist negativ")
} else if x == 0 {
    print("x ist 0")
} else {
    print("x ist positiv")
}

print("\n")


//Zahl einlesen:
print("Bitte Zahl eingeben: ")
let intLine = readLine()
var choose = Int(intLine!)

//Switch-Case
switch choose {
case 1?:
    print("choose ist 1")
case 2?, 3?:
    print("chooser ist 2 oder 3")
default:
    print("chooser ist größer 3 oder kleiner 1")
}
print("\n")


//Rückgabewerte
func multipliziere (a: Double, b: Double) -> Double { //nach dem Pfeil: Datentyp des Rückgabewerts (wenn nicht, dann void)
    return a*b
}

var produkt = multipliziere(a: 5.5, b: 4.3)
print(produkt)
print("\n")


//Exceptions
let str = "Hi"
do {
    try str.write(toFile: "hi.txt", atomically: true, encoding: .utf8)
} catch {
    print("Fehler: \(error)")
}

//Kurzschreibweise
try? str.write(toFile: "kurz.txt", atomically: true, encoding: .utf8)
//try! beendet das Programm im Falle eines Fehlers


//Ternäre Operatoren
let tr: Bool = true
var trx: Int = (tr ? 1 : 2)
//wenn tr==true, dann ist trx 1, sonst 2


//Enumerations
enum Color {
    case red, green, blue, white, black
}
var col = Color.red
if col==Color.red {
    print("col enthält die Farbe rot\n")
}

//Enumerations mit Definitionen
enum Color1: Int {
    case red = 0, green = 1, blue = 2
}
print("Farbe grün: \(Color1.green.rawValue)\n")


//Nil Coalescing
var nii: Int
let inputString = "123"
let defaultValue = 0
let n = Int(inputString)

//Standardvariante
if n == nil {
    nii = defaultValue
} else {
    nii = n!
}

//Swift-Variante
nii = (n == nil) ? defaultValue : n!


//Versionstest
if #available(macOS 10.12, *) {
    print("macOS Sierra oder höher\n")
} else {
    print("zu alt!\n")
}


//Funktion mit Tupel als Rückgabewert
func sincos (x: Double) -> (Double, Double) {
    return (sin(x), cos(x))
}


//Funktionen ohne Rückkehr
func f() -> Never {
    fatalError("Aus!")
}


//Strukturen
struct BeispielStruct {
    var i: Int
    var d: Double
}


//Datentypen umwandeln (mit Kontrolle)
let dd = arc4random_uniform(1000)
if let ui = UInt8(exactly: dd) {
    print(ui)
} else {
    print("dd ist zu groß für eine UInt8-Zahl!\n")
}


//Zufallszahlen
var oneToTen = arc4random_uniform(10) //Zahl zwischen 0 und 9
var doubleZZ = Double(arc4random())


//Boolesche Werte
var resultat: Bool
resultat = true
resultat = !resultat //resultat ist jetzt false
print("\(resultat)\n")


//NSNumber
let n1 = NSNumber(value: 2.7)
let n2 = NSNumber(value: 17)
let n3 = NSNumber(value: true)
let n4 = NSNumber(value: arc4random())

print("n1 als Double: \(n1.doubleValue)")
print("n1 als Integer: \(n1.intValue)")
print("n1 als Boolean: \(n1.boolValue)")
print("n2 als Double: \(n2.doubleValue)")
print("n3 als Integer: \(n3.intValue)")
print("n4 als Integer: \(n4.intValue)\n")


//Datum und Uhrzeit
let now = Date()
print(now)

//Formatieren
let formatter = DateFormatter()
formatter.dateFormat = "dd.MM.yyyy HH:mm:ss"
print(formatter.string(from: now))

let xformatter = DateFormatter()
xformatter.locale = Locale(identifier: "de_AT")
xformatter.dateFormat = "dd. MMMM yyyy HH:mm:ss"
print(xformatter.string(from: now))


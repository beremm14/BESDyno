#include "global.h"

#include <stdio.h>
#include <string.h>

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

#include "app.h"
#include "mon.h"
#include "sys.h"

// defines
// ...


// declarations and definations

enum State {MUXVCC, MSTARTVCC, MUXTEMP, MSTARTTEMP};
enum State state = MUXVCC;

volatile struct App app;


// functions


void app_init (void) {
    memset((void *)&app, 0, sizeof(app));
    
    ADMUX  = (1<<REFS0) | (1<<ADLAR) | 0x0e;
    //REFS0 setzt den Referenzwert auf die Versorgungsspannung
    //ADLAR lässt den Wert im ADC links ausrichten
    //0x0e setzt die Bandgap-Reference auf 1.1V, Multiplexerkanal
    
    ADCSRA = (1<<ADEN) | (1<<ADPS2) | (1<<ADPS1) | (1<<ADPS0);
    //ADEN enabled den ADC
    //Die weiteren 3 Bits setzen den Prescaler auf 128
    //ADC-Frequenz = Arduino-Frequenz/Prescaler
}


//--------------------------------------------------------

void app_main (void) {
    float vcc = (1.1 * 256 * 0.9784735812) / ADCH; //In Klammer, damit nicht einzeln gerechnet
    //256: weil 8-Bit-Wert
    //Korrekturwert, gemessen, real (5V/5.11V)
    int vk = (int)vcc;
    int nk = (int)((vcc- (float)vk) * 100.0);
    
    printf("ADC = 0x%02x VCC = %d.%02d         \n", ADCH, vk, nk);
}

//--------------------------------------------------------

void app_task_1ms (void) {
    ADCSRA |= (1<<ADSC);
}
void app_task_2ms (void) {}
void app_task_4ms (void) {}
void app_task_8ms (void) {}
void app_task_16ms (void) {}
void app_task_32ms (void) {}
void app_task_64ms (void) {}
void app_task_128ms (void) {}



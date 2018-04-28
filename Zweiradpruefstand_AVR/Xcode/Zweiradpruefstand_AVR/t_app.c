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

volatile struct App app;


// functions

void adc_init (void) {
    ADMUX = (1<<REFS0);    //select AVCC as reference
    ADSRA = (1<<ADEN) | 7; //enable and prescale = 128 (16MHz/128 = 125kHz)
}

int readAdc (char chan) {
    ADMUX = (1<<REFS0) | (chan & 0x0f); //select input and ref
    ADSRA |= (1<<ADSC);                 //start the conversation
    while (ADSRA & (1<<ADSC));          //wait for the end
    return ADCW;
}


void app_init (void) {
    memset((void *)&app, 0, sizeof(app));
}


//--------------------------------------------------------

void app_main (void) {
    
}

//--------------------------------------------------------

void app_task_1ms (void) {}
void app_task_2ms (void) {}
void app_task_4ms (void) {}
void app_task_8ms (void) {}
void app_task_16ms (void) {}
void app_task_32ms (void) {}
void app_task_64ms (void) {}
void app_task_128ms (void) {}




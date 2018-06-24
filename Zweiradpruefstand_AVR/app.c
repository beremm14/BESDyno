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

uint8_t engCounts;
uint8_t wheelCounts;
uint8_t measureTime;


volatile struct App app;


// functions

void app_init (void) {
    engCounts = 0;
    wheelCounts = 0;
    measureTime = 0;
    
    memset((void *)&app, 0, sizeof(app));
    
    PORTD |= (1<<PD2);
    PORTD |= (1<<PD3);
}


//--------------------------------------------------------

void app_main (void) {
    
}

//--------------------------------------------------------

void app_task_1ms (void) {
    measureTime++;
    if (PIND & (1<<PD2)) {
        engCounts++;
    }
    if (PIND & (1<<PD3)) {
        wheelCounts++;
    }
}
void app_task_2ms (void) {}
void app_task_4ms (void) {}
void app_task_8ms (void) {}
void app_task_16ms (void) {}
void app_task_32ms (void) {}
void app_task_64ms (void) {}
void app_task_128ms (void) {}



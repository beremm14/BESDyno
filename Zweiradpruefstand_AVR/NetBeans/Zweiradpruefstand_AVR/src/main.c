//***********************************************************************
// AIIT Template Level 4
// ----------------------------------------------------------------------
// Description:
//   UART-Support, Timer, Task-System, 7-Segment-Support, LCD-Support
//   System-Monitor with command line interface and line-monitor
// ----------------------------------------------------------------------
// Created: Aug 23, 2016 (SX)
// ----------------------------------------------------------------------
// Program with ...
// mkII:   avrdude -c avrisp2 -p m328p -P usb -U flash:w:arduino.elf.hex:i
// STK500: avrdude -c stk500v2 -p m328p -P /dev/ttyUSBx -U flash:w:arduino.elf.hex:i
// USBASP: avrdude -c usbasp -p m328p -U flash:w:arduino.elf.hex:i
//***********************************************************************

#include "global.h"

#include <stdio.h>
#include <string.h>

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <avr/pgmspace.h>

#include "sys.h"
#include "app.h"
#include "mon.h"

// defines
// ...

// declarations and definations
// ...

// constants located in program flash
const char FLASHSTRING_WELCOME[] PROGMEM = "\n\rProgramm ?? ";
const char FLASHSTRING_DATE[] PROGMEM = __DATE__;
const char FLASHSTRING_TIME[] PROGMEM = __TIME__;


int main (void)
{
  sys_init();
  app_init();
#ifdef GLOBAL_MONITOR
  mon_init();
#endif
  sys_printString_P(FLASHSTRING_WELCOME);
  sys_printString_P(FLASHSTRING_DATE);
  printf(" ");
  sys_puts_P(FLASHSTRING_TIME);

#ifdef GLOBAL_SURE_LCD
  printf("LCD ");
  if (sys.lcd.status==1)
  {
    printf("detected and ready to use\n");
    sys_lcd_putString("?? - ");
    sys_lcd_putString(__TIME__);
  }
  else
    printf("not ready (status=%d)\n", sys.lcd.status);
#endif // GLOBAL_SURE_LCD
  sys_newline();

#ifdef GLOBAL_MONITOR
  printf(">");
#endif

  // enable interrupt system
  sei();

  while (1)
  {
    sys_main();
    app_main();
#ifdef GLOBAL_MONITOR
    mon_main();
#endif
  }
  return 0;
}

//***********************************************************************
// Vorlage PRT Level 2
// ----------------------------------------------------------------------
// Beinhaltet:
// UART-Support, Timer, Task-System
// ----------------------------------------------------------------------
// Author: SX 
// Version: 
// Vorlage Version: 19.2.2015 (SX)
//***********************************************************************

#include "global.h"

#include <stdio.h>
#include <string.h>

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

#include "sys.h"
#include "app.h"

// Defines, nur in main.c sichtbar
// ...

// globale Variable, nur in main.c sichtbar
// ...

// Konstante, die im Flash abgelegt werden

int main (void)
{
  sys_init();
  app_init();
  sys_newline();

  // Interrupt-System jetzt einschalten
  sei();

  while (1)
  {
    sys_main();
    app_main();
  }
  return 0;
}

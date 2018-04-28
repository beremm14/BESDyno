#ifndef MON_H_INCLUDED
#define MON_H_INCLUDED

#ifdef GLOBAL_MONITOR

#include "sys.h"
#include <avr/pgmspace.h>

// Deklaration Strukturvariablentyp

struct Mon
{
  uint8_t flags_u8;
};


// Deklaration Strukturvariablen
extern volatile struct Mon mon;
extern const struct Sys_MonCmdInfo MON_PMEMSTR_CMDS[] PROGMEM;


// globale Defines


// Funktionsdeklarationen
void    mon_init            (void);
void    mon_main            (void);
uint8_t mon_getCmdCount     (void);
int8_t  mon_printLineHeader (uint8_t lineIndex);
int8_t  mon_printLine       (uint8_t lineIndex, char keyPressed);

#endif // GLOBAL_MONITOR
#endif // MON_H_INCLUDED

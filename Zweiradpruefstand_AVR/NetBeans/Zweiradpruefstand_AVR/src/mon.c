#include "global.h"

#include <stdio.h>
#include <string.h>

#include <avr/io.h>
#include <util/delay.h>
#include <avr/pgmspace.h>

#include "mon.h"
#include "app.h"
#include "sys.h"

#ifdef GLOBAL_MONITOR

// defines
// ...

// declarations and definations

int8_t mon_cmd_info (uint8_t argc, char *argv[]);
int8_t mon_cmd_test (uint8_t argc, char *argv[]);

const char MON_LINE_WELCOME[] PROGMEM = "Line-Mode: CTRL-X, CTRL-Y, CTRL-C, Return  \n\r";
const char MON_PMEM_CMD_INFO[] PROGMEM = "info\0Application infos\0info";
const char MON_PMEM_CMD_TEST[] PROGMEM = "test\0commando for test\0test";

const struct Sys_MonCmdInfo MON_PMEMSTR_CMDS[] PROGMEM =
{
    { MON_PMEM_CMD_INFO, mon_cmd_info }
  , { MON_PMEM_CMD_TEST, mon_cmd_test }
};

volatile struct Mon mon;

// functions

void mon_init (void)
{
  memset((void *)&mon, 0, sizeof(mon));
}


//--------------------------------------------------------

inline void mon_main (void)
{
}

inline uint8_t mon_getCmdCount (void)
{
  return sizeof(MON_PMEMSTR_CMDS)/sizeof(struct Sys_MonCmdInfo);
}


// --------------------------------------------------------
// Monitor commands of the application
// --------------------------------------------------------

int8_t mon_cmd_info (uint8_t argc, char *argv[])
{
  printf("app.flags_u8  : ");
  sys_printHexBin8(sys.flags_u8);
  sys_newline();
  return 0;
}


int8_t mon_cmd_test (uint8_t argc, char *argv[])
{
  uint8_t i;

  for (i = 0; i<argc; i++)
    printf("%u: %s\n\r", i, argv[i]);

  return 0;
}


// --------------------------------------------------------
// Monitor-Line for continues output
// --------------------------------------------------------

int8_t mon_printLineHeader (uint8_t lineIndex)
{
  if (lineIndex==0)
    sys_printString_P(MON_LINE_WELCOME);
  
  switch (lineIndex)
  {
    case 0: printf("L0 | app.flags_u8"); return 20;
    case 1: printf("L1 | counter  (press 'c' for reset)"); return 40;
    default: return -1; // this line index is not valid
  }
}

int8_t mon_printLine   (uint8_t lineIndex, char keyPressed)
{

  switch (lineIndex)
  {
    case 0:
      printf("   | "); sys_printBin(app.flags_u8, 0);
      return 2;

    case 1:
      {
        static uint16_t cnt = 0;
        if (keyPressed=='c')
        cnt = 0;
        printf("   |  0x%04x   ", cnt++);
      }
      return 2;

    default: return -1;
  }
}

#endif // GLOBAL_MONITOR




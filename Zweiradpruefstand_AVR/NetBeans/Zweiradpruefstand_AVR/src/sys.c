#include "global.h"

#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>
#include <util/delay.h>
#include <avr/pgmspace.h>
#include <avr/eeprom.h>

#include "sys.h"
#include "app.h"
#include "mon.h"

// defines

#ifdef __AVR_ATmega16__
  #define SYS_UART_BYTE_RECEIVED (UCSRA & (1<<RXC))
  #define SYS_UART_UDR_IS_EMPTY (UCSRA & (1<<UDRE))
  #define SYS_UDR UDR
  #define SYS_UART_RECEIVE_VECTOR USART_RXC_vect
  #define SYS_TIMER0_VECTOR TIMER0_COMP_vect
#elif __AVR_ATmega324P__
  #define SYS_UART_BYTE_RECEIVED (UCSR0A & (1<<RXC0))
  #define SYS_UART_UDR_IS_EMPTY (UCSR0A & (1<<UDRE0))
  #define SYS_UDR UDR0
  #define SYS_UART_RECEIVE_VECTOR USART0_RX_vect
  #define SYS_TIMER0_VECTOR TIMER0_COMPA_vect
#elif __AVR_ATmega328P__
  #define SYS_UART_BYTE_RECEIVED (UCSR0A & (1<<RXC0))
  #define SYS_UART_UDR_IS_EMPTY (UCSR0A & (1<<UDRE0))
  #define SYS_UDR UDR0
  #define SYS_UART_RECEIVE_VECTOR USART_RX_vect
  #define SYS_TIMER0_VECTOR TIMER0_COMPA_vect
#elif __AVR_AT90CAN128__
  #define SYS_UART_BYTE_RECEIVED (UCSR0A & (1<<RXC0))
  #define SYS_UART_UDR_IS_EMPTY (UCSR0A & (1<<UDRE0))
  #define SYS_UDR UDR0
  #define SYS_UART_RECEIVE_VECTOR USART0_RX_vect
  #define SYS_TIMER0_VECTOR TIMER0_COMP_vect

#endif

#ifdef GLOBAL_MONITOR

  #define SYS_MONITOR_FLAG_LINEMODE 0x01
  #define SYS_MONITOR_FLAG_LINE     0x02
  #define SYS_MONITOR_FLAG_CONT     0x04

  #define SYS_MONITOR_CTRL_C        0x03
  #define SYS_MONITOR_CTRL_X        0x18
  #define SYS_MONITOR_CTRL_Y        0x19

  struct Sys_Monitor
  {
    uint8_t flags;
    uint8_t lineIndex;
    uint8_t cursorPos;
    char cmdLine[40];
  };
  volatile struct Sys_Monitor sys_mon;

  void   sys_mon_main (void);
  int8_t sys_cmd_help (uint8_t argc, char *argv[]);
  int8_t sys_cmd_sinfo (uint8_t argc, char *argv[]);
  int8_t sys_cmd_hexdump (uint8_t argc, char *argv[]);
  int8_t sys_cmd_setmem (uint8_t argc, char *argv[]);

// globale Konstante im Flash
  const char SYS_PMEM_LINESTART[] PROGMEM = "\n\r>";
  const char SYS_PMEM_ERR0[] PROGMEM = "Error ";
  const char SYS_PMEM_ERR1[] PROGMEM = "Error: Unknown command\n\r";
  const char SYS_PMEM_ERR2[] PROGMEM = " -> Syntax error\n\rUsage: ";
  const char SYS_PMEM_CMD_HELP[] PROGMEM = "help\0List of all commands\0help";
  const char SYS_PMEM_CMD_SINFO[] PROGMEM = "sinfo\0Systeminformation\0sinfo";
#ifdef GLOBAL_MONCMD_HEXDUMP
  const char SYS_PMEM_CMD_HEXDUMP[] PROGMEM = "hexdump\0Hexdump of memory content\0hexdump {f|s|e} start [length]";
#endif // GLOBAL_MONCMD_HEXDUMP
#ifdef GLOBAL_MONCMD_SETMEM
  const char SYS_PMEM_CMD_SETMEM[] PROGMEM = "setmem\0Write bytes into SRAM/EEPROM\0setmem [s|e] address value";
#endif // GLOBAL_MONCMD_SETMEM

  const struct Sys_MonCmdInfo SYS_PMEMSTR_CMDS[] PROGMEM =
  {
    { SYS_PMEM_CMD_HELP, sys_cmd_help }
    , { SYS_PMEM_CMD_SINFO, sys_cmd_sinfo }
#ifdef GLOBAL_MONCMD_HEXDUMP
    , { SYS_PMEM_CMD_HEXDUMP, sys_cmd_hexdump }
#endif // GLOBAL_MONCMD_HEXDUMP
#ifdef GLOBAL_MONCMD_SETMEM
    , { SYS_PMEM_CMD_SETMEM, sys_cmd_setmem }
#endif // GLOBAL_MONCMD_SETMEM
  };

#endif // GLOBAL_MONITOR

/// declarations and definations

volatile struct Sys sys;

// // functions
int sys_uart_putch (char c, FILE *f);
int sys_uart_getch (FILE *f);

static FILE mystdout = FDEV_SETUP_STREAM(sys_uart_putch, NULL, _FDEV_SETUP_WRITE);
static FILE mystdin  = FDEV_SETUP_STREAM(NULL, sys_uart_getch, _FDEV_SETUP_READ);

void sys_init (void)
{
  memset((void *)&sys, 0, sizeof(sys));
 _delay_ms(150);

#ifdef __AVR_ATmega16__
  OCR0  = (F_CPU+4)/8/10000-1;
  TCCR0 = (1 << WGM01) | (1 << CS01);
  TIMSK = (1 << OCIE0);
  TIFR  = (1 << OCF0);
  UBRRL = (F_CPU+8)/16/GLOBAL_UART_BITRATE-1;
  UBRRH = 0x00;
  UCSRA = 0x00;
  UCSRC = (1<<URSEL) | (1 << UCSZ1) | (1 << UCSZ0);
  UCSRB = (1 << RXCIE) | (1 << TXEN) | (1 << RXEN);

#elif __AVR_ATmega324P__
  OCR0A  = (F_CPU+4)/8/10000-1;
  TCCR0A = (1 << WGM01);
  TCCR0A = (1 << CS01);
  TIMSK0 = (1 << OCIE0A);
  TIFR0  = (1 << OCF0A);
  UBRR0L = (F_CPU/GLOBAL_UART_BITRATE + 4)/8 - 1;
  UBRR0H = 0x00;
  UCSR0A = (1<<U2X0);
  UCSR0C = (1 << UCSZ01) | (1 << UCSZ00);
  UCSR0B = (1 << RXCIE0) | (1 << TXEN0) | (1 << RXEN0);

#elif __AVR_ATmega328P__
  OCR0A  = (F_CPU+4)/8/10000-1;
  TCCR0A = (1 << WGM01);
  TCCR0B = (1 << CS01);
  TIMSK0 = (1 << OCIE0A);
  TIFR0  = (1 << OCF0A);
  UBRR0L = (F_CPU/GLOBAL_UART_BITRATE + 4)/8 - 1;
  UBRR0H = 0x00;
  UCSR0A = (1<<U2X0);
  UCSR0C = (1 << UCSZ01) | (1 << UCSZ00);
  UCSR0B = (1 << RXCIE0) | (1 << TXEN0) | (1 << RXEN0);

#elif __AVR_AT90CAN128__
  OCR0A  = (F_CPU+4)/8/10000-1;
  TCCR0A = (1 << WGM01);
  TCCR0A = (1 << CS01);
  TIMSK0 = (1 << OCIE0A);
  TIFR0  = (1 << OCF0A);
  UBRR0L = (F_CPU/GLOBAL_UART_BITRATE + 4)/8 - 1;
  UBRR0H = 0x00;
  UCSR0A = (1<<U2X0);
  UCSR0C = (1 << UCSZ01) | (1 << UCSZ00);
  UCSR0B = (1 << RXCIE0) | (1 << TXEN0) | (1 << RXEN0);

#endif

#ifdef SURE
  PORTA  =  0x0f;  // all LEDs off
  DDRA   =  0x0f;  // LEDs (PA3:0)
  DDRC  &= ~0xc0;  // Push Buttons SW1 (PC7) and SW2 (PC6)
#elif ARDUINO
  DDRB  = (1<< PB5); // LED L (yellow on pin PB5/SCK))
  PORTB = 0x00;
#elif CRUMB128
  DDRB  = (1<< PB7); // LED1 (green on pin PB7))
  PORTB = 0x00;  
#elif ASURO
  PORTB &= ~0x01; // green LED off
  DDRB |= 0x01;   // green LED port (PB0) is output
  PORTD &= ~0x04; // red LED off
  DDRD |= 0x04;   // red LED port (PD2) is output
#endif

#ifdef GLOBAL_SURE_LCD  
  PORTB  =    0x00;   // LCD-DATA
  PORTD  &= ~(0x1c);  // LCD E, R/W, RS
  DDRB   =    0xff;   // LCD-Data
  DDRD   |=  (0x1c);  // LCD-Data
  sys_lcd_init();
#endif
#ifdef  GLOBAL_SURE_SEG7
  PORTA  =  0x0f;  // all LEDs off
  DDRA   =  0xff;  // LEDs (PA3:0) and 7-Seg common cathode (PA7:0)
  PORTB  =  0x00;  // all 7-Seg anodes off
  DDRB   =  0xff;  // 7-Seg anodes
#endif

  // connect libc functions printf(), gets()... to UART
  // fdevopen(sys_uart_putch, sys_uart_getch);
  stdout = &mystdout;
  stdin  = &mystdin;
}


void sys_main (void)
{
#ifdef GLOBAL_MONITOR
  sys_mon_main();
#endif
}

//----------------------------------------------------------------------------

uint8_t sys_inc8BitCnt (uint8_t count)
{
  return count<0xff ? count+1 : count;
}


uint16_t sys_inc16BitCnt (uint16_t count)
{
  return count<0xffff ? count+1 : count;
}


void sys_sei (void)
{
  if (sys.flags_u8 & SYS_FLAG_SREG_I)
    sei();
}


void sys_cli (void)
{
  sys.flags_u8 |= (SREG & 0x80);
  cli();
}


void sys_newline (void)
{
  printf("\n\r");
}


void sys_printString_P (PGM_P str)
{
  char c;

  while (1)
  {
    memcpy_P(&c, str++, 1);
    if (!c) break;
    putchar(c);
  }
}


void sys_puts_P (PGM_P str)
{
  sys_printString_P(str);
  sys_newline();
}

#ifdef GLOBAL_MONITOR

int16_t sys_getByte (char typ, uint16_t add)
{
  uint8_t value = 0;

  switch (typ)
  {
    case 'f': // flash
      if (add>FLASHEND) return -1;
      memcpy_P(&value, (PGM_P) add, 1);
      break;

    case 's': // SRAM
      if (add>RAMEND ) return -1;
      value = *((uint8_t *)add);
      break;

    case 'e': // EEPROM
      if (add>E2END) return -1;
      //value = eeprom_read_byte ((uint8_t *)add);
      break;

    default:
      return -1;
  }

  return value;
}


void sys_printBin (uint8_t value, char sep)
{
  uint8_t i;

  for (i=0; i<8; i++,value<<=1)
  {
    putchar(value&0x80 ? '1' : '0');
    if (i==3 && sep) putchar(sep);
  }
}


void sys_printHexBin8 (uint8_t value)
{
  printf("0x%02x (", value);
  sys_printBin(value, ' ');
  putchar(')');
}


void sys_printHexBin16 (uint16_t value)
{
  printf("0x%04x (", value);
  sys_printBin(value>>8, ' ');
  putchar(' ');
  sys_printBin(value & 0xff, ' ');
  putchar(')');
}


int8_t sys_cmd_sinfo (uint8_t argc, char *argv[])
{
  printf("sys.flags_u8  : ");
  sys_printHexBin8(sys.flags_u8);
  sys_newline();
  printf("sys.taskErr_u8: ");
  sys_printHexBin8(sys.taskErr_u8);
  sys_newline();
  return 0;
}


int16_t sys_readArgument (char *str, int16_t max)
{
  int16_t value;

  if (str[0]=='0' && str[1]=='x')
    value = strtol(str, NULL, 16);
  else
    value = strtol(str, NULL, 10);

  if (value>max)
    return -1;

  return value;
}

#endif

//----------------------------------------------------------------------------

int sys_uart_getch (FILE *f)
{
  if (f != stdin)
    return _FDEV_EOF;
  if (sys.uart.wpos_u8 == sys.uart.rpos_u8) return _FDEV_EOF;
  uint8_t c = sys.uart.rbuffer_u8[sys.uart.rpos_u8++];
  if (sys.uart.rpos_u8>=GLOBAL_UART_RECBUFSIZE)
    sys.uart.rpos_u8 = 0;
  return (int) c;
}


int sys_uart_putch (char c, FILE *f)
{
  if (f != stdout)
    return _FDEV_EOF;
  while (!SYS_UART_UDR_IS_EMPTY);
  SYS_UDR = c;
  return (int)c;
}


uint8_t sys_uart_available (void)
{
  return sys.uart.wpos_u8 >= sys.uart.rpos_u8
           ? sys.uart.wpos_u8 - sys.uart.rpos_u8
           : ((int16_t)sys.uart.wpos_u8) + GLOBAL_UART_RECBUFSIZE - sys.uart.rpos_u8;
}


//----------------------------------------------------------------------------

int16_t sys_uart_getBufferByte (uint8_t pos)
{
  int16_t value;
  sys_cli();

  if (pos >= sys_uart_available())
    value = -1;
  else
  {
    uint8_t bufpos = sys.uart.rpos_u8 + pos;
    if (bufpos >= GLOBAL_UART_RECBUFSIZE)
      bufpos -= GLOBAL_UART_RECBUFSIZE;
    value = sys.uart.rbuffer_u8[bufpos];
  }

  sys_sei();
  return value;
}


void sys_uart_flush (void)
{
  sys_cli();
  while (SYS_UART_BYTE_RECEIVED)
    sys.uart.rbuffer_u8[0] = SYS_UDR;

  sys.uart.rpos_u8 = 0;
  sys.uart.wpos_u8 = 0;
  sys.uart.errcnt_u8 = 0;
  sys_sei();
}


//****************************************************************************
// Event Handling
//****************************************************************************

Sys_Event sys_setEvent (Sys_Event event)
{
  uint8_t eventIsPending = 0;
  sys_cli();
  if (sys.eventFlag & event)
      eventIsPending = 1;
  sys.eventFlag |= event;
  sys_sei();

  return eventIsPending;
}


Sys_Event sys_clearEvent (Sys_Event event)
{
  uint8_t eventIsPending = 0;
  sys_cli();
  if (sys.eventFlag & event)
    eventIsPending = 1;
  sys.eventFlag &= ~event;
  sys_sei();

  return eventIsPending;
}


Sys_Event sys_isEventPending (Sys_Event event)
{
  return (sys.eventFlag & event) != 0;
}



//****************************************************************************
// 7 Segment Handling for Sure DEM2 Board
//****************************************************************************

#ifdef GLOBAL_SURE_SEG7

const uint8_t sys_mask_seg7[19] = { 0x00,
                                    0x3f, 0x06, 0x5b, 0x4f, 0x66, 0x6d, 0x7d, 0x07,
                                    0x7f, 0x6f, 0x77, 0x7c, 0x39, 0x5e, 0x79, 0x71,
                                    0x40, 0x08 };

void sys_7seg_refresh ()
{
  static uint8_t refresh = 0;

  refresh = (refresh + 1) % 4;
  PORTA &= ~0xf0;
  PORTB = sys_mask_seg7[sys.seg7.digit_u8[refresh]];
  if (sys.seg7.point_u8 & (1<<refresh))
    PORTB |= 0x80;
  PORTA |= (0x80 >> refresh);
}


void sys_7seg_setDigit (uint8_t index, uint8_t digitValue)
{
  if (index>3) return;
#ifdef GLOBAL_SURE_LCD
  if (sys.lcd.status == 1)
    return;
#endif
  digitValue++;
  if (digitValue >= sizeof(sys_mask_seg7))
    digitValue = 0; // -> digit is switched off
  sys.seg7.digit_u8[index] = digitValue;
}


void sys_7seg_setPoint (uint8_t index, uint8_t pointState)
{
#ifdef GLOBAL_SURE_LCD
  if (sys.lcd.status == 1)
    return;
#endif
  if (pointState)
    sys.seg7.point_u8 |= (1<<index);
  else
    sys.seg7.point_u8 &= ~(1<<index);
}


void sys_7seg_setString (const char *str)
{
  int8_t i;

#ifdef GLOBAL_SURE_LCD
  if (sys.lcd.status == 1)
    return;
#endif

  for (i=3; i>=0 && *str; i--)
  {
    if (*str>='0' && *str<='9')
      sys_7seg_setDigit(i, *str-'0');
    else if (*str>='a' && *str<='f')
      sys_7seg_setDigit(i, *str-'a');
    else if (*str>='A' && *str<='Z')
      sys_7seg_setDigit(i, *str-'A');
    else if (*str=='-')
      sys_7seg_setDigit(i, SYS_SEG7_DIGIT_MINUS);
    else
      sys_7seg_setDigit(i, SYS_SEG7_DIGIT_UNDERLINE);
    str++;
  }
}

#endif // GLOBAL_SURE_SEG7


//****************************************************************************
// LCD Handling (only for SURE Board)
//****************************************************************************

#ifdef GLOBAL_SURE_LCD

void sys_lcd_init (void)
{
  uint8_t i;

  //_delay_ms(16);
  sys.lcd.status = 0;
  for (i=0; i<4; i++)
  {
    sys_lcd_setRegister(SYS_LCD_CMD_SET_FUNCTION | 0x18); // 8 Bit, 2 Zeilen, 5x7
    if (i==0) _delay_ms(5); else _delay_us(100);
  }

  sys_lcd_setRegister(SYS_LCD_CMD_DISPLAY_ON_OFF | 0x04); // display on, cursor off
  if (!sys_lcd_isReady(50)) { sys.lcd.status = -1; return; }

  sys_lcd_setRegister(SYS_LCD_CMD_DISPLAY_ON_OFF | 0x04); // display on, cursor off
  if (!sys_lcd_isReady(50)) { sys.lcd.status = -3; return; }

  sys_lcd_setRegister(SYS_LCD_CMD_DISPLAY_CLEAR);
  if (!sys_lcd_isReady(1200)) { sys.lcd.status = -4; return; }

  sys.lcd.status = 1;
}


uint8_t sys_lcd_isReady (uint16_t us)
{
  if (sys.lcd.status<0) return 0;

  uint8_t busy;
  PORTB = 0xff;
  DDRB = 0x00;  // Direction Port LCD-Data to Input
  do
  {
    SYS_LCD_SET_RW;
    SYS_LCD_CLR_RS;
    _delay_us(1);
    SYS_LCD_SET_E;
    _delay_us(SYS_LCD_PULSE_LENGTH);
    sys.lcd.data = PINB;
    busy = sys.lcd.data & SYS_LCD_BUSY_FLAG;
    SYS_LCD_CLR_E;
    SYS_LCD_CLR_RW;
    us = (us>=11) ? us-11 : 0;
  }
  while (us>0 && busy);

  if (sys.lcd.status== 1 && busy)
    sys.lcd.status = -5;

  DDRB = 0xff;  // Direction Port LCD-Data to Input

  return busy == 0;
}


void sys_lcd_setRegister (uint8_t cmd)
{
  PORTD &= ~0x1c; //  E=0, R/W=0, RS=0
  PORTB = cmd;
  DDRB = 0xff;    // Direction Port LCD-Data to Output
  SYS_LCD_SET_E;
  _delay_us(SYS_LCD_PULSE_LENGTH);
  SYS_LCD_CLR_E;
  _delay_us(1);
}


void sys_lcd_waitOnReady (void)
{
  if(sys_lcd_isReady(50)==0)
    sys.lcd.status = -6;
}


void sys_lcd_setDisplayOff (void)
{
  sys_lcd_waitOnReady();
  sys_lcd_setRegister(SYS_LCD_CMD_DISPLAY_ON_OFF); // display off
  sys_lcd_waitOnReady();
}


void sys_lcd_setDisplayOn (void)
{
  sys_lcd_waitOnReady();
  sys_lcd_setRegister(SYS_LCD_CMD_DISPLAY_ON_OFF | 0x04); // display on
  sys_lcd_waitOnReady();
}


void sys_lcd_clear (void)
{
  sys_lcd_waitOnReady();
  sys_lcd_setRegister(SYS_LCD_CMD_DISPLAY_CLEAR);
  while (!sys_lcd_isReady(1200));
}


void sys_lcd_setDRAddr (uint8_t address)
{
  sys_lcd_waitOnReady();
  sys_lcd_setRegister(SYS_LCD_CMD_SET_DDRAM_ADDR | address);
  sys_lcd_waitOnReady();
}


// 1.Line->rowIndex=0, 1.Column->colIndex=0
void sys_lcd_setCursorPosition (uint8_t rowIndex, uint8_t columnIndex)
{
	if (sys.lcd.status!=1 || rowIndex>1) return;
  if (rowIndex)
    sys_lcd_setDRAddr(0x40 + columnIndex);
  else
    sys_lcd_setDRAddr(columnIndex);
}


inline void sys_lcd_putc (char c, FILE *stream)
{
  sys_lcd_putchar(c);
}


void sys_lcd_putchar (int character)
{
  if (sys.lcd.status!=1) return;
  sys_lcd_waitOnReady();

  DDRB = 0xff;
  PORTB = (uint8_t) character;
  SYS_LCD_SET_RS;
  SYS_LCD_CLR_RW;
  SYS_LCD_SET_E;
  _delay_us(SYS_LCD_PULSE_LENGTH);
  SYS_LCD_CLR_E;
  _delay_us(10);
  DDRB = 0x00;
}


void sys_lcd_putString (const char * str)
{
  while (*str && sys.lcd.status==1)
  {
    sys_lcd_putchar(*str++);
  }
  sys_lcd_waitOnReady();
}

#endif // GLOBAL_SURE_LCD


//****************************************************************************
// LED Handling
//****************************************************************************

#ifdef SURE

void sys_setAllLeds (uint8_t ledState)
{
  if (ledState)
  {
    PORTA = 0xf0;
    PORTB = 0xff;
  }
  else
  {
    PORTA = 0x0f;
    PORTB = 0x00;
  }
}


void sys_setLed (uint8_t index, uint8_t ledState)
{
  if (index>3) return;
  if (ledState)
    PORTA &= ~(1<<index);
  else
    PORTA |= (1<<index);
}


void sys_toggleLed (uint8_t index)
{
  if (index>3) return;
  PORTA ^=  (1<<index);
}

#endif // SURE

#ifdef ARDUINO

void sys_setLed (uint8_t ledState)
{
  if (ledState)
    PORTB |= (1<<PB5);
  else
    PORTB &= ~(1<<PB5);
}

void sys_toggleLed (void)
{
  PORTB ^= (1<<PB5);
}

#endif

#ifdef CRUMB128

void sys_setLed (uint8_t ledState)
{
  if (ledState)
    PORTB |= (1<<PB7);
  else
    PORTB &= ~(1<<PB7);
}

void sys_toggleLed (void)
{
  PORTB ^= (1<<PB7);
}

#endif

#ifdef ASURO

void sys_setGreenLed (uint8_t ledState)
{
  if (ledState)
    PORTB |= 0x01;
  else
    PORTB &= ~0x01;
}


void sys_setRedLed (uint8_t ledState)
{
  if (ledState)
    PORTD |= 0x04;
  else
    PORTD &= ~0x04;
}


void sys_toggleGreenLed (void)
{
  PORTB ^= 0x01;
}


void sys_toggleRedLed (void)
{
  PORTD ^= 0x04;
}

#endif // ASURO


#ifdef GLOBAL_MONITOR

//****************************************************************************
// Monitor Handling
//****************************************************************************

struct Sys_MonCmdInfo sys_getMonCmdInfo (uint8_t index)
{
  struct Sys_MonCmdInfo ci = { NULL, NULL} ;
  PGM_P p;

  if (index < sizeof(SYS_PMEMSTR_CMDS)/sizeof(struct Sys_MonCmdInfo))
    p = (PGM_P)&(SYS_PMEMSTR_CMDS[index]);
  else
  {
    index -= sizeof(SYS_PMEMSTR_CMDS)/sizeof(struct Sys_MonCmdInfo);
    if (index >= mon_getCmdCount())
      return ci;
    p = (PGM_P)&(MON_PMEMSTR_CMDS[index]);
  }

  memcpy_P(&ci, p, sizeof(struct Sys_MonCmdInfo));
  return ci;
}


void sys_mon_printUsageInfo (struct Sys_MonCmdInfo *pCmdInfo)
{
  PGM_P p = pCmdInfo->pInfo;

  uint8_t i;
  for (i=0; i<2; i++)
  {
    char c;
    do
    {
      memcpy_P(&c, p++, 1);
    }
    while (c != 0);
  }
  sys_printString_P(p);
}


void sys_mon_ExecuteCmd (void)
{
  struct Sys_Monitor *pmon = (struct Sys_Monitor *)&sys_mon;
  sys_newline();

  uint8_t i;
  char *argv[GLOBAL_MON_MAXARGV];
  uint8_t argc;

  char *ps = pmon->cmdLine;
  argc = 0;

  i = 0;
  for (i=0; i<GLOBAL_MON_MAXARGV && *ps; i++)
  {
    while(*ps==' ') ps++; // ignore leading spaces
    if (*ps == 0) break;
    argv[i] = ps;
    argc++;
    while (*ps != ' ' && *ps)
      ps++;
    if (*ps==' ')
      *ps++ = 0;
  }
  for (;i<GLOBAL_MON_MAXARGV; i++)
    argv[i] = NULL;

  /*
  printf("\n\rargc=%d\n\r", argc);
  for (i=0; i<GLOBAL_MON_MAXARGV; i++)
  {
    printf("  argv[%d]=0x%04x", i, (uint16_t)argv[i]);
    if (argv[i])
      printf(" len=%d cmd='%s'", strlen(argv[i]), argv[i]);
    sys_newline();
  }
  */

  if (argc>0 && *argv[0])
  {
    i = 0;
    while (1)
    {
      struct Sys_MonCmdInfo ci = sys_getMonCmdInfo(i++);
      if (ci.pFunction == NULL)
      {
        sys_printString_P(SYS_PMEM_ERR1);
        break;
      }
      else if (strcmp_P(pmon->cmdLine, ci.pInfo)==0)
      {
        if (argc==2 && strcmp_P(argv[1], SYS_PMEM_CMD_HELP)==0)
          sys_mon_printUsageInfo(&ci);
        else
        {
          int8_t retCode = (*ci.pFunction)(argc, argv);
          if (retCode)
          {
            sys_printString_P(SYS_PMEM_ERR0);
            printf("%d", retCode);
            if (retCode<0)
            {
              sys_printString_P(SYS_PMEM_ERR2);
              sys_mon_printUsageInfo(&ci);
            }
          }
        }
        sys_newline();
        break;
      }
    }
  }

  sys_printString_P(SYS_PMEM_LINESTART);
  pmon->cursorPos = 0;
  pmon->cmdLine[0] = 0;
}


void sys_mon_CmdLineBack (void)
{
  struct Sys_Monitor *pmon = (struct Sys_Monitor *)&sys_mon;
  if (pmon->cursorPos==0) return;
  printf("\b \b");
  pmon->cmdLine[--pmon->cursorPos] = 0;
}

void sys_putnchar (char c, uint8_t count)
{
  for (; count>0; count--)
    putchar(c);
}


void sys_mon_main (void)
{
  struct Sys_Monitor *pmon = (struct Sys_Monitor *)&sys_mon;
  char c = 0;
  int8_t incLineIndex = 0;
  static uint8_t lastbyte = 0;

  if (sys_mon.flags & SYS_MONITOR_FLAG_LINEMODE)
  {
    while (sys_uart_available()>0)
    {
      c = getchar();
      if (lastbyte != '\n' && c == '\r')
        c = '\n';
      else
        lastbyte = c;
      if (c=='\n')
      {
        sys_mon.flags &= ~(SYS_MONITOR_FLAG_LINEMODE | SYS_MONITOR_FLAG_LINE);
        printf("\n\n\r>");
        return;
      }
    }
    if (c==SYS_MONITOR_CTRL_X)
    {
      incLineIndex = 1;
      sys_mon.flags &= ~SYS_MONITOR_FLAG_LINE;
    }
    else if (c==SYS_MONITOR_CTRL_Y)
    {
      incLineIndex = -1;
      sys_mon.flags &= ~SYS_MONITOR_FLAG_LINE;
    }
    else if (c==SYS_MONITOR_CTRL_C)
    {
      if (sys_mon.flags & SYS_MONITOR_FLAG_CONT)
        sys_mon.flags &= ~(SYS_MONITOR_FLAG_CONT | SYS_MONITOR_FLAG_LINE);
      else
        sys_mon.flags |= SYS_MONITOR_FLAG_CONT;
    }

    if (sys_mon.flags & SYS_MONITOR_FLAG_LINE)
    {
      int8_t lenSpaces = mon_printLine(sys_mon.lineIndex, c);
      sys_putnchar(' ', lenSpaces);
      printf("\r");
      if (sys_mon.flags & SYS_MONITOR_FLAG_CONT)
        printf("\n");
    }
    else
    {
      int8_t len;

      printf("\n\n\r");
      do
      {
        if (incLineIndex)
          sys_mon.lineIndex = incLineIndex>0 ? sys_mon.lineIndex+1 : sys_mon.lineIndex-1;
        len = mon_printLineHeader(sys_mon.lineIndex);
      }
      while (len<0 && sys_mon.lineIndex!=0);

      sys_newline();
      if (len>0)
        sys_putnchar('-', len);
      sys_newline();
      sys_mon.flags |= SYS_MONITOR_FLAG_LINE;
    }
  }
  else
  {
    while (sys_uart_available()>0)
    {
      c = getchar();
      if (lastbyte != '\n' && c == '\r')
        c = '\n';
      else
        lastbyte = c;

      if (c==SYS_MONITOR_CTRL_X || c==SYS_MONITOR_CTRL_Y)
        sys_mon.flags |= SYS_MONITOR_FLAG_LINEMODE;
      else if (c=='\n')
        sys_mon_ExecuteCmd();
      else if (c==127)
        ; // ignore Taste 'Entf' -> maybe implemented later
      else if (c=='\b')
        sys_mon_CmdLineBack();
      else if (c<' ' || c>126)
        ; // ignore control codes
      else if (pmon->cursorPos<(sizeof(pmon->cmdLine)-1))
      {
        #ifdef GLOBAL_MON_ONLYLOCASE
          c = tolower(c);
        #endif
        pmon->cmdLine[pmon->cursorPos++] = c;
        pmon->cmdLine[pmon->cursorPos] = 0;
        putchar(c);
      }
    }
  }
}


//****************************************************************************
// Monitor Commands
//****************************************************************************

int8_t sys_cmd_help (uint8_t argc, char *argv[])
{
  // struct Sys_Monitor *pmon = (struct Sys_Monitor *)&sys_mon;
  uint8_t i, j, max;

  i = 0; max = 0;
  while (1)
  {
    struct Sys_MonCmdInfo ci = sys_getMonCmdInfo(i++);
    if (ci.pFunction == NULL)
      break;
    uint8_t len = strlen_P(ci.pInfo);
    max = (len>max) ? len : max;
  }

  i = 0;
  while (1)
  {
    struct Sys_MonCmdInfo ci = sys_getMonCmdInfo(i++);
    if (ci.pFunction == NULL)
      break;

    sys_printString_P(ci.pInfo);
    for (j=strlen_P(ci.pInfo); j<max+2; j++)
      putchar(' ');
    sys_puts_P(ci.pInfo + strlen_P(ci.pInfo) + 1);
  }

  return 0;
}


#ifdef GLOBAL_MONCMD_SETMEM
int8_t sys_cmd_setmem (uint8_t argc, char *argv[])
{
  char     typ;    // 1st parameter (s='SRAM | 'e'=EEPROM)
  uint16_t add;   // 2nd parameter (address)
  uint16_t value; // 3rd parameter (value)
  char     *padd, *pval;

  if (argc==3)
  {
    typ = 's'; // default is set of SRAM-Byte
    padd = argv[1];
    pval = argv[2];
  }
  else if (argc==4)
  {
    typ = argv[1][0];
    padd = argv[2];
    pval = argv[3];
  }
  else
    return -1; // Syntax Error

  add = sys_readArgument(padd, RAMEND);
  value = sys_readArgument(pval, 255);
  if (value<0) return -2;

  printf("set 0x%02x to address 0x%04x\n\r", value, add);

  switch (typ)
  {
    case 's':
      *((uint8_t *)add) = value;
      break;

    case 'e':
     eeprom_write_byte((uint8_t *)add, value);
     eeprom_busy_wait();
     break;
  }

  return 0;
}
#endif // GLOBAL_MONCMD_SETMEM


#ifdef GLOBAL_MONCMD_HEXDUMP
int8_t sys_cmd_hexdump (uint8_t argc, char *argv[])
{
  if (argc<3 || argc>4) return -1;

  char     typ; // 1st parameter ('s'=SRAM | 'f'=FLASH | 'e'=EEPROM)
  uint16_t add; // 2nd parameter (start address of hexdump)
  uint16_t len; // [3rd parameter] (number of bytes to dump)

  typ = argv[1][0];
  if (typ!='f' && typ!='s' && typ!='e')
    return -1;
  if (argv[2][0]=='0' && argv[2][1]=='x')
    add = strtol(&argv[2][2], NULL, 16);
  else
    add = strtol(argv[2], NULL, 10);

  if (argc==4)
  {
    if (argv[3][0]=='0' && argv[3][1]=='x')
      len = strtol(&argv[3][2], NULL, 16);
    else
      len = strtol(&argv[3][0], NULL, 10);
  }
  else
    len = 32;

  char s[19] = "  ";
  s[18] = 0;
  uint16_t i;

  for (i=0; i<len; add++, i++)
  {
    int16_t i16 = sys_getByte(typ, add);
    if (i16<0) break;
    uint8_t value = (uint8_t)i16;

    if (i%16==0)
      printf("0x%04x: ", add);
    else if (i%4==0)
      putchar(' ');

    s[(i%16)+2] = value>=32 && value<127 ? value : '.';
    printf("%02x ", value);
    if (i%16==15)
    {
      printf(s);
      sys_newline();
    }
    
  }

  if ((i%16)!=0)
  {
    for (;(i%16)!=0; i++)
    {
      printf("   ");
      s[(i%16)+2] = ' ';
      if (i%4==0) putchar(' ');
    }
    printf(s);
    sys_newline();
  }

  return 0;
}
#endif // GLOBAL_MONCMD_HEXDUMP

#endif // GLOBAL_MONITOR


// ------------------------------------
// Interrupt Service Routinen
// ------------------------------------

ISR (SYS_UART_RECEIVE_VECTOR)
{
  static uint8_t lastChar;
  uint8_t c = SYS_UDR;

  if (c=='R' && lastChar=='@')
  {
    wdt_enable(WDTO_15MS);
    wdt_reset();
    while(1) {};
  }
  lastChar = c;

  sys.uart.rbuffer_u8[sys.uart.wpos_u8++] = c;
  if (sys.uart.wpos_u8 >= GLOBAL_UART_RECBUFSIZE)
    sys.uart.wpos_u8 = 0;
  if (sys.uart.wpos_u8 == sys.uart.rpos_u8)
  {
    sys.uart.wpos_u8 == 0 ? sys.uart.wpos_u8 = GLOBAL_UART_RECBUFSIZE-1 : sys.uart.wpos_u8--;
    sys.uart.errcnt_u8 = sys_inc8BitCnt(sys.uart.errcnt_u8);
  }
  sys.uart.rbuffer_u8[sys.uart.wpos_u8] = 0;
}


// Timer 0 Output/Compare Interrupt
// called every 100us
ISR (SYS_TIMER0_VECTOR)
{
  static uint8_t cnt100us = 0;
  static uint8_t cnt500us = 0;
  static uint8_t busy = 0;

  cnt100us++;
  if (cnt100us>=5)
  {
    cnt100us = 0;
    cnt500us++;
    if (busy)
      sys.taskErr_u8 = sys_inc8BitCnt(sys.taskErr_u8);
    else
    {
      busy = 1;
      sei();
      if      (cnt500us & 0x01) app_task_1ms();
      else if (cnt500us & 0x02)
      {
#ifdef GLOBAL_SURE_SEG7
#ifdef GLOBAL_SURE_LCD
        if (sys.lcd.status != 1)
          sys_7seg_refresh();
        else
          PORTA &= ~0xf0; // disable 7-Segment
#else
        sys_7seg_refresh();
#endif
#endif
        app_task_2ms();
      }
      else if (cnt500us & 0x04) app_task_4ms();
      else if (cnt500us & 0x08) app_task_8ms();
      else if (cnt500us & 0x10) app_task_16ms();
      else if (cnt500us & 0x20) app_task_32ms();
      else if (cnt500us & 0x40) app_task_64ms();
      else if (cnt500us & 0x80) app_task_128ms();
      busy = 0;
    }
  }

}

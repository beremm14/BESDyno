#include "global.h"

#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/wdt.h>
#include <util/delay.h>

#include "sys.h"
#include "app.h"

// Defines, nur in sys.c sichtbar
#ifdef __AVR_ATmega328P__
#define SYS_UART_BYTE_RECEIVED (UCSR0A & (1<<RXC0))
#define SYS_UART_UDR_IS_EMPTY (UCSR0A & (1<<UDRE0))
#define SYS_UDR UDR0
#define SYS_UART_RECEIVE_VECTOR USART_RX_vect
#endif

#ifdef SURE
#define SYS_UART_BYTE_RECEIVED (UCSRA & (1<<RXC))
#define SYS_UART_UDR_IS_EMPTY (UCSRA & (1<<UDRE))
#define SYS_UDR UDR
#define SYS_UART_RECEIVE_VECTOR USART_RXC_vect
#define SYS_TIMER0_VECTOR TIMER0_COMP_vect
#endif

// globale Variablen nur in sys.c sichtbar
// ...

// globale Strukturvariable, ueberall sichtbar
volatile struct Sys sys;

// Deklaration von Funktionen
int sys_uart_putch (char c, FILE *f);
int sys_uart_getch (FILE *f);

static FILE mystdout = FDEV_SETUP_STREAM(sys_uart_putch, NULL, _FDEV_SETUP_WRITE);
static FILE mystdin  = FDEV_SETUP_STREAM(NULL, sys_uart_getch, _FDEV_SETUP_READ);


void sys_init (void)
{
  memset((void *)&sys, 0, sizeof(sys));
 _delay_ms(150);
#ifdef __AVR_ATmega328P__
  // init UART-Interface (Atmega328 - USART0)
  UBRR0L = (F_CPU/GLOBAL_UART_BITRATE + 4)/8 - 1;
  UBRR0H = 0x00;
  UCSR0A = (1<<U2X0);
  UCSR0C = (1 << UCSZ01) | (1 << UCSZ00);
  UCSR0B = (1 << RXCIE0) | (1 << TXEN0) | (1 << RXEN0);
#endif
#ifdef ARDUINO_UNO_R3
  DDRB  = (1<< PB5); // LED L (yellow on pin PB5/SCK))
  PORTB = 0x00;
#endif  
#ifdef ASURO
  PORTB &= ~0x01; // green LED off
  DDRB |= 0x01;   // green LED port (PB0) is output
  PORTD &= ~0x04; // red LED off
  DDRD |= 0x04;   // red LED port (PD2) is output
#endif

#ifdef SURE
  // Timer 0 used in CTC mode (clear timer in compare match)
  OCR0  = 149; // TOV0 interrupt every 100us (12E6/8/150=10000Hz)
  TCCR0 = (1 << WGM01) | (1 << CS01); // CTC mode with prescaler 8
  TIMSK = (1 << OCIE0);
  TIFR  = (1 << OCF0);

  // init UART-Interface (Atmega328 - USART0)
  UBRRL = F_CPU/16/GLOBAL_UART_BITRATE-1;
  UBRRH = 0x00;
  UCSRA = 0x00;
  UCSRC = (1<<URSEL) | (1 << UCSZ1) | (1 << UCSZ0);
  UCSRB = (1 << RXCIE) | (1 << TXEN) | (1 << RXEN);

  PORTA  =  0x0f;  // all LEDs off
  PORTB  =  0x00;  // all 7-Seg anodes off, LCD-DATA=0
  DDRA   =  0xff;  // LEDs (PA3:0) and 7-Seg common cathode (PA7:0)
  DDRC  &= ~0xc0;  // Push Buttons SW1 (PC7) and SW2 (PC6)
  DDRB   =  0xff;  // 7-Seg Segment-Anode, LCD-Data

#endif

  // Bibliotheksfunktionen printf(), gets()... anbinden
  // fdevopen(sys_uart_putch, sys_uart_getch);
  stdout = &mystdout;
  stdin  = &mystdin;
}


void sys_main (void)
{
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


int8_t sys_inc8BitCntAndReturn (uint8_t *count, int8_t retValue)
{
  *count = (*count)<0xff ? (*count)+1 : (*count);
  return retValue;
}


int8_t sys_inc16BitCntAndReturn (uint16_t *count, int8_t retValue)
{
  *count = (*count)<0xffff ? (*count)+1 : (*count);
  return retValue;
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

//----------------------------------------------------------------------------

int sys_uart_getch (FILE *f)
{
  if (sys.uart.wpos_u8 == sys.uart.rpos_u8) return _FDEV_EOF;
  uint8_t c = sys.uart.rbuffer_u8[sys.uart.rpos_u8++];
  if (sys.uart.rpos_u8>=GLOBAL_UART_RECBUFSIZE)
    sys.uart.rpos_u8 = 0;
  return (int) c;
}


int sys_uart_putch (char c, FILE *f)
{
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
    sys.uart.errcnt_u8 = SYS_UDR;

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
  PORTA ^= (1<<index);
}

#endif // SURE


#ifdef ARDUINO_UNO_R3

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

  app_uart_isr(c);
}
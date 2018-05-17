#ifndef SYS_H_INCLUDED
#define SYS_H_INCLUDED

#if GLOBAL_UART_RECBUFSIZE > 255
  #error "GLOBAL_UART_RECBUFSIZE darf nicht groesser als 255 sein"
#endif

// Neue Datentypen
typedef uint8_t Sys_Event;


// Deklaration Strukturvariablentyp

struct Sys_Uart
{
  uint8_t rpos_u8;
  uint8_t wpos_u8;
  uint8_t errcnt_u8;
  uint8_t rbuffer_u8[GLOBAL_UART_RECBUFSIZE];
};


struct Sys
{
  uint8_t  flags_u8;
  uint8_t  taskErr_u8;
  Sys_Event  eventFlag;
  struct Sys_Uart uart;
};

// Deklaration Strukturvariablen
extern volatile struct Sys sys;


// globale Defines
// SYS_FLAG_SREG_I must have same position as I-Bit in Status-Register!!
#define SYS_FLAG_SREG_I          0x80


// Funktionsdeklarationen
void     sys_init                 (void);
void     sys_main                 (void);

void     sys_sei                  (void);
void     sys_cli                  (void);

uint8_t  sys_inc8BitCnt           (uint8_t count);
int8_t   sys_inc8BitCntAndReturn  (uint8_t *count, int8_t retValue);
uint16_t sys_inc16BitCnt          (uint16_t count);
int8_t   sys_inc16BitCntAndReturn (uint16_t *count, int8_t retValue);

void     sys_newline              (void);

Sys_Event  sys_setEvent             (Sys_Event event);
Sys_Event  sys_clearEvent           (Sys_Event event);
Sys_Event  sys_isEventPending       (Sys_Event event);

uint8_t  sys_uart_available       (void);
int16_t  sys_uart_getBufferByte   (uint8_t pos);
void     sys_uart_flush           (void);

#ifdef SURE
void    sys_setAllLeds            (uint8_t ledState);
void    sys_setLed                (uint8_t index, uint8_t ledState);
void    sys_toggleLed             (uint8_t index);
#endif

#ifdef ASURO
void sys_setGreenLed    (uint8_t ledState);
void sys_setRedLed      (uint8_t ledState);
void sys_toggleGreenLed (void);
void sys_toggleRedLed   (void);
#endif

#endif // SYS_H_INCLUDED

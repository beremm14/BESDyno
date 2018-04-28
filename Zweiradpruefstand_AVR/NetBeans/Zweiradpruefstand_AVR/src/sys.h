#ifndef SYS_H_INCLUDED
#define SYS_H_INCLUDED

#include <avr/pgmspace.h>

#if GLOBAL_UART_RECBUFSIZE > 255
  #error "Error: GLOBAL_UART_RECBUFSIZE value over maximum (255)"
#endif

// declarations

typedef uint8_t Sys_Event;

struct Sys_MonCmdInfo
{
  PGM_P pInfo;
  int8_t (*pFunction)(uint8_t, char *[]);
};

struct Sys_Uart
{
  uint8_t rpos_u8;
  uint8_t wpos_u8;
  uint8_t errcnt_u8;
  uint8_t rbuffer_u8[GLOBAL_UART_RECBUFSIZE];
};

struct Sys_Seg7
{
  uint8_t point_u8;
  uint8_t digit_u8[4];
};

struct Sys_Lcd
{
  int8_t   status;  // 0=not initialized, 1=ready, <0->error
  uint8_t  data;
};

struct Sys
{
  uint8_t   flags_u8;
  uint8_t   taskErr_u8;
  Sys_Event eventFlag;
  struct Sys_Uart uart;
#ifdef GLOBAL_SURE_LCD
  struct Sys_Lcd  lcd;
#endif
#ifdef GLOBAL_SURE_SEG7
  struct Sys_Seg7 seg7;
#endif 
};

// defines and declarations

extern volatile struct Sys sys;

// SYS_FLAG_SREG_I must have same position as I-Bit in Status-Register!
#define SYS_FLAG_SREG_I          0x80
#define SYS_FLAG_7SEG_ENABLED    0x01
#define SYS_FLAG_LCD_ENABLED     0x02
#define SYS_SEG7_DIGIT_MINUS     0x10
#define SYS_SEG7_DIGIT_UNDERLINE 0x11
#define SYS_SEG7_DIGIT_OFF       0x13



// LCD-Display 2x20 Zeichen, 5x8 Pixel/Zeichen
// Controller KS0076B
// Data: Port B (Bit 7:0)
// RS=PD2, RW=PD3, E=PD4, VO (Kontrast) ueber Poti und OC1A(=PD5)

#define SYS_LCD_PULSE_LENGTH 15
#define SYS_LCD_SET_RS   PORTD |=  0x04; // Signal RS=1
#define SYS_LCD_CLR_RS   PORTD &= ~0x04; // Signal RS=0
#define SYS_LCD_SET_RW   PORTD |=  0x08; // Signal RW=1
#define SYS_LCD_CLR_RW   PORTD &= ~0x08; // Signal RW=0
#define SYS_LCD_SET_E    PORTD |=  0x10; // Signal E=1 
#define SYS_LCD_CLR_E    PORTD &= ~0x10; // Signal E=0 

#define SYS_LCD_CMD_DISPLAY_CLEAR  0x01  // Display clear
#define SYS_LCD_CMD_CURSOR_HOME    0x02  // Move cursor digit 1
#define SYS_LCD_CMD_SET_ENTRY_MODE 0x04  // Entry Mode Set
#define SYS_LCD_CMD_DISPLAY_ON_OFF 0x08  // Display on/off
#define SYS_LCD_CMD_SHIFT          0x10  // Display shift
#define SYS_LCD_CMD_SET_FUNCTION   0x20  // 4/8 Bits...
#define SYS_LCD_CMD_SET_CGRAM_ADDR 0x40  // Character Generator ROM
#define SYS_LCD_CMD_SET_DDRAM_ADDR 0x80  // Display Data RAM
#define SYS_LCD_BUSY_FLAG          0x80



// functions

void     sys_init                 (void);
void     sys_main                 (void);

void     sys_sei                  (void);
void     sys_cli                  (void);

uint8_t  sys_inc8BitCnt           (uint8_t count);
uint16_t sys_inc16BitCnt          (uint16_t count);

void     sys_newline              (void);
void     sys_printString_P        (PGM_P str);
void     sys_puts_P               (PGM_P str);
void     sys_printBin             (uint8_t value, char sep);
void     sys_printHexBin8         (uint8_t value);
void     sys_printHexBin16        (uint16_t value);
int16_t  sys_readArgument         (char *str, int16_t max);

int16_t  sys_getByte              (char typ, uint16_t add);

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

#ifdef GLOBAL_SURE_SEG7
void    sys_7seg_setDigit         (uint8_t index, uint8_t digitValue);
void    sys_7seg_setPoint         (uint8_t index, uint8_t pointState);
void    sys_7seg_setString        (const char *str);
#endif

#ifdef GLOBAL_SURE_LCD
void    sys_lcd_init              (void);
void    sys_lcd_setRegister       (uint8_t cmd);
void    sys_lcd_setData           (uint8_t addr, uint8_t data);
uint8_t sys_lcd_isReady           (uint16_t us);
void    sys_lcd_setDisplayOn      (void);
void    sys_lcd_setDisplayOff     (void);
void    sys_lcd_clear             (void);
void    sys_lcd_setCursorPosition (uint8_t rowIndex, uint8_t columnIndex);
void    sys_lcd_putchar           (int character);
void    sys_lcd_putString         (const char * str);
#endif

#ifdef ARDUINO
void    sys_setLed                (uint8_t ledState);
void    sys_toggleLed             (void);
#endif

#ifdef CRUMB128
void    sys_setLed                (uint8_t ledState);
void    sys_toggleLed             (void);
#endif

#ifdef ASURO
void sys_setGreenLed    (uint8_t ledState);
void sys_setRedLed      (uint8_t ledState);
void sys_toggleGreenLed (void);
void sys_toggleRedLed   (void);
#endif

#endif // SYS_H_INCLUDED

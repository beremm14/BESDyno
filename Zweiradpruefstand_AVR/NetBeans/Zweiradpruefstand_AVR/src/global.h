#ifndef GLOBAL_H_INCLUDED
#define GLOBAL_H_INCLUDED

// globale Defines...
#ifdef ARDUINO_UNO_R3_ATMEGA328P
  #define F_CPU 16000000UL
  #define ARDUINO 1
  #define ARDUINO_UNO 1
  #define ARDUINO_UNO_R3 1
  #ifndef __AVR_ATmega328P__
    #define __AVR_ATmega328P__
  #endif
#elif ARDUINO_UNO_R3_ATMEGA328
  #define F_CPU 16000000UL
  #define ARDUINO 1
  #define ARDUINO_UNO 1
  #define ARDUINO_UNO_R3 1
  #ifndef __AVR_ATmega328__
    #define __AVR_ATmega328__
  #endif
#elif ARDUINO_NANO_ATMEGA328P
  #define F_CPU 16000000UL
  #define ARDUINO 1
  #define ARDUINO_UNO 1
  #define ARDUINO_UNO_R3 1
  #ifndef __AVR_ATmega328__
    #define __AVR_ATmega328__
  #endif
#elif ASURO_ATMEGA328P
  #define F_CPU 12000000UL
  #define ASURO 1
  #ifndef __AVR_ATmega328P__
    #define __AVR_ATmega328P__
  #endif
#elif ASURO_ATMEGA328
  #define F_CPU 12000000UL
  #define ASURO 1
  #ifndef __AVR_ATmega328__
    #define __AVR_ATmega328__
  #endif
#elif SURE_ATMEGA16
  #define F_CPU 12000000UL
  #define SURE 1
  #ifndef __AVR_ATmega16__
    #define __AVR_ATmega16__
  #endif
#elif CRUMB128_AT90CAN128
  #define F_CPU 16000000UL
  #define CRUMB128 1
  #ifndef __AVR_AT90CAN128__
    #define __AVR_AT90CAN128__
  #endif
#elif PROJECT_ATMEGA324P
  #define F_CPU 16000000UL
#ifndef __AVR_ATmega324P__
    #define __AVR_ATmega324P__
  #endif
#else
  #error "Error: unsupported board"
#endif

#ifdef CRUMB128_AT90CAN128
  #define GLOBAL_UART_BITRATE 115200
#else
  #define GLOBAL_UART_BITRATE  57600
#endif
#define GLOBAL_UART_RECBUFSIZE    16

// Comment out the line .. to disable ...

#ifdef SURE
//#define GLOBAL_SURE_LCD 1
//#define GLOBAL_SURE_SEG7 1
#endif

// Monitor commands, maximum number of arguments (command itself is first)
#define GLOBAL_MON_MAXARGV 4

#define GLOBAL_MONITOR
//#define GLOBAL_MONCMD_HEXDUMP
//#define GLOBAL_MONCMD_SETMEM


#endif // GLOBAL_H_INCLUDED

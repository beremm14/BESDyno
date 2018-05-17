#ifndef GLOBAL_H_INCLUDED
#define GLOBAL_H_INCLUDED

// globale Defines...
#ifdef ARDUINO_UNO_R3_ATMEGA328P
  #define F_CPU 16000000UL
  #define ARDUINO
  #define ARDUINO_UNO
  #define ARDUINO_UNO_R3
  #ifndef __AVR_ATmega328P__
    #define __AVR_ATmega328P__
  #endif
#elif ARDUINO_UNO_R3_ATMEGA328
  #define F_CPU 16000000UL
  #define ARDUINO
  #define ARDUINO_UNO
  #define ARDUINO_UNO_R3
  #ifndef __AVR_ATmega328__
    #define __AVR_ATmega328__
  #endif
#elif ASURO_ATMEGA328P
  #define F_CPU 12000000UL
  #define ASURO
  #ifndef __AVR_ATmega328P__
    #define __AVR_ATmega328P__
  #endif
#elif ASURO_ATMEGA328
  #define F_CPU 12000000UL
  #define ASURO
  #ifndef __AVR_ATmega328__
    #define __AVR_ATmega328__
  #endif
#elif SURE_ATMEGA16
  #define F_CPU 12000000UL
  #define SURE
  #ifndef __AVR_ATmega16__
    #define __AVR_ATmega16__
  #endif
#else    
  #ifdef __AVR_ATmega16__
    #define SURE
  #elif __AVR_ATmega328P__
    #define ASURO
  #elif __AVR_ATmega328__
    #define ASURO
  #else
    #error "Kein gueltiger uC-Typ gewaehlt"
  #endif
#endif


#define GLOBAL_UART_BITRATE    57600
#define GLOBAL_UART_RECBUFSIZE    16

#endif // GLOBAL_H_INCLUDED

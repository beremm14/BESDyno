#ifndef DHT22_H_INCLUDED
#define DHT22_H_INCLUDED

// Deklaration Strukturvariablentyp

//setup port
#define DHT_DDR DDRD
#define DHT_PORT PORTD
#define DHT_PIN PIND
#define DHT_INPUTPIN PD6

//sensor type
#define DHT_DHT11 1
#define DHT_DHT22 2
#define DHT_TYPE DHT_DHT22

//enable decimal precision (float)
#if DHT_TYPE == DHT_DHT11
#define DHT_FLOAT 0
#elif DHT_TYPE == DHT_DHT22
#define DHT_FLOAT 1
#endif

//timeout retries
#define DHT_TIMEOUT 200

//values

void dht22(int8_t *tempHigh, uint8_t *tempLow, uint8_t *humidityHigh);

#endif // APP_H_INCLUDED

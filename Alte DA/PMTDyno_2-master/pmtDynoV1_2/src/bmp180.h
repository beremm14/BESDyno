#ifndef BMP180_H_INCLUDED
#define BMP180_H_INCLUDED

void bmp180_init(void);

// Deklaration Strukturvariablentyp

void getBmp180Values(int8_t *temperatureH, uint8_t *temperatureL, char pressure[], int maxPressureLength);

#endif // APP_H_INCLUDED

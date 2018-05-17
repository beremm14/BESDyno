#include "global.h"

#include <stdio.h>
#include <string.h>

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <util/twi.h>

#define BMP_WRITE_ADRESS 0xEE
#define BMP_READ_ADRESS 0xEF
#define F_SCL 100000UL // SCL frequency
#define Prescaler 1
#define TWBR_val ((((F_CPU / F_SCL) / Prescaler) - 16 ) / 2)
// Defines, nur in app.c sichtbar
// ...

//globale Variablen nur in app.c sichtbar
// ...

//globale Strukturvariable, ueberall sichtbar
struct BMP180Variables//strukture for calibration and measurement values
{
  short AC1;
  short AC2;
  short AC3;
  unsigned short AC4;
  unsigned short AC5;
  unsigned short AC6;
  short B1;
  short B2;
  short MB;
  short MC;
  short MD;
  
  long UT;
  long UP;
  long X1;
  long X2;
  long X3;
  long B3;
  long B4;
  long B5;
  long B6;
  long B7;
  long T;
  long P;
};

void bmp180_init (void)
{
  TWCR = (1<<TWINT) | (1<<TWEN) | (1<<TWSTO);
}

uint8_t i2c_start(uint8_t address)
{
	// reset TWI control register
	TWCR = 0;
	// transmit START condition 
	TWCR = (1<<TWINT) | (1<<TWSTA) | (1<<TWEN);
	// wait for end of transmission
	while( !(TWCR & (1<<TWINT)) );
	
	// check if the start condition was successfully transmitted
	if((TWSR & 0xF8) != TW_START){ return 1; }
	
	// load slave address into data register
	TWDR = address;
	// start transmission of address
	TWCR = (1<<TWINT) | (1<<TWEN);
	// wait for end of transmission
	while( !(TWCR & (1<<TWINT)) );
	
	// check if the device has acknowledged the READ / WRITE mode
	uint8_t twst = TW_STATUS & 0xF8;
	if ( (twst != TW_MT_SLA_ACK) && (twst != TW_MR_SLA_ACK) ) return 1;
	
	return 0;
}

uint8_t i2c_write(uint8_t data)
{
	// load data into data register
	TWDR = data;
	// start transmission of data
	TWCR = (1<<TWINT) | (1<<TWEN);
	// wait for end of transmission
	while( !(TWCR & (1<<TWINT)) );
	
	if( (TWSR & 0xF8) != TW_MT_DATA_ACK ){ return 1; }
	
	return 0;
}

uint8_t i2c_read_ack(void)
{
	// start TWI module and acknowledge data after reception
	TWCR = (1<<TWINT) | (1<<TWEN) | (1<<TWEA); 
	// wait for end of transmission
	while( !(TWCR & (1<<TWINT)) );
	// return received data from TWDR
	return TWDR;
}

void i2c_stop(void)
{
	// transmit STOP condition
	TWCR = (1<<TWINT) | (1<<TWEN) | (1<<TWSTO);
}
//------------------------------------------------------------

struct BMP180Variables getCalibrationValues(struct BMP180Variables cal)
{
  uint8_t i = 0xaa;
  uint8_t j = 0;
  for(j=0;j<11;j++)
  {
    i2c_start(0xee);
    i2c_write(i); 
    i2c_stop();
    i2c_start(0xef);
    switch(j)
    {
      case 0: cal.AC1 = ((uint8_t)i2c_read_ack())<<8;
        cal.AC1 |= i2c_read_ack();
        break;
      case 1: cal.AC2 = ((uint8_t)i2c_read_ack())<<8;
        cal.AC2 |= i2c_read_ack();
        break;
      case 2: cal.AC3 = ((uint8_t)i2c_read_ack())<<8;
        cal.AC3 |= i2c_read_ack();
        break;
      case 3: cal.AC4 = ((uint8_t)i2c_read_ack())<<8;
        cal.AC4 |= i2c_read_ack();
        break;
      case 4: cal.AC5 = ((uint8_t)i2c_read_ack())<<8;
        cal.AC5 |= i2c_read_ack();
        break;
      case 5: cal.AC6 = ((uint8_t)i2c_read_ack())<<8;
        cal.AC6 |= i2c_read_ack();
        break;
      case 6: cal.B1 = ((uint8_t)i2c_read_ack())<<8;
        cal.B1 |= i2c_read_ack();
        break;
      case 7: cal.B2 = ((uint8_t)i2c_read_ack())<<8;
        cal.B2 |= i2c_read_ack();
        break;
      case 8: cal.MB = ((uint8_t)i2c_read_ack())<<8;
        cal.MB |= i2c_read_ack();
        break;
      case 9: cal.MC = ((uint8_t)i2c_read_ack())<<8;
        cal.MC |= i2c_read_ack();
        break;
    case 10: cal.MD = ((uint8_t)i2c_read_ack())<<8;
        cal.MD |= i2c_read_ack();
        break;
    }
    i2c_stop();
    i = i+2;
  }
  return cal;
}

struct BMP180Variables getTemp(struct BMP180Variables cal)
{
  i2c_start(0xee);
  i2c_write(0xf4);
  i2c_write(0x2e);
  i2c_stop();
  
  _delay_ms(4.5);
  
  i2c_start(0xee);
  i2c_write(0xf6);
  i2c_stop();
  
  i2c_start(0xef);
  cal.UT = ((uint16_t)i2c_read_ack())<<8;
  cal.UT |= i2c_read_ack();
  i2c_stop();
  
  cal.X1 = (cal.UT-cal.AC6)*cal.AC5/32768.0;
  //printf("\nx1: %d\n",cal.X1);
  
  cal.X2 = cal.MC*2048.0/(cal.X1+cal.MD);
  //printf("x2: %d\n",(int)cal.X2);
  
  cal.B5 = (cal.X1+cal.X2);
  //printf("B5: %d\n",cal.B5);
  
  cal.T = (cal.B5+8)/16.0;
  
  
  return cal;
}

struct BMP180Variables getPressure(struct BMP180Variables cal)
{
  i2c_start(0xee);
  i2c_write(0xf4);
  i2c_write(0x34);
  i2c_stop();
  _delay_ms(10);
  i2c_start(0xee);//ee->Slave adress + write
  i2c_write(0xf6);//ef->Slave adress + read
  i2c_stop();
  i2c_start(0xef);
  cal.UP = ((uint16_t)i2c_read_ack())<<8;
  cal.UP |= i2c_read_ack();
  i2c_stop();

  cal.UP = cal.UP;
  //printf("%ld\n",cal.UP);
  cal.B6 = cal.B5 - 4000;
  //printf("B6:%ld\n",cal.B6);
  cal.X1 = (cal.B2 *(cal.B6 * cal.B6/4096.0))/2048.0;
  //printf("X1:%ld\n",cal.X1);
  cal.X2 = cal.AC2 * cal.B6 / 2048.0;
  //printf("X2:%ld\n",cal.X2);
  cal.X3 = cal.X1 + cal.X2;
  //printf("X3:%ld\n",cal.X3);
  cal.B3 = ((cal.AC1*4.0+cal.X3)+2)/4.0;
  //printf("B3:%ld\n",cal.B3);
  cal.X1 = cal.AC3*cal.B6/8192.0;
  //printf("X1:%ld\n",cal.X1);
  cal.X2 = (cal.B1*(cal.B6*cal.B6/4096.0))/65536.0;
  //printf("X2:%ld\n",cal.X2);
  cal.X3 = ((cal.X1+cal.X2)+2)/4.0;
  //printf("X3:%ld\n",cal.X3);
  cal.B4 = cal.AC4*(unsigned long)(cal.X3+32768)/32768.0;
  //printf("B4:%ld\n",cal.B4);
  cal.B7 = ((unsigned long)cal.UP-cal.B3)*(50000);
  //printf("B7:%ld\n",cal.B7);
  if(cal.B7<0x80000000)
  {
    cal.P = (cal.B7*2.0)/cal.B4;
    //printf("P:%ld\n",cal.P);
  }
  else
  {
    cal.P = (cal.B7/cal.B4)*2.0;
    //printf("P:%ld\n",cal.P);
  }
  cal.X1 = (cal.P/256.0) * (cal.P/256.0);
  //printf("X1:%ld\n",cal.X1);
  cal.X1 = (cal.X1 * 3038)/65536.0;
  //printf("X1:%ld\n",cal.X1);
  cal.X2 = (-7357*cal.P)/65536.0;
  //printf("X2:%ld\n",cal.X2);
  cal.P = cal.P +(cal.X1+cal.X2+3791)/16.0;
  
  return cal;
}

void getBmp180Values(int8_t *temperatureH, uint8_t *temperatureL, char pressure[],int maxPressureLength)
{
  struct BMP180Variables cal;
  cal = getCalibrationValues(cal);
  int16_t temp;
  
/*
  printf("AC1:%d AC2:%d AC3:%d AC4%d AC5%d AC6%d\n B1:%u B2:%u\n MB:%u MC:%d MD:%d\n UT:%d",cal.AC1,cal.AC2,cal.AC3,cal.AC4,cal.AC5,cal.AC6,
            cal.B1,cal.B2,cal.MB,cal.MC,cal.MD,cal.UT);
*/
  cal = getTemp(cal);
  temp = cal.T;
  temperatureH = (int8_t *)(temp/10);
  temperatureL = (uint8_t *)(temp%10);
  
  cal=getPressure(cal);
  snprintf(pressure,maxPressureLength,"%ld",cal.P);
}
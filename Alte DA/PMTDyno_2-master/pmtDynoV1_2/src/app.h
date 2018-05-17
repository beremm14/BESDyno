#ifndef APP_H_INCLUDED
#define APP_H_INCLUDED

// declarations

#define APP_UART_BUFFER_SIZE 64

#define APP_SOT 2
#define APP_EOT 3

struct App_Uart
{
  uint8_t rxBuffer[APP_UART_BUFFER_SIZE];
  uint8_t txBuffer[APP_UART_BUFFER_SIZE];
  uint8_t recIndex;
  uint8_t framePending;
  uint8_t errCnt_recFrameTooLong;
  uint8_t errCnt_recFrameWhilePending;
  uint8_t errCnt_recFrameError;
};

struct App_Timer
{
  uint32_t motorRpm;
  uint32_t shaftRpm;
  uint32_t ovfCounter0;
  uint32_t ovfCounter1;
  uint32_t ovfCounter2;
  uint8_t edgeDetection0;
  uint8_t edgeDetection1;
  uint8_t busyMoto;
  uint8_t busyWelle;
};
struct App
{
  struct App_Uart uart;
  struct App_Timer timer;
};

extern volatile struct App app;


// defines

#define APP_EVENT_FRAME_RECEIVED   0x01
#define APP_EVENT_1   0x02
#define APP_EVENT_2   0x04
#define APP_EVENT_3   0x08
#define APP_EVENT_4   0x10
#define APP_EVENT_5   0x20
#define APP_EVENT_6   0x40
#define APP_EVENT_7   0x80


// functions

void app_init (void);
void app_main (void);

void app_uart_isr (uint8_t b);

#endif // APP_H_INCLUDED

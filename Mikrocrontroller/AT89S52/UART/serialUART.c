#include <reg52.h>

char uart_data;
char x;
char y;

void init(){
    SCON = 0x50;  		// uart mode 1 (8 bit) REN=1 
    TMOD = TMOD | 0x20 ;        // Timer 1  mode 2 
    TH1  = 0xF5;                // Serial Overflow Bytes
    TL1  = 0xF5;     		// 9600 Bds at 11.059MHz, 4800Bds at 20MHz
    ES = 1;        		// Enable serial interrupt
    EA = 1;         		// Enable global interrupt 
    TR1 = 1;     		// Start Timer 1       
}

void main () 
{
    init();
    while(1){                    /* endless */
        if(P2!=0xFF){
            SBUF = 66;
            for(y = 0; y < 255; y = y + 1){
                for(x = 0; x < 255; x = x + 1);
            }
        }
    
   }
}

void serial_IT(void) interrupt 4 
{
if (RI == 1) 
{                       // On reception
  RI = 0;            	// Clear flag
  uart_data = SBUF;     // Read data
  SBUF = uart_data;     //  Send back same data
}
else TI = 0;        	// on emission
}                 	// clear emission flag
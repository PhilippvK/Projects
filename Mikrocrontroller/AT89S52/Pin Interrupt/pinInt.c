#include <reg52_.h>

static char count = 0;

void ISR_in() interrupt 0 { 
    count = count + 1;
    if(count >= 200){
        count = 200;
        P3_0 = 0;
    }
    P0 = ~count;
}

void ISR_out() interrupt 2 { 
    count = count - 1;
    if(count == 255){
        count = 0;
    }
    P3_0 = 0;
    P0 = ~count;
}

void main(){
    IT0 = 1; // Enable Pin Interrupt 0
    IT1 = 1; // Enable Pin Interrupt 1
    EX0 = 1; // Enable Rising Edge Detection on P3_0
    EX1 = 1; // Enable Rising Edge Detection on P3_1
    EA = 1;  // Global Interrupt Enable
}
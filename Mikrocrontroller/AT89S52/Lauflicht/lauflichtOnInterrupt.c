#include <reg52_.h>

void waiting(){
    char w;
    TR0=1;
    for(w=0; w < 7; w = w + 1){
        while(TF0!=1){}
		TF0=0;
    }
    TR0=0;
    TF0=0;
    TL0=0;
}

void ISR_in() interrupt 0 { 
    char c;
    for(c = 1; c > 0; c = c << 1){
        P0=~c;
        waiting();
    }
    P0=0xFF;
    waiting();
    waiting();
}


void main(){
    IT0 = 0;
    EX0 = 1;
    EA = 1;
    TMOD = 1;
    while(1){
    
    }
}
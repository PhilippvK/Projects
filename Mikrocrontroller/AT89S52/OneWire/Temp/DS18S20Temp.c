#include <reg52.h>

/*
 * THIS IST BASED ON THE 1WIRE TEMPERATURE SENSOR DS18S20
 * Only works with max. 1 sensor
 */
 
at 0xB3 sbit P3_3;

char i;
int j;
char x;
char out;
char out2;
char out_;
char maske;

void reset(){
	bit bus = 1;
	while(bus == 1){
		P3_3 = 0;
		for(i = 0; i< 100; i++){}
		P3_3 = 1;
		for(i = 0; i< 14; i++){}
		bus = P3_3;
		for(i = 0; i< 85; i++){}
	}
	
}

void send(char b){
    x = b;
		maske = 1;
		while (maske != 0){
				if ((maske & x) == 0){
					P3_3 = 0;
					for(i = 0; i< 12; i++){}
					P3_3 = 1;
					for(i = 0; i< 2; i++){}
					maske = maske << 1;
				} else {
					P3_3 = 0;
					for(i = 0; i< 1; i++){}
					P3_3 = 1;
					for(i = 0; i< 13; i++){}
					maske = maske << 1;
				}
		}
        for(i = 0; i< 100; i++){}
}

char recieve(){
    char c = 0x00;
    maske = 1;
	while (maske != 0){  
        P3_3 = 0;
        for(i = 0; i< 1; i++){}
        P3_3 = 1;
        for(i = 0; i< 2; i++){}
        if(P3_3 == 1){
           c = c + maske;
        } else {}
        for(i = 0; i< 8; i++){}
        for(i = 0; i< 2; i++){}
        maske = maske << 1;
    }
    return c;
}
void main(){
    mode = 0;  
	while(1){
    
		// Reset
		reset();
		
        send(0xCC);
        
        send(0x44);
          
        for(j = 0; j < 600; j++){ // long wait for measure
            for(i = 0; i< 255; i++){}
        }
        
        reset();
        
        send(0xCC);
        
        send(0xBE);
        
		
		if (mode == 0){
			out = (recieve()/2);
		} else {
			out = recieve();
			out2 = recieve();
			out = ((out >> 4) | (out2 << 4));
		}
        
        P0 = ~out;   
        
        
	}
	
	
	
}
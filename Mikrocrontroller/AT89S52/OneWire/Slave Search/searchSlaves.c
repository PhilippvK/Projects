#include <reg52.h>

at 0xB3 sbit P3_3;
at 0xB4 sbit P3_4;

#define ONE P3_3	//Pin für OneWire


char x;
char i;
char maske;
code char addr[8][8] = {    {0x9C,0x00,0x00,0x06,0x1E,0xAC,0x11,0x28},
                            {0x45,0x00,0x00,0x06,0x20,0xD2,0x99,0x28},
                            {0x5F,0x00,0x00,0x06,0x20,0x4A,0x33,0x28},  
                            {0xB1,0x00,0x00,0x06,0x1F,0x16,0xDB,0x28},  
                            {0xBC,0x00,0x00,0x06,0x21,0x56,0x07,0x28},
                            {0x25,0x00,0x00,0x06,0x1E,0xEB,0x94,0x28},
                            {0xCB,0x00,0x00,0x06,0x20,0xE8,0x3C,0x28},
                            {0x3D,0x00,0x00,0x06,0x1E,0xF6,0x7C,0x28}};

bit reset(){ //OneWire Reset Impuls
	bit bus = 1;
		ONE = 0;
		for(i = 0; i< 100; i++){} //? ms
		ONE = 1;
		for(i = 0; i< 14; i++){}  //? ms
		bus = ONE;
		for(i = 0; i< 85; i++){}  //? ms
    if(bus == 0){
        return 1;
	} else {
        return 0;
    }
}

void sendByte(char in){      //OneWire send Byte x
		maske = 1;
		while (maske != 0){
				if ((maske & in) == 0){
					ONE = 0;
					for(i = 0; i< 12; i++){}    //? ms
					ONE = 1;
					for(i = 0; i< 2; i++){}     //? ms
					maske = maske << 1;
				} else {
					ONE = 0;
					for(i = 0; i< 1; i++){}     //? ms
					ONE = 1;
					for(i = 0; i< 13; i++){}    //? ms
					maske = maske << 1;
				}
		}
        for(i = 0; i< 100; i++){}       //? ms
}

void sendBit(char in){      //OneWire send Bit x
	if (in == 0){
		ONE = 0;
		for(i = 0; i< 12; i++){}    //? ms
		ONE = 1;
		for(i = 0; i< 2; i++){}     //? ms
		maske = maske << 1;
	} else {
		ONE = 0;
		for(i = 0; i< 1; i++){}     //? ms
		ONE = 1;
		for(i = 0; i< 13; i++){}    //? ms
		maske = maske << 1;
    }
    for(i = 0; i< 100; i++){}       //? ms
}

char receiveByte(){     //OneWire receive Byte c
    char c = 0x00;
    maske = 1;
	while (maske != 0){  
        ONE = 0;
        for(i = 0; i< 1; i++){}     //? ms
        ONE = 1;
        for(i = 0; i< 2; i++){}     //? ms
        if(ONE == 1){
           c = c + maske;
        } else {}
        for(i = 0; i< 8; i++){}     //? ms
        for(i = 0; i< 2; i++){}     //? ms
        maske = maske << 1;
    }
    return c;
}

void printBinary(){
    char j; 
    char out;
    SBUF = 50; 
    newLine();
    SBUF = 48;
    while(TI == 0){}
    TI = 0;
    SBUF = 98;
    while(TI == 0){}
    TI = 0;

    for(j = 0; j < SIZE;j = j + 1){
        out = ((addr[j/8]) & (0b1 << (7-(j%8))));
        if(out != 0){
            SBUF = 49; 
        } else {
            SBUF = 48; 
        }
        
        
        while(TI == 0){}
        TI = 0;
    }
   
}

void initUART(){
    SCON = 0x50;  /* uart in mode 1 (8 bit), REN=1 */
    TMOD = TMOD | 0x20 ;         /* Timer 1 in mode 2 */
    TH1  = 0xF5;                 /* 9600 Bds at 11.059MHz */ /* 4800 Bds at 20.000MHz */
    TL1  = 0xF5;     /* 9600 Bds at 11.059MHz */ /* 4800 Bds at 20.000MHz */
    ES = 1;         /* Enable serial interrupt*/
    EA = 1;         /* Enable global interrupt */
    TR1 = 1;     /* Timer 1 run */
}


void printHex(){
    char j; 
    char out;
    char out2 = 0;
    newLine();
    SBUF = 48;
    while(TI == 0){}
    TI = 0;
    SBUF = 120;
    while(TI == 0){}
    TI = 0;

    for(j = 0; j < SIZE;j = j + 1){
        out = ((addr[j/8]) & (0b1 << (7-(j%8))));
        if((j % 4) == 0){
            out2 = (out != 0)*8;
        } else if((j % 4) == 1){
            out2 = out2 +(out != 0)*4;
        } else if((j % 4) == 2){
            out2 = out2 +(out != 0)*2;
        } else {
            out2 = out2 +(out != 0);
            if(out2 < 10){
                SBUF = out2+48; 
            } else {
                SBUF = (out2-10)+65; 
            }
            out2 = 0;
            
            while(TI == 0){}
            TI = 0;
        }
    
        
    }
   
}

bit findAdress(char letztePos){
    char letzteNull = 0;
    char b,b0,b1;
    char index;
    reset();
    sendByte(0xF0);
    for(index = 1; index <=64; index++){
        b0 = receiveBit();
        b1 = receiveBit();
        if((b0 == 1)&&(b1 == 1)){
            return 0;
        } else {
            if((b0 == 0) && (b1 == 0)){
                if(index == letztePos){
                    b = 1;
                } else {
                    if(index < letztePos){
                        b = 0 != (addr[7-(index-1)/8] & (0b1 << (((index-1)%8))));
                    } else {
                        b = 0;
                    }
                }
                if(b == 0){
                    letzteNull = index;
                }
            } else {
                b = b0;
            }
            P3_4 = 1;
            if(b != 0){
                addr[7-(index-1)/8] |= (0b1 << (((index-1)%8)));
            } else {
                addr[7-(index-1)/8] &= ~(0b1 << (((index-1)%8)));
            }
            P3_4 = 0;
            sendBit(b);
        }      
    }
    last = letzteNull;
    return 1;

    
}


void sendROM(){
    char stelle;
    sendByte(0x55);
    for(stelle = 0; stelle < 8; stelle++){
        sendByte(addr[7-stelle]);
    }
}

char getCRC(){
  char divisor = 0b10001100;
  char currentByte = 0b0;
  char currentCRC = 0b0;
  char i0;
  char i1;
  char carry = 0; 
  for(i0=0;i0 < 7; i0 = i0 + 1){
    currentByte = addr[7-i0];
    
    for(i1 = 0; i1 < 8; i1 = i1 + 1){
      if((currentByte % 2) == 1){
        carry = carry + 1;
      }
      if((currentCRC % 2) == 1){
        carry = carry + 1;
      }

      
      currentByte = currentByte >> 1;
      currentCRC = currentCRC >> 1;
      
      
      if(carry == 1){
        currentCRC = currentCRC ^ divisor;
      }

      carry = 0;
    }
  }
  return currentCRC;
}

void main(){
    initUART();
    while(1){                    
        if(P2!=0xFF){ // when button pressed
            if(findAdress(last)){
                printBinary();
                printHex();
                if(getCRC() == addr[0]){ // if crc okay
                    SBUF = 58;
                    while(TI == 0){}
                    TI = 0;
                } else { // error
                    SBUF = 33;
                    while(TI == 0){}
                    TI = 0;
                }   
            } else { // error
                SBUF = 33;
                while(TI == 0){}
                TI = 0;
            }
            for (zaehl2 = 0; zaehl2 < 400; zaehl2 = zaehl2 + 1){ // wait ~ half second
                for(i = 0; i< 255; i++){} 
            }
            
            newLine();
        }
    }
}

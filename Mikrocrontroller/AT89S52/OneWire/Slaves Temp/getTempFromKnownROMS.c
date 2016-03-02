#include <reg52.h>

/*
 * THIS IST BASED ON THE 1WIRE TEMPERATURE SENSOR DS18S20
 */


#define ONE P3_3	//Pin für OneWire


char x;
char i;
char maske;
code char addr[8][8] = {    {0x9C,0x00,0x00,0x06,0x1E,0xAC,0x11,0x28}, // 8 known ROMS!
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

void initUART(){
    SCON = 0x50;  /* uart in mode 1 (8 bit), REN=1 */
    TMOD = TMOD | 0x20 ;         /* Timer 1 in mode 2 */
    TH1  = 0xF5;                 /* 9600 Bds at 11.059MHz */ /* 4800 Bds at 20.000MHz */
    TL1  = 0xF5;     /* 9600 Bds at 11.059MHz */ /* 4800 Bds at 20.000MHz */
    ES = 1;         /* Enable serial interrupt*/
    EA = 1;         /* Enable global interrupt */
    TR1 = 1;     /* Timer 1 run */
}

int nthdigit(int x, int n)
{
    while (n--) {
        x /= 10;
    }
    return (x % 10);
}

void sendROM(char select){
    char stelle;
    sendByte(0x55);
    for(stelle = 0; stelle < 8; stelle++){
        sendByte(addr[select][7-stelle]);
    }
}

void printTemp(char vorkomma, char nachkomma){
    SBUF = 48+nthdigit(vorkomma,1);
    while(TI == 0){}
    TI = 0;
    SBUF = 48+nthdigit(vorkomma,0);
    while(TI == 0){}
    TI = 0;
    SBUF = 44;
    while(TI == 0){}
    TI = 0;
    SBUF = 48+nthdigit(nachkomma,3);
    while(TI == 0){}
    TI = 0;
    SBUF = 48+nthdigit(nachkomma,2);
    while(TI == 0){}
    TI = 0;
    SBUF = 48+nthdigit(nachkomma,1);
    while(TI == 0){}
    TI = 0;
    SBUF = 48+nthdigit(nachkomma,0);
    while(TI == 0){}
    TI = 0;
    SBUF = 39;
    while(TI == 0){}
    TI = 0;
    SBUF = 67;
    while(TI == 0){}
    TI = 0;
}
void measure(char select){
        if(reset()){
           sendROM(select); 
           sendByte(0x44); //TRIGGER MEASURE
        }
}

float getTemp(char select){
    
    char zahl;
    char nachkomma;
    char byte0, byte1;
    if(reset()){
            sendROM(select); 
            sendByte(0xBE); //ALLOW TO SEND
            byte0 = receiveByte();
            byte1 = receiveByte();
            zahl = ((byte0 >> 4) | ((byte1 << 4) & 0xF0));
            nachkomma = (byte0 & 0xF0)*625;
            printTemp(zahl, nachkomma);
            return (zahl);
    }
    return 0;
}

void newLine(){
        SBUF = 13;
        while(TI == 0){}
        TI = 0;
        SBUF = 10;
        while(TI == 0){}
        TI = 0;
}

void printHex(char select){
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

    for(j = 0; j < 64;j = j + 1){
        out = ((addr[select][j/8]) & (0b1 << (7-(j%8))));
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

void main(){
    int zaehl2;
    int zaehl;
    char s;
    initUART();
   
    while(1){
            for(s=0;s<8;s = s + 1){
            measure(s);
            }
                
            for(zaehl = 0; zaehl < 500; zaehl++){       //Langes warten auf auswertung
                for(i = 0; i< 255; i++){}   //ODER BIT LESEN!
            }
        
            for(s=0;s<8;s = s + 1){
            
                getTemp(s);
                if(s != 7){
                    SBUF = 44;
                    while(TI == 0){}
                    TI = 0;
                } else {
                    SBUF = 13;
                    while(TI == 0){}
                    TI = 0;
                }
            
            }
                
                
            }
            for (zaehl2 = 0; zaehl2 < 2000; zaehl2 = zaehl2 + 1){ // long wait
                for(i = 0; i< 255; i++){} 
            }
            
            newLine();
        }
    



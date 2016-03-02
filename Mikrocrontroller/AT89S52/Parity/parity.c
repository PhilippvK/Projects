#include <reg52_.h>

code char Data[8] = {0xFE,0x01,0xF4,0x7F,0xAB,0x65,0x69,0x11};
char Parity[2]; // [0] = Parity for the horizontal rows, [1] = Parity for the vertical collums

void setParityV1(){
	char i;
	Parity[0]=0xFF;
    	Parity[1]=0xFF;
	for(i = 0; i < 64 ; i = i + 1){
		if (((Data[i/8] & (0b1 << (7-(i%8)))) != 0) ){
			Parity[0] ^= (0b1 << ((7-(i/8))));
			Parity[1] ^= (0b1 << ((7-(i%8))));
		}	
	}
}



void setParityV2(){
	char i;
	Parity[0]=0xFF;
   	Parity[1]=0xFF;
	for(i = 0; i < 64 ; i = i + 1){
		Parity[0] ^= (((Data[i/8] & (0b1 << (7-(i%8)))) != 0) << ((7-(i/8))));
		Parity[1] ^= (((Data[i/8] & (0b1 << (7-(i%8)))) != 0) << ((7-(i%8))));
	}
}

void setParityV3(){
	char i;
	char maske;
	Parity[0]=0xFF; Parity[1]=0xFF;
	for(i = 0; i < 8 ; i = i + 1){
		Parity[1] ^= Data[i];
		for(maske = 1; maske != 0; maske = maske << 1)
			Parity[0] ^= ((Data[i] & maske) != 0) << i);
		}	
	}
}

void main(){
   
    setParityV3();

    while(1){
        
    }
}
void main(){
  char bytes[8] = {0x28,0x3C,0xE8,0x20,0x06,0x00,0x00,0xCB}; // 8 Data Bytes = 64 bit (CRC = 0x28)

  char currentByte = 0b0;
  char currentCRC = 0b0;
  char i;
  char j;
  char carry = 0; 

  for(i=0;i < 8; i = i + 1){
    currentByte = bytes[7-i];
    
    for(j = 0; j < 8; j = j + 1){
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

  // Print or Compare!

  while(1){
  
  }
}
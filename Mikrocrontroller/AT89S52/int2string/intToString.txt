char get_num_len (unsigned int value){
  char l=1;
  while(value>9){ l++; value/=10; }
  return l;
}

char nthdigit(unsigned int x, char n){
    while (n--) {
        x /= 10;
    }
    return (x % 10);
}

void main(){
    unsigned int out = 69;
    char len = get_num_len(out);
    char str[len] = ""; // must 
    char zz;


    for(zz = 0; zz < len; zz = zz + 1){
        str[zz] = (48+nthdigit(out,len-1-zz));
    }

    //strcat(str,". Zahl"); // TESTING SOON

   while(1){
   
   }
}
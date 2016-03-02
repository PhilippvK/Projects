#include <reg52_.h>

unsigned int fibo(char n){
    int sum = 0, alt2=0, alt1=0;
    char i = 1;
    while(i <= n){
        if(i < 3){
            sum = 1;
        } else {
            sum = alt2 + alt1;
        }
        alt2 = alt1;
        alt1 = sum;
        i = i + 1;
    }
    return sum;

}

void sleep(int s){
    int i;
    unsigned char c;
    for(i = 0; i < s; i = i + 1){
        for(c = 0; c < 255; c = c + 1){
        }
    }

}

void main(){
    while(1){
        unsigned int test = fibo(24);
        P0=~0;
        sleep(30000);
        P0=~(bla/256);
        sleep(30000);
        P0=~(bla%256);
        sleep(30000);
    }

}
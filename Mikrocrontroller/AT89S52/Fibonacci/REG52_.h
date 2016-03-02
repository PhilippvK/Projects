/*----------------------------------------*/
/* Include file for 8052 SFR Definitions  */
/* Copyright Raisonance SA, 1990-2003     */
/*----------------------------------------*/

/*  BYTE Registers  */
at 0x80 sfr P0    ;
at 0x90 sfr P1    ;
at 0xA0 sfr P2    ;
at 0xB0 sfr P3    ;
at 0xD0 sfr PSW   ;
at 0xE0 sfr ACC   ;
at 0xF0 sfr B     ;
at 0x81 sfr SP    ;
at 0x82 sfr DPL   ;
at 0x83 sfr DPH   ;
at 0x87 sfr PCON  ;
at 0x88 sfr TCON  ;
at 0x89 sfr TMOD  ;
at 0x8A sfr TL0   ;
at 0x8B sfr TL1   ;
at 0x8C sfr TH0   ;
at 0x8D sfr TH1   ;
at 0xA8 sfr IE    ;
at 0xB8 sfr IP    ;
at 0x98 sfr SCON  ;
at 0x99 sfr SBUF  ;

/*  Portbits  */
at 0x80 sbit P0_0  ;
at 0x81 sbit P0_1  ;
at 0x82 sbit P0_2  ;
at 0x83 sbit P0_3  ;
at 0x84 sbit P0_4  ;
at 0x85 sbit P0_5  ;
at 0x86 sbit P0_6  ;
at 0x87 sbit P0_7  ;
at 0x90 sbit P1_0  ;
at 0x91 sbit P1_1  ;
at 0x92 sbit P1_2  ;
at 0x93 sbit P1_3  ;
at 0x94 sbit P1_4  ;
at 0x95 sbit P1_5  ;
at 0x96 sbit P1_6  ;
at 0x97 sbit P1_7  ;
at 0xA0 sbit P2_0  ;
at 0xA1 sbit P2_1  ;
at 0xA2 sbit P2_2  ;
at 0xA3 sbit P2_3  ;
at 0xA4 sbit P2_4  ;
at 0xA5 sbit P2_5  ;
at 0xA6 sbit P2_6  ;
at 0xA7 sbit P2_7  ;
at 0xB0 sbit P3_0  ;
at 0xB1 sbit P3_1  ;
at 0xB2 sbit P3_2  ;
at 0xB3 sbit P3_3  ;
at 0xB4 sbit P3_4  ;
at 0xB5 sbit P3_5  ;
at 0xB6 sbit P3_6  ;
at 0xB7 sbit P3_7  ;

/*  8052 Extensions  */
at 0xC8 sfr T2CON ;
at 0xCA sfr RCAP2L;
at 0xCB sfr RCAP2H;
at 0xCC sfr TL2   ;
at 0xCD sfr TH2   ;


/*  BIT Registers  */
/*  PSW  */
at 0xD7 sbit CY    ;
at 0xD6 sbit AC    ;
at 0xD5 sbit F0    ;
at 0xD4 sbit RS1   ;
at 0xD3 sbit RS0   ;
at 0xD2 sbit OV    ;
at 0xD1 sbit F1    ;
at 0xD0 sbit P     ;

/*  TCON  */
at 0x8F sbit TF1   ;
at 0x8E sbit TR1   ;
at 0x8D sbit TF0   ;
at 0x8C sbit TR0   ;
at 0x8B sbit IE1   ;
at 0x8A sbit IT1   ;
at 0x89 sbit IE0   ;
at 0x88 sbit IT0   ;

/*  IE  */
at 0xAF sbit EA    ;
at 0xAC sbit ES    ;
at 0xAB sbit ET1   ;
at 0xAA sbit EX1   ;
at 0xA9 sbit ET0   ;
at 0xA8 sbit EX0   ;

/*  IP  */
at 0xBC sbit PS    ;
at 0xBB sbit PT1   ;
at 0xBA sbit PX1   ;
at 0xB9 sbit PT0   ;
at 0xB8 sbit PX0   ;

/*  P3  */
at 0xB7 sbit RD    ;
at 0xB6 sbit WR    ;
at 0xB5 sbit T1    ;
at 0xB4 sbit T0    ;
at 0xB3 sbit INT1  ;
at 0xB2 sbit INT0  ;
at 0xB1 sbit TXD   ;
at 0xB0 sbit RXD   ;

/*  SCON  */
at 0x9F sbit SM0   ;
at 0x9E sbit SM1   ;
at 0x9D sbit SM2   ;
at 0x9C sbit REN   ;
at 0x9B sbit TB8   ;
at 0x9A sbit RB8   ;
at 0x99 sbit TI    ;
at 0x98 sbit RI    ;

/*  8052 Extensions  */
/*  IE  */
at 0xAD sbit ET2   ;

/*  IP  */
at 0xBD sbit PT2   ;

/*  P1  */
at 0x91 sbit T2EX  ;
at 0x90 sbit T2    ;

/*  T2CON  */
at 0xCF sbit TF2   ;
at 0xCE sbit EXF2  ;
at 0xCD sbit RCLK  ;
at 0xCC sbit TCLK  ;
at 0xCB sbit EXEN2 ;
at 0xCA sbit TR2   ;
at 0xC9 sbit C_T2  ;
at 0xC8 sbit CP_RL2;

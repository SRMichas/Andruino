#include <EEPROM.h>
//#include <SoftwareSerial.h>
#include <DHT11.h>
//SoftwareSerial BT(11,10);
#define H A1
#define T 12
DHT11 dht11(T);
int ENA = 10, IN1 = 9,IN2= 8;
int FC1 = 0, FC2 = 0,V = 11,B=13;
float hum,temp,hum11;
boolean flag = true;
char ch;
void setup()
{
  pinMode(A1,INPUT);
  pinMode(T,INPUT);
  pinMode(ENA,OUTPUT);
  pinMode(IN1,OUTPUT);
  pinMode(IN2,OUTPUT);
  pinMode(FC1,INPUT);
  pinMode(FC2,INPUT);
  pinMode(V,OUTPUT);
  pinMode(B,OUTPUT);
  //BT.begin(38000);
  Serial.begin(38000);
}

void loop(){
  ch = ' ';
  if( Serial.available()){
    ch = Serial.read();
    switch(ch){
      case 'A':
        flag = false;
        desactivaSistema();
        break;
      case 'E':
        flag = true;
        break;
    }
  }
  if( flag ){
    chkTemp();
    delay(500);
  }
  /*if(BT.available()){
    ch = BT.read();
    if( ch == '0'){*/
      //chkHum();
      
      //BT.print(";");
      
      
    /*}
  }*/
  
  /*if( Serial.available()){
    ch = char(Serial.read());
    guarda(ch);
    flag = true;
  }else{
    char ch = recupera();
    if( ch == 'T')
      flag = true;
    else
      flag = false;
  }*/
 // if( flag ){
    //chkHum();
    //delay(1000);
    
 // }
  //chkTemp();
  //delay(500);
}
void chkHum(){ 
  /*hum = analogRead(H);
  hum = 100.0 - ((hum /1023.0)*100.0);*/
  hum = analogRead(A1);
  hum = 100.0 - ((hum/1023.0)*100.0);
  Serial.print(String(hum)+"_");
  if( hum > 65){
    Serial.print("Maximo -> Dejar de Regar_");
    digitalWrite(B,LOW);
  }
  if( hum > 40 && hum < 65){
    flag = false;
  }
  if( hum > 30 && hum < 40){
    if( flag ){
      Serial.print("Regando..._");
    }
  }
  if( hum < 30){
    Serial.print("Critico -> Regado Automatico_");
    digitalWrite(B,HIGH);
    flag = true;
  }
  //BT.print(String(hum)+" %");
  //Serial.print(String(hum)+" % ");
  //delay(200);
}
void chkTemp(){
  //temp = analogRead(T); 
  
  int v1 = digitalRead(FC1), v2 = digitalRead(FC2);
  //temp = ((5.0/1024.0)*temp)*100;
  if( dht11.read(hum11,temp) == 0){
    chkHum();
    Serial.print("!");
    Serial.print(String(temp)+"_");
      if( temp >= 20){
    //if( v1 == HIGH && v2 == LOW){
        Serial.print("Abriendo..._");
        digitalWrite(V,HIGH);
        adelante();
    //}
    //if( v1 == LOW && v2 == HIGH){ 
        Serial.print("Techo Abierto_");
      //parar();
      //digitalWrite(V,LOW);
    //}
    }
    if( temp <= 10){
    //if( v1 == LOW && v2 == HIGH){
      Serial.print("Cerrando..._");
        digitalWrite(V,LOW);
      atras();
    //}
    //if( v1 == HIGH && v2 == LOW){
      //Serial.print("Techo Cerrado ");
      //parar();

    //}
    }
    Serial.println();
  }
  
  //Serial.print(String(temp)+" °C");
  //Serial.println(",");
  //Serial.println(String(temp)+" C ");
  //delay(200);
}
void adelante(){
    digitalWrite (IN1, HIGH);
    digitalWrite (IN2, LOW);
    analogWrite (ENA, 255); 
}
void atras(){
    digitalWrite (IN1, LOW);
    digitalWrite (IN2, HIGH);
    analogWrite (ENA, 255); 
}
void parar(){
    digitalWrite (IN1, LOW);
    digitalWrite (IN2, LOW);
    analogWrite (ENA, 0);
}
void guarda(char op){
    EEPROM.write(0,op);
}
char recupera(){
  return char(EEPROM.read(0));
}
void desactivaSistema(){
  digitalWrite(V,LOW);
  digitalWrite(B,LOW);
}

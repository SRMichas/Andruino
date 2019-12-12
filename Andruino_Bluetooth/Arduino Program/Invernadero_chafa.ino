#include <DHT11.h>
#define H A1
#define T 12
DHT11 dht11(T);
int ENA = 10, IN1 = 9,IN2= 8;
int FC1 = 7, FC2 = 6,V = 11,B=2;
float hum,temp,hum11;
boolean sistema = true,regar=false,
ventilacion=false,puerta=false,algo=false;
char ch;
void setup()          
{
  pinMode(H,INPUT);    //definimos como entrada el pin del sensor de humedad de tirra
  pinMode(T,INPUT); //definimos como entrada el pin del sensor de temperatura y humedad
  pinMode(ENA,OUTPUT);  //definimos como salida el pin del activado del puente H
  pinMode(IN1,OUTPUT); //definimos como salida el pin de control de giro 1
  pinMode(IN2,OUTPUT); //definimos como salida el pin de control de giro 2
  pinMode(FC1,INPUT);   //definimos como entrada el pin para el final de carrera 1
  pinMode(FC2,INPUT);   //definimos como entrada el pin para el final de carrera 2
  pinMode(V,OUTPUT);    //definimos como salida el pin para el ventilador
  pinMode(B,OUTPUT);    //definimos como salida el pin para la bomba de agua
  Serial.begin(38000); //
}

void loop(){
  ch = ' ';   //caracter de control
 
  if( Serial.available()){  //pregunta si hay algo informacion entrante
    ch = Serial.read();     //lee el caracter enviado
    switch(ch){             
      case '0':             
        sistema = false;    //se apaga la bandera que controla el sistema
        desactivaSistema(); //metodo que desactiva el sistema
        break;
      case '1': sistema = true; break;    //encendemos la bandera que controla el sistema
      case '2': ventilacion = false; break; //apagamos la bandera que controla la ventilacion
      case '3': ventilacion = true; break; //encendemos la bandera que controla la ventilacion
      case '4': regar = false; break; //apagamos la bandera que controla la bomba
      case '5': regar = true; break; //encendemos la bandera que controla la bomba
      case '6': puerta = false; break;  //apagamos la bandera que controla la puerta del techo
      case '7': puerta = true; break; //encendemos la bandera que controla la puerta del trecho
    }
  }
  if( sistema ){  //pregunta si el sistema esta activado
    chkTemp();  //llamamos el metodo que revisa la temperatura
    delay(500); //detenemos el programa por medio segundo
  }else{
    Serial.println("Sistema Desactivado");  //mensage que se envia a la aplicacion
  }
}
void chkHum(){ 
  hum = analogRead(H);    //leemos el valor de la humedad en 
  hum = 100.0 - ((hum/1023.0)*100.0); //ajustamos el valor leido para obtener una escala del 0 - 100 %
  Serial.print(String(hum)+"_");  //enviamos el valor de la humedad
  
  if( regar )   //pregunta si la bandera bomba de agua esta encendiada
    digitalWrite(B,HIGH); //encendemos la bomba
  else
    digitalWrite(B,LOW);  //apagamos la bomba

  if( hum > 65){  //en caso de la humedad sea mayor al 65%
    Serial.print("Maximo -> Dejar de Regar_");  //mensaje para la aplicacion
    digitalWrite(B,LOW);  //apagamos la bomba 
  }
  if( hum > 40 && hum < 65){  //nivel de humedad entre 40 y 65 %
    algo = false; //apagamos la bandera para evitar mostrar en mensaje
  }
  if( hum > 30 && hum < 40){  //nivel de humedad entre 30 y 40
    if( algo ){ //pregunta si esta encendida
      Serial.print("Regando..._"); //imprimimos el mensaje
    }
  }
  if( hum < 30){  //nivel de humedad por debajo del 30%
    Serial.print("Critico -> Regado Automatico_");  //imprimimos el mensaje
    digitalWrite(B,HIGH); //encendemos la bomba
    algo = true;  //encendemos la bandera
    regar = false; //apagamos la bandera que controla la bomba
  }
  if( digitalRead(B) == HIGH ){ //caracteres de control para la apliacion
      Serial.print("+B_");  //caracter para encender un switch de la app
  }else
      Serial.print("B_");   //caracter para apagar un switch de la app
}
void chkTemp(){
  int v1 = digitalRead(FC1), v2 = digitalRead(FC2); //leemos los valoes de los finales de carrera
  if( dht11.read(hum11,temp) == 0){ //preguntamos si hay un fallo al leer la temperatura
    chkHum();   //metodo que monitorea la humedad
    Serial.print("!");  //caracter de control para direnciar los mensajes de humedad y temperatura
    Serial.print(String(temp)+"_"); //enviamos la temperatura

    if( ventilacion )   //pregunta si esta encendiad la bandera
      digitalWrite(V,HIGH); //prendemos el ventilador

    if( puerta )  //pregunta si la bandera esta encendida la bandera
      adelante(); //abrimos la puerta

    if( temp >= 23 ){ //temperatura mayor o igual a 20°
      if( v1 == HIGH && v2 == LOW){ //la puerta esta cerrada?
        Serial.print("Abriendo..._"); //enviamos mensaje
        digitalWrite(V,HIGH); //encendemos el ventilador
        adelante(); //abrimos la puerta
      }
      if( v1 == LOW && v2 == LOW)
      Serial.print("Abriendo..._"); //enviamos mensaje
      
      if( v1 == LOW && v2 == HIGH){ //la puerta esta cerrada?
        Serial.print("Techo Abierto_"); //enviamos mensaje
        parar();    //apagamos el motor
      }
    }
    if( temp <= 20){  //temperatura menor o igual a 15°
      if( v1 == LOW && v2 == HIGH){ //puerta abierta?
        Serial.print("Cerrando..._"); //enviamos mensaje
        digitalWrite(V,LOW);  ////apagamos el ventilador
        atras();  //cerramos la puerta
      }
      if( v1 == HIGH && v2 == LOW){ //pierta cerrada?
        Serial.print("Techo Cerrado "); //enviamos mensaje
        parar();  //apagamos el motor
      }
    }
    if( digitalRead(V) == HIGH ) //caracter de control para el ventilador
        Serial.print("+V_"); //caracter para encender un switch de la app
    else
        Serial.print("V_"); //caracter para apagar un switch de la app

    if( v1 == LOW && (v2 == LOW || v2 == HIGH) )//caracter de control el techo
        Serial.print("+P_"); //caracter para encender un switch de la app
    else
        Serial.print("P_"); //caracter para apagar un switch de la app
    Serial.println(); //cortamos los mensajes
  } 
}
void adelante(){
    //instrucciones para controlar la direccion del motor (normal)
    digitalWrite (IN1, HIGH); 
    digitalWrite (IN2, LOW); 
    analogWrite (ENA, 255); //velocidad/fuerza del motor
}
void atras(){
    //instrucciones para controlar la direccion del motor (polaridad inversa)
    digitalWrite(IN1, LOW); 
    digitalWrite(IN2, HIGH);
    analogWrite (ENA, 255); //velocidad/fuerza del motor
}
void parar(){
    //instrucciones para controlar la direccion del motor (frenado por inercia)
    digitalWrite (IN1, LOW);
    digitalWrite (IN2, LOW);
    analogWrite (ENA, 0); //velocidad/fuerza del motor
}
void desactivaSistema(){
  digitalWrite(V,LOW);  //apagamos ventilador
  digitalWrite(B,LOW);  //apagamos bomba
  parar();  //apagamos el motor
}

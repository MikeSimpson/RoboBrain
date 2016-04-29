#include <SoftwareSerial.h>
#include <AFMotor.h>
#include <Servo.h>
 
int bluetoothTx = 2; // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3; // RX-I pin of bluetooth mate, Arduino D3

AF_DCMotor motorLeft(4);
AF_DCMotor motorRight(3);

Servo clawServo;
Servo phoneServo;

SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

int lastLeft = 0;
int lastRight = 0;

int clawAngle = 0;
int phoneAngle = 0;

int stepCount = 0;
 
void setup(){
   Serial.begin(9600); // Begin the serial monitor at 9600bps
   

         clawServo.attach(10);
   
   bluetooth.begin(115200); // The Bluetooth Mate defaults to 115200bps
   bluetooth.print("$"); // Print three times individually
   bluetooth.print("$");
   bluetooth.print("$"); // Enter command mode
   delay(100); // Short delay, wait for the Mate to send back CMD
   bluetooth.println("U,9600,N"); // Temporarily Change the baudrate to 9600, no parity
   // 115200 can be too fast at times for NewSoftSerial to relay the data reliably
   bluetooth.begin(9600); // Start bluetooth serial at 9600
}
 
void loop(){
  
//         phoneServo.attach(10);
//         clawServo.attach(9);
//         clawServo.write(110);
//         Serial.print("phone: ");
//         Serial.print(phoneServo.read());
//        Serial.print(" claw: ");
//        Serial.println(clawServo.read());

  
   if(bluetooth.available()){ // If the bluetooth sent any characters
          Serial.print("Data: ");

     // Send any characters the bluetooth prints to the serial monitor
     String dataFromBt = "";
     while (bluetooth.available()){
        char character = bluetooth.read(); // Receive a single character from the software serial port
        if(character == 'n'){
          break;
        }
        dataFromBt.concat(character); // Add the received character to the receive buffer    
    }
     
     Serial.print("Data: ");
     Serial.println(dataFromBt);
     char tag = dataFromBt.charAt(0);
     dataFromBt = dataFromBt.substring(1);
     if(tag == 'd'){
       int commaIndex = dataFromBt.indexOf(',');
       int leftFromBt = dataFromBt.substring(0,commaIndex).toInt();
       int rightFromBt = dataFromBt.substring(commaIndex+1).toInt();
  //     Serial.print("Left: ");
  //     Serial.println(leftFromBt);
  //     Serial.print("Right: ");
  //     Serial.println(rightFromBt);
  
  //     int smoothedRight = lastRight + (rightFromBt-lastRight)/10;
       if(rightFromBt<0){
         motorRight.run(BACKWARD);
       }else{
         motorRight.run(FORWARD);
       }
       motorRight.setSpeed(abs(rightFromBt));
  //     lastRight = smoothedRight;
       
  //     int smoothedLeft = lastLeft + (leftFromBt-lastLeft)/10;
        if(leftFromBt<0){
         motorLeft.run(FORWARD);
       }else{
         motorLeft.run(BACKWARD);
       }
       motorLeft.setSpeed(abs(leftFromBt));
      //     lastLeft = smoothedLeft;
     }else if(tag == 'c'){
       clawAngle = dataFromBt.toInt();
//       clawServo.attach(9);
       
       clawServo.write(clawAngle);
//       delay(1000);
//       clawServo.detach();
     }else if(tag == 'p'){
       phoneAngle = dataFromBt.toInt();
       phoneServo.attach(9);
       
       phoneServo.write(phoneAngle);
       delay(2000);
       phoneServo.detach();
     }      
//     if(clawServo.attached()){
//      if(clawServo.read() - clawAngle < 10){
//         clawServo.detach();
//      }else{
//        clawServo.write(clawAngle);
//      }
//     }
//     
//     if(phoneServo.attached()){
//       if(phoneServo.read() - phoneAngle <10){
//         phoneServo.detach();
//       }else{
//        phoneServo.write(phoneAngle); 
//       }
//     }
//     if(stepCount % 100 == 0){ //check angle every 100 iterations
//       clawServo.attach(9);
//       phoneServo.attach(10);
//     }
//     stepCount++;
   }
}

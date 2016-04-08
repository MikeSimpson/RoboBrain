#include <SoftwareSerial.h>
#include <AFMotor.h>
 
int bluetoothTx = 2; // TX-O pin of bluetooth mate, Arduino D2
int bluetoothRx = 3; // RX-I pin of bluetooth mate, Arduino D3

AF_DCMotor motorLeft(4);
AF_DCMotor motorRight(3);

SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

int lastLeft = 0;
int lastRight = 0;
 
void setup(){
   Serial.begin(9600); // Begin the serial monitor at 9600bps
   
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
   if(bluetooth.available()){ // If the bluetooth sent any characters
     
     // Send any characters the bluetooth prints to the serial monitor
     String dataFromBt = "";
     while (bluetooth.available()){
        char character = bluetooth.read(); // Receive a single character from the software serial port
        dataFromBt.concat(character); // Add the received character to the receive buffer    
    }
     
     Serial.print("Data: ");
     Serial.println(dataFromBt);
     int commaIndex = dataFromBt.indexOf(',');
     int leftFromBt = dataFromBt.substring(0,commaIndex).toInt();
     int rightFromBt = dataFromBt.substring(commaIndex+1).toInt();
//     Serial.print("Left: ");
//     Serial.println(leftFromBt);
//     Serial.print("Right: ");
//     Serial.println(rightFromBt);

     int smoothedRight = lastRight + (rightFromBt-lastRight)/10;
     if(smoothedRight<0){
       motorRight.run(BACKWARD);
     }else{
       motorRight.run(FORWARD);
     }
     motorRight.setSpeed(abs(smoothedRight));
     lastRight = smoothedRight;
     
     int smoothedLeft = lastLeft + (leftFromBt-lastLeft)/10;
      if(smoothedLeft<0){
       motorLeft.run(FORWARD);
     }else{
       motorLeft.run(BACKWARD);
     }
     motorLeft.setSpeed(abs(smoothedLeft));
     lastLeft = smoothedLeft;
            
//     if(Serial.available()){ // If stuff was typed in the serial monitor
//       // Send any characters the Serial monitor prints to the bluetooth
//       bluetooth.print((char)Serial.read());
//     }
   }
//   delay(10);
}

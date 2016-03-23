package nz.co.mikesimpson.robobrain.UDP;

import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class UDPClient extends UDPMachine {

    private static final int PORT = 5000;
    private DatagramSocket clientSocket;
    private boolean go = true;
    private InetAddress IPAddress;

    public UDPClient(final UDPCallBackActivity callback, final InetAddress IPAddress){
        this.IPAddress = IPAddress;
        try {
            clientSocket = new DatagramSocket(5000);
        } catch (SocketException e) {
//            clientSocket = null;
            e.printStackTrace();
        }

        //send first packet so server has ip


        new Thread(new Runnable() {

            @Override
            public void run() {

                sendData("0,0");
                while(go){
                    byte[] receiveData = new byte[1024];
//                    System.out.println("recieving: ");
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        clientSocket.receive(receivePacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String data = new String( receivePacket.getData()).trim();
                    callback.recieveData(data);

                }
            }
        }).start();
    }

    public void sendData(String data){
//        System.out.println("sending: "+data);

//        data = data.trim();
        byte[] sendData = new byte[1024];
        sendData = data.getBytes();

//        System.out.println("sending: "+sendData.toString());
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
//        go = false;
        clientSocket.close();
    }

}
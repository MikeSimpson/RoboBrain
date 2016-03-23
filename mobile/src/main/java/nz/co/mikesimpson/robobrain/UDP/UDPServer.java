package nz.co.mikesimpson.robobrain.UDP;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Mike Simpson on 21/03/2016.
 */
public class UDPServer extends UDPMachine {


    private DatagramSocket serverSocket;
//    private static final int PORT = 5000;
    private boolean go = true;


    public InetAddress IPAddress;
    private int port;

    public UDPServer(final UDPCallBackActivity callback) {


        try {
            serverSocket = new DatagramSocket(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                while(go)
                {
                    byte[] receiveData = new byte[1024];

                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        serverSocket.receive(receivePacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String data = new String(receivePacket.getData()).trim();
                    System.out.println("data: "+data);

                    callback.recieveData(data);

//                    if(UDPServer.this.IPAddress == null) {
                        UDPServer.this.IPAddress = receivePacket.getAddress();
                    UDPServer.this.port = receivePacket.getPort();
//                    }
                    port = receivePacket.getPort();
                }
            }
        }).start();

    }

    @Override
    public void sendData(String data) {
        System.out.println("cat: "+IPAddress);
        if(IPAddress != null) {
//            data = data.trim();
            byte[] sendData = new byte[1024];
            sendData = data.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            try {
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        go = false;
        serverSocket.close();
    }
}

package nz.co.mikesimpson.robobrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import nz.co.mikesimpson.robobrain.UDP.UDPCallBackActivity;
import nz.co.mikesimpson.robobrain.UDP.UDPClient;
import nz.co.mikesimpson.robobrain.UDP.UDPMachine;
import nz.co.mikesimpson.robobrain.UDP.UDPServer;

public class RobotActivity extends UDPCallBackActivity {

    public static final String EXTRA_SERVER_IP = "server_ip";

//    private ServerSocket serverSocket;

//    Handler updateConversationHandler;

//    Thread serverThread = null;

    private TextView text;

//    public static final int SERVERPORT = 5000;

    //bluetooth
    public static final String EXTRA_DEVICE_NAME = "device_name";
    private OutputStream outputStream;
    private BluetoothSocket btSocket;
    private String serverIP;
    private UDPMachine udpMachine;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        //open bt connection
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        BluetoothDevice btDevice = null;
        String selectedDevice = this.getIntent().getStringExtra(EXTRA_DEVICE_NAME);
        for(BluetoothDevice bt:pairedDevices){
            if(bt.getName().equals(selectedDevice)){
                btDevice = bt;
            }
        }
        UUID uuid  = btDevice.getUuids()[0].getUuid();
        try {
            btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            outputStream = btSocket.getOutputStream();
            Toast.makeText(getApplicationContext(),"Connection acquired" ,Toast.LENGTH_SHORT).show();
//            outputStream.write(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        text = (TextView) findViewById(R.id.tv_messages);

        serverIP = this.getIntent().getStringExtra(EXTRA_SERVER_IP);

        //UDP
        if(serverIP != null) {//if we are given an IP then we are client
            System.out.println("server: "+serverIP);

            try {
                this.udpMachine = new UDPClient(this, InetAddress.getByName(serverIP));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else{
            this.udpMachine = new UDPServer(this);
            ((TextView)findViewById(R.id.tv_header)).setText("Server open on ip "+ Utils.getIPAddress(true) + "\n");

        }

//        updateConversationHandler = new Handler();

//        this.serverThread = new Thread(new ServerThread());
//        this.serverThread.start();




    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            btSocket.close();
            udpMachine.close();
//            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recieveData(final String data) {

        try {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.setText("Signal: "+data + "\n"+text.getText().toString());
                }
            });
            if(outputStream != null) {
                System.out.println("writing: "+data);
                outputStream.write(data.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    class ServerThread implements Runnable {
//
//        public void run() {
//            Socket socket = null;
//            try {
//                serverSocket = new ServerSocket(SERVERPORT);
//
//                //                serverSocket.setSoTimeout(30000);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            text.setText(text.getText().toString()+"Server open on ip "+ Utils.getIPAddress(true) + "\n");
//
//            while (!Thread.currentThread().isInterrupted()) {
//
//                try {
//
//                    socket = serverSocket.accept();
//
//                    CommunicationThread commThread = new CommunicationThread(socket);
//                    new Thread(commThread).start();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    class CommunicationThread implements Runnable {
//
//        private Socket clientSocket;
//
//        private BufferedReader input;
//
//        public CommunicationThread(Socket clientSocket) {
//
//            this.clientSocket = clientSocket;
//
//            try {
//
//                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void run() {
//
//            while (!Thread.currentThread().isInterrupted()) {
//
//                try {
//
//                    int read = Integer.parseInt(input.readLine());
//
//                    updateConversationHandler.post(new updateUIThread(read));
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }

//    class updateUIThread implements Runnable {
//        private int msg;
//
//        public updateUIThread(int str) {
//            this.msg = str;
//        }
//
//        @Override
//        public void run() {
//            text.setText(text.getText().toString()+"Client Says: "+ msg + "\n");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        outputStream.write(msg);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }
//    }
}
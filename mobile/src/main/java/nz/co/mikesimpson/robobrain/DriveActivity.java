package nz.co.mikesimpson.robobrain;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import nz.co.mikesimpson.robobrain.UDP.UDPCallBackActivity;
import nz.co.mikesimpson.robobrain.UDP.UDPClient;
import nz.co.mikesimpson.robobrain.UDP.UDPMachine;
import nz.co.mikesimpson.robobrain.UDP.UDPServer;

public class DriveActivity extends UDPCallBackActivity {

    public static final String EXTRA_SERVER_IP = "server_ip";

    private Thread buttonScanThread;

    //sockety stuff
//    private Socket socket;

//    private static final int SERVERPORT = 5000;
    private String serverIP;
    private UDPMachine udpMachine;
    private VertSeekBar throttleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        serverIP = this.getIntent().getStringExtra(EXTRA_SERVER_IP);

        //UDP
        if(serverIP != null) {//if we are given an IP then we are client
            try {
                this.udpMachine = new UDPClient(this,InetAddress.getByName(serverIP));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else{
            this.udpMachine = new UDPServer(this);
            ((TextView)findViewById(R.id.tv_server_ip)).setText("Server open on ip "+ Utils.getIPAddress(true) + "\n");
        }


//        final SeekBar leftBar = (SeekBar) findViewById(R.id.sb_left);
//        leftBar.setMax(500);
        throttleBar = (VertSeekBar) findViewById(R.id.sb_throttle);
        throttleBar.setMax(250);

        //servey stuff
//        new Thread(new ClientThread()).start();

        buttonScanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //get state of buttons
//                    if(!leftBar.isPressed()){
//                        leftBar.setProgress(250);
//                    }
//                    if(!rightBar.isPressed()){
//                        rightBar.setProgress(250);
//                    }
                    int left = 0;// = leftBar.getProgress()-250;
                    int right = 0;// = rightBar.getProgress()-250;

                    int throttle = throttleBar.getProgress();
                    if(findViewById(R.id.bt_left_forward).isPressed()){
                        left = throttle;
                    }
                    if(findViewById(R.id.bt_right_forward).isPressed()){
                        right = throttle;
                    }
                    if(findViewById(R.id.bt_left_back).isPressed()){
                        left = -throttle;
                    }
                    if(findViewById(R.id.bt_right_back).isPressed()){
                        right = -throttle;
                    }
//                    if(serverIP != null) {
                    System.out.println("Left: "+left+" Right: "+right);

                        sendOrder(left+","+right);
//                        sendOrder(right);
//                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttonScanThread.start();


    }

    @Override
    public void onStart(){
        super.onStart();
//        findViewById(R.id.ll_seek).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1.0f));

    }

    private void sendOrder(final String order){
        new Thread(new Runnable() {
            @Override
            public void run() {
                udpMachine.sendData(order);
            }
        }).start();
//        if(socket != null) {
//            try {
//                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//                out.println(order);
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void recieveData(String data) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        udpMachine.close();
    }

//    class ClientThread implements Runnable {
//
//        @Override
//        public void run() {
//
//            try {
//                InetAddress serverAddr = InetAddress.getByName(serverIP);
//
//                socket = new Socket(serverAddr, SERVERPORT);
//
//            } catch (UnknownHostException e1) {
//                e1.printStackTrace();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//
//        }
//
//    }
}

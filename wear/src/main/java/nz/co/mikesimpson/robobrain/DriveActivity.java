package nz.co.mikesimpson.robobrain;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class DriveActivity extends Activity {

    public static final String EXTRA_DEVICE_NAME = "device_name";
    public static final int LEFT_FORWARD = 0;
    public static final int RIGHT_FORWARD = 1;
    public static final int LEFT_BACK = 2;
    public static final int RIGHT_BACK = 3;

    BluetoothDevice pairedDevice;
    private OutputStream outputStream;
    private BluetoothSocket socket;
    private Thread thread;
    private Button btLeftForward;
    private Button btLeftBack;
    private Button btRightForward;
    private Button btRightBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);


        btLeftForward=(Button)findViewById(R.id.bt_left_forward);
        btLeftBack=(Button)findViewById(R.id.bt_left_back);
        btRightForward=(Button)findViewById(R.id.bt_right_forward);
        btRightBack=(Button)findViewById(R.id.bt_right_back);


    }

    protected void onStart(){
        super.onStart();
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
            socket = btDevice.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            outputStream = socket.getOutputStream();
            Toast.makeText(getApplicationContext(),"Connection acquired" ,Toast.LENGTH_SHORT).show();
            outputStream.write(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //setup buttons
//        findViewById(R.id.bt_left_back).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                sendOrder(LEFT_BACK);
//                return false;
//            }
//        });
//        findViewById(R.id.bt_right_back).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                sendOrder(RIGHT_BACK);
//                return false;
//            }
//        });
//        findViewById(R.id.bt_left_forward).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                sendOrder(LEFT_FORWARD);
//                return false;
//            }
//        });
//        findViewById(R.id.bt_right_forward).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                sendOrder(RIGHT_FORWARD);
//                return false;
//            }
//        });

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //get state of buttons
                    String left = "3";
                    String right = "3";
                    if(btLeftForward.isPressed()){
                        left = "1";
                    }
                    if(btRightForward.isPressed()){
                        right = "1";
                    }
                    if(btLeftBack.isPressed()){
                        left = "2";
                    }
                    if(btRightBack.isPressed()){
                        right = "2";
                    }
                    sendOrder(Integer.parseInt(left+right));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void sendOrder(int order){
        try {
            outputStream.write(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy(){
        try {
            socket.close();
//            thread.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}

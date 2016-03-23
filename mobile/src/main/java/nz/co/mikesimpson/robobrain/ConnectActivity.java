package nz.co.mikesimpson.robobrain;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class ConnectActivity extends Activity  {
    Button b1,b2,b3,b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    private String selectedDevice;
    ListView lv;


    private OutputStream outputStream;
    private InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);
    }


    public  void client(View v){
        Intent intent = new Intent(ConnectActivity.this,DriveActivity.class);
        if(((ToggleButton)findViewById(R.id.tb_server_client)).isChecked()) {
            intent.putExtra(DriveActivity.EXTRA_SERVER_IP, ((TextView) findViewById(R.id.et_server_ip)).getText().toString());
        }
        ConnectActivity.this.startActivity(intent);
        Toast.makeText(getApplicationContext(),"Acquiring connection" ,Toast.LENGTH_SHORT).show();
    }

    public void list(final View v){
        pairedDevices = BA.getBondedDevices();
        final ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());
//        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                 view.setBackgroundColor(Color.BLUE);
//                lv.setSelection(position);
//                selectedDevice = (String) list.get(position);
                Intent intent = new Intent(ConnectActivity.this,RobotActivity.class);
                intent.putExtra(RobotActivity.EXTRA_DEVICE_NAME,(String) list.get(position));
                System.out.println("starting intent: "+((ToggleButton)findViewById(R.id.tb_server_client)).isChecked());

                if(((ToggleButton)findViewById(R.id.tb_server_client)).isChecked()) {
                    System.out.println("starting intent: "+((TextView) findViewById(R.id.et_server_ip)).getText().toString());

                    intent.putExtra(DriveActivity.EXTRA_SERVER_IP, ((TextView) findViewById(R.id.et_server_ip)).getText().toString());
                }
                ConnectActivity.this.startActivity(intent);
                Toast.makeText(getApplicationContext(),"Acquiring connection" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
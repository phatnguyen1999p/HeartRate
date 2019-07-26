package com.application.heartrate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.application.heartrate.Thread.ConnectThread;
import com.application.heartrate.Thread.ManageConnectThread;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class Bluetooth_Connected extends AppCompatActivity {
    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice bluetoothDevice;
    private ManageConnectThread manageConnectThread = new ManageConnectThread();
    private ConnectThread connectThread;
    private ShowDataTask showDataTask = new ShowDataTask();
    String Address;
    Button Cancel_btn;
    boolean Status;
    TextView DATA_TV;
    TextView TIME_TV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth__connected);
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Address = bundle.getString("Selected_Device");
        }

        bluetoothDevice = BTAdapter.getRemoteDevice(Address);
        connectThread = new ConnectThread(bluetoothDevice);
        Status = connectThread.connect();

        Cancel_btn = findViewById(R.id.cancel_btn);
        Cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataTask.cancel(true);
                //connectThread.cancel();
                finish();
                /*Intent intent1 = new Intent(Bluetooth_Connected.this, MainActivity.class);
                startActivity(intent1);*/
            }
        });

        TextView Device_Name = findViewById(R.id.DEVICE_TEXTVIEW);
        Device_Name.setText(bluetoothDevice.getName());

        TextView Device_Address = findViewById(R.id.MAC_ADDRESS_TEXT);
        Device_Address.setText(bluetoothDevice.getAddress());

        TextView Status_TV = findViewById(R.id.STATUS);
        if (Status) {
            Status_TV.setText("KẾT NỐI THÀNH CÔNG");
            Status_TV.setTextColor(Color.GREEN);
        } else {
            Status_TV.setText("KẾT NỐI KHÔNG THÀNH CÔNG");
            Status_TV.setTextColor(Color.RED);
        }

        final Button Start_btn = findViewById(R.id.START_BTN);
        Start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start_btn.setEnabled(false);
                showDataTask.execute();
            }
        });
    }

    protected void onPause(){
        super.onPause();
            connectThread.cancel();
    }

    /*protected void onResume() {
        super.onResume();
        bluetoothDevice = BTAdapter.getRemoteDevice(Address);
        connectThread = new ConnectThread(bluetoothDevice);

        if (!connectThread.mySocket().isConnected()) {
            Status = connectThread.connect();
        }
        TextView Status_TV = findViewById(R.id.STATUS);
        if (Status) {
            Status_TV.setText("KẾT NỐI THÀNH CÔNG");
            Status_TV.setTextColor(Color.GREEN);
        } else {
            Status_TV.setText("KẾT NỐI KHÔNG THÀNH CÔNG");
            Status_TV.setTextColor(Color.RED);
        }
    }*/

    private class ShowDataTask extends AsyncTask<Void, String, Void> {
        int i = 0;
        ArrayList<String> AllInput = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {

            while (connectThread.mySocket().isConnected()) {
                try {
                    String[] Input = manageConnectThread.receiveData(connectThread.mySocket());
                    //Log.d("DATA", Input);
                    //Log.d("DATA", String.valueOf(Input));
                    AllInput.addAll(Arrays.asList(Input));
                    publishProgress( String.valueOf(Input));
                }
                catch (IOException e){
                    Log.d("RECEIVE_ERROR", e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            TIME_TV = findViewById(R.id.TIME_TEXTVIEW);
            DATA_TV = findViewById(R.id.DATA_TEXTVIEW);
            TIME_TV.setText(String.valueOf(i++));
            DATA_TV.setText(Arrays.deepToString(values));
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            String[] temp = AllInput.toArray(new String[AllInput.size()]);
            FileIO.saveData(temp);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}

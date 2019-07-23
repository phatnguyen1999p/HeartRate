package com.application.heartrate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class BLConnecting_Activity extends AppCompatActivity {
    private BluetoothAdapter BTAdapter;
    public static int REQUEST_BLUETOOTH = 1;
    private ArrayList<BluetoothDevice> DevicesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blconnecting);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Khong Kha Dung")
                    .setMessage("Thiet bi khong ho tro Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else if (!BTAdapter.isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }
        createList();
    }
    public void createList(){
        DevicesList = getPairedDevices();
        ArrayList<String> DeviceName = new ArrayList<>();

        for (BluetoothDevice i: DevicesList){
            DeviceName.add(i.getName());
        }

        ListView listView = findViewById(R.id.List);
        ArrayAdapter<String> ListContent = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, DeviceName);
        listView.setAdapter(ListContent);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String value = (String)adapterView.getItemAtPosition(position);
                //Log.d("Selected Device",value);
                BluetoothDevice devices = selectedDevice(value);
                String Address = devices.getAddress();
                Intent intent = new Intent(BLConnecting_Activity.this,Bluetooth_Connected.class);

                Bundle bundle = new Bundle();
                bundle.putString("Selected_Device",Address);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    public BluetoothDevice selectedDevice(String Name){
        //Devices temp = new Devices();

        for (BluetoothDevice temp: DevicesList) {
            if (temp.getName().equals(Name)) {
                Log.d("Selected Device", temp.getAddress()+" " + temp.getName());
                return temp;
            }
        }
        return null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                recreate();
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }
    public ArrayList getPairedDevices(){
        Set<BluetoothDevice> bondedDevices;
        if (BTAdapter != null) {
            bondedDevices = BTAdapter.getBondedDevices();
            for (BluetoothDevice Device: bondedDevices){
                DevicesList.add(Device);
            }
        }
        return DevicesList;
    }
}

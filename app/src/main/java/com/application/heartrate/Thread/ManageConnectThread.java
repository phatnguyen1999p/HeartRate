package com.application.heartrate.Thread;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ManageConnectThread extends Thread {
    public ManageConnectThread() { }

    public void sendData(BluetoothSocket socket, int data) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4);
        output.write(data);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(output.toByteArray());
    }

    public String receiveData(BluetoothSocket socket) throws IOException{
        byte[] buffer = new byte[8];
        //ByteArrayInputStream input = new ByteArrayInputStream(buffer);
        InputStream inputStream = socket.getInputStream();
        //inputStream.read(buffer);

        int bytes = inputStream.read(buffer);
        String tmp = new String(buffer, 0, bytes);
        /*if (tmp!=";") {
            String Message = Message + tmp;
        }*/
        Log.d("STRING", tmp);
        //String string = convert(inputStream,Charset.forName("UTF-8"));
        return tmp;
    }
}


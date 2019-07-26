package com.application.heartrate.Thread;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class ManageConnectThread extends Thread {
    public ManageConnectThread() { }

    public void sendData(BluetoothSocket socket, int data) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4);
        output.write(data);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(output.toByteArray());
    }

    String temp =new String();
    public String[] receiveData(BluetoothSocket socket) throws IOException{
        byte[] buffer = new byte[1024];
        while (true){
            ByteArrayInputStream input = new ByteArrayInputStream(buffer);
            InputStream inputStream = socket.getInputStream();
            int bytes = inputStream.read(buffer);

            String tmp = new String(buffer, 0, bytes);
            tmp = temp + tmp;

            String[] tmp_1 = tmp.split(System.getProperty("line.separator"));
            temp = tmp_1[tmp_1.length-1];
            tmp_1 = Arrays.copyOf(tmp_1,tmp_1.length-1);
            return tmp_1;
        }
    }
}


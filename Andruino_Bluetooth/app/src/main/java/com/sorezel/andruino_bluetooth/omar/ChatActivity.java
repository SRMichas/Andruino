package com.sorezel.andruino_bluetooth.omar;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sorezel.andruino_bluetooth.R;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

/**
 * Created by Omar on 20/12/2017.
 */

public class ChatActivity extends AppCompatActivity {
   TextView state;
   TextView messages;
   Button helloWorld;

    private Bluetooth bluetooth;
    private BluetoothDevice device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //...
        state = findViewById(R.id.activity_chat_status);
        messages = findViewById(R.id.activity_chat_messages);
        helloWorld = findViewById(R.id.activity_chat_hello_world);
        //...

        device = getIntent().getParcelableExtra("device");
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setDeviceCallback(deviceCallback);
        helloWorld.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        bluetooth.connectToDevice(device);
        state.setText("Connecting...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.disconnect();
        bluetooth.onStop();
    }

    private void appendToChat(String msg){
        String text = messages.getText().toString()+"\n"+msg;
        messages.setText(text);
    }


    public void onHelloWorld(View v){
        String msg = "Hello World !";
        bluetooth.send(msg);
        appendToChat("-> "+msg);
    }

    private DeviceCallback deviceCallback = new DeviceCallback() {
        @Override
        public void onDeviceConnected(BluetoothDevice device) {


            helloWorld.setEnabled(true);
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
            state.setText("Device disconnected !");
            helloWorld.setEnabled(false);
        }

        @Override
        public void onMessage(byte[] message) {
            String str = new String(message);
            appendToChat("<- "+str);
        }

        @Override
        public void onError(int errorCode) {

        }

        @Override
        public void onConnectError(final BluetoothDevice device, String message) {
            state.setText("Could not connect, next try in 3 sec...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetooth.connectToDevice(device);
                }
            }, 3000);
        }
    };
}
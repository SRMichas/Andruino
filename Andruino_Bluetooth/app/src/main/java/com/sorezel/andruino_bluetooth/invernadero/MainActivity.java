package com.sorezel.andruino_bluetooth.invernadero;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sorezel.andruino_bluetooth.R;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class MainActivity extends AppCompatActivity {

    private Bluetooth bluetooth;
    private BluetoothDevice device;
    private TextView tv;
    private Button b1,b2,b3,b4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);
        Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv = findViewById(R.id.log);
        device = getIntent().getParcelableExtra("disp");
        arrancaBT();

        b1=findViewById(R.id.btn1);
        b2=findViewById(R.id.btn2);
        b3=findViewById(R.id.btn3);
        b4 = findViewById(R.id.btn4);
        b1.setEnabled(false);
        b4.setEnabled(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = tv.getText().toString()+"LED 1 encendido";
                tv.setText(txt);
                bluetooth.send("E");
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("LED 2 encendido");
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("LED 3 encendido");
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("LED's Apagados");
                bluetooth.send("A");
            }
        });
    }
    private void arrancaBT(){
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setDeviceCallback(callBack);
        bluetooth.onStart();
        bluetooth.connectToDevice(device);
    }
    private DeviceCallback callBack = new DeviceCallback() {
        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            tv.setText("Connected !\n");
            b1.setEnabled(true);
            b4.setEnabled(true);
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {

        }

        @Override
        public void onMessage(byte[] message) {
            String msg = new String(message);
            tv.setText(tv.getText().toString()+"\n"+msg);
        }

        @Override
        public void onError(int errorCode) {

        }

        @Override
        public void onConnectError(final BluetoothDevice device, String message) {
            tv.setText("Could not connect, next try in 3 sec...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetooth.connectToDevice(device);
                }
            }, 3000);
        }
    };
}

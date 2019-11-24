package com.sorezel.andruino_bluetooth.invernadero;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sorezel.andruino_bluetooth.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DeviceCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class StartScreenActivity extends AppCompatActivity {

    Bluetooth bluetooth;
    private ArrayAdapter<String> pairedListAdapter,scanListAdapter;
    private ListView rcvP,rcvS;
    Dialog dialog;
    boolean scanning = false;
    private Handler ayudante;

    private List<BluetoothDevice> pairedDevices;
    private List<BluetoothDevice> scannedDevices;
    dialogo inicio;
    final String mac = "00:18:E4:36:0F:10";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        ayudante = new Handler();
        Button b = findViewById(R.id.str_screen);
        inicio = new dialogo(this);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creaDialogo();
                //arrancaBluetooh();
            }
        });

    }


    private AdapterView.OnItemClickListener onScanListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(scanning){
                bluetooth.stopScanning();
            }
            //setProgressAndState("Pairing...", View.VISIBLE);
            bluetooth.pair(scannedDevices.get(i));
        }
    };
    private AdapterView.OnItemClickListener onPairedListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device  = pairedDevices.get(i);
            if( device.getAddress().equals(mac)){
                Intent in = new Intent(StartScreenActivity.this,InvernaderoActivity.class);
                in.putExtra("disp",device);
                startActivity(in);
            }else{
                Toast.makeText(StartScreenActivity.this,"El dispositivo ["+device.getName()+"] no es el modulo BT de arduino",Toast.LENGTH_SHORT).show();
            }

        }
    };
    private void creaDialogo(){

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_list);

        rcvP = dialog.findViewById(R.id.lstv_paired);
        //rcvS = dialog.findViewById(R.id.lstv_scan);

        pairedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        rcvP.setAdapter(pairedListAdapter);
        rcvP.setOnItemClickListener(onPairedListItemClick);

        scanListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
       // rcvS.setAdapter(scanListAdapter);
        //rcvS.setOnItemClickListener(onScanListItemClick);
        /*Button btn = dialog.findViewById(R.id.btn_scan);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.startScanning();
                //displayScannedDevices();
            }
        });*/
        /*TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

        dialog.show();
        arrancaBluetooh();
    }
    private void arrancaBluetooh(){
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setBluetoothCallback(bluetoothCallback);
        bluetooth.setDiscoveryCallback(discoveryCallback);
        bluetooth.onStart();
        //bluetooth.startScanning();
        if( bluetooth.isEnabled() ){
            displayPairedDevices();
            //displayScannedDevices();
        }else{
            bluetooth.enable();
            displayPairedDevices();
            //displayScannedDevices();
        }
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                ayudante.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(300);
                        }catch (Exception e){}
                        Dialogo.showList(StartScreenActivity.this,bluetooth);
                    }
                });
            }
        }).start();*/
        //inicio.showListDialog(R.layout.dialog_list,true);
    }
    private void displayPairedDevices(){
        pairedDevices = bluetooth.getPairedDevices();
        for(BluetoothDevice device : pairedDevices){
            pairedListAdapter.add(device.getAddress()+" : "+device.getName());
        }
    }
    private void displayScannedDevices(){
        for( BluetoothDevice device: scannedDevices){
            scanListAdapter.add(device.getAddress()+" : "+device.getName());
        }
    }

    private BluetoothCallback bluetoothCallback = new BluetoothCallback() {
        @Override
        public void onBluetoothTurningOn() {
        }

        @Override
        public void onBluetoothOn() {
            //displayPairedDevices();
            //scanButton.setEnabled(true);
        }

        @Override
        public void onBluetoothTurningOff() {
            //scanButton.setEnabled(false);
        }

        @Override
        public void onBluetoothOff() {
        }

        @Override
        public void onUserDeniedActivation() {
            Toast.makeText(StartScreenActivity.this, "I need to activate bluetooth...", Toast.LENGTH_SHORT).show();
        }
    };
    private DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
        @Override
        public void onDiscoveryStarted() {
            //setProgressAndState("Scanning...", View.VISIBLE);
            scannedDevices = new ArrayList<>();
            scanning = true;
        }

        @Override
        public void onDiscoveryFinished() {
            //setProgressAndState("Done.", View.VISIBLE);
            scanning = false;
        }

        @Override
        public void onDeviceFound(BluetoothDevice device) {
            scannedDevices.add(device);
            scanListAdapter.add(device.getAddress()+" : "+device.getName());
            //displayScannedDevices();
        }

        @Override
        public void onDevicePaired(BluetoothDevice device) {
            Toast.makeText(StartScreenActivity.this, "Paired !", Toast.LENGTH_SHORT).show();
            //startChatActivity(device);
        }

        @Override
        public void onDeviceUnpaired(BluetoothDevice device) {

        }

        @Override
        public void onError(int errorCode) {

        }
    };
    private class dialogo{
        Dialog dia;
        Context con;
        public dialogo(Context c){
            con = c;
        }
        public void showListDialog(int layout,boolean cancalable){
            dia = new Dialog(con);
            dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dia.setCancelable(cancalable);
            dia.setContentView(layout);
            if( cancalable ){//lista
                rcvP = dia.findViewById(R.id.lstv_paired);
                pairedListAdapter = new ArrayAdapter<>(StartScreenActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
                rcvP.setAdapter(pairedListAdapter);
                //rcvP.setOnItemClickListener(onPairedListItemClick);
                displayPairedDevices();
            }else{//cargando

            }
            dia.show();
        }

    }
}

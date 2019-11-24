package com.sorezel.andruino_bluetooth.omar;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sorezel.andruino_bluetooth.R;

import java.util.ArrayList;
import java.util.List;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class ScanActivity extends AppCompatActivity {
    ListView pairedDeviceList;
    ListView deviceList;
    TextView state;
    ProgressBar progress;
    Button scanButton;

    private Bluetooth bluetooth;
    private ArrayAdapter<String> scanListAdapter;
    private ArrayAdapter<String> pairedListAdapter;
    private List<BluetoothDevice> pairedDevices;
    private List<BluetoothDevice> scannedDevices;

    private boolean scanning = false, found = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        //...
        pairedDeviceList = findViewById(R.id.activity_scan_paired_list);
        deviceList = findViewById(R.id.activity_scan_list);
        state = findViewById(R.id.activity_scan_state);
        progress = findViewById(R.id.activity_scan_progress);
        scanButton = findViewById(R.id.activity_scan_button);
        //...

        // list for paired devices
        pairedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        pairedDeviceList.setAdapter(pairedListAdapter);
        pairedDeviceList.setOnItemClickListener(onPairedListItemClick);

        // list for scanned devices
        scanListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        deviceList.setAdapter(scanListAdapter);
        deviceList.setOnItemClickListener(onScanListItemClick);

        // bluetooth lib
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setBluetoothCallback(bluetoothCallback);
        bluetooth.setDiscoveryCallback(discoveryCallback);

        // ui...
        setProgressAndState("", View.GONE);
        scanButton.setEnabled(false);
    }

    private void setProgressAndState(String msg, int p){
        state.setText(msg);
        progress.setVisibility(p);
    }

    private void displayPairedDevices(){
        pairedDevices = bluetooth.getPairedDevices();
        for(BluetoothDevice device : pairedDevices){
            pairedListAdapter.add(device.getAddress()+" : "+device.getName());
        }
    }


    public void onScanClick(View v){
        bluetooth.startScanning();
    }

    private AdapterView.OnItemClickListener onPairedListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(scanning){
                bluetooth.stopScanning();
            }
            startChatActivity(pairedDevices.get(i));
        }
    };

    private AdapterView.OnItemClickListener onScanListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(scanning){
                bluetooth.stopScanning();
            }
            setProgressAndState("Pairing...", View.VISIBLE);
            bluetooth.pair(scannedDevices.get(i));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        if(bluetooth.isEnabled()){
            displayPairedDevices();
            scanButton.setEnabled(true);
        } else {
            bluetooth.showEnableDialog(ScanActivity.this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bluetooth.onActivityResult(requestCode, resultCode);
    }

    private BluetoothCallback bluetoothCallback = new BluetoothCallback() {
        @Override
        public void onBluetoothTurningOn() {
        }

        @Override
        public void onBluetoothOn() {
            displayPairedDevices();
            scanButton.setEnabled(true);
        }

        @Override
        public void onBluetoothTurningOff() {
            scanButton.setEnabled(false);
        }

        @Override
        public void onBluetoothOff() {
        }

        @Override
        public void onUserDeniedActivation() {
            Toast.makeText(ScanActivity.this, "I need to activate bluetooth...", Toast.LENGTH_SHORT).show();
        }
    };

    private DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
        @Override
        public void onDiscoveryStarted() {
            setProgressAndState("Scanning...", View.VISIBLE);
            scannedDevices = new ArrayList<>();
            scanning = true;
        }

        @Override
        public void onDiscoveryFinished() {
            if (found)
                setProgressAndState("Done.", View.VISIBLE);
            else
                setProgressAndState("Nothing Found", View.VISIBLE);
            scanning = false;
            found = false;
        }

        @Override
        public void onDeviceFound(BluetoothDevice device) {
            found = true;
            scannedDevices.add(device);
            scanListAdapter.add(device.getAddress()+" : "+device.getName());
        }

        @Override
        public void onDevicePaired(BluetoothDevice device) {
            Toast.makeText(ScanActivity.this, "Paired !", Toast.LENGTH_SHORT).show();
            startChatActivity(device);
        }

        @Override
        public void onDeviceUnpaired(BluetoothDevice device) {

        }

        @Override
        public void onError(int errorCode) {

        }
    };

    private void startChatActivity(BluetoothDevice device){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);
    }
}

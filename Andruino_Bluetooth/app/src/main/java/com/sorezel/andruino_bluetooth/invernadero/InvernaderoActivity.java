package com.sorezel.andruino_bluetooth.invernadero;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.sorezel.andruino_bluetooth.R;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class InvernaderoActivity extends AppCompatActivity implements DeviceCallback{

    private Bluetooth bluetooth;
    private BluetoothDevice device;
    Fragment fragment;
    FragmentManager fm;
    int container = R.id.fragContainer;
    boolean prueba = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invernadero);
        Toolbar tb = findViewById(R.id.inv_tb);
        setSupportActionBar(tb);
        device = getIntent().getParcelableExtra("disp");
        fm = getSupportFragmentManager();
        fragment = new LoadingFragment();
        fm.beginTransaction().add(container,fragment).commit();
        preparaBluetooth();
    }

    private void preparaBluetooth(){
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setDeviceCallback(this);
        bluetooth.onStart();
        bluetooth.connectToDevice(device);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Toast.makeText(this,"Dispsitivo disponible!!!",Toast.LENGTH_SHORT).show();
        fragment = new AppFragment();
        ((AppFragment) fragment).setBluetooth(bluetooth);
        fm.beginTransaction().add(container,fragment).commit();
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) { }

    @Override
    public void onMessage(byte[] message) {
        String str = new String(message),auxHumedad,auxTemperatura;
        int mitad = str.indexOf('!');
        auxHumedad = str.substring(0,mitad);
        auxTemperatura = str.substring(mitad+1,str.length());
        AppFragment appFragment = ((AppFragment)fragment);
        appFragment.displayData(auxHumedad,auxTemperatura);
    }

    @Override
    public void onError(int errorCode) { }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Toast.makeText(this,"Could not connect, next try in 3 sec...",Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetooth.connectToDevice(device);
            }
        }, 2000);
        /*if ( prueba ){
            fragment = new AppFragment();
            fm.beginTransaction().add(container,fragment).commit();
            prueba = false;
        }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.onStop();
    }
}

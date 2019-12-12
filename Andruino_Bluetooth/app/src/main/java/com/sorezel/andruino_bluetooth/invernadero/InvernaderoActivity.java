package com.sorezel.andruino_bluetooth.invernadero;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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
    Toolbar tb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invernadero);
        tb = findViewById(R.id.inv_tb);
        setSupportActionBar(tb);
        device = getIntent().getParcelableExtra("disp");
        fm = getSupportFragmentManager();
        fragment = new LoadingFragment();
        fm.beginTransaction().add(container,fragment).commit();
        preparaBluetooth(); //metodo que instancia el bt
    }

    private void preparaBluetooth(){
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setDeviceCallback(this);
        bluetooth.onStart(); //incia 
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
        fm.beginTransaction().remove(fragment).detach(fragment).commit();
        fragment = new AppFragment();
        ((AppFragment) fragment).setBluetooth(bluetooth);
        fm.beginTransaction().add(container,fragment).commit();
        tb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) { }

    @Override
    public void onMessage(byte[] message) {
        /*
            Obtenemos el mensaje del Arduino como un arreglo de bytes, y lo convetirmos
            en una cadena de caracteres
        */
        String str = new String(message),auxHumedad,auxTemperatura;
        
        AppFragment appFragment = ((AppFragment)fragment);

        if( !str.equals("Sistema Desactivado")){ //verifica que el sistema este activado
            int mitad = str.indexOf('!');   //obtenemos el indice del separador
            auxHumedad = str.substring(0,mitad); //buscamos la subcadena desde su inicio haste el separador
            //obtenemos la subcadena desde un caracter despues del separador hasta el final de la cadena
            auxTemperatura = str.substring(mitad+1,str.length());   

            appFragment.displayData(auxHumedad,auxTemperatura); //enviamos los datos al fragmento
        }else{
            //el sistema esta apagado, procedemos a activar la vista "desactivado".
            appFragment.displayOff(str);
        }

    }

    @Override
    public void onError(int errorCode) { }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Toast.makeText(this,"Error al conectar, intentando en 2 segundos",Toast.LENGTH_SHORT).show();
        //como ocurrio problemas al conectar, reintentamos otra vez despues de 2 seguntos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetooth.connectToDevice(device);
            }
        }, 2000);
        /*if ( prueba ){
            fm.beginTransaction().remove(fragment).detach(fragment).commit();
            fragment = new AppFragment();
            fm.beginTransaction().add(container,fragment).commit();
            tb.setVisibility(View.VISIBLE);
            prueba = false;
        }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.onStop();
    }
}

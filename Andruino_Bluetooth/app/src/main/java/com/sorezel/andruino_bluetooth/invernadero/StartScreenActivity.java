package com.sorezel.andruino_bluetooth.invernadero;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.sorezel.andruino_bluetooth.R;
import java.util.List;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;

public class StartScreenActivity extends AppCompatActivity {
    //Instancias de objetos
    Bluetooth bluetooth;
    Dialog dialog;
    private List<BluetoothDevice> pairedDevices;
    LinearLayout lly;
    //..
    final String mac = "00:18:E4:36:0F:10"; //direccion mac del modulo bt
    boolean cargar = false; //bandera de cargar

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Button b = findViewById(R.id.str_screen);
        lly = findViewById(R.id.layout_start);
        AnimationDrawable ad =  (AnimationDrawable) lly.getBackground();
        ad.setEnterFadeDuration(1500);
        ad.setExitFadeDuration(3000);
        ad.start();
        //oyente del boton
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrancaBluetooh();
                if( cargar )
                    creaDialogo(); //metodo que muestra un cuadro de dialogo
            }
        });
    }
    //oyente que actua cuando se selecciona un item de la lista de dispositivos
    private AdapterView.OnItemClickListener onPairedListItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device  = pairedDevices.get(i);
            if( device.getAddress().equals(mac)){ //verifica la direccion mac del disp con el modulo bt
                //en caso de que cumpla, se prepara la siguiente ventana
                Intent in = new Intent(StartScreenActivity.this,InvernaderoActivity.class);
                in.putExtra("disp",device);
                startActivity(in); //se inicia la nueva ventana
                dialog.dismiss(); //cerramos el cuadro de dialogo
            }else{
                Toast.makeText(StartScreenActivity.this,"El dispositivo ["+device.getName()+"] no es el modulo BT de arduino",Toast.LENGTH_SHORT).show();
            }

        }
    };
    //..

    private void creaDialogo(){
        //inicializacion del dialogo
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_list);
        Window window = dialog.getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.80), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setLayout((int) (size.y * 0.50), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;
        ListView rcvP = dialog.findViewById(R.id.lstv_paired);
        //..
        //se llena una lista con los dispositovos vinculados
        pairedDevices = bluetooth.getPairedDevices();
        DeviceAdapter myAdapter = new DeviceAdapter(this,pairedDevices);
        rcvP.setAdapter(myAdapter);
        rcvP.setOnItemClickListener(onPairedListItemClick);
        //..
        dialog.show(); //muestra el cuadro de dialogo

    }
    private void arrancaBluetooh(){
        //instancia bt
        bluetooth = new Bluetooth(this);
        bluetooth.setCallbackOnUI(this);
        bluetooth.setBluetoothCallback(bluetoothCallback);
        bluetooth.onStart();
        cargar = bluetooth.isEnabled();
        if( !cargar )
            bluetooth.enable();

    }
   /* private void displayPairedDevices(){
        pairedDevices = bluetooth.getPairedDevices();
        for(BluetoothDevice device : pairedDevices){
            pairedListAdapter.add(device.getAddress()+" : "+device.getName());
        }
    }*/
    //objeto que se encarga de las conexiones bt
    private BluetoothCallback bluetoothCallback = new BluetoothCallback() {
        @Override
        public void onBluetoothTurningOn() { }
        @Override
        public void onBluetoothOn() {
            if( !cargar ) {
                creaDialogo();
                cargar = true;
            }
        }
        @Override
        public void onBluetoothTurningOff() { }
        @Override
        public void onBluetoothOff() { }
        @Override
        public void onUserDeniedActivation() {
            Toast.makeText(StartScreenActivity.this, "Activa el bluetooth, por favor", Toast.LENGTH_SHORT).show();
        }
    };
}

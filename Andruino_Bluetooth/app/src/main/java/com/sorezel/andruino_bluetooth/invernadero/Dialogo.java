package com.sorezel.andruino_bluetooth.invernadero;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sorezel.andruino_bluetooth.R;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;

public class Dialogo{

    private  static AlertDialog.Builder dBuilder;
    private static AlertDialog alert;
   static List<BluetoothDevice> pairedDevices = null;
    static View layoutView;
    public static void showMainDialog(Context c){
        dBuilder = new AlertDialog.Builder(c);
        View layoutView = LayoutInflater.from(c).inflate(0, null);
        /*Button dialogButton = layoutView.findViewById(R.id.btnDialog);
        dBuilder.setView(layoutView);
        alert = dBuilder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });*/
    }
    public static void showList(final Context c, Bluetooth bt){

        dBuilder = new AlertDialog.Builder(c);
        layoutView = LayoutInflater.from(c).inflate(R.layout.dialog_list, null);
        ListView rcvP = layoutView.findViewById(R.id.lstv_paired);
        ArrayAdapter<String> pairedListAdapter = new ArrayAdapter<>(c, android.R.layout.simple_list_item_1, new ArrayList<String>());
        rcvP.setAdapter(pairedListAdapter);
        pairedDevices = bt.getPairedDevices();
        for(BluetoothDevice device : pairedDevices){
            pairedListAdapter.add(device.getAddress()+" : "+device.getName());
        }
        rcvP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                layoutView = LayoutInflater.from(c).inflate(R.layout.dialog_loading, null);
                dBuilder.setView(layoutView);
                alert = dBuilder.create();
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(3000);
                        }catch (Exception e){}
                        Intent in = new Intent(c,InvernaderoActivity.class);
                        in.putExtra("disp",pairedDevices.get(i));
                        c.startActivity(in);
                    }
                }).start();*/

            }
        });





        //
        dBuilder.setView(layoutView);
        alert = dBuilder.create();
        alert.show();
    }
}

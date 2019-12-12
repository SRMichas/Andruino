package com.sorezel.andruino_bluetooth.invernadero;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.sorezel.andruino_bluetooth.R;
import java.util.Random;
import me.aflak.bluetooth.Bluetooth;

public class AppFragment extends Fragment {

    private TextView txvT,txvH,txvTMsg,txvHMsg;
    private int val,val2,vueltas,vueltas2;
    private CircularProgressBar cpg,cpg2;
    private View view;
    private Switch swActivarSistema,swVentilacion,swPuerta,swRegar,swAlgo;
    private CardView cardV;
    private Bluetooth bluetooth;
    private boolean flag;
    private void init(){
        txvT = view.findViewById(R.id.txv_temp);
        txvH = view.findViewById(R.id.txv_humedad);
        txvTMsg = view.findViewById(R.id.txv_temp_msg);
        txvHMsg = view.findViewById(R.id.txv_hum_msg);
        swActivarSistema = view.findViewById(R.id.sw_sistema);
        swVentilacion = view.findViewById(R.id.sw_ventilacion);
        swPuerta = view.findViewById(R.id.sw_puerta);
        swRegar = view.findViewById(R.id.sw_regar);
        swAlgo = view.findViewById(R.id.sw_algo);

        try{
            bluetooth.send(swActivarSistema.isChecked()? "1":"0");
        }catch (NullPointerException e){}

        //oyente que enviar el valor dependiendo del estado en que se encuentre
        swActivarSistema.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String val = (b ? "Encendido":"Apagado");
                Toast.makeText(view.getContext(),"Sistema = "+val,Toast.LENGTH_SHORT).show();
                /*if( b )
                    bluetooth.send("1");
                else
                    bluetooth.send("0");*/
                try{
                    //envia 1 cuando es true, 0 cuando es false
                    bluetooth.send(b ?  "1" : "0" );
                    flag = b;
                }catch (NullPointerException e){}

            }
        });
        //oyente que enviar el valor dependiendo del estado en que se encuentre
        swVentilacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(view.getContext(),"Ventilador = "+b,Toast.LENGTH_SHORT).show();
                /*if( b )
                    bluetooth.send("3");
                else
                    bluetooth.send("2");*/
                try{
                    //envia 1 cuando es true, 0 cuando es false
                    bluetooth.send(b ?  "3" : "2" );
                }catch (NullPointerException e){}
            }
        });
        //oyente que enviar el valor dependiendo del estado en que se encuentre
        swPuerta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(view.getContext(),"Puerta = "+b,Toast.LENGTH_SHORT).show();
                /*if( b )
                    bluetooth.send("5");
                else
                    bluetooth.send("4");*/
                try{
                    //envia 1 cuando es true, 0 cuando es false
                    bluetooth.send(b ?  "5" : "4" );
                }catch (NullPointerException e){}
            }
        });
        //oyente que enviar el valor dependiendo del estado en que se encuentre
        swRegar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(view.getContext(),"Regado = "+b,Toast.LENGTH_SHORT).show();
                /*if( b )
                    bluetooth.send("7");
                else
                    bluetooth.send("6");*/
                try{
                    //envia 1 cuando es true, 0 cuando es false
                    bluetooth.send(b ?  "7" : "6" );
                }catch (NullPointerException e){}
            }
        });

        cpg = view.findViewById(R.id.prog1);
        cpg2 = view.findViewById(R.id.prog2);
        cardV = view.findViewById(R.id.app_card);

        //muevele();
        LinearLayout lyout = view.findViewById(R.id.layout_frag);
        lyout.setBackgroundColor(Color.TRANSPARENT);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app,container,false);
        init();
        setHasOptionsMenu(true);
        return view;
    }
    public void setBluetooth(Bluetooth bt){
        bluetooth = bt;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void modoManual(boolean respuesta){
        swActivarSistema.setClickable(respuesta);
        swVentilacion.setClickable(respuesta);
        swPuerta.setClickable(respuesta);
        swRegar.setClickable(respuesta);
        swAlgo.setClickable(respuesta);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.id_manual:
                modoManual(true);
                Toast.makeText(view.getContext(),"Modo manual activado",Toast.LENGTH_SHORT).show();
                break;
            /*case R.id.id_log:
                cardV.setVisibility(View.VISIBLE);
                break;*/
            default:
                muevele();
                char dis = swActivarSistema.isChecked()? '1':'0';
                Toast.makeText(getContext(),""+dis,Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void muevele(){
        final Handler handler = new Handler();
        Random rn = new Random();
        val = rn.nextInt(50);
        val2 = rn.nextInt(100);
        txvT.setText(""+val);
        txvH.setText(""+val2);
        vueltas = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (vueltas < val) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cpg.setProgress((float)vueltas);
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    vueltas++;
                }

            }
        }).start();
        vueltas2 = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (vueltas2 < val2) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cpg2.setProgress((float)vueltas2);
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    vueltas2++;
                }

            }
        }).start();
    }

    public void displayData(String auxHumedad, String auxTemperatura) {
        //dividimos las cadenas por medio del caracter "_", y los guardamos en arreglos de cadenas
        String[] dataH = auxHumedad.split("_"),dataT = auxTemperatura.split("_");
        //limpiamos las cadenas
        auxHumedad = "";
        auxTemperatura = "";
        //insertarmos la primer cadea del arreglo en la vista de temperatura y humedad
        txvH.setText(dataH[0]+" %");
        txvT.setText(dataT[0]+" °C");
        cpg.setProgress(Float.parseFloat(dataT[0]));
        cpg2.setProgress(Float.parseFloat(dataH[0]));

        for (int i = 1; i < dataH.length;i++){ //recorremos el arreglo empezando del segundo elemento
            if( dataH[i].contains("B")) //preguntamos si es el caracter de control del switch
                if(dataH[i].contains("+"))  //pregunta si el caracter esta "encendido"
                    swRegar.setChecked(true);  //encendemos el switch
                else                          //pregutna si el caracter esta "apagado"
                    swRegar.setChecked(false);  //apagamos el switch
            else
                auxHumedad += dataH[i];     //enviamos las demas cadenas a la zona de monitoreo
        }
        for (int i = 1; i < dataT.length;i++){
            if( dataT[i].contains("V") )    //preguntamos si es el caracter de control del switch
                if(dataT[i].contains("+"))  //pregunta si el caracter esta "encendido"
                    swVentilacion.setChecked(true); //encendemos el switch
                else
                    swVentilacion.setChecked(false); //apagamos el switch
            else if( dataT[i].contains("P") )   //preguntamos si es el caracter de control del switch
                if(dataT[i].contains("+"))  //pregunta si el caracter esta "encendido"
                    swPuerta.setChecked(true); //encendemos el switch
                else
                    swPuerta.setChecked(false); //apagamos el switch
            else
                auxTemperatura += dataT[i];     //enviamos las demas cadenas a la zona de monitoreo
        }

        txvHMsg.setText(auxHumedad);
        txvTMsg.setText(auxTemperatura);
    }

    public void displayOff(String str) {
        txvTMsg.setText(str);   //ponemos el mensaje obtenido
        cpg.setProgress(0);     //limpiamos la barra de progreso
        cpg2.setProgress(0);    //limpiamos la barra de progreso
        txvT.setText("0 °C");   //limpiamos la vista especificada
        txvH.setText("0 %");    //limpiamos la vista especificada
        txvHMsg.setText("");    //limpiamos la vista especificada
        swVentilacion.setChecked(false);        //apagamos el switch
        swVentilacion.setClickable(flag);   //evitamos que se le pueda dar click al switch
        swRegar.setChecked(false);      //apagamos el switch
        swRegar.setClickable(flag); //evitamos que se le pueda dar click al switch
        swPuerta.setChecked(false);     //apagamos el switch
        swPuerta.setClickable(flag);    //evitamos que se le pueda dar click al switch
        swAlgo.setChecked(false);       //apagamos el switch
        swAlgo.setClickable(flag);  //evitamos que se le pueda dar click al switch
    }
}

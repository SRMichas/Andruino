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

        bluetooth.send(swActivarSistema.isChecked()? "1":"0");

        swActivarSistema.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(view.getContext(),"Sistema = "+b,Toast.LENGTH_SHORT).show();
                /*if( b )
                    bluetooth.send("1");
                else
                    bluetooth.send("0");*/
                bluetooth.send(b ?  "1" : "0" );
            }
        });
        swVentilacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(view.getContext(),"Ventilador = "+b,Toast.LENGTH_SHORT).show();
                /*if( b )
                    bluetooth.send("3");
                else
                    bluetooth.send("2");*/
                bluetooth.send(b ?  "3" : "2" );
            }
        });
        swPuerta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(view.getContext(),"Puerta = "+b,Toast.LENGTH_SHORT).show();
                /*if( b )
                    bluetooth.send("5");
                else
                    bluetooth.send("4");*/
                bluetooth.send(b ?  "5" : "4" );
            }
        });
        swRegar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(view.getContext(),"Sistema = "+b,Toast.LENGTH_SHORT).show();
                /*if( b )
                    bluetooth.send("7");
                else
                    bluetooth.send("6");*/
                bluetooth.send(b ?  "7" : "6" );
            }
        });

        cpg = view.findViewById(R.id.prog1);
        cpg2 = view.findViewById(R.id.prog2);
        //cpg.setProgressMax(100f);
        cpg.setProgressBarColorStart(Color.YELLOW);
        cpg.setProgressBarColorEnd(Color.GREEN);
        cpg.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

        cpg2.setProgressBarColorStart(Color.YELLOW);
        cpg2.setProgressBarColorEnd(Color.GREEN);
        cpg2.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        cardV = view.findViewById(R.id.app_card);

        muevele();
        LinearLayout lyout = view.findViewById(R.id.layout_frag);
        //lyout.setBackgroundColor(Color.TRANSPARENT);
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
                //fm.beginTransaction().add(R.id.fragContainer,mf).commit();
                modoManual(true);
                Toast.makeText(view.getContext(),"Modo manual activado",Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_log:
                //fm.beginTransaction().remove(mf).commit();
                cardV.setVisibility(View.VISIBLE);
                break;
            default:
                //muevele();
                char dis = swActivarSistema.isChecked()? '1':'0';
                Toast.makeText(getContext(),dis,Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void muevele(){
        final Handler handler = new Handler();
        Random rn = new Random();
        val = rn.nextInt(100);
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
                            //txtProgress.setText(pStatus + " %");
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
                            //txtProgress.setText(pStatus + " %");
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
        String[] dataH = auxHumedad.split("_"),dataT = auxTemperatura.split("_");
        auxHumedad = "";
        auxTemperatura = "";

        txvH.setText(dataH[0]+" %");
        txvT.setText(dataT[0]+" °C");
        cpg.setProgress(Float.parseFloat(dataT[0]));
        cpg2.setProgress(Float.parseFloat(dataH[0]));

        for (int i = 1; i < dataH.length;i++)
            auxHumedad += dataH[i];
        for (int i = 1; i < dataT.length;i++)
            auxTemperatura += dataT[i];

        txvHMsg.setText(auxHumedad);
        txvTMsg.setText(auxTemperatura);
    }
}

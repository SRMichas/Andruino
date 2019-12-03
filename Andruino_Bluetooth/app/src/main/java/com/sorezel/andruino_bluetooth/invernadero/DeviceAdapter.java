package com.sorezel.andruino_bluetooth.invernadero;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sorezel.andruino_bluetooth.R;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {
    private final List<BluetoothDevice> devices;
    private final LayoutInflater inflater;


    public DeviceAdapter(Context ctxt,List<BluetoothDevice> devs) {
        inflater = LayoutInflater.from(ctxt); devices = devs;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int i) {
        return devices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (view == null)
        {
            view = inflater.inflate(R.layout.adapter_device, null);
            //convertViewCounter++;
            holder = new ViewHolder();
            holder.txvName = view.findViewById(R.id.txv_dev_Nombre);
            holder.txvAddress = view.findViewById(R.id.txv_dev_MAC);
            view.setTag(holder);

        }else
            holder = (ViewHolder) view.getTag();

        BluetoothDevice device = devices.get(i);
        holder.txvName.setText(device.getName());
        holder.txvAddress.setText(device.getAddress());

        return view;
    }
    static class ViewHolder
    {
        TextView txvName;
        TextView txvAddress;
    }
}

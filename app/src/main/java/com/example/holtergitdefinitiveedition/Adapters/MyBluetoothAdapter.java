package com.example.holtergitdefinitiveedition.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.holtergitdefinitiveedition.Activitys.BluetoothActivity;
import com.example.holtergitdefinitiveedition.R;

import java.util.ArrayList;
import java.util.logging.Handler;

public class MyBluetoothAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> deviceList;
    private Context context;
    private short rssi;

    public MyBluetoothAdapter(Context context,
                              ArrayList<BluetoothDevice> deviceList, short rssi) {
        this.context = context;
        this.deviceList = deviceList;
        this.rssi = rssi;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_device, null);
        TextView tv_address = view.findViewById(R.id.tv_address);
        TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_state = view.findViewById(R.id.tv_state);
        TextView tv_signal = view.findViewById(R.id.tv_signal);
        tv_address.setText(deviceList.get(position).getAddress());
        tv_name.setText(deviceList.get(position).getName());
        int bondState = deviceList.get(position).getBondState();
        if (rssi > -50 && rssi < 0 || rssi == 0) {
            tv_signal.setText("流畅");
            tv_signal.setTextColor(context.getResources().getColor(R.color.strong));
        } else if (rssi > -70 && rssi < -50) {
            tv_signal.setText("一般");
            tv_signal.setTextColor(context.getResources().getColor(R.color.medium));
        } else {
            tv_signal.setText("较差");
            tv_signal.setTextColor(context.getResources().getColor(R.color.weak));
        }
        switch (bondState) {
            case BluetoothDevice.BOND_NONE:
                tv_state.setText("未配对");
                tv_state.setTextColor(Color.BLACK);
                break;
            case BluetoothDevice.BOND_BONDING:
                tv_state.setText("正在配对");
                tv_state.setTextColor(Color.RED);
                break;
            case BluetoothDevice.BOND_BONDED:
                tv_state.setText("已配对");
                tv_state.setTextColor(Color.BLUE);
                break;
            default:
                break;
        }

        return view;
    }

}

package com.example.holtergitdefinitiveedition;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holtergitdefinitiveedition.Activitys.AddUserActivity;
import com.example.holtergitdefinitiveedition.Activitys.BluetoothActivity;
import com.example.holtergitdefinitiveedition.Activitys.SettingActivity;
import com.example.holtergitdefinitiveedition.Activitys.UserListActivity;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test";
    private static final int REQUEST_NAME = 100;
    private TextView tv_choose;
    private BluetoothDevice remoteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initView();
        getConnectBt();
    }

    private void initView() {
        tv_choose = findViewById(R.id.tv_choose);
    }

    /**
     * 用户列表
     *
     * @param v
     */
    public void user(View v) {
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra("remoteDevice", remoteDevice);
        startActivity(intent);
    }

    /**
     * 添加用户
     *
     * @param v
     */
    public void add(View v) {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
    }

    /**
     * 蓝牙连接
     *
     * @param v
     */
    public void connect(View v) {
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivityForResult(intent, REQUEST_NAME);
    }

    /**
     * 设置页面
     *
     * @param v
     */
    public void setting(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            if (requestCode == REQUEST_NAME) {
                String device_name = data.getStringExtra("device_name");
                remoteDevice = data.getParcelableExtra("remoteDevice");
                if (device_name != null && !device_name.equals("")) {
                    tv_choose.setText(device_name);
                }
            }
        }
    }

    /**
     * 检查已连接的蓝牙设备
     */
    private void getConnectBt() {
        BluetoothAdapter _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (_bluetoothAdapter != null) {
            Set<BluetoothDevice> bondedDevices = _bluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                for (BluetoothDevice bluetoothDevice : bondedDevices) {
                    remoteDevice = bluetoothDevice;
                    tv_choose.setText(bluetoothDevice.getName());
                }
            }
        } else {
            Log.e(TAG, "_bluetoothAdapter……是空的");
        }
    }

}

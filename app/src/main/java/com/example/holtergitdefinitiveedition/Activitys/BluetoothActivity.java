package com.example.holtergitdefinitiveedition.Activitys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.holtergitdefinitiveedition.Adapters.MyBluetoothAdapter;
import com.example.holtergitdefinitiveedition.MainActivity;
import com.example.holtergitdefinitiveedition.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "test";
    public static final int DEVICENAME = 1;
    private int click_position = 0;
    private ListView lv_bluetooths;
    private BluetoothAdapter defaultAdapter;
    private BroadcastReceiver receiver;
    private BluetoothDevice bluetoothDevice;
    private BluetoothDevice remoteDevice;
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private MyBluetoothAdapter myBluetoothAdapter;
    private short rssi = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DEVICENAME) {
                Intent intent = new Intent(BluetoothActivity.this, MainActivity.class);
                intent.putExtra("remoteDevice", remoteDevice);
                intent.putExtra("device_name", device_name);
                setResult(101, intent);
                finish();
            }
        }
    };
    private String device_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_bluetooth);
        initView();
        initBluetooth();
    }

    private void initView() {
        lv_bluetooths = findViewById(R.id.lv_bluetooths);
    }

    private void initBluetooth() {

        // 获取蓝牙适配对象
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        // 打开蓝牙设备
        if (defaultAdapter != null) {
            defaultAdapter.enable();
        } else {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
        //注册广播
        registBluetoothReceiver();

        // 搜寻蓝牙设备 已配对和没有配对的设备
        searchBondedDevices();
        // 搜索未配对设备
        searchUnBondedDevices();
        // 展示设备
        setAdapter(rssi);

        lv_bluetooths.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                click_position = position;
                device_name = deviceList.get(position).getName();
                pairing();
            }
        });
    }

    private void pairing() {
        try {
            //获取点击的条目
            BluetoothDevice bluetoothDevice = deviceList.get(click_position);
            //未配对，点击进行配对
            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                String address = bluetoothDevice.getAddress();
                // 获取远程设备
                if (defaultAdapter != null) {
                    remoteDevice = defaultAdapter
                            .getRemoteDevice(address);
                }
                // 配对操作
                // 先获取字节码文件对象
                Class<BluetoothDevice> clz = BluetoothDevice.class;
                // 获取方法，这个方法是隐藏的，调不到，随时用反射
                Method method = clz.getMethod("createBond", new Class[0]);
                // 执行配对该方法
                method.invoke(remoteDevice, new Object[]{});
            }
        } catch (Exception e) {
            Log.d("Exception is :", e.toString());
        }
    }

    /**
     * 注册蓝牙广播
     */
    private void registBluetoothReceiver() {
        // 定义一个广播接收器
        receiver = new MyBluetoothReceiver();
        // 创建一个意图过滤器
        IntentFilter filter = new IntentFilter();
        // 注册一个搜素到蓝牙的一个意图action
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        // 添加一个action 监听配对状态改变的一个事件
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }

    /**
     * 返回
     *
     * @param v
     */
    public void back_bluetooth(View v) {
        finish();
    }

    /**
     * 搜寻已经配对的设备
     */
    private void searchBondedDevices() {
        if (defaultAdapter != null) {
            Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                if (!deviceList.contains(bluetoothDevice))
                    deviceList.add(bluetoothDevice);
            }
        }
    }

    /**
     * 搜索未配对设备
     */
    private void searchUnBondedDevices() {
        new Thread() {
            public void run() {
                // 如果当前正在搜素，先停止，开始本次搜索
                if (defaultAdapter != null) {
                    if (defaultAdapter.isDiscovering()) {
                        defaultAdapter.cancelDiscovery();
                    }
                    // 开始搜索，就可以搜索到未配对的设备
                    defaultAdapter.startDiscovery();
                }
            }
        }.start();
    }

    // 接收所注册过的action的消息
    class MyBluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取事件类型
            String action = intent.getAction();
            // 获取蓝牙设备
            bluetoothDevice = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                // 添加到一开始定义的集合中
                if (!deviceList.contains(bluetoothDevice)) {
                    deviceList.add(bluetoothDevice);
                }
                // 刷新数据适配器
                setAdapter(rssi);
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int bondState = bluetoothDevice.getBondState();
                switch (bondState) {
                    case BluetoothDevice.BOND_NONE:
                        Toast.makeText(BluetoothActivity.this, "配对失败", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Toast.makeText(BluetoothActivity.this, "正在配对", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Toast.makeText(BluetoothActivity.this, "配对成功", Toast.LENGTH_SHORT).show();
                        // 在集合中移除
                        deviceList.remove(bluetoothDevice);
                        // 将这个条目重新添加到集合中
                        deviceList.add(0, bluetoothDevice);
                        //信号强度
                        rssi = intent.getExtras().getShort(
                                BluetoothDevice.EXTRA_RSSI);
                        // 设置数据适配器
                        setAdapter(rssi);
                        handler.sendEmptyMessageDelayed(DEVICENAME, 1500);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 设置数据适配器
     */
    private void setAdapter(short rssi) {
        // 如果适配器为空，创建，设置适配器
        if (myBluetoothAdapter == null) {
            myBluetoothAdapter = new MyBluetoothAdapter(this, deviceList, rssi);
            lv_bluetooths.setAdapter(myBluetoothAdapter);
        } else {
            // 刷新适配器
            myBluetoothAdapter.notifyDataSetChanged();
        }
    }

}

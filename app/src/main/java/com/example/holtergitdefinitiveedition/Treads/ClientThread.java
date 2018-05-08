package com.example.holtergitdefinitiveedition.Treads;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.holtergitdefinitiveedition.Activitys.ShowActivity;
import com.example.holtergitdefinitiveedition.Common.OnlineFirFilter;
import com.example.holtergitdefinitiveedition.Utils.FileUtil;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class ClientThread extends Thread {
    private BluetoothDevice bluetoothDevice;
    private Handler handler;
    private Context context;
    private static final String TAG = "test";
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    // 通过一个规定好的串号，生成一个UUID号
    private static final UUID MY_UUID = UUID.fromString(SPP_UUID);
    public static String CACHEFILEPATH = ".mnt/sdcard/shijian.txt";
    /**
     * 设定一个标记值，来标记客户端连接是否成功
     */
    public static boolean flag = false;

    //状态，用于解析蓝牙接收到的数据
    int state = 0;
    byte[] byte_2 = new byte[2];
    byte[] byte_26 = new byte[26];
    byte[] byte_24 = new byte[24];
    int num = 0;
    int n = 0;
    int index = 0;
    //用于缓存100次采样数据
    byte[] byte_data_1 = new byte[3];
    byte[] byte_data_2 = new byte[3];
    byte[] byte_data_3 = new byte[3];
    byte[] byte_data_4 = new byte[3];
    byte[] byte_data_5 = new byte[3];
    byte[] byte_data_6 = new byte[3];
    byte[] byte_data_7 = new byte[3];
    byte[] byte_data_8 = new byte[3];
    double[] doubles_12;
    double[] doubles12_all = new double[1280 * 12];

    private boolean isReceiving;
    private InputStream inputStream;
    private double[] sb_doubles;
    private OnlineFirFilter fir_1;
    private OnlineFirFilter fir_2;
    private OnlineFirFilter fir_3;
    private OnlineFirFilter fir_4;
    private OnlineFirFilter fir_5;
    private OnlineFirFilter fir_6;
    private OnlineFirFilter fir_7;
    private OnlineFirFilter fir_8;
    private double mm1;
    private double mm2;
    private double mm3;
    private double mm4;
    private double mm5;
    private double mm6;
    private double mm7;
    private double mm8;
    private int m;
    private double mm9;
    private double mm10;
    private double mm11;
    private double mm12;
    private OutputStreamWriter outStream;
    private FileOutputStream fileOut;

    /**
     * 在创建这个客户端的线程的时候，需要将要连接的bluetoothDevice传递过来
     *
     * @param bluetoothDevice
     * @param handler
     */
    public ClientThread(BluetoothDevice bluetoothDevice, Handler handler, Context context) {
        this.handler = handler;
        this.bluetoothDevice = bluetoothDevice;
        this.context = context;
        //接收数据线程循环
        isReceiving = true;

        sb_doubles = OnlineFirFilter.BandPass(250, 1, 26, 25);
        fir_1 = new OnlineFirFilter(sb_doubles);
        fir_2 = new OnlineFirFilter(sb_doubles);
        fir_3 = new OnlineFirFilter(sb_doubles);
        fir_4 = new OnlineFirFilter(sb_doubles);
        fir_5 = new OnlineFirFilter(sb_doubles);
        fir_6 = new OnlineFirFilter(sb_doubles);
        fir_7 = new OnlineFirFilter(sb_doubles);
        fir_8 = new OnlineFirFilter(sb_doubles);

    }

    public ClientThread() {
        //接收数据线程循环
        isReceiving = true;
    }

    public void end() {

        try {

            //结束循环，结束线程
            isReceiving = false;

            //关闭流
            if (inputStream != null) {
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();
        try {

            // 获取客户端的socket
            final BluetoothSocket socket = bluetoothDevice
                    .createRfcommSocketToServiceRecord(MY_UUID);
            //连接服务器------要么成功，要么异常
            socket.connect();
            // 如果没有抛出异常，代表连接成功，进行一个标志位
            flag = true;
            inputStream = socket.getInputStream();

            /**
             * holter
             */
            while (isReceiving) {

                switch (state) {

                    case 0:

                        //读取第一个字节
                        num = inputStream.read(byte_2);
                        //判断包头，如果为A5即为数据包头
                        if (byte_2[1] == (byte) 0xA5) {
                            state = 1;
                        } else {
                            state = 0;
                        }

                        break;
                    case 1:

                        //读取包头之后的26个字节
                        num = inputStream.read(byte_26);
                        //判断最后两个字节是否为数据包尾AA
                        if (byte_26[25] == (byte) 0xAA) {

                            System.arraycopy(byte_26, 0, byte_24, 0, 24);

                            System.arraycopy(byte_24, 0, byte_data_1, 0, 3);
                            System.arraycopy(byte_24, 3, byte_data_2, 0, 3);
                            System.arraycopy(byte_24, 6, byte_data_3, 0, 3);
                            System.arraycopy(byte_24, 9, byte_data_4, 0, 3);
                            System.arraycopy(byte_24, 12, byte_data_5, 0, 3);
                            System.arraycopy(byte_24, 15, byte_data_6, 0, 3);
                            System.arraycopy(byte_24, 18, byte_data_7, 0, 3);
                            System.arraycopy(byte_24, 21, byte_data_8, 0, 3);

                            mm1 = frombytes(byte_data_1);//8
                            mm2 = frombytes(byte_data_2);//7
                            mm3 = frombytes(byte_data_3);//6
                            mm4 = frombytes(byte_data_4);//5
                            mm5 = frombytes(byte_data_5);//4
                            mm6 = frombytes(byte_data_6);//1
                            mm7 = frombytes(byte_data_7);//2
                            mm8 = frombytes(byte_data_8);//3

                            mm9 = mm8 - mm7;
                            mm10 = -(mm7 + mm8) / 2;
                            mm11 = mm7 - mm8 / 2;
                            mm12 = mm8 - mm7 / 2;

                            doubles_12 = new double[]{fir_1.ProcessSample(mm8), fir_2.ProcessSample(mm7), fir_3.ProcessSample(mm6), fir_4.ProcessSample(mm5), fir_5.ProcessSample(mm4), fir_6.ProcessSample(mm1), fir_7.ProcessSample(mm2), fir_8.ProcessSample(mm3), mm9, mm10, mm11, mm12};
                            System.arraycopy(doubles_12, 0, doubles12_all, index * 12, 12);

                            index++;

                            if (index == 1280) {

                                final Message msg = new Message();
                                Bundle data = new Bundle();
                                data.putDoubleArray("doubles12_all", doubles12_all);
                                msg.what = 1;
                                msg.setData(data);
                                ShowActivity.handler.sendMessage(msg);

                                index = 0;

                            }

                        }
                        state = 0;

                        break;

                    default:
                        break;

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            //如果抛出异常，进行一下标志位的置位
            flag = false;
            Log.i(TAG, "客户端连接失败");
        }
    }

    private void writeToFile(double[] doubles12_all) {
        try {
            File newFile = new File(CACHEFILEPATH);
            if (!newFile.exists()) {
                newFile.mkdirs();
            }
            JSONArray numbers = new JSONArray();
            for (double number : doubles12_all) {
                numbers.put(number);
            }
            String jsonString = numbers.toString();
            fileOut = null;
            outStream = null;
            fileOut = new FileOutputStream(newFile, false);
            outStream = new OutputStreamWriter(fileOut);
            outStream.write(jsonString);
        } catch (Exception e) {
        } finally {
            try {
                if (null != outStream)
                    outStream.close();
                if (null != fileOut)
                    fileOut.close();
            } catch (Exception e) {
            }
        }
    }

    private double frombytes(byte[] byte_data_1) {
        double mm = 0;
        int int1 = byte_data_1[0] & 0xFF;
        int int2 = byte_data_1[1] & 0xFF;
        int int3 = byte_data_1[2] & 0xFF;
        if (int1 > 127) {
            m = (((int1 << 24) | (int2 << 16) | (int3 << 8) + 1));
            mm = (m * 0.00028610233) / 256;
        } else {
            m = (((int1 << 16) | (int2 << 8) | int3));
            mm = (m * 0.00028610233);
        }
        return mm;
    }

}

package com.example.holtergitdefinitiveedition.Common;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

import com.example.holtergitdefinitiveedition.Common.db.DaoMaster;
import com.example.holtergitdefinitiveedition.Common.db.DaoSession;
import com.example.holtergitdefinitiveedition.Treads.ClientThread;

/**
 * 如果DaoMaster等无法正常导入，只需run一下即可。
 */
public class MyApp extends Application {
    public static final String DB_NAME = "user.db";//数据库名称

    private static DaoSession daoSession;
    private static ClientThread clientThread;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDb());
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static void openThread(BluetoothDevice bluetoothDevice, Handler handler, Context context) {
        clientThread = new ClientThread(bluetoothDevice, handler, context);
        clientThread.start();
    }

    public static void closeThread() {
        clientThread.end();
        clientThread.interrupt();
    }

}

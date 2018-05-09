package com.example.holtergitdefinitiveedition.Activitys;

import android.bluetooth.BluetoothDevice;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.holtergitdefinitiveedition.Common.MyApp;
import com.example.holtergitdefinitiveedition.R;
import com.example.holtergitdefinitiveedition.Utils.FileUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowActivity extends AppCompatActivity {

    private static final String TAG = "test";
    private SurfaceView sfv_bottom;

    //SurfaceView用来显示心电波形
    private static SurfaceView show;

    private SurfaceHolder holder;

    private static SurfaceHolder holder_show;

    //Paint类,用来绘制图形
    private Paint paint;

    //屏宽
    private static int screen_width;

    //屏高
    private static int screen_height;

    //通道数量
    private final static int channel_count = 12;

    //显示用户名
    private TextView showUserName;

    //显示导联标号
    private TextView[] channel_label;

    //导联标号显示TextView的ID
    private int[] channel_label_id;

    //显示心率
    private TextView hr_tv;

    //显示心跳图案
    private ImageView hr_im;

    private static Canvas canvas_show;
    private static Paint mPaint;

    //心跳波形基线y坐标
    static int[] y_axis_baseline = new int[channel_count];
    // 每段波形开始的点
    public static int e_startX = 0;

    public static int y_range = 10;
    private static int the_width;
    private static int the_height;
    private static double[] int_arr;
    private static final int LOCALTIME = 3;
    private static TextView local_time;
    private TimerThread timerThread;
    private static String data_date;
    private static String data_string;
    private double[] datas_double;
    private boolean isLocal;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_show);

        int show_ecg = getIntent().getIntExtra("show", 0);
        BluetoothDevice remoteDevice = getIntent().getParcelableExtra("remoteDevice");
        if (show_ecg != 0) {
            MyApp.openThread(remoteDevice, handler, ShowActivity.this);
        }

        datas_double = getIntent().getDoubleArrayExtra("datas_double");
        isLocal = getIntent().getBooleanExtra("isLocal", false);
        if (datas_double.length != 0) {
            int l_w = getIntent().getIntExtra("screen_width", 0);
            int l_h = getIntent().getIntExtra("screen_height", 0);
            if (l_w != 0 && l_h != 0) {
                the_width = l_w;
                the_height = l_h;
            }
            Message msg = new Message();
            Bundle data1 = new Bundle();
            data1.putDoubleArray("doubles12_all", datas_double);
            msg.what = 1;
            msg.setData(data1);
            handler.sendMessageDelayed(msg, 1000);
        }

        timerThread = new TimerThread();
        timerThread.start();

        String uname = getIntent().getStringExtra("user_name");
        showUserName = findViewById(R.id.showUserNameAndNumber);
        if (uname != null && !uname.equals("")) {
            showUserName.setText("用户：" + uname);
        } else {
            showUserName.setText("用户");
        }
        local_time = findViewById(R.id.showNowTime);

        //屏幕长亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //画笔属性
        mPaint = new Paint();

        //宽度
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Style.STROKE);

        //颜色
        mPaint.setColor(Color.GREEN);

        //获取屏幕尺寸
        WindowManager wm = getWindowManager();
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_height = wm.getDefaultDisplay().getHeight();

        //获取心电波形基线y坐标
        for (int i = 0; i < y_axis_baseline.length; i++) {
            y_axis_baseline[i] = screen_height * (2 * i + 3)
                    / (2 * channel_count + 4);
        }

        sfv_bottom = findViewById(R.id.sfv_bottom);
        show = findViewById(R.id.show);
        show.setClickable(false);
        show.setZOrderOnTop(true);

        show.post(new Runnable() {
            @Override
            public void run() {
                screen_width = show.getWidth();
                screen_height = show.getHeight();
                handler.obtainMessage(2, screen_width, screen_height).sendToTarget();
            }
        });

        paint = new Paint();
        holder = sfv_bottom.getHolder();
        holder.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }

            //SurfaceView创建时调用
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();

                //画笔效果
                PathEffect effect = new PathEffect();

                //绿色
                paint.setColor(Color.GREEN);

                //宽度
                paint.setStrokeWidth(1);

                //实线
                paint.setStyle(Style.STROKE);
                effect = new DashPathEffect(new float[]{0, 0}, 0);
                paint.setPathEffect(effect);

                //画线
                canvas.drawLine(0, 0, screen_width, 0, paint);
                canvas.drawLine(0, screen_height, screen_width, screen_height,
                        paint);

                //虚线
                paint.setStrokeWidth(1);
                effect = new DashPathEffect(new float[]{5, 10}, 0);
                paint.setPathEffect(effect);
                //画心电波形基准线
                for (int i = 0; i < channel_count; i++) {
                    canvas.drawLine(0, screen_height * (2 * i + 3)
                                    / (2 * channel_count + 4), screen_width,
                            screen_height * (2 * i + 3)
                                    / (2 * channel_count + 4), paint);
                }
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {

            }
        });

        holder_show = show.getHolder();
        holder_show.setFormat(PixelFormat.TRANSPARENT);

        //导联标号和y坐标
        channel_label_id = new int[]{R.id.label1, R.id.label2, R.id.label3,
                R.id.label4, R.id.label5, R.id.label6, R.id.label7,
                R.id.label8, R.id.label9, R.id.label10, R.id.label11,
                R.id.label12};
        channel_label = new TextView[channel_count];
        for (int i = 0; i < channel_label.length; i++) {
            channel_label[i] = findViewById(channel_label_id[i]);
            channel_label[i].setY((2 * i + 3) * screen_height
                    / (2 * channel_count + 4));
        }

        hr_im = findViewById(R.id.hr_im);
        LayoutParams para = (LayoutParams) hr_im.getLayoutParams();
        para.height = screen_height / (4 + channel_count * 2);
        para.width = screen_height / (4 + channel_count * 2);
        hr_im.setLayoutParams(para);
        hr_tv = findViewById(R.id.hr_tv);
        Animation animation = AnimationUtils.loadAnimation(ShowActivity.this,
                R.anim.hr_anim);
        hr_im.startAnimation(animation);

//        if (datas_double.length != 0) {
//            drawEEGWave(e_startX, datas_double);
//        }

    }

    public static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle b = msg.getData();
                    int_arr = b.getDoubleArray("doubles12_all");
                    data_string = datatoString(int_arr);
                    drawEEGWave(e_startX, int_arr);
                    break;
                case 2:
                    the_width = msg.arg1;
                    the_height = msg.arg2;
                    break;
                case LOCALTIME:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
                    data_date = format.format(date);
                    local_time.setText(data_date);
                    break;
                default:
                    break;
            }
        }

    };

    //画心电波形
    public static void drawEEGWave(int startX, double[] data) {
        canvas_show = holder_show.lockCanvas(new Rect(0, 0, the_width, the_height));
        canvas_show.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);

        for (int i = 1; i < the_width; i++) {
            for (int j = 0; j < channel_count; j++) {
                canvas_show.drawLine(i - 1, (float) (y_axis_baseline[j] - data[(i - 1) * channel_count + j] * (400 / y_range)), i, (float) (y_axis_baseline[j] - data[i * channel_count + j] * (400 / y_range)), mPaint);
            }
        }

        holder_show.unlockCanvasAndPost(canvas_show);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class TimerThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {

                try {
                    sleep(1000);
                    Message msg = new Message();
                    msg.what = LOCALTIME;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } while (true);
        }
    }

    private static String datatoString(double[] int_arr) {
        String data = null;
        StringBuffer stringBuffer = new StringBuffer();
        for (double d : int_arr) {
            data = String.valueOf(d);
            stringBuffer.append(data);
            stringBuffer.append(",");
        }
        return stringBuffer.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isLocal) {
            if (data_date != null && !data_date.equals("")) {
                FileUtil.setFileName(data_date);
            }
            if (data_string != null && !data_string.equals("")) {
                Log.i(TAG, data_string.length() + "");
                FileUtil.writeData(data_string);
            }
        }

        timerThread.interrupt();
        MyApp.closeThread();
    }
}

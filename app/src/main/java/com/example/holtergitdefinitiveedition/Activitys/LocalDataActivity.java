package com.example.holtergitdefinitiveedition.Activitys;

import android.content.Intent;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.holtergitdefinitiveedition.Adapters.LocalDataAdapter;
import com.example.holtergitdefinitiveedition.R;
import com.example.holtergitdefinitiveedition.Utils.FileUtil;
import com.example.holtergitdefinitiveedition.dao.LocalData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalDataActivity extends AppCompatActivity {

    private static final String TAG = "test";
    private ListView lv_localdata;
    private List<LocalData> datalist;
    private LocalDataAdapter localDataAdapter;
    private int screen_width;
    private int screen_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_local_data);
        initView();
        initData();
    }

    private void initView() {
        lv_localdata = findViewById(R.id.lv_localdata);
    }

    private void initData() {

        WindowManager wm = getWindowManager();
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_height = wm.getDefaultDisplay().getHeight();

        datalist = getimages();
        if (datalist != null) {
            localDataAdapter = new LocalDataAdapter(datalist, this);
            lv_localdata.setAdapter(localDataAdapter);
        }
        lv_localdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String data_path = datalist.get(i).getDpath();
                String data = FileUtil.readData(data_path);
                String[] datas_str = data.split(",");
                double[] datas_double = new double[datas_str.length];
                for (int j = 0; j < datas_str.length; j++) {
                    datas_double[j] = Double.valueOf(datas_str[j]);
                }
                Intent intent = new Intent(LocalDataActivity.this, ShowActivity.class);
                intent.putExtra("datas_double", datas_double);
                intent.putExtra("screen_width", screen_width);
                intent.putExtra("screen_height", screen_height);
                intent.putExtra("isLocal", true);
                startActivity(intent);
            }
        });
    }

    //找到所有数据（实则为数据所在地址） 并存入集合中
    public ArrayList<LocalData> getimages() {
        //获得外部存储的根目录
        File dir = new File(Environment.getExternalStorageDirectory() + "/Holter_Data");
        ArrayList<LocalData> images = new ArrayList<LocalData>();
        //调用遍历所有文件的方法
        recursionFile(dir, images);
        //返回文件路径集合
        return images;
    }

    //遍历手机所有文件 并将路径名存入集合中 参数需要 路径和集合
    public void recursionFile(File dir, List<LocalData> datas) {
        //得到某个文件夹下所有的文件
        File[] files = dir.listFiles();
        //文件为空
        if (files == null) {
            return;
        }
        //遍历当前文件下的所有文件
        for (File file : files) {
            //如果是文件夹
            if (file.isDirectory()) {
                //则递归(方法自己调用自己)继续遍历该文件夹
                recursionFile(file, datas);
            } else { //如果不是文件夹 则是文件
                //如果文件名以 .txt结尾则是数据文件
                if (file.getName().endsWith(".txt")) {
                    //往数据集合中 添加数据路径
                    LocalData localData = new LocalData();
                    localData.setDname(file.getName());
                    localData.setDpath(file.getAbsolutePath());
                    datas.add(localData);
                }
            }
        }
    }

}

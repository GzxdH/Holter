package com.example.holtergitdefinitiveedition.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.holtergitdefinitiveedition.Adapters.UserAdapter;
import com.example.holtergitdefinitiveedition.Common.MyApp;
import com.example.holtergitdefinitiveedition.Common.db.DaoSession;
import com.example.holtergitdefinitiveedition.Common.db.UserDao;
import com.example.holtergitdefinitiveedition.R;
import com.example.holtergitdefinitiveedition.dao.User;
import com.zyyoona7.lib.EasyPopup;

import java.util.Collections;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private ListView lv_users;
    private UserDao userDao;
    private List<User> userList;
    private UserAdapter userAdapter;
    private EasyPopup mCirclePop;
    private static final String TAG = "test";
    private static final int REQUEST_UPDATE = 200;
    private int position;
    private String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_user_list);
        initView();
        initDb();
        search_show();
        adapterdo();
    }

    private void initDb() {
        DaoSession daoSession = MyApp.getDaoSession();
        userDao = daoSession.getUserDao();
    }

    private void initView() {
        lv_users = findViewById(R.id.lv_users);

        //是否允许点击PopupWindow之外的地方消失
        mCirclePop = new EasyPopup(this).
                setContentView(R.layout.layout_show_menu)
                .setAnimationStyle(R.style.PopupAnimation)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                //允许背景变暗
                .setBackgroundDimEnable(true)
                //变暗的透明度(0-1)，0为完全透明
                .setDimValue(0.4f)
                .createPopup();
    }

    private void search_show() {
        userList = userDao.queryBuilder().build().list();
        Collections.reverse(userList);
        userAdapter = new UserAdapter(userList, this);
        lv_users.setAdapter(userAdapter);
        lv_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                user_name = userList.get(i).getUserName();
                position = i;
                mCirclePop.showAtLocation(view, Gravity.CENTER | Gravity.CENTER, 0, 0);
            }
        });
    }

    private void adapterdo() {

        mCirclePop.getView(R.id.ll_begincare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "开始监护");
                Intent intent = new Intent(UserListActivity.this, ShowActivity.class);
                intent.putExtra("user_name", user_name);
                intent.putExtra("show", 1);
                intent.putExtra("remoteDevice", getIntent().getParcelableExtra("remoteDevice"));
                startActivity(intent);
                finish();
                mCirclePop.dismiss();
            }
        });
        mCirclePop.getView(R.id.ll_edituser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserListActivity.this, AddUserActivity.class);
                intent.putExtra("isupdate", true);
                intent.putExtra("user", userList.get(position));
                startActivityForResult(intent, REQUEST_UPDATE);
                mCirclePop.dismiss();
            }
        });
        mCirclePop.getView(R.id.ll_deleteuser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDao.deleteByKey(userList.get(position).getUid());
                userList.remove(position);
                userAdapter.notifyDataSetChanged();
                mCirclePop.dismiss();
            }
        });
        mCirclePop.getView(R.id.ll_uploaddata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "上传数据");
                mCirclePop.dismiss();
            }
        });
        mCirclePop.getView(R.id.ll_localdata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserListActivity.this, LocalDataActivity.class);
                startActivity(intent);
                mCirclePop.dismiss();
            }
        });

    }

    /**
     * 返回
     *
     * @param v
     */
    public void back_user(View v) {
        finish();
    }

    /**
     * 添加用户
     *
     * @param v
     */
    public void add_user(View v) {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 201) {
            if (requestCode == REQUEST_UPDATE) {
                if (data != null) {
                    int seccess_upload = data.getIntExtra("seccess_upload", 0);
                    if (seccess_upload != 0) {
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Log.i(TAG, "修改失败");
                    }
                } else {
                    Log.i(TAG, "data为空");
                }
            }
        }
    }
}

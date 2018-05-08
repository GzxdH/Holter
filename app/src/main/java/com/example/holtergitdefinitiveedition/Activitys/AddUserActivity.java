package com.example.holtergitdefinitiveedition.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.holtergitdefinitiveedition.Common.MyApp;
import com.example.holtergitdefinitiveedition.Common.db.DaoSession;
import com.example.holtergitdefinitiveedition.Common.db.UserDao;
import com.example.holtergitdefinitiveedition.R;
import com.example.holtergitdefinitiveedition.dao.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddUserActivity extends AppCompatActivity {

    private TimePickerView pvCustomLunar;
    private TextView tv_choose;
    private EditText et_name;
    private EditText et_num;
    private RadioButton rb_man;
    private RadioButton rb_madam;
    private EditText et_height;
    private EditText et_weight;
    private EditText et_doctor_num;
    private EditText et_diagnosis;
    private UserDao userDao;
    private int user_num;
    private User user;
    private boolean isUpdate;
    private long uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_user);
        initView();
        initLunarPicker();
        initDb();
        initData();
    }

    private void initData() {
        isUpdate = getIntent().getBooleanExtra("isupdate", false);
        user = (User) getIntent().getSerializableExtra("user");
        if (user != null && isUpdate == true) {
            uid = user.getUid();
            et_name.setText(user.getUserName());
            et_num.setText(user.getUserNum() + "");
            tv_choose.setText(user.getUserDate());
            et_height.setText(user.getUserHeight() + "");
            et_weight.setText(user.getUserWeight() + "");
            et_doctor_num.setText(user.getUserDocNum() + "");
            et_diagnosis.setText(user.getUserResult());
            if (user.getUserSex().equals("男")) {
                rb_man.setChecked(true);
            } else {
                rb_madam.setChecked(true);
            }
        }
    }

    private void initDb() {
        DaoSession daoSession = MyApp.getDaoSession();
        userDao = daoSession.getUserDao();
    }

    private void initView() {
        et_name = findViewById(R.id.et_name);
        et_num = findViewById(R.id.et_num);
        rb_man = findViewById(R.id.rb_man);
        rb_madam = findViewById(R.id.rb_madam);
        tv_choose = findViewById(R.id.tv_choose);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        et_doctor_num = findViewById(R.id.et_doctor_num);
        et_diagnosis = findViewById(R.id.et_diagnosis);
    }

    public void back_edit(View v) {
        finish();
    }

    public void save(View v) {
        String user_name = et_name.getText().toString();
        user_num = 0;
        if (et_num.getText().toString() != null && !et_num.getText().toString().equals("")) {
            user_num = Integer.valueOf(et_num.getText().toString());
        }
        String user_date = tv_choose.getText().toString();
        String user_sex = "";
        if (rb_man.isChecked()) {
            user_sex = rb_man.getText().toString();
        } else if (rb_madam.isChecked()) {
            user_sex = rb_madam.getText().toString();
        }
        String user_height = et_height.getText().toString();
        String user_weight = et_weight.getText().toString();
        String user_doctor_num = et_doctor_num.getText().toString();
        String user_diagnosis = et_diagnosis.getText().toString();

        Intent intent = new Intent(AddUserActivity.this, UserListActivity.class);

        if (user_name.isEmpty() || et_num.getText().toString().isEmpty() || user_date.isEmpty() || user_sex.isEmpty() || user_height.isEmpty() || user_weight.isEmpty()
                || user_doctor_num.isEmpty() || user_diagnosis.isEmpty()) {
            Toast.makeText(this, "请务必全部填写",
                    Toast.LENGTH_LONG).show();
        } else {
            if (isUpdate == true) {
                if (uid != -1) {
                    updateInDb(uid, user_name, user_num, user_date, user_sex, user_height,
                            user_weight, user_doctor_num, user_diagnosis);
                    intent.putExtra("seccess_upload", 1);
                    setResult(201, intent);
                    finish();
                }
            } else {
                saveInDb(user_name, user_num, user_date, user_sex, user_height,
                        user_weight, user_doctor_num, user_diagnosis);
                startActivity(intent);
                finish();
            }
        }

        /**
         * 1.保存按钮非空判断 √
         * 2.跳入到list页面 √
         * 3.查找数据库，展示在list上 √
         * 4.条目交互
         */

    }

    public void cancel(View v) {
        finish();
    }

    public void choose_date(View v) {
        pvCustomLunar.show();
    }

    /**
     * 存储到数据库
     * <p>
     * //add，这里的null 代表自增长的id，你还可以为user表插入unique的userid
     * User user1 = new User(null, "ag1", "123456");
     * userDao.insert(user1);
     * <p>
     * //update，这里更新id是3的user的名字，id从1开始的，在where来添加匹配条件
     * User user4 = userDao.queryBuilder().where(UserDao.Properties.Id.eq(3)).build().unique();
     * user4.setUserName("kk");
     * userDao.update(user4);
     * <p>
     * //delete，这里删除id是2的user
     * List<User> userList2 = userDao.queryBuilder().where(UserDao.Properties.Id.eq(2)).build().list();
     * for (User user5 : userList2)
     * userDao.delete(user5);
     * <p>
     * //query，重新user表全部user
     * List<User> userList = userDao.queryBuilder().build().list();
     * for (User user : userList)
     * Log.i(TAG, user.toString());
     *
     * @param user_name
     * @param user_num
     * @param user_date
     * @param user_sex
     * @param user_height
     * @param user_weight
     * @param user_doctor_num
     * @param user_diagnosis
     */
    private void saveInDb(String user_name, int user_num, String user_date, String user_sex, String user_height, String user_weight, String user_doctor_num, String user_diagnosis) {
        User user_insert = new User();
        user_insert.setUid(null);
        user_insert.setUserName(user_name);
        user_insert.setUserNum(user_num);
        user_insert.setUserDate(user_date);
        user_insert.setUserSex(user_sex);
        user_insert.setUserHeight(user_height);
        user_insert.setUserWeight(user_weight);
        user_insert.setUserDocNum(user_doctor_num);
        user_insert.setUserResult(user_diagnosis);
        userDao.insert(user_insert);
    }

    private void updateInDb(long i, String user_name, int user_num, String user_date, String user_sex, String user_height, String user_weight, String user_doctor_num, String user_diagnosis) {
        User user_update = userDao.queryBuilder().where(UserDao.Properties.Uid.eq(i)).build().unique();
        user_update.setUserName(user_name);
        user_update.setUserNum(user_num);
        user_update.setUserDate(user_date);
        user_update.setUserSex(user_sex);
        user_update.setUserHeight(user_height);
        user_update.setUserWeight(user_weight);
        user_update.setUserDocNum(user_doctor_num);
        user_update.setUserResult(user_diagnosis);
        userDao.update(user_update);
    }

    /**
     * 时间选择器
     */
    private void initLunarPicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(selectedDate.get(Calendar.YEAR), 2, 28);
        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                tv_choose.setText(getTime(date));
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.layout_picker, new CustomListener() {

                    @Override
                    public void customLayout(final View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.returnData();
                                pvCustomLunar.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.dismiss();
                            }
                        });

                    }

                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}

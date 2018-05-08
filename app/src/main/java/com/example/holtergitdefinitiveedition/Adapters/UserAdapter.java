package com.example.holtergitdefinitiveedition.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.holtergitdefinitiveedition.R;
import com.example.holtergitdefinitiveedition.dao.User;

import java.util.List;

/**
 * Created by 王子龙 on 2018/4/17.
 */

public class UserAdapter extends BaseAdapter {
    private List<User> userList;
    private Context context;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_userslist, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = convertView.findViewById(R.id.tv_user_name);
            viewHolder.tv_num = convertView.findViewById(R.id.tv_user_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_name.setText(userList.get(position).getUserName());
        viewHolder.tv_num.setText("" + userList.get(position).getUserNum());
        return convertView;
    }

    class ViewHolder {
        public TextView tv_name;
        TextView tv_num;
    }

}

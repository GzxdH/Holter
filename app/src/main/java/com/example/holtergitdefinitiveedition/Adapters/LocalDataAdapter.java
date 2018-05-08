package com.example.holtergitdefinitiveedition.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.holtergitdefinitiveedition.R;
import com.example.holtergitdefinitiveedition.dao.LocalData;
import com.example.holtergitdefinitiveedition.dao.User;

import java.util.List;

/**
 * Created by 王子龙 on 2018/4/17.
 */

public class LocalDataAdapter extends BaseAdapter {
    private List<LocalData> datalist;
    private Context context;

    public LocalDataAdapter(List<LocalData> datalist, Context context) {
        this.datalist = datalist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_localdata, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = convertView.findViewById(R.id.tv_localdata);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_name.setText(datalist.get(position).getDname());
        return convertView;
    }

    class ViewHolder {
        TextView tv_name;
    }

}

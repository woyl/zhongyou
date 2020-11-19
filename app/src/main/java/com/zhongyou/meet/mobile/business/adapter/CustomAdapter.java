package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.Custom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongce
 * create time: 2018/10/25
 */
public class CustomAdapter extends BaseAdapter {

    private List<Custom> customs = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public CustomAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void add(List<Custom> customs) {
        this.customs = customs;
    }

    @Override
    public int getCount() {
        return customs != null ? customs.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return customs != null ? customs.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_custom, null);
            holder.customName = convertView.findViewById(R.id.tv_item_custom_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.customName.setText(customs.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        public TextView customName;
    }
}

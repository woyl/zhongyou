package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.District;

import java.util.List;

/**
 * Created by whatisjava on 18-1-23.
 */

public class DistrictAdapter extends BaseAdapter {

    private List<District> districts;
    private LayoutInflater inflater;
    private Context mContext;

    public DistrictAdapter(Context context, List<District> districts){
        this.mContext = context;
        this.districts = districts;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return districts != null ? districts.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return districts != null ? districts.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_district, null);
            holder.name = view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        District district = districts.get(i);
        holder.name.setText(district.getName());

        return view;
    }


    static class ViewHolder{
        public TextView name;
    }

}

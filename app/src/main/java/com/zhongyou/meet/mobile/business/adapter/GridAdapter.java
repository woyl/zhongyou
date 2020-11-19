package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.Grid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongce
 * create time: 2018/10/25
 */
public class GridAdapter extends BaseAdapter {

    private List<Grid> grids = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public GridAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void add(List<Grid> grids) {
        this.grids = grids;
    }

    @Override
    public int getCount() {
        return grids != null ? grids.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return grids != null ? grids.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new GridAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_grid, null);
            holder.gridName = convertView.findViewById(R.id.tv_item_grid_name);
            convertView.setTag(holder);
        } else {
            holder = (GridAdapter.ViewHolder) convertView.getTag();
        }

        holder.gridName.setText(grids.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        public TextView gridName;
    }
}

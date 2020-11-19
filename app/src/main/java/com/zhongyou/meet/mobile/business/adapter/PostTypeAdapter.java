package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.PostType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongce
 * create time: 2018/10/25
 */
public class PostTypeAdapter extends BaseAdapter {

    private List<PostType> postTypes = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public PostTypeAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void add(List<PostType> postTypes) {
        this.postTypes = postTypes;
    }

    @Override
    public int getCount() {
        return postTypes != null ? postTypes.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return postTypes != null ? postTypes.get(position) : null;
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
            convertView = layoutInflater.inflate(R.layout.item_posttype, null);
            holder.postTypeName = convertView.findViewById(R.id.tv_item_posttype_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.postTypeName.setText(postTypes.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        public TextView postTypeName;
    }
}

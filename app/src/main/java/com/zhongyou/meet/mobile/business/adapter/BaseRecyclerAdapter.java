package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的RecyclerView Adapter,实现了数据的添加,删除,点击事件
 * Created by wufan on 2017/8/3.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {
    public String TAG = getClass().getSimpleName();
    public LayoutInflater mInflater;
    protected Context mContext;
    public List<T> mData = new ArrayList<>();


    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public BaseRecyclerAdapter(Context context, List<T> data) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mData = data;

    }

    public void onDestroy(){

    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    private void setItemClick(View itemView, final int position) {
        if (mListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v, position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }


    public void setData(List<T> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void addData(List<T> data) {
        mData.addAll(data);
    }

    public void clearData() {
        mData.clear();
    }

    public List<T> getData() {
        return mData;
    }

}

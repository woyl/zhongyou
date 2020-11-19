package com.zhongyou.meet.mobile.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by whatisjava on 16-11-26.
 */

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {


    private int top;


    public GridSpaceItemDecoration(int top) {
        this.top = top;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.top = top;
        }
    }




}

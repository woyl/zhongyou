package com.zhongyou.meet.mobile.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by whatisjava on 16-11-26.
 */

public class MeetingSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int left;
    private int top;
    private int right;
    private int bottom;

    public MeetingSpaceItemDecoration(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view)!=0){
                outRect.left = left;
                outRect.top = top;
                outRect.right = right;
                outRect.bottom = bottom;
            }

    }

}

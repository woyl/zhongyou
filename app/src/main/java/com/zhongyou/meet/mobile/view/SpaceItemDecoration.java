package com.zhongyou.meet.mobile.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by whatisjava on 16-11-26.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

	private int left;
	private int top;
	private int right;
	private int bottom;

	private boolean firstChildShowTop;


	public SpaceItemDecoration(boolean firstChildShowTop, int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.firstChildShowTop = firstChildShowTop;
	}

	public SpaceItemDecoration(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

		/*if (parent.getChildAdapterPosition(view) == 0) {
			if (firstChildShowTop) {
				outRect.top = top;
			}

		}else {
			outRect.top = top;
		}*/
		outRect.top = top;
		outRect.left = left;
		outRect.right = right;
		outRect.bottom = bottom;


	}

}

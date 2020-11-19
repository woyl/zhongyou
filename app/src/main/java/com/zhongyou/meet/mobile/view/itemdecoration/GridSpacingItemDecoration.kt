package com.zhongyou.meet.mobile.view.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration  : RecyclerView.ItemDecoration {
    private var spanCount:Int?= null
    private var spacing:Int?= null
    private var iscludeEdge:Boolean?= null

    constructor(spanCount:Int,spacing :Int,iscludeEdge:Boolean) {
        this.spanCount = spanCount
        this.iscludeEdge = iscludeEdge
        this.spacing = spacing
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount!!
        if (iscludeEdge!!) {
            outRect.left = spacing?.minus(column*spacing!!/spanCount!!) ?: 0
            outRect.right = (column+1) * spacing!!/spanCount!!

            if (position < spanCount!!) {
                outRect.top = spacing!!
            }

            outRect.bottom = spacing!!
        } else {
            outRect.left = column * spacing!! /spanCount!!
            outRect.right = spacing!! - (column+1) * spacing!! /spanCount!!
            if (position >= spanCount!!) {
                outRect.top = spacing!!
            }
        }
    }
}
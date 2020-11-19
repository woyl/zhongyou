package com.zhongyou.meet.mobile.utils

import android.content.res.Resources
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.zhongyou.meet.mobile.R

object TabViewUtils {

    fun checkTab(tabAt: TabLayout.Tab?, b: Boolean, resources : Resources) {
        val view = tabAt?.customView
        if (view == null) {
            tabAt?.setCustomView(R.layout.tablayout_item)
        }
        val textview = tabAt?.customView?.findViewById<TextView>(R.id.tv_top_item)
        val viewLine = tabAt?.customView?.findViewById<View>(R.id.view)
        if (b) {
            textview?.let {
                it.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                it.textSize = 17f
                it.setTextColor(resources.getColor(R.color.c3a8ae6))
                it.text = tabAt.text
            }
            viewLine?.visibility = View.VISIBLE
        } else {
            textview?.let {
                it.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
//                it.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
                it.textSize = 14f
                it.setTextColor(resources.getColor(R.color.color_666666))
                it.text = tabAt.text
            }
            viewLine?.visibility = View.GONE
        }
    }

}
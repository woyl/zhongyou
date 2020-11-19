package com.zhongyou.meet.mobile.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import cn.bingoogolapple.bgabanner.BGABanner

class BGABannerView(context: Context?, attrs: AttributeSet?) : BGABanner(context, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }
}
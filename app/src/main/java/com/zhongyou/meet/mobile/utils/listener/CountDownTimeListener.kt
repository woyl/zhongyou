package com.zhongyou.meet.mobile.utils.listener

interface CountDownTimeListener {
    /**计时完成*/
    fun onTimeFinish()
    /**计时过程*/
    fun onTimeTick(time:Long)
}
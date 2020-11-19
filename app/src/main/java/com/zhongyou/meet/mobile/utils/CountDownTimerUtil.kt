package com.zhongyou.meet.mobile.utils

import android.os.CountDownTimer
import com.zhongyou.meet.mobile.utils.listener.CountDownTimeListener

class CountDownTimerUtil (var millisInFuture:Long,var countDownInterval:Long,var listener:CountDownTimeListener) : CountDownTimer(millisInFuture,countDownInterval) {

    override fun onFinish() {
        listener.onTimeFinish()
    }

    override fun onTick(millisUntilFinished: Long) {
        listener.onTimeTick(millisUntilFinished)
    }

}
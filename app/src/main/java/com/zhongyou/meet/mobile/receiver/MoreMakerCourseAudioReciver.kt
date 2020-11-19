package com.zhongyou.meet.mobile.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zhongyou.meet.mobile.utils.listener.MonitorTwoListener

class MoreMakerCourseAudioReciver :BroadcastReceiver() {

    private var monitorListener : MonitorTwoListener<Context, Intent>?= null

    fun setMonitorListener (monitorListener : MonitorTwoListener<Context, Intent>?) {
        this.monitorListener = monitorListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        abortBroadcast()
        context?.let { intent?.let { it1 -> monitorListener?.OnMonitor(it, it1) } }
    }
}
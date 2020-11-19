package com.zhongyou.meet.mobile.core

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error

/**
 * @author golangdorid@gmail.com
 * @date 2020/7/30 1:00 PM.
 * @
 */

class FloatingService : Service(), AnkoLogger {

    companion object {
        private var mServiceVoice: FloatingService? = null
        const val ACTION_SHOW_FLOATING = "action_show_floating"
        const val ACTION_DISMISS_FLOATING = "action_dismiss_floating"

        @JvmField
        var isStart = false

        var role = 0
        var uid = 0
        var appId = ""
        fun stopSelf() {
            mServiceVoice?.stopSelf()
            mServiceVoice = null
        }


    }

    private var mFloatingView: FloatingView? = null

    /**
     * 监听本地广播显示或隐藏悬浮窗
     */
    private var mLocalBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ACTION_SHOW_FLOATING == intent?.action) {
                mFloatingView?.show(role, uid, appId)
                error { "FloatingService   show" }
            } else if (ACTION_DISMISS_FLOATING == intent?.action) {
                mFloatingView?.dismiss()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        mServiceVoice = this
        isStart = true
        //初始化悬浮View
        mFloatingView = FloatingView(this)
        //注册监听本地广播
        val intentFilter = IntentFilter(ACTION_SHOW_FLOATING)
        intentFilter.addAction(ACTION_DISMISS_FLOATING)
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalBroadcastReceiver, intentFilter)

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        error("FloatingService onStartCommand")
        //显示悬浮窗
        intent?.let {
            role = it.getIntExtra("role", 0)
            uid = it.getIntExtra("uid", 0)
            appId = it.getStringExtra("appId")

        }

        mFloatingView?.show(role, uid, appId)
        /* mVoiceFloatingView?.setOnLongClickListener {
             val voiceActivityIntent = Intent(this, VoiceActivity::class.java)
             voiceActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
             startActivity(voiceActivityIntent)
             mVoiceFloatingView?.dismiss()
             true
         }*/

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mFloatingView?.dismiss()
        mFloatingView = null
        isStart = false
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalBroadcastReceiver)
        super.onDestroy()
    }
}
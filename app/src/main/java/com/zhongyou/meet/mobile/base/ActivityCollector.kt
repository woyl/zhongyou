package com.zhongyou.meet.mobile.base

import androidx.appcompat.app.AppCompatActivity


/**
 * @author golangdorid@gmail.com
 * @date 2020/6/2 2:20 PM.
 * @
 */
object ActivityCollector {
    val activityLists = ArrayList<AppCompatActivity>()

    fun addActivity(appCompatActivity: AppCompatActivity) {
        activityLists.add(appCompatActivity)
    }

    fun removeActivity(appCompatActivity: AppCompatActivity) = activityLists.remove(appCompatActivity)

    fun finishAllActivity() {
        for (activityList in activityLists) {
            if (!activityList.isFinishing) {
                activityList.finish()
            }
        }
    }
}
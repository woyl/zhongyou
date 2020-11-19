package com.zhongyou.meet.mobile.utils.listener

interface MonitorTwoListener<K,V> {
    fun OnMonitor(k:K,v:V)
}
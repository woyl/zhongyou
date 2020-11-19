package com.zhongyou.meet.mobile.utils.listener

interface MonitorThreeListener<K,V,T> {
    fun OnMonitor(k:K,v:V,t:T)
}
package com.zhongyou.meet.mobile.utils.listener

interface MonitorFourLister<K,V,T,L> {
    fun OnMonitor(k:K,v:V,t:T,l:L)
}
package com.zhongyou.meet.mobile.core

import com.zhongyou.meet.mobile.entiy.MoreAudio

class MusicManager private constructor(){

    var position = -1
    var positionNew :Int = -1
    var oldMusiPosition = -1
    var isMoreMusicDatas = true
    var isMoreDetails = true
    var isOtherPause = true
    var moreAudios = mutableListOf<MoreAudio>()

    companion object {
        val instance :MusicManager by lazy( mode = LazyThreadSafetyMode.SYNCHRONIZED) { MusicManager() }
    }

    fun addMusics(data : MutableList<MoreAudio>?, position:Int) {
        data?.let {
            moreAudios.addAll(it)
        }
        this.position = position
    }

    fun addMusics(data : MutableList<MoreAudio>?) {
        data?.let {
            moreAudios.addAll(it)
        }
    }

}
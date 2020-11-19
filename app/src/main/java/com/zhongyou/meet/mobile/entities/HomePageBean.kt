package com.zhongyou.meet.mobile.entities

data class HomePageBean(
    val `data`: List<Data>,
    val errcode: Int,
    val errmsg: String
)

data class Data(
    val columnId: String,
    val columnName: String,
    val homeLinkPages: List<HomeLinkPage>,
    val id: String,
    val sort: Int
)

data class HomeLinkPage(
        val homeId: String,
        val id: String,
        val imageUrl: String,
        val lecturerInfo: String,
        val pageId: String,
        val pageName: String,
        val pageType: Int,
        val sort: Int,
        val studyNum: Int,
        val videoNum: Int,
        val seriesId:String?,
        var isSign:Int,
        val studyNumStr:String?
)
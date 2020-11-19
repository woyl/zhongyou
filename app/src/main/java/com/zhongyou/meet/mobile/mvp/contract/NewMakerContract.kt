package com.zhongyou.meet.mobile.mvp.contract

import android.app.Activity
import com.alibaba.fastjson.JSONObject
import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.zhongyou.meet.mobile.entities.Data
import com.zhongyou.meet.mobile.entiy.MakerColumn
import com.zhongyou.meet.mobile.entiy.MoreAudio
import com.zhongyou.meet.mobile.entiy.RecomandData
import com.zhongyou.meet.mobile.entiy.RecomandData.ChangeLessonByDayBean
import io.reactivex.Observable


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 09/25/2020 17:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
interface NewMakerContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun getActivity(): Activity?
        fun getBannerData(bannerData : RecomandData)
        fun getAudioDataSuccess(data: ChangeLessonByDayBean?)
        fun getHomepage(homePageBean: MutableList<Data>?)
        fun getMakerColumn(column: MakerColumn?, isSuccess: Boolean)
        fun showRedDot(isShow: Boolean, count: Int)
        fun getDataComplete(dataLists: List<MoreAudio>?): List<MoreAudio?>?
        fun signSuccess(isSuccess: Boolean, sign: Int,message:String?,position:Int,positionItem:Int,typeFragment:Int)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getRecommend(type: Int): Observable<JSONObject?>?
        fun getHomePage():Observable<JSONObject>
        fun getMakerColumn(): Observable<JSONObject?>?

        fun verifyRole(jsonObject: JSONObject?): Observable<JSONObject?>?
        fun joinMeeting(jsonObject: JSONObject?): Observable<JSONObject?>?
        fun getAgoraKey(channel: String?, account: String?, role: String?): Observable<JSONObject?>?

        fun quickJoin(json: JSONObject?): Observable<JSONObject?>?
        fun getUnReadMessageCount(): Observable<JSONObject?>?

        fun getMoreAudio(pageNo: Int): Observable<JSONObject?>?

        fun clickCourse(id:String):Observable<JSONObject?>?

        fun signCourse(typeID: String?, isSign: Int): Observable<JSONObject?>?
    }

}

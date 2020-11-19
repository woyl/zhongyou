package com.zhongyou.meet.mobile.mvp.contract

import com.alibaba.fastjson.JSONObject
import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.zhongyou.meet.mobile.entiy.MeetingJoin
import com.zhongyou.meet.mobile.entiy.MeetingsData
import io.reactivex.Observable


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/03/2020 15:58
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
interface ClassRoomContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun showEmptyView(isShow: Boolean,isMeeting: String)
        fun showMeetings(meetingsData: MeetingsData?,isMeeting:String)
        fun getAgoraKey(type: Int, jsonObject: JSONObject?, join: MeetingJoin?, joinRole: Int)
        fun verifyRole(jsonObject: JSONObject?, meeting: MeetingsData.DataBean.ListBean?, code: String?)
        fun joinMeeting(jsonObject: JSONObject?, meeting: MeetingsData.DataBean.ListBean?, code: String?)
        fun showRedDot(isShow: Boolean, count: Int)
        fun subscribeMeetingSuccess(message:Boolean,position: Int,isMeeting:Int)
        fun cancelSubcribeMeeting(message:Boolean,position:Int,isMeeting:Int)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getAllMeeting(type: Int, pageNo: Int, title: String?,isMeeting:String?): Observable<JSONObject?>?
        fun quickJoin(json: JSONObject?): Observable<JSONObject?>?
        fun getAgoraKey(channel: String?, account: String?, role: String?): Observable<JSONObject?>?
        fun verifyRole(jsonObject: JSONObject?): Observable<JSONObject?>?
        fun joinMeeting(jsonObject: JSONObject?): Observable<JSONObject?>?
        fun getUnReadMessageCount(): Observable<JSONObject?>?
        fun subscribeMeeting(meetingId:String):Observable<JSONObject?>?
        fun cancelSubscribeMeeting(meetingId:String):Observable<JSONObject?>?
    }

}

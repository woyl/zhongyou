package com.zhongyou.meet.mobile.mvp.model

import android.app.Application
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.zhongyou.meet.mobile.ameeting.network.ApiService
import com.zhongyou.meet.mobile.mvp.contract.ClassRoomContract
import io.reactivex.Observable
import javax.inject.Inject


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
@ActivityScope
class ClassRoomModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), ClassRoomContract.Model {
    @Inject
    lateinit var mGson: Gson;

    @Inject
    lateinit var mApplication: Application;
    override fun getAllMeeting(type: Int, pageNo: Int, title: String?,isMeeting:String?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).getAllMeeting(type, pageNo, title,isMeeting)
    }

    override fun quickJoin(json: JSONObject?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).quickJoin(json)
    }

    override fun getAgoraKey(channel: String?, account: String?, role: String?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).getAgoraKey(channel, account, role)
    }

    override fun verifyRole(jsonObject: JSONObject?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).verifyRole(jsonObject)
    }

    override fun joinMeeting(jsonObject: JSONObject?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).joinMeeting(jsonObject)
    }

    override fun getUnReadMessageCount(): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).isExistUnread
    }

    override fun subscribeMeeting(meetingId: String): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).meetingReserve(meetingId)
    }

    override fun cancelSubscribeMeeting(meetingId: String): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).cancelAdvance(meetingId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

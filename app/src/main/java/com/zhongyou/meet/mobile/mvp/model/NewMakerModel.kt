package com.zhongyou.meet.mobile.mvp.model

import android.app.Application
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.zhongyou.meet.mobile.ameeting.network.ApiService
import com.zhongyou.meet.mobile.mvp.contract.NewMakerContract
import io.reactivex.Observable
import javax.inject.Inject


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
@FragmentScope
class NewMakerModel
@Inject constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), NewMakerContract.Model {
    @Inject
    lateinit var mGson: Gson;

    @Inject
    lateinit var mApplication: Application;
    override fun getRecommend(type: Int): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).getPromotionPageId(type)
    }

    override fun getHomePage(): Observable<JSONObject> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).homePage
    }

    override fun getMakerColumn(): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).makerColumn
    }

    override fun verifyRole(jsonObject: JSONObject?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).verifyRole(jsonObject)
    }

    override fun joinMeeting(jsonObject: JSONObject?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).joinMeeting(jsonObject)
    }

    override fun getAgoraKey(channel: String?, account: String?, role: String?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).getAgoraKey(channel, account, role)
    }

    override fun quickJoin(json: JSONObject?): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).quickJoin(json)
    }

    override fun getUnReadMessageCount(): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).isExistUnread
    }

    override fun getMoreAudio(pageNo: Int): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).getMoreAudioCourse(pageNo)
    }

    override fun clickCourse(id: String): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).clickCourse(id);
    }

    override fun signCourse(typeID: String?, isSign: Int): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).siginCourse(typeID, isSign)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

package com.zhongyou.meet.mobile.mvp.model

import android.app.Application
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.zhongyou.meet.mobile.ameeting.network.ApiService
import com.zhongyou.meet.mobile.mvp.contract.MyNewContract
import io.reactivex.Observable
import javax.inject.Inject


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/02/2020 11:31
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
class MyNewModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), MyNewContract.Model {
    @Inject
    lateinit var mGson: Gson;

    @Inject
    lateinit var mApplication: Application;
    override fun getUnReadMessageCount(): Observable<JSONObject?>? {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).isExistUnread
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}

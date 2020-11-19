package com.zhongyou.meet.mobile.mvp.presenter

import android.app.Application
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.entiy.MyAllCourse
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import com.zhongyou.meet.mobile.mvp.contract.MyClassRoomContract
import com.zhongyou.meet.mobile.utils.MMKVHelper
import io.reactivex.disposables.Disposable


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/03/2020 11:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
class MyClassRoomPresenter
@Inject
constructor(model: MyClassRoomContract.Model, rootView: MyClassRoomContract.View) :
        BasePresenter<MyClassRoomContract.Model, MyClassRoomContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mAppManager: AppManager


    fun getCollectMeeting() {
        if (mModel != null) {
//            mModel.getCollectMeeting(MMKV.defaultMMKV().decodeString(MMKVHelper.ID))
            mModel.getCollectMeetingAndLive(MMKV.defaultMMKV().decodeString(MMKVHelper.ID))
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            mRootView.hideLoading()
                            if (jsonObject?.getInteger("errcode") == 0) {
                                val data = jsonObject.getJSONObject("data")
                                val myAllCourse = JSON.parseObject(data.toJSONString(), MyAllCourse::class.java)
                                mRootView.getDataSuccess(myAllCourse)
                            } else {
                                jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                            }
                        }

                        override fun onComplete() {
                            super.onComplete()
                            mRootView.hideLoading()
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.hideLoading()
                        }
                    })
        }
    }

    /**
     * 取消订阅
     */
    fun cancelSubscribe(meetingId: String?, isClassRoom: Boolean) {
        if (meetingId == null) return
        if (mModel != null) {
            mModel.cancelSubscribeMeeting(meetingId)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                mRootView.cancelSubcribeMeeting(true, meetingId, isClassRoom)
                            } else {
                                mRootView.cancelSubcribeMeeting(false, meetingId, isClassRoom)
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.cancelSubcribeMeeting(false, meetingId, isClassRoom)
                        }
                    })
        }
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}

package com.zhongyou.meet.mobile.mvp.presenter

import android.app.Application
import com.alibaba.fastjson.JSONObject
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.RxLifecycleUtils
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.mvp.contract.NewMakerDetailContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 09/29/2020 16:00
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
class NewMakerDetailPresenter
@Inject
constructor(model: NewMakerDetailContract.Model, rootView: NewMakerDetailContract.View) :
        BasePresenter<NewMakerDetailContract.Model, NewMakerDetailContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mAppManager: AppManager


    fun getMoreCourse(pageId: String?, seriesId: String?) {
        if (mModel != null) {
            mModel.getMoreAudio(pageId, seriesId)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {

                        override fun _onNext(jsonObject: JSONObject?) {
                            mRootView.hideLoading()
                            if (jsonObject?.getInteger("errcode") == 0) {
                                mRootView.getMoreAudioSuccess(jsonObject.getJSONObject("data"))
                            } else {
                                jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.hideLoading()
                        }
                    })
        }
    }

    fun signCourse(type: String?, isSign: Int) {
        if (mModel != null) {
            mModel.signCourse(type, isSign)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                if (mRootView != null) {
                                    mRootView.signSuccess(true, isSign,jsonObject.getString("data"))
                                }
                            } else {
                                if (mRootView != null) {
                                    mRootView.signSuccess(false, isSign,"")
                                }
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            if (mRootView != null) {
                                mRootView.signSuccess(false, isSign,"")
                            }
                        }
                    })
        }
    }

    fun unLockSeries(seriesId: String?) {
        if (mModel != null) {
            mModel.unLockSeries(seriesId)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 11022) {
                                mRootView.unLockSeriesResult(false, jsonObject.getJSONObject("data").getString("wechat"))
                            } else {
                                mRootView.unLockSeriesResult(true, "")
                            }
                        }
                    })
        }
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}

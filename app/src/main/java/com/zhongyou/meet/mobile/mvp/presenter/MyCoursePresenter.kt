package com.zhongyou.meet.mobile.mvp.presenter

import android.app.Application
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.RxLifecycleUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.entiy.MyAllCourse
import com.zhongyou.meet.mobile.mvp.contract.MyCourseContract
import com.zhongyou.meet.mobile.utils.MMKVHelper
import io.reactivex.disposables.Disposable
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/03/2020 11:00
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
class MyCoursePresenter
@Inject
constructor(model: MyCourseContract.Model, rootView: MyCourseContract.View) :
        BasePresenter<MyCourseContract.Model, MyCourseContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mAppManager: AppManager


    fun getCollectMeeting(type:Int) {
        if (mModel != null) {
            mModel.getCollectMeeting(MMKV.defaultMMKV().decodeString(MMKVHelper.ID))
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
                                mRootView.getDataSuccess(myAllCourse,type)
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

    fun signCourse(type: String?, isSign: Int,position:Int,typeFragment:Int) {
        if (mModel != null) {
            mModel.signCourse(type, isSign)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                if (mRootView != null) {
                                    mRootView.signSuccess(true, isSign,jsonObject.getString("data"),position,typeFragment)
                                }
                            } else {
                                if (mRootView != null) {
                                    mRootView.signSuccess(false, isSign,"",position,typeFragment)
                                }
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            if (mRootView != null) {
                                mRootView.signSuccess(false, isSign,"",position,typeFragment)
                            }
                        }
                    })
        }
    }


    override fun onDestroy() {
        super.onDestroy();
    }
}

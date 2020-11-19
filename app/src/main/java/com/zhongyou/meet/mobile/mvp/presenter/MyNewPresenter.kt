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
import com.zhongyou.meet.mobile.mvp.contract.MyNewContract
import io.reactivex.disposables.Disposable
import me.jessyan.rxerrorhandler.core.RxErrorHandler
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
class MyNewPresenter
@Inject
constructor(model: MyNewContract.Model, rootView: MyNewContract.View) :
        BasePresenter<MyNewContract.Model, MyNewContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mAppManager: AppManager


    /**
     * 获取未读消息
     */
    fun getUnReadMessage() {
        if (mModel != null) {
            mModel.getUnReadMessageCount()!!.compose(RxSchedulersHelper.io_main<JSONObject>())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                val data = jsonObject.getInteger("data")
                                if (data == 0) {
                                    mRootView.showRedDot(false, data)
                                } else {
                                    mRootView.showRedDot(true, data)
                                }
                            } else {
                                mRootView.showRedDot(false, 0)
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.showRedDot(false, 0)
                        }
                    })
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}

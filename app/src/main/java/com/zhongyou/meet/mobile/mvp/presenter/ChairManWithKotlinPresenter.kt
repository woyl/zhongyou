package com.zhongyou.meet.mobile.mvp.presenter

import android.app.Application

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import com.zhongyou.meet.mobile.mvp.contract.ChairManWithKotlinContract


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 05/20/2020 13:53
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
class ChairManWithKotlinPresenter
@Inject
constructor(model: ChairManWithKotlinContract.Model, rootView: ChairManWithKotlinContract.View) :
        BasePresenter<ChairManWithKotlinContract.Model, ChairManWithKotlinContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager




    override fun onDestroy() {
        super.onDestroy();
    }
}

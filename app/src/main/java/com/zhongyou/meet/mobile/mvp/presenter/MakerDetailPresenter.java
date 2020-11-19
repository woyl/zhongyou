package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;

import com.alibaba.fastjson.JSONObject;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.mvp.contract.MakerDetailContract;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/07/2020 11:46
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MakerDetailPresenter extends BasePresenter<MakerDetailContract.Model, MakerDetailContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;

	@Inject
	public MakerDetailPresenter(MakerDetailContract.Model model, MakerDetailContract.View rootView) {
		super(model, rootView);
	}

	public void getMoreCourse(String pageId,String seriesId) {
		if (mModel != null) {
			mModel.getMoreAudio(pageId,seriesId).compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							mRootView.hideLoading();
							if (jsonObject.getInteger("errcode") == 0) {

								mRootView.getDataSuccess(jsonObject.getJSONObject("data"));
							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}

						@Override
						public void _onError(int code, String msg) {
							super._onError(code, msg);
							mRootView.hideLoading();
						}
					});
		}
	}

	public void signCourse(String type, int isSign) {
		if (mModel != null) {
			mModel.signCourse(type, isSign)
					.compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode")==0){
								if (mRootView!=null){
									mRootView.signSuccess(true,isSign);
								}
							}else {
								if (mRootView!=null){
									mRootView.signSuccess(false,isSign);
								}
							}

						}

						@Override
						public void _onError(int code, String msg) {
							super._onError(code, msg);
							if (mRootView!=null){
								mRootView.signSuccess(false,isSign);
							}
						}
					});
		}
	}

	public void unLockSeries(String seriesId){
		if (mModel!=null){
			mModel.unLockSeries(seriesId).compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode")==11022){
								mRootView.unLockSeriesResult(false,jsonObject.getJSONObject("data").getString("wechat"));
							}else {
								mRootView.unLockSeriesResult(true,"");
							}
						}
					});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mErrorHandler = null;
		this.mAppManager = null;
		this.mImageLoader = null;
		this.mApplication = null;
	}
}

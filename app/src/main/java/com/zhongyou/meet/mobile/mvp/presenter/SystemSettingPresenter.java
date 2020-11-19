package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;

import com.alibaba.fastjson.JSONObject;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.mvp.contract.SystemSettingContract;
import com.zhongyou.meet.mobile.utils.ApkUtil;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 11:12
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class SystemSettingPresenter extends BasePresenter<SystemSettingContract.Model, SystemSettingContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;

	@Inject
	public SystemSettingPresenter(SystemSettingContract.Model model, SystemSettingContract.View rootView) {
		super(model, rootView);
	}

	public void versionCheck(JSONObject jsonObject) {
		if (mModel != null) {
			mModel.versionCheck(jsonObject).compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<com.alibaba.fastjson.JSONObject>() {
						@Override
						public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								if (jsonObject.getJSONObject("data") == null) {
									if (mRootView != null) {
										mRootView.versionCheck(false);
									}
								} else {
									Integer isForce = jsonObject.getJSONObject("data").getInteger("isForce");
									String versionCode = jsonObject.getJSONObject("data").getString("version");
									String describe = jsonObject.getJSONObject("data").getString("describe");
									String downURL = jsonObject.getJSONObject("data").getString("downURL");
									try {
										if (ApkUtil.compareVersion(versionCode, BuildConfig.VERSION_NAME) > 0) {
											if (mRootView != null) {
												mRootView.versionCheck(true);
											}
										} else {
											if (mRootView != null) {
												mRootView.versionCheck(false);
											}
										}
									} catch (Exception ex) {
										ex.printStackTrace();
										if (mRootView != null) {
											mRootView.versionCheck(false);
										}
									}

								}
							}else {
								if (mRootView != null) {
									mRootView.versionCheck(false);
									mRootView.showMessage(jsonObject.getString("errmsg"));
								}
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

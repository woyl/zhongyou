package com.zhongyou.meet.mobile.mvp.model;

import android.app.Application;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.mvp.contract.AccountSettingContract;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 12:36
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class AccountSettingModel extends BaseModel implements AccountSettingContract.Model {
	@Inject
	Gson mGson;
	@Inject
	Application mApplication;

	@Inject
	ApiService mApiService;

	@Inject
	public AccountSettingModel(IRepositoryManager repositoryManager) {
		super(repositoryManager);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mGson = null;
		this.mApplication = null;
	}

	@Override
	public Observable<JSONObject> requestQiNiuToken() {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).requestQiNiuToken();
	}

	@Override
	public Observable<JSONObject> setUserInformation(String userId, JSONObject json) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).setUserInformation(userId, json);
	}

	@Override
	public Observable<JSONObject> upDateUserNickName(String userId, JSONObject json) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).setUserInformation(userId, json);
	}

	@Override
	public Observable<JSONObject> getAllUserType() {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getAllUserType();
	}

	@Override
	public Observable<JSONObject> getUserProfile(String parentId) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getUserProfile(parentId);
	}

	@Override
	public Observable<JSONObject> getToken(JSONObject jsonObject) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getToken(jsonObject);
	}
}
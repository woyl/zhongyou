package com.zhongyou.meet.mobile.mvp.model;

import android.app.Application;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.mvp.contract.MyContract;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/03/2020 23:02
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
public class MyModel extends BaseModel implements MyContract.Model {
	@Inject
	Gson mGson;
	@Inject
	Application mApplication;

	@Inject
	ApiService mApiService;

	@Inject
	public MyModel(IRepositoryManager repositoryManager) {
		super(repositoryManager);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mGson = null;
		this.mApplication = null;
	}

	@Override
	public Observable<JSONObject> getCollectMeeting(String userId) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getCollectMeeting(userId);
	}

	@Override
	public Observable<JSONObject> verfyRole(JSONObject jsonObject) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).verifyRole(jsonObject);
	}

	@Override
	public Observable<JSONObject> joinMeeting(JSONObject jsonObject) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).joinMeeting(jsonObject);
	}

	@Override
	public Observable<JSONObject> getAgoraKey(String channel, String account, String role) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getAgoraKey(channel, account, role);
	}

	@Override
	public Observable<JSONObject> siginCourse(String typeId, int isSign) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).siginCourse(typeId, isSign);
	}

	@Override
	public Observable<JSONObject> cancelAdvance(String meetingId) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).cancelAdvance(meetingId);
	}


	@Override
	public Observable<JSONObject> getUnReadMessageCount() {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getIsExistUnread();
	}
}
package com.zhongyou.meet.mobile.mvp.model;

import android.app.Application;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.mvp.contract.MessageDetailContract;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 21:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MessageDetailModel extends BaseModel implements MessageDetailContract.Model {
	@Inject
	Gson mGson;
	@Inject
	Application mApplication;

	@Inject
	ApiService mApiService;

	@Inject
	public MessageDetailModel(IRepositoryManager repositoryManager) {
		super(repositoryManager);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mGson = null;
		this.mApplication = null;
	}

	@Override
	public Observable<JSONObject> getMessageDetail(String userId, String type ,int pageNo) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getMessageDetailByUserId(userId,pageNo);
	}
	@Override
	public Observable<JSONObject> verifyRole(JSONObject jsonObject) {
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
	public Observable<JSONObject> readOneMessage(String id) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).readOneMessage(id);
	}

	@Override
	public Observable<JSONObject> readAllMessage(String type) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).readAllMessage();
	}

	@Override
	public Observable<JSONObject> deteleItem(String msgId) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).deteleItemMessage(msgId);
	}

}
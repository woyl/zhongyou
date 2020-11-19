package com.zhongyou.meet.mobile.mvp.model;

import android.app.Application;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.mvp.contract.MeetingContract;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 03/27/2020 15:18
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
public class MeetingModel extends BaseModel implements MeetingContract.Model {
	@Inject
	Gson mGson;
	@Inject
	Application mApplication;


	@Inject
	public MeetingModel(IRepositoryManager repositoryManager) {
		super(repositoryManager);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mGson = null;
		this.mApplication = null;
	}

	@Override
	public Observable<JSONObject> requestMeetingAdmin() {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).requestMeetingAdmin();
	}

	@Override
	public Observable<JSONObject> getAllMeeting(int type, int pageNo, String title,String meeting) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getAllMeeting(type, pageNo, title,meeting);
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
	public Observable<JSONObject> getBanner() {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getBanner();
	}

	@Override
	public Observable<JSONObject> quickJoin(JSONObject json) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).quickJoin(json);
	}

	@Override
	public Observable<JSONObject> getRecommend(int type) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getPromotionPageId(type);
	}

	@Override
	public Observable<JSONObject> orderMeeting(String id) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).meetingReserve(id);
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
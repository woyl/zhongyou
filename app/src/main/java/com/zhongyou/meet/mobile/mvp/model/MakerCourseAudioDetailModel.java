package com.zhongyou.meet.mobile.mvp.model;

import android.app.Application;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioDetailContract;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/09/2020 10:55
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MakerCourseAudioDetailModel extends BaseModel implements MakerCourseAudioDetailContract.Model {
	@Inject
	Gson mGson;
	@Inject
	Application mApplication;

	@Inject
	ApiService mApiService;

	@Inject
	public MakerCourseAudioDetailModel(IRepositoryManager repositoryManager) {
		super(repositoryManager);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mGson = null;
		this.mApplication = null;
	}

	@Override
	public Observable<JSONObject> getDatail(String pageId, String id) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getMoreCourse(pageId,id);
	}



	@Override
	public Observable<JSONObject> getQuestion(String pageID) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getQuestion(pageID);
	}

	@Override
	public Observable<JSONObject> commitQuestion(JSONObject json) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).commitQuestion(json);
	}
}
package com.zhongyou.meet.mobile.mvp.model;

import android.app.Application;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioContract;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/08/2020 13:43
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MakerCourseAudioModel extends BaseModel implements MakerCourseAudioContract.Model {
	@Inject
	Gson mGson;
	@Inject
	Application mApplication;

	@Inject
	ApiService mApiService;

	@Inject
	public MakerCourseAudioModel(IRepositoryManager repositoryManager) {
		super(repositoryManager);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mGson = null;
		this.mApplication = null;
	}

	@Override
	public Observable<JSONObject> getMoreAudio(int pageNo) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getMoreAudioCourse(pageNo);
	}

	@Override
	public Observable<JSONObject> clickCourse(String id) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).clickCourse(id);
	}
}
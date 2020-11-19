package com.zhongyou.meet.mobile.mvp.model;

import android.app.Application;
import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.IM.IMInfoProvider;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.mvp.contract.IndexContract;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.MMKVHelper;

import io.reactivex.Observable;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 03/20/2020 14:20
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class IndexModel extends BaseModel implements IndexContract.Model {
	@Inject
	Gson mGson;
	@Inject
	Application mApplication;
	@Inject
	ApiService mApiService;


	@Inject
	public IndexModel(IRepositoryManager repositoryManager) {
		super(repositoryManager);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mGson = null;
		this.mApplication = null;
	}


	@Override
	public Observable<JSONObject> versionCheck(String applicationID, String versionCode) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).versionCheck(applicationID, versionCode);
	}

	@Override
	public Observable<JSONObject> registerDevice(JSONObject jsonObject) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).registerDevice(jsonObject);
	}

	@Override
	public Observable<JSONObject> getUserInfo() {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).getUserInfo();
	}

	@Override
	public Observable<JSONObject> meetingJoinStats(JSONObject jsonObject) {
		return mRepositoryManager.obtainRetrofitService(ApiService.class).meetingJoinStats(jsonObject);
	}

	@Override
	public void initImUserProvider() {
		IMInfoProvider infoProvider = new IMInfoProvider();
		infoProvider.init(mApplication);

		UserInfo info = new UserInfo(MMKV.defaultMMKV().decodeString(MMKVHelper.ID)
				, MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME),
				Uri.parse(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO)));
		RongIM.getInstance().refreshUserInfoCache(info);
		RongIM.getInstance().setCurrentUserInfo(info);
		RongIM.getInstance().setMessageAttachedUserInfo(true);
	}


}
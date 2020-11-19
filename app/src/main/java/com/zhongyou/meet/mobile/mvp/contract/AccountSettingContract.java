package com.zhongyou.meet.mobile.mvp.contract;

import android.app.Activity;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.zhongyou.meet.mobile.entities.User;

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
public interface AccountSettingContract {
	//对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
	interface View extends IView {

		Activity getOnAttachActivity();

		void UpDateUI(User user);

		void changeUserType(String userCenter, String type);

		void changeUserNickName(String nickname);

		void changeUserLocation(String location);

		ViewGroup getViewGroup();

		boolean isFirst();

		void showInputSchoolName(boolean isShow);

		void changeUserSchoolName(String schoolName);

		void startActivity(Class activity);

	}

	//Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
	interface Model extends IModel {
		Observable<JSONObject> requestQiNiuToken();

		Observable<JSONObject> setUserInformation(String userId, JSONObject json);

		Observable<JSONObject> upDateUserNickName(String userId, JSONObject json);

		Observable<JSONObject> getAllUserType();

		//获取用户身份
		Observable<JSONObject> getUserProfile(String parentId);


		Observable<JSONObject> getToken(JSONObject jsonObject);

	}
}

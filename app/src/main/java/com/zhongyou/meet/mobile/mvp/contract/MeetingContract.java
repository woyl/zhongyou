package com.zhongyou.meet.mobile.mvp.contract;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.zhongyou.meet.mobile.entiy.MeetingJoin;
import com.zhongyou.meet.mobile.entiy.MeetingsData;

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
public interface MeetingContract {
	//对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
	interface View extends IView {

		Activity getOnAttachActivity();

		void showAdminViews(boolean isShow);

		void showMeetings(MeetingsData meetingsData);

		void showBannerView(JSONObject jsonObject);

		void showEmptyView(boolean isShow);

		void showBannerView(boolean isShow);

		void verifyRole(JSONObject jsonObject, MeetingsData.DataBean.ListBean meeting, String code);

		void joinMeeting(JSONObject jsonObject, MeetingsData.DataBean.ListBean meeting, String code);

		void getAgoraKey(int type, JSONObject jsonObject, MeetingJoin join, int joinRole);

		void initDialog(MeetingsData.DataBean.ListBean meeting, String isToken);

		void isShowMarqueeView(boolean isShow);

		void orderMeetingResult(boolean isSuccess, int position);

		void showPhoneStatus(int i, final MeetingsData.DataBean.ListBean meeting, String isToken);

		void cancleAdvance(int position, boolean isSuccess);
		void showRedDot(boolean isShow,int count);

	}

	//Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
	interface Model extends IModel {
		Observable<JSONObject> requestMeetingAdmin();

		Observable<JSONObject> getAllMeeting(int type, int pageNo, String title,String isMeeting);

		Observable<JSONObject> verifyRole(JSONObject jsonObject);

		Observable<JSONObject> joinMeeting(JSONObject jsonObject);

		Observable<JSONObject> getAgoraKey(String channel, String account, String role);

		Observable<JSONObject> getBanner();

		Observable<JSONObject> quickJoin(JSONObject json);

		Observable<JSONObject> getRecommend(int type);

		Observable<JSONObject> orderMeeting(String id);

		Observable<JSONObject> cancelAdvance(String meetingId);
		Observable<JSONObject> getUnReadMessageCount();
	}
}

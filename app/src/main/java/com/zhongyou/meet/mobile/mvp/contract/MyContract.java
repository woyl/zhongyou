package com.zhongyou.meet.mobile.mvp.contract;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.zhongyou.meet.mobile.entiy.MyAllCourse;

import java.util.List;

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
public interface MyContract {
	//对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
	interface View extends IView {
		Activity getActivity();

		void getDataSuccess(MyAllCourse myAllCourse);

		void cancelAdvance(boolean isSuccess, List<MyAllCourse.CollectMeetingsBean> dataList, String id, int position);

		void unSignCourse(boolean isSuccess, List<MyAllCourse.SignUpSeriesBean> dataList, String id, int position);
		void showRedDot(boolean isShow,int count);
	}

	//Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
	interface Model extends IModel {
		Observable<JSONObject> getCollectMeeting(String userId);

		Observable<JSONObject> verfyRole(JSONObject jsonObject);

		Observable<JSONObject> joinMeeting(JSONObject jsonObject);

		Observable<JSONObject> getAgoraKey(String channel, String account, String role);

		Observable<JSONObject> siginCourse(String typeId, int isSign);

		Observable<JSONObject> cancelAdvance(String meetingId);

		Observable<JSONObject> getUnReadMessageCount();

	}
}

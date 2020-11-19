package com.zhongyou.meet.mobile.mvp.contract;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

import com.alibaba.fastjson.JSONObject;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.zhongyou.meet.mobile.entiy.MoreAudio;

import java.util.List;

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
public interface MakerCourseAudioContract {
	//对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
	interface View extends IView {
		void upDateAudioProgress(int posotion, long duration, long total);

		void pauseMusic(int position);

		void startMusic(int position);

		void completeMusic(int position);

//		void onServiceConnected(ComponentName name, IBinder binder);

		List<MoreAudio> getDataComplete(List<MoreAudio> dataLists);

		void getHeaderImageResult(String picUrl);

		Context getActivity();

		List<MoreAudio> getData();

		void setTotalPage(int totalPage);
	}

	//Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
	interface Model extends IModel {
		Observable<JSONObject> getMoreAudio(int pageNo);

		Observable<JSONObject> clickCourse(String id);
	}
}

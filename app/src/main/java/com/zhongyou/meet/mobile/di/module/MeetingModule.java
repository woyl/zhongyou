package com.zhongyou.meet.mobile.di.module;

import com.jess.arms.di.scope.FragmentScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.zhongyou.meet.mobile.mvp.contract.MeetingContract;
import com.zhongyou.meet.mobile.mvp.model.MeetingModel;


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
@Module
public abstract class MeetingModule {

	@Binds
	abstract MeetingContract.Model bindMeetingModel(MeetingModel model);
}
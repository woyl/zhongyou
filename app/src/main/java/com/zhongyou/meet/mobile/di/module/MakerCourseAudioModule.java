package com.zhongyou.meet.mobile.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioContract;
import com.zhongyou.meet.mobile.mvp.model.MakerCourseAudioModel;


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
@Module
public abstract class MakerCourseAudioModule {

	@Binds
	abstract MakerCourseAudioContract.Model bindMakerCourseAudioModel(MakerCourseAudioModel model);
}
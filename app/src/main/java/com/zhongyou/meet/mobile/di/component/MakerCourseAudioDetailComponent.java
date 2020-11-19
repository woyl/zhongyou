package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MakerCourseAudioDetailModule;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioDetailContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseAudioDetailActivity;

import dagger.BindsInstance;
import dagger.Component;


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
@Component(modules = {MakerCourseAudioDetailModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MakerCourseAudioDetailComponent {
	void inject(MakerCourseAudioDetailActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MakerCourseAudioDetailComponent.Builder view(MakerCourseAudioDetailContract.View view);

		MakerCourseAudioDetailComponent.Builder appComponent(AppComponent appComponent);

		MakerCourseAudioDetailComponent build();
	}
}
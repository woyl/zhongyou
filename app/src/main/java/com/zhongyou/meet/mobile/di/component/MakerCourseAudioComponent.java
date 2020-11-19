package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MakerCourseAudioModule;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseAudioActivity;

import dagger.BindsInstance;
import dagger.Component;


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
@Component(modules = {MakerCourseAudioModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MakerCourseAudioComponent {
	void inject(MakerCourseAudioActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MakerCourseAudioComponent.Builder view(MakerCourseAudioContract.View view);

		MakerCourseAudioComponent.Builder appComponent(AppComponent appComponent);

		MakerCourseAudioComponent build();
	}
}
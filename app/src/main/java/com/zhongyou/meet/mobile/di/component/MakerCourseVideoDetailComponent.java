package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MakerCourseVideoDetailModule;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseVideoDetailContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseVideoDetailActivity;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/07/2020 15:13
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = {MakerCourseVideoDetailModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MakerCourseVideoDetailComponent {
	void inject(MakerCourseVideoDetailActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MakerCourseVideoDetailComponent.Builder view(MakerCourseVideoDetailContract.View view);

		MakerCourseVideoDetailComponent.Builder appComponent(AppComponent appComponent);

		MakerCourseVideoDetailComponent build();
	}
}
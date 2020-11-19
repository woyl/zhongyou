package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MakerCourseMeetDetailModule;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseMeetDetailContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseMeetDetailActivity;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/17/2020 17:45
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = {MakerCourseMeetDetailModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MakerCourseMeetDetailComponent {
	void inject(MakerCourseMeetDetailActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MakerCourseMeetDetailComponent.Builder view(MakerCourseMeetDetailContract.View view);

		MakerCourseMeetDetailComponent.Builder appComponent(AppComponent appComponent);

		MakerCourseMeetDetailComponent build();
	}
}
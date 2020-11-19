package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MakerDetailModule;
import com.zhongyou.meet.mobile.mvp.contract.MakerDetailContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerDetailActivity;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/07/2020 10:28
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = {MakerDetailModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MakerDetailComponent {
	void inject(MakerDetailActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MakerDetailComponent.Builder view(MakerDetailContract.View view);

		MakerDetailComponent.Builder appComponent(AppComponent appComponent);

		MakerDetailComponent build();
	}
}
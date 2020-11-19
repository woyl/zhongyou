package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.SystemSettingModule;
import com.zhongyou.meet.mobile.mvp.contract.SystemSettingContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.SystemSettingActivity;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 11:12
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = {SystemSettingModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface SystemSettingComponent {
	void inject(SystemSettingActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		SystemSettingComponent.Builder view(SystemSettingContract.View view);

		SystemSettingComponent.Builder appComponent(AppComponent appComponent);

		SystemSettingComponent build();
	}
}
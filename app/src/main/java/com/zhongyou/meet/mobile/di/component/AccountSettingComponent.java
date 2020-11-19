package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.AccountSettingModule;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.mvp.contract.AccountSettingContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.AccountSettingActivity;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 12:36
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = {AccountSettingModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface AccountSettingComponent {
	void inject(AccountSettingActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		AccountSettingComponent.Builder view(AccountSettingContract.View view);

		AccountSettingComponent.Builder appComponent(AppComponent appComponent);

		AccountSettingComponent build();
	}
}
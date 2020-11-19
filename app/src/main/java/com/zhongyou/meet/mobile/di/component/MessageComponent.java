package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MessageModule;
import com.zhongyou.meet.mobile.mvp.contract.MessageContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageActivity;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 20:09
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = {MessageModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MessageComponent {
	void inject(MessageActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MessageComponent.Builder view(MessageContract.View view);

		MessageComponent.Builder appComponent(AppComponent appComponent);

		MessageComponent build();
	}
}
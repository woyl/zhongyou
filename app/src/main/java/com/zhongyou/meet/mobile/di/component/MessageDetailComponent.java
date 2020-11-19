package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MessageDetailModule;
import com.zhongyou.meet.mobile.mvp.contract.MessageDetailContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 21:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = {MessageDetailModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MessageDetailComponent {
	void inject(MessageDetailActivity activity);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MessageDetailComponent.Builder view(MessageDetailContract.View view);

		MessageDetailComponent.Builder appComponent(AppComponent appComponent);

		MessageDetailComponent build();
	}
}
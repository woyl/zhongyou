package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MeetingModule;
import com.zhongyou.meet.mobile.mvp.contract.MeetingContract;
import com.zhongyou.meet.mobile.mvp.ui.fragment.MeetingFragment;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 03/27/2020 15:18
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = {MeetingModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MeetingComponent {
	void inject(MeetingFragment fragment);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MeetingComponent.Builder view(MeetingContract.View view);

		MeetingComponent.Builder appComponent(AppComponent appComponent);

		MeetingComponent build();
	}
}
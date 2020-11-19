package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MakerModule;
import com.zhongyou.meet.mobile.mvp.contract.MakerContract;
import com.zhongyou.meet.mobile.mvp.ui.fragment.MakerFragment;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/05/2020 10:34
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = {MakerModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MakerComponent {
	void inject(MakerFragment fragment);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MakerComponent.Builder view(MakerContract.View view);

		MakerComponent.Builder appComponent(AppComponent appComponent);

		MakerComponent build();
	}
}
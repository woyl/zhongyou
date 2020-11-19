package com.zhongyou.meet.mobile.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.zhongyou.meet.mobile.di.module.HttpModule;
import com.zhongyou.meet.mobile.di.module.MyModule;
import com.zhongyou.meet.mobile.mvp.contract.MyContract;
import com.zhongyou.meet.mobile.mvp.ui.fragment.MyFragment;

import dagger.BindsInstance;
import dagger.Component;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/03/2020 23:02
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = {MyModule.class, HttpModule.class}, dependencies = AppComponent.class)
public interface MyComponent {
	void inject(MyFragment fragment);

	@Component.Builder
	interface Builder {
		@BindsInstance
		MyComponent.Builder view(MyContract.View view);

		MyComponent.Builder appComponent(AppComponent appComponent);

		MyComponent build();
	}
}
package com.zhongyou.meet.mobile.config;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.integration.ConfigModule;
import com.zhongyou.meet.mobile.Constant;

import java.util.List;

/**
 * @author luopan@centerm.com
 * @date 2019-11-20 14:58.
 */
public class GlobalConfiguration implements ConfigModule {
	@Override
	public void applyOptions(@NonNull Context context, @NonNull GlobalConfigModule.Builder builder) {
		//使用 builder 可以为框架配置一些配置信息

		builder.baseurl(Constant.getAPIHOSTURL())
				.globalHttpHandler(new GlobalHttpHandlerImpl(context));
	}

	@Override
	public void injectAppLifecycle(@NonNull Context context, @NonNull List<AppLifecycles> lifecycles) {
		//向 Application的 生命周期中注入一些自定义逻辑
		lifecycles.add(new AppLifecyclesImpl());
	}

	@Override
	public void injectActivityLifecycle(@NonNull Context context, @NonNull List<Application.ActivityLifecycleCallbacks> lifecycles) {
				//向 Activity 的生命周期中注入一些自定义逻辑
		lifecycles.add(new ActivityLifecycleCallbacksImpl());
	}

	@Override
	public void injectFragmentLifecycle(@NonNull Context context, @NonNull List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
			//向 Fragment 的生命周期中注入一些自定义逻辑
		lifecycles.add(new FragmentLifecycleCallbacksImpl());
	}
}

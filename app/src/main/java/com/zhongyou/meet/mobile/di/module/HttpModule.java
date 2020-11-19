package com.zhongyou.meet.mobile.di.module;

import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;

import dagger.Module;
import dagger.Provides;

/**
 * @author golangdorid@gmail.com
 * @date 2020/3/20 2:20 PM.
 * @
 */
@Module
public class HttpModule {
	@Provides
	public ApiService providerOkHttpClient() {
		return HttpsRequest.provideClientApi();
	}
}

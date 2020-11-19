package com.zhongyou.meet.mobile.di.module;

import com.jess.arms.di.scope.FragmentScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.zhongyou.meet.mobile.mvp.contract.MyContract;
import com.zhongyou.meet.mobile.mvp.model.MyModel;


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
@Module
public abstract class MyModule {

	@Binds
	abstract MyContract.Model bindMyModel(MyModel model);
}
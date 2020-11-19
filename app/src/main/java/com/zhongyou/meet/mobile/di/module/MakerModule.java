package com.zhongyou.meet.mobile.di.module;

import com.jess.arms.di.scope.FragmentScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.zhongyou.meet.mobile.mvp.contract.MakerContract;
import com.zhongyou.meet.mobile.mvp.model.MakerModel;


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
@Module
public abstract class MakerModule {

	@Binds
	abstract MakerContract.Model bindMakerModel(MakerModel model);
}
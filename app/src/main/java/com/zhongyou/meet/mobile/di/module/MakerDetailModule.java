package com.zhongyou.meet.mobile.di.module;

import com.zhongyou.meet.mobile.mvp.contract.MakerDetailContract;
import com.zhongyou.meet.mobile.mvp.model.MakerDetailModel;

import dagger.Binds;
import dagger.Module;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/07/2020 11:46
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class MakerDetailModule {

	@Binds
	abstract MakerDetailContract.Model bindMakerDetailModel(MakerDetailModel model);
}
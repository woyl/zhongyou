package com.zhongyou.meet.mobile.di.module

import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import com.zhongyou.meet.mobile.mvp.contract.NewMakerDetailContract
import com.zhongyou.meet.mobile.mvp.model.NewMakerDetailModel


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 09/29/2020 16:00
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
//构建NewMakerDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class NewMakerDetailModule(private val view: NewMakerDetailContract.View) {
    @ActivityScope
    @Provides
    fun provideNewMakerDetailView(): NewMakerDetailContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideNewMakerDetailModel(model: NewMakerDetailModel): NewMakerDetailContract.Model {
        return model
    }
}

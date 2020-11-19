package com.zhongyou.meet.mobile.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import com.zhongyou.meet.mobile.mvp.contract.NewMakerContract
import com.zhongyou.meet.mobile.mvp.model.NewMakerModel


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 09/25/2020 17:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
//构建NewMakerModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class NewMakerModule(private val view: NewMakerContract.View) {
    @FragmentScope
    @Provides
    fun provideNewMakerView(): NewMakerContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideNewMakerModel(model: NewMakerModel): NewMakerContract.Model {
        return model
    }
}

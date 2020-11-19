package com.zhongyou.meet.mobile.di.module

import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import com.zhongyou.meet.mobile.mvp.contract.MyClassRoomContract
import com.zhongyou.meet.mobile.mvp.model.MyClassRoomModel


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/03/2020 11:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
//构建MyClassRoomModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class MyClassRoomModule(private val view: MyClassRoomContract.View) {
    @ActivityScope
    @Provides
    fun provideMyClassRoomView(): MyClassRoomContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideMyClassRoomModel(model: MyClassRoomModel): MyClassRoomContract.Model {
        return model
    }
}

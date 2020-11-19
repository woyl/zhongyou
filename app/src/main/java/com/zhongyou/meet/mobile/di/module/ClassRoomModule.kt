package com.zhongyou.meet.mobile.di.module

import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import com.zhongyou.meet.mobile.mvp.contract.ClassRoomContract
import com.zhongyou.meet.mobile.mvp.model.ClassRoomModel


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/03/2020 15:58
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
//构建ClassRoomModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class ClassRoomModule(private val view: ClassRoomContract.View) {
    @ActivityScope
    @Provides
    fun provideClassRoomView(): ClassRoomContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideClassRoomModel(model: ClassRoomModel): ClassRoomContract.Model {
        return model
    }
}

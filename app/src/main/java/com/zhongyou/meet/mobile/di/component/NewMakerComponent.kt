package com.zhongyou.meet.mobile.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.zhongyou.meet.mobile.di.module.NewMakerModule

import com.zhongyou.meet.mobile.di.module.HttpModule
import com.jess.arms.di.scope.FragmentScope
import com.zhongyou.meet.mobile.mvp.ui.fragment.NewMakerFragment


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
@FragmentScope
@Component(modules = arrayOf(NewMakerModule::class, HttpModule::class), dependencies = arrayOf(AppComponent::class))
interface NewMakerComponent {
    fun inject(fragment: NewMakerFragment)
}

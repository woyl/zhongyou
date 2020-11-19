package com.zhongyou.meet.mobile.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.zhongyou.meet.mobile.di.module.ChairManWithKotlinModule

import com.zhongyou.meet.mobile.di.module.HttpModule
import com.jess.arms.di.scope.ActivityScope
import com.zhongyou.meet.mobile.mvp.ui.activity.ChairManWithKotlinActivity


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 05/20/2020 13:53
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = arrayOf(ChairManWithKotlinModule::class, HttpModule::class), dependencies = arrayOf(AppComponent::class))
interface ChairManWithKotlinComponent {
    fun inject(activity: ChairManWithKotlinActivity)
}

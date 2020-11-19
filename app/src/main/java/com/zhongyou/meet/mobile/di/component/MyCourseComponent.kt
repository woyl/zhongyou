package com.zhongyou.meet.mobile.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.zhongyou.meet.mobile.di.module.MyCourseModule

import com.jess.arms.di.scope.ActivityScope
import com.zhongyou.meet.mobile.mvp.ui.activity.MyCourseActivity


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/03/2020 11:00
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = arrayOf(MyCourseModule::class), dependencies = arrayOf(AppComponent::class))
interface MyCourseComponent {
    fun inject(activity: MyCourseActivity)
}

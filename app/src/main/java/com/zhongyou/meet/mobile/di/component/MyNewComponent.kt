package com.zhongyou.meet.mobile.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.zhongyou.meet.mobile.di.module.MyNewModule

import com.jess.arms.di.scope.ActivityScope
import com.zhongyou.meet.mobile.mvp.ui.fragment.MyNewFragment


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/02/2020 11:31
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = arrayOf(MyNewModule::class), dependencies = arrayOf(AppComponent::class))
interface MyNewComponent {
    fun inject(fragment: MyNewFragment)
}

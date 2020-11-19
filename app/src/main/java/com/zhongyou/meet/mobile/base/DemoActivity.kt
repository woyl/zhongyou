package com.zhongyou.meet.mobile.base

import com.zhongyou.meet.mobile.BR
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.databinding.ActivityDemoBinding

class DemoActivity : BaseMVVMActivity<ActivityDemoBinding, DemoViewModel>() {
    private lateinit var demoViewModel:DemoViewModel

    
    override fun initData() {
        demoViewModel.getData()

    }

    override fun getLayoutId(): Int = R.layout.activity_demo

    override fun initViewModel(): DemoViewModel {
        demoViewModel=createViewModel(this,DemoViewModel::class.java)
        return demoViewModel
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }
}
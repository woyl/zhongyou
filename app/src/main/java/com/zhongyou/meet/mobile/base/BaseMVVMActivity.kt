package com.zhongyou.meet.mobile.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions
import com.zhongyou.meet.mobile.ameeting.network.ApiService
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/1 1:09 PM.
 * @
 */
abstract class BaseMVVMActivity<V : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(), AnkoLogger {


    protected lateinit var mBinding: V

    protected var viewModelId = 0

    lateinit var viewModel: VM

    protected val mApiService: ApiService by lazy {
        HttpsRequest.provideClientApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMVVM()
        initViewObservable()
        initData()
        ActivityCollector.addActivity(this)
    }

    abstract fun initData()

    private fun initMVVM() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())

        viewModelId = initVariableId()
        viewModel = initViewModel()
        //关联ViewModel
        mBinding.setVariable(viewModelId, viewModel)
        //支持LiveData绑定xml，数据改变，UI自动会更新
        mBinding.executePendingBindings()
        mBinding.lifecycleOwner = this
        //让ViewModel拥有View的生命周期感应
//        lifecycle.addObserver(MyObserver())
    }

    abstract fun getLayoutId(): Int

    abstract fun initViewModel(): VM

    abstract fun initVariableId(): Int

    open fun initViewObservable() {

    }

    open fun showMessage(msg: String) {
        toast(msg)
    }

    open fun launchActivity(intent: Intent) {
        Preconditions.checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    open fun <T : ViewModel> createViewModel(activity: AppCompatActivity, cls: Class<T>): T {
        return ViewModelProviders.of(activity)[cls]
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
        viewModel.onCleared()
        ActivityCollector.removeActivity(this)

    }
}
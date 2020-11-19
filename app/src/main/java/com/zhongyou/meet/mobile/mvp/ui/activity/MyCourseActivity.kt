package com.zhongyou.meet.mobile.mvp.ui.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.RxBus

import com.zhongyou.meet.mobile.di.component.DaggerMyCourseComponent
import com.zhongyou.meet.mobile.di.module.MyCourseModule
import com.zhongyou.meet.mobile.mvp.contract.MyCourseContract
import com.zhongyou.meet.mobile.mvp.presenter.MyCoursePresenter

import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.adapter.MakerAdapter
import com.zhongyou.meet.mobile.entiy.MyAllCourse
import com.zhongyou.meet.mobile.event.CollectionHomeEvent
import com.zhongyou.meet.mobile.event.CollectionOtherEvent
import com.zhongyou.meet.mobile.mine.fragment.CollectionFragment
import com.zhongyou.meet.mobile.mine.fragment.PurchasedFragment
import com.zhongyou.meet.mobile.utils.TabViewUtils
import com.zhongyou.meet.mobile.utils.ToastUtils
import com.zhongyou.meet.mobile.utils.listener.MonitorThreeListener
import kotlinx.android.synthetic.main.activity_my_course.*
import kotlinx.android.synthetic.main.activity_my_course.tabLayout
import kotlinx.android.synthetic.main.activity_my_course.view_page2
import kotlinx.android.synthetic.main.fragment_class_room.*
import kotlinx.android.synthetic.main.include_base_title_tool_bar.*


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
/**
 * 如果没presenter
 * 你可以这样写
 *
 * @ActivityScope(請注意命名空間) class NullObjectPresenterByActivity
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class MyCourseActivity : BaseActivity<MyCoursePresenter>(), MyCourseContract.View {

    private var collectionFragment: CollectionFragment? = null
    private var purchasedFragment: PurchasedFragment? = null
    private var fragments = mutableListOf<Fragment>()

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerMyCourseComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myCourseModule(MyCourseModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_my_course //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {

        title = "我的课程"
        collectionFragment = CollectionFragment.newInstance()
        purchasedFragment = PurchasedFragment.newInstance()
        collectionFragment?.let {
            fragments.add(it)
            it.setPrasenterOne(mPresenter)
            it.setMonitor(object : MonitorThreeListener<String, Int, Int> {
                override fun OnMonitor(k: String, v: Int, t: Int) {
                    mPresenter?.signCourse(k, v, t,0)
                }
            })
        }
        purchasedFragment?.let {
            fragments.add(it)
            it.setPrasenterOne(mPresenter)
            it.setMonitor(object : MonitorThreeListener<String, Int, Int> {
                override fun OnMonitor(k: String, v: Int, t: Int) {
                    mPresenter?.signCourse(k, v, t,1)
                }
            })
        }
        view_page2.adapter = MakerAdapter(this, fragments)
        view_page2.offscreenPageLimit = 2
        TabLayoutMediator(tabLayout, view_page2, true, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            if (position == 0) {
                tab.text =  Html.fromHtml("<strong>收藏<strong>")
            } else {
                tab.text =  Html.fromHtml("<strong>已购<strong>")
            }

        }).attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                TabViewUtils.checkTab(tab, false,resources)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                TabViewUtils.checkTab(tab, true,resources)
            }

        })
        TabViewUtils.checkTab(tabLayout.getTabAt(0), true,resources)
    }


    override fun getDataSuccess(myAllCourse: MyAllCourse?,type:Int) {
        if (type == 0) {
            collectionFragment?.setData(myAllCourse)
        } else {
            purchasedFragment?.setData(myAllCourse)
        }


    }

    override fun signSuccess(isSuccess: Boolean, sign: Int, message: String?, position: Int,typeFragment:Int) {
        if (isSuccess) {
            if ("报名成功" == message) {
                ToastUtils.showToast("收藏成功")
            } else {
                ToastUtils.showToast("取消收藏")
            }
        }
//        collectionFragment?.notifyItemData(position)
//        purchasedFragment?.notifyItemData(position)
//        collectionFragment?.refreshData()

        if (typeFragment == 0) {
            collectionFragment?.reMoveDatas(position)
        } else {
            purchasedFragment?.refreshData()
        }



        RxBus.getDefault().send(CollectionHomeEvent(true))
        RxBus.getDefault().send(CollectionOtherEvent(true))
    }


    override fun showLoading() {

    }

    override fun hideLoading() {
        collectionFragment?.finshRefush()
        purchasedFragment?.finshRefush()
    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }

}

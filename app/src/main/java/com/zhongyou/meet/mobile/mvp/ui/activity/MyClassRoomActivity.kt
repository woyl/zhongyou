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
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader

import com.zhongyou.meet.mobile.di.component.DaggerMyClassRoomComponent
import com.zhongyou.meet.mobile.di.module.MyClassRoomModule
import com.zhongyou.meet.mobile.mvp.contract.MyClassRoomContract
import com.zhongyou.meet.mobile.mvp.presenter.MyClassRoomPresenter

import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.adapter.MakerAdapter
import com.zhongyou.meet.mobile.entiy.MyAllCourse
import com.zhongyou.meet.mobile.mine.fragment.CloudClassRoom
import com.zhongyou.meet.mobile.mine.fragment.LiveClassFragment
import com.zhongyou.meet.mobile.utils.TabViewUtils
import kotlinx.android.synthetic.main.activity_my_class_room.*
import kotlinx.android.synthetic.main.activity_my_class_room.tabLayout
import kotlinx.android.synthetic.main.activity_my_class_room.view_page2
import kotlinx.android.synthetic.main.activity_my_course.*


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
class MyClassRoomActivity : BaseActivity<MyClassRoomPresenter>(), MyClassRoomContract.View {
    private var liveClassFragment: LiveClassFragment?= null
    private var cloudClassRoom:CloudClassRoom?= null
    private var fragments= mutableListOf<Fragment>()

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerMyClassRoomComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myClassRoomModule(MyClassRoomModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_my_class_room //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {
        title = "我的教室"
        liveClassFragment = LiveClassFragment.newInstance()
        cloudClassRoom = CloudClassRoom.newInstance()
        liveClassFragment?.let {
            fragments.add(it)
            it.setPrasenterOne(mPresenter)
        }
        cloudClassRoom?.let {
            fragments.add(it)
            it.setPrasenterOne(mPresenter)
        }

        val fragmentAdapter = MakerAdapter(this,fragments)
        view_page2.adapter = fragmentAdapter
        view_page2.offscreenPageLimit = 2
        TabLayoutMediator(tabLayout,view_page2,true, TabLayoutMediator.TabConfigurationStrategy {
            tab, position ->
            if (position == 0) {
                tab.text = Html.fromHtml("<strong>直播课<strong>")
            } else {
                tab.text =Html.fromHtml("<strong>云教室<strong>")
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


        smart_refresh?.setRefreshHeader(ClassicsHeader(this))
        smart_refresh?.setRefreshFooter(ClassicsFooter(this))
        smart_refresh?.setEnableLoadMore(false)
        smart_refresh?.setOnRefreshListener {
            if (view_page2.currentItem == 0) {
                (fragments[view_page2.currentItem] as LiveClassFragment).onRefresh(it)
            } else {
                (fragments[view_page2.currentItem] as CloudClassRoom).onRefresh(it)
            }
        }
    }

    override fun getDataSuccess(myAllCourse: MyAllCourse?) {

        liveClassFragment?.setData(myAllCourse)
        cloudClassRoom?.setData(myAllCourse)
        smart_refresh?.finishRefresh()
    }

    override fun cancelSubcribeMeeting(isSuccess: Boolean, meetingId: String, isClassRoom: Boolean) {
         if (isClassRoom){
             cloudClassRoom?.cancelSubcribeMeeting(isSuccess, meetingId)
         }else{
             liveClassFragment?.cancelSubcribeMeeting(isSuccess, meetingId)
         }
    }
    override fun showLoading() {

    }

    override fun hideLoading() {
        smart_refresh?.finishRefresh()
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

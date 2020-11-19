package com.zhongyou.meet.mobile.mvp.ui.fragment

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.RxBus
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.adapter.MakerAdapter
import com.zhongyou.meet.mobile.business.MeetingSearchActivity
import com.zhongyou.meet.mobile.business.MeetingsFragment
import com.zhongyou.meet.mobile.core.JoinMeetingDialog.showQuickJoinMeeting
import com.zhongyou.meet.mobile.core.MusicManager
import com.zhongyou.meet.mobile.core.PlayService
import com.zhongyou.meet.mobile.di.component.DaggerNewMakerComponent
import com.zhongyou.meet.mobile.di.module.NewMakerModule
import com.zhongyou.meet.mobile.entities.Data
import com.zhongyou.meet.mobile.entiy.MakerColumn
import com.zhongyou.meet.mobile.entiy.MoreAudio
import com.zhongyou.meet.mobile.entiy.RecomandData
import com.zhongyou.meet.mobile.event.CollectionHomeEvent
import com.zhongyou.meet.mobile.event.CollectionOtherEvent
import com.zhongyou.meet.mobile.event.LikeEvent
import com.zhongyou.meet.mobile.event.RefreshHomeEvent
import com.zhongyou.meet.mobile.mvp.contract.NewMakerContract
import com.zhongyou.meet.mobile.mvp.presenter.NewMakerPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity
import com.zhongyou.meet.mobile.utils.*
import com.zhongyou.meet.mobile.utils.listener.MonitorThreeListener
import kotlinx.android.synthetic.main.fragment_new_maker.*
import kotlinx.android.synthetic.main.meeting_fragment_head.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.collections.ArrayList


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
/**
 * 如果没presenter
 * 你可以这样写
 *
 * @FragmentScope(請注意命名空間) class NullObjectPresenterByFragment
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class NewMakerFragment : BaseFragment<NewMakerPresenter>(), NewMakerContract.View {


    /**
     * 页面类型 1：详情页面；2：直播；3 视频；4：音频
     * */

    private var mFragments = mutableListOf<Fragment>()
    private var makerFragment: NewMakerCourseFragment? = null
    private var makerOtherFragment : NewMakerCourseOtherFragment?= null
    private var mTableLists = mutableListOf<String>()
    private var ids = mutableListOf<String>()
    private var listContents = mutableListOf<MakerColumn.SeriesBean.ListBean>()
    private var scope = CoroutineScope(Dispatchers.Main)
    private var isRefush = false
    private val mAudioList: List<MoreAudio> = ArrayList()
    private var dataContent: MutableList<Data>?= null

    companion object {
        fun newInstance(): NewMakerFragment {
            return NewMakerFragment()
        }
    }


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerNewMakerComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newMakerModule(NewMakerModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_new_maker, container, false);
    }

    private fun setStatusHeight(height : Int) {
        val layoutParams = base_head.layoutParams as LinearLayout.LayoutParams
        layoutParams.setMargins(0,StatusBarUtils.getStatusBarHeight(mContext) + height,0,0)
        base_head.layoutParams = layoutParams
    }

    override fun initData(savedInstanceState: Bundle?) {
        if (StatusBarUtils.hasNotchInScreen(activity)) {
            setStatusHeight(StatusBarUtils.getNotchSizeAtHuawei(mContext))
        }
        makerFragment = NewMakerCourseFragment.newInstanse()

        initDataHome()

        tv_fails.setOnClickListener {
            isRefush = true
            initDataHome()
        }

        icon_notify.setOnClickListener {
            launchActivity(Intent(context, MessageDetailActivity::class.java))
        }

        inputCode.setOnClickListener {
            if (mPresenter != null) {
                if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
                    mPresenter?.showPhoneStatusDialog(1)
                    return@setOnClickListener
                }
                if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
                    mPresenter?.showPhoneStatusDialog(2)
                    return@setOnClickListener
                }
                if (activity != null) {
                    showQuickJoinMeeting((activity as AppCompatActivity?)!!)
                }
            }
        }



        txt_search.setOnClickListener {
            val searchMeetingIntent = Intent(mContext, MeetingSearchActivity::class.java)
            searchMeetingIntent.putExtra(MeetingsFragment.KEY_MEETING_TYPE, 0)
            launchActivity(searchMeetingIntent)
        }

        setSearchDrawable()

        RxBus.handleMessage { o ->
            if (o is LikeEvent) {
                Timber.e("o--LikeEvent-->%s", JSON.toJSONString(o))
                if ((o as LikeEvent).isLike) {
                    if (mPresenter != null) {
                        mPresenter?.getUnReadMessage()
                    }
                }
            } else if(o is RefreshHomeEvent) {
                if (o.isTrue) {
                    isRefush = true
                    mPresenter?.getRecommendData()
                    mPresenter?.getHomePage()
                    mPresenter?.getUnReadMessage()
                }
            }
        }

    }

    private fun setSearchDrawable() {
        val drawable = resources.getDrawable(R.mipmap.search)
        drawable.setBounds(0,0,drawable.minimumWidth,drawable.minimumHeight)
        val text = "span 请输入教室名称"
        val spannableString = SpannableString(text)
        val imageSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(drawable,ImageSpan.ALIGN_CENTER)
        } else {
            ImageSpan(drawable,ImageSpan.ALIGN_BOTTOM)
        }
        spannableString.setSpan(imageSpan,0,"span".length,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        txt_search.text = spannableString
    }

    private fun initDataHome() {
//        scope.launch {
//            async {
//
//            }
//        }
        qustData()
    }

    private fun qustData(): Any? {
        //tab
        mPresenter?.getMakerColumn()
        //未读
        mPresenter?.getUnReadMessage()

        return null
    }

    /**
     * 通过此方法可以使 Fragment 能够与外界做一些交互和通信, 比如说外部的 Activity 想让自己持有的某个 Fragment 对象执行一些方法,
     * 建议在有多个需要与外界交互的方法时, 统一传 {@link Message}, 通过 what 字段来区分不同的方法, 在 {@link #setData(Object)}
     * 方法中就可以 {@code switch} 做不同的操作, 这样就可以用统一的入口方法做多个不同的操作, 可以起到分发的作用
     * <p>
     * 调用此方法时请注意调用时 Fragment 的生命周期, 如果调用 {@link #setData(Object)} 方法时 {@link Fragment#onCreate(Bundle)} 还没执行
     * 但在 {@link #setData(Object)} 里却调用了 Presenter 的方法, 是会报空的, 因为 Dagger 注入是在 {@link Fragment#onCreate(Bundle)} 方法中执行的
     * 然后才创建的 Presenter, 如果要做一些初始化操作,可以不必让外部调用 {@link #setData(Object)}, 在 {@link #initData(Bundle)} 中初始化就可以了
     * <p>
     * Example usage:
     * <pre>
     *fun setData(data:Any) {
     *   if(data is Message){
     *       when (data.what) {
     *           0-> {
     *               //根据what 做你想做的事情
     *           }
     *           else -> {
     *           }
     *       }
     *   }
     *}
     *
     *
     *
     *
     *
     * // call setData(Object):
     * val data = Message();
     * data.what = 0;
     * data.arg1 = 1;
     * fragment.setData(data);
     * </pre>
     *
     * @param data 当不需要参数时 {@code data} 可以为 {@code null}
     */
    override fun setData(data: Any?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }

    override fun getBannerData(bannerData: RecomandData) {
        makerFragment?.setBannerData(bannerData)
    }

    /**这里是首次请求出一条音频数据*/
    override fun getAudioDataSuccess(data: RecomandData.ChangeLessonByDayBean?) {
        data?.let {
            makerFragment?.setAutoUnscramble(it,mPresenter)
        }
    }

    override fun getHomepage(data: MutableList<Data>?) {
        dataContent = data
        makerFragment?.setHomePageBean(data)
    }

    override fun getMakerColumn(column: MakerColumn?, isSuccess: Boolean) {

        val result = column?.apply {
            if (isRefush) {
                mTableLists.clear()
                ids.clear()
                mFragments.clear()
            }
            mTableLists.add("推荐")
            ids.add("0")
            for (its in this.columns) {
                mTableLists.add(its.typeName)
                ids.add(its.id)
            }
            listContents.addAll(column.series.list)
            mTableLists.let {
                for (its in mTableLists.indices) {
                    if (its == 0) {
                        mFragments.add(makerFragment!!)
                    } else {
                        makerOtherFragment = NewMakerCourseOtherFragment.newInstanse(ids[its], mFragments.size -1, column.series.totalPage.toString())
                        makerOtherFragment?.setPresenter(mPresenter)
                        makerOtherFragment?.let { it1 ->
                            mFragments.add(it1)
                            it1.setMonitor(object :MonitorThreeListener<String,Int,Int> {
                                override fun OnMonitor(k: String, v: Int, t: Int) {
                                    mPresenter?.signCourse(k,v,t,0,1)
                                }
                            })
                        }
                    }
                }
                if (activity != null) {
                    view_page2.adapter = MakerAdapter(activity!!, mFragments)
                    view_page2.offscreenPageLimit = mTableLists.size
                    TabLayoutMediator(tabLayout, view_page2, true, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        tab.text = Html.fromHtml("<strong>${mTableLists[position]}<strong>")
                    }).attach()

                    tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
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
            }
        }

        if (result != null) {
            tv_fails.visibility = View.GONE
            view_page2.visibility = View.VISIBLE
            tabLayout.visibility = View.VISIBLE
            //banner today
            mPresenter?.getRecommendData()
            //content
            mPresenter?.getHomePage()
        } else {
            tv_fails.visibility = View.VISIBLE
            view_page2.visibility = View.GONE
            tabLayout.visibility = View.GONE
        }
    }

    override fun showRedDot(isShow: Boolean, count: Int) {
        red_dot.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    /**这里加载更过的音频数据*/
    override fun getDataComplete(dataLists: List<MoreAudio>?): List<MoreAudio?>? {
        makerFragment?.setMoreAudio(dataLists)
        MusicManager.instance.addMusics(dataLists as MutableList<MoreAudio>?)
        return mAudioList
    }

    override fun signSuccess(isSuccess: Boolean, sign: Int, message: String?,position:Int,positionItem:Int,typeFragment:Int) {
        if (isSuccess) {
            if ("报名成功" == message) {
                ToastUtils.showToast("收藏成功")
                if (typeFragment == 0) {
                    RxBus.getDefault().send(CollectionOtherEvent(true))
                    dataContent?.let {
                        it[position].let { data ->
                            if (data.homeLinkPages.isNotEmpty()) {
                                data.homeLinkPages[positionItem].isSign = 1
                                makerFragment?.getlinearListAdapter()?.getContentItemAdapter()?.notifyItemChanged(positionItem)
                            }
                        }
                    }

                } else {
                    RxBus.getDefault().send(CollectionHomeEvent(true))
                    makerOtherFragment?.notifyItemData(position)
                }

            } else{
                if (typeFragment == 0) {
                    RxBus.getDefault().send(CollectionOtherEvent(true))
                    dataContent?.let {
                        it[position].let { data ->
                            if (data.homeLinkPages.isNotEmpty()) {
                                data.homeLinkPages[positionItem].isSign = 0
                                makerFragment?.getlinearListAdapter()?.getContentItemAdapter()?.notifyItemChanged(positionItem)
                            }
                        }
                    }
                } else {
                    RxBus.getDefault().send(CollectionHomeEvent(true))
                    makerOtherFragment?.notifyItemData(position)
                }

                ToastUtils.showToast("取消收藏")
            }
        }
    }

    fun setMusicBinder(play: PlayService.MusicBinder) {
        makerFragment?.setMusicBinder(play)
    }

    override fun onResume() {
        super.onResume()
        if (mPresenter != null) {
            mPresenter?.getUnReadMessage()
        }
    }

}

package com.zhongyou.meet.mobile.mvp.ui.fragment

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.RxBus
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.BaseApplication
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.IM.IMInfoProvider
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.adapter.MakerAdapter
import com.zhongyou.meet.mobile.business.MeetingSearchActivity
import com.zhongyou.meet.mobile.core.JoinMeetingDialog.showQuickJoinMeeting
import com.zhongyou.meet.mobile.di.component.DaggerClassRoomComponent
import com.zhongyou.meet.mobile.di.module.ClassRoomModule
import com.zhongyou.meet.mobile.entities.Agora
import com.zhongyou.meet.mobile.entiy.MeetingJoin
import com.zhongyou.meet.mobile.entiy.MeetingsData
import com.zhongyou.meet.mobile.event.LikeEvent
import com.zhongyou.meet.mobile.mine.fragment.ClassRoomCloundFragment
import com.zhongyou.meet.mobile.mine.fragment.ClassRoomLiveClassFragment
import com.zhongyou.meet.mobile.mvp.contract.ClassRoomContract
import com.zhongyou.meet.mobile.mvp.presenter.ClassRoomPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity
import com.zhongyou.meet.mobile.persistence.Preferences
import com.zhongyou.meet.mobile.utils.*
import io.agora.openlive.ui.MeetingInitActivity
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_new_maker_detail.*
import kotlinx.android.synthetic.main.fragment_class_room.tabLayout
import kotlinx.android.synthetic.main.fragment_class_room.view_page2
import kotlinx.android.synthetic.main.meeting_fragment_head.*


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
class ClassRoomFragment : BaseFragment<ClassRoomPresenter>(), ClassRoomContract.View {

    private var classRoomLiveFragment: ClassRoomLiveClassFragment? = null
    private var classRoomCloundFragment: ClassRoomCloundFragment? = null
    private val fragments = mutableListOf<Fragment>()
    private val mCurrentMeetingType = 0
    private var meetingsLiveData: MeetingsData? = null
    private var meetingsCloudData: MeetingsData? = null

    companion object {
        fun newInstance(): ClassRoomFragment {
            return ClassRoomFragment()
        }
    }


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerClassRoomComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .classRoomModule(ClassRoomModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_class_room, container, false);
    }

    private fun setStatusHeight() {
        val layoutParams = base_head.layoutParams as LinearLayout.LayoutParams
        layoutParams.setMargins(0, StatusBarUtils.getStatusBarHeight(mContext) + StatusBarUtils.getNotchSizeAtHuawei(mContext), 0, 0)
        base_head.layoutParams = layoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //????
        setInfo()
    }

    override fun initData(savedInstanceState: Bundle?) {
        if (StatusBarUtils.hasNotchInScreen(activity)) {
            setStatusHeight()
        }
        classRoomCloundFragment = ClassRoomCloundFragment.newInstance()
        classRoomLiveFragment = ClassRoomLiveClassFragment.newInstance()
        classRoomLiveFragment?.let {
            fragments.add(it)
            it.setPresenter(mPresenter)
        }
        classRoomCloundFragment?.let {
            fragments.add(it)
            it.setPresenter(mPresenter)
        }
        view_page2.adapter = activity?.let { MakerAdapter(it, fragments) }
        view_page2.offscreenPageLimit = 2
        TabLayoutMediator(tabLayout, view_page2, true, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            if (position == 0) {
                tab.text = Html.fromHtml("<strong>直播课<strong>")
            } else {
                tab.text = Html.fromHtml("<strong>云教室<strong>")
            }

        }).attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
        setClick()

        setSearchDrawable()
        RxBus.handleMessage { o ->
            if (o is LikeEvent) {
                if ((o as LikeEvent).isLike) {
                    if (mPresenter != null) {
                        mPresenter?.getUnReadMessage()
                    }
                }
            }
        }

    }

    private fun setSearchDrawable() {
        val drawable = resources.getDrawable(R.mipmap.search)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        val text = "span 请输入教室名称"
        val spannableString = SpannableString(text)
        val imageSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(drawable, ImageSpan.ALIGN_CENTER)
        } else {
            ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM)
        }
        spannableString.setSpan(imageSpan, 0, "span".length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        txt_search.text = spannableString
    }

    private fun setInfo() {
        val infoProvider = IMInfoProvider()
        infoProvider.init(BaseApplication.getInstance())

        val info = UserInfo(MMKV.defaultMMKV().decodeString(MMKVHelper.ID), MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME), Uri.parse(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO)))

        RongIM.getInstance().refreshUserInfoCache(info)
        RongIM.getInstance().setCurrentUserInfo(info)
        RongIM.getInstance().setMessageAttachedUserInfo(true)

        if (!TextUtils.isEmpty(Constant.KEY_meetingID) || !TextUtils.isEmpty(Constant.KEY_code)) {
            val listBean = MeetingsData.DataBean.ListBean()
            listBean.id = Constant.KEY_meetingID
            val params = JSONObject()
            params["clientUid"] = UIDUtil.generatorUID(Preferences.getUserId())
            params["meetingId"] = Constant.KEY_meetingID
            params["token"] = Constant.KEY_code
            //如果需要打开app就进入会议 就放开这个
            mPresenter?.verifyRole(params, listBean, Constant.KEY_code)
        }
    }

    private fun setClick() {
        txt_search.setOnClickListener {
            val searchMeetingIntent = Intent(mContext, MeetingSearchActivity::class.java)
            searchMeetingIntent.putExtra(MeetingFragment.KEY_MEETING_TYPE, mCurrentMeetingType)
            launchActivity(searchMeetingIntent)
        }
        icon_notify.setOnClickListener {
            launchActivity(Intent(context, MessageDetailActivity::class.java))
        }
        inputCode.setOnClickListener {
            if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
                mPresenter!!.showPhoneStatusDialog(1, mContext)
                return@setOnClickListener
            }
            if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
                mPresenter!!.showPhoneStatusDialog(2, mContext)
                return@setOnClickListener
            }
            showQuickJoinMeeting(((activity as AppCompatActivity?)!!))
        }
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

    override fun showEmptyView(isShow: Boolean,isMeeting: String) {
        if (isShow) {
            if (TextUtils.equals("1",isMeeting)){
                classRoomCloundFragment?.getDataNull()
            } else {
                classRoomLiveFragment?.getDataNull()
            }
        }
    }

    override fun showMeetings(meetingsData: MeetingsData?, isMeeting: String) {
        if (TextUtils.equals("1", isMeeting)) {
            classRoomCloundFragment?.getSmallRefrush()?.finishRefresh()
            classRoomCloundFragment?.getSmallRefrush()?.finishLoadMore()
            meetingsCloudData = meetingsData
        } else {
            classRoomLiveFragment?.getSmallRefrush()?.finishRefresh()
            classRoomLiveFragment?.getSmallRefrush()?.finishLoadMore()
            meetingsLiveData = meetingsData
        }



        meetingsData?.let {
            if (TextUtils.equals("1", isMeeting)) {
                classRoomCloundFragment?.setData(it)
            } else {
                classRoomLiveFragment?.setData(it)
            }
        }
    }

    override fun getAgoraKey(type: Int, jsonObject: JSONObject?, join: MeetingJoin?, joinRole: Int) {
        val agora = Agora()
        agora.appID = jsonObject?.getJSONObject("data")?.getString("appID")
        agora.isTest = jsonObject?.getJSONObject("data")?.getString("isTest")
        agora.signalingKey = jsonObject?.getJSONObject("data")?.getString("signalingKey")
        agora.token = jsonObject?.getJSONObject("data")?.getString("token")

        val intent = Intent(mContext, MeetingInitActivity::class.java)
        BaseApplication.getInstance().agora = agora
        intent.putExtra("agora", agora)
        intent.putExtra("isMaker", type != 1)
        intent.putExtra("meeting", join?.data)
        intent.putExtra("role", joinRole)
        launchActivity(intent)
    }

    override fun verifyRole(jsonObject: JSONObject?, meeting: MeetingsData.DataBean.ListBean?, code: String?) {
        Constant.KEY_meetingID = ""
        Constant.KEY_code = ""

        //需要录制

        //需要录制
        Constant.isNeedRecord = meeting?.isRecord == 1

        meeting?.resolvingPower?.let { MMKV.defaultMMKV().encode(MMKVHelper.KEY_meetingQuilty, it) }
        val params = JSONObject()
        params["clientUid"] = UIDUtil.generatorUID(Preferences.getUserId())
        params["meetingId"] = meeting?.id
        params["token"] = code
        mPresenter?.joinMeeting(params, meeting, code)
    }

    override fun joinMeeting(jsonObject: JSONObject?, meeting: MeetingsData.DataBean.ListBean?, code: String?) {
        val meetingJoin = JSON.parseObject(jsonObject?.toJSONString(), MeetingJoin::class.java)
        if (meetingJoin == null || meetingJoin.data == null || TextUtils.isEmpty(meetingJoin.data.meeting.id)) {
            showMessage("出错了 请重试")
            return
        }
        MMKV.defaultMMKV().encode(meeting?.id, code)

        Constant.isNeedRecord = meetingJoin.data.meeting.isRecord == 1
        val mJoinRole = meetingJoin.data.role
        var role = "Subscriber"
        when (mJoinRole) {
            0 -> {
                role = "Publisher"
            }
            1 -> {
                role = "Publisher"
            }
            2 -> {
                role = "Subscriber"
            }
        }
        meeting?.isMeeting?.let { mPresenter?.getAgoraKey(it, meetingJoin.data.meeting.id, UIDUtil.generatorUID(Preferences.getUserId()), role, mJoinRole, meetingJoin) }
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.getUnReadMessage()
    }

    override fun showRedDot(isShow: Boolean, count: Int) {
        red_dot?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun subscribeMeetingSuccess(message: Boolean, position: Int,isMeeting:Int) {
        if (message) {
            if (isMeeting == 1) {
                ToastUtils.showToast("会议提醒成功")
                meetingsCloudData?.let {
                    it.data?.let { data ->
                        data.list?.let { list ->
                            if (list.size > 0) {
                                list[position].isCollection = 1
                                classRoomCloundFragment?.getCloudAdapter()?.notifyItemChanged(position)
                            }
                        }
                    }
                }
            } else {
                ToastUtils.showToast("开课提醒成功")
                meetingsLiveData?.let {
                    it.data?.let { data ->
                        data.list?.let { list ->
                            if (list.size > 0) {
                                list[position].isCollection = 1
                                classRoomLiveFragment?.getLiveAdapter()?.notifyItemChanged(position)
                            }
                        }
                    }
                }
            }

        }
    }

    override fun cancelSubcribeMeeting(message: Boolean, position: Int,isMeeting:Int) {
        if (message) {

            if (isMeeting == 1) {
                ToastUtils.showToast("取消会议提醒")
                meetingsCloudData?.let {
                    it.data?.let { data ->
                        data.list?.let { list ->
                            if (list.size > 0) {
                                list[position].isCollection = 0
                                classRoomCloundFragment?.getCloudAdapter()?.notifyItemChanged(position)

                            }
                        }
                    }
                }
            } else {
                ToastUtils.showToast("取消开课提醒")
                meetingsLiveData?.let {
                    it.data?.let { data ->
                        data.list?.let { list ->
                            if (list.size > 0) {
                                list[position].isCollection = 0
                                classRoomLiveFragment?.getLiveAdapter()?.notifyItemChanged(position)
                            }
                        }
                    }
                }
            }

        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {
        classRoomLiveFragment?.getSmallRefrush()?.finishRefresh()
        classRoomCloundFragment?.getSmallRefrush()?.finishRefresh()
        classRoomLiveFragment?.getSmallRefrush()?.finishLoadMore()
        classRoomCloundFragment?.getSmallRefrush()?.finishLoadMore()
        mPresenter?.getUnReadMessage()
    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }

    override fun onRestartUI() {
        //先都刷新一次
        if(classRoomLiveFragment?.isAdded == true)
            classRoomLiveFragment?.onRestartUI()
        if(classRoomCloundFragment?.isAdded == true)
            classRoomCloundFragment?.onRestartUI()
    }
}

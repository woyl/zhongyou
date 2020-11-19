package com.zhongyou.meet.mobile.mvp.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import cn.bingoogolapple.transformerstip.TransformersTip
import cn.bingoogolapple.transformerstip.gravity.TipGravity
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.NewAudienceVideoAdapter
import com.zhongyou.meet.mobile.di.component.DaggerChairManWithKotlinComponent
import com.zhongyou.meet.mobile.di.module.ChairManWithKotlinModule
import com.zhongyou.meet.mobile.entities.Agora
import com.zhongyou.meet.mobile.entities.AudienceVideo
import com.zhongyou.meet.mobile.entities.Material
import com.zhongyou.meet.mobile.entiy.MeetingJoin
import com.zhongyou.meet.mobile.mvp.contract.ChairManWithKotlinContract
import com.zhongyou.meet.mobile.mvp.presenter.ChairManWithKotlinPresenter
import io.agora.openlive.ui.NewInMeetingChatFragment
import kotlinx.android.synthetic.main.activity_chair_man_with_kotlin.*
import kotlinx.android.synthetic.main.chair_man_bottom_tools.*
import q.rorbin.badgeview.Badge
import java.util.*


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
class ChairManWithKotlinActivity : BaseActivity<ChairManWithKotlinPresenter>(), ChairManWithKotlinContract.View {

    private val meetingData by lazy<MeetingJoin.DataBean> {
        intent.getParcelableExtra("meeting");
    }

    private val audienceVideos by lazy {
        ArrayList<AudienceVideo>()
    }

    private val chatFragment by lazy {
        NewInMeetingChatFragment()
    }
    private var currentMaterial: Material? = null
    var isFullScreen: Boolean = false
    var isPPTModel: Boolean = false
    private val agora by lazy<Agora> {
        intent.getParcelableExtra("agora")
    }
    private var currentAudience: AudienceVideo? = null
    private var newAudience: AudienceVideo? = null;
    private var currentAudienceId = 0
    private val gridLayoutHelper by lazy {
        GridLayoutHelper(2)
    }
    private val virtualLayoutManager by lazy {
        VirtualLayoutManager(this)
    }
    private val delegateAdapter by lazy {
        DelegateAdapter(virtualLayoutManager, false)
    }
    private val mVideoAdapter by lazy {
        NewAudienceVideoAdapter(this, gridLayoutHelper, audienceVideos)
    }

    private var mTransformersTipPop: TransformersTip? = null

    var badge: Badge? = null
    val audienceHashMap = HashMap<Int, AudienceVideo>()


    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerChairManWithKotlinComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .chairManWithKotlinModule(ChairManWithKotlinModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_chair_man_with_kotlin //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {


        val uri = Uri.parse("rong://" + applicationInfo.packageName).buildUpon()
                .appendPath("conversation").appendPath("group")
                .appendQueryParameter("isMaker", "1")
                .appendQueryParameter("targetId", meetingData.meeting.id).build()
        chatFragment.uri = uri
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.makerChatContent, chatFragment)
        fragmentTransaction.show(chatFragment)
        discuss.visibility = View.GONE

        setListener();
    }

    private fun setListener() {
        full_screen.setOnClickListener {
            if (currentMaterial == null) {
                if (isFullScreen) {
                    full_screen.text = "全屏"
                    isFullScreen = false
                    waiter.visibility = View.GONE
                    if (currentAudience == null && currentAudienceId == 0) {
                        stop_audience.visibility = View.VISIBLE
                    } else {
                        stop_audience.visibility = View.INVISIBLE
                    }
                    doc.visibility = View.VISIBLE
                    mute_audio.visibility = View.VISIBLE
                    spliteView.visibility = View.VISIBLE
                    switch_camera.visibility = (if (isPPTModel) View.GONE else View.VISIBLE)
                    if (mVideoAdapter.dataSize >= 1 && currentMaterial != null) {
                        audience_list.visibility = View.VISIBLE
                        mVideoAdapter.setVisibility(View.VISIBLE)
                    } else {
                        audience_list.visibility = View.GONE
                        mVideoAdapter.setVisibility(View.GONE)
                    }
                    badge?.badgeNumber = audienceHashMap.size
                    doc_layout.visibility = if (currentMaterial != null) View.VISIBLE else View.GONE

                } else {
                    full_screen.text = "恢复"
                    isFullScreen = true
                    waiter.visibility = View.INVISIBLE
                    stop_audience.visibility = View.INVISIBLE
                    doc.visibility = View.INVISIBLE
                    mute_audio.visibility = View.INVISIBLE
                    spliteView.visibility = View.INVISIBLE
                    switch_camera.visibility = View.INVISIBLE
                    audience_list.visibility = View.GONE
                    mVideoAdapter.setVisibility(View.GONE)
                    doc_layout.visibility = View.GONE
                    badge?.badgeNumber = 0
                }
            } else {
                mTransformersTipPop = object : TransformersTip(it, R.layout.pop_full_screen_choose) {
                    override fun initView(contentView: View) {
                        contentView.findViewById<View>(R.id.not_full_screen).setOnClickListener {
                            mTransformersTipPop!!.dismissTip()
//                            notFullScreenState()
                        }
                        contentView.findViewById<View>(R.id.full_screen).setOnClickListener {
                            mTransformersTipPop!!.dismissTip()
//                            FullScreenState()
                        }
                        contentView.findViewById<View>(R.id.clearAll).setOnClickListener {
                            mTransformersTipPop!!.dismissTip()
//                            clearAllState()
                        }
                    }
                }
                        .setTipGravity(TipGravity.TO_TOP_CENTER) // 设置浮窗相对于锚点控件展示的位置
                        .setTipOffsetYDp(-6) // 设置浮窗在 y 轴的偏移量*/
                        .setBackgroundDimEnabled(false) // 设置是否允许浮窗的背景变暗
                        .setDismissOnTouchOutside(true)
            }
        }
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
        finish()
    }
}

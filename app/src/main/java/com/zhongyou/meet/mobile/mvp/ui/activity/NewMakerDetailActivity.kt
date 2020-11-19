package com.zhongyou.meet.mobile.mvp.ui.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.RxBus
import com.kongzue.dialog.v3.MessageDialog
import com.like.LikeButton
import com.like.OnLikeListener
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.adapter.MakerAdapter
import com.zhongyou.meet.mobile.di.component.DaggerNewMakerDetailComponent
import com.zhongyou.meet.mobile.di.module.NewMakerDetailModule
import com.zhongyou.meet.mobile.entiy.MakerDetail
import com.zhongyou.meet.mobile.event.ColloctionEvent
import com.zhongyou.meet.mobile.event.UnLockSeriesResultEvent
import com.zhongyou.meet.mobile.event.UnlockEvent
import com.zhongyou.meet.mobile.mvp.contract.NewMakerDetailContract
import com.zhongyou.meet.mobile.mvp.presenter.NewMakerDetailPresenter
import com.zhongyou.meet.mobile.mvp.ui.fragment.CourseDetailsFragment
import com.zhongyou.meet.mobile.mvp.ui.fragment.CourseSelectionFragment
import com.zhongyou.meet.mobile.utils.CountDownTimerUtil
import com.zhongyou.meet.mobile.utils.TabViewUtils
import com.zhongyou.meet.mobile.utils.ToastUtils
import com.zhongyou.meet.mobile.utils.listener.CountDownTimeListener
import kotlinx.android.synthetic.main.activity_new_maker_detail.*
import kotlinx.android.synthetic.main.activity_new_maker_detail.tabLayout
import kotlinx.android.synthetic.main.activity_new_maker_detail.view_page2
import kotlinx.android.synthetic.main.fragment_class_room.*
import kotlinx.android.synthetic.main.fragment_course_details.*
import kotlinx.android.synthetic.main.fragment_maker_course_detail.*
import kotlinx.android.synthetic.main.include_base_title_tool_bar.*
import me.jessyan.autosize.utils.AutoSizeUtils


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 09/29/2020 16:00
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
class NewMakerDetailActivity : BaseActivity<NewMakerDetailPresenter>(), NewMakerDetailContract.View, CountDownTimeListener {

    private var mFragments = mutableListOf<Fragment>()
    private var courseDetailsFragment :CourseDetailsFragment?= null
    private var courseSelectionFragment :CourseSelectionFragment?= null
    private var mPageId: String? = null
    private var mSeriesId: String? = null
    private var isAuth = -1

    /**到计时 */
    private var countDownTimerUtil: CountDownTimerUtil? = null
    private var weChat: String? = null

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerNewMakerDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newMakerDetailModule(NewMakerDetailModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_new_maker_detail //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {

        mPageId = intent.getStringExtra("pageId")
        mSeriesId = intent.getStringExtra("seriesId")
        isAuth = intent.getIntExtra("isAuth", 0)
        val isSignUp = intent.getIntExtra("isSignUp", 1)

        if (isAuth != 1) {
            needBuyCourseLayout_head?.visibility = View.GONE
        } else {
            needBuyCourseLayout_head?.visibility = View.VISIBLE
        }

        if (!TextUtils.isEmpty(mPageId) && mPresenter != null) {
            mPresenter?.getMoreCourse(mPageId, mSeriesId)
        }

        //collection
        collection_like.setOnLikeListener(object :OnLikeListener{
            override fun liked(likeButton: LikeButton?) {
                if (mPresenter != null) {
                    mPresenter?.signCourse(mSeriesId, 1)
                }
            }

            override fun unLiked(likeButton: LikeButton?) {
                if (mPresenter != null) {
                    mPresenter?.signCourse(mSeriesId, 0)
                }
            }
        })

        courseDetailsFragment = CourseDetailsFragment()
        courseSelectionFragment = CourseSelectionFragment()
        mFragments.add(courseDetailsFragment!!)
        mFragments.add(courseSelectionFragment!!)

        view_page2.adapter = MakerAdapter(this,mFragments)
        view_page2.offscreenPageLimit = 2
        TabLayoutMediator(tabLayout,view_page2,true,TabLayoutMediator.TabConfigurationStrategy {
            tab, position ->
            if (position == 0) {
                tab.text = "课程详情"
            } else {
                tab.text = "选课"
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

        needBuyCourseLayout_head?.setOnClickListener {
            if (isAuth == 1) {
                if (mPresenter != null) {
                    mPresenter?.unLockSeries(mSeriesId)
                }
            } else if (isAuth == 2) {
                MessageDialog.show(this, "提示", "该系列已成功解锁", "确定")
            }
        }


        weChatTextView_head?.setOnClickListener {
            val mClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            if (mClipboardManager == null) {
                ToastUtils.showToast(this, "获取剪切板权限出错")
                return@setOnClickListener
            }
            if (!TextUtils.isEmpty(weChat)) {
                val mClipData = ClipData.newPlainText("wechat", weChat)
                mClipboardManager.setPrimaryClip(mClipData)
                ToastUtils.showToast(this, "复制成功 请去微信添加好友")
            } else {
                ToastUtils.showToast(this, "复制微信失败")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun getMoreAudioSuccess(jsonObject: JSONObject?) {
        val makerDetail = JSON.parseObject(jsonObject!!.toJSONString(), MakerDetail::class.java)
        if (!TextUtils.isEmpty(makerDetail.name)) {
            toolbar_title.text = makerDetail.name
//            tv_title.text = intent.getStringExtra("name")
            tv_title.text = makerDetail.name
        }

        makerDetail?.let {
//            tv_name.text = intent.getStringExtra("lecturerInfo")
//            tv_up.text = "更新${intent.getIntExtra("videoNum", 0)}节 | "
//            tv_study.text = "${intent.getIntExtra("studyNum", 0).toString()}人已学"
            tv_name.text = it.lecturerInfo
            tv_up.text = "更新${it.videoNum}节 | "
            tv_study.text = "${it.studyNumStr}人已学"
            tv_content.text = it.seriesIntro
//            Glide.with(this).`as`(PictureDrawable::class.java).listener(SvgSoftwareLayerSetter()).load(it.lecturerHeadUrl).into(img_head)
            Glide.with(this).load(it.lecturerHeadUrl).circleCrop().into(img_head)

            //head
            val roundedCorners = RoundedCorners( AutoSizeUtils.pt2px(this, 10f))
            val options = RequestOptions.bitmapTransform(roundedCorners)
            Glide.with(this).load(it.headUrl).apply(options).placeholder(R.drawable.ico_tx).error(R.drawable.ico_tx).into(img_content)

        }

        collection_like.isLiked = makerDetail.isSign == 1

        if (makerDetail != null) {
            courseDetailsFragment?.setMakerDetail(makerDetail,isAuth,mPresenter,mSeriesId,this)
            val list = makerDetail.subPages
            courseSelectionFragment?.setSubPagesBeanData(list,isAuth,mPresenter,mSeriesId,this)
        }
    }

    override fun signSuccess(isSuccess: Boolean, sign: Int,message: String?) {
        if (isSuccess) {
            if ("报名成功" == message) {
                ToastUtils.showToast("收藏成功")
            } else{
                ToastUtils.showToast("取消收藏")
            }

            collection_like.isLiked = sign != 0
        } else {
            collectButton.isLiked = sign == 0
        }
        RxBus.getDefault().send(ColloctionEvent(true))
    }

    @SuppressLint("SetTextI18n")
    override fun unLockSeriesResult(isSuccess: Boolean, weChatResult: String?) {
        RxBus.getDefault().send(UnLockSeriesResultEvent(isSuccess,weChat))

        if (isSuccess) {
            isAuth = 2
            if (needBuyCourseTextView_new_head != null) {
                setTextViewDrawableLeft(needBuyCourseTextView_new_head, R.drawable.icon_buy2)
                needBuyCourseTextView_new_head.text = "解锁成功"
            }

            needBuyCourseLayout_head?.setBackgroundColor(Color.TRANSPARENT)
            ToastUtils.showToast(this, "解锁成功")
            RxBus.sendMessage(UnlockEvent(true))
            countDownTimerUtil = CountDownTimerUtil(3000, 1000, this)
            countDownTimerUtil?.start()
        } else {
            weChat = weChatResult
            if (needBuyCourseTextView_new_head != null) {
                setTextViewDrawableLeft(needBuyCourseTextView_new_head, R.drawable.icon_buy4)
                needBuyCourseTextView_new_head.text = "解锁失败"
            }

            ToastUtils.showToast(this, "解锁失败")
            if (needbuyHint_head != null) {
                needbuyHint_head.visibility = View.VISIBLE
            }
            if (weChatTextView_head != null) {
                weChatTextView_head.visibility = View.VISIBLE
                weChatTextView_head.text = "微信:$weChatResult"
            }

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTextViewDrawableLeft(view: TextView?, drawable: Int) {
        val left = resources.getDrawable(drawable)
        view?.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null)
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

    override fun onTimeFinish() {
        needBuyCourseLayout_head?.visibility = View.GONE
    }

    override fun onTimeTick(time: Long) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if(countDownTimerUtil != null) {
            countDownTimerUtil?.cancel()
        }
    }
}

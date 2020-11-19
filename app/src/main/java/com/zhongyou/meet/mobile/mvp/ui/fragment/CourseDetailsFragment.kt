package com.zhongyou.meet.mobile.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.jess.arms.base.BaseLazyLoadFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.RxBus
import com.kongzue.dialog.v3.MessageDialog
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.entiy.MakerDetail
import com.zhongyou.meet.mobile.event.UnLockSeriesResultEvent
import com.zhongyou.meet.mobile.event.UnlockEvent
import com.zhongyou.meet.mobile.mvp.presenter.NewMakerDetailPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity
import com.zhongyou.meet.mobile.utils.*
import com.zhongyou.meet.mobile.utils.DownloadUtil.OnDownloadListener
import com.zhongyou.meet.mobile.utils.listener.CountDownTimeListener
import io.rong.subscaleview.ImageSource
import kotlinx.android.synthetic.main.fragment_course_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

@Page(name = "课程详情的Fragment")
class CourseDetailsFragment : BaseLazyLoadFragment<IPresenter>(), CountDownTimeListener {

    private var makerDetail:MakerDetail?= null
    //不等于1就是未解锁
    private var isAuth = 0
    private var scopeCoroutineScope = CoroutineScope(Dispatchers.Main)

    private var detailsPresenter : NewMakerDetailPresenter?= null
    private var mSeriesId: String? = null
    private var activity: NewMakerDetailActivity?= null
    private var weChat: String? = null

    /**到计时 */
    private var countDownTimerUtil: CountDownTimerUtil? = null

    override fun lazyLoadData() {
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
    }

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        needBuyCourseTextView_new?.setOnClickListener {
            if (isAuth == 1) {
                if (detailsPresenter != null) {
                    detailsPresenter?.unLockSeries(mSeriesId)
                }
            } else if (isAuth == 2) {
                activity?.let { it1 -> MessageDialog.show(it1, "提示", "该系列已成功解锁", "确定") }
            }
        }

        weChatTextView?.setOnClickListener {
            val mClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            if (mClipboardManager == null) {
                ToastUtils.showToast(activity, "获取剪切板权限出错")
                return@setOnClickListener
            }
            if (!TextUtils.isEmpty(weChat)) {
                val mClipData = ClipData.newPlainText("wechat", weChat)
                mClipboardManager.setPrimaryClip(mClipData)
                ToastUtils.showToast(activity, "复制成功 请去微信添加好友")
            } else {
                ToastUtils.showToast(activity, "复制微信失败")
            }
        }

        RxBus.handleMessage { t ->
            when (t) {
                is UnLockSeriesResultEvent -> {
                    if (t.isSuccess) {
                        isAuth = 2
                        if (needBuyCourseTextView_new != null) {
                            setTextViewDrawableLeft(needBuyCourseTextView_new, R.drawable.icon_buy2)
                            needBuyCourseTextView_new.text = "解锁成功"
                        }
                        needBuyCourseLayout?.setBackgroundColor(Color.TRANSPARENT)
                        ToastUtils.showToast(activity, "解锁成功")
                        RxBus.sendMessage(UnlockEvent(true))
                        countDownTimerUtil = CountDownTimerUtil(3000, 1000, this)
                        countDownTimerUtil?.start()
                    } else {
                        weChat = t.weChat
                        if (needBuyCourseTextView_new != null) {
                            setTextViewDrawableLeft(needBuyCourseTextView_new, R.drawable.icon_buy4)
                            needBuyCourseTextView_new.text = "解锁失败"
                        }

                        ToastUtils.showToast(activity, "解锁失败")
                        if (needbuyHint != null) {
                            needbuyHint.visibility = View.VISIBLE
                        }
                        if (weChatTextView != null) {
                            weChatTextView.visibility = View.VISIBLE
                            weChatTextView.text = "微信:$weChat"
                        }

                    }
                }
            }
        }

    }

    override fun setData(data: Any?) {
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_course_details, container, false)
    }

    fun setMakerDetail(makerDetail: MakerDetail?,isAuth: Int,detailsPresenter : NewMakerDetailPresenter?,mSeriesId: String?,activity: NewMakerDetailActivity) {
        this.makerDetail = makerDetail
        this.isAuth = isAuth
        this.detailsPresenter = detailsPresenter
        this.mSeriesId = mSeriesId
        this.activity = activity
        var fileName = ""
        fileName = try {
            makerDetail?.id + "_" + makerDetail?.pictureURL?.substring(makerDetail.pictureURL.lastIndexOf("/") + 1)
        } catch (e: Exception) {
            e.printStackTrace()
            makerDetail?.id + "_" + makerDetail?.pictureURL
        }
        val file = File(FileUtils.getPath(activity) + fileName)
        if (file.exists()) {
            detailImage?.let {
                detailImage.setImage(ImageSource.uri(FileUtils.getPath(activity) + fileName))
                detailImage.isZoomEnabled = false
                detailImage.visibility = View.VISIBLE
            }

            circle_progress?.visibility= View.GONE

            setNeedBuyView(1)

            getIntroduceImage(activity, makerDetail)
        } else {
            downLoadFile(makerDetail,fileName)
        }

    }

    private fun downLoadFile(makerDetail: MakerDetail?, fileName: String) {
        DownloadUtil.get().download(activity, makerDetail!!.pictureURL, FileUtils.getPath(activity), fileName, object : OnDownloadListener {
            override fun onDownloadSuccess(file: File) {
                Timber.e("下载文件成功---->%s", file.path)
                try {
                    if (detailImage != null) {
                        detailImage.setImage(ImageSource.uri(file.path))
                        detailImage.isZoomEnabled = false
                    }
                    if (circle_progress != null) {
                        circle_progress.visibility = View.GONE
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    getIntroduceImage(activity, makerDetail)
                }
                setNeedBuyView(1)
            }

            override fun onDownloading(progress: Int) {
                Timber.e("progress---->%s", progress)
                if (circle_progress != null) {
                    circle_progress.progress = progress
                }
                if (detailImageCopy != null) {
                    detailImageCopy.visibility = View.GONE
                }
            }

            override fun onDownloadFailed(e: java.lang.Exception) {
                try {
                    if (detailImage != null) {
                        detailImage.visibility = View.GONE
                    }
                    if (circle_progress != null) {
                        circle_progress.visibility = View.GONE
                    }
                    if (detailImageCopy != null) {
                        detailImageCopy.visibility = View.VISIBLE
                        activity?.let {
                            Glide.with(it).load(makerDetail.pictureURL)
                                    .priority(Priority.HIGH)
                                    .into(detailImageCopy)
                        }
                        setNeedBuyView(2)
                    }
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                } finally {
                    getIntroduceImage(activity, makerDetail)
                }
            }
        })
    }

    private fun getIntroduceImage(activity: FragmentActivity?, makerDetail: MakerDetail?) {
        var fileName = ""
        fileName = try {
            makerDetail?.id + "_" + makerDetail?.introduceURL?.substring(makerDetail.introduceURL.lastIndexOf("/") + 1)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            makerDetail?.id + "_" + makerDetail?.introduceURL
        }

        val file = File(FileUtils.getPath(context) + fileName)
        if (file.exists()) {
            descriptionImage?.let {
                descriptionImage.setImage(ImageSource.uri(FileUtils.getPath(context) + fileName))
                descriptionImage.isZoomEnabled = false
                descriptionImage.visibility = View.VISIBLE
            }
            circle_progress1?.visibility = View.GONE

        } else {
            DownloadUtil.get().download(activity, makerDetail!!.introduceURL, FileUtils.getPath(context), fileName, object : OnDownloadListener {
                override fun onDownloadSuccess(file: File) {
                    Timber.e("下载文件成功---->%s", file.path)
                    if (descriptionImage != null) {
                        descriptionImage.setImage(ImageSource.uri(file.path))
                        descriptionImage.isZoomEnabled = false
                    }
                    if (circle_progress1 != null) {
                        circle_progress1.visibility = View.GONE
                    }
                }

                override fun onDownloading(progress: Int) {
                    Timber.e("progress---->%s", progress)
                    if (circle_progress1 != null) {
                        circle_progress1.progress = progress
                    }
                    if (descriptionImageCopy != null) {
                        descriptionImageCopy.visibility = View.GONE
                    }
                }

                override fun onDownloadFailed(e: java.lang.Exception) {
                    if (descriptionImage != null) {
                        descriptionImage.visibility = View.GONE
                    }
                    if (circle_progress1 != null) {
                        circle_progress1.visibility = View.GONE
                    }
                    if (descriptionImageCopy != null) {
                        descriptionImageCopy.visibility = View.VISIBLE
                        Glide.with(context!!).load(makerDetail.introduceURL)
                                .priority(Priority.HIGH)
                                .into(descriptionImageCopy)
                    }
                }
            })
        }
    }

    private fun setNeedBuyView(type: Int) {
        scopeCoroutineScope.launch {
            if (isAuth != 0) {
                needBuyCourseLayout?.visibility = View.VISIBLE
            } else {
                needBuyCourseLayout?.visibility = View.GONE
            }

            if (type == 1) {
                val layoutParams = needBuyCourseLayout?.layoutParams
                layoutParams?.height = detailImage?.height
                needBuyCourseLayout?.layoutParams = layoutParams
            } else {
                val layoutParams = needBuyCourseLayout?.layoutParams
                layoutParams?.height = detailImageCopy?.height
                needBuyCourseLayout?.layoutParams = layoutParams
            }

            when (isAuth) {
                0 -> {
                }
                1 -> {
                    needBuyCourseLayout.setBackgroundColor(Color.parseColor("#55000000"))
                    if (needBuyCourseTextView_new != null) {
                        setTextViewDrawableLeft(needBuyCourseTextView_new, R.drawable.icon_buy3)
                        needBuyCourseTextView_new.text = "解锁课程"
                    }
                }
                2 -> {
                    needBuyCourseLayout.setBackgroundColor(Color.TRANSPARENT)
                    if (needBuyCourseTextView_new != null) {
                        setTextViewDrawableLeft(needBuyCourseTextView_new, R.drawable.icon_buy2)
                        needBuyCourseTextView_new.text = "解锁成功"
                    }
                    if (needBuyCourseLayout != null) {
                        needBuyCourseLayout.visibility = View.GONE
                    }

                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTextViewDrawableLeft(view: TextView?, drawable: Int) {
        val left = resources.getDrawable(drawable)
        view?.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null)
    }

    override fun onTimeFinish() {
        needBuyCourseLayout?.visibility = View.GONE
    }

    override fun onTimeTick(time: Long) {

    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimerUtil?.cancel()
    }
}
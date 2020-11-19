package com.zhongyou.meet.mobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.bgabanner.BGABanner
import com.adorkable.iosdialog.AlertDialog
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter
import com.zhongyou.meet.mobile.core.JoinMeetingDialog.showNeedCodeDialog
import com.zhongyou.meet.mobile.core.JoinMeetingDialog.showNoCodeDialog
import com.zhongyou.meet.mobile.core.PlayService
import com.zhongyou.meet.mobile.entiy.RecomandData
import com.zhongyou.meet.mobile.entiy.RecomandData.ChuangkeListBean
import com.zhongyou.meet.mobile.mvp.ui.activity.*
import com.zhongyou.meet.mobile.utils.DisplayUtil
import com.zhongyou.meet.mobile.utils.ImageUtils
import com.zhongyou.meet.mobile.utils.MMKVHelper
import com.zhongyou.meet.mobile.utils.listener.MonitorListener
import com.zhongyou.meet.mobile.utils.listener.MonitorTwoListener
import timber.log.Timber
import java.util.*


class RecommendAdapter(context : FragmentActivity?, mHelper:LayoutHelper) :DelegateAdapter.Adapter<RecyclerView.ViewHolder>(){

    private var view :View?= null
    private var mHelper:LayoutHelper?=null
    private var context : Context?= null
    private var data: RecomandData?= null
    private val textLists: MutableList<String> = ArrayList()
    private var adapter :BaseRecyclerViewAdapter<RecomandData.ChangeLessonByDayBean>?= null
    private var mAudioList : MutableList<RecomandData.ChangeLessonByDayBean> ?= null

    private var monitorListener:MonitorTwoListener<Int,RecomandData.ChangeLessonByDayBean>?= null
    private var isShowBanner = false

    private var mPhoneStatusDialog: AlertDialog? = null

    private var dataChange: RecomandData.ChangeLessonByDayBean?= null

    private var monitorListenerBoolean : MonitorListener<Boolean>?= null

    fun setMonitorMore(monitorListenerBoolean : MonitorListener<Boolean>?) {
        this.monitorListenerBoolean = monitorListenerBoolean
    }



    fun setMonitor(monitorListener:MonitorTwoListener<Int,RecomandData.ChangeLessonByDayBean>?) {
        this.monitorListener = monitorListener
    }

    init {
        this.mHelper = mHelper
        this.context = context
        mAudioList = mutableListOf()
    }

    companion object {
        val TYPE_ONE = 0
        val TYPE_TWO = 1
    }

    class RecommendHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerGuide  = itemView.findViewById<BGABanner>(R.id.banner_guide_content)
    }

    fun setBannerData(bannerData: RecomandData) {
        if (data != null) {
            data = null
        }
        this.data = bannerData
        notifyDataSetChanged()
    }

    fun setAutoUnscramble (data: RecomandData.ChangeLessonByDayBean?) {
        if (mAudioList != null) {
            mAudioList?.clear()
        }
        dataChange = data

        data?.let { mAudioList?.add(it) }
        adapter?.notifyData(mAudioList)
    }

    fun getAutoUnscrambleItem () :RecomandData.ChangeLessonByDayBean?{
        return dataChange
    }

    fun getAutoUnscramble () : MutableList<RecomandData.ChangeLessonByDayBean>?{
        return mAudioList
    }

    fun getContentAdapter() :BaseRecyclerViewAdapter<RecomandData.ChangeLessonByDayBean>? {
        return adapter
    }

    fun startBanner(){
        isShowBanner = false
        notifyDataSetChanged()
    }

    fun pauseBanner() {
        isShowBanner = true
        notifyDataSetChanged()
    }

    class RecommendContent(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler)
        val tvMore = itemView.findViewById<AppCompatTextView>(R.id.tv_more)
        val tv_1 = itemView.findViewById<AppCompatTextView>(R.id.tv_1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_ONE ->{
                view = LayoutInflater.from(context).inflate(R.layout.item_recommend_banner,parent,false)
                return RecommendHolder(view!!)
            }

            TYPE_TWO -> {
                view = LayoutInflater.from(context).inflate(R.layout.item_recomment_information,parent,false)
                return RecommendContent(view!!)
            }
        }
        return  RecommendHolder(view!!)
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return mHelper!!
    }


    override fun getItemViewType(position: Int): Int {
        when (position) {
            TYPE_ONE -> return TYPE_ONE
            TYPE_TWO -> return TYPE_TWO
        }
        return TYPE_ONE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecommendHolder) {
            if (data != null) {
                if (data?.chuangkeList == null) {
                    return
                }
                for (meetingListBean in data?.chuangkeList!!) {
//                    textLists.add(meetingListBean.name)
                    textLists.add("")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ImageUtils.setClipViewCornerRadius(holder.bannerGuide,DisplayUtil.px2dip(context,45f))
                }
                holder.bannerGuide.setData(R.layout.item_localimage, data?.chuangkeList, textLists)
                holder.bannerGuide.setAdapter { banner, itemView, model, position ->
                    context?.let {
                        Glide.with(it)
                                .load((model as ChuangkeListBean).pictureURL )
                                .centerCrop()
                                .dontAnimate()
                                .error(R.drawable.dafault_course)
                                .placeholder(R.drawable.defaule_banner)
                                .into(itemView as ImageView)
                    }
                }

//                if (isShowBanner) {
//                    holder.bannerGuide.stopAutoPlay()
//                } else{
//                    holder.bannerGuide.startAutoPlay()
//                }

                holder.bannerGuide.setDelegate { banner, itemView, model, position ->
                    Timber.e("mode---->%s", JSON.toJSONString(model))
                    val models: ChuangkeListBean = model as ChuangkeListBean ?: return@setDelegate
                    if (models.isDefaultImg == 1) {
                        return@setDelegate
                    }

                    if (TextUtils.isEmpty(models.urlId)) {
                        return@setDelegate
                    }

                    if (model.linkType == 1) {

                        /*MeetingsData.DataBean.ListBean meeting = new MeetingsData.DataBean.ListBean();
				meeting.setId(model.getUrlId());*/
                        if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
                            showPhoneStatus(1, model, model.isToken)
                            return@setDelegate
                        }
                        if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
                            showPhoneStatus(2, model, model.isToken)
                            return@setDelegate
                        }
                        //					initDialog(model, model.getIsToken());//弹窗加入会议
                        try {
                            if (model.isToken.toDouble() == 1.0) { //需要加入码的
                                if (context != null) {
                                    showNeedCodeDialog((context as AppCompatActivity), model.urlId)
                                }
                            } else {
                                if (context != null) {
                                    showNoCodeDialog((context as AppCompatActivity), model.urlId)
                                }
                            }
                        } catch (e: Exception) {
                            if (context!= null) {
                                showNeedCodeDialog((context as AppCompatActivity), model.urlId)
                            }
                        }
                    } else if (model.linkType == 2) {
                        when (model.pageType) {
                            1 -> {
                                val intent = Intent(context, NewMakerDetailActivity::class.java)
                                intent.putExtra("pageId", model.urlId)
                                intent.putExtra("seriesId", model.seriesId)
                                intent.putExtra("isSignUp", model.isSignUp)
                                intent.putExtra("url",model.pictureURL)
                                context?.startActivity(intent)
                            }
                            2 -> {
                                val intent = Intent(context, MakerCourseMeetDetailActivity::class.java)
                                intent.putExtra("pageId", model.urlId)
                                context?.startActivity(intent)
                            }
                            3 -> {
                                val intent = Intent(context, MakerCourseVideoDetailActivity::class.java)
                                intent.putExtra("pageId", model.urlId)
                                context?.startActivity(intent)
                            }
                            4 -> {
                                val intent = Intent(context, MakerCourseAudioDetailActivity::class.java)
                                intent.putExtra("pageId", model.urlId)
                                context?.startActivity(intent)
                            }
                        }
                    }
                }
            }
        } else if (holder is RecommendContent) {
            holder.recyclerView.layoutManager = LinearLayoutManager(context)
            if (adapter == null && mAudioList != null) {
                adapter = RecommendConntentAdapter(monitorListener, context, mAudioList, R.layout.item_recyc_recomment_infor,holder.tv_1)
            } else {
                adapter?.notifyData(mAudioList)
            }
            holder.recyclerView.adapter = adapter

            holder.tvMore.setOnClickListener {
                monitorListenerBoolean?.OnMonitor(true)

            }
        }
    }


    fun showPhoneStatus(i: Int, model: ChuangkeListBean, isToken: String?) {
        mPhoneStatusDialog = AlertDialog(context)
                .builder()
                .setTitle("提示")
                .setMsg(if (i == 1) "检测到摄像头关闭 是否打开？" else "检测到麦克风关闭 是否打开？")
                .setNegativeButton("进入会议") {
                    mPhoneStatusDialog?.dismiss()
                    try {
                        if (model.isToken.toDouble() == 1.0) { //需要加入码的
                            if (context != null) {
                                showNeedCodeDialog((context as AppCompatActivity), model.urlId)
                            }
                        } else {
                            if (context != null) {
                                showNoCodeDialog((context as AppCompatActivity), model.urlId)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        if (context != null) {
                            showNeedCodeDialog((context as AppCompatActivity), model.urlId)
                        }
                    }
                }
                .setPositiveButton("去设置") {
                    mPhoneStatusDialog?.dismiss()
                    context?.startActivity(Intent(context, MeetingSettingActivity::class.java))
                }
        mPhoneStatusDialog?.show()
    }
}
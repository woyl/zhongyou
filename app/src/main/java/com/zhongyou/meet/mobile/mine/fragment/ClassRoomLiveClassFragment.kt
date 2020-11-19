package com.zhongyou.meet.mobile.mine.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.adorkable.iosdialog.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jess.arms.base.BaseLazyLoadFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.maning.mndialoglibrary.MToast
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.BaseApplication
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter
import com.zhongyou.meet.mobile.core.JoinMeetingDialog.showNeedCodeDialog
import com.zhongyou.meet.mobile.core.JoinMeetingDialog.showNoCodeDialog
import com.zhongyou.meet.mobile.entiy.MeetingsData
import com.zhongyou.meet.mobile.mvp.presenter.ClassRoomPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity
import com.zhongyou.meet.mobile.utils.DisplayUtil
import com.zhongyou.meet.mobile.utils.MMKVHelper
import com.zhongyou.meet.mobile.view.itemdecoration.LinearLayoutItemDecoration
import kotlinx.android.synthetic.main.fragment_new_makercoruse_other.*
import me.jessyan.autosize.utils.AutoSizeUtils

class ClassRoomLiveClassFragment : BaseLazyLoadFragment<IPresenter>() {

    private var mAdapter: BaseRecyclerViewAdapter<MeetingsData.DataBean.ListBean>? = null
    private var mCurrentMeetingType = 0
    private var curPosition = 1
    private var presenter: ClassRoomPresenter? = null
    private var list = mutableListOf<MeetingsData.DataBean.ListBean>()
    private var mPhoneStatusDialog: AlertDialog? = null

    companion object {
        fun newInstance(): ClassRoomLiveClassFragment? {
            return ClassRoomLiveClassFragment()
        }
    }

    override fun lazyLoadData() {

    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
    }

    override fun initData(savedInstanceState: Bundle?) {
        recycler.layoutManager = LinearLayoutManager(mContext)
        //recycler.addItemDecoration(LinearLayoutItemDecoration(DisplayUtil.dip2px(mContext, 15f)))
        smart_refresh?.setOnRefreshListener {
            curPosition = 1
            presenter?.getMeetings(mCurrentMeetingType, curPosition, "","2")
        }
        smart_refresh?.setOnLoadMoreListener {
            curPosition++
            presenter?.getMeetings(mCurrentMeetingType, curPosition, "","2")
        }
    }

    override fun onRestartUI() {
        curPosition = 1
        presenter?.getMeetings(mCurrentMeetingType, curPosition, "","2")
    }

    override fun setData(data: Any?) {
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_new_makercoruse_other, container, false)
    }

    fun setPresenter(presenter: ClassRoomPresenter?) {
        this.presenter = presenter
        presenter?.getMeetings(mCurrentMeetingType, curPosition, "","2")
    }

    fun getSmallRefrush(): SmartRefreshLayout {
        return smart_refresh
    }

    fun getDataNull() {
        emptyView.visibility = View.VISIBLE
        recycler.visibility = View.GONE
    }

    fun setData(meetingsData: MeetingsData?) {//1会议 2直播
        if (meetingsData == null || meetingsData.data == null || meetingsData.data?.list == null) {
            emptyView.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        }
        if (meetingsData?.data?.list?.size == 0) {
            emptyView.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }

        meetingsData?.let {
            it.data?.let { data ->
                if (data.totalPage <= curPosition) {
                    smart_refresh.setEnableLoadMore(false)
                } else {
                    smart_refresh.setEnableLoadMore(true)
                }

                if (curPosition == 1) {
                    mAdapter?.clearData()
                    list.clear()
                }

                for (its in it.data.list) {
                    if (its.isMeeting == 2) {
                        list.add(its)
                    }
                }
            }
        }

        if (mAdapter == null) {
            mAdapter = object : BaseRecyclerViewAdapter<MeetingsData.DataBean.ListBean>(mContext, list, R.layout.item_my_class_room) {
                @SuppressLint("SetTextI18n")
                override fun convert(holder: BaseRecyclerHolder?, item: MeetingsData.DataBean.ListBean?, position: Int, isScrolling: Boolean) {
                    holder?.let { view ->
                        val tvTime = holder.getView<AppCompatTextView>(R.id.tv_time)
                        val tvName = holder.getView<AppCompatTextView>(R.id.tv_name)
                        val tvContent = holder.getView<AppCompatTextView>(R.id.tv_content)
                        val imgState = holder.getView<AppCompatImageView>(R.id.img_state)
                        val img_small_head = holder.getView<ImageView>(R.id.img_small_head)
                        val flyLiveDoing = holder.getView<View>(R.id.flyLiveDoing)
//                        val roundedCorners = RoundedCorners( AutoSizeUtils.pt2px(mContext, 10f))
//                        val options = RequestOptions.bitmapTransform(roundedCorners)
                        item?.let {
                            holder.setImageByUrlWithCorner(R.id.img_head,
                                    it.playUrl, R.drawable.dafault_course,
                                    R.drawable.dafault_course, AutoSizeUtils.pt2px(holder.itemView.context, 18f))

                            Glide.with(mContext).load(it.lecturerHeadUrl).placeholder(R.drawable.ico_tx).error(R.drawable.ico_tx).into(img_small_head)

                            if (it.isCollection == 0) {
                                val drawable = resources.getDrawable(R.mipmap.notice)
                                drawable.setBounds(0,0,drawable.minimumWidth,drawable.minimumHeight)
                                tvTime.setCompoundDrawables(null,null,drawable,null)
                            } else {
                                val drawable = resources.getDrawable(R.mipmap.notice_on)
                                drawable.setBounds(0,0,drawable.minimumWidth,drawable.minimumHeight)
                                tvTime.setCompoundDrawables(null,null,drawable,null)
                            }

                            tvTime.text = "直播时间:${it.meetingStartTime}"
                            tvName.text = "讲师: ${it.lecturerName}"
                            tvContent.text = it.title
                            if (it.meetingProcess == 1) {
                                Glide.with(mContext).load(R.mipmap.live_starting).into(imgState)
                                flyLiveDoing?.visibility = View.VISIBLE
                            } else {
                                Glide.with(mContext).load(R.mipmap.soon_starting).into(imgState)
                            }

                            tvTime.setOnClickListener {view->
                                if (it.isCollection == 0) {
                                    presenter?.subscribeMeeting(it.id,position,it.isMeeting)
                                } else {
                                    presenter?.cancelSubscribe(it.id,position,it.isMeeting)
                                    flyLiveDoing?.visibility = View.GONE
                                }
                            }


                            view.itemView.setOnClickListener { view ->

                                if (position >= list.size || position < 0) {
                                    MToast.makeTextShort(if (activity == null) BaseApplication.getInstance() else activity!!, "数据出现错误 请刷新后再试")
                                    return@setOnClickListener
                                }

                                if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
                                    showPhoneStatus(1, list[position], list[position].isToken)
                                    return@setOnClickListener
                                }
                                if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
                                    showPhoneStatus(2, list[position], list[position].isToken)
                                    return@setOnClickListener
                                }

                                /*	initDialog	(list.get(position), list.get(position).getIsToken());*/

                                /*	initDialog	(list.get(position), list.get(position).getIsToken());*/
                                try {
                                    if (list[position].isToken.toDouble() == 1.0) { //需要加入码的
                                        if (activity != null) {
                                            showNeedCodeDialog((activity as AppCompatActivity?)!!, list[position].id)
                                        }
                                    } else {
                                        if (activity != null) {
                                            showNoCodeDialog((activity as AppCompatActivity?)!!, list[position].id)
                                        }
                                    }
                                } catch (e: Exception) {
                                    if (activity != null) {
                                        showNeedCodeDialog((activity as AppCompatActivity?)!!, list[position].id)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            mAdapter?.notifyData(list)
        }
        recycler?.itemAnimator = null
        recycler?.adapter = mAdapter
    }

    fun getLiveAdapter() : BaseRecyclerViewAdapter<MeetingsData.DataBean.ListBean>? {
        return mAdapter
    }

    private fun showPhoneStatus(i: Int, meeting: MeetingsData.DataBean.ListBean, token: String?) {
        mPhoneStatusDialog = AlertDialog(mContext)
                .builder()
                .setTitle("提示")
                .setMsg(if (i == 1) "检测到摄像头关闭 是否打开？" else "检测到麦克风关闭 是否打开？")
                .setNegativeButton("进入会议") {
                    mPhoneStatusDialog!!.dismiss()
                    try {
                        if (meeting.isToken.toDouble() == 1.0) { //需要加入码
                            if (activity != null) {
                                showNeedCodeDialog((activity as AppCompatActivity?)!!, meeting.id)
                            }
                        } else {
                            if (activity != null) {
                                showNoCodeDialog((activity as AppCompatActivity?)!!, meeting.id)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        if (activity != null) {
                            showNeedCodeDialog((activity as AppCompatActivity?)!!, meeting.id)
                        }
                    }
                }
                .setPositiveButton("去设置") {
                    mPhoneStatusDialog!!.dismiss()
                    mContext.startActivity(Intent(mContext, MeetingSettingActivity::class.java))
                }
        mPhoneStatusDialog?.show()
    }
}
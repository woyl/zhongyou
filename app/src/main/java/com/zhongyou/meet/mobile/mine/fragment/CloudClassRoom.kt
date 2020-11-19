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
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.BaseApplication
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter
import com.zhongyou.meet.mobile.core.JoinMeetingDialog
import com.zhongyou.meet.mobile.entiy.MyAllCourse
import com.zhongyou.meet.mobile.mvp.presenter.MyClassRoomPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity
import com.zhongyou.meet.mobile.utils.MMKVHelper
import com.zhongyou.meet.mobile.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_class_room_live.*
import kotlinx.android.synthetic.main.fragment_class_room_live.emptyView
import kotlinx.android.synthetic.main.fragment_class_room_live.recycler
import me.jessyan.autosize.utils.AutoSizeUtils

class CloudClassRoom :BaseLazyLoadFragment<IPresenter>() {

    private var mCollectMeetingsAdapter: BaseRecyclerViewAdapter<MyAllCourse.CollectMeetingsBean>? = null
    private var mCollectionPresenter: MyClassRoomPresenter? = null
    private var mPhoneStatusDialog: AlertDialog? = null

    companion object {
        fun newInstance() :CloudClassRoom?{
            return CloudClassRoom()
        }
    }

    override fun lazyLoadData() {
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
    }

    override fun initData(savedInstanceState: Bundle?) {
        recycler.layoutManager = LinearLayoutManager(mContext)
//        smart_refresh?.setOnRefreshListener {
//            mCollectionPresenter?.getCollectMeeting()
//        }
    }

    override fun setData(data: Any?) {
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_class_room_live, container, false)
    }

    fun setPrasenterOne(mPresenter: MyClassRoomPresenter?) {
        mCollectionPresenter = mPresenter
        mCollectionPresenter?.getCollectMeeting()
    }

    fun setData(myAllCourse: MyAllCourse?) {
        myAllCourse?.let {
            if (it.collectMeetings == null) {
                emptyView.visibility = View.VISIBLE
                recycler.visibility = View.GONE
            }
            it.collectMeetings?.let { collects ->
                if (collects.size <= 0) {
                    emptyView.visibility = View.VISIBLE
                    recycler.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                    setAdapter(collects)
                }
            }
        }
    }

    fun onRefresh(refreshLayout:RefreshLayout?) {
        refreshLayout?.layout?.postDelayed({
            mCollectionPresenter?.getCollectMeeting()
        },1000)
    }

    fun cancelSubcribeMeeting(isSuccess: Boolean, meetingId: String) {
        //取消订阅成功后移除当前项
        if (isSuccess && mCollectMeetingsAdapter != null){
            ToastUtils.showToast("取消会议提醒")
            val list = mCollectMeetingsAdapter!!.list
            val index = list!!.indexOfFirst { it.id == meetingId }
            if (index < 0) return
            mCollectMeetingsAdapter!!.delete(index)
        }
    }

    private fun setAdapter(collects: List<MyAllCourse.CollectMeetingsBean>) {
        if (mCollectMeetingsAdapter == null) {
            mCollectMeetingsAdapter = object : BaseRecyclerViewAdapter<MyAllCourse.CollectMeetingsBean>(mContext, collects, R.layout.item_my_class_room) {
                @SuppressLint("SetTextI18n")
                override fun convert(holder: BaseRecyclerHolder?, item: MyAllCourse.CollectMeetingsBean?, position: Int, isScrolling: Boolean) {
                    holder?.let { view ->
                        val tvTime = holder.getView<AppCompatTextView>(R.id.tv_time)
                        val tvName = holder.getView<AppCompatTextView>(R.id.tv_name)
                        val tvContent = holder.getView<AppCompatTextView>(R.id.tv_content)
                        val imgState = holder.getView<AppCompatImageView>(R.id.img_state)
                        val img_small_head = holder.getView<ImageView>(R.id.img_small_head)
                        val img_head = holder.getView<AppCompatImageView>(R.id.img_head)
                        val roundedCorners = RoundedCorners( AutoSizeUtils.pt2px(mContext, 10f))
                        val options = RequestOptions.bitmapTransform(roundedCorners)
                        item?.let {
                            holder.setImageByUrlWithCorner(R.id.img_head,
                                    it.playUrl, R.drawable.dafault_course,
                                    R.drawable.dafault_course, AutoSizeUtils.pt2px(holder.itemView.context, 16f))

                            Glide.with(mContext).load(R.mipmap.bg_meeting_item_a).apply(options).placeholder(R.drawable.defaule_banner).error(R.drawable.defaule_banner).into(img_head)

//                            tvName.text = it.lecturerInfo
                            val drawable = resources.getDrawable(R.mipmap.notice_on)
                            drawable.setBounds(0,0,drawable.minimumWidth,drawable.minimumHeight)
                            tvTime.setCompoundDrawables(null,null,drawable,null)

                            tvTime.text = "直播时间:${it.startTime}"
                            tvContent.text = it.title
                            if (it.meetingProcess == 1) {
                                Glide.with(mContext).load(R.mipmap.live_starting).into(imgState)
                            } else {
                                Glide.with(mContext).load(R.mipmap.no_start).into(imgState)
                            }


                            view.itemView.setOnClickListener { view ->

                                if (position >= collects.size || position < 0) {
                                    MToast.makeTextShort(if (activity == null) BaseApplication.getInstance() else activity!!, "数据出现错误 请刷新后再试")
                                    return@setOnClickListener
                                }

                                if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
                                    showPhoneStatus(1, collects[position])
                                    return@setOnClickListener
                                }
                                if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
                                    showPhoneStatus(2, collects[position])
                                    return@setOnClickListener
                                }

                                /*	initDialog	(list.get(position), list.get(position).getIsToken());*/

                                /*	initDialog	(list.get(position), list.get(position).getIsToken());*/
                                try {
                                    if (collects[position].isToken.toDouble() == 1.0) { //需要加入码的
                                        if (activity != null) {
                                            JoinMeetingDialog.showNeedCodeDialog((activity as AppCompatActivity?)!!, collects[position].id)
                                        }
                                    } else {
                                        if (activity != null) {
                                            JoinMeetingDialog.showNoCodeDialog((activity as AppCompatActivity?)!!, collects[position].id)
                                        }
                                    }
                                } catch (e: Exception) {
                                    if (activity != null) {
                                        JoinMeetingDialog.showNeedCodeDialog((activity as AppCompatActivity?)!!, collects[position].id)
                                    }
                                }
                            }

                            tvTime.setOnClickListener {_->
                                mCollectionPresenter?.cancelSubscribe(it.id, true)
                            }
                        }
                    }
                }
            }
        } else {
            mCollectMeetingsAdapter?.notifyData(collects)
        }

        recycler.adapter = mCollectMeetingsAdapter
    }

    private fun showPhoneStatus(i: Int, meeting: MyAllCourse.CollectMeetingsBean) {
        mPhoneStatusDialog = AlertDialog(mContext)
                .builder()
                .setTitle("提示")
                .setMsg(if (i == 1) "检测到摄像头关闭 是否打开？" else "检测到麦克风关闭 是否打开？")
                .setNegativeButton("进入会议") {
                    mPhoneStatusDialog!!.dismiss()
                    try {
                        if (meeting.isToken.toDouble() == 1.0) { //需要加入码
                            if (activity != null) {
                                JoinMeetingDialog.showNeedCodeDialog((activity as AppCompatActivity?)!!, meeting.id)
                            }
                        } else {
                            if (activity != null) {
                                JoinMeetingDialog.showNoCodeDialog((activity as AppCompatActivity?)!!, meeting.id)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        if (activity != null) {
                            JoinMeetingDialog.showNeedCodeDialog((activity as AppCompatActivity?)!!, meeting.id)
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
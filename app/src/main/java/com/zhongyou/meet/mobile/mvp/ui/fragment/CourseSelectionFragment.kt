package com.zhongyou.meet.mobile.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jess.arms.base.BaseLazyLoadFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.RxBus
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.v3.MessageDialog
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter
import com.zhongyou.meet.mobile.entiy.MakerDetail
import com.zhongyou.meet.mobile.event.UnLockSeriesResultEvent
import com.zhongyou.meet.mobile.event.UnlockEvent
import com.zhongyou.meet.mobile.mvp.presenter.NewMakerDetailPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.*
import com.zhongyou.meet.mobile.utils.CountDownTimerUtil
import com.zhongyou.meet.mobile.utils.Page
import com.zhongyou.meet.mobile.utils.ToastUtils
import com.zhongyou.meet.mobile.view.itemdecoration.LinearLayoutItemDecoration
import kotlinx.android.synthetic.main.fragment_course_details.*
import kotlinx.android.synthetic.main.fragment_new_makercoruse_other.*
import me.jessyan.autosize.utils.AutoSizeUtils

@Page(name = "选课的Fragment")
class CourseSelectionFragment : BaseLazyLoadFragment<IPresenter>() {

    private var adapter : BaseRecyclerViewAdapter<MakerDetail.SubPagesBean>?= null
    private var lists = mutableListOf<MakerDetail.SubPagesBean>()
    private var isAuth = 0
    private var detailsPresenter : NewMakerDetailPresenter?= null
    private var mSeriesId: String? = null
    private var activitys: NewMakerDetailActivity?= null

    override fun lazyLoadData() {
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
    }

    override fun initData(savedInstanceState: Bundle?) {

        recycler.layoutManager = LinearLayoutManager(mContext)
        if (recycler.itemDecorationCount == 0) {
            recycler.addItemDecoration(LinearLayoutItemDecoration(AutoSizeUtils.pt2px(activity, 30f)))
        }
        if (adapter != null) {
            adapter?.notifyDataSetChanged()
        } else {
            adapter = object :BaseRecyclerViewAdapter<MakerDetail.SubPagesBean> (mContext,lists,R.layout.item_selection_course) {
                @SuppressLint("SetTextI18n")
                override fun convert(holder: BaseRecyclerHolder?, item: MakerDetail.SubPagesBean?, position: Int, isScrolling: Boolean) {
                    holder?.let {
                        val tv_title = holder.getView<AppCompatTextView>(R.id.tv_title)
                        val tv_time = holder.getView<AppCompatTextView>(R.id.tv_time)
                        item?.let { data ->
                            holder.setImageByUrlWithCorner(R.id.img_video, if (data.subPageURL.isEmpty()) "" else data.subPageURL,
                                    R.drawable.defaule_banner, R.drawable.defaule_banner, AutoSizeUtils.pt2px(holder.itemView.context, 16f))
                            tv_title.text = data.name
                            tv_time.text = "课程时长:${data.resourceTime}"
                        }

                        val needBuyItemCourseLayout = holder.getView<RelativeLayout>(R.id.needBuyItemCourseLayout)
                        if (isAuth != 1) {
                            needBuyItemCourseLayout.visibility = View.GONE
                        } else {
                            needBuyItemCourseLayout.visibility = View.VISIBLE
                        }

                        it.itemView.setOnClickListener {
                            //type 1：详情页面 2：直播；3 视频；4：音频
                            if (isAuth == 1) {
                                MessageDialog.show(activitys!!, "提示", "请先解锁课程", "解锁").onOkButtonClickListener = OnDialogButtonClickListener { baseDialog, v ->
                                    if (detailsPresenter != null) {
                                        detailsPresenter?.unLockSeries(mSeriesId)
                                    }
                                    false
                                }
                                return@setOnClickListener
                            }
                            activitys?.let {
                                when (lists[position].type) {
                                    1 -> activitys!!.launchActivity(Intent(activitys, NewMakerDetailActivity::class.java).putExtra("pageId", lists[position].subPageId))
                                    2 -> activitys!!.launchActivity(Intent(activitys, MakerCourseMeetDetailActivity::class.java).putExtra("pageId", lists[position].subPageId))
                                    3 -> activitys!!.launchActivity(Intent(activitys, MakerCourseVideoDetailActivity::class.java).putExtra("pageId", lists[position].subPageId))
                                    4 -> activitys!!.launchActivity(Intent(activitys, MakerCourseAudioDetailActivity::class.java).putExtra("pageId", lists[position].subPageId))
                                }
                            }
                        }
                    }
                }
            }
            recycler.adapter = adapter
        }


        RxBus.handleMessage { t ->
            when (t) {
                is UnLockSeriesResultEvent -> {
                    //解锁
                    if (t.isSuccess) {
                        isAuth = 2
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun setData(data: Any?) {
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_maker_selection,container,false)
    }
    
    fun setSubPagesBeanData(data: MutableList<MakerDetail.SubPagesBean>?,isAuth:Int?,detailsPresenter : NewMakerDetailPresenter?,mSeriesId: String?,activity: NewMakerDetailActivity){
        if (data != null) {
            this.lists = data
            adapter?.notifyData(data)
            if (isAuth != null) {
                this.isAuth = isAuth
            }
            this.detailsPresenter = detailsPresenter
            this.mSeriesId = mSeriesId
            this.activitys = activity
        }
    }
}
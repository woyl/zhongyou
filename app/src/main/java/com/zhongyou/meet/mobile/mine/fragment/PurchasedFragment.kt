package com.zhongyou.meet.mobile.mine.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jess.arms.base.BaseLazyLoadFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.RxBus
import com.like.LikeButton
import com.like.OnLikeListener
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter
import com.zhongyou.meet.mobile.entiy.MyAllCourse
import com.zhongyou.meet.mobile.entiy.MyAllCourse.AuthSeriesBean
import com.zhongyou.meet.mobile.event.ColloctionEvent
import com.zhongyou.meet.mobile.mvp.contract.MyCourseContract
import com.zhongyou.meet.mobile.mvp.presenter.MyCoursePresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity
import com.zhongyou.meet.mobile.utils.DisplayUtil
import com.zhongyou.meet.mobile.utils.listener.MonitorThreeListener
import com.zhongyou.meet.mobile.view.itemdecoration.LinearLayoutItemDecoration
import kotlinx.android.synthetic.main.fragment_new_makercoruse_other.*
import me.jessyan.autosize.utils.AutoSizeUtils

class PurchasedFragment : BaseLazyLoadFragment<IPresenter>() {

    private var mAdapter: BaseRecyclerViewAdapter<AuthSeriesBean>? = null
    private var mCollectionPresenter: MyCoursePresenter? = null

    private var monitorListener: MonitorThreeListener<String, Int, Int>? = null

    fun setMonitor(monitorListener: MonitorThreeListener<String, Int, Int>?) {
        this.monitorListener = monitorListener
    }

    companion object {
        fun newInstance(): PurchasedFragment? {
            return PurchasedFragment()
        }
    }

    override fun lazyLoadData() {
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
    }

    override fun initData(savedInstanceState: Bundle?) {
        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.addItemDecoration(LinearLayoutItemDecoration(DisplayUtil.dip2px(mContext, 15f)))
        smart_refresh?.setOnRefreshListener {
            mCollectionPresenter?.getCollectMeeting(1)
        }
        smart_refresh?.setEnableLoadMore(false)
        RxBus.handleMessage { o ->
            if (o is ColloctionEvent) {
                if (o.isFrush) {
                    smart_refresh?.autoRefresh()
                }
            }
        }
    }

    override fun setData(data: Any?) {
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_new_makercoruse_other, container, false)
    }

    fun setPrasenterOne(mPresenter: MyCoursePresenter?) {
        mCollectionPresenter = mPresenter
        mCollectionPresenter?.getCollectMeeting(1)
    }

    fun finshRefush() {
        smart_refresh?.finishRefresh()
    }

    fun setData(myAllCourse: MyAllCourse?) {
        myAllCourse?.let {
            if (it.authSeries == null) {
                emptyView?.visibility = View.VISIBLE
                recycler?.visibility = View.GONE
            }
            it.authSeries?.let { auths ->
                if (auths.size <= 0) {
                    emptyView?.visibility = View.VISIBLE
                    recycler?.visibility = View.GONE
                } else {
                    emptyView?.visibility = View.GONE
                    recycler?.visibility = View.VISIBLE
                    setAdapters(it)
                }
            }
        }
    }

    fun refreshData() {
        smart_refresh?.autoRefresh()
    }

    private fun setAdapters(it: MyAllCourse) {
        if (mAdapter == null) {
            mAdapter = object : BaseRecyclerViewAdapter<AuthSeriesBean>(mContext, it.authSeries, R.layout.item_new_maker_course_other) {
                @SuppressLint("SetTextI18n")
                override fun convert(holder: BaseRecyclerHolder?, item: AuthSeriesBean?, position: Int, isScrolling: Boolean) {
                    holder?.let { view ->
                        val tvTitle = holder.getView<AppCompatTextView>(R.id.tv_title)
                        val tvName = holder.getView<AppCompatTextView>(R.id.tv_name)
                        val tvContent = holder.getView<AppCompatTextView>(R.id.tv_content)
                        val tvUp = holder.getView<AppCompatTextView>(R.id.tv_up)
                        val tvStudy = holder.getView<AppCompatTextView>(R.id.tv_study)
                        val imgCollection = holder.getView<LikeButton>(R.id.collection_like)

                        item?.let {
                            holder.setImageByUrlWithCorner(R.id.img_head,
                                    it.pictureURL, R.drawable.dafault_course,
                                    R.drawable.dafault_course, AutoSizeUtils.pt2px(holder.itemView.context, 16f))
                            tvTitle.text = it.name
                            tvName.text = it.lecturerInfo
                            tvContent.text = it.seriesIntro
                            tvUp.text = "更新${it.videoNum}节"
                            tvStudy.text = it.studyNumStr

                            imgCollection.isLiked = it.isSign != 0

                            imgCollection.setOnLikeListener(object : OnLikeListener {
                                override fun liked(likeButton: LikeButton?) {
                                    monitorListener?.OnMonitor(it.id, 1, position)
                                }

                                override fun unLiked(likeButton: LikeButton?) {
                                    monitorListener?.OnMonitor(it.id, 0, position)
                                }
                            })

                            view.itemView.setOnClickListener { view ->
                                startActivity(Intent(mContext, NewMakerDetailActivity::class.java)
                                        .putExtra("pageId", it.pageId)
                                        .putExtra("isSignUp", it.isSignUp)
                                        .putExtra("seriesId", it.id)
                                        .putExtra("isAuth", it.isAuth)
                                        .putExtra("url", it.pictureURL))
                            }
                        }
                    }
                }
            }
        } else {
            mAdapter?.notifyData(it.authSeries)
        }

        recycler?.adapter = mAdapter
    }
}
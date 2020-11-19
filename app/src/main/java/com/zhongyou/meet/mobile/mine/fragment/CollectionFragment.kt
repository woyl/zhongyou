package com.zhongyou.meet.mobile.mine.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
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
import com.zhongyou.meet.mobile.entiy.MyAllCourse.SignUpSeriesBean
import com.zhongyou.meet.mobile.event.ColloctionEvent
import com.zhongyou.meet.mobile.mvp.presenter.MyCoursePresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity
import com.zhongyou.meet.mobile.utils.DisplayUtil
import com.zhongyou.meet.mobile.utils.listener.MonitorThreeListener
import com.zhongyou.meet.mobile.view.itemdecoration.LinearLayoutItemDecoration
import kotlinx.android.synthetic.main.fragment_new_makercoruse_other.*
import me.jessyan.autosize.utils.AutoSizeUtils
import timber.log.Timber

class CollectionFragment : BaseLazyLoadFragment<IPresenter>() {

    private var mSignUpViewAdapter: BaseRecyclerViewAdapter<SignUpSeriesBean>? = null
    private var mCollectionPresenter: MyCoursePresenter? = null
    private var monitorListener: MonitorThreeListener<String, Int, Int>? = null

    private var datas = mutableListOf<SignUpSeriesBean>()

    fun setMonitor(monitorListener: MonitorThreeListener<String, Int, Int>?) {
        this.monitorListener = monitorListener
    }

    companion object {
        fun newInstance(): CollectionFragment? {
            return CollectionFragment()
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
            mCollectionPresenter?.getCollectMeeting(0)
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

    fun setPrasenterOne(mPresenter: MyCoursePresenter?) {
        mCollectionPresenter = mPresenter
        mCollectionPresenter?.getCollectMeeting(0)
    }

    fun finshRefush() {
        smart_refresh?.finishRefresh()
    }

    override fun setData(data: Any?) {
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_new_makercoruse_other, container, false)
    }

    fun setData(myAllCourse: MyAllCourse?) {
        myAllCourse?.let {
            if (it.signUpSeries == null) {
                emptyView?.visibility = View.VISIBLE
                recycler?.visibility = View.GONE
            }
            it.signUpSeries?.let { signUps ->
                if (signUps.size <= 0) {
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

    fun reMoveDatas(position: Int) {
        if (datas.size > 0 && datas.size > position) {
            datas.remove(datas[position])
            mSignUpViewAdapter?.notifyItemRemoved(position)
            mSignUpViewAdapter?.notifyItemRangeChanged(0,datas.size)
        }
    }

    private fun setAdapters(it: MyAllCourse) {

        datas.addAll(it.signUpSeries)

        if (mSignUpViewAdapter == null) {
            mSignUpViewAdapter = object : BaseRecyclerViewAdapter<SignUpSeriesBean>(mContext, datas, R.layout.item_new_maker_course_other) {
                @SuppressLint("SetTextI18n")
                override fun convert(holder: BaseRecyclerHolder?, item: SignUpSeriesBean?, position: Int, isScrolling: Boolean) {
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


//                            context?.let { conte ->
//                                if (it.isSign == 0) {
//                                    Glide.with(conte).load(R.mipmap.course_nocollection).into(imgCollection)
//                                } else {
//                                    Glide.with(conte).load(R.mipmap.course_collection).into(imgCollection)
//                                }
//                            }

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
            mSignUpViewAdapter?.notifyData(datas)
        }
        recycler?.itemAnimator = null
        recycler?.adapter = mSignUpViewAdapter

    }

}
package com.zhongyou.meet.mobile.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.jess.arms.base.BaseLazyLoadFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.RxBus
import com.jess.arms.utils.RxLifecycleUtils
import com.like.LikeButton
import com.like.OnLikeListener
import com.maning.mndialoglibrary.MToast
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.entiy.MakerColumn
import com.zhongyou.meet.mobile.entiy.MakerColumn.SeriesBean
import com.zhongyou.meet.mobile.event.CollectionHomeEvent
import com.zhongyou.meet.mobile.event.CollectionOtherEvent
import com.zhongyou.meet.mobile.event.ColloctionEvent
import com.zhongyou.meet.mobile.event.UnlockEvent
import com.zhongyou.meet.mobile.mvp.presenter.NewMakerPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity
import com.zhongyou.meet.mobile.utils.DisplayUtil
import com.zhongyou.meet.mobile.utils.listener.MonitorThreeListener
import com.zhongyou.meet.mobile.view.itemdecoration.LinearLayoutItemDecoration
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_new_makercoruse_other.*
import kotlinx.coroutines.*
import me.jessyan.autosize.utils.AutoSizeUtils

class NewMakerCourseOtherFragment : BaseLazyLoadFragment<IPresenter>() {

    private var adapter: BaseRecyclerViewAdapter<MakerColumn.SeriesBean.ListBean>? = null

//    private var scope = CoroutineScope(Dispatchers.Main)


    private var mTotalPage = "1"
    private var mCurrentPage = 1
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()

    private var courseID: String? = null
    private var list = mutableListOf<SeriesBean.ListBean>()
    private var size = -1

    private var persenter: NewMakerPresenter?= null

    private var monitorListener: MonitorThreeListener<String, Int, Int>?= null

    fun setMonitor(monitorListener: MonitorThreeListener<String, Int, Int>?) {
        this.monitorListener = monitorListener
    }


    companion object {
        var ID = "courseID"
        var LIST = "list"
        var TOTAL_PAGE = "page"
        fun newInstanse(courseID: String?, size : Int?, totalPage: String?) : NewMakerCourseOtherFragment?{
            val ags = Bundle()
            ags.putString(ID,courseID)
            ags.putInt(LIST,size!!)
            ags.putString(TOTAL_PAGE,totalPage)
            val fragment = NewMakerCourseOtherFragment()
            fragment.arguments = ags
            return fragment
        }
    }

//    init {
//        this.courseID = courseID
//        if (totalPage != null) {
//            this.mTotalPage = totalPage
//        }
//        if (listBean != null) {
//            this.list = listBean
//        }
//    }

    override fun lazyLoadData() {
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCompositeDisposable != null) {
            mCompositeDisposable?.clear() //保证 Activity 结束时取消所有正在执行的订阅
        }
    }

    private fun getSeriesPageByType(courseID: String?, mCurrentPage: Int, b: Boolean): MutableList<SeriesBean.ListBean> {
        HttpsRequest.provideClientApi().getSeriesPageByType(courseID, mCurrentPage, Constant.pageSize).compose(RxLifecycleUtils.bindToLifecycle(this))
                .compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject?>() {
                    override fun onSubscribe(d: Disposable) {
                        mCompositeDisposable?.add(d)
                    }

                    override fun _onNext(jsonObject: JSONObject?) {
                        jsonObject?.let {
                            if (it.getInteger("errcode") == 0) {
                                if (mCurrentPage >= it.getJSONObject("data").getInteger("totalPage")) {
                                    smart_refresh?.setEnableLoadMore(false)
                                } else {
                                    smart_refresh?.setEnableLoadMore(true)
                                }
                                val data = jsonObject.getJSONObject("data").getJSONArray("list")
                                if (mCurrentPage == 1) {
                                    list = JSON.parseArray(data.toJSONString(), SeriesBean.ListBean::class.java)
                                } else {
                                    list.addAll(JSON.parseArray(data.toJSONString(), SeriesBean.ListBean::class.java))
                                }
                                if (list.size <= 0) {
                                    emptyView.visibility = View.VISIBLE
                                    recycler.visibility = View.GONE
                                } else {
                                    emptyView.visibility = View.GONE
                                    recycler.visibility = View.VISIBLE
                                    setOtherAdapter()
                                }
                            } else {
                                MToast.makeTextShort(activity!!, jsonObject.getString("errmsg"))
                                emptyView.visibility = View.VISIBLE
                                recycler.visibility = View.GONE
                            }
                        }
                    }

                    override fun onComplete() {
                        super.onComplete()
                        smart_refresh?.finishLoadMore()
                        smart_refresh?.finishRefresh()
                    }

                    override fun _onError(code: Int, msg: String) {
                        super._onError(code, msg)
                        emptyView.visibility = View.VISIBLE
                        recycler.visibility = View.GONE
                        smart_refresh?.finishLoadMore()
                        smart_refresh?.finishRefresh()
                    }
                })
        return list
    }

    private fun setOtherAdapter() {
        if (adapter == null) {
            adapter = object : BaseRecyclerViewAdapter<MakerColumn.SeriesBean.ListBean>(mContext, list, R.layout.item_new_maker_course_other) {
                @SuppressLint("SetTextI18n")
                override fun convert(holder: BaseRecyclerHolder?, item: MakerColumn.SeriesBean.ListBean?, position: Int, isScrolling: Boolean) {
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
                                    it.isSign = 1
                                    monitorListener?.OnMonitor(it.id,1,position)
                                }

                                override fun unLiked(likeButton: LikeButton?) {
                                    it.isSign = 0
                                    monitorListener?.OnMonitor(it.id,0,position)
                                }
                            })

//                            context?.let { cont ->
//                                if (it.isSign == 0) {
//                                    Glide.with(cont).load(R.mipmap.course_nocollection).into(imgCollection)
//                                } else {
//                                    Glide.with(cont).load(R.mipmap.course_collection).into(imgCollection)
//                                }
//                            }

                            view.itemView.setOnClickListener { view ->
                                startActivity(Intent(mContext, NewMakerDetailActivity::class.java)
                                        .putExtra("pageId", it.pageId)
                                        .putExtra("isSignUp", it.isSignUp)
                                        .putExtra("seriesId", it.id)
                                        .putExtra("isAuth", it.isAuth)
                                        .putExtra("url",it.pictureURL))
                            }
                        }


                    }
                }
            }
        } else {
            adapter?.notifyData(list)
        }
        recycler.adapter = adapter
    }

    fun setPresenter(persenter: NewMakerPresenter?) {
        this.persenter = persenter
    }

    fun notifyItemData(position:Int) {
        adapter?.notifyItemChanged(position)
    }

    @SuppressLint("LogNotTimber")
    override fun initData(savedInstanceState: Bundle?) {
        Log.i("mlt", "-------------NewMakerCourseOtherFragment--------initData=======")

        arguments?.let {
            courseID = it.getString(ID)
            mTotalPage = it.getString(TOTAL_PAGE).toString()
            size = it.getInt(LIST)
        }


        smart_refresh?.setOnRefreshListener {
//            scope.launch {
////                val result = async {
////
////                }
//                refreshData()
//            }
            refreshData()
        }

        smart_refresh?.setOnLoadMoreListener {

            mCurrentPage++
            getSeriesPageByType(courseID, mCurrentPage, false)
//            scope.launch {
//                val result = async {
//                    addMoreData()
//                }
//                adapter?.addNewData(result.await())
//            }
        }

        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.addItemDecoration(LinearLayoutItemDecoration(DisplayUtil.dip2px(mContext, 15f)))
        setOtherAdapter()

        refreshData()

        RxBus.handleMessage { o ->
            if (o is UnlockEvent) {
                if (o.isLock) {
                    refreshData()
                }
            } else if (o is ColloctionEvent) {
                if (o.isFrush) {
                    refreshData()
                }
            }
            else if (o is CollectionOtherEvent) {
                if (o.isRefrush) {
                    refreshData()
                }
            }
        }
    }

    private fun refreshData(): MutableList<MakerColumn.SeriesBean.ListBean>? {
        if (courseID != null) {
            mCurrentPage = 1
            return getSeriesPageByType(courseID, mCurrentPage, false)
        }
        return null
    }

    private suspend fun addMoreData(): MutableList<MakerColumn.SeriesBean.ListBean> {
        delay(500)
        mCurrentPage++
        return getSeriesPageByType(courseID, mCurrentPage, false)
    }

    override fun setData(data: Any?) {
        Log.i("mlt", "-------------NewMakerCourseOtherFragment--------setData=======")
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_new_makercoruse_other, container, false)
    }

    @ExperimentalCoroutinesApi
    override fun onStop() {
        super.onStop()
//        scope.cancel()
    }
}
package com.zhongyou.meet.mobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.like.LikeButton
import com.like.OnLikeListener
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter
import com.zhongyou.meet.mobile.entities.Data
import com.zhongyou.meet.mobile.entities.HomeLinkPage
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseAudioDetailActivity
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseMeetDetailActivity
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseVideoDetailActivity
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity
import com.zhongyou.meet.mobile.utils.DisplayUtil
import com.zhongyou.meet.mobile.utils.listener.MonitorFourLister
import com.zhongyou.meet.mobile.utils.listener.MonitorThreeListener
import com.zhongyou.meet.mobile.view.glide.RoundedCornersTransform
import com.zhongyou.meet.mobile.view.itemdecoration.GridSpacingItemDecoration
import me.jessyan.autosize.utils.AutoSizeUtils

class RecommendContentAdapter(context: Context, mHelper: LayoutHelper, list: MutableList<Data>?) : DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {

    private var mHelper: LayoutHelper? = null
    private var list: MutableList<Data>? = null
    private var context: Context? = null
    private var adapter: BaseRecyclerViewAdapter<HomeLinkPage>? = null
    private var monitorListener: MonitorFourLister<String?,Int, Int,Int>?= null

    fun setMonitor(monitorListener:MonitorFourLister<String?,Int,Int,Int>?) {
        this.monitorListener = monitorListener
    }

    init {
        this.mHelper = mHelper
        this.list = list
        this.context = context
    }

    class RecommendContentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title = itemView.findViewById<AppCompatTextView>(R.id.tv_title)
        val recycler_recommend_list = itemView.findViewById<RecyclerView>(R.id.recycler_recommend_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendContentHolder {
        return RecommendContentHolder(LayoutInflater.from(context).inflate(R.layout.item_recommend_list, parent, false))
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return mHelper!!
    }

    fun addList(lists: MutableList<Data>?) {
        list?.let {
            lists?.let { it1 ->
                list = it1
                notifyDataSetChanged()
            }
        }
    }

    fun getContentItemAdapter() : BaseRecyclerViewAdapter<HomeLinkPage>?{
        return adapter
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecommendContentHolder) {
            if (list.isNullOrEmpty()) {
                return
            }

            val gridLayoutManager = GridLayoutManager(context, 2)
            holder.recycler_recommend_list.layoutManager = gridLayoutManager
            if (holder.recycler_recommend_list.itemDecorationCount == 0) {
                holder.recycler_recommend_list.addItemDecoration(GridSpacingItemDecoration(2, DisplayUtil.dip2px(context, 9f), false))
            }

            holder.recycler_recommend_list.itemAnimator = null

            list?.let {
                holder.tv_title.text = list!![position].columnName

                adapter = object : BaseRecyclerViewAdapter<HomeLinkPage>(context, list!![position].homeLinkPages, R.layout.item_recommend_list_content) {
                    override fun convert(holder: BaseRecyclerHolder?, item: HomeLinkPage?, positionItem: Int, isScrolling: Boolean) {
                        holder?.let { view ->
                            val tvContext = view.getView<AppCompatTextView>(R.id.tv_content)
                            val tvName = view.getView<AppCompatTextView>(R.id.tv_name)
                            val tvUp = view.getView<AppCompatTextView>(R.id.tv_up)
                            val tvStudy = view.getView<AppCompatTextView>(R.id.tv_study)
                            val imgCollection = view.getView<LikeButton>(R.id.collection_like)
                            item?.let { data ->
                                tvContext.text = data.pageName
                                tvName.text = data.lecturerInfo
                                tvUp.text = "更新${data.videoNum}节"
                                tvStudy.text = data.studyNumStr

                                imgCollection.isLiked = data.isSign != 0

                                imgCollection.setOnLikeListener(object :OnLikeListener {
                                    override fun liked(likeButton: LikeButton?) {
                                        data.isSign = 1
                                        monitorListener?.OnMonitor(data.seriesId,1,position,positionItem)
                                    }

                                    override fun unLiked(likeButton: LikeButton?) {
                                        data.isSign = 0
                                        monitorListener?.OnMonitor(data.seriesId,0,position,positionItem)
                                    }
                                })

//                                holder.setImageByUrlWithCorner(
//                                        R.id.img_head,
//                                        item.imageUrl,
//                                        R.drawable.dafault_course,
//                                        R.drawable.dafault_course,
//                                        AutoSizeUtils.pt2px(holder.itemView.context, 16f))
                                val ron = RoundedCornersTransform(holder.itemView.context, AutoSizeUtils.pt2px(holder.itemView.context, 16f).toFloat())
                                ron.setNeedCorner(true, true, false, false)
                                val ops = RequestOptions().placeholder(R.drawable.dafault_course).transform(ron)
                                context?.let { it1 -> Glide.with(it1).asBitmap().load(item.imageUrl).apply(ops).into(holder.getView(R.id.img_head)) }

                                view.itemView.setOnClickListener {
                                    when (data.pageType) {
                                        1 -> context?.startActivity(Intent(context, NewMakerDetailActivity::class.java).putExtra("pageId", data.pageId))
                                        2 -> context?.startActivity(Intent(context, MakerCourseMeetDetailActivity::class.java).putExtra("pageId", data.pageId))
                                        3 -> context?.startActivity(Intent(context, MakerCourseVideoDetailActivity::class.java).putExtra("pageId", data.pageId))
                                        4 -> context?.startActivity(Intent(context, MakerCourseAudioDetailActivity::class.java).putExtra("pageId", data.pageId))
                                    }
                                }
                            }
                        }
                    }
                }
                holder.recycler_recommend_list.itemAnimator = null
                holder.recycler_recommend_list.adapter = adapter
            }
        }
    }
}
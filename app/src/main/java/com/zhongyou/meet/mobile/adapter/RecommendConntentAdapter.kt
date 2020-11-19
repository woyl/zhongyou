package com.zhongyou.meet.mobile.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter
import com.zhongyou.meet.mobile.core.MusicManager
import com.zhongyou.meet.mobile.core.PlayService
import com.zhongyou.meet.mobile.entiy.RecomandData
import com.zhongyou.meet.mobile.mvp.ui.activity.MoreDetailsActivity
import com.zhongyou.meet.mobile.utils.listener.MonitorTwoListener
import com.zhongyou.meet.mobile.view.CircularProgressView
import com.zhongyou.meet.mobile.view.CompletedProgressView
import timber.log.Timber

class RecommendConntentAdapter(monitorListener: MonitorTwoListener<Int, RecomandData.ChangeLessonByDayBean>?,
                               context: Context?,
                               list: MutableList<RecomandData.ChangeLessonByDayBean>?,
                               itemLayoutId: Int,
                               tvTiltle:AppCompatTextView) :
        BaseRecyclerViewAdapter<RecomandData.ChangeLessonByDayBean>(context, list, itemLayoutId) {

    private var context: Context? = null
    private var monitorListener: MonitorTwoListener<Int, RecomandData.ChangeLessonByDayBean>? = null
    private var tvTiltle:AppCompatTextView?= null

    init {
        this.context = context
        this.monitorListener = monitorListener
        this.tvTiltle = tvTiltle
    }

    override fun convert(holder: BaseRecyclerHolder?, item: RecomandData.ChangeLessonByDayBean?, position: Int, isScrolling: Boolean) {
        item?.let { data ->
            context?.let { it1 ->
                val options = RequestOptions.bitmapTransform(RoundedCorners(30)) //图片圆角为30
                val requestOptions = RequestOptions()

                requestOptions.placeholder(R.drawable.dafault_course)
                RequestOptions.circleCropTransform()
                requestOptions.transforms(RoundedCorners(30))

                Constant.SMALL_URL_IMG = data.smallUrl

                Glide.with(it1)
                        .load(data.smallUrl)
                        .skipMemoryCache(false)
                        .apply(options)
                        .into(holder?.getView(R.id.img_head) as ImageView)
                Glide.with(it1).load(data.comentURL).into(holder.getView(R.id.descriptionImage))

                val imgPlayBt = holder.getView<AppCompatImageView>(R.id.img_play_button)
                if (data.type != 1) {
                    it1.let { Glide.with(it).load(R.mipmap.pause_music).into(imgPlayBt) }
                } else {
                    it1.let { Glide.with(it).load(R.mipmap.play_music).into(imgPlayBt) }
                }

//                tvTiltle?.text = "报！大咖有话说"

                holder.let {
                    it.setText(R.id.tv_title, if (data.name.isNullOrEmpty()) "" else data.name)
                    it.setText(R.id.tv_content, if (data.name.isNullOrEmpty()) "" else data.name)
//                    it.setText(R.id.tv_name, if (data.name.isNullOrEmpty()) "" else "主讲人: ${data.anchorName}")
                    //屏蔽 主讲人 文本
                    it.setText(R.id.tv_name, if (data.name.isNullOrEmpty()) "" else (data.anchorName ?: ""))
                }
            }
        }

        holder?.getView<FrameLayout>(R.id.fl_aniu)?.setOnClickListener {

            item?.let {it1 ->
                monitorListener?.OnMonitor(position, it1)
            }
        }

        holder?.itemView?.setOnClickListener {
            item?.let {
                context?.startActivity(Intent(context,MoreDetailsActivity::class.java)
                        .putExtra("id",it.id)
                        .putExtra("name",it.name)
                        .putExtra("belongTime",it.belongTime)
                        .putExtra("author",it.anchorName)
                        .putExtra("url",it.comentURL)
                        .putExtra("video",it.vioceURL))
            }
        }
    }

    override fun onBindViewHolder(holder: BaseRecyclerHolder, position: Int, payloads: MutableList<Any>) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val datas = payloads as MutableList<*>
//            val progress = holder.getView<CircularProgressView>(R.id.circle_progress)
            val progress = holder.getView<CompletedProgressView>(R.id.circle_progress)
            val data = datas[position] as RecomandData.ChangeLessonByDayBean
            if (data.currentDuration.toInt() != 0 && data.totalTime != 0) {
                val percentage = data.totalTime.div(100)
                val resultF = data.currentDuration.div(percentage)
                Timber.tag("mlt").i("-----------------------------%s", resultF)
                progress.setProgress(resultF.toInt())
            }

            val imgPlayBt = holder.getView<AppCompatImageView>(R.id.img_play_button)
            imgPlayBt.visibility = View.VISIBLE
            when (data.type) {
                1 -> {
                    context?.let { Glide.with(it).load(R.mipmap.play_music).into(imgPlayBt) }
                }
                else ->{
                    context?.let { Glide.with(it).load(R.mipmap.pause_music).into(imgPlayBt) }
                }

            }
        }
    }
}
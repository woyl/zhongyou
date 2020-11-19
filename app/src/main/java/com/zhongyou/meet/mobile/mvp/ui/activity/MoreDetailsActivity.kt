package com.zhongyou.meet.mobile.mvp.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.jess.arms.utils.RxBus
import com.jess.arms.utils.DeviceUtils
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.core.GlobalConsts
import com.zhongyou.meet.mobile.core.PlayService.MusicBinder
import com.zhongyou.meet.mobile.event.AdapaterRefushEvent
import com.zhongyou.meet.mobile.mvp.ui.fragment.NewMakerCourseFragment
import com.zhongyou.meet.mobile.receiver.MoreMakerDetailsReciver
import com.zhongyou.meet.mobile.utils.listener.MonitorTwoListener
import kotlinx.android.synthetic.main.activity_more_details.*
import kotlinx.android.synthetic.main.base_tool_bar_k.*
import timber.log.Timber

class MoreDetailsActivity : AppCompatActivity() {

    //    private var receiver: MusicInfoReceiver? = null
    private var receiver: MoreMakerDetailsReciver? = null
    private var context: Context? = null
    private var isFrist = true
    private var position = 0

    companion object {

        private var mMusicBinder: MusicBinder? = null

        fun setMusicBinder(binder: MusicBinder?) {
            mMusicBinder = binder
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_details)
        context = this



        intent?.let {
            tv_title.text = it.getStringExtra("name")
            title_k.text = "报！大咖有哦话说"
            position = it.getIntExtra("position", 0)

            tv_time_author.text = "${it.getStringExtra("belongTime")}  |  ${it.getStringExtra("author")}"
//            Glide.with(this).load(it.getStringExtra("url")).into(img_content)
            val stringUrl = it.getStringExtra("url")

            val requestOptions = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)//关键代码，加载原始大小
                    .format(DecodeFormat.PREFER_RGB_565)//设置为这种格式去掉透明度通道，可以减少内存占有
                    .placeholder(R.drawable.dafault_course)
                    .error(R.drawable.dafault_course)
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(stringUrl)
                    .into(img_content)

//            Glide.with(this)
//                    .asBitmap()
//                    .load(stringUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存所有数据
//                    .into(object : SimpleTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    val imageWidth = resource.width
//                    val imageHeight = resource.height
//                    val screenWidth = DeviceUtils.getScreenWidth(context)
//                    val screenHeight = DeviceUtils.getScreenHeight(context)
//
//                    val para: ViewGroup.LayoutParams = img_content.layoutParams
//
//                    if (imageWidth > screenWidth) {
//                        val scle = screenWidth / imageWidth
//                        val finshW = imageWidth * scle
//                        val finishH = imageHeight*scle
//                        para.height = finishH.toInt()
//                        para.width = finshW.toInt()
//                    } else {
//                        val scle = screenWidth / imageWidth
//                        val finshW = imageWidth * scle
//                        val finishH = imageHeight * scle
//                        para.height = finishH.toInt()
//                        para.width = finshW.toInt()
//                    }
//                    img_content.setImageBitmap(resource)
//                }
//            })


            mMusicBinder?.moreDetails = false
            fl_aniu.setOnClickListener { view ->

                if (isFrist) {
                    isFrist = false
                    if (mMusicBinder?.isPlaying!!) {
                        if (position == mMusicBinder?.position) {
                            mMusicBinder?.pause()
                            mMusicBinder?.setIsOtherPause(false)
                            context?.let { Glide.with(it).load(R.mipmap.pause_music).into(img_play_button) }
                        } else {
                            registerReceiver()
                            mMusicBinder?.pause()
                            mMusicBinder?.playMusic(it.getStringExtra("video"))
                            context?.let { Glide.with(it).load(R.mipmap.play_music).into(img_play_button) }
                            if (mMusicBinder?.oldPosition != mMusicBinder?.position) {
                                mMusicBinder?.oldPosition = mMusicBinder?.position!!
                            }
                        }
                    } else {
//                        if (position != mMusicBinder?.position) {
//                            registerReceiver()
//                            mMusicBinder?.playMusic(it.getStringExtra("video"))
//                            if (mMusicBinder?.oldPosition != mMusicBinder?.position) {
//                                mMusicBinder?.oldPosition = mMusicBinder?.position!!
//                            }
//                        }
                        registerReceiver()
                        mMusicBinder?.playMusic(it.getStringExtra("video"))
                        if (mMusicBinder?.oldPosition != mMusicBinder?.position) {
                            mMusicBinder?.oldPosition = mMusicBinder?.position!!
                        }
                    }
                } else {
                    if (mMusicBinder?.isPlaying!!) {
                        mMusicBinder?.pause()
                        context?.let { Glide.with(it).load(R.mipmap.pause_music).into(img_play_button) }
                    } else {
                        mMusicBinder?.start()
                        context?.let { Glide.with(it).load(R.mipmap.play_music).into(img_play_button) }
                    }
                }
            }
        }

        back.setOnClickListener { finish() }

    }

    override fun onResume() {
        super.onResume()
        if (mMusicBinder?.position == position) {
            registerReceiver()
        }
    }

    private fun registerReceiver() {
        receiver = MoreMakerDetailsReciver()
        val filter = IntentFilter()
        filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED)
        filter.addAction(GlobalConsts.ACTION_UPDATE_PROGRESS)
        filter.addAction(GlobalConsts.ACTION_STATR_MUSIC)
        filter.addAction(GlobalConsts.ACTION_PAUSE_MUSIC)
        filter.addAction(GlobalConsts.ACTION_LOCAL_MUSIC)
        filter.addAction(GlobalConsts.ACTION_ONLINE_MUSIC)
        filter.addAction(GlobalConsts.ACTION_NEXT_MUSIC)
        filter.addAction(GlobalConsts.ACTION_COMPLETE_MUSIC)
        registerReceiver(receiver, filter)
//        RxBus.sendMessage(AdapaterRefushEvent(true))
        receiver?.setMonitorListener(object : MonitorTwoListener<Context, Intent> {
            override fun OnMonitor(k: Context, v: Intent) {
                setReceiver(k, v)
            }

        })
    }

    private fun setReceiver(k: Context, intent: Intent) {
        when (intent?.action) {
            GlobalConsts.ACTION_UPDATE_PROGRESS -> {
                val current = intent.getIntExtra("current", NewMakerCourseFragment.typeZero)
                val total = intent.getIntExtra("total", NewMakerCourseFragment.typeZero)
                val percentage = total.div(100)
                val resultF = current.div(percentage)
                Timber.tag("mlt").i("-----------------------------%s", resultF)
                circle_progress_play.setProgress(resultF.toInt())
                mMusicBinder?.position = position
                applicationContext?.let { Glide.with(it).load(R.mipmap.play_music).into(img_play_button) }
            }
            GlobalConsts.ACTION_PAUSE_MUSIC -> {
                applicationContext?.let {
                    Glide.with(it).load(R.mipmap.pause_music).into(img_play_button)
                }
            }
            GlobalConsts.ACTION_MUSIC_STARTED -> {
                applicationContext?.let {
                    Glide.with(it).load(R.mipmap.play_music).into(img_play_button)
                }
            }
            GlobalConsts.ACTION_COMPLETE_MUSIC -> {
                circle_progress_play.setProgress(0)
                mMusicBinder?.moreDetails = false
                mMusicBinder?.pause()
                RxBus.sendMessage(AdapaterRefushEvent(true))
            }
        }
    }

    override fun onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver)
        }
        super.onDestroy()
    }

}
package com.zhongyou.meet.mobile.mvp.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.jess.arms.base.BaseLazyLoadFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.RxBus
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.adapter.RecommendAdapter
import com.zhongyou.meet.mobile.adapter.RecommendContentAdapter
import com.zhongyou.meet.mobile.core.GlobalConsts
import com.zhongyou.meet.mobile.core.MusicManager
import com.zhongyou.meet.mobile.core.PlayService
import com.zhongyou.meet.mobile.entities.Data
import com.zhongyou.meet.mobile.entiy.MoreAudio
import com.zhongyou.meet.mobile.entiy.RecomandData
import com.zhongyou.meet.mobile.event.AdapaterRefushEvent
import com.zhongyou.meet.mobile.event.CollectionHomeEvent
import com.zhongyou.meet.mobile.event.CollectionOtherEvent
import com.zhongyou.meet.mobile.event.RefreshHomeEvent
import com.zhongyou.meet.mobile.mvp.presenter.MakerPresenter
import com.zhongyou.meet.mobile.mvp.presenter.NewMakerPresenter
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseAudioActivity
import com.zhongyou.meet.mobile.receiver.NewMakerCourseReceiver
import com.zhongyou.meet.mobile.utils.Page
import com.zhongyou.meet.mobile.utils.listener.MonitorFourLister
import com.zhongyou.meet.mobile.utils.listener.MonitorListener
import com.zhongyou.meet.mobile.utils.listener.MonitorTwoListener
import kotlinx.android.synthetic.main.fragment_new_makercoruse_other.*
import kotlinx.coroutines.*
import java.util.ArrayList

@Page(name = "创客分类的Fragment")
class NewMakerCourseFragment : BaseLazyLoadFragment<MakerPresenter>() {

    private var linearAdapter: RecommendAdapter? = null
    private var play: PlayService.MusicBinder? = null
//    private var clickPosition = -1
//    private var receiver: MusicInfoReceiver? = null
    private var receiver : NewMakerCourseReceiver?= null

    private var linearListAdapter: RecommendContentAdapter? = null
    private val lists = mutableListOf<Data>()
    private var scope = CoroutineScope(Dispatchers.Main)
    private val mAudioList = mutableListOf<MoreAudio>()

    private var newMakerPresenter: NewMakerPresenter? = null
    private var isClickPlayAudio = false

    private var isRe = false
    private var isOnclickMore = false

    private var mReceiverTag = false

    companion object {
        const val typeZero = 0
        const val typeOne = 1
        const val typeTwo = 2

        fun newInstanse(): NewMakerCourseFragment? {
            return NewMakerCourseFragment()
        }
    }


    override fun lazyLoadData() {
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
    }

    override fun initData(savedInstanceState: Bundle?) {
        smart_refresh?.setOnRefreshListener {
//            scope.launch {
//
//            }

            RxBus.getDefault().send(RefreshHomeEvent(true))
            smart_refresh?.finishRefresh()
        }
        smart_refresh?.setEnableLoadMore(false)

        if (activity != null) {
            val layoutManager = VirtualLayoutManager(activity!!)
            recycler.layoutManager = layoutManager

            val linearLayoutHelper = LinearLayoutHelper()
            linearAdapter = RecommendAdapter(activity!!, linearLayoutHelper)
            linearAdapter?.setMonitorMore(object :MonitorListener<Boolean> {
                override fun OnMonitor(t: Boolean) {
                    if (t) {
                        play?.setIsOtherPause(true)
                        activity?.startActivity(Intent(context, MakerCourseAudioActivity::class.java))
                    }
                }
            })

            val linearLayoutHelperList = LinearLayoutHelper()
            linearListAdapter = RecommendContentAdapter(mContext, linearLayoutHelperList, lists)
            val adapters = mutableListOf<DelegateAdapter.Adapter<RecyclerView.ViewHolder>>()
            adapters.add(linearAdapter!!)
            adapters.add(linearListAdapter!!)
            val delegateAdapter = DelegateAdapter(layoutManager)
            delegateAdapter.addAdapters(adapters)
            recycler.adapter = delegateAdapter

            linearAdapter?.setMonitor(object : MonitorTwoListener<Int, RecomandData.ChangeLessonByDayBean> {
                override fun OnMonitor(k: Int, v: RecomandData.ChangeLessonByDayBean) {
                    isClickPlayAudio = true
                    play?.setIsOtherPause(true)
                    if (newMakerPresenter != null) {
                        play?.isPlaying?.let {
                            if (!it) {
                                newMakerPresenter?.clickCourse(v.id)
                            }
                        }
                    }

                    if (mAudioList.size == 0) {
                        newMakerPresenter?.getMoreAudio(1)
                    }

                    //clickPosition
                    if (play?.position == -1) {
                        playMusic(k, v, typeOne)
                    } else {
                        if (play?.position == k) {//clickPosition
                            linearAdapter?.let { it ->
                                it.getAutoUnscramble()?.let { auto ->
                                    when (auto[k].type) {
                                        typeZero -> playMusic(k, v, typeOne)
                                        typeOne -> {
                                            play?.pause()
                                            auto[k].type = typeTwo
                                        }
                                        typeTwo -> {
                                            play?.start()
                                            auto[k].type = typeOne
                                        }
                                    }
                                }
                            }
                        } else {
//                            linearAdapter?.let { it ->
//                                it.getAutoUnscramble()?.let { auto ->
//                                    auto[k].type = typeZero
//                                    auto[k].currentDuration = typeZero.toLong()
//                                    playMusic(k, v, typeOne)
//                                }
//                            }

                            linearAdapter?.let { it ->
                                it.getAutoUnscramble()?.let { auto ->
                                    when (auto[k].type) {
                                        typeZero -> playMusic(k, v, typeOne)
                                        typeOne -> {
                                            play?.pause()
                                            auto[k].type = typeTwo
                                        }
                                        typeTwo -> {
                                            play?.start()
                                            auto[k].type = typeOne
                                        }
                                    }
                                }
                            }
                        }
                    }
//                    linearAdapter?.notifyDataSetChanged()
                }

            })


            linearListAdapter?.setMonitor(object : MonitorFourLister<String?, Int, Int,Int> {
                override fun OnMonitor(k:String?, v: Int, t:Int,l:Int) {
//                    if (lists.size > 0 && lists[t].homeLinkPages.isNotEmpty()) {
//                        lists[t].homeLinkPages[l].isSign = 1
//                    }
                    newMakerPresenter?.signCourse(k,v,t,l,0)
                }
            })
        }

        RxBus.handleMessage { o ->
            if (o is AdapaterRefushEvent) {
                if (o.isRefush) {
                    isRe = true
                    val bean = RecomandData.ChangeLessonByDayBean()
                    play?.position?.let {
                        if (it == -1) return@let
                        if (play?.position!! < mAudioList.size) {
                            linearAdapter?.setAutoUnscramble(null)
                            bean.type = mAudioList[it].type
                            bean.currentDuration = mAudioList[it].currentDuration
                            bean.comentURL = mAudioList[it].comentURL
                            bean.totalTime = mAudioList[it].totalTime
                            bean.vioceURL = mAudioList[it].vioceURL
                            bean.name = mAudioList[it].name
                            bean.anchorName = mAudioList[it].anchorName
                            bean.belongTime = mAudioList[it].belongTime
                            bean.smallUrl = Constant.SMALL_URL_IMG
                            linearAdapter?.setAutoUnscramble(bean)
                        }
                    }
                }
            } else if (o is CollectionHomeEvent) {
                if (o.isRefrush) {
                    smart_refresh?.autoRefresh()
                }
            }
        }

    }

    fun getlinearListAdapter() :RecommendContentAdapter? {
        return linearListAdapter
    }

    //加载更多的音频数据
    fun setMoreAudio(dataLists: List<MoreAudio>?) {
        dataLists?.let {
            if (it.isNotEmpty()) {
                mAudioList.clear()
            }
            this.mAudioList.addAll(dataLists)
            //clickPosition
            play?.position?.let {
                it1 ->
//                play?.setGlobalPlayback(mAudioList as ArrayList<MoreAudio>?, it1)
                MusicManager.instance.addMusics(mAudioList,it1)
            }
        }

    }

    private fun playMusic(k: Int, v: RecomandData.ChangeLessonByDayBean, typeThree: Int) {
        linearAdapter?.let { it ->
            it.getAutoUnscramble()?.let {
                it[k].type = typeThree
            }
            it.getContentAdapter()?.notifyItemChanged(k)
        }
        play?.let {
            it.position = typeZero
            it.playMusic(v.vioceURL)
        }
    }

    override fun setData(data: Any?) {
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_new_makercourse, container, false)
    }

    fun setBannerData(bannerData: RecomandData) {
        linearAdapter?.setBannerData(bannerData)
    }

    /**首次加载和刷新 首次会加载更多的音频数据*/
    fun setAutoUnscramble(data: RecomandData.ChangeLessonByDayBean, newMakerPresenter: NewMakerPresenter?) {
        val position = MusicManager.instance.position
        if (MusicManager.instance.moreAudios.size > 0 && position != -1) {
            val bean = RecomandData.ChangeLessonByDayBean()
            linearAdapter?.setAutoUnscramble(null)
            bean.type = mAudioList[position].type
            bean.currentDuration = mAudioList[position].currentDuration
            bean.comentURL = mAudioList[position].comentURL
            bean.totalTime = mAudioList[position].totalTime
            bean.vioceURL = mAudioList[position].vioceURL
            bean.name = mAudioList[position].name
            bean.anchorName = mAudioList[position].anchorName
            bean.belongTime = mAudioList[position].belongTime
            bean.smallUrl = Constant.SMALL_URL_IMG
            linearAdapter?.setAutoUnscramble(bean)
        } else {
            linearAdapter?.setAutoUnscramble(data)
        }

        this.newMakerPresenter = newMakerPresenter
        if (!isOnclickMore) {
            if (mAudioList.size == 0) {
                newMakerPresenter?.getMoreAudio(1)
            }
            isOnclickMore = true
        }
    }

    fun setMusicBinder(play: PlayService.MusicBinder?) {
        this.play = play
        linearAdapter?.notifyDataSetChanged()
    }

    fun setHomePageBean(date: MutableList<Data>?) {
        date?.let {
            if (lists.size != 0) {
                lists.clear()
            }
            lists.addAll(it)
            linearListAdapter?.addList(lists)
        }
    }

    fun notifyItemData(position:Int) {
        linearListAdapter?.getContentItemAdapter()?.notifyItemChanged(position)
    }


    override fun onResume() {
        super.onResume()
//        linearAdapter?.startBanner()
        registerReceiver()
        if (isRe) {
            val bean = RecomandData.ChangeLessonByDayBean()
            play?.position?.let {
                if (play?.position!! < mAudioList.size) {
                    linearAdapter?.setAutoUnscramble(null)
                    bean.type = mAudioList[it].type
                    bean.currentDuration = mAudioList[it].currentDuration
                    bean.comentURL = mAudioList[it].comentURL
                    bean.totalTime = mAudioList[it].totalTime
                    bean.vioceURL = mAudioList[it].vioceURL
                    bean.name = mAudioList[it].name
                    bean.anchorName = mAudioList[it].anchorName
                    bean.belongTime = mAudioList[it].belongTime
                    bean.smallUrl = Constant.SMALL_URL_IMG
                    linearAdapter?.setAutoUnscramble(bean)
                }
            }
        }

        play?.isOtherPause?.let {
            if (!it) {
                linearAdapter?.getAutoUnscramble()?.let { lists->
                    if (lists.size > 0) {
                        lists[typeZero].type = typeTwo
                        linearAdapter?.getContentAdapter()?.notifyItemChanged(typeZero)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //clickPosition
        if (play?.position != -1) {
            //音乐暂停播放
            linearAdapter?.let { data ->

                if (mAudioList.size > 0) {
                    val datas = data.getAutoUnscrambleItem()
                    datas?.let {
                        it.type = typeTwo
                    }
                } else {
                    data.getAutoUnscramble()?.let {
                        //clickPosition
                        if (it.size > 0) {
                            it[typeZero].type = typeTwo
                        }
                    }
                }
                data.notifyItemRangeChanged(typeZero, typeOne)
                if (mReceiverTag) {
                    if (receiver != null && activity != null) {
                        activity?.unregisterReceiver(receiver)
                    }
                }
//                data.pauseBanner()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //clickPosition
        if (play?.position != -1) {
            linearAdapter?.let { data ->

                if (mAudioList.size > 0) {
                    val datas = data.getAutoUnscrambleItem()
                    datas?.let {
                        it.type = typeTwo
                        it.currentDuration = typeZero.toLong()
                    }
                } else {
                    data.getAutoUnscramble()?.let {
                        //clickPosition
                        if (it.size > 0) {
                            it[typeZero].type = typeTwo
                            it[typeZero].currentDuration = typeZero.toLong()
                        }
                    }
                }

                data.notifyItemRangeChanged(typeZero, typeOne)
                if (play != null) {
                    play?.pause()
                }
                if (mReceiverTag) {
                    mReceiverTag = false
                    if (receiver != null && activity != null) {
                        activity?.unregisterReceiver(receiver)
                    }
                }

                //clickPosition
                play?.position = -1
            }
        }
        scope.cancel()
    }

    private fun registerReceiver() {
//        receiver = MusicInfoReceiver()
        if (!mReceiverTag) {
            receiver = NewMakerCourseReceiver()
            val filter = IntentFilter()
            filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED)
            filter.addAction(GlobalConsts.ACTION_UPDATE_PROGRESS)
            filter.addAction(GlobalConsts.ACTION_STATR_MUSIC)
            filter.addAction(GlobalConsts.ACTION_PAUSE_MUSIC)
            filter.addAction(GlobalConsts.ACTION_LOCAL_MUSIC)
            filter.addAction(GlobalConsts.ACTION_ONLINE_MUSIC)
            filter.addAction(GlobalConsts.ACTION_NEXT_MUSIC)
            filter.addAction(GlobalConsts.ACTION_COMPLETE_MUSIC)
            activity?.registerReceiver(receiver, filter)
            receiver?.setMonitorListener(object :MonitorTwoListener<Context,Intent> {
                override fun OnMonitor(k: Context, v: Intent) {
                    setReceivers(k,v)
                }

            })
        }

    }

    private fun setReceivers(k: Context, intent: Intent) {
        val action = intent?.action

        //clickPosition
        if (action == null || play?.position == -1) {
            return
        }

        when (action) {
            GlobalConsts.ACTION_UPDATE_PROGRESS -> {
                val current = intent.getIntExtra("current", typeZero)
                val total = intent.getIntExtra("total", typeZero)
                //clickPosition
                val postion = intent.getIntExtra("position", typeZero)
                linearAdapter?.let { data ->
                    if (mAudioList.size > 0) {
                        val datas = data.getAutoUnscrambleItem()
                        datas?.let {
                            it.type = typeOne
                            it.currentDuration = current.toLong()
                            it.totalTime = total
                            data.getContentAdapter()?.notifyItemChanged(typeZero, it)
                            play?.position = postion
                        }
                    } else {
                        data.getAutoUnscramble()?.let {
                            if (it.size > 0) {
                                it[typeZero].currentDuration = current.toLong()
                                it[typeZero].type = typeOne
                                it[typeZero].totalTime = total
//                            data.notifyItemRangeChanged(clickPosition+1, typeOne)
                                data.getContentAdapter()?.notifyItemChanged(typeZero, data.getAutoUnscramble()!![typeZero])
                            }
                        }
                    }
                }
            }
            GlobalConsts.ACTION_PAUSE_MUSIC -> {
                linearAdapter?.let { data ->
                    if (mAudioList.size > 0) {
                        val datas = data.getAutoUnscrambleItem()
                        datas?.let {
                            it.type = typeTwo
                            data.getContentAdapter()?.notifyItemChanged(typeZero, it)
                        }
                    } else {
                        data.getAutoUnscramble()?.let {
                          if (it.size > 0) {
                              //clickPosition
                              it[typeZero].type = typeTwo
//                            data.notifyItemRangeChanged(clickPosition,typeOne)
                              //clickPosition
                              data.getContentAdapter()?.notifyItemChanged(typeZero, it[typeZero])
                          }
                        }
                    }
                }
            }
            GlobalConsts.ACTION_MUSIC_STARTED -> {
                linearAdapter?.let { data ->
                    val datas = data.getAutoUnscrambleItem()
                    if (mAudioList.size > 0) {
                        datas?.let {
                            it.type = typeZero
                            data.getContentAdapter()?.notifyItemChanged(typeZero, it)
                        }

                    } else {
                        data.getAutoUnscramble()?.let {
                            if (it.size > 0) {
                                //clickPosition
                                it[typeZero].type = typeZero
                                data.notifyDataSetChanged()
                            }
                        }
                    }

//                        if (mAudioList.size == 0) {
//                            val dataMore = MoreAudio()
//                            datas?.let {
//                                dataMore.anchorName = it.anchorName
//                                dataMore.name = it.name
//                                dataMore.comentURL = it.comentURL
//                                dataMore.type = it.type
//                                dataMore.vioceURL = it.vioceURL
//                                mAudioList.add(typeZero,dataMore)
//                            }
//                        }

//                        play?.setGlobalPlayback(mAudioList as ArrayList<MoreAudio>?,clickPosition,data.getContentAdapter())
                }
            }
            GlobalConsts.ACTION_COMPLETE_MUSIC -> {
                linearAdapter?.let { data ->

                    if (mAudioList.size > 0) {
                        //clickPosition
                        val posi = play?.position
                        if (posi!! < mAudioList.size) {
                            val datas = data.getAutoUnscrambleItem()
                            datas?.let {
                                it.comentURL = mAudioList[posi].comentURL
                                it.name = mAudioList[posi].name
                                it.anchorName = mAudioList[posi].anchorName
                                it.vioceURL = mAudioList[posi].vioceURL
                                data.setAutoUnscramble(it)
                                play?.playMusic(mAudioList[posi].vioceURL)
                                play?.position = posi
                                data.getContentAdapter()?.notifyItemChanged(typeZero, it)
                            }

                        } else { // 播放完了
                            data.getAutoUnscramble()?.let {
                                if (it.size > 0) {
                                    it[typeZero].type = typeZero
                                    it[typeZero].currentDuration = typeZero.toLong()
                                    data.getContentAdapter()?.notifyItemChanged(typeZero)
                                }
                            }
                        }
                    } else {
                        data.getAutoUnscramble()?.let {
                            //clickPosition
                           if (it.size > 0) {
                               val posiPlay = typeZero
                               val posi = posiPlay + 1
                               if (posi < it.size) {
                                   it[posiPlay].type = typeZero
                                   data.getContentAdapter()?.notifyItemChanged(posiPlay)
                                   play?.position = posi
                                   it[posi].type = typeOne
                                   play?.playMusic(it[posi].vioceURL)
                                   data.getContentAdapter()?.notifyItemChanged(posi)
                               } else {
                                   it[posiPlay].type = typeZero
                                   it[posiPlay].currentDuration = typeZero.toLong()
                                   play?.playMusic(it[posiPlay].vioceURL)
                                   data.notifyDataSetChanged()
                               }
                           }
                        }
                    }
                }
            }
        }
    }
}
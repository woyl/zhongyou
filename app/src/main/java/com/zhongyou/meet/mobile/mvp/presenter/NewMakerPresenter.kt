package com.zhongyou.meet.mobile.mvp.presenter

import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.adorkable.iosdialog.AlertDialog
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.RxLifecycleUtils
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.entities.Agora
import com.zhongyou.meet.mobile.entities.Data
import com.zhongyou.meet.mobile.entiy.MakerColumn
import com.zhongyou.meet.mobile.entiy.MeetingJoin
import com.zhongyou.meet.mobile.entiy.MoreAudio
import com.zhongyou.meet.mobile.entiy.RecomandData
import com.zhongyou.meet.mobile.mvp.contract.NewMakerContract
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity
import com.zhongyou.meet.mobile.persistence.Preferences
import com.zhongyou.meet.mobile.utils.UIDUtil
import io.agora.openlive.ui.MeetingInitActivity
import io.reactivex.disposables.Disposable
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 09/25/2020 17:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
class NewMakerPresenter
@Inject
constructor(model: NewMakerContract.Model, rootView: NewMakerContract.View) :
        BasePresenter<NewMakerContract.Model, NewMakerContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mImageLoader: ImageLoader

    @Inject
    lateinit var mAppManager: AppManager

    private var mPhoneStatusDialog: AlertDialog? = null
    private var dialog: Dialog? = null

    fun getRecommendData() {
        if (mModel != null) {
            mModel.getRecommend(2)?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            mRootView.hideLoading()
                            if (jsonObject?.getInteger("errcode") == 0) {
                                val data = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), RecomandData::class.java)
                                if (mRootView != null) {
                                    mRootView.getBannerData(data)
                                    mRootView.getAudioDataSuccess(data.changeLessonByDay)
                                }

                            } else {
                                if (jsonObject != null) {
                                    mRootView.showMessage(jsonObject.getString("errmsg"))
                                }
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.hideLoading()
                        }

                        override fun onComplete() {
                            super.onComplete()
                            mRootView.hideLoading()
                        }
                    })
        }
    }

    fun getHomePage() {
        mModel?.let {
            it.getHomePage().compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(object : RxSubscriber<JSONObject>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(t: JSONObject?) {
                            mRootView.hideLoading()
                            if (t?.getInteger("errcode") == 0) {
                                val data = JSON.parseArray(t.getJSONArray("data").toJSONString(), Data::class.java)
                                mRootView?.getHomepage(data)
                            } else {
                                t?.let { mRootView.showMessage(t.getString("errmsg")) }
                            }
                        }

                        override fun _onError(code: Int, msg: String?) {
                            super._onError(code, msg)
                            mRootView.hideLoading()
                        }

                        override fun onComplete() {
                            super.onComplete()
                            mRootView.hideLoading()
                        }
                    })
        }
    }

    fun getMakerColumn() {
        if (mModel != null) {
            mModel.getMakerColumn()!!.compose(RxSchedulersHelper.io_main<JSONObject>())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            jsonObject?.let {
                                if (jsonObject.getInteger("errcode") == 0) {
                                    val data = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), MakerColumn::class.java)
                                    mRootView.getMakerColumn(data, true)
                                } else {
                                    mRootView.showMessage(jsonObject.getString("errmsg"))
                                }
                            }

                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.getMakerColumn(null, false)
                        }
                    })
        }
    }

    /**
     * 获取未读消息
     */

    fun getUnReadMessage() {
        if (mModel != null) {
            mModel.getUnReadMessageCount()!!.compose(RxSchedulersHelper.io_main<JSONObject>())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(object : RxSubscriber<JSONObject>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject) {
                            if (jsonObject.getInteger("errcode") == 0) {
                                val data = jsonObject.getInteger("data")
                                if (data == 0) {
                                    mRootView.showRedDot(false, data)
                                } else {
                                    mRootView.showRedDot(true, data)
                                }
                            } else {
                                mRootView.showRedDot(false, 0)
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.showRedDot(false, 0)
                        }
                    })
        }
    }

    fun showPhoneStatusDialog(i: Int) {
        mPhoneStatusDialog = AlertDialog(mRootView.getActivity())
                .builder()
                .setTitle("提示")
                .setMsg(if (i == 1) "检测到摄像头关闭 是否打开？" else "检测到麦克风关闭 是否打开？")
                .setNegativeButton("进入会议") {
                    mPhoneStatusDialog!!.dismiss()
                    showQuickJoinDialog()
                }
                .setPositiveButton("去设置") {
                    mPhoneStatusDialog!!.dismiss()
                    mRootView.launchActivity(Intent(mRootView.getActivity(), MeetingSettingActivity::class.java))
                }
        mPhoneStatusDialog?.show()
    }

    fun showQuickJoinDialog() {
        val view = View.inflate(mRootView.getActivity(), R.layout.dialog_meeting_input_code, null)
        val codeEdit = view.findViewById<EditText>(R.id.code)
        val title = view.findViewById<TextView>(R.id.textView2)
        title.text = "请输入加入码快速加入"
        view.findViewById<View>(R.id.confirm).setOnClickListener {
            dialog?.dismiss()
            if (!TextUtils.isEmpty(codeEdit.text)) {
                val params = JSONObject()
                params["clientUid"] = UIDUtil.generatorUID(Preferences.getUserId())
                params["meetingId"] = ""
                params["token"] = codeEdit.text.toString().trim { it <= ' ' }
                quickJoinMeeting(params)
            } else {
                codeEdit.error = "加入码不能为空"
            }
        }
        view.findViewById<View>(R.id.cancel).setOnClickListener { dialog?.dismiss() }
        dialog = Dialog(mRootView.getActivity()!!, R.style.CustomDialog)
        dialog?.setContentView(view)
        dialog?.show()
    }


    private fun quickJoinMeeting(json: JSONObject) {
        if (mRootView == null) {
            return
        }
        if (mModel != null) {
            mModel.quickJoin(json)
                    ?.compose<JSONObject>(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.compose(RxSchedulersHelper.io_main())
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                val meetingJoin = JSON.parseObject(jsonObject.toJSONString(), MeetingJoin::class.java)
                                val mJoinRole = meetingJoin.data.role
                                var role = "Subscriber"
                                if (mJoinRole == 0) {
                                    role = "Publisher"
                                } else if (mJoinRole == 1) {
                                    role = "Publisher"
                                } else if (mJoinRole == 2) {
                                    role = "Subscriber"
                                }
                                Constant.isNeedRecord = meetingJoin.data.meeting.isRecord == 1
                                getAgoraKey(meetingJoin.data.meeting.isMeeting, role, mJoinRole, meetingJoin)
                            } else {
                                jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                            }
                        }
                    })
        }
    }

    private fun getAgoraKey(type: Int, role: String, joinrole: Int, meetingJoin: MeetingJoin) {
        if (mModel == null) {
            return
        }
        mModel.getAgoraKey(meetingJoin.data.meeting.id, UIDUtil.generatorUID(Preferences.getUserId()), role)
                ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                ?.subscribe(object : RxSubscriber<JSONObject?>() {
                    override fun _onNext(jsonObject: JSONObject?) {
                        if (jsonObject?.getInteger("errcode") == 0) {
                            val agora = Agora()
                            agora.appID = jsonObject.getJSONObject("data").getString("appID")
                            agora.isTest = jsonObject.getJSONObject("data").getString("isTest")
                            agora.signalingKey = jsonObject.getJSONObject("data").getString("signalingKey")
                            agora.token = jsonObject.getJSONObject("data").getString("token")
                            val intent = Intent(mRootView.getActivity(), MeetingInitActivity::class.java)
                            intent.putExtra("agora", agora)
                            intent.putExtra("meeting", meetingJoin.data)
                            intent.putExtra("isMaker", type != 1)
                            intent.putExtra("role", joinrole)
                            mRootView.launchActivity(intent)
                        } else {
                            jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                        }
                    }
                })
    }

    private val moreAudios = mutableListOf<MoreAudio>()
    fun getMoreAudio(page: Int) {
        if (mModel != null) {
            mModel.getMoreAudio(page)?.compose(RxLifecycleUtils.bindToLifecycle<JSONObject>(mRootView))
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                val json = jsonObject.getJSONObject("data")
                                if (json.containsKey("changeLessonByDayList")) {
                                    val totalPage = json.getJSONObject("changeLessonByDayList").getInteger("totalPage")
                                    val data = json.getJSONObject("changeLessonByDayList").getJSONArray("list")
                                    for (i in data.indices) {
                                        val list = JSONArray.parseArray(data.getJSONObject(i).getJSONArray("list").toJSONString(), MoreAudio::class.java)
                                        for (moreAudio in list) {
                                            moreAudio?.date = data.getJSONObject(i).getString("date")
                                        }
                                        moreAudios.addAll(list)
                                    }
                                    if (mRootView != null) {
                                        mRootView.getDataComplete(moreAudios)
                                    }

                                    /*for (int i = 0; i < data.size(); i++) {
										List<MoreAudio> list = JSONArray.parseArray(data.getJSONObject(i).getJSONArray("list").toJSONString(), MoreAudio.class);
										MoreAudio moreAudio = new MoreAudio();
										if (i == 0) {
											moreAudio.setDate(data.getJSONObject(i).getString("date"));
											list.add(0, moreAudio);
										}
										if (moreAudios.size() > 0 && moreAudios.get(moreAudios.size() - 1).getDate() != null && list.get(0).getDate() != null) {
											Timber.e("list.get(0).getDate()---->%s", list.get(0).getDate());
											if (moreAudios.get(moreAudios.size() - 1).getDate().equals(list.get(0).getDate())) {
												Timber.e("moreAudios.get(moreAudios.size() - 1).getDate()---->%s", moreAudios.get(moreAudios.size() - 1).getDate());
												list.remove(moreAudio);
											}
										}
										moreAudios.addAll(list);
									}*/
                                }
                            } else {
                                mRootView.getDataComplete(null)
                                jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                            }
                        }
                    })
        }
    }

    fun clickCourse(id:String?) {
        if (mModel != null) {
            id?.let { mModel.clickCourse(it)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object :RxSubscriber<JSONObject?>(){
                        override fun _onNext(t: JSONObject?) {

                        }
                    })}
        }
    }

    fun signCourse(type: String?, isSign: Int,position:Int,positionItem: Int,typeFragment:Int) {
        if (mModel != null) {
            mModel.signCourse(type, isSign)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                if (mRootView != null) {
                                    mRootView.signSuccess(true, isSign,jsonObject.getString("data"),position,positionItem,typeFragment)
                                }
                            } else {
                                if (mRootView != null) {
                                    mRootView.signSuccess(false, isSign,"",position,positionItem,typeFragment)
                                }
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            if (mRootView != null) {
                                mRootView.signSuccess(false, isSign,"",position,positionItem,typeFragment)
                            }
                        }
                    })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

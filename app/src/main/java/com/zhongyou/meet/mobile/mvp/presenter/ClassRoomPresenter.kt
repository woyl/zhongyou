package com.zhongyou.meet.mobile.mvp.presenter

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.adorkable.iosdialog.AlertDialog
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.RxLifecycleUtils
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.entiy.MeetingJoin
import com.zhongyou.meet.mobile.entiy.MeetingsData
import com.zhongyou.meet.mobile.mvp.contract.ClassRoomContract
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity
import com.zhongyou.meet.mobile.persistence.Preferences
import com.zhongyou.meet.mobile.utils.UIDUtil
import io.reactivex.disposables.Disposable
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/03/2020 15:58
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
class ClassRoomPresenter
@Inject
constructor(model: ClassRoomContract.Model, rootView: ClassRoomContract.View) :
        BasePresenter<ClassRoomContract.Model, ClassRoomContract.View>(model, rootView) {
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


    //获取首页会议 titlt传空字符串  0 公开会议 1 受邀会议
    fun getMeetings(type: Int, pageNo: Int, title: String?, isMeeting: String) {
        if (mModel != null) {
            mModel.getAllMeeting(type, pageNo, title, isMeeting)?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onComplete() {
                            super.onComplete()
                            if (type == 0) {
//								getBannerData();//完成后再去请求banner
                            }
                        }

                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            if (mRootView != null) {
                                mRootView.hideLoading()
                            }
                            if (jsonObject?.getInteger("errcode") == 0) {
                                val meetingsData = JSON.parseObject(jsonObject.toJSONString(), MeetingsData::class.java)
                                if (mRootView != null) {
                                    if (pageNo == 1) {
                                        if (meetingsData.data.list.size <= 0) {
                                            mRootView.showEmptyView(true,isMeeting)
                                            return
                                        } else {
                                            mRootView.showEmptyView(false,isMeeting)
                                        }
                                    }
                                    mRootView.showMeetings(meetingsData, isMeeting)
                                }
                            } else {
                                if (mRootView != null && pageNo == 1) {
                                    mRootView.showEmptyView(true,isMeeting)
                                }
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            if (mRootView != null) {
                                mRootView.hideLoading()
                            }
                            if (mRootView != null && pageNo == 1) {
                                mRootView.showEmptyView(true,isMeeting)
                            }
                            if (type == 0) {
//								getBannerData();//完成后再去请求banner
                            }
                        }
                    })
        }
    }


    fun verifyRole(jsonObject: JSONObject?, meeting: MeetingsData.DataBean.ListBean?, code: String?) {
        Constant.KEY_code = ""
        Constant.KEY_meetingID = ""
        if (mModel != null) {
            mModel.verifyRole(jsonObject)
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                if (mRootView != null) {
                                    try {
                                        Thread.sleep(1500)
                                    } catch (e: InterruptedException) {
                                        e.printStackTrace()
                                    }
                                    mRootView.verifyRole(jsonObject, meeting, code)
                                }
                            } else {
                                if (mRootView != null) {
                                    jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                                }
                            }
                        }
                    })
        }
    }

    fun joinMeeting(jsonObject: JSONObject?, meeting: MeetingsData.DataBean.ListBean?, code: String?) {
        if (mModel != null) {
            mModel.joinMeeting(jsonObject)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                if (mRootView != null) {
                                    mRootView.joinMeeting(jsonObject, meeting, code)
                                }
                            } else {
                                if (mRootView != null) {
                                    jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                                }
                            }
                        }
                    })
        }
    }


    fun showPhoneStatusDialog(i: Int, context: Context?) {
        mPhoneStatusDialog = AlertDialog(context)
                .builder()
                .setTitle("提示")
                .setMsg(if (i == 1) "检测到摄像头关闭 是否打开？" else "检测到麦克风关闭 是否打开？")
                .setNegativeButton("进入会议") {
                    mPhoneStatusDialog?.dismiss()
                    showQuickJoinDialog(context)
                }
                .setPositiveButton("去设置") {
                    mPhoneStatusDialog?.dismiss()
                    mRootView.launchActivity(Intent(context, MeetingSettingActivity::class.java))
                }
        mPhoneStatusDialog?.show()
    }

    private fun showQuickJoinDialog(context: Context?) {
        val view = View.inflate(context, R.layout.dialog_meeting_input_code, null)
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
                codeEdit.error = "会议加入码不能为空"
            }
        }
        view.findViewById<View>(R.id.cancel).setOnClickListener { dialog?.dismiss() }
        dialog = context?.let { Dialog(it, R.style.CustomDialog) }
        dialog?.setContentView(view)
        dialog?.show()
    }

    private fun quickJoinMeeting(json: JSONObject) {
        if (mRootView == null) {
            return
        }
        if (mModel != null) {
            mModel.quickJoin(json)
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
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
                                getAgoraKey(meetingJoin.data.meeting.isMeeting, meetingJoin.data.meeting.id, UIDUtil.generatorUID(Preferences.getUserId()), role, mJoinRole, meetingJoin)
                            } else {
                                jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                            }
                        }
                    })
        }
    }

    fun getAgoraKey(type: Int, channel: String?, account: String?, role: String?, joinrole: Int, meetingJoin: MeetingJoin?) {
        if (mModel != null) {
            mModel.getAgoraKey(channel, account, role)
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                if (mRootView != null) {
                                    mRootView.getAgoraKey(type, jsonObject, meetingJoin, joinrole)
                                }
                            } else {
                                if (mRootView != null) {
                                    jsonObject?.getString("errmsg")?.let { mRootView.showMessage(it) }
                                }
                            }
                        }
                    })
        }
    }

    /**
     * 获取未读消息
     */
    fun getUnReadMessage() {
        if (mModel != null) {
            mModel.getUnReadMessageCount()
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
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


    /**
     * 订阅
     */
    fun subscribeMeeting(meetingId:String,position:Int,isMeeting :Int) {
        if (mModel != null) {
            mModel.subscribeMeeting(meetingId)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                mRootView.subscribeMeetingSuccess(true,position,isMeeting)
                            } else {
                                mRootView.subscribeMeetingSuccess(false,position,isMeeting)
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.subscribeMeetingSuccess(false,position,isMeeting)
                        }
                    })
        }
    }



    /**
     * 取消订阅
     */
    fun cancelSubscribe(meetingId:String,position:Int,isMeeting :Int) {
        if (mModel != null) {
            mModel.cancelSubscribeMeeting(meetingId)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {
                            addDispose(d)
                        }

                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                mRootView.cancelSubcribeMeeting(true,position,isMeeting)
                            } else {
                                mRootView.cancelSubcribeMeeting(false,position,isMeeting)
                            }
                        }

                        override fun _onError(code: Int, msg: String) {
                            super._onError(code, msg)
                            mRootView.cancelSubcribeMeeting(false,position,isMeeting)
                        }
                    })
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}

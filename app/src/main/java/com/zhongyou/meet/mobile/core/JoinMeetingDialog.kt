package com.zhongyou.meet.mobile.core

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.allen.library.SuperButton
import com.kongzue.dialog.v3.CustomDialog
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.BaseApplication
import com.zhongyou.meet.mobile.Constant
import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.entities.Agora
import com.zhongyou.meet.mobile.entiy.MeetingJoin
import com.zhongyou.meet.mobile.persistence.Preferences
import com.zhongyou.meet.mobile.utils.MMKVHelper
import com.zhongyou.meet.mobile.utils.ToastUtils
import com.zhongyou.meet.mobile.utils.UIDUtil
import io.agora.openlive.ui.MeetingInitActivity

/**
 * @author golangdorid@gmail.com
 * @date 2020/7/9 9:35 AM.
 * @
 */
object JoinMeetingDialog {


    fun showQuickJoinMeeting(activity: AppCompatActivity) {
        CustomDialog.show(activity, R.layout.dialog_meeting_input_code) { dialog, view ->
            val codeEdit: EditText = view.findViewById(R.id.code)
            val title: TextView = view.findViewById(R.id.textView2)
            title.text = "请输入加入码快速加入"
            view.findViewById<Button>(R.id.cancelDialog).visibility = View.GONE
            view.findViewById<Button>(R.id.confirm).setOnClickListener(View.OnClickListener {

                if (!TextUtils.isEmpty(codeEdit.text)) {
                    val params = JSONObject()
                    params["clientUid"] = UIDUtil.generatorUID(Preferences.getUserId())
                    params["meetingId"] = ""
                    params["token"] = codeEdit.text.toString().trim()
                    quickJoinMeeting(activity, params)
                    dialog.doDismiss()
                } else {
                   ToastUtils.showToast("会议加入码不能为空")
                }
            })
        }
    }

    private fun quickJoinMeeting(activity: AppCompatActivity, params: JSONObject) {
        HttpsRequest.provideClientApi().quickJoin(params).compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun _onNext(jsonObject: JSONObject?) {
                        jsonObject?.let {
                            if (jsonObject.getInteger("errcode") == 0) {
                                val meetingJoin = JSON.parseObject(jsonObject.toJSONString(), MeetingJoin::class.java)
                                val mJoinRole = meetingJoin.data.role
                                var role = "Subscriber"
                                when (mJoinRole) {
                                    0 -> {
                                        role = "Publisher"
                                    }
                                    1 -> {
                                        role = "Publisher"
                                    }
                                    2 -> {
                                        role = "Subscriber"
                                    }
                                }
                                getAgoraKey(activity, meetingJoin.data.meeting.isMeeting, meetingJoin.data.meeting.id, UIDUtil.generatorUID(Preferences.getUserId()), role, mJoinRole, meetingJoin);
                            } else {
                                ToastUtils.showToast(jsonObject.getString("errmsg"))
                            }
                        }
                    }

                })
    }

    fun showNoCodeDialog(activity: AppCompatActivity, meetingId: String) {
        CustomDialog.show(activity, R.layout.dialog_no_code) { dialog, v ->
            v.findViewById<SuperButton>(R.id.noCodeJoin).setOnClickListener {

                val params = JSONObject()
                params["clientUid"] = UIDUtil.generatorUID(Preferences.getUserId())
                params["meetingId"] = meetingId
                params["token"] = ""
                verifyRole(activity, params, meetingId, "")
                dialog.doDismiss()
            }

            v.findViewById<SuperButton>(R.id.otherMethod).setOnClickListener {

                showNeedCodeDialog(activity, meetingId)
                dialog.doDismiss()
            }
        }
    }

    fun showNeedCodeDialog(activity: AppCompatActivity, meetingId: String) {
        CustomDialog.show(activity, R.layout.dialog_meeting_input_code) { dialog, v ->
            val codeEdit: EditText = v.findViewById(R.id.code)
            val code = MMKV.defaultMMKV().decodeString(meetingId)
            if (TextUtils.isEmpty(code)) {
                codeEdit.setText("")
            } else {
                codeEdit.setText(code)
                codeEdit.setSelection(code.length)
            }

            v.findViewById<Button>(R.id.confirm).setOnClickListener(View.OnClickListener {
                if (!TextUtils.isEmpty(codeEdit.text)) {
                    val params = JSONObject()
                    params["clientUid"] = UIDUtil.generatorUID(Preferences.getUserId())
                    params["meetingId"] = meetingId
                    params["token"] = codeEdit.text.toString().trim()
                    verifyRole(activity, params, meetingId, codeEdit.text.toString().trim())
                    dialog.doDismiss()
                } else {
                    ToastUtils.showToast("会议加入码不能为空")
                }
            })

            v.findViewById<Button>(R.id.cancelDialog).setOnClickListener {
                dialog.doDismiss()
            }

        }

    }

    private fun verifyRole(activity: AppCompatActivity, params: JSONObject, meetingId: String, code: String) {
        HttpsRequest.provideClientApi().verifyRole(params)
                .compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun _onNext(jsonObject: JSONObject?) {

                        jsonObject?.let {
                            if (jsonObject.getInteger("errcode") == 0) {
                                Constant.KEY_meetingID = ""
                                Constant.KEY_code = ""
                                val jsonObject = JSONObject()
                                jsonObject["clientUid"] = UIDUtil.generatorUID(Preferences.getUserId())
                                jsonObject["meetingId"] = meetingId
                                jsonObject["token"] = code
                                joinMeeting(activity, jsonObject, meetingId, code)
                            } else {
                                ToastUtils.showToast(jsonObject.getString("errmsg"))
                            }
                        }
                    }

                })
    }

    private fun joinMeeting(activity: AppCompatActivity, jsonObject: JSONObject, meetingId: String, code: String) {
        HttpsRequest.provideClientApi().joinMeeting(jsonObject)
                .compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun _onNext(jsonObject: JSONObject?) {
                        jsonObject?.let {
                            if (jsonObject.getInteger("errcode") == 0) {
                                val meetingJoin = JSON.parseObject(jsonObject.toJSONString(), MeetingJoin::class.java)
                                if (meetingJoin == null || meetingJoin.data == null || TextUtils.isEmpty(meetingJoin.data.meeting.id)) {
                                    ToastUtils.showToast("出错了 请重试")
                                    return
                                }
                                if (!TextUtils.isEmpty(code)) {
                                    MMKV.defaultMMKV().encode(meetingId, code)
                                }


                                Constant.isNeedRecord = meetingJoin.data.meeting.isRecord == 1

                                MMKV.defaultMMKV().encode(MMKVHelper.KEY_meetingQuilty, meetingJoin.data.meeting.resolvingPower)

                                val mJoinRole = meetingJoin.data.role
                                var role = "Subscriber"
                                when (mJoinRole) {
                                    0 -> {
                                        role = "Publisher"
                                    }
                                    1 -> {
                                        role = "Publisher"
                                    }
                                    2 -> {
                                        role = "Subscriber"
                                    }
                                }
                                Logger.e("meetingJoin111-->"+JSON.toJSONString(meetingJoin))
                                getAgoraKey(activity, meetingJoin.data.meeting.isMeeting, meetingJoin.data.meeting.id, UIDUtil.generatorUID(Preferences.getUserId()), role, mJoinRole, meetingJoin);
                            } else {
                                ToastUtils.showToast(jsonObject.getString("errmsg"))
                            }
                        }
                    }

                })
    }

    private fun getAgoraKey(activity: AppCompatActivity, isMeeting: Int, id: String?, generatorUID: String?, role: String, mJoinRole: Int, meetingJoin: MeetingJoin) {
        HttpsRequest.provideClientApi().getAgoraKey(id, generatorUID, role).compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun _onNext(jsonObject: JSONObject?) {
                        jsonObject?.let {
                            if (jsonObject.getInteger("errcode") == 0) {
                                val agora = Agora()
                                agora.appID = jsonObject.getJSONObject("data").getString("appID")
                                agora.isTest = jsonObject.getJSONObject("data").getString("isTest")
                                agora.signalingKey = jsonObject.getJSONObject("data").getString("signalingKey")
                                agora.token = jsonObject.getJSONObject("data").getString("token")

                                val intent = Intent(activity, MeetingInitActivity::class.java)
                                intent.putExtra("agora", agora)
                                intent.putExtra("isMaker", isMeeting != 1)
                                intent.putExtra("meeting", meetingJoin.data)
                                Logger.e("meetingJoin222-->"+JSON.toJSONString(meetingJoin.data))
                                intent.putExtra("role", mJoinRole)
                                BaseApplication.getInstance().agora=agora
                                activity.startActivity(intent)
                            } else {
                                ToastUtils.showToast(jsonObject.getString("errmsg"))
                            }
                        }
                    }

                })
    }
}
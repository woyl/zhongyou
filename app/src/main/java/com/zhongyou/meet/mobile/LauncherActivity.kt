package com.zhongyou.meet.mobile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.jess.arms.utils.RxBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV
import com.zhongyou.meet.mobile.ameeting.network.ApiService
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber
import com.zhongyou.meet.mobile.business.BindActivity
import com.zhongyou.meet.mobile.entities.FinishWX
import com.zhongyou.meet.mobile.mvp.ui.activity.AccountSettingActivity
import com.zhongyou.meet.mobile.mvp.ui.activity.IndexActivity
import com.zhongyou.meet.mobile.persistence.Preferences
import com.zhongyou.meet.mobile.utils.CountDownTimerUtil
import com.zhongyou.meet.mobile.utils.MMKVHelper
import com.zhongyou.meet.mobile.utils.listener.CountDownTimeListener
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.Disposable
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.RongIMClient.ConnectCallbackEx
import io.rong.imlib.RongIMClient.DatabaseOpenStatus
import io.rong.imlib.model.UserInfo

class LauncherActivity : AppCompatActivity(), CountDownTimeListener {

    /**到计时 */
    private var countDownTimerUtil: CountDownTimerUtil? = null
    private var mApiService: ApiService? = null
    private var context:Context?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        context = this;

        countDownTimerUtil = CountDownTimerUtil(1000, 1000, this)
        countDownTimerUtil?.start()

        mApiService = HttpsRequest.provideClientApi()
    }

    override fun onTimeFinish() {
        if (Preferences.isLogin()) {
            val userMobile = Preferences.getUserMobile()
            when {
                TextUtils.isEmpty(userMobile) -> {
                    BindActivity.actionStart(this, true, false)
                    finish()
                }
                Preferences.isUserinfoEmpty() -> {
                    startActivity(Intent(this, AccountSettingActivity::class.java).putExtra("isFirst", true))
                    finish()
                }
                else -> {
                    loginIM()
                }
            }
        } else {
            goWxLogin()
        }
    }

    override fun onTimeTick(time: Long) {
    }

    fun goWxLogin() {
        startActivity(Intent(this,WXEntryActivity::class.java))
        finish()
    }


    private fun loginIM() {
        if (!TextUtils.isEmpty(Preferences.getImToken())) {
            IMConnect(Preferences.getImToken())
            Logger.e("IM token 不为空 直接登录 IM")
        } else {
            var photo = Preferences.getUserPhoto()
            var userName = Preferences.getUserName()
            if (TextUtils.isEmpty(Preferences.getUserPhoto())) {
                photo = Preferences.getWeiXinHead()
            }
            if (TextUtils.isEmpty(userName)) {
                userName = Preferences.getWeiXinNickName()
            }
            if (userName.isNullOrEmpty() && photo.isNullOrEmpty() && Preferences.getUserId().isNullOrEmpty()) {
                goWxLogin()
                return
            }
            val `object` = JSONObject()
            `object`["nickname"] = userName
            `object`["userId"] = Preferences.getUserId()
            `object`["avatar"] = photo
            mApiService?.getToken(`object`)
                    ?.compose(RxSchedulersHelper.io_main<JSONObject>())
                    ?.subscribe(object : RxSubscriber<JSONObject?>() {
                        override fun onSubscribe(d: Disposable) {}
                        override fun _onNext(jsonObject: JSONObject?) {
                            if (jsonObject?.getInteger("errcode") == 0) {
                                if (jsonObject.getJSONObject("data").getInteger("code") == 200) {
                                    Constant.IMTOKEN = jsonObject.getJSONObject("data").getString("token")
                                    MMKVHelper.getInstance().saveID(Preferences.getUserId())
                                    MMKVHelper.getInstance().saveAddress(Preferences.getUserAddress())
                                    MMKVHelper.getInstance().saveAreainfo(Preferences.getAreaInfo())
                                    MMKVHelper.getInstance().savePhoto(Preferences.getUserPhoto())
                                    MMKVHelper.getInstance().saveTOKEN(Preferences.getToken())
                                    MMKVHelper.getInstance().savePostTypeName(Preferences.getUserPostType())
                                    MMKVHelper.getInstance().saveMobie(Preferences.getUserMobile())
                                    MMKVHelper.getInstance().saveUserNickName(Preferences.getUserName())
                                    MMKV.defaultMMKV().encode(MMKVHelper.CUSTOMNAME, Preferences.getUserCustom())
                                    Preferences.setImToken(Constant.IMTOKEN)
                                    IMConnect(Constant.IMTOKEN)
                                } else {
                                    showToastyInfo("当前登陆信息失效 请重新登陆")
                                    Preferences.clear()
                                }
                            } else {
                                Preferences.clear()
                                goWxLogin()
                            }
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                            Preferences.clear()
                            goWxLogin()
                        }
                    })
        }
    }

    fun IMConnect(token: String) {
        Logger.e("IM  token:$token")
        RongIM.connect(token, object : ConnectCallbackEx() {
            override fun OnDatabaseOpened(databaseOpenStatus: DatabaseOpenStatus) {}
            override fun onTokenIncorrect() {
                Logger.e("onTokenIncorrect")
                Preferences.setImToken("")
                runOnUiThread {
                    showToastyInfo("当前登陆信息异常 请重试")
                }
                goWxLogin()
            }

            override fun onSuccess(s: String) {
                Logger.e("容云IM登陆成功:$s")
                MMKVHelper.getInstance().saveID(Preferences.getUserId())
                MMKVHelper.getInstance().saveAddress(Preferences.getUserAddress())
                MMKVHelper.getInstance().saveAreainfo(Preferences.getAreaInfo())
                MMKVHelper.getInstance().savePhoto(Preferences.getUserPhoto())
                MMKVHelper.getInstance().saveTOKEN(Preferences.getToken())
                MMKVHelper.getInstance().savePostTypeName(Preferences.getUserPostType())
                MMKVHelper.getInstance().saveMobie(Preferences.getUserMobile())
                MMKVHelper.getInstance().saveUserNickName(Preferences.getUserName())
                RongIM.setUserInfoProvider({
                    val userInfo = UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto()))
                    RongIM.getInstance().refreshUserInfoCache(userInfo)
                    RongIM.getInstance().setCurrentUserInfo(userInfo)
                    userInfo //根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
                }, false)
                startActivity(Intent(context,IndexActivity::class.java))
                finish()
                RxBus.sendMessage(FinishWX())
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {
                Logger.e(errorCode.toString())
                Preferences.setImToken("")
                loginIM()
            }
        })
    }

    fun showToastyInfo(message: String?) {
        Toasty.info(this, message!!, Toast.LENGTH_SHORT, true).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimerUtil?.cancel()
    }
}
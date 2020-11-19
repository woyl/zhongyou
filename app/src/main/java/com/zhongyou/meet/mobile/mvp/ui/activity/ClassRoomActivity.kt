package com.zhongyou.meet.mobile.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import com.alibaba.fastjson.JSONObject

import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import com.zhongyou.meet.mobile.di.component.DaggerClassRoomComponent
import com.zhongyou.meet.mobile.di.module.ClassRoomModule
import com.zhongyou.meet.mobile.mvp.contract.ClassRoomContract
import com.zhongyou.meet.mobile.mvp.presenter.ClassRoomPresenter

import com.zhongyou.meet.mobile.R
import com.zhongyou.meet.mobile.entiy.MeetingJoin
import com.zhongyou.meet.mobile.entiy.MeetingsData


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
/**
 * 如果没presenter
 * 你可以这样写
 *
 * @ActivityScope(請注意命名空間) class NullObjectPresenterByActivity
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class ClassRoomActivity : BaseActivity<ClassRoomPresenter>(), ClassRoomContract.View {

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerClassRoomComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .classRoomModule(ClassRoomModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_class_room //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {

    }


    override fun showEmptyView(isShow: Boolean, isMeeting: String) {
    }

    override fun showMeetings(meetingsData: MeetingsData?,isMeeting:String) {
    }

    override fun getAgoraKey(type: Int, jsonObject: JSONObject?, join: MeetingJoin?, joinRole: Int) {
    }

    override fun verifyRole(jsonObject: JSONObject?, meeting: MeetingsData.DataBean.ListBean?, code: String?) {
    }

    override fun joinMeeting(jsonObject: JSONObject?, meeting: MeetingsData.DataBean.ListBean?, code: String?) {
    }

    override fun showRedDot(isShow: Boolean, count: Int) {

    }

    override fun subscribeMeetingSuccess(message: Boolean, position: Int,isMeeting:Int) {
    }

    override fun cancelSubcribeMeeting(message: Boolean, position: Int,isMeeting:Int) {
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }
}

package com.zhongyou.meet.mobile.mvp.contract

import com.alibaba.fastjson.JSONObject
import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.zhongyou.meet.mobile.entiy.MyAllCourse
import io.reactivex.Observable


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/03/2020 11:00
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
interface MyCourseContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun getDataSuccess(myAllCourse: MyAllCourse?,type:Int)
        fun signSuccess(isSuccess: Boolean, sign: Int,message:String?,position:Int,typeFragment:Int)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getCollectMeeting(userId: String?): Observable<JSONObject?>?
        fun signCourse(typeID: String?, isSign: Int): Observable<JSONObject?>?
    }

}

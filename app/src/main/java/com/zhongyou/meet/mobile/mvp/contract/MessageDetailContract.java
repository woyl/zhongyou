package com.zhongyou.meet.mobile.mvp.contract;

import android.app.Activity;

import com.alibaba.fastjson.JSONObject;
import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhongyou.meet.mobile.entiy.MessageDetail;

import java.util.List;

import io.reactivex.Observable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 21:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public interface MessageDetailContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void getMessageDetailComplete(List<MessageDetail> list);

        Activity getActivity();

        RxPermissions getRxPermissions();

        void canLoadMore(int page);

        void readOneMessageResult(boolean success, int position, MessageDetail item);

        void readAllMessageResult(boolean success);

        void deleteSuccess(boolean success,int posi);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<JSONObject> getMessageDetail(String userId, String type, int pageNo);

        Observable<JSONObject> verifyRole(JSONObject jsonObject);

        Observable<JSONObject> joinMeeting(JSONObject jsonObject);

        Observable<JSONObject> getAgoraKey(String channel, String account, String role);

        Observable<JSONObject> readOneMessage(String id);

        Observable<JSONObject> readAllMessage(String type);

        Observable<JSONObject> deteleItem(String msgId);
    }
}

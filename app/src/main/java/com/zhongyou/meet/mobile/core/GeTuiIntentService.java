package com.zhongyou.meet.mobile.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.message.BindAliasCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.Constant;

/**
 * @author golangdorid@gmail.com
 * @date 2020/9/11 2:16 PM.
 * @
 */
public class GeTuiIntentService extends GTIntentService {
	@Override
	public void onReceiveServicePid(Context context, int i) {

	}

	// 接收 cid
	@Override
	public void onReceiveClientId(Context context, String clientid) {
		Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
	}

	// 处理透传消息
	@Override
	public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
			// 透传消息的处理，详看 SDK demo
	}

	// cid 离线上线通知
	@Override
	public void onReceiveOnlineState(Context context, boolean b) {

	}

	// 各种事件处理回执
	@Override
	public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
		Log.e(TAG, "onReceiveCommandResult: "+ JSON.toJSONString(gtCmdMessage));
		if (gtCmdMessage.getAction()== PushConsts.BIND_ALIAS_RESULT){
			String sn = ((BindAliasCmdMessage) gtCmdMessage).getSn();
			String code = ((BindAliasCmdMessage) gtCmdMessage).getCode();
			if (code.equals("0")){
				MMKV.defaultMMKV().encode(Constant.KEY_BIND_ALIAS_TAG,true);
			}else {
				LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("com.meeting.mobile.bindaliansfail"));
			}
		}
	}

	// 通知到达，只有个推通道下发的通知会回调此方法
	@Override
	public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

	}
	// 通知点击，只有个推通道下发的通知会回调此方法
	@Override
	public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {
		Intent intent=new Intent();
		intent.setAction("com.meeting.mobile.opennotifycation");
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}

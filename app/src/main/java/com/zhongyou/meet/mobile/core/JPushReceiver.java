package com.zhongyou.meet.mobile.core;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity;
import com.zhongyou.meet.mobile.utils.MMKVHelper;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * @author golangdorid@gmail.com
 * @date 2020/7/8 5:07 PM.
 * @
 */
public class JPushReceiver extends JPushMessageReceiver {

    private int num = 0;

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        Logger.e(JSON.toJSONString(jPushMessage));
        if (jPushMessage.getErrorCode() == 6002) {
            Intent intent = new Intent();
            intent.setAction("com.meeting.mobile.setaliasfaliled");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            return;
        }
        MMKV.defaultMMKV().encode(MMKVHelper.KEY_SETALIASSUCCESS, true);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public Notification getNotification(Context context, NotificationMessage notificationMessage) {
        return super.getNotification(context, notificationMessage);
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {//自定义消息
        Logger.e(JSON.toJSONString(customMessage));

        Intent intent = new Intent();
        num++;
        intent.setAction("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", "com.zhongyou.meet.mobile");
        intent.putExtra("className", "com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity");
        intent.putExtra("notificationNum", num);
        if (num != 0) {
            context.sendBroadcast(intent);
        }

        Bundle bunlde = new Bundle();
        bunlde.putString("package", "com.zhongyou.meet.mobile"); // com.test.badge is your package name
        bunlde.putString("class", "com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity"); // com.test. badge.MainActivity is your apk main activity
        bunlde.putInt("badgenumber", num);
        context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);

        super.onMessage(context, customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {//点击了通知栏
//		Intent intent=new Intent();
//		intent.setAction("com.meeting.mobile.opennotifycation");
//		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Logger.e(notificationMessage.notificationContent);
        super.onNotifyMessageOpened(context, notificationMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {//通知
        Logger.e(JSON.toJSONString(notificationMessage));
//		super.onNotifyMessageArrived(context, notificationMessage);
/*
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new NotificationCompat.Builder(context, "subscribe")
				.setContentTitle(notificationMessage.notificationTitle)
				.setContentText(notificationMessage.notificationContent)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher_logo)
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_logo))
				.setAutoCancel(true)
				.build();

		manager.notify(1, notification);*/

    }

    @Override
    public void onNotifyMessageUnShow(Context context, NotificationMessage notificationMessage) {
        Logger.e(JSON.toJSONString(notificationMessage));
        super.onNotifyMessageUnShow(context, notificationMessage);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
        Logger.e(JSON.toJSONString(notificationMessage));
        super.onNotifyMessageDismiss(context, notificationMessage);
    }

    @Override
    public void onRegister(Context context, String s) {
        Logger.e(JSON.toJSONString(s));
        super.onRegister(context, s);
    }

    @Override
    public void onConnected(Context context, boolean b) {
        Logger.e(JSON.toJSONString(b));
        super.onConnected(context, b);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        Logger.e(JSON.toJSONString(cmdMessage));
        super.onCommandResult(context, cmdMessage);
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        Logger.e(JSON.toJSONString(intent));
        super.onMultiActionClicked(context, intent);
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean b, int i) {
        //true 打开通知  false 关闭通知
        Logger.e(b + "----" + i);
        super.onNotificationSettingsCheck(context, b, i);
    }

    @Override
    public boolean isNeedShowNotification(Context context, NotificationMessage notificationMessage, String s) {
        Logger.e(JSON.toJSONString(notificationMessage));
        Logger.e(JSON.toJSONString(s));
        return false;
    }
}

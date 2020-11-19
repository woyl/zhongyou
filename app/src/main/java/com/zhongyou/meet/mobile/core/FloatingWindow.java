package com.zhongyou.meet.mobile.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;


/**
 * @author golangdorid@gmail.com
 * @date 2020/7/30 11:44 AM.
 * @
 */
public class FloatingWindow {
	/**
	 * 判断是否拥有悬浮窗权限
	 *
	 * @param isApplyAuthorization 是否申请权限
	 */
	public static boolean canDrawOverlays(Context context, boolean isApplyAuthorization) {
		//Android 6.0 以下无需申请权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//判断是否拥有悬浮窗权限，无则跳转悬浮窗权限授权页面
			if (Settings.canDrawOverlays(context)) {
				return true;
			} else {
				if (isApplyAuthorization) {
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
					if (context instanceof Service) {
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					context.startActivity(intent);
					return false;
				} else {
					return false;
				}
			}
		} else {
			return true;
		}
	}


}

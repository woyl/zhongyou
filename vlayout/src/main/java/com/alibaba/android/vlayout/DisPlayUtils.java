package com.alibaba.android.vlayout;

import android.content.Context;
import android.view.WindowManager;

/**
 * @author luopan@centerm.com
 * @date 2020-01-17 17:40.
 */
public class DisPlayUtils {

	public static int getHeight(Context context){
		 WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}
}

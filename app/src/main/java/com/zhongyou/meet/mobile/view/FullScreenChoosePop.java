package com.zhongyou.meet.mobile.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.AttachPopupView;
import com.zhongyou.meet.mobile.R;

/**
 * @author luopan@centerm.com
 * @date 2020-01-08 15:49.
 */
public class FullScreenChoosePop extends AttachPopupView {
	public FullScreenChoosePop(@NonNull Context context) {
		super(context);
	}

	@Override
	protected int getImplLayoutId() {
		return R.layout.pop_full_screen_choose;
	}

	@Override
	protected void onCreate() {
		super.onCreate();


	}

}

package com.zhongyou.meet.mobile.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.just.agentwebX5.IWebLayout;
import com.tencent.smtt.sdk.WebView;
import com.zhongyou.meet.mobile.R;


/**
 * @author golangdorid@gmail.com
 * @date 2020/4/8 9:51 AM.
 * @
 */
public class WebLayout implements IWebLayout {

	private Activity mActivity;
	private WebView mWebView = null;
	private  View mView;

	public WebLayout(Activity activity) {
		this.mActivity = activity;
		mView = LayoutInflater.from(activity).inflate(R.layout.fragment_twk_web, null);
		mWebView = (WebView) mView.findViewById(R.id.webView);
	}

	@NonNull
	@Override
	public ViewGroup getLayout() {
		return mWebView;
	}

	@Nullable
	@Override
	public WebView getWeb() {
		return mWebView;
	}



}

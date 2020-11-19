package com.zhongyou.meet.mobile.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.utils.DisplayUtil;

/**
 * @author luopan@centerm.com
 * @date 2019-11-18 10:14.
 */
public class MyWebView extends WebView {
	private ProgressBar progressBar1 = null;


	@SuppressLint("SetJavaScriptEnabled")
	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWeb();
	}

	@SuppressLint("SetJavaScriptEnabled")
	public MyWebView(Context arg0) {
		super(arg0);
		initWeb();
		setBackgroundColor(85621);
	}

	private void initWeb() {
		progressBar1 = (ProgressBar) LayoutInflater.from(getContext()).inflate(R.layout.progress_view, null);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(), 2));

		addView(progressBar1, params);
		WebSettings webSettings = getSettings();
		//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
		setWebChromeClient(webChromeClient);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);//允许使用js

		//不使用缓存，只从网络获取数据.
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		//支持屏幕缩放
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(false);

		//开启H5页面的DOM Storage功能
		webSettings.setDomStorageEnabled(true);


	}

	WebChromeClient webChromeClient = new WebChromeClient() {


		@Override
		public void onProgressChanged(WebView view, int newProgress) {

			if (newProgress >= 100) {
				progressBar1.setVisibility(View.GONE);
			} else {
				if (progressBar1.getVisibility() == View.GONE) {
					progressBar1.setVisibility(View.VISIBLE);
				}
				progressBar1.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
		}

	};
}

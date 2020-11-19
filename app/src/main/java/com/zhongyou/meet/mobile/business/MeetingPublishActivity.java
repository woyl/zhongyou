package com.zhongyou.meet.mobile.business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.MeetingAdmin;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;

/**
 * @author luopan
 */
public class MeetingPublishActivity extends AppCompatActivity {

	private WebView webView;
	private String url;
	private ProgressBar progressBar;
	private FrameLayout frameLayout;

	private MeetingAdmin meetingAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_publish);

		url = getIntent().getStringExtra("meeting_public_url");

		Log.v("url--->", url);
		progressBar = findViewById(R.id.progressBar);

		webView = findViewById(R.id.web_view);

		initControl();

		webView.loadUrl(url);

		frameLayout = findViewById(R.id.start_meeting);
		frameLayout.setOnClickListener(v -> {
			if (meetingAdmin != null) {
				startActivity(new Intent(MeetingPublishActivity.this, MeetingPublishActivity.class).putExtra("meeting_public_url", meetingAdmin.getPublishMeetingUrl()));
			}
		});

		ApiClient.getInstance().meetingAdmin(meetingAdminCallback);
	}

	private OkHttpCallback meetingAdminCallback = new OkHttpCallback<Bucket<MeetingAdmin>>() {

		@Override
		public void onSuccess(Bucket<MeetingAdmin> meetingAdminBucket) {
			meetingAdmin = meetingAdminBucket.getData();
			if (meetingAdmin.isMeetingAdmin()) {
				frameLayout.setVisibility(View.GONE);
			} else {
				frameLayout.setVisibility(View.GONE);
			}
		}
	};

	private void initControl() {
		//添加js监听 这样html就能调用客户端
		webView.addJavascriptInterface(this, "android");
		webView.setWebChromeClient(webChromeClient);
		webView.setWebViewClient(webViewClient);

		WebSettings webSettings = webView.getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);//允许使用js

		/**
		 * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
		 * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
		 * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
		 * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
		 */
		//不使用缓存，只从网络获取数据.
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		//支持屏幕缩放
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);

		//开启H5页面的DOM Storage功能
		webSettings.setDomStorageEnabled(true);

		//不显示webview缩放按钮
//        webSettings.setDisplayZoomControls(false);
	}


	//WebViewClient主要帮助WebView处理各种通知、请求事件
	private WebViewClient webViewClient = new WebViewClient() {
		@Override
		public void onPageFinished(WebView view, String url) {//页面加载完成
			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Uri meetingListUri = Uri.parse(url);
			Log.d("url--->", "" + url);
			if (TextUtils.equals("/meeting/list", meetingListUri.getPath())) {
				String meetingListType = meetingListUri.getQueryParameter("type");
				int type = meetingListType != null ? Integer.parseInt(meetingListType) : MeetingsFragment.TYPE_PUBLIC_MEETING;
				setResult(type);
				Log.d("url list--->", "" + type);
				MeetingPublishActivity.this.finish();
				return true;
			} else if (TextUtils.equals("/forum", meetingListUri.getPath())) {
				setResult(MeetingsFragment.TYPE_FORUM_MEETING);
				MeetingPublishActivity.this.finish();
				return true;
			} else if (TextUtils.equals("/meeting/creatBack", meetingListUri.getPath())) {
				String meetingListType = meetingListUri.getQueryParameter("type");
				int type = meetingListType != null ? Integer.parseInt(meetingListType) : MeetingsFragment.TYPE_PUBLIC_MEETING;
				Log.d("url creatBack--->", "" + type);
				setResult(type);
				MeetingPublishActivity.this.finish();
				return true;
			} else if (TextUtils.equals("/meeting", meetingListUri.getPath())) {
				Log.d("meeting--->", "" + url);
//                view.loadUrl(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, meetingListUri);
				startActivity(intent);
				MeetingPublishActivity.this.finish();
				return true;
			}
			ToastUtils.showToast("程序出错，请按回退键返回，并联系管理员");
			setResult(MeetingsFragment.TYPE_PUBLIC_MEETING);
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}

	};

	//WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
	private WebChromeClient webChromeClient = new WebChromeClient() {
		//不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
		@Override
		public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
			localBuilder.setMessage(message).setPositiveButton("确定", null);
			localBuilder.setCancelable(false);
			localBuilder.create().show();

			//注意:
			//必须要这一句代码:result.confirm()表示:
			//处理结果为确定状态同时唤醒WebCore线程
			//否则不能继续点击按钮
			result.confirm();
			return true;
		}

		//获取网页标题
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
		}

		//加载进度回调
		@Override
		public void onProgressChanged(WebView view, int newProgress) {

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//点击返回按钮的时候判断有没有上一页
		if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
			webView.goBack(); // goBack()表示返回webView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * JS调用android的方法
	 *
	 * @param str
	 * @return
	 */
	@JavascriptInterface //仍然必不可少
	public void getClient(String str) {
		System.out.println("ansen：html调用客户端:" + str);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//释放资源
		webView.destroy();
		webView = null;
	}

}

package com.zhongyou.meet.mobile;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zhongyou.meet.mobile.business.BaseDataBindingActivity;
import com.zhongyou.meet.mobile.databinding.ActivityWebViewBinding;

public class WebViewActivity extends BaseDataBindingActivity<ActivityWebViewBinding> {


	@Override
	protected int initContentView() {
		return R.layout.activity_web_view;
	}

	@Override
	public String getStatisticsTag() {
		return "隐私协议";
	}


	@Override
	protected void initView() {
		mBinding.title.setText("隐私协议");
		mBinding.webView.loadUrl(getIntent().getStringExtra("data"));
		mBinding.webView.setWebViewClient(mywebViewClient);

		mBinding.back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public WebViewClient mywebViewClient=new WebViewClient(){
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

	};


}

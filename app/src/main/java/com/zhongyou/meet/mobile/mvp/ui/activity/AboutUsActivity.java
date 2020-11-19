package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;

import butterknife.BindView;

public class AboutUsActivity extends BaseActivity {


	@BindView(R.id.webView)
	WebView webView;


	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {

	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		return R.layout.activity_about_us;
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {
		setTitle("");

		webView.loadUrl(getIntent().getStringExtra("url"));

		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}
}

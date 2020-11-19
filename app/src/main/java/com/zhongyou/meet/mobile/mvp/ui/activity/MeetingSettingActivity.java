package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.allen.library.SuperTextView;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.Page;

import butterknife.BindView;


@Page(name = "会议设置")
public class MeetingSettingActivity extends BaseActivity {

	@BindView(R.id.toolbar_back)
	RelativeLayout toolbarBack;
	@BindView(R.id.toolbar_title)
	TextView toolbarTitle;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.MicrophoneState)
	SuperTextView microphoneState;
	@BindView(R.id.cameraState)
	SuperTextView cameraState;
	@BindView(R.id.beautifulState)
	SuperTextView beautifulState;

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {

	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		setTitle("教室设置");
		return R.layout.activity_meeting_setting; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@SuppressLint("CheckResult")
	@Override
	public void initData(@Nullable Bundle savedInstanceState) {

		microphoneState.setSwitchIsChecked(MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true));
		cameraState.setSwitchIsChecked(MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true));
		beautifulState.setSwitchIsChecked(MMKV.defaultMMKV().decodeBool(MMKVHelper.BEAUTIFULL, true));

		microphoneState.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MMKV.defaultMMKV().encode(MMKVHelper.MICROPHONE_STATE, isChecked);
			}
		});
		cameraState.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MMKV.defaultMMKV().encode(MMKVHelper.CAMERA_STATE, isChecked);
			}
		});

		beautifulState.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				MMKV.defaultMMKV().encode(MMKVHelper.BEAUTIFULL, isChecked);
			}
		});

	}

}

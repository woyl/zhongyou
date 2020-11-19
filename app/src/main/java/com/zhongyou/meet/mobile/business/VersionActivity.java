package com.zhongyou.meet.mobile.business;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.allen.library.SuperButton;
import com.jess.arms.utils.RxLifecycleUtils;
import com.ycbjie.ycupdatelib.UpdateFragment;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.entities.Version;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.utils.ApkUtil;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.Page;
import com.zhongyou.meet.mobile.utils.ToastUtils;

import java.io.File;
import java.util.Locale;

import es.dmoral.toasty.Toasty;


@Page(name = "更新检测")
public class VersionActivity extends BasicActivity {

	private SuperButton mBtnUpgrade;
	private TextView mLabelViersion;
	private String upDataUrl;
	private Integer mIsForce;
	private String mVersionCode;
	private String mDescribe;
	private String mDownURL;

	@Override
	public String getStatisticsTag() {
		return "版本更新";
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);
		mBtnUpgrade = findViewById(R.id.bt_update);
		mLabelViersion = findViewById(R.id.label_version);
		//设置标题和返回键
		findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setTitle("更新检测");

		versionCheck();

		mBtnUpgrade.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(mVersionCode) && TextUtils.isEmpty(mDescribe) && TextUtils.isEmpty(mDownURL)) {
					versionCheck();
				} else {
					showUpDateDialog(mIsForce, mVersionCode, mDescribe, mDownURL);
				}

			}
		});
	}

	private void versionCheck() {

		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("appCode", "zyou_online_android");
		jsonObject.put("clientType", 1);


		HttpsRequest.provideClientApi().VersionCheck(jsonObject).compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void _onNext(JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							if (jsonObject.getJSONObject("data") == null) {
								mLabelViersion.setText(String.format(Locale.CHINA, "已是最新版本：中幼在线%s", BuildConfig.VERSION_NAME));
								mBtnUpgrade.setEnabled(false);
							} else {
								mIsForce = jsonObject.getJSONObject("data").getInteger("isForce");
								mVersionCode = jsonObject.getJSONObject("data").getString("version");
								mDescribe = jsonObject.getJSONObject("data").getString("describe");
								mDownURL = jsonObject.getJSONObject("data").getString("downURL");
								try {

									if (ApkUtil.compareVersion(mVersionCode, BuildConfig.VERSION_NAME) > 0) {
										mLabelViersion.setText(String.format(Locale.CHINA, "已有最新版本：中幼在线%s", mVersionCode));
										mBtnUpgrade.setEnabled(true);
										showUpDateDialog(mIsForce, mVersionCode, mDescribe, mDownURL);
									} else {
										mBtnUpgrade.setEnabled(false);
										mLabelViersion.setText(String.format(Locale.CHINA, "已是最新版本：中幼在线%s", BuildConfig.VERSION_NAME));
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}

							}
						} else {
							mBtnUpgrade.setEnabled(true);
							mLabelViersion.setText(String.format(Locale.CHINA, "已是最新版本：中幼在线%s", BuildConfig.VERSION_NAME));
							ToastUtils.showToast(VersionActivity.this, jsonObject.getString("errmsg"));
						}
					}
				});


	}

	public void showUpDateDialog(int isForce, String versionCode, String describe, String downURL) {

		UpdateUtils.APP_UPDATE_DOWN_APK_PATH = UpdateUtils.getFilePath(getApplicationContext()) + "download";
		String desc = "更新提示: V" + versionCode + "\n" + describe;
		UpdateFragment updateFragment = UpdateFragment.showFragment(this,
				isForce == 1, downURL,
				"zhongyouzaixian" + "_" + versionCode,
				desc, BuildConfig.APPLICATION_ID);


		updateFragment.setOnClickListener(new UpdateFragment.OnClickListener() {
			@Override
			public void onClick(int i) {
				//点击下载 1   暂停下载 1  或者下次再说 0
				if (i == 0) {

				} else if (i == 1) {
					int downloadStatus = updateFragment.getDownloadStatus();
					if (downloadStatus == UpdateUtils.DownloadStatus.UPLOADING) {
						//正在下载中  不能跳转到首页
						Toasty.info(VersionActivity.this, "当前下载已暂停", Toast.LENGTH_SHORT, true).show();
					}
				}


			}
		});
	}
}

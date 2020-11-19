package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.allen.library.SuperButton;
import com.allen.library.SuperTextView;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.ArmsUtils;
import com.maning.mndialoglibrary.MProgressDialog;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.VersionActivity;
import com.zhongyou.meet.mobile.di.component.DaggerSystemSettingComponent;
import com.zhongyou.meet.mobile.mvp.contract.SystemSettingContract;
import com.zhongyou.meet.mobile.mvp.presenter.SystemSettingPresenter;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.FileUtils;
import com.zhongyou.meet.mobile.utils.Page;
import com.zhongyou.meet.mobile.utils.Sampler;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 11:12
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "系统设置")
public class SystemSettingActivity extends BaseActivity<SystemSettingPresenter> implements SystemSettingContract.View {

	@BindView(R.id.toolbar_back)
	RelativeLayout toolbarBack;
	@BindView(R.id.toolbar_title)
	TextView toolbarTitle;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.acountSetting)
	SuperTextView acountSetting;
	@BindView(R.id.meetingSetting)
	SuperTextView meetingSetting;
	@BindView(R.id.aboutUs)
	SuperTextView aboutUs;
	@BindView(R.id.contactUs)
	SuperTextView contactUs;
	@BindView(R.id.checkUpDate)
	SuperTextView checkUpDate;
	@BindView(R.id.loginOut)
	SuperButton loginOut;
	@BindView(R.id.clearCache)
	SuperTextView clearCache;

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {
		DaggerSystemSettingComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);
	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		setTitle("系统设置");
		return R.layout.activity_system_setting; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {
		Sampler.getInstance().init(getApplicationContext(), 100L);
		Sampler.getInstance().start();

		checkUpDate.setRightString("当前版本:v" + BuildConfig.VERSION_NAME);
		if (mPresenter != null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("appCode", "zyou_online_android");
			jsonObject.put("clientType", 1);
			mPresenter.versionCheck(jsonObject);
		}
		getCacheSize();

	}

	public void getCacheSize() {
		try {
			String path = getExternalCacheDir().getPath();
			String cacheSize = FileUtils.getFormatSize(FileUtils.getFolderSize(new File(path)) + FileUtils.getFolderSize(new File(getExternalFilesDir(null).getAbsolutePath())));
			clearCache.setRightString(cacheSize);
			Timber.e("cacheSize---->%s", cacheSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {

	}

	@Override
	public void showMessage(@NonNull String message) {
		checkNotNull(message);
		ArmsUtils.snackbarText(message);
	}

	@Override
	public void launchActivity(@NonNull Intent intent) {
		checkNotNull(intent);
		ArmsUtils.startActivity(intent);
	}

	@Override
	public void killMyself() {
		finish();
	}

	@OnClick({R.id.toolbar_back, R.id.acountSetting, R.id.meetingSetting, R.id.aboutUs, R.id.contactUs, R.id.checkUpDate, R.id.loginOut, R.id.clearCache})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.toolbar_back:
				finish();
				break;
			case R.id.acountSetting:
				launchActivity(new Intent(this, AccountSettingActivity.class));
				break;
			case R.id.meetingSetting:
				launchActivity(new Intent(this, MeetingSettingActivity.class));
				break;
			case R.id.aboutUs:
				launchActivity(new Intent(this, AboutAgreementActivity.class));
				break;
			case R.id.contactUs:
				launchActivity(new Intent(this, ContactUsActivity.class));
				break;
			case R.id.checkUpDate:
				launchActivity(new Intent(this, VersionActivity.class));
				break;
			case R.id.loginOut:
				Preferences.clear();
				AppManager.getAppManager().startActivity(WXEntryActivity.class);
				finish();
				break;
			case R.id.clearCache:
				try {
					MProgressDialog.showProgress(this, "清理中...");
					FileUtils.deleteFolderFile(getExternalCacheDir().getPath(), true);
					FileUtils.deleteFolderFile(getExternalFilesDir(null).getPath(), true);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					MProgressDialog.dismissProgress();
					getCacheSize();
				}
				break;
		}
	}


	@Override
	public void versionCheck(boolean isNeedUpData) {
		if (isNeedUpData) {
			checkUpDate.setRightTvDrawableRight(ContextCompat.getDrawable(this, R.drawable.icon_red_point));
		} else {
			checkUpDate.setRightTvDrawableRight(null);
		}
	}
}

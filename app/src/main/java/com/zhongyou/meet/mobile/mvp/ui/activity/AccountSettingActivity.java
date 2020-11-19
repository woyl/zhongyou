package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.allen.library.SuperButton;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.maning.mndialoglibrary.MProgressDialog;
import com.tencent.mmkv.MMKV;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.di.component.DaggerAccountSettingComponent;
import com.zhongyou.meet.mobile.entities.User;
import com.zhongyou.meet.mobile.mvp.contract.AccountSettingContract;
import com.zhongyou.meet.mobile.mvp.presenter.AccountSettingPresenter;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.MatisseGlideEngine;
import com.zhongyou.meet.mobile.utils.Page;
import com.zhongyou.meet.mobile.utils.ToastUtils;

import org.reactivestreams.Subscription;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.autosize.utils.AutoSizeUtils;
import timber.log.Timber;
import top.zibin.luban.Luban;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 12:36
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "账户资料设置")
public class AccountSettingActivity extends BaseActivity<AccountSettingPresenter> implements AccountSettingContract.View {

	@BindView(R.id.toolbar_back)
	RelativeLayout toolbarBack;
	@BindView(R.id.toolbar_title)
	TextView toolbarTitle;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.userHeader)
	SuperTextView userHeader;
	@BindView(R.id.userNickName)
	SuperTextView userNickName;
	@BindView(R.id.userProfile)
	SuperTextView userProfile;
	@BindView(R.id.userLocation)
	SuperTextView userLocation;
	@BindView(R.id.save)
	SuperButton save;
	private static final int REQUEST_CODE_CHOOSE = 23;
	@BindView(R.id.userSchoolName)
	SuperTextView userSchoolName;
	@BindView(R.id.phoneNumber)
	SuperTextView phoneNumber;
	@BindView(R.id.relativeLayout)
	RelativeLayout relativeLayout;
	private CompositeDisposable mDisposable;

	private boolean isFirst = false;
	int count = 0;

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {
		DaggerAccountSettingComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);
	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		return R.layout.activity_account_setting; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {
		isFirst = getIntent().getBooleanExtra("isFirst", false);
		if (isFirst) {
			setTitle("设置账号");
			toolbarBack.setVisibility(View.GONE);
		} else {
			setTitle("个人资料");
			toolbarBack.setVisibility(View.VISIBLE);
		}
		mDisposable = new CompositeDisposable();
		showUserInformation(null);
		/*if (mPresenter != null) {
			mPresenter.getUserParentProfile();
		}*/
		phoneNumber.setRightString(MMKV.defaultMMKV().decodeString(MMKVHelper.MOBILE, ""));


//		relativeLayout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				count++;
//				if (count == 3) {
//					phoneNumber.setVisibility(View.VISIBLE);
//				} else if (count > 3) {
//					count = 0;
//					phoneNumber.setVisibility(View.GONE);
//				}
//			}
//		});
	}

	private void showUserInformation(User user) {
		if (user != null) {
			if (userHeader != null && userHeader.getRightIconIV() != null) {
				Glide.with(this)
						.asBitmap()
						.error(R.drawable.tx)
						.placeholder(R.drawable.tx)
						.load(user.getPhoto())
						.into(userHeader.getRightIconIV());
			}
			userNickName.setRightString(user.getName());
			if (TextUtils.isEmpty(user.getPostTypeName())) {
				userProfile.setRightString(MMKV.defaultMMKV().decodeString(MMKVHelper.POSTTYPENAME, ""));
			} else {
				userProfile.setRightString(user.getPostTypeName());
			}
			userLocation.setRightString(user.getAddress());

			if (userProfile.getRightString() != null
					&& (userProfile.getRightString().contains("幼儿园")
					|| (userProfile.getRightString().contains("园长"))
					|| (userProfile.getRightString().contains("教师")))) {
				showInputSchoolName(true);
				if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.KEY_UserSchoolName, ""))) {
					changeUserSchoolName("请输入幼儿园名称");
				} else {
					changeUserSchoolName(MMKV.defaultMMKV().decodeString(MMKVHelper.KEY_UserSchoolName));
				}
			} else {
				showInputSchoolName(false);
			}

		} else {
			if (!TextUtils.isEmpty(MMKV.defaultMMKV().getString(MMKVHelper.PHOTO, ""))) {
				Glide.with(this)
						.asBitmap()
						.error(R.drawable.tx)
						.placeholder(R.drawable.tx)
						.load(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO, ""))
						.into(userHeader.getRightIconIV());
			} else {
				Glide.with(this)
						.asBitmap()
						.error(R.drawable.tx)
						.placeholder(R.drawable.tx)
						.load(MMKV.defaultMMKV().decodeString(MMKVHelper.WEIXINHEAD, ""))
						.into(userHeader.getRightIconIV());
			}
			String nickName = MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME);
			String wechatNickName = MMKV.defaultMMKV().decodeString(MMKVHelper.weixinNickName);
			if (!TextUtils.isEmpty(nickName)) {
				userNickName.setRightString(nickName);
			} else if (!TextUtils.isEmpty(wechatNickName)) {
				userNickName.setRightString(wechatNickName);
			} else {
				userNickName.setRightString("请输入昵称");
			}
			if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.ADDRESS))) {
				userLocation.setRightString("请选择地址");
			} else {
				userLocation.setRightString(MMKV.defaultMMKV().decodeString(MMKVHelper.ADDRESS));
			}
			userProfile.setRightString(MMKV.defaultMMKV().decodeString(MMKVHelper.POSTTYPENAME, "请选择身份"));


			if (userProfile.getRightString() != null
					&& (userProfile.getRightString().contains("幼儿园")
					|| (userProfile.getRightString().contains("园长"))
					|| (userProfile.getRightString().contains("教师")))) {
				showInputSchoolName(true);
				if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.KEY_UserSchoolName, ""))) {
					changeUserSchoolName("请输入幼儿园名称");
				} else {
					changeUserSchoolName(MMKV.defaultMMKV().decodeString(MMKVHelper.KEY_UserSchoolName));
				}
			} else {
				showInputSchoolName(false);
			}

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


	@OnClick({R.id.toolbar_back, R.id.userHeader, R.id.userNickName, R.id.userProfile, R.id.userLocation, R.id.save, R.id.userSchoolName})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.toolbar_back:
				finish();
				break;
			case R.id.userHeader:
				Matisse.from(this)
						.choose(MimeType.of(MimeType.PNG, MimeType.JPEG, MimeType.BMP, MimeType.WEBP, MimeType.WEBM))
//						.choose(MimeType.ofImage(), false)
						.countable(true)
						.theme(R.style.MyMatisse_Zhihu)
						.capture(true)
						.captureStrategy(
								new CaptureStrategy(true, getPackageName() + ".fileProvider"))
						.maxSelectable(1)
						.gridExpectedSize(AutoSizeUtils.dp2px(this, 120))
						.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
						.thumbnailScale(0.85f)
						.imageEngine(new MatisseGlideEngine())
						.forResult(REQUEST_CODE_CHOOSE);
				break;
			case R.id.userNickName:
				if (mPresenter != null) {
					mPresenter.showInputNickNameDialog();
				}
				break;
			case R.id.userProfile:
				if (mPresenter != null) {
					mPresenter.showProfileDialog();
				}
				break;
			case R.id.userLocation:
				if (mPresenter != null) {
					mPresenter.showCityPicker();
				}
				break;
			case R.id.save:
				if (mPresenter != null) {
					if (isFirst) {
						if (TextUtils.isEmpty(userNickName.getRightString()) || userNickName.getRightString().trim().equals("请输入昵称")) {
							ToastUtils.showToast(AccountSettingActivity.this, "请输入昵称");
							return;
						}

						if (TextUtils.isEmpty(userProfile.getRightString()) || userProfile.getRightString().trim().equals("请选择身份")) {
							ToastUtils.showToast(AccountSettingActivity.this, "请选择身份");
							return;
						}
						if (TextUtils.isEmpty(userLocation.getRightString()) || userLocation.getRightString().trim().equals("请选择地址")) {
							ToastUtils.showToast(AccountSettingActivity.this, "请选择地址");
							return;
						}
					}
					mPresenter.setUserInformation();
				}
				break;
			case R.id.userSchoolName:
				// 幼儿园名称
				if (mPresenter != null) {
					mPresenter.showUserSchoolNameDialog();
				}
				break;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
			if (data != null) {
				List<String> list = Matisse.obtainPathResult(data);
				if (list.size() >= 0 && !TextUtils.isEmpty(list.get(0))) {
					withRx(list);
				}
			}

		}
	}

	private <T> void withRx(final List<T> photos) {
		mDisposable.add(Flowable.just(photos)
				.subscribeOn(Schedulers.io())
				.map(new Function<List<T>, List<File>>() {
					@Override
					public List<File> apply(@NonNull List<T> list) throws Exception {

						return Luban.with(AccountSettingActivity.this)
								.setTargetDir(getPath())
								.load(list)
								.get();
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe(new Consumer<Subscription>() {
					@Override
					public void accept(Subscription subscription) throws Exception {
						MProgressDialog.showProgress(AccountSettingActivity.this, "加载中……");
					}
				})
				.doOnError(new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) {
						ToastUtils.showToast("上传失败");
						Timber.e("上传失败---->%s", throwable.getMessage());
						MProgressDialog.dismissProgress();
					}
				})
				.onErrorResumeNext(Flowable.<List<File>>empty())
				.subscribe(new Consumer<List<File>>() {
					@Override
					public void accept(@NonNull List<File> list) {
						for (File file : list) {
							Timber.e("path---->%s", file.getAbsolutePath());
							Glide.with(AccountSettingActivity.this)
									.asBitmap()
									.error(R.drawable.tx)
									.placeholder(R.drawable.tx)
									.load(file.getAbsolutePath())
									.into(userHeader.getRightIconIV());

							if (mPresenter != null) {
								mPresenter.requestQiNiuToken(file.getAbsolutePath());
							}
						}
					}
				}));
	}


	private String getPath() {
		String path = UpdateUtils.getFilePath(getApplicationContext()) + "/Luban/image/";
		File file = new File(path);
		if (file.mkdirs()) {
			return path;
		}
		return path;
	}


	@Override
	public Activity getOnAttachActivity() {
		return this;
	}


	@Override
	public void UpDateUI(User user) {
		showUserInformation(user);
	}

	@Override
	public void changeUserType(String userCenter, String type) {
		if (TextUtils.isEmpty(type) || userCenter.equals(type)) {
			userProfile.setRightString(userCenter);
		} else {
			userProfile.setRightString(userCenter + "/" + type);
		}

	}

	@Override
	public void changeUserNickName(String nickname) {
		userNickName.setRightString(nickname);
	}

	@Override
	public void changeUserLocation(String location) {
		userLocation.setRightString(location);
	}

	@Override
	public ViewGroup getViewGroup() {
		return findViewById(R.id.container);
	}

	@Override
	public boolean isFirst() {
		return isFirst;
	}

	@Override
	public void showInputSchoolName(boolean isShow) {
		userSchoolName.setVisibility(isShow ? View.VISIBLE : View.GONE);
		if (isShow) {
			if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.KEY_UserSchoolName, ""))) {
				changeUserSchoolName("请输入幼儿园名称");
			} else {
				changeUserSchoolName(MMKV.defaultMMKV().decodeString(MMKVHelper.KEY_UserSchoolName));
			}
		}

	}

	@Override
	public void changeUserSchoolName(String schoolName) {
		userSchoolName.setRightString(schoolName);
	}

	@Override
	public void startActivity(Class activity) {
		Intent intent = new Intent(this, activity);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (mPresenter != null) {
			mPresenter.showSaveDialog();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		count = 0;
		if (mDisposable != null) {
			mDisposable.dispose();
			mDisposable.clear();
		}
	}
}

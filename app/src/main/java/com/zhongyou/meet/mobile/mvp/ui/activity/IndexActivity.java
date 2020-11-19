package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import android.text.TextUtils;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.igexin.sdk.PushManager;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.ycbjie.ycupdatelib.UpdateFragment;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.IM.IMInfoProvider;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.core.PlayService;
import com.zhongyou.meet.mobile.di.component.DaggerIndexComponent;
import com.zhongyou.meet.mobile.mvp.contract.IndexContract;
import com.zhongyou.meet.mobile.mvp.presenter.IndexPresenter;
import com.zhongyou.meet.mobile.mvp.presenter.MakerCourseAudioPresenter;
import com.zhongyou.meet.mobile.mvp.ui.fragment.ClassRoomFragment;
import com.zhongyou.meet.mobile.mvp.ui.fragment.MakerFragment;
import com.zhongyou.meet.mobile.mvp.ui.fragment.MeetingFragment;
import com.zhongyou.meet.mobile.mvp.ui.fragment.MyFragment;
import com.zhongyou.meet.mobile.mvp.ui.fragment.MyNewFragment;
import com.zhongyou.meet.mobile.mvp.ui.fragment.NewMakerFragment;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.ApkUtil;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.Page;
import com.zhongyou.meet.mobile.utils.StatusBarUtils;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import es.dmoral.toasty.Toasty;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import me.jessyan.autosize.AutoSizeCompat;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.NormalItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 03/20/2020 14:20
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "首页")
public class IndexActivity extends BaseActivity<IndexPresenter> implements IndexContract.View {
	@BindView(R.id.pager_bottom_tab)
	PageNavigationView pagerBottomTab;
	@BindView(R.id.container)
	FrameLayout container;

	private List<Fragment> mFragments;
	private FragmentManager mMamager;
	private Fragment mFragment;
	private long mExitTime;
	private ServiceConnection serviceConn;
	private PlayService.MusicBinder musicBinder;
	private AliansReeiver mAliansReeiver;
	private Handler handler;
	private Runnable runnable;

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {
		DaggerIndexComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);

	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		return R.layout.activity_index; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}



	@Override
	public void initData(@Nullable Bundle savedInstanceState) {
		if (StatusBarUtils.hasNotchInScreen(this)) {
			StatusBarUtils.setImmersionBar(this);
		}
		initFragment();
		initBottomBar();


		if (mPresenter != null) {
			//版本检测
//			mPresenter.versionCheck(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE + "");
			//设备注册
			mPresenter.registerDevices();

			//会议状态更改
			mPresenter.meetingJoinStats();
		}
		initPlayService();

		mPresenter.LoginIM();

		IMInfoProvider infoProvider = new IMInfoProvider();
		infoProvider.init(this);

		if (!TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.ID)) && !TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME)) && !TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO)) ) {
			UserInfo info = new UserInfo(MMKV.defaultMMKV().decodeString(MMKVHelper.ID), MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME), Uri.parse(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO)));

			RongIM.getInstance().refreshUserInfoCache(info);
			RongIM.getInstance().setCurrentUserInfo(info);
			RongIM.getInstance().setMessageAttachedUserInfo(true);
		}


		Intent intent = getIntent();
		Uri uri = intent.getData();
		if (uri != null) {
			if (uri.getHost().contains("com.hezy.guide.phone")) {
				String code = uri.getQueryParameter("j");
				String meetingId = uri.getQueryParameter("m");
				Timber.e("code---->%s", code);
				Timber.e("meetingId---->%s", meetingId);
				if (mFragments != null) {
					Constant.KEY_meetingID = meetingId;
					Constant.KEY_code = code;
				}

			}


		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String channelId = "video";
			String channelName = "视频直播";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			createNotificationChannel(channelId, channelName, importance);

		}
		//showUpDateDialog();
		DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
		Logger.e("Build.BRAND:--->" + Build.BRAND);

		if (BuildConfig.DEBUG && Build.BRAND.contains("samsung")) {
			verifyHasFloatViewPermission();
		}
			//腾讯个推
		if (!MMKV.defaultMMKV().decodeBool(Constant.KEY_BIND_ALIAS_TAG, false)) {
			PushManager.getInstance().bindAlias(this, MMKV.defaultMMKV().decodeString(MMKVHelper.ID));
		}
		//极光推送
		if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.KEY_SETALIASSUCCESS, false)) {
			JPushInterface.setAlias(this, 1, MMKV.defaultMMKV().decodeString(MMKVHelper.ID));
		}

		registerBoradCastReceiver();
		checkNotifySetting(MMKV.defaultMMKV().decodeBool(MMKVHelper.KEY_CHECK_NOTIFY, false));


//		new Thread(){
//			@Override
//			public void run() {
//				try {
//					// read from agconnect-services.json
//					// operation in MAIN thread prohibited
//					String appId = AGConnectServicesConfig.fromContext(IndexActivity.this).getString("client/app_id");
//					String token = HmsInstanceId.getInstance(IndexActivity.this).getToken(appId, "HCM");
//					Log.e("HMSTOKEN","HMS TOKEN-->"+token);
//				} catch (ApiException e) {
//					e.printStackTrace();
//				}
//
//			}
//		}.start();
//
//		String regId = MiPushClient.getRegId(getApplicationContext());
//		Logger.e("MIPUSH--->"+regId);

		//MMKVHelper.getInstance().saveFirstLogin(Preferences.getUserId() + "false");

		runnable = new Runnable() {
			@Override
			public void run() {
				checkVersion();
			}
		};

		handler = new Handler();
		handler.postDelayed(runnable,2000);
	}


	public void checkVersion() {
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("appCode", "zyou_online_android");
		jsonObject.put("clientType", 1);
		HttpsRequest.provideClientApi().VersionCheck(jsonObject).compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							if (jsonObject.getJSONObject("data") != null) {
								Integer isForce = jsonObject.getJSONObject("data").getInteger("isForce");
								String versionCode = jsonObject.getJSONObject("data").getString("version");
								String describe = jsonObject.getJSONObject("data").getString("describe");
								String downURL = jsonObject.getJSONObject("data").getString("downURL");
								try {
									if (ApkUtil.compareVersion(versionCode, BuildConfig.VERSION_NAME) > 0) {
										//MMKVHelper.getInstance().saveFirstLogin(Preferences.getUserId() + "true");
										if (isForce == 1) {
											showUpDateDialog(true, versionCode, describe, downURL);
										} else {
											showUpDateDialog(false, versionCode, describe, downURL);
										}
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					}

					@Override
					public void _onError(int code, String msg) {
						super._onError(code, msg);
					}
				});
	}



	public void showUpDateDialog(boolean isForce, String versionCode, String describe, String downURL) {

		UpdateUtils.APP_UPDATE_DOWN_APK_PATH = UpdateUtils.getFilePath(getApplicationContext()) + "download";
		String desc = "更新提示: V" + versionCode + "\n" + describe;
		UpdateFragment updateFragment = UpdateFragment.showFragment(IndexActivity.this,
				isForce, downURL,
				"zhongyouzaixian" + "_" + versionCode,
				desc, BuildConfig.APPLICATION_ID);

		updateFragment.setOnClickListener(new UpdateFragment.OnClickListener() {
			@Override
			public void onClick(int i) {
				//点击下载 1   暂停下载 1  或者下次再说 0
				if (i == 1) {
					int downloadStatus = updateFragment.getDownloadStatus();
					if (downloadStatus == UpdateUtils.DownloadStatus.UPLOADING) {
						//正在下载中  不能跳转到首页
						Toasty.info(IndexActivity.this, "当前下载已暂停", Toast.LENGTH_SHORT, true).show();
					}
				}
			}
		});
	}

	private void checkNotifySetting(boolean isNeedNotify) {
		NotificationManagerCompat manager = NotificationManagerCompat.from(this);
		// areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
		boolean isOpened = manager.areNotificationsEnabled();

		if (!isOpened && !isNeedNotify) {
			MessageDialog.show(IndexActivity.this, "是否开启通知权限", "没有开启通知权限，可能会错过会议", "开启", "不再提示").setOnOkButtonClickListener(new OnDialogButtonClickListener() {
				@Override
				public boolean onClick(BaseDialog baseDialog, View v) {
					// 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
					try {
						Intent intent = new Intent();
						intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
						//这种方案适用于 API 26, 即8.0（含8.0）以上可以用
						intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
						intent.putExtra(Settings.EXTRA_CHANNEL_ID, getApplicationInfo().uid);
						//这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
						intent.putExtra("app_package", getPackageName());
						intent.putExtra("app_uid", getApplicationInfo().uid);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
						// 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
						Intent intent = new Intent();
						intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri uri = Uri.fromParts("package", getPackageName(), null);
						intent.setData(uri);
						startActivity(intent);

					}
					return false;
				}
			}).setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
				@Override
				public boolean onClick(BaseDialog baseDialog, View v) {
					MMKV.defaultMMKV().encode(MMKVHelper.KEY_CHECK_NOTIFY, true);
					return false;
				}
			});
		}
	}

	private void registerBoradCastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.meeting.mobile.bindaliansfail");
		intentFilter.addAction("com.meeting.mobile.opennotifycation");
		intentFilter.addAction("com.meeting.mobile.setaliasfaliled");
		mAliansReeiver = new AliansReeiver();
		LocalBroadcastManager.getInstance(this).registerReceiver(mAliansReeiver, intentFilter);

	}

	private void verifyHasFloatViewPermission() {
		//Android 6.0 以下无需获取权限，可直接展示悬浮窗
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//判断是否拥有悬浮窗权限，无则跳转悬浮窗权限授权页面
			if (Settings.canDrawOverlays(this)) {

			} else {
				//跳转悬浮窗权限授权页面
				MessageDialog.show(IndexActivity.this, "是否开启视频悬浮窗", "检测到没有开启视频悬浮窗权限 是否开启", "开启", "取消").setOnOkButtonClickListener(new OnDialogButtonClickListener() {
					@Override
					public boolean onClick(BaseDialog baseDialog, View v) {
						startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
						return false;
					}
				});
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private void createNotificationChannel(String channelId, String channelName, int importance) {
		NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
		NotificationManager notificationManager = (NotificationManager) getSystemService(
				NOTIFICATION_SERVICE);
		if (notificationManager != null) {
			notificationManager.createNotificationChannel(channel);
		}

	}

	private void initPlayService() {
		//播放服务
		Intent intent = new Intent(getActivity(), PlayService.class);
		serviceConn = new ServiceConnection() {
			//连接异常断开
			public void onServiceDisconnected(ComponentName name) {

			}

			//连接成功
			public void onServiceConnected(ComponentName name, IBinder binder) {
				musicBinder = (PlayService.MusicBinder) binder;
				NewMakerFragment fragment = (NewMakerFragment) mFragments.get(0);
				fragment.setMusicBinder(musicBinder);
//				MakerFragment fragment = (MakerFragment) mFragments.get(0);
//				fragment.setMusicBinder(musicBinder);
				MakerCourseAudioPresenter.setMusicBinder(musicBinder);
				MakerCourseAudioDetailActivity.setMusicBinder(musicBinder);
				MoreDetailsActivity.Companion.setMusicBinder(musicBinder);
				MakerCourseVideoDetailActivity.setMusicBinder(musicBinder);
			}
		};

		bindService(intent, serviceConn, Service.BIND_AUTO_CREATE);
	}

	@Override
	public void initFragment() {
		mFragments = new ArrayList<>();
//		mFragments.add(new MakerFragment());
		mFragments.add(new NewMakerFragment());
//		mFragments.add(new MeetingFragment());
		mFragments.add(new ClassRoomFragment());
//		mFragments.add(new DiscussFragment());
//		mFragments.add(new MyFragment());
		mFragments.add(new MyNewFragment());
		mFragment = mFragments.get(0);
		mMamager = getSupportFragmentManager();
		FragmentTransaction transaction = mMamager.beginTransaction();
		transaction.replace(R.id.container, mFragments.get(0));
		transaction.commit();


	}


	@Override
	public void initBottomBar() {
		NavigationController navigationController = pagerBottomTab.custom()
				.addItem(newItem(R.drawable.icon_creater, R.drawable.icon_creater_select, "创客"))
				.addItem(newItem(R.drawable.icon_meeting, R.drawable.icon_meeting_select, "教室"))
//				.addItem(newItem(R.drawable.rzh, R.drawable.rzl, "日志"))
				.addItem(newItem(R.drawable.wdh, R.drawable.my_select, "我的"))
				.build();

		//底部按钮的点击事件监听
		navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
			@Override
			public void onSelected(int index, int old) {

				switchContent(mFragment, index);

			}

			@Override
			public void onRepeat(int index) {
			}
		});
//		navigationController.setSelect(0);
//		navigationController.setMessageNumber(2, 100);
//		navigationController.setHasMessage(3, true);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public AppCompatActivity getActivity() {
		return this;
	}

	/*因为启动页已经弄了版本检测更新 这个地方没必要了*/
	@Override
	public void showUpDateDialog() {

		Dialog dialog = new Dialog(this, R.style.MyDialog);
		View v = View.inflate(this, R.layout.dialog_update, null);
		dialog.setContentView(v);

		TextView tilte = v.findViewById(R.id.title);
		TextView message = v.findViewById(R.id.content);
		TextView close = v.findViewById(R.id.close);
		TextView update = v.findViewById(R.id.update);
		tilte.setText("更新提示");
		message.setText("1. 修复登陆问题\n2020.03.19");
		close.setText("关闭");
		update.setText("立即更新");
		dialog.show();

		update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});


	}

	@Override
	public void showAgreementDialog() {

	}

	@Override
	public void LoginImSuccess(boolean b) {
		if (!b) {
			MessageDialog.show(IndexActivity.this, "提示", "登录聊天系统失败", "退出").setOnOkButtonClickListener(new OnDialogButtonClickListener() {
				@Override
				public boolean onClick(BaseDialog baseDialog, View v) {
					killMyself();
					return false;
				}
			}).setCancelable(false);
		}
	}


	//创建一个Item 带文字图表
	private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
		NormalItemView normalItemView = new NormalItemView(this);
		normalItemView.initialize(drawable, checkedDrawable, text);
		normalItemView.setTextDefaultColor(getResources().getColor(R.color.color_999999));
		normalItemView.setTextCheckedColor(getResources().getColor(R.color.color_4095e8));
		return normalItemView;
	}

	public void switchContent(Fragment from, int position) {
		if (mFragment != mFragments.get(position)) {
			mFragment = mFragments.get(position);
			FragmentTransaction transaction = mMamager.beginTransaction();
			if (!mFragment.isAdded()) {
				// 隐藏当前的fragment，add下一个到Activity中
				transaction.hide(from).add(R.id.container, mFragment).commitAllowingStateLoss();
			} else {
				// 隐藏当前的fragment，显示下一个
				transaction.hide(from).show(mFragment).commitAllowingStateLoss();
				if(mFragment!=null && mFragment instanceof BaseFragment<?>){
					((BaseFragment<?>)mFragment).onRestartUI();
				}
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//查找当前fragment
		if(mFragment!=null && mFragment instanceof BaseFragment<?>){
			((BaseFragment<?>)mFragment).onRestartUI();
		}
	}


	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {

	}

	@Override
	protected void onDestroy() {
		mFragments = null;
		mFragment = null;
		mMamager = null;

		try {
			if (serviceConn != null) {
				unbindService(serviceConn);
			}

			if (mAliansReeiver != null) {
				LocalBroadcastManager.getInstance(this).unregisterReceiver(mAliansReeiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			super.onDestroy();
		}

		if (handler != null) {
			handler.removeCallbacks(runnable);
			handler = null;
		}
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


	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			Toast.makeText(this, "再按一次退出中幼在线", Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			quit();
		}
	}

	private void quit() {
		ZYAgent.onEvent(getApplicationContext(), "返回退出应用 连接服务 请求停止");
//		WSService.stopService(this);
		finish();
		System.exit(0);
	}

	@Override
	public Resources getResources() {
		AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));
		return super.getResources();
	}

	public class AliansReeiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() != null) {
				if (intent.getAction().equals("com.meeting.mobile.bindaliansfail")) {
					new Handler().postDelayed((Runnable) () -> {
						if (MMKV.defaultMMKV().decodeBool(Constant.KEY_BIND_ALIAS_TAG, false)) {
							PushManager.getInstance().bindAlias(IndexActivity.this, MMKV.defaultMMKV().decodeString(MMKVHelper.ID));
						}
					}, 60 * 1000);
				}else if (intent.getAction().equals("com.meeting.mobile.opennotifycation")) {
					intent = new Intent(IndexActivity.this, MessageDetailActivity.class);
					startActivity(intent);
				}else if (intent.getAction().equals("com.meeting.mobile.setaliasfaliled")) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							JPushInterface.setAlias(IndexActivity.this, 1, MMKV.defaultMMKV().decodeString(MMKVHelper.ID));
						}
					}, 60 * 1000);

				}
			}
		}
	}
}

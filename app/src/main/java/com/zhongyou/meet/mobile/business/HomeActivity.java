package com.zhongyou.meet.mobile.business;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.KickOffEvent;
import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.IM.IMInfoProvider;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.MeetingJoinStats;
import com.zhongyou.meet.mobile.entities.UserData;
import com.zhongyou.meet.mobile.entities.Version;
import com.zhongyou.meet.mobile.entities.Wechat;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.receiver.PhoneReceiver;
import com.zhongyou.meet.mobile.utils.DeviceUtil;
import com.zhongyou.meet.mobile.utils.Installation;
import com.zhongyou.meet.mobile.utils.Logger;
import com.zhongyou.meet.mobile.utils.Login.LoginHelper;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import rx.Subscription;
import rx.functions.Action1;


/**
 * 主页
 * Created by wufan on 2017/7/24.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {

	private ViewPager viewPager;
	private LinearLayout bottomLayout;
	private RadioGroup radioGroup;
	private RadioButton mettingRadio, discussRadio, logRadio, myRadio;

	private HomePagerAdapter mHomePagerAdapter;

	private ArrayList<Fragment> mFragments;

	private int mIntentType;


	private Subscription subscription;

	private PhoneReceiver phoneReceiver;
	private IntentFilter intentFilter;
	private AlertDialog mAlertDialog;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);

//		WSService.actionStart(mContext);

		subscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (o instanceof KickOffEvent) {
					KickOffEvent kickOffEvent= (KickOffEvent) o;
					if (kickOffEvent.isKickOff()){
						mAlertDialog = new AlertDialog(HomeActivity.this)
								.builder()
								.setTitle("账号异常")
								.setMsg("您的账号在别的地方进行了登陆 请重新登陆")
								.setPositiveButton("退出", new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										mAlertDialog.dismiss();
										startActivity(new Intent(HomeActivity.this, WXEntryActivity.class));
										finish();
									}
								});
						mAlertDialog.show();
					}else {
						mAlertDialog = new AlertDialog(HomeActivity.this)
								.builder()
								.setTitle("账号异常")
								.setMsg("当前登陆信息已失效 请重新登陆")
								.setPositiveButton("退出", new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										mAlertDialog.dismiss();
										startActivity(new Intent(HomeActivity.this, WXEntryActivity.class));
										finish();
									}
								});
						mAlertDialog.show();
					}
				}
			}
		});


		initView();
		versionCheck();
		initData();
		registerDevice();



		if (!TextUtils.isEmpty(Preferences.getMeetingTraceId())) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("meetingJoinTraceId", Preferences.getMeetingTraceId());
			params.put("meetingId", Preferences.getMeetingId());
			params.put("status", 2);
			params.put("type", 1);
			params.put("leaveType", 1);
			ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);
		}

	}







	private OkHttpCallback meetingJoinStatsCallback = new OkHttpCallback<Bucket<MeetingJoinStats>>() {

		@Override
		public void onSuccess(Bucket<MeetingJoinStats> meetingJoinStatsBucket) {
			Preferences.setMeetingId(null);
			Preferences.setMeetingTraceId(null);
		}
	};

	private void initView() {
		viewPager = findViewById(R.id.view_pager);
		bottomLayout = findViewById(R.id.bottom_layout);

		radioGroup = findViewById(R.id.radio_group);
		//会议
		mettingRadio = findViewById(R.id.meeting);
		//讨论
		discussRadio = findViewById(R.id.meeting_discuss);
		//日志
		logRadio = findViewById(R.id.meeting_log);
		// 我的
		myRadio = findViewById(R.id.meeting_my);


		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				switch (i) {
					case R.id.meeting:
						viewPager.setCurrentItem(0);
						break;
					case R.id.meeting_discuss:
						viewPager.setCurrentItem(1);
						break;
					case R.id.meeting_log:
						viewPager.setCurrentItem(2);
						break;
					case R.id.meeting_my:
						viewPager.setCurrentItem(3);
						break;
					default:
						Log.v("HomeActivity", "onCheckedChanged   has no current index:" + i);
						break;
				}
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				switch (position) {
					case 0:
						mettingRadio.setChecked(true);
						break;
					case 1:
						discussRadio.setChecked(true);
						break;
					case 2:
//						myRadio.setChecked(true);
						logRadio.setChecked(true);
						break;
					case 3:
						myRadio.setChecked(true);
						break;
					default:
						Logger.v("HomeActivity", "onPageSelected has no current position:" + position);
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		if (TextUtils.isEmpty(Preferences.getUserId())){
			startActivity(new Intent(this,WXEntryActivity.class).putExtra("isBadToken",true));
			finish();
		}

		IMInfoProvider infoProvider=new IMInfoProvider();
		infoProvider.init(this);

		UserInfo info=new UserInfo(Preferences.getUserId(),Preferences.getUserName(),Uri.parse(Preferences.getUserPhoto()));

		RongIM.getInstance().refreshUserInfoCache(info);
		RongIM.getInstance().setCurrentUserInfo(info);
		RongIM.getInstance().setMessageAttachedUserInfo(true);

		mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
		mFragments = new ArrayList<>();
		//会议
		mFragments.add(MeetingsFragment.newInstance());

		//讨论
		mFragments.add(DiscussFragment.newInstance());

		//日志
		mFragments.add(new RecordFragment());
		// 我的
		mFragments.add(ProfileFragment.newInstance());
		mHomePagerAdapter.setData(mFragments);
		viewPager.setAdapter(mHomePagerAdapter);
		viewPager.setOffscreenPageLimit(3);
		phoneReceiver = new PhoneReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.PHONE_STATE");
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(phoneReceiver, intentFilter);

	}

	private void initData() {
		mettingRadio.setChecked(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		setState(WSService.isPhoneOnline());
		if (!TextUtils.isEmpty(Preferences.getToken())) {
			ApiClient.getInstance().requestUser();
			getUserInfo();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (TextUtils.isEmpty(Preferences.getToken())) {
			startActivity(new Intent(this, WXEntryActivity.class));
			finish();
		}
	}


	private void getUserInfo() {
		if (Preferences.isLogin()) {
			ApiClient.getInstance().requestUserNew(this, new OkHttpCallback<com.alibaba.fastjson.JSONObject>() {
				@Override
				public void onSuccess(com.alibaba.fastjson.JSONObject json) {


					UserData entity = JSON.parseObject(json.getJSONObject("data").toJSONString(), UserData.class);


					if (entity == null || entity.getUser() == null) {
						return;
					}
//					Logger.i(JSON.toJSONString(entity));

//                    BindActivity.actionStart(WXEntryActivity.this,true,true);

					Wechat wechat = entity.getWechat();
					if (wechat != null) {
						LoginHelper.savaWeChat(wechat);
					}
				}


				@Override
				public void onFailure(int errorCode, BaseException exception) {
//					com.orhanobut.logger.Logger.e(exception.getMessage());
					if (errorCode == 40003 || errorCode == 40001) {
						ToastUtils.showToast("登陆信息已过期 请重新登陆");
						startActivity(new Intent(HomeActivity.this, WXEntryActivity.class));
						finish();
					}
				}
			});
		}

	}






	private void versionCheck() {
		ApiClient.getInstance().versionCheck(this, new OkHttpCallback<BaseBean<Version>>() {
			@Override
			public void onSuccess(BaseBean<Version> entity) {
				Version version = entity.getData();
				if (version == null || version.getImportance() == 0) {
					return;
				}
				//1:最新版，不用更新 2：小改动，可以不更新 3：建议更新 4 强制更新
				if (version.getImportance() != 1 && version.getImportance() != 2) {
					startActivity(new Intent(HomeActivity.this, UpdateActivity.class).putExtra("version", version));
				}


			}
		});
	}

	/**
	 * 上传设备信息
	 */
	private void registerDevice() {
		String uuid = Installation.id(this);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		float density = metric.density;
		int densityDpi = metric.densityDpi;

		StringBuffer msg = new StringBuffer("registerDevice ");
		if (TextUtils.isEmpty(uuid)) {
			msg.append("UUID为空");
			Logger.e(TAG, msg.toString());
		} else {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("uuid", uuid);
				jsonObject.put("androidId", TextUtils.isEmpty(Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID)) ? "" : Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
				jsonObject.put("manufacturer", Build.MANUFACTURER);
				jsonObject.put("name", Build.BRAND);
				jsonObject.put("model", Build.MODEL);
				jsonObject.put("sdkVersion", Build.VERSION.SDK_INT);
				jsonObject.put("screenDensity", "width:" + width + ",height:" + height + ",density:" + density + ",densityDpi:" + densityDpi);
				jsonObject.put("display", Build.DISPLAY);
				jsonObject.put("finger", Build.FINGERPRINT);
				//BUILD_TYPE
				jsonObject.put("appVersion", BuildConfig.BUILD_TYPE + "_" + BuildConfig.VERSION_NAME + "_" + BuildConfig.VERSION_CODE);
				jsonObject.put("cpuSerial", Installation.getCPUSerial());
				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				jsonObject.put("androidDeviceId", tm != null ? tm.getDeviceId() : "");
				jsonObject.put("buildSerial", Build.SERIAL);
				jsonObject.put("source", 2);
				jsonObject.put("internalSpace", DeviceUtil.getDeviceTotalMemory(this));
				jsonObject.put("internalFreeSpace", DeviceUtil.getDeviceAvailMemory(this));
				jsonObject.put("sdSpace", DeviceUtil.getDeviceTotalInternalStorage());
				jsonObject.put("sdFreeSpace", DeviceUtil.getDeviceAvailInternalStorage());
				ApiClient.getInstance().deviceRegister(this, jsonObject.toString(), registerDeviceCb);
				msg.append("apiClient.deviceRegister call");
			} catch (JSONException e) {
				e.printStackTrace();
				msg.append("registerDevice error jsonObject.put e.getMessage() = " + e.getMessage());
			} catch (SecurityException e) {
				e.printStackTrace();
				msg.append("registerDevice error jsonObject.put e.getMessage() = " + e.getMessage());
			}

		}
	}

	private OkHttpCallback registerDeviceCb = new OkHttpCallback<BaseBean>() {
		@Override
		public void onSuccess(BaseBean entity) {
			Log.d(TAG, "registerDevice 成功===");
		}
	};


	private long mExitTime = 0;

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
	protected void onStop() {
		super.onStop();

	}

	@Override
	public void onDestroy() {
		if (subscription!=null){
			subscription.unsubscribe();
		}
		if (mAlertDialog!=null){
			mAlertDialog.dismiss();
		}
		try {
			if (phoneReceiver != null) {
				unregisterReceiver(phoneReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			super.onDestroy();
			/*if (WSService.isOnline()) {
				//当前状态在线,可切换离线
				Log.i(TAG, "当前状态在线,可切换离线");
				ZYAgent.onEvent(mContext, "离线按钮,当前在线,切换到离线");
				RxBus.sendMessage(new SetUserChatEvent(false));
//                                            WSService.SOCKET_ONLINE =false;
//                                            setState(false);
			} else {
				ZYAgent.onEvent(mContext, "离线按钮,当前离线,无效操作");
			}*/
		}

	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {

	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		return 0;
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {

	}
}

package com.zhongyou.meet.mobile.wxapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.allen.library.SuperButton;
import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.RxBus;
import com.kongzue.dialog.interfaces.OnBackClickListener;
import com.kongzue.dialog.v3.FullScreenDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.maning.mndialoglibrary.MToast;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mmkv.MMKV;
import com.ycbjie.ycupdatelib.UpdateFragment;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.WebViewActivity;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.business.BindActivity;
import com.zhongyou.meet.mobile.entities.Data;
import com.zhongyou.meet.mobile.entities.FinishWX;
import com.zhongyou.meet.mobile.entities.LoginWechat;
import com.zhongyou.meet.mobile.entities.User;
import com.zhongyou.meet.mobile.entities.Version;
import com.zhongyou.meet.mobile.entities.Wechat;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.mvp.ui.activity.AccountSettingActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.IndexActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.ApkUtil;
import com.zhongyou.meet.mobile.utils.DataUtil;
import com.zhongyou.meet.mobile.utils.DeviceIdUtils;
import com.zhongyou.meet.mobile.utils.DeviceUtil;
import com.zhongyou.meet.mobile.utils.Installation;
import com.zhongyou.meet.mobile.utils.Login.LoginHelper;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.NetUtils;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.SpUtil;
import com.zhongyou.meet.mobile.utils.StatusBarUtils;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.Utils;
import com.zhongyou.meet.mobile.utils.listener.MonitorListener;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;
import com.zhongyou.meet.mobile.view.MyWebView;
import com.zhongyou.meet.mobile.view.dialog.LaucherLoginDialogFragment;
import com.zhongyou.meet.mobile.view.dialog.NoAgreeLoginDialogFragment;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by wufan on 2017/7/14.
 */

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler, EasyPermissions.PermissionCallbacks {

	@BindView(R.id.txt_phone)
	EditText txtPhone;
	@BindView(R.id.txt_code)
	EditText txtCode;
	@BindView(R.id.bt_getcode_text)
	Button btGetcodeText;
	@BindView(R.id.check)
	ImageView check;
	@BindView(R.id.argment)
	TextView argment;
	@BindView(R.id.argmentPrivate)
	TextView argmentPrivate;
	private RelativeLayout wchatLoginImage;
	private IWXAPI mWxApi;

	private Subscription subscription;
	private BasePopupView mLoadingDialog;
	private boolean mIsBadToken;

	private FullScreenDialog mFullScreenDialog;

	private int checked = 0;

	private static final String CHECKED = "check";
	private static final String PHONE = "phone";
	private static final String CODE = "code";
	//弹出协议同意记录
	private static final String DATA_AGREEMENT_AGREE = "data_agreement_agree";


	@SuppressLint("UseCompatLoadingForDrawables")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.e("onCreate");

		/*List<Activity> activityList = AppManager.getAppManager().getActivityList();
		for (Activity activity : activityList) {
			AppManager.getAppManager().removeActivity(activity);
		}*/
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
//		StatusBarUtils.setRootViewFitsSystemWindows(this,false);
		ButterKnife.bind(this);

		reToWx();
		mIsBadToken = getIntent().getBooleanExtra("isBadToken", false);

		wchatLoginImage = findViewById(R.id.layout_wx);
		wchatLoginImage.setClickable(true);
		wchatLoginImage.setEnabled(true);
		wchatLoginImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				if (checked != 1) {
//					ToastUtils.showToast("同意协议才能继续使用");
//					return;
//				}
				wxLogin();
			}
		});

		wchatLoginImage.setVisibility(View.VISIBLE);


		/*if (TextUtils.isEmpty(Preferences.getWeiXinHead())){
			getHostUrl(2);
		}else {
			getHostUrl(1);
		}*/


		if (EasyPermissions.hasPermissions(this, perms)) {
			if (Preferences.isLogin()) {
				HttpsRequest.provideClientApi().getUserInfo().compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<com.alibaba.fastjson.JSONObject>() {

					@Override
					public void onSubscribe(Disposable d) {
						super.onSubscribe(d);
					}

					@Override
					public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							User user = JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("user").toJSONString(), User.class);
							MMKVHelper.getInstance().saveID(user.getId());
							MMKVHelper.getInstance().saveAddress(user.getAddress());
							MMKVHelper.getInstance().saveAreainfo(user.getAreaInfo());
							MMKVHelper.getInstance().savePhoto(user.getPhoto());
							MMKVHelper.getInstance().saveTOKEN(user.getToken());
							MMKVHelper.getInstance().savePostTypeName(user.getPostTypeName());
							MMKVHelper.getInstance().saveMobie(user.getMobile());
							MMKVHelper.getInstance().saveUserNickName(user.getName());
							MMKV.defaultMMKV().encode(MMKVHelper.KEY_UserSchoolName, user.getCompanyName());
							Wechat wechat = null;
							if (jsonObject.getJSONObject("data").getJSONObject("wechat") != null) {
								wechat = JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("wechat").toJSONString(), Wechat.class);
							}
							LoginHelper.savaUser(user);
							if (TextUtils.isEmpty(user.getPhoto())) {
								if (wechat != null) {
									Preferences.setUserPhoto(wechat.getHeadimgurl());
									MMKVHelper.getInstance().savePhoto(wechat.getHeadimgurl());
								}

							}
						}
						registerDevice();

					}

					@Override
					public void _onError(int code, String msg) {
						super._onError(code, msg);
						Preferences.clear();
						MMKV.defaultMMKV().clearAll();
						MMKV.defaultMMKV().clearMemoryCache();
					}
				});
			} else {
				registerDevice();
			}

		} else {
			EasyPermissions.requestPermissions(this, "请授予必要的权限", 0, perms);
		}

		checkAgreement();
	}

	/**
	 * 协议温馨提示
	 * 1. 登录页打开时直接弹出
	 * 2. 不同意 则 显示二次提示
	 * 3. 二次提示 不同意 则直接退出程序
	 * 4. 同意后 记录到缓存，以后不再提示
	 */
	private void checkAgreement(){
		String agree = DataUtil.getStr(DATA_AGREEMENT_AGREE);
		if (TextUtils.isEmpty(agree)){
			showAgreementDialog();
		}
	}

	/**
	 * 同意了协议
	 */
	private void agreeAgreement(){
		//值可以随便设置 没有特殊意义 不为空就行
		DataUtil.putStr(DATA_AGREEMENT_AGREE, "" + System.currentTimeMillis());
	}

	/**
	 * 协议温馨提示 对话框
	 */
	private void showAgreementDialog() {
		LaucherLoginDialogFragment dialog1 = new LaucherLoginDialogFragment(WXEntryActivity.this);
		dialog1.setMonitorListener(new MonitorListener<Boolean>() {
			@Override
			public void OnMonitor(Boolean agree) {
				if (agree) {
					agreeAgreement();
				}else{
					//不同意 再次提示
					showAgreementAgainDialog();
				}
			}
		});
		dialog1.show(getSupportFragmentManager(),"");
	}

	/**
	 * 协议温馨提示 不同意对话框
	 */
	private void showAgreementAgainDialog(){
		NoAgreeLoginDialogFragment dialog2 = new NoAgreeLoginDialogFragment(WXEntryActivity.this);
		dialog2.setMonitorListener(new MonitorListener<Boolean>() {
			@Override
			public void OnMonitor(Boolean agree) {
				if (agree) {
					agreeAgreement();
				}else{
					//不同意 退出程序
					finish();
					quit();
				}
			}
		});
		dialog2.show(getSupportFragmentManager(),"");
	}

	/**
	 * 退出程序
	 */
	private void quit() {
		ZYAgent.onEvent(getApplicationContext(), "返回退出应用 连接服务 请求停止");
		AppManager.getAppManager().appExit();
	}

	private void wxLogin() {
		if (EasyPermissions.hasPermissions(WXEntryActivity.this, perms)) {
			if (Utils.isWeixinAvilible(WXEntryActivity.this)) {
				if (!NetUtils.isNetworkConnected(WXEntryActivity.this)) {
					ToastUtils.showToast("当前无网络连接 请检查网络");
					return;
				}
				if (Preferences.isLogin()) {
					initView();
					return;
				}
				wchatLogin();
			} else {
				ToastUtils.showToast(WXEntryActivity.this, "请安装微信");
			}
		} else {
			EasyPermissions.requestPermissions(WXEntryActivity.this, "请授予必要的权限", 0, perms);
		}
	}

	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		if (res != null) {
			Configuration config = res.getConfiguration();
			if (config != null && config.fontScale != 1.0f) {
				config.fontScale = 1.0f;
				res.updateConfiguration(config, res.getDisplayMetrics());
			}
		}
		return res;
	}

//	private void showAgreementDialog() {
		/*Dialog dialog = new Dialog(this, R.style.MyDialog);
		View v = View.inflate(this, R.layout.dialog_agrement, null);
		dialog.setContentView(v);
		TextView title = v.findViewById(R.id.title);
		WebView content = v.findViewById(R.id.content);
		CheckBox checkBox = v.findViewById(R.id.checkBox);
		SuperButton confirm = v.findViewById(R.id.confirm);


		try {
			content.loadUrl(Constant.AGREEMENTURL);
		} catch (Exception e) {
			e.printStackTrace();
		}


		confirm.setEnabled(false);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					confirm.setEnabled(true);
				} else {
					confirm.setEnabled(false);
				}
			}
		});

		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.dismiss();
				registerDevice();
			}
		});
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();*/

//	}

	@Override
	public void onDestroy() {
		if (subscription != null) {
			subscription.unsubscribe();
		}
		if (mWxApi != null) {
			mWxApi.detach();
		}

		cancelDialog();
		super.onDestroy();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onPermissionsGranted(int i, @NonNull List<String> list) {
		/*if (SpUtil.getBoolean("isFirstIn", true)) {
			showAgreementDialog();
			return;
		}*/
		registerDevice();

	}

	@Override
	public void onPermissionsDenied(int i, @NonNull List<String> list) {
		if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
			new AppSettingsDialog.Builder(this)
					.setTitle("权限不足")
					.setRationale("请授予必须的权限，否则应用无法正常运行")
					.build().show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
			// Do something after user returned from app settings screen, like showing a Toast.
//            Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
		}
	}

	private String[] perms = {
			Manifest.permission.CAMERA,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
	};


	protected void initView() {
		Logger.e("initView");
		if (mIsBadToken) {
			Toasty.info(WXEntryActivity.this, "登陆信息失效 请重新登陆", Toasty.LENGTH_SHORT, true).show();
			Logger.e("当前token异常 需要重新登陆");
			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
			mIsBadToken = false;
			return;
		}
		if (Preferences.isLogin()) {
			String userMobile = Preferences.getUserMobile();
			if (TextUtils.isEmpty(userMobile)) {
				BindActivity.actionStart(WXEntryActivity.this, true, false);
				finish();
			} else if (Preferences.isUserinfoEmpty()) {
				startActivity(new Intent(WXEntryActivity.this, AccountSettingActivity.class).putExtra("isFirst", true));
				finish();
			} else {
				loginIM();
			}


		} else {
			Logger.e("没有登陆");
			if (mLoadingDialog != null) {
				Logger.e("mLoadingDialog!=null");
				mLoadingDialog.dismiss();
			}
		}

		mWxApi.handleIntent(getIntent(), this);

		subscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (o instanceof FinishWX) {
					//会打开两个微信界面
					finish();
				}
			}
		});


	}

	private void loginIM() {
		if (mLoadingDialog == null) {
			mLoadingDialog = new XPopup.Builder(this)
					.dismissOnBackPressed(false)
					.dismissOnTouchOutside(false)
					.asLoading("正在加载中")
					.show();
		} else {
			mLoadingDialog.show();
		}

		if (!TextUtils.isEmpty(Preferences.getImToken())) {
			IMConnect(Preferences.getImToken());
			Logger.e("IM token 不为空 直接登录 IM");
		} else {
			String photo = Preferences.getUserPhoto();
			String userName = Preferences.getUserName();
			if (TextUtils.isEmpty(Preferences.getUserPhoto())) {
				photo = Preferences.getWeiXinHead();
			}
			if (TextUtils.isEmpty(userName)) {
				userName = Preferences.getWeiXinNickName();
			}
			com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
			object.put("nickname", userName);
			object.put("userId", Preferences.getUserId());
			object.put("avatar", photo);
			HttpsRequest.provideClientApi().getToken(object)
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<com.alibaba.fastjson.JSONObject>() {

						@Override
						public void onSubscribe(Disposable d) {

						}

						@Override
						public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								if (jsonObject.getJSONObject("data").getInteger("code") == 200) {
									Constant.IMTOKEN = jsonObject.getJSONObject("data").getString("token");

									MMKVHelper.getInstance().saveID(Preferences.getUserId());
									MMKVHelper.getInstance().saveAddress(Preferences.getUserAddress());
									MMKVHelper.getInstance().saveAreainfo(Preferences.getAreaInfo());
									MMKVHelper.getInstance().savePhoto(Preferences.getUserPhoto());
									MMKVHelper.getInstance().saveTOKEN(Preferences.getToken());
									MMKVHelper.getInstance().savePostTypeName(Preferences.getUserPostType());
									MMKVHelper.getInstance().saveMobie(Preferences.getUserMobile());
									MMKVHelper.getInstance().saveUserNickName(Preferences.getUserName());
									MMKV.defaultMMKV().encode(MMKVHelper.CUSTOMNAME, Preferences.getUserCustom());

									Preferences.setImToken(Constant.IMTOKEN);
									IMConnect(Constant.IMTOKEN);
								} else {
									if (mLoadingDialog != null) {
										mLoadingDialog.dismiss();
									}
									showToastyInfo("当前登陆信息失效 请重新登陆");
									Preferences.clear();
								}
							} else {
								Preferences.clear();
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
								}
							}
						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							Preferences.clear();
							if (mLoadingDialog != null) {
								mLoadingDialog.dismiss();
							}
						}
					});
		}
	}

	public void showToastyInfo(String message) {
		Toasty.info(this, message, Toast.LENGTH_SHORT, true).show();
	}

	/**
	 * 上传设备信息
	 */
	private void registerDevice() {
		Logger.e("registerDevice");
		if (!NetUtils.isNetworkConnected(this)) {
			ToastUtils.showToast("当前无网络连接");
			return;
		}
		if (mLoadingDialog == null) {
			mLoadingDialog = new XPopup.Builder(this)
					.dismissOnBackPressed(false)
					.dismissOnTouchOutside(false)
					.asLoading("正在加载中")
					.show();
		} else {
			if (!mLoadingDialog.isShow()) {
				mLoadingDialog.show();
			}
		}

		String uuid = Installation.id(this);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		float density = metric.density;
		int densityDpi = metric.densityDpi;
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
			//BUILD_TYPE FLAVOR
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OkHttpCallback registerDeviceCb = new OkHttpCallback<BaseBean>() {
		@Override
		public void onSuccess(BaseBean entity) {

		}

		@Override
		public void onFinish() {
			checkVersion();
		}
	};

	public void checkVersion() {
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("appCode", "zyou_online_android");
		jsonObject.put("clientType", 1);
		HttpsRequest.provideClientApi().VersionCheck(jsonObject).compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<com.alibaba.fastjson.JSONObject>() {
					@Override
					public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							if (jsonObject.getJSONObject("data") == null) {
								initEntry(0);
							} else {
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
									} else {
										initEntry(8);
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}

						} else {
							initEntry(0);
						}
					}

					@Override
					public void _onError(int code, String msg) {
						super._onError(code, msg);
						initEntry(2);
					}
				});
	}


	public void showUpDateDialog(boolean isForce, String versionCode, String describe, String downURL) {

		UpdateUtils.APP_UPDATE_DOWN_APK_PATH = UpdateUtils.getFilePath(getApplicationContext()) + "download";
		String desc = "更新提示: V" + versionCode + "\n" + describe;
		UpdateFragment updateFragment = UpdateFragment.showFragment(WXEntryActivity.this,
				isForce, downURL,
				"zhongyouzaixian" + "_" + versionCode,
				desc, BuildConfig.APPLICATION_ID);

		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
		}

		updateFragment.setOnClickListener(new UpdateFragment.OnClickListener() {
			@Override
			public void onClick(int i) {
				//点击下载 1   暂停下载 1  或者下次再说 0
				if (i == 0) {
					initEntry(1);
				} else if (i == 1) {
					int downloadStatus = updateFragment.getDownloadStatus();
					if (downloadStatus == UpdateUtils.DownloadStatus.UPLOADING) {
						//正在下载中  不能跳转到首页
						Toasty.info(WXEntryActivity.this, "当前下载已暂停", Toast.LENGTH_SHORT, true).show();
					}
				}


			}
		});
	}


	private void initEntry(int i) {
		Logger.e("initEntry   " + i);
		if (EasyPermissions.hasPermissions(this, perms)) {
			initView();
		} else {
			EasyPermissions.requestPermissions(this, "请授予必要的权限", 0, perms);
		}
	}

	private void versionCheck() {
		Logger.e("versionCheck");
		if (!NetUtils.isNetworkConnected(this)) {
			ToastUtils.showToast("当前无网络连接");
			return;
		}
		ApiClient.getInstance().versionCheck(this, new OkHttpCallback<BaseBean<Version>>() {
			@Override
			public void onSuccess(BaseBean<Version> entity) {
				Logger.e(JSON.toJSONString(entity));
				Version version = entity.getData();
				if (version == null || version.getImportance() == 0 || version.getImportance() == 1) {
					initEntry(0);
					return;
				}
				//1:最新版，不用更新 2：小改动，可以不更新 3：建议更新 4 强制更新
				boolean isForceUpDate = false;
				if (version.getImportance() == 4) {
					/*startActivity(new Intent(getApplication(), UpdateActivity.class).putExtra("version", version));
					finish();*/
					isForceUpDate = true;
				} else if (version.getImportance() == 2 || version.getImportance() == 3) {
					//弹窗提醒更新
					isForceUpDate = false;
				}

				try {
					if (ApkUtil.compareVersion(version.getVersionDesc(), BuildConfig.VERSION_NAME) > 0) {
						UpdateUtils.APP_UPDATE_DOWN_APK_PATH = UpdateUtils.getFilePath(getApplicationContext()) + "download";
						String desc = version.getName() + "\n" + "最新版本:" + version.getVersionDesc();
						UpdateFragment updateFragment = UpdateFragment.showFragment(WXEntryActivity.this,
								isForceUpDate, version.getUrl(),
								"zhongyouzaixian" + "_" + version.getVersionDesc(),
								desc, BuildConfig.APPLICATION_ID);

						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}

						updateFragment.setOnClickListener(new UpdateFragment.OnClickListener() {
							@Override
							public void onClick(int i) {
								//点击下载 1   暂停下载 1  或者下次再说 0
								if (i == 0) {
									initEntry(1);
								} else if (i == 1) {
									int downloadStatus = updateFragment.getDownloadStatus();
									if (downloadStatus == UpdateUtils.DownloadStatus.UPLOADING) {
										//正在下载中  不能跳转到首页
										Toasty.info(WXEntryActivity.this, "当前下载已暂停", Toast.LENGTH_SHORT, true).show();
									}
								}


							}
						});
					} else {
						initEntry(2);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				initEntry(3);

//                initEntry();
			}
		});
	}

	private void reToWx() {
		String app_id = BuildConfig.WEIXIN_APP_ID;//wx9a61fda9d6d2e0fb
		mWxApi = WXAPIFactory.createWXAPI(this.getApplicationContext(), app_id, false);
		mWxApi.registerApp(app_id);
	}

	public void wchatLogin() {
		Logger.e("wchatLogin");
		if (mWxApi != null && !mWxApi.isWXAppInstalled()) {
			ToastUtils.showToast("您还未安装微信客户端，请先安装");
			return;
		}

		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "GuideMobile_wx_login";
		mWxApi.sendReq(req);
		Log.v("wxphone98", "1");
		showDialog("正在加载...");
	}

	@Override
	public void onReq(BaseReq baseReq) {
		Log.v("wxphone98", "2");
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
//app发送消息给微信，处理返回消息的回调
	@Override
	public void onResp(BaseResp baseResp) {
		Logger.e("wchatLogin  onResp");
		String result;
		Log.e("WXEntryActivity", "onResp: " + baseResp.errCode);
		switch (baseResp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = "登录成功";
				if (baseResp.getType() == 1) {
					ZYAgent.onEvent(WXEntryActivity.this, "微信按钮 登录成功");
					final SendAuth.Resp sendResp = ((SendAuth.Resp) baseResp);
					String code = sendResp.code;
					Log.e("WXEntryActivity", "onResp: " + code + " -  state -  " + sendResp.state);
					requestWechatLogin(sendResp.code, sendResp.state);
					ZYAgent.onEvent(WXEntryActivity.this, "请求微信登录");
				}
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				ZYAgent.onEvent(WXEntryActivity.this, "微信按钮 用户拒绝授权");
				result = "用户拒绝授权";
				cancelDialog();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				ZYAgent.onEvent(WXEntryActivity.this, "微信按钮 用户取消");
				result = "用户取消";
				cancelDialog();
				break;
			default:
				ZYAgent.onEvent(WXEntryActivity.this, "微信按钮 失败");
				result = "失败";
				cancelDialog();
				break;
		}

		if (!TextUtils.isEmpty(result) && baseResp.errCode != BaseResp.ErrCode.ERR_OK) {
			showToastyInfo(result);
			return;
		}
	}


	private void requestWechatLogin(String code, String state) {
		if (!NetUtils.isNetworkConnected(this)) {
			ToastUtils.showToast("当前无网络连接");
			return;
		}
		ApiClient.getInstance().requestWechat(code, state, this, new OkHttpCallback<BaseBean<LoginWechat>>() {

			@Override
			public void onSuccess(BaseBean<LoginWechat> entity) {
				ZYAgent.onEvent(WXEntryActivity.this, "请求微信登录回调 成功");
				cancelDialog();
				Logger.e(entity.getData().toString());
				if (entity.getData() == null) {
					return;
				}

				LoginWechat loginWechat = entity.getData();
				Wechat wechat = loginWechat.getWechat();
				Preferences.setWeixinNickname(wechat.getNickname());
				MMKV.defaultMMKV().encode(MMKVHelper.weixinNickName, wechat.getNickname());
				MMKV.defaultMMKV().encode(MMKVHelper.WEIXINHEAD, wechat.getHeadimgurl());
				Preferences.setWeiXinHead(wechat.getHeadimgurl());
				Preferences.setUserPhoto(wechat.getHeadimgurl());
				Preferences.setUserName(wechat.getNickname());
				User user = loginWechat.getUser();


				if (loginWechat.getUser() == null) {
				} else {
					MMKVHelper.getInstance().saveID(user.getId());
					MMKVHelper.getInstance().saveAddress(user.getAddress());
					MMKVHelper.getInstance().saveAreainfo(user.getAreaInfo());
					MMKVHelper.getInstance().savePhoto(user.getPhoto());
					MMKVHelper.getInstance().saveTOKEN(user.getToken());
					MMKVHelper.getInstance().savePostTypeName(user.getPostTypeName());
					MMKVHelper.getInstance().saveMobie(user.getMobile());
					MMKVHelper.getInstance().saveUserNickName(user.getName());
					MMKV.defaultMMKV().encode(MMKVHelper.KEY_UserSchoolName, user.getCompanyName());
					LoginHelper.savaUser(user);
					if (TextUtils.isEmpty(user.getPhoto())) {
						Preferences.setUserPhoto(wechat.getHeadimgurl());
					}
					if (TextUtils.isEmpty(user.getMobile())) {
						BindActivity.actionStart(WXEntryActivity.this, true, false);
						RxBus.sendMessage(new FinishWX());
						finish();
					} else if (Preferences.isUserinfoEmpty()) {
//						boolean isUserAuthByHEZY = user.getAuditStatus() == 1;
						startActivity(new Intent(WXEntryActivity.this, AccountSettingActivity.class).putExtra("isFirst", true));
//						UserInfoActivity.actionStart(WXEntryActivity.this, true, isUserAuthByHEZY);
//						RxBus.sendMessage(new FinishWX());
						finish();
					} else {
						/*startActivity(new Intent(WXEntryActivity.this, HomeActivity.class));
						finish();
						RxBus.sendMessage(new FinishWX());*/
						loginIM();
					}

				}
			}

			@Override
			public void onFinish() {
				cancelDialog();
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
			}
		});
	}

	private ProgressDialog progressDialog;

	protected void showDialog(String message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(message);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	protected void cancelDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}


	public void IMConnect(String token) {
		Logger.e("IM  token:" + token);
		RongIM.connect(token, new RongIMClient.ConnectCallbackEx() {


			@Override
			public void OnDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

			}

			@Override
			public void onTokenIncorrect() {
				Logger.e("onTokenIncorrect");
				Preferences.setImToken("");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}
						showToastyInfo("当前登陆信息异常 请重试");
					}
				});

			}

			@Override
			public void onSuccess(String s) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}

					}
				});
				Logger.e("容云IM登陆成功:" + s);

				MMKVHelper.getInstance().saveID(Preferences.getUserId());
				MMKVHelper.getInstance().saveAddress(Preferences.getUserAddress());
				MMKVHelper.getInstance().saveAreainfo(Preferences.getAreaInfo());
				MMKVHelper.getInstance().savePhoto(Preferences.getUserPhoto());
				MMKVHelper.getInstance().saveTOKEN(Preferences.getToken());
				MMKVHelper.getInstance().savePostTypeName(Preferences.getUserPostType());
				MMKVHelper.getInstance().saveMobie(Preferences.getUserMobile());
				MMKVHelper.getInstance().saveUserNickName(Preferences.getUserName());

				RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

					@Override
					public UserInfo getUserInfo(String userId) {

						UserInfo userInfo = new UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto()));
						RongIM.getInstance().refreshUserInfoCache(userInfo);
						RongIM.getInstance().setCurrentUserInfo(userInfo);
						return userInfo;//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
					}
				}, false);
				startActivity(new Intent(WXEntryActivity.this, IndexActivity.class));
				finish();
				RxBus.sendMessage(new FinishWX());
			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				Logger.e(errorCode.toString());
				Preferences.setImToken("");
				loginIM();
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		quit();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (mWxApi != null && intent != null) {
			mWxApi.handleIntent(intent, this);
		}
	}

	@OnClick({R.id.bt_getcode_text, R.id.bt_login, R.id.argment, R.id.argmentPrivate, R.id.check})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.bt_getcode_text:
				RxPermissions rxPermissions = new RxPermissions(this);
				rxPermissions.request(Manifest.permission.CAMERA,
						Manifest.permission.READ_PHONE_STATE,
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.RECORD_AUDIO,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE)
						.subscribe(new Consumer<Boolean>() {
							@Override
							public void accept(Boolean aBoolean) throws Exception {
								if (aBoolean) {
									if (txtPhone.getText().toString().trim().length() != 11) {
										ToastUtils.showToast("请输入正确的手机号码");
										return;
									}
									getCheckCode(txtPhone.getText().toString().trim());
								} else {
									ToastUtils.showToast("请授予必要的权限");
								}

							}
						});
				break;
			case R.id.bt_login:
				if (txtPhone.getText().toString().trim().length() != 11) {
					ToastUtils.showToast("请输入正确的手机号码");
					return;
				}
				if (txtCode.getText().toString().trim().length() <= 0) {
					ToastUtils.showToast("请输入验证码");
					return;
				}
				/**这里去掉了点击选择*/
//				if (checked != 1) {
//					ToastUtils.showToast("同意协议才能继续使用");
//					return;
//				}
				loginWithPhone(txtPhone.getText().toString().trim(), txtCode.getText().toString().trim());
				break;
			case R.id.argment:
				mFullScreenDialog = FullScreenDialog.show(this, R.layout.activity_web_view, new FullScreenDialog.OnBindView() {
					@Override
					public void onBind(FullScreenDialog dialog, View rootView) {
						rootView.findViewById(R.id.titleRela).setVisibility(View.GONE);
						MyWebView webView = rootView.findViewById(R.id.web_view);
						webView.loadUrl(Constant.AGREEMENTURL);
					}
				}).setOkButton("关闭")
						.setOnBackClickListener(new OnBackClickListener() {
							@Override
							public boolean onBackClick() {
								if (mFullScreenDialog != null && mFullScreenDialog.isShow) {
									mFullScreenDialog.doDismiss();
								}
								return true;
							}
						});
				/*Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra("data", Constant.AGREEMENTURL);
				startActivity(intent);*/
				break;
			case R.id.argmentPrivate:
				mFullScreenDialog = FullScreenDialog.show(this, R.layout.activity_web_view, new FullScreenDialog.OnBindView() {
					@Override
					public void onBind(FullScreenDialog dialog, View rootView) {
						rootView.findViewById(R.id.titleRela).setVisibility(View.GONE);
						MyWebView webView = rootView.findViewById(R.id.web_view);
						webView.loadUrl(Constant.AGREEMENTURL_PRIVATE);
					}
				}).setOkButton("关闭")
						.setOnBackClickListener(new OnBackClickListener() {
							@Override
							public boolean onBackClick() {
								if (mFullScreenDialog != null && mFullScreenDialog.isShow) {
									mFullScreenDialog.doDismiss();
								}
								return true;
							}
						});
				break;
			case R.id.check:
				if (checked == 0) {
					check.setImageDrawable(getResources().getDrawable(R.drawable.icon_checked));
					checked = 1;
				} else {
					check.setImageDrawable(getResources().getDrawable(R.drawable.icon_check));
					checked = 0;
				}
				break;

		}
	}

	private void loginWithPhone(String phone, String code) {
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("mobile", phone);
		jsonObject.put("verifyCode", code);
		jsonObject.put("deviceUUID", DeviceIdUtils.getDeviceId());
		jsonObject.put("requestUUID", "");
		jsonObject.put("loginNotice", false);
		jsonObject.put("userToken", "");
		HttpsRequest.provideClientApi().loginWithPhone(jsonObject)
				.compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<com.alibaba.fastjson.JSONObject>() {
					@Override
					public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							if (TextUtils.isEmpty(jsonObject.getJSONObject("data").getJSONObject("user").getString("name"))
									|| TextUtils.isEmpty(jsonObject.getJSONObject("data").getJSONObject("user").getString("address"))
									|| TextUtils.isEmpty(jsonObject.getJSONObject("data").getJSONObject("user").getString("postTypeName"))) {
								Preferences.setUserMobile(phone);
								MMKV.defaultMMKV().encode(MMKVHelper.MOBILE, phone);
								MMKV.defaultMMKV().encode(MMKVHelper.ID, jsonObject.getJSONObject("data").getJSONObject("user").getString("id"));
								MMKV.defaultMMKV().encode(MMKVHelper.TOKEN, jsonObject.getJSONObject("data").getJSONObject("user").getString("token"));
								MMKV.defaultMMKV().encode(MMKVHelper.ADDRESS, jsonObject.getJSONObject("data").getJSONObject("user").getString("address"));
								Preferences.setToken(jsonObject.getJSONObject("data").getJSONObject("user").getString("token"));
								startActivity(new Intent(WXEntryActivity.this, AccountSettingActivity.class).putExtra("isFirst", true));
								finish();
							} else {
								User user = JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("user").toJSONString(), User.class);
								LoginHelper.savaUser(user);
								MMKV.defaultMMKV().encode(MMKVHelper.KEY_UserSchoolName, jsonObject.getJSONObject("data").getJSONObject("user").getString("companyName"));
								MMKV.defaultMMKV().encode(MMKVHelper.POSTTYPENAME, jsonObject.getJSONObject("data").getJSONObject("user").getString("postTypeName"));
								Timber.e("user---->%s", JSON.toJSONString(user));
								loginIM();
//								startActivity(new Intent(WXEntryActivity.this, IndexActivity.class));
							}

						} else {
							MToast.makeTextShort(WXEntryActivity.this, jsonObject.getString("errmsg"));
						}
					}
				});
	}

	private void getCheckCode(String phone) {
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("mobile", phone);
		HttpsRequest.provideClientApi().getVerifyCode(jsonObject)
				.compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<com.alibaba.fastjson.JSONObject>() {
					@Override
					public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") != 0) {
							MToast.makeTextShort(jsonObject.getString("errmsg"));
						} else {
							//倒计时
							MyCountDownTimer timer = new MyCountDownTimer(60000, 1000);
							timer.start();
						}
					}
				});
	}

	//倒计时效果
	private class MyCountDownTimer extends CountDownTimer {

		//millisInFuture：总时间  countDownInterval：每隔多少时间刷新一次
		public MyCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		//计时过程
		@Override
		public void onTick(long lm) {
			//不允许再次点击
			btGetcodeText.setClickable(false);
			btGetcodeText.setText(lm / 1000 + "s");
		}

		//计时结束
		@Override
		public void onFinish() {
			btGetcodeText.setClickable(true);
			btGetcodeText.setText("重新获取");
		}
	}

}

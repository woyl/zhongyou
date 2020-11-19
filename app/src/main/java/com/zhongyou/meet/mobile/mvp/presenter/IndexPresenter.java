package com.zhongyou.meet.mobile.mvp.presenter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxBus;
import com.jess.arms.utils.RxLifecycleUtils;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.ycbjie.ycupdatelib.UpdateFragment;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.network.LoadingDialog;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.entities.FinishWX;
import com.zhongyou.meet.mobile.entiy.UserInfo;
import com.zhongyou.meet.mobile.entiy.VersionCheck;
import com.zhongyou.meet.mobile.mvp.contract.IndexContract;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.ApkUtil;
import com.zhongyou.meet.mobile.utils.DeviceIdUtils;
import com.zhongyou.meet.mobile.utils.DeviceUtil;
import com.zhongyou.meet.mobile.utils.Installation;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import java.util.UUID;

import javax.inject.Inject;

import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import timber.log.Timber;


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
@ActivityScope
public class IndexPresenter extends BasePresenter<IndexContract.Model, IndexContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;

	@Inject
	public IndexPresenter(IndexContract.Model model, IndexContract.View rootView) {
		super(model, rootView);
	}

	//检测版本
	@Deprecated
	public void versionCheck(String applicationID, String versionCode) {
		if (mModel != null) {
			mModel.versionCheck(applicationID, versionCode)
					.compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {

						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {


							if (jsonObject.getInteger("errcode") == 0) {

								VersionCheck versionCheck = JSON.parseObject(jsonObject.toJSONString(), VersionCheck.class);
								//不需要检测更新
								if (versionCheck.getData() == null
										|| versionCheck.getData().toString().isEmpty()
										|| versionCheck.getData().getImportance() == 0
										|| versionCheck.getData().getImportance() == 1) {
									return;
								}
								//1:最新版，不用更新 2：小改动，可以不更新 3：建议更新 4 强制更新
								int updateFlag = versionCheck.getData().getImportance();
								boolean needForceUpdate = false;
								if (updateFlag == 4) {
									needForceUpdate = true;
								}
								try {
									if (ApkUtil.compareVersion(versionCheck.getData().getVersionDesc(), BuildConfig.VERSION_NAME) > 0) {
										UpdateUtils.APP_UPDATE_DOWN_APK_PATH = UpdateUtils.getFilePath(mApplication) + "download";
										String desc = versionCheck.getData().getName() + "\n" + "最新版本:" + versionCheck.getData().getVersionDesc();
										UpdateFragment updateFragment = UpdateFragment.showFragment(mRootView.getActivity(),
												needForceUpdate, versionCheck.getData().getUrl(),
												versionCheck.getData().getName() + "-" + versionCheck.getData().getVersionDesc(),
												desc, BuildConfig.APPLICATION_ID);
										updateFragment.setOnClickListener(new UpdateFragment.OnClickListener() {
											@Override
											public void onClick(int i) {
												//点击下载 1   暂停下载 1  或者下次再说 0
												if (i == 0) {
													//initEntry(1);
												} else if (i == 1) {
													int downloadStatus = updateFragment.getDownloadStatus();
													if (downloadStatus == UpdateUtils.DownloadStatus.UPLOADING) {
														//正在下载中  不能跳转到首页
														Toasty.info(mApplication, "当前下载已暂停", Toast.LENGTH_SHORT, true).show();
													}
												}
											}
										});
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								Timber.d("版本检测失败%s", jsonObject.getString("errmsg"));
							}
						}

					});
		}
	}

	//设备注册
	@SuppressLint({"MissingPermission", "HardwareIds"})
	public void registerDevices() {
		if (mModel == null) {
			return;
		}
		JSONObject jsonObject = new JSONObject();
		String uuid = Installation.id(mApplication);
		DisplayMetrics metric = new DisplayMetrics();
		mRootView.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		float density = metric.density;
		int densityDpi = metric.densityDpi;
		jsonObject.put("uuid", uuid);
		jsonObject.put("androidId", TextUtils.isEmpty(Settings.System.getString(mRootView.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)) ? "" : Settings.System.getString(mRootView.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
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
		jsonObject.put("androidDeviceId", DeviceIdUtils.getDeviceId());
		jsonObject.put("buildSerial", Build.SERIAL);
		jsonObject.put("source", 2);
		jsonObject.put("internalSpace", DeviceUtil.getDeviceTotalMemory(mApplication));
		jsonObject.put("internalFreeSpace", DeviceUtil.getDeviceAvailMemory(mApplication));
		jsonObject.put("sdSpace", DeviceUtil.getDeviceTotalInternalStorage());
		jsonObject.put("sdFreeSpace", DeviceUtil.getDeviceAvailInternalStorage());
		mModel.registerDevice(jsonObject).compose(RxSchedulersHelper.io_main())
				.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void onSubscribe(Disposable d) {
						addDispose(d);
					}

					@Override
					public void _onNext(JSONObject jsonObject) {
						Timber.d("注册设备：%s", jsonObject.toJSONString());
					}
				});
	}

	//获取用户信息
	public void getUserInfo() {
		if (mModel == null) {
			return;
		}
		mModel.getUserInfo().compose(RxLifecycleUtils.bindToLifecycle(mRootView))
				.compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<JSONObject>() {

					@Override
					public void onSubscribe(Disposable d) {
						addDispose(d);
					}

					@Override
					public void _onNext(JSONObject jsonObject) {
						UserInfo userInfo = JSON.parseObject(jsonObject.toJSONString(), UserInfo.class);
						if (userInfo == null || userInfo.getData() == null) {
							return;
						}
						if (userInfo.getErrcode() == 0) {
							UserInfo.DataBean.WechatBean wechat = userInfo.getData().getWechat();
							if (wechat != null) {
								MMKVHelper.getInstance().saveWeiXinHead(wechat.getHeadimgurl());
							}
							UserInfo.DataBean.UserBean user = userInfo.getData().getUser();
							if (user != null) {
								MMKVHelper.getInstance().saveID(user.getId());
								MMKVHelper.getInstance().saveAddress(user.getAddress());
								MMKVHelper.getInstance().saveAreainfo(user.getAreaInfo());
								MMKVHelper.getInstance().savePhoto(user.getPhoto());
								MMKVHelper.getInstance().saveTOKEN(user.getToken());
								MMKVHelper.getInstance().savePostTypeName(user.getPostTypeName());
								MMKVHelper.getInstance().saveMobie(user.getMobile());
								MMKVHelper.getInstance().saveUserNickName(user.getName());
								initUserProvider();
							}
						} else {
							Timber.d("获取用户信息失败%s", userInfo.getErrmsg());
						}
					}
				});
	}

	//初始化IM用户信息
	private void initUserProvider() {
		if (mModel != null) {
			mModel.initImUserProvider();
		}
	}

	//会议状态的更改 不知道接口是什么作用
	public void meetingJoinStats() {
		if (TextUtils.isEmpty(Preferences.getMeetingTraceId())) {
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("meetingJoinTraceId", Preferences.getMeetingTraceId());
		jsonObject.put("meetingId", Preferences.getMeetingId());
		jsonObject.put("status", 2);
		jsonObject.put("type", 1);
		jsonObject.put("leaveType", 1);
		if (mModel != null) {
			mModel.meetingJoinStats(jsonObject)
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							Preferences.setMeetingId(null);
							Preferences.setMeetingTraceId(null);
						}
					});
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		LoadingDialog.closeDialog();
		this.mErrorHandler = null;
		this.mAppManager = null;
		this.mImageLoader = null;
		this.mApplication = null;
	}

	public void LoginIM() {
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
									Preferences.clear();
								}
							} else {
								Preferences.clear();

							}
						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							Preferences.clear();
							mRootView.LoginImSuccess(false);
						}
					});
		}
	}

	private void IMConnect(String imtoken) {
		RongIM.connect(imtoken, new RongIMClient.ConnectCallbackEx() {


			@Override
			public void OnDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

			}

			@Override
			public void onTokenIncorrect() {
				Logger.e("onTokenIncorrect");
				Preferences.setImToken("");
				mRootView.LoginImSuccess(false);

			}

			@Override
			public void onSuccess(String s) {

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
					public io.rong.imlib.model.UserInfo getUserInfo(String userId) {

						io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto()));
						RongIM.getInstance().refreshUserInfoCache(userInfo);
						RongIM.getInstance().setCurrentUserInfo(userInfo);
						return userInfo;//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
					}
				}, false);
				mRootView.LoginImSuccess(true);
			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				Logger.e(errorCode.toString());
				Preferences.setImToken("");
				mRootView.LoginImSuccess(false);

			}
		});
	}
}

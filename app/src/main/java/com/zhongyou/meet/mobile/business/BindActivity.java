package com.zhongyou.meet.mobile.business;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jess.arms.utils.RxBus;
import com.kongzue.dialog.interfaces.OnBackClickListener;
import com.kongzue.dialog.v3.FullScreenDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.maning.mndialoglibrary.MToast;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.WebViewActivity;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.databinding.ActivityBindingBinding;
import com.zhongyou.meet.mobile.entities.FinishWX;
import com.zhongyou.meet.mobile.entities.LoginWithPhoneNumber;
import com.zhongyou.meet.mobile.entities.User;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.entities.base.BaseErrorBean;
import com.zhongyou.meet.mobile.mvp.ui.activity.AccountSettingActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.IndexActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.Login.LoginHelper;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.view.MyWebView;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import timber.log.Timber;

/**
 * @author luopan@centerm.com
 * @date 2019-10-08 17:08.
 * <p>
 * 微信登录后 绑定手机号界面
 */
public class BindActivity extends BaseDataBindingActivity<ActivityBindingBinding> {
	private CountDownTimer countDownTimer;
	private static boolean userIsAuthByHEZY;


	public static boolean isFirst;
	private BasePopupView mLoadingDialog;
	private FullScreenDialog mFullScreenDialog;

	@Override
	protected int initContentView() {
		return R.layout.activity_binding;
	}

	@Override
	public String getStatisticsTag() {
		return "绑定微信";
	}

	@Override
	protected void initView() {
		mBinding.btGetcodeText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBinding.txtPhone.getText().toString().trim().length() != 11) {
					showToast("手机号位数不正确");
				} else {
					getCheckCode(mBinding.txtPhone.getText().toString().trim());
				}
			}
		});

		countDownTimer = new CountDownTimer(60 * 1000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				mBinding.btGetcodeText.setText(millisUntilFinished / 1000 + "S");
			}

			@Override
			public void onFinish() {
				mBinding.btGetcodeText.setEnabled(true);
				mBinding.btGetcodeText.setText("获取验证码");
			}
		};

		mBinding.btLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (!mBinding.check.isChecked()) {
//					ToastUtils.showToast("您还未同意\"中幼在线用户服务协议\" 请点击同意");
//					return;
//				}
				if (mBinding.txtPhone.getText().toString().length() != 11) {
					ToastUtils.showToast("手机号位数不正确 请重新输入");
					return;
				} else if (mBinding.txtCode.toString().length() < 4) {
					ToastUtils.showToast("验证码位数不正确 请重新输入");
					return;
				}

				bindUserWithWeChat();
			}
		});

		mBinding.argment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFullScreenDialog = FullScreenDialog.show(BindActivity.this, R.layout.activity_web_view, new FullScreenDialog.OnBindView() {
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
			}
		});
		mBinding.argmentPrivate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFullScreenDialog = FullScreenDialog.show(BindActivity.this, R.layout.activity_web_view, new FullScreenDialog.OnBindView() {
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
			}
		});
		mBinding.back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 绑定手机号
	 */
	private void bindUserWithWeChat() {

		JSONObject params = new JSONObject();
		String userPhone = mBinding.txtPhone.getText().toString().trim();
		String userCheckCode = mBinding.txtCode.getText().toString().trim();
//        if (!TextUtils.isEmpty(userName)) {
//            params.put("name", userName);
//        }
		if (userPhone.length() == 11 && userCheckCode.length() >= 4) {
			params.put("mobile", userPhone);
			params.put("verifyCode", userCheckCode);
			params.put("userToken", Preferences.getToken());
		}

		HttpsRequest.provideClientApi().loginWithPhone(params)
				.compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<JSONObject>() {
							@Override
							public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							if (TextUtils.isEmpty(jsonObject.getJSONObject("data").getJSONObject("user").getString("name"))
									|| TextUtils.isEmpty(jsonObject.getJSONObject("data").getJSONObject("user").getString("address"))
									|| TextUtils.isEmpty(jsonObject.getJSONObject("data").getJSONObject("user").getString("postTypeName"))) {
								Preferences.setUserMobile(userPhone);
								MMKV.defaultMMKV().encode(MMKVHelper.MOBILE, userPhone);
								MMKV.defaultMMKV().encode(MMKVHelper.ID, jsonObject.getJSONObject("data").getJSONObject("user").getString("id"));
								MMKV.defaultMMKV().encode(MMKVHelper.TOKEN, jsonObject.getJSONObject("data").getJSONObject("user").getString("token"));
								MMKV.defaultMMKV().encode(MMKVHelper.ADDRESS, jsonObject.getJSONObject("data").getJSONObject("user").getString("address"));
								Preferences.setToken(jsonObject.getJSONObject("data").getJSONObject("user").getString("token"));
								startActivity(new Intent(BindActivity.this, AccountSettingActivity.class).putExtra("isFirst", true));
								finish();
							} else {
								User user = JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("user").toJSONString(), User.class);
								LoginHelper.savaUser(user);
								MMKV.defaultMMKV().encode(MMKVHelper.KEY_UserSchoolName, jsonObject.getJSONObject("data").getJSONObject("user").getString("companyName"));
								MMKV.defaultMMKV().encode(MMKVHelper.POSTTYPENAME, jsonObject.getJSONObject("data").getJSONObject("user").getString("postTypeName"));
								Timber.e("user---->%s", JSON.toJSONString(user));
								loginIM();
							}

						} else {
							MToast.makeTextShort(BindActivity.this, jsonObject.getString("errmsg"));
						}
					}
				});
		/*ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
			@Override
			public void onSuccess(BaseErrorBean entity) {

				if (isFirst) {
					// TODO: 2019-10-08 成功之后根据用户手机号是否存在来判断是否登录成功
					Preferences.setUserMobile(userPhone);
//					UserInfoActivity.actionStart(BindActivity.this, true, true);
					startActivity(new Intent(BindActivity.this, AccountSettingActivity.class).putExtra("isFirst", true));
				}
				finish();
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				ToastUtils.showToast(exception.getMessage());
			}


		});*/
	}

	private void getCheckCode(String phone) {
		mBinding.btGetcodeText.setEnabled(false);
		countDownTimer.start();
		ApiClient.getInstance().requestVerifyCode(this, phone, new OkHttpCallback<BaseErrorBean>() {
			@Override
			public void onSuccess(BaseErrorBean entity) {

			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				countDownTimer.cancel();
				mBinding.btGetcodeText.setEnabled(true);
				mBinding.btGetcodeText.setText("获取验证码");
			}

		});
	}


	public static void actionStart(Context context, boolean isFirst, boolean userIsAuthByHEZY) {
		Intent intent = new Intent(context, BindActivity.class);
		intent.putExtra("isFirst", isFirst);
		intent.putExtra("userIsAuthByHEZY", userIsAuthByHEZY);
		context.startActivity(intent);
	}

	@Override
	protected void initExtraIntent() {
		isFirst = getIntent().getBooleanExtra("isFirst", false);
		userIsAuthByHEZY = getIntent().getBooleanExtra("userIsAuthByHEZY", false);
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
			mApiService.getToken(object)
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
				startActivity(new Intent(BindActivity.this, IndexActivity.class));
				finish();
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
}

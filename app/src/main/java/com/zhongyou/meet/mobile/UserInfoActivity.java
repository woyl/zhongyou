package com.zhongyou.meet.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jess.arms.utils.RxBus;
import com.maning.mndialoglibrary.MProgressDialog;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zaaach.citypicker.CityPickerActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.business.BaseDataBindingActivity;
import com.zhongyou.meet.mobile.business.CustomActivity;
import com.zhongyou.meet.mobile.business.DistrictActivity;
import com.zhongyou.meet.mobile.business.GridActivity;
import com.zhongyou.meet.mobile.business.PostTypeActivity;
import com.zhongyou.meet.mobile.databinding.UserinfoActivityBinding;
import com.zhongyou.meet.mobile.entities.Custom;
import com.zhongyou.meet.mobile.entities.District;
import com.zhongyou.meet.mobile.entities.Grid;
import com.zhongyou.meet.mobile.entities.PostType;
import com.zhongyou.meet.mobile.entities.QiniuToken;
import com.zhongyou.meet.mobile.entities.StaticResource;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.entities.base.BaseErrorBean;
import com.zhongyou.meet.mobile.event.UserUpdateEvent;
import com.zhongyou.meet.mobile.mvp.ui.activity.IndexActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.Logger;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.MatisseGlideEngine;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import me.jessyan.autosize.utils.AutoSizeUtils;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;
import top.zibin.luban.Luban;


/**
 * 用户信息fragment
 * Created by wufan on 2017/7/24.
 * <p>
 * 绑定手机号后 设置用户资料界面
 */

public class UserInfoActivity extends BaseDataBindingActivity<UserinfoActivityBinding> {

	private Subscription subscription;

	private String imagePath;


	public static boolean isFirst;
	private static boolean userIsAuthByHEZY;

	public static final int CODE_USERINFO_DISTRICT = 0x100;
	public static final int CODE_USERINFO_POSTTYPE = 0x101;
	public static final int CODE_USERINFO_GRID = 0x102;
	public static final int CODE_USERINFO_CUSTOM = 0x103;

	public static final String KEY_USERINFO_POSTTYPE = "key_posttype";
	public static final String KEY_USERINFO_DISTRICT = "key_district";
	public static final String KEY_DISTRICT_ID = "key_district_Id";
	public static final String KEY_USERINFO_GRID = "key_grid";
	public static final String KEY_USERINFO_CUSTOM = "key_custom";
	private CompositeDisposable mDisposable;

//    public static void actionStart(Context context) {
//        Intent intent = new Intent(context, UserInfoActivity.class);
//        context.startActivity(intent);
//    }

	public static void actionStart(Context context, boolean isFirst, boolean userIsAuthByHEZY) {
		Intent intent = new Intent(context, UserInfoActivity.class);
		intent.putExtra("isFirst", isFirst);
		intent.putExtra("userIsAuthByHEZY", userIsAuthByHEZY);
		context.startActivity(intent);
	}

	@Override
	protected void initExtraIntent() {
		isFirst = getIntent().getBooleanExtra("isFirst", false);

		userIsAuthByHEZY = getIntent().getBooleanExtra("userIsAuthByHEZY", false);
	}

	@Override
	protected int initContentView() {
		return R.layout.userinfo_activity;
	}

	private void setUserUI() {
		hideEditTextFocus();
		//手机号
//		mBinding.tvUserInfoMobile.setText(Preferences.getUserMobile());
		((TextView) findViewById(R.id.title)).setText(Preferences.getUserMobile());
		if (!isFirst) {
			findViewById(R.id.back).setVisibility(View.VISIBLE);
			findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		} else {
			findViewById(R.id.back).setVisibility(View.GONE);
		}
		//姓名
		mBinding.edtUserInfoName.setText(Preferences.getUserName());
		//选择用户类型
		mBinding.edtUserInfoPostType.setText(Preferences.getUserPostType());
		//选择中心
		mBinding.edtUserInfoDistrict.setText(Preferences.getUserDistrict());

		//选择客户
		mBinding.edtUserInfoCustom.setText(Preferences.getUserCustom());
		//选择地址
		mBinding.edtUserInfoAddress.setText(Preferences.getUserAddress());
		mBinding.imgUserInfoHead.setVisibility(View.VISIBLE);

		if (!TextUtils.isEmpty(Preferences.getUserPhoto())) {
			Glide.with(BaseApplication.getInstance()).load(Preferences.getUserPhoto())
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.error(R.mipmap.baby_default_avatar)
					.placeholder(R.drawable.tx)
					.into(mBinding.imgUserInfoHead);

		}

		String postTypeId = Preferences.getUserPostTypeId();
		checkPostType(postTypeId);
	}

	private void hideEditTextFocus() {
		mBinding.edtUserInfoPostType.setInputType(InputType.TYPE_NULL);
		mBinding.edtUserInfoDistrict.setInputType(InputType.TYPE_NULL);
		mBinding.edtUserInfoGrid.setInputType(InputType.TYPE_NULL);
		mBinding.edtUserInfoCustom.setInputType(InputType.TYPE_NULL);
		mBinding.edtUserInfoAddress.setInputType(InputType.TYPE_NULL);

		mBinding.edtUserInfoGrid.setText(Preferences.getUserGrid());

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {
		mDisposable = new CompositeDisposable();
		subscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (o instanceof UserUpdateEvent) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setUserUI();
						}
					});

				}
			}
		});
		setUserUI();
		 new Thread(new Runnable() {
			 @Override
			 public void run() {
				 try {
					 Thread.sleep(500);
				 } catch (InterruptedException e) {
					 e.printStackTrace();
				 }
			 }
		 }).start();
	}

	@Override
	protected void initListener() {
		mBinding.btnUserInfoSave.setOnClickListener(this);
		mBinding.imgUserInfoHead.setOnClickListener(this);
		mBinding.edtUserInfoName.setOnFocusChangeListener(edtUserInfoNameFocusChangeListener);
		mBinding.edtUserInfoPostType.setOnTouchListener(onTouchListener);
		mBinding.edtUserInfoDistrict.setOnTouchListener(onTouchListener);
		mBinding.edtUserInfoGrid.setOnTouchListener(onTouchListener);
		mBinding.edtUserInfoCustom.setOnTouchListener(onTouchListener);
		mBinding.edtUserInfoAddress.setOnTouchListener(onTouchListener);
	}

	private View.OnFocusChangeListener edtUserInfoNameFocusChangeListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			String userName = ((EditText) v).getText().toString().trim();
			if (!hasFocus && !TextUtils.isEmpty(userName)) {
				saveUserName(userName);
			}
		}
	};

	/**
	 * 保存用户数据
	 *
	 * @param userName
	 */
	private void saveUserName(String userName) {
		Preferences.setUserName(userName);
		Map<String, String> params = new HashMap<>();
		params.put("name", userName);
		ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
			@Override
			public void onSuccess(BaseErrorBean entity) {
			}
		});
	}

	private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				switch (v.getId()) {
					case R.id.edtUserInfoPostType:
						Intent postTypeIntent = new Intent(mContext, PostTypeActivity.class);
						startActivityForResult(postTypeIntent, CODE_USERINFO_POSTTYPE);
						break;
					case R.id.edtUserInfoDistrict:
						Intent districtIntent = new Intent(mContext, DistrictActivity.class);
						startActivityForResult(districtIntent, CODE_USERINFO_DISTRICT);
						break;
					case R.id.edtUserInfoGrid:
						String g_districtID = Preferences.getUserDistrictId();
						if (TextUtils.isEmpty(g_districtID)) {
							ToastUtils.showToast("请先选择中心");
							return false;
						}
						Intent gridIntent = new Intent(mContext, GridActivity.class);
						gridIntent.putExtra(KEY_DISTRICT_ID, g_districtID);
						startActivityForResult(gridIntent, CODE_USERINFO_GRID);
						break;
					case R.id.edtUserInfoCustom:
						String c_districtID = Preferences.getUserDistrictId();
						if (TextUtils.isEmpty(c_districtID)) {
							ToastUtils.showToast("请先选择中心");
							return false;
						}
						Intent customIntent = new Intent(mContext, CustomActivity.class);
						customIntent.putExtra(KEY_DISTRICT_ID, c_districtID);
						startActivityForResult(customIntent, CODE_USERINFO_CUSTOM);
						break;
					case R.id.edtUserInfoAddress:
						Intent intent = new Intent(mContext, CityPickerActivity.class);
						startActivityForResult(intent, CityPickerActivity.REQUEST_CODE_PICK_CITY);
						break;
					default:
						Log.d(TAG, "onTouch: no view touched");
						break;
				}
			}
			return false;
		}
	};

	@Override
	public String getStatisticsTag() {
		return "编辑资料";
	}


	private static final int REQUEST_CODE_CHOOSE = 23;

	@Override
	protected void normalOnClick(View v) {

		switch (v.getId()) {
			case R.id.imgUserInfoHead:
				Matisse.from(this)
						.choose(MimeType.of(MimeType.PNG, MimeType.JPEG, MimeType.BMP, MimeType.WEBP, MimeType.WEBM))
//						.choose(MimeType.ofImage(), false)
						.countable(true)
						.theme(R.style.Matisse_Dracula)
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
			case R.id.btnUserInfoSave:
				save();
				break;
			default:
				Log.d(TAG, "normalOnClick:no view id clicked ");
				break;
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == CityPickerActivity.REQUEST_CODE_PICK_CITY) {
				String result = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
				mBinding.edtUserInfoAddress.setText(result);

				if ((!TextUtils.isEmpty(result)) && !Preferences.getUserAddress().equals(result)) {
					Preferences.setUserAddress(result);
					Map<String, String> params = new HashMap<>();
					params.put("address", result);
					ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
						@Override
						public void onSuccess(BaseErrorBean entity) {
							showToast("设置地址成功");
						}

					});
				}
			} else if (requestCode == CODE_USERINFO_DISTRICT) {
				//设置大区、中心
				District district = data.getParcelableExtra(KEY_USERINFO_DISTRICT);
				String districtId = district.getId();
				String districtName = district.getName();

				mBinding.edtUserInfoDistrict.setText(districtName);
				Preferences.setUserDistrict(districtName);
				Preferences.setUserDistrictId(districtId);

				Map<String, String> params = new HashMap<>();
				params.put("areaId", districtId);
				ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
					@Override
					public void onSuccess(BaseErrorBean entity) {
						showToast("设置大区成功");
					}
				});
			} else if (requestCode == CODE_USERINFO_POSTTYPE) {
				//用户类型
				PostType postType = data.getParcelableExtra(KEY_USERINFO_POSTTYPE);
				String postTypeId = postType.getId();
				String postTypeName = postType.getName();
				checkPostType(postTypeId);
				mBinding.edtUserInfoPostType.setText(postTypeName);
				Preferences.setUserPostType(postTypeName);
				Preferences.setUserPostTypeId(postTypeId);

				Map<String, String> params = new HashMap<>();
				params.put("postTypeId", postTypeId);
				ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
					@Override
					public void onSuccess(BaseErrorBean entity) {
						showToast("设置用户类型成功");
					}
				});

			} else if (requestCode == CODE_USERINFO_GRID) {
				//网格
				Grid grid = data.getParcelableExtra(KEY_USERINFO_GRID);
				mBinding.edtUserInfoGrid.setText(grid.getName());
				Preferences.setUserGrid(grid.getName());
				Preferences.setUserGridId(grid.getId());

				Map<String, String> params = new HashMap<>();
				params.put("gridId", grid.getId());
				ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
					@Override
					public void onSuccess(BaseErrorBean entity) {
						showToast("设置用户网格成功");
					}

				});

			} else if (requestCode == CODE_USERINFO_CUSTOM) {
				Custom custom = data.getParcelableExtra(KEY_USERINFO_CUSTOM);
				mBinding.edtUserInfoCustom.setText(custom.getName());
				Preferences.setUserCustom(custom.getName());
				Preferences.setUserCustomId(custom.getId());

				Map<String, String> params = new HashMap<>();
				params.put("customId", custom.getId());
				ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
					@Override
					public void onSuccess(BaseErrorBean entity) {
						showToast("设置客户成功");
					}

				});
			} else if (requestCode == REQUEST_CODE_CHOOSE) {
				List<String> list = Matisse.obtainPathResult(data);
				withRx(list);
			}
		}
	}

	private <T> void withRx(final List<T> photos) {
		mDisposable.add(Flowable.just(photos)
				.subscribeOn(Schedulers.io())
				.map(new Function<List<T>, List<File>>() {
					@Override
					public List<File> apply(@NonNull List<T> list) throws Exception {

						return Luban.with(UserInfoActivity.this)
								.setTargetDir(getPath())
								.load(list)
								.get();
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe(new Consumer<org.reactivestreams.Subscription>() {
					@Override
					public void accept(org.reactivestreams.Subscription subscription) throws Exception {
						MProgressDialog.showProgress(UserInfoActivity.this, "加载中……");
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
							Glide.with(UserInfoActivity.this)
									.asBitmap()
									.error(R.drawable.tx)
									.placeholder(R.drawable.tx)
									.load(file.getAbsolutePath())
									.into(mBinding.imgUserInfoHead);
							imagePath = file.getAbsolutePath();
							uploadImage();
							MProgressDialog.dismissProgress();
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

	/**
	 * 检测PostTypeID类型
	 *
	 * @param postTypeId
	 */
	private void checkPostType(String postTypeId) {
		if ("4".equals(postTypeId)) {
			showPostTypeIs4();
		} else if ("10".equals(postTypeId)) {
			showPostTypeIs10();
		} else {
			showAllInfoEditText();
		}
	}

	private void uploadImage() {
		ApiClient.getInstance().requestQiniuToken(this, new OkHttpCallback<BaseBean<QiniuToken>>() {

			@Override
			public void onSuccess(BaseBean<QiniuToken> result) {
				String token = result.getData().getToken();
				if (TextUtils.isEmpty(token)) {
					showToast("七牛token获取错误");
					return;
				}
				Configuration config = new Configuration.Builder().connectTimeout(5).responseTimeout(5).build();
				UploadManager uploadManager = new UploadManager(config);
				System.out.println(imagePath);
				uploadManager.put(
						new File(imagePath),
						"osg/user/expostor/photo/" + UUID.randomUUID().toString().replace("-", "") + ".jpg",
						token,
						new UpCompletionHandler() {
							@Override
							public void complete(String key, ResponseInfo info, JSONObject response) {
								if (info.isNetworkBroken() || info.isServerError()) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											showToast("上传失败");
										}
									});

									return;
								}
								if (info.isOK()) {
									relate(key);
								} else {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											showToast("上传失败");
										}
									});

								}
							}
						},
						new UploadOptions(null, null, true, new UpProgressHandler() {
							@Override
							public void progress(final String key, final double percent) {
							}

						}, null));
			}
		});
	}

	private void relate(final String key) {
		Logger.i(TAG, "key " + key);
		if (!TextUtils.isEmpty(key)) {
			if (TextUtils.isEmpty(Preferences.getImgUrl())) {
				ApiClient.getInstance().getImageUrlPath(TAG, imgePathCallBack(key));
				ApiClient.getInstance().urlConfig(staticResCallback(key));
			} else {
				Map<String, String> params = new HashMap<>();
				//服务器存储全路径
				params.put("photo", Preferences.getImgUrl() + key);
				ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
					@Override
					public void onSuccess(BaseErrorBean entity) {
						Preferences.setUserPhoto(Preferences.getImgUrl() + key);
						ToastUtils.showToast("保存照片成功");
					}
				});
			}
		}
	}

	private OkHttpCallback imgePathCallBack(final String key) {
		return new OkHttpCallback<BaseBean<Object>>() {
			@Override
			public void onSuccess(BaseBean<Object> entity) {
				try {
					com.alibaba.fastjson.JSONObject obj = JSON.parseObject(JSON.toJSONString(entity));
					if (obj.getInteger("errcode") == 0) {
						String imageUrl = obj.getJSONObject("data").getString("host");
						if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
							Preferences.setImgUrl(imageUrl);
							Map<String, String> params = new HashMap<>();
							params.put("photo", Preferences.getImgUrl() + key); //服务器存储全路径
							ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpCallback<BaseErrorBean>() {
								@Override
								public void onSuccess(BaseErrorBean entity) {
									Logger.i("photo", entity.toString());
									Preferences.setUserPhoto(Preferences.getImgUrl() + key);
									ToastUtils.showToast("保存照片成功");
								}
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				Toast.makeText(mContext, "获取图片路径前缀失败", Toast.LENGTH_SHORT).show();
			}
		};
	}

	private OkHttpCallback staticResCallback(final String key) {
		return new OkHttpCallback<BaseBean<StaticResource>>() {
			@Override
			public void onSuccess(BaseBean<StaticResource> entity) {
				Preferences.setImgUrl(entity.getData().getStaticRes().getImgUrl());
				Preferences.setVideoUrl(entity.getData().getStaticRes().getVideoUrl());
				Preferences.setDownloadUrl(entity.getData().getStaticRes().getDownloadUrl());
				Preferences.setCooperationUrl(entity.getData().getStaticRes().getDownloadUrl());

			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				Toast.makeText(mContext, "获取前缀失败", Toast.LENGTH_SHORT).show();
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void requestData() {
	}

	private void save() {

		if (TextUtils.isEmpty(Preferences.getUserPhoto())) {
			ToastUtils.showToast("请设置一张图片");
			return;
		}

		String userName = mBinding.edtUserInfoName.getText().toString().trim();
		if (TextUtils.isEmpty(userName)) {
			ToastUtils.showToast("请输入姓名");
			return;
		}
		saveUserName(userName);

		if (TextUtils.isEmpty(mBinding.edtUserInfoPostType.getText().toString().trim())) {
			ToastUtils.showToast("请选择用户类型");
			return;
		}
		if (TextUtils.isEmpty(mBinding.edtUserInfoDistrict.getText().toString().trim())) {
			ToastUtils.showToast("请选择中心");
			return;
		}
		if (TextUtils.isEmpty(mBinding.edtUserInfoAddress.getText().toString().trim())) {
			ToastUtils.showToast("请选择地址");
			return;
		}

		if (isFirst) {
			if (TextUtils.isEmpty(Preferences.getImToken())) {
				com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
				object.put("nickname", Preferences.getUserName());
				object.put("userId", Preferences.getUserId());
				if (TextUtils.isEmpty(Preferences.getUserPhoto())) {
					object.put("avatar", Preferences.getWeiXinHead());
				} else {
					object.put("avatar", Preferences.getUserPhoto());
				}

				MMKVHelper.getInstance().saveID(Preferences.getUserId());
				MMKVHelper.getInstance().savePhoto(Preferences.getUserPhoto());
				MMKVHelper.getInstance().saveUserNickName(Preferences.getUserName());

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
										Preferences.setImToken(Constant.IMTOKEN);
										IMConnect(Constant.IMTOKEN);
									} else {
										showToastyInfo("当前登陆信息失效 请重新登陆");
									}
								}
							}

							@Override
							public void onError(Throwable e) {
								super.onError(e);
								Preferences.clear();
							}
						});
			}

		}
		finish();
		RxBus.sendMessage(new UserUpdateEvent());
	}


	public void IMConnect(String token) {
		RongIM.connect(token, new RongIMClient.ConnectCallbackEx() {


			@Override
			public void OnDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {
				com.orhanobut.logger.Logger.e(JSON.toJSONString(databaseOpenStatus));
			}

			@Override
			public void onTokenIncorrect() {
				com.orhanobut.logger.Logger.e("onTokenIncorrect");
				showToastyWarn("融云token错误");
			}

			@Override
			public void onSuccess(String s) {
				com.orhanobut.logger.Logger.e("容云IM登陆成功:" + s);

				/*RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

					@Override
					public UserInfo getUserInfo(String userId) {
						UserInfo userInfo = new UserInfo(Preferences.getUserId(), Preferences.getUserName(), Uri.parse(Preferences.getUserPhoto()));
						com.orhanobut.logger.Logger.e(JSON.toJSONString(userInfo));
						RongIM.getInstance().refreshUserInfoCache(userInfo);
						RongIM.getInstance().setCurrentUserInfo(userInfo);
						return userInfo;//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
					}
				}, false);*/
				startActivity(new Intent(UserInfoActivity.this, IndexActivity.class));
			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				com.orhanobut.logger.Logger.e(errorCode.toString());
			}
		});
	}

	/**
	 * 用户类型为4-网格主，不提交客户信息，提交网格信息
	 */
	private void showPostTypeIs4() {
		mBinding.UserInfoCustomLayout.setVisibility(View.GONE);
		mBinding.userInfoGridLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 大区中心为总部
	 * 且用户类型为10-推广经理
	 * 不提交客户信息，网格信息
	 */
	private void showPostTypeIs10() {
		mBinding.UserInfoCustomLayout.setVisibility(View.GONE);
		mBinding.userInfoGridLayout.setVisibility(View.GONE);
//		mBinding.imgEdtUserInfoCustom.setVisibility(View.GONE);
//		mBinding.edtUserInfoGrid.setVisibility(View.GONE);
//		mBinding.imgEdtUserInfoGrid.setVisibility(View.GONE);
	}

	/**
	 * 除用户类型为4、10以外，显示客户信息，不显示网格信息
	 */
	private void showAllInfoEditText() {
		mBinding.UserInfoCustomLayout.setVisibility(View.VISIBLE);
		mBinding.userInfoGridLayout.setVisibility(View.GONE);
//		mBinding.imgEdtUserInfoCustom.setVisibility(View.VISIBLE);
//		mBinding.edtUserInfoGrid.setVisibility(View.GONE);
//		mBinding.imgEdtUserInfoGrid.setVisibility(View.GONE);
	}

	@Override
	protected void onDestroy() {
		subscription.unsubscribe();
		super.onDestroy();
	}
}

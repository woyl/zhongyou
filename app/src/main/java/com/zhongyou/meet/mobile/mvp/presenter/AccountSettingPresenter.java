package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allen.library.SuperTextView;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.Interface.OnCustomCityPickerItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.CustomCityData;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CustomConfig;
import com.lljjcoder.style.citycustome.CustomCityPicker;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MToast;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.UserInfoActivity;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.ameeting.network.L;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.entities.PostType;
import com.zhongyou.meet.mobile.entities.User;
import com.zhongyou.meet.mobile.entiy.UserProfile;
import com.zhongyou.meet.mobile.mvp.contract.AccountSettingContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.AccountSettingActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.IndexActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.DisplayUtil;
import com.zhongyou.meet.mobile.utils.Login.LoginHelper;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.ToastUtils;

import java.io.File;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import me.jessyan.autosize.utils.AutoSizeUtils;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import timber.log.Timber;


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
@ActivityScope
public class AccountSettingPresenter extends BasePresenter<AccountSettingContract.Model, AccountSettingContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;
	BaseRecyclerViewAdapter<PostType> adapter;

	private String userPhoto;
	private String userNickName;
	private String userType;
	private String userTypeId;
	private String userAddress;
	JSONObject resultJson = new JSONObject();
	private AlertDialog mAlertDialog;
	List<UserProfile> customCityDataList = new ArrayList<>();
	private Dialog dialog;
	private BaseRecyclerViewAdapter<UserProfile> mChildrenAdapter;


	@Inject
	public AccountSettingPresenter(AccountSettingContract.Model model, AccountSettingContract.View rootView) {
		super(model, rootView);
	}


	public void requestQiNiuToken(String path) {

		if (mModel != null && mRootView != null) {
			mModel.requestQiNiuToken().compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void onSubscribe(Disposable d) {
//							super.onSubscribe(d);
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								String token = jsonObject.getJSONObject("data").getString("token");
								upLoadImage(path, token);
							}
						}

					});
		}
	}


	private void upLoadImage(String path, String token) {
		Configuration config = new Configuration.Builder().connectTimeout(5).responseTimeout(5).build();
		UploadManager uploadManager = new UploadManager(config);
		if (mRootView == null) {
			return;
		}

		MProgressDialog.showProgress(mRootView.getOnAttachActivity(), "加载中...");
		uploadManager.put(
				new File(path),
				"osg/user/expostor/photo/" + UUID.randomUUID().toString().replace("-", "") + ".jpg",
				token,
				(key, info, response) -> {
					if (info.isNetworkBroken() || info.isServerError()) {
						mRootView.getOnAttachActivity().runOnUiThread(() -> {
							MProgressDialog.showProgress(mRootView.getOnAttachActivity(), "加载中...");
							MToast.makeTextShort(mRootView.getOnAttachActivity(), "上传失败");
						});

						return;
					}
					if (info.isOK()) {

						resultJson.put("photo", Constant.getImageHost() + key);
						MMKVHelper.getInstance().savePhotoOld(Constant.getImageHost() + key);
//						setUserInformation(jsonObject);

//						MProgressDialog.dismissProgress();
					} else {
						mRootView.getOnAttachActivity().runOnUiThread(() -> {
							MProgressDialog.showProgress(mRootView.getOnAttachActivity(), "加载中...");
							MToast.makeTextShort(mRootView.getOnAttachActivity(), "上传失败");
						});

					}
				},
				new UploadOptions(null, null, true, new UpProgressHandler() {
					@Override
					public void progress(final String key, final double percent) {

					}

				}, null));

	}


	public void showSaveDialog() {
		if (resultJson.keySet().size() > 0) {
			mAlertDialog = new AlertDialog(mRootView.getOnAttachActivity())
					.builder()
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setTitle("是否保存")
					.setMsg("是否保存个人信息")
					.setNegativeButton("取消", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mRootView.killMyself();
						}
					})
					.setPositiveButton("保存", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							setUserInformation();
						}
					});
			mAlertDialog.show();
		} else {
			mRootView.killMyself();
		}
	}

	public void showUserSchoolNameDialog() {
		Dialog dialog = new Dialog(mRootView.getOnAttachActivity(), R.style.MyDialog);
		View v = View.inflate(mRootView.getOnAttachActivity(), R.layout.dialog_input_name, null);
		((TextView) v.findViewById(R.id.title)).setText("幼儿园名称");
		TextView upDateNickName = v.findViewById(R.id.update);
		upDateNickName.setText("确定");
		View close = v.findViewById(R.id.close);
		EditText inputNickNameEdit = v.findViewById(R.id.userNickNameInput);
		inputNickNameEdit.setHint("请输入幼儿园名称");
		inputNickNameEdit.setText(MMKV.defaultMMKV().decodeString(MMKVHelper.KEY_UserSchoolName, ""));
		inputNickNameEdit.setSelection(inputNickNameEdit.getText().toString().trim().length());
		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		upDateNickName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.dismiss();
				if (inputNickNameEdit.getText().toString().trim().length() <= 0) {
					MToast.makeTextShort("幼儿园名称不能为空");
					return;
				}

				resultJson.put("companyName", inputNickNameEdit.getText().toString().trim());
				if (mRootView != null) {
					mRootView.changeUserSchoolName(inputNickNameEdit.getText().toString().trim());
				}
//				setUserInformation(json);
			}
		});
		dialog.setContentView(v);
		dialog.show();
	}

	public void setUserInformation() {
		if (mModel != null || mRootView != null) {

			if (mRootView.isFirst()) {
				if (!resultJson.containsKey("photo")) {
					if (!TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.WEIXINHEAD))) {
						resultJson.put("photo", MMKV.defaultMMKV().decodeString(MMKVHelper.WEIXINHEAD));
					}
					if (!TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO))) {
						resultJson.put("photo", MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO));
					}


				} else if (resultJson.containsKey("photo") && resultJson.get("photo") != null) {
					if (TextUtils.isEmpty(resultJson.getString("photo"))) {
						mRootView.showMessage("请选择头像");
						return;
					}
				}
			}

			if (TextUtils.isEmpty(resultJson.getString("photo")) && mRootView.isFirst()) {
				mRootView.showMessage("请选择头像");
				return;
			}

			if (resultJson.size() <= 0) {
				MToast.makeTextShort(mRootView.getOnAttachActivity(), "当前数据无变化");
				return;
			}
			if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.ID, ""))) {
				return;
			}
			//https://hezy-static.oss-cn-shanghai.aliyuncs.com/osg/user/avatar.png
			String head = MMKV.defaultMMKV().getString(MMKVHelper.PHOTO, "");
			String defaultHead = "https://hezy-static.oss-cn-shanghai.aliyuncs.com/osg/user/avatar.png";
//			if (!TextUtils.isEmpty(head) && head != null && !head.contains(defaultHead)) {
//				resultJson.put("photo", head);
//			}
			
			String phoneOld = MMKV.defaultMMKV().getString(MMKVHelper.PHOTO_OLD, "");
			if (!TextUtils.isEmpty(phoneOld)) {
				resultJson.put("photo",phoneOld);
			} else {
				resultJson.put("photo", head);
			}
			//第一次进入页面 并且没有改变photo

			mModel.setUserInformation(MMKV.defaultMMKV().decodeString(MMKVHelper.ID, ""), resultJson)
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {

						@Override
						public void onSubscribe(Disposable d) {
							super.onSubscribe(d);
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								resultJson.clear();
								User userBean = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
								MMKVHelper.getInstance().saveID(userBean.getId());
								MMKVHelper.getInstance().saveAddress(userBean.getAddress());
								MMKVHelper.getInstance().saveAreainfo(userBean.getAreaInfo());
								//MMKV.defaultMMKV().getString(MMKVHelper.PHOTO, "")
								MMKVHelper.getInstance().savePhoto(userBean.getPhoto());
								MMKVHelper.getInstance().saveTOKEN(userBean.getToken());

								Preferences.setUserPostType(userBean.getPostTypeId());

								if (!TextUtils.isEmpty(userBean.getPostTypeName())) {
									MMKVHelper.getInstance().savePostTypeName(userBean.getPostTypeName());
								} else if (!TextUtils.isEmpty(userType)) {
									MMKVHelper.getInstance().savePostTypeName(userType);
								}
								MMKVHelper.getInstance().saveMobie(userBean.getMobile());
								MMKVHelper.getInstance().saveUserNickName(userBean.getName());
								MMKV.defaultMMKV().encode(MMKVHelper.CUSTOMNAME, userBean.getCustomName());
								MMKV.defaultMMKV().encode(MMKVHelper.KEY_UserSchoolName, userBean.getCompanyName());
								LoginHelper.savaUser(userBean);
								if (mRootView != null) {
									if (mRootView.isFirst()) {
										com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
										object.put("nickname", MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME));
										object.put("userId", MMKV.defaultMMKV().decodeString(MMKVHelper.ID));
										if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO))) {
											object.put("avatar", MMKV.defaultMMKV().decodeString(MMKVHelper.WEIXINHEAD));
										} else {
											object.put("avatar", MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO));
										}
										getToken(object);


									} else {
										mRootView.UpDateUI(userBean);
										ToastUtils.showToast(mRootView.getOnAttachActivity(), "保存成功");
										mRootView.killMyself();
									}

								}

							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}
					});

		}

	}


	public void showInputNickNameDialog() {
		if (mRootView.getOnAttachActivity() == null) {
			return;
		}
		Dialog dialog = new Dialog(mRootView.getOnAttachActivity(), R.style.MyDialog);
		View v = View.inflate(mRootView.getOnAttachActivity(), R.layout.dialog_input_name, null);
		View upDateNickName = v.findViewById(R.id.update);
		View close = v.findViewById(R.id.close);
		EditText inputNickNameEdit = v.findViewById(R.id.userNickNameInput);
		inputNickNameEdit.setText(MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME, ""));
		inputNickNameEdit.setSelection(inputNickNameEdit.getText().toString().trim().length());
		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		upDateNickName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.dismiss();
				if (inputNickNameEdit.getText().toString().trim().length() <= 0) {
					MToast.makeTextShort("昵称不能为空");
					return;
				}

				resultJson.put("name", inputNickNameEdit.getText().toString().trim());
				if (mRootView != null) {
					mRootView.changeUserNickName(inputNickNameEdit.getText().toString().trim());
				}
//				setUserInformation(json);
			}
		});
		dialog.setContentView(v);
		dialog.show();
	}

	private UserProfile mParentUserProfile;
	private UserProfile mChildrenUserProfile;

	private void getUserParentProfile(RecyclerView recyclerView) {
		if (mModel != null) {
			mModel.getUserProfile("0")
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								List<UserProfile> data = jsonObject.getJSONArray("data").toJavaList(UserProfile.class);
								recyclerView.setAdapter(new BaseRecyclerViewAdapter<UserProfile>(mRootView.getOnAttachActivity(), data, R.layout.item_user_profile) {

									@Override
									public void convert(BaseRecyclerHolder holder, UserProfile item, int position, boolean isScrolling) {

										holder.setText(R.id.name, item.getName());
										holder.getView(R.id.name).setOnClickListener(new View.OnClickListener() {
											@Override
											public void onClick(View v) {

												if (item.isSelect()) {
													return;
												}
												for (UserProfile datum : data) {
													datum.setSelect(false);
												}

												mChildrenUserProfile = null;
												resultJson.remove("postTypeId");
												mParentUserProfile = item;
												data.get(position).setSelect(true);
												getUserChildProfile(item.getId(), item);
												notifyDataSetChanged();

												if (data.get(position).getName().contains("幼儿园")) {
													mRootView.showInputSchoolName(true);
												} else {
													mRootView.showInputSchoolName(false);
												}
											}
										});

										if (item.isSelect()) {

											((TextView) holder.getView(R.id.name)).setTextColor(mRootView.getOnAttachActivity().getResources().getColor(R.color.agora_blue));
										} else {
											((TextView) holder.getView(R.id.name)).setTextColor(mRootView.getOnAttachActivity().getResources().getColor(R.color.black));
										}
										holder.getView(R.id.more).setVisibility(item.isSelect() ? View.VISIBLE : View.INVISIBLE);


									}
								});

							} else {
								MToast.makeTextShort(mRootView.getOnAttachActivity(), jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}


	private void getUserChildProfile(String parentId, UserProfile parentItem) {
		if (mModel != null) {
			mModel.getUserProfile(parentId)
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								mChildrenList.clear();
								List<UserProfile> data = jsonObject.getJSONArray("data").toJavaList(UserProfile.class);
								if (data.size() <= 0) {
									parentItem.setSelect(false);
									data.add(parentItem);
								}
								mChildrenList.addAll(data);
								if (mChildrenAdapter != null) {
									mChildrenAdapter.notifyDataSetChanged();
								}
							} else {
								MToast.makeTextShort(mRootView.getOnAttachActivity(), jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}

	List<UserProfile> mChildrenList = new ArrayList<>();

	public void showProfileDialog() {
		mChildrenList = new ArrayList<>();
		View view = View.inflate(mRootView.getOnAttachActivity(), R.layout.dialog_profile, null);

		RecyclerView parentRecyclerView = view.findViewById(R.id.parentRecyclerView);
		RecyclerView childrenRecyclerView = view.findViewById(R.id.childrenRecyclerView);

		parentRecyclerView.setLayoutManager(new LinearLayoutManager(mRootView.getOnAttachActivity()));
		childrenRecyclerView.setLayoutManager(new LinearLayoutManager(mRootView.getOnAttachActivity()));
		getUserParentProfile(parentRecyclerView);

		view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!resultJson.containsKey("postTypeId")) {
					MToast.makeTextShort(mRootView.getOnAttachActivity(), "请选择用户类型");
				} else {
					dialog.dismiss();
					if (mParentUserProfile != null && mChildrenUserProfile != null) {
						mRootView.changeUserType(mParentUserProfile.getName(), mChildrenUserProfile.getName());
					}
				}
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		mChildrenAdapter = new BaseRecyclerViewAdapter<UserProfile>(mRootView.getOnAttachActivity(), mChildrenList, R.layout.item_user_profile) {
			@Override
			public void convert(BaseRecyclerHolder holder, UserProfile item, int position, boolean isScrolling) {

				holder.setText(R.id.name, item.getName());
				holder.getView(R.id.name).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for (UserProfile datum : mChildrenList) {
							datum.setSelect(false);
						}
						mChildrenUserProfile = item;
						mChildrenList.get(position).setSelect(true);
						notifyDataSetChanged();
						resultJson.put("postTypeId", item.getId());
					}
				});
				holder.getView(R.id.more).setVisibility(View.GONE);

				if (item.isSelect()) {
					((TextView) holder.getView(R.id.name)).setTextColor(mRootView.getOnAttachActivity().getResources().getColor(R.color.agora_blue));
				} else {
					((TextView) holder.getView(R.id.name)).setTextColor(mRootView.getOnAttachActivity().getResources().getColor(R.color.black));
				}
			}
		};

		childrenRecyclerView.setAdapter(mChildrenAdapter);


		dialog = new Dialog(mRootView.getOnAttachActivity(), R.style.dialog);
		Window window = dialog.getWindow();

		if (window != null) {
			window.setGravity(Gravity.BOTTOM);
			window.getDecorView().setPadding(0, 0, 0, 0);
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = (int) (DisplayUtil.getHeight(mRootView.getOnAttachActivity()) * 0.6);
			window.setAttributes(lp);
		}
		dialog.setContentView(view);
		dialog.show();

	}

	public void showUserProfilePicker() {


		List<List<UserProfile>> mChildProfileList = new ArrayList<>();
		List<UserProfile> mParentProfileList = new ArrayList<>();
		/*Collections.sort(customCityDataList, new Comparator<UserProfile>() {
			@Override
			public int compare(UserProfile o1, UserProfile o2) {
				return Integer.compare(o1.getPriority(), o2.getPriority());
			}
		});*/
		Timber.e("customCityDataList---->%s", JSON.toJSONString(customCityDataList));

		for (UserProfile userProfile : customCityDataList) {

			mParentProfileList.add(userProfile);
			List<UserProfile> list = new ArrayList<>();
			list.addAll(userProfile.getList());
			mChildProfileList.add(list);
		}


		OptionsPickerView picker = new OptionsPickerBuilder(mRootView.getOnAttachActivity(), new OnOptionsSelectListener() {
			@Override
			public void onOptionsSelect(int options1, int options2, int options3, View v) {

				userType = mParentProfileList.get(options1).getName();
				userTypeId = mParentProfileList.get(options1).getId();

				if (mChildProfileList.get(options1).size() > 0) {
					if (mChildProfileList.get(options1).get(options2) != null) {
//						userType = mChildProfileList.get(options1).get(options2).getName();
						userTypeId = mChildProfileList.get(options1).get(options2).getId();
					}
				}

				if (mChildProfileList.get(options1).size() > 0 && mChildProfileList.get(options1).get(options2) != null) {
					mRootView.changeUserType(userType, mChildProfileList.get(options1).get(options2).getName());
				} else {
					mRootView.changeUserType(userType, null);
				}


				if (userType.contains("幼儿园")) {
					mRootView.showInputSchoolName(true);
				} else {
					mRootView.showInputSchoolName(false);
				}

				//返回的分别是三个级别的选中位置
				/*String tx = options1Items.get(options1).getPickerViewText()
						+ options2Items.get(options1).get(options2)
						*//* + options3Items.get(options1).get(options2).get(options3).getPickerViewText()*//*;
				btn_Options.setText(tx);*/
			}
		})
				.setTitleText("身份选择")
				.setContentTextSize(20)//设置滚轮文字大小
				.setDividerColor(Color.LTGRAY)//设置分割线的颜色
				.setSelectOptions(0, 0)//默认选中项
				.setBgColor(Color.WHITE)
				.setTitleBgColor(Color.LTGRAY)
				.setTitleColor(Color.BLACK)
				.setCancelColor(Color.BLACK)
				.setSubmitColor(Color.BLUE)
				.setTextColorCenter(Color.BLACK)
				.setDecorView(mRootView.getViewGroup())
				.isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
				.isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
				.setOutSideColor(0x00000000) //设置外部遮罩颜色
				.setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
					@Override
					public void onOptionsSelectChanged(int options1, int options2, int options3) {


					}
				})
				.build();
		picker.setPicker(mParentProfileList, mChildProfileList);
		Timber.e("mParentProfileList---->%s", JSON.toJSONString(mParentProfileList));
		Timber.e("mChildProfileList---->%s", JSON.toJSONString(mChildProfileList));
		picker.show();
	}


	public void showCityPicker() {
		if (mRootView.getOnAttachActivity() != null) {
			JDCityPicker cityPicker = new JDCityPicker();
			JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();

			jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
			cityPicker.init(mRootView.getOnAttachActivity());
			cityPicker.setConfig(jdCityConfig);
			cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
				@Override
				public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
					resultJson.put("address", String.format("%s %s %s", province.getName(), city.getName(), district.getName()));
					mRootView.changeUserLocation(String.format("%s %s %s", province.getName(), city.getName(), district.getName()));
				}

				@Override
				public void onCancel() {
					resultJson.remove("address");
				}
			});
			cityPicker.showCityPicker();
		}

	}


	private void getToken(JSONObject jsonObject) {
		mModel.getToken(jsonObject).compose(RxSchedulersHelper.io_main())
				.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void _onNext(JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							if (jsonObject.getJSONObject("data").getInteger("code") == 200) {
								Constant.IMTOKEN = jsonObject.getJSONObject("data").getString("token");
								Preferences.setImToken(Constant.IMTOKEN);
								IMConnect(Constant.IMTOKEN);
							} else {
								mRootView.showMessage("当前登陆信息失效 请重新登陆");
							}
						}
					}
				});
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
				mRootView.getOnAttachActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mRootView.showMessage("融云token错误");
					}
				});

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
				if (mRootView != null && mRootView.getOnAttachActivity() != null) {
					mRootView.launchActivity(new Intent(mRootView.getOnAttachActivity(), IndexActivity.class));
					mRootView.killMyself();
				} else {
					if (mRootView != null) {
						mRootView.startActivity(IndexActivity.class);
					}

				}

			}

			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				mRootView.getOnAttachActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						count++;

						if (count <= 3) {
							com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
							object.put("nickname", MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME));
							object.put("userId", MMKV.defaultMMKV().decodeString(MMKVHelper.ID));
							if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO))) {
								object.put("avatar", MMKV.defaultMMKV().decodeString(MMKVHelper.WEIXINHEAD));
							} else {
								object.put("avatar", MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO));
							}
							getToken(object);
						} else {
							ToastUtils.showToast("聊天系统登录失败  请重试");
							count = 0;
						}

					}
				});
				com.orhanobut.logger.Logger.e(errorCode.toString());
			}
		});
	}

	private int count = 0;

	@Override
	public void onDestroy() {
		super.onDestroy();
		resultJson.clear();
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
		}
		this.mErrorHandler = null;
		this.mAppManager = null;
		this.mImageLoader = null;
		this.mApplication = null;
	}
}

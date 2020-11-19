package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entiy.MakerColumn;
import com.zhongyou.meet.mobile.entiy.MeetingJoin;
import com.zhongyou.meet.mobile.entiy.RecomandData;
import com.zhongyou.meet.mobile.mvp.contract.MakerContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseAudioDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseMeetDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseVideoDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.UIDUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.bingoogolapple.bgabanner.BGABanner;
import io.agora.openlive.ui.MeetingInitActivity;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import timber.log.Timber;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/05/2020 10:34
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
public class MakerPresenter extends BasePresenter<MakerContract.Model, MakerContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;
	private Dialog dialog;
	private AlertDialog mPhoneStatusDialog;

	@Inject
	public MakerPresenter(MakerContract.Model model, MakerContract.View rootView) {
		super(model, rootView);
	}

	public void getRecommendData(BGABanner banner, RecyclerView recyclerView) {
		if (mModel != null) {
			mModel.getRecommend(2).compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {

						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							mRootView.hideLoading();
							if (jsonObject.getInteger("errcode") == 0) {
								RecomandData data = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), RecomandData.class);
								setBannerData(banner, data);
								if (mRootView != null) {
									mRootView.getAudioDataSuccess(data.getChangeLessonByDay());
								}

							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}


						}

						@Override
						public void _onError(int code, String msg) {
							super._onError(code, msg);
							mRootView.hideLoading();
							getMakerColumn();
						}

						@Override
						public void onComplete() {
							super.onComplete();
							mRootView.hideLoading();

						}
					});
		}
	}


	private void setBannerData(BGABanner banner, RecomandData data) {
		List<String> textLists = new ArrayList<>();
		if (data.getChuangkeList() == null) {
			return;
		}
		for (RecomandData.ChuangkeListBean meetingListBean : data.getChuangkeList()) {
			textLists.add(meetingListBean.getName());
		}
		banner.setData(R.layout.item_localimage, data.getChuangkeList(), textLists);
		banner.setAdapter(new BGABanner.Adapter<AppCompatImageView, RecomandData.ChuangkeListBean>() {
			@Override
			public void fillBannerItem(BGABanner banner, AppCompatImageView itemView, RecomandData.ChuangkeListBean model, int position) {
				Glide.with(mRootView.getActivity())
						.load(model.getPictureURL())
						.centerCrop()
						.dontAnimate()
						.error(R.drawable.defaule_banner)
						.placeholder(R.drawable.defaule_banner)
						.into(itemView);
			}
		});
		banner.setDelegate(new BGABanner.Delegate<AppCompatImageView, RecomandData.ChuangkeListBean>() {
			@Override
			public void onBannerItemClick(BGABanner banner, AppCompatImageView itemView, @Nullable RecomandData.ChuangkeListBean model, int position) {

				Timber.e("mode---->%s", JSON.toJSONString(model));
				if (model == null) {
					return;
				}
				if (model.getIsDefaultImg() == 1) {
					return;
				}

				if (TextUtils.isEmpty(model.getUrlId())) {
					return;
				}

				if (model.getLinkType()==1){

				/*MeetingsData.DataBean.ListBean meeting = new MeetingsData.DataBean.ListBean();
				meeting.setId(model.getUrlId());*/
					if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
						showPhoneStatus(1, model, model.getIsToken());
						return;
					}
					if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
						showPhoneStatus(2, model, model.getIsToken());
						return;
					}
//					initDialog(model, model.getIsToken());//弹窗加入会议

					try {
						if (Double.parseDouble(model.getIsToken()) == 1.0D) {//需要加入码的
							if (mRootView.getActivity() != null) {
								JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), model.getUrlId());
							}

						} else {
							if (mRootView.getActivity() != null) {
								JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) mRootView.getActivity(), model.getUrlId());
							}
						}
					} catch (Exception e) {
						if (mRootView.getActivity() != null) {
							JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(),model.getUrlId());
						}
					}

				}else if (model.getLinkType()==2){
					switch (model.getPageType()) {
						case 1://详情页面
							Intent intent = new Intent(mRootView.getActivity(), NewMakerDetailActivity.class);
							intent.putExtra("pageId", model.getUrlId());
							intent.putExtra("seriesId", model.getSeriesId());
							intent.putExtra("isSignUp", model.getIsSignUp());
							mRootView.launchActivity(intent);
							break;
						case 2://直播页面
							intent = new Intent(mRootView.getActivity(), MakerCourseMeetDetailActivity.class);
							intent.putExtra("pageId", model.getUrlId());
							mRootView.launchActivity(intent);
							break;
						case 3://视频页面
							intent = new Intent(mRootView.getActivity(), MakerCourseVideoDetailActivity.class);
							intent.putExtra("pageId", model.getUrlId());
							mRootView.launchActivity(intent);
							break;
						case 4://音频页面
							intent = new Intent(mRootView.getActivity(), MakerCourseAudioDetailActivity.class);
							intent.putExtra("pageId", model.getUrlId());
							mRootView.launchActivity(intent);
							break;
					}
				}

				/*try {

				} catch (Exception e) {
					e.printStackTrace();
				}*/

			}
		});
	}

	public void showPhoneStatus(int i, RecomandData.ChuangkeListBean model, String isToken) {
		mPhoneStatusDialog = new AlertDialog(mRootView.getActivity())
				.builder()
				.setTitle("提示")
				.setMsg(i == 1 ? "检测到摄像头关闭 是否打开？" : "检测到麦克风关闭 是否打开？")
				.setNegativeButton("进入会议", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						try {
							if (Double.parseDouble(model.getIsToken()) == 1.0D) {//需要加入码的
								if (mRootView.getActivity() != null) {
									JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), model.getUrlId());
								}

							} else {
								if (mRootView.getActivity() != null) {
									JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) mRootView.getActivity(), model.getUrlId());
								}
							}
						} catch (Exception e) {
							if (mRootView.getActivity() != null) {
								JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(),model.getUrlId());
							}
						}
					}
				})
				.setPositiveButton("去设置", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						mRootView.launchActivity(new Intent(mRootView.getActivity(), MeetingSettingActivity.class));
					}
				});
		mPhoneStatusDialog.show();
	}

	private void verifyRole(JSONObject params, RecomandData.ChuangkeListBean model, String code) {
		if (mModel == null) {
			return;
		}
		mModel.verifyRole(params).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
				.compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void _onNext(JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							if (model.getIsRecord().equals("1")) {
								Constant.isNeedRecord = true;
							} else {
								Constant.isNeedRecord = false;
							}
							JSONObject params = new JSONObject();
							params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
							params.put("meetingId", model.getUrlId());
							params.put("token", code);
							joinMeeting(params, code);
						} else {
							mRootView.showMessage(jsonObject.getString("errmsg"));
						}
					}
				});
	}

	private void joinMeeting(JSONObject params, String code) {
		if (mModel == null) {
			return;
		}
		mModel.joinMeeting(params).compose(RxSchedulersHelper.io_main())
				.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void _onNext(JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							MeetingJoin meetingJoin = JSON.parseObject(jsonObject.toJSONString(), MeetingJoin.class);
							MMKV.defaultMMKV().encode(meetingJoin.getData().getMeeting().getId(), code);

							if (meetingJoin.getData().getMeeting().getIsRecord()== 1) {
								Constant.isNeedRecord = true;
							} else {
								Constant.isNeedRecord = false;
							}

							int mJoinRole = meetingJoin.getData().getRole();
							String role = "Subscriber";
							if (mJoinRole == 0) {
								role = "Publisher";
							} else if (mJoinRole == 1) {
								role = "Publisher";
							} else if (mJoinRole == 2) {
								role = "Subscriber";
							}
							getAgoraKey(meetingJoin.getData().getMeeting().getIsMeeting(), role, mJoinRole, meetingJoin);
						} else {
							mRootView.showMessage(jsonObject.getString("errmsg"));
						}
					}
				});

	}

	private void getAgoraKey(int type, String role, int joinrole, MeetingJoin meetingJoin) {
		if (mModel == null) {
			return;
		}
		mModel.getAgoraKey(meetingJoin.getData().getMeeting().getId(), UIDUtil.generatorUID(Preferences.getUserId()), role)
				.compose(RxSchedulersHelper.io_main())
				.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void _onNext(JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {
							Agora agora = new Agora();
							agora.setAppID(jsonObject.getJSONObject("data").getString("appID"));
							agora.setIsTest(jsonObject.getJSONObject("data").getString("isTest"));
							agora.setSignalingKey(jsonObject.getJSONObject("data").getString("signalingKey"));
							agora.setToken(jsonObject.getJSONObject("data").getString("token"));
							Intent intent = new Intent(mRootView.getActivity(), MeetingInitActivity.class);
							intent.putExtra("agora", agora);
							intent.putExtra("meeting", meetingJoin.getData());
							intent.putExtra("isMaker", type != 1);
							intent.putExtra("role", joinrole);
							mRootView.launchActivity(intent);
						} else {
							mRootView.showMessage(jsonObject.getString("errmsg"));
						}
					}
				});

	}

	private void initDialog(RecomandData.ChuangkeListBean model, String isToken) {
		View view = View.inflate(mRootView.getActivity(), R.layout.dialog_meeting_input_code, null);


		View audienceConfirm = view.findViewById(R.id.audienceConfirm);
		try {
			audienceConfirm.setVisibility(Double.parseDouble(isToken) == 1.0d ? View.GONE : View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
			audienceConfirm.setVisibility(View.GONE);
		}

		if (audienceConfirm.getVisibility()==View.VISIBLE){
			view.findViewById(R.id.confirm).setBackground(mRootView.getActivity().getResources().getDrawable(R.drawable.btn_shap_gray));
		}else {
			view.findViewById(R.id.confirm).setBackground(mRootView.getActivity().getResources().getDrawable(R.drawable.btn_shap_blue));
		}
		final EditText codeEdit = view.findViewById(R.id.code);
		/*if (!SpUtil.getString(meeting.getId(), "").equals("")) {
			codeEdit.setText(SpUtil.getString(meeting.getId(), ""));
		} else {
			codeEdit.setText("");
		}*/
		String code = MMKV.defaultMMKV().decodeString(model.getId());
		if (TextUtils.isEmpty(code)) {
			codeEdit.setText("");
		} else {
			codeEdit.setText(code);
			codeEdit.setSelection(code.length());
		}
		//需要录制
		if (model.getIsRecord().equals("1")) {
			Constant.isNeedRecord = true;
		} else {
			Constant.isNeedRecord = false;
		}
		view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if (!TextUtils.isEmpty(codeEdit.getText())) {
					JSONObject params = new JSONObject();
					params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
					params.put("meetingId", model.getUrlId());
					params.put("token", codeEdit.getText().toString().trim());
					verifyRole(params, model, codeEdit.getText().toString().trim());
				} else {
					codeEdit.setError("加入码不能为空");
				}
			}
		});

		audienceConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				JSONObject params = new JSONObject();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", model.getUrlId());
				params.put("token", "");
				verifyRole(params, model, "");
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = new Dialog(mRootView.getActivity(), R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.show();
	}


	public void getMakerColumn() {
		if (mModel != null) {
			mModel.getMakerColumn().compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								MakerColumn data = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), MakerColumn.class);
								mRootView.getColumnData(data,true);
							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}

						@Override
						public void _onError(int code, String msg) {
							super._onError(code, msg);
							mRootView.getColumnData(null,true);
						}
					});
		}
	}


	public void showQuickJoinDialog() {
		View view = View.inflate(mRootView.getActivity(), R.layout.dialog_meeting_input_code, null);

		final EditText codeEdit = view.findViewById(R.id.code);
		TextView title = view.findViewById(R.id.textView2);
		title.setText("请输入加入码快速加入");
		view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if (!TextUtils.isEmpty(codeEdit.getText())) {
					JSONObject params = new JSONObject();
					params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
					params.put("meetingId", "");
					params.put("token", codeEdit.getText().toString().trim());
					quickJoinMeeting(params);
				} else {
					codeEdit.setError("加入码不能为空");
				}
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = new Dialog(mRootView.getActivity(), R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.show();
	}

	public void showPhoneStatusDialog(int i) {
		mPhoneStatusDialog = new AlertDialog(mRootView.getActivity())
				.builder()
				.setTitle("提示")
				.setMsg(i == 1 ? "检测到摄像头关闭 是否打开？" : "检测到麦克风关闭 是否打开？")
				.setNegativeButton("进入会议", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						showQuickJoinDialog();
					}
				})
				.setPositiveButton("去设置", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						mRootView.launchActivity(new Intent(mRootView.getActivity(), MeetingSettingActivity.class));
					}
				});
		mPhoneStatusDialog.show();

	}

	private void quickJoinMeeting(JSONObject json) {
		if (mRootView == null) {
			return;
		}
		if (mModel != null) {
			mModel.quickJoin(json)
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								MeetingJoin meetingJoin = JSON.parseObject(jsonObject.toJSONString(), MeetingJoin.class);
								int mJoinRole = meetingJoin.getData().getRole();
								String role = "Subscriber";
								if (mJoinRole == 0) {
									role = "Publisher";
								} else if (mJoinRole == 1) {
									role = "Publisher";
								} else if (mJoinRole == 2) {
									role = "Subscriber";
								}
								if (meetingJoin.getData().getMeeting().getIsRecord() == 1) {
									Constant.isNeedRecord = true;
								} else {
									Constant.isNeedRecord = false;
								}
								getAgoraKey(meetingJoin.getData().getMeeting().getIsMeeting(), role, mJoinRole, meetingJoin);
							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}

	/**
	 * 获取未读消息
	 */
	public void getUnReadMessage() {
		if (mModel != null) {
			mModel.getUnReadMessageCount().compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {

						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								Integer data = jsonObject.getInteger("data");
								if (data == 0) {
									mRootView.showRedDot(false, data);
								} else {
									mRootView.showRedDot(true, data);
								}
							} else {
								mRootView.showRedDot(false, 0);
							}
						}

						@Override
						public void _onError(int code, String msg) {
							super._onError(code, msg);
							mRootView.showRedDot(false, 0);
						}
					});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dialog != null) {
			dialog.dismiss();
		}
		if (mPhoneStatusDialog != null) {
			mPhoneStatusDialog.dismiss();
		}
		this.mErrorHandler = null;
		this.mAppManager = null;
		this.mImageLoader = null;
		this.mApplication = null;
	}
}

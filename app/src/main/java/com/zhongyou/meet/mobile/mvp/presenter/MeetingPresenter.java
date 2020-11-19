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
import com.xj.marqueeview.MarqueeView;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.MarqueeAdapter;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.entiy.MakerDetail;
import com.zhongyou.meet.mobile.entiy.MeetingJoin;
import com.zhongyou.meet.mobile.entiy.MeetingsData;
import com.zhongyou.meet.mobile.entiy.RecomandData;
import com.zhongyou.meet.mobile.mvp.contract.MeetingContract;
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
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 03/27/2020 15:18
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
public class MeetingPresenter extends BasePresenter<MeetingContract.Model, MeetingContract.View> {
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
	public MeetingPresenter(MeetingContract.Model model, MeetingContract.View rootView) {
		super(model, rootView);
	}

	//判断是否是超级管理员 超级管理员可在app内创建会议
	public void requestAdmin() {
		if (mModel != null) {
			mModel.requestMeetingAdmin().compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								Boolean isAdmin = jsonObject.getJSONObject("data").getBoolean("meetingAdmin");
								if (mRootView != null) {
									mRootView.showAdminViews(isAdmin);
								}
							}

						}
					});
		}
	}

	//获取首页会议 titlt传空字符串  0 公开会议 1 受邀会议
	public void getMeetings(int type, int pageNo, String title,String isMeeting) {
		if (mModel != null) {
			mModel.getAllMeeting(type, pageNo, title,isMeeting).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {

						@Override
						public void onComplete() {
							super.onComplete();
							if (type == 0) {
//								getBannerData();//完成后再去请求banner
							}

						}

						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);

						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								MeetingsData meetingsData = JSON.parseObject(jsonObject.toJSONString(), MeetingsData.class);
								if (mRootView != null) {
									if (pageNo == 1) {
										if (meetingsData.getData().getList().size() <= 0) {
											mRootView.showEmptyView(true);
											return;
										} else {
											mRootView.showEmptyView(false);
										}
									}
									mRootView.showMeetings(meetingsData);
								}
							} else {
								if (mRootView != null && pageNo == 1) {
									mRootView.showEmptyView(true);
								} else if (mRootView != null) {
									mRootView.hideLoading();
								}
							}
						}

						@Override
						public void _onError(int code, String msg) {
							super._onError(code, msg);
							if (mRootView != null && pageNo == 1) {
								mRootView.showEmptyView(true);
							} else if (mRootView != null) {
								mRootView.hideLoading();
							}
							if (type == 0) {
//								getBannerData();//完成后再去请求banner
							}
						}
					});
		}
	}

	@Deprecated
	private void getBannerData() {
		if (mModel != null) {
			mModel.getBanner().compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {

						@Override
						public void _onError(int code, String msg) {
							super._onError(code, msg);
							if (mRootView != null) {
								mRootView.showBannerView(false);
							}
						}

						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								if (mRootView != null) {
									mRootView.showBannerView(jsonObject);
									if (jsonObject.getJSONArray("data").size() <= 0) {
										// TODO: 2020/4/3   上线的时候需要删除 只留下 mRootView.showBannerView(false);
										if (BuildConfig.DEBUG) {
											mRootView.showBannerView(true);
										} else {
											mRootView.showBannerView(false);
										}
									} else {
										mRootView.showBannerView(true);
									}
								}
							} else {
								if (mRootView != null) {
									mRootView.showBannerView(false);
								}
							}
						}
					});


		}
	}

	public void verifyRole(JSONObject jsonObject, MeetingsData.DataBean.ListBean meeting, String code) {
		Constant.KEY_code = "";
		Constant.KEY_meetingID = "";
		if (mModel != null) {
			mModel.verifyRole(jsonObject).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								if (mRootView != null) {
									try {
										Thread.sleep(1500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									mRootView.verifyRole(jsonObject, meeting, code);
								}
							} else {
								if (mRootView != null) {
									mRootView.showMessage(jsonObject.getString("errmsg"));
								}
							}


						}
					});
		}
	}

	public void joinMeeting(JSONObject jsonObject, MeetingsData.DataBean.ListBean meeting, String code) {
		if (mModel != null) {
			mModel.joinMeeting(jsonObject).compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								if (mRootView != null) {
									mRootView.joinMeeting(jsonObject, meeting, code);

								}
							} else {
								if (mRootView != null) {
									mRootView.showMessage(jsonObject.getString("errmsg"));
								}
							}
						}
					});
		}
	}

	public void getAgoraKey(int type, String channel, String account, String role, int joinrole, MeetingJoin meetingJoin) {
		if (mModel != null) {
			mModel.getAgoraKey(channel, account, role).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								if (mRootView != null) {
									mRootView.getAgoraKey(type, jsonObject, meetingJoin, joinrole);
								}
							} else {
								if (mRootView != null) {
									mRootView.showMessage(jsonObject.getString("errmsg"));
								}
							}
						}
					});
		}
	}


	public void getRecommendData(BGABanner banner, MarqueeView marqueeView) {
		if (mRootView == null) {
			return;
		}
		if (mModel != null) {
			mModel.getRecommend(1).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void onSubscribe(Disposable d) {
							addDispose(d);
						}

						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								RecomandData recomandData = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), RecomandData.class);
								try {
									setBannerView(banner, recomandData);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									setMarqueeView(marqueeView, recomandData);
								}

							} else if (mRootView != null) {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}

						@Override
						public void onComplete() {
							super.onComplete();
							getMeetings(0, 1, "","1");
						}

						@Override
						public void _onError(int code, String msg) {
							super._onError(code, msg);
							getMeetings(0, 1, "","1");

						}
					});
		}
	}

	private void setMarqueeView(MarqueeView marqueeView, RecomandData data) {
		if (data.getMeetingAdvance() == null) {
			mRootView.isShowMarqueeView(false);
			return;
		}
		if (data.getMeetingAdvance().size() <= 0) {
			if (mRootView != null) {
				mRootView.isShowMarqueeView(false);
			}
			return;
		} else {
			if (mRootView != null) {
				mRootView.isShowMarqueeView(true);
			}
		}
		MarqueeAdapter adapter = new MarqueeAdapter(mRootView.getOnAttachActivity(), data.getMeetingAdvance());

		marqueeView.setAdapter(adapter);
		adapter.setOnAddCollectListener(position -> {
			if (mModel != null) {
				orderMeeting(data.getMeetingAdvance().get(position).getMeetingId(), 0, position);
			}
		});

	}

	public void orderMeeting(String meetingid, int type, int positon) {
		mModel.orderMeeting(meetingid)
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
						if (jsonObject.getInteger("errcode") != 0 && mRootView != null) {
							mRootView.showMessage(jsonObject.getString("errmsg"));
							if (type == 1) {
								mRootView.orderMeetingResult(false, positon);
							}
						} else {
							if (mRootView != null) {
								if (type == 0) {
									mRootView.showMessage("预定成功");
								} else {
//									mRootView.showMessage("收藏成功");
									getUnReadMessage();
									mRootView.orderMeetingResult(true, positon);
								}

							}
						}
					}


				});
	}

	public void cancleAdvance(String meetingId, int position) {
		if (mModel != null) {
			mModel.cancelAdvance(meetingId).compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") != 0) {
								mRootView.showMessage(jsonObject.getString("errmsg"));
								if (mRootView != null) {
									mRootView.cancleAdvance(position, false);
								}
							} else {
								if (mRootView != null) {
									mRootView.cancleAdvance(position, true);
								}
							}
						}
					});
		}
	}

	private void setBannerView(BGABanner banner, RecomandData recomandData) {
		List<String> textLists = new ArrayList<>();
		if (recomandData.getMeetingList() == null) {
			return;
		}
		for (RecomandData.MeetingListBean meetingListBean : recomandData.getMeetingList()) {
			textLists.add(meetingListBean.getName());
		}
		banner.setData(R.layout.item_localimage, recomandData.getMeetingList(), textLists);
		banner.setAdapter(new BGABanner.Adapter<AppCompatImageView, RecomandData.MeetingListBean>() {
			@Override
			public void fillBannerItem(BGABanner banner, AppCompatImageView itemView, RecomandData.MeetingListBean model, int position) {
				Glide.with(mRootView.getOnAttachActivity())
						.load(model.getPictureURL())
						.centerCrop()
						.dontAnimate()
						.error(R.drawable.defaule_banner)
						.placeholder(R.drawable.defaule_banner)
						.into(itemView);
			}
		});
		banner.setDelegate(new BGABanner.Delegate<AppCompatImageView, RecomandData.MeetingListBean>() {
			@Override
			public void onBannerItemClick(BGABanner banner, AppCompatImageView itemView, @Nullable RecomandData.MeetingListBean model, int position) {
				if (model == null) {
					return;
				}
				if (model.getIsDefaultImg() == 1) {
					return;
				}

				if (TextUtils.isEmpty(model.getUrlId())) {
					return;
				}
				if (model.getLinkType() == 1) {
					MeetingsData.DataBean.ListBean meeting = new MeetingsData.DataBean.ListBean();
					meeting.setId(model.getUrlId());
					try {
						meeting.setIsRecord(Integer.parseInt(model.getIsRecord()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (mRootView != null) {
						if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
							mRootView.showPhoneStatus(1, meeting, model.getIsToken());
							return;
						}

						if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
							mRootView.showPhoneStatus(2, meeting, model.getIsToken());
							return;
						}
						try {
							if (Double.parseDouble(String.valueOf(model.getIsToken())) == 1.0D) {//需要加入码的
								if (mRootView.getOnAttachActivity() != null) {
									JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getOnAttachActivity(), model.getUrlId());
								}

							} else {
								if (mRootView.getOnAttachActivity() != null) {
									JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) mRootView.getOnAttachActivity(), model.getUrlId());
								}
							}
						} catch (Exception e) {
							if (mRootView.getOnAttachActivity() != null) {
								JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getOnAttachActivity(), model.getUrlId());
							}
						}
					}
				} else if (model.getLinkType() == 2) {
					switch (model.getPageType()) {
						case 1://详情页面
							Intent intent = new Intent(mRootView.getOnAttachActivity(), NewMakerDetailActivity.class);
							intent.putExtra("pageId", model.getUrlId());
							intent.putExtra("seriesId", model.getSeriesId());
							intent.putExtra("isSignUp", model.getIsSignUp());
							mRootView.launchActivity(intent);
							break;
						case 2://直播页面
							intent = new Intent(mRootView.getOnAttachActivity(), MakerCourseMeetDetailActivity.class);
							intent.putExtra("pageId", model.getUrlId());
							mRootView.launchActivity(intent);
							break;
						case 3://视频页面
							intent = new Intent(mRootView.getOnAttachActivity(), MakerCourseVideoDetailActivity.class);
							intent.putExtra("pageId", model.getUrlId());
							mRootView.launchActivity(intent);
							break;
						case 4://音频页面
							intent = new Intent(mRootView.getOnAttachActivity(), MakerCourseAudioDetailActivity.class);
							intent.putExtra("pageId", model.getUrlId());
							mRootView.launchActivity(intent);
							break;

					}
				}

			}
		});


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


								getAgoraKey(meetingJoin.getData().getMeeting().getIsMeeting(), meetingJoin.getData().getMeeting().getId(), UIDUtil.generatorUID(Preferences.getUserId()), role, mJoinRole, meetingJoin);
							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}


	public void showQuickJoinDialog() {

		View view = View.inflate(mRootView.getOnAttachActivity(), R.layout.dialog_meeting_input_code, null);
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
					codeEdit.setError("会议加入码不能为空");
				}
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = new Dialog(mRootView.getOnAttachActivity(), R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.show();
	}

	public void showPhoneStatusDialog(int i) {
		mPhoneStatusDialog = new AlertDialog(mRootView.getOnAttachActivity())
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
						mRootView.launchActivity(new Intent(mRootView.getOnAttachActivity(), MeetingSettingActivity.class));
					}
				});
		mPhoneStatusDialog.show();

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

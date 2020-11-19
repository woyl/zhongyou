package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allen.library.SuperButton;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.tencent.mmkv.MMKV;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entiy.LiveData;
import com.zhongyou.meet.mobile.entiy.MeetingJoin;
import com.zhongyou.meet.mobile.entiy.Question;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseMeetDetailContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.UIDUtil;

import java.util.List;

import javax.inject.Inject;

import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.MeetingInitActivity;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/17/2020 17:45
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MakerCourseMeetDetailPresenter extends BasePresenter<MakerCourseMeetDetailContract.Model, MakerCourseMeetDetailContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;
	private Dialog mDialog;
	private Dialog dialog;
	private AlertDialog mPhoneStatusDialog;

	@Inject
	public MakerCourseMeetDetailPresenter(MakerCourseMeetDetailContract.Model model, MakerCourseMeetDetailContract.View rootView) {
		super(model, rootView);
	}

	public void getDatail(String pageId) {
		if (mModel != null) {
			mModel.getDatail(pageId, "")
					.compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								LiveData data = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), LiveData.class);
								mRootView.getDataSuccess(data);
							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}

	public void getQuestion(String pageId) {
		if (mModel != null) {
			mModel.getQuestion(pageId)
					.compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								List<Question> data = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), Question.class);
								showSignScoreDialog(data, pageId);
							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}

	private void showSignScoreDialog(List<Question> data, String pageId) {
		if (mRootView == null) {
			return;
		}
		if (mDialog == null) {
			mDialog = new Dialog(mRootView.getActivity(), R.style.MyDialog);
		}

		for (Question datum : data) {
			datum.setScore(1.0f);
		}

		View v = LayoutInflater.from(mRootView.getActivity()).inflate(R.layout.dialog_sign_score, null, false);
		RecyclerView recyclerView = v.findViewById(R.id.recyclerview);
		EditText evaluteEd = v.findViewById(R.id.writeWorldsEd);

		recyclerView.setLayoutManager(new LinearLayoutManager(mRootView.getActivity()));
		recyclerView.setAdapter(new BaseRecyclerViewAdapter<Question>(mRootView.getActivity(), data, R.layout.item_dialog_sign_score) {

			@Override
			public void convert(BaseRecyclerHolder holder, Question item, int position, boolean isScrolling) {
				holder.setText(R.id.title, (position + 1) + ". " + item.getName());
				ScaleRatingBar simpleRatingBar = holder.getView(R.id.simpleRatingBar);
				simpleRatingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
					@Override
					public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
						item.setScore(rating);
					}
				});
				if (position == data.size() - 1) {
					holder.getView(R.id.line).setVisibility(View.INVISIBLE);
				}
			}
		});

		SuperButton commitButton = v.findViewById(R.id.commitButton);
		commitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject jsonObject = new JSONObject();
				for (int i = 0; i < data.size(); i++) {
					if (i == 0) {
						jsonObject.put("firstQuestionGrade", data.get(i).getScore());
					} else if (i == 1) {
						jsonObject.put("secondQuestionGrade", data.get(i).getScore());
					} else if (i == 2) {
						jsonObject.put("thirdQuestionGrade", data.get(i).getScore());
					}
				}
				jsonObject.put("pageId", pageId);
				jsonObject.put("evaluate", evaluteEd.getText().toString().trim());
				if (mModel != null) {
					mModel.commitQuestion(jsonObject)
							.compose(RxSchedulersHelper.io_main())
							.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
							.subscribe(new RxSubscriber<JSONObject>() {
								@Override
								public void _onNext(JSONObject jsonObject) {

									if (jsonObject.getInteger("errcode") == 0) {
										mRootView.commitQuestionSuccess(true);
										mDialog.dismiss();
									} else {
										mRootView.showMessage(jsonObject.getString("errmsg"));
										mRootView.commitQuestionSuccess(false);
									}
								}

								@Override
								public void _onError(int code, String msg) {
									super._onError(code, msg);
									mRootView.commitQuestionSuccess(false);
								}
							});
				}

			}
		});
		mDialog.setContentView(v);
		mDialog.show();
	}


	public void initDialog(LiveData meeting, int isToken) {
		View view = View.inflate(mRootView.getActivity(), R.layout.dialog_meeting_input_code, null);

		final EditText codeEdit = view.findViewById(R.id.code);

		View audienceConfirm = view.findViewById(R.id.audienceConfirm);
		audienceConfirm.setVisibility(isToken == 1 ? View.GONE : View.VISIBLE);
		String code = MMKV.defaultMMKV().decodeString(meeting.getLiveId());
		if (TextUtils.isEmpty(code)) {
			codeEdit.setText("");
		} else {
			codeEdit.setText(code);
			codeEdit.setSelection(code.length());
		}

		if (audienceConfirm.getVisibility()==View.VISIBLE){
			view.findViewById(R.id.confirm).setBackground(mRootView.getActivity().getResources().getDrawable(R.drawable.btn_shap_gray));
		}else {
			view.findViewById(R.id.confirm).setBackground(mRootView.getActivity().getResources().getDrawable(R.drawable.btn_shap_blue));
		}

		view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if (!TextUtils.isEmpty(codeEdit.getText())) {
					JSONObject params = new JSONObject();
					params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
					params.put("meetingId", meeting.getLiveId());
					params.put("token", codeEdit.getText().toString().trim());

					verifyRole(params, meeting.getLiveId(), meeting.getIsLiveRecord(), 1, codeEdit.getText().toString().trim());

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
				params.put("meetingId", meeting.getLiveId());
				params.put("token", "");
				verifyRole(params, meeting.getLiveId(), meeting.getIsRecord(), 1, "");

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

	public void showPhoneStatus(int i, final LiveData meeting, int isToken) {
		mPhoneStatusDialog = new AlertDialog(mRootView.getActivity())
				.builder()
				.setTitle("提示")
				.setMsg(i == 1 ? "检测到摄像头关闭 是否打开？" : "检测到麦克风关闭 是否打开？")
				.setNegativeButton("进入会议", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						try {
							if (Double.parseDouble(String.valueOf(isToken)) == 1.0D) {//需要加入码的
								if (mRootView.getActivity() != null) {
									JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), meeting.getLiveId());
								}

							} else {
								if (mRootView.getActivity() != null) {
									JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) mRootView.getActivity(), meeting.getLiveId());
								}
							}
						} catch (Exception e) {
							if (mRootView.getActivity() != null) {
								JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(),meeting.getLiveId());
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

	private void verifyRole(JSONObject params, String id, int isRecord, int resolvingPower, String code) {
		if (mModel != null) {
			mModel.verfyRole(params).compose(RxSchedulersHelper.io_main())
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
							/*	int profileIndex = 0;
								if (resolvingPower == 1.0) {
									profileIndex = 0;
								} else if (resolvingPower == 2.0) {
									profileIndex = 1;
								} else if (resolvingPower == 3.0) {
									profileIndex = 2;
								}

								SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mRootView.getActivity());
								SharedPreferences.Editor editor = pref.edit();
								editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
								editor.apply();*/



								JSONObject params = new JSONObject();
								params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
								params.put("meetingId", id);
								params.put("token", code);
								joinMeeting(params, id, code);
							} else {
								if (mRootView != null) {
									mRootView.showMessage(jsonObject.getString("errmsg"));
								}
							}
						}
					});
		}
	}


	public void joinMeeting(JSONObject jsonObject, String id, String code) {

		if (mModel != null) {
			mModel.joinMeeting(jsonObject).compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {

								MeetingJoin meetingJoin = JSON.parseObject(jsonObject.toJSONString(), MeetingJoin.class);
								if (meetingJoin == null || meetingJoin.getData() == null || TextUtils.isEmpty(meetingJoin.getData().getMeeting().getId())) {
									mRootView.showMessage("出错了 请重试");
									return;
								}

								if (meetingJoin.getData().getMeeting().getIsRecord() == 1) {
									Constant.isNeedRecord = true;
								} else {
									Constant.isNeedRecord = false;
								}
								MMKV.defaultMMKV().encode(id, code);
								int mJoinRole = meetingJoin.getData().getRole();
								String role = "Subscriber";
								if (mJoinRole == 0) {
									role = "Publisher";
								} else if (mJoinRole == 1) {
									role = "Publisher";
								} else if (mJoinRole == 2) {
									role = "Subscriber";
								}
								getAgoraKey(meetingJoin.getData().getMeeting().getId(), UIDUtil.generatorUID(Preferences.getUserId()), role, mJoinRole, meetingJoin);
								MMKV.defaultMMKV().encode(MMKVHelper.KEY_meetingQuilty, meetingJoin.getData().getMeeting().getResolvingPower());

							} else {
								if (mRootView != null) {
									mRootView.showMessage(jsonObject.getString("errmsg"));
								}
							}
						}
					});
		}


	}

	public void getAgoraKey(String channel, String account, String role, int joinrole, MeetingJoin meetingJoin) {
		if (mModel != null) {
			mModel.getAgoraKey(channel, account, role).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								if (mRootView != null) {
									Agora agora = new Agora();
									agora.setAppID(jsonObject.getJSONObject("data").getString("appID"));
									agora.setIsTest(jsonObject.getJSONObject("data").getString("isTest"));
									agora.setSignalingKey(jsonObject.getJSONObject("data").getString("signalingKey"));
									agora.setToken(jsonObject.getJSONObject("data").getString("token"));

									Intent intent = new Intent(mRootView.getActivity(), MeetingInitActivity.class);
									intent.putExtra("agora", agora);
									intent.putExtra("meeting", meetingJoin.getData());
									intent.putExtra("role", joinrole);
									intent.putExtra("isMaker", meetingJoin.getData().getMeeting().getIsMeeting() != 1);
									mRootView.launchActivity(intent);
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


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDialog != null) {
			mDialog.dismiss();
		}
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

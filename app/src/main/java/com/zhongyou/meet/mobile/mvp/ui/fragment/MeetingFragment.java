package com.zhongyou.meet.mobile.mvp.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.appbar.AppBarLayout;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxBus;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.maning.mndialoglibrary.MToast;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mmkv.MMKV;
import com.xj.marqueeview.MarqueeView;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.IM.IMInfoProvider;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.business.MeetingSearchActivity;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.di.component.DaggerMeetingComponent;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entiy.MeetingJoin;
import com.zhongyou.meet.mobile.entiy.MeetingsData;
import com.zhongyou.meet.mobile.event.LikeEvent;
import com.zhongyou.meet.mobile.mvp.contract.MeetingContract;
import com.zhongyou.meet.mobile.mvp.presenter.MeetingPresenter;
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.BadgeHelper;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.Page;
import com.zhongyou.meet.mobile.utils.UIDUtil;
import com.zhongyou.meet.mobile.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import io.agora.openlive.ui.MeetingInitActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import me.jessyan.autosize.utils.AutoSizeUtils;
import rx.functions.Action1;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


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
@Page(name = "会议首页")
public class MeetingFragment extends BaseFragment<MeetingPresenter> implements MeetingContract.View {

	@BindView(R.id.txt_search)
	TextView txtSearch;
	@BindView(R.id.banner_guide_content)
	BGABanner bannerGuideContent;
	@BindView(R.id.appBarLayout)
	AppBarLayout appBarLayout;
	@BindView(R.id.mRecyclerView)
	RecyclerView mRecyclerView;
	@BindView(R.id.emptyView)
	TextView emptyView;
	@BindView(R.id.start_meeting)
	FrameLayout startMeeting;
	@BindView(R.id.id_coordinatorlayout_appbar_scroll)
	CoordinatorLayout idCoordinatorlayoutAppbarScroll;
	@BindView(R.id.mSwipeRefreshLayout)
	SmartRefreshLayout mSwipeRefreshLayout;
	private static MeetingFragment fragment;
	@BindView(R.id.marqueeView)
	MarqueeView marqueeView;
	@BindView(R.id.icon_notify)
	ImageView iconNotify;
	@BindView(R.id.red_dot)
	RelativeLayout redDot;

	private int mCurrentPage = 1;
	private int mCurrentMeetingType = 0;
	private BaseRecyclerViewAdapter<MeetingsData.DataBean.ListBean> mAdapter;
	private Dialog dialog;
	private String meetingCode;
	public static final String KEY_MEETING_TYPE = "meetingType";
	List<MeetingsData.DataBean.ListBean> list = new ArrayList<>();
	private AlertDialog mPhoneStatusDialog;
	private BadgeHelper mBadgeHelper;

	public static MeetingFragment newInstance() {
		if (fragment == null) {
			fragment = new MeetingFragment();
		}
		return fragment;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		IMInfoProvider infoProvider = new IMInfoProvider();
		infoProvider.init(BaseApplication.getInstance());

		UserInfo info = new UserInfo(MMKV.defaultMMKV().decodeString(MMKVHelper.ID), MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME), Uri.parse(MMKV.defaultMMKV().decodeString(MMKVHelper.PHOTO)));

		RongIM.getInstance().refreshUserInfoCache(info);
		RongIM.getInstance().setCurrentUserInfo(info);
		RongIM.getInstance().setMessageAttachedUserInfo(true);

		if (!TextUtils.isEmpty(Constant.KEY_meetingID) || !TextUtils.isEmpty(Constant.KEY_code)) {
			MeetingsData.DataBean.ListBean listBean = new MeetingsData.DataBean.ListBean();
			listBean.setId(Constant.KEY_meetingID);
			JSONObject params = new JSONObject();
			params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
			params.put("meetingId", Constant.KEY_meetingID);
			params.put("token", Constant.KEY_code);
			//如果需要打开app就进入会议 就放开这个
			if (mPresenter != null) {
				mPresenter.verifyRole(params, listBean, Constant.KEY_code);
			}
		}

	}

	@Override
	public void setupFragmentComponent(@NonNull AppComponent appComponent) {
		DaggerMeetingComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);
	}

	@Override
	public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_meeting, container, false);
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {
		setListener();
		if (mPresenter != null) {
			mSwipeRefreshLayout.setRefreshHeader(new MaterialHeader(BaseApplication.getInstance()));
			mSwipeRefreshLayout.autoRefresh();
		}
		RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (o instanceof LikeEvent) {
					if (((LikeEvent) o).isLike()) {
						if (mPresenter != null) {
							mPresenter.getUnReadMessage();
						}
					}
				}
			}
		});
	}

	private void setListener() {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(@NonNull RefreshLayout refreshLayout) {
				mCurrentPage = 1;
				if (mPresenter != null) {
					mPresenter.getRecommendData(bannerGuideContent, marqueeView);

				}
			}
		});
		mSwipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
				mCurrentPage++;
				if (mPresenter != null) {
					mPresenter.getMeetings(mCurrentMeetingType, mCurrentPage, "","1");
				}
			}
		});


	}

	@Override
	public void onStop() {
		super.onStop();

		if (bannerGuideContent != null) {
			bannerGuideContent.stopAutoPlay();
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		if (bannerGuideContent != null) {
			bannerGuideContent.startAutoPlay();
		}

		if (mPresenter != null) {
			mPresenter.getUnReadMessage();
		}


	}

	@Override
	public void setData(@Nullable Object data) {

	}


	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {
		mSwipeRefreshLayout.finishRefresh();
		mSwipeRefreshLayout.finishLoadMore();
		if (mPresenter != null) {
			mPresenter.getUnReadMessage();
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

	}

	@OnClick({R.id.txt_search, R.id.inputCode, R.id.icon_notify})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.txt_search:
				Intent searchMeetingIntent = new Intent(mContext, MeetingSearchActivity.class);
				searchMeetingIntent.putExtra(KEY_MEETING_TYPE, mCurrentMeetingType);
				launchActivity(searchMeetingIntent);
				break;
			case R.id.inputCode:
				if (mPresenter != null) {
					if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
						mPresenter.showPhoneStatusDialog(1);
						return;
					}
					if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
						mPresenter.showPhoneStatusDialog(2);
						return;
					}
					JoinMeetingDialog.INSTANCE.showQuickJoinMeeting((AppCompatActivity) getActivity());
				}
//				launchActivity(new Intent(getActivity(), MakerCourseAudioDetailActivity.class));
				break;
			case R.id.icon_notify:
				launchActivity(new Intent(getContext(), MessageDetailActivity.class));
				break;
		}
	}

	@Override
	public Activity getOnAttachActivity() {
		return getActivity();
	}

	/**
	 * 目前都不显示管理员界面
	 */
	@Override
	public void showAdminViews(boolean isShow) {
		if (isShow) {
			startMeeting.setVisibility(View.GONE);
		} else {
			startMeeting.setVisibility(View.GONE);
		}
	}

	/**
	 * 会议数据
	 */
	@Override
	public void showMeetings(MeetingsData meetingsData) {

		if (meetingsData.getData().getTotalPage() <= mCurrentPage) {
			mSwipeRefreshLayout.setEnableLoadMore(false);
		} else {
			mSwipeRefreshLayout.setEnableLoadMore(true);
		}

		mSwipeRefreshLayout.finishRefresh();
		mSwipeRefreshLayout.finishLoadMore();

		if (mCurrentPage == 1) {
			if (mAdapter != null) {
				mAdapter.clearData();
			}
			if (list != null) {
				list.clear();
			}
			list = meetingsData.getData().getList();
		} else {
			list.addAll(meetingsData.getData().getList());
		}

		if (mAdapter == null) {
			mAdapter = new BaseRecyclerViewAdapter<MeetingsData.DataBean.ListBean>(getActivity(), list, R.layout.item_meeting) {
				@Override
				public void convert(BaseRecyclerHolder holder, MeetingsData.DataBean.ListBean item, int position, boolean isScrolling) {
					holder.setText(R.id.title, item.getTitle());
					holder.setText(R.id.begin_time, item.getStartTime());
					TextView state = holder.getView(R.id.meeting_state);
					LikeButton collectButton = holder.getView(R.id.collectButton);
					if (item.getIsCollection() == 0) {
						collectButton.setLiked(false);
					} else {
						collectButton.setLiked(true);
					}
					collectButton.setOnLikeListener(new OnLikeListener() {
						@Override
						public void liked(LikeButton likeButton) {
							if (mPresenter != null) {
								mPresenter.orderMeeting(item.getId(), 1, position);
							}
						}

						@Override
						public void unLiked(LikeButton likeButton) {
							if (mPresenter != null) {
								mPresenter.cancleAdvance(item.getId(), position);
							}
						}
					});
					if (item.getMeetingProcess() == 1) {
						state.setBackgroundResource(R.drawable.bg_meeting_state_new);
						state.setText("进行中");
					} else {
						state.setBackgroundResource(R.drawable.bg_meeting_state_a_new);
						state.setText("未开始");
					}
					if (position % 2 == 0) {
						((ImageView) holder.getView(R.id.imageView)).setImageDrawable(getResources().getDrawable(R.mipmap.bg_meeting_item_a));
					} else {
						((ImageView) holder.getView(R.id.imageView)).setImageDrawable(getResources().getDrawable(R.mipmap.bg_meeting_item_b));
					}
				}
			};
			mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(RecyclerView parent, View view, int position) {

//					initDialog(list.get(position), list.get(position).getIsToken());
					if (position >= list.size() || position < 0) {
						MToast.makeTextShort(getActivity() == null ? BaseApplication.getInstance() : getActivity(), "数据出现错误 请刷新后再试");
						return;
					}

					if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
						showPhoneStatus(1, list.get(position), list.get(position).getIsToken());
						return;
					}
					if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
						showPhoneStatus(2, list.get(position), list.get(position).getIsToken());
						return;
					}

					/*	initDialog	(list.get(position), list.get(position).getIsToken());*/
					try {
						if (Double.parseDouble(list.get(position).getIsToken()) == 1.0D) {//需要加入码的
							if (getActivity() != null) {
								JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) getActivity(), list.get(position).getId());
							}

						} else {
							if (getActivity() != null) {
								JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) getActivity(), list.get(position).getId());
							}
						}
					} catch (Exception e) {
						if (getActivity() != null) {
							JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) getActivity(), list.get(position).getId());
						}
					}


				}
			});

			mRecyclerView.addItemDecoration(new SpaceItemDecoration(0, AutoSizeUtils.pt2px(getActivity(), 10), 0, 0));
			mRecyclerView.setAdapter(mAdapter);
		} else {
			mAdapter.addNewData(list);
		}


	}

	@Override
	public void showBannerView(JSONObject jsonObject) {

	}

	@Override
	public void showEmptyView(boolean isShow) {
		mSwipeRefreshLayout.finishRefresh();
		mSwipeRefreshLayout.finishLoadMore();

		if (isShow) {
			mRecyclerView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		} else {
			mRecyclerView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		}
	}

	@Override
	public void showBannerView(boolean isShow) {
		if (isShow) {
			ViewGroup.LayoutParams layoutParams = bannerGuideContent.getLayoutParams();
			layoutParams.height = AutoSizeUtils.pt2px(getActivity(), 300);
			bannerGuideContent.setLayoutParams(layoutParams);
		} else {
			ViewGroup.LayoutParams layoutParams = bannerGuideContent.getLayoutParams();
			layoutParams.height = 0;//此处不能改为0 否则会报错
			bannerGuideContent.setLayoutParams(layoutParams);
		}
	}

	@Override
	public void verifyRole(JSONObject jsonObject, MeetingsData.DataBean.ListBean meeting, String code) {


		Constant.KEY_meetingID = "";
		Constant.KEY_code = "";

		//需要录制
		if (meeting.getIsRecord() == 1) {
			Constant.isNeedRecord = true;
		} else {
			Constant.isNeedRecord = false;
		}
//		int profileIndex = 0;
//		if (meeting.getResolvingPower() == 1.0) {
//			profileIndex = 0;
//		} else if (meeting.getResolvingPower() == 2.0) {
//			profileIndex = 1;
//		} else if (meeting.getResolvingPower() == 3.0) {
//			profileIndex = 2;
//		}

		MMKV.defaultMMKV().encode(MMKVHelper.KEY_meetingQuilty, meeting.getResolvingPower());

		/*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
		editor.apply();*/

		if (mPresenter != null) {
			JSONObject params = new JSONObject();
			params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
			params.put("meetingId", meeting.getId());
			params.put("token", code);
			mPresenter.joinMeeting(params, meeting, code);
		}
	}

	@Override
	public void joinMeeting(JSONObject jsonObject, MeetingsData.DataBean.ListBean meeting, String code) {
		MeetingJoin meetingJoin = JSON.parseObject(jsonObject.toJSONString(), MeetingJoin.class);
		if (meetingJoin == null || meetingJoin.getData() == null || TextUtils.isEmpty(meetingJoin.getData().getMeeting().getId())) {
			showMessage("出错了 请重试");
			return;
		}
		MMKV.defaultMMKV().encode(meeting.getId(), code);
//		SpUtil.put(meeting.getId(), code);
	/*	JSONObject params = new JSONObject();
		params.put("channel", meetingJoin.getData().getMeeting().getId());
		params.put("account", UIDUtil.generatorUID(Preferences.getUserId()));

	 */

		if (meetingJoin.getData().getMeeting().getIsRecord() == 1) {
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
		if (mPresenter != null) {
			mPresenter.getAgoraKey(meeting.getIsMeeting(), meetingJoin.getData().getMeeting().getId(), UIDUtil.generatorUID(Preferences.getUserId()), role, mJoinRole, meetingJoin);
		}
	}

	@Override
	public void getAgoraKey(int type, JSONObject jsonObject, MeetingJoin meetingJoin, int joinRole) {

		Agora agora = new Agora();
		agora.setAppID(jsonObject.getJSONObject("data").getString("appID"));
		agora.setIsTest(jsonObject.getJSONObject("data").getString("isTest"));
		agora.setSignalingKey(jsonObject.getJSONObject("data").getString("signalingKey"));
		agora.setToken(jsonObject.getJSONObject("data").getString("token"));

		Intent intent = new Intent(mContext, MeetingInitActivity.class);
		BaseApplication.getInstance().setAgora(agora);
		intent.putExtra("agora", agora);
		intent.putExtra("isMaker", type != 1);
		intent.putExtra("meeting", meetingJoin.getData());
		intent.putExtra("role", joinRole);
		/*if (isAdded()) {
			startActivity(intent);
		} else {
			BaseApplication.getInstance().startActivity(intent);
		}*/
		launchActivity(intent);
	}


	public void enterMeetingByUrl(String id, String token) {

	}

	@Override
	public void initDialog(final MeetingsData.DataBean.ListBean meeting, String isToken) {

		View view = View.inflate(mContext, R.layout.dialog_meeting_input_code, null);

		final EditText codeEdit = view.findViewById(R.id.code);

		View audienceConfirm = view.findViewById(R.id.audienceConfirm);
		try {
			audienceConfirm.setVisibility(Double.parseDouble(isToken) == 1.0d ? View.GONE : View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
			audienceConfirm.setVisibility(View.GONE);
		}

		if (audienceConfirm.getVisibility() == View.VISIBLE) {
			view.findViewById(R.id.confirm).setBackground(getResources().getDrawable(R.drawable.btn_shap_gray));
		} else {
			view.findViewById(R.id.confirm).setBackground(getResources().getDrawable(R.drawable.btn_shap_blue));
		}

		String code = MMKV.defaultMMKV().decodeString(meeting.getId());
		if (TextUtils.isEmpty(code)) {
			codeEdit.setText("");
		} else {
			codeEdit.setText(code);
			codeEdit.setSelection(code.length());
		}
		//需要录制
		if (meeting.getIsRecord() == 1) {
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
					params.put("meetingId", meeting.getId());
					params.put("token", codeEdit.getText().toString().trim());
					if (mPresenter != null) {
						mPresenter.verifyRole(params, meeting, codeEdit.getText().toString().trim());
					}
				} else {
					codeEdit.setError("会议加入码不能为空");
				}
			}
		});
		audienceConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				JSONObject params = new JSONObject();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meeting.getId());
				params.put("token", "");
				if (mPresenter != null) {
					mPresenter.verifyRole(params, meeting, "");
				}
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = new Dialog(mContext, R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	public void showPhoneStatus(int i, final MeetingsData.DataBean.ListBean meeting, String isToken) {
		mPhoneStatusDialog = new AlertDialog(getOnAttachActivity())
				.builder()
				.setTitle("提示")
				.setMsg(i == 1 ? "检测到摄像头关闭 是否打开？" : "检测到麦克风关闭 是否打开？")
				.setNegativeButton("进入会议", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						try {
							if (Double.parseDouble(meeting.getIsToken()) == 1.0D) {//需要加入码
								if (getActivity() != null) {
									JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) getActivity(), meeting.getId());
								}
							} else {
								if (getActivity() != null) {
									JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) getActivity(), meeting.getId());
								}
							}
						} catch (Exception e) {
							if (getActivity() != null) {
								JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) getActivity(), meeting.getId());
							}
						}
					}
				})
				.setPositiveButton("去设置", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						launchActivity(new Intent(getOnAttachActivity(), MeetingSettingActivity.class));
					}
				});
		mPhoneStatusDialog.show();
	}

	@Override
	public void cancleAdvance(int position, boolean isSuccess) {
		if (position == -1) {
			return;
		}
		if (isSuccess) {
			if (list.size() > position) {
				list.get(position).setIsCollection(0);

			}

		} else {
			if (list.size() > position) {
				list.get(position).setIsCollection(1);
			}
		}
		mAdapter.notifyItemChanged(position);
	}

	@Override
	public void showRedDot(boolean isShow, int count) {
		Timber.e("isShow:---->%s   count:------>%s", isShow, count);
		if (redDot != null) {
			redDot.setVisibility(isShow ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void isShowMarqueeView(boolean isShow) {
		marqueeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	@Override
	public void orderMeetingResult(boolean isSuccess, int position) {
		if (list.size() > position) {
			if (!isSuccess) {
				list.get(position).setIsCollection(0);
				mAdapter.notifyItemChanged(position);
			}

			RxBus.sendMessage(new LikeEvent(true));
		}
	}

	@Override
	public void onDestroy() {
		if (dialog != null) {
			dialog.dismiss();
		}
		if (mPhoneStatusDialog != null) {
			mPhoneStatusDialog.dismiss();
		}
		super.onDestroy();
	}
}

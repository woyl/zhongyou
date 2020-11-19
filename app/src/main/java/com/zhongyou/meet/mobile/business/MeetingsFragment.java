package com.zhongyou.meet.mobile.business;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.elvishew.xlog.XLog;
import com.google.android.material.appbar.AppBarLayout;
import com.jess.arms.utils.RxBus;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.adapter.ForumMeetingAdapter;
import com.zhongyou.meet.mobile.business.adapter.GeneralAdapter;
import com.zhongyou.meet.mobile.business.adapter.MeetingAdapter;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.entities.ForumMeeting;
import com.zhongyou.meet.mobile.entities.MeeetingAdmin;
import com.zhongyou.meet.mobile.entities.Meeting;
import com.zhongyou.meet.mobile.entities.MeetingAdmin;
import com.zhongyou.meet.mobile.entities.MeetingJoin;
import com.zhongyou.meet.mobile.event.ForumSendEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.Logger;
import com.zhongyou.meet.mobile.utils.Login.LoginHelper;
import com.zhongyou.meet.mobile.utils.NetUtils;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.SpUtil;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.UIDUtil;
import com.zhongyou.meet.mobile.view.RoundRectImageView;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.bgabanner.BGABanner;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.MeetingInitActivity;
import rx.Subscription;

public class MeetingsFragment extends BaseFragment {

	private Subscription subscription;
	private SmartRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private LinearLayoutManager mLayoutManager;
	private MeetingAdapter meetingAdapter;
	private TextView tv_meeting_public, tv_meeting_private;
	private Dialog dialog;
	private ForumMeetingAdapter forumMeetingAdapter;
	private ArrayList<ForumMeeting> forumMeetingList = new ArrayList<>();

	private View v_public, v_invite;

	public static final int TYPE_PUBLIC_MEETING = 0;
	public static final int TYPE_PRIVATE_MEETING = 1;
	public static final int TYPE_OWNER_MEETING = 2;
	public static final int TYPE_FORUM_MEETING = 3;
	private final int SEARCH_REQUEST_CODE = 1001;
	private final int CODE_MEETING_LIST_REQUEST = 101;
	public static final String KEY_MEETING_TYPE = "meetingType";
	private int currentMeetingListPageIndex = TYPE_PUBLIC_MEETING;
	private TextView mEmptyView;
	private FrameLayout frameLayout;
	private com.elvishew.xlog.Logger mLogger;
	private int mJoinRole;
	private AppBarLayout mAppBarLayout;
	private BGABanner mBanner;

	@Override
	public String getStatisticsTag() {
		return "会议列表";
	}

	private MeetingAdapter.OnItemClickListener onMeetingListItemClickListener = new MeetingAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, Meeting meeting) {
			if (Build.VERSION.SDK_INT >= 23) {
				//视频会议拍照功能
				int REQUEST_CODE_CONTACT = 101;
				String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.CAMERA};
				//验证是否许可权限
				for (String str : permissions) {
					if (getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
						//申请权限
						getActivity().requestPermissions(permissions, REQUEST_CODE_CONTACT);
						return;
					}
				}
			}
			initDialog(meeting);
		}

		@Override
		public void onCollectionClick(int type, Meeting meeting, int position) {

		}
	};

	private void clearForumListUnReadInformationAndAtailFlag(ForumMeeting forumMeeting, ForumMeetingAdapter forumMeetingAdapter, int itemPosition) {
		forumMeeting.setNewMsgCnt(0);
		if (forumMeeting.isAtailFlag()) {
			forumMeeting.setAtailFlag(!forumMeeting.isAtailFlag());
		}
		forumMeetingAdapter.notifyItemChanged(itemPosition);
	}

	private void updateForumListUnReadInformationAndAtailFlag(ForumMeeting forumMeeting, ChatMesData.PageDataEntity entity, ForumMeetingAdapter forumMeetingAdapter) {
		forumMeeting.setNewMsgCnt(forumMeeting.getNewMsgCnt() + 1);
		//讨论区功能里区分用户的userId字段作用，等同于User实体类里的id字段。在微信授权进入app时，已进行存储。
		//找到userId是不是自己，已决定是否显示被@标示
		if (Preferences.getUserId().equals(entity.getAtailUserId())) {
			forumMeeting.setAtailFlag(!forumMeeting.isAtailFlag());
		}
		forumMeetingAdapter.notifyDataSetChanged();
	}

	private ForumMeetingAdapter.OnItemClickListener onForumMeetingItemClickListener = new ForumMeetingAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, ForumMeeting forumMeeting, int position) {
			clearForumListUnReadInformationAndAtailFlag(forumMeeting, forumMeetingAdapter, position);
//			startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("title", forumMeeting.getTitle()).putExtra("meetingId", forumMeeting.getMeetingId()).putExtra("num", forumMeeting.getUserCnt()));
		}
	};

	public static MeetingsFragment newInstance() {
		MeetingsFragment fragment = new MeetingsFragment();
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		forumMeetingAdapter = new ForumMeetingAdapter(mContext, onForumMeetingItemClickListener);
		forumMeetingAdapter.addData(forumMeetingList);

	/*	//设置会议tag
		showMeeting(currentMeetingListPageIndex);*/

		checkAdminAccount();

		subscription = RxBus.handleMessage(o -> {
			if (o instanceof ForumSendEvent) {
				ChatMesData.PageDataEntity entity = ((ForumSendEvent) o).getEntity();
				for (ForumMeeting forumMeeting : forumMeetingList) {
					if (entity.getMeetingId().equals(forumMeeting.getMeetingId())) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								updateForumListUnReadInformationAndAtailFlag(forumMeeting, entity, forumMeetingAdapter);
							}
						});
					}
				}
			}
		});
		mLogger = XLog.tag(TAG)
				.t()
				.st(2)
				.b()
				.build();

		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 检测当前账户是否为会议管理员。如是显示"会议管理"功能
	 */
	private void checkAdminAccount() {
		apiClient.requestMeetingAdmin(this, requestMeetingAdminCallback);
	}

	private OkHttpCallback<Bucket<MeeetingAdmin>> requestMeetingAdminCallback = new OkHttpCallback<Bucket<MeeetingAdmin>>() {
		@Override
		public void onSuccess(Bucket<MeeetingAdmin> entity) {
			MeeetingAdmin meeetingAdmin = entity.getData();
			if (meeetingAdmin.isMeetingAdmin()) {
				showOwnerMeeting(meeetingAdmin.getMeetingMgrUrl());

			} else {
				hideOwnerMeeting();

			}
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			hideOwnerMeeting();
		}
	};


	private void showOwnerMeeting(String meetingMgrUrl) {
		frameLayout.setVisibility(View.GONE);

	}

	private void hideOwnerMeeting() {
		frameLayout.setVisibility(View.GONE);

	}

	@Override
	public void onDestroyView() {
		subscription.unsubscribe();
		super.onDestroyView();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.meeting_fragment, null, false);
		swipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout);
		swipeRefreshLayout.setRefreshHeader(new MaterialHeader(BaseApplication.getInstance()));
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(@NonNull RefreshLayout refreshLayout) {
				currentPage = 1;
				showMeeting(currentMeetingListPageIndex);
			}
		});

		swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
				currentPage++;
				showMeeting(currentMeetingListPageIndex);
			}
		});

		swipeRefreshLayout.autoRefresh();

		view.findViewById(R.id.txt_search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent searchMeetingIntent = new Intent(mContext, MeetingSearchActivity.class);
				searchMeetingIntent.putExtra(KEY_MEETING_TYPE, currentMeetingListPageIndex);
				MeetingsFragment.this.startActivityForResult(searchMeetingIntent, SEARCH_REQUEST_CODE);


			}
		});
		mBanner = view.findViewById(R.id.banner_guide_content);
		mAppBarLayout = view.findViewById(R.id.appBarLayout);
		recyclerView = view.findViewById(R.id.mRecyclerView);
		mLayoutManager = new LinearLayoutManager(mContext);
		mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(mLayoutManager);

		// 设置ItemAnimator
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setHasFixedSize(true);
		mEmptyView = view.findViewById(R.id.emptyView);

		tv_meeting_public = view.findViewById(R.id.tv_meet_public);
		tv_meeting_private = view.findViewById(R.id.tv_meet_invite);
		tv_meeting_public.setOnClickListener(tvMeetingOnClickListener);
		tv_meeting_private.setOnClickListener(tvMeetingOnClickListener);

		v_public = view.findViewById(R.id.v_public);
		v_invite = view.findViewById(R.id.v_invite);

		frameLayout = view.findViewById(R.id.start_meeting);
		frameLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ApiClient.getInstance().meetingAdmin(meetingAdminCallback);
			}
		});

		mBanner.setAdapter(new BGABanner.Adapter<RoundRectImageView, String>() {
			@Override
			public void fillBannerItem(BGABanner banner, RoundRectImageView itemView, String model, int position) {
				Glide.with(getActivity())
						.load(model)
						.centerCrop()
						.dontAnimate()
						.into(itemView);
			}
		});
		mBanner.setData(R.layout.item_localimage, Arrays.asList("https://inews.gtimg.com/newsapp_bt/0/11503675620/1000",
				"https://inews.gtimg.com/newsapp_bt/0/11503325278/1000"
				, "https://inews.gtimg.com/newsapp_bt/0/11498943583/1000")
				, null);


		return view;
	}

	private OkHttpCallback meetingAdminCallback = new OkHttpCallback<Bucket<MeetingAdmin>>() {

		@Override
		public void onSuccess(Bucket<MeetingAdmin> meetingAdminBucket) {
			MeetingAdmin meetingAdmin = meetingAdminBucket.getData();
//            if (meetingAdmin.isMeetingAdmin()) {
//                startActivity(new Intent(HomeShoperActivity.this, MeetingAdminActivity.class).putExtra("meeting_mgr_url", meetingAdmin.getMeetingMgrUrl()));
//            } else {
			startActivityForResult(new Intent(getContext(), MeetingPublishActivity.class).putExtra("meeting_public_url", meetingAdmin.getPublishMeetingUrl()), CODE_MEETING_LIST_REQUEST);
//            }
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case SEARCH_REQUEST_CODE:
				showMeeting(resultCode);
				break;
			case CODE_MEETING_LIST_REQUEST:
				if (resultCode == TYPE_FORUM_MEETING) {
					startForumActivity();
				} else {
					showMeeting(resultCode);
				}
				break;
			default:
				Logger.v("MeetingFragments", "onActivityResult has no requestCode: " + requestCode);
				break;
		}
	}

	private void startForumActivity() {
		startActivity(new Intent(mContext, ForumActivity.class));
	}

	private View.OnClickListener tvMeetingOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_meet_public:
					currentPage = 1;
					showMeeting(TYPE_PUBLIC_MEETING);
					v_invite.setVisibility(View.GONE);
					v_public.setVisibility(View.VISIBLE);
					break;
				case R.id.tv_meet_invite:
					currentPage = 1;
					showMeeting(TYPE_PRIVATE_MEETING);
					v_invite.setVisibility(View.VISIBLE);
					v_public.setVisibility(View.GONE);
					break;
				//进入讨论区
				case R.id.emptyView:
					startForumActivity();
					break;
				default:
					Logger.v("MeetingFragments", "onClick has no views`id");
					break;
			}
		}
	};

	private void showMeeting(int type) {
		//停止一切动画效果，包括recyclerView滚动效果，让appBarLayout常显，让刷新功能生效
		swipeRefreshLayout.setEnabled(true);
		recyclerView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));

		switch (type) {
			case TYPE_PUBLIC_MEETING:
				showPublicMeeting();
				break;
			case TYPE_PRIVATE_MEETING:
				showPrivateMeeting();
				break;
			default:
				Logger.v("MeetdingsFragment", "showMeeting has no current type:" + type);
				break;
		}
	}

	private void showPublicMeeting() {
		TextViewCompat.setTextAppearance(tv_meeting_public, R.style.MeetingTypeFocus);
		TextViewCompat.setTextAppearance(tv_meeting_private, R.style.MeetingTypeUnFocus);
		currentMeetingListPageIndex = TYPE_PUBLIC_MEETING;
		requestMeetings(TYPE_PUBLIC_MEETING);
	}

	private void showPrivateMeeting() {
		TextViewCompat.setTextAppearance(tv_meeting_public, R.style.MeetingTypeUnFocus);
		TextViewCompat.setTextAppearance(tv_meeting_private, R.style.MeetingTypeFocus);
		currentMeetingListPageIndex = TYPE_PRIVATE_MEETING;
		requestMeetings(TYPE_PRIVATE_MEETING);
	}


	/**
	 * 请求会议类型
	 *
	 * @param type
	 */
	private int currentPage = 1;

	private void requestMeetings(int type) {
		apiClient.getAllMeeting(TAG, null, type, currentPage, meetingsCallback);
		if (!NetUtils.isNetworkConnected(getActivity() == null ? BaseApplication.getInstance() : getActivity())) {
			swipeRefreshLayout.finishLoadMore();
			swipeRefreshLayout.finishRefresh();
		}
	}

	private GeneralAdapter mGeneralAdapter;
	private OkHttpCallback meetingsCallback = new OkHttpCallback<JSONObject>() {

		@Override
		public void onSuccess(final JSONObject meetingBucket) {
			com.orhanobut.logger.Logger.e(meetingBucket.toJSONString());
			if (meetingBucket.getInteger("errcode") == 40001) {
				ToastUtils.showToast("登陆信息已失效 请重新登陆");
				LoginHelper.logoutCustom(getActivity() == null ? BaseApplication.getInstance() : getActivity());
				startActivity(new Intent(getActivity(), WXEntryActivity.class));
				if (getActivity() != null) {
					getActivity().finish();
				}
				return;
			}

			JSONArray jsonArray = meetingBucket.getJSONObject("data").getJSONArray("list");
			List<Meeting> meetings = jsonArray.toJavaList(Meeting.class);
			if (meetingBucket.getJSONObject("data").getInteger("totalPage") <= currentPage) {
				swipeRefreshLayout.setEnableLoadMore(false);
			} else {
				swipeRefreshLayout.setEnableLoadMore(true);
			}

			if (currentPage == 1) {
				meetingAdapter = null;
			}

			if (currentPage == 1 && meetings.size() <= 0) {
				loadForumListFaildView();
			}
			/*if (meetings.size() > 0) {*/
			if (meetingAdapter == null) {
				meetingAdapter = new MeetingAdapter(mContext, meetings, onMeetingListItemClickListener);
				mGeneralAdapter = new GeneralAdapter(meetingAdapter);
				recyclerView.setAdapter(mGeneralAdapter);
			} else {
				meetingAdapter.notifyDataSetChanged(meetings);
			}

			if (meetingAdapter.getItemCount() <= 0) {
				loadForumListFaildView();
			} else {
				loadForumListSuccessView();
			}

			/*} else {
				loadForumListFaildView();
			}*/
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
//			loadForumListFaildView();
			ToastUtils.showToast("网络连接失败 请重试");
//			Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFinish() {
			super.onFinish();
			swipeRefreshLayout.finishRefresh();
			swipeRefreshLayout.finishLoadMore();
		}
	};

	private void loadForumListSuccessView() {
		recyclerView.setVisibility(View.VISIBLE);
		mEmptyView.setVisibility(View.GONE);
	}

	private void loadForumListFaildView() {
		recyclerView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
	}

	private void initDialog(final Meeting meeting) {
		View view = View.inflate(mContext, R.layout.dialog_meeting_input_code, null);
	/*	if (meeting.getScreenshotFrequency() == Meeting.SCREENSHOTFREQUENCY_INVALID) {
			view.findViewById(R.id.dialog_meeting_warnning).setVisibility(View.GONE);
			view.findViewById(R.id.dialog_meeting_warnning_text).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.dialog_meeting_warnning).setVisibility(View.VISIBLE);
			view.findViewById(R.id.dialog_meeting_warnning_text).setVisibility(View.VISIBLE);
		}*/
		final EditText codeEdit = view.findViewById(R.id.code);
		if (!SpUtil.getString(meeting.getId(), "").equals("")) {
			codeEdit.setText(SpUtil.getString(meeting.getId(), ""));
		} else {
			codeEdit.setText("");
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
					enterMeeting(codeEdit.getText().toString(), meeting);
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
		dialog = new Dialog(mContext, R.style.CustomDialog);
		dialog.setContentView(view);
		dialog.show();
	}

	/**
	 * 进入会议直播间
	 *
	 * @param joinCode 会议加入码
	 * @param meeting  会议
	 */
	private void enterMeeting(String joinCode, Meeting meeting) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
		params.put("meetingId", meeting.getId());
		params.put("token", joinCode);
		apiClient.verifyRole(TAG, verifyRoleCallback(meeting, joinCode), params);
	}

	private OkHttpCallback verifyRoleCallback(final Meeting meeting, final String token) {
		return new OkHttpCallback<Bucket<MeetingJoin>>() {

			@Override
			public void onSuccess(Bucket<MeetingJoin> meetingJoinBucket) {
				SpUtil.put(meeting.getId(), token.trim());
				mLogger.e(JSONObject.toJSONString(meetingJoinBucket));

				int profileIndex = 0;
				if (meeting.getResolvingPower() == 1.0) {
					profileIndex = 0;
				} else if (meeting.getResolvingPower() == 2.0) {
					profileIndex = 1;
				} else if (meeting.getResolvingPower() == 3.0) {
					profileIndex = 2;
				}

				mLogger.e("profileIndex:  " + profileIndex);

				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
				editor.apply();

				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
				params.put("meetingId", meeting.getId());
				params.put("token", token);
				apiClient.joinMeeting(TAG, joinMeetingCallback, params);
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
			}
		};
	}

	private OkHttpCallback joinMeetingCallback = new OkHttpCallback<Bucket<MeetingJoin>>() {

		@Override
		public void onSuccess(Bucket<MeetingJoin> meetingJoinBucket) {
			mLogger.e(JSONObject.toJSONString(meetingJoinBucket));
			MeetingJoin meetingJoin = meetingJoinBucket.getData();
			if (meetingJoin == null || meetingJoin.getMeeting() == null || meetingJoin.getMeeting().getId() == null) {
				ToastUtils.showToast("出错了  请重试");
				return;
			}
			Map<String, String> params = new HashMap<String, String>();
			params.put("channel", meetingJoin.getMeeting().getId());
			params.put("account", UIDUtil.generatorUID(Preferences.getUserId()));
			mJoinRole = meetingJoin.getRole();
			if (mJoinRole == 0) {
				params.put("role", "Publisher");
			} else if (mJoinRole == 1) {
				params.put("role", "Publisher");
			} else if (mJoinRole == 2) {
				params.put("role", "Subscriber");
			}
			com.orhanobut.logger.Logger.e("meetingJoin11111-->"+JSON.toJSONString(meetingJoin));
			apiClient.getAgoraKey(mContext, params, getAgoraCallback(meetingJoin));
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			if (errorCode != -1) {
				Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	};

	private OkHttpCallback getAgoraCallback(final MeetingJoin meetingJoin) {
		return new OkHttpCallback<Bucket<Agora>>() {

			@Override
			public void onSuccess(Bucket<Agora> agoraBucket) {
				com.orhanobut.logger.Logger.e(JSON.toJSONString(agoraBucket));
				dialog.dismiss();
				Intent intent = new Intent(mContext, MeetingInitActivity.class);
				intent.putExtra("agora", agoraBucket.getData());
				intent.putExtra("meeting", meetingJoin);
				intent.putExtra("role", mJoinRole);
				com.orhanobut.logger.Logger.e("meetingJoin-->"+JSON.toJSONString(meetingJoin));
				if (isAdded()) {
					startActivity(intent);
				} else {
					BaseApplication.getInstance().startActivity(intent);
				}
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				if (errorCode != -1) {
					Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}

		};
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
//        //停止翻页

	}
}

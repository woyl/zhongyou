package com.zhongyou.meet.mobile.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jess.arms.integration.AppManager;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.business.adapter.ForumMeetingAdapter;
import com.zhongyou.meet.mobile.business.adapter.GeneralAdapter;
import com.zhongyou.meet.mobile.business.adapter.MeetingAdapter;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.Meeting;
import com.zhongyou.meet.mobile.entities.MeetingJoin;
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.SpUtil;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.UIDUtil;
import com.zhongyou.meet.mobile.view.MeetingSpaceItemDecoration;
import com.zhongyou.meet.mobile.view.SpaceItemDecoration;

import java.util.HashMap;
import java.util.List;

import io.agora.openlive.ui.MeetingInitActivity;
import io.reactivex.disposables.Disposable;
import me.jessyan.autosize.utils.AutoSizeUtils;

public class MeetingSearchActivity extends BasicActivity {

	private SmartRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private EditText searchEdit;
	private TextView cancelText;
	private LinearLayoutManager mLayoutManager;
	private MeetingAdapter meetingAdapter;
	private TextView emptyText;
	//会议类型
	private int meetingType;
	private ForumMeetingAdapter forumMeetingAdapter;
	private String mKeyWords;
	private int mMJoinRole;
	private AlertDialog mPhoneStatusDialog;

	@Override
	public String getStatisticsTag() {
		return "会议搜索列表";
	}

	private MeetingAdapter.OnItemClickListener onItemClickListener = new MeetingAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, Meeting meeting) {
			if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
				showPhoneStatus(1, meeting);
				return;
			}
			if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
				showPhoneStatus(2, meeting);
				return;
			}
			try {
				if (Double.parseDouble(String.valueOf(meeting.getIsToken())) == 1.0D) {//需要加入码的
					JoinMeetingDialog.INSTANCE.showNeedCodeDialog(MeetingSearchActivity.this, meeting.getId());

				} else {
					JoinMeetingDialog.INSTANCE.showNoCodeDialog(MeetingSearchActivity.this, meeting.getId());
				}
			} catch (Exception e) {

				JoinMeetingDialog.INSTANCE.showNeedCodeDialog(MeetingSearchActivity.this, meeting.getId());
			}
		}

		@Override
		public void onCollectionClick(int type, Meeting meeting, int position) {
			if (type == 0) {
				HttpsRequest.provideClientApi().cancelAdvance(meeting.getId()).compose(RxSchedulersHelper.io_main())
						.subscribe(new RxSubscriber<JSONObject>() {


							@Override
							public void _onNext(JSONObject jsonObject) {
								if (jsonObject.getInteger("errcode") != 0) {
									ToastUtils.showToast(mContext, jsonObject.getString("errmsg"));
									meeting.setIsCollection(1);
								} else {
									meeting.setIsCollection(0);
								}
								mGeneralAdapter.notifyItemChanged(position);
							}
						});

			} else if (type == 1) {
				HttpsRequest.provideClientApi().meetingReserve(meeting.getId()).compose(RxSchedulersHelper.io_main())
						.subscribe(new RxSubscriber<JSONObject>() {

							@Override
							public void _onNext(JSONObject jsonObject) {
								if (jsonObject.getInteger("errcode") != 0) {
									ToastUtils.showToast(mContext, jsonObject.getString("errmsg"));
									meeting.setIsCollection(0);
								} else {
									meeting.setIsCollection(1);
								}
								mGeneralAdapter.notifyItemChanged(position);
							}
						});
			}
		}
	};

	private ForumMeetingAdapter.OnItemClickListener onForumMeetingItemClickListener = (view, forumMeeting, position) -> {
		forumMeeting.setNewMsgCnt(0);
		if (forumMeeting.isAtailFlag()) {
			forumMeeting.setAtailFlag(!forumMeeting.isAtailFlag());
		}
		forumMeetingAdapter.notifyItemChanged(position);
//		startActivity(new Intent(getApplication(), ChatActivity.class).putExtra("title", forumMeeting.getTitle()).putExtra("meetingId", forumMeeting.getMeetingId()).putExtra("num", forumMeeting.getUserCnt()));
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_search);
		initView();
		initData();
	}

	private void initView() {
		swipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);
		setTitle("教室搜索");
		swipeRefreshLayout.setRefreshHeader(new MaterialHeader(this));
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(@NonNull RefreshLayout refreshLayout) {
				mKeyWords = searchEdit.getText().toString();
				if (TextUtils.isEmpty(mKeyWords)) {
					Toast.makeText(mContext, "搜索会议名称不能为空", Toast.LENGTH_SHORT).show();
				} else {
					currentPage = 1;
					mKeyWords = searchEdit.getText().toString();
					requestMeetings(mKeyWords, meetingType);
				}
			}
		});


		swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
				currentPage++;
				requestMeetings(mKeyWords, meetingType);
			}
		});

		searchEdit = findViewById(R.id.search_text);
		searchEdit.setFocusable(true);
		searchEdit.requestFocus();
		cancelText = findViewById(R.id.cancel);

		cancelText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
					if (TextUtils.isEmpty(searchEdit.getText())) {
						Toast.makeText(mContext, "搜索会议名称不能为空", Toast.LENGTH_SHORT).show();
					} else {
						swipeRefreshLayout.autoRefresh();
//						requestMeetings(searchEdit.getText().toString(), meetingType);
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
					}
					return true;
				} else {
					return false;
				}
			}
		});

		searchEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (TextUtils.isEmpty(s)) {
					cancelText.setVisibility(View.GONE);
				} else {
					cancelText.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		recyclerView = findViewById(R.id.mRecyclerView);
		mLayoutManager = new LinearLayoutManager(mContext);
		mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.addItemDecoration(new MeetingSpaceItemDecoration(0, AutoSizeUtils.pt2px(this, 10), 0, 0));
		// 设置ItemAnimator
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
		emptyText = findViewById(R.id.emptyView);


	}

	private void initData() {
		meetingType = getIntent().getIntExtra(MeetingsFragment.KEY_MEETING_TYPE, MeetingsFragment.TYPE_PUBLIC_MEETING);
		setResult(meetingType);
	}

	private int currentPage = 1;

	private void requestMeetings(String meetingTitle, int meetingType) {
		//type此时无用 因为要所有所有相关的会议 包括受邀会议 公开会议
		apiClient.getAllMeeting(TAG, meetingTitle, 0, currentPage, meetingsCallback);


	}

	private GeneralAdapter mGeneralAdapter;
	private OkHttpCallback meetingsCallback = new OkHttpCallback<JSONObject>() {

		@Override
		public void onSuccess(final JSONObject meetingBucket) {


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
			if (meetings.size() > 0) {
				if (meetingAdapter == null) {
					meetingAdapter = new MeetingAdapter(mContext, meetings, onItemClickListener);
					mGeneralAdapter = new GeneralAdapter(meetingAdapter);
					recyclerView.setAdapter(mGeneralAdapter);

				} else {
					meetingAdapter.notifyDataSetChanged(meetings);
				}
			} else {
				recyclerView.setVisibility(View.GONE);
				emptyText.setVisibility(View.VISIBLE);
			}

		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
//            Toasty.error(mContext, exception.getMessage(), Toast.LENGTH_SHORT, true).show();
		}

		@Override
		public void onFinish() {
			super.onFinish();
			swipeRefreshLayout.finishRefresh();
			swipeRefreshLayout.finishLoadMore();
		}
	};


	private Dialog dialog;



	public void showPhoneStatus(int i, Meeting meeting) {
		mPhoneStatusDialog = new AlertDialog(this)
				.builder()
				.setTitle("提示")
				.setMsg(i == 1 ? "检测到摄像头关闭 是否打开？" : "检测到麦克风关闭 是否打开？")
				.setNegativeButton("进入会议", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						try {
							if (Double.parseDouble(String.valueOf(meeting.getIsToken())) == 1.0D) {//需要加入码的
								JoinMeetingDialog.INSTANCE.showNeedCodeDialog(MeetingSearchActivity.this, meeting.getId());

							} else {
								JoinMeetingDialog.INSTANCE.showNoCodeDialog(MeetingSearchActivity.this, meeting.getId());
							}
						} catch (Exception e) {

							JoinMeetingDialog.INSTANCE.showNeedCodeDialog(MeetingSearchActivity.this, meeting.getId());
						}
					}
				})
				.setPositiveButton("去设置", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPhoneStatusDialog.dismiss();
						startActivity(new Intent(MeetingSearchActivity.this, MeetingSettingActivity.class));
					}
				});
		mPhoneStatusDialog.show();
	}









}

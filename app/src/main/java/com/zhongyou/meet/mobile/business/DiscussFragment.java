package com.zhongyou.meet.mobile.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.network.NetworkUtil;
import com.zhongyou.meet.mobile.business.adapter.ForumMeetingAdapter;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.entities.ForumMeeting;
import com.zhongyou.meet.mobile.entities.PaginationData;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.NetUtils;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

import static com.zhongyou.meet.mobile.business.MeetingsFragment.KEY_MEETING_TYPE;

/**
 * @author luopan@centerm.com
 * @date 2019-10-09 17:05.
 * 讨论界面
 */
public class DiscussFragment extends BaseFragment {


	public String TAG = getClass().getSimpleName();
	private RecyclerView recyclerView;
	private SmartRefreshLayout swipeRefreshLayout;
	private TextView emptyText;


	private ApiClient apiClient;
	private LinearLayoutManager mLayoutManager;
	private ForumMeetingAdapter forumMeetingAdapter;
	private PaginationData<ForumMeeting> paginationData;

	//分页信息
	private final int PAGE_SIZE = 20;
	private final int PAGE_NO = 1;
	private int pageNo = PAGE_NO;
	private TextView mSearchView;
	private Subscriber<Integer> mSubscriber;

	@Override
	public String getStatisticsTag() {
		return "讨论";
	}

	public static DiscussFragment newInstance() {
		return new DiscussFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.e("DiscussFragment  onCreateView ");
		View view = inflater.inflate(R.layout.fragment_discuss, null, false);
		emptyText = view.findViewById(R.id.tv_forum_empty);
		swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_forum);
		swipeRefreshLayout.setRefreshHeader(new MaterialHeader(BaseApplication.getInstance()));
		recyclerView = view.findViewById(R.id.recyclerView_forum);
		mSearchView = view.findViewById(R.id.input_keyword);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		apiClient = ApiClient.getInstance();
		forumMeetingAdapter = new ForumMeetingAdapter(DiscussFragment.this.getActivity(), onForumMeetingItemClickListener);
		mLayoutManager = new LinearLayoutManager(DiscussFragment.this.getActivity());
		mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(mLayoutManager);
		// 设置ItemAnimator
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setHasFixedSize(true);

		initControl();
		showMeeting();
	}



	private void initControl() {


		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(@NonNull RefreshLayout refreshLayout) {
				if (NetUtils.isNetworkConnected(getActivity())) {
					showMeeting();
				} else {
					ToastUtils.showToast("当前网络不用 请检查网络再试");
					swipeRefreshLayout.finishRefresh();
				}
			}
		});

		swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
				if (NetUtils.isNetworkConnected(getActivity())) {
					if (nextPage()) {
						requestForum(null);
					}
				} else {
					ToastUtils.showToast("当前网络不用 请检查网络再试");
					swipeRefreshLayout.finishLoadMore();
				}
			}
		});


		/*subscription = RxBus.handleMessage(o -> {
			if (o instanceof ForumSendEvent) {
				*//*ChatMesData.PageDataEntity entity = ((ForumSendEvent) o).getEntity();
				Observable observable = Observable.create(new Observable.OnSubscribe<ForumMeeting>() {

					@Override
					public void call(Subscriber<? super ForumMeeting> subscriber) {
//						subscriber.onNext("Hello，RxJava");
						if (entity.getUserId().equals(Preferences.getUserId())) {
							return;
						}
						for (ForumMeeting forumMeeting : forumMeetingAdapter.getData()) {
							if (entity.getMeetingId().equals(forumMeeting.getMeetingId())) {
								subscriber.onNext(forumMeeting);
								subscriber.onCompleted();
							}
						}

					}
				});
				Subscriber<ForumMeeting> subscriber = new Subscriber<ForumMeeting>() {
					@Override
					public void onNext(ForumMeeting forumMeeting) {
						updateForumListUnReadInformationAndAtailFlag(forumMeeting, entity, forumMeetingAdapter);

					}

					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
					}
				};

				observable
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(subscriber);*//*


			}
		});*/

		mSearchView.setOnClickListener(v -> {
			Intent searchMeetingIntent = new Intent(DiscussFragment.this.getContext(), MeetingSearchActivity.class);
			searchMeetingIntent.putExtra(KEY_MEETING_TYPE, MeetingsFragment.TYPE_PUBLIC_MEETING);
			startActivity(searchMeetingIntent);

		});
	}

	private void showMeeting() {
		//停止一切动画效果，包括recyclerView滚动效果，让appBarLayout常显，让刷新功能生效
		swipeRefreshLayout.setEnabled(true);
		recyclerView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
		showForumMeeting();
	}

	private void showForumMeeting() {
		initForumPage();
		requestForum(null);
	}

	private void initForumPage() {
		pageNo = PAGE_NO;
		forumMeetingAdapter.clearData();
		recyclerView.setAdapter(forumMeetingAdapter);
	}


	private ForumMeetingAdapter.OnItemClickListener onForumMeetingItemClickListener = new ForumMeetingAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, ForumMeeting forumMeeting, int position) {
			if (!NetworkUtil.isAvailable(getActivity())) {
				ToastUtils.showToast("当前网络不可用 请检查网络");
				return;
			}
			clearForumListUnReadInformationAndAtailFlag(forumMeeting, forumMeetingAdapter, position);
//			startActivity(new Intent(DiscussFragment.this.getActivity(), ChatActivity.class).putExtra("title", forumMeeting.getTitle()).putExtra("meetingId", forumMeeting.getMeetingId()).putExtra("num", forumMeeting.getUserCnt()));
		}
	};

	private void clearForumListUnReadInformationAndAtailFlag(ForumMeeting forumMeeting, ForumMeetingAdapter forumMeetingAdapter, int itemPosition) {
		forumMeeting.setNewMsgCnt(0);
		if (forumMeeting.isAtailFlag()) {
			forumMeeting.setAtailFlag(false);
		}
		forumMeetingAdapter.notifyItemChanged(itemPosition);
	}

	/**
	 * 请求讨论组会议数据
	 *
	 * @param title
	 */
	private void requestForum(String title) {
		if (title == null || title.equals("")) {
			title = null;
		}

		Map<String, String> params = new HashMap<>();
		params.put(ApiClient.PAGE_NO, String.valueOf(pageNo));
		params.put(ApiClient.PAGE_SIZE, String.valueOf(PAGE_SIZE));
		if (title != null) {
			params.put("title", title);
		}
		apiClient.getAllForumMeeting(TAG, params, forumMeetingsCallback);
	}

	private OkHttpCallback forumMeetingsCallback = new OkHttpCallback<Bucket<PaginationData<ForumMeeting>>>() {
		@Override
		public void onSuccess(Bucket<PaginationData<ForumMeeting>> forumMeetingBucket) {
			paginationData = forumMeetingBucket.getData();
			Logger.e(paginationData.toString());

			ArrayList<ForumMeeting> forumMeetingList = paginationData.getPageData();
			if (forumMeetingList.size() == 0) {
				recyclerView.setVisibility(View.GONE);
				emptyText.setVisibility(View.VISIBLE);
				return;
			}
			recyclerView.setVisibility(View.VISIBLE);
			emptyText.setVisibility(View.GONE);

			forumMeetingAdapter.addData(forumMeetingList);
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			ZYAgent.onEvent(DiscussFragment.this.getActivity(), exception.getMessage());
			ToastUtils.showToast("请求讨论区数据失败，请重试");
		}

		@Override
		public void onFinish() {
			super.onFinish();
			swipeRefreshLayout.finishRefresh();
			swipeRefreshLayout.finishLoadMore();
		}
	};


	private void updateForumListUnReadInformationAndAtailFlag(ForumMeeting forumMeeting, ChatMesData.PageDataEntity entity, ForumMeetingAdapter forumMeetingAdapter) {
		forumMeeting.setNewMsgCnt(forumMeeting.getNewMsgCnt() + 1);
		//讨论区功能里区分用户的userId字段作用，等同于User实体类里的id字段。在微信授权进入app时，已进行存储。
		//找到userId是不是自己，已决定是否显示被@标示
		if (Preferences.getUserId().equals(entity.getAtailUserId())) {
			forumMeeting.setAtailFlag(true);
		}
		forumMeetingAdapter.notifyDataSetChanged();
	}


	private boolean nextPage() {
		if (pageNo >= paginationData.getTotalPage()) {
			ToastUtils.showToast("已经到底了！");
			swipeRefreshLayout.finishLoadMore();
			swipeRefreshLayout.setEnableLoadMore(false);
			return false;
		}
		swipeRefreshLayout.setEnableLoadMore(true);
		pageNo += PAGE_NO;
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}

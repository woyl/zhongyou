package com.zhongyou.meet.mobile.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.adapter.ForumMeetingAdapter;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.entities.ForumMeeting;
import com.zhongyou.meet.mobile.entities.PaginationData;
import com.zhongyou.meet.mobile.event.ForumSendEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.listener.RecyclerViewScrollListener;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

import static com.zhongyou.meet.mobile.business.MeetingsFragment.KEY_MEETING_TYPE;

/**
 * @author Dongce
 * create time: 2018/12/19
 * 讨论的界面
 */
public class ForumActivity extends AppCompatActivity {

    public String TAG = getClass().getSimpleName();
    private Subscription subscription;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppBarLayout appBarLayout;
    private TextView emptyText;
    private ImageView titleForumBack;

    private ApiClient apiClient;
    private LinearLayoutManager mLayoutManager;
    private ForumMeetingAdapter forumMeetingAdapter;
    private PaginationData<ForumMeeting> paginationData;
    //分页信息
    private final int PAGE_SIZE = 20;
    private final int PAGE_NO = 1;
    private int pageNo = PAGE_NO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        initView();
        initData();
        initControl();
        showMeeting();
    }

    private void initForumPage() {
        pageNo = PAGE_NO;
        forumMeetingAdapter.clearData();
        recyclerView.setAdapter(forumMeetingAdapter);
    }

    private boolean nextPage() {
        if (pageNo >= paginationData.getTotalPage()) {
            ToastUtils.showToast("已经到底了！");
            return false;
        }
        pageNo += PAGE_NO;
        return true;
    }

    private void initView() {
        emptyText = findViewById(R.id.tv_forum_empty);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_forum);
        recyclerView = findViewById(R.id.recyclerView_forum);
        appBarLayout = findViewById(R.id.appBarLayout_forum);
        titleForumBack = findViewById(R.id.title_forum_back);
    }

    private void initData() {
        apiClient = ApiClient.getInstance();
        forumMeetingAdapter = new ForumMeetingAdapter(ForumActivity.this, onForumMeetingItemClickListener);
        mLayoutManager = new LinearLayoutManager(ForumActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        // 设置ItemAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

    }

    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(() -> showMeeting());
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset >= 0) {
                swipeRefreshLayout.setEnabled(true);
            } else {
                swipeRefreshLayout.setEnabled(false);
            }
        });
        recyclerView.addOnScrollListener(recyclerViewScrollListener);
        subscription = RxBus.handleMessage(o -> {
            if (o instanceof ForumSendEvent) {
                ChatMesData.PageDataEntity entity = ((ForumSendEvent) o).getEntity();
                for (ForumMeeting forumMeeting : forumMeetingAdapter.getData()) {
                    if (entity.getMeetingId().equals(forumMeeting.getMeetingId())) {
                        runOnUiThread(() -> updateForumListUnReadInformationAndAtailFlag(forumMeeting, entity, forumMeetingAdapter));
                    }
                }
            }
        });
        titleForumBack.setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.fl_forum_searchText).setOnClickListener(v -> {
            Intent searchMeetingIntent = new Intent(ForumActivity.this, MeetingSearchActivity.class);
            searchMeetingIntent.putExtra(KEY_MEETING_TYPE, MeetingsFragment.TYPE_FORUM_MEETING);
            startActivity(searchMeetingIntent);
        });
    }

    private void updateForumListUnReadInformationAndAtailFlag(ForumMeeting forumMeeting, ChatMesData.PageDataEntity entity, ForumMeetingAdapter forumMeetingAdapter) {
        forumMeeting.setNewMsgCnt(forumMeeting.getNewMsgCnt() + 1);
        //讨论区功能里区分用户的userId字段作用，等同于User实体类里的id字段。在微信授权进入app时，已进行存储。
        //找到userId是不是自己，已决定是否显示被@标示
        if (Preferences.getUserId().equals(entity.getAtailUserId())) {
            forumMeeting.setAtailFlag(true);
        }
        forumMeetingAdapter.notifyDataSetChanged();
    }

    private void showMeeting() {
        //停止一切动画效果，包括recyclerView滚动效果，让appBarLayout常显，让刷新功能生效
        swipeRefreshLayout.setEnabled(true);
        recyclerView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
        appBarLayout.setExpanded(true, true);
        showForumMeeting();
    }

    private void showForumMeeting() {
        initForumPage();
        requestForum(null);
    }

    /**
     * 滑动到底部分页监听
     */
    private RecyclerViewScrollListener recyclerViewScrollListener = new RecyclerViewScrollListener() {
        @Override
        public void onScrollToBottom() {
            if (nextPage()) {
                requestForum(null);
            }
        }
    };

    private ForumMeetingAdapter.OnItemClickListener onForumMeetingItemClickListener = new ForumMeetingAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, ForumMeeting forumMeeting, int position) {
            clearForumListUnReadInformationAndAtailFlag(forumMeeting, forumMeetingAdapter, position);
//            startActivity(new Intent(ForumActivity.this, ChatActivity.class).putExtra("title", forumMeeting.getTitle()).putExtra("meetingId", forumMeeting.getMeetingId()).putExtra("num", forumMeeting.getUserCnt()));
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
        swipeRefreshLayout.setRefreshing(true);
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
            ZYAgent.onEvent(getApplicationContext(), exception.getMessage());
            ToastUtils.showToast("请求讨论区数据失败，请重试");
        }

        @Override
        public void onFinish() {
            super.onFinish();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    protected void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }
}

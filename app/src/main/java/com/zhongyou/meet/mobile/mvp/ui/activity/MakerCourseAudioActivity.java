package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxBus;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.core.GlobalConsts;
import com.zhongyou.meet.mobile.core.PlayService;
import com.zhongyou.meet.mobile.di.component.DaggerMakerCourseAudioComponent;
import com.zhongyou.meet.mobile.entiy.MoreAudio;
import com.zhongyou.meet.mobile.event.AdapaterRefushEvent;
import com.zhongyou.meet.mobile.event.IMMessgeEvent;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioContract;
import com.zhongyou.meet.mobile.mvp.presenter.MakerCourseAudioPresenter;
import com.zhongyou.meet.mobile.mvp.ui.fragment.NewMakerFragment;
import com.zhongyou.meet.mobile.utils.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.message.GroupNotificationMessage;
import rx.functions.Action1;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/08/2020 13:43
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "创客音频更多界面")
public class MakerCourseAudioActivity extends BaseActivity<MakerCourseAudioPresenter> implements MakerCourseAudioContract.View {

    @BindView(R.id.headerImage)
    ImageView headerImage;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_back)
    RelativeLayout toolbarBack;
    @BindView(R.id.emptyView)
    RelativeLayout emptyView;

    private BaseRecyclerViewAdapter<MoreAudio> mAdapter;
    private List<MoreAudio> mAudioList = new ArrayList<>();
    private Animation mOperatingAnim;
    private PlayService.MusicBinder mMusicBinder;
    private int mCurrentPage = 1;
    private ServiceConnection serviceConn;



    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMakerCourseAudioComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_maker_course_audio; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setTitle(getString(R.string.sb_says));

        initPlayService();
        if (mPresenter != null) {
            mPresenter.registerAudioReceiver();
        }

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.autoRefresh();

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mCurrentPage = 1;
                if (mPresenter != null) {
                    mPresenter.getMoreAudio(mCurrentPage);
                }

            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mCurrentPage++;
                    mPresenter.getMoreAudio(mCurrentPage);
                }
            }
        });


//        if (recyclerview.getItemAnimator() != null) {
//            ((SimpleItemAnimator) recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
//        }

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mOperatingAnim = AnimationUtils.loadAnimation(MakerCourseAudioActivity.this, R.anim.tip);
        Objects.requireNonNull(recyclerview.getItemAnimator()).setChangeDuration(0);
        mAudioList = new ArrayList<>();


        RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {

                if (o instanceof AdapaterRefushEvent) {
                    mAdapter.notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            if (mMusicBinder != null) {
                mPresenter.setCurrentPosition(mMusicBinder.getPosition());
                mPresenter.setOldPosition(mMusicBinder.getOldPosition());
                if (mMusicBinder.getOldPosition() != -1) {
                    mAdapter.notifyItemChanged(mMusicBinder.getOldPosition());
                }
                mAdapter.notifyItemChanged(mMusicBinder.getPosition());
            }
        }
    }

    private void initPlayService() {
        //播放服务
        Intent intent = new Intent(getActivity(), PlayService.class);
        serviceConn = new ServiceConnection() {
            //连接异常断开
            public void onServiceDisconnected(ComponentName name) {

            }

            //连接成功
            public void onServiceConnected(ComponentName name, IBinder binder) {
                mMusicBinder = (PlayService.MusicBinder) binder;
            }
        };

        bindService(intent, serviceConn, Service.BIND_AUTO_CREATE);
    }



    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.unregisterReceiver();
        }
    }

    @Override
    public void upDateAudioProgress(int position, long duration, long total) {
        mAudioList.get(position).setCurrentDuration(duration);
        mAudioList.get(position).setTotalTime((int) total);
        mAdapter.notifyItemChanged(position);

    }

    @Override
    public void pauseMusic(int position) {
        mAudioList.get(position).setType(2);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void startMusic(int position) {
        mAudioList.get(position).setType(1);
        mOperatingAnim.cancel();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void completeMusic(int position) {
		mAudioList.get(position).setType(0);
		mAudioList.get(position).setCurrentDuration(0);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public List<MoreAudio> getData() {
        return mAudioList == null ? new ArrayList<>() : mAudioList;
    }

    @Override
    public List<MoreAudio> getDataComplete(List<MoreAudio> dataLists) {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        if (dataLists == null || dataLists.isEmpty()) {
            refreshLayout.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return dataLists;
        } else {
            refreshLayout.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        mAudioList = dataLists;
        if (mPresenter != null) {
            if (mAdapter == null) {
                mAdapter = mPresenter.initAudioRecyclerAdapter(mMusicBinder, mOperatingAnim,this,mPresenter);
                recyclerview.setAdapter(mAdapter);
            } else {
                mAdapter.addNewData(mAudioList);
            }

        }
        return mAudioList;
    }

    @Override
    public void getHeaderImageResult(String picUrl) {
        Glide.with(this).asBitmap()
                .load(picUrl)
                .placeholder(R.drawable.item_meeting_bg_one)
                .error(R.drawable.item_meeting_bg_one)
                .into(headerImage);
    }

    @Override
    public Context getActivity() {
        return this;
    }

    @Override
    public void setTotalPage(int totalPage) {
        if (mCurrentPage >= totalPage) {
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
        }
    }


    @OnClick({R.id.toolbar_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                onBackPressed();
                break;
        }
    }



}



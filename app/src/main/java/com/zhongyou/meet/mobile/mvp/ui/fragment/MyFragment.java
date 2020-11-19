package com.zhongyou.meet.mobile.mvp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxBus;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.di.component.DaggerMyComponent;
import com.zhongyou.meet.mobile.entiy.MessageDetail;
import com.zhongyou.meet.mobile.entiy.MyAllCourse;
import com.zhongyou.meet.mobile.event.LikeEvent;
import com.zhongyou.meet.mobile.event.UnlockEvent;
import com.zhongyou.meet.mobile.mvp.contract.MyContract;
import com.zhongyou.meet.mobile.mvp.presenter.MyPresenter;
import com.zhongyou.meet.mobile.mvp.ui.activity.AccountSettingActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.SystemSettingActivity;
import com.zhongyou.meet.mobile.utils.BadgeHelper;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.Page;
import com.zhongyou.meet.mobile.view.MeetingSpaceItemDecoration;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;
import rx.functions.Action1;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/03/2020 23:02
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "我的页面")
public class MyFragment extends BaseFragment<MyPresenter> implements MyContract.View {

    @BindView(R.id.txt_search)
    TextView txtSearch;
    @BindView(R.id.inputCode)
    ImageView inputCode;
    @BindView(R.id.icon_notify)
    ImageView iconNotify;
    @BindView(R.id.userInfoTextView)
    SuperTextView userInfoTextView;
    @BindView(R.id.myMeetingRecyclerView)
    RecyclerView myMeetingRecyclerView;
    @BindView(R.id.my_meeting_expandableLayout)
    ExpandableLayout myMeetingExpandableLayout;
    @BindView(R.id.my_buy_expandableLayout)
    ExpandableLayout myBuyExpandableLayout;
    @BindView(R.id.my_meeting)
    SuperTextView myMeeting;
    @BindView(R.id.my_sign)
    SuperTextView mySign;
    @BindView(R.id.mySignRecyclerView)
    RecyclerView mySignRecyclerView;
    @BindView(R.id.my_sign_expandableLayout)
    ExpandableLayout mySignExpandableLayout;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.noCollectionDataView)
    TextView noCollectionDataView;
    @BindView(R.id.noSignDataView)
    TextView noSignDataView;
    @BindView(R.id.red_dotone)
    RelativeLayout redDot1;
    @BindView(R.id.my_Buy)
    SuperTextView myBuy;
    @BindView(R.id.noBuyDataView)
    TextView noBuyDataView;
    @BindView(R.id.myBuyRecyclerView)
    RecyclerView myBuyRecyclerView;

    private Context mContext;
    private BaseRecyclerViewAdapter<MyAllCourse.SignUpSeriesBean> mSignUpViewAdapter;
    private BaseRecyclerViewAdapter<MyAllCourse.CollectMeetingsBean> mCollectMeetingsAdapter;
    private BadgeHelper mBadgeHelper;
    private BaseRecyclerViewAdapter<MyAllCourse.AuthSeriesBean> mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerMyComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        txtSearch.setVisibility(View.INVISIBLE);
        inputCode.setImageResource(R.drawable.icon_notify);
        iconNotify.setImageResource(R.drawable.setting);
        if (myMeeting != null) {
            myMeeting.getRightIconIV().setRotation(180);
        }
        if (mySign != null) {
            mySign.getRightIconIV().setRotation(180);
        }

        if (myBuy != null) {
            myBuy.getRightIconIV().setRotation(180);
        }
        initListener();

        setAdapter();

        RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof LikeEvent) {
                    if (((LikeEvent) o).isLike()) {
                        if (mPresenter != null) {
                            mPresenter.getUnReadMessage();
                        }
                    }
                } else if (o instanceof UnlockEvent) {
                    UnlockEvent isUnlock = (UnlockEvent) o;
                    if (isUnlock.isLock()) {
                        if (refreshLayout != null) {
                            refreshLayout.autoRefresh();
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(MMKV.defaultMMKV().getString(MMKVHelper.PHOTO, "")) && mContext != null) {
            Glide.with(mContext).asBitmap().load(MMKV.defaultMMKV().getString(MMKVHelper.PHOTO, "")).into(userInfoTextView.getLeftIconIV());
            userInfoTextView.getLeftTextView().setText(MMKV.defaultMMKV().getString(MMKVHelper.USERNICKNAME, ""));
            userInfoTextView.getLeftBottomTextView().setText(MMKV.defaultMMKV().getString(MMKVHelper.POSTTYPENAME, ""));
        }

        if (mPresenter != null) {
            mPresenter.getUnReadMessage();
        }

    }

    private void setAdapter() {


        userInfoTextView.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                Timber.e("点击了个人信息");
                launchActivity(new Intent(mContext, AccountSettingActivity.class));
            }
        });

    }

    private void initListener() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        myMeetingRecyclerView.setLayoutManager(layoutManager);
        myMeetingRecyclerView.addItemDecoration(new MeetingSpaceItemDecoration(0, AutoSizeUtils.pt2px(getActivity(), 10), 0, 0));

        mySignRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mySignRecyclerView.addItemDecoration(new MeetingSpaceItemDecoration(0, AutoSizeUtils.pt2px(getActivity(), 10), 0, 0));
        myBuyRecyclerView.addItemDecoration(new MeetingSpaceItemDecoration(0, AutoSizeUtils.pt2px(getActivity(), 10), 0, 0));
        myMeetingExpandableLayout.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                //expansionFraction==1 是展开

                if (myMeeting != null && myMeeting.getRightIconIV() != null) {
                    myMeeting.getRightIconIV().setRotation(expansionFraction * 180);
                }
            }
        });
        myMeeting.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                if (myMeetingExpandableLayout != null) {
                    myMeetingExpandableLayout.toggle();
                }
            }
        });

        mySignExpandableLayout.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                if (mySign != null && mySign.getRightIconIV() != null) {
                    mySign.getRightIconIV().setRotation(expansionFraction * 180);
                }
            }
        });

        myBuyExpandableLayout.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                if (myBuy != null && myBuy.getRightIconIV() != null) {
                    myBuy.getRightIconIV().setRotation(expansionFraction * 180);
                }
            }
        });

        mySign.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                if (mySignExpandableLayout != null) {
                    mySignExpandableLayout.toggle();
                }
            }
        });
        myBuy.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                if (myBuyExpandableLayout != null) {
                    myBuyExpandableLayout.toggle();
                }
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.getCollectMeeting();
                }
            }
        });
        refreshLayout.autoRefresh();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        refreshLayout.finishRefresh();
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

    @OnClick({R.id.inputCode, R.id.icon_notify, R.id.my_meeting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inputCode://通知按钮
                launchActivity(new Intent(getContext(), MessageDetailActivity.class));
                break;
            case R.id.icon_notify://设置按钮
                launchActivity(new Intent(getContext(), SystemSettingActivity.class));
//				launchActivity(new Intent(getContext(), DemoActivity.class));
                break;

            case R.id.my_meeting:
                break;
        }
    }

    @Override
    public void getDataSuccess(MyAllCourse myAllCourse) {
        if (myAllCourse == null) {
            return;
        }
        if (mPresenter != null) {
            if (myAllCourse.getSignUpSeries() != null) {
                if (myAllCourse.getSignUpSeries().size() <= 0) {
                    noSignDataView.setVisibility(View.VISIBLE);
                    mySignRecyclerView.setVisibility(View.GONE);
                } else {
                    noSignDataView.setVisibility(View.GONE);
                    mySignRecyclerView.setVisibility(View.VISIBLE);
                    mSignUpViewAdapter = mPresenter.initCollectCourseAdapter(myAllCourse.getSignUpSeries());
                    mySignRecyclerView.setAdapter(mSignUpViewAdapter);
                }

            }
            if (myAllCourse.getCollectMeetings() != null) {
                if (myAllCourse.getCollectMeetings().size() <= 0) {
                    noCollectionDataView.setVisibility(View.VISIBLE);
                    myMeetingRecyclerView.setVisibility(View.GONE);
                } else {
                    noCollectionDataView.setVisibility(View.GONE);
                    myMeetingRecyclerView.setVisibility(View.VISIBLE);
                    mCollectMeetingsAdapter = mPresenter.initCollectMeetingAdapter(myAllCourse.getCollectMeetings());
                    myMeetingRecyclerView.setAdapter(mCollectMeetingsAdapter);
                }

            }

            if (myAllCourse.getAuthSeries() != null) {
                if (myAllCourse.getAuthSeries().size() <= 0) {
                    noBuyDataView.setVisibility(View.VISIBLE);
                    myBuyRecyclerView.setVisibility(View.GONE);
                } else {
                    noBuyDataView.setVisibility(View.GONE);
                    myBuyRecyclerView.setVisibility(View.VISIBLE);
                    initBuyAdapter(myAllCourse);
                }
            }

        }

    }

    private void initBuyAdapter(MyAllCourse myAllCourse) {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerViewAdapter<MyAllCourse.AuthSeriesBean>(getActivity(), myAllCourse.getAuthSeries(), R.layout.item_sigin) {
                @Override
                public void convert(BaseRecyclerHolder holder, MyAllCourse.AuthSeriesBean item, int position, boolean isScrolling) {
                    holder.setText(R.id.name, item.getName());
//				holder.setText(R.id.begin_time, "———" + item.getName() + "———");
                    holder.getView(R.id.item_time).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.meeting_state).setVisibility(View.GONE);
                    holder.setImageByUrl(R.id.imageView, item.getPictureURL());

                    holder.getView(R.id.collectButton).setVisibility(View.GONE);

//				if (position % 2 == 0) {
//					((ImageView) holder.getView(R.id.imageView)).setImageDrawable(mRootView.getActivity().getResources().getDrawable(R.mipmap.bg_meeting_item_a));
//				} else {
//					((ImageView) holder.getView(R.id.imageView)).setImageDrawable(mRootView.getActivity().getResources().getDrawable(R.mipmap.bg_meeting_item_b));
//				}
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchActivity(new Intent(getActivity(), NewMakerDetailActivity.class)
                                    .putExtra("pageId", item.getPageId())
                                    .putExtra("isAuth", item.getIsAuth())
                                    .putExtra("isSignUp", item.getIsSignUp())
                                    .putExtra("seriesId", item.getId())
                                    .putExtra("url", item.getPictureURL()));
                        }
                    });
                }
            };
            myBuyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            myBuyRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyData(myAllCourse.getAuthSeries());
//			mAdapter.notifyDataSetChanged();
        }

    }

    //取消会议收藏
    @Override
    public void cancelAdvance(boolean isSuccess, List<MyAllCourse.CollectMeetingsBean> dataList, String id, int position) {
        if (mCollectMeetingsAdapter != null) {
            if (isSuccess) {
                dataList.get(position).setIsCollection(0);
                dataList.remove(position);
            } else {
                dataList.get(position).setIsCollection(1);

            }
            if (dataList.size() <= 0) {
                noCollectionDataView.setVisibility(View.VISIBLE);
                myMeetingRecyclerView.setVisibility(View.GONE);
            } else {
                noCollectionDataView.setVisibility(View.GONE);
                myMeetingRecyclerView.setVisibility(View.VISIBLE);
            }
            mCollectMeetingsAdapter.notifyData(dataList);
        }
    }

    @Override
    public void unSignCourse(boolean isSuccess, List<MyAllCourse.SignUpSeriesBean> dataList, String id, int position) {
        if (mSignUpViewAdapter != null) {
            if (isSuccess) {
                dataList.get(position).setIsSign(0);
                dataList.remove(position);
            } else {
                dataList.get(position).setIsSign(1);
            }
            if (dataList.size() <= 0) {
                noSignDataView.setVisibility(View.VISIBLE);
                mySignRecyclerView.setVisibility(View.GONE);
            } else {
                noSignDataView.setVisibility(View.GONE);
                mySignRecyclerView.setVisibility(View.VISIBLE);
            }
            mSignUpViewAdapter.notifyData(dataList);
        }
    }

    @Override
    public void showRedDot(boolean isShow, int count) {
        if (redDot1 != null) {
            redDot1.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }
}

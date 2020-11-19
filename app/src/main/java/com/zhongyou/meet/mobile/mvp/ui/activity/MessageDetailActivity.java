package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxBus;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.di.component.DaggerMessageDetailComponent;
import com.zhongyou.meet.mobile.entiy.MessageDetail;
import com.zhongyou.meet.mobile.event.LikeEvent;
import com.zhongyou.meet.mobile.event.UnlockEvent;
import com.zhongyou.meet.mobile.mvp.contract.MessageDetailContract;
import com.zhongyou.meet.mobile.mvp.presenter.MessageDetailPresenter;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.view.SwipeView;

import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 21:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public class MessageDetailActivity extends BaseActivity<MessageDetailPresenter> implements MessageDetailContract.View {

    @BindView(R.id.toolbar_back)
    RelativeLayout toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int mCurrentPage = 1;
    @BindView(R.id.rightTextView)
    TextView rightTextView;
    private BaseRecyclerViewAdapter<MessageDetail> mBaseRecyclerViewAdapter;
    List<MessageDetail> dataLists;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMessageDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {

        return R.layout.activity_message_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
		/*String type = getIntent().getStringExtra("type");
		switch (Integer.parseInt(type)) {
			case 1:
				setTitle("教室通知");
				break;
			case 2:
				setTitle("创客通知");
				break;
			case 3:
				setTitle("系统通知");
				break;
			default:

				break;
		}*/
        setTitle("消息中心");
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout.autoRefresh();

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mCurrentPage = 1;
                if (mPresenter != null) {
                    mPresenter.getMessageDetail("1", mCurrentPage);
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mCurrentPage++;
                if (mPresenter != null) {
                    mPresenter.getMessageDetail("1", mCurrentPage);
                }
            }
        });

        rightTextView.setVisibility(View.VISIBLE);

        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataLists == null) return;
                if (mPresenter != null) {
                    mPresenter.readAllMessage("1");
                }

            }
        });
        RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof UnlockEvent) {
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
    public void getMessageDetailComplete(List<MessageDetail> list) {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();

        dataLists = list;
        initAdapter();

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public void initAdapter() {
        if (mBaseRecyclerViewAdapter == null) {
            mBaseRecyclerViewAdapter = new BaseRecyclerViewAdapter<MessageDetail>(this, dataLists, R.layout.item_message_detail) {
                @Override
                public void convert(BaseRecyclerHolder holder, MessageDetail item, int position, boolean isScrolling) {

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!SwipeView.closeMenu(v)) {
                                ItemClicked(item, position);
                            }
                        }
                    });

                    holder.getView(R.id.checkClass).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!SwipeView.closeMenu(v)) {
                                ItemClicked(item, position);
                            }
                        }
                    });

                    holder.getView(R.id.tv_usb_delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SwipeView.closeMenu(v);
                            if (mPresenter != null) {
                                mPresenter.detele(item.getId(), position);
                            }
                        }
                    });

//					if (item.getType()==1){
//						holder.getView(R.id.checkClass).setVisibility(View.VISIBLE);
//					}else {
//						holder.getView(R.id.checkClass).setVisibility(View.INVISIBLE);
//					}

                    holder.setText(R.id.title, item.getTitle());
                    holder.setText(R.id.meetingName, item.getAppNotice());
                    holder.setText(R.id.sendTime, item.getTime());

                    if (item.getIsReader() == 1) {
                        holder.getView(R.id.readTag).setVisibility(View.GONE);
                    } else {
                        holder.getView(R.id.readTag).setVisibility(View.VISIBLE);
                    }

                }
            };
            recyclerview.setLayoutManager(new LinearLayoutManager(this));
            recyclerview.setAdapter(mBaseRecyclerViewAdapter);
        } else {
            mBaseRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void ItemClicked(MessageDetail item, int position) {
        if (item.getType() == 1) {
            try {
                if (Double.parseDouble(String.valueOf(item.getIsToken())) == 1.0D) {//需要加入码的
                    JoinMeetingDialog.INSTANCE.showNeedCodeDialog(MessageDetailActivity.this, item.getUrlId());

                } else {
                    JoinMeetingDialog.INSTANCE.showNoCodeDialog(MessageDetailActivity.this, item.getUrlId());
                }
            } catch (Exception e) {
                JoinMeetingDialog.INSTANCE.showNeedCodeDialog(MessageDetailActivity.this, item.getUrlId());
            } finally {
                if (mPresenter != null && item.getIsReader() == 0) {
                    mPresenter.readOneMessage(item.getId(), position, item);
                }
            }
        } else {
            if (mPresenter != null) {
                switch (item.getIsReader()) {
                    case 0:
                        mPresenter.readOneMessage(item.getId(), position, item);
                        break;
                    case 1:
                        jumpIntent(item, position);

                        break;
                }
            }
        }

    }

    private void jumpIntent(MessageDetail item, int position) {
        RxBus.sendMessage(new LikeEvent(true));
        startActivity(new Intent(getActivity(), NewMakerDetailActivity.class)
                .putExtra("pageId", item.getPageId())
                .putExtra("isSignUp", -1)
                .putExtra("seriesId", item.getUrlId())
                .putExtra("isAuth", item.getIsAuth())
                .putExtra("url", "")
        );
    }


    @Override
    public RxPermissions getRxPermissions() {
        return new RxPermissions(this);
    }


    @Override
    public void canLoadMore(int page) {
        if (mCurrentPage >= page) {
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
        }
    }

    @Override
    public void readOneMessageResult(boolean success, int position, MessageDetail item) {
        if (success) {
            dataLists.get(position).setIsReader(1);
            mBaseRecyclerViewAdapter.notifyItemChanged(position);
        }

        if (item.getType() == 2) {
            jumpIntent(item, position);
        }

    }

    @Override
    public void readAllMessageResult(boolean success) {
        if (success) {
            if (dataLists == null) return;
            for (MessageDetail dataList : dataLists) {
                dataList.setIsReader(1);
            }
            mBaseRecyclerViewAdapter.notifyItemRangeChanged(0, dataLists.size());
            RxBus.sendMessage(new LikeEvent(true));
        }
    }

    @Override
    public void deleteSuccess(boolean success, int poi) {
        if (success) {
            dataLists.remove(poi);
            mBaseRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}

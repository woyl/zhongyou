package com.zhongyou.meet.mobile.mvp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.RxBus;
import com.jess.arms.utils.RxLifecycleUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.maning.mndialoglibrary.MToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.entiy.MakerColumn;
import com.zhongyou.meet.mobile.event.LikeEvent;
import com.zhongyou.meet.mobile.event.RefreshEnent;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity;
import com.zhongyou.meet.mobile.utils.Page;
import com.zhongyou.meet.mobile.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.jessyan.autosize.utils.AutoSizeUtils;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/5 12:31 PM.
 * @
 */
@Page(name = "创客分类的Fragment")
public class MakerCourseFragment extends BaseLazyLoadFragment {


	@BindView(R.id.recyclerview)
	RecyclerView recyclerview;
	@BindView(R.id.refreshLayout)
	SmartRefreshLayout refreshLayout;
	@BindView(R.id.emptyView)
	TextView emptyView;
	@BindView(R.id.circularProgressBar)
	ProgressBar mCircularProgressBar;

	private String courseID;
	private int mCurrentPage = 1;
	private String mTotalPage = "1";
	List<MakerColumn.SeriesBean.ListBean> listBean = new ArrayList<>();
	private BaseRecyclerViewAdapter<MakerColumn.SeriesBean.ListBean> mAdapter;
	CompositeDisposable mCompositeDisposable = new CompositeDisposable();


	public MakerCourseFragment() {

	}

	@SuppressLint("ValidFragment")
	public MakerCourseFragment(String courseID, List<MakerColumn.SeriesBean.ListBean> listBean, String totalPage) {

		this.courseID = courseID;
		if (listBean != null) {
			this.listBean = listBean;
		}
		if (totalPage == null) {
			this.mTotalPage = "1";
		} else {
			this.mTotalPage = totalPage;
		}

	}


	@Override
	public void setupFragmentComponent(@NonNull AppComponent appComponent) {

	}

	@Override
	public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_maker_course, container, false);
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {


		setListener();

		recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
		recyclerview.addItemDecoration(new SpaceItemDecoration(AutoSizeUtils.pt2px(getActivity(), 10)
				, AutoSizeUtils.pt2px(getActivity(), 10)
				, AutoSizeUtils.pt2px(getActivity(), 10)
				, AutoSizeUtils.pt2px(getActivity(), 10)));

	}

	private void setListener() {
		refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
				mCurrentPage++;
				getSeriesPageByType(courseID, mCurrentPage, false);
			}
		});


	}

	private void getSeriesPageByType(String courseID, int currentPage, boolean isShowLoading) {
		HttpsRequest.provideClientApi().getSeriesPageByType(courseID, currentPage, Constant.pageSize).compose(RxLifecycleUtils.bindToLifecycle(this))
				.compose(RxSchedulersHelper.io_main())
				.subscribe(new RxSubscriber<JSONObject>() {

					@Override
					public void onSubscribe(Disposable d) {
						mCompositeDisposable.add(d);
						if (mCircularProgressBar != null) {
							mCircularProgressBar.setVisibility(isShowLoading ? View.VISIBLE : View.GONE);
						}
					}

					@Override
					public void _onNext(JSONObject jsonObject) {
						if (jsonObject.getInteger("errcode") == 0) {

							if (mCurrentPage >= jsonObject.getJSONObject("data").getInteger("totalPage")) {
								refreshLayout.setEnableLoadMore(false);
							} else {
								refreshLayout.setEnableLoadMore(true);
							}

							JSONArray data = jsonObject.getJSONObject("data").getJSONArray("list");
							if (currentPage == 1) {
								listBean = JSON.parseArray(data.toJSONString(), MakerColumn.SeriesBean.ListBean.class);
							} else {
								listBean.addAll(JSON.parseArray(data.toJSONString(), MakerColumn.SeriesBean.ListBean.class));
							}
							if (listBean.size() <= 0) {
								mCircularProgressBar.setVisibility(View.GONE);
								emptyView.setVisibility(View.VISIBLE);
								recyclerview.setVisibility(View.GONE);
							} else {
								mCircularProgressBar.setVisibility(View.GONE);
								emptyView.setVisibility(View.GONE);
								recyclerview.setVisibility(View.VISIBLE);
								setAdapter();
							}


						} else {
							MToast.makeTextShort(getActivity(), jsonObject.getString("errmsg"));
							mCircularProgressBar.setVisibility(View.GONE);
							emptyView.setVisibility(View.VISIBLE);
							recyclerview.setVisibility(View.GONE);
							refreshLayout.finishLoadMore();


						}

					}

					@Override
					public void onComplete() {
						super.onComplete();
						if (refreshLayout != null) {
							refreshLayout.finishLoadMore();
						}
					}

					@Override
					public void _onError(int code, String msg) {
						super._onError(code, msg);
						mCircularProgressBar.setVisibility(View.GONE);
						emptyView.setVisibility(View.VISIBLE);
						recyclerview.setVisibility(View.GONE);
						refreshLayout.finishLoadMore();
						mCircularProgressBar.clearAnimation();
					}
				});
	}


	private void setAdapter() {

		if (mAdapter == null) {
			mAdapter = new BaseRecyclerViewAdapter<MakerColumn.SeriesBean.ListBean>(getActivity(), listBean, R.layout.item_maker_course_video) {

				@Override
				public void convert(BaseRecyclerHolder holder, MakerColumn.SeriesBean.ListBean item, int position, boolean isScrolling) {
					holder.setText(R.id.videoName, item.getName());
					holder.setImageByUrlWithCorner(R.id.imageView,
							item.getPictureURL(), R.drawable.dafault_course,
							R.drawable.dafault_course, AutoSizeUtils.pt2px(holder.itemView.getContext(), 16));
					if (item.getIsSignUp() == 1) {
						holder.getView(R.id.collectButton).setVisibility(View.VISIBLE);
					} else {
						holder.getView(R.id.collectButton).setVisibility(View.GONE);
					}
					LikeButton likeButton = holder.getView(R.id.collectButton);
					likeButton.setLiked(item.getIsSign() == 1);

					likeButton.setOnLikeListener(new OnLikeListener() {
						@Override
						public void liked(LikeButton likeButton) {
							signCourse(item.getId(), 1, position);
						}

						@Override
						public void unLiked(LikeButton likeButton) {
							signCourse(item.getId(), 0, position);
						}
					});

				}
			};
			mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(RecyclerView parent, View view, int position) {
					/*if (listBean.get(position).getType() == 1) {
						//详情页面
					} else if (listBean.get(position).getType() == 2) {
						//直播页面
					} else if (listBean.get(position).getType() == 3) {
						//视频页面
					} else if (listBean.get(position).getType() == 4) {
						//音频界面
					}*/
					getActivity().startActivity(new Intent(getActivity(), NewMakerDetailActivity.class)
							.putExtra("pageId", listBean.get(position).getPageId())
							.putExtra("isSignUp", listBean.get(position).getIsSignUp())
							.putExtra("seriesId", listBean.get(position).getId())
							.putExtra("isAuth",listBean.get(position).getIsAuth()));
				}
			});
			recyclerview.setAdapter(mAdapter);
		} else {
			mAdapter.notifyData(listBean);
		}

	}


	@Override
	public void setData(@Nullable Object data) {
		if (mAdapter != null && listBean != null) {
			Message msg = (Message) data;
			try {
				this.listBean = (List<MakerColumn.SeriesBean.ListBean>) ((Message) data).obj;
				Timber.e("listBean---->%s", JSON.toJSONString(listBean));
				mAdapter.notifyData(listBean);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	public void signCourse(String typeId, int isSign, int position) {
		HttpsRequest.provideClientApi().siginCourse(typeId, isSign)
				.compose(RxSchedulersHelper.io_main())
				.compose(RxLifecycleUtils.bindToLifecycle(this))
				.subscribe(new RxSubscriber<JSONObject>() {
					@Override
					public void _onNext(JSONObject jsonObject) {

						if (jsonObject.getInteger("errcode") == 0) {
							//成功就不管
							//listBean.get(position).setIsUp(isSign);
							if (isSign == 1) {
								RxBus.sendMessage(new LikeEvent(true));
							}

						} else {
							//失败后需要重新设置为没有报名
							MToast.makeTextShort(jsonObject.getString("errmsg"));
							listBean.get(position).setIsSign(0);
							mAdapter.notifyItemChanged(position);
						}
					}

					@Override
					public void _onError(int code, String msg) {
						super._onError(code, msg);
						//失败后需要重新设置为没有报名
						listBean.get(position).setIsSign(0);
						mAdapter.notifyItemChanged(position);
					}
				});
	}

	@Override
	protected void lazyLoadData() {
		Timber.e("courseID---->%s", courseID);
		if (courseID.equals("0")) {
			setAdapter();
		} else {
			getSeriesPageByType(courseID, mCurrentPage, true);
		}

		Timber.e("mTotalPage---->%s,mCurrentPage----->%s,courseID---->%s", mTotalPage, mCurrentPage, courseID);
		if (this.mTotalPage != null && courseID != null) {
			if (Integer.parseInt(mTotalPage) <= mCurrentPage) {
				refreshLayout.setEnableLoadMore(false);
			} else {
				refreshLayout.setEnableLoadMore(true);
			}
		}

		RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (isViewCreated && isVisibleToUser) {
					//如果等于0 下拉刷新的时候 会从获取栏目的接口返回全部的数据  不需要进行接口获取 但是需要将当前页设置为1；

					if (o instanceof RefreshEnent) {
						RefreshEnent enent = (RefreshEnent) o;
						mCurrentPage = 1;
						if (courseID != null && courseID.equals("0")) {

							return;
						}
						if (enent.isRefresh()) {
							getSeriesPageByType(courseID, mCurrentPage, false);
						}
					}
				}
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		if (courseID != null) {
			mCurrentPage = 1;
			getSeriesPageByType(courseID, mCurrentPage, false);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mCompositeDisposable != null) {
			mCompositeDisposable.clear();//保证 Activity 结束时取消所有正在执行的订阅
		}
	}
}

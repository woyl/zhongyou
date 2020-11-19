package com.zhongyou.meet.mobile.mvp.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.appbar.AppBarLayout;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxBus;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mmkv.MMKV;
import com.xw.repo.BubbleSeekBar;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.business.MeetingSearchActivity;
import com.zhongyou.meet.mobile.core.CommonUtils;
import com.zhongyou.meet.mobile.core.GlobalConsts;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.core.PlayService;
import com.zhongyou.meet.mobile.di.component.DaggerMakerComponent;
import com.zhongyou.meet.mobile.entiy.MakerColumn;
import com.zhongyou.meet.mobile.entiy.RecomandData;
import com.zhongyou.meet.mobile.event.LikeEvent;
import com.zhongyou.meet.mobile.event.RefreshEnent;
import com.zhongyou.meet.mobile.mvp.contract.MakerContract;
import com.zhongyou.meet.mobile.mvp.presenter.MakerPresenter;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerCourseAudioActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MessageDetailActivity;
import com.zhongyou.meet.mobile.utils.BadgeHelper;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.Page;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import rx.functions.Action1;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;
import static com.zhongyou.meet.mobile.business.MeetingsFragment.KEY_MEETING_TYPE;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/05/2020 10:34
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "创客")
public class MakerFragment extends BaseFragment<MakerPresenter> implements MakerContract.View {

	@BindView(R.id.txt_search)
	TextView txtSearch;
	@BindView(R.id.inputCode)
	ImageView inputCode;
	@BindView(R.id.icon_notify)
	ImageView iconNotify;
	@BindView(R.id.banner_guide_content)
	BGABanner bannerGuideContent;
	@BindView(R.id.recommendAudioMore)
	RelativeLayout recommendAudioMore;
	@BindView(R.id.tabLayout)
	SlidingTabLayout tabLayout;
	@BindView(R.id.appBarLayout)
	AppBarLayout appBarLayout;
	@BindView(R.id.mSwipeRefreshLayout)
	SmartRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.viewPager)
	ViewPager mViewPager;
	@BindView(R.id.audioRecyclerview)
	RecyclerView audioRecyclerview;
	@BindView(R.id.circularProgressBar)
	ProgressBar circularProgressBar;
	@BindView(R.id.red_dot)
	RelativeLayout redDot;

	private MusicInfoReceiver receiver;
	private PlayService.MusicBinder musicBinder;
	private ServiceConnection serviceConn;

	private int clickPosition = -1;
	private BaseRecyclerViewAdapter<RecomandData.ChangeLessonByDayBean> mAdapter;
	private List<RecomandData.ChangeLessonByDayBean> mAudioList;
	private Animation mOperatingAnim;
	ArrayList<Fragment> mFragments = new ArrayList<>();
	private List<String> mTableLists;
	private MyPagerAdapter mAdapter1;
	private BadgeHelper mBadgeHelper;


	public static MakerFragment newInstance() {
		MakerFragment fragment = new MakerFragment();
		return fragment;
	}

	@Override
	public void setupFragmentComponent(@NonNull AppComponent appComponent) {
		DaggerMakerComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);
	}

	@Override
	public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//		AutoSize.autoConvertDensity(getActivity(), 750, true);
		return inflater.inflate(R.layout.fragment_maker, container, false);
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {


		circularProgressBar.setVisibility(View.VISIBLE);
		mViewPager.setVisibility(View.GONE);

		mSwipeRefreshLayout.setRefreshHeader(new MaterialHeader(BaseApplication.getInstance()));
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(@NonNull RefreshLayout refreshLayout) {
				if (mPresenter != null) {
					mPresenter.getRecommendData(bannerGuideContent, audioRecyclerview);

				}
			}
		});
		mSwipeRefreshLayout.autoRefresh();
		recommendAudioMore.setVisibility(View.GONE);

		tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
			@Override
			public void onTabSelect(int position) {

			}

			@Override
			public void onTabReselect(int position) {

			}
		});

		audioRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
		((SimpleItemAnimator) audioRecyclerview.getItemAnimator()).setSupportsChangeAnimations(false);


		RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				if (o instanceof LikeEvent) {
					Timber.e("o--LikeEvent-->%s", JSON.toJSONString(o));
					if (((LikeEvent) o).isLike()) {
						if (mPresenter != null) {
							mPresenter.getUnReadMessage();
						}
					}
				}
			}
		});

	}

	public void playMusic(int position, String url, int type) {
		mAudioList.get(position).setType(type);
		musicBinder.playMusic(url);
		clickPosition = position;

	}

	@Override
	public void setData(@Nullable Object data) {

	}

	@Override
	public void showLoading() {

	}


	@Override
	public void hideLoading() {
		mSwipeRefreshLayout.finishLoadMore();
		mSwipeRefreshLayout.finishRefresh();
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

	@OnClick({R.id.txt_search, R.id.inputCode, R.id.icon_notify, R.id.recommendAudioMore})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.txt_search:
				Intent searchMeetingIntent = new Intent(mContext, MeetingSearchActivity.class);
				searchMeetingIntent.putExtra(KEY_MEETING_TYPE, 0);
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
					if (getActivity() != null) {
						JoinMeetingDialog.INSTANCE.showQuickJoinMeeting((AppCompatActivity) getActivity());
					}
				}
				break;
			case R.id.icon_notify:
				launchActivity(new Intent(getContext(), MessageDetailActivity.class));
				break;
			case R.id.recommendAudioMore:
				launchActivity(new Intent(getContext(), MakerCourseAudioActivity.class));
				break;

		}
	}


	@Override
	public void onPause() {
		super.onPause();

		if (clickPosition != -1) {
			//音乐暂停播放
			mAudioList.get(clickPosition).setType(2);
			mAdapter.notifyItemRangeChanged(clickPosition, 1);
		}
		if (bannerGuideContent != null) {
			bannerGuideContent.stopAutoPlay();
		}
		try {
			if (receiver != null && getActivity() != null) {
				getActivity().unregisterReceiver(receiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void getAudioDataSuccess(RecomandData.ChangeLessonByDayBean data) {
		recommendAudioMore.setVisibility(View.VISIBLE);
		Timber.e("data---->%s", JSON.toJSONString(data));
		if (data == null) {
			if (mPresenter != null) {
				mPresenter.getMakerColumn();
			}
			return;
		}
		mAudioList = new ArrayList<>();
		mAudioList.add(data);
		mAdapter = new BaseRecyclerViewAdapter<RecomandData.ChangeLessonByDayBean>(getActivity(), mAudioList, R.layout.item_maker_course_audio) {
			@Override
			public void convert(BaseRecyclerHolder holder, RecomandData.ChangeLessonByDayBean item, int position, boolean isScrolling) {
				if (item == null) {
					return;
				}
				try {
					Glide.with(getContext()).load(item.getComentURL()).skipMemoryCache(false).into((ImageView) holder.getView(R.id.descriptionImage));
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (position == mAudioList.size() - 1) {
					holder.getView(R.id.line).setVisibility(View.GONE);
				}

				holder.setText(R.id.className, item.getName() == null ? "" : item.getName());

				holder.setText(R.id.teacherName, "主讲人:" + item.getAnchorName());
				holder.setText(R.id.classDate, item.getBelongTime() == null ? "" : item.getBelongTime());
				if (item.isExpanded()) {
					if (holder.getView(R.id.descriptionImage).getVisibility() != View.VISIBLE) {
						holder.getView(R.id.descriptionImage).setVisibility(View.VISIBLE);
					}
				} else {
					if (holder.getView(R.id.descriptionImage).getVisibility() != View.GONE) {
						holder.getView(R.id.descriptionImage).setVisibility(View.GONE);
					}
				}

				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null == item.getVioceURL()) {
							return;
						}
						if (clickPosition == -1) {
							playMusic(position, item.getVioceURL(), 3);
						} else {
							if (position == clickPosition) {
								if (mAudioList.get(position).getType() == 0) {
									playMusic(position, item.getVioceURL(), 3);
								} else if (mAudioList.get(position).getType() == 1) {
									musicBinder.pause();
									mAudioList.get(position).setType(2);
								} else if (mAudioList.get(position).getType() == 2) {
									musicBinder.start();
									mAudioList.get(position).setType(1);
								}
							} else {

								mAudioList.get(clickPosition).setType(0);
								mAudioList.get(clickPosition).setCurrentDuration(0);
								playMusic(position, item.getVioceURL(), 3);
							/*
								clickPosition = position;
								musicBinder.playMusic("http://file.zhongyouie.cn/yoyo_project/teacherResource/3a925c0a19454210be511921315324fd.mp3");*/
							}

						}

						if (item.getType() != 1 && item.getType() != 3) {
							item.setExpanded(false);
							holder.getView(R.id.descriptionImage).setVisibility(View.GONE);
						} else {
							item.setExpanded(true);
							holder.getView(R.id.descriptionImage).setVisibility(View.VISIBLE);
						}

						notifyDataSetChanged();
					}
				});

				if (mAudioList.get(position).getType() == 1) {
					holder.setImageResource(R.id.audioState, R.drawable.icon_audio_pause);
				} else if (mAudioList.get(position).getType() == 3) {
					holder.setImageResource(R.id.audioState, R.drawable.icon_audio_loading);

					mOperatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.tip);
					LinearInterpolator lin = new LinearInterpolator();
					mOperatingAnim.setInterpolator(lin);
					holder.getView(R.id.audioState).startAnimation(mOperatingAnim);

				} else {
					holder.setImageResource(R.id.audioState, R.drawable.audio);
				}
//				Timber.e("item.getCurrentDuration()==%s", item.getCurrentDuration());
				holder.setText(R.id.classTime, CommonUtils.getFormattedTime(item.getCurrentDuration()) + "/" + item.getTotalDate());

				BubbleSeekBar seekBar = holder.getView(R.id.seekBar);
				seekBar.getConfigBuilder().max(item.getTotalTime()).progress((int) item.getCurrentDuration()).build();

				if (item.getType() == 0) {
					seekBar.setVisibility(View.GONE);
				} else if (seekBar.getVisibility() != View.VISIBLE) {
					seekBar.setVisibility(View.VISIBLE);
				}

				holder.getView(R.id.audioState).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null == item.getVioceURL()) {
							return;
						}
						if (clickPosition == -1) {
							playMusic(position, item.getVioceURL(), 3);
						} else {
							if (position == clickPosition) {
								if (mAudioList.get(position).getType() == 0) {
									playMusic(position, item.getVioceURL(), 3);
								} else if (mAudioList.get(position).getType() == 1) {
									musicBinder.pause();
									mAudioList.get(position).setType(2);
								} else if (mAudioList.get(position).getType() == 2) {
									musicBinder.start();
									mAudioList.get(position).setType(1);
									//playMusic(position, item.getVioceURL(), 3);
								}
							} else {

								mAudioList.get(clickPosition).setType(0);
								mAudioList.get(clickPosition).setCurrentDuration(0);
								playMusic(position, item.getVioceURL(), 3);
							/*
								clickPosition = position;
								musicBinder.playMusic("http://file.zhongyouie.cn/yoyo_project/teacherResource/3a925c0a19454210be511921315324fd.mp3");*/
							}

						}

						if (item.getType() != 1 && item.getType() != 3) {
							item.setExpanded(false);
							holder.getView(R.id.descriptionImage).setVisibility(View.GONE);
						} else {
							item.setExpanded(true);
							holder.getView(R.id.descriptionImage).setVisibility(View.VISIBLE);
						}

						notifyDataSetChanged();

					}
				});
			}
		};
		audioRecyclerview.setAdapter(mAdapter);
		if (mPresenter != null) {
			mPresenter.getMakerColumn();
		}
		RxBus.getDefault().send(new RefreshEnent(true));
	}

	MakerCourseFragment e1;


	@Override
	public void getColumnData(MakerColumn column, boolean isSuccess) {

		if (mPresenter != null) {
			mPresenter.getUnReadMessage();
		}

		Timber.e("column---->%s", JSON.toJSONString(column));
		if (column == null) {
			circularProgressBar.setVisibility(View.GONE);
			return;
		}

		if (mTableLists == null) {
			mTableLists = new ArrayList<>();
		} else {
			mTableLists.clear();
		}
		if (mFragments == null) {
			mFragments = new ArrayList<>();
		} else {
			mFragments.clear();
		}

		if (column.getColumns() == null) {
			column.setColumns(new ArrayList<>());
		}
		/*MakerColumn.ColumnsBean e = new MakerColumn.ColumnsBean();
		e.setId("0");
		e.setTypeName("全部");

		column.getColumns().add(0, e);*/


		for (MakerColumn.ColumnsBean columnBean : column.getColumns()) {
			mTableLists.add(columnBean.getTypeName());
			if (columnBean.getId().equals("0")) {

				if (e1 == null) {
					e1 = new MakerCourseFragment(columnBean.getId(), column.getSeries().getList(), column.getSeries().getTotalPage() + "");
				} else {
					Message message = new Message();
					message.obj = column.getSeries().getList();
					e1.setData(message);
				}

				mFragments.add(e1);
			} else {
				mFragments.add(new MakerCourseFragment(columnBean.getId(), null, null));
			}
		}
		String[] strings = new String[mTableLists.size()];

		if (mAdapter1 == null) {
			mAdapter1 = new MyPagerAdapter(getChildFragmentManager(), mFragments, mTableLists.toArray(strings));
			mViewPager.setAdapter(mAdapter1);
			mViewPager.setOffscreenPageLimit(mFragments.size());
			tabLayout.setViewPager(mViewPager);
		} else {
			mAdapter1.notifyDataSetChanged();
		}
		circularProgressBar.setVisibility(View.GONE);
		mViewPager.setVisibility(View.VISIBLE);

	}

	@Override
	public void showRedDot(boolean isShow, int count) {
		Timber.e("isShow:---->%s   count:------>%s", isShow, count);
		if (redDot != null) {
			redDot.setVisibility(isShow ? View.VISIBLE : View.GONE);
		}

	}


	class MusicInfoReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Timber.e(action);
			if (action == null || clickPosition == -1) {
				return;
			}
			if (action.equals(GlobalConsts.ACTION_UPDATE_PROGRESS)) {

				int current = intent.getIntExtra("current", 0);
				int total = intent.getIntExtra("total", 0);
				Timber.e(current + "---");
				mAudioList.get(clickPosition).setType(1);
				mAudioList.get(clickPosition).setCurrentDuration(current);
				mAudioList.get(clickPosition).setTotalTime(total);
				mAdapter.notifyItemRangeChanged(clickPosition, 1);


			} else if (GlobalConsts.ACTION_LOCAL_MUSIC.equals(action)) {


			} else if (GlobalConsts.ACTION_PAUSE_MUSIC.equals(action)) {
				//音乐暂停播放
				mAudioList.get(clickPosition).setType(2);
				mAdapter.notifyItemRangeChanged(clickPosition, 1);


			} else if (GlobalConsts.ACTION_STATR_MUSIC.equals(action)) {
				//只有点暂停和播放按钮时

			} else if (GlobalConsts.ACTION_ONLINE_MUSIC.equals(action)) {

			} else if (GlobalConsts.ACTION_NEXT_MUSIC.equals(action)) {

			} else if (GlobalConsts.ACTION_MUSIC_STARTED.equals(action)) {
				//音乐开始播放
				mAudioList.get(clickPosition).setType(1);
				mOperatingAnim.cancel();
				mAdapter.notifyDataSetChanged();

			} else if (GlobalConsts.ACTION_COMPLETE_MUSIC.equals(action)) {
				//播放完成
				mAudioList.get(clickPosition).setType(0);
				mAudioList.get(clickPosition).setCurrentDuration(0);
				mAdapter.notifyDataSetChanged();

			}
		}
	}


	class MyPagerAdapter extends FragmentPagerAdapter {
		private ArrayList<Fragment> fragments = new ArrayList<>();
		private String[] titles;

		public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
			super(fm);
			this.fragments = fragments;
			this.titles = titles;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 覆写destroyItem并且空实现,这样每个Fragment中的视图就不会被销毁
			// super.destroyItem(container, position, object);
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		try {
			if (clickPosition != -1) {
				mAudioList.get(clickPosition).setType(2);
				mAudioList.get(clickPosition).setCurrentDuration(0);
				mAdapter.notifyItemRangeChanged(clickPosition, 1);
				if (musicBinder != null) {
					musicBinder.pause();
				}
				if (receiver != null && getActivity() != null) {
					getActivity().unregisterReceiver(receiver);
				}

				clickPosition = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver();
		if (bannerGuideContent != null) {
			bannerGuideContent.startAutoPlay();
		}

		if (mPresenter != null) {
			mPresenter.getUnReadMessage();
		}

	}


	private void registerReceiver() {
		receiver = new MusicInfoReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
		filter.addAction(GlobalConsts.ACTION_UPDATE_PROGRESS);
		filter.addAction(GlobalConsts.ACTION_STATR_MUSIC);
		filter.addAction(GlobalConsts.ACTION_PAUSE_MUSIC);
		filter.addAction(GlobalConsts.ACTION_LOCAL_MUSIC);
		filter.addAction(GlobalConsts.ACTION_ONLINE_MUSIC);
		filter.addAction(GlobalConsts.ACTION_NEXT_MUSIC);
		filter.addAction(GlobalConsts.ACTION_COMPLETE_MUSIC);
		if (getActivity() != null) {
			getActivity().registerReceiver(receiver, filter);
		}

	}

	public void setMusicBinder(PlayService.MusicBinder binder) {
		this.musicBinder = binder;
	}
}

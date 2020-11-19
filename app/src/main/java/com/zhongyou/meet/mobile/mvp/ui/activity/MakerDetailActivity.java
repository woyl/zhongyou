package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxBus;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.di.component.DaggerMakerDetailComponent;
import com.zhongyou.meet.mobile.entiy.MakerDetail;
import com.zhongyou.meet.mobile.event.UnlockEvent;
import com.zhongyou.meet.mobile.mvp.contract.MakerDetailContract;
import com.zhongyou.meet.mobile.mvp.presenter.MakerDetailPresenter;
import com.zhongyou.meet.mobile.utils.CountDownTimerUtil;
import com.zhongyou.meet.mobile.utils.DownloadUtil;
import com.zhongyou.meet.mobile.utils.FileUtils;
import com.zhongyou.meet.mobile.utils.Page;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.listener.CountDownTimeListener;
import com.zhongyou.meet.mobile.view.SpaceItemDecoration;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import io.rong.subscaleview.ImageSource;
import io.rong.subscaleview.SubsamplingScaleImageView;
import me.jessyan.autosize.utils.AutoSizeUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/07/2020 11:46
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "创客详情页面")
public class MakerDetailActivity extends BaseActivity<MakerDetailPresenter> implements MakerDetailContract.View, CountDownTimeListener {


	@BindView(R.id.toolbar_back)
	RelativeLayout toolbarBack;
	@BindView(R.id.toolbar_title)
	TextView toolbarTitle;
	@BindView(R.id.collectButton)
	LikeButton collectButton;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.detailImage)
	SubsamplingScaleImageView detailImage;
	@BindView(R.id.recyclerview)
	RecyclerView recyclerview;
	@BindView(R.id.refreshLayout)
	SmartRefreshLayout refreshLayout;
	@BindView(R.id.descriptionImage)
	SubsamplingScaleImageView descriptionImage;
	@BindView(R.id.circle_progress)
	CircleProgress circleProgress;
	@BindView(R.id.circle_progress1)
	CircleProgress circleProgress1;
	@BindView(R.id.detailImageCopy)
	ImageView detailImageCopy;
	@BindView(R.id.descriptionImageCopy)
	ImageView descriptionImageCopy;
	@BindView(R.id.needBuyCourseTextView)
	TextView needBuyCourseTextView;
	@BindView(R.id.needBuyCourseLayout)
	RelativeLayout needBuyCourseLayout;
	@BindView(R.id.needbuyHint)
	TextView needbuyHint;
	@BindView(R.id.weChatTextView)
	TextView weChatTextView;
	private String mPageId;
	private BaseRecyclerViewAdapter<MakerDetail.SubPagesBean> mAdapter;
	private String mSeriesId;
	private int isAuth = 0;

	/**到计时*/
	private CountDownTimerUtil countDownTimerUtil;

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {
		DaggerMakerDetailComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);
	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		setTitle("创客详情");
		return R.layout.activity_maker_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {
		refreshLayout.setRefreshHeader(new MaterialHeader(BaseApplication.getInstance()));
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(@NonNull RefreshLayout refreshLayout) {
				if (!TextUtils.isEmpty(mPageId) && mPresenter != null) {
					mPresenter.getMoreCourse(mPageId, mSeriesId);
				}
			}
		});
		mPageId = getIntent().getStringExtra("pageId");
		mSeriesId = getIntent().getStringExtra("seriesId");
		isAuth = getIntent().getIntExtra("isAuth", 0);

		if (!TextUtils.isEmpty(mPageId) && mPresenter != null) {
			mPresenter.getMoreCourse(mPageId, mSeriesId);
		}
		int isSignUp = getIntent().getIntExtra("isSignUp", 1);

		collectButton.setVisibility(isSignUp == 1 ? View.VISIBLE : View.GONE);

		initListener();

	}

	private void initListener() {
		collectButton.setOnLikeListener(new OnLikeListener() {
			@Override
			public void liked(LikeButton likeButton) {
				if (mPresenter != null) {
					mPresenter.signCourse(mSeriesId, 1);
				}
			}

			@Override
			public void unLiked(LikeButton likeButton) {
				if (mPresenter != null) {
					mPresenter.signCourse(mSeriesId, 0);
				}
			}
		});

		needBuyCourseTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isAuth == 1) {
					if (mPresenter != null) {
						mPresenter.unLockSeries(mSeriesId);
					}
				} else if (isAuth == 2) {
					MessageDialog.show(MakerDetailActivity.this, "提示", "该系列已成功解锁", "确定");
				}
			}
		});
		weChatTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				if (mClipboardManager == null) {
					ToastUtils.showToast(MakerDetailActivity.this, "获取剪切板权限出错");
					return;
				}
				if (!TextUtils.isEmpty(weChat)) {
					ClipData mClipData = ClipData.newPlainText("wechat", weChat);
					mClipboardManager.setPrimaryClip(mClipData);
					ToastUtils.showToast(MakerDetailActivity.this, "复制成功 请去微信添加好友");
				} else {
					ToastUtils.showToast(MakerDetailActivity.this, "复制微信失败");
				}


			}
		});
	}

	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {
		refreshLayout.finishRefresh();
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
	public void getDataSuccess(JSONObject jsonObject) {

		MakerDetail makerDetail = JSON.parseObject(jsonObject.toJSONString(), MakerDetail.class);
		collectButton.setLiked(makerDetail.getIsSign() == 1);
		if (!TextUtils.isEmpty(makerDetail.getName())) {
			toolbarTitle.setText(makerDetail.getName());
		}


		if (detailImage != null) {
			String fileName = "";
			try {
				fileName = makerDetail.getId() + "_" + makerDetail.getPictureURL().substring(makerDetail.getPictureURL().lastIndexOf("/") + 1);
			} catch (Exception e) {
				e.printStackTrace();
				fileName = makerDetail.getId() + "_" + makerDetail.getPictureURL();
			}
			File file = new File(FileUtils.getPath(this) + fileName);
			if (file.exists()) {
				try {
					detailImage.setImage(ImageSource.uri(FileUtils.getPath(this) + fileName));
					detailImage.setZoomEnabled(false);
					circleProgress.setVisibility(View.GONE);
					detailImage.setVisibility(View.VISIBLE);


					setNeedBuyView(1);


				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					getIntroduceImage(MakerDetailActivity.this, makerDetail);
				}

			} else {
				DownloadUtil.get().download(this, makerDetail.getPictureURL(), FileUtils.getPath(this), fileName, new DownloadUtil.OnDownloadListener() {
					@Override
					public void onDownloadSuccess(File file) {

						Timber.e("下载文件成功---->%s", file.getPath());
						try {
							if (detailImage != null) {
								detailImage.setImage(ImageSource.uri(file.getPath()));
								detailImage.setZoomEnabled(false);
							}
							if (circleProgress != null) {
								circleProgress.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							getIntroduceImage(MakerDetailActivity.this, makerDetail);
						}

						setNeedBuyView(1);

					}

					@Override
					public void onDownloading(int progress) {
						Timber.e("progress---->%s", progress);
						if (circleProgress != null) {
							circleProgress.setProgress(progress);
						}
						if (detailImageCopy != null) {
							detailImageCopy.setVisibility(View.GONE);
						}


					}

					@Override
					public void onDownloadFailed(Exception e) {

						try {
							if (detailImage != null) {
								detailImage.setVisibility(View.GONE);
							}
							if (circleProgress != null) {
								circleProgress.setVisibility(View.GONE);
							}
							if (detailImageCopy != null) {
								detailImageCopy.setVisibility(View.VISIBLE);
								Glide.with(MakerDetailActivity.this).load(makerDetail.getPictureURL())
										.priority(Priority.HIGH)
										.into(detailImageCopy);

								setNeedBuyView(2);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							getIntroduceImage(MakerDetailActivity.this, makerDetail);
						}


					}
				});
			}
		}

		if (makerDetail.getSubPages() == null) {
			return;
		}

		List<MakerDetail.SubPagesBean> list = makerDetail.getSubPages();

		if (mAdapter == null) {
			mAdapter = new BaseRecyclerViewAdapter<MakerDetail.SubPagesBean>(this, list, R.layout.item_maker_course_video) {
				@Override
				public void convert(BaseRecyclerHolder holder, MakerDetail.SubPagesBean item, int position, boolean isScrolling) {
					if (item == null) {
						return;
					}
					if (isAuth != 1) {
						holder.getView(R.id.needBuyCourseLayout).setVisibility(View.GONE);
					} else {
						holder.getView(R.id.needBuyCourseLayout).setVisibility(View.VISIBLE);
					}
					holder.setImageByUrlWithCorner(R.id.imageView, item.getSubPageURL(),
							R.drawable.defaule_banner, R.drawable.defaule_banner, AutoSizeUtils.pt2px(holder.itemView.getContext(), 16));

					holder.setText(R.id.videoName, item.getName());
					holder.getView(R.id.collectButton).setVisibility(View.GONE);
				}
			};
			recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
			recyclerview.addItemDecoration(new SpaceItemDecoration(AutoSizeUtils.pt2px(this, 10)
					, AutoSizeUtils.pt2px(this, 10)
					, AutoSizeUtils.pt2px(this, 10)
					, AutoSizeUtils.pt2px(this, 10)));
			recyclerview.setAdapter(mAdapter);


			mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(RecyclerView parent, View view, int position) {
					//type 1：详情页面 2：直播；3 视频；4：音频
					if (isAuth == 1) {
						MessageDialog.show(MakerDetailActivity.this, "提示", "请先解锁课程", "解锁").setOnOkButtonClickListener(new OnDialogButtonClickListener() {
							@Override
							public boolean onClick(BaseDialog baseDialog, View v) {
								if (mPresenter != null) {
									mPresenter.unLockSeries(mSeriesId);
								}
								return false;
							}
						});
						return;
					}
					switch (list.get(position).getType()) {
						case 1:
							launchActivity(new Intent(MakerDetailActivity.this, MakerDetailActivity.class).putExtra("pageId", list.get(position).getSubPageId()));
							break;
						case 2:
							launchActivity(new Intent(MakerDetailActivity.this, MakerCourseMeetDetailActivity.class).putExtra("pageId", list.get(position).getSubPageId()));
							break;
						case 3:
							launchActivity(new Intent(MakerDetailActivity.this, MakerCourseVideoDetailActivity.class).putExtra("pageId", list.get(position).getSubPageId()));
							break;
						case 4:
							launchActivity(new Intent(MakerDetailActivity.this, MakerCourseAudioDetailActivity.class).putExtra("pageId", list.get(position).getSubPageId()));
							break;
					}
				}
			});
		} else {
			mAdapter.notifyData(list);
		}

	}

	private void getIntroduceImage(MakerDetailActivity context, MakerDetail makerDetail) {
		if (descriptionImage != null) {
			String fileName = "";
			try {
				fileName = makerDetail.getId() + "_" + makerDetail.getIntroduceURL().substring(makerDetail.getIntroduceURL().lastIndexOf("/") + 1);
			} catch (Exception e) {
				e.printStackTrace();
				fileName = makerDetail.getId() + "_" + makerDetail.getIntroduceURL();
			}

			File file = new File(FileUtils.getPath(context) + fileName);
			if (file.exists()) {
				descriptionImage.setImage(ImageSource.uri(FileUtils.getPath(context) + fileName));
				descriptionImage.setZoomEnabled(false);
				circleProgress1.setVisibility(View.GONE);
				descriptionImage.setVisibility(View.VISIBLE);
			} else {
				DownloadUtil.get().download(this, makerDetail.getIntroduceURL(), FileUtils.getPath(context), fileName, new DownloadUtil.OnDownloadListener() {
					@Override
					public void onDownloadSuccess(File file) {

						Timber.e("下载文件成功---->%s", file.getPath());
						if (descriptionImage != null) {
							descriptionImage.setImage(ImageSource.uri(file.getPath()));
							descriptionImage.setZoomEnabled(false);
						}
						if (circleProgress != null) {
							circleProgress.setVisibility(View.GONE);
						}


					}

					@Override
					public void onDownloading(int progress) {
						Timber.e("progress---->%s", progress);
						if (circleProgress != null) {
							circleProgress.setProgress(progress);
						}
						if (descriptionImageCopy != null) {
							descriptionImageCopy.setVisibility(View.GONE);
						}


					}

					@Override
					public void onDownloadFailed(Exception e) {

						if (descriptionImage != null) {
							descriptionImage.setVisibility(View.GONE);
						}
						if (circleProgress != null) {
							circleProgress.setVisibility(View.GONE);
						}
						if (descriptionImageCopy != null) {
							descriptionImageCopy.setVisibility(View.VISIBLE);
							Glide.with(context).load(makerDetail.getIntroduceURL())
									.priority(Priority.HIGH)
									.into(descriptionImageCopy);
						}

					}
				});
			}
		}

	}

	@Override
	public void signSuccess(boolean isSuccess, int sign) {
		if (isSuccess) {
			if (sign == 0) {
				collectButton.setLiked(false);
			} else {
				collectButton.setLiked(true);
			}
		} else {
			if (sign == 0) {
				collectButton.setLiked(true);
			} else {
				collectButton.setLiked(false);
			}
		}
	}

	private String weChat;

	@Override
	public void unLockSeriesResult(boolean isSuccess, String weChat) {
		if (isSuccess) {
			isAuth = 2;
			mAdapter.notifyDataSetChanged();
			setTextViewDrawableLeft(needBuyCourseTextView, R.drawable.icon_buy2);
			needBuyCourseTextView.setText("解锁成功");
			needBuyCourseLayout.setBackgroundColor(Color.TRANSPARENT);
			ToastUtils.showToast(MakerDetailActivity.this, "解锁成功");
			RxBus.sendMessage(new UnlockEvent(true));
			countDownTimerUtil = new CountDownTimerUtil(3000,1000,this);
			countDownTimerUtil.start();
		} else {
			this.weChat = weChat;
			setTextViewDrawableLeft(needBuyCourseTextView, R.drawable.icon_buy4);
			needBuyCourseTextView.setText("解锁失败");
			ToastUtils.showToast(MakerDetailActivity.this, "解锁失败");
			needbuyHint.setVisibility(View.VISIBLE);
			weChatTextView.setVisibility(View.VISIBLE);
			weChatTextView.setText("微信:" + weChat);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DownloadUtil.get().cancleDownLoad();
		if (countDownTimerUtil != null) {
			countDownTimerUtil.cancel();
		}
	}

	public void setTextViewDrawableLeft(TextView view, int drawable) {
		Drawable left = getResources().getDrawable(drawable);
		view.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);

	}

	public void setNeedBuyView(int type) {
		new Handler().postDelayed(() -> {
			if (isAuth != 0) {
				needBuyCourseLayout.setVisibility(View.VISIBLE);
			} else {
				needBuyCourseLayout.setVisibility(View.GONE);
			}
			if (type == 1) {
				ViewGroup.LayoutParams layoutParams = needBuyCourseLayout.getLayoutParams();
				layoutParams.height = detailImage.getHeight();
				needBuyCourseLayout.setLayoutParams(layoutParams);
			} else {
				ViewGroup.LayoutParams layoutParams = needBuyCourseLayout.getLayoutParams();
				layoutParams.height = detailImageCopy.getHeight();
				needBuyCourseLayout.setLayoutParams(layoutParams);
			}


			switch (isAuth) {
				case 0:
					break;
				case 1://需要授权但未解锁
					needBuyCourseLayout.setBackgroundColor(Color.parseColor("#55000000"));
					setTextViewDrawableLeft(needBuyCourseTextView, R.drawable.icon_buy3);
					needBuyCourseTextView.setText("解锁课程");
					break;
				case 2://需要授权 已经解锁
					needBuyCourseLayout.setBackgroundColor(Color.TRANSPARENT);
					setTextViewDrawableLeft(needBuyCourseTextView, R.drawable.icon_buy2);
					needBuyCourseTextView.setText("解锁成功");
					needBuyCourseLayout.setVisibility(View.GONE);
					break;
			}
		}, 300);
	}

	@Override
	public void onTimeFinish() {
		needBuyCourseLayout.setVisibility(View.GONE);
	}

	@Override
	public void onTimeTick(long time) {

	}
}

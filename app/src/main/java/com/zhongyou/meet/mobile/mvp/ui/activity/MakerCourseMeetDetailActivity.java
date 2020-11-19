package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.allen.library.SuperButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.di.component.DaggerMakerCourseMeetDetailComponent;
import com.zhongyou.meet.mobile.entiy.LiveData;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseMeetDetailContract;
import com.zhongyou.meet.mobile.mvp.presenter.MakerCourseMeetDetailPresenter;
import com.zhongyou.meet.mobile.utils.DownloadUtil;
import com.zhongyou.meet.mobile.utils.FileUtils;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.Page;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.subscaleview.ImageSource;
import io.rong.subscaleview.SubsamplingScaleImageView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/17/2020 17:45
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "直播详情页面")
public class MakerCourseMeetDetailActivity extends BaseActivity<MakerCourseMeetDetailPresenter> implements MakerCourseMeetDetailContract.View {

	@BindView(R.id.toolbar_title)
	TextView toolbarTitle;
	@BindView(R.id.signCourse)
	SuperButton signCourse;
	@BindView(R.id.enterMeeting)
	SuperButton enterMeeting;
	@BindView(R.id.descriptionImage)
	SubsamplingScaleImageView webContainer;
	@BindView(R.id.topImage)
	ImageView topImage;
	@BindView(R.id.circle_progress)
	CircleProgress circleProgress;
	@BindView(R.id.imageView)
	ImageView imageView;
	private String mPageId;
	private LiveData mMeeting;

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {
		DaggerMakerCourseMeetDetailComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);
	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		return R.layout.activity_maker_course_meet_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {
		mPageId = getIntent().getStringExtra("pageId");
		if (mPresenter != null && !TextUtils.isEmpty(mPageId)) {
			mPresenter.getDatail(mPageId);
		} else {
			showMessage("数据错误 请重试");
		}

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
	public Activity getActivity() {
		return this;
	}

	@Override
	public void getDataSuccess(LiveData data) {
		mMeeting = data;
		if (webContainer != null) {

			String fileName = "";
			try {
				fileName = data.getLiveId() + "_" + data.getIntroduceURL().substring(data.getIntroduceURL().lastIndexOf("/") + 1);
			} catch (Exception e) {
				e.printStackTrace();
				fileName = data.getLiveId() + "_" + data.getIntroduceURL();
			}
			File file = new File(FileUtils.getPath(this) + fileName);
			if (file.exists()) {
				webContainer.setImage(ImageSource.uri(FileUtils.getPath(this) + fileName));
				webContainer.setZoomEnabled(file.length() >= Constant.KEY_BIG_FILE);
				circleProgress.setVisibility(View.GONE);
				webContainer.setVisibility(View.VISIBLE);
			} else {
				DownloadUtil.get().download(this, data.getIntroduceURL(), FileUtils.getPath(this), fileName, new DownloadUtil.OnDownloadListener() {
					@Override
					public void onDownloadSuccess(File file) {

						Timber.e("下载文件成功---->%s", file.getPath());
						if (webContainer != null) {
							webContainer.setImage(ImageSource.uri(file.getPath()));
							webContainer.setZoomEnabled(file.length() >= Constant.KEY_BIG_FILE);
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
						if (imageView != null) {
							imageView.setVisibility(View.GONE);
						}


					}

					@Override
					public void onDownloadFailed(Exception e) {

						if (webContainer != null) {
							webContainer.setVisibility(View.GONE);
						}
						if (circleProgress != null) {
							circleProgress.setVisibility(View.GONE);
						}
						if (imageView != null) {
							imageView.setVisibility(View.VISIBLE);
							Glide.with(MakerCourseMeetDetailActivity.this).load(data.getIntroduceURL())
									.priority(Priority.HIGH)
									.into(imageView);
						}

					}
				});
			}


		}
		((TextView) findViewById(R.id.title)).setText(data.getName());
		toolbarTitle.setVisibility(View.GONE);
		if (signCourse != null) {
			signCourse.setVisibility(data.getIsNeedRecord() == 1 ? View.VISIBLE : View.GONE);
		}
		if (data.getIsRecord() == 1) {//已经打卡
			signCourse.setEnabled(false);
			signCourse.setText("已打卡");
			signCourse.setTextColor(getResources().getColor(R.color.gray));
			signCourse.setShapeSelectorNormalColor(getResources().getColor(R.color.white))
					.setUseShape();
		} else {
			signCourse.setEnabled(true);
		}
		if (topImage != null) {
			Glide.with(this).asBitmap().load(data.getTopUrl()).into(topImage);
		}

	}

	@Override
	public void commitQuestionSuccess(boolean isSuccess) {
		if (isSuccess) {
			signCourse.setEnabled(false);
			signCourse.setText("已打卡");
			signCourse.setTextColor(getResources().getColor(R.color.gray));
			signCourse.setShapeSelectorNormalColor(getResources().getColor(R.color.white))
					.setUseShape();
		} else {
			signCourse.setEnabled(true);
		}
	}

	@OnClick({R.id.signCourse, R.id.enterMeeting})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.signCourse:
				if (mPresenter != null && !TextUtils.isEmpty(mPageId)) {
					mPresenter.getQuestion(mPageId);
				} else {
					showMessage("数据出错 请重试");
				}
				break;
			case R.id.enterMeeting:
				if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
					mPresenter.showPhoneStatus(1, mMeeting, mMeeting.getIsToken());
					return;
				}
				if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
					mPresenter.showPhoneStatus(2, mMeeting, mMeeting.getIsToken());
					return;
				}
				if (mPresenter != null && mMeeting != null) {
					try {
						if (Double.parseDouble(String.valueOf(mMeeting.getIsToken())) == 1.0D) {//需要加入码的

							JoinMeetingDialog.INSTANCE.showNeedCodeDialog(this, mMeeting.getLiveId());


						} else {

							JoinMeetingDialog.INSTANCE.showNoCodeDialog(this, mMeeting.getLiveId());

						}
					} catch (Exception e) {

						JoinMeetingDialog.INSTANCE.showNeedCodeDialog(this, mMeeting.getLiveId());

					}
				}
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DownloadUtil.get().cancleDownLoad();
	}
}

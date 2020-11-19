package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.allen.library.SuperButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.Target;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.xw.repo.BubbleSeekBar;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.core.CommonUtils;
import com.zhongyou.meet.mobile.core.PlayService;
import com.zhongyou.meet.mobile.di.component.DaggerMakerCourseAudioDetailComponent;
import com.zhongyou.meet.mobile.entiy.AudioData;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioDetailContract;
import com.zhongyou.meet.mobile.mvp.presenter.MakerCourseAudioDetailPresenter;
import com.zhongyou.meet.mobile.utils.DownLoadUtils;
import com.zhongyou.meet.mobile.utils.DownloadUtil;
import com.zhongyou.meet.mobile.utils.FileUtils;
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
 * Created by MVPArmsTemplate on 04/09/2020 10:55
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "创客音频播放详情界面")
public class MakerCourseAudioDetailActivity extends BaseActivity<MakerCourseAudioDetailPresenter> implements MakerCourseAudioDetailContract.View {

	@BindView(R.id.signCourse)
	SuperButton signCourse;
	@BindView(R.id.title)
	TextView title;
	@BindView(R.id.playState)
	ImageView playState;
	@BindView(R.id.seekBar)
	BubbleSeekBar seekBar;
	@BindView(R.id.audioTime)
	TextView audioTime;
	@BindView(R.id.webContainer)
	SubsamplingScaleImageView webContainer;
	@BindView(R.id.toolbar_title)
	TextView toolbarTitle;

	private static PlayService.MusicBinder mMusicBinder;
	@BindView(R.id.circle_progress)
	CircleProgress circleProgress;
	@BindView(R.id.imageView)
	ImageView imageView;
	private Animation mOperatingAnim;
	private String mPageId;
	private String mSources;
	private int tag = -1;
	private String mResourceTime;


	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {
		DaggerMakerCourseAudioDetailComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);
	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		return R.layout.activity_maker_course_audio_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {

		try {
			if (mPresenter != null) {
				mPresenter.registerAudioReceiver();
			}


			mPageId = getIntent().getStringExtra("pageId");

			if (mPresenter != null) {
				mPresenter.getDatail(mPageId);
			}
			mOperatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
			LinearInterpolator lin = new LinearInterpolator();
			mOperatingAnim.setInterpolator(lin);


			seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
				@Override
				public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
					if (fromUser && mMusicBinder != null) {
						mMusicBinder.seekTo(progress);
					}
				}

				@Override
				public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
				}

				@Override
				public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

				}
			});
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
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
	protected void onDestroy() {
		super.onDestroy();
		if (mPresenter != null) {
			mPresenter.unregisterReceiver();
		}
		if (mMusicBinder != null) {
			mMusicBinder.pause();
		}
		DownloadUtil.get().cancleDownLoad();

	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void upDateAudioProgress(int posotion, long duration, long total) {
		audioTime.setText(CommonUtils.getFormattedTime(duration) + "/" + mResourceTime);
		seekBar.getConfigBuilder().max(total).progress(duration).build();
	}

	@Override
	public void pauseMusic(int position) {
		playState.setImageResource(R.drawable.icon_detail_audio_play);
	}

	@Override
	public void startMusic(int position) {
		mOperatingAnim.cancel();
		playState.clearAnimation();
		playState.setImageResource(R.drawable.icon_detail_audio_pause);

	}

	@Override
	public void completeMusic(int position) {
		playState.setImageResource(R.drawable.icon_detail_audio_play);
		audioTime.setText("00:00/" + mResourceTime);
		seekBar.getConfigBuilder().max(100).progress(0).build();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		/*mMusicBinder = (PlayService.MusicBinder) binder;
		mMusicBinder.resetMediaPlayer();*/
	}

	public static void setMusicBinder(PlayService.MusicBinder binder) {
		mMusicBinder = binder;

	}

	@Override
	public void getDataSuccess(AudioData audioData) {
		title.setText(audioData.getName());
		if (signCourse != null) {
			signCourse.setVisibility(audioData.getIsNeedRecord() == 1 ? View.VISIBLE : View.GONE);
		}
		if (!TextUtils.isEmpty(audioData.getName())) {
			toolbarTitle.setText("");
		}
		mSources = audioData.getResourceURL();
		mResourceTime = audioData.getResourceTime();
		audioTime.setText("00:00/" + mResourceTime);
		String fileName = "";
		try {
			fileName = audioData.getId() + "_" + audioData.getIntroduceURL().substring(audioData.getIntroduceURL().lastIndexOf("/") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			fileName = audioData.getId() + "_" + audioData.getIntroduceURL();
		}
		File file = new File(FileUtils.getPath(this) + fileName);
		if (file.exists()) {
			webContainer.setImage(ImageSource.uri(FileUtils.getPath(this) + fileName));
			webContainer.setZoomEnabled(file.length() >= Constant.KEY_BIG_FILE);
			circleProgress.setVisibility(View.GONE);
			webContainer.setVisibility(View.VISIBLE);
		} else {
			DownloadUtil.get().download(this, audioData.getIntroduceURL(), FileUtils.getPath(this), fileName, new DownloadUtil.OnDownloadListener() {
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
						Glide.with(MakerCourseAudioDetailActivity.this).load(audioData.getIntroduceURL())
								.priority(Priority.HIGH)
								.into(imageView);
					}

				}
			});
		}

		/*Glide.with(this).asBitmap().load(audioData.getIntroduceURL())
				.priority(Priority.HIGH).into(webContainer);*/

		if (audioData.getIsRecord() == 1) {//已经打卡
			signCourse.setEnabled(false);
			signCourse.setText("已打卡");
			signCourse.setTextColor(getResources().getColor(R.color.gray));
			signCourse.setShapeSelectorNormalColor(getResources().getColor(R.color.white))
					.setUseShape();
		} else {
			signCourse.setEnabled(true);
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

	@OnClick({R.id.signCourse, R.id.playState})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.signCourse:
				if (mPresenter != null) {
					if (!TextUtils.isEmpty(mPageId)) {
						mPresenter.getQuestion(mPageId);
					} else {
						showMessage("当前暂无打卡信息");
					}
				}
				break;
			case R.id.playState:
				if (tag == -1) {
					if (mOperatingAnim != null) {
						playState.setImageResource(R.drawable.icon_audio_loading);
						playState.startAnimation(mOperatingAnim);
					}
					if (mMusicBinder != null) {
						mMusicBinder.playMusic(mSources);
					}
					tag = 1;
					if (mPresenter != null) {
						mPresenter.setTag(tag);
					}
					return;
				}
				if (mMusicBinder != null) {
					Timber.e("isplaying:==%s  currentDuration%s", mMusicBinder.isPlaying(), mMusicBinder.getCurrentDutation());
					if (mMusicBinder.isPlaying()) {
						mMusicBinder.pause();
					} else if (mMusicBinder.getCurrentDutation() == -1 || mMusicBinder.getCurrentDutation() == 0) {
						playState.setImageResource(R.drawable.icon_audio_loading);
						if (mOperatingAnim != null) {
							playState.startAnimation(mOperatingAnim);
						}
						mMusicBinder.playMusic(mSources);
					} else {
						mMusicBinder.start();
					}
				}
				break;
		}
	}


}

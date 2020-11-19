package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.allen.library.SuperButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.like.LikeButton;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MToast;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.core.GlobalConsts;
import com.zhongyou.meet.mobile.core.PlayService;
import com.zhongyou.meet.mobile.di.component.DaggerMakerCourseVideoDetailComponent;
import com.zhongyou.meet.mobile.entiy.AudioData;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseVideoDetailContract;
import com.zhongyou.meet.mobile.mvp.presenter.MakerCourseVideoDetailPresenter;
import com.zhongyou.meet.mobile.utils.DownloadUtil;
import com.zhongyou.meet.mobile.utils.FileUtils;
import com.zhongyou.meet.mobile.utils.Page;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import io.agora.openlive.ui.MeetingInitActivity;
import io.rong.subscaleview.ImageSource;
import io.rong.subscaleview.SubsamplingScaleImageView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/07/2020 15:13
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Page(name = "创客详情播放页面")
public class MakerCourseVideoDetailActivity extends BaseActivity<MakerCourseVideoDetailPresenter> implements MakerCourseVideoDetailContract.View {


	/*@BindView(R.id.collectButton)
	LikeButton collectButton;*/
	/*@BindView(R.id.signCourse)
	SuperButton signCourse;*/
	@BindView(R.id.videoplayer)
	JzvdStd videoplayer;
	@BindView(R.id.webContainer)
	SubsamplingScaleImageView webContainer;
	@BindView(R.id.circle_progress)
	CircleProgress circleProgress;
	@BindView(R.id.imageView)
	ImageView imageView;

	@BindView(R.id.tv_name)
	AppCompatTextView tv_name;
	@BindView(R.id.tv_time_author)
	AppCompatTextView tv_time_author;
	@BindView(R.id.play_m)
	AppCompatImageView play_m;

	private String mSources;
	private String mPageId;
	private MusicInfoReceiver mReceiver;
	private static PlayService.MusicBinder mMusicBinder;
	private ServiceConnection mServiceConn;


	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {
		DaggerMakerCourseVideoDetailComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);
	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {

		return R.layout.activity_maker_course_video_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {

		mPageId = getIntent().getStringExtra("pageId");
//		refreshLayout.setRefreshHeader(new MaterialHeader(BaseApplication.getInstance()));

		if (mPresenter != null && !TextUtils.isEmpty(mPageId)) {
			mPresenter.getDatail(mPageId);
		} else {
			showMessage("数据错误 请稍后再试");
		}
//		registerReceiver();

		/*boolean isSigned=true;
		if(isSigned){
			signCourse.setTextColor(getResources().getColor(R.color.gray));
			signCourse.setShapeSelectorNormalColor(getResources().getColor(R.color.white))
					.setUseShape();

		}*/

//		webView.loadUrl("https://blog.csdn.net/yonghuming_jesse/article/details/80027487");


		ImmersionBar.with(this)
				.fullScreen(true)
//				.transparentStatusBar()
				.fitsSystemWindows(true)
//				.hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
//				.statusBarColor(R.color.white)
				.statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
//				.flymeOSStatusBarFontColor(R.color.black)  //修改flyme OS状态栏字体颜色
				.navigationBarColor(R.color.white) //导航栏颜色，不写默认黑色
				.navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
				.init();


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


	@OnClick({R.id.signCourse,R.id.play_m})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.signCourse:
				if (mPresenter != null && !TextUtils.isEmpty(mPageId)) {

					mPresenter.getQuestion(mPageId);
				} else {
					showMessage("数据错误 请稍后再试");
				}
				break;
			case R.id.play_m:
				mMusicBinder.pause();
				mMusicBinder.playMusic(mSources);
				break;
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		Jzvd.releaseAllVideos();
	}

	@Override
	public void getDataSuccess(AudioData audioData) {

	/*	if (signCourse != null) {
			signCourse.setVisibility(audioData.getIsNeedRecord() == 1 ? View.VISIBLE : View.GONE);
		}*/
		mSources = audioData.getResourceURL();
		videoplayer.setUp(audioData.getResourceURL()
				, audioData.getName());
		videoplayer.backButton.setVisibility(View.VISIBLE);
		videoplayer.thumbImageView.setVisibility(View.VISIBLE);

		videoplayer.setOnBackPressListener(this::finish);
		videoplayer.setStartVideo(new JzvdStd.onStartVideo() {
			@Override
			public void onStart(boolean isStart) {
				if (isStart) {
					mMusicBinder.pause();
					mMusicBinder.setIsOtherPause(false);
				}
			}
		});
		videoplayer.setOnPlayTimeChangeListener(new JzvdStd.OnPlayTimeChange() {
			@Override
			public void onPlayTimeChange(int progress, long position, long duration) {
				if (mMusicBinder.isPlaying()) {
					mMusicBinder.pause();
				}
			}
		});
		if (audioData.getPictureURL() != null) {
			Glide.with(this).load(audioData.getPictureURL())
					.into(videoplayer.thumbImageView);
		} else {
			Glide.with(this).load(audioData.getResourceURL())
					.into(videoplayer.thumbImageView);
		}



		String fileName = "";
		try {
			fileName = audioData.getId() + "_" + audioData.getIntroduceURL().substring(audioData.getIntroduceURL().lastIndexOf("/") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			fileName = audioData.getId() + "_" + audioData.getIntroduceURL();
		}

		File file = new File(getPath() + fileName);
		if (file.exists()) {
			webContainer.setImage(ImageSource.uri(getPath() + fileName));
			webContainer.setZoomEnabled(file.length() >= Constant.KEY_BIG_FILE);
			circleProgress.setVisibility(View.GONE);
			webContainer.setVisibility(View.VISIBLE);
		} else {

			DownloadUtil.get().download(this, audioData.getIntroduceURL(), getPath(), fileName, new DownloadUtil.OnDownloadListener() {
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

					if (MakerCourseVideoDetailActivity.this.isFinishing()) {
						return;
					}
					if (webContainer != null) {
						webContainer.setVisibility(View.GONE);
					}
					if (circleProgress != null) {
						circleProgress.setVisibility(View.GONE);
					}
					if (imageView != null && !MakerCourseVideoDetailActivity.this.isDestroyed()) {
						imageView.setVisibility(View.VISIBLE);
						Glide.with(MakerCourseVideoDetailActivity.this).load(audioData.getIntroduceURL())
								.priority(Priority.HIGH)
								.into(imageView);
					}

				}
			});
		}






		/*if (audioData.getIsRecord() == 1) {//已经打卡
			signCourse.setEnabled(false);
			signCourse.setText("已打卡");
			signCourse.setTextColor(getResources().getColor(R.color.gray));
			signCourse.setShapeSelectorNormalColor(getResources().getColor(R.color.white))
					.setUseShape();
		} else {
			signCourse.setEnabled(true);
		}*/
	}

	@Override
	public void commitQuestionSuccess(boolean isSuccess) {
		/*if (isSuccess) {
			signCourse.setEnabled(false);
			signCourse.setText("已打卡");
			signCourse.setTextColor(getResources().getColor(R.color.gray));
			signCourse.setShapeSelectorNormalColor(getResources().getColor(R.color.white))
					.setUseShape();
		} else {
			signCourse.setEnabled(true);
		}*/

	}

	@Override
	public Activity getActivity() {
		return this;
	}

	private String getPath() {
		String path = UpdateUtils.getFilePath(getApplicationContext()) + "Luban/image/";
		File file = new File(path);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return path;
			}
		}
		return path;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DownloadUtil.get().cancleDownLoad();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
		if (mServiceConn!=null){
			unbindService(mServiceConn);
		}
	}

	public static void setMusicBinder(PlayService.MusicBinder binder) {
		mMusicBinder = binder;
	}

	private void registerReceiver() {
		mReceiver = new MusicInfoReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
		filter.addAction(GlobalConsts.ACTION_UPDATE_PROGRESS);
		filter.addAction(GlobalConsts.ACTION_STATR_MUSIC);
		filter.addAction(GlobalConsts.ACTION_PAUSE_MUSIC);
		filter.addAction(GlobalConsts.ACTION_LOCAL_MUSIC);
		filter.addAction(GlobalConsts.ACTION_ONLINE_MUSIC);
		filter.addAction(GlobalConsts.ACTION_NEXT_MUSIC);
		filter.addAction(GlobalConsts.ACTION_COMPLETE_MUSIC);

		registerReceiver(mReceiver, filter);
		//播放服务
		Intent intent = new Intent(this, PlayService.class);
		mServiceConn = new ServiceConnection() {


			//连接异常断开
			public void onServiceDisconnected(ComponentName name) {

			}

			//连接成功
			public void onServiceConnected(ComponentName name, IBinder binder) {
				mMusicBinder = (PlayService.MusicBinder) binder;
//				mMusicBinder.pause();
			}
		};
		bindService(intent, mServiceConn, Service.BIND_AUTO_CREATE);
	}

	class MusicInfoReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null && action.equals(GlobalConsts.ACTION_UPDATE_PROGRESS)) {
				if (mMusicBinder != null) {
//					mMusicBinder.pause();
				}
			}
		}

	}
}

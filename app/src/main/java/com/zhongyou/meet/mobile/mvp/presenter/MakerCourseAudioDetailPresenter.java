package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allen.library.SuperButton;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.core.GlobalConsts;
import com.zhongyou.meet.mobile.entiy.AudioData;
import com.zhongyou.meet.mobile.entiy.Question;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseAudioDetailContract;

import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import timber.log.Timber;


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
@ActivityScope
public class MakerCourseAudioDetailPresenter extends BasePresenter<MakerCourseAudioDetailContract.Model, MakerCourseAudioDetailContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;
	private MusicInfoReceiver receiver;
	private Dialog mDialog;


	@Inject
	public MakerCourseAudioDetailPresenter(MakerCourseAudioDetailContract.Model model, MakerCourseAudioDetailContract.View rootView) {
		super(model, rootView);
	}


	public void getDatail(String pageId) {
		if (mModel != null) {
			mModel.getDatail(pageId, "")
					.compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								AudioData data = JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), AudioData.class);
								mRootView.getDataSuccess(data);
							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}


	public void registerAudioReceiver() {
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
		mApplication.registerReceiver(receiver, filter);
		//播放服务
	}


	private int tag = -1;

	public void setTag(int tag) {
		this.tag = tag;
	}

	class MusicInfoReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Timber.e(action);
			if (tag==-1){
				return;
			}
			if (action == null || mRootView == null) {
				return;
			}
			if (action.equals(GlobalConsts.ACTION_UPDATE_PROGRESS)) {

				int current = intent.getIntExtra("current", 0);
				int total = intent.getIntExtra("total", 0);
				/*mAudioList.get(clickPosition).setCurrentDuration(current);
				mAdapter.notifyItemRangeChanged(clickPosition, 1);*/
				mRootView.upDateAudioProgress(0, current, total);

			} else if (GlobalConsts.ACTION_LOCAL_MUSIC.equals(action)) {


			} else if (GlobalConsts.ACTION_PAUSE_MUSIC.equals(action)) {
				//音乐暂停播放
				mRootView.pauseMusic(0);
			} else if (GlobalConsts.ACTION_STATR_MUSIC.equals(action)) {
				//只有点暂停和播放按钮时
				mRootView.startMusic(0);

			} else if (GlobalConsts.ACTION_ONLINE_MUSIC.equals(action)) {

			} else if (GlobalConsts.ACTION_NEXT_MUSIC.equals(action)) {

			} else if (GlobalConsts.ACTION_MUSIC_STARTED.equals(action)) {
				//音乐开始播放
				/*mAudioList.get(clickPosition).setType(1);
				mOperatingAnim.cancel();
				mAdapter.notifyDataSetChanged();*/
				mRootView.startMusic(0);

			} else if (GlobalConsts.ACTION_COMPLETE_MUSIC.equals(action)) {
				//播放完成
				/*mAudioList.get(clickPosition).setType(0);
				mAudioList.get(clickPosition).setCurrentDuration(0);
				mAdapter.notifyDataSetChanged();*/
				mRootView.completeMusic(0);
			}
		}

	}


	public void getQuestion(String pageId) {
		if (mModel != null) {
			mModel.getQuestion(pageId)
					.compose(RxSchedulersHelper.io_main())
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.subscribe(new RxSubscriber<JSONObject>() {
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								List<Question> data = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), Question.class);
								showSignScoreDialog(data, pageId);
							} else {
								mRootView.showMessage(jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}

	private void showSignScoreDialog(List<Question> data, String pageId) {
		if (mRootView == null) {
			return;
		}
		if (mDialog == null) {
			mDialog = new Dialog(mRootView.getActivity(), R.style.MyDialog);
		}

		for (Question datum : data) {
			datum.setScore(1.0f);
		}

		View v = LayoutInflater.from(mRootView.getActivity()).inflate(R.layout.dialog_sign_score, null, false);
		RecyclerView recyclerView = v.findViewById(R.id.recyclerview);
		EditText evaluteEd = v.findViewById(R.id.writeWorldsEd);

		recyclerView.setLayoutManager(new LinearLayoutManager(mRootView.getActivity()));
		recyclerView.setAdapter(new BaseRecyclerViewAdapter<Question>(mRootView.getActivity(), data, R.layout.item_dialog_sign_score) {

			@Override
			public void convert(BaseRecyclerHolder holder, Question item, int position, boolean isScrolling) {
				holder.setText(R.id.title, (position + 1) + ". " + item.getName());
				ScaleRatingBar simpleRatingBar = holder.getView(R.id.simpleRatingBar);
				simpleRatingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
					@Override
					public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
						item.setScore(rating);
					}
				});
				if (position == data.size() - 1) {
					holder.getView(R.id.line).setVisibility(View.INVISIBLE);
				}
			}
		});

		SuperButton commitButton = v.findViewById(R.id.commitButton);
		commitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject jsonObject = new JSONObject();
				for (int i = 0; i < data.size(); i++) {
					if (i == 0) {
						jsonObject.put("firstQuestionGrade", data.get(i).getScore());
					} else if (i == 1) {
						jsonObject.put("secondQuestionGrade", data.get(i).getScore());
					} else if (i == 2) {
						jsonObject.put("thirdQuestionGrade", data.get(i).getScore());
					}
				}
				jsonObject.put("pageId", pageId);
				jsonObject.put("evaluate", evaluteEd.getText().toString().trim());
				if (mModel != null) {
					mModel.commitQuestion(jsonObject)
							.compose(RxSchedulersHelper.io_main())
							.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
							.subscribe(new RxSubscriber<JSONObject>() {
								@Override
								public void _onNext(JSONObject jsonObject) {

									if (jsonObject.getInteger("errcode") == 0) {
										mRootView.commitQuestionSuccess(true);
										mDialog.dismiss();
									} else {
										mRootView.commitQuestionSuccess(false);
									}
									mRootView.showMessage(jsonObject.getString("errmsg"));
								}

								@Override
								public void _onError(int code, String msg) {
									super._onError(code, msg);
									mRootView.commitQuestionSuccess(false);
								}
							});
				}

			}
		});
		mDialog.setContentView(v);
		mDialog.show();
	}


	public void unregisterReceiver() {
		if (receiver != null && mApplication != null) {
			mApplication.unregisterReceiver(receiver);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDialog != null) {
			mDialog.dismiss();
		}
		this.mErrorHandler = null;
		this.mAppManager = null;
		this.mImageLoader = null;
		this.mApplication = null;
	}
}

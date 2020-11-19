package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;
import android.app.Dialog;
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
import com.zhongyou.meet.mobile.entiy.AudioData;
import com.zhongyou.meet.mobile.entiy.Question;
import com.zhongyou.meet.mobile.mvp.contract.MakerCourseVideoDetailContract;

import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;


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
@ActivityScope
public class MakerCourseVideoDetailPresenter extends BasePresenter<MakerCourseVideoDetailContract.Model, MakerCourseVideoDetailContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;
	private Dialog mDialog;
	private BaseRecyclerViewAdapter<Question> mAdapter;

	@Inject
	public MakerCourseVideoDetailPresenter(MakerCourseVideoDetailContract.Model model, MakerCourseVideoDetailContract.View rootView) {
		super(model, rootView);
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

	private void showSignScoreDialog(List<Question> data, String pageId) {
		if (mRootView == null) {
			return;
		}

		mDialog = new Dialog(mRootView.getActivity(), R.style.MyDialog);

		for (Question datum : data) {
			datum.setScore(1.0f);
		}

		View v = LayoutInflater.from(mRootView.getActivity()).inflate(R.layout.dialog_sign_score, null, false);
		RecyclerView recyclerView = v.findViewById(R.id.recyclerview);
		EditText evaluteEd = v.findViewById(R.id.writeWorldsEd);

		recyclerView.setLayoutManager(new LinearLayoutManager(mRootView.getActivity()));

		mAdapter = new BaseRecyclerViewAdapter<Question>(mRootView.getActivity(), data, R.layout.item_dialog_sign_score) {

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
		};
		recyclerView.setAdapter(mAdapter);


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
										mRootView.showMessage(jsonObject.getString("errmsg"));
										mRootView.commitQuestionSuccess(false);
									}
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

package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;
import android.view.Gravity;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.maning.mndialoglibrary.MToast;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.entiy.MessageNotify;
import com.zhongyou.meet.mobile.mvp.contract.MessageContract;
import com.zhongyou.meet.mobile.utils.MMKVHelper;

import java.util.List;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import q.rorbin.badgeview.QBadgeView;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 20:09
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MessagePresenter extends BasePresenter<MessageContract.Model, MessageContract.View> {
	@Inject
	RxErrorHandler mErrorHandler;
	@Inject
	Application mApplication;
	@Inject
	ImageLoader mImageLoader;
	@Inject
	AppManager mAppManager;

	@Inject
	public MessagePresenter(MessageContract.Model model, MessageContract.View rootView) {
		super(model, rootView);
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	public void getNoticeMessageTypeByUserId() {
		if (mModel != null) {
			mModel.getNoticeMessageTypeByUserId(MMKV.defaultMMKV().decodeString(MMKVHelper.ID))
					.compose(RxLifecycleUtils.bindToLifecycle(mRootView))
					.compose(RxSchedulersHelper.io_main())
					.subscribe(new RxSubscriber<JSONObject>() {
						
						@Override
						public void _onNext(JSONObject jsonObject) {
							if (jsonObject.getInteger("errcode") == 0) {
								JSONArray data = jsonObject.getJSONArray("data");
								List<MessageNotify> messageNotifies = JSONArray.parseArray(data.toJSONString(), MessageNotify.class);
								if (mRootView != null) {
									mRootView.getMessageComplete(messageNotifies);
								}
							} else {
								MToast.makeTextShort(jsonObject.getString("errmsg"));
							}
						}
					});
		}
	}

	public <T> BaseRecyclerViewAdapter<T> initAdapter(List<T> datas) {
		return new BaseRecyclerViewAdapter<T>(mRootView.getActivity(), datas, R.layout.item_message) {
			@Override
			public void convert(BaseRecyclerHolder holder, T item, int position, boolean isScrolling) {
				if (item instanceof MessageNotify) {
					MessageNotify notify = (MessageNotify) item;
					new QBadgeView(mRootView.getActivity())
							.bindTarget(holder.getView(R.id.RelativeLayout))
							.setGravityOffset(10, 10, true)
							.setBadgeGravity(Gravity.END | Gravity.BOTTOM).setBadgeNumber(notify.getNumber());

					if (notify.getType() == 1) {
						holder.setImageResource(R.id.image, R.drawable.meet_notify);
						holder.setText(R.id.textTitle, "教室通知");
					} else if (notify.getType() == 2) {
						holder.setImageResource(R.id.image, R.drawable.maker_notify);
						holder.setText(R.id.textTitle, "创客通知");
					} else if (notify.getType() == 3) {
						holder.setImageResource(R.id.image, R.drawable.system_notify);
						holder.setText(R.id.textTitle, "系统通知");
					}

					holder.setText(R.id.time, notify.getTime());
					holder.setText(R.id.message, notify.getNotice());

					if (position == datas.size() - 1) {
						holder.getView(R.id.line).setVisibility(View.GONE);
					}

				}
			}
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mErrorHandler = null;
		this.mAppManager = null;
		this.mImageLoader = null;
		this.mApplication = null;
	}
}

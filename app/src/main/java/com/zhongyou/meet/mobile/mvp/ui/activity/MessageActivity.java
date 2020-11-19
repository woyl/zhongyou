package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.di.component.DaggerMessageComponent;
import com.zhongyou.meet.mobile.entiy.MessageNotify;
import com.zhongyou.meet.mobile.mvp.contract.MessageContract;
import com.zhongyou.meet.mobile.mvp.presenter.MessagePresenter;
import com.zhongyou.meet.mobile.utils.Page;

import java.util.List;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


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
@Page(name = "消息中心")
public class MessageActivity extends BaseActivity<MessagePresenter> implements MessageContract.View {

	@BindView(R.id.toolbar_back)
	RelativeLayout toolbarBack;
	@BindView(R.id.toolbar_title)
	TextView toolbarTitle;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.recyclerview)
	RecyclerView recyclerview;

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {
		DaggerMessageComponent //如找不到该类,请编译一下项目
				.builder()
				.appComponent(appComponent)
				.view(this)
				.build()
				.inject(this);

	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		setTitle("消息中心");
		return R.layout.activity_message; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {

		/*if (mPresenter != null) {
			mPresenter.getNoticeMessageTypeByUserId();
		}*/
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
	public void getMessageComplete(List<MessageNotify> lists) {
		if (mPresenter != null) {
			for (int i = 1; i <= lists.size(); i++) {
				if (TextUtils.isEmpty(lists.get(i - 1).getTime())) {
					lists.get(i - 1).setType(i);
				}
			}

			BaseRecyclerViewAdapter<MessageNotify> messageNotifyBaseRecyclerViewAdapter = mPresenter.initAdapter(lists);
			recyclerview.setAdapter(messageNotifyBaseRecyclerViewAdapter);
			messageNotifyBaseRecyclerViewAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(RecyclerView parent, View view, int position) {
					launchActivity(new Intent(MessageActivity.this, MessageDetailActivity.class)
							.putExtra("type", lists.get(position).getType() + ""));
				}
			});
		}
	}
}

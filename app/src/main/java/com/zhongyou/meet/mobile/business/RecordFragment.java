package com.zhongyou.meet.mobile.business;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.IM.ConversationListAdapterEx;
import com.zhongyou.meet.mobile.IM.ConversionListActivity;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.adapter.GuideLogAdapter;
import com.zhongyou.meet.mobile.entities.RecordData;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;

import java.util.HashMap;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;

import static com.zhongyou.meet.mobile.business.MeetingsFragment.KEY_MEETING_TYPE;


/**
 * Created by wufan on 2017/7/26.
 */

public class RecordFragment extends BaseFragment {

	private SmartRefreshLayout mSwipeRefreshLayout;
	private TextView emptyText;

	private int mTotalPage = -1;
	private int mPageNo = -1;
	private TextView mSearchView;
	private ConversationListAdapterEx conversationListAdapterEx;


	public static RecordFragment newInstance() {
		RecordFragment fragment = new RecordFragment();
		return fragment;
	}

	@Override
	public String getStatisticsTag() {
		return "";
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.record_fragment, null, false);
		mSwipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout);
		mSwipeRefreshLayout.setEnableLoadMore(false);
		mSwipeRefreshLayout.setEnableRefresh(false);
		emptyText = view.findViewById(R.id.emptyView);


		mSearchView = view.findViewById(R.id.input_keyword);


		ConversationListFragment fragement = new ConversationListFragment();


//		fragement.setAdapter(new ConversationListAdapterEx(getActivity()));
		Uri uri = Uri.parse("rong://" + BaseApplication.getInstance().getApplicationInfo().packageName).buildUpon()
				.appendPath("conversationlist")
				.appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.CHATROOM.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.CUSTOMER_SERVICE.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")
				.build();

		fragement.setUri(uri);

		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.rong_content, fragement);
		transaction.commit();

//        RongIM.getInstance().startConversation(getActivity(),Conversation.ConversationType.GROUP, "1234", "安卓测试");

		mSearchView.setOnClickListener(v -> {
			/*HashMap<String, Boolean> hashMap = new HashMap<>();
			//会话类型 以及是否聚合显示
			hashMap.put(Conversation.ConversationType.GROUP.getName(),false);
			hashMap.put(Conversation.ConversationType.PRIVATE.getName(),false);
			hashMap.put(Conversation.ConversationType.PUSH_SERVICE.getName(),true);
			hashMap.put(Conversation.ConversationType.SYSTEM.getName(),true);
			RongIM.getInstance().startConversationList(getActivity() , hashMap);*/

			startActivity(new Intent(getActivity(),MeetingSearchActivity.class));

		});


		return view;
	}


}

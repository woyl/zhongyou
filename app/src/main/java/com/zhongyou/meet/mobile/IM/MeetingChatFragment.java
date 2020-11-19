package com.zhongyou.meet.mobile.IM;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;

import io.rong.imkit.RongExtension;
import io.rong.imkit.fragment.ConversationFragment;


public class MeetingChatFragment extends ConversationFragment {
	private RongExtension rongExtension;
	private ListView listView;
	private OnShowAnnounceListener onShowAnnounceListener;
	private OnExtensionChangeListener onExtensionChangeListener;




	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		findViewById(v,io.rong.imkit.R.id.rc_voice_toggle).setVisibility(View.GONE);
		View messageListView = findViewById(v, io.rong.imkit.R.id.rc_layout_msg_list);
		listView = findViewById(messageListView, io.rong.imkit.R.id.rc_list);
		return v;
	}

	/**
	 * 设置通知信息回调。
	 *
	 * @param listener
	 */
	public void setOnShowAnnounceBarListener(OnShowAnnounceListener listener) {
		onShowAnnounceListener = listener;
	}

	public void setOnExtensionChangeListener(OnExtensionChangeListener listener) {
		onExtensionChangeListener = listener;
	}

	public RongExtension getRongExtension() {
		return rongExtension;
	}

	/**
	 * 显示通告栏的监听器
	 */
	public interface OnShowAnnounceListener {

		/**
		 * 展示通告栏的回调
		 *
		 * @param announceMsg 通告栏展示内容
		 * @param annouceUrl  通告栏点击链接地址，若此参数为空，则表示不需要点击链接，否则点击进入链接页面
		 * @return
		 */
		void onShowAnnounceView(String announceMsg, String annouceUrl);
	}

	public interface OnExtensionChangeListener {

		void onExtensionHeightChange(int h);

		void onExtensionExpanded(int h);

		void onExtensionCollapsed();

		void onPluginToggleClick(View v, ViewGroup extensionBoard);
	}
}

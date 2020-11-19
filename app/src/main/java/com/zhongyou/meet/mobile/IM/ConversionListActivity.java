package com.zhongyou.meet.mobile.IM;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.zhongyou.meet.mobile.R;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class ConversionListActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversion_list);

		FragmentManager fragmentManage = getSupportFragmentManager();
		ConversationListFragment fragement = (ConversationListFragment) fragmentManage.findFragmentById(R.id.conversationlist);
		Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
				.appendPath("conversationlist")
				.appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")
				.appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")
				.build();
		fragement.setUri(uri);
	}
}

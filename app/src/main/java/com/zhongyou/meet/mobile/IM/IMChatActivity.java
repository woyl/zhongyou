package com.zhongyou.meet.mobile.IM;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.orhanobut.logger.Logger;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.BasicActivity;

import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.agora.openlive.ui.BaseActivity;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

public class IMChatActivity extends BasicActivity {
	private String mTargetId; //目标 Id
	private Conversation.ConversationType mConversationType; //会话类型
	@Override
	public String getStatisticsTag() {
		return "";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.e("IMChatActivity   onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imchat);

		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView tilleView=findViewById(R.id.title);

		try {
			Intent intent = getIntent();

			mTargetId = intent.getData().getQueryParameter("targetId");
			tilleView.setText(intent.getData().getQueryParameter("title"));

			mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.US));
//		mTargetIds = intent.getData().getQueryParameter("targetIds");
			/*mLogger.e("mTargetId:   "+mTargetId);
			mLogger.e("mConversationType:   "+mConversationType.getName().toLowerCase());*/
			FragmentManager fragmentManage = getSupportFragmentManager();
			MeetingChatFragment fragement = (MeetingChatFragment) fragmentManage.findFragmentById(R.id.conversation);
			Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
					.appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
					.appendQueryParameter("targetId", mTargetId).build();

			fragement.setUri(uri);
		} catch (Exception e) {
			e.printStackTrace();
			showToastyWarn("会话列表出现了错误 请稍后再试");
			finish();
		}
	}

}

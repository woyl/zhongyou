package com.zhongyou.meet.mobile.ameeting.adater;

import android.content.Context;
import android.view.View;

import com.zhongyou.meet.mobile.R;

import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.Message;


/**
 * @author golangdorid@gmail.com
 * @date 2020/4/21 6:29 PM.
 * @
 */
public class NewMessageListAdapter extends MessageListAdapter {


	public NewMessageListAdapter(Context context) {
		super(context);
	}

	@Override
	protected void bindView(View v, int position, UIMessage data) {
		super.bindView(v, position, data);
		final MessageListAdapter.ViewHolder holder = (MessageListAdapter.ViewHolder)v.getTag();
		if (data.getMessageDirection() == Message.MessageDirection.SEND) {//消息方向，自己发送的
			holder.layout.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
		} else {
			holder.layout.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
		}
	}
}



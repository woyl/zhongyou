package io.agora.openlive.ui;


import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.NewMessageListAdapter;

import io.rong.imkit.InputBar;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import me.jessyan.autosize.utils.AutoSizeUtils;
import timber.log.Timber;


public class NewInMeetingChatFragment extends ConversationFragment {
	private RongExtension rongExtension;
	private ListView listView;
	private EditText mEditText;
	private Context context;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		context = container.getContext();
		//editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
		if (v != null) {
			v.setBackgroundColor(ContextCompat.getColor(container.getContext(),android.R.color.transparent));
			mEditText = findViewById(v, io.rong.imkit.R.id.rc_edit_text);
			rongExtension = (RongExtension) v.findViewById(io.rong.imkit.R.id.rc_extension);
			rongExtension.setBackgroundColor(ContextCompat.getColor(container.getContext(),R.color.transparent));
			rongExtension.setInputBarStyle(InputBar.Style.STYLE_CONTAINER);
			ImageView emoticon = (ImageView) rongExtension.findViewById(R.id.rc_emoticon_toggle);
			emoticon.setVisibility(View.GONE);
			View messageListView = findViewById(v, io.rong.imkit.R.id.rc_layout_msg_list);
			listView = findViewById(messageListView, io.rong.imkit.R.id.rc_list);
			RongContext.getInstance().showNewMessageIcon(false);
			RongContext.getInstance().showUnreadMessageIcon(false);


//			this.mEditText.addTextChangedListener(new TextWatcher() {
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//				}
//
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//				}
//
//				@Override
//				public void afterTextChanged(Editable s) {
//
//				}
//			});
//
//
//			this.mEditText.setOnKeyListener(new View.OnKeyListener() {
//				@Override
//				public boolean onKey(View v, int keyCode, KeyEvent event) {
//					Timber.e("keyCode---->%s", keyCode);
//					if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
//						hideInputKeyBoard();
//					}
//					return false;
//				}
//			});
			this.mEditText.setVisibility(View.GONE);
		}

		String isMaker = getUri().getQueryParameter("isMaker");
		Timber.e("isMaker---->%s", isMaker);
		if (!TextUtils.isEmpty(isMaker)) {
			if (isMaker.equals("0")) {
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
				layoutParams.setMargins(AutoSizeUtils.pt2px(container.getContext(), 10), 0, 0, 0);
				mEditText.setLayoutParams(layoutParams);
			}
		}

		return v;
	}

	public void smoothScrollToBottom() {
		if (listView != null && getMessageAdapter() != null) {
			listView.smoothScrollToPosition(getMessageAdapter().getCount());
		}
	}

	void hideInputKeyBoard() {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(this.mEditText.getWindowToken(), 0);
		}

		this.mEditText.clearFocus();
	}

	public ListView getListView() {
		return listView;

	}

	@Override
	public void onResume() {
		super.onResume();
		smoothScrollToBottom();
	}

	public void setListViewScrollToBottom(){
		if (this.listView!=null){
			this.listView.smoothScrollToPosition(getMessageAdapter().getCount());
		}
	}

	public RongExtension getRongExtension() {
		return rongExtension;
	}

	/*@Override
	public MessageListAdapter onResolveAdapter(Context context) {
		return new NewMessageListAdapter(context);
	}*/


}

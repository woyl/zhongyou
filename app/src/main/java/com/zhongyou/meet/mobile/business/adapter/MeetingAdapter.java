package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.entities.Meeting;
import com.zhongyou.meet.mobile.utils.ToastUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;


/**
 * Created by whatisjava on 17-10-18.
 */
public class MeetingAdapter extends OpenPresenter {

	private Context mContext;
	private List<Meeting> meetings;

	private GeneralAdapter mAdapter;
	private OnItemClickListener listener;

	public MeetingAdapter(Context context, List<Meeting> meetings, OnItemClickListener listener) {
		this.mContext = context;
		this.meetings = meetings;
		this.listener = listener;
	}

	public void notifyDataSetChanged(List<Meeting> meetings) {
		this.meetings.addAll(meetings);
		this.mAdapter.notifyDataSetChanged();
	}

	@Override
	public void setAdapter(GeneralAdapter adapter) {
		this.mAdapter = adapter;
	}

	@Override
	public int getItemCount() {
		return meetings != null ? meetings.size() : 0;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_meeting, parent, false);
		return new MeetingHodler(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
		final Meeting meeting = meetings.get(position);
		MeetingHodler holder = (MeetingHodler) viewHolder;

		if (meeting.getMeetingProcess() == 1) {
			holder.stateText.setBackgroundResource(R.drawable.bg_meeting_state_new);
			holder.stateText.setText("进行中");
		} else {
			holder.stateText.setBackgroundResource(R.drawable.bg_meeting_state_a_new);
			holder.stateText.setText("未开始");
		}


		if (meeting.getIsCollection() == 0) {
			holder.collectButton.setLiked(false);
		} else {
			holder.collectButton.setLiked(true);
		}

		holder.collectButton.setOnLikeListener(new OnLikeListener() {
			@Override
			public void liked(LikeButton likeButton) {

				if (listener!=null){
					listener.onCollectionClick(1,meeting,position);
				}

			}

			@Override
			public void unLiked(LikeButton likeButton) {
				if (listener!=null){
					listener.onCollectionClick(0,meeting,position);
				}

			}
		});


		holder.titleText.setText(meeting.getTitle());

		holder.beginTimeText.setText(meeting.getStartTime());

		if (position % 2 == 0) {
			holder.bgLayout.setBackgroundResource(R.mipmap.bg_meeting_item_b);
		} else {
			holder.bgLayout.setBackgroundResource(R.mipmap.bg_meeting_item_a);
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (listener != null) {
					listener.onItemClick(view, meeting);
				}
			}
		});

	}

	public interface OnItemClickListener {

		void onItemClick(View view, Meeting meeting);

		void onCollectionClick(int type,Meeting meeting,int position);
	}

	private class MeetingHodler extends ViewHolder {

		View itemView;
		RelativeLayout bgLayout;
		TextView titleText, beginTimeText, stateText;
		LikeButton collectButton;

		MeetingHodler(View itemView) {
			super(itemView);
			this.itemView = itemView;
			bgLayout = itemView.findViewById(R.id.background);
			titleText = itemView.findViewById(R.id.title);
			beginTimeText = itemView.findViewById(R.id.begin_time);
			stateText = itemView.findViewById(R.id.meeting_state);
			collectButton = itemView.findViewById(R.id.collectButton);
		}
	}

}

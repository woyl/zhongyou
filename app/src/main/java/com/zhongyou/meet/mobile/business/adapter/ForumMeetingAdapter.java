package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.ForumMeeting;

import java.util.ArrayList;
import java.util.List;

/**
 * 讨论区列表adapter
 *
 * @author Dongce
 * create time: 2018/11/14
 */
public class ForumMeetingAdapter extends RecyclerView.Adapter<ForumMeetingAdapter.ForumMeetingHolder> {

    private Context mContext;
    private List<ForumMeeting> forumMeetings = new ArrayList<>();
    private OnItemClickListener listener;

    public ForumMeetingAdapter(Context context, OnItemClickListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    public void addData(ArrayList<ForumMeeting> forumMeetings) {
        this.forumMeetings.addAll(forumMeetings);
        notifyItemRangeInserted(this.forumMeetings.size() - 1, forumMeetings.size());
    }

    public List<ForumMeeting> getData() {
        return forumMeetings;
    }

    public void clearData() {
        this.forumMeetings.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return forumMeetings.size();
    }

    @NonNull
    @Override
    public ForumMeetingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_forum_meeting, parent, false);
        return new ForumMeetingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumMeetingHolder viewHolder, int position) {
        final ForumMeeting forumMeeting = forumMeetings.get(position);
        ForumMeetingHolder holder = (ForumMeetingHolder) viewHolder;

        //图片背景及文字显示
        if (forumMeeting.getMeetingType() == ForumMeeting.TYPE_MEETING_PUBLIC) {
            holder.img_forum_meeting_item_head.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_forum_meeting_public));
            holder.tv_forum_meeting_item_head.setText(mContext.getResources().getText(R.string.forum_meeting_public));
        } else if (forumMeeting.getMeetingType() == ForumMeeting.TYPE_MEETING_PRIVATE) {
            holder.img_forum_meeting_item_head.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_forum_meeting_private));
            holder.tv_forum_meeting_item_head.setText(mContext.getResources().getText(R.string.forum_meeting_private));
        }

        //会议title
        holder.tv_forum_meeting_item_title.setText(forumMeeting.getTitle());


        //未读消息，0条消息不显示
        if (forumMeeting.getNewMsgCnt() == 0) {
            holder.tv_forum_meeting_item_unread.setVisibility(View.GONE);
        } else {
            holder.tv_forum_meeting_item_unread.setVisibility(View.VISIBLE);
            holder.tv_forum_meeting_item_unread.setText(forumMeeting.getNewMsgCnt() + "条新消息");
        }

        //是否被@
        if (forumMeeting.isAtailFlag()) {
            holder.tv_forum_meeting_item_at.setVisibility(View.VISIBLE);
        } else {
            holder.tv_forum_meeting_item_at.setVisibility(View.GONE);
        }

        //最后消息时间
        holder.tv_forum_meeting_item_msg_lasttime.setText(forumMeeting.getNewMsgReplyTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(v, forumMeeting, position);
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ForumMeeting forumMeeting, int position);
    }

    class ForumMeetingHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView img_forum_meeting_item_head;
        TextView tv_forum_meeting_item_head, tv_forum_meeting_item_title, tv_forum_meeting_item_unread, tv_forum_meeting_item_at, tv_forum_meeting_item_msg_lasttime;

        ForumMeetingHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            img_forum_meeting_item_head = itemView.findViewById(R.id.img_forum_meeting_item_head);
            tv_forum_meeting_item_head = itemView.findViewById(R.id.tv_forum_meeting_item_head);
            tv_forum_meeting_item_title = itemView.findViewById(R.id.tv_forum_meeting_item_title);
            tv_forum_meeting_item_unread = itemView.findViewById(R.id.tv_forum_meeting_item_unread);
            tv_forum_meeting_item_at = itemView.findViewById(R.id.tv_forum_meeting_item_at);
            tv_forum_meeting_item_msg_lasttime = itemView.findViewById(R.id.tv_forum_meeting_item_msg_lasttime);
        }
    }
}

package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.zhongyou.meet.mobile.persistence.Preferences;

import java.util.List;

public class ChatMsgData {

	private int pageNo;
	private int totalPage;
	private int pageSize;
	private int totalCount;
	private List<PageDataEntity> pageData;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<PageDataEntity> getPageData() {
		return pageData;
	}

	public void setPageData(List<PageDataEntity> pageData) {
		this.pageData = pageData;
	}


	public static class PageDataEntity implements Parcelable, MultiItemEntity {
		private String id;
		private String userName;
		private String replyTime;
		private String content;
		private String userId;
		private int type;
		private String userLogo;
		private long replyTimestamp;
		private long ts;
		private int msgType;
		private int localState;//0发送成功1正在发送2发送失败
		private String meetingId;
		private String atailUserId;

		public PageDataEntity() {

		}

		public PageDataEntity(Parcel in) {
			userName = in.readString();
			replyTime = in.readString();
			content = in.readString();
			type = in.readInt();
			userLogo = in.readString();
			userId = in.readString();
			id = in.readString();
			replyTimestamp = in.readLong();
			msgType = in.readInt();
			localState = in.readInt();
			ts = in.readLong();
			meetingId = in.readString();
			atailUserId = in.readString();
		}

		public static final Creator<PageDataEntity> CREATOR = new Creator<PageDataEntity>() {
			@Override
			public PageDataEntity createFromParcel(Parcel in) {
				return new PageDataEntity(in);
			}

			@Override
			public PageDataEntity[] newArray(int size) {
				return new PageDataEntity[size];
			}
		};

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getReplyTime() {
			return replyTime;
		}

		public void setReplyTime(String replyTime) {
			this.replyTime = replyTime;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getUserLogo() {
			return userLogo;
		}

		public void setUserLogo(String userLogo) {
			this.userLogo = userLogo;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getReplyTimestamp() {
			return replyTimestamp;
		}

		public void setReplyTimestamp(long replyTimestamp) {
			this.replyTimestamp = replyTimestamp;
		}

		public int getMsgType() {
			return msgType;
		}

		public void setMsgType(int msgType) {
			this.msgType = msgType;
		}

		public int getLocalState() {
			return localState;
		}

		public void setLocalState(int localState) {
			this.localState = localState;
		}

		public long getTs() {
			return ts;
		}

		public void setTs(long ts) {
			this.ts = ts;
		}

		public void setMeetingId(String meetingId) {
			this.meetingId = meetingId;
		}

		public String getMeetingId() {
			return meetingId;
		}

		public void setAtailUserId(String atailUserId) {
			this.atailUserId = atailUserId;
		}

		public String getAtailUserId() {
			return atailUserId;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel parcel, int i) {
			parcel.writeString(userName);
			parcel.writeString(replyTime);
			parcel.writeString(content);
			parcel.writeInt(type);
			parcel.writeString(userLogo);
			parcel.writeString(userId);
			parcel.writeString(id);
			parcel.writeLong(replyTimestamp);
			parcel.writeInt(msgType);
			parcel.writeInt(localState);
			parcel.writeLong(ts);
			parcel.writeString(meetingId);
			parcel.writeString(atailUserId);
		}

		@Override
		public int getItemType() {
			if (getMsgType() == 1 || getMsgType() == 2) {
				return 2;
			} else {
				if (null == getUserId()) {
					return 0;
				}
				if (!getUserId().equals(Preferences.getUserId())) {
					return 0;
				} else {
					return 1;
				}
			}
		}
	}
}

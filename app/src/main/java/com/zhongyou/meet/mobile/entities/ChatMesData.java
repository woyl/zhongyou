package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.zhongyou.meet.mobile.persistence.Preferences;

import java.util.List;

public class ChatMesData {

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
        private boolean isNewAdd;

        private boolean upLoadScuucess;

        private boolean isNewAdd() {
            return isNewAdd;
        }

        private void setNewAdd(boolean newAdd) {
            isNewAdd = newAdd;
        }

        public boolean isUpLoadScuucess() {
            return upLoadScuucess;
        }

        public void setUpLoadScuucess(boolean upLoadScuucess) {
            this.upLoadScuucess = upLoadScuucess;
        }

        public boolean getIsNewAdd() {
            return isNewAdd;
        }

        public void setIsNewAdd(boolean newAdd) {
            isNewAdd = newAdd;
        }

        public PageDataEntity(){

        }

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.userName);
            dest.writeString(this.replyTime);
            dest.writeString(this.content);
            dest.writeString(this.userId);
            dest.writeInt(this.type);
            dest.writeString(this.userLogo);
            dest.writeLong(this.replyTimestamp);
            dest.writeLong(this.ts);
            dest.writeInt(this.msgType);
            dest.writeInt(this.localState);
            dest.writeString(this.meetingId);
            dest.writeString(this.atailUserId);
            dest.writeByte(this.isNewAdd ? (byte) 1 : (byte) 0);
            dest.writeByte(this.upLoadScuucess ? (byte) 1 : (byte) 0);
        }

        protected PageDataEntity(Parcel in) {
            this.id = in.readString();
            this.userName = in.readString();
            this.replyTime = in.readString();
            this.content = in.readString();
            this.userId = in.readString();
            this.type = in.readInt();
            this.userLogo = in.readString();
            this.replyTimestamp = in.readLong();
            this.ts = in.readLong();
            this.msgType = in.readInt();
            this.localState = in.readInt();
            this.meetingId = in.readString();
            this.atailUserId = in.readString();
            this.isNewAdd = in.readByte() != 0;
            this.upLoadScuucess = in.readByte() != 0;
        }

        public static final Creator<PageDataEntity> CREATOR = new Creator<PageDataEntity>() {
            @Override
            public PageDataEntity createFromParcel(Parcel source) {
                return new PageDataEntity(source);
            }

            @Override
            public PageDataEntity[] newArray(int size) {
                return new PageDataEntity[size];
            }
        };
    }
}

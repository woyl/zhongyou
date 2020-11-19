package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 讨论区列表的会议
 *
 * @author Dongce
 * create time: 2018/11/15
 */
public class ForumMeeting implements Parcelable, Entity {

    //公开会议指定代码
    public static final int TYPE_MEETING_PUBLIC = 0;
    //受邀会议指定代码
    public static final int TYPE_MEETING_PRIVATE = 1;

    private String meetingId;
    private int meetingType;
    private String title;
    private int newMsgCnt;
    private int userCnt;
    private String newMsgReplyTime;
    private boolean atailFlag;


    protected ForumMeeting(Parcel in) {
        meetingId = in.readString();
        meetingType = in.readInt();
        title = in.readString();
        newMsgCnt = in.readInt();
        userCnt = in.readInt();
        newMsgReplyTime = in.readString();
        atailFlag = in.readByte() != 0;
    }

    @Override
    public String toString() {
        return "ForumMeeting{" +
                "meetingId='" + meetingId + '\'' +
                ", meetingType='" + meetingType +
                ", title='" + title + '\'' +
                ", newMsgCnt='" + newMsgCnt +
                ", userCnt=" + userCnt +
                ", newMsgReplyTime=" + newMsgReplyTime + '\'' +
                ", atailFlag=" + atailFlag +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForumMeeting forumMeeting = (ForumMeeting) o;

        if (meetingId != null ? !meetingId.equals(forumMeeting.meetingId) : forumMeeting.meetingId != null)
            return false;
        if (meetingType != forumMeeting.meetingType) return false;
        if (title != null ? !title.equals(forumMeeting.title) : forumMeeting.title != null)
            return false;
        if (newMsgCnt != forumMeeting.newMsgCnt) return false;
        if (userCnt != forumMeeting.userCnt) return false;
        if (newMsgReplyTime != null ? !newMsgReplyTime.equals(forumMeeting.newMsgReplyTime) : forumMeeting.newMsgReplyTime != null)
            return false;
        return atailFlag != forumMeeting.atailFlag;
    }

    @Override
    public int hashCode() {
        int result = meetingId != null ? meetingId.hashCode() : 0;
        result = 31 * result + meetingType;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + newMsgCnt;
        result = 31 * result + userCnt;
        result = 31 * result + (newMsgReplyTime != null ? newMsgReplyTime.hashCode() : 0);
        result = 31 * result + (atailFlag ? 1 : 0);
        return result;
    }

    public static final Creator<ForumMeeting> CREATOR = new Creator<ForumMeeting>() {
        @Override
        public ForumMeeting createFromParcel(Parcel in) {
            return new ForumMeeting(in);
        }

        @Override
        public ForumMeeting[] newArray(int size) {
            return new ForumMeeting[size];
        }
    };

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public int getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(int meetingType) {
        this.meetingType = meetingType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNewMsgCnt() {
        return newMsgCnt;
    }

    public void setNewMsgCnt(int newMsgCnt) {
        this.newMsgCnt = newMsgCnt;
    }

    public int getUserCnt() {
        return userCnt;
    }

    public void setUserCnt(int userCnt) {
        this.userCnt = userCnt;
    }

    public String getNewMsgReplyTime() {
        return newMsgReplyTime;
    }

    public void setNewMsgReplyTime(String newMsgReplyTime) {
        this.newMsgReplyTime = newMsgReplyTime;
    }

    public boolean isAtailFlag() {
        return atailFlag;
    }

    public void setAtailFlag(boolean atailFlag) {
        this.atailFlag = atailFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(meetingId);
        dest.writeInt(meetingType);
        dest.writeString(title);
        dest.writeInt(newMsgCnt);
        dest.writeInt(userCnt);
        dest.writeString(newMsgReplyTime);
        dest.writeByte((byte) (atailFlag ? 1 : 0));
    }
}

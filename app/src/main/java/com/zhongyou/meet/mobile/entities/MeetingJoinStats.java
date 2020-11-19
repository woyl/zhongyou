package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whatisjava on 18-2-27.
 */

public class MeetingJoinStats implements Parcelable {

    private String id;

    private String meetingId;

    private String userId;

    private String deviceId;

    private int joinType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getJoinType() {
        return joinType;
    }

    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    @Override
    public String toString() {
        return "MeetingJoinStats{" +
                "id='" + id + '\'' +
                ", meetingId='" + meetingId + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", joinType=" + joinType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingJoinStats that = (MeetingJoinStats) o;

        if (joinType != that.joinType) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (meetingId != null ? !meetingId.equals(that.meetingId) : that.meetingId != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return deviceId != null ? deviceId.equals(that.deviceId) : that.deviceId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (meetingId != null ? meetingId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        result = 31 * result + joinType;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.meetingId);
        dest.writeString(this.userId);
        dest.writeString(this.deviceId);
        dest.writeInt(this.joinType);
    }

    public MeetingJoinStats() {
    }

    protected MeetingJoinStats(Parcel in) {
        this.id = in.readString();
        this.meetingId = in.readString();
        this.userId = in.readString();
        this.deviceId = in.readString();
        this.joinType = in.readInt();
    }

    public static final Creator<MeetingJoinStats> CREATOR = new Creator<MeetingJoinStats>() {
        @Override
        public MeetingJoinStats createFromParcel(Parcel source) {
            return new MeetingJoinStats(source);
        }

        @Override
        public MeetingJoinStats[] newArray(int size) {
            return new MeetingJoinStats[size];
        }
    };
}

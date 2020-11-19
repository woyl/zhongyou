package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Dongce
 * create time: 2018/10/25
 */
public class MeetingAdmin implements Parcelable, Entity {

    private String meetingMgrUrl;
    private String publishMeetingUrl;
    private boolean meetingAdmin;

    public String getMeetingMgrUrl() {
        return meetingMgrUrl;
    }

    public void setMeetingMgrUrl(String meetingMgrUrl) {
        this.meetingMgrUrl = meetingMgrUrl;
    }

    public String getPublishMeetingUrl() {
        return publishMeetingUrl;
    }

    public void setPublishMeetingUrl(String publishMeetingUrl) {
        this.publishMeetingUrl = publishMeetingUrl;
    }

    public boolean isMeetingAdmin() {
        return meetingAdmin;
    }

    public void setMeetingAdmin(boolean meetingAdmin) {
        this.meetingAdmin = meetingAdmin;
    }

    @Override
    public String toString() {
        return "MeetingAdmin{" +
                "meetingMgrUrl='" + meetingMgrUrl + '\'' +
                ", publishMeetingUrl='" + publishMeetingUrl + '\'' +
                ", meetingAdmin=" + meetingAdmin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingAdmin that = (MeetingAdmin) o;

        if (meetingAdmin != that.meetingAdmin) return false;
        if (meetingMgrUrl != null ? !meetingMgrUrl.equals(that.meetingMgrUrl) : that.meetingMgrUrl != null)
            return false;
        return publishMeetingUrl != null ? publishMeetingUrl.equals(that.publishMeetingUrl) : that.publishMeetingUrl == null;
    }

    @Override
    public int hashCode() {
        int result = meetingMgrUrl != null ? meetingMgrUrl.hashCode() : 0;
        result = 31 * result + (publishMeetingUrl != null ? publishMeetingUrl.hashCode() : 0);
        result = 31 * result + (meetingAdmin ? 1 : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.meetingMgrUrl);
        dest.writeString(this.publishMeetingUrl);
        dest.writeByte(this.meetingAdmin ? (byte) 1 : (byte) 0);
    }

    public MeetingAdmin() {
    }

    protected MeetingAdmin(Parcel in) {
        this.meetingMgrUrl = in.readString();
        this.publishMeetingUrl = in.readString();
        this.meetingAdmin = in.readByte() != 0;
    }

    public static final Creator<MeetingAdmin> CREATOR = new Creator<MeetingAdmin>() {
        @Override
        public MeetingAdmin createFromParcel(Parcel source) {
            return new MeetingAdmin(source);
        }

        @Override
        public MeetingAdmin[] newArray(int size) {
            return new MeetingAdmin[size];
        }
    };
}

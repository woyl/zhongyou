package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuna on 2017/7/27.
 */

public class HostUser implements Entity, Parcelable {

    private String clientUid;

    private String hostUserName;
    private String hostUserId;

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    @Override
    public String toString() {
        return "HostUser{" +
                "clientUid='" + clientUid + '\'' +
                ", hostUserName='" + hostUserName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HostUser hostUser = (HostUser) o;

        if (clientUid != null ? !clientUid.equals(hostUser.clientUid) : hostUser.clientUid != null)
            return false;
        return hostUserName != null ? hostUserName.equals(hostUser.hostUserName) : hostUser.hostUserName == null;
    }

    @Override
    public int hashCode() {
        int result = clientUid != null ? clientUid.hashCode() : 0;
        result = 31 * result + (hostUserName != null ? hostUserName.hashCode() : 0);
        return result;
    }

    public String getClientUid() {
        return clientUid;
    }

    public void setClientUid(String clientUid) {
        this.clientUid = clientUid;
    }

    public String getHostUserName() {
        return hostUserName;
    }

    public void setHostUserName(String hostUserName) {
        this.hostUserName = hostUserName;
    }

    public HostUser() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.clientUid);
        dest.writeString(this.hostUserName);
        dest.writeString(this.hostUserId);
    }

    protected HostUser(Parcel in) {
        this.clientUid = in.readString();
        this.hostUserName = in.readString();
        this.hostUserId = in.readString();
    }

    public static final Creator<HostUser> CREATOR = new Creator<HostUser>() {
        @Override
        public HostUser createFromParcel(Parcel source) {
            return new HostUser(source);
        }

        @Override
        public HostUser[] newArray(int size) {
            return new HostUser[size];
        }
    };
}

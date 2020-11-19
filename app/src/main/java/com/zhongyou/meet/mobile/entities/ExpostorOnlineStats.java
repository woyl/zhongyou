package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whatisjava on 18-2-27.
 */

public class ExpostorOnlineStats implements Parcelable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ExpostorOnlineStats{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpostorOnlineStats that = (ExpostorOnlineStats) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
    }

    public ExpostorOnlineStats() {
    }

    protected ExpostorOnlineStats(Parcel in) {
        this.id = in.readString();
    }

    public static final Creator<ExpostorOnlineStats> CREATOR = new Creator<ExpostorOnlineStats>() {
        @Override
        public ExpostorOnlineStats createFromParcel(Parcel source) {
            return new ExpostorOnlineStats(source);
        }

        @Override
        public ExpostorOnlineStats[] newArray(int size) {
            return new ExpostorOnlineStats[size];
        }
    };
}

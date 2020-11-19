package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yuna on 2017/7/27.
 */

public class Meetings implements Entity, Parcelable {

    private int pageNo;

    private int totalPage;

    private int pageSize;

    private int totalCount;

    private int count;

    private ArrayList<Meeting> data;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Meeting> getData() {
        return data;
    }

    public void setData(ArrayList<Meeting> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Meetings{" +
                "pageNo=" + pageNo +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", count=" + count +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meetings meetings = (Meetings) o;

        if (pageNo != meetings.pageNo) return false;
        if (totalPage != meetings.totalPage) return false;
        if (pageSize != meetings.pageSize) return false;
        if (totalCount != meetings.totalCount) return false;
        if (count != meetings.count) return false;
        return data != null ? data.equals(meetings.data) : meetings.data == null;
    }

    @Override
    public int hashCode() {
        int result = pageNo;
        result = 31 * result + totalPage;
        result = 31 * result + pageSize;
        result = 31 * result + totalCount;
        result = 31 * result + count;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageNo);
        dest.writeInt(this.totalPage);
        dest.writeInt(this.pageSize);
        dest.writeInt(this.totalCount);
        dest.writeInt(this.count);
        dest.writeTypedList(this.data);
    }

    public Meetings() {
    }

    protected Meetings(Parcel in) {
        this.pageNo = in.readInt();
        this.totalPage = in.readInt();
        this.pageSize = in.readInt();
        this.totalCount = in.readInt();
        this.count = in.readInt();
        this.data = in.createTypedArrayList(Meeting.CREATOR);
    }

    public static final Creator<Meetings> CREATOR = new Creator<Meetings>() {
        @Override
        public Meetings createFromParcel(Parcel source) {
            return new Meetings(source);
        }

        @Override
        public Meetings[] newArray(int size) {
            return new Meetings[size];
        }
    };
}

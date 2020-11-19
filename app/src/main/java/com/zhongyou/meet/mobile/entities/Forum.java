package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 讨论区列表，列举所有会议
 *
 * @author Dongce
 * create time: 2018/11/14
 */
public class Forum implements Parcelable, Entity {

    private int pageNo;
    private int totalPage;
    private int pageSize;
    private ArrayList<ForumMeeting> pageData;

    protected Forum(Parcel in) {
        pageNo = in.readInt();
        totalPage = in.readInt();
        pageSize = in.readInt();
        in.readTypedList(pageData, ForumMeeting.CREATOR);
    }

    @Override
    public String toString() {
        return "Forum{" +
                "pageNo='" + pageNo +
                ", totalPage='" + totalPage +
                ", pageSize='" + pageSize +
                ", pageData='" + pageData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Forum forum = (Forum) o;

        if (pageNo != forum.pageNo) return false;
        if (totalPage != forum.totalPage) return false;
        if (pageSize != forum.pageSize) return false;
        return pageData != null ? !pageData.equals(forum.pageData) : forum.pageData == null;
    }

    @Override
    public int hashCode() {
        int result = pageNo;
        result = 31 * result + totalPage;
        result = 31 * result + pageSize;
        result = 31 * result + (pageData != null ? pageData.hashCode() : 0);
        return result;
    }

    public static final Creator<Forum> CREATOR = new Creator<Forum>() {
        @Override
        public Forum createFromParcel(Parcel in) {
            return new Forum(in);
        }

        @Override
        public Forum[] newArray(int size) {
            return new Forum[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pageNo);
        dest.writeInt(totalPage);
        dest.writeInt(pageSize);
        dest.writeTypedList(pageData);
    }

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

    public ArrayList<ForumMeeting> getPageData() {
        return pageData;
    }

    public void setPageData(ArrayList<ForumMeeting> pageData) {
        this.pageData = pageData;
    }
}

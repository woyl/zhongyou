package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 中心 entities
 *
 * @author Dongce
 * create time: 2018/10/25
 */
public class PostType implements Parcelable, Entity {

    private String id;
    private String createDate;
    private String updateDate;
    private String delFlag;
    private String name;
    private int priority;

    public PostType() {

    }

    protected PostType(Parcel in) {
        id = in.readString();
        createDate = in.readString();
        updateDate = in.readString();
        delFlag = in.readString();
        name = in.readString();
        priority = in.readInt();
    }

    public static final Creator<PostType> CREATOR = new Creator<PostType>() {
        @Override
        public PostType createFromParcel(Parcel in) {
            return new PostType(in);
        }

        @Override
        public PostType[] newArray(int size) {
            return new PostType[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "PostType{" +
                "id='" + id + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateDate=" + updateDate + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", name=" + name + '\'' +
                ", priority=" + priority +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostType postType = (PostType) o;

        if (id != null ? !id.equals(postType.id) : postType.id != null) return false;
        if (createDate != null ? !createDate.equals(postType.createDate) : postType.createDate != null)
            return false;
        if (updateDate != null ? !updateDate.equals(postType.updateDate) : postType.updateDate != null)
            return false;
        if (delFlag != null ? !delFlag.equals(postType.delFlag) : postType.delFlag != null)
            return false;
        if (name != null ? !name.equals(postType.name) : postType.name != null)
            return false;
        return priority != postType.priority;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (delFlag != null ? delFlag.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + priority;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(createDate);
        dest.writeString(updateDate);
        dest.writeString(delFlag);
        dest.writeString(name);
        dest.writeInt(priority);
    }
}

package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Dongce
 * create time: 2018/10/25
 */
public class Custom implements Parcelable, Entity {
    private String id;
    private String createDate;
    private String updateDate;
    private String delFlag;
    private String name;
    private String areaId;

    protected Custom(Parcel in) {
        id = in.readString();
        createDate = in.readString();
        updateDate = in.readString();
        delFlag = in.readString();
        name = in.readString();
        areaId = in.readString();
    }

    public static final Creator<Custom> CREATOR = new Creator<Custom>() {
        @Override
        public Custom createFromParcel(Parcel in) {
            return new Custom(in);
        }

        @Override
        public Custom[] newArray(int size) {
            return new Custom[size];
        }
    };

    @Override
    public String toString() {
        return "Custom{" +
                "id='" + id + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateDate=" + updateDate + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", name='" + name + '\'' +
                ", areaId='" + areaId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Custom custom = (Custom) o;

        if (id != null ? !id.equals(custom.id) : custom.id != null) return false;
        if (createDate != null ? !createDate.equals(custom.createDate) : custom.createDate != null)
            return false;
        if (updateDate != null ? !updateDate.equals(custom.updateDate) : custom.updateDate != null)
            return false;
        if (delFlag != null ? !delFlag.equals(custom.delFlag) : custom.delFlag != null)
            return false;
        if (name != null ? !name.equals(custom.name) : custom.name != null) return false;
        return areaId != null ? !areaId.equals(custom.areaId) : custom.areaId != null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (delFlag != null ? delFlag.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (areaId != null ? areaId.hashCode() : 0);
        return result;
    }

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

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
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
        dest.writeString(areaId);
    }
}

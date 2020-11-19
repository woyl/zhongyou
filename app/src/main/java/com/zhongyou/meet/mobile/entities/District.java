package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whatisjava on 18-1-22.
 */

public class District implements Parcelable, Entity {

    private String id;

    private String districtName;

    private Integer districtCode;

    private String centerName;

    private Integer centerCode;

    private int priority;   // 排序

    private String name;

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public Integer getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(Integer centerCode) {
        this.centerCode = centerCode;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "District{" +
                "id='" + id + '\'' +
                ", districtName='" + districtName + '\'' +
                ", districtCode=" + districtCode +
                ", centerName='" + centerName + '\'' +
                ", centerCode=" + centerCode +
                ", priority=" + priority +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        District district = (District) o;

        if (priority != district.priority) return false;
        if (id != null ? !id.equals(district.id) : district.id != null) return false;
        if (districtName != null ? !districtName.equals(district.districtName) : district.districtName != null)
            return false;
        if (districtCode != null ? !districtCode.equals(district.districtCode) : district.districtCode != null)
            return false;
        if (centerName != null ? !centerName.equals(district.centerName) : district.centerName != null)
            return false;
        if (centerCode != null ? !centerCode.equals(district.centerCode) : district.centerCode != null)
            return false;
        return name != null ? name.equals(district.name) : district.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (districtName != null ? districtName.hashCode() : 0);
        result = 31 * result + (districtCode != null ? districtCode.hashCode() : 0);
        result = 31 * result + (centerName != null ? centerName.hashCode() : 0);
        result = 31 * result + (centerCode != null ? centerCode.hashCode() : 0);
        result = 31 * result + priority;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.districtName);
        dest.writeValue(this.districtCode);
        dest.writeString(this.centerName);
        dest.writeValue(this.centerCode);
        dest.writeInt(this.priority);
        dest.writeString(this.name);
    }

    public District() {
    }

    protected District(Parcel in) {
        this.id = in.readString();
        this.districtName = in.readString();
        this.districtCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.centerName = in.readString();
        this.centerCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.priority = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<District> CREATOR = new Creator<District>() {
        @Override
        public District createFromParcel(Parcel source) {
            return new District(source);
        }

        @Override
        public District[] newArray(int size) {
            return new District[size];
        }
    };
}

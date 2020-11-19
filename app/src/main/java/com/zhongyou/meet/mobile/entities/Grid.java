package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 网格 entities
 *
 * @author Dongce
 * create time: 2018/10/25
 */
public class Grid implements Parcelable, Entity {

    private String id;
    private String delFlag;
    private String name;
    private String areaId;

    protected Grid(Parcel in) {
        id = in.readString();
        delFlag = in.readString();
        name = in.readString();
        areaId = in.readString();
    }

    @Override
    public String toString() {
        return "Grid{" +
                "id='" + id + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", name=" + name + '\'' +
                ", areaId='" + areaId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grid grid = (Grid) o;

        if (id != null ? !id.equals(grid.id) : grid.id != null) return false;
        if (delFlag != null ? !delFlag.equals(grid.delFlag) : grid.delFlag != null) return false;
        if (name != null ? !name.equals(grid.name) : grid.name != null) return false;
        return areaId != null ? !areaId.equals(grid.areaId) : grid.areaId != null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (delFlag != null ? delFlag.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (areaId != null ? areaId.hashCode() : 0);
        return result;
    }

    public static final Creator<Grid> CREATOR = new Creator<Grid>() {
        @Override
        public Grid createFromParcel(Parcel in) {
            return new Grid(in);
        }

        @Override
        public Grid[] newArray(int size) {
            return new Grid[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(delFlag);
        dest.writeString(name);
        dest.writeString(areaId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}

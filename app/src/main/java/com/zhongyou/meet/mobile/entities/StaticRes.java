package com.zhongyou.meet.mobile.entities;

/**
 * Created by whatisjava on 18-1-15.
 */

public class StaticRes implements Entity {

    private String imgUrl;

    private String videoUrl;

    private String downloadUrl;

    private String cooperationUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setCooperationUrl(String cooperationUrl) {
        this.cooperationUrl = cooperationUrl;
    }

    @Override
    public String toString() {
        return "StaticRes{" +
                "imgUrl='" + imgUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", cooperationUrl='" + cooperationUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaticRes staticRes = (StaticRes) o;

        if (imgUrl != null ? !imgUrl.equals(staticRes.imgUrl) : staticRes.imgUrl != null)
            return false;
        if (videoUrl != null ? !videoUrl.equals(staticRes.videoUrl) : staticRes.videoUrl != null)
            return false;
        if (downloadUrl != null ? !downloadUrl.equals(staticRes.downloadUrl) : staticRes.downloadUrl != null)
            return false;
        return cooperationUrl != null ? cooperationUrl.equals(staticRes.cooperationUrl) : staticRes.cooperationUrl == null;
    }

    @Override
    public int hashCode() {
        int result = imgUrl != null ? imgUrl.hashCode() : 0;
        result = 31 * result + (videoUrl != null ? videoUrl.hashCode() : 0);
        result = 31 * result + (downloadUrl != null ? downloadUrl.hashCode() : 0);
        result = 31 * result + (cooperationUrl != null ? cooperationUrl.hashCode() : 0);
        return result;
    }
}

package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.SurfaceView;

public class AudienceVideo implements Parcelable {


    private String uname;

    private String postTypeName;

    private int auditStatus; // 1.已认证

    private int callStatus; // 0-未通话，1-连接中，2-正在通话

    private boolean handsUp; // true-申请发言，false-未申请发言

    private int videoStatus;//0 正常 1 卡顿 2 播放失败

    public int getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(int videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPostTypeName() {
        return postTypeName;
    }

    public void setPostTypeName(String postTypeName) {
        this.postTypeName = postTypeName;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    public boolean isHandsUp() {
        return handsUp;
    }

    public void setHandsUp(boolean handsUp) {
        this.handsUp = handsUp;
    }





    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int uid;

    private String name;

    private int volume;

    private boolean broadcaster;

    private SurfaceView surfaceView;

    private boolean muted;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getName() {
        if (name==null){
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name="";
        this.name = name;
    }

    public boolean isBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(boolean broadcaster) {
        this.broadcaster = broadcaster;
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AudienceVideo that = (AudienceVideo) o;

        if (uid != that.uid) return false;
        if (broadcaster != that.broadcaster) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (broadcaster ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AudienceVideo{" +
                "uname='" + uname + '\'' +
                ", postTypeName='" + postTypeName + '\'' +
                ", auditStatus=" + auditStatus +
                ", callStatus=" + callStatus +
                ", handsUp=" + handsUp +
                ", position=" + position +
                ", uid=" + uid +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", broadcaster=" + broadcaster +
                ", muted=" + muted +
                '}';
    }

    public AudienceVideo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uname);
        dest.writeString(this.postTypeName);
        dest.writeInt(this.auditStatus);
        dest.writeInt(this.callStatus);
        dest.writeByte(this.handsUp ? (byte) 1 : (byte) 0);
        dest.writeInt(this.videoStatus);
        dest.writeInt(this.position);
        dest.writeInt(this.uid);
        dest.writeString(this.name);
        dest.writeInt(this.volume);
        dest.writeByte(this.broadcaster ? (byte) 1 : (byte) 0);
        dest.writeByte(this.muted ? (byte) 1 : (byte) 0);
    }

    protected AudienceVideo(Parcel in) {
        this.uname = in.readString();
        this.postTypeName = in.readString();
        this.auditStatus = in.readInt();
        this.callStatus = in.readInt();
        this.handsUp = in.readByte() != 0;
        this.videoStatus = in.readInt();
        this.position = in.readInt();
        this.uid = in.readInt();
        this.name = in.readString();
        this.volume = in.readInt();
        this.broadcaster = in.readByte() != 0;
        this.muted = in.readByte() != 0;
    }

    public static final Creator<AudienceVideo> CREATOR = new Creator<AudienceVideo>() {
        @Override
        public AudienceVideo createFromParcel(Parcel source) {
            return new AudienceVideo(source);
        }

        @Override
        public AudienceVideo[] newArray(int size) {
            return new AudienceVideo[size];
        }
    };
}

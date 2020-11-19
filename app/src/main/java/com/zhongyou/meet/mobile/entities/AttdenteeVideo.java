package com.zhongyou.meet.mobile.entities;

import android.view.SurfaceView;

public class AttdenteeVideo {

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
        return name;
    }

    public void setName(String name) {
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

    private int startX;
    private int StatyY;

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStatyY() {
        return StatyY;
    }

    public void setStatyY(int statyY) {
        StatyY = statyY;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttdenteeVideo that = (AttdenteeVideo) o;

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
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", volume=" + volume +
                ", broadcaster=" + broadcaster +
                ", surfaceView=" + surfaceView +
                ", muted=" + muted +
                '}';
    }
}

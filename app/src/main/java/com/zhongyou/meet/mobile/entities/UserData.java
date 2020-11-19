package com.zhongyou.meet.mobile.entities;

public class UserData {

    private Wechat wechat;
    private User user;
    private Device device;

    public Wechat getWechat() {
        return wechat;
    }

    public void setWechat(Wechat wechat) {
        this.wechat = wechat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "wechat=" + wechat +
                ", user=" + user +
                ", device=" + device +
                '}';
    }
}

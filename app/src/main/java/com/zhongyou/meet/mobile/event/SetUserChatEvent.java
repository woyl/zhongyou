package com.zhongyou.meet.mobile.event;

public class SetUserChatEvent {

    private boolean isOnline;

    public SetUserChatEvent(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}

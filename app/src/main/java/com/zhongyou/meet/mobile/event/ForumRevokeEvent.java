package com.zhongyou.meet.mobile.event;

import com.zhongyou.meet.mobile.entities.ChatMesData;

public class ForumRevokeEvent {
    public ChatMesData.PageDataEntity getEntity() {
        return entity;
    }

    public void setEntity(ChatMesData.PageDataEntity entity) {
        this.entity = entity;
    }

    private ChatMesData.PageDataEntity entity;
}

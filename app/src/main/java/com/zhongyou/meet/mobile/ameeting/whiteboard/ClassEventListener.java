package com.zhongyou.meet.mobile.ameeting.whiteboard;

import com.zhongyou.meet.mobile.entities.User;

import androidx.annotation.Nullable;

public interface ClassEventListener {



    void onClassStateChanged(boolean isBegin, long time);

    void onWhiteboardChanged(String uuid, String roomToken);

    void onLockWhiteboard(boolean locked);

    void onJoinRoomSuccess(String roomToken);


}

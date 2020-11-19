package io.agora.whiteboard.netless.listener;

import com.herewhite.sdk.domain.PlayerPhase;

public interface ReplayEventListener {

//    void onPlayerPrepared(ReplayManager replayBoard);

    void onPhaseChanged(PlayerPhase playerPhase);

    void onLoadFirstFrame();

    void onScheduleTimeChanged(long l);

}

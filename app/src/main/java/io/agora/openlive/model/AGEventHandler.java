package io.agora.openlive.model;

import io.agora.rtc.IRtcEngineEventHandler;

public interface AGEventHandler {
	void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

	void onJoinChannelSuccess(String channel, int uid, int elapsed);

	void onUserOffline(int uid, int reason);

	void onConnectionLost();

	void onConnectionInterrupted();

	void onUserMuteVideo(int uid, boolean muted);

	void onUserMuteAudio(int uid, boolean muted);

	void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume);

	void onLastmileQuality(int quality);

	void onNetworkQuality(int uid, int txQuality, int rxQuality);

	void onWarning(int warn);

	void onError(int err);

	 void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed);

	 void onRtcStats(IRtcEngineEventHandler.RtcStats stats);

	void onLocalVideoStateChanged(int localVideoState, int error);
}

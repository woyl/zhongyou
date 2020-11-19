package io.agora.openlive.model;

import android.content.Context;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.agora.rtc.IRtcEngineEventHandler;
import timber.log.Timber;

public class MyEngineEventHandler {
	public MyEngineEventHandler(Context ctx, EngineConfig config) {
		this.mContext = ctx;
		this.mConfig = config;
	}

	private final EngineConfig mConfig;

	private final Context mContext;

	private final ConcurrentMap<AGEventHandler, Integer> mEventHandlerList = new ConcurrentHashMap();

	public void addEventHandler(AGEventHandler handler) {
		this.mEventHandlerList.put(handler, 0);
	}

	public void removeEventHandler(AGEventHandler handler) {
		this.mEventHandlerList.remove(handler);
	}

	final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
		private final Logger log = LoggerFactory.getLogger(this.getClass());


		@Override
		public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
			log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + width + " " + height + " " + elapsed);

			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
			}
		}

		@Override
		public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
			super.onRemoteVideoStateChanged(uid, state, reason, elapsed);

			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onRemoteVideoStateChanged(uid, state, reason, elapsed);
			}
		}

		/**
		 * 报告本地用户的网络质量，该回调函数每 2 秒触发一次
		 * @param quality
		 */
		@Override
		public void onLastmileQuality(int quality) {
			log.debug("onLastmileQuality " + quality);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onLastmileQuality(quality);
			}
		}


		@Override
		public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
			log.debug("onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
		}

		@Override
		public void onUserJoined(int uid, int elapsed) {
		}

		@Override
		public void onUserOffline(int uid, int reason) {
			// FIXME this callback may return times
			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onUserOffline(uid, reason);
			}
		}

		@Override
		public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
			log.debug("onNetworkQuality " + uid + "" + txQuality + "" + rxQuality);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onNetworkQuality(uid, txQuality, rxQuality);
			}
		}



		@Override
		public void onUserMuteVideo(int uid, boolean muted) {
		}

		@Override
		public void onUserMuteAudio(int uid, boolean muted) {
			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onUserMuteAudio(uid, muted);
			}
		}

		@Override
		public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onAudioVolumeIndication(speakers, totalVolume);
			}
		}


		@Override
		public void onRtcStats(RtcStats stats) {
//			Timber.e("stats.lastmileDelay---->%s", stats.lastmileDelay);
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onRtcStats(stats);
			}
		}


		@Override
		public void onLeaveChannel(RtcStats stats) {

		}

		@Override
		public void onConnectionLost() {
			log.debug("onConnectionLost");
			Iterator<AGEventHandler> iterator = mEventHandlerList.keySet().iterator();
			while (iterator.hasNext()) {
				AGEventHandler handler = iterator.next();
				handler.onConnectionLost();
			}
		}


		@Override
		public void onError(int err) {
			super.onError(err);
			log.debug("onError " + err);

			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onError(err);
			}
		}

		@Override
		public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
			log.debug("onJoinChannelSuccess " + channel + " " + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onJoinChannelSuccess(channel, uid, elapsed);
			}
		}

		public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
			log.debug("onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
		}

		public void onWarning(int warn) {
			log.debug("onWarning " + warn);

			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onWarning(warn);
			}
		}

		@Override
		public void onLocalVideoStateChanged(int localVideoState, int error) {
			Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
			while (it.hasNext()) {
				AGEventHandler handler = it.next();
				handler.onLocalVideoStateChanged(localVideoState,error);
			}
		}
	};

}

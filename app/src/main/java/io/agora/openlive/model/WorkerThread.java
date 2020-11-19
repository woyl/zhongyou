package io.agora.openlive.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class WorkerThread extends Thread {
	private final static Logger log = LoggerFactory.getLogger(WorkerThread.class);

	private final Context mContext;

	private static final int ACTION_WORKER_THREAD_QUIT = 0X1010; // quit this thread

	private static final int ACTION_WORKER_JOIN_CHANNEL = 0X2010;

	private static final int ACTION_WORKER_LEAVE_CHANNEL = 0X2011;

	private static final int ACTION_WORKER_CONFIG_ENGINE = 0X2012;

	private static final int ACTION_WORKER_PREVIEW = 0X2014;

	private static final class WorkerThreadHandler extends Handler {

		private WorkerThread mWorkerThread;

		WorkerThreadHandler(WorkerThread thread) {
			this.mWorkerThread = thread;
		}

		public void release() {
			mWorkerThread = null;
		}

		@Override
		public void handleMessage(Message msg) {
			if (this.mWorkerThread == null) {
				log.warn("handler is already released! " + msg.what);
				return;
			}

			switch (msg.what) {
				case ACTION_WORKER_THREAD_QUIT:
					mWorkerThread.exit();
					break;
				case ACTION_WORKER_JOIN_CHANNEL:
					String[] data = (String[]) msg.obj;
					mWorkerThread.joinChannel(data[0], data[1], msg.arg1);
					break;
				case ACTION_WORKER_LEAVE_CHANNEL:
					String channel = (String) msg.obj;
					mWorkerThread.leaveChannel(channel);
					break;
				case ACTION_WORKER_CONFIG_ENGINE:
					Object[] configData = (Object[]) msg.obj;
					if (configData[1] instanceof VideoEncoderConfiguration.VideoDimensions){
						mWorkerThread.configEngine((int) configData[0], (VideoEncoderConfiguration.VideoDimensions) configData[1]);
					}
					break;
				case ACTION_WORKER_PREVIEW:
					Object[] previewData = (Object[]) msg.obj;
					mWorkerThread.preview((boolean) previewData[0], (SurfaceView) previewData[1], (int) previewData[2]);
					break;
			}
		}
	}

	private WorkerThreadHandler mWorkerHandler;

	private boolean mReady;

	public final void waitForReady() {
		while (!mReady) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.debug("wait for " + WorkerThread.class.getSimpleName());
		}
	}

	@Override
	public void run() {
		log.trace("start to run");
		Looper.prepare();

		mWorkerHandler = new WorkerThreadHandler(this);

		ensureRtcEngineReadyLock();

		mReady = true;

		// enter thread looper
		Looper.loop();
	}

	private RtcEngine mRtcEngine;

	public final void joinChannel(final String channelKey, final String channel, int uid) {
		if (Thread.currentThread() != this) {
			log.warn("joinChannel() - worker thread asynchronously " + channel + " " + uid);
			Message envelop = new Message();
			envelop.what = ACTION_WORKER_JOIN_CHANNEL;
			envelop.obj = new String[]{channelKey, channel};
			envelop.arg1 = uid;
			if (mWorkerHandler == null) {
				mWorkerHandler = new WorkerThreadHandler(this);
			}
			mWorkerHandler.sendMessage(envelop);
			return;
		}

		ensureRtcEngineReadyLock();
		int guideMobile = mRtcEngine.joinChannel(channelKey, channel, "GuideMobile", uid);

		Log.e("guideMobile", "joinChannel: " + guideMobile);

		mEngineConfig.mChannel = channel;

		log.debug("joinChannel " + channel + " " + uid);
	}

	public final void leaveChannel(String channel) {
		if (Thread.currentThread() != this) {
			log.warn("leaveChannel() - worker thread asynchronously " + channel);
			Message envelop = new Message();
			envelop.what = ACTION_WORKER_LEAVE_CHANNEL;
			envelop.obj = channel;
			if (mWorkerHandler == null) {
				mWorkerHandler = new WorkerThreadHandler(this);
			}
			mWorkerHandler.sendMessage(envelop);
			return;
		}

		if (mRtcEngine != null) {
			mRtcEngine.leaveChannel();
		}

		int clientRole = mEngineConfig.mClientRole;
		mEngineConfig.reset();
		log.debug("leaveChannel " + channel + " " + clientRole);
	}

	private EngineConfig mEngineConfig;

	public final EngineConfig getEngineConfig() {
		if (mEngineConfig == null) {
			this.mEngineConfig = new EngineConfig();
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
			this.mEngineConfig.mUid = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_UID, 0);
		}
		return mEngineConfig;
	}

	private final MyEngineEventHandler mEngineEventHandler;

	public final void configEngine(int cRole, VideoEncoderConfiguration.VideoDimensions vProfile) {
		if (Thread.currentThread() != this) {
			log.warn("configEngine() - worker thread asynchronously " + cRole + " " + vProfile);
			Message envelop = new Message();
			envelop.what = ACTION_WORKER_CONFIG_ENGINE;
			envelop.obj = new Object[]{cRole, vProfile};
			if (mWorkerHandler == null) {
				mWorkerHandler = new WorkerThreadHandler(this);
			}
			mWorkerHandler.sendMessage(envelop);
			return;
		}
		if (mEngineConfig == null) {
			this.mEngineConfig = new EngineConfig();
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
			this.mEngineConfig.mUid = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_UID, 0);
		}

		ensureRtcEngineReadyLock();
		mEngineConfig.mClientRole = cRole;
//		mEngineConfig.mVideoProfile = vProfile;
		mRtcEngine.setVideoEncoderConfiguration(
				new VideoEncoderConfiguration(vProfile,
						VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15
						, VideoEncoderConfiguration.STANDARD_BITRATE
						, VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE));
//		mRtcEngine.setVideoProfile(mEngineConfig.mVideoProfile, false);

		mRtcEngine.setClientRole(cRole);
		log.debug("configEngine " + cRole + " " + mEngineConfig.mVideoProfile);
	}

	public final void preview(boolean start, SurfaceView view, int uid) {
		if (Thread.currentThread() != this) {
			log.warn("preview() - worker thread asynchronously " + start + " " + view + " " + (uid & 0XFFFFFFFFL));
			Message envelop = new Message();
			envelop.what = ACTION_WORKER_PREVIEW;
			envelop.obj = new Object[]{start, view, uid};
			if (mWorkerHandler == null) {
				mWorkerHandler = new WorkerThreadHandler(this);
			}
			mWorkerHandler.sendMessage(envelop);
			return;
		}

		ensureRtcEngineReadyLock();
		if (start) {
			mRtcEngine.setupLocalVideo(new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid));
			mRtcEngine.startPreview();
		} else {
			mRtcEngine.stopPreview();
		}
	}

	public static String getDeviceID(Context context) {
		// XXX according to the API docs, this value may change after factory reset
		// use Android id as device id
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	private String appId;

	private RtcEngine ensureRtcEngineReadyLock() {
		if (mRtcEngine == null) {
//            String appId = mContext.getString(R.string.private_app_id);
			if (TextUtils.isEmpty(appId)) {
				appId = "5772e481b21e40eebbe9cb8c7f2a72de";
			}
			try {
				mRtcEngine = RtcEngine.create(mContext, appId, mEngineEventHandler.mRtcEventHandler);
				mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
				mRtcEngine.enableVideo();
				Log.v("log_file_path", Environment.getExternalStorageDirectory().getPath());
				mRtcEngine.setLogFile(Environment.getExternalStorageDirectory().getPath() + "/mobile_expostor.log");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mRtcEngine;
	}

	public MyEngineEventHandler eventHandler() {
		if (mEngineEventHandler == null) {
			this.mEngineConfig = new EngineConfig();
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
			this.mEngineConfig.mUid = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_UID, 0);

			return new MyEngineEventHandler(mContext, this.mEngineConfig);
		}
		return mEngineEventHandler;
	}

	public RtcEngine getRtcEngine() {
		if (mRtcEngine == null) {
			try {
				mRtcEngine = RtcEngine.create(mContext, appId, mEngineEventHandler.mRtcEventHandler);
				mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
				mRtcEngine.enableVideo();
				mRtcEngine.setLogFile(Environment.getExternalStorageDirectory().getPath() + "/mobile_expostor.log");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mRtcEngine;
	}

	/**
	 * call this method to exit
	 * should ONLY call this method when this thread is running
	 */
	public final void exit() {
		if (Thread.currentThread() != this) {
			log.warn("exit() - exit app thread asynchronously");
			if (mWorkerHandler == null) {
				mWorkerHandler = new WorkerThreadHandler(this);
			}
			mWorkerHandler.sendEmptyMessage(ACTION_WORKER_THREAD_QUIT);
			return;
		}

		mReady = false;

		// TODO should remove all pending(read) messages

		log.debug("exit() > start");

		// exit thread looper
		Looper.myLooper().quit();

		if (mWorkerHandler != null) {
			mWorkerHandler.release();
		}


		log.debug("exit() > end");
	}

	public WorkerThread(Context context, String appId) {
		this.mContext = context;
		this.appId = appId;

		this.mEngineConfig = new EngineConfig();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		this.mEngineConfig.mUid = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_UID, 0);

		this.mEngineEventHandler = new MyEngineEventHandler(mContext, this.mEngineConfig);
	}
}

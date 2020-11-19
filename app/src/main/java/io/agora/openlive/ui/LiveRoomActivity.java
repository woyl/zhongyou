package io.agora.openlive.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entities.base.BaseErrorBean;
import com.zhongyou.meet.mobile.event.HangOnEvent;
import com.zhongyou.meet.mobile.event.HangUpEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.receiver.PhoneReceiver;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.UIDUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.model.VideoStatusData;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import rx.Subscription;
import rx.functions.Action1;

public class LiveRoomActivity extends BaseActivity implements AGEventHandler {

    private final String TAG = LiveRoomActivity.class.getSimpleName();
    private final static Logger log = LoggerFactory.getLogger(LiveRoomActivity.class);

    private GridVideoViewContainer mGridVideoViewContainer;

    private RelativeLayout mSmallVideoViewDock;

    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.mUid

    private PhoneReceiver phoneReceiver;

    private AudioManager mAudioManager;

    private TextView textChannelName;

    private String channelName, callInfo;
    private Agora agora;

    private int remoteUid;
    private Subscription hangonScription;

    private AgoraAPIOnlySignal agoraAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);


    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.d(TAG, "AUDIOFOCUS_GAIN [" + this.hashCode() + "]");

                    break;
                case AudioManager.AUDIOFOCUS_LOSS:

                    Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK [" + this.hashCode() + "]");
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);

        Intent i = getIntent();
        int cRole = i.getIntExtra(ConstantApp.ACTION_KEY_CROLE, 0);

        if (cRole == 0) {
            throw new RuntimeException("Should not reach here");
        }

        channelName = i.getStringExtra(ConstantApp.ACTION_KEY_ROOM_NAME);

        config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));

        callInfo = i.getStringExtra("callInfo");
        agora = i.getParcelableExtra("agora");

        phoneReceiver = new PhoneReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(phoneReceiver, intentFilter);

        doConfigEngine(cRole);

        mGridVideoViewContainer = (GridVideoViewContainer) findViewById(R.id.grid_video_view_container);
        mGridVideoViewContainer.setItemEventHandler(new VideoViewEventListener() {
            @Override
            public void onItemDoubleClick(View v, Object item) {
                log.debug("onItemDoubleClick " + v + " " + item);

                if (mUidsList.size() < 2) {
                    return;
                }

                VideoStatusData user = (VideoStatusData) item;

                int uid = user.mUid == config().mUid ? remoteUid : config().mUid;
                switchToSmallVideoView(uid);


            }
        });

        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
        rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);

        mUidsList.put(config().mUid, surfaceV); // get first surface view

        mGridVideoViewContainer.initViewContainer(getApplicationContext(), config().mUid, mUidsList); // first is now full view
        worker().preview(true, surfaceV, config().mUid);


        if ("true".equals(agora.getIsTest())) {
            worker().joinChannel(null, channelName, config().mUid);
        } else {
            worker().joinChannel(agora.getToken(), channelName, config().mUid);
        }

        agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());
        agoraAPI.callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LiveRoomActivity.this, "观众登录信令系统成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                agoraAPI.channelJoin(channelName);
            }

            @Override
            public void onLoginFailed(final int ecode) {
                super.onLoginFailed(ecode);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LiveRoomActivity.this, "观众登录信令系统失败" + ecode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onChannelJoined(String channelID) {
                super.onChannelJoined(channelID);

            }

            @Override
            public void onChannelJoinFailed(String channelID, int ecode) {
                super.onChannelJoinFailed(channelID, ecode);

            }

            @Override
            public void onChannelQueryUserNumResult(String channelID, int ecode, final int num) {
                super.onChannelQueryUserNumResult(channelID, ecode, num);
            }

            @Override
            public void onChannelUserJoined(String account, int uid) {
                super.onChannelUserJoined(account, uid);
            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                super.onChannelUserLeaved(account, uid);
            }

            @Override
            public void onUserAttrResult(final String account, final String name, final String value) {
                super.onUserAttrResult(account, name, value);
            }

            @Override
            public void onMessageSendSuccess(String messageID) {
                super.onMessageSendSuccess(messageID);
            }

            @Override
            public void onMessageSendError(String messageID, int ecode) {
                super.onMessageSendError(messageID, ecode);
            }

            @Override
            public void onMessageInstantReceive(final String account, final int uid, final String msg) {
                super.onMessageInstantReceive(account, uid, msg);
            }

            @Override
            public void onMessageChannelReceive(String channelID, String account, int uid, final String msg) {
                super.onMessageChannelReceive(channelID, account, uid, msg);
            }

            @Override
            public void onError(final String name, final int ecode, final String desc) {
                super.onError(name, ecode, desc);
                if (BuildConfig.DEBUG) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LiveRoomActivity.this, "name: " + name + "ecode: " + ecode + "desc: " + desc, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        agoraAPI.login(agora.getAppID(), "" + config().mUid, agora.getSignalingKey(), 0, "");

        textChannelName = findViewById(R.id.channel_name);
        textChannelName.setText(callInfo);

        findViewById(R.id.bottom_action_end_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCallRecord();
            }
        });

        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                worker().getRtcEngine().switchCamera();
            }
        });

        hangonScription = RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof HangOnEvent) {
                    hangonScription.unsubscribe();
                    ApiClient.getInstance().startOrStopOrRejectCallExpostor(channelName, "7", new ExpostorCallback());
                }
            }
        });
    }

    class ExpostorCallback extends OkHttpCallback<BaseErrorBean> {

        @Override
        public void onSuccess(BaseErrorBean entity) {
            Log.d("stop when calling", entity.toString());
            Toast.makeText(LiveRoomActivity.this, "通话被其他应用打断", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish() {
            finish();
        }
    }

    private void doConfigEngine(int cRole) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
        int vProfile = ConstantApp.VIDEO_PROFILES[prefIndex];
        worker().configEngine(cRole, VideoEncoderConfiguration.VD_320x240);
    }

    @Override
    protected void deInitUIandEvent() {
        doLeaveChannel();
        event().removeEventHandler(this);

        mUidsList.clear();
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        worker().preview(false, null, config().mUid);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        doRenderRemoteUi(uid);
    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    return;
                }

                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                mUidsList.put(uid, surfaceV);

                boolean useDefaultLayout = mViewType == VIEW_TYPE_DEFAULT && mUidsList.size() != 2;

                surfaceV.setZOrderOnTop(!useDefaultLayout);
                surfaceV.setZOrderMediaOverlay(!useDefaultLayout);

                rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                remoteUid = uid;

                if (useDefaultLayout) {
                    log.debug("doRenderRemoteUi LAYOUT_TYPE_DEFAULT " + (uid & 0xFFFFFFFFL));
                    switchToDefaultVideoView();
                } else {
                    int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
                    log.debug("doRenderRemoteUi LAYOUT_TYPE_SMALL " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
                    switchToSmallVideoView(bigBgUid);
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    log.debug("already added to UI, ignore it " + (uid & 0xFFFFFFFFL) + " " + mUidsList.get(uid));
                    return;
                }

                worker().getEngineConfig().mUid = uid;

                SurfaceView surfaceV = mUidsList.remove(0);
                if (surfaceV != null) {
                    mUidsList.put(uid, surfaceV);
                }

            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        doRemoveRemoteUi(uid);
    }

    @Override
    public void onConnectionLost() {

    }

    @Override
    public void onConnectionInterrupted() {

    }

    @Override
    public void onUserMuteVideo(final int uid, final boolean muted) {
        if (BuildConfig.DEBUG) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LiveRoomActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {

    }

    @Override
    public void onWarning(int warn) {

    }

    @Override
    public void onError(int err) {

    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state,int i ,int x) {

    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {

    }

    @Override
    public void onLocalVideoStateChanged(int localVideoState, int error) {

    }


    private void requestRemoteStreamType(final int currentHostCount) {
        log.debug("requestRemoteStreamType " + currentHostCount);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap.Entry<Integer, SurfaceView> highest = null;
                for (HashMap.Entry<Integer, SurfaceView> pair : mUidsList.entrySet()) {
                    log.debug("requestRemoteStreamType " + currentHostCount + " local " + (config().mUid & 0xFFFFFFFFL) + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getHeight() + " " + pair.getValue().getWidth());
                    if (pair.getKey() != config().mUid && (highest == null || highest.getValue().getHeight() < pair.getValue().getHeight())) {
                        if (highest != null) {
                            rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_LOW);
                            log.debug("setRemoteVideoStreamType switch highest VIDEO_STREAM_LOW " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                        }
                        highest = pair;
                    } else if (pair.getKey() != config().mUid && (highest != null && highest.getValue().getHeight() >= pair.getValue().getHeight())) {
                        rtcEngine().setRemoteVideoStreamType(pair.getKey(), Constants.VIDEO_STREAM_LOW);
                        log.debug("setRemoteVideoStreamType VIDEO_STREAM_LOW " + currentHostCount + " " + (pair.getKey() & 0xFFFFFFFFL) + " " + pair.getValue().getWidth() + " " + pair.getValue().getHeight());
                    }
                }
                if (highest != null && highest.getKey() != 0) {
                    rtcEngine().setRemoteVideoStreamType(highest.getKey(), Constants.VIDEO_STREAM_HIGH);
                    log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + currentHostCount + " " + (highest.getKey() & 0xFFFFFFFFL) + " " + highest.getValue().getWidth() + " " + highest.getValue().getHeight());
                }
            }
        }, 500);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }


                Object target = mUidsList.remove(uid);
                if (target == null) {
                    return;
                }

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }

                log.debug("doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL) + " " + mViewType);

                if (mViewType == VIEW_TYPE_DEFAULT || uid == bigBgUid) {
                    switchToDefaultVideoView();
                } else {
                    switchToSmallVideoView(bigBgUid);
                }
                stopCallRecord();
            }
        });
    }

    public void stopCallRecord(){
        ApiClient.getInstance().startOrStopOrRejectCallExpostor(channelName, "9", new OkHttpCallback<BaseErrorBean>() {
            @Override
            public void onSuccess(BaseErrorBean entity) {
                Log.d("stop receive", entity.toString());
            }

            @Override
            public void onFinish() {
                RxBus.sendMessage(new HangUpEvent());
                finish();
            }
        });

    }

    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private void switchToDefaultVideoView() {
        if (mSmallVideoViewDock != null)
            mSmallVideoViewDock.setVisibility(View.GONE);
        mGridVideoViewContainer.initViewContainer(getApplicationContext(), config().mUid, mUidsList);

        mViewType = VIEW_TYPE_DEFAULT;

        int sizeLimit = mUidsList.size();
        if (sizeLimit > ConstantApp.MAX_PEER_COUNT + 1) {
            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1;
        }
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).mUid;
            if (config().mUid != uid) {
                rtcEngine().setRemoteVideoStreamType(uid, Constants.VIDEO_STREAM_HIGH);
                log.debug("setRemoteVideoStreamType VIDEO_STREAM_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
            }
        }
    }

    private void switchToSmallVideoView(int uid) {
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(uid, mUidsList.get(uid));
        mGridVideoViewContainer.initViewContainer(getApplicationContext(), uid, slice);

        bindToSmallVideoView(uid);

        mViewType = VIEW_TYPE_SMALL;

        requestRemoteStreamType(mUidsList.size());
    }

    public int mViewType = VIEW_TYPE_DEFAULT;

    public static final int VIEW_TYPE_DEFAULT = 0;

    public static final int VIEW_TYPE_SMALL = 1;

    private void bindToSmallVideoView(int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        RecyclerView recycler = (RecyclerView) findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, exceptUid, mUidsList, new VideoViewEventListener() {
                @Override
                public void onItemDoubleClick(View v, Object item) {
                    switchToDefaultVideoView();
                }
            });
            mSmallVideoViewAdapter.setHasStableIds(true);
        }
        recycler.setHasFixedSize(true);

        recycler.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        recycler.setAdapter(mSmallVideoViewAdapter);

        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        if (!create) {
            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, null, null);
        }
        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop..........................");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.out.println("onDestroy..........................");

        hangonScription.unsubscribe();

        unregisterReceiver(phoneReceiver);

        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);

    }
}

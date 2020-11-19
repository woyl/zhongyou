package com.zhongyou.meet.mobile.business;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.databinding.OnCallActivityBinding;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.entities.base.BaseErrorBean;
import com.zhongyou.meet.mobile.event.CallEvent;
import com.zhongyou.meet.mobile.event.TvLeaveChannel;
import com.zhongyou.meet.mobile.event.TvTimeoutHangUp;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.UIDUtil;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;

import java.util.HashMap;
import java.util.Map;

import io.agora.openlive.ui.MainActivity;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by wufan on 2017/8/3.
 */

public class OnCallActivity extends BaseDataBindingActivity<OnCallActivityBinding> {
    public static final String TAG = "OnCallActivity";

    private String channelId;
    private String tvSocketId;
    private String callInfo;

    private MediaPlayer mp;

    @Override
    public String getStatisticsTag() {
        return "被呼叫";
    }

    public static void actionStart(Context context, String channelId, String tvSocketId, String callInfo,String photo,String deviceInfo, int shopOwnerResolution, String shopPhoto) {
        Intent intent = new Intent(context, OnCallActivity.class);
        //service中调用需要添加flag
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("channelId", channelId);
        intent.putExtra("tvSocketId", tvSocketId);
        intent.putExtra("callInfo", callInfo);
        intent.putExtra("photo", photo);
        intent.putExtra("deviceInfo", deviceInfo);
        intent.putExtra("shopOwnerResolution", shopOwnerResolution);
        intent.putExtra("shopPhoto", shopPhoto);
        context.startActivity(intent);
    }

    @Override
    protected void initExtraIntent() {
        channelId = getIntent().getStringExtra("channelId");
        tvSocketId = getIntent().getStringExtra("tvSocketId");
        callInfo = getIntent().getStringExtra("callInfo");

        avatar = getIntent().getStringExtra("photo");
        deviceInfo = getIntent().getStringExtra("deviceInfo");
        shopOwnerResolution = getIntent().getIntExtra("shopOwnerResolution", 0);
        shopPhoto = getIntent().getStringExtra("shopPhoto");
    }

    private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        subscription = RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof TvLeaveChannel) {
                    ToastUtils.showToast("顾客已经挂断");
                    releaseMP();
                    ApiClient.getInstance().startOrStopOrRejectCallExpostor(channelId, "8", new OkHttpCallback<BaseErrorBean>() {
                        @Override
                        public void onSuccess(BaseErrorBean entity) {
                            Log.d(TAG, entity.toString());
                            finish();
                        }

                        @Override
                        public void onFailure(int errorCode, BaseException exception) {
                            Toast.makeText(getApplication(), "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinish() {
                            releaseMP();
                        }
                    });
                }else if(o instanceof TvTimeoutHangUp){
                    subscription.unsubscribe();
                    ToastUtils.showToast("未接听呼叫");
                    releaseMP();
                    finish();
                }
            }
        });

        mp = new MediaPlayer();
        try {
            mp.setDataSource(OnCallActivity.this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        avatar = getIntent().getStringExtra("photo");
        name = callInfo.split(" ")[1];

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        mBinding.mWaterWave.startAnimation(AnimationUtils.loadAnimation(this, R.anim.water_wave));

    }

    @Override
    protected int initContentView() {
        return R.layout.on_call_activity;
    }


    @Override
    protected void initView() {
        mBinding.mIvAccept.setOnClickListener(this);
        mBinding.mIvReject.setOnClickListener(this);
        mBinding.mTvCallInfo.setText(callInfo);
    }

    @Override
    public void normalOnClick(View v) {
        switch (v.getId()) {
            case R.id.mIvAccept:
                ZYAgent.onEvent(mContext,"接听按钮");
                releaseMP();
                ApiClient.getInstance().startOrStopOrRejectCallExpostor(channelId, "1", new OkHttpCallback<BaseErrorBean>() {
                    @Override
                    public void onSuccess(BaseErrorBean entity) {
                        Log.d("start receive", entity.toString());
                        RxBus.sendMessage(new CallEvent(true, tvSocketId));
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("channel", channelId);
                        params.put("account", UIDUtil.generatorUID(Preferences.getUserId()));
                        params.put("role", "Publisher");
                        ApiClient.getInstance().getAgoraKey(OnCallActivity.this, params, new AgoraCallback());
                    }

                    @Override
                    public void onFailure(int errorCode, BaseException exception) {
                        Toast.makeText(getApplication(), "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.mIvReject:
                ZYAgent.onEvent(mContext,"拒绝按钮");
                releaseMP();
                ApiClient.getInstance().startOrStopOrRejectCallExpostor(channelId, "2", new OkHttpCallback<BaseErrorBean>() {
                    @Override
                    public void onSuccess(BaseErrorBean entity) {
                        Log.d("reject receive", entity.toString());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        RxBus.sendMessage(new CallEvent(false, tvSocketId));
                        finish();
                    }

                    @Override
                    public void onFailure(int errorCode, BaseException exception) {
                        Toast.makeText(getApplication(), "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private String deviceInfo;
    private int shopOwnerResolution;
    private String shopPhoto;
    private String avatar, name;

    class AgoraCallback extends OkHttpCallback<BaseBean<Agora>> {

        @Override
        public void onSuccess(BaseBean<Agora> agoraBucket) {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("channelId", channelId);
            intent.putExtra("callInfo", callInfo);
            intent.putExtra("agora", agoraBucket.getData());

            intent.putExtra("deviceInfo", deviceInfo);
            intent.putExtra("shopOwnerResolution", shopOwnerResolution);
            intent.putExtra("photo", avatar);
            intent.putExtra("name", name);
            intent.putExtra("shopPhoto", shopPhoto);

            startActivity(intent);
            finish();
            ZYAgent.onEvent(BaseApplication.getInstance(),"点击事件 拨打电话 进入房间");
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            Toast.makeText(getApplication(), "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
        }

    }

    private Subscription subscription;

    @Override
    public void onBackPressed() {
        //返回键禁用
    }

    private void releaseMP(){
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        releaseMP();
        super.onDestroy();
    }


    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.d(TAG, "AUDIOFOCUS_GAIN [" + this.hashCode() + "]");
                    if (!mp.isPlaying()) {
                        try {
                            mp.setDataSource(OnCallActivity.this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                            mp.prepare();
                            mp.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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

}

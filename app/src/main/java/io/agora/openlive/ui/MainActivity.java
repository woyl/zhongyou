package io.agora.openlive.ui;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.utils.Logger;

import java.util.Locale;

import io.agora.openlive.model.ConstantApp;
import io.agora.rtc.Constants;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final String FTAG = Logger.lifecycle;

    private String channelId, callInfo;

    private Agora agora;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            forwardToLiveRoom(Constants.CLIENT_ROLE_BROADCASTER);
        }
    };

    private AudioManager mAudioManager;

    public static synchronized String getRoot(Context context) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return String.format(Locale.CHINA, "%s/Android/data/com.zhongyou.meet.mobile/", Environment.getExternalStorageDirectory().getAbsolutePath());
        } else {
            return null;
        }
    }

	@Override
    protected void onResume() {
        super.onResume();
        Logger.d(FTAG + TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(FTAG + TAG, "onResume");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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
    protected void initUIandEvent() {
        channelId = getIntent().getStringExtra("channelId");
        callInfo = getIntent().getStringExtra("callInfo");
        agora = getIntent().getParcelableExtra("agora");
        setAppId(agora.getAppID());

        EditText textRoomName = (EditText) findViewById(R.id.room_name);
        textRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.toString().isEmpty();
                findViewById(R.id.button_join).setEnabled(!isEmpty);
            }
        });

        textRoomName.setText(channelId);

        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    protected void deInitUIandEvent() {
    }

    public void forwardToLiveRoom(int cRole) {
        final EditText v_room = (EditText) findViewById(R.id.room_name);
        String room = v_room.getText().toString();

        Intent i = new Intent(MainActivity.this, LiveRoomActivity.class);
        i.putExtra(ConstantApp.ACTION_KEY_CROLE, cRole);
        i.putExtra(ConstantApp.ACTION_KEY_ROOM_NAME, room);
        i.putExtra("callInfo", callInfo);
        i.putExtra("agora", agora);
        startActivity(i);

        finish();
    }

}

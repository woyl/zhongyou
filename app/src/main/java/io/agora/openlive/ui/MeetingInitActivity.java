package io.agora.openlive.ui;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;
import com.qiniu.android.utils.Json;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.ChairManActivity;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.core.GlobalConsts;
import com.zhongyou.meet.mobile.core.PlayService;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entities.ImUserInfo;
import com.zhongyou.meet.mobile.entiy.MeetingJoin;
import com.zhongyou.meet.mobile.persistence.Preferences;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.GroupNotificationMessage;

public class MeetingInitActivity extends BaseActivity {

	private static final String TAG = MeetingInitActivity.class.getSimpleName();
	private PlayService.MusicBinder musicBinder;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e(TAG, "角色为:" + meetingJoin.getRole());
			int role = getIntent().getIntExtra("role", 0);
			Log.e(TAG, "角色为role:=============" + role + "======================");
			if (role == 0) {
				forwardToLiveRoom(0);
			} else if (role == 1) {
				forwardToLiveRoom(1);
			} else if (role == 2) {
				forwardToLiveRoom(2);
			}
		}
	};

	private Agora agora;
	private MeetingJoin.DataBean meetingJoin;

	private AudioManager mAudioManager;
	private boolean mIsMaker;
	private MusicInfoReceiver receiver;
	private ServiceConnection serviceConn;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_openlive);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (mAudioManager != null) {
			mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		}

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

		registerReceiver();
		meetingJoin = getIntent().getParcelableExtra("meeting");
		agora = getIntent().getParcelableExtra("agora");
		mIsMaker = getIntent().getBooleanExtra("isMaker", true);
		BaseApplication.getInstance().setAgora(agora);
		setAppId(agora.getAppID());

		handler.sendEmptyMessageDelayed(0, 600);
	}

	private void registerReceiver() {
		receiver = new MusicInfoReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
		filter.addAction(GlobalConsts.ACTION_UPDATE_PROGRESS);
		filter.addAction(GlobalConsts.ACTION_STATR_MUSIC);
		filter.addAction(GlobalConsts.ACTION_PAUSE_MUSIC);
		filter.addAction(GlobalConsts.ACTION_LOCAL_MUSIC);
		filter.addAction(GlobalConsts.ACTION_ONLINE_MUSIC);
		filter.addAction(GlobalConsts.ACTION_NEXT_MUSIC);
		filter.addAction(GlobalConsts.ACTION_COMPLETE_MUSIC);

		registerReceiver(receiver, filter);
		//播放服务
		Intent intent = new Intent(this, PlayService.class);
		serviceConn = new ServiceConnection() {


			//连接异常断开
			public void onServiceDisconnected(ComponentName name) {

			}

			//连接成功
			public void onServiceConnected(ComponentName name, IBinder binder) {
				musicBinder = (PlayService.MusicBinder) binder;
				musicBinder.pause();
			}
		};
		bindService(intent, serviceConn, Service.BIND_AUTO_CREATE);
	}


	class MusicInfoReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null && action.equals(GlobalConsts.ACTION_UPDATE_PROGRESS)) {
				if (musicBinder != null) {
					musicBinder.pause();
				}
			}
		}

	}

	@Override
	protected void deInitUIandEvent() {
	}



	public void forwardToLiveRoom(int cRole) {
		Intent intent = null;
		Constant.currentGroupID = meetingJoin.getMeeting().getId();

		Logger.e("cRole :" + cRole + "-----" + "meetingJoin.getMeeting().getType(): " + meetingJoin.getMeeting().getType());
		if (cRole == 0) {//主持人进入
			Constant.videoType = 0;
			Constant.isChairMan = true;

			intent = new Intent(MeetingInitActivity.this, ChairManActivity.class);

		} else if (cRole == 1 || cRole == 2) {//参会人进入
			Constant.videoType = cRole;
			Constant.isChairMan = false;

			intent = new Intent(MeetingInitActivity.this, MeetingAudienceActivity.class);
		}

		if (intent != null) {
			intent.putExtra("meeting", meetingJoin);
			intent.putExtra("agora", agora);
			BaseApplication.getInstance().setAgora(agora);
			intent.putExtra("isMaker", true);
			verifyIsInGroup(intent);
		}

	}

	/**
	 * 获取融云群组登录信息，可以获取进入人数
	 * */
	private void verifyIsInGroup(Intent intent) {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("groupId", meetingJoin.getMeeting().getId());
		mApiService.queryGroupUser(jsonObject).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<JSONObject>() {

			@Override
			public void onSubscribe(Disposable d) {
			}

			@Override
			public void _onNext(JSONObject jsonObject) {

				Observable.create(new ObservableOnSubscribe<Boolean>() {
					@Override
					public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
						Logger.e(jsonObject.toJSONString());
						try {
							ImUserInfo imUserInfo = jsonObject.toJavaObject(ImUserInfo.class);
							if (imUserInfo.getErrcode() == 0) {
								if (imUserInfo.getData().getCode().equals("200")) {
									intent.putExtra("personNumber",imUserInfo.getData().getUsers().size());
									for (int i = 0; i < imUserInfo.getData().getUsers().size(); i++) {
										if (imUserInfo.getData().getUsers().get(i).getId().equals(Preferences.getUserId())) {
											emitter.onNext(true);
											return;
										}
									}
									emitter.onNext(false);
								}
							}
						} catch (Exception e) {
							emitter.onNext(false);
						}
					}
				}).observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.computation())
						.subscribe(new Observer<Boolean>() {
							@Override
							public void onSubscribe(Disposable d) {

							}

							@Override
							public void onNext(Boolean aBoolean) {
								Logger.e("aBoolean:-->"+aBoolean);
								if (!aBoolean) {
									runOnUiThread(() -> {
												groupCreateAndJoin(intent);
											});

								} else {
									startActivity(intent);
									finish();
								}
							}

							@Override
							public void onError(Throwable e) {
								showToastyWarn("进入聊天室失败");
								finish();
							}

							@Override
							public void onComplete() {

							}
						});
			}
		});
	}

	private void groupCreateAndJoin(Intent intent) {
		Logger.e(Thread.currentThread().getName());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("groupId", meetingJoin.getMeeting().getId());
		jsonObject.put("userId", Preferences.getUserId());
		jsonObject.put("groupName", meetingJoin.getMeeting().getTitle());
		mApiService.groupCreateAndJoin(jsonObject).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<JSONObject>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void _onNext(JSONObject jsonObject) {
				if (jsonObject.getInteger("errcode") == 0) {
					if (jsonObject.getJSONObject("data").getInteger("code") == 200) {
						startActivity(intent);
						finish();
						//向本地会话中插入一条消息。这条消息只是插入本地会话，
						// 不会实际发送给服务器和对方。该消息不一定插入本地数据库，
						// 是否入库由消息的属性决定
						JSONObject object = new JSONObject();
						object.put("operatorNickname", Preferences.getUserName());
						object.put("targetUserIds", new String[]{Preferences.getUserId()});
						object.put("targetUserDisplayNames", new String[]{Preferences.getUserName()});
						GroupNotificationMessage message = GroupNotificationMessage.obtain(
								Preferences.getUserId(),
								"Add",
								object.toString(),
								"加入群组");
						RongIMClient.getInstance().sendMessage(Conversation.ConversationType.GROUP, meetingJoin.getMeeting().getId(), message, "", "", new RongIMClient.SendMessageCallback() {
							@Override
							public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
								runOnUiThread(() -> {
									showToastyWarn("进入聊天室失败");
									finish();
								});

							}

							@Override
							public void onSuccess(Integer integer) {
								runOnUiThread(() -> {
									startActivity(intent);
									finish();
								});

							}
						});
					}else {
						showToastyWarn("进入聊天室失败");
						finish();
					}
				}else {
					showToastyWarn("进入聊天室失败");
					finish();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {

			if (receiver != null) {
				unregisterReceiver(receiver);
			}
			unbindService(serviceConn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package io.agora.openlive.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.SpliteGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.elvishew.xlog.XLog;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.jess.arms.utils.RxBus;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.tencent.mmkv.MMKV;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.NewAudienceVideoAdapter;
import com.zhongyou.meet.mobile.ameeting.adater.VideoAdapter;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.base.BaseMVVMActivity;
import com.zhongyou.meet.mobile.core.FloatingView;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entities.Audience;
import com.zhongyou.meet.mobile.entities.AudienceVideo;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.entities.ImUserInfo;
import com.zhongyou.meet.mobile.entities.Material;
import com.zhongyou.meet.mobile.entities.MeetingHostingStats;
import com.zhongyou.meet.mobile.entities.MeetingJoinStats;
import com.zhongyou.meet.mobile.entities.MeetingMaterialsPublish;
import com.zhongyou.meet.mobile.entities.MeetingScreenShot;
import com.zhongyou.meet.mobile.entities.QiniuToken;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.entiy.MeetingJoin;
import com.zhongyou.meet.mobile.event.IMMessgeEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.DisplayUtil;
import com.zhongyou.meet.mobile.utils.GridSpaceItemDecoration;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.SizeUtils;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.UIDUtil;
import com.zhongyou.meet.mobile.utils.helper.ImageHelper;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;
import com.zhongyou.meet.mobile.view.MyGridLayoutHelper;
import com.zhongyou.meet.mobile.view.PreviewPlayer;
import com.zhongyou.meet.mobile.view.SpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.bingoogolapple.transformerstip.TransformersTip;
import cn.bingoogolapple.transformerstip.gravity.TipGravity;
import cn.jpush.android.api.JPushInterface;
import cn.jzvd.JzvdStd;
import es.dmoral.toasty.Toasty;
import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.openlive.model.AGEventHandler;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.BeautyOptions;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rong.message.FileMessage;
import io.rong.message.GroupNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import me.jessyan.autosize.utils.AutoSizeUtils;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

import static io.agora.rtc.video.BeautyOptions.LIGHTENING_CONTRAST_NORMAL;

/**
 * 会议直播端-参会人
 */
public class MeetingAudienceActivity extends BaseActivity implements AGEventHandler {

	private final String TAG = MeetingAudienceActivity.class.getSimpleName();
	private Timer takePhotoTimer;
	private TimerTask takePhotoTimerTask;
	private final int CODE_REQUEST_TAKEPHOTO = 8011;

	private MeetingJoin.DataBean meetingJoin;
	private MeetingJoin.DataBean.MeetingBean meeting;
	private Agora agora;
	private String broadcastId;
	private HashMap<String, String> menberMap = new HashMap<>();

	private FrameLayout broadcasterLayout, localAudienceFrameView;
	private TextView broadcastNameText, broadcastTipsText, countText;
	private Button requestTalkButton, stopTalkButton, disCussButton, fullScreenButton;
	private TextView exitButton, pageText;

	private ImageView docImage;

	private boolean isDocShow = false;

	private Material currentMaterial;
	private MeetingMaterialsPublish currentMaterialPublish;
	private int doc_index = 0;

	private String channelName;

	private String audienceName;

	private Subscription subscription;
	private SurfaceView remoteBroadcasterSurfaceView, remoteAudienceSurfaceView, localSurfaceView;

	private AgoraAPIOnlySignal agoraAPI;
	private static final String DOC_INFO = "doc_info";
	private static final String CALLING_AUDIENCE = "calling_audience";
	private int count = 0;//当意外退出时  再重新进入会议 会重复收到几条一样的消息 导致视频画面可能不可见 使用次数来判断
	private int currentAudienceId;
	NewInMeetingChatFragment newInMeetingChatFragment;
	boolean hideFragment = false;
	boolean JoinSuc = false;
	private MyGridLayoutHelper mGridLayoutHelper;
	private VirtualLayoutManager mVirtualLayoutManager;
	private DelegateAdapter mDelegateAdapter;
	private RecyclerView mAudienceRecyclerView;
	private RecyclerView rcyChat;
	private NewAudienceVideoAdapter mVideoAdapter;
	private AudienceVideo mCurrentAudienceVideo;
	private SurfaceView mAudienceVideoSurfaceView;
	private AudienceVideo mLocalAudienceVideo;
	private Button mMuteAudio;
	private Button bt_beautiful;
	private boolean isMuted, isSplitView;
	private TextView mSwtichCamera;
	private SizeUtils mSizeUtils;
	private TransformersTip mTransformersTipPop;
	private int lastX, lastY;
	private PreviewPlayer player;
	int touchCount = 0;
	private SpaceItemDecoration mDecor;
	private String mMaterialId;

	private AlertDialog mConnectionLostDialog;
	private AlertDialog mSignalDialog;
	private TextView mNetWorkNumber;
	private TextView mNetworkIcon;
	private boolean mIsMaker;
	private TipDialog mTipDialog;


	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meeting_audience);
		JPushInterface.stopPush(getApplicationContext());
//        if (!WSService.isOnline()) {
//            //当前状态离线,可切换在线
//            ZYAgent.onEvent(this, "在线按钮,当前离线,切换到在线");
//            Log.i(TAG, "当前状态离线,可切换在线");
//            RxBus.sendMessage(new SetUserChatEvent(true));
//        } else {
//            ZYAgent.onEvent(this, "在线按钮,当前在线,,无效操作");
//        }

		registerReceiver(homeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		mSizeUtils = new SizeUtils(this);
		mDecor = new SpaceItemDecoration(10, 10, 0, 0);
		mGridSpaceItemDecoration = new GridSpaceItemDecoration(DisplayUtil.getHeight(this) / 4);
		subscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {

			/*	if (o instanceof IMMessgeEvent) {
					IMMessgeEvent msg = (IMMessgeEvent) o;
					io.rong.imlib.model.Message imMessage = msg.getImMessage();
//					Logger.e(JSON.toJSONString(imMessage));
					if (imMessage.getContent() instanceof GroupNotificationMessage) {
						return;
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (!hideFragment) {
								FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) llChat.getLayoutParams();
								int[] location = DisplayUtil.getLocation(disCussButton);

								layoutParams.setMargins(location[0] + disCussButton.getWidth() / 4, 0, 0, disCussButton.getHeight());
								llChat.setLayoutParams(layoutParams);
								llChat.setVisibility(View.VISIBLE);
								try {
									tvChatName.setText(imMessage.getContent().getUserInfo().getName() + ":");
								} catch (Exception e) {
									e.printStackTrace();
								}


								if (imMessage.getContent() instanceof TextMessage) {
									TextMessage msg = (TextMessage) imMessage.getContent();
									String content = msg.getContent();
									if (content.length() >= 8) {
										content = content.substring(0, 8) + "...";
									}
									tvChat.setText(content);
								} else if (imMessage.getContent() instanceof ImageMessage) {
									ImageMessage msg = (ImageMessage) imMessage.getContent();
									tvChat.setText("发送了一张图片");
								} else if (imMessage.getContent() instanceof LocationMessage) {
									LocationMessage msg = (LocationMessage) imMessage.getContent();
									tvChat.setText("发送了一个位置");
								} else if (imMessage.getContent() instanceof FileMessage) {
									LocationMessage msg = (LocationMessage) imMessage.getContent();
									tvChat.setText("发送了一个文件");
								}
								showOperatorHandler.sendEmptyMessageDelayed(4, 3 * 1000);
							} else {
								llChat.setVisibility(View.GONE);
							}
						}
					});
				}*/

			}
		});
		DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;

		// 白板控制
		/*getSupportFragmentManager().beginTransaction()
				.remove(whiteboardFragment)
				.commitNow();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_whiteboard, whiteboardFragment)
				.show(whiteboardFragment)
				.commit();

		getRoomToken("0df1f93099ba11eab1a7f931cdbc18a9");
		 */

		localAudienceFrameView = findViewById(R.id.localAudienceFrameView);
		View mRlayout = findViewById(R.id.parentContainer);

		/**
		 * 自己连麦向窗口 可拖动
		 * */
		localAudienceFrameView.setOnTouchListener((v, event) -> {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (touchCount != 0) {
						lastX = (int) event.getRawX();
						lastY = (int) event.getRawY();
					}
					touchCount++;
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;

//						logAction("dx="+dx+"   event.getRawX()"+event.getRawX()+"   lastX="+lastX);
//						logAction("dy="+dy+"   event.getRawY()"+event.getRawY()+"   lastY="+lastY);
					if (v == null) {
						return false;
					}
					FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();

					int l = layoutParams.leftMargin + dx;
					int t = layoutParams.topMargin + dy;
					int b = mRlayout.getHeight() - t - v.getHeight();
					int r = mRlayout.getWidth() - l - v.getWidth();
					if (l < 0) {//处理按钮被移动到上下左右四个边缘时的情况，决定着按钮不会被移动到屏幕外边去
						l = 0;
						r = mRlayout.getWidth() - v.getWidth();
					}
					if (t < 0) {
						t = 0;
						b = mRlayout.getHeight() - v.getHeight();
					}

					if (r < 0) {
						r = 0;
						l = mRlayout.getWidth() - v.getWidth();
					}
					if (b < 0) {
						b = 0;
						t = mRlayout.getHeight() - v.getHeight();
					}
					layoutParams.leftMargin = l;
					layoutParams.topMargin = t;
					layoutParams.bottomMargin = b;
					layoutParams.rightMargin = r;
//						logAction("left="+l+"   top="+t+"    right="+r+"   bottom="+b);
					v.setLayoutParams(layoutParams);

					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					v.postInvalidate();
					break;
				case MotionEvent.ACTION_UP:
					break;
			}
			return true;
		});

		mAudienceRecyclerView = findViewById(R.id.audience_list);

		mGridLayoutHelper = new MyGridLayoutHelper(2);
		mGridLayoutHelper.setGap(10);
		mGridLayoutHelper.setItemCount(8);
		mGridLayoutHelper.setAutoExpand(false);

		//将参会人列表设置成固定大小
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 240), RelativeLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		layoutParams.setMargins(0, DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 10), DisplayUtil.dip2px(this, 60));
		mAudienceRecyclerView.setLayoutParams(layoutParams);

		RelativeLayout relative = findViewById(R.id.relative);

		FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) relative.getLayoutParams();
		layoutParams1.setMargins(0, 0, 0, 0);
		relative.setLayoutParams(layoutParams1);


		mVirtualLayoutManager = new VirtualLayoutManager(this);
		mDelegateAdapter = new DelegateAdapter(mVirtualLayoutManager);
		RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
		mAudienceRecyclerView.setRecycledViewPool(viewPool);
		viewPool.setMaxRecycledViews(0, 8);
		mAudienceRecyclerView.setLayoutManager(mVirtualLayoutManager);


		mAudienceRecyclerView.removeItemDecoration(mDecor);
		mAudienceRecyclerView.addItemDecoration(mDecor);
		mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);

		mVideoAdapter = new NewAudienceVideoAdapter(this, mGridLayoutHelper);
		mDelegateAdapter.addAdapter(mVideoAdapter);
		mAudienceRecyclerView.setAdapter(mDelegateAdapter);

		//设置参会人列表的点击事件
		mVideoAdapter.setOnItemClickListener(new NewAudienceVideoAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(RecyclerView parent, View view, int position) {

				try {
					if (mVideoAdapter.getAudienceVideoLists().size() <= position) {
						return;
					}
					if (isSplitView || isDocShow) {
						return;
					}
					if (mVideoAdapter.isHaveChairMan()) {

						if (isHostCommeIn) {
							broadcastTipsText.setVisibility(View.GONE);
						} else {
							broadcastTipsText.setVisibility(View.VISIBLE);
							count = 0;
						}
						//点击的如果是主持人
						if (mVideoAdapter.getAudienceVideoLists().get(position).isBroadcaster()) {
							if (mCurrentAudienceVideo != null) {
								mVideoAdapter.removeItem(position);
								mVideoAdapter.insertItem(position, mCurrentAudienceVideo);
								broadcasterLayout.removeAllViews();
								stripSurfaceView(remoteAudienceSurfaceView);
								stripSurfaceView(remoteBroadcasterSurfaceView);
								if (remoteBroadcasterSurfaceView != null) {
									broadcasterLayout.addView(remoteBroadcasterSurfaceView);
								}
							}
							mCurrentAudienceVideo = null;
							return;
						} else {
							//如果点击的不是主持人 先将大的画面broadcasterView   添加到列表中
							// 然后再将点击的画面添加到大的broadcasterView中 主持人的画面再添加到列表中去
							if (mCurrentAudienceVideo != null) {
								AudienceVideo audienceVideo = new AudienceVideo();
								audienceVideo.setUid(mCurrentAudienceVideo.getUid());
								audienceVideo.setName(mCurrentAudienceVideo.getName());
								audienceVideo.setBroadcaster(false);
								stripSurfaceView(mAudienceVideoSurfaceView);
								audienceVideo.setSurfaceView(mAudienceVideoSurfaceView);
								int i = (int) mAudienceVideoSurfaceView.getTag();
								mVideoAdapter.removeItem(i);
								mVideoAdapter.insertItem(i, audienceVideo);

							}

						}
					}
					broadcastTipsText.setVisibility(View.GONE);
					//将参会人的画面移到主持人界面
					broadcasterLayout.removeAllViews();
					mCurrentAudienceVideo = mVideoAdapter.getAudienceVideoLists().get(position);
					mAudienceVideoSurfaceView = mCurrentAudienceVideo.getSurfaceView();
					if (mAudienceVideoSurfaceView == null) {
						return;
					}
					mAudienceVideoSurfaceView.setTag(position);

					mVideoAdapter.removeItem(position);
					stripSurfaceView(mAudienceVideoSurfaceView);
					broadcasterLayout.addView(mAudienceVideoSurfaceView);


					stripSurfaceView(remoteBroadcasterSurfaceView);
					//主持人画面 加入到列表中
					AudienceVideo audienceVideo = new AudienceVideo();
					audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
					audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
					audienceVideo.setBroadcaster(true);
					audienceVideo.setSurfaceView(remoteBroadcasterSurfaceView);
					mVideoAdapter.insetChairMan(position, audienceVideo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		showOperatorHandler.sendEmptyMessageDelayed(0, 5000);

	}

	/**
	 * 移除SurfaceView的parent
	 * */
	private void stripSurfaceView(SurfaceView view) {
		if (view == null) {
			logAction("view==null");
			return;
		}
		ViewParent parent = view.getParent();
		if (parent != null) {
			((FrameLayout) parent).removeView(view);
		}
	}


	/**
	 * 设置view的drawableTop属性
	 * */
	private void setTextViewDrawableTop(TextView view, int drawable) {
		Drawable top = getResources().getDrawable(drawable);
		view.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	private boolean handsUp = false;
	private boolean isFullScreen = false;
	private BeautyOptions beautyOptions;
	private boolean isOpenBeau = false;

	@Override
	protected void initUIandEvent() {
		//添加声网监听回调
		event().addEventHandler(this);

		Intent intent = getIntent();
		agora = intent.getParcelableExtra("agora");
		meetingJoin = intent.getParcelableExtra("meeting");
		mIsMaker = intent.getBooleanExtra("isMaker", false);

		disCussButton = findViewById(R.id.discuss);
		floatView = View.inflate(this, R.layout.float_view, null);
		DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
		if (meetingJoin == null || meetingJoin.getHostUser() == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				if (this.isDestroyed()) {
					return;
				}
			}
			if (this.isFinishing()) {
				return;

			}
			NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);

			dialogBuilder
					.withTitle("发生了意外")
					.withMessage("没有获取到主持人信息 请重新进入教室")
					.withButton1Text("退出")
					.isCancelableOnTouchOutside(false)
					.setButton1Click(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialogBuilder.dismiss();
							finish();
						}
					})
					.show();
			return;
		}
		meeting = meetingJoin.getMeeting();
		mNetWorkNumber = findViewById(R.id.netWorkNumber);
		mNetworkIcon = findViewById(R.id.networkIcon);

		broadcastId = meetingJoin.getHostUser().getClientUid();

		config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));

		MMKV.defaultMMKV().encode(MMKVHelper.KEY_MEETING_UID, config().mUid);


		channelName = meetingJoin.getMeeting().getId();

		newInMeetingChatFragment = new NewInMeetingChatFragment();

		//容云相关代码 具体去看融云官方文档 isMaker 是否是创客页面
		Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
				.appendPath("conversation").appendPath("group")
				.appendQueryParameter("isMaker", mIsMaker ? "1" : "0")
				.appendQueryParameter("targetId", meetingJoin.getMeeting().getId()).build();
		newInMeetingChatFragment.setUri(uri);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		disCussButton.setVisibility(mIsMaker ? View.GONE : View.VISIBLE);
		findViewById(R.id.makerChatContent).setVisibility(mIsMaker ? View.VISIBLE : View.GONE);
		findViewById(R.id.closeChat).setVisibility(mIsMaker ? View.VISIBLE : View.GONE);
		LinearLayout toolsBar = findViewById(R.id.toolsbar);

		if (mIsMaker) {
			toolsBar.setGravity(Gravity.RIGHT);
			fragmentTransaction.add(R.id.makerChatContent, newInMeetingChatFragment);
			fragmentTransaction.show(newInMeetingChatFragment);
		} else {
			toolsBar.setGravity(Gravity.CENTER);
			fragmentTransaction.add(R.id.rl_content, newInMeetingChatFragment);
			fragmentTransaction.hide(newInMeetingChatFragment);
		}
		//隐藏聊天fragment按钮的点击事件
		findViewById(R.id.closeChat).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View makerChatContent = findViewById(R.id.makerChatContent);
				View llyChatInput = findViewById(R.id.llyChatInput);
				if (makerChatContent.getVisibility() == View.INVISIBLE) {
					makerChatContent.setVisibility(View.VISIBLE);
					llyChatInput.setVisibility(View.VISIBLE);
					if (newInMeetingChatFragment != null) {
						newInMeetingChatFragment.setListViewScrollToBottom();
					}
				} else {
					makerChatContent.setVisibility(View.INVISIBLE);
					llyChatInput.setVisibility(View.INVISIBLE);
				}
			}
		});
		//新增聊天发送逻辑
		EditText editChatMsg = findViewById(R.id.editChatMsg);
		editChatMsg.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				View btnChatSend = findViewById(R.id.btnChatSend);
				if(s!=null && s.length() > 0){
					btnChatSend.setVisibility(View.VISIBLE);
				}else{
					btnChatSend.setVisibility(View.GONE);
				}
			}
		});
		editChatMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
					sendChatMsg();
				}
				return false;//返回true，保留软键盘。false，隐藏软键盘
			}
		});
		View btnChatSend = findViewById(R.id.btnChatSend);
		btnChatSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendChatMsg();
			}
		});
		//默认隐藏
		btnChatSend.setVisibility(View.GONE);

		fragmentTransaction.commitAllowingStateLoss();

		audienceName = Preferences.getUserName();

		broadcasterLayout = findViewById(R.id.broadcaster_view);
		broadcasterLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mIsMaker) {
					hideFragment();
					if (isFullScreen) {


					}
				}

			}
		});
		broadcastTipsText = findViewById(R.id.broadcast_tips);
		broadcastNameText = findViewById(R.id.broadcaster);
		Logger.e("meetingJoin4444-->" + JSON.toJSONString(meetingJoin));
		Logger.e("meetingJoin5555-->" + meetingJoin.getHostUser().getHostUserName());
		broadcastNameText.setText("主持人：" + meetingJoin.getHostUser().getHostUserName());
		docImage = findViewById(R.id.doc_image);
		docImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mIsMaker) {
					hideFragment();
					if (isFullScreen) {
					}

				}

			}
		});
		pageText = findViewById(R.id.page);


		disCussButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				initFragment();
			}
		});


		fullScreenButton = findViewById(R.id.full_screen);
		//全屏按钮点击事件 当前是分屏模式  或者没有显示PPT的时候 是不能切换全屏的
		fullScreenButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isDocShow) {

					if (isSplitView && !isDocShow) {
						showToastyWarn("当前模式不能全屏切换");
						return;
					}

					if (isFullScreen) {
						isFullScreen = false;
						fullScreenButton.setText("全屏");
						findViewById(R.id.toolsbar).setVisibility(View.VISIBLE);
						if (isDocShow) {

						} else {
							mVideoAdapter.setVisibility(View.VISIBLE);
							mAudienceRecyclerView.setVisibility(View.VISIBLE);
						}


					} else {
						isFullScreen = true;
						fullScreenButton.setText("恢复");
						findViewById(R.id.toolsbar).setVisibility(View.GONE);
						mVideoAdapter.setVisibility(View.GONE);
						mAudienceRecyclerView.setVisibility(View.GONE);

					}
				} else {
					//三种模式 第一非全屏 默认的 第二 全屏 画面背景为PPT内容，右下角悬浮自己的画面 悬浮画面可以拖动 第三 全屏 只显示主持人画面 或者ppt画面
					mTransformersTipPop = new TransformersTip(v, R.layout.pop_full_screen_choose) {
						@Override
						protected void initView(View contentView) {
							contentView.findViewById(R.id.not_full_screen).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									mTransformersTipPop.dismissTip();

									notFullScreenState();

								}
							});
							contentView.findViewById(R.id.full_screen).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									mTransformersTipPop.dismissTip();
									FullScreenState();
								}
							});

							contentView.findViewById(R.id.clearAll).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									mTransformersTipPop.dismissTip();
									clearAllState();
								}
							});
						}
					}
							.setTipGravity(TipGravity.TO_TOP_CENTER) // 设置浮窗相对于锚点控件展示的位置
							/*.setTipOffsetXDp(0) // 设置浮窗在 x 轴的偏移量
							.setTipOffsetYDp(-6) // 设置浮窗在 y 轴的偏移量*/

							.setBackgroundDimEnabled(false) // 设置是否允许浮窗的背景变暗
							.setDismissOnTouchOutside(true);
					if (!mTransformersTipPop.isShowing()) {
						mTransformersTipPop // 设置点击浮窗外部时是否自动关闭浮窗
								.show(); // 显示浮窗
					}
				}

			}
		});


		countText = findViewById(R.id.online_count);

		/**
		 * 1会议 2直播
		 * */

		stopTalkButton = findViewById(R.id.stop_talk);
		stopTalkButton.setOnClickListener(view -> {
			showDialog(2, "确定终止发言吗？", "取消", "确定", null);
		});

		requestTalkButton = findViewById(R.id.request_talk);
		/**
		 * 申请发言的按钮
		 * */
		requestTalkButton.setOnClickListener(view -> {
			if (remoteBroadcasterSurfaceView != null) {
				if (handsUp) {
					handsUp = false;
					requestTalkButton.setText("申请发言");
				} else {
					handsUp = true;
					requestTalkButton.setText("取消申请");
				}
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("uid", config().mUid);
					jsonObject.put("uname", audienceName);
					jsonObject.put("name", audienceName);
					jsonObject.put("handsUp", handsUp);
					jsonObject.put("callStatus", 0);
					jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
					jsonObject.put("postTypeName", Preferences.getUserPostType());
					if (Constant.videoType == 1) {
						jsonObject.put("isAudience", false);
					} else {
						jsonObject.put("isAudience", true);
					}

					agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(MeetingAudienceActivity.this, "主持人加入才能申请发言", Toast.LENGTH_SHORT).show();
			}
		});

		exitButton = findViewById(R.id.exit);
		mMuteAudio = findViewById(R.id.mute_audio);
		bt_beautiful = findViewById(R.id.bt_beautiful);

		mSwtichCamera = findViewById(R.id.switch_camera);
		isOpenBeau = MMKV.defaultMMKV().decodeBool(MMKVHelper.BEAUTIFULL,false);
		bt_beautiful.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isOpenBeau) {
					setTextViewDrawableTop(bt_beautiful, R.mipmap.close_betuful);
					MMKVHelper.getInstance().saveBeaultiful(false);
					worker().getRtcEngine().setBeautyEffectOptions(false,beautyOptions);
				} else {
					setTextViewDrawableTop(bt_beautiful, R.mipmap.open_beautiful);
					MMKVHelper.getInstance().saveBeaultiful(true);
					worker().getRtcEngine().setBeautyEffectOptions(true,beautyOptions);
				}
				isOpenBeau = !isOpenBeau;
			}
		});

		if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
			rtcEngine().enableLocalVideo(false);
			setTextViewDrawableTop(mSwtichCamera, R.drawable.icon_close_video);
			mSwtichCamera.setText("打开摄像");
		} else {
			rtcEngine().enableLocalVideo(true);
			mSwtichCamera.setText("切换摄像");
		}

		mSwtichCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
					rtcEngine().enableLocalVideo(true);
					MMKV.defaultMMKV().encode(MMKVHelper.CAMERA_STATE, true);
				} else {
					rtcEngine().switchCamera();
				}
				mSwtichCamera.setText("切换摄像");
			}
		});

		if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
			isMuted = true;
			mMuteAudio.setText("话筒关闭");
			setTextViewDrawableTop(mMuteAudio, R.drawable.icon_unspeek);
			rtcEngine().enableLocalAudio(false);
		} else {
			isMuted = false;
			rtcEngine().enableLocalAudio(true);
			setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek);
			mMuteAudio.setText("话筒打开");
		}


		mMuteAudio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
					rtcEngine().enableLocalAudio(true);
					isMuted = false;
					rtcEngine().enableLocalAudio(true);
					setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek);
					mMuteAudio.setText("话筒打开");
					MMKV.defaultMMKV().encode(MMKVHelper.MICROPHONE_STATE, true);
					return;
				}

				if (!isMuted) {
					isMuted = true;
					setTextViewDrawableTop(mMuteAudio, R.drawable.icon_unspeek);
					mMuteAudio.setText("话筒关闭");
				} else {
					isMuted = false;
					setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek);
					mMuteAudio.setText("话筒打开");
				}
				rtcEngine().muteLocalAudioStream(isMuted);
			}
		});

		exitButton.setOnClickListener(view -> {
			showDialog(1, "确定退出教室吗？", "取消", "确定", null);
		});


		agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());
		/**
		 * 设置自己的昵称 用于参会人列表进行展示和参会人窗口姓名展示
		 * */
		agoraAPI.setAttr("userName", Preferences.getUserName());
		agoraAPI.setAttr("uname", Preferences.getUserName());
		/**
		 * 声网信令相关回调 具体查看声网
		 * */
		agoraAPI.callbackSet(new AgoraAPI.CallBack() {

			@Override
			public void onLoginSuccess(int uid, int fd) {
				super.onLoginSuccess(uid, fd);
				logAction("onLoginSuccess   uid:" + uid + " --- " + "fd=" + fd);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "观众登录信令系统成功", Toast.LENGTH_SHORT).show());
				}


				agoraAPI.channelJoin(channelName);

				runOnUiThread(()->{
					beautyOptions = new BeautyOptions(LIGHTENING_CONTRAST_NORMAL,0.7f,0.5f,0.1f);
					if (MMKV.defaultMMKV().decodeBool(MMKVHelper.BEAUTIFULL,false)) {
						worker().getRtcEngine().setBeautyEffectOptions(true,beautyOptions);
						setTextViewDrawableTop(bt_beautiful, R.mipmap.open_beautiful);
					} else {
						worker().getRtcEngine().setBeautyEffectOptions(false,beautyOptions);
						setTextViewDrawableTop(bt_beautiful, R.mipmap.close_betuful);
					}
				});
			}

			@Override
			public void onLoginFailed(final int ecode) {
				super.onLoginFailed(ecode);
				logAction("onLoginFailed   ecode:" + ecode);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "观众登录信令系统失败" + ecode, Toast.LENGTH_SHORT).show());
				}
				//登录失败 继续登录
				if ("true".equals(agora.getIsTest())) {
					agoraAPI.login2(agora.getAppID(), "" + config().mUid, "noneed_token", 0, "", 20, 30);
				} else {
					agoraAPI.login2(agora.getAppID(), "" + config().mUid, agora.getSignalingKey(), 0, "", 20, 30);
				}

			}

			@Override
			public void onLogout(int ecode) {
				super.onLogout(ecode);
				logAction("onLogout  " + ecode);
				Logger.e("onLogout   ecode:" + ecode);
				try {
					if (ecode == 102) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (mSignalDialog == null) {
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
										if (MeetingAudienceActivity.this.isDestroyed()) {
											return;
										}
									}
									if (MeetingAudienceActivity.this.isFinishing()) {
										return;
									}
									mSignalDialog = new AlertDialog(MeetingAudienceActivity.this)
											.builder()
											.setTitle("登陆系统失败")
											.setMsg("系统登陆失败 请重新进入教室")
											.setCancelable(false)
											.setCanceledOnTouchOutside(false)
											.setPositiveButton("退出", new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													mSignalDialog.dismiss();
													doLeaveChannel();
													finish();
												}
											});
									mSignalDialog.show();
								} else {
									if (!mSignalDialog.isShowing()) {
										mSignalDialog.show();
									}
								}

							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "观众登出信令系统成功" + ecode, Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onChannelJoined(String channelID) {
				super.onChannelJoined(channelID);
				logAction("onChannelJoined   channelID:" + channelID);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(MeetingAudienceActivity.this, "观众登录信令频道成功", Toast.LENGTH_SHORT).show();
					}
					//加入频道成功 设置昵称 查询人数
					agoraAPI.setAttr("uname", Preferences.getUserName());
					agoraAPI.setAttr("userName", Preferences.getUserName());
					agoraAPI.queryUserStatus(broadcastId);
					agoraAPI.channelQueryUserNum(channelName);
				});
			}

			@Override
			public void onReconnecting(int nretry) {
				super.onReconnecting(nretry);
				logAction("信令重连失败第" + nretry + "次");
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "信令重连失败第" + nretry + "次", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onReconnected(int fd) {
				super.onReconnected(fd);
				logAction("信令系统重连成功");
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "信令系统重连成功", Toast.LENGTH_SHORT).show());
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						agoraAPI.channelJoin(channelName);
					}
				});

			}

			@Override
			public void onQueryUserStatusResult(String name, String status) {
				super.onQueryUserStatusResult(name, status);
				if (name.equals(meetingJoin.getHostUser().getClientUid()) && "1".equals(status)) {
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("uid", config().mUid);
						jsonObject.put("uname", audienceName);
						jsonObject.put("name", audienceName);
						if (Constant.videoType == 1) {
							jsonObject.put("callStatus", 2);
							jsonObject.put("isAudience", false);
						} else {
							if (currentAudienceId == config().mUid) {
								jsonObject.put("callStatus", 2);
								jsonObject.put("isAudience", true);
							} else {
								jsonObject.put("callStatus", 0);
								jsonObject.put("isAudience", true);
							}

						}

						jsonObject.put("handsUp", handsUp);
						jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
						jsonObject.put("postTypeName", Preferences.getUserPostType());
						agoraAPI.messageInstantSend(name, 0, jsonObject.toString(), "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onChannelJoinFailed(String channelID, int ecode) {
				super.onChannelJoinFailed(channelID, ecode);
				runOnUiThread(() -> {
					logAction("观众登录信令频道失败  channelID:" + channelID + "   ecode:" + ecode);
					if (BuildConfig.DEBUG) {
						Toast.makeText(MeetingAudienceActivity.this, "观众登录信令频道失败", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@SuppressLint("SetTextI18n")
			@Override
			public void onChannelQueryUserNumResult(String channelID, int ecode, final int num) {
				super.onChannelQueryUserNumResult(channelID, ecode, num);
//				runOnUiThread(() -> {
//					if (meeting.getIsMeeting() == 1) {//1 是会议 2 是直播
//						countText.setText("在线人数：" + num);
//					} else if (meeting.getIsMeeting() == 2){
//						//addNum
//						verifyIsInGroup(num);
//					} else {
//						countText.setText("在线人数：" + num);
//					}
//				});
				runOnUiThread(() -> {
					verifyIsInGroup(num);
				});
			}

			@Override
			public void onChannelUserJoined(String account, int uid) {
				super.onChannelUserJoined(account, uid);
				runOnUiThread(() -> {
					logAction("onChannelUserJoined  " + "观众" + account + "加入房间了---" + meetingJoin.getHostUser().getClientUid());
					agoraAPI.channelQueryUserNum(channelName);
					agoraAPI.getUserAttr(account, "userName");//会走回调onUserAttrResult
					agoraAPI.getUserAttr(account, "uname");//会走回调onUserAttrResult
				});
			}


			//getUserAttr(String account,String name)的回调
			@Override
			public void onUserAttrResult(final String account, final String name, final String value) {
				super.onUserAttrResult(account, name, value);
				logAction("获取到用户" + account + "的属性" + name + "的值为" + value);
				runOnUiThread(() -> {
					synchronized (this) {
						if (BuildConfig.DEBUG) {
							Toast.makeText(MeetingAudienceActivity.this, "获取到用户" + account + "的属性" + name + "的值为" + value, Toast.LENGTH_SHORT).show();
						}

						try {
							if (name.equals("userName") || name.equals("uname")) {
								if (TextUtils.isEmpty(value)) {
									return;
								}
								int position = mVideoAdapter.getPositionById(Integer.parseInt(account));
								if (position != -1) {
									AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(position);
									if (audienceVideo != null) {
										audienceVideo.setName(value);
										audienceVideo.setUname(value);
										mVideoAdapter.notifyDataSetChanged();
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}


				});
			}


			@Override
			public void onChannelUserLeaved(String account, int uid) {
				super.onChannelUserLeaved(account, uid);

				logAction("用户account:" + account + "---" + "uid:" + uid + "退出信令频道");
				runOnUiThread(() -> {
					try {
						if (BuildConfig.DEBUG) {
							Toast.makeText(MeetingAudienceActivity.this, "用户" + account + "退出信令频道", Toast.LENGTH_SHORT).show();
						}
					/*	if (account.equals(broadcastId)) {
							isDocShow = false;
							isHostCommeIn = false;
							broadcasterLayout.removeView(remoteBroadcasterSurfaceView);
							broadcasterLayout.setVisibility(View.VISIBLE);

							docImage.setVisibility(View.GONE);
//							onUserOffline(Integer.parseInt(account), Constants.USER_OFFLINE_DROPPED);

						}*/
						agoraAPI.channelQueryUserNum(channelName);

						if (mVideoAdapter.getDataSize() <= 0) {
							fullScreenButton.setVisibility(View.GONE);
						} else {
							fullScreenButton.setVisibility(View.VISIBLE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				});
			}


			@Override
			public void onMessageSendSuccess(String messageID) {
				super.onMessageSendSuccess(messageID);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> {
						Toast.makeText(MeetingAudienceActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
					});
				}
			}

			@Override
			public void onMessageSendError(String messageID, int ecode) {
				super.onMessageSendError(messageID, ecode);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> {
						Toast.makeText(MeetingAudienceActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
					});
				}
			}

			/**
			 * 收到点对点的消息回调
			 * */
			@Override
			public void onMessageInstantReceive(final String account, final int uid, final String msg) {
				super.onMessageInstantReceive(account, uid, msg);
				Logger.e(msg);
				logAction("account:" + account + "uid:" + uid + "msg:" + msg);
				runOnUiThread(() -> {
					try {
						if (BuildConfig.DEBUG) {
							Toast.makeText(MeetingAudienceActivity.this, "接收到消息" + msg, Toast.LENGTH_SHORT).show();
						}
						JSONObject jsonObject = new JSONObject(msg);
						Logger.e(jsonObject.toString());

						if (jsonObject.has("response")) {
							boolean result = jsonObject.getBoolean("response");
							if (result) { // 连麦成功
								if (BuildConfig.DEBUG) {
									Toast.makeText(MeetingAudienceActivity.this, "主持人要和我连麦", Toast.LENGTH_SHORT).show();
								}

								mMuteAudio.setVisibility(View.VISIBLE);
								mSwtichCamera.setVisibility(View.VISIBLE);


								remoteAudienceSurfaceView = null;

//								agoraAPI.setAttr("uname", audienceName); // 设置当前登录用户的相关属性值。
								agoraAPI.setAttr("uname", Preferences.getUserName());
								agoraAPI.setAttr("userName", Preferences.getUserName());
								localSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
								localSurfaceView.setZOrderOnTop(true);
								localSurfaceView.setZOrderMediaOverlay(true);
								rtcEngine().setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));


								stopTalkButton.setVisibility(View.VISIBLE);
								requestTalkButton.setVisibility(View.GONE);

								worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

								HashMap<String, Object> params = new HashMap<String, Object>();
								params.put("status", 1);
								params.put("meetingId", meetingJoin.getMeeting().getId());
								ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
							} else {
								if (BuildConfig.DEBUG) {
									Toast.makeText(MeetingAudienceActivity.this, "主持人拒绝和我连麦", Toast.LENGTH_SHORT).show();
								}
								stopTalkButton.setVisibility(View.GONE);
								requestTalkButton.setVisibility(View.VISIBLE);
								if (Constant.videoType == 2) {
									mMuteAudio.setVisibility(View.GONE);
									mSwtichCamera.setVisibility(View.GONE);
								} else if (Constant.videoType == 1) {
									mMuteAudio.setVisibility(View.VISIBLE);
									mSwtichCamera.setVisibility(View.VISIBLE);
								}
							}
							handsUp = false;
							requestTalkButton.setText("申请发言");
						}
						if (jsonObject.has("finish")) {
							boolean finish = jsonObject.getBoolean("finish");
							if (finish) {
								logAction("当前观众的ID是:" + currentAudienceId);
								//account:==242007174,uid:==0,msg:=={"finish":true}
								//此时观众在列表中
								/*观众此时在列表中*/

								if (!isHostCommeIn) {
									//主持人离开了
									if (mVideoAdapter.isHaveChairMan()) {
										int chairManPosition = mVideoAdapter.getChairManPosition();
										if (chairManPosition != -1) {
											mVideoAdapter.removeItem(chairManPosition);
										}
									} else {
										if (currentAudienceId != 0) {
											mVideoAdapter.deleteItemById(currentAudienceId);
										}

									}
									broadcasterLayout.removeAllViews();
									broadcasterLayout.setVisibility(View.GONE);
									broadcastTipsText.setVisibility(View.VISIBLE);
									count = 0;

									if (mVideoAdapter.getDataSize() > 0) {
										mAudienceRecyclerView.setVisibility(View.VISIBLE);
										mVideoAdapter.setVisibility(View.VISIBLE);

									} else {
										mAudienceRecyclerView.setVisibility(View.GONE);
										mVideoAdapter.setVisibility(View.GONE);

									}
								}

								if (currentAudienceId == config().mUid) {
									if (mVideoAdapter.getPositionById(config().mUid) != -1) {
										mVideoAdapter.deleteItem(config().mUid);

									}
								}

								if (mVideoAdapter.getDataSize() >= 1) {
									fullScreenButton.setVisibility(View.VISIBLE);
								} else {
									fullScreenButton.setVisibility(View.GONE);
								}

//								agoraAPI.setAttr("uname", null);
								if (Constant.videoType == 2) {
									localSurfaceView = null;
								}


//								if (!isDocShow) {
//									fullScreenButton.setVisibility(View.GONE);
//								}


								if (Constant.videoType == 2) {
									mMuteAudio.setVisibility(View.GONE);
									mSwtichCamera.setVisibility(View.GONE);
									requestTalkButton.setVisibility(View.VISIBLE);
									stopTalkButton.setVisibility(View.GONE);
									worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
								} else if (Constant.videoType == 1) {
									mMuteAudio.setVisibility(View.VISIBLE);
									mSwtichCamera.setVisibility(View.VISIBLE);
									worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
								}

								handsUp = false;
								requestTalkButton.setText("申请发言");

								if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
									HashMap<String, Object> params = new HashMap<String, Object>();
									params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
									params.put("status", 2);
									params.put("meetingId", meetingJoin.getMeeting().getId());
									params.put("type", 2);
									params.put("leaveType", 1);
									ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
								}

							}
						}

						/*if (jsonObject.has("getInformation")) {
							JSONObject json = new JSONObject();
							String audienceName = (TextUtils.isEmpty(Preferences.getAreaName()) ? "" : Preferences.getAreaName()) + "-" + (TextUtils.isEmpty(Preferences.getUserCustom()) ? "" : Preferences.getUserCustom()) + "-" + Preferences.getUserName();
							json.put("uid", config().mUid);
							json.put("uname", audienceName);
							json.put("callStatus", 2);
							json.put("returnInformation", 1);
							json.put("auditStatus", Preferences.getUserAuditStatus());
							json.put("postTypeName", Preferences.getUserPostType());
							agoraAPI.messageInstantSend(meetingJoin.getHostUser().getClientUid(), 0, json.toString(), "");
						}*/

					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}

			/**
			 * 收到频道消息的回调
			 * */
			@Override
			public void onMessageChannelReceive(String channelID, String account, int uid, final String msg) {
				super.onMessageChannelReceive(channelID, account, uid, msg);
				logAction("channelID " + channelID + ", account:" + account + ", uid:" + uid + ", msg:" + msg);
				runOnUiThread(() -> {
					try {
						if (BuildConfig.DEBUG) {
							Toast.makeText(MeetingAudienceActivity.this, "接收到频道消息：" + msg, Toast.LENGTH_SHORT).show();
						}
						JSONObject jsonObject = new JSONObject(msg);
						Logger.e(jsonObject.toString());
						//ppt消息
						if (jsonObject.has("material_id") && jsonObject.has("doc_index")) {
							agoraAPI.channelQueryUserNum(channelName);
							doc_index = jsonObject.getInt("doc_index");
							mMaterialId = jsonObject.getString("material_id");
							if (model == 0 || model == 1) {
								notFullScreenState();
							} else if (model == 2) {
								FullScreenState();
							} else if (model == 3) {
								clearAllState();
							}
							if (isSplitView && !isDocShow) {
								fullScreenButton.setVisibility(View.GONE);
							} else {
								fullScreenButton.setVisibility(View.VISIBLE);
							}
							if (currentMaterial != null) {
								if (!mMaterialId.equals(currentMaterial.getId())) {
									ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, mMaterialId);
								} else {
									currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);
									if (remoteBroadcasterSurfaceView != null) {
										broadcasterLayout.removeView(remoteBroadcasterSurfaceView);
										broadcasterLayout.setVisibility(View.GONE);

									}

									if (currentMaterialPublish.getType().equals("1")) {
										PlayVideo();
									} else {
										stopPlayVideo();
										findViewById(R.id.app_video_box).setVisibility(View.GONE);
										docImage.setVisibility(View.VISIBLE);
										Glide.with(MeetingAudienceActivity.this).load(currentMaterialPublish.getUrl())
												.placeholder(docImage.getDrawable())
												.error(R.drawable.rc_image_error)
												.into(docImage);
									}
									pageText.setVisibility(View.VISIBLE);
									pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");


								}
							} else {
								ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, mMaterialId);
							}
						}
						if (jsonObject.has("finish_meeting")) {
							boolean finishMeeting = jsonObject.getBoolean("finish_meeting");
							if (finishMeeting) {
								if (BuildConfig.DEBUG) {
									Toast.makeText(MeetingAudienceActivity.this, "主持人结束了会议", Toast.LENGTH_SHORT).show();
								}
								doLeaveChannel();
								if (agoraAPI.getStatus() == 2) {
									agoraAPI.logout();
								}
								finish();
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}

			/**
			 * 频道属性发生变化的回调
			 * */
			@Override
			public void onChannelAttrUpdated(String channelID, String name, String value, String type) {
				super.onChannelAttrUpdated(channelID, name, value, type);
				logAction("channelID:" + channelID + ", name:" + name + ", value:" + value + ", type:" + type);
				runOnUiThread(() -> {
					if (CALLING_AUDIENCE.equals(name)) {
						if (TextUtils.isEmpty(value)) {//取消连麦
							if (remoteAudienceSurfaceView != null) {
								remoteAudienceSurfaceView = null;
							}

							if (localSurfaceView != null) {

								if (Constant.videoType == 2) {
									worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
									localSurfaceView = null;
								}

							}
							if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
								HashMap<String, Object> params = new HashMap<String, Object>();
								params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
								params.put("status", 2);
								params.put("meetingId", meetingJoin.getMeeting().getId());
								params.put("type", 2);
								params.put("leaveType", 1);
								ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
							}

							stopTalkButton.setVisibility(View.GONE);

							if (Constant.videoType == 2) {
								requestTalkButton.setVisibility(View.VISIBLE);
								mMuteAudio.setVisibility(View.GONE);
								mSwtichCamera.setVisibility(View.GONE);
								rtcEngine().muteLocalAudioStream(true);
							} else {
								requestTalkButton.setVisibility(View.GONE);
								mMuteAudio.setVisibility(View.VISIBLE);
								mSwtichCamera.setVisibility(View.VISIBLE);
							}
							setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek);
							mMuteAudio.setText("话筒打开");


							if (remoteBroadcasterSurfaceView == null) {
								mVideoAdapter.deleteItem(currentAudienceId);
								broadcastTipsText.setVisibility(View.VISIBLE);
								return;
							}
							if (currentAudienceId == config().mUid) {
								/*观众此时在列表中*/
								if (mVideoAdapter.getPositionById(config().mUid) != -1) {
									mVideoAdapter.deleteItem(config().mUid);
									stripSurfaceView(remoteBroadcasterSurfaceView);
									broadcasterLayout.removeAllViews();
									remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
									remoteBroadcasterSurfaceView.setZOrderOnTop(false);
									broadcasterLayout.addView(remoteBroadcasterSurfaceView);
									logAction("观众在列表中");
								} else {
									logAction("观众不  在列表中");
									/*观众不再列表中 此时主持人在列表中*/
									if (mVideoAdapter.isHaveChairMan()) {
										logAction("主持人在列表中");
										int chairManPosition = mVideoAdapter.getChairManPosition();
										if (chairManPosition != -1) {
											stripSurfaceView(remoteBroadcasterSurfaceView);
											remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
											remoteBroadcasterSurfaceView.setZOrderOnTop(false);
											broadcasterLayout.removeAllViews();
											broadcasterLayout.addView(remoteBroadcasterSurfaceView);
											mVideoAdapter.deleteItem(chairManPosition);
										}
									}
								}

								handsUp = false;
								requestTalkButton.setText("申请发言");
								currentAudienceId = 0;
								count = 0;//取消连麦 重置count
								if (isFullScreen || model == 2 || model == 3) {
									mVideoAdapter.setVisibility(View.GONE);
									mAudienceRecyclerView.setVisibility(View.GONE);
									localAudienceFrameView.removeAllViews();
									localAudienceFrameView.setVisibility(View.GONE);
								} else {
									mVideoAdapter.setVisibility(View.VISIBLE);
									mAudienceRecyclerView.setVisibility(View.VISIBLE);
								}


								//不管取消谁 需要判断当前集合大小 如果集合小于等于1 就不再分屏
								if (mVideoAdapter.getDataSize() <= 1) {
									exitSpliteMode();
								} else if (isSplitView && isDocShow) {
									SpliteViews(7);
								}

							}
						} else {
							if (BuildConfig.DEBUG) {
								Toast.makeText(MeetingAudienceActivity.this, "收到主持人设置的连麦人ID：" + value + ", \ntype:" + type, Toast.LENGTH_SHORT).show();
							}
							currentAudienceId = Integer.parseInt(value);
							if (currentAudienceId == config().mUid) { // 连麦人是我
								logAction("连麦人是我" + currentAudienceId);


								mMuteAudio.setVisibility(View.VISIBLE);
								mSwtichCamera.setVisibility(View.VISIBLE);


								JSONObject jsonObject = new JSONObject();
								try {
									jsonObject.put("uid", config().mUid);
									jsonObject.put("uname", audienceName);
									jsonObject.put("name", audienceName);
									jsonObject.put("handsUp", handsUp);
									if (Constant.videoType == 1) {
										jsonObject.put("callStatus", 2);
										jsonObject.put("isAudience", false);
									} else {
										if (currentAudienceId == config().mUid) {
											jsonObject.put("callStatus", 2);
											jsonObject.put("isAudience", true);
										} else {
											jsonObject.put("callStatus", 0);
											jsonObject.put("isAudience", true);
										}

									}
									jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
									jsonObject.put("postTypeName", Preferences.getUserPostType());
									agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
								} catch (JSONException e) {
									e.printStackTrace();
								}


								//全屏模式下 列表不可见  在ppt模式下 model==2或者3下 列表不可见

								if (isFullScreen || model == 2 || model == 3) {
									mVideoAdapter.setVisibility(View.GONE);
									mAudienceRecyclerView.setVisibility(View.GONE);
								} else {
									mVideoAdapter.setVisibility(View.VISIBLE);
									mAudienceRecyclerView.setVisibility(View.VISIBLE);
								}

								JSONObject json = new JSONObject();
								try {
									json.put("userName", Preferences.getUserName());
									json.put("uid", config().mUid);
									agoraAPI.channelSetAttr(channelName, Constant.USERNAME, json.toString());
								} catch (JSONException e) {
									e.printStackTrace();
								}


								logAction("count:=  " + count);
								/*if (count != 0) {//当意外退出后再进入会议 会收到重复的连麦消息 导致视频画面显示错误 当count不等于0 就不往下走
									return;
								}
								count++;*/


								if (Constant.videoType == 1) {

								} else if (Constant.videoType == 2) {
//									agoraAPI.setAttr("uname", audienceName); // 设置正在连麦的用户名
									agoraAPI.setAttr("uname", Preferences.getUserName());
									agoraAPI.setAttr("userName", Preferences.getUserName());
									remoteAudienceSurfaceView = null;
									localSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
									localSurfaceView.setZOrderOnTop(true);
									localSurfaceView.setZOrderMediaOverlay(true);
									rtcEngine().setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));

									mLocalAudienceVideo = new AudienceVideo();
									mLocalAudienceVideo.setUid(config().mUid);
									mLocalAudienceVideo.setName(audienceName);
									mLocalAudienceVideo.setBroadcaster(false);
									mLocalAudienceVideo.setSurfaceView(localSurfaceView);
									mVideoAdapter.insertItem(mLocalAudienceVideo);
									requestTalkButton.setVisibility(View.GONE);
									stopTalkButton.setVisibility(View.VISIBLE);
//									fullScreenButton.setVisibility(View.GONE);
									worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

									rtcEngine().muteLocalAudioStream(isMuted);

									HashMap<String, Object> params = new HashMap<String, Object>();
									params.put("status", 1);
									params.put("meetingId", meetingJoin.getMeeting().getId());
									ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);

									if (isSplitView && !isDocShow) {
										SpliteViews(6);
									}

									if (model == 2) {
										FullScreenState();
									}
								}


							} else {  // 连麦人不是我
								logAction("连麦人不是我！！" + currentAudienceId);
								count = 0;//连麦人不是我 重置count
								agoraAPI.getUserAttr(String.valueOf(currentAudienceId), "userName");
								agoraAPI.getUserAttr(String.valueOf(currentAudienceId), "uname");
								if (Constant.videoType == 2) {
									worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
									requestTalkButton.setVisibility(View.VISIBLE);
									requestTalkButton.setText("申请发言");
									stopTalkButton.setVisibility(View.GONE);
									//参会人（我）此时在列表中
									int positionById = mVideoAdapter.getPositionById(config().mUid);
									if (positionById != -1) {
										AudienceVideo video = mVideoAdapter.getAudienceVideoLists().get(positionById);
										if (video != null && video.getSurfaceView() != null) {
											video.getSurfaceView().setZOrderMediaOverlay(false);
											video.getSurfaceView().setZOrderOnTop(false);
										}
										mVideoAdapter.deleteItemById(config().mUid);
									}

								} else {
									worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
									requestTalkButton.setVisibility(View.GONE);
									stopTalkButton.setVisibility(View.GONE);
								}
//								agoraAPI.setAttr("uname", null);

								broadcasterLayout.removeAllViews();
								if (remoteBroadcasterSurfaceView != null) {
									stripSurfaceView(remoteBroadcasterSurfaceView);
									broadcasterLayout.addView(remoteBroadcasterSurfaceView);
								}


								//参会人（我）此时在大的视图里面 主持人在列表中
								if (mVideoAdapter.isHaveChairMan()) {
									int chairManPosition = mVideoAdapter.getChairManPosition();
									if (chairManPosition != -1) {
										try {
											if (chairManPosition < mVideoAdapter.getDataSize()) {
												AudienceVideo chairManVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
												if (chairManVideo != null && chairManVideo.getSurfaceView() != null) {
													chairManVideo.getSurfaceView().setZOrderMediaOverlay(false);
													chairManVideo.getSurfaceView().setZOrderOnTop(false);
													stripSurfaceView(chairManVideo.getSurfaceView());
												}
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										mVideoAdapter.removeItem(chairManPosition);
									}

								}


								if (mCurrentAudienceVideo != null) {
									mVideoAdapter.insertItem(mCurrentAudienceVideo);
									mCurrentAudienceVideo = null;
								}


								if (Constant.videoType == 2) {
									mMuteAudio.setVisibility(View.GONE);
									mSwtichCamera.setVisibility(View.GONE);
								} else if (Constant.videoType == 1) {
									mMuteAudio.setVisibility(View.VISIBLE);
									mSwtichCamera.setVisibility(View.VISIBLE);
								}

								if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
									HashMap<String, Object> params = new HashMap<String, Object>();
									params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
									params.put("status", 2);
									params.put("meetingId", meetingJoin.getMeeting().getId());
									params.put("type", 2);
									params.put("leaveType", 1);
									ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
								}
								/*if (localSurfaceView != null) {
									localSurfaceView = null;
								}*/

								//全屏模式下 列表不可见  在ppt模式下 model==2或者3下 列表不可见

								if (isFullScreen || model == 2 || model == 3) {
									mVideoAdapter.setVisibility(View.GONE);
									mAudienceRecyclerView.setVisibility(View.GONE);
									if (model == 2 && localSurfaceView != null) {
										FullScreenState();
									}
								} else {
									mVideoAdapter.setVisibility(View.VISIBLE);
									mAudienceRecyclerView.setVisibility(View.VISIBLE);
								}

							}
						}
					}
					if ("doc_info".equals(name)) {
						agoraAPI.channelQueryUserNum(channelName);
						if (!TextUtils.isEmpty(value)) {
							logAction("当前是ppt模式");
							isDocShow = true;
//							exitSpliteMode();
							try {
								JSONObject jsonObject = new JSONObject(value);
								if (jsonObject.has("material_id") && jsonObject.has("doc_index")) {

									doc_index = jsonObject.getInt("doc_index");
									if (BuildConfig.DEBUG) {
										Toast.makeText(MeetingAudienceActivity.this, "收到主持人端index：" + doc_index, Toast.LENGTH_SHORT).show();
									}
									mMaterialId = jsonObject.getString("material_id");
								/*	mAudienceRecyclerView.setVisibility(View.GONE);
									mVideoAdapter.setVisibility(View.GONE);*/
									if (model <= 0) {
										notFullScreenState();
									}
									if (isSplitView && !isDocShow) {
										fullScreenButton.setVisibility(View.GONE);
									} else {
										fullScreenButton.setVisibility(View.VISIBLE);
									}
									if (Constant.videoType == 1) {
										stopTalkButton.setVisibility(View.GONE);
									} else if (currentAudienceId == config().mUid) {
										stopTalkButton.setVisibility(View.VISIBLE);
									}

									if (currentMaterial != null) {
										if (!mMaterialId.equals(currentMaterial.getId())) {
											ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, mMaterialId);
										} else {
											currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);
											if (currentMaterialPublish.getType().equals("1")) {
												PlayVideo();
											} else {
												stopPlayVideo();
												findViewById(R.id.app_video_box).setVisibility(View.GONE);
//												mPlayVideoText.setVisibility(View.GONE);

												pageText.setVisibility(View.VISIBLE);
												pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

												if (docImage != null) {
													Glide.with(BaseApplication.getInstance()).load(currentMaterialPublish.getUrl())
															.placeholder(docImage.getDrawable())
															.error(R.drawable.rc_image_error)
															.into(docImage);
													docImage.setVisibility(View.VISIBLE);
												}

											}
											if (remoteBroadcasterSurfaceView != null) {
												broadcasterLayout.removeView(remoteBroadcasterSurfaceView);
												broadcasterLayout.setVisibility(View.GONE);

											}

										}
									} else {
										ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, mMaterialId);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							logAction("当前是取消ppt模式");
							stopPlayVideo();
							findViewById(R.id.app_video_box).setVisibility(View.GONE);
							model = -1;
							doc_index = 0;
							pageText.setVisibility(View.GONE);
							docImage.setVisibility(View.GONE);

							if (Constant.videoType == 1) {
								stopTalkButton.setVisibility(View.GONE);
							} else if (currentAudienceId == config().mUid) {
								stopTalkButton.setVisibility(View.VISIBLE);
							}
							currentMaterial = null;
							currentMaterialPublish = null;

							isDocShow = false;
							if (isSplitView) {
								fullScreenButton.setVisibility(View.GONE);
							} else {
								fullScreenButton.setVisibility(View.VISIBLE);
							}

							//退出ppt模式
							//需要判断自己是否是参会人或者连麦人 如果是 需要将自己加入到adapter中
							//还需要判断当前是否是分屏模式 如果是分屏模式 需要重新进行分屏
							if (remoteBroadcasterSurfaceView != null) {
								remoteBroadcasterSurfaceView.setVisibility(View.VISIBLE);
							}
							if (Constant.videoType == 1 || currentAudienceId == config().mUid) {
								if (mVideoAdapter.getPositionById(config().mUid) == -1) {
									AudienceVideo audienceVideo = new AudienceVideo();
									audienceVideo.setUid(config().mUid);
									audienceVideo.setName("参会人" + meetingJoin.getHostUser().getHostUserName());
									audienceVideo.setBroadcaster(false);
									audienceVideo.setSurfaceView(localSurfaceView);
									mVideoAdapter.insertItem(audienceVideo);
								}
							}

							logAction(isSplitView ? "当前是分屏模式" : "当前是大屏模式");
							if (isSplitView) {
								exitSpliteMode();
								SpliteViews(1);
							} else {
								//如果不是分屏模式 需要判断主持人是否在列表中
								if (mVideoAdapter.isHaveChairMan()) {
									//主持人在列表中 删除主持人
									int chairManPosition = mVideoAdapter.getChairManPosition();
									if (chairManPosition != -1) {
										mVideoAdapter.removeItem(chairManPosition);
									}
								}
								//将主持人加入到大的view中
								broadcasterLayout.removeAllViews();
								broadcasterLayout.setVisibility(View.VISIBLE);
								if (remoteBroadcasterSurfaceView != null) {
									stripSurfaceView(remoteBroadcasterSurfaceView);
									remoteBroadcasterSurfaceView.setZOrderOnTop(false);
									remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
									broadcasterLayout.addView(remoteBroadcasterSurfaceView);
								}

							}
							mAudienceRecyclerView.setVisibility(View.VISIBLE);
							mVideoAdapter.setVisibility(View.VISIBLE);


						}
					}

					if (Constant.MODEL_CHANGE.equals(name)) {
						if (value.equals(Constant.EQUALLY)) {//均分


							logAction("当前集合的大小=  " + mVideoAdapter.getDataSize());

							if (mVideoAdapter.getDataSize() >= 1) {
								if (!mVideoAdapter.isHaveChairMan()) {
									logAction("均分模式中  列表中没有主持人");
									if (remoteBroadcasterSurfaceView != null) {
										stripSurfaceView(remoteBroadcasterSurfaceView);
									}
									if (isHostCommeIn) {
										AudienceVideo audienceVideo = new AudienceVideo();
										audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
										audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
										audienceVideo.setBroadcaster(true);
										audienceVideo.setSurfaceView(remoteBroadcasterSurfaceView);
										mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
										mVideoAdapter.notifyDataSetChanged();
										broadcasterLayout.removeAllViews();
									}
								}
							}

							if (isHostCommeIn && mVideoAdapter.getDataSize() > 1 && !isDocShow) {
								SpliteViews(5);
							}
							mVideoAdapter.setVisibility(View.VISIBLE);
							mVideoAdapter.notifyDataSetChanged();
							mAudienceRecyclerView.setVisibility(View.VISIBLE);
							/*else {
								int chairManPosition = mVideoAdapter.getChairManPosition();
								if (chairManPosition != -1) {
									mVideoAdapter.removeItem(chairManPosition);
								}
								if (remoteBroadcasterSurfaceView != null) {
									stripSurfaceView(remoteBroadcasterSurfaceView);
									broadcasterLayout.removeAllViews();
									broadcasterLayout.addView(remoteBroadcasterSurfaceView);
								} else {
									broadcastTipsText.setVisibility(View.VISIBLE);
								}

							}*/
							if (isDocShow) {
								fullScreenButton.setVisibility(View.VISIBLE);
							} else {
								fullScreenButton.setVisibility(View.GONE);
							}


						} else if (value.equals(Constant.BIGSCREEN)) {//大屏
							logAction("大屏模式");
							isSplitView = false;
							mVideoAdapter.notifyDataSetChanged();
							if (isFullScreen || model == 2 || model == 3) {
								mVideoAdapter.setVisibility(View.GONE);
								mAudienceRecyclerView.setVisibility(View.GONE);
							} else {
								mVideoAdapter.setVisibility(View.VISIBLE);
								mAudienceRecyclerView.setVisibility(View.VISIBLE);
							}

							fullScreenButton.setVisibility(View.VISIBLE);
							if (!isDocShow) {
								exitSpliteMode();
							}
						}


					}

					if (Constant.VIDEO.equals(name)) {
						if (value.equals(Constant.PLAYVIDEO)) {
							if (player != null) {
								findViewById(R.id.app_video_box).setVisibility(View.VISIBLE);

								try {
									player.showWifiDialog();
								} catch (Exception e) {
									findViewById(R.id.app_video_box).setVisibility(View.GONE);
									logAction("出错了：  " + e.getMessage());
//									PlayVideo();
								}
//								player.startButton.performClick();
							} else {
								PlayVideo();
							}
						} else if (value.equals(Constant.PAUSEVIDEO)) {
							if (player != null) {
								/*if (player.state == Jzvd.STATE_PAUSE) {
									return;
								}*/
								player.pauseVideo();
//								player.startButton.performClick();
							}
						}
					}


					if (TextUtils.isEmpty(name) && type.equals("clear")) {
						isDocShow = false;

						findViewById(R.id.app_video_box).setVisibility(View.GONE);
						stopPlayVideo();

						//应该是会议统计的接口
						if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
							params.put("status", 2);
							params.put("meetingId", meetingJoin.getMeeting().getId());
							params.put("type", 2);
							params.put("leaveType", 1);
							ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
						}

						if (Constant.videoType == 2) {
							requestTalkButton.setVisibility(View.VISIBLE);
						} else {
							requestTalkButton.setVisibility(View.GONE);
						}
						docImage.setVisibility(View.GONE);
						pageText.setVisibility(View.GONE);

						if (Constant.videoType == 2) {
							mMuteAudio.setVisibility(View.GONE);
							mSwtichCamera.setVisibility(View.GONE);
						} else if (Constant.videoType == 1) {
							mMuteAudio.setVisibility(View.VISIBLE);
							mSwtichCamera.setVisibility(View.VISIBLE);
						}
						/*setTextViewDrawableTop(mMuteAudio, R.drawable.icon_speek);
						mMuteAudio.setText("话筒打开");*/
						if (isSplitView && !isDocShow) {
							fullScreenButton.setVisibility(View.GONE);
						} else {
							fullScreenButton.setVisibility(View.VISIBLE);
						}

						currentMaterial = null;


						if (currentAudienceId != 0) {
							mVideoAdapter.deleteItem(currentAudienceId);
							int positionById = mVideoAdapter.getPositionById(currentAudienceId);
							if (positionById != -1) {
								AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
								if (audienceVideo != null) {
									SurfaceView surfaceView = audienceVideo.getSurfaceView();
									if (surfaceView != null) {
										surfaceView.setVisibility(View.GONE);
									}
								}
							}
							currentAudienceId = 0;
						}

						//退出ppt模式
						//需要判断自己是否是参会人或者连麦人 如果是 需要将自己加入到adapter中
						//还需要判断当前是否是分屏模式 如果是分屏模式 需要重新进行分屏

						if (Constant.videoType == 1 || currentAudienceId == config().mUid) {
							if (mVideoAdapter.getPositionById(config().mUid) == -1) {
								AudienceVideo audienceVideo = new AudienceVideo();
								audienceVideo.setUid(config().mUid);
								audienceVideo.setName("参会人" + meetingJoin.getHostUser().getHostUserName());
								audienceVideo.setBroadcaster(false);
								audienceVideo.setSurfaceView(localSurfaceView);
								mVideoAdapter.insertItem(audienceVideo);
							}
						}

						if (remoteBroadcasterSurfaceView != null) {
							remoteBroadcasterSurfaceView.setVisibility(View.VISIBLE);
						}
						if (isSplitView) {
							SpliteViews(2);
						} else {
							//如果不是分屏模式 需要判断主持人是否在列表中
							if (mVideoAdapter.isHaveChairMan()) {
								//主持人在列表中 删除主持人
								int chairManPosition = mVideoAdapter.getChairManPosition();
								if (chairManPosition != -1) {
									mVideoAdapter.removeItem(chairManPosition);
								}
							}
							//将主持人加入到大的view中
							broadcasterLayout.removeAllViews();
							broadcasterLayout.setVisibility(View.VISIBLE);
							if (remoteBroadcasterSurfaceView != null) {
								stripSurfaceView(remoteBroadcasterSurfaceView);
								remoteBroadcasterSurfaceView.setZOrderOnTop(false);
								remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
								broadcasterLayout.addView(remoteBroadcasterSurfaceView);
							}

						}
						mAudienceRecyclerView.setVisibility(View.VISIBLE);
						mVideoAdapter.setVisibility(View.VISIBLE);


						if (mVideoAdapter.getDataSize() <= 0) {
							mAudienceRecyclerView.setVisibility(View.GONE);
							mVideoAdapter.setVisibility(View.GONE);
						} else {
							mAudienceRecyclerView.setVisibility(View.VISIBLE);
							mVideoAdapter.setVisibility(View.VISIBLE);
						}
						count = 0;//清除所有  重置count
						stopTalkButton.setVisibility(View.GONE);
						model = -1;
						doc_index = 0;
					}


					if (Constant.KEY_ClOSE_MIC.equals(name)) {
						isMuted = true;
						rtcEngine().muteLocalAudioStream(isMuted);
						mMuteAudio.setText("话筒关闭");
						setTextViewDrawableTop(mMuteAudio, R.drawable.icon_unspeek);
					}
				});
			}


			@Override
			public void onError(final String name, final int ecode, final String desc) {
				super.onError(name, ecode, desc);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> {
						if (ecode != 208)
							Toast.makeText(MeetingAudienceActivity.this, "收到错误信息\nname: " + name + "\necode: " + ecode + "\ndesc: " + desc, Toast.LENGTH_SHORT).show();
					});
				}
				if (ecode == 1002) {
					return;
				}
//                if (agoraAPI.getStatus() != 1 && agoraAPI.getStatus() != 2 && agoraAPI.getStatus() != 3) {
//                    if ("true".equals(agora.getIsTest())) {
//                        agoraAPI.login2(agora.getAppID(), "" + config().mUid, "noneed_token", 0, "", 20, 30);
//                    } else {
//                        agoraAPI.login2(agora.getAppID(), "" + config().mUid, agora.getSignalingKey(), 0, "", 20, 30);
//                    }
//                }
			}

			@Override
			public void onLog(String txt) {
				super.onLog(txt);
				Log.v("audience信令", txt);
			}
		});

		ApiClient.getInstance().getMeetingHost(TAG, meeting.getId(), joinMeetingCallback(0));
		mTipDialog = WaitDialog.show(this, "请稍候...");
		//这个目前没有使用了
		startMeetingCamera(meeting.getScreenshotFrequency());

		//初始化在线用户数
		changeRoomOnlineCountUI(null);
	}

	/**
	 * 获取融云群组登录信息，可以获取进入人数
	 * */
	private void verifyIsInGroup(int numPerson) {
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		jsonObject.put("groupId", meetingJoin.getMeeting().getId());
		mApiService.queryGroupUser(jsonObject).compose(RxSchedulersHelper.io_main()).subscribe(new RxSubscriber<com.alibaba.fastjson.JSONObject>() {

			@Override
			public void onSubscribe(Disposable d) {
			}

			@Override
			public void _onNext(com.alibaba.fastjson.JSONObject jsonObject) {
                Logger.e(jsonObject.toJSONString());
                try {
                    ImUserInfo imUserInfo = jsonObject.toJavaObject(ImUserInfo.class);
                    List<ImUserInfo.DataBean.UsersBean> groups = null;
                    if (imUserInfo.getErrcode() == 0 && imUserInfo.getData()!=null && imUserInfo.getData().getCode().equals("200")) {
                        groups = imUserInfo.getData().getUsers();
                    }
                    List<ImUserInfo.DataBean.UsersBean> finalGroups = groups;
                    runOnUiThread(() -> {
                        changeRoomOnlineCountUI(finalGroups);
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        //查询解析失败逻辑(采用以前逻辑 退出)
                        showToastyWarn("进入聊天室失败");
                        finish();
                    });
                }

//				Observable.create(new ObservableOnSubscribe<Boolean>() {
//					@SuppressLint("SetTextI18n")
//					@Override
//					public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
//						Logger.e(jsonObject.toJSONString());
//						try {
//							ImUserInfo imUserInfo = jsonObject.toJavaObject(ImUserInfo.class);
//							if (imUserInfo.getErrcode() == 0) {
//								if (imUserInfo.getData().getCode().equals("200")) {
//									countText.setText("在线人数：" + imUserInfo.getData().getUsers().size());
//								} else {
//									countText.setText("在线人数：" + numPerson);
//								}
//							} else {
//								countText.setText("在线人数：" + numPerson);
//							}
//						} catch (Exception e) {
//							emitter.onNext(false);
//						}
//					}
//				}).observeOn(AndroidSchedulers.mainThread())
//						.subscribeOn(Schedulers.computation())
//						.subscribe(new Observer<Boolean>() {
//							@Override
//							public void onSubscribe(Disposable d) {
//
//							}
//
//							@Override
//							public void onNext(Boolean aBoolean) {
//								Logger.e("aBoolean:-->"+aBoolean);
//								runOnUiThread(() -> {
//									countText.setText("在线人数：" + numPerson);
//								});
//
//								if (!aBoolean) {
//
//								}
//							}
//
//							@Override
//							public void onError(Throwable e) {
//								showToastyWarn("进入聊天室失败");
//								finish();
//							}
//
//							@Override
//							public void onComplete() {
//
//							}
//						});
			}
		});
	}

	private void startMeetingCamera(int screenshotFrequency) {

		//不抓拍
		return;

	/*	takePhotoTimer = new Timer();
		takePhotoTimerTask = new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent(MeetingAudienceActivity.this, Camera1ByServiceActivity.class);
				intent.putExtra(Camera1ByServiceActivity.KEY_IMAGE_COMPRESSION_RATIO, meeting.getScreenshotCompressionRatio());
				startActivityForResult(intent, CODE_REQUEST_TAKEPHOTO);
				overridePendingTransition(0, 0);
			}
		};
		takePhotoTimer.schedule(takePhotoTimerTask, screenshotFrequency * 1000, screenshotFrequency * 1000);*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CODE_REQUEST_TAKEPHOTO) {
			try {
				String pictureLocalPath = data.getStringExtra("pictureLocalPath");
				uploadMeetingImageToQiniu(pictureLocalPath);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * 上传参会人参会时图片到七牛服务器
	 *
	 * @param imagePath
	 */
	private void uploadMeetingImageToQiniu(String imagePath) {
		ApiClient.getInstance().requestQiniuToken(TAG, new OkHttpCallback<BaseBean<QiniuToken>>() {

			@Override
			public void onSuccess(BaseBean<QiniuToken> result) {
				String token = result.getData().getToken();
				if (TextUtils.isEmpty(token)) {
					String errorMsg = "七牛token获取错误";
					ZYAgent.onEvent(getApplicationContext(), errorMsg);
					ToastUtils.showToast(errorMsg);
					return;
				}
				Configuration config = new Configuration.Builder().connectTimeout(5).responseTimeout(5).build();
				UploadManager uploadManager = new UploadManager(config);
				ZYAgent.onEvent(getApplicationContext(), "拍照完毕，准备上传本地图片：" + imagePath);
				if (imagePath == null || imagePath.length() <= 0) {
					return;
				}

				try {
					String uploadKey = BuildConfig.QINIU_IMAGE_UPLOAD_PATH + meeting.getId() + "/" + Preferences.getUserId() + "/" + imagePath.substring(imagePath.lastIndexOf('/') + 1);
					uploadManager.put(new File(imagePath), uploadKey, token, meetingImageUpCompletionHandler, new UploadOptions(null, null, true, new UpProgressHandler() {
						@Override
						public void progress(final String key, final double percent) {
						}
					}, null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private UpCompletionHandler meetingImageUpCompletionHandler = new UpCompletionHandler() {
		@Override
		public void complete(String key, ResponseInfo info, JSONObject response) {
			if (info.isNetworkBroken() || info.isServerError()) {
				ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传七牛云失败");
				return;
			}
			if (info.isOK()) {
				String meetingImageUrl = BuildConfig.QINIU_IMAGE_DOMAIN + key;
				ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传七牛云成功，地址：" + meetingImageUrl);
				uploadMeetingImageToServer(meeting.getId(), meetingImageUrl);
			} else {
				ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传七牛云失败");
			}
		}
	};

	/**
	 * 上传参会人参会时图片到Server
	 *
	 * @param meetingId
	 * @param qiniuImageUrl
	 */
	public void uploadMeetingImageToServer(String meetingId, String qiniuImageUrl) {
		Map<String, Object> params = new HashMap<>();
		params.put("meetingId", meetingId);
		params.put("imgUrl", qiniuImageUrl);
		params.put("ts", System.currentTimeMillis());
		ApiClient.getInstance().meetingScreenshot(this, params, uploadMeetingImageToServerCallback);
	}

	private OkHttpCallback<Bucket<MeetingScreenShot>> uploadMeetingImageToServerCallback = new OkHttpCallback<Bucket<MeetingScreenShot>>() {

		@Override
		public void onSuccess(Bucket<MeetingScreenShot> meetingScreenShotBucket) {
			ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传服务器成功");
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			ZYAgent.onEvent(getApplicationContext(), "参会人直播图像上传服务器失败，错误提示: " + errorCode + ", Excepton: " + exception.getMessage());
		}
	};
	private boolean isHostCommeIn = false;
	private int model = -1;

	/**
	 * 加入会议的回调 得到的是参会人 或者主持人
	 * */
	private OkHttpCallback joinMeetingCallback(int uid) {
		return new OkHttpCallback<Bucket<MeetingJoin.DataBean.HostUserBean>>() {

			@Override
			public void onSuccess(Bucket<MeetingJoin.DataBean.HostUserBean> meetingJoinBucket) {
				logAction(JSON.toJSONString(meetingJoinBucket));
				meetingJoin.setHostUser(meetingJoinBucket.getData());
				broadcastId = meetingJoinBucket.getData().getClientUid();
				if (!TextUtils.isEmpty(meetingJoinBucket.getData().getHostUserName())){
					broadcastNameText.setText("主持人：" + meetingJoinBucket.getData().getHostUserName());
				}
				if (uid != 0 && broadcastId != null) {
					Logger.e("uid:  " + uid + "---------" + "broadcastId:" + broadcastId);
					if (String.valueOf(uid).equals(broadcastId)) {
						if (BuildConfig.DEBUG) {
							logAction("主持人进入");
							Toast.makeText(MeetingAudienceActivity.this, "主持人" + broadcastId + "---" + uid + meetingJoin.getHostUser().getHostUserName() + "进入了", Toast.LENGTH_SHORT).show();
						}
						model = -1;
						isHostCommeIn = true;
						agoraAPI.channelJoin(channelName);
						agoraAPI.queryUserStatus(broadcastId);

						broadcastTipsText.setVisibility(View.GONE);

						remoteBroadcasterSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
						remoteBroadcasterSurfaceView.setZOrderOnTop(false);
						remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
						rtcEngine().setupRemoteVideo(new VideoCanvas(remoteBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));

							/*rtcEngine().setRemoteRenderMode(uid, Constants.RENDER_MODE_FIT);
					broadcasterSmallLayout.setVisibility(View.INVISIBLE);
						broadcasterSmallView.removeAllViews();
						docImage.setVisibility(View.GONE);*/

						logAction("isDocShow:   " + isDocShow + "----   model:  " + model);

						if (isDocShow) {
							if (model == 2 || model == 3) {
								mVideoAdapter.setVisibility(View.GONE);
								mAudienceRecyclerView.setVisibility(View.GONE);

							} else if (model == 0 || model == 1) {

								//主持人加入到列表中
								if (!mVideoAdapter.isHaveChairMan()) {
									AudienceVideo audienceVideo = new AudienceVideo();
									audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
									audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
									audienceVideo.setBroadcaster(true);
									audienceVideo.setSurfaceView(remoteBroadcasterSurfaceView);
									mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
								} else {
									int chairManPosition = mVideoAdapter.getChairManPosition();
									if (chairManPosition != -1) {
										mVideoAdapter.getAudienceVideoLists().get(chairManPosition).setSurfaceView(remoteBroadcasterSurfaceView);
										mVideoAdapter.notifyDataSetChanged();
									}
								}
								mVideoAdapter.setVisibility(View.VISIBLE);
								mAudienceRecyclerView.setVisibility(View.VISIBLE);
							}
						} else {
							if (isSplitView) {
								if (!mVideoAdapter.isHaveChairMan()) {
									AudienceVideo audienceVideo = new AudienceVideo();
									audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
									audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
									audienceVideo.setBroadcaster(true);
									audienceVideo.setSurfaceView(remoteBroadcasterSurfaceView);
									mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
								} else {
									int chairManPosition = mVideoAdapter.getChairManPosition();
									if (chairManPosition != -1) {
										mVideoAdapter.getAudienceVideoLists().get(chairManPosition).setSurfaceView(remoteBroadcasterSurfaceView);
										mVideoAdapter.notifyDataSetChanged();
									}
								}
								mVideoAdapter.setVisibility(View.VISIBLE);
								mAudienceRecyclerView.setVisibility(View.VISIBLE);

							} else {
								docImage.setVisibility(View.GONE);
								broadcasterLayout.setVisibility(View.VISIBLE);
								broadcasterLayout.removeAllViews();
								broadcasterLayout.addView(remoteBroadcasterSurfaceView);
								mAudienceRecyclerView.setVisibility(View.VISIBLE);
								mVideoAdapter.setVisibility(View.VISIBLE);
							}

						}
					} else {
						if (BuildConfig.DEBUG) {
							Toast.makeText(MeetingAudienceActivity.this, "参会人" + uid + "正在连麦", Toast.LENGTH_SHORT).show();
						}
						logAction("参会人" + uid + "正在连麦");
						/*audienceLayout.setVisibility(View.VISIBLE);
						audienceView.removeAllViews();*/
						remoteAudienceSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
						remoteAudienceSurfaceView.setZOrderOnTop(true);
						remoteAudienceSurfaceView.setZOrderMediaOverlay(true);
						rtcEngine().setupRemoteVideo(new VideoCanvas(remoteAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
//						audienceView.addView(remoteAudienceSurfaceView);

						AudienceVideo audienceVideo = new AudienceVideo();
						audienceVideo.setUid(uid);
						audienceVideo.setName("参会人" + uid);
						audienceVideo.setBroadcaster(false);
						audienceVideo.setSurfaceView(remoteAudienceSurfaceView);
						mVideoAdapter.insertItem(audienceVideo);
						agoraAPI.getUserAttr(String.valueOf(uid), "userName");
						agoraAPI.getUserAttr(String.valueOf(uid), "uname");//会走回调onUserAttrResult
					}
				} else {
					if ("true".equals(agora.getIsTest())) {
						worker().joinChannel(null, channelName, config().mUid);
					} else {
						worker().joinChannel(agora.getToken(), channelName, config().mUid);
					}
				}
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("uid", config().mUid);
					jsonObject.put("uname", audienceName);
					jsonObject.put("name", audienceName);
					jsonObject.put("handsUp", handsUp);
					if (Constant.videoType == 1) {
						jsonObject.put("callStatus", 2);
						jsonObject.put("isAudience", false);

					} else {
						if (currentAudienceId == config().mUid) {
							jsonObject.put("callStatus", 2);
							jsonObject.put("isAudience", true);
						} else {
							jsonObject.put("callStatus", 0);
							jsonObject.put("isAudience", true);
						}

					}
					jsonObject.put("auditStatus", Preferences.getUserAuditStatus());
					jsonObject.put("postTypeName", Preferences.getUserPostType());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");


				JSONObject json = new JSONObject();
				try {
					json.put("userName", audienceName);
					json.put("uid", uid);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				agoraAPI.channelSetAttr(channelName, Constant.USERNAME, json.toString());

				if (isSplitView && mVideoAdapter.getDataSize() >= 1 && !isDocShow && isHostCommeIn && mVideoAdapter.getDataSize() <= 8) {
					SpliteViews(3);
				}


				if (model == 2 || model == 3 || isFullScreen) {
					mVideoAdapter.setVisibility(View.GONE);
					mAudienceRecyclerView.setVisibility(View.GONE);
				} else {
					if (model == 0 || model == 1) {
						notFullScreenState();
					}
					mVideoAdapter.setVisibility(View.VISIBLE);
					mAudienceRecyclerView.setVisibility(View.VISIBLE);
				}


				if (isDocShow) {
					mVideoAdapter.notifyDataSetChanged();
				}


				if (mVideoAdapter.getDataSize() >= 1) {
					fullScreenButton.setVisibility(View.VISIBLE);
				} else {
					fullScreenButton.setVisibility(View.GONE);
				}

			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
				mTipDialog.doDismiss();
				Logger.e("?????----->" + exception.getMessage());
//				Toast.makeText(MeetingAudienceActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

				MessageDialog.show(MeetingAudienceActivity.this, "提示", "因为网络原因 获取视频失败 可重新进入会议重试", "退出", "取消").setOnOkButtonClickListener(new OnDialogButtonClickListener() {
					@Override
					public boolean onClick(BaseDialog baseDialog, View v) {
						doLeaveChannel();
						finish();
						return false;
					}
				});
			}
		};
	}


	/**
	 * 会议资料的回调
	 * */
		private OkHttpCallback meetingMaterialCallback = new OkHttpCallback<Bucket<Material>>() {

		@Override
		public void onSuccess(Bucket<Material> materialBucket) {
			Log.v("material", materialBucket.toString());
			currentMaterial = materialBucket.getData();

			if (currentMaterial.getMeetingMaterialsPublishList().size() <= 0) {
				ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, mMaterialId);
			}

			/*MeetingMaterialsPublish e1 = new MeetingMaterialsPublish();
			e1.setCreateDate(System.currentTimeMillis() + "");
			e1.setId(System.currentTimeMillis() + "");
			e1.setType("1");
			e1.setPriority(4);
			e1.setUrl("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");

			currentMaterial.getMeetingMaterialsPublishList().add(e1);*/

			Collections.sort(currentMaterial.getMeetingMaterialsPublishList(), (o1, o2) -> (o1.getPriority() < o2.getPriority()) ? -1 : 1);

			currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(doc_index);

			if (remoteBroadcasterSurfaceView != null) {
				broadcasterLayout.removeView(remoteBroadcasterSurfaceView);
				broadcasterLayout.setVisibility(View.GONE);
			}

			if (currentMaterialPublish.getType().equals("1")) {
				PlayVideo();
			} else {
				findViewById(R.id.app_video_box).setVisibility(View.GONE);
//				mPlayVideoText.setVisibility(View.GONE);
				stopPlayVideo();
				pageText.setVisibility(View.VISIBLE);
				pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
				docImage.setVisibility(View.VISIBLE);
				Glide.with(MeetingAudienceActivity.this)
						.load(currentMaterialPublish.getUrl())
						.placeholder(docImage.getDrawable())
						.error(R.drawable.rc_image_error)
						.into(docImage);
			}
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(MeetingAudienceActivity.this, errorCode + "---" + exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	private String meetingHostJoinTraceId;


	/**
	 * 加入会议统计的回调
	 * */
	private OkHttpCallback meetingHostJoinTraceCallback = new OkHttpCallback<Bucket<MeetingHostingStats>>() {

		@Override
		public void onSuccess(Bucket<MeetingHostingStats> meetingHostingStatsBucket) {
			if (TextUtils.isEmpty(meetingHostJoinTraceId)) {
				meetingHostJoinTraceId = meetingHostingStatsBucket.getData().getId();
			} else {
				meetingHostJoinTraceId = null;
			}
		}
	};

	private Dialog dialog;

	private void showDialog(final int type, final String title, final String leftText, final String rightText, final Audience audience) {
		View view = View.inflate(this, R.layout.dialog_selector, null);
		TextView titleText = view.findViewById(R.id.title);
		titleText.setText(title);

		Button leftButton = view.findViewById(R.id.left);
		leftButton.setText(leftText);
		leftButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.cancel();
			}
		});

		if (agoraAPI == null) {
			agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());
		}

		Button rightButton = view.findViewById(R.id.right);
		rightButton.setText(rightText);
		rightButton.setOnClickListener(view1 -> {
			dialog.cancel();
			if (type == 1) {
				if (localSurfaceView != null && remoteAudienceSurfaceView == null) {

					localSurfaceView = null;

					if (agoraAPI.getStatus() == 2) {
//						agoraAPI.setAttr("uname", null);
						agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
					}

					requestTalkButton.setVisibility(View.VISIBLE);
					stopTalkButton.setVisibility(View.GONE);

					handsUp = false;
					requestTalkButton.setText("申请发言");

					worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);

					if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
						params.put("status", 2);
						params.put("meetingId", meetingJoin.getMeeting().getId());
						params.put("type", 2);
						params.put("leaveType", 1);
						ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
					}
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("finish", true);
						agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (localSurfaceView == null && remoteAudienceSurfaceView != null) {

					remoteAudienceSurfaceView = null;


				}

				doLeaveChannel();
				if (agoraAPI.getStatus() == 2) {
					agoraAPI.logout();
				}
				finish();
			} else if (type == 2) {
				worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
				stopTalkButton.setVisibility(View.GONE);
				requestTalkButton.setVisibility(View.VISIBLE);
				localSurfaceView = null;


				agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);


				handsUp = false;
				requestTalkButton.setText("申请发言");

				if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
					params.put("status", 2);
					params.put("meetingId", meetingJoin.getMeeting().getId());
					params.put("type", 2);
					params.put("leaveType", 1);
					ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
				}
			}
		});

		dialog = new Dialog(this, R.style.MyDialog);
		dialog.setContentView(view);

		dialog.show();
	}

	@Override
	protected void deInitUIandEvent() {
		doLeaveChannel();
		event().removeEventHandler(this);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (JoinSuc) {
			if (TextUtils.isEmpty(meetingJoinTraceId)) {
				doTEnterChannel();
			}
		}


	}

	private void doTEnterChannel() {
		try {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("status", 1);
			params.put("type", 2);
			params.put("agoraUid", config().mUid);
			if (meetingJoin == null) {
				meetingJoin = getIntent().getParcelableExtra("meeting");
			}
			params.put("meetingId", meetingJoin.getMeeting().getId());
			ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doLeaveChannel() {
		try {
			worker().leaveChannel(config().mChannel);
			worker().preview(false, null, 0);

			if (!TextUtils.isEmpty(meetingJoinTraceId)) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("meetingJoinTraceId", meetingJoinTraceId);
				if (meetingJoin == null) {
					meetingJoin = getIntent().getParcelableExtra("meeting");
				}
				params.put("meetingId", meetingJoin.getMeeting().getId());
				params.put("status", 2);
				params.put("type", 2);
				params.put("leaveType", 1);
				params.put("agoraUid", config().mUid);
				ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 声网的回调 加入频道成功
	 * */
	@Override
	public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {


		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}

			logAction("channel:  " + channel + "  uid:" + uid + " elapsed:" + elapsed);
			config().mUid = uid;
			channelName = channel;


			agoraAPI.setAttr("userName", Preferences.getUserName());
			agoraAPI.setAttr("uname", Preferences.getUserName());

			int quilty = MMKV.defaultMMKV().decodeInt(MMKVHelper.KEY_meetingQuilty);
			VideoEncoderConfiguration.VideoDimensions vProfile = VideoEncoderConfiguration.VD_320x180;
			if (quilty == 1.0) {
				vProfile = VideoEncoderConfiguration.VD_320x180;//参会人是320*180
			} else if (quilty == 2.0) {
				vProfile = VideoEncoderConfiguration.VD_640x360;//参会人是320*180
			} else if (quilty == 3.0) {
				vProfile = VideoEncoderConfiguration.VD_1280x720;//参会人是640 x 480
			} else {
				vProfile = VideoEncoderConfiguration.VD_640x480;//参会人是320*180
			}
			// 观众的方式进入
			if (Constant.videoType == 2) {
				logAction(config().mUid);
				worker().configEngine(Constants.CLIENT_ROLE_AUDIENCE, vProfile);//参会人是320*180
				mMuteAudio.setVisibility(View.GONE);
				mSwtichCamera.setVisibility(View.GONE);

			} else if (Constant.videoType == 1) {
				//参会人的方式进入
				localSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
				rtcEngine().setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
				worker().preview(true, localSurfaceView, config().mUid);
				mLocalAudienceVideo = new AudienceVideo();
				mLocalAudienceVideo.setUid(config().mUid);
				mLocalAudienceVideo.setName(audienceName);
				mLocalAudienceVideo.setBroadcaster(false);
				mLocalAudienceVideo.setSurfaceView(localSurfaceView);
				mVideoAdapter.insertItem(mLocalAudienceVideo);
				worker().configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);//参会人是320*180
//			rtcEngine().enableAudioVolumeIndication(400, 3);
				rtcEngine().muteLocalAudioStream(false);
				requestTalkButton.setVisibility(View.GONE);
				mMuteAudio.setVisibility(View.VISIBLE);
				mSwtichCamera.setVisibility(View.VISIBLE);
			}
			if ("true".equals(agora.getIsTest())) {
				agoraAPI.login2(agora.getAppID(), "" + uid, "noneed_token", 0, "", 20, 30);
			} else {
				agoraAPI.login2(agora.getAppID(), "" + uid, agora.getSignalingKey(), 0, "", 20, 30);
			}

			mTipDialog.doDismiss();
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("status", 1);
			params.put("type", 2);
			params.put("meetingId", meetingJoin.getMeeting().getId());
			JoinSuc = true;
			ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);
		});
	}

	private String meetingJoinTraceId;

	/**
	 * 加入频道成功的回调
	 * */
	private OkHttpCallback meetingJoinStatsCallback = new OkHttpCallback<Bucket<MeetingJoinStats>>() {

		@Override
		public void onSuccess(Bucket<MeetingJoinStats> meetingJoinStatsBucket) {
			try {
				if (TextUtils.isEmpty(meetingJoinTraceId)) {
					meetingJoinTraceId = meetingJoinStatsBucket.getData().getId();
				} else {
					meetingJoinTraceId = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 声网相关 当远端视频进入
	 * */
	@Override
	public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}
			ApiClient.getInstance().getMeetingHost(TAG, meeting.getId(), joinMeetingCallback(uid));
		});
	}

	/**
	 * 声网回调 当有人离开频道
	 * */
	@Override
	public void onUserOffline(int uid, int reason) {

		logAction("onUserOffline   uid:" + uid + "   ----   " + "reason:" + reason);
		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}
			synchronized (this) {
				if (uid == floatAudienceId) {
					if (FloatWindow.get("audience") != null) {
						FloatWindow.get("audience").hide();
					}
					floatSurfaceView = null;
					floatAudienceId = -1;
				}
				if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == uid) {
					broadcasterLayout.removeAllViews();
					if (mVideoAdapter.isHaveChairMan()) {
						int chairManPosition = mVideoAdapter.getChairManPosition();
						if (chairManPosition != -1) {
							AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
							if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
								mVideoAdapter.removeItem(chairManPosition);
							}

						}
					}
					remoteBroadcasterSurfaceView.setZOrderOnTop(true);
					remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
					stripSurfaceView(remoteBroadcasterSurfaceView);
					broadcasterLayout.addView(remoteBroadcasterSurfaceView);
					mCurrentAudienceVideo = null;
				}

				if (String.valueOf(uid).equals(broadcastId)) {
					if (Constant.videoType == 2 && config().mUid == currentAudienceId) {//我是连麦观众
						if (mVideoAdapter.getPositionById(config().mUid) != -1) {
							try {
								AudienceVideo video = mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getPositionById(config().mUid));
							/*if (video != null && video.getSurfaceView() != null) {
								video.getSurfaceView().setVisibility(View.GONE);
								video.getSurfaceView().setZOrderMediaOverlay(false);
							}*/
							} catch (Exception e) {
								e.printStackTrace();
							}
							mVideoAdapter.removeItem(mVideoAdapter.getPositionById(config().mUid));
							mVideoAdapter.notifyDataSetChanged();

						}

						mSwtichCamera.setVisibility(View.GONE);
						requestTalkButton.setText("申请发言");
						stopTalkButton.setVisibility(View.GONE);
						mMuteAudio.setVisibility(View.GONE);
					}
					logAction("主持人退出了");

					if (currentMaterial != null) {
						if (currentMaterial.getMeetingMaterialsPublishList().get(doc_index).getType().equals("1")) {
							if (player != null) {
								JzvdStd.releaseAllVideos();
							}
							player = null;
							findViewById(R.id.app_video_box).setVisibility(View.GONE);
						}
					}

					if (currentAudienceId == config().mUid) {
						/*观众此时在列表中*/
						if (mVideoAdapter.getPositionById(config().mUid) != -1) {
							mVideoAdapter.deleteItem(config().mUid);
							stripSurfaceView(remoteBroadcasterSurfaceView);
							broadcasterLayout.removeAllViews();
							if (remoteBroadcasterSurfaceView != null) {
								remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
								remoteBroadcasterSurfaceView.setZOrderOnTop(false);
								broadcasterLayout.addView(remoteBroadcasterSurfaceView);
							}

							logAction("观众在列表中");
						} else {
							logAction("观众不  在列表中");
							/*观众不再列表中 此时主持人在列表中*/
							if (mVideoAdapter.isHaveChairMan()) {
								logAction("主持人在列表中");
								int chairManPosition = mVideoAdapter.getChairManPosition();
								if (chairManPosition != -1) {
									stripSurfaceView(remoteBroadcasterSurfaceView);
									if (remoteBroadcasterSurfaceView != null) {
										remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
										remoteBroadcasterSurfaceView.setZOrderOnTop(false);
										broadcasterLayout.removeAllViews();
										broadcasterLayout.addView(remoteBroadcasterSurfaceView);
									}
									mVideoAdapter.deleteItem(chairManPosition);
								}
							}
						}
					}


					if (mVideoAdapter.isHaveChairMan()) {
						int chairManPosition = mVideoAdapter.getChairManPosition();
						if (chairManPosition != -1) {
							mVideoAdapter.removeItem(chairManPosition);
							if (mCurrentAudienceVideo != null) {
								mVideoAdapter.insertItem(chairManPosition, mCurrentAudienceVideo);
							}
						}

					}
					isHostCommeIn = false;
					broadcasterLayout.removeAllViews();
					if (reason == 0) {
						broadcastTipsText.setText("等待主持人进入...");
					} else if (reason == 1) {
						broadcastTipsText.setText("主持人掉线了……");
					}
					broadcastTipsText.setVisibility(View.VISIBLE);
					count = 0;
					broadcastNameText.setText("");
					remoteBroadcasterSurfaceView = null;

					if (remoteAudienceSurfaceView != null) {
						remoteAudienceSurfaceView = null;
					}
					if (localSurfaceView != null) {
						if (Constant.videoType == 1) {
							worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
							int positionById = mVideoAdapter.getPositionById(config().mUid);
							if (positionById > 0 && positionById < mVideoAdapter.getAudienceVideoLists().size()) {
								AudienceVideo video = mVideoAdapter.getAudienceVideoLists().get(positionById);
								rtcEngine().setupLocalVideo(new VideoCanvas(video.getSurfaceView(), VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
								worker().preview(true, video.getSurfaceView(), config().mUid);
							}
						} else {
							worker().getRtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
							stopTalkButton.setVisibility(View.GONE);
							requestTalkButton.setVisibility(View.VISIBLE);
							if (Constant.videoType == 2) {
								localSurfaceView = null;
							}

						}


						try {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("finish", true);
							agoraAPI.messageInstantSend(broadcastId, 0, jsonObject.toString(), "");
						} catch (Exception e) {
							e.printStackTrace();
						}

						currentAudienceId = -1;
						mCurrentAudienceVideo = null;
						if (!TextUtils.isEmpty(meetingHostJoinTraceId)) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("meetingHostJoinTraceId", meetingHostJoinTraceId);
							params.put("status", 2);
							params.put("meetingId", meetingJoin.getMeeting().getId());
							params.put("type", 2);
							params.put("leaveType", 1);
							ApiClient.getInstance().meetingHostStats(TAG, meetingHostJoinTraceCallback, params);
						}
					}
				} else {
					if (BuildConfig.DEBUG) {
						Toast.makeText(MeetingAudienceActivity.this, "连麦观众" + uid + "退出了" + config().mUid, Toast.LENGTH_SHORT).show();
					}
					//如果连麦的观众 在大的视图 那就移除这个人  把主持人放回到大视图
					// TODO: 2019-12-16 有问题  mCurrentAudienceVideo在主持人回到大的视图里面后 就置成空
					if (mCurrentAudienceVideo != null && mCurrentAudienceVideo.getUid() == uid) {
						logAction("连麦的观众 在大的视图   把主持人放回到大视图 ");
						broadcasterLayout.removeAllViews();
						if (mVideoAdapter.isHaveChairMan()) {
							int chairManPosition = mVideoAdapter.getChairManPosition();
							if (chairManPosition != -1) {
								AudienceVideo video = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
								if (video != null && video.getSurfaceView() != null) {
									video.getSurfaceView().setZOrderMediaOverlay(false);
									video.getSurfaceView().setZOrderOnTop(false);
									stripSurfaceView(video.getSurfaceView());
									broadcasterLayout.addView(video.getSurfaceView());
								}
								mVideoAdapter.removeItem(chairManPosition);
							}
						}
					} else {
						logAction("集合数据为" + mVideoAdapter.getAudienceVideoLists().toString());
						logAction("连麦观众在列表中");
						int postion = mVideoAdapter.getPositionById(uid);
						if (postion != -1) {
							AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(postion);
							if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
								audienceVideo.getSurfaceView().setZOrderOnTop(false);
								audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
							}
						}

						mVideoAdapter.deleteItemById(uid);
//					mVideoAdapter.getAudienceVideoLists().remove(mVideoAdapter.getPositionById(uid));
					}
					remoteAudienceSurfaceView = null;
				}

				if (isSplitView && !isDocShow) {
					if (mVideoAdapter.getDataSize() <= 1) {
						logAction("当前集合大小是小于等于1");
						exitSpliteMode();

					} else {
						logAction("当前集合大小是大于1的");
						SpliteViews(4);
					}
				}

				mVideoAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onConnectionLost() {
		runOnUiThread(() -> {

			logAction("onConnectionLost");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				if (MeetingAudienceActivity.this.isDestroyed()) {
					return;
				}
			}


			if (MeetingAudienceActivity.this.isFinishing()) {
				return;
			}
			if (mConnectionLostDialog == null) {
				mConnectionLostDialog = new AlertDialog(this)
						.builder()
						.setTitle("网络连接失败")
						.setMsg("与声网服务器连接断开  请检查网络连接")
						.setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton("退出", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mConnectionLostDialog.dismiss();
								finish();
							}
						});
				mConnectionLostDialog.show();
			} else {
				if (!mConnectionLostDialog.isShowing()) {
					mConnectionLostDialog.show();
				}
			}


		});
	}

	@Override
	public void onConnectionInterrupted() {
		runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "网络连接不佳，视频将会有卡顿，可尝试降低分辨率", Toast.LENGTH_SHORT).show());
	}

	@Override
	public void onUserMuteVideo(final int uid, final boolean muted) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show());
		}
	}

	@Override
	public void onUserMuteAudio(int uid, boolean muted) {

	}

	@Override
	public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

	}

	@Override
	public void onLastmileQuality(final int quality) {
		if (BuildConfig.DEBUG) {
//			runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "本地网络质量报告：" + showNetQuality(quality), Toast.LENGTH_SHORT).show());
		}
	}

	@Override
	public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(() -> {
				showNetQuality(txQuality, rxQuality);
//                    Toast.makeText(MeetingAudienceActivity.this, "用户" + uid + "的\n上行网络质量：" + showNetQuality(txQuality) + "\n下行网络质量：" + showNetQuality(rxQuality), Toast.LENGTH_SHORT).show();
			});
		}
	}

	@Override
	public void onWarning(int warn) {
//        runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "警告：" + warn, Toast.LENGTH_SHORT).show());
	}

	private void showNetQuality(int upload, int download) {
	}


	@Override
	public void onError(final int err) {
		runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "发生错误的错误码：" + err, Toast.LENGTH_SHORT).show());

//        if (BuildConfig.DEBUG) {
//            runOnUiThread(() -> Toast.makeText(MeetingAudienceActivity.this, "发生错误的错误码：" + err, Toast.LENGTH_SHORT).show());
//        }
	}

	/**
	 *
	 * 声网回调 当远端视频属性发生变化的回调
	 * */
	@Override
	public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
		//远端视频流卡顿
		logAction("uid:" + uid + "   state:" + state + "   reason:" + reason + "   elapsed:" + elapsed);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (state == 2) {
					try {
						if (meetingJoin != null && uid == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
							Log.e(TAG, "onRemoteVideoStateChanged: 主持人的视频在正常解码了 isDocShow:" + isDocShow + "  isSplitView:" + isSplitView);
							if (isDocShow || isSplitView) {
								if (remoteBroadcasterSurfaceView == null) {
									remoteBroadcasterSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
									remoteBroadcasterSurfaceView.setZOrderOnTop(false);
									remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
									rtcEngine().setupRemoteVideo(new VideoCanvas(remoteBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
									stripSurfaceView(remoteBroadcasterSurfaceView);
									if (mVideoAdapter.isHaveChairMan()) {
										int chairManPosition = mVideoAdapter.getChairManPosition();
										AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
										if (audienceVideo != null) {
											audienceVideo.setSurfaceView(remoteAudienceSurfaceView);
											audienceVideo.setVideoStatus(0);
											mVideoAdapter.notifyDataSetChanged();
										}
									}
								}
								if (isSplitView) {
									SpliteViews(10);
								}
							} else {
								if (remoteBroadcasterSurfaceView == null) {
									remoteBroadcasterSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
									remoteBroadcasterSurfaceView.setZOrderOnTop(false);
									remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
									rtcEngine().setupRemoteVideo(new VideoCanvas(remoteBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
									stripSurfaceView(remoteBroadcasterSurfaceView);
									broadcasterLayout.removeAllViews();
									broadcasterLayout.addView(remoteBroadcasterSurfaceView);
									broadcasterLayout.setVisibility(View.VISIBLE);
								}
							}
							isHostCommeIn = true;
							broadcastTipsText.setVisibility(View.GONE);
						} else {
							if (mVideoAdapter != null && mVideoAdapter.getPositionById(uid) != -1) {
								Log.e(TAG, "run: onRemoteVideoStateChanged  其他参会人的视频正常在解码");
								AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getPositionById(uid));
								if (audienceVideo != null) {
									SurfaceView surfaceView = RtcEngine.CreateRendererView(getApplicationContext());
									surfaceView.setZOrderOnTop(true);
									surfaceView.setZOrderMediaOverlay(true);
									audienceVideo.setVideoStatus(0);
									rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
									audienceVideo.setSurfaceView(surfaceView);
									mVideoAdapter.notifyItemChanged(mVideoAdapter.getPositionById(uid));
								}

							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (state == 4) {//远端视频流播放失败
					if (uid == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
						int positionById = mVideoAdapter.getPositionById(uid);
						if (positionById != -1) {
							mVideoAdapter.getAudienceVideoLists().get(positionById).setVideoStatus(2);
							mVideoAdapter.notifyItemChanged(positionById);
						} else if (uid == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
							remoteBroadcasterSurfaceView = null;
							broadcasterLayout.removeAllViews();
							broadcastTipsText.setText("主持人的视频播放失败了");
							broadcastTipsText.setVisibility(View.VISIBLE);
						}
					}
				} else if (state == 3) {//远端视频流卡顿
					int positionById = mVideoAdapter.getPositionById(uid);
					if (positionById != -1) {
						mVideoAdapter.getAudienceVideoLists().get(positionById).setVideoStatus(1);
						mVideoAdapter.notifyItemChanged(positionById);
					} else if (uid == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
						broadcastTipsText.setText("主持人的视频卡顿了");
						broadcastTipsText.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}

	/**
	 * 声网回调 本地视频变化
	 * */
	@Override
	public void onLocalVideoStateChanged(int localVideoState, int error) {
		logAction("localVideoState: " + localVideoState + "   error:" + error);
		if (localVideoState == 3) {//本地视频启动失败
			Message message = new Message();
			message.what = 3;
			message.arg1 = error;
			showOperatorHandler.sendMessageDelayed(message, 1000);

		}
	}

	/**
	 * 本地网络延迟检测
	 * */
	@Override
	public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
		runOnUiThread(() -> {
			Drawable right = getResources().getDrawable(R.drawable.icon_network_a);
			if (mNetworkIcon != null) {
				if (stats.lastmileDelay > 0 && stats.lastmileDelay <= 50) {
					right = getResources().getDrawable(R.drawable.icon_network_a);
				} else if (stats.lastmileDelay > 50 && stats.lastmileDelay <= 100) {
					right = getResources().getDrawable(R.drawable.icon_network_b);
				} else if (stats.lastmileDelay > 100 && stats.lastmileDelay <= 200) {
					right = getResources().getDrawable(R.drawable.icon_network_c);
				} else if (stats.lastmileDelay > 200) {
					right = getResources().getDrawable(R.drawable.icon_network_d);
				}
				mNetworkIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
			}
			if (mNetWorkNumber != null) {
				mNetWorkNumber.setText(stats.lastmileDelay + "ms");
			}
		});

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_HOME == keyCode) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

	@Override
	public void onAttachedToWindow() {
		this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
		super.onAttachedToWindow();
	}

	private boolean isShowNetWorkDialog = true;

	@Override
	public void onBackPressed() {
		if (dialog == null || !dialog.isShowing()) {
			showDialog(1, "确定退出教室吗？", "取消", "确定", null);
		}
	}

	@Override
	protected void onDestroy() {
		JPushInterface.resumePush(getApplicationContext());
		super.onDestroy();


		if (mSignalDialog != null) {
			mSignalDialog.dismiss();
		}

		if (mConnectionLostDialog != null) {
			mConnectionLostDialog.dismiss();
		}

		unregisterReceiver(homeKeyEventReceiver);

		subscription.unsubscribe();
//        if (WSService.isOnline()) {
//            //当前状态在线,可切换离线
//            Log.i(TAG, "当前状态在线,可切换离线");
//            ZYAgent.onEvent(this, "离线按钮,当前在线,切换到离线");
//            RxBus.sendMessage(new SetUserChatEvent(false));
////                                            WSService.SOCKET_ONLINE =false;
////                                            setState(false);
//        } else {
//            ZYAgent.onEvent(this, "离线按钮,当前离线,无效操作");
//        }

		if (takePhotoTimer != null && takePhotoTimerTask != null) {
			takePhotoTimer.cancel();
			takePhotoTimerTask.cancel();
		}

		JzvdStd.releaseAllVideos();

//        BaseApplication.getInstance().deInitWorkerThread();
	}

	private void initFragment() {
		hideFragment = true;
		findViewById(R.id.chatContent).setVisibility(View.VISIBLE);
		findViewById(R.id.chatContent).setBackgroundColor(Color.parseColor("#cc000000"));
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.show(newInMeetingChatFragment);
		fragmentTransaction.commitAllowingStateLoss();

		findViewById(R.id.close).setVisibility(View.VISIBLE);
		findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideFragment();
			}
		});
	}

	private void hideFragment() {
		hideFragment = false;
		findViewById(R.id.chatContent).setVisibility(View.GONE);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.hide(newInMeetingChatFragment);
		fragmentTransaction.commitAllowingStateLoss();
		findViewById(R.id.close).setVisibility(View.GONE);
	}

	/**
	 * home键 多任务按键被按下的监听
	 * */
	private BroadcastReceiver homeKeyEventReceiver = new BroadcastReceiver() {
		String REASON = "reason";
		String HOMEKEY = "homekey";
		String RECENTAPPS = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action) || Intent.ACTION_SHUTDOWN.equals(action)) {
				String reason = intent.getStringExtra(REASON);
				if (TextUtils.equals(reason, HOMEKEY) || TextUtils.equals(reason, RECENTAPPS)) {
					intent = new Intent(MeetingAudienceActivity.this, MeetingAudienceActivity.class);
					PendingIntent pendingIntent = PendingIntent.getActivity(MeetingAudienceActivity.this, 0, intent, 0);

					NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					Notification notification = new NotificationCompat.Builder(MeetingAudienceActivity.this, "video")
							.setContentTitle("中幼在线")
							.setContentText("中幼在线正在使用摄像头")
							.setWhen(System.currentTimeMillis())
							.setSmallIcon(R.drawable.ic_launcher_logo)
							.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_logo))
							.setAutoCancel(false)
							.setContentIntent(pendingIntent)
							.build();
					notification.flags = Notification.FLAG_NO_CLEAR;
					manager.notify(1, notification);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						if (Settings.canDrawOverlays(MeetingAudienceActivity.this)) {
							showFloatingView();
						}
					} else {
						showFloatingView();
					}
				}
			}
		}
	};
	/**
	 * 悬浮窗相关代码没有要求 是我自己写的 不用管
	 * 显示悬浮窗
	 */
	private View floatView;
	SurfaceView floatSurfaceView;
	int floatAudienceId = -1;

	/*没有要求 我自己写的 不管*/
	private void showFloatingView() {

		if (Constant.videoType == 1) {
			floatSurfaceView = localSurfaceView;
			floatAudienceId = config().mUid;
		} else if (Constant.videoType == 2) {
			if (currentAudienceId == config().mUid && localSurfaceView != null) {
				floatSurfaceView = localSurfaceView;
				floatAudienceId = config().mUid;
			} else if (remoteBroadcasterSurfaceView != null) {
				floatSurfaceView = remoteBroadcasterSurfaceView;
				floatAudienceId = Integer.parseInt(meetingJoin.getHostUser().getClientUid());
			} else {
				for (AudienceVideo audienceVideoList : mVideoAdapter.getAudienceVideoLists()) {
					if (audienceVideoList.getSurfaceView() != null) {
						floatSurfaceView = audienceVideoList.getSurfaceView();
						floatAudienceId = audienceVideoList.getUid();
					}
				}
			}
		}

		if (floatSurfaceView == null && mCurrentAudienceVideo != null) {
			floatSurfaceView = mCurrentAudienceVideo.getSurfaceView();
			floatAudienceId = mCurrentAudienceVideo.getUid();
		}

		if (floatSurfaceView == null) {
			return;
		}

		if (FloatWindow.get("audience") == null && floatSurfaceView != null) {
			FrameLayout relativeLayout = floatView.findViewById(R.id.floatView);
			relativeLayout.removeAllViews();
			stripSurfaceView(floatSurfaceView);
			relativeLayout.addView(floatSurfaceView);
			FloatWindow
					.with(getApplicationContext())
					.setView(floatView)
					.setWidth(AutoSizeUtils.pt2px(MeetingAudienceActivity.this, 120)) //设置悬浮控件宽高
					.setHeight(AutoSizeUtils.pt2px(MeetingAudienceActivity.this, 150))
					.setX(Screen.width, 0)
					.setY(Screen.height, 0)
					.setMoveType(MoveType.slide, 0, 0)
					.setMoveStyle(500, new BounceInterpolator())
					.setFilter(false, BaseActivity.class, com.jess.arms.base.BaseActivity.class, BaseMVVMActivity.class)
					.setDesktopShow(true)
					.setTag("audience")
					.build();
			FloatWindow.get("audience").show();
		} else if (FloatWindow.get("audience") != null && floatSurfaceView != null) {
			if (!FloatWindow.get("audience").isShowing()) {
				FrameLayout relativeLayout = floatView.findViewById(R.id.floatView);
				relativeLayout.removeAllViews();
				stripSurfaceView(floatSurfaceView);
				relativeLayout.addView(floatSurfaceView);
				FloatWindow.get("audience").show();
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.canDrawOverlays(MeetingAudienceActivity.this)) {
				recoverNormalWindow();
			}
		} else {
			recoverNormalWindow();
		}

	}

	/**
	 * todo 没有要求 是我自己写的 不管
	 * app重新回到前台 将悬浮窗的视频重新加入的正常的布局
	 * */
	public void recoverNormalWindow() {
		if (floatSurfaceView == null || floatAudienceId == -1) {
			return;
		}
		if (isSplitView) {
			int positionById = mVideoAdapter.getPositionById(floatAudienceId);
			if (positionById != -1) {
				AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
				if (audienceVideo != null) {
					audienceVideo.setSurfaceView(floatSurfaceView);
					mVideoAdapter.notifyItemChanged(positionById);
				}
			}
		} else if (isDocShow) {
			if (model == 1) {
				int positionById = mVideoAdapter.getPositionById(floatAudienceId);
				if (positionById != -1) {
					AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
					if (audienceVideo != null) {
						audienceVideo.setSurfaceView(floatSurfaceView);
						mVideoAdapter.notifyItemChanged(positionById);
					}
				}
			} else if (model == 2) {
				if (localSurfaceView == null) {
					return;
				}
				localAudienceFrameView.removeAllViews();
				localAudienceFrameView.setVisibility(View.VISIBLE);
				floatSurfaceView.setVisibility(View.VISIBLE);

				floatSurfaceView.setZOrderOnTop(true);
				floatSurfaceView.setZOrderMediaOverlay(true);
				stripSurfaceView(floatSurfaceView);
				localAudienceFrameView.addView(floatSurfaceView);
			} else if (model == 3) {
				localAudienceFrameView.setVisibility(View.GONE);

				if (floatSurfaceView != null) {
					floatSurfaceView.setVisibility(View.GONE);
				}
			}
		} else {
			int positionById = mVideoAdapter.getPositionById(floatAudienceId);
			if (positionById != -1) {
				AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
				if (audienceVideo != null) {
					audienceVideo.setSurfaceView(floatSurfaceView);
					mVideoAdapter.notifyItemChanged(positionById);
				}
			} else if (floatAudienceId == Integer.parseInt(meetingJoin.getHostUser().getClientUid())) {
				broadcasterLayout.removeAllViews();
				stripSurfaceView(floatSurfaceView);
				broadcasterLayout.addView(floatSurfaceView);
			}
		}
	}


	/**
	 * 操作栏的隐藏和展示 本地视频的异常的展示
	 * */
	@SuppressLint("HandlerLeak")
	private Handler showOperatorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					findViewById(R.id.toolsbar).setVisibility(View.GONE);
					if (exitButton != null) {
						exitButton.setVisibility(View.GONE);
					}

					break;
				case 1:
					if (exitButton != null) {
						exitButton.setVisibility(View.VISIBLE);
					}
					findViewById(R.id.toolsbar).setVisibility(View.VISIBLE);
					showOperatorHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
					break;
				case 3:
					String message = "";
					switch (msg.arg1) {
						case 0:
							message = "本地视频状态正常";
							break;
						case 1:
							message = "出错原因不明";
							break;
						case 3:
							message = "没有权限启动本地视频采集设备";
							break;
						case 4:
							message = "本地视频采集设备正在使用中";
							break;
						case 5:
							message = "本地视频编码失败";
							break;
					}
					MessageDialog.show(MeetingAudienceActivity.this, "本地视频发生了问题", message, "退出").setOnOkButtonClickListener(new OnDialogButtonClickListener() {
						@Override
						public boolean onClick(BaseDialog baseDialog, View v) {
							finish();
							return false;
						}
					}).setCancelable(false);
					break;
			}
		}
	};

	/**
	 * 事件分发 触摸屏幕展示和隐藏底部工具栏
	 * 点击的是聊天框以为 就隐藏聊天框
	 * */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (showOperatorHandler.hasMessages(0)) {
					showOperatorHandler.removeMessages(0);
				}
				if (findViewById(R.id.toolsbar).getVisibility() == View.VISIBLE) {
					showOperatorHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
				} else if (findViewById(R.id.toolsbar).getVisibility() == View.GONE) {
					showOperatorHandler.sendEmptyMessage(1);
				}

				if (!mIsMaker) {
					int[] position = new int[2];
					findViewById(R.id.rl_content).getLocationInWindow(position);

					if (ev.getRawY() < position[1]) {
						hideFragment();
					}
				}


				break;

		}
		return super.dispatchTouchEvent(ev);
	}

	//分屏 x没有任何作用 就是为了展示是从那个方法进行调用的
	private void SpliteViews(int x) {
		logAction("SpliteViews:=" + x);
		isSplitView = true;
		//主持人在列表中 则将大的broadcasterView的视频加入到receclerview中去  将主持人移动到集合第一个去
		if (mVideoAdapter.isHaveChairMan()) {
			logAction("主持人再列表中");
			mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getChairManPosition()).setSurfaceView(remoteBroadcasterSurfaceView);
			mVideoAdapter.notifyDataSetChanged();
			if (mCurrentAudienceVideo != null) {
				broadcasterLayout.removeAllViews();
				stripSurfaceView(mCurrentAudienceVideo.getSurfaceView());
				mVideoAdapter.getAudienceVideoLists().add(mCurrentAudienceVideo);
				mVideoAdapter.notifyDataSetChanged();
				//将主持人移动到集合第一个
							/*int chairManPosition = audienceVideoAdapter.getChairManPosition();
							if (chairManPosition!=-1){
								audienceVideoAdapter.insertItem(0,audienceVideoAdapter.getAudienceVideoLists().get(chairManPosition));
								audienceVideoAdapter.removeItem(chairManPosition+1);
							}*/
				mCurrentAudienceVideo = null;
			}
		} else {
			logAction("主持人不再列表中");
			/*if (remoteBroadcasterSurfaceView == null) {
				return;
			}
			//将主持人加入到recyclerView中去
			if (mVideoAdapter.getChairManPosition() != -1) {
				return;
			}*/
			stripSurfaceView(remoteBroadcasterSurfaceView);
			AudienceVideo audienceVideo = new AudienceVideo();
			audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
			audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
			audienceVideo.setBroadcaster(true);
			audienceVideo.setSurfaceView(remoteBroadcasterSurfaceView);
			mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
			mVideoAdapter.notifyDataSetChanged();
			broadcasterLayout.removeAllViews();

		}

		localAudienceFrameView.removeAllViews();
		localAudienceFrameView.setVisibility(View.GONE);

		if (currentMaterial == null) {
			//将recyclerview变成全屏布局
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAudienceRecyclerView.getLayoutParams();
			mAudienceRecyclerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			layoutParams.setMargins(0, 0, 0, 0);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);


			findViewById(R.id.relative).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) findViewById(R.id.relative).getLayoutParams();
			layoutParams1.setMargins(0, 0, 0, 0);
			findViewById(R.id.relative).setLayoutParams(layoutParams1);

			layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
			layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
			mAudienceRecyclerView.setLayoutParams(layoutParams);
			mSizeUtils.setViewMatchParent(mAudienceRecyclerView);
		} else {
			//将recyclerview变成固定大小
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 240), RelativeLayout.LayoutParams.MATCH_PARENT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			layoutParams.setMargins(0, DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 10), DisplayUtil.dip2px(this, 60));
			mAudienceRecyclerView.setLayoutParams(layoutParams);

			RelativeLayout relative = findViewById(R.id.relative);

			FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) relative.getLayoutParams();
			layoutParams1.setMargins(0, 0, 0, 0);
			relative.setLayoutParams(layoutParams1);
		}
		changeViewLayout(x);
	}

	private GridSpaceItemDecoration mGridSpaceItemDecoration;

	/**
	 * 分屏 展示不同的情况 具体的看需求文档
	 * */
	private void changeViewLayout(int x) {
		findViewById(R.id.relative).setBackground(getResources().getDrawable(R.drawable.bj));
		int dataSize = mVideoAdapter.getDataSize();
		logAction("集合大小: " + dataSize);
		logAction(currentMaterial == null);

		if (dataSize == 1) {
			if (currentMaterial == null) {
				//如果不是分屏模式 需要判断主持人是否在列表中
				if (mVideoAdapter.isHaveChairMan()) {
					//主持人在列表中 删除主持人
					int chairManPosition = mVideoAdapter.getChairManPosition();
					if (chairManPosition != -1) {
						mVideoAdapter.removeItem(chairManPosition);
					}
				}
				//将主持人加入到大的view中
				broadcasterLayout.removeAllViews();
				broadcasterLayout.setVisibility(View.VISIBLE);
				if (remoteBroadcasterSurfaceView != null) {
					stripSurfaceView(remoteBroadcasterSurfaceView);
					remoteBroadcasterSurfaceView.setZOrderOnTop(false);
					remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
					broadcasterLayout.addView(remoteBroadcasterSurfaceView);
				}
			}
		} else {

			mDelegateAdapter.clear();
			if (currentMaterial == null) {
				mAudienceRecyclerView.removeItemDecoration(mDecor);
				SpaceItemDecoration zeroSpanceItemDecoraton = new SpaceItemDecoration(0, 0, 0, 0);
				mAudienceRecyclerView.addItemDecoration(zeroSpanceItemDecoraton);

				if (mVideoAdapter.getDataSize() == 3) {
					mAudienceRecyclerView.removeItemDecoration(zeroSpanceItemDecoraton);
					mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
					mAudienceRecyclerView.addItemDecoration(mGridSpaceItemDecoration);
				} else {
					mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
				}

				if (dataSize == 4 || dataSize == 6) {
					SpliteGridLayoutHelper gridLayoutHelper = new SpliteGridLayoutHelper(2);
					mVideoAdapter.setLayoutHelper(gridLayoutHelper);
					mVideoAdapter.notifyDataSetChanged();
				} else {
					StaggeredGridLayoutHelper staggeredGridLayoutHelper = new StaggeredGridLayoutHelper(dataSize < 7 ? 2 : 4, this);
					mVideoAdapter.setLayoutHelper(staggeredGridLayoutHelper);
					mVideoAdapter.notifyDataSetChanged();
				}


			} else {
				MyGridLayoutHelper mGridLayoutHelper = new MyGridLayoutHelper(2);
				mGridLayoutHelper.setItemCount(8);
				mGridLayoutHelper.setGap(10);
				mGridLayoutHelper.setAutoExpand(false);
				mVideoAdapter.setLayoutHelper(mGridLayoutHelper);
				mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
				mAudienceRecyclerView.removeItemDecoration(mDecor);
				mAudienceRecyclerView.addItemDecoration(mDecor);
			}
			mVideoAdapter.notifyDataSetChanged();
			mDelegateAdapter.addAdapter(mVideoAdapter);
		}
	}


	/**
	 * 退出分屏
	 * */
	private void exitSpliteMode() {
		findViewById(R.id.relative).setBackground(null);
		//将主持人拿出来
		int chairManPosition = mVideoAdapter.getChairManPosition();
		//如果主持人 在的话
		if (chairManPosition != -1) {
			//将主持人添加到大的画面上
			if (remoteBroadcasterSurfaceView != null) {
				stripSurfaceView(remoteBroadcasterSurfaceView);
				broadcasterLayout.removeAllViews();
				remoteBroadcasterSurfaceView.setZOrderMediaOverlay(false);
				remoteBroadcasterSurfaceView.setZOrderOnTop(false);
				broadcasterLayout.setVisibility(View.VISIBLE);

				remoteBroadcasterSurfaceView.setVisibility(View.VISIBLE);
				mVideoAdapter.removeChairman(chairManPosition);
				broadcasterLayout.addView(remoteBroadcasterSurfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			} else {
				logAction("remoteBroadcasterSurfaceView == null");
				mVideoAdapter.removeChairman(chairManPosition);
			}

		}
		if (mVideoAdapter.getPositionById(config().mUid) == -1) {//自己不在列表中
			if (Constant.videoType == 1 || currentAudienceId == config().mUid) {
				AudienceVideo audienceVideo = new AudienceVideo();
				audienceVideo.setUid(config().mUid);
				audienceVideo.setName("参会人" + meetingJoin.getHostUser().getHostUserName());
				audienceVideo.setBroadcaster(false);
				audienceVideo.setSurfaceView(localSurfaceView);
				mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
				mVideoAdapter.notifyDataSetChanged();
			}
		}
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 240), RelativeLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		layoutParams.setMargins(0, DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 10), DisplayUtil.dip2px(this, 60));
		mAudienceRecyclerView.setLayoutParams(layoutParams);

		RelativeLayout relative = findViewById(R.id.relative);

		FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) relative.getLayoutParams();
		layoutParams1.setMargins(0, 0, 0, 0);
		relative.setLayoutParams(layoutParams1);

		mAudienceRecyclerView.removeItemDecoration(mDecor);
		mAudienceRecyclerView.addItemDecoration(mDecor);
		mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);

		mDelegateAdapter.clear();
		MyGridLayoutHelper helper = new MyGridLayoutHelper(2);
		helper.setAutoExpand(false);
		helper.setGap(10);
		helper.setItemCount(8);
		mVideoAdapter.setLayoutHelper(helper);
		mVideoAdapter.notifyDataSetChanged();
		mDelegateAdapter.addAdapter(mVideoAdapter);


		if (currentMaterial != null) {
			broadcasterLayout.setVisibility(View.GONE);
		} else {
			broadcasterLayout.setVisibility(View.VISIBLE);
		}
		isSplitView = false;

	}

	/**
	 * 非全屏状态，画面背景为ppt，主持人 参会人悬浮在ppt内容上
	 * <p>
	 * **需要判断集合大小，先将主持人加入到集合中 再判断  如果大于8人  就不显示自己  如果小于8人，所以的都显示
	 */
	private void notFullScreenState() {
		try {
			if (model == 1) {
				return;
			}
			model = 1;
			fullScreenButton.setText("非全屏");
			if (mCurrentAudienceVideo != null) {
				mVideoAdapter.getAudienceVideoLists().add(mCurrentAudienceVideo);
				mCurrentAudienceVideo = null;

			}
			//主持人加入到列表中
			if (!mVideoAdapter.isHaveChairMan()) {
				AudienceVideo audienceVideo = new AudienceVideo();
				audienceVideo.setUid(Integer.parseInt(meetingJoin.getHostUser().getClientUid()));
				audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
				audienceVideo.setBroadcaster(true);
				audienceVideo.setSurfaceView(remoteBroadcasterSurfaceView);
				mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
				mVideoAdapter.notifyDataSetChanged();
			} else {
				int manPosition = mVideoAdapter.getChairManPosition();
				if (manPosition != -1) {
					mVideoAdapter.getAudienceVideoLists().get(manPosition).setSurfaceView(remoteBroadcasterSurfaceView);
					mVideoAdapter.notifyDataSetChanged();
				}

			}


			localAudienceFrameView.removeAllViews();
			localAudienceFrameView.setVisibility(View.GONE);

			//如果自己不再列表中 将自己也加入到列表中

			if (mVideoAdapter.getPositionById(config().mUid) == -1) {
				//判断当前连麦人是否是自己或者是否是参会人
				if (Constant.videoType == 1 || currentAudienceId == config().mUid) {
					if (localSurfaceView == null) {
						return;
					}
					localSurfaceView.setVisibility(View.VISIBLE);
					AudienceVideo audienceVideo = new AudienceVideo();
					audienceVideo.setUid(config().mUid);
					audienceVideo.setName("参会人" + config().mUid);
					audienceVideo.setBroadcaster(false);
					audienceVideo.setSurfaceView(localSurfaceView);
					mVideoAdapter.insertItem(audienceVideo);
				}
			}

			logAction("当前播放ppt  集合大小是:" + mVideoAdapter.getDataSize());
			if (mVideoAdapter.getDataSize() > 8) {
				int myPosition = mVideoAdapter.getPositionById(config().mUid);
				if (mVideoAdapter.getChairManPosition() != -1) {
					mVideoAdapter.getAudienceVideoLists().get(myPosition).getSurfaceView().setVisibility(View.GONE);
					mVideoAdapter.removeItem(mVideoAdapter.getChairManPosition());
				}
			}

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 240), RelativeLayout.LayoutParams.MATCH_PARENT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			layoutParams.setMargins(0, DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 10), DisplayUtil.dip2px(this, 60));
			mAudienceRecyclerView.setLayoutParams(layoutParams);

			RelativeLayout relative = findViewById(R.id.relative);

			FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) relative.getLayoutParams();
			layoutParams1.setMargins(0, 0, 0, 0);
			relative.setLayoutParams(layoutParams1);
			relative.setBackground(null);

			mDelegateAdapter.clear();

			mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
			mAudienceRecyclerView.removeItemDecoration(mDecor);
			mAudienceRecyclerView.addItemDecoration(mDecor);

			mVideoAdapter.setItemSize(DisplayUtil.dip2px(this, 70), DisplayUtil.dip2px(this, 114));
			mVideoAdapter.notifyDataSetChanged();

			MyGridLayoutHelper helper = new MyGridLayoutHelper(2);
			helper.setAutoExpand(false);
			helper.setVGap(10);
			helper.setHGap(10);
			helper.setItemCount(8);

			mVideoAdapter.setLayoutHelper(helper);

			mDelegateAdapter.addAdapter(mVideoAdapter);
			mVideoAdapter.notifyDataSetChanged();

			mVideoAdapter.setVisibility(View.VISIBLE);
			mAudienceRecyclerView.setVisibility(View.VISIBLE);

			broadcasterLayout.setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 全屏状态：画面背景为PPT内容，右下角悬浮自己的画面 悬浮画面可以拖动
	 */
	private void FullScreenState() {
		if (model == 2) {
			return;
		}
		model = 2;
		fullScreenButton.setText("全屏");
		//将自己的画面从列表中移除 然后悬浮
		mAudienceRecyclerView.setVisibility(View.GONE);
		mVideoAdapter.setVisibility(View.GONE);

		if (currentAudienceId != config().mUid && Constant.videoType != 1) {
			return;
		}


		int currentAudiencePosition = mVideoAdapter.getPositionById(config().mUid);
		logAction(currentAudiencePosition + "-----" + config().mUid);
		if (currentAudiencePosition != -1) {
			mVideoAdapter.deleteItem(config().mUid);

		}

		logAction(localSurfaceView == null ? "localSurfaceView == null" : "localSurfaceView != null");
		if (localSurfaceView == null) {
			return;
		}


		localAudienceFrameView.removeAllViews();
		localAudienceFrameView.setVisibility(View.VISIBLE);
		localSurfaceView.setVisibility(View.VISIBLE);

		localSurfaceView.setZOrderOnTop(true);
		localSurfaceView.setZOrderMediaOverlay(true);
		stripSurfaceView(localSurfaceView);

		localAudienceFrameView.addView(localSurfaceView);
	}

	/**
	 * 隐藏浮窗状态：画面只有PPT内容；
	 */
	private void clearAllState() {
		if (model == 3) {
			return;
		}
		model = 3;
		fullScreenButton.setText("隐藏浮窗");
		mVideoAdapter.setVisibility(View.GONE);
		mAudienceRecyclerView.setVisibility(View.GONE);

		localAudienceFrameView.setVisibility(View.GONE);
		if (localSurfaceView != null) {
			localSurfaceView.setVisibility(View.GONE);
		}
	}


	/**
	 * 播放视频
	 * */
	public void PlayVideo() {

		if (currentMaterial == null) {
			findViewById(R.id.app_video_box).setVisibility(View.GONE);
			return;
		}

		player = findViewById(R.id.video_view);

		docImage.setVisibility(View.GONE);
		findViewById(R.id.app_video_box).setVisibility(View.VISIBLE);
		/*if (player==null){
			player=findViewById(R.id.video_view);
		}*/
		player.setUp(currentMaterial.getMeetingMaterialsPublishList().get(0).getUrl(), "");
		player.getSeekBar().setVisibility(View.GONE);
		player.getCurrentTimeView().setVisibility(View.GONE);
		player.getTotalTimeView().setVisibility(View.GONE);
		player.getFullScreenView().setVisibility(View.GONE);

		player.startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				return;
			}
		});


		String imageUrl = ImageHelper.videoFrameUrl(currentMaterial.getMeetingMaterialsPublishList().get(0).getUrl()
				, AutoSizeUtils.dp2px(MeetingAudienceActivity.this, 300)
				, AutoSizeUtils.dp2px(MeetingAudienceActivity.this, 400));
		Glide.with(MeetingAudienceActivity.this).load(imageUrl)
				.error(R.drawable.item_forum_img_error)
				.skipMemoryCache(true)
				.placeholder(R.drawable.item_forum_img_loading)
				.into(player.thumbImageView);


	}

	private void stopPlayVideo() {
		if (player != null) {
			JzvdStd.releaseAllVideos();
		}

//		mPlayVideoText.setText("播放");

	}

	@Override
	protected void onStop() {
		JPushInterface.resumePush(getApplicationContext());
		super.onStop();
	}

	//change 2020/11/10

	/**
	 * 改变在线人数UI
	 * note: 1. 需要在线人数 只加不减
	 * @param groups 在线用户列表 如果为空显示历史人数
	 */
	@SuppressLint("SetTextI18n")
	private void changeRoomOnlineCountUI(List<ImUserInfo.DataBean.UsersBean> groups){
		if(meetingJoin == null || meetingJoin.getMeeting() == null) return;
	    //获取缓存记录
		String key = "room_group_" + meetingJoin.getMeeting().getId();
        Set<String> caches = MMKV.defaultMMKV().decodeStringSet(key);
        if(caches == null)
			caches = new HashSet<>();
        if(groups !=null){
        	for (ImUserInfo.DataBean.UsersBean item : groups){
	            caches.add(item.getId());
            }
        }
        MMKV.defaultMMKV().encode(key, caches);
        countText.setText("在线人数：" + caches.size());
	}

	/**
	 * 发送群组消息
	 */
	private void sendChatMsg(){
		if(newInMeetingChatFragment == null) return;
		EditText editChatMsg = findViewById(R.id.editChatMsg);
		if(editChatMsg == null) return;
		String msg = editChatMsg.getText().toString();
		//onSendToggleClick
		newInMeetingChatFragment.onSendToggleClick(editChatMsg, msg);
		//清空消息框
		editChatMsg.setText("");
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(editChatMsg.getWindowToken(), 0);
		}

		editChatMsg.clearFocus();
	}
}

package com.zhongyou.meet.mobile.ameeting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.SpliteGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.dou361.ijkplayer.widget.PlayerView;
import com.elvishew.xlog.XLog;
import com.jess.arms.utils.RxBus;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.NewAudienceVideoAdapter;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entities.Audience;
import com.zhongyou.meet.mobile.entities.AudienceVideo;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.Material;
import com.zhongyou.meet.mobile.entities.Materials;
import com.zhongyou.meet.mobile.entities.Meeting;
import com.zhongyou.meet.mobile.entities.MeetingJoinStats;
import com.zhongyou.meet.mobile.entities.MeetingMaterialsPublish;
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
import com.zhongyou.meet.mobile.view.FocusFixedLinearLayoutManager;
import com.zhongyou.meet.mobile.view.MyGridLayoutHelper;
import com.zhongyou.meet.mobile.view.PreviewPlayer;
import com.zhongyou.meet.mobile.view.SpaceItemDecoration;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.transformerstip.TransformersTip;
import cn.bingoogolapple.transformerstip.gravity.TipGravity;
import cn.jzvd.JzvdStd;
import es.dmoral.toasty.Toasty;
import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.ui.BaseActivity;
import io.agora.openlive.ui.MaterialAdapter;
import io.agora.openlive.ui.NewAudienceAdapter;
import io.agora.openlive.ui.NewInMeetingChatFragment;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.rong.message.GroupNotificationMessage;
import me.jessyan.autosize.utils.AutoSizeUtils;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;


public class MakerChairManActivity extends BaseActivity implements AGEventHandler {

//    private final static Logger LOG = LoggerFactory.getLogger(MeetingBroadcastActivity.class);

	private final String TAG = MakerChairManActivity.class.getSimpleName();
	private List<Audience> menberLists = new ArrayList<>();
	private MeetingJoin.DataBean meetingJoin;
	private Agora agora;
	private HashMap<Integer, AudienceVideo> audienceHashMap = new HashMap<Integer, AudienceVideo>();
	private ArrayList<AudienceVideo> audiences = new ArrayList<AudienceVideo>();
	private Material currentMaterial;
	private int position;

	private String channelName;
	private int memberCount;

	private boolean isMuted = false;

	private boolean isFullScreen = false, isPPTModel;

	private FrameLayout broadcasterLayout, broadcasterSmallLayout, broadcasterSmallView;
	private TextView broadcastNameText, broadcastTipsText, switchCamera, full_screen;
	private TextView previewButton, nextButton, exitDocButton;
	private ImageView exitButton;
	private AgoraAPIOnlySignal agoraAPI;
	private TextView audiencesButton, stopButton, disCussButton, docButton, muteButton;
	private ImageView docImage;
	private TextView pageText;
	private SurfaceView localBroadcasterSurfaceView, remoteAudienceSurfaceView;
	private LinearLayout docLayout;

	private AudienceVideo currentAudience, newAudience;
	private int currentAiducenceId;
	private Subscription subscription;
	private RelativeLayout rlContent;
	NewInMeetingChatFragment fragment;
	boolean hideFragment = false;
	private GridSpaceItemDecoration mGridSpaceItemDecoration;

	private static final String DOC_INFO = "doc_info";
	private static final String CALLING_AUDIENCE = "calling_audience";


	private RecyclerView mAudienceRecyclerView;
	private MyGridLayoutHelper mGridLayoutHelper;
	private VirtualLayoutManager mVirtualLayoutManager;
	private DelegateAdapter mDelegateAdapter;
	private com.elvishew.xlog.Logger mLogger;
	private NewAudienceVideoAdapter mVideoAdapter;
	private int model = 0;
	private String mResourceId;
	private String mSid;
	private String mRecordUid;
	private TextView mNetWorkView;
	private TextView mNetWorkNumber;
	private TextView mNetworkIcon;


	private com.adorkable.iosdialog.AlertDialog mConnectionLostDialog, mSignalDialog;


	@SuppressLint("HandlerLeak")
	private Handler showOperatorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					mOperaTools.setVisibility(View.GONE);
					/*findViewById(R.id.ll_bottom_bar).setVisibility(View.GONE);//视频播放底部操作栏
					if (isPlayComplete) {
						findViewById(R.id.app_video_status_text).setVisibility(View.VISIBLE);
					}*/
					if (mTransformersTipPop != null) {
						if (mTransformersTipPop.isShowing()) {
							mTransformersTipPop.dismissTip();
						}
					}
//					mPlayVideoText.setVisibility(View.GONE);
					break;
				case 1:
					mOperaTools.setVisibility(View.VISIBLE);

					//视频播放底部操作栏
					/*if (currentMaterial != null && !currentMaterial.isVideo()) {
						findViewById(R.id.ll_bottom_bar).setVisibility(View.VISIBLE);
					}*/


					showOperatorHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
					break;
				case 3:
					mNetWorkView.setVisibility(View.GONE);
					break;
				case 4:

				/*	if (llChat != null) {
						llChat.setVisibility(View.GONE);
					}*/
					break;


			}
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler connectingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (isConnecting) {
				Toast.makeText(MakerChairManActivity.this, "连麦超时，请稍后再试!", Toast.LENGTH_SHORT).show();

				isConnecting = false;

				agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);

				if (currentAudience != null) {
					currentAudience.setCallStatus(0);
					audienceHashMap.put(currentAudience.getUid(), currentAudience);
					updateAudienceList();

					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("finish", true);
						agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
						mLogger.e("发送了结束消息");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (newAudience != null) {
					newAudience.setCallStatus(0);
					audienceHashMap.put(newAudience.getUid(), newAudience);
					updateAudienceList();
					newAudience = null;
				} else {
					currentAudience = null;
				}
			}
		}
	};
	private AudienceVideo mCurrentAudienceVideo;
	private SurfaceView mAudienceVideoSurfaceView;
	private SizeUtils mSizeUtils;
	private TextView mSpilteView;
	private LinearLayout mOperaTools;
	private TransformersTip mTransformersTipPop;
	private View mRootView;
	private ImageView mPlayVideoText;
	private SpaceItemDecoration mDecor;
	private PlayerView mPlayerView;
	private PreviewPlayer mPreviewPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		mRootView = LayoutInflater.from(this).inflate(R.layout.activity_maker_chairman, null);
//		setContentView(mRootView);
		setContentView(R.layout.activity_maker_chairman);

		mSizeUtils = new SizeUtils(this);
		mDecor = new SpaceItemDecoration(10, 10, 0, 0);
		mGridSpaceItemDecoration = new GridSpaceItemDecoration(DisplayUtil.getHeight(this) / 4);

		subscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {


				if (o instanceof IMMessgeEvent) {
					IMMessgeEvent msg = (IMMessgeEvent) o;
					io.rong.imlib.model.Message imMessage = msg.getImMessage();
//					Logger.e(JSON.toJSONString(imMessage));
					if (imMessage.getContent() instanceof GroupNotificationMessage) {
						return;
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							/*if (!hideFragment) {
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
							}*/
						}
					});
				}

			}
		});


		mLogger = XLog.tag(TAG)
				.t()
				.st(2)
				.b()
				.build();


		mOperaTools = findViewById(R.id.operaTools);


	}

	private void exitSpliteMode() {
		findViewById(R.id.container).setBackground(null);
		//将主持人拿出来
		int chairManPosition = mVideoAdapter.getChairManPosition();
		//如果主持人 在的话
		if (chairManPosition != -1) {
			//将主持人添加到大的画面上
			mVideoAdapter.removeItem(chairManPosition);
			stripSurfaceView(localBroadcasterSurfaceView);
			broadcasterLayout.removeAllViews();
			localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
			localBroadcasterSurfaceView.setZOrderOnTop(false);
			broadcasterLayout.setVisibility(View.VISIBLE);
			broadcasterLayout.addView(localBroadcasterSurfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		} else {
			// TODO: 2019-11-26 主持人不在
		}
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 240), RelativeLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		layoutParams.setMargins(0, DisplayUtil.dip2px(this, 0), DisplayUtil.dip2px(this, 16), DisplayUtil.dip2px(this, 60));
		mAudienceRecyclerView.setLayoutParams(layoutParams);

		mAudienceRecyclerView.removeItemDecoration(mDecor);
		mAudienceRecyclerView.addItemDecoration(mDecor);
		mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);

		mDelegateAdapter.clear();
		MyGridLayoutHelper helper = new MyGridLayoutHelper(2);
		helper.setAutoExpand(false);
		helper.setItemCount(8);
		mVideoAdapter.setLayoutHelper(helper);
		mVideoAdapter.notifyDataSetChanged();
		mDelegateAdapter.addAdapter(mVideoAdapter);


		if (currentMaterial != null) {
			broadcasterLayout.setVisibility(View.GONE);
		} else {
			broadcasterLayout.setVisibility(View.VISIBLE);
		}

		if (mVideoAdapter.getDataSize() >= 1) {
			mAudienceRecyclerView.setVisibility(View.VISIBLE);
		} else {
			mAudienceRecyclerView.setVisibility(View.GONE);
		}
		mSpilteView.setText("均分模式");

	}

	private void changeViewLayout() {

		int dataSize = mVideoAdapter.getDataSize();
		mLogger.e("集合大小：%d", dataSize);
		mLogger.e(currentMaterial == null);


		if (dataSize == 1 || dataSize == 0) {
			exitSpliteMode();
			return;
		}


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
			GridLayoutHelper mGridLayoutHelper = new GridLayoutHelper(2);
			mGridLayoutHelper.setItemCount(8);
			mGridLayoutHelper.setAutoExpand(false);
			mAudienceRecyclerView.removeItemDecoration(mDecor);
			mAudienceRecyclerView.addItemDecoration(mDecor);
			mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
			mVideoAdapter.setLayoutHelper(mGridLayoutHelper);
		}

		mVideoAdapter.notifyDataSetChanged();
		mDelegateAdapter.addAdapter(mVideoAdapter);
	}


	private void setTextViewDrawableTop(TextView view, int drawable) {
		Drawable top = getResources().getDrawable(drawable);
		view.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
//        initFragment();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	private void stripSurfaceView(SurfaceView view) {
		if (view == null) {
			mLogger.e("view==null");
			return;
		}
		ViewParent parent = view.getParent();
		if (parent != null) {
			((FrameLayout) parent).removeView(view);
		}
	}

	private boolean isSplitMode = false;

	private int lastX, lastY;
	int count = 0;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void initUIandEvent() {
		event().addEventHandler(this);

		Intent intent = getIntent();
		agora = intent.getParcelableExtra("agora");
		meetingJoin = intent.getParcelableExtra("meeting");
		channelName = meetingJoin.getMeeting().getId();

		fragment = new NewInMeetingChatFragment();

		mNetWorkNumber = findViewById(R.id.netWorkNumber);
		mNetworkIcon = findViewById(R.id.networkIcon);

		Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
				.appendPath("conversation").appendPath("group")
				.appendQueryParameter("targetId", meetingJoin.getMeeting().getId()).build();
		fragment.setUri(uri);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.chatContent, fragment);
		fragmentTransaction.show(fragment);
		fragmentTransaction.commitAllowingStateLoss();


		broadcastTipsText = findViewById(R.id.broadcast_tips);
		rlContent = (RelativeLayout) findViewById(R.id.rl_content);
//		llSmallChat = findViewById(R.id.small_chat);

		broadcastNameText = findViewById(R.id.broadcaster);
		broadcastNameText.setText("主持人：" + meetingJoin.getHostUser().getHostUserName());
		broadcasterLayout = findViewById(R.id.broadcaster_view);
		mSpilteView = findViewById(R.id.spliteView);
		full_screen = findViewById(R.id.full_screen);
		mPreviewPlayer = findViewById(R.id.video_view);
		mPlayVideoText = mPreviewPlayer.startButton;
		mNetWorkView = findViewById(R.id.networkView);

		full_screen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//恢复  退出会议

				if (currentMaterial == null) {
					if (isFullScreen) {
						full_screen.setText("全屏");

						isFullScreen = false;
						audiencesButton.setVisibility(View.VISIBLE);
						if (currentAudience != null && currentAiducenceId != -1) {
							stopButton.setVisibility(View.VISIBLE);
						} else {
							stopButton.setVisibility(View.INVISIBLE);
						}
						disCussButton.setVisibility(View.VISIBLE);
						docButton.setVisibility(View.VISIBLE);
						muteButton.setVisibility(View.VISIBLE);
						mSpilteView.setVisibility(View.VISIBLE);
						switchCamera.setVisibility(View.VISIBLE);
						if (mVideoAdapter != null && mVideoAdapter.getDataSize() >= 1 && currentMaterial == null) {
							mAudienceRecyclerView.setVisibility(View.VISIBLE);
							mVideoAdapter.setVisibility(View.VISIBLE);
						} else {
							mAudienceRecyclerView.setVisibility(View.GONE);
							mVideoAdapter.setVisibility(View.GONE);
						}
						if (badge != null) {
							badge.setBadgeNumber(audienceHashMap.size());
						}
						if (currentMaterial != null) {
							docLayout.setVisibility(View.VISIBLE);
						} else {
							docLayout.setVisibility(View.GONE);
						}
					} else {
						full_screen.setText("恢复");
						isFullScreen = true;
						audiencesButton.setVisibility(View.INVISIBLE);
						stopButton.setVisibility(View.INVISIBLE);
						disCussButton.setVisibility(View.INVISIBLE);
						docButton.setVisibility(View.INVISIBLE);
						muteButton.setVisibility(View.INVISIBLE);
						mSpilteView.setVisibility(View.INVISIBLE);
						switchCamera.setVisibility(View.INVISIBLE);
						mAudienceRecyclerView.setVisibility(View.GONE);
						mVideoAdapter.setVisibility(View.GONE);

						docLayout.setVisibility(View.GONE);

						if (badge != null) {
							badge.setBadgeNumber(0);
						}
					}
				} else {
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
		mSpilteView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mVideoAdapter.getDataSize() <= 0) {
					showToastyInfo("当前还没有参会人 不支持模式切换");
					return;
				}
				if (isSplitMode) {
					isSplitMode = false;
					/*mSpilteView.setText("均分模式");
					full_screen.setVisibility(View.VISIBLE);*/
//					exitSpliteMode();
					agoraAPI.channelSetAttr(channelName, Constant.MODEL_CHANGE, Constant.BIGSCREEN);
				} else {
					isSplitMode = true;
//					mSpilteView.setText("退出均分");
//					full_screen.setVisibility(View.GONE);
					agoraAPI.channelSetAttr(channelName, Constant.MODEL_CHANGE, Constant.EQUALLY);
//					SpliteViews();
				}
			}
		});

		broadcasterLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			/*	hideFragment();
				if (isFullScreen) {
					if (!tvContent.getText().toString().isEmpty())
						llMsg.setVisibility(View.GONE);

				} else {
//                    if(!tvChat.getText().toString().isEmpty())
//                    llChat.setVisibility(View.VISIBLE);
//					rtcEngine().switchCamera();
				}*/
			}
		});
		broadcasterSmallLayout = findViewById(R.id.broadcaster_small_layout);
		broadcasterSmallView = findViewById(R.id.broadcaster_small_view);
		switchCamera = findViewById(R.id.switch_camera);
		switchCamera.setOnClickListener(view -> {
			if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
				rtcEngine().enableLocalVideo(true);
				MMKV.defaultMMKV().encode(MMKVHelper.CAMERA_STATE, true);
				setTextViewDrawableTop(switchCamera, R.drawable.icon_switch_camera);
			} else {
				rtcEngine().switchCamera();
			}
			switchCamera.setText("切换摄像");
		});
		View mRlayout = findViewById(R.id.parentContainer);
		broadcasterSmallLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (count != 0) {
							lastX = (int) event.getRawX();
							lastY = (int) event.getRawY();
						}
						count++;
						break;
					case MotionEvent.ACTION_MOVE:
						int dx = (int) event.getRawX() - lastX;
						int dy = (int) event.getRawY() - lastY;

//						mLogger.e("dx="+dx+"   event.getRawX()"+event.getRawX()+"   lastX="+lastX);
//						mLogger.e("dy="+dy+"   event.getRawY()"+event.getRawY()+"   lastY="+lastY);
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
//						mLogger.e("left="+l+"   top="+t+"    right="+r+"   bottom="+b);
						v.setLayoutParams(layoutParams);

						lastX = (int) event.getRawX();
						lastY = (int) event.getRawY();
						v.postInvalidate();
						break;
					case MotionEvent.ACTION_UP:
						break;
				}
				return true;
			}
		});


		mAudienceRecyclerView = findViewById(R.id.audience_list);


		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 240), RelativeLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		layoutParams.setMargins(0, DisplayUtil.dip2px(this, 0), DisplayUtil.dip2px(this, 16), DisplayUtil.dip2px(this, 60));
		mAudienceRecyclerView.setLayoutParams(layoutParams);

		mGridLayoutHelper = new MyGridLayoutHelper(2);
		mGridLayoutHelper.setItemCount(8);
		mGridLayoutHelper.setAutoExpand(false);
		mVirtualLayoutManager = new VirtualLayoutManager(this);
		mDelegateAdapter = new DelegateAdapter(mVirtualLayoutManager, false);
		RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
		mAudienceRecyclerView.setRecycledViewPool(viewPool);
		viewPool.setMaxRecycledViews(0, 10);
		mAudienceRecyclerView.setLayoutManager(mVirtualLayoutManager);

		mAudienceRecyclerView.removeItemDecoration(mDecor);
		mAudienceRecyclerView.addItemDecoration(mDecor);
		mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);

		mVideoAdapter = new NewAudienceVideoAdapter(this, mGridLayoutHelper);
		mVideoAdapter.setItemSize(DisplayUtil.dip2px(this, 70), DisplayUtil.dip2px(this, 114));
		mDelegateAdapter.addAdapter(mVideoAdapter);
		mAudienceRecyclerView.setAdapter(mDelegateAdapter);

		mVideoAdapter.setOnItemClickListener(new NewAudienceVideoAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(RecyclerView parent, View view, int position) {
				mLogger.e("on   onItemClick");

				if (isSplitMode || currentMaterial != null) {
					return;
				}
				if (mVideoAdapter.isHaveChairMan()) {
					//点击的如果是主持人
					if (mVideoAdapter.getAudienceVideoLists().get(position).isBroadcaster()) {
						if (mCurrentAudienceVideo != null) {
							mVideoAdapter.removeItem(position);
							mVideoAdapter.insertItem(position, mCurrentAudienceVideo);
							broadcasterLayout.removeAllViews();
							localBroadcasterSurfaceView.setZOrderOnTop(false);
							localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
							stripSurfaceView(localBroadcasterSurfaceView);
							broadcasterLayout.addView(localBroadcasterSurfaceView);
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
							audienceVideo.setSurfaceView(mAudienceVideoSurfaceView);
							int i = (int) mAudienceVideoSurfaceView.getTag();
							mVideoAdapter.removeItem(i);
							mVideoAdapter.insertItem(i, audienceVideo);

						}

					}
				}
				//将参会人的画面移到主持人界面
				broadcasterLayout.removeAllViews();
				mCurrentAudienceVideo = mVideoAdapter.getAudienceVideoLists().get(position);
				mAudienceVideoSurfaceView = mCurrentAudienceVideo.getSurfaceView();
				mAudienceVideoSurfaceView.setTag(position);

				mVideoAdapter.removeItem(position);
				stripSurfaceView(mAudienceVideoSurfaceView);
				mAudienceVideoSurfaceView.setZOrderOnTop(false);
				mAudienceVideoSurfaceView.setZOrderMediaOverlay(false);
				broadcasterLayout.addView(mAudienceVideoSurfaceView);


				stripSurfaceView(localBroadcasterSurfaceView);
				//主持人画面 加入到列表中
				AudienceVideo audienceVideo = new AudienceVideo();
				audienceVideo.setUid(config().mUid);
				Logger.e(config().mUid + "");
				audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
				audienceVideo.setBroadcaster(true);
				audienceVideo.setSurfaceView(localBroadcasterSurfaceView);
				mVideoAdapter.insetChairMan(position, audienceVideo);
			}
		});

		docLayout = findViewById(R.id.doc_layout);
		/*llMsg = findViewById(R.id.ll_msg);
		llChat = findViewById(R.id.ll_chat);

		tvChatAddress = findViewById(R.id.tv_chat_address);
		tvChatName = findViewById(R.id.tv_chat_name);
		tvName = findViewById(R.id.tv_name);
		tvAddress = findViewById(R.id.tv_addres);
		tvContent = findViewById(R.id.tv_content);
		tvOpenComment = findViewById(R.id.open_comment);*/


		docImage = findViewById(R.id.doc_image);
		pageText = findViewById(R.id.page);

		previewButton = findViewById(R.id.preview);
		disCussButton = findViewById(R.id.discuss);

		previewButton.setOnClickListener(view -> {
			if (currentMaterial != null) {
				if (position > 0) {
					position--;
					stopPlayVideo();
					MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
					if (currentMaterialPublish.getType().equals("1")) {
						PlayVideo();
//						mPlayVideoText.setback
//						setTextViewDrawableTop(mPlayVideoText, R.drawable.icon_play);
					} else {
						findViewById(R.id.app_video_box).setVisibility(View.GONE);
//						mPlayVideoText.setVisibility(View.GONE);
						docImage.setVisibility(View.VISIBLE);
						String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());

						Glide.with(MakerChairManActivity.this).load(imageUrl)
								.placeholder(docImage.getDrawable())
								.error(R.drawable.rc_image_error)
								.into(docImage);

						pageText.setVisibility(View.VISIBLE);
					}

					pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("material_id", currentMaterial.getId());
						jsonObject.put("doc_index", position);
						agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
//                        agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(MakerChairManActivity.this, "当前是第一张了", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(MakerChairManActivity.this, "没找到ppt", Toast.LENGTH_SHORT).show();
			}
		});

		nextButton = findViewById(R.id.next);
		nextButton.setOnClickListener(view -> {
			if (currentMaterial != null) {
				if (position < (currentMaterial.getMeetingMaterialsPublishList().size() - 1)) {
					position++;
					stopPlayVideo();
					MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
					if (currentMaterialPublish.getType().equals("1")) {
						PlayVideo();
//						setTextViewDrawableTop(mPlayVideoText, R.drawable.icon_play);
					} else {
//						mPlayVideoText.setVisibility(View.GONE);
						findViewById(R.id.app_video_box).setVisibility(View.GONE);
						docImage.setVisibility(View.VISIBLE);
						String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());

						Glide.with(MakerChairManActivity.this).load(imageUrl)
								.placeholder(docImage.getDrawable())
								.error(R.drawable.rc_image_error)
								.into(docImage);
					}
					pageText.setVisibility(View.VISIBLE);
					pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("material_id", currentMaterial.getId());
						jsonObject.put("doc_index", position);
						agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
//                        agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
//					Toast.makeText(ChairManActivity.this, "当前是最后一张了", Toast.LENGTH_SHORT).show();
					showToastyWarn("当前是最后一张了");
				}
			} else {
				Toast.makeText(MakerChairManActivity.this, "没找到ppt", Toast.LENGTH_SHORT).show();
			}

		});

		exitDocButton = findViewById(R.id.exit_ppt);
		exitDocButton.setOnClickListener(view -> {
			mLogger.e("退出ppt");

			if (mPreviewPlayer != null) {
				JzvdStd.releaseAllVideos();
			}

			findViewById(R.id.app_video_box).setVisibility(View.GONE);
			isFullScreen = false;
            /*docImage.setVisibility(View.GONE);
            pageText.setVisibility(View.GONE);
            isFullScreen = false;
            llMsg.setVisibility(View.GONE);
//            if(!tvChat.getText().toString().isEmpty())
//            llChat.setVisibility(View.VISIBLE);

            broadcasterSmallView.removeView(localBroadcasterSurfaceView);
            broadcasterSmallLayout.setVisibility(View.GONE);



            broadcasterLayout.setVisibility(View.VISIBLE);
            broadcasterLayout.removeAllViews();
            broadcasterLayout.addView(localBroadcasterSurfaceView);

            currentMaterial = null;*/

			docLayout.setVisibility(View.GONE);
			agoraAPI.channelDelAttr(channelName, DOC_INFO);
			agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);
			/*if (currentAudience == null) {
				if (currentMaterial == null) {
					fullScreenButton.setVisibility(View.VISIBLE);
				} else {
					fullScreenButton.setVisibility(View.GONE);
				}
			} else {
				fullScreenButton.setVisibility(View.VISIBLE);
			}*/
			position = 0;


		});

		docButton = findViewById(R.id.doc);
		docButton.setOnClickListener((view) -> {
			ApiClient.getInstance().meetingMaterials(TAG, new OkHttpCallback<Bucket<Materials>>() {
				@Override
				public void onSuccess(Bucket<Materials> materialsBucket) {
					showPPTListDialog(materialsBucket.getData().getPageData());

				}

				@Override
				public void onFailure(int errorCode, BaseException exception) {
					super.onFailure(errorCode, exception);
					Toast.makeText(MakerChairManActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

					if (isSplitMode && currentMaterial == null) {
						full_screen.setVisibility(View.GONE);
					} else {
						full_screen.setVisibility(View.VISIBLE);
					}
				}
			}, meetingJoin.getMeeting().getId());
		});

		muteButton = findViewById(R.id.mute_audio);

		if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
			isMuted = true;
			muteButton.setText("话筒关闭");
			setTextViewDrawableTop(muteButton, R.drawable.icon_unspeek);
			rtcEngine().enableLocalAudio(false);
		} else {
			isMuted = false;
			rtcEngine().enableLocalAudio(true);
			setTextViewDrawableTop(muteButton, R.drawable.icon_speek);
			muteButton.setText("话筒打开");
		}

		muteButton.setOnClickListener(v -> {


			mTransformersTipPop = new TransformersTip(v, R.layout.pop_audio_choose) {
				@Override
				protected void initView(View contentView) {
					//全员静音
					contentView.findViewById(R.id.close_all).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mTransformersTipPop.dismissTip();

							muteButton.setText("全员静音");
							agoraAPI.channelSetAttr(channelName, Constant.KEY_ClOSE_MIC, System.currentTimeMillis() + "");

						}
					});

					TextView audioButtonMySelf = contentView.findViewById(R.id.close_self);
					if (!isMuted) {
						audioButtonMySelf.setText("话筒打开");
					} else {
						audioButtonMySelf.setText("话筒关闭");
					}

					//关闭自己

					audioButtonMySelf.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mTransformersTipPop.dismissTip();

							if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
								rtcEngine().enableLocalAudio(true);
								isMuted = false;
								rtcEngine().enableLocalAudio(true);
								setTextViewDrawableTop(muteButton, R.drawable.icon_speek);
								muteButton.setText("话筒打开");
								MMKV.defaultMMKV().encode(MMKVHelper.MICROPHONE_STATE, true);
								return;
							}
							if (!isMuted) {
								isMuted = true;
								setTextViewDrawableTop(muteButton, R.drawable.icon_unspeek);
								muteButton.setText("话筒关闭");
							} else {
								isMuted = false;
								setTextViewDrawableTop(muteButton, R.drawable.icon_speek);
								muteButton.setText("话筒打开");

							}
							rtcEngine().muteLocalAudioStream(isMuted);

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

		});

		audiencesButton = findViewById(R.id.waiter);
		audiencesButton.setOnClickListener(view -> {
			if (audiences.size() > 0) {
				showAlertDialog();
			} else {
				/*agoraAPI.channelClearAttr(channelName);
				if (currentAiducenceId != 0) {
					stopButton.setVisibility(View.GONE);
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("finish", true);
						agoraAPI.messageInstantSend("" + currentAiducenceId, 0, jsonObject.toString(), "");
						mLogger.e("发送了结束消息");
					} catch (Exception e) {
						e.printStackTrace();

					}
				}*/
			}
		});

		exitButton = findViewById(R.id.exit);
		exitButton.setOnClickListener(view -> {
			showExitDialog();
		});

		stopButton = findViewById(R.id.stop_audience);
		stopButton.setOnClickListener(view -> {
			if (currentAudience != null) {
				mLogger.e("当前连麦人的信息：" + JSON.toJSONString(currentAudience.toString()));
				showDialog(3, "结束" + currentAudience.getName() + "的发言？", "取消", "确定", currentAudience);
			} else {
				Toast.makeText(this, "当前没有连麦的参会人", Toast.LENGTH_SHORT).show();
			}
		});

		config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));
//		rtcEngine().enableLastmileTest();


		if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
			rtcEngine().enableLocalVideo(false);
			setTextViewDrawableTop(switchCamera, R.drawable.icon_close_video);
			switchCamera.setText("打开摄像");
		} else {
			rtcEngine().enableLocalVideo(true);
			switchCamera.setText("切换摄像");
		}


		doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER);

		localBroadcasterSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
		rtcEngine().setupLocalVideo(new VideoCanvas(localBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
		localBroadcasterSurfaceView.setZOrderOnTop(false);
		localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
		broadcasterLayout.addView(localBroadcasterSurfaceView);
		worker().preview(true, localBroadcasterSurfaceView, config().mUid);

		broadcastTipsText.setVisibility(View.GONE);

		if ("true".equals(agora.getIsTest())) {
			worker().joinChannel(null, channelName, config().mUid);
		} else {
			worker().joinChannel(agora.getToken(), channelName, config().mUid);
		}


		mOperaTools.setVisibility(View.VISIBLE);
		showOperatorHandler.sendEmptyMessageDelayed(0, 5000);

		/*findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLogger.e("findViewById(R.id.container)");
				if (mOperaTools.getVisibility() == View.VISIBLE) {
					showOperatorHandler.sendEmptyMessageDelayed(0, 3000);
				} else if (mOperaTools.getVisibility() == View.GONE) {
					showOperatorHandler.sendEmptyMessage(1);
				}
			}
		});*/


		agoraAPI = AgoraAPIOnlySignal.getInstance(this, agora.getAppID());
		agoraAPI.callbackSet(new AgoraAPI.CallBack() {
			@Override
			public void onChannelLeaved(String channelID, int ecode) {
				super.onChannelLeaved(channelID, ecode);
			}

			@Override
			public void onLoginSuccess(int uid, int fd) {
				super.onLoginSuccess(uid, fd);
				mLogger.e("onLoginSuccess:" + uid);

				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						runOnUiThread(() -> Toast.makeText(MakerChairManActivity.this, "信令系统登录成功", Toast.LENGTH_SHORT).show());
					}
					agoraAPI.channelJoin(channelName);

					if (Constant.isNeedRecord) {
						Map<String, String> map = new HashMap<>();
						map.put("cname", meetingJoin.getMeeting().getId());
						map.put("uid", meetingJoin.getHostUser().getClientUid());
						map.put("clientRequest", "{}");
						ApiClient.getInstance().startRecordVideo(this, map, new OkHttpCallback<com.alibaba.fastjson.JSONObject>() {
							@Override
							public void onSuccess(com.alibaba.fastjson.JSONObject json) {
								mLogger.e(JSON.toJSONString(json));
								if (json.getInteger("errcode") != 0) {
									if (json.getInteger("errcode") == 40003) {
										Preferences.clear();
										MakerChairManActivity.this.startActivity(new Intent(MakerChairManActivity.this, WXEntryActivity.class).putExtra("isBadToken", true)
												.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
										doLeaveChannel();
										finish();
									} else {
										ToastUtils.showToast(json.getString("errmsg"));
									}


								} else {
									mResourceId = json.getJSONObject("data").getJSONObject("result").getString("resourceId");
									mSid = json.getJSONObject("data").getJSONObject("result").getString("sid");
									mRecordUid = json.getJSONObject("data").getJSONObject("result").getString("uid");
								}
							}

							@Override
							public void onFailure(int errorCode, BaseException exception) {
								super.onFailure(errorCode, exception);
								mLogger.e(exception.getMessage());
							}
						});
					}
				});

			}

			@Override
			public void onLoginFailed(final int ecode) {
				super.onLoginFailed(ecode);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(MakerChairManActivity.this, "信令系统登录失败" + ecode, Toast.LENGTH_SHORT).show();
					}
				});

				if ("true".equals(agora.getIsTest())) {
					agoraAPI.login2(agora.getAppID(), "" + config().mUid, "noneed_token", 0, "", 20, 30);
				} else {
					agoraAPI.login2(agora.getAppID(), "" + config().mUid, agora.getSignalingKey(), 0, "", 20, 30);
				}

			}

			@Override
			public void onReconnecting(int nretry) {
				super.onReconnecting(nretry);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MakerChairManActivity.this, "信令重连失败第" + nretry + "次", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onReconnected(int fd) {
				super.onReconnected(fd);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MakerChairManActivity.this, "信令系统重连成功", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onChannelJoined(String channelID) {
				super.onChannelJoined(channelID);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(MakerChairManActivity.this, "加入信令频道成功", Toast.LENGTH_SHORT).show();
					}
					agoraAPI.channelQueryUserNum(channelName);
				});
			}

			@Override
			public void onChannelJoinFailed(String channelID, int ecode) {
				super.onChannelJoinFailed(channelID, ecode);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(MakerChairManActivity.this, "加入信令频道失败", Toast.LENGTH_SHORT).show();
						updateAudienceList();
					}
				});
			}

			@Override
			public void onLogout(int ecode) {
				super.onLogout(ecode);
				runOnUiThread(() -> {
					if (ecode == 102) {
						if (mSignalDialog == null) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
								if (MakerChairManActivity.this.isDestroyed()) {
									return;
								}
							}
							if (MakerChairManActivity.this.isFinishing()) {
								return;
							}
							mSignalDialog = new com.adorkable.iosdialog.AlertDialog(MakerChairManActivity.this)
									.builder()
									.setTitle("登陆系统失败")
									.setMsg("系统登陆失败 请重新进入会议")
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

			@Override
			public void onChannelQueryUserNumResult(String channelID, int ecode, int num) {
				super.onChannelQueryUserNumResult(channelID, ecode, num);
				runOnUiThread(() -> {
					memberCount = num;
					Logger.e("当前用户数量为=:" + num);
				});
			}

			@Override
			public void onUserAttrResult(String account, String name, String value) {
				super.onUserAttrResult(account, name, value);

				mLogger.e("onUserAttrResult 获取到用户account   " + account + "  name的属性  " + name + "   的值为  " + value);
				int key = Integer.parseInt(account);
				if (audienceHashMap.containsKey(key) && !TextUtils.isEmpty(value) && audienceHashMap.get(key) != null) {
					audienceHashMap.get(key).setUname(value);
					audienceHashMap.get(key).setName(value);
				}
				if (name.equals("userName")) {
					Audience audience = new Audience();
					audience.setUid(Integer.parseInt(account));
					audience.setUname(value);
					menberLists.add(audience);
				}
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(MakerChairManActivity.this, "获取到用户" + account + "的属性" + name + "的值为" + value, Toast.LENGTH_SHORT).show();
					}
//					audienceNameText.setText(TextUtils.isEmpty(value) ? "" : value);
				});
			}


			@Override
			public void onChannelUserJoined(String account, int uid) {
				mLogger.e("onChannelUserJoined     account==%s,uid=%d", account, uid);
				super.onChannelUserJoined(account, uid);

				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(MakerChairManActivity.this, "参会人" + account + "进入信令频道", Toast.LENGTH_SHORT).show();
					}
					agoraAPI.getUserAttr(account, "userName");

					agoraAPI.channelQueryUserNum(channelName);
					if (currentAudience != null) { // 正在连麦
						agoraAPI.channelSetAttr(channelName, CALLING_AUDIENCE, "" + currentAudience.getUid());
					} else { // 没有正在连麦
						agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
					}
					if (currentMaterial != null) { //正在演示ppt
						try {
							if (BuildConfig.DEBUG) {
								Toast.makeText(MakerChairManActivity.this, "向参会人" + account + "发送ppt信息", Toast.LENGTH_SHORT).show();
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("material_id", currentMaterial.getId());
							jsonObject.put("doc_index", position);
							agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
//                            agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else { // 没有在演示ppt
						agoraAPI.channelDelAttr(channelName, DOC_INFO);
						if (BuildConfig.DEBUG) {
							Toast.makeText(MakerChairManActivity.this, "参会人" + account + "上来时主持人端没有ppt信息", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			@Override
			public void onChannelUserLeaved(String account, int uid) {
				super.onChannelUserLeaved(account, uid);

				mLogger.e(account + "退出信令频道" + "----uid:=" + uid);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(MakerChairManActivity.this, account + "退出信令频道" + uid, Toast.LENGTH_SHORT).show();
					}
					agoraAPI.channelQueryUserNum(channelName);
					AudienceVideo audience = audienceHashMap.remove(Integer.parseInt(account));
					updateAudienceList();
					if (menberLists.contains(account)) {
						menberLists.remove(account);
					}
					if (audience != null) {
						if (BuildConfig.DEBUG) {
							Toast.makeText(MakerChairManActivity.this, audience.getUname() + "退出信令频道", Toast.LENGTH_SHORT).show();
						}
					}

				});
			}


			@Override
			public void onMessageSendSuccess(String messageID) {
				super.onMessageSendSuccess(messageID);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MakerChairManActivity.this, messageID + "-发送成功", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onMessageSendError(String messageID, int ecode) {
				super.onMessageSendError(messageID, ecode);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(MakerChairManActivity.this, messageID + "-发送失败", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onMessageInstantReceive(final String account, final int uid, final String msg) {
				super.onMessageInstantReceive(account, uid, msg);
				mLogger.e("account==%s,uid==%d,msg==%s", account, uid, msg);
				runOnUiThread(() -> {
					try {
						JSONObject jsonObject = new JSONObject(msg);

						if (jsonObject.has("handsUp")) {
							AudienceVideo audience = JSON.parseObject(jsonObject.toString(), AudienceVideo.class);
							audience.setName(audience.getUname());
							audience.setUname(audience.getUname());
							if (jsonObject.has("isAudience") && jsonObject.getBoolean("isAudience") && audience.getCallStatus() == 2) {
								currentAudience = audience;

							}
							audienceHashMap.put(audience.getUid(), audience);
							updateAudienceList();

							try {
								int positionById = mVideoAdapter.getPositionById(Integer.parseInt(account));

								if (positionById != -1) {
									AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
									if (audienceVideo != null) {
										mVideoAdapter.getAudienceVideoLists().get(positionById).setName(audienceHashMap.get(audience.getUid()).getName());
										mVideoAdapter.notifyDataSetChanged();
									}

								}
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}

							if (audience.isHandsUp()) {
								Toast.makeText(MakerChairManActivity.this, audience.getUname() + "请求发言", Toast.LENGTH_SHORT).show();
							}
						}
						if (jsonObject.has("finish")) {
							boolean finish = jsonObject.getBoolean("finish");
							if (finish) {
								if (currentAudience != null && account.equals("" + currentAudience.getUid())) {
									stopButton.setVisibility(View.INVISIBLE);
/*
									audienceView.removeAllViews();
									audienceNameText.setText("");
									audienceLayout.setVisibility(View.GONE);*/

									remoteAudienceSurfaceView = null;

									agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
								}
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}

			@Override
			public void onChannelAttrUpdated(String channelID, String name, String value, String type) {
				super.onChannelAttrUpdated(channelID, name, value, type);
				mLogger.e("channelID:" + channelID + "---" + "name:" + name + "---" + "value:" + value + "---" + "type:" + type);
				runOnUiThread(() -> {
					if (CALLING_AUDIENCE.equals(name)) {
						if (TextUtils.isEmpty(value)) {
							if (currentAudience != null) {
								currentAudience.setCallStatus(0);
								currentAudience.setHandsUp(false);
								audienceHashMap.put(currentAudience.getUid(), currentAudience);
								updateAudienceList();
							}
							remoteAudienceSurfaceView = null;

							isConnecting = false;

							/*stopButton.setVisibility(View.GONE);
							audienceView.removeAllViews();
							audienceNameText.setText("");
							audienceLayout.setVisibility(View.GONE);*/
							/*if (currentMaterial != null) {
								fullScreenButton.setVisibility(View.VISIBLE);
							} else {
								fullScreenButton.setVisibility(View.GONE);
							}*/
						} else {
							currentAiducenceId = Integer.parseInt(value);
						}
					}

					if (DOC_INFO.equals(name) && type.equals("update")) {
						isPPTModel = true;
						nextButton.setVisibility(View.VISIBLE);
						previewButton.setVisibility(View.VISIBLE);
						exitDocButton.setVisibility(View.VISIBLE);
						if (currentMaterial == null) {
							position = JSON.parseObject(value).getInteger("doc_index");
							ApiClient.getInstance().meetingMaterial(TAG, meetingMaterialCallback, JSON.parseObject(value).getString("material_id"));
						} else {
							ApiClient.getInstance().meetingSetMaterial(TAG, setMaterialCallback, meetingJoin.getMeeting().getId(), JSON.parseObject(value).getString("material_id"));
						}
					}

					if (DOC_INFO.equals(name) && type.equals("del")) {
						mLogger.e("当前是退出ppt模式");
						isPPTModel = false;
						currentMaterial = null;
						exitPPT();
					}

					if (Constant.MODEL_CHANGE.equals(name)) {
						if (value.equals(Constant.BIGSCREEN)) {
							isSplitMode = false;
							if (isFullScreen) {
								mVideoAdapter.setVisibility(View.GONE);
								mAudienceRecyclerView.setVisibility(View.GONE);
							} else {
								mVideoAdapter.setVisibility(View.VISIBLE);
								mAudienceRecyclerView.setVisibility(View.VISIBLE);
							}
							if (!isPPTModel) {
								exitSpliteMode();
							}
							full_screen.setVisibility(View.VISIBLE);

							mSpilteView.setText("均分模式");

						} else if (value.equals(Constant.EQUALLY)) {
							isSplitMode = true;
							if (mVideoAdapter.getDataSize() >= 1) {
								if (!isPPTModel) {
									SpliteViews();
								}
								full_screen.setVisibility(View.GONE);
								mSpilteView.setText("退出均分");
							}
						}
					}


					if (TextUtils.isEmpty(name) && TextUtils.isEmpty(value) && type.equals("clear")) {

						mLogger.e("ppt退出时的集合大小为" + mVideoAdapter.getDataSize());
//						currentAiducenceId = 0;
//						currentAudience = null;
						isConnecting = false;
						isPPTModel = false;
						exitPPT();
					}


				});
			}

			@Override
			public void onError(final String name, final int ecode, final String desc) {
				super.onError(name, ecode, desc);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> {
						if (ecode != 208)
							Toast.makeText(MakerChairManActivity.this, "收到错误信息\nname: " + name + "\necode: " + ecode + "\ndesc: " + desc, Toast.LENGTH_SHORT).show();
					});
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
				Log.v("信令broadcast", txt);
			}
		});


	}

	private OkHttpCallback meetingMaterialCallback = new OkHttpCallback<Bucket<Material>>() {

		@Override
		public void onSuccess(Bucket<Material> materialBucket) {
			mLogger.e(JSON.toJSONString(materialBucket.toString()));
			changeViewByPPTModel(materialBucket.getData());
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(MakerChairManActivity.this, errorCode + "---" + exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	private void exitPPT() {
		currentMaterial = null;
		localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
		if (mVideoAdapter.getDataSize() > 0) {
			mAudienceRecyclerView.setVisibility(View.VISIBLE);
			mVideoAdapter.setVisibility(View.VISIBLE);
		}
		if (mVideoAdapter.isHaveChairMan() && !isSplitMode) {
			if (mCurrentAudienceVideo != null) {
				SurfaceView surfaceView = mCurrentAudienceVideo.getSurfaceView();
				stripSurfaceView(surfaceView);
				broadcasterLayout.removeAllViews();
				broadcasterLayout.addView(surfaceView);
			} else {
				mLogger.e("mCurrentAudienceVideo==null");
				int chairManPosition = mVideoAdapter.getChairManPosition();
				if (chairManPosition != -1) {
					mVideoAdapter.removeItem(chairManPosition);
				}
				localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
				localBroadcasterSurfaceView.setZOrderOnTop(false);
				localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
				broadcasterLayout.removeAllViews();
				stripSurfaceView(localBroadcasterSurfaceView);
				broadcasterLayout.addView(localBroadcasterSurfaceView);
				broadcasterLayout.setVisibility(View.VISIBLE);

			}
		}

		findViewById(R.id.app_video_box).setVisibility(View.GONE);

		//如果是分屏模式 清除掉ppt的时候 需要重新分屏
		mLogger.e("isSplitMode:   " + isSplitMode + "-----  model:  " + model + "--------   mVideoAdapter.getDataSize():  " + mVideoAdapter.getDataSize());
		if (isSplitMode) {
			//当是分屏模式时
			//如果mode==2 两人退出一个时 集合大小为0
			// 如果mode==1, 两人退出一个时  集合大小为1
			//如果 mode==3时 两人退出一个时 集合大小为1
			if (model == 1 || model == 3) {
				if (mVideoAdapter.getDataSize() == 1) {
					if (mVideoAdapter.isHaveChairMan()) {
						mVideoAdapter.removeItem(mVideoAdapter.getChairManPosition());
					}
					stripSurfaceView(localBroadcasterSurfaceView);
					broadcasterLayout.removeAllViews();
					localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
					broadcasterLayout.addView(localBroadcasterSurfaceView);
					broadcasterLayout.setVisibility(View.VISIBLE);
					broadcasterLayout.setVisibility(View.VISIBLE);
				} else {
					SpliteViews();
					broadcasterLayout.setVisibility(View.GONE);
					mSpilteView.setText("退出均分");
				}

			} else if (model == 2) {
				if (mVideoAdapter.getDataSize() <= 0) {//此时列表中没有参会人或者观众 就直接将主持人画面移动到大的视图
					if (mVideoAdapter.isHaveChairMan()) {
						mVideoAdapter.removeItem(mVideoAdapter.getChairManPosition());
					}
					stripSurfaceView(localBroadcasterSurfaceView);
					broadcasterLayout.removeAllViews();
					localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
					broadcasterLayout.addView(localBroadcasterSurfaceView);
					//broadcasterLayout.setVisibility(View.VISIBLE);
					broadcasterLayout.setVisibility(View.VISIBLE);

				} else {
					SpliteViews();
					broadcasterLayout.setVisibility(View.GONE);
//					if (currentMaterial == null) {
//						full_screen.setVisibility(View.GONE);
//					}
				}
			}


		} else {
			//如果列表中有主持人 则从列表中将主持人移除来
			if (mVideoAdapter.isHaveChairMan()) {
				int chairManPosition = mVideoAdapter.getChairManPosition();
				if (chairManPosition != -1) {
					mVideoAdapter.removeItem(chairManPosition);
				}
			}
			localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
			localBroadcasterSurfaceView.setZOrderOnTop(false);
			localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
			broadcasterLayout.removeAllViews();
			stripSurfaceView(localBroadcasterSurfaceView);
			broadcasterLayout.addView(localBroadcasterSurfaceView);
			broadcasterLayout.setVisibility(View.VISIBLE);


			full_screen.setVisibility(View.VISIBLE);
		}


		docImage.setVisibility(View.GONE);
		pageText.setVisibility(View.GONE);
		previewButton.setVisibility(View.GONE);
		nextButton.setVisibility(View.GONE);
		exitDocButton.setVisibility(View.GONE);
		broadcasterSmallView.removeView(localBroadcasterSurfaceView);
		broadcasterSmallLayout.setVisibility(View.GONE);
		mSpilteView.setVisibility(View.VISIBLE);
		model = 0;
		position = 0;
	}

	private void SpliteViews() {

		findViewById(R.id.container).setBackground(getResources().getDrawable(R.drawable.bj));

		mSpilteView.setText("退出均分");
		//主持人在列表中 则将大的broadcasterView的视频加入到receclerview中去  将主持人移动到集合第一个去
		if (mVideoAdapter.isHaveChairMan()) {
			mLogger.e("主持人再列表中");
			mVideoAdapter.getAudienceVideoLists().get(mVideoAdapter.getChairManPosition()).setSurfaceView(localBroadcasterSurfaceView);
			mVideoAdapter.notifyDataSetChanged();
			if (mCurrentAudienceVideo != null) {
				broadcasterLayout.removeAllViews();
				stripSurfaceView(mCurrentAudienceVideo.getSurfaceView());
				mVideoAdapter.insertItem(mCurrentAudienceVideo);
				//将主持人移动到集合第一个
							/*int chairManPosition = audienceVideoAdapter.getChairManPosition();
							if (chairManPosition!=-1){
								audienceVideoAdapter.insertItem(0,audienceVideoAdapter.getAudienceVideoLists().get(chairManPosition));
								audienceVideoAdapter.removeItem(chairManPosition+1);
							}*/
				mCurrentAudienceVideo = null;

			} else {
				ToastUtils.showToast("当前放大的视频丢失 不能均分视频");
			}
		} else {
			mLogger.e("主持人不再列表中");
			if (currentMaterial == null) {
				//将主持人加入到recyclerView中去
				stripSurfaceView(localBroadcasterSurfaceView);
				AudienceVideo audienceVideo = new AudienceVideo();
				audienceVideo.setUid(config().mUid);
				audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
				audienceVideo.setBroadcaster(true);
				audienceVideo.setSurfaceView(localBroadcasterSurfaceView);
				mVideoAdapter.insertItem(audienceVideo);
				broadcasterLayout.removeAllViews();
			}

		}

		//将recyclerview编程全屏布局
		if (mAudienceRecyclerView == null) {
			changeViewLayout();
			return;
		}
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAudienceRecyclerView.getLayoutParams();
		layoutParams.setMargins(0, 0, 0, 0);
		mAudienceRecyclerView.setLayoutParams(layoutParams);
		mSizeUtils.setViewMatchParent(mAudienceRecyclerView);
		changeViewLayout();
	}


	private Dialog dialog, exitDialog;

	private void showDialog(final int type, final String title, final String leftText, final String rightText, final AudienceVideo audience) {
		View view = View.inflate(this, R.layout.dialog_selector, null);
		TextView titleText = view.findViewById(R.id.title);
		titleText.setText(title);

		Button leftButton = view.findViewById(R.id.left);
		leftButton.setText(leftText);
		leftButton.setOnClickListener(view12 -> {
			dialog.cancel();
			if (type == 1) {
				if (currentAudience != null) {
					agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("finish", true);
						agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
						mLogger.e("使用connectingHandler发送了消息type==" + type);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (BuildConfig.DEBUG) {
						Toast.makeText(this, "当前没有连麦人", Toast.LENGTH_SHORT).show();
					}
					if (currentAiducenceId != 0) {
						stopButton.setVisibility(View.INVISIBLE);
						try {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("finish", true);
							agoraAPI.messageInstantSend("" + currentAiducenceId, 0, jsonObject.toString(), "");
							mLogger.e("使用connectingHandler发送了消息type==" + type);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("clientUid", "" + config().mUid);
				params.put("hostUserId", Preferences.getUserId());
				params.put("hostUserName", meetingJoin.getHostUser().getHostUserName());
				params.put("status", "2");
				ApiClient.getInstance().meetingLeaveTemp(TAG, params, meetingTempLeaveCallback, meetingJoin.getMeeting().getId());

				doLeaveChannel();
				if (agoraAPI.getStatus() == 2) {
					agoraAPI.logout();
				}
				finish();
			} else if (type == 2 || type == 5) {
				isConnecting = true;
				connectingHandler.sendEmptyMessageDelayed(0, 10000);
				currentAudience = audience;
				currentAudience.setCallStatus(1);
				currentAudience.setHandsUp(false);
				audienceHashMap.put(currentAudience.getUid(), currentAudience);
				mLogger.e("使用connectingHandler发送了消息type==" + type);
				updateAudienceList();
//				audienceNameText.setText(currentAudience.getUname());

				agoraAPI.channelSetAttr(channelName, CALLING_AUDIENCE, "" + currentAudience.getUid());

			}
		});

		Button rightButton = view.findViewById(R.id.right);
		rightButton.setText(rightText);
		rightButton.setOnClickListener(view1 -> {
			dialog.cancel();
			if (type == 1) {
				ApiClient.getInstance().finishMeeting(TAG, meetingJoin.getMeeting().getId(), memberCount, finishMeetingCallback);
			} else if (type == 2) {
				audience.setCallStatus(0);
				audience.setHandsUp(false);
				audienceHashMap.put(audience.getUid(), audience);
				updateAudienceList();
				stopButton.setVisibility(View.INVISIBLE);
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("response", false);
					agoraAPI.messageInstantSend("" + audience.getUid(), 0, jsonObject.toString(), "");
					mLogger.e("发送了response==fasle");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (type == 3) {
				audience.setCallStatus(0);
				audience.setHandsUp(false);
				audienceHashMap.put(audience.getUid(), audience);
				updateAudienceList();

				stopButton.setVisibility(View.INVISIBLE);

				/*if (currentMaterial != null) {
					fullScreenButton.setVisibility(View.VISIBLE);
				}*/

				/*audienceView.removeAllViews();
				audienceNameText.setText("");
				audienceLayout.setVisibility(View.GONE);*/

				currentAudience = null;
				agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);

				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("finish", true);
					agoraAPI.messageInstantSend("" + audience.getUid(), 0, jsonObject.toString(), "");
					mLogger.e("发送了结束消息type==3");
					//rtcEngine().muteRemoteAudioStream(audience.getUid(),true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (type == 4) {

				currentAudience = audience;//新增加的代码

				newAudience = audience;

				isConnecting = true;
				connectingHandler.sendEmptyMessageDelayed(0, 10000);
				mLogger.e("使用connectingHandler发送了消息type==4");
				if (currentAudience != null) {
					currentAudience.setCallStatus(0);
					currentAudience.setHandsUp(false);
					audienceHashMap.put(currentAudience.getUid(), currentAudience);

					newAudience.setCallStatus(1);
					newAudience.setHandsUp(false);
					audienceHashMap.put(newAudience.getUid(), newAudience);

					updateAudienceList();

					/*audienceView.removeAllViews();
					audienceNameText.setText("");
					audienceLayout.setVisibility(View.GONE);*/

					stopButton.setVisibility(View.INVISIBLE);

					agoraAPI.channelSetAttr(channelName, CALLING_AUDIENCE, "" + newAudience.getUid());
				}
			}
		});

		dialog = new Dialog(this, R.style.MyDialog);
		dialog.setContentView(view);
		dialog.show();
	}

	AlertDialog alertDialog, pptAlertDialog, pptDetailDialog;
	NewAudienceAdapter audienceAdapter;

	private NewAudienceAdapter.OnAudienceButtonClickListener listener = new NewAudienceAdapter.OnAudienceButtonClickListener() {
		@Override
		public void onTalkButtonClick(AudienceVideo audience) {
			mLogger.e(JSON.toJSONString(audience.toString()));
			if (isConnecting) {
				Toast.makeText(MakerChairManActivity.this, "暂时无法切换连麦，请10秒后尝试", Toast.LENGTH_SHORT).show();
			} else {
				if (currentAudience != null) {
					if (currentAudience.getCallStatus() == 2 && currentAudience.getUid() != audience.getUid()) {
						showDialog(4, "中断当前" + currentAudience.getUname() + "的连麦，连接" + audience.getUname() + "的连麦", "取消", "确定", audience);
					} else {
						Toast.makeText(MakerChairManActivity.this, "正在与当前参会人连麦中", Toast.LENGTH_SHORT).show();
					}
				} else {

					if (audience.getCallStatus() == 0) {
						if (audience.isHandsUp()) {
							showDialog(2, audience.getUname() + "请求连麦", "接受", "拒绝", audience);
						} else {
							showDialog(5, "确定与" + audience.getUname() + "连麦", "确定", "取消", audience);
						}
					} else {
						Toast.makeText(MakerChairManActivity.this, "正在与当前参会人连麦中", Toast.LENGTH_SHORT).show();
					}
				}
				alertDialog.cancel();
			}
		}
	};

	private void showExitDialog() {
		View contentView = View.inflate(this, R.layout.dialog_exit_meeting, null);
		TextView finishTips = contentView.findViewById(R.id.finish_meeting_tips);
		Button tempLeaveButton = contentView.findViewById(R.id.left);
		tempLeaveButton.setOnClickListener(view -> {
			if (currentAudience != null) {
				agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("finish", true);
					agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
					mLogger.e("发送了结束消息");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (BuildConfig.DEBUG) {
					Toast.makeText(MakerChairManActivity.this, "当前没有连麦人", Toast.LENGTH_SHORT).show();
				}
				if (currentAiducenceId != 0) {
					stopButton.setVisibility(View.INVISIBLE);
					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("finish", true);
						agoraAPI.messageInstantSend("" + currentAiducenceId, 0, jsonObject.toString(), "");
						mLogger.e("发送了结束消息");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			stopRecordVideo();
			agoraAPI.channelDelAttr(channelName, DOC_INFO);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("clientUid", "" + config().mUid);
			params.put("hostUserId", Preferences.getUserId());
			params.put("hostUserName", meetingJoin.getHostUser().getHostUserName());
			params.put("status", "2");
			ApiClient.getInstance().meetingLeaveTemp(TAG, params, meetingTempLeaveCallback, meetingJoin.getMeeting().getId());

			doLeaveChannel();
			if (agoraAPI.getStatus() == 2) {
				agoraAPI.logout();
			}
			if (exitDialog.isShowing()) {
				exitDialog.dismiss();
			}
			finish();
		});
		Button finishMeetingButton = contentView.findViewById(R.id.right);
		finishMeetingButton.setOnClickListener(view -> {
			stopRecordVideo();
			if (finishTips.getVisibility() == View.VISIBLE) {
				ApiClient.getInstance().finishMeeting(TAG, meetingJoin.getMeeting().getId(), memberCount, finishMeetingCallback);
				exitDialog.cancel();
			} else {
				finishTips.setVisibility(View.VISIBLE);
			}
			agoraAPI.channelDelAttr(channelName, DOC_INFO);
		});
		exitDialog = new Dialog(this, R.style.MyDialog);
		exitDialog.setContentView(contentView);

		if (!exitDialog.isShowing()) {
			exitDialog.show();
		}
	}

	private TextView audienceCountText;
	private EditText searchEdit;
	private Button searchButton;
	private boolean isConnecting = false;

	private void showAlertDialog() {
		View view = View.inflate(this, R.layout.dialog_audience_list, null);
		audienceCountText = view.findViewById(R.id.audience_count);
		audienceCountText.setText("所有参会人 (" + audiences.size() + ")");
		searchEdit = view.findViewById(R.id.search_edit);
		searchButton = view.findViewById(R.id.search_button);
		searchButton.setOnClickListener((view1) -> {
			if (TextUtils.isEmpty(searchEdit.getText())) {
				Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
			} else {
				if (audienceAdapter != null) {
					audienceAdapter.setData(searchAudiences(audiences, searchEdit.getText().toString()));
				}
			}
		});
		ListView listView = view.findViewById(R.id.list_view);
		if (audienceAdapter == null) {
			audienceAdapter = new NewAudienceAdapter(this, audiences, listener);
		} else {
			audienceAdapter.setData(audiences);
		}
		listView.setAdapter(audienceAdapter);
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
		builder.setView(view);
		alertDialog = builder.create();

		Window dialogWindow = alertDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = 1000;
		lp.height = 600;
		dialogWindow.setAttributes(lp);

		alertDialog.show();
	}

	private ArrayList<AudienceVideo> searchAudiences(ArrayList<AudienceVideo> audiences, String keyword) {
		ArrayList<AudienceVideo> audienceArrayList = new ArrayList<>();
		try {
			for (AudienceVideo audience : audiences) {
				if (audience != null) {
					if (!TextUtils.isEmpty(audience.getUname())) {
						if (audience.getUname().contains(keyword))
							audienceArrayList.add(audience);
					} else if (!TextUtils.isEmpty(audience.getName())) {
						if (audience.getName().contains(keyword)) {
							audienceArrayList.add(audience);
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return audienceArrayList;
	}

	private void showPPTListDialog(ArrayList<Material> materials) {
		View view = View.inflate(this, R.layout.dialog_ppt_list, null);
		view.findViewById(R.id.exit).setOnClickListener(v -> {
			if (pptAlertDialog.isShowing()) {
				pptAlertDialog.dismiss();
			}
		});
		RecyclerView recyclerViewTV = view.findViewById(R.id.meeting_doc_list);
		FocusFixedLinearLayoutManager gridlayoutManager = new FocusFixedLinearLayoutManager(this); // 解决快速长按焦点丢失问题.
		gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
		recyclerViewTV.addItemDecoration(new SpaceItemDecoration((int) (getResources().getDimension(R.dimen.my_px_20)), 0, (int) (getResources().getDimension(R.dimen.my_px_20)), 0));
		recyclerViewTV.setLayoutManager(gridlayoutManager);
		recyclerViewTV.setFocusable(false);
		MaterialAdapter materialAdapter = new MaterialAdapter(this, materials);
		recyclerViewTV.setAdapter(materialAdapter);
		materialAdapter.setOnClickListener((v, material, position) -> showPPTDetailDialog(material));
		AlertDialog.Builder builder = new AlertDialog.Builder(MakerChairManActivity.this, R.style.MyDialog);
		builder.setView(view);
		pptAlertDialog = builder.create();
		pptAlertDialog.setCancelable(true);
		pptAlertDialog.setCanceledOnTouchOutside(true);

		if (!pptAlertDialog.isShowing()) {
			pptAlertDialog.show();
		}
	}

	JzvdStd jzvdStd;

	private void showPPTDetailDialog(Material material) {
		View view = View.inflate(this, R.layout.dialog_ppt_detail, null);
		ViewPager viewPager = view.findViewById(R.id.view_pager);
		TextView pageText = view.findViewById(R.id.page);
		pageText.setText("第1/" + material.getMeetingMaterialsPublishList().size() + "页");
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return material.getMeetingMaterialsPublishList().size();
			}

			@NonNull
			@Override
			public Object instantiateItem(@NonNull ViewGroup container, int position) {
				View view = View.inflate(container.getContext(), R.layout.item_doc_detail, null);
				LinearLayout videoLayout = view.findViewById(R.id.video_view_container);
				ImageView imageView = view.findViewById(R.id.image_view);
				if (material.getType().equals("1")) {//视频
					videoLayout.setVisibility(View.VISIBLE);
					imageView.setVisibility(View.GONE);

					jzvdStd = (JzvdStd) view.findViewById(R.id.jz_video);
					if (jzvdStd == null) {
						mLogger.e("jzvdStd==null");
					}
					mLogger.e("url:   " + material.getMeetingMaterialsPublishList().get(0).getUrl());
					jzvdStd.setUp(material.getMeetingMaterialsPublishList().get(0).getUrl()
							, "");
					String imageUrl = ImageHelper.videoFrameUrl(material.getMeetingMaterialsPublishList().get(0).getUrl()
							, AutoSizeUtils.dp2px(MakerChairManActivity.this, 500)
							, AutoSizeUtils.dp2px(MakerChairManActivity.this, 280));

					Picasso.with(MakerChairManActivity.this).load(imageUrl).error(R.drawable.default_img).placeholder(R.drawable.loading).into(jzvdStd.thumbImageView);
				} else {
					videoLayout.setVisibility(View.GONE);
					imageView.setVisibility(View.VISIBLE);
					String imageUrl = ImageHelper.getThumb(material.getMeetingMaterialsPublishList().get(position).getUrl());
					Glide.with(MakerChairManActivity.this).load(imageUrl).into(imageView);


				}
				container.addView(view);
				return view;
			}

			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view == object;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				if (jzvdStd != null) {
					JzvdStd.releaseAllVideos();
				}
				container.removeView((View) object);
			}
		});
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				pageText.setText("第" + (position + 1) + "/" + material.getMeetingMaterialsPublishList().size() + "页");
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		TextView nameText = view.findViewById(R.id.name);
		nameText.setText(material.getName());
		TextView timeText = view.findViewById(R.id.time);
		timeText.setText(material.getCreateDate() + "创建");
		view.findViewById(R.id.use_doc).setOnClickListener(v -> {
			currentMaterial = material;
			mLogger.e("ppt使用时 集合大小为" + mVideoAdapter.getDataSize());
			agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);
			Collections.sort(currentMaterial.getMeetingMaterialsPublishList(), (o1, o2) -> (o1.getPriority() < o2.getPriority()) ? -1 : 1);
			position = 0;
			ApiClient.getInstance().meetingSetMaterial(TAG, setMaterialCallback, meetingJoin.getMeeting().getId(), currentMaterial.getId());
		});
		view.findViewById(R.id.exit_preview).setOnClickListener(v -> {
			if (pptDetailDialog.isShowing()) {
				pptDetailDialog.dismiss();
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(MakerChairManActivity.this, R.style.MyDialog);
		builder.setView(view);
		pptDetailDialog = builder.create();
		pptDetailDialog.setCanceledOnTouchOutside(true);
		pptDetailDialog.setCanceledOnTouchOutside(true);
		if (!pptDetailDialog.isShowing()) {
			pptDetailDialog.show();
		}
		pptDetailDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (jzvdStd != null) {
					JzvdStd.releaseAllVideos();
				}
			}
		});
	}

	private OkHttpCallback setMaterialCallback = new OkHttpCallback<Bucket>() {
		@Override
		public void onSuccess(Bucket bucket) {
			if (pptDetailDialog != null && pptDetailDialog.isShowing()) {
				pptDetailDialog.dismiss();
			}
			if (pptAlertDialog != null && pptAlertDialog.isShowing()) {
				pptAlertDialog.dismiss();
			}


			changeViewByPPTModel(null);
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(MakerChairManActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	/**
	 * 非全屏状态，画面背景为ppt，主持人 参会人悬浮在ppt内容上
	 * <p>
	 * **需要判断集合大小，先讲主持人加入到集合中再判断  如果大于8人  就不显示自己  如果小于8人，所以的都显示
	 */
	private void notFullScreenState() {
		if (model == 1) {
			return;
		}
		model = 1;
		if (!mVideoAdapter.isHaveChairMan()) {
			AudienceVideo audienceVideo = new AudienceVideo();
			audienceVideo.setUid(config().mUid);
			audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
			audienceVideo.setBroadcaster(true);
			audienceVideo.setSurfaceView(localBroadcasterSurfaceView);
			mVideoAdapter.getAudienceVideoLists().add(audienceVideo);
		} else {
			if (mCurrentAudienceVideo != null) {
				mVideoAdapter.insertItem(mCurrentAudienceVideo);
				mCurrentAudienceVideo = null;
			}
		}
		mLogger.e("当前播放ppt  集合大小是:" + mVideoAdapter.getDataSize());
		if (mVideoAdapter.getDataSize() > 8) {
			int chairManPosition = mVideoAdapter.getChairManPosition();
			if (mVideoAdapter.getChairManPosition() != -1) {
				mVideoAdapter.getAudienceVideoLists().get(chairManPosition).getSurfaceView().setVisibility(View.GONE);
				mVideoAdapter.removeItem(mVideoAdapter.getChairManPosition());
			}
		}


		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 240), RelativeLayout.LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		layoutParams.setMargins(0, DisplayUtil.dip2px(this, 0), DisplayUtil.dip2px(this, 16), DisplayUtil.dip2px(this, 60));
		mAudienceRecyclerView.setLayoutParams(layoutParams);
		mAudienceRecyclerView.removeItemDecoration(mDecor);
		mAudienceRecyclerView.addItemDecoration(mDecor);
		mAudienceRecyclerView.removeItemDecoration(mGridSpaceItemDecoration);
		mDelegateAdapter.clear();

		mVideoAdapter.setItemSize(DisplayUtil.dip2px(this, 70), DisplayUtil.dip2px(this, 114));
		mVideoAdapter.notifyDataSetChanged();

		MyGridLayoutHelper helper = new MyGridLayoutHelper(2);
		helper.setAutoExpand(false);
		helper.setItemCount(8);

		mVideoAdapter.setLayoutHelper(helper);

		mDelegateAdapter.addAdapter(mVideoAdapter);
		mVideoAdapter.notifyDataSetChanged();

		mVideoAdapter.setVisibility(View.VISIBLE);
		mAudienceRecyclerView.setVisibility(View.VISIBLE);

		broadcasterSmallLayout.setVisibility(View.GONE);
		broadcasterSmallView.setVisibility(View.GONE);

	}

	/**
	 * 全屏状态：画面背景为PPT内容，右下角悬浮自己的画面 悬浮画面可以拖动
	 */
	private void FullScreenState() {
		if (model == 2) {
			return;
		}
		model = 2;
		//如果当前列表里面有主持人 则需要将主持人拿出来放在右下角  然后将大的参会人放在列表中去
		if (mVideoAdapter.isHaveChairMan()) {
			int chairManPosition = mVideoAdapter.getChairManPosition();
			if (chairManPosition != -1) {
				mVideoAdapter.getAudienceVideoLists().remove(chairManPosition);
				mVideoAdapter.notifyDataSetChanged();
			}
			if (mCurrentAudienceVideo != null) {
				mVideoAdapter.insertItem(mCurrentAudienceVideo);
			}
		}


		mAudienceRecyclerView.setVisibility(View.GONE);
		mVideoAdapter.setVisibility(View.GONE);

		broadcasterSmallView.setVisibility(View.VISIBLE);
		broadcasterSmallLayout.setVisibility(View.VISIBLE);

		broadcasterSmallView.removeAllViews();
		localBroadcasterSurfaceView.setVisibility(View.VISIBLE);
		localBroadcasterSurfaceView.setZOrderOnTop(true);
		localBroadcasterSurfaceView.setZOrderMediaOverlay(true);
		stripSurfaceView(localBroadcasterSurfaceView);

		broadcasterSmallView.addView(localBroadcasterSurfaceView);
	}

	/**
	 * 隐藏浮窗状态：画面只有PPT内容；
	 */
	private void clearAllState() {
		if (model == 3) {
			return;
		}
		model = 3;
		mVideoAdapter.setVisibility(View.GONE);
		mAudienceRecyclerView.setVisibility(View.GONE);

		broadcasterSmallLayout.setVisibility(View.GONE);
		broadcasterSmallView.setVisibility(View.GONE);
	}


	/**
	 * 播放本地视频
	 */

	private String getLocalVideoPath(String name) {
		String sdCard = Environment.getExternalStorageDirectory().getPath();
		String uri = sdCard + File.separator + name;
		return uri;
	}

	private void changeViewByPPTModel(Material material) {

		if (currentMaterial == null && material != null) {
			currentMaterial = material;
		}


	/*	MeetingMaterialsPublish e1 = new MeetingMaterialsPublish();
		e1.setCreateDate(System.currentTimeMillis() + "");
		e1.setId(System.currentTimeMillis() + "");
		e1.setType("1");
		e1.setPriority(4);
		e1.setUrl("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
		currentMaterial.getMeetingMaterialsPublishList().add(e1);*/

		if (currentMaterial == null || currentMaterial.getMeetingMaterialsPublishList().size() <= position) {
			return;
		}
		MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);

		if (currentMaterial != null && currentMaterialPublish.getType().equals("1")) {
			PlayVideo();
		} else {
			findViewById(R.id.app_video_box).setVisibility(View.GONE);
//			mPlayVideoText.setVisibility(View.GONE);
			docLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					/*hideFragment();
					if (isFullScreen) {
						if (!tvContent.getText().toString().isEmpty())
							llMsg.setVisibility(View.GONE);
					} else {
//                        if(!tvChat.getText().toString().isEmpty())
//                        llChat.setVisibility(View.VISIBLE);
					}*/
				}
			});

			docImage.setVisibility(View.VISIBLE);
			docImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				/*	hideFragment();
					if (isFullScreen) {
						if (!tvContent.getText().toString().isEmpty())
							llMsg.setVisibility(View.GONE);
					} else {
						if (!tvChat.getText().toString().isEmpty()) {
//                            llChat.setVisibility(View.VISIBLE);
						}

					}*/
				}
			});

//			fullScreenButton.setVisibility(View.VISIBLE);

			String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
			Glide.with(MakerChairManActivity.this).load(imageUrl)
					.placeholder(docImage.getDrawable())
					.error(R.drawable.rc_image_error)
					.into(docImage);

		}

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("material_id", currentMaterial.getId());
			jsonObject.put("doc_index", position);
			agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
//                agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		pageText.setVisibility(View.VISIBLE);
		pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

		docLayout.setVisibility(View.VISIBLE);
		//非全屏状态：画面背景为PPT内容，主持人+各参会人画面悬浮在PPT内容上，悬浮窗口不能移动（该状态3种角色统一）；
		//全屏状态：画面背景为PPT内容，右下角悬浮自己的画面（主持人角色显示主持人自己画面、各参会人角色显示各自参会人自己画面、观众不显示浮窗画面），悬窗支持移动；
		//隐藏浮窗状态：画面只有PPT内容；

		//进入ppt模式后 默认为非全屏状态
		if (model == 1 || model == 0) {
			notFullScreenState();
		} else if (model == 2) {
			FullScreenState();
		} else {
			clearAllState();
		}
		broadcasterLayout.removeAllViews();
		broadcasterLayout.setVisibility(View.GONE);

		if (isSplitMode && currentMaterial == null) {
			full_screen.setVisibility(View.GONE);
		} else {
			full_screen.setVisibility(View.VISIBLE);
		}
		mSpilteView.setVisibility(View.GONE);


	}

	private OkHttpCallback finishMeetingCallback = new OkHttpCallback<Bucket<Meeting>>() {
		@Override
		public void onSuccess(Bucket<Meeting> meetingBucket) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("finish_meeting", true);
				agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");

				stopButton.setVisibility(View.INVISIBLE);

//				audienceNameText.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}

			doLeaveChannel();
			if (agoraAPI.getStatus() == 2) {
				agoraAPI.logout();
			}
			finish();
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(MakerChairManActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("finish_meeting", true);
				agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");

				stopButton.setVisibility(View.INVISIBLE);

//				audienceNameText.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}

			doLeaveChannel();
			if (agoraAPI.getStatus() == 2) {
				agoraAPI.logout();
			}
			finish();
		}

	};

	private void doConfigEngine(int cRole) {
		int quilty = MMKV.defaultMMKV().decodeInt(MMKVHelper.KEY_meetingQuilty);
		if (quilty == 1.0) {
			worker().configEngine(cRole, VideoEncoderConfiguration.VD_640x480);//主持人是640*480
		} else if (quilty == 2.0) {
			worker().configEngine(cRole, VideoEncoderConfiguration.VD_960x720);//主持人是960*720
		} else if (quilty == 3.0) {
			worker().configEngine(cRole, VideoEncoderConfiguration.VD_1280x720);//主持人是1920 × 1080
		} else {
			worker().configEngine(cRole, VideoEncoderConfiguration.VD_640x480);//主持人是640*480
		}

	}

	@Override
	protected void deInitUIandEvent() {
		doLeaveChannel();
		event().removeEventHandler(this);
	}

	private void doTEnterChannel() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("status", 1);
		params.put("type", 1);
		params.put("meetingId", ((MeetingJoin.DataBean) (getIntent().getParcelableExtra("meeting"))).getMeeting().getId());
		ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);

	}

	private void doTLeaveChannel() {
		try {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("meetingJoinTraceId", meetingJoinTraceId);
			params.put("meetingId", channelName);
			params.put("status", 2);
			params.put("type", 1);
			params.put("leaveType", 1);
			ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doLeaveChannel() {

		worker().leaveChannel(config().mChannel);
		worker().preview(false, null, 0);
	}

	@Override
	public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}
			worker().getEngineConfig().mUid = uid;
			if ("true".equals(agora.getIsTest())) {
				agoraAPI.login2(agora.getAppID(), "" + uid, "noneed_token", 0, "", 20, 30);
			} else {
				agoraAPI.login2(agora.getAppID(), "" + uid, agora.getSignalingKey(), 0, "", 20, 30);
			}

			mLogger.e(config().mUid + "----" + agora.getAppID());
		});
	}

	private OkHttpCallback meetingTempLeaveCallback = new OkHttpCallback<Bucket>() {

		@Override
		public void onSuccess(Bucket meetingTempLeaveBucket) {
			Log.v("meetingTempLeave", meetingTempLeaveBucket.toString());
		}
	};

	private String meetingJoinTraceId;

	private OkHttpCallback meetingJoinStatsCallback = new OkHttpCallback<Bucket<MeetingJoinStats>>() {

		@Override
		public void onSuccess(Bucket<MeetingJoinStats> meetingJoinStatsBucket) {
			if (TextUtils.isEmpty(meetingJoinTraceId)) {
				meetingJoinTraceId = meetingJoinStatsBucket.getData().getId();
				if (meetingJoin == null || meetingJoin.getMeeting() == null || meetingJoin.getMeeting().getId() == null) {
					return;
				}
				Preferences.setMeetingId(meetingJoin.getMeeting().getId());
			} else {
				meetingJoinTraceId = null;
				Preferences.setMeetingId(null);
			}
			Preferences.setMeetingTraceId(meetingJoinTraceId);
		}
	};


	@Override
	public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {


		mLogger.e("参会人" + uid + "的视频流进入");
		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}

			if (BuildConfig.DEBUG) {
				Toast.makeText(MakerChairManActivity.this, "参会人" + uid + "的视频流进入", Toast.LENGTH_SHORT).show();
			}


			remoteAudienceSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
			remoteAudienceSurfaceView.setZOrderOnTop(true);
			remoteAudienceSurfaceView.setZOrderMediaOverlay(true);
			rtcEngine().setupRemoteVideo(new VideoCanvas(remoteAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

			AudienceVideo audienceVideo = new AudienceVideo();
			audienceVideo.setUid(uid);


			/*if (currentAiducenceId!=-1&&currentAudience!=null&&currentAudience.getName()!=null&&!currentAudience.getName().isEmpty()){
				audienceVideo.setName(currentAudience.getUname());
			}else if (currentAudience!=null&&currentAudience.getUname()!=null&&!currentAudience.getUname().isEmpty()){
				audienceVideo.setName(currentAudience.getUname());
				audienceVideo.setUname(currentAudience.getUname());
			}else {
				audienceVideo.setName("参会人:"+uid);
			}*/

			audienceVideo.setBroadcaster(false);
			audienceVideo.setCallStatus(2);
			audienceVideo.setSurfaceView(remoteAudienceSurfaceView);
			mVideoAdapter.insertItem(audienceVideo);


//			audienceHashMap.put(uid, audienceVideo);
//			updateAudienceList();

			//调用这个方法  会得到取某个特定用户的用户属性  调用成功会回调 onUserAttrResult
//			agoraAPI.getUserAttr(String.valueOf(uid), "uname");
			agoraAPI.getUserAttr(String.valueOf(uid), "userName");
			//观众上线
			if (currentAudience != null) {
				audienceVideo.setName(currentAudience.getName());
				audienceVideo.setUname(currentAudience.getUname());
			}

			if (uid != 0 && uid == currentAiducenceId) {
				currentAudience = audienceVideo;
				mLogger.e("观众视频进入了……" + JSON.toJSONString(currentAudience.toString()));
				stopButton.setVisibility(View.VISIBLE);
				audienceVideo.setName(currentAudience.getUname());
				audienceVideo.setUname(currentAudience.getUname());
				isConnecting = false;
			}

//			audienceHashMap.put(uid, audienceVideo);
//			updateAudienceList();

			if (connectingHandler.hasMessages(0)) {
				connectingHandler.removeMessages(0);
			}

			//分屏模式下 改变布局
			if (isSplitMode && mVideoAdapter.getDataSize() <= 7) {
				SpliteViews();
				mSpilteView.setText("退出均分");
			}

			//如果是全屏 参会人进入会议  列表直接隐藏
			//如果是ppt模式 model==2或者==3时，列表是隐藏的
			//如果是ppt模式进入 不是全屏 也不是分屏模式 需要将列表恢复大小


			if (isFullScreen || model == 2 | model == 3) {
				mAudienceRecyclerView.setVisibility(View.GONE);
				mVideoAdapter.setVisibility(View.GONE);
			} else {
				if (model == 1) {
					notFullScreenState();
				}
				mVideoAdapter.setVisibility(View.VISIBLE);
				mAudienceRecyclerView.setVisibility(View.VISIBLE);

			}


			if (isSplitMode && currentMaterial == null) {
				full_screen.setVisibility(View.GONE);
			} else {
				full_screen.setVisibility(View.VISIBLE);
			}



			/*JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("uid", uid);
				jsonObject.put("getInformation", true);
				agoraAPI.messageInstantSend(uid + "", 0, jsonObject.toString(), "");
			} catch (JSONException e) {
				e.printStackTrace();
			}*/


		});
	}

	Badge badge;

	private void updateAudienceList() {
		Iterator iter = audienceHashMap.entrySet().iterator();
		audiences.clear();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			audiences.add((AudienceVideo) entry.getValue());
		}
		if (audienceAdapter != null) {
			audienceAdapter.setData(audiences);
		}

		/*if (audienceCountText != null) {
			audienceCountText.setText("所有参会人 (" + audiences.size() + ")");
		}*/
		if (badge == null) {
			badge = new QBadgeView(this)
					.bindTarget(audiencesButton)
					.setBadgeBackgroundColor(getResources().getColor(R.color.red))
					.setBadgeTextColor(getResources().getColor(R.color.white))
					.setBadgeGravity(Gravity.END | Gravity.TOP)
					.setGravityOffset(20, -3, true)
					.setBadgeNumber(audiences.size());
		} else {
			badge.bindTarget(audiencesButton).setBadgeNumber(audiences.size());
		}

		if (isFullScreen) {
			badge.hide(false);
		} else {
			badge.setBadgeNumber(audiences.size());
		}

	}

	@Override
	public void onUserOffline(int uid, int reason) {
//        LOG.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
		mLogger.e("onUserOffline " + (uid) + "   " + reason);
		if (reason == Constants.USER_OFFLINE_BECOME_AUDIENCE) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLogger.e("用户变成了观众模式:" + uid);
					mLogger.e("currentAudience:  " + currentAudience + "   currentAiducenceId" + currentAiducenceId);
					if (currentAudience != null && currentAiducenceId == uid) {
						AudienceVideo audienceVideo = audienceHashMap.get(currentAudience.getUid());

						if (audienceVideo == null) {
							return;
						}
						audienceVideo.setCallStatus(0);
						audienceVideo.setHandsUp(false);
						currentAudience = null;
						currentAiducenceId = 0;
						Logger.e("currentAudience?=null:===" + currentAudience);

						updateAudienceList();
//						currentAudience = null;
						stopButton.setVisibility(View.INVISIBLE);
					}
					changeView(broadcasterLayout, localBroadcasterSurfaceView);

				/*	audienceView.removeAllViews();
					audienceNameText.setText("");
					audienceLayout.setVisibility(View.GONE);*/
					/*if (currentMaterial != null) {
						fullScreenButton.setVisibility(View.VISIBLE);
					} else {
						fullScreenButton.setVisibility(View.GONE);
					}*/
				}
			});
		} else if (reason == Constants.USER_OFFLINE_QUIT) {
			mLogger.e("用户退出了……");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (currentAudience != null) {
						if (audienceHashMap.containsKey(currentAudience.getUid())) {
							audienceHashMap.remove(currentAudience.getUid());
							currentAudience = null;
							updateAudienceList();
						}

					}
				}
			});
			if (uid == currentAiducenceId) {
				currentAudience = null;
				currentAiducenceId = 0;
				stopButton.setVisibility(View.INVISIBLE);
			}

		}

		doRemoveRemoteUi(uid);
	}

	/**
	 * 当用户退出 或者变成观众的时候
	 * 需要判断主持人是否在列表中
	 * 如果主持人在列表中 就需要将主持人放大
	 * 然后列表中移除参会人
	 */
	public void changeView(FrameLayout broadcasterLayout, SurfaceView broadCastView) {
		if (mVideoAdapter.isHaveChairMan()) {
			int chairManPosition = mVideoAdapter.getChairManPosition();
			if (chairManPosition != -1) {
				mVideoAdapter.deleteItem(chairManPosition);
				stripSurfaceView(broadCastView);
				broadcasterLayout.addView(broadCastView);
			}
		}
	}

	private void doRemoveRemoteUi(final int uid) {
		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}
			if (BuildConfig.DEBUG) {
				Toast.makeText(MakerChairManActivity.this, uid + "退出了", Toast.LENGTH_SHORT).show();
			}

			if (currentAiducenceId == uid) {
				stopButton.setVisibility(View.INVISIBLE);
			} else if (currentAiducenceId != 0) {
				stopButton.setVisibility(View.VISIBLE);
			}
			if (isSplitMode) {
				//没有使用ppt 只有主持人 参会人是 集合大小为2
				//使用ppt 只有主持人 参会人 集合大小是 集合大小是1
				//没有使用ppt 只有主持人  观众是 集合大小是2
				//使用ppt 只有主持人 观众时  集合大小是 集合大小是1

				// 使用了ppt  集合大小为1  没有使用ppt 集合大小为2

				mLogger.e("当前集合大小是：" + mVideoAdapter.getDataSize());
				//集合大小为1 代表只有主持人在了
				if (currentMaterial != null && mVideoAdapter.getDataSize() == 1 || currentMaterial == null && mVideoAdapter.getDataSize() == 2) {

					if (mVideoAdapter.isHaveChairMan()) {
						mLogger.e("此时没有在使用ppt   主持人在列表中");
						int chairManPosition = mVideoAdapter.getChairManPosition();
						if (chairManPosition != -1) {
							AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(chairManPosition);
							if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
								mVideoAdapter.removeItem(chairManPosition);
							}
						}

						localBroadcasterSurfaceView.setZOrderOnTop(true);
						localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
						stripSurfaceView(localBroadcasterSurfaceView);
						broadcasterLayout.addView(localBroadcasterSurfaceView);

						int positionById = mVideoAdapter.getPositionById(uid);
						if (positionById != -1) {
							AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
							if (audienceVideo != null) {
								audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
								audienceVideo.getSurfaceView().setZOrderOnTop(false);
							}
						}
						mVideoAdapter.deleteItem(uid);

					} else if (currentMaterial != null) {
						//在使用ppt的时候 主持人是不再列表中 此时有人退出 就直接移除此人就行
						mLogger.e("此时在使用ppt 主持人不再列表中");
						int positionById = mVideoAdapter.getPositionById(uid);
						if (positionById != -1) {
							AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
							if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
								audienceVideo.getSurfaceView().setZOrderOnTop(false);
								audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
							}
							mVideoAdapter.deleteItem(uid);
						}
					}
					if (currentMaterial == null) {
						localBroadcasterSurfaceView.setZOrderOnTop(false);
						localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
						stripSurfaceView(localBroadcasterSurfaceView);
						broadcasterLayout.removeAllViews();
						broadcasterLayout.addView(localBroadcasterSurfaceView);
					}


					mSpilteView.setText("均分模式");
					if (model == 3) {
						exitSpliteMode();
					} else if (model == 2) {
						FullScreenState();
					} else if (model == 1) {
						notFullScreenState();
					}

				} else {
					//如果大2的话 直接移除此人就行
					int positionById = mVideoAdapter.getPositionById(uid);
					if (positionById != -1) {
						AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
						if (audienceVideo != null) {
							audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
							audienceVideo.getSurfaceView().setZOrderOnTop(false);
						}
					}
					mVideoAdapter.deleteItem(uid);

				}
				if (isSplitMode) {
					changeViewLayout();
				}
			} else {
				//不是分屏模式 如果此人在大的视图 直接移除大视图 将主持人拿出来放到大的视图
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
					localBroadcasterSurfaceView.setZOrderOnTop(true);
					localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
					stripSurfaceView(localBroadcasterSurfaceView);
					broadcasterLayout.addView(localBroadcasterSurfaceView);
				} else {
					//如果此人不再大的视图里面 直接删除此人
					int positionById = mVideoAdapter.getPositionById(uid);
					if (positionById != -1) {
						AudienceVideo audienceVideo = mVideoAdapter.getAudienceVideoLists().get(positionById);
						if (audienceVideo != null) {
							audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
							audienceVideo.getSurfaceView().setZOrderOnTop(false);
						}
					}
					mVideoAdapter.deleteItem(uid);
				}

			}
		});
	}

	@Override
	public void onConnectionLost() {
		runOnUiThread(() -> {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				if (MakerChairManActivity.this.isDestroyed()) {
					return;
				}
			}
			if (MakerChairManActivity.this.isFinishing()) {
				return;
			}
			if (mConnectionLostDialog == null) {
				mConnectionLostDialog = new com.adorkable.iosdialog.AlertDialog(this)
						.builder()
						.setTitle("网络连接失败")
						.setMsg("与声网服务器连接断开  请检查网络连接")
						.setPositiveButton("退出", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mConnectionLostDialog.dismiss();
								finish();
							}
						})
						.setNegativeButton("取消", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mConnectionLostDialog.dismiss();
							}
						});

				mConnectionLostDialog.show();
			} else {
				if (!mConnectionLostDialog.isShowing()) {
					mConnectionLostDialog.show();
				}
			}
			/*finish();*/
		});
	}

	@Override
	public void onConnectionInterrupted() {
		runOnUiThread(() -> Toast.makeText(MakerChairManActivity.this, "网络连接不佳，视频将会有卡顿，可尝试降低分辨率", Toast.LENGTH_SHORT).show());
	}

	@Override
	public void onUserMuteVideo(final int uid, final boolean muted) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(() -> Toast.makeText(MakerChairManActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show());
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
			runOnUiThread(() -> {
				showNetQuality(quality, 0);
			});
			/*runOnUiThread(() -> Toast.makeText(ChairManActivity.this, "本地网络质量报告：" +, Toast.LENGTH_SHORT).show());*/

		}
	}


	@Override
	public void onNetworkQuality(int uid, int txQuality, int rxQuality) {

		runOnUiThread(() -> {
			showNetQuality(txQuality, rxQuality);
		});

	}

	@Override
	public void onWarning(int warn) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLogger.e("onWarning   " + warn);
				}
			});
		}
	}

	private void showNetQuality(int upload, int download) {

	}


	@Override
	public void onError(final int err) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MakerChairManActivity.this, "错误码：" + err, Toast.LENGTH_SHORT).show();
					mLogger.e("onError   " + err);

				}
			});
		}
	}

	@Override
	public void onRemoteVideoStateChanged(int uid, int state,int i ,int x) {

		//远端视频流卡顿
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				/*if (state == 2) {
					if (currentAudience != null) {
						if (currentAiducenceId == uid && currentAiducenceId != 0) {
							if (audienceHashMap != null && audienceHashMap.get(uid) != null && audienceHashMap.containsKey(uid)) {
								if (!TextUtils.isEmpty(audienceHashMap.get(uid).getName())) {
									Toasty.warning(ChairManActivity.this, audienceHashMap.get(uid).getName() + "的视频可能卡顿", Toasty.LENGTH_SHORT, true).show();
								}
								return;
							}
						}
					}
					if (audienceHashMap != null && audienceHashMap.containsKey(uid)) {
						if (audienceHashMap.get(uid) != null) {
							Toasty.warning(ChairManActivity.this, audienceHashMap.get(uid).getName() + "的视频可能卡顿", Toast.LENGTH_LONG, true).show();
						}
					}
				} else*/
				if (state == 4) {//远端视频流播放失败
					if (audienceHashMap != null && audienceHashMap.containsKey(uid)) {
						if (audienceHashMap.get(uid) != null) {
							Toasty.warning(MakerChairManActivity.this, audienceHashMap.get(uid).getName() + "的视频传输失败", Toast.LENGTH_LONG, true).show();
						}
					}
				}
			}
		});

	}

	@Override
	public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
		runOnUiThread(() -> {
			Timber.e("stats.lastmileDelay---->%s", stats.lastmileDelay);
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
	public void onLocalVideoStateChanged(int localVideoState, int error) {

	}


	@Override
	protected void onStart() {
		super.onStart();
//        initUIandEvent();
		doTEnterChannel();
	}

	@Override
	protected void onStop() {
		super.onStop();
//        if (BuildConfig.DEBUG) {
//            Toast.makeText(this, "当前没有连麦人", Toast.LENGTH_SHORT).show();
//        }
		doTLeaveChannel();

	}

	@Override
	protected void onUserLeaveHint() {
	/*	super.onUserLeaveHint();
		mLogger.e("onUserLeaveHint:" + agoraAPI.getStatus());
		doLeaveChannel();
		doTLeaveChannel();
		if (agoraAPI.getStatus() == 2) {
			agoraAPI.setAttr("uname", null);
			agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
			agoraAPI.logout();
		}

		finish();*/
	}

	private boolean isShowNetWorkDialog = true;

	@Override
	public void onBackPressed() {
		showExitDialog();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();




		if (mSignalDialog != null) {
			mSignalDialog.dismiss();
		}

		if (mConnectionLostDialog != null) {
			mConnectionLostDialog.dismiss();
		}
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

//		unregisterReceiver(homeKeyEventReceiver);

		subscription.unsubscribe();
		doLeaveChannel();
		if (agoraAPI == null) {
			return;
		}
		if (agoraAPI.getStatus() == 2) {
			agoraAPI.channelClearAttr(channelName);

			if (currentAudience != null) {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("finish", true);
					agoraAPI.messageInstantSend("" + currentAudience.getUid(), 0, jsonObject.toString(), "");
					mLogger.e("发送了结束消息");
				} catch (Exception e) {
					e.printStackTrace();
				}
				currentAudience = null;
			}
			agoraAPI.logout();
		}
		agoraAPI.destroy();

		if (mPreviewPlayer != null) {
			mPreviewPlayer.startButton.performClick();
		}

//        BaseApplication.getInstance().deInitWorkerThread();
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (showOperatorHandler.hasMessages(0)) {
					showOperatorHandler.removeMessages(0);
				}


				if (mOperaTools.getVisibility() == View.VISIBLE) {
					showOperatorHandler.sendEmptyMessageDelayed(0, Constant.delayTime);
				} else if (mOperaTools.getVisibility() == View.GONE) {
					showOperatorHandler.sendEmptyMessage(1);
				}
				break;

		}
		return super.dispatchTouchEvent(ev);
	}

	private void stopRecordVideo() {
		if (Constant.isNeedRecord) {

			HashMap<String, String> map = new HashMap<>();
			map.put("sid", mSid);
			map.put("resourceId", mResourceId);
			map.put("cname", meetingJoin.getMeeting().getId());
			map.put("uid", mRecordUid);
			map.put("createByMobile", Preferences.getUserMobile());
			mLogger.e(JSON.toJSONString(map));
			ApiClient.getInstance().stopRecordVideo(this, map, new OkHttpCallback<com.alibaba.fastjson.JSONObject>() {
				@Override
				public void onSuccess(com.alibaba.fastjson.JSONObject json) {
					if (json.getInteger("errcode") != 0) {
						ToastUtils.showToast(MakerChairManActivity.this, json.getString("errmsg"));
					}
				}

				@Override
				public void onFailure(int errorCode, BaseException exception) {
					super.onFailure(errorCode, exception);
					mLogger.e(exception.getMessage());
				}
			});
		}

	}


	private void stopPlayVideo() {
		if (mPreviewPlayer != null) {
			mPreviewPlayer.startButton.performClick();
		}
		agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PAUSEVIDEO);
//		mPlayVideoText.setText("播放");
//		setTextViewDrawableTop(mPlayVideoText, R.drawable.icon_play);
	}

	private boolean isPlayComplete;

	public void PlayVideo() {
		findViewById(R.id.app_video_box).setVisibility(View.VISIBLE);
//		findViewById(R.id.app_video_status_text).setVisibility(View.GONE);
		docImage.setVisibility(View.GONE);
//		mPlayVideoText.setVisibility(View.VISIBLE);
		/*mPreviewPlayer.startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPreviewPlayer.state == Jzvd.STATE_PLAYING) {
					mPreviewPlayer.mediaInterface.pause();
				}
				if (mPreviewPlayer.state == Jzvd.STATE_PAUSE) {
					mPreviewPlayer.mediaInterface.start();
				} else {
					mPreviewPlayer.startVideo();
				}
					*//*if (mPreviewPlayer.state==JzvdStd.STATE_PLAYING) {
						agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PAUSEVIDEO);
						mPreviewPlayer.onStatePause();
//						setTextViewDrawableTop(mPlayVideoText, R.drawable.icon_play);
					} else {
						agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PLAYVIDEO);
						mPreviewPlayer.startVideo();
//						setTextViewDrawableTop(mPlayVideoText, R.drawable.icon_pause);
					}*//*

			}
		});*/


		if (mPreviewPlayer != null) {
			mPreviewPlayer.setUp(currentMaterial.getMeetingMaterialsPublishList().get(position).getUrl(), "");
			mPreviewPlayer.getSeekBar().setVisibility(View.GONE);
			mPreviewPlayer.getCurrentTimeView().setVisibility(View.GONE);
			mPreviewPlayer.getTotalTimeView().setVisibility(View.GONE);
			mPreviewPlayer.getFullScreenView().setVisibility(View.GONE);

			String imageUrl = ImageHelper.videoFrameUrl(currentMaterial.getMeetingMaterialsPublishList().get(0).getUrl()
					, AutoSizeUtils.dp2px(MakerChairManActivity.this, 300)
					, AutoSizeUtils.dp2px(MakerChairManActivity.this, 400));

			Picasso.with(MakerChairManActivity.this).load(imageUrl)
					.error(R.drawable.item_forum_img_error)
					.placeholder(R.drawable.item_forum_img_loading)
					.into(mPreviewPlayer.thumbImageView);

//			Glide.with(ChairManActivity.this).load(currentMaterial.getMeetingMaterialsPublishList().get(0).getUrl()).into(mPreviewPlayer.thumbImageView);

			mPreviewPlayer.setOnVideoPlayChangeListener(new PreviewPlayer.OnVideoPlayChangeListener() {
				@Override
				public void play() {
					mLogger.e("当前是播放视频");

					agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PLAYVIDEO);
				}

				@Override
				public void pause() {
					agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.PAUSEVIDEO);
				}

				@Override
				public void complete() {
					agoraAPI.channelSetAttr(channelName, Constant.VIDEO, Constant.STOPVIDEO);
				}

			});
		}


	}

}

package io.agora.openlive.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.FixLayoutHelper;
import com.alibaba.android.vlayout.layout.FloatLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelper;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelperEx;
import com.alibaba.android.vlayout.layout.ScrollFixLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
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
import com.zhongyou.meet.mobile.entities.HostUser;
import com.zhongyou.meet.mobile.entities.Material;
import com.zhongyou.meet.mobile.entities.Materials;
import com.zhongyou.meet.mobile.entities.Meeting;
import com.zhongyou.meet.mobile.entities.MeetingJoin;
import com.zhongyou.meet.mobile.entities.MeetingJoinStats;
import com.zhongyou.meet.mobile.entities.MeetingMaterialsPublish;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.DensityUtil;
import com.zhongyou.meet.mobile.utils.DisplayUtil;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.SizeUtils;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.UIDUtil;
import com.zhongyou.meet.mobile.utils.helper.ImageHelper;
import com.zhongyou.meet.mobile.view.FocusFixedLinearLayoutManager;
import com.zhongyou.meet.mobile.view.SpaceItemDecoration;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.openlive.model.AGEventHandler;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class InviteMeetingBroadcastActivity extends BaseActivity implements AGEventHandler {


	private final String TAG = InviteMeetingBroadcastActivity.class.getSimpleName();

	private MeetingJoin meetingJoin;
	private Agora agora;
	private Material currentMaterial;
	private int position;

	private RecyclerView audienceRecyclerView;
	private NewAudienceVideoAdapter audienceVideoAdapter;

	private String channelName;
	private int memberCount;

	private boolean isMuted = false;

	private boolean isFullScreen = false;

	private LinearLayout docLayout;
	private FrameLayout broadcasterView;
	private TextView broadcasterNameText;
	private Button finishMeetingButton, pptButton, previewButton, nextButton, exitDocButton;
	private ImageButton muteAudioButton, fullScreenButton, switchCameraButton;
	private ImageView docImage;
	private TextView pageText;
	private SurfaceView localBroadcasterSurfaceView;

	private static final String DOC_INFO = "doc_info";
	private static final String CALLING_AUDIENCE = "calling_audience";

	private AgoraAPIOnlySignal agoraAPI;
	private AudienceVideo mCurrentAudienceVideo;
	private SurfaceView mAudienceVideoSurfaceView;
	private Logger mLogger;
	private GridLayoutHelper mGridLayoutHelper;

	private boolean isSpliteScreenModel = false;
	private SizeUtils mSizeUtils;
	private VirtualLayoutManager mVirtualLayoutManager;
	private DelegateAdapter mDelegateAdapter;
	private FrameLayout mChairManSmallVideoLayoutContainer;
	private FrameLayout mChairManSmallVideoLayout;
	private TextView mSpilteView;
	private Button mAttdenteeCountButton;

	private EditText searchEdit;
	private Button searchButton, stopButton;
	private boolean isConnecting = false;
	private AlertDialog pptAlertDialog, pptDetailDialog, alertDialog;
	private AudienceVideo currentAudience, newAudience;
	private int currentAiducenceId;
	private String broadcastId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_meeting_broadcast);

		registerReceiver(homeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));


	}

	@SuppressLint("HandlerLeak")
	private Handler connectingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (isConnecting) {
				Toast.makeText(InviteMeetingBroadcastActivity.this, "连麦超时，请稍后再试!", Toast.LENGTH_SHORT).show();

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
		for (AudienceVideo audience : audiences) {
			if (audience.getUname().contains(keyword)) {
				audienceArrayList.add(audience);
			}
		}
		return audienceArrayList;
	}

	private NewAudienceAdapter.OnAudienceButtonClickListener listener = new NewAudienceAdapter.OnAudienceButtonClickListener() {
		@Override
		public void onTalkButtonClick(AudienceVideo audience) {
			if (isConnecting) {
				Toast.makeText(InviteMeetingBroadcastActivity.this, "暂时无法切换连麦，请10秒后尝试", Toast.LENGTH_SHORT).show();
			} else {
				if (currentAudience != null) {
					if (currentAudience.getCallStatus() == 2 && currentAudience.getUid() != audience.getUid()) {
						showDialog(4, "中断当前" + currentAudience.getUname() + "的连麦，连接" + audience.getUname() + "的连麦", "取消", "确定", audience);
					} else {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "正在与当前参会人连麦中", Toast.LENGTH_SHORT).show();
					}
				} else {
					if (audience.getCallStatus() == 0) {
						if (audience.isHandsUp()) {
							showDialog(2, audience.getUname() + "请求连麦", "接受", "拒绝", audience);
						} else {
							showDialog(5, "确定与" + audience.getUname() + "连麦", "确定", "取消", audience);
						}
					} else {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "正在与当前参会人连麦中", Toast.LENGTH_SHORT).show();
					}
				}
				alertDialog.cancel();
			}
		}
	};

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
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (BuildConfig.DEBUG) {
						Toast.makeText(this, "当前没有连麦人", Toast.LENGTH_SHORT).show();
					}
					if (currentAiducenceId != 0) {
						stopButton.setVisibility(View.GONE);
						try {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("finish", true);
							agoraAPI.messageInstantSend("" + currentAiducenceId, 0, jsonObject.toString(), "");
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
				stopButton.setVisibility(View.GONE);
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("response", false);
					agoraAPI.messageInstantSend("" + audience.getUid(), 0, jsonObject.toString(), "");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (type == 3) {
				audience.setCallStatus(0);
				audience.setHandsUp(false);
				audienceHashMap.put(audience.getUid(), audience);
				updateAudienceList();

				stopButton.setVisibility(View.GONE);

				if (currentMaterial != null) {
					fullScreenButton.setVisibility(View.VISIBLE);
				}

				/*audienceView.removeAllViews();
				audienceNameText.setText("");
				audienceLayout.setVisibility(View.GONE);*/

				currentAudience = null;
				agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);

				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("finish", true);
					agoraAPI.messageInstantSend("" + audience.getUid(), 0, jsonObject.toString(), "");
					//rtcEngine().muteRemoteAudioStream(audience.getUid(),true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (type == 4) {
				newAudience = audience;
				isConnecting = true;
				connectingHandler.sendEmptyMessageDelayed(0, 10000);
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

					stopButton.setVisibility(View.GONE);

					agoraAPI.channelSetAttr(channelName, CALLING_AUDIENCE, "" + newAudience.getUid());
				}
			}
		});

		dialog = new Dialog(this, R.style.MyDialog);
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private HashMap<Integer, AudienceVideo> audienceHashMap = new HashMap<Integer, AudienceVideo>();
	private ArrayList<AudienceVideo> audiences = new ArrayList<AudienceVideo>();
	NewAudienceAdapter audienceAdapter;
	private TextView audienceCountText;

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

		if (audienceCountText != null) {
			audienceCountText.setText("所有参会人 (" + audiences.size() + ")");
		}
		mAttdenteeCountButton.setText("参会人（" + audiences.size() + "）");
	}

	@Override
	protected void initUIandEvent() {


		mLogger = XLog.tag(TAG)
				.t()
				.st(2)
				.b()
				.build();
		event().addEventHandler(this);


		Intent intent = getIntent();
		agora = intent.getParcelableExtra("agora");
		meetingJoin = intent.getParcelableExtra("meeting");

		channelName = meetingJoin.getMeeting().getId();
		config().mUid = Integer.parseInt(UIDUtil.generatorUID(Preferences.getUserId()));

		audienceRecyclerView = findViewById(R.id.audience_list);

		mGridLayoutHelper = new GridLayoutHelper(2);
		mGridLayoutHelper.setWeights(new float[]{50f});
		mGridLayoutHelper.setHGap(10);
		mGridLayoutHelper.setVGap(10);
		mGridLayoutHelper.setItemCount(8);

		mVirtualLayoutManager = new VirtualLayoutManager(this);
		mDelegateAdapter = new DelegateAdapter(mVirtualLayoutManager);
		RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
		audienceRecyclerView.setRecycledViewPool(viewPool);
		viewPool.setMaxRecycledViews(0, 10);
		audienceRecyclerView.setLayoutManager(mVirtualLayoutManager);

		audienceVideoAdapter = new NewAudienceVideoAdapter(this, mGridLayoutHelper);
		mDelegateAdapter.addAdapter(audienceVideoAdapter);
		audienceRecyclerView.setAdapter(mDelegateAdapter);

		mChairManSmallVideoLayoutContainer = findViewById(R.id.chairManSmallVideoLayoutContainer);
		mChairManSmallVideoLayout = findViewById(R.id.chairManSmallVideoLayout);

		mAttdenteeCountButton = findViewById(R.id.waiter);
		stopButton = findViewById(R.id.stop_audience);

		mAttdenteeCountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (audiences.size() > 0) {
					showAlertDialog();
				} else {
					agoraAPI.channelClearAttr(channelName);
					if (currentAiducenceId != 0) {
						stopButton.setVisibility(View.GONE);
						try {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("finish", true);
							agoraAPI.messageInstantSend("" + currentAiducenceId, 0, jsonObject.toString(), "");
						} catch (Exception e) {
							e.printStackTrace();

						}
					}
				}
			}
		});

		audienceVideoAdapter.setOnDoucleClickListener(new NewAudienceVideoAdapter.onDoubleClickListener() {
			@Override
			public void onDoubleClick(View parent, View view, int position) {
				if (isSpliteScreenModel) {
					return;
				}

				if (position >= audienceVideoAdapter.getDataSize()) {
					mLogger.e("onDoubleClick: position大于等于集合大小 出现错误");
					return;
				}
				if (audienceVideoAdapter.isHaveChairMan()) {
					//点击的如果是主持人
					if (audienceVideoAdapter.getAudienceVideoLists().get(position).isBroadcaster()) {
						if (mCurrentAudienceVideo != null) {
							audienceVideoAdapter.removeItem(position);
							audienceVideoAdapter.insertItem(position, mCurrentAudienceVideo);
							broadcasterView.removeAllViews();
							stripSurfaceView(localBroadcasterSurfaceView);
							broadcasterView.addView(localBroadcasterSurfaceView);
						}
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
							audienceVideoAdapter.removeItem(i);
							audienceVideoAdapter.insertItem(i, audienceVideo);

						}

					}
				}
				//将参会人的画面移到主持人界面
				broadcasterView.removeAllViews();
				mCurrentAudienceVideo = audienceVideoAdapter.getAudienceVideoLists().get(position);
				mAudienceVideoSurfaceView = mCurrentAudienceVideo.getSurfaceView();
				mAudienceVideoSurfaceView.setTag(position);

				audienceVideoAdapter.removeItem(position);
				stripSurfaceView(mAudienceVideoSurfaceView);
				broadcasterView.addView(mAudienceVideoSurfaceView);


				stripSurfaceView(localBroadcasterSurfaceView);
				//主持人画面 加入到列表中
				AudienceVideo audienceVideo = new AudienceVideo();
				audienceVideo.setUid(config().mUid);
				audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
				audienceVideo.setBroadcaster(true);
				audienceVideo.setSurfaceView(localBroadcasterSurfaceView);
				audienceVideoAdapter.insetChairMan(position, audienceVideo);

				mLogger.e("当前的集合大小是：" + audienceVideoAdapter.getDataSize());

			}
		});


//		audienceLayout = findViewById(R.id.audience_layout);
		docLayout = findViewById(R.id.doc_layout);
		broadcasterNameText = findViewById(R.id.broadcaster_name);
		broadcasterNameText.setText("主持人：" + meetingJoin.getHostUser().getHostUserName());
		broadcasterView = findViewById(R.id.broadcaster_view);
		docImage = findViewById(R.id.doc_image);
		pageText = findViewById(R.id.page);
		mSizeUtils = new SizeUtils(InviteMeetingBroadcastActivity.this);
		mSpilteView = findViewById(R.id.spliteView);
		mSpilteView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSpliteScreenModel) {
					isSpliteScreenModel = false;
					mSpilteView.setText("均分模式");
					exitSpliteMode();
				} else {
					mSpilteView.setText("退出均分");
					isSpliteScreenModel = true;

					//主持人在列表中 则将大的broadcasterView的视频加入到receclerview中去  将主持人移动到集合第一个去
					if (audienceVideoAdapter.isHaveChairMan()) {
						mLogger.e("主持人再列表中");
						if (mCurrentAudienceVideo != null) {
							broadcasterView.removeAllViews();
							stripSurfaceView(mCurrentAudienceVideo.getSurfaceView());
							audienceVideoAdapter.insertItem(mCurrentAudienceVideo);
							//将主持人移动到集合第一个
							/*int chairManPosition = audienceVideoAdapter.getChairManPosition();
							if (chairManPosition!=-1){
								audienceVideoAdapter.insertItem(0,audienceVideoAdapter.getAudienceVideoLists().get(chairManPosition));
								audienceVideoAdapter.removeItem(chairManPosition+1);
							}*/

						} else {
							ToastUtils.showToast("当前放大的视频丢失 不能均分视频");
						}
					} else {
						mLogger.e("主持人不再列表中");
						//将主持人加入到recyclerView中去
						stripSurfaceView(localBroadcasterSurfaceView);
						AudienceVideo audienceVideo = new AudienceVideo();
						audienceVideo.setUid(config().mUid);
						audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
						audienceVideo.setBroadcaster(true);
						audienceVideo.setSurfaceView(localBroadcasterSurfaceView);
						audienceVideoAdapter.insertItem(audienceVideo);
						broadcasterView.removeAllViews();
					}

					//将recyclerview编程全屏布局
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) audienceRecyclerView.getLayoutParams();
					layoutParams.setMargins(0, 0, 0, 0);
					audienceRecyclerView.setLayoutParams(layoutParams);
					mSizeUtils.setViewMatchParent(audienceRecyclerView);
					changeViewLayout();
				}

			}
		});

		previewButton = findViewById(R.id.preview);
		previewButton.setOnClickListener(view -> {
			if (currentMaterial != null) {
				if (position > 0) {
					position--;
					MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
					docImage.setVisibility(View.VISIBLE);
					String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
					Picasso.with(InviteMeetingBroadcastActivity.this).load(imageUrl).into(docImage);

					pageText.setVisibility(View.VISIBLE);
					pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("material_id", currentMaterial.getId());
						jsonObject.put("doc_index", position);
						agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
						agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(this, "当前是第一张了", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "没找到ppt", Toast.LENGTH_SHORT).show();
			}
		});

		nextButton = findViewById(R.id.next);
		nextButton.setOnClickListener(view -> {
			if (currentMaterial != null) {
				if (position < (currentMaterial.getMeetingMaterialsPublishList().size() - 1)) {
					position++;
					MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);
					docImage.setVisibility(View.VISIBLE);
					String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
					Picasso.with(InviteMeetingBroadcastActivity.this).load(imageUrl).into(docImage);

					pageText.setVisibility(View.VISIBLE);
					pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

					try {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("material_id", currentMaterial.getId());
						jsonObject.put("doc_index", position);
						agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
						agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(this, "当前是最后一张了", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "没找到ppt", Toast.LENGTH_SHORT).show();
			}

		});

		exitDocButton = findViewById(R.id.exit_ppt);
		exitDocButton.setOnClickListener(view -> {
			docImage.setVisibility(View.GONE);
			pageText.setVisibility(View.GONE);

			docLayout.setVisibility(View.GONE);
			mChairManSmallVideoLayoutContainer.setVisibility(View.GONE);
			mChairManSmallVideoLayout.removeAllViews();
			stripSurfaceView(localBroadcasterSurfaceView);
			broadcasterView.setVisibility(View.VISIBLE);
			broadcasterView.removeAllViews();
			broadcasterView.addView(localBroadcasterSurfaceView);

			currentMaterial = null;
			agoraAPI.channelDelAttr(channelName, DOC_INFO);

		});

		muteAudioButton = findViewById(R.id.mute_audio);
		muteAudioButton.setOnClickListener(v -> {
			if (!isMuted) {
				isMuted = true;
				muteAudioButton.setImageResource(R.drawable.ic_muted);
			} else {
				isMuted = false;
				muteAudioButton.setImageResource(R.drawable.ic_unmuted);
			}
			rtcEngine().muteLocalAudioStream(isMuted);
		});

		finishMeetingButton = findViewById(R.id.finish_meeting);
		finishMeetingButton.setOnClickListener(view -> showExitDialog());

		findViewById(R.id.exit).setOnClickListener(view -> {
			showExitDialog();
		});

		switchCameraButton = findViewById(R.id.camera_switch);
		switchCameraButton.setOnClickListener(view -> {
			rtcEngine().switchCamera();
		});

		fullScreenButton = findViewById(R.id.full_screen);

		pptButton = findViewById(R.id.meeting_doc);
		pptButton.setOnClickListener(view -> {
			ApiClient.getInstance().meetingMaterials(TAG, new OkHttpCallback<Bucket<Materials>>() {
				@Override
				public void onSuccess(Bucket<Materials> materialsBucket) {
					showPPTListDialog(materialsBucket.getData().getPageData());
				}

				@Override
				public void onFailure(int errorCode, BaseException exception) {
					super.onFailure(errorCode, exception);
					Toast.makeText(InviteMeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}, meetingJoin.getMeeting().getId());
		});



		if (Constant.videoType == 0) {
			localBroadcasterSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
			rtcEngine().setupLocalVideo(new VideoCanvas(localBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, config().mUid));
			localBroadcasterSurfaceView.setZOrderOnTop(true);
			localBroadcasterSurfaceView.setZOrderMediaOverlay(true);
			broadcasterView.addView(localBroadcasterSurfaceView);
			worker().preview(true, localBroadcasterSurfaceView, config().mUid);
			worker().configEngine(Constants.CLIENT_ROLE_BROADCASTER, VideoEncoderConfiguration.VD_320x240);
			rtcEngine().enableAudioVolumeIndication(400, 3,true);

		} else {
			ApiClient.getInstance().getMeetingHost(TAG, meetingJoin.getMeeting().getId(), joinMeetingCallback(0));
			worker().configEngine(Constants.CLIENT_ROLE_AUDIENCE, VideoEncoderConfiguration.VD_320x240);
			// TODO: 2019-11-27
//			startMeetingCamera(meeting.getScreenshotFrequency());
		}

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
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "信令系统登陆成功", Toast.LENGTH_SHORT).show());
					}
					agoraAPI.channelJoin(channelName);
				});

			}

			@Override
			public void onLoginFailed(final int ecode) {
				super.onLoginFailed(ecode);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "信令系统登陆失败" + ecode, Toast.LENGTH_SHORT).show();
					}
					if ("true".equals(agora.getIsTest())) {
						agoraAPI.login2(agora.getAppID(), "" + config().mUid, "noneed_token", 0, "", 20, 30);
					} else {
						agoraAPI.login2(agora.getAppID(), "" + config().mUid, agora.getSignalingKey(), 0, "", 20, 30);
					}

				});

			}

			@Override
			public void onReconnecting(int nretry) {
				super.onReconnecting(nretry);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "信令重连失败第" + nretry + "次", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onReconnected(int fd) {
				super.onReconnected(fd);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "信令系统重连成功", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onChannelJoined(String channelID) {
				super.onChannelJoined(channelID);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "加入信令频道成功", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onChannelJoinFailed(String channelID, int ecode) {
				super.onChannelJoinFailed(channelID, ecode);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "加入信令频道失败", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onLogout(int ecode) {
				super.onLogout(ecode);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "退出信令频道成功", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onChannelQueryUserNumResult(String channelID, int ecode, final int num) {
				super.onChannelQueryUserNumResult(channelID, ecode, num);
				runOnUiThread(() -> {
					memberCount = num;
					HashMap<String, String> params = new HashMap<>();
					params.put("count", "" + num);
					ApiClient.getInstance().channelCount(TAG, channelCountCallback(), meetingJoin.getMeeting().getId(), params);
				});
			}

			@Override
			public void onUserAttrResult(String account, String name, String value) {
				super.onUserAttrResult(account, name, value);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "获取到用户" + account + "的属性" + name + "的值为" + value, Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onChannelUserJoined(String account, int uid) {
				super.onChannelUserJoined(account, uid);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + account + "进入信令频道", Toast.LENGTH_SHORT).show();
					}

					agoraAPI.channelQueryUserNum(channelName);
					if (currentMaterial != null) { //正在演示ppt
						try {
							if (BuildConfig.DEBUG) {
								Toast.makeText(InviteMeetingBroadcastActivity.this, "向参会人" + account + "发送ppt信息", Toast.LENGTH_SHORT).show();
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("material_id", currentMaterial.getId());
							jsonObject.put("doc_index", position);
							agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
							agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else { // 没有在演示ppt
						agoraAPI.channelDelAttr(channelName, DOC_INFO);
						if (BuildConfig.DEBUG) {
							Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + account + "上来时主持人端没有ppt信息", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

			@Override
			public void onChannelUserLeaved(String account, int uid) {
				super.onChannelUserLeaved(account, uid);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + account + "退出信令频道", Toast.LENGTH_SHORT).show();
					}
					agoraAPI.channelQueryUserNum(channelName);
					audienceHashMap.remove(Integer.parseInt(account));
					updateAudienceList();
				});
			}

			@Override
			public void onMessageSendSuccess(String messageID) {
				super.onMessageSendSuccess(messageID);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, messageID + "发送成功", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onMessageSendError(String messageID, int ecode) {
				super.onMessageSendError(messageID, ecode);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, messageID + "发送失败", Toast.LENGTH_SHORT).show());
				}
			}

			@Override
			public void onMessageInstantReceive(final String account, final int uid, final String msg) {
				super.onMessageInstantReceive(account, uid, msg);
				mLogger.e("收到参会人" + account + "发来的消息" + msg);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "收到参会人" + account + "发来的消息" + msg, Toast.LENGTH_SHORT).show());
					}

					try {
						JSONObject jsonObject = new JSONObject(msg);
						if (jsonObject.has("handsUp")) {
							AudienceVideo audience = JSON.parseObject(jsonObject.toString(), AudienceVideo.class);
							if (audience.getCallStatus() == 2) {
								currentAudience = audience;
							}
							audienceHashMap.put(audience.getUid(), audience);
							updateAudienceList();
							if (audience.isHandsUp()) {
								Toast.makeText(InviteMeetingBroadcastActivity.this, audience.getUname() + "请求发言", Toast.LENGTH_SHORT).show();
							}
						}
						if (jsonObject.has("finish")) {
							boolean finish = jsonObject.getBoolean("finish");
							if (finish) {
								if (currentAudience != null && account.equals("" + currentAudience.getUid())) {
									stopButton.setVisibility(View.GONE);
/*
									audienceView.removeAllViews();
									audienceNameText.setText("");
									audienceLayout.setVisibility(View.GONE);

									remoteAudienceSurfaceView = null;*/

									agoraAPI.channelDelAttr(channelName, CALLING_AUDIENCE);
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				});
			}

			@Override
			public void onChannelAttrUpdated(String channelID, String name, String value, String type) {
				super.onChannelAttrUpdated(channelID, name, value, type);
				runOnUiThread(() -> {
					if (BuildConfig.DEBUG) {
						runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "" + channelID + "" + name + "" + value + "" + type, Toast.LENGTH_SHORT).show());
					}

					if (CALLING_AUDIENCE.equals(name)) {
						if (TextUtils.isEmpty(value)) {
							if (currentAudience != null) {
								currentAudience.setCallStatus(0);
								currentAudience.setHandsUp(false);
								audienceHashMap.put(currentAudience.getUid(), currentAudience);
								updateAudienceList();
							}

							currentAudience = null;

//							remoteAudienceSurfaceView = null;

							stopButton.setVisibility(View.GONE);
							/*audienceView.removeAllViews();
							audienceNameText.setText("");
							audienceLayout.setVisibility(View.GONE);*/

							if (currentMaterial != null) {
								fullScreenButton.setVisibility(View.VISIBLE);
							} else {
								fullScreenButton.setVisibility(View.GONE);
							}
						} else {
							currentAiducenceId = Integer.parseInt(value);
							nextButton.setVisibility(View.VISIBLE);
							previewButton.setVisibility(View.VISIBLE);
							exitDocButton.setVisibility(View.VISIBLE);
						}
					}

				});
			}

			@Override
			public void onError(final String name, final int ecode, final String desc) {
				super.onError(name, ecode, desc);
				if (BuildConfig.DEBUG) {
					runOnUiThread(() -> {
						if (ecode != 208)
							Toast.makeText(InviteMeetingBroadcastActivity.this, "收到错误信息\nname: " + name + "\necode: " + ecode + "\ndesc: " + desc, Toast.LENGTH_SHORT).show();
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
				Log.v("信令--->", txt);
			}
		});

	}

	private OkHttpCallback joinMeetingCallback(int uid) {
		return new OkHttpCallback<Bucket<HostUser>>() {
			@Override
			public void onSuccess(Bucket<HostUser> meetingJoinBucket) {
				mLogger.e("获取到房间的信息为："+JSON.toJSONString(meetingJoinBucket));
				meetingJoin.setHostUser(meetingJoinBucket.getData());
				broadcastId = meetingJoin.getHostUser().getClientUid();


				/*if (String.valueOf(uid).equals(broadcastId)) {
					agoraAPI.channelJoin(channelName);
					agoraAPI.queryUserStatus(broadcastId);

					localBroadcasterSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
					localBroadcasterSurfaceView.setZOrderOnTop(false);
					localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
					rtcEngine().setupRemoteVideo(new VideoCanvas(localBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
					broadcasterView.removeAllViews();
					broadcasterView.addView(localBroadcasterSurfaceView);
				}*/
			}
		};
	}

	private void exitSpliteMode() {

		//将主持人拿出来
		int chairManPosition = audienceVideoAdapter.getChairManPosition();
		//如果主持人 在的话
		if (chairManPosition != -1) {
			AudienceVideo chairManVideo = audienceVideoAdapter.getAudienceVideoLists().get(chairManPosition);
			audienceVideoAdapter.removeItem(chairManPosition);
			SurfaceView surfaceView = chairManVideo.getSurfaceView();
			//将主持人添加到大的画面上
			stripSurfaceView(surfaceView);
			broadcasterView.removeAllViews();
			broadcasterView.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		} else {
			// TODO: 2019-11-26 主持人不在
		}
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(this, 240), RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(DisplayUtil.dip2px(this, 500), DisplayUtil.dip2px(this, 40), 0, DisplayUtil.dip2px(this, 60));
		audienceRecyclerView.setLayoutParams(layoutParams);

		//还原布局
		int dataSize = audienceVideoAdapter.getDataSize();
		if (dataSize == 1) {
			audienceVideoAdapter.insertItem(0, new AudienceVideo());
		} else if (dataSize == 2) {
			audienceVideoAdapter.insertItem(0, new AudienceVideo());
			audienceVideoAdapter.insertItem(2, new AudienceVideo());
		} else if (dataSize == 3) {
			audienceVideoAdapter.insertItem(0, new AudienceVideo());
			audienceVideoAdapter.insertItem(2, new AudienceVideo());
			audienceVideoAdapter.insertItem(4, new AudienceVideo());
		} else if (dataSize == 4) {
			audienceVideoAdapter.insertItem(2, new AudienceVideo());
			audienceVideoAdapter.insertItem(4, new AudienceVideo());
		} else if (dataSize == 5) {
			audienceVideoAdapter.insertItem(4, new AudienceVideo());
		}

		mDelegateAdapter.clear();
		GridLayoutHelper helper = new GridLayoutHelper(2);
		helper.setWeights(new float[]{50f});
		helper.setVGap(10);
		helper.setHGap(10);
		helper.setItemCount(6);
		audienceVideoAdapter.setItemSize(DisplayUtil.dip2px(this, 70), DisplayUtil.dip2px(this, 114));
		audienceVideoAdapter.setLayoutHelper(helper);
		audienceVideoAdapter.notifyDataSetChanged();
		mDelegateAdapter.addAdapter(audienceVideoAdapter);
		mLogger.e("exitSpliteMode");

	}

	private void changeViewLayout() {
		for (int i = 0; i < audienceVideoAdapter.getAudienceVideoLists().size(); i++) {
			AudienceVideo video = audienceVideoAdapter.getAudienceVideoLists().get(i);
			if (video.getSurfaceView() == null) {
				audienceVideoAdapter.removeItem(i);
				audienceVideoAdapter.notifyDataSetChanged();
			}
		}


		int dataSize = audienceVideoAdapter.getDataSize();
		mLogger.e("集合大小：%d", dataSize);
		if (dataSize == 1) {
			return;
		} else if (dataSize == 2) {
			mLogger.e("走了集合大小为2");
			mGridLayoutHelper.setWeights(new float[]{50f});
			mGridLayoutHelper.setItemCount(2);
			audienceVideoAdapter.setLayoutHelper(mGridLayoutHelper);
			audienceVideoAdapter.setItemSize(DisplayUtil.getHeight(this), DisplayUtil.getWidth(this) / 2);
			audienceVideoAdapter.notifyDataSetChanged();
		} else if (dataSize == 3) {
			mDelegateAdapter.clear();
			OnePlusNLayoutHelper helper = new OnePlusNLayoutHelper(3);
			helper.setItemCount(3);
			helper.setColWeights(new float[]{50f});
			helper.setRowWeight(50f);
			audienceVideoAdapter.setLayoutHelper(helper);

			audienceVideoAdapter.setItemSize(DisplayUtil.getHeight(this), DisplayUtil.getWidth(this));
			audienceVideoAdapter.notifyDataSetChanged();
			mDelegateAdapter.addAdapter(audienceVideoAdapter);

		} else if (dataSize == 4) {
			mDelegateAdapter.clear();
			mGridLayoutHelper = new GridLayoutHelper(2);
			mGridLayoutHelper.setWeights(new float[]{50f});
			mGridLayoutHelper.setItemCount(4);
			audienceVideoAdapter.setLayoutHelper(mGridLayoutHelper);

			audienceVideoAdapter.setItemSize(DisplayUtil.getHeight(this) / 2, DisplayUtil.getWidth(this) / 2);
			audienceVideoAdapter.notifyDataSetChanged();
			mDelegateAdapter.addAdapter(audienceVideoAdapter);
		} else if (dataSize == 5) {
			mDelegateAdapter.clear();
			StaggeredGridLayoutHelper helper = new StaggeredGridLayoutHelper(2, 10);
			helper.setItemCount(5);
			audienceVideoAdapter.setLayoutHelper(helper);
			audienceVideoAdapter.notifyDataSetChanged();
			mDelegateAdapter.addAdapter(audienceVideoAdapter);
		} else if (dataSize == 6) {
			mDelegateAdapter.clear();
			GridLayoutHelper helper = new GridLayoutHelper(2);
			helper.setWeights(new float[]{50f});
			helper.setItemCount(6);
			audienceVideoAdapter.setItemSize(DisplayUtil.getHeight(this) / 3, DisplayUtil.getWidth(this) / 2);
			audienceVideoAdapter.setLayoutHelper(helper);
			audienceVideoAdapter.notifyDataSetChanged();
			mDelegateAdapter.addAdapter(audienceVideoAdapter);
		} else if (dataSize == 7) {
			mDelegateAdapter.clear();
			GridLayoutHelper helper = new GridLayoutHelper(4);
			helper.setSpanSizeLookup(new GridLayoutHelper.SpanSizeLookup() {
				@Override
				public int getSpanSize(int position) {
					if (position == 4) {
						return 2;
					}
					return 1;
				}
			});
			helper.setWeights(new float[]{100f / 4});
			helper.setItemCount(7);
			audienceVideoAdapter.setItemSize(DisplayUtil.getHeight(this) / 2, DisplayUtil.getWidth(this) / 4);
			audienceVideoAdapter.setLayoutHelper(helper);
			audienceVideoAdapter.notifyDataSetChanged();
			mDelegateAdapter.addAdapter(audienceVideoAdapter);
		} else if (dataSize >= 8) {
			mDelegateAdapter.clear();
			GridLayoutHelper helper = new GridLayoutHelper(4);
			helper.setWeights(new float[]{100f / 4});
			helper.setItemCount(8);
			audienceVideoAdapter.setItemSize(DisplayUtil.getHeight(this) / 2, DisplayUtil.getWidth(this) / 4);
			audienceVideoAdapter.setLayoutHelper(helper);
			audienceVideoAdapter.notifyDataSetChanged();
			mDelegateAdapter.addAdapter(audienceVideoAdapter);
		}
	}

	private OkHttpCallback channelCountCallback() {
		return new OkHttpCallback<Bucket>() {
			@Override
			public void onSuccess(Bucket bucket) {
				Log.v("channel_count", bucket.toString());
			}
		};
	}


	private void stripSurfaceView(SurfaceView view) {
		ViewParent parent = view.getParent();
		if (parent != null) {
			((FrameLayout) parent).removeView(view);
		}
	}

	private Dialog exitDialog, dialog;

	private void showExitDialog() {
		View contentView = View.inflate(this, R.layout.dialog_exit_meeting, null);
		TextView finishTips = contentView.findViewById(R.id.finish_meeting_tips);
		Button tempLeaveButton = contentView.findViewById(R.id.left);
		tempLeaveButton.setOnClickListener(view -> {
			exitDialog.cancel();

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
		});
		Button finishMeetingButton = contentView.findViewById(R.id.right);
		finishMeetingButton.setOnClickListener(view -> {
			if (finishTips.getVisibility() == View.VISIBLE) {
				ApiClient.getInstance().finishMeeting(TAG, meetingJoin.getMeeting().getId(), memberCount, finishMeetingCallback);
				exitDialog.cancel();
			} else {
				finishTips.setVisibility(View.VISIBLE);
			}
		});
		exitDialog = new Dialog(this, R.style.MyDialog);
		exitDialog.setContentView(contentView);
		exitDialog.show();
	}

	private OkHttpCallback meetingTempLeaveCallback = new OkHttpCallback<Bucket>() {

		@Override
		public void onSuccess(Bucket meetingTempLeaveBucket) {
			Log.v("meetingTempLeave", meetingTempLeaveBucket.toString());
		}
	};


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
		recyclerViewTV.setLayoutManager(gridlayoutManager);
		recyclerViewTV.setFocusable(false);
		recyclerViewTV.addItemDecoration(new SpaceItemDecoration((int) (getResources().getDimension(R.dimen.my_px_20)), 0, (int) (getResources().getDimension(R.dimen.my_px_20)), 0));
		MaterialAdapter materialAdapter = new MaterialAdapter(this, materials);
		recyclerViewTV.setAdapter(materialAdapter);
		materialAdapter.setOnClickListener(new MaterialAdapter.OnClickListener() {
			@Override
			public void onPreviewButtonClick(View v, Material material, int position) {
				showPPTDetailDialog(material);
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(InviteMeetingBroadcastActivity.this, R.style.MyDialog);
		builder.setView(view);
		pptAlertDialog = builder.create();
		if (!pptAlertDialog.isShowing()) {
			pptAlertDialog.show();
		}
	}

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
				ImageView imageView = view.findViewById(R.id.image_view);
				String imageUrl = ImageHelper.getThumb(material.getMeetingMaterialsPublishList().get(position).getUrl());
				Picasso.with(InviteMeetingBroadcastActivity.this).load(imageUrl).into(imageView);
				container.addView(view);
				return view;
			}

			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view == object;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
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
			if (currentMaterial != null && currentMaterial.getId().equals(material.getId())) {
				if (pptAlertDialog.isShowing()) {
					pptAlertDialog.dismiss();
				}
				if (pptDetailDialog.isShowing()) {
					pptDetailDialog.dismiss();
				}
			} else {
				currentMaterial = material;
				Collections.sort(currentMaterial.getMeetingMaterialsPublishList(), (o1, o2) -> (o1.getPriority() < o2.getPriority()) ? -1 : 1);
				ApiClient.getInstance().meetingSetMaterial(TAG, setMaterialCallback, meetingJoin.getMeeting().getId(), currentMaterial.getId());
			}
		});
		view.findViewById(R.id.exit_preview).setOnClickListener(v -> {
			if (pptDetailDialog.isShowing()) {
				pptDetailDialog.dismiss();
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(InviteMeetingBroadcastActivity.this, R.style.MyDialog);
		builder.setView(view);
		pptDetailDialog = builder.create();
		if (!pptDetailDialog.isShowing()) {
			pptDetailDialog.show();
		}
	}

	private OkHttpCallback setMaterialCallback = new OkHttpCallback<Bucket>() {
		@Override
		public void onSuccess(Bucket bucket) {
			com.orhanobut.logger.Logger.e(bucket.toString());
			if (pptAlertDialog.isShowing()) {
				pptAlertDialog.dismiss();
			}
			if (pptDetailDialog.isShowing()) {
				pptDetailDialog.dismiss();
			}
			broadcasterView.removeAllViews();
			broadcasterView.setVisibility(View.GONE);
			docLayout.setVisibility(View.VISIBLE);
			docImage.setVisibility(View.VISIBLE);
			mSpilteView.setVisibility(View.GONE);


			position = 0;
			MeetingMaterialsPublish currentMaterialPublish = currentMaterial.getMeetingMaterialsPublishList().get(position);

			pageText.setVisibility(View.VISIBLE);
			pageText.setText("第" + currentMaterialPublish.getPriority() + "/" + currentMaterial.getMeetingMaterialsPublishList().size() + "页");

			String imageUrl = ImageHelper.getThumb(currentMaterialPublish.getUrl());
			Picasso.with(InviteMeetingBroadcastActivity.this).load(imageUrl).into(docImage);

			mChairManSmallVideoLayoutContainer.setVisibility(View.VISIBLE);
			mChairManSmallVideoLayout.removeAllViews();
			stripSurfaceView(localBroadcasterSurfaceView);
			mChairManSmallVideoLayout.addView(localBroadcasterSurfaceView);

			/*AudienceVideo audienceVideo = new AudienceVideo();
			audienceVideo.setUid(config().mUid);
			audienceVideo.setName("主持人" + meetingJoin.getHostUser().getHostUserName());
			audienceVideo.setBroadcaster(true);
			audienceVideo.setSurfaceView(localBroadcasterSurfaceView);
			audienceVideoAdapter.insertItem(audienceVideo);*/


			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("material_id", currentMaterial.getId());
				jsonObject.put("doc_index", position);
				agoraAPI.channelSetAttr(channelName, DOC_INFO, jsonObject.toString());
				agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			Toast.makeText(InviteMeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
	};

	private OkHttpCallback finishMeetingCallback = new OkHttpCallback<Bucket<Meeting>>() {
		@Override
		public void onSuccess(Bucket<Meeting> meetingBucket) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("finish_meeting", true);
				agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
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
			Toast.makeText(InviteMeetingBroadcastActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("finish_meeting", true);
				agoraAPI.messageChannelSend(channelName, jsonObject.toString(), "");
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

	@Override
	protected void deInitUIandEvent() {
		doLeaveChannel();
		event().removeEventHandler(this);
	}

	private void doLeaveChannel() {
		currentMaterial = null;
		agoraAPI.channelDelAttr(channelName, DOC_INFO);

		worker().leaveChannel(config().mChannel);
		worker().preview(false, null, 0);
		if (!TextUtils.isEmpty(meetingJoinTraceId)) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("meetingJoinTraceId", meetingJoinTraceId);
			params.put("meetingId", meetingJoin.getMeeting().getId());
			params.put("status", 2);
			params.put("type", 1);
			params.put("leaveType", 1);
			ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback(), params);
		}
	}

	@Override
	public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}

			config().mUid = uid;
			channelName = channel;


			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("meetingId", meetingJoin.getMeeting().getId());
			params.put("status", 1);
			params.put("type", 1);
			ApiClient.getInstance().meetingJoinStats(TAG, meetingJoinStatsCallback(), params);

			if ("true".equals(agora.getIsTest())) {
				agoraAPI.login2(agora.getAppID(), "" + uid, "noneed_token", 0, "", 20, 30);
			} else {
				agoraAPI.login2(agora.getAppID(), "" + uid, agora.getSignalingKey(), 0, "", 20, 30);
			}

		});
	}

	private String meetingJoinTraceId;

	private OkHttpCallback meetingJoinStatsCallback() {
		return new OkHttpCallback<Bucket<MeetingJoinStats>>() {

			@Override
			public void onSuccess(Bucket<MeetingJoinStats> meetingJoinStatsBucket) {
				if (TextUtils.isEmpty(meetingJoinTraceId)) {
					meetingJoinTraceId = meetingJoinStatsBucket.getData().getId();
				} else {
					meetingJoinTraceId = null;
				}
			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				super.onFailure(errorCode, exception);
			}
		};
	}

	@Override
	public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}
			if (BuildConfig.DEBUG) {
				Toast.makeText(InviteMeetingBroadcastActivity.this, "参会人" + uid + "的视频流进入", Toast.LENGTH_SHORT).show();
			}
			SurfaceView remoteAudienceSurfaceView = RtcEngine.CreateRendererView(getApplicationContext());
			remoteAudienceSurfaceView.setZOrderOnTop(true);
			remoteAudienceSurfaceView.setZOrderMediaOverlay(true);
			rtcEngine().setupRemoteVideo(new VideoCanvas(remoteAudienceSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
			if (uid==Integer.parseInt(broadcastId)){
				//主持人的画面进来了
				localBroadcasterSurfaceView=remoteAudienceSurfaceView;
				localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
				localBroadcasterSurfaceView.setZOrderMediaOverlay(false);
				broadcasterView.removeAllViews();
				stripSurfaceView(localBroadcasterSurfaceView);
				broadcasterView.addView(localBroadcasterSurfaceView);
			}else {

				audienceRecyclerView.setVisibility(View.VISIBLE);
				AudienceVideo audienceVideo = new AudienceVideo();
				audienceVideo.setUid(uid);
				audienceVideo.setName("参会人" + uid);
				audienceVideo.setBroadcaster(false);
				audienceVideo.setPosition(audienceVideoAdapter.getDataSize());
				audienceVideo.setSurfaceView(remoteAudienceSurfaceView);
				audienceVideoAdapter.insertItem(audienceVideo);


				AudienceVideo emptyVideoView = new AudienceVideo();
				emptyVideoView.setName("虚假数据");

				audienceHashMap.put(uid, audienceVideo);

				updateAudienceList();

				for (int i = 0; i < audienceVideoAdapter.getDataSize(); i++) {
					if (audienceVideoAdapter.getAudienceVideoLists().get(i).getSurfaceView() == null) {
						audienceVideoAdapter.removeItem(i);
					}
				}
				mLogger.e("当前集合大小是：" + audienceVideoAdapter.getDataSize());
				switch (audienceVideoAdapter.getDataSize()) {

					case 1:
						audienceVideoAdapter.insertItem(0, emptyVideoView);
						break;
					case 2:
						audienceVideoAdapter.insertItem(0, emptyVideoView);
						audienceVideoAdapter.insertItem(2, emptyVideoView);
						break;
					case 3:
						audienceVideoAdapter.insertItem(0, emptyVideoView);
						audienceVideoAdapter.insertItem(2, emptyVideoView);
						audienceVideoAdapter.insertItem(4, emptyVideoView);
						break;
					case 4:
						audienceVideoAdapter.insertItem(2, emptyVideoView);
						audienceVideoAdapter.insertItem(4, emptyVideoView);
						break;
					case 5:
						audienceVideoAdapter.insertItem(4, emptyVideoView);
						break;

				}
				//分屏模式下 改变布局
				if (isSpliteScreenModel && audienceVideoAdapter.getDataSize() <= 7) {
					changeViewLayout();
				}
			}



		});
	}

	/**
	 * 还原receclerView的布局
	 */
	public void restoreRecyclerViewLayout() {
		for (int i = 0; i < audienceVideoAdapter.getDataSize(); i++) {
			if (audienceVideoAdapter.getAudienceVideoLists().get(i).getSurfaceView() == null) {
				audienceVideoAdapter.removeItem(i);
			}
		}
		int dataSize = audienceVideoAdapter.getDataSize();
		mLogger.e("dataSize===%d", dataSize);
		if (dataSize == 1) {
			audienceVideoAdapter.insertItem(0, new AudienceVideo());
		} else if (dataSize == 2) {
			audienceVideoAdapter.insertItem(0, new AudienceVideo());
			audienceVideoAdapter.insertItem(2, new AudienceVideo());
		} else if (dataSize == 3) {
			audienceVideoAdapter.insertItem(0, new AudienceVideo());
			audienceVideoAdapter.insertItem(2, new AudienceVideo());
			audienceVideoAdapter.insertItem(4, new AudienceVideo());
		} else if (dataSize == 4) {
			audienceVideoAdapter.insertItem(2, new AudienceVideo());
			audienceVideoAdapter.insertItem(4, new AudienceVideo());
		} else if (dataSize == 5) {
			audienceVideoAdapter.insertItem(4, new AudienceVideo());
		}
	}

	@Override
	public void onUserOffline(int uid, int reason) {
		mLogger.e("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
		runOnUiThread(() -> {
			if (isFinishing()) {
				return;
			}


			if (mCurrentAudienceVideo != null) {
				mLogger.e("当前放大的id是：%d", mCurrentAudienceVideo.getUid());
			}


			if (audienceVideoAdapter.getItemCount() == 0) {
				audienceRecyclerView.setVisibility(View.INVISIBLE);
			}
			audienceVideoAdapter.deleteItem(uid);
			restoreRecyclerViewLayout();
			//分屏模式下 改变布局
			if (isSpliteScreenModel) {
				changeViewLayout();
			} else {//还原布局

				//如果列表有主持人 并且离开的是放大的view，则将主持人移回到broadcasterView中
				if (audienceVideoAdapter.isHaveChairMan() && mCurrentAudienceVideo.getUid() == uid) {
					if (BuildConfig.DEBUG) {
						ToastUtils.showToast("当前离开的是放大的view 并且列表中有主持人");
					}
					int chairManPosition = audienceVideoAdapter.getChairManPosition();
					mLogger.e("主持人的position==%d", chairManPosition);
					if (chairManPosition == -1) {
						if (BuildConfig.DEBUG) {
							ToastUtils.showToast("列表的中主持人未找到 不能进行还原");
						}
						return;
					}
					audienceVideoAdapter.removeItem(chairManPosition);
					restoreRecyclerViewLayout();
					broadcasterView.removeAllViews();
					stripSurfaceView(localBroadcasterSurfaceView);
					broadcasterView.addView(localBroadcasterSurfaceView);
				} else {
					restoreRecyclerViewLayout();
				}
			}


			if (BuildConfig.DEBUG)
				Toast.makeText(InviteMeetingBroadcastActivity.this, uid + "退出了", Toast.LENGTH_SHORT).show();

		});
	}

	@Override
	public void onConnectionLost() {
		runOnUiThread(() -> {
			Toast.makeText(InviteMeetingBroadcastActivity.this, "与声网服务器连接断开，请检查网络连接", Toast.LENGTH_SHORT).show();
			finish();
		});
	}

	@Override
	public void onConnectionInterrupted() {
		runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "网络连接不佳，视频将会有卡顿，可尝试降低分辨率", Toast.LENGTH_SHORT).show());
	}

	@Override
	public void onUserMuteVideo(final int uid, final boolean muted) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, uid + " 的视频被暂停了 " + muted, Toast.LENGTH_SHORT).show());
		}
	}

	@Override
	public void onUserMuteAudio(int uid, boolean muted) {
		runOnUiThread(() -> {
			audienceVideoAdapter.setMutedStatusByUid(uid, muted);
		});
	}

	@Override
	public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
		runOnUiThread(() -> {
			for (IRtcEngineEventHandler.AudioVolumeInfo audioVolumeInfo : speakers) {
				audienceVideoAdapter.setVolumeByUid(audioVolumeInfo.uid, audioVolumeInfo.volume);
			}
		});
	}

	@Override
	public void onLastmileQuality(final int quality) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "本地网络质量报告：" + showNetQuality(quality), Toast.LENGTH_SHORT).show());
		}
	}

	@Override
	public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(() -> {
//                    Toast.makeText(MeetingBroadcastActivity.this, "用户" + uid + "的\n上行网络质量：" + showNetQuality(txQuality) + "\n下行网络质量：" + showNetQuality(rxQuality), Toast.LENGTH_SHORT).show();
			});
		}
	}

	@Override
	public void onWarning(int warn) {
		if (BuildConfig.DEBUG) {
//            runOnUiThread(() -> Toast.makeText(MeetingBroadcastActivity.this, "警告码：" + warn, Toast.LENGTH_SHORT).show());
		}
	}

	private String showNetQuality(int quality) {
		String lastmileQuality;
		switch (quality) {
			case 0:
				lastmileQuality = "UNKNOWN0";
				break;
			case 1:
				lastmileQuality = "EXCELLENT";
				break;
			case 2:
				lastmileQuality = "GOOD";
				break;
			case 3:
				lastmileQuality = "POOR";
				break;
			case 4:
				lastmileQuality = "BAD";
				break;
			case 5:
				lastmileQuality = "VBAD";
				break;
			case 6:
				lastmileQuality = "DOWN";
				break;
			default:
				lastmileQuality = "UNKNOWN";
		}
		return lastmileQuality;
	}

	@Override
	public void onError(final int err) {
		if (BuildConfig.DEBUG) {
			runOnUiThread(() -> Toast.makeText(InviteMeetingBroadcastActivity.this, "错误码：" + err, Toast.LENGTH_SHORT).show());
		}
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


	@Override
	protected void onStop() {
		super.onStop();
		doLeaveChannel();

		currentMaterial = null;
		if (agoraAPI.getStatus() == 2) {
			agoraAPI.channelDelAttr(channelName, DOC_INFO);
			agoraAPI.logout();
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		showExitDialog();
	}

//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//
//        Toast.makeText(this, "按home退出了", Toast.LENGTH_SHORT).show();
//
//        doLeaveChannel();
//        if (agoraAPI.getStatus() == 2) {
//            agoraAPI.logout();
//        }
//
//        finish();
//    }

	@Override
	protected void onDestroy() {
		super.onDestroy();


		unregisterReceiver(homeKeyEventReceiver);

		currentMaterial = null;

		if (agoraAPI.getStatus() == 2) {
			agoraAPI.channelClearAttr(channelName);
			agoraAPI.logout();
		}
		agoraAPI.destroy();
	}

	private BroadcastReceiver homeKeyEventReceiver = new BroadcastReceiver() {
		String REASON = "reason";
		String HOMEKEY = "homekey";
		String RECENTAPPS = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action) || Intent.ACTION_SHUTDOWN.equals(action)) {
				String reason = intent.getStringExtra(REASON);
				if (TextUtils.equals(reason, HOMEKEY)) {
					// 点击 Home键
					if (BuildConfig.DEBUG)
						Toast.makeText(getApplicationContext(), "您点击了Home键", Toast.LENGTH_SHORT).show();

					currentMaterial = null;

					HashMap<String, String> params = new HashMap<String, String>();
					params.put("clientUid", "" + config().mUid);
					params.put("hostUserId", Preferences.getUserId());
					params.put("hostUserName", meetingJoin.getHostUser().getHostUserName());
					params.put("status", "2");
					ApiClient.getInstance().meetingLeaveTemp(TAG, params, meetingTempLeaveCallback, meetingJoin.getMeeting().getId());

					doLeaveChannel();
					if (agoraAPI.getStatus() == 2) {
						agoraAPI.channelDelAttr(channelName, DOC_INFO);
						agoraAPI.logout();
					}
					agoraAPI.destroy();
				} else if (TextUtils.equals(reason, RECENTAPPS)) {
					// 点击 菜单键
					if (BuildConfig.DEBUG)
						Toast.makeText(getApplicationContext(), "您点击了菜单键", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

}

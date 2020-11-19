/*
package com.zhongyou.meet.mobile.business;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;

import com.jess.arms.utils.RxBus;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.network.NetworkUtil;
import com.zhongyou.meet.mobile.business.adapter.ImageBrowerAdapter;
import com.zhongyou.meet.mobile.business.adapter.NewChatAdapter;
import com.zhongyou.meet.mobile.business.adapter.chatAdapter;
import com.zhongyou.meet.mobile.business.adapter.inputAdapter;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.entities.QiniuToken;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.event.ForumRevokeEvent;
import com.zhongyou.meet.mobile.event.ForumSendEvent;
import com.zhongyou.meet.mobile.event.SocketStatus;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.NetUtils;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.SoftKeyBoardListener;
import com.zhongyou.meet.mobile.utils.ToastUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import rx.Subscription;
import rx.functions.Action1;

public class ChatFragment extends BaseFragment implements chatAdapter.onClickCallBack, TakePhoto.TakeResultListener, InvokeListener, inputAdapter.onItemClickInt {


	//    private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerViewInput, recyclerViewChat;
	//    private RecyclerView mRecycler;
	private TextView emptyText;
	private String imagePath;
	private InvokeParam invokeParam;
	private static String FORUM_REVOKE = "FORUM_REVOKE";
	private static String FORUM_SEND_CONTENT = "FORUM_SEND_CONTENT";
	private LinearLayoutManager mLayoutManager;
	private NewChatAdapter adapter;
	private ImageBrowerAdapter imgAdapter;
	private boolean isRefresh;
	private int mTotalPage = -1;
	private int mPageNo = 1;
	private Button btnSend;
	private boolean hideInput = false;
	private EditText mEditText;
	private int count = 0;
	private RelativeLayout rlContent;
	ArrayList<String> list = new ArrayList<>();
	private TakePhoto takePhoto;
	private Subscription subscription;
	private RelativeLayout openCamera;
	private boolean onCreate = true;
	private String mMeetingId = "";
	private String atUserId = "";
	private ProgressBar proBar;
	private static int delayTime = 2000;
	private static int delayRevokeTime = 2000;
	private ChatMesData.PageDataEntity mDataEntity;

	@Override
	public String getStatisticsTag() {
		return "聊天";
	}

	public static ChatFragment newInstance() {
		ChatFragment fragment = new ChatFragment();
		return fragment;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		getTakePhoto().onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		getTakePhoto().onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	private void uploadImage(long ts, ChatMesData.PageDataEntity entity) {
		ApiClient.getInstance().requestQiniuToken(this, new OkHttpCallback<BaseBean<QiniuToken>>() {

			@Override
			public void onSuccess(BaseBean<QiniuToken> result) {
				String token = result.getData().getToken();
				if (TextUtils.isEmpty(token)) {
					showToast("七牛token获取错误");
					return;
				}
				Configuration config = new Configuration.Builder().connectTimeout(5).responseTimeout(5).build();
				UploadManager uploadManager = new UploadManager(config);
				String key = "osg/forum/" + mMeetingId + "/" + UUID.randomUUID().toString().replace("-", "") + ".jpg";
				System.out.println(imagePath);
				uploadManager.put(
						new File(imagePath),
						key,
						token,
						new UpCompletionHandler() {
							@Override
							public void complete(String key, ResponseInfo info, JSONObject response) {
								if (info.isNetworkBroken() || info.isServerError()) {
									if (getActivity() != null) {
										getActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												showToast("上传失败");
												entity.setLocalState(2);
												entity.setUpLoadScuucess(false);
												if (adapter != null) {
													adapter.notifyItemChanged(adapter.getData().indexOf(entity));
												}

											}
										});
									}
									return;
								}
								if (info.isOK()) {
//									int i = adapter.getData().indexOf(entity);
//									adapter.getData().get(i).setContent(Preferences.getImgUrl() + key);
									entity.setUpLoadScuucess(true);
									entity.setContent(Preferences.getImgUrl() + key);
									HashMap<String, Object> params = new HashMap<String, Object>();
									params.put("meetingId", mMeetingId);
									params.put("ts", ts);
									params.put("content", Preferences.getImgUrl() + key);
									params.put("type", 1);
									ApiClient.getInstance().expostorPostChatMessage(TAG, expostorStatsCallback, params);

								} else {
									entity.setUpLoadScuucess(false);
									entity.setLocalState(2);
									if (adapter != null) {
										adapter.notifyItemChanged(adapter.getData().indexOf(entity));
									}

									showToast("上传失败" + info.error);
								}
							}
						},
						new UploadOptions(null, null, true, new UpProgressHandler() {
							@Override
							public void progress(final String key, final double percent) {

							}

						}, null));
			}
		});
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getTakePhoto().onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_meeting_chat, null, false);
//        mSwipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout);
		mMeetingId = getActivity().getIntent().getStringExtra("meetingId");
		initView(view);
		initRecy();
		getData();
		setData();
		subscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				Logger.e(JSON.toJSONString(o));
				if (o instanceof ForumSendEvent) {
					if (mMeetingId != null) {
						if (!((ForumSendEvent) o).getEntity().getMeetingId().equals(mMeetingId)) {
							return;
						}
					}
					if (((ForumSendEvent) o).getEntity().getUserId().equals(Preferences.getUserId())) {
						getActivity().runOnUiThread(() -> {
							if (adapter == null) {
								return;
							}
							for (ChatMesData.PageDataEntity data : adapter.getData()) {
								if (data.getTs() == ((ForumSendEvent) o).getEntity().getTs()) {
									int index = adapter.getData().indexOf(data);
									ChatMesData.PageDataEntity entity = ((ForumSendEvent) o).getEntity();
									if (entity.getTs() == adapter.getData().get(index).getTs()) {
										if (entity.getType() == 1) {
											entity.setContent(adapter.getData().get(index).getContent());
										}
									}
									Logger.e(JSON.toJSONString(entity));
									adapter.getData().set(index, entity);
									adapter.notifyItemChanged(index, index);
									recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
									break;
								}
							}
						});


					} else {
						getActivity().runOnUiThread(() -> {
							if (adapter != null) {
								adapter.addData(adapter.getData().size(), ((ForumSendEvent) o).getEntity());
								recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
							}

						});

					}

				} else if (o instanceof ForumRevokeEvent) {
//                    requestRecordOnlyLast(true);
					Log.e("backMessage", "call: " + JSON.toJSONString(((ForumRevokeEvent) o).getEntity()));
					if (mMeetingId != null) {
						if (!((ForumRevokeEvent) o).getEntity().getMeetingId().equals(mMeetingId)) {
							return;
						}
					}
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (adapter == null) {
								return;
							}
							for (ChatMesData.PageDataEntity datum : adapter.getData()) {
								if (datum.getId().equals(((ForumRevokeEvent) o).getEntity().getId())) {
									datum.setMsgType(1);
									adapter.notifyItemChanged(adapter.getData().indexOf(datum));
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
								}
							}
						}
					});

				} else if (o instanceof SocketStatus) {
					//socket连接成功 如果有发送中的消息 尝试重新发送 并且改变状态
					if (((SocketStatus) o).getIsConnected()) {
						if (getActivity() != null) {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (adapter != null) {
										for (int i = 0; i < adapter.getData().size(); i++) {
											if (adapter.getData().get(i).getLocalState() == 1) {
												sendAction(System.currentTimeMillis(), adapter.getData().get(i).getContent());
											}
										}
									}

								}
							});
						}
					} else {
						//断开了socket的连接
						if (getActivity() == null) {
							return;
						}
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (adapter == null) {
									return;
								}
								for (int i = 0; i < adapter.getData().size(); i++) {
									//正在发送中 就改变状态 改成发送失败
									if (adapter.getData().get(i).getLocalState() == 1) {
										adapter.getData().get(i).setLocalState(2);
										adapter.notifyItemChanged(i);
									}
								}
							}
						});

					}

				}

			}
		});
		return view;
	}

	private void setData() {
		ArrayList<String> list2 = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			String str = "abd";
			list2.add(str);
		}
		inputAdapter iadapter = new inputAdapter(getActivity(), list2, this);

		recyclerViewInput.setAdapter(iadapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		subscription.unsubscribe();

	}

	public TakePhoto getTakePhoto() {
		if (takePhoto == null) {
			takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
		}
		CompressConfig compressConfig = new CompressConfig.Builder().setMaxPixel(800).create();
		takePhoto.onEnableCompress(compressConfig, true);
		return takePhoto;
	}

	private void configTakePhotoOption(TakePhoto takePhoto) {
		TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();

		builder.setCorrectImage(true);
		takePhoto.setTakePhotoOptions(builder.create());
	}


	private void initView(View rootView) {
		openCamera = (RelativeLayout) rootView.findViewById(R.id.open_camera);
		recyclerViewChat = rootView.findViewById(R.id.recycler1);
		recyclerViewInput = (RecyclerView) rootView.findViewById(R.id.recycler2);
		btnSend = (Button) rootView.findViewById(R.id.btn_send);
		mEditText = (EditText) rootView.findViewById(R.id.edit_text);
		proBar = (ProgressBar) rootView.findViewById(R.id.progressbar);

		proBar.setVisibility(View.GONE);
		openCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recyclerViewInput.setVisibility(View.VISIBLE);
				if (adapter != null) {
					recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
				}else {
					ToastUtils.showToast("没有获取到聊天信息 请稍后再试");
				}

			}
		});
		rlContent = (RelativeLayout) rootView.findViewById(R.id.rl_content);
		rlContent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				*/
/*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.bottomMargin = (int) getResources().getDimension(R.dimen.my_px_180);
				recyclerViewChat.setLayoutParams(params);*//*

				recyclerViewInput.setVisibility(View.GONE);
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			}
		});
		mEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (adapter!=null){
					recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
				}
				recyclerViewInput.setVisibility(View.GONE);

			}
		});
		mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				Log.v("edittextfocus", b + "");
				if (b) {
//                    Log.v("edittextfocus",b+"");
					recyclerViewInput.setVisibility(View.GONE);
				}
			}
		});
		mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				Log.v("medittext98", "setOnEditorActionListener==" + i);
				return false;
			}
		});

		mEditText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int i, KeyEvent keyEvent) {
				Log.v("medittext98", "onkey事件==" + keyEvent.getAction());
				return false;
			}
		});

		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {

				if (mEditText.getText().toString().isEmpty()) {
					openCamera.setVisibility(View.VISIBLE);
					btnSend.setVisibility(View.GONE);
				} else {
					openCamera.setVisibility(View.GONE);
					btnSend.setVisibility(View.VISIBLE);

				}
			}
		});
		mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

				if (actionId == EditorInfo.IME_ACTION_SEND) {

				}
				return false;
			}
		});

		SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
			@Override
			public void keyBoardShow(int height) {
				if (adapter != null) {
					recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
				}
			}

			@Override
			public void keyBoardHide(int height) {

			}
		});

		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDataEntity = new ChatMesData.PageDataEntity();
				long ts = System.currentTimeMillis();
				mDataEntity.setContent(mEditText.getText().toString());
				mDataEntity.setId("");
				mDataEntity.setMsgType(0);
				mDataEntity.setTs(ts);
				mDataEntity.setType(0);
				mDataEntity.setUserName(Preferences.getUserName());
				mDataEntity.setUserId(Preferences.getUserId());
				mDataEntity.setUserLogo(Preferences.getUserPhoto());
				mDataEntity.setLocalState(1);
				if (adapter != null) {
					adapter.addData(adapter.getData().size(), mDataEntity);
					recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
				}
				if (NetUtils.isNetworkConnected(getActivity())) {
					sendAction(ts, mEditText.getText().toString());
				} else {
					mDataEntity.setLocalState(2);
					if (adapter != null) {
						mEditText.setText("");
						adapter.notifyItemChanged(adapter.getData().indexOf(mDataEntity));
					}
					Toasty.error(getActivity(), "当前没有网路连接 请检查网络再重试!").show();
				}
			}
		});
	}

	private void sendAction(long ts, String content) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("meetingId", mMeetingId);
		params.put("ts", ts);
		params.put("content", content);
		params.put("type", 0);
		if (!atUserId.isEmpty()) {
			params.put("atailUserId", atUserId);
			atUserId = "";
		}
		mEditText.setText("");
		ApiClient.getInstance().expostorPostChatMessage(TAG, expostorStatsCallback, params);
	}

	private OkHttpCallback expostorStatsCallback = new OkHttpCallback<Bucket<ChatMesData.PageDataEntity>>() {

		@Override
		public void onSuccess(Bucket<ChatMesData.PageDataEntity> entity) {

//            ToastUtils.showToast("提交成功");
		}

		@Override
		public void onFailure(int errorCode, BaseException exception) {
			super.onFailure(errorCode, exception);
			if (mDataEntity != null) {
				mDataEntity.setLocalState(2);
				if (adapter!=null){
					adapter.notifyItemChanged(adapter.getData().indexOf(mDataEntity));
					recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
				}
			}

			if (progressDialog != null && progressDialog.isShowing()) {
				if (handler.hasMessages(14)) {
					handler.removeMessages(14);
				}
				progressDialog.dismiss();
			}
			ToastUtils.showToast(exception.getMessage());
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		mEditText.setFocusable(true);
	}

	public void hintKeyBoard() {
		//拿到InputMethodManager
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		//如果window上view获取焦点 && view不为空
		if (imm.isActive() && getActivity().getCurrentFocus() != null) {
			//拿到view的token 不为空
			if (getActivity().getCurrentFocus().getWindowToken() != null) {
				//表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
				imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initRecy() {
		mLayoutManager = new LinearLayoutManager(getActivity());
		mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
		recyclerViewChat.setLayoutManager(mLayoutManager);

		recyclerViewChat.setFocusable(false);
		SimpleItemAnimator itemAnimator = (SimpleItemAnimator) recyclerViewChat.getItemAnimator();
		itemAnimator.setAddDuration(0);
		itemAnimator.setChangeDuration(0);
		itemAnimator.setMoveDuration(0);
		itemAnimator.setRemoveDuration(0);

		GridLayoutManager gridlayoutManager2 = new GridLayoutManager(getActivity(), 4);
		gridlayoutManager2.setOrientation(GridLayoutManager.VERTICAL);
		recyclerViewInput.setLayoutManager(gridlayoutManager2);
		recyclerViewInput.setFocusable(false);
		recyclerViewInput.setVisibility(View.GONE);

		recyclerViewChat.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hintKeyBoard();
				recyclerViewInput.setVisibility(View.GONE);
				return false;
			}
		});

	}


	private int mCurrentPage = 1;
	private int mPageSize = 20;
	private ChatMesData.PageDataEntity mReSendEntity;
	private List<ChatMesData.PageDataEntity> mDataLists = new ArrayList<>();

	private void getData() {
		if (!NetworkUtil.isAvailable(getActivity())){
			ToastUtils.showToast("当前网络不可用");
			proBar.setVisibility(View.GONE);
			return;
		}



		ApiClient.getInstance().getChatMessages(this, mMeetingId, mCurrentPage + "", mPageSize + "", new OkHttpCallback<BaseBean<ChatMesData>>() {
			@Override
			public void onSuccess(BaseBean<ChatMesData> entity) {
				mTotalPage = entity.getData().getTotalPage();
				Logger.i(JSON.toJSONString(entity));


				if (adapter == null) {
					adapter = new NewChatAdapter(entity.getData().getPageData());
					recyclerViewChat.setAdapter(adapter);
					recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
					if (mCurrentPage >= mTotalPage) {
						adapter.setUpFetchEnable(false);
					} else {
						adapter.setUpFetchEnable(true);
					}
					adapter.setUpFetchListener(new BaseQuickAdapter.UpFetchListener() {
						@Override
						public void onUpFetch() {
							mCurrentPage++;
							if (mCurrentPage >= mTotalPage) {
								adapter.setUpFetchEnable(false);
							} else {
								adapter.setUpFetchEnable(true);
							}
							adapter.setUpFetching(true);
							getData();
						}
					});
					ApiClient.getInstance().postViewLog(mMeetingId, this, expostorStatsCallback);
					adapter.setOnItemClickListener(new NewChatAdapter.onItemClickListener() {
						@Override
						public void onItemClick(int position, View view, List<ImageView> imageViewList, List<String> imagePathList) {
							int index = 0;
							for (int i = 0; i < imagePathList.size(); i++) {
								if (imagePathList.get(i).equals(adapter.getData().get(position).getContent())) {
									index = i;
									break;
								}
							}

						}
					});

					adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
						@Override
						public void onItemChildClick(BaseQuickAdapter pter, View view, int position) {
							switch (view.getId()) {
								case R.id.tv_edit:
									mEditText.setText(adapter.getData().get(position).getContent());
									mEditText.setSelection(mEditText.getText().length());
									break;
								case R.id.send_sate:
									Logger.e("重新发送……");
									mReSendEntity = adapter.getData().get(position);
									onReSend(adapter.getData().get(position).getContent(), adapter.getData().get(position).getType());
									break;
								default:
									break;

							}
						}
					});

					adapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
						@Override
						public boolean onItemChildLongClick(BaseQuickAdapter pter, View view, int position) {
							switch (view.getId()) {
								case R.id.tv_content:
									if (adapter.getItemViewType(position) == 0) {
										showPopupWindow(view, null, adapter.getData().get(position).getContent());
									} else {
										showPopupWindow(view, adapter.getData().get(position).getId(), adapter.getData().get(position).getContent());
										Logger.e(adapter.getData().get(position).getId());
									}
									break;
								case R.id.img_pic:
									if (adapter.getItemViewType(position) == 1) {
										showPopupWindow(view, adapter.getData().get(position).getId(), null);
									}
									break;
								case R.id.mIvHead:
									mEditText.setText("@" + adapter.getData().get(position).getUserName());
									mEditText.setSelection(mEditText.getText().length());
									atUserId = adapter.getData().get(position).getUserId();
									break;
								default:
									break;


							}

							return true;
						}
					});

				} else {
					adapter.addData(0, entity.getData().getPageData());

				}


			}

			@Override
			public void onFinish() {
				Logger.e("获取信息结束");
				if (adapter != null) {
					adapter.setUpFetching(false);
				}


			}
		});
	}


	@Override
	protected void normalOnClick(View v) {
		switch (v.getId()) {
			case R.id.mLayoutNoData:
//                requestRecord();
				break;
			default:
				break;
		}
	}

	@Override
	public void onClickCallBackFuc() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.bottomMargin = (int) getResources().getDimension(R.dimen.my_px_180);
		recyclerViewChat.setLayoutParams(params);

		recyclerViewInput.setVisibility(View.GONE);
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}

	@Override
	public void onLongImgHead(String name, String userId) {
		mEditText.setText("@" + name);
		mEditText.setSelection(mEditText.getText().length());
		atUserId = userId;
//        recyclerViewChat.scrollToPosition(dataChat.size() - 1);
	}

	@Override
	public void onLongContent(View view, String id, String content) {
		showPopupWindow(view, id, content);
	}

	@Override
	public void onEditCallBack(String content) {
		mEditText.setText(content);
		mEditText.setSelection(mEditText.getText().length());
	}

	@Override
	public void onReSend(String content, int type) {
		if (type == 0) {

			mReSendEntity.setLocalState(1);
			adapter.notifyItemChanged(adapter.getData().indexOf(mReSendEntity));
//			initLastData(dataChat, true);
			sendAction(mReSendEntity.getTs(), content);

		} else {
//			ChatMesData.PageDataEntity entity = new ChatMesData.PageDataEntity();
//			long ts = System.currentTimeMillis();
//			entity.setContent(content);
//			entity.setId("");
//			entity.setMsgType(0);
//			entity.setTs(ts);
//			entity.setType(1);
//			entity.setUserName(Preferences.getUserName());
//			entity.setUserId(Preferences.getUserId());
//			entity.setUserLogo(Preferences.getUserPhoto());
//			entity.setLocalState(1);

			int index = adapter.getData().indexOf(mReSendEntity);
			adapter.getData().get(index).setType(1);
			adapter.getData().get(index).setLocalState(1);
			adapter.getData().get(index).setMsgType(0);
			adapter.notifyItemChanged(index);

			Logger.e(JSON.toJSONString(adapter.getData().get(index)));
//			adapter.addData(adapter.getData().size(), (ChatMesData.PageDataEntity) entity);
//			initLastData(dataChat, true);
			uploadImage(adapter.getData().get(index).getTs(), adapter.getData().get(index));
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.v("chatfragment9090", "收到msg==" + msg.what + "*****" + msg.obj);
			if (msg.obj instanceof ChatMesData.PageDataEntity) {
				Log.v("chatfragment9090", "2s收到");
			} else if (msg.what == 11) {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.bottomMargin = (int) getResources().getDimension(R.dimen.my_px_500);
				recyclerViewChat.setLayoutParams(params);
				recyclerViewInput.setVisibility(View.VISIBLE);
				recyclerViewChat.scrollToPosition(adapter.getData().size());
			} else if (msg.what == 10) {
				recyclerViewChat.scrollToPosition(adapter.getData().size());
			} else if (msg.what == 12) {
//                dataChat.add((ChatMesData.PageDataEntity) msg.obj);
//                initLastData(dataChat,true);
				adapter.notifyItemChanged(msg.arg1);
			} else if (msg.what == 14) {
				if (progressDialog != null && progressDialog.isShowing()) {
					ToastUtils.showToast("网络出现问题");
					progressDialog.dismiss();
				}

			} else if (msg.what == 16) {
//				initLastData(dataChat, true);
			}

		}
	};


	@Override
	public void takeSuccess(TResult result) {
		if (!NetworkUtil.isAvailable(getActivity())){
			ToastUtils.showToast("当前网络不可用");
			return;
		}
		imagePath = result.getImage().getOriginalPath();
		ChatMesData.PageDataEntity entity = new ChatMesData.PageDataEntity();
		long ts = System.currentTimeMillis();
		entity.setContent("file://" + result.getImage().getOriginalPath());
		entity.setId("");
		entity.setMsgType(0);
		entity.setTs(ts);
		entity.setType(1);
		entity.setUserName(Preferences.getUserName());
		entity.setUserId(Preferences.getUserId());
		entity.setUserLogo(Preferences.getUserPhoto());
		entity.setLocalState(1);

		Logger.e("开始加入图片");
		if (adapter!=null){
			adapter.getData().add(adapter.getData().size(), entity);
			adapter.notifyItemInserted(adapter.getData().size());
			recyclerViewChat.smoothScrollToPosition(adapter.getData().size());
		}
		recyclerViewInput.setVisibility(View.GONE);
//		initLastData(dataChat, true);
		Logger.e(JSON.toJSONString(entity));

		uploadImage(ts, entity);
		Log.i(TAG, "takeSuccess：" + result.getImage().getOriginalPath());
	}

	@Override
	public void takeFail(TResult result, String msg) {
		Log.i(TAG, "takeSuccess：" + msg);
	}

	@Override
	public void takeCancel() {
		Log.i(TAG, "takeSuccess：");
	}

	@Override
	public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
		PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
		if (PermissionManager.TPermissionType.WAIT.equals(type)) {
			this.invokeParam = invokeParam;
		}
		return type;
	}

	@Override
	public void onItemClick(int pos) {
		configTakePhotoOption(takePhoto);
		if (pos == 0) {
			takePhoto.onPickFromGallery();
		} else {
			File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			final Uri imageUri = Uri.fromFile(file);
			takePhoto.onPickFromCapture(imageUri);
		}

	}

	private void showPopupWindow(View view, String id, String content) {

		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(mContext).inflate(
				R.layout.pop_window, null);
		// 设置按钮的点击事件

		TextView button = (TextView) contentView.findViewById(R.id.del);
		TextView btnCopy = (TextView) contentView.findViewById(R.id.copy);
		ImageView min = (ImageView) contentView.findViewById(R.id.min);
		if (id == null) {
			button.setVisibility(View.GONE);
			min.setVisibility(View.GONE);
		}
		if (content == null) {
			btnCopy.setVisibility(View.GONE);
			min.setVisibility(View.GONE);
		}
		final PopupWindow popupWindow = new PopupWindow(contentView,
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

		popupWindow.setTouchable(true);
		btnCopy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText(content);
			}
		});
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//                Toast.makeText(mContext, "button is pressed",
//                        Toast.LENGTH_SHORT).show();
				popupWindow.dismiss();
				showRevokeDialog();
//				handler.sendEmptyMessageDelayed(14, delayRevokeTime);
//                handler.sendMessageDelayed()
				ApiClient.getInstance().expostorDeleteChatMessage(this, expostorStatsCallback, id);

			}
		});


		popupWindow.setTouchInterceptor(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				Log.i("mengdd", "onTouch : ");

				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));

		if (content == null) {
			popupWindow.showAsDropDown(view, 0, -view.getHeight() - 100);
		} else if (id == null) {
			popupWindow.showAsDropDown(view, 0, -view.getHeight() - 100);
		} else {
			popupWindow.showAsDropDown(view, view.getWidth() - 400, -view.getHeight() - 100);
		}


//        popupWindow.sh

	}

	Dialog progressDialog;

	private void showRevokeDialog() {
		progressDialog = new Dialog(getActivity(), R.style.progress_dialog);
		progressDialog.setContentView(R.layout.dialog_revoke);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

}*/

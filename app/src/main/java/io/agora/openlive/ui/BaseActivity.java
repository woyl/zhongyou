package io.agora.openlive.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.herewhite.sdk.Converter;
import com.herewhite.sdk.ConverterCallbacks;
import com.herewhite.sdk.Room;
import com.herewhite.sdk.RoomParams;
import com.herewhite.sdk.domain.ConversionInfo;
import com.herewhite.sdk.domain.ConvertException;
import com.herewhite.sdk.domain.ConvertedFiles;
import com.jess.arms.utils.KickOffEvent;
import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.ameeting.WhiteBoardFragment;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.ameeting.whiteboard.ClassEventListener;
import com.zhongyou.meet.mobile.ameeting.whiteboard.DemoAPI;
import com.zhongyou.meet.mobile.ameeting.whiteboard.ToastManager;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.model.EngineConfig;
import io.agora.openlive.model.MyEngineEventHandler;
import io.agora.openlive.model.WorkerThread;
import io.agora.rtc.RtcEngine;
import io.agora.whiteboard.netless.manager.BoardManager;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity implements ClassEventListener {
	private final static Logger log = LoggerFactory.getLogger(BaseActivity.class);
	private final String TAG = getClass().getSimpleName();
	private String appId;

	protected ApiService mApiService;
	private AlertDialog mAlertDialog;
	private Subscription mSubscription;
	private boolean isKickOff;
	private boolean activityIsStop;
	private KickOffEvent mKickOffEvent;
	public com.elvishew.xlog.Logger mLogger;
	protected void logAction(Object list) {

		mLogger.e(Thread.currentThread().getStackTrace()[3].getMethodName() + "\n" + JSON.toJSONString(list));
	}

	protected WhiteBoardFragment whiteboardFragment = new WhiteBoardFragment();

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((BaseApplication) getApplication()).initWorkerThread(BaseApplication.getInstance().getAgora().getAppID());
		final View layout = findViewById(Window.ID_ANDROID_CONTENT);
		ViewTreeObserver vto = layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
				Log.v("addonClobalLayoutlis", "进入初始化initUIandEvent");
				initUIandEvent();
			}
		});

		mLogger = XLog.tag(TAG)
				.t()
				.st(2)
				.b()
				.build();

		mApiService = HttpsRequest.provideClientApi();

		mSubscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (o instanceof KickOffEvent) {
							mKickOffEvent = (KickOffEvent) o;
							isKickOff = true;
							if (activityIsStop) {
								return;
							}
							showKickOffDialog();

						}
					}
				});

			}
		});

		whiteboardFragment.getBoardManager().setNotFoundListener(new BoardManager.NotFoundListener() {
			@Override
			public void noFoundRoom() {
				createRoom();
			}

			@Override
			public void onRoomJoinSuccess(RoomParams roomParams) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						whiteboardFragment.convertPPTX(roomParams.getRoomToken(), "");
					}
				});
			}
		});

	}

	final DemoAPI demoAPI = new DemoAPI();

	//region room
	protected void createRoom() {
		demoAPI.getNewRoom(new DemoAPI.Result() {
			@Override
			public void success(String uuid, String roomToken) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Timber.e("房间的：uuid---->%s", uuid);
						whiteboardFragment.initBoardWithRoomToken(uuid, roomToken);
					}
				});

			}

			@Override
			public void fail(String message) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ToastManager.showShort("获取房间失败：" + message);
					}
				});
			}
		});
	}


	protected void getRoomToken(final String uuid) {
		demoAPI.getRoomToken(uuid, new DemoAPI.Result() {
			@Override
			public void success(String uuid, String roomToken) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Timber.e("获取房间成功---->uuid:%s----->roomToken:%s", uuid, roomToken);
						whiteboardFragment.initBoardWithRoomToken(uuid, roomToken);
					}
				});

			}

			@Override
			public void fail(String message) {
				Timber.e("获取房间失败---->%s", message);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ToastManager.showShort("获取房间失败：" + message);
					}
				});

			}
		});
	}


	@Override
	protected void onStart() {
		super.onStart();
//        initUIandEvent();
	}

	protected abstract void initUIandEvent();

	protected abstract void deInitUIandEvent();

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isFinishing()) {
					return;
				}

				boolean checkPermissionResult = checkSelfPermissions();

				if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
					// so far we do not use OnRequestPermissionsResultCallback
				}
			}
		}, 500);
	}

	private boolean checkSelfPermissions() {
		return checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
				checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA) &&
				checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
	}

	@Override
	protected void onDestroy() {
		deInitUIandEvent();
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
		}
		whiteboardFragment.releaseBoard();
		if (mSubscription != null) {
			mSubscription.unsubscribe();
		}
		super.onDestroy();
	}

	public final void closeIME(View v) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(v.getWindowToken(), 0); // 0 force close IME
		v.clearFocus();
	}

	public final void closeIMEWithoutFocus(View v) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(v.getWindowToken(), 0); // 0 force close IME
	}

	public void openIME(final EditText v) {
		final boolean focus = v.requestFocus();
		if (v.hasFocus()) {
			final Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					boolean result = mgr.showSoftInput(v, InputMethodManager.SHOW_FORCED);
					log.debug("openIME " + focus + " " + result);
				}
			});
		}
	}

	public boolean checkSelfPermission(String permission, int requestCode) {
		log.debug("checkSelfPermission " + permission + " " + requestCode);
		if (ContextCompat.checkSelfPermission(this,
				permission)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{permission},
					requestCode);
			return false;
		}

		if (Manifest.permission.CAMERA.equals(permission)) {
			((BaseApplication) getApplication()).initWorkerThread(appId);
		}
		return true;
	}

	protected RtcEngine rtcEngine() {
		return worker().getRtcEngine();
	}

	protected final WorkerThread worker() {
		return ((BaseApplication) getApplication()).getWorkerThread();
	}

	protected final EngineConfig config() {
		return worker().getEngineConfig();
	}

	protected final MyEngineEventHandler event() {
		return worker().eventHandler();
	}

	public final void showLongToast(final String msg) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[], @NonNull int[] grantResults) {
		log.debug("onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
		switch (requestCode) {
			case ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA);
				} else {
					finish();
				}
				break;
			}
			case ConstantApp.PERMISSION_REQ_ID_CAMERA: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
					((BaseApplication) getApplication()).initWorkerThread(appId);
				} else {
					finish();
				}
				break;
			}
			case ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				} else {
					finish();
				}
				break;
			}
		}
	}


	public void showToastyInfo(String message) {
		Toasty.info(this, message, Toast.LENGTH_SHORT, true).show();
	}

	public void showToastyWarn(String message) {
		Toasty.warning(this, message, Toast.LENGTH_SHORT, true).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isKickOff && mKickOffEvent != null) {
			showKickOffDialog();
			isKickOff = false;
			mKickOffEvent = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		activityIsStop = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		activityIsStop = true;

	}

	private void showKickOffDialog() {
		if (mKickOffEvent.isKickOff()) {
			mAlertDialog = new com.adorkable.iosdialog.AlertDialog(BaseActivity.this)
					.builder()
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setTitle("账号异常")
					.setMsg("您的账号在别的地方进行了登陆 请重新登陆")
					.setPositiveButton("退出", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mAlertDialog.dismiss();
							startActivity(new Intent(BaseActivity.this, WXEntryActivity.class).putExtra("isBadToken", true));
							finish();
						}
					});
			mAlertDialog.show();
		} else {
			mAlertDialog = new com.adorkable.iosdialog.AlertDialog(BaseActivity.this)
					.builder()
					.setTitle("账号异常")
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setMsg("当前登陆信息已失效 请重新登陆")
					.setPositiveButton("退出", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mAlertDialog.dismiss();
							startActivity(new Intent(BaseActivity.this, WXEntryActivity.class).putExtra("isBadToken", true));
							finish();
						}
					});
			mAlertDialog.show();
		}
	}

	@Override
	public void onClassStateChanged(boolean isBegin, long time) {

	}

	@Override
	public void onWhiteboardChanged(String uuid, String roomToken) {
		whiteboardFragment.initBoardWithRoomToken(uuid, roomToken);
	}

	@Override
	public void onLockWhiteboard(boolean locked) {
		whiteboardFragment.disableCameraTransform(locked);
	}

	@Override
	public void onJoinRoomSuccess(String roomToken) {

	}
}

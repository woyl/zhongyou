package com.zhongyou.meet.mobile.business;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.jess.arms.utils.KickOffEvent;
import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.network.ApiService;
import com.zhongyou.meet.mobile.ameeting.network.HttpsRequest;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.NetUtils;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import me.jessyan.autosize.AutoSizeCompat;
import rx.Subscription;
import rx.functions.Action1;

public abstract class BasicActivity extends AppCompatActivity implements View.OnClickListener {

	public final String TAG = getClass().getSimpleName();

	private static final String RELOGINACTION = BuildConfig.APPLICATION_ID + Constant.RELOGIN_ACTION;

	private ReLoginBroadcastReceiver reLoginBroadcastReceiver = new ReLoginBroadcastReceiver();

	protected Context mContext;
	private BaseApplication mMyApp;

	protected ApiClient apiClient;
	protected String userId;
	protected String token;

	protected ProgressDialog progressDialog;
	protected Logger mLogger;

	protected ApiService mApiService;
	private com.adorkable.iosdialog.AlertDialog mAlertDialog;
	private Subscription mSubscription;
	private KickOffEvent mKickOffEvent;
	private boolean isActivityStop;
	private boolean isKickOff;

	public abstract String getStatisticsTag();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;

		IntentFilter filter = new IntentFilter();
		filter.addAction(RELOGINACTION);
		registerReceiver(reLoginBroadcastReceiver, filter);

		mMyApp = (BaseApplication) this.getApplicationContext();
		userId = Preferences.getUserId();
		token = Preferences.getToken();

		apiClient = ApiClient.getInstance();

		mApiService = HttpsRequest.provideClientApi();
		mLogger = XLog.tag(TAG)
				.t()
				.st(2)
				.b()
				.build();


		mSubscription = RxBus.handleMessage(new Action1() {
			@Override
			public void call(Object o) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (o instanceof KickOffEvent) {
							mKickOffEvent = (KickOffEvent) o;
							isKickOff = true;
							if (isActivityStop) {
								return;
							}
							showKickOffDialog();
						}
					}
				});

			}
		});


		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
	}

	private void showKickOffDialog() {
		if (mKickOffEvent.isKickOff()) {
			mAlertDialog = new com.adorkable.iosdialog.AlertDialog(BasicActivity.this)
					.builder()
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setTitle("账号异常")
					.setMsg("您的账号在别的地方进行了登陆 请重新登陆")
					.setPositiveButton("退出", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mAlertDialog.dismiss();
							startActivity(new Intent(BasicActivity.this, WXEntryActivity.class).putExtra("isBadToken", true));
							finish();
						}
					});
			mAlertDialog.show();
		} else {
			mAlertDialog = new com.adorkable.iosdialog.AlertDialog(BasicActivity.this)
					.builder()
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setTitle("账号异常")
					.setMsg("当前登陆信息已失效 请重新登陆")
					.setPositiveButton("退出", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mAlertDialog.dismiss();
							startActivity(new Intent(BasicActivity.this, WXEntryActivity.class).putExtra("isBadToken", true));
							finish();
						}
					});
			mAlertDialog.show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		ZYAgent.onPageStart(this, getStatisticsTag());
		if (isKickOff && mKickOffEvent != null) {
			showKickOffDialog();
			isKickOff = false;
			mKickOffEvent = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		ZYAgent.onPageEnd(this, getStatisticsTag());
		isActivityStop=true;
	}

    @Override
    protected void onStop() {
        super.onStop();
        isActivityStop=true;
    }

    @Override
	protected void onDestroy() {
//        OkHttpUtil.getInstance().cancelTag(this);
		unregisterReceiver(mHomeKeyEventReceiver);
		unregisterReceiver(reLoginBroadcastReceiver);
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
		}
		if (mSubscription != null) {
			mSubscription.unsubscribe();
		}
		cancelDialog();
		super.onDestroy();
	}

	public void showToast(int resId) {
		ToastUtils.showToast(resId);
	}

	public void showToast(String str) {
		ToastUtils.showToast(str);
	}


	protected void showDialog(String message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this, R.style.MyDialog);
			progressDialog.setMessage(message);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	protected void cancelDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}

	protected BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";
		String SYSTEM_HOME_KEY_LONG = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
					//表示按了home键,程序到了后台
					Log.i(TAG, "mHomeKeyEventReceiver SYSTEM_HOME_KEY");
					onHomeKey();
				} else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
					//表示长按home键,显示最近使用的程序列表
					Log.i(TAG, "mHomeKeyEventReceiver SYSTEM_HOME_KEY_LONG");
					onHomeKeyLong();
				}
			}
		}
	};

	protected void onHomeKey() {

	}

	protected void onHomeKeyLong() {

	}

	public static final int MIN_CLICK_DELAY_TIME = 1000;
	private long lastClickTime = 0;

	/**
	 * 点击事件
	 *
	 * @param
	 */
	@Override
	public void onClick(View v) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		//防止重复提交订单，最小点击为1秒
		if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
			lastClickTime = currentTime;

			normalOnClick(v);
			if (!NetUtils.isNetworkConnected(this)) {//not network
				Toast.makeText(this, getResources().getString(R.string.network_err_toast), Toast.LENGTH_SHORT).show();
			} else {//have network
				checkNetWorkOnClick(v);
			}
		}

	}

	/**
	 * 检查网络，如果没有网络的话，就不能点击
	 *
	 * @param v
	 */
	protected void checkNetWorkOnClick(View v) {

	}

	/**
	 * 不用检查网络，可以直接触发的点击事件
	 *
	 * @param v
	 */
	protected void normalOnClick(View v) {

	}

	private AlertDialog dialog;

	class ReLoginBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (BuildConfig.DEBUG) {
				Toast.makeText(context, "40001|40003", Toast.LENGTH_SHORT).show();
			}
			if (!intent.getBooleanExtra("active", false)) {
				if (dialog != null) {
					if (!dialog.isShowing()) {
						dialog.show();
					}
				} else {
					/* createDialog().show();*/
				}
			} else {
				finish();
			}
		}
	}

	private AlertDialog createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_common_ok, null);
		TextView titleText = view.findViewById(R.id.title);
		titleText.setText("您的登录信息已过期，请重新登录");
		Button button = view.findViewById(R.id.ok);
		button.requestFocus();
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				BasicActivity.this.finish();
			}
		});
		builder.setView(view);
		dialog = builder.create();
		return dialog;
	}


	public void showToastyInfo(String message) {
		Toasty.info(this, message, Toast.LENGTH_SHORT, true).show();
	}

	public void showToastyWarn(String message) {
		Toasty.warning(this, message, Toast.LENGTH_SHORT, true).show();
	}

	@Override
	public Resources getResources() {
		AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));
		return super.getResources();
	}

}

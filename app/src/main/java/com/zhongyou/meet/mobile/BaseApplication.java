package com.zhongyou.meet.mobile;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;

import androidx.multidex.MultiDex;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.igexin.sdk.IUserLoggerInterface;
import com.igexin.sdk.PushManager;
import com.llew.huawei.verifier.LoadedApkHuaWei;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zhongyou.meet.mobile.IM.IMManager;
import com.zhongyou.meet.mobile.ameeting.network.AppManager;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entities.StaticResource;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.Logger;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import java.io.File;
import java.net.URISyntaxException;
import cn.jpush.android.api.JPushInterface;
import es.dmoral.toasty.Toasty;
import io.agora.openlive.model.WorkerThread;
import io.socket.client.IO;
import io.socket.client.Socket;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

public class BaseApplication extends com.jess.arms.base.BaseApplication {

	public static final String TAG = "BaseApplication";

	private static BaseApplication instance;


	private Socket mSocket;


	@Override
	public void onCreate() {
		super.onCreate();
		FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
				.showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
				.methodCount(2)         // (Optional) How many method line to show. Default 2
				.methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
				.build();
		instance = this;

		PushManager.getInstance().initialize(this);
		if (BuildConfig.DEBUG){
			PushManager.getInstance().setDebugLogger(this, new IUserLoggerInterface() {
				@Override
				public void log(String s) {
					com.orhanobut.logger.Logger.e("PUSH_LOG"+s);
				}
			});
		}

		/**
		 *
		 *
		 * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
		 * io.rong.push 为融云 push 进程名称，不可修改。
		 */


		if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
				"io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
			/**
			 * IMKit SDK调用第一步 初始化
			 */
			//	//8luwapkv846rl
			IMManager.getInstance().init(this,"8brlm7uf8qod3");

		}
		JPushInterface.init(this);
		JPushInterface.setDebugMode(BuildConfig.DEBUG);
		ActivitStats();


		//规避修改 DisplayMetrics#density使用 dp 布局的系统控件或三方库控件的不良影响
		AutoSizeConfig.getInstance().getUnitsManager()
				.setSupportDP(false)
				.setSupportSP(false)
				.setSupportSubunits(Subunits.PT);

		//????
//		ApiClient.getInstance().urlConfig(staticResCallback);



//		initSocket();
		com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter());

		LogConfiguration config = new LogConfiguration.Builder()
				.logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE)
				.tag("meeting")
				.addInterceptor(new BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
						"blacklist1", "blacklist2", "blacklist3"))
				.build();
		Printer androidPrinter = new AndroidPrinter();             // Printer that print the log using android.util.Log
		Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
				.Builder(new File(UpdateUtils.getFilePath(this), "中幼在线日志").getPath())       // Specify the path to save log file
				.fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
				.flattener(new ClassicFlattener())
				.cleanStrategy(file -> true)
				.build();
		XLog.init(                                                 // Initialize XLog
				config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
				androidPrinter,                                        // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
				filePrinter);


		MultiDex.install(this);

		Toasty.Config.getInstance()
				.setToastTypeface(Typeface.createFromAsset(getAssets(), "PCap Terminal.otf"))
				.allowQueue(false)
				.apply();

		//内存泄露检测工具,开发中最好开启
//        if (BuildConfig.DEBUG) {
//            Log.i(TAG, "debug下开启LeakCanary");
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
//        }


		try {
			LoadedApkHuaWei.hookHuaWeiVerifier(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		if (res != null) {
			Configuration config = res.getConfiguration();
			if (config != null && config.fontScale != 1.0f) {
				config.fontScale = 1.0f;
				res.updateConfiguration(config, res.getDisplayMetrics());
			}
		}
		return res;
	}

	/*private void getHostUrl() {
		ApiClient.getInstance().getHttpBaseUrl(this, new OkHttpCallback<com.alibaba.fastjson.JSONObject>() {

			@Override
			public void onSuccess(com.alibaba.fastjson.JSONObject entity) {

				if (entity.getInteger("errcode") == 0.0) {
					Constant.WEBSOCKETURL = entity.getJSONObject("data").getJSONObject("staticRes").getString("websocket");
					Constant.APIHOSTURL = entity.getJSONObject("data").getJSONObject("staticRes").getString("domain");
					Constant.DOWNLOADURL = entity.getJSONObject("data").getJSONObject("staticRes").getString("apiDownloadUrl");
					com.orhanobut.logger.Logger.e("webSocket:=" + Constant.WEBSOCKETURL);
					com.orhanobut.logger.Logger.e("ApiHost:=" + Constant.APIHOSTURL);
					com.orhanobut.logger.Logger.e("DownLoadUrl:=" + Constant.DOWNLOADURL);
					if (Constant.WEBSOCKETURL == null || Constant.APIHOSTURL == null) {
						return;
					}


				}

			}

			@Override
			public void onFailure(int errorCode, BaseException exception) {
				com.orhanobut.logger.Logger.e(exception.getMessage());
				Toasty.error(getInstance(), exception.getMessage(), Toast.LENGTH_SHORT, true).show();
			}

			@Override
			public void onFinish() {

			}
		});
	}
*/
	public static BaseApplication getInstance() {
		return instance;
	}

	private WorkerThread mWorkerThread;
	private String appId;

	public synchronized void initWorkerThread(String appId) {
		this.appId = appId;
		if (mWorkerThread == null) {
			mWorkerThread = new WorkerThread(getApplicationContext(), appId);
			mWorkerThread.start();

			mWorkerThread.waitForReady();
		}
	}

	public synchronized WorkerThread getWorkerThread() {
		if (mWorkerThread == null) {
			return new WorkerThread(getApplicationContext(), appId);
		}
		return mWorkerThread;
	}

	public  Agora mAgora;

	public Agora getAgora() {
		return mAgora;
	}

	public void setAgora(Agora agora) {
		mAgora = agora;
	}

	public synchronized void deInitWorkerThread() {
		mWorkerThread.exit();
		try {
			mWorkerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mWorkerThread = null;
	}

	public void initSocket() {
		try {
			IO.Options options = new IO.Options();
			options.forceNew = false;
			options.reconnection = true;
			options.reconnectionDelay = 1000;
			options.reconnectionDelayMax = 5000;
			options.reconnectionAttempts = 10;
			options.query = "userId=" + Preferences.getUserId();
			mSocket = IO.socket(Constant.getWEBSOCKETURL(), options);
			Logger.i(TAG, "初始化WebSocket成功");
		} catch (URISyntaxException e) {
			Logger.i(TAG, "初始化WebSocket失败" + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public Socket getSocket() {
		if (mSocket == null) {
			try {
				IO.Options options = new IO.Options();
				options.forceNew = false;
				options.reconnection = true;
				options.reconnectionDelay = 1000;
				options.reconnectionDelayMax = 5000;
				options.reconnectionAttempts = 10;
				options.query = "userId=" + Preferences.getUserId();
				mSocket = IO.socket(Constant.getWEBSOCKETURL(), options);
			} catch (URISyntaxException e) {
			}
		}
		return mSocket;
	}

	private OkHttpCallback staticResCallback = new OkHttpCallback<BaseBean<StaticResource>>() {

		@Override
		public void onSuccess(BaseBean<StaticResource> entity) {

			com.orhanobut.logger.Logger.e(JSON.toJSONString(entity));
			Preferences.setImgUrl(entity.getData().getStaticRes().getImgUrl());
			Preferences.setVideoUrl(entity.getData().getStaticRes().getVideoUrl());
			Preferences.setDownloadUrl(entity.getData().getStaticRes().getDownloadUrl());
			Preferences.setCooperationUrl(entity.getData().getStaticRes().getDownloadUrl());
		}
	};

	//监听当前Activit状态
	private void ActivitStats() {
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

			}

			@Override
			public void onActivityStarted(Activity activity) {

			}

			@Override
			public void onActivityResumed(Activity activity) {
				AppManager.getInstance().setCurrentActivity(activity);
			}

			@Override
			public void onActivityPaused(Activity activity) {

			}

			@Override
			public void onActivityStopped(Activity activity) {

			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

			}

			@Override
			public void onActivityDestroyed(Activity activity) {

			}
		});
	}


}

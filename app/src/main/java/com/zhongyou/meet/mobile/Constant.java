package com.zhongyou.meet.mobile;

/**
 * Created by wufan on 2017/8/2.
 */

public class Constant {
	public static int DEVICE_FIRM = -1;
	public static int Dutation = 2 * 60 * 1000;

	public static String APIHOSTURL = "http://osg.apitest.zhongyouie.cn";
	public static String WEBSOCKETURL = "";
	public static String DOWNLOADURL = "";
	public static final String RELOGIN_ACTION = ".ACTION.RELOGIN";


	public static final String MODEL_CHANGE = "model_change";
	public static final String EQUALLY = "equally";
	public static final String BIGSCREEN = "bigScreen";

	public static final String VIDEO = "video";
	public static final String USERNAME = "USERNAME";
	public static final String PLAYVIDEO = "playVideo";
	public static final String PAUSEVIDEO = "pauseVideo";
	public static final String STOPVIDEO = "stopVideo";

	public static final String KEY_ClOSE_MIC = "ClOSE_MIC";

	public static String KEY_meetingID = "";
	public static String KEY_code = "";
	public static final String force_change_user_info = "force_change_user_info";

	public final static String AGREEMENTURL = "https://qiniu.zhongyouie.cn/agreement.html?v=" + System.currentTimeMillis();
	//https://qiniu.zhongyouie.cn/agreement.html
	public final static String AGREEMENTURL_PRIVATE = "https://qiniu.zhongyouie.cn/privacypolicy.html?v=" + System.currentTimeMillis();
	/**
	 * 当图片大于3M的时候 可以双击放大图片
	 */
	public static final int KEY_BIG_FILE = 3 * 1024 * 1204;

	public static String currentGroupID = "";

	public static boolean isChairMan = false;
	private static boolean debug = BuildConfig.DEBUG;


	public final static int delayTime = 5000;

	//0 为主持人 1 为参会人  2为观众 默认为观众
	public static int videoType = 2;
	public static boolean isNeedRecord = false;

	public static final String KEY_BIND_ALIAS_TAG = "bind_alias_tag";


	/**
	 * 检查更新地址
	 */
   /* public static final String VERSION_UPDATE_URL = DOWNLOADURL+"/dz/app/version/"
            + BuildConfig.APPLICATION_ID+"/android/GA/latest?versionCode=" + BuildConfig.VERSION_CODE;*/

	public final static int NICKNAME_MAX = 8;
	public final static int NICKNAME_MIN = 2;

	public final static int pageSize = 20;

	//签名限制
	public final static int SIGNATURE_MAX = 30;

	//回复限制
	public final static int REPLY_REVIEW_MAX = 1000;


	public static String IMTOKEN = "";

	public static String SMALL_URL_IMG = "";


	public static String getAPIHOSTURL() {

		if (debug) {
//			return "http://osg.apitest.zhongyouie.cn";
			return "https://osgtadmin.zhongyouie.cn";
//			return "https://api.zhongyouie.com";

		} else {
			return "https://api.zhongyouie.com";
		}
	}

	public static String getImageHost() {
		if (debug) {
			return "https://syimage.zhongyouie.com/";
		} else {
			return "https://image.zhongyouie.cn/";
		}


	}

	public static String getWEBSOCKETURL() {
		if (debug) {
			return "http://wstest.zhongyouie.cn/sales";
		} else {
			return "http://ws.zhongyouie.com/sales";
		}

	}

	public static String getDOWNLOADURL() {
		if (debug) {
			return "http://tapi.zhongyouie.cn";
		} else {
			return "https://api.zhongyouie.cn";
		}
	}

}

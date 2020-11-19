package com.zhongyou.meet.mobile;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jess.arms.utils.RxBus;
import com.maning.mndialoglibrary.MToast;
import com.orhanobut.logger.Logger;
import com.zhongyou.meet.mobile.entities.RankInfo;
import com.zhongyou.meet.mobile.entities.RecordData;
import com.zhongyou.meet.mobile.entities.RecordTotal;
import com.zhongyou.meet.mobile.entities.StaticRes;
import com.zhongyou.meet.mobile.entities.User;
import com.zhongyou.meet.mobile.entities.UserData;
import com.zhongyou.meet.mobile.entities.Version;
import com.zhongyou.meet.mobile.entities.Wechat;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.entities.base.BaseErrorBean;
import com.zhongyou.meet.mobile.event.UserUpdateEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.DeviceIdUtils;
import com.zhongyou.meet.mobile.utils.Installation;
import com.zhongyou.meet.mobile.utils.Login.LoginHelper;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by whatisjava on 16/4/12.
 */
public class ApiClient {

	public static final String PAGE_NO = "pageNo";
	public static final String PAGE_SIZE = "pageSize";


	private OkHttpUtil okHttpUtil;

	private static class SingletonHolder {
		private static ApiClient instance = new ApiClient();
	}

	public static ApiClient getInstance() {
		return SingletonHolder.instance;
	}

	private ApiClient() {
		okHttpUtil = OkHttpUtil.getInstance();
	}

	public static String jointParamsToUrl(String url, Map<String, String> params) {
		if (params != null && params.size() > 0) {
			Uri uri = Uri.parse(url);
			Uri.Builder b = uri.buildUpon();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				b.appendQueryParameter(entry.getKey(), entry.getValue());
			}
			return b.build().toString();
		}
		return url;
	}

	private static Map<String, String> getCommonHead() {
		Map<String, String> params = new HashMap<>();
		params.put("Content-Type", "application/json;charset=UTF-8");
		params.put("Content-Type", "application/json;charset=UTF-8");
		if (!TextUtils.isEmpty(Preferences.getToken())) {
			params.put("Authorization", "Token " + Preferences.getToken());
		}

		params.put("DeviceUuid", Installation.id(BaseApplication.getInstance()) == null ? DeviceIdUtils.getDeviceId() : Installation.id(BaseApplication.getInstance()));

		params.put("User-Agent", "HRZY_HOME"
				+ "_"
				+ BuildConfig.APPLICATION_ID
				+ "_"
				+ BuildConfig.VERSION_NAME
				+ "_"
				+ DeviceIdUtils.getDeviceId() + "(android_OS_"
				+ Build.VERSION.RELEASE + ";" + Build.MANUFACTURER
				+ "_" + Build.MODEL + ")");

		return params;
	}

	public void expostorOnlineStats(Object tag, OkHttpCallback callback, Map<String, Object> values) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/user/expostor/online/stats", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}

	//删除聊天内容/osg/app/forum/{forumId}

	public void expostorDeleteChatMessage(Object tag, OkHttpCallback callback, String forumId) {

		okHttpUtil.delete(Constant.getAPIHOSTURL() + "/osg/app/forum/" + forumId, getCommonHead(), null, callback);
	}

	public void postViewLog(String meetingid, Object tag, OkHttpCallback callback) {
		okHttpUtil.post(Constant.getAPIHOSTURL() + "/osg/app/forum/" + meetingid + "/view/log", getCommonHead(), null, callback);
	}

	//提交聊天内容
	public void expostorPostChatMessage(Object tag, OkHttpCallback callback, Map<String, Object> values) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/forum", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}

	public void meetingJoinStats(Object tag, OkHttpCallback callback, Map<String, Object> values) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/join/stats", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}

	public void meetingHostStats(Object tag, OkHttpCallback callback, Map<String, Object> values) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/host/stats", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}

	public void searchMeeting(Object tag, String meetingName, OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/meeting/all?title=" + meetingName, getCommonHead(), null, callback);
	}

	public void getAllMeeting(Object tag, String meetingName, int type, int pageNumber, OkHttpCallback callback) {
		String url = Constant.getAPIHOSTURL() + "/osg/app/meeting/listPage";
		url += "?pageNo=" + pageNumber;

		if (!TextUtils.isEmpty(meetingName))
			url += "&title=" + meetingName;
		okHttpUtil.get(url, getCommonHead(), null, callback);
	}

	public void getAllForumMeeting(Object tag, Map<String, String> params, OkHttpCallback callback) {
		String url = Constant.getAPIHOSTURL() + "/osg/app/forum";
		okHttpUtil.get(url, getCommonHead(), params, callback);
	}

	///osg/app/forum/{meetingId}/content
	public void getChatMessages(Object tag, String meetingId, String pageNo, String pageSize, OkHttpCallback callback) {
		String url = Constant.getAPIHOSTURL() + "/osg/app/forum/" + meetingId + "/content";
		Map<String, String> params = new HashMap<>();
		params.put("pageNo", pageNo);
		params.put("pageSize", pageSize);
		okHttpUtil.get(url, getCommonHead(), params, callback);
	}

	public void verifyRole(Object tag, OkHttpCallback callback, Map<String, Object> values) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/verify", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}

	public void joinMeeting(Object tag, OkHttpCallback callback, Map<String, Object> values) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/join", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}

	public void getMeeting(Object tag, String meetingId, OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/meeting/" + meetingId, getCommonHead(), null, callback);
	}

	public void getMeetingHost(Object tag, String meetingId, OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/meeting/" + meetingId + "/host", getCommonHead(), null, callback);
	}

	public void finishMeeting(Object tag, String meetingId, int attendance, OkHttpCallback callback) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/" + meetingId + "/end?attendance=" + attendance, getCommonHead(), null, callback, tag);
	}

	//  软件更新
	public void versionCheck(Object tag, OkHttpCallback<BaseBean<Version>> callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/version/"
				+ BuildConfig.APPLICATION_ID + "/android/GA/latest?versionCode=" + BuildConfig.VERSION_CODE, tag, callback);
	}

	//获取全局配置信息接口
	public void urlConfig(OkHttpCallback<BaseBean<StaticRes>> callback) {
		okHttpUtil.get(Constant.getDOWNLOADURL() + "/dz/app/config", null, null, callback);
	}

	//获取大区（中心）接口
	public void districts(OkHttpCallback callback, String parentId) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user/district?parentId=" + parentId, getCommonHead(), null, callback);
	}

	//  注册设备信息
	public void deviceRegister(Object tag, String jsonStr, OkHttpCallback callback) {
		Logger.e(Constant.getAPIHOSTURL());
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/device", getCommonHead(), jsonStr, callback, tag);
	}

	//获取大区（中心）接口
	public void meetingAdmin(OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user/meeting/admin", getCommonHead(), null, callback);
	}

	/**
	 * 收到客户退出通知或者主动退出房间时请求
	 *
	 * @param recordId
	 * @param responseCallback
	 * @return
	 */
	public void startOrStopOrRejectCallExpostor(String recordId, String state, OkHttpCallback responseCallback) {
		okHttpUtil.put(Constant.getAPIHOSTURL() + "/osg/app/call/record/" + recordId + "?state=" + state, getCommonHead(), null, responseCallback);
	}

	/**
	 * 更新设备信息，目前只有socketid的更新
	 *
	 * @param tag
	 * @param deviceId
	 * @param responseCallback
	 * @param jsonStr
	 */
	public void updateDeviceInfo(Object tag, String deviceId, OkHttpCallback responseCallback, String jsonStr) {
		okHttpUtil.putJson(Constant.getAPIHOSTURL() + "/osg/app/device/" + deviceId, getCommonHead(), jsonStr, responseCallback, tag);
	}

	/**
	 * 获取声网参数
	 *
	 * @param params
	 * @param responseCallback
	 */
	public void getAgoraKey(Object tag, Map<String, String> params, OkHttpCallback responseCallback) {
		okHttpUtil.get(jointParamsToUrl(Constant.getAPIHOSTURL() + "/osg/agora/key/osgV2", params), tag, responseCallback);
	}

	/**
	 *
	 */
	public void requestWxToken(Object tag, OkHttpCallback callback) {
		Map<String, String> params = new HashMap<>();
		OkHttpUtil.getInstance().get("https://api.weixin.qq.com/sns/oauth2/access_token", params, tag, callback);
	}

	/**
	 * 通过微信code登录
	 *
	 * @param code
	 * @param state
	 * @param tag
	 * @param callback
	 */
	public void requestWechat(String code, String state, Object tag, OkHttpCallback callback) {
		Map<String, String> params = new HashMap<>();
		params.put("code", code);
		params.put("state", state);
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/wechat", params, tag, callback);
	}

	/**
	 * 获取用户信息,每次启动刷新用户数据
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestUser(Object tag, OkHttpCallback<BaseBean<UserData>> callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user", getCommonHead(), null, callback, tag);
	}

	public void requestUserNew(Object tag, OkHttpCallback<com.alibaba.fastjson.JSONObject> callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user", getCommonHead(), null, callback, tag);
	}

	public void requestUser() {
		if (!Preferences.isLogin()) {
			return;
		}
		ApiClient.getInstance().requestUser(this, new OkHttpCallback<BaseBean<UserData>>() {
			@Override
			public void onSuccess(BaseBean<UserData> entity) {
				if (entity == null || entity.getData() == null || entity.getData().getUser() == null) {
					MToast.makeTextShort(BaseApplication.getInstance(), "用户信息不存在");
					return;
				}
				User user = entity.getData().getUser();
				Wechat wechat = entity.getData().getWechat();
				LoginHelper.savaUser(user);
//                initCurrentItem();
				if (wechat != null) {
					LoginHelper.savaWeChat(wechat);
				}
				RxBus.sendMessage(new UserUpdateEvent());

			}
		});
	}

	/**
	 * 设置用户信息
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestUserExpostor(Object tag, Map<String, String> params, OkHttpCallback callback) {
		okHttpUtil.putJson(Constant.getAPIHOSTURL() + "/osg/app/user/expostor/" + Preferences.getUserId(), getCommonHead(),
				OkHttpUtil.getGson().toJson(params), callback, tag);
	}

	/**
	 * 心跳加在线状态
	 *
	 * @param tag
	 * @param params
	 * @param callback
	 */
	public void requestUserExpostorState(Object tag, Map<String, String> params, OkHttpCallback callback) {
		okHttpUtil.putJson(Constant.getAPIHOSTURL() + "/osg/app/user/expostor/" + Preferences.getUserId() + "/state"
				, getCommonHead(), OkHttpUtil.getGson().toJson(params), callback, tag);
	}

	/**
	 * 获取七牛图片上传token
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestQiniuToken(Object tag, OkHttpCallback callback) {
		//使用唷唷兔地址
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/resource/uploadtoken/image", tag, callback);
	}

	/**
	 * 获取导购通话时间
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestRecordTotal(Object tag, OkHttpCallback<BaseBean<RecordTotal>> callback) {
		//使用唷唷兔地址
		String userId = Preferences.getUserId();
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/call/expostor/" + userId + "/record/total", getCommonHead(), null, callback, tag);
	}

	/**
	 * 获取导购通话记录
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestRecord(Object tag, String starFilter, String pageNo, String pageSize, OkHttpCallback<BaseBean<RecordData>> callback) {
		//使用唷唷兔地址
		String userId = Preferences.getUserId();
		Map<String, String> params = new HashMap<>();
		if (!TextUtils.isEmpty(starFilter)) {
			params.put("starFilter", "1");
		}
		params.put("pageNo", pageNo);
		params.put("pageSize", pageSize);
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/call/expostor/" + userId + "/record", getCommonHead(), params, callback, tag);
	}

	/**
	 * 获得手机验证码
	 *
	 * @param tag
	 * @param mobile
	 */
	public void requestVerifyCode(Object tag, String mobile, OkHttpCallback<BaseErrorBean> callback) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("mobile", mobile);
			okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/user/verifyCode", getCommonHead(), jsonObject.toString(), callback, tag);
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.e(e.getMessage());
		}

	}

	/**
	 * 获取导购员评分
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestRankInfo(Object tag, OkHttpCallback<BaseBean<RankInfo>> callback) {
		//使用唷唷兔地址
		String userId = Preferences.getUserId();
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/call/rankInfo/" + userId, getCommonHead(), null, callback, tag);
	}

	public void requestReplayComment(Object tag, String callRecordId, String replyRating, OkHttpCallback<BaseErrorBean> callback) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("callRecordId", callRecordId);
			jsonObject.put("replyRating", replyRating);
			okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/call/replyComment", getCommonHead(), jsonObject.toString(), callback, tag);
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.e(e.getMessage());
		}

	}

	/**
	 * 手机号携带验证码进行用户信息校验
	 *
	 * @param tag
	 * @param params
	 * @param callback
	 */
	public void requestLoginWithPhoneNumber(Object tag, Map<String, String> params, OkHttpCallback callback) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/user/mobile/auth", getCommonHead(), JSON.toJSONString(params), callback, tag);
	}

	/**
	 * 获取用户类型接口
	 *
	 * @param tag
	 * @param callback
	 */
	public void requestPostType(Object tag, OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user/post/type", tag, callback);
	}

	/**
	 * 获取用户网格接口
	 *
	 * @param tag
	 * @param params
	 * @param callback
	 */
	public void requestGrid(Object tag, Map<String, String> params, OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user/area/grid", params, tag, callback);
	}

	/**
	 * 获取用户接口
	 *
	 * @param tag
	 * @param params
	 * @param callback
	 */
	public void requestCustom(Object tag, Map<String, String> params, OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user/area/custom", params, tag, callback);
	}

	public void meetingMaterials(Object tag, OkHttpCallback callback, String meetingId) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/meeting/materials?meetingId=" + meetingId, getCommonHead(), null, callback);
	}

	public void meetingMaterial(Object tag, OkHttpCallback callback, String materialsId) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/meeting/materials/" + materialsId, getCommonHead(), null, callback);
	}

	public void meetingSetMaterial(Object tag, OkHttpCallback callback, String meetingId, String materialId) {
		okHttpUtil.post(Constant.getAPIHOSTURL() + "/osg/app/meeting/" + meetingId + "/materials/" + materialId, getCommonHead(), null, callback);
	}

	public void meetingLeaveTemp(Object tag, Map<String, String> params, OkHttpCallback callback, String meetingId) {
		okHttpUtil.post(Constant.getAPIHOSTURL() + "/osg/app/meeting/" + meetingId + "/leave/temp", getCommonHead(), params, callback);
	}

	public void channelCount(Object tag, OkHttpCallback callback, String meetingId, Map<String, String> params) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/" + meetingId + "/user/count", getCommonHead(), JSON.toJSONString(params), callback, tag);
	}

	/**
	 * 会议参会人摄像头抓拍
	 * /osg/app/meeting/screenshot
	 *
	 * @param tag
	 * @param values
	 * @param callback
	 */
	public void meetingScreenshot(Object tag, Map<String, Object> values, OkHttpCallback callback) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/screenshot", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}

	/**
	 * 获取该用户是否是会议管理员
	 * /osg/app/user/meeting/admin
	 */
	public void requestMeetingAdmin(Object tag, OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/app/user/meeting/admin", getCommonHead(), null, callback);
	}


	/**
	 * 获取地址前缀
	 */

	public void getImageUrlPath(Object tag, OkHttpCallback callback) {
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/resource/host/image", tag, callback);
	}

	/**
	 * 获取服务器地址
	 */

	public void getHttpBaseUrl(Object tag, OkHttpCallback callback) {
		String env = "test";
		if (!BuildConfig.DEBUG) {
			env = "formal";
		}
		okHttpUtil.get(Constant.getAPIHOSTURL() + "/osg/" + env + "/android/config", tag, callback);
	}

	/**
	 * 开始录制
	 */
	public void startRecordVideo(Object tag, Map<String, String> values, OkHttpCallback callback) {
//        okHttpUtil.post(BuildConfig.API_DOMAIN_NAME+"/osg/app/meeting/startRecordVideo",getCommonHead(),values,callback);
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/startRecordVideo", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}


	/**
	 * 结束录制
	 */
	public void stopRecordVideo(Object tag, Map<String, String> values, OkHttpCallback callback) {
		okHttpUtil.postJson(Constant.getAPIHOSTURL() + "/osg/app/meeting/stopRecordVideo", getCommonHead(), JSON.toJSONString(values), callback, tag);
	}
}

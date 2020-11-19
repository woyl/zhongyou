package com.zhongyou.meet.mobile.persistence;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.utils.MMKVHelper;


public class Preferences {
    private static final String tag = Preferences.class.getSimpleName();

    private static final String PREFERENCE_TOKEN = "token";

    private static final String PREFERENCE_USER_ID = "u_id";

    private static final String PREFERENCE_USER_NAME = "u_name";
    private static final String PREFERENCE_USER_MOBILE = "u_mobile";
    private static final String PREFERENCE_USER_ADDRESS = "u_address";
    private static final String PREFERENCE_USER_DISTRICT = "u_district";
    private static final String PREFERENCE_USER_DISTRICT_ID = "u_district_id";
    private static final String PREFERENCE_USER_PHOTO = "u_photo";
    private static final String PREFERENCE_USER_SIGNATURE = "u_signature";
    private static final String PREFERENCE_USER_AREA_INFO = "u_area_info";
    private static final String PREFERENCE_USER_AREA_NAME = "u_area_name";
    private static final String PREFERENCE_USER_POST_TYPE = "u_post_type";
    private static final String PREFERENCE_USER_POST_TYPE_ID = "u_post_type_id";
    private static final String PREFERENCE_USER_GRID = "u_grid";
    private static final String PREFERENCE_USER_GRID_ID = "u_grid_id";
    private static final String PREFERENCE_USER_CUSTOM = "u_custom";
    private static final String PREFERENCE_USER_CUSTOM_ID = "u_custom_id";
    private static final String PREFERENCE_USER_RANK = "u_rank";
    private static final String PREFERENCE_USER_AUDIT_STATUS = "u_audit_status";

    private static final String PREFERENCE_MEETING_ID = "meeting_id";
    private static final String PREFERENCE_MEETING_TRACE_ID = "meeting_trace_id";

    private static final String PREFERENCE_STUDENT_ID = "s_id";

    public static final String PREFERENCE_WEIXIN_HEAD = "weixin_head";
    public static final String WEIXINNICKNAME = "weixinNickName";

    private static final String PREFERENCE_UUID = "uuid";
    private static final String PREFERENCE_IMTOKEN = "imToken";

    /**
     * url前缀要持久化保存
     **/
    private static String PREFERENCE_IMG_URL = "imgUrl";
    private static String PREFERENCE_VIDEO_URL = "videoUrl";
    private static String PREFERENCE_DOWNLOAD_URL = "downloadUrl";
    private static String PREFERENCE_COOPERATION_URL = "cooperationUrl";


    public static void setStudentId(String studentId) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_STUDENT_ID, studentId);
        if (!editor.commit()) {
            Log.d(tag, "student id save failure");
        } else {
            Log.d(tag, "student id save success");
        }
    }

    public static String getMeetingTraceId() {
        return getPreferences().getString(PREFERENCE_MEETING_TRACE_ID, null);
    }

    public static void setMeetingTraceId(String meetingTraceId) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_MEETING_TRACE_ID, meetingTraceId);
        if (!editor.commit()) {
            Log.d(tag, "meetingTraceId save failure");
        } else {
            Log.d(tag, "meetingTraceId save success");
        }
    }

    public static String getMeetingId() {
        return getPreferences().getString(PREFERENCE_MEETING_ID, null);
    }


    public static void setWeixinNickname(String nickname) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(WEIXINNICKNAME, nickname);
        if (!editor.commit()) {
            Log.d(tag, "student id save failure");
        } else {
            Log.d(tag, "student id save success");
        }
    }

    public static String getWeiXinNickName(){
        return getPreferences().getString(WEIXINNICKNAME, "");
    }
    public static void setMeetingId(String meetingId) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_MEETING_ID, meetingId);
        if (!editor.commit()) {
            Log.d(tag, "meetingId save failure");
        } else {
            Log.d(tag, "meetingId save success");
        }
    }

    public static String getUserId() {
        return getPreferences().getString(PREFERENCE_USER_ID, "");
    }

    public static void setUserId(String userId) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_ID, userId);
        if (!editor.commit()) {
            Log.d(tag, "User id save failure");
        } else {
            Log.d(tag, "User id save success");
        }
    }

    public static int getUserRank() {
        return getPreferences().getInt(PREFERENCE_USER_RANK, 0);
    }

    public static void setUserRank(int rank) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(PREFERENCE_USER_RANK, rank);
        if (!editor.commit()) {
            Log.d(tag, "User rank save failure");
        } else {
            Log.d(tag, "User rank save success");
        }
    }

    public static int getUserAuditStatus() {
        return getPreferences().getInt(PREFERENCE_USER_AUDIT_STATUS, 0);
    }

    public static void setUserAuditStatus(int rank) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(PREFERENCE_USER_AUDIT_STATUS, rank);
        if (!editor.commit()) {
            Log.d(tag, "User AuditStatus save failure");
        } else {
            Log.d(tag, "User AuditStatus save success");
        }
    }

    public static String getUserMobile() {
        return getPreferences().getString(PREFERENCE_USER_MOBILE, "");
    }

    public static void setUserMobile(String userMobile) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_MOBILE, userMobile);
        if (!editor.commit()) {
            Log.d(tag, "User mobile save failure");
        } else {
            Log.d(tag, "User mobile save success");
        }
    }

    //获取用户名
    public static String getUserName() {
        return getPreferences().getString(PREFERENCE_USER_NAME, "");
    }

    //设置用户名
    public static void setUserName(String userName) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_NAME, userName);
        if (!editor.commit()) {
            Log.d(tag, "User name save failure");
        } else {
            Log.d(tag, "User name save success");
        }
    }

    //获取用户地理位置
    public static String getUserAddress() {
        return getPreferences().getString(PREFERENCE_USER_ADDRESS, "");
    }

    //设置用户地理位置
    public static void setUserAddress(String userName) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_ADDRESS, userName);
        if (!editor.commit()) {
            Log.d(tag, "User address save failure");
        } else {
            Log.d(tag, "User address save success");
        }
    }

    //获取用户所属中心（大区）
    public static String getUserDistrict() {
        return getPreferences().getString(PREFERENCE_USER_DISTRICT, "");
    }

    //设置用户所属中心（大区）
    public static void setUserDistrict(String district) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_DISTRICT, district);
        if (!editor.commit()) {
            Log.d(tag, "User district save failure");
        } else {
            Log.d(tag, "User address save success");
        }
    }

    //获取用户所属中心ID（大区）
    public static String getUserDistrictId() {
        return getPreferences().getString(PREFERENCE_USER_DISTRICT_ID, "");
    }

    //设置用户所属中心ID（大区）
    public static void setUserDistrictId(String districtId) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_DISTRICT_ID, districtId);
        if (!editor.commit()) {
            Log.d(tag, "User district id save failure");
        } else {
            Log.d(tag, "User district id save success");
        }
    }

    //获取用户类型
    public static String getUserPostType() {
        return getPreferences().getString(PREFERENCE_USER_POST_TYPE, "");
    }

    //设置用户类型
    public static void setUserPostType(String postType) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_POST_TYPE, postType);
        if (!editor.commit()) {
            Log.d(tag, "User postType save failure");
        } else {
            Log.d(tag, "User postType save success");
        }
    }

    //获取用户类型id
    public static String getUserPostTypeId() {
        return getPreferences().getString(PREFERENCE_USER_POST_TYPE_ID, "");
    }

    //设置用户类型id
    public static void setUserPostTypeId(String postType) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_POST_TYPE_ID, postType);
        if (!editor.commit()) {
            Log.d(tag, "User postType id save failure");
        } else {
            Log.d(tag, "User postType id save success");
        }
    }

    //获取用户网格
    public static String getUserGrid() {
        return getPreferences().getString(PREFERENCE_USER_GRID, "");
    }

    //设置用户网格
    public static void setUserGrid(String grid) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_GRID, grid);
        if (!editor.commit()) {
            Log.d(tag, "User grid save failure");
        } else {
            Log.d(tag, "User grid save success");
        }
    }

    //获取用户网格id
    public static String getUserGridId() {
        return getPreferences().getString(PREFERENCE_USER_GRID_ID, "");
    }

    //设置用户网格id
    public static void setUserGridId(String grid) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_GRID_ID, grid);
        if (!editor.commit()) {
            Log.d(tag, "User grid id save failure");
        } else {
            Log.d(tag, "User grid id save success");
        }
    }

    //移除网格信息、网格信息ID
    public static void removeUserGridInfo() {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(PREFERENCE_USER_GRID);
        editor.remove(PREFERENCE_USER_GRID_ID);
        if (!editor.commit()) {
            Log.d(tag, "User grid info remove failure");
        } else {
            Log.d(tag, "User grid info remove success");
        }
    }

    //获取客户
    public static String getUserCustom() {
        return getPreferences().getString(PREFERENCE_USER_CUSTOM, "");
    }

    //设置客户
    public static void setUserCustom(String custom) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_CUSTOM, custom);
        if (!editor.commit()) {
            Log.d(tag, "User custom save failure");
        } else {
            Log.d(tag, "User custom save success");
        }
    }

    //获取客户id
    public static String getUserCustomId() {
        return getPreferences().getString(PREFERENCE_USER_CUSTOM_ID, "");
    }

    //设置客户id
    public static void setUserCustomId(String custom) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_CUSTOM_ID, custom);
        if (!editor.commit()) {
            Log.d(tag, "User custom id save failure");
        } else {
            Log.d(tag, "User custom id save success");
        }
    }

    //移除客户信息、客户信息ID
    public static void removeUserCustomInfo() {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(PREFERENCE_USER_CUSTOM);
        editor.remove(PREFERENCE_USER_CUSTOM_ID);
        if (!editor.commit()) {
            Log.d(tag, "User custom info remove failure");
        } else {
            Log.d(tag, "User custom info remove success");
        }
    }

    public static String getUserPhoto() {
        return getPreferences().getString(PREFERENCE_USER_PHOTO, "");
    }

    public static void setUserPhoto(String userName) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_PHOTO, userName);
        if (!editor.commit()) {
            Log.d(tag, "User photo save failure");
        } else {
            Log.d(tag, "User photo save success");
        }
    }

    public static void setAreaName(String areaName) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_AREA_NAME, areaName);
        if (!editor.commit()) {
            Log.d(tag, "User areaname save failure");
        } else {
            Log.d(tag, "User areaname save success");
        }
    }

    public static String getAreaName() {
        return getPreferences().getString(PREFERENCE_USER_AREA_NAME, null);
    }

    public static void setAreaInfo(String areaInfo) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_AREA_INFO, areaInfo);
        if (!editor.commit()) {
            Log.d(tag, "User Address save failure");
        } else {
            Log.d(tag, "User Address save success");
        }
    }

    public static String getAreaInfo() {
        return getPreferences().getString(PREFERENCE_USER_AREA_INFO, null);
    }

    public static String getUserSignature() {
        return getPreferences().getString(PREFERENCE_USER_SIGNATURE, "");
    }

    public static void setUserSignature(String userName) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_USER_SIGNATURE, userName);
        if (!editor.commit()) {
            Log.d(tag, "User Signature save failure");
        } else {
            Log.d(tag, "User Signature save success");
        }
    }

    public static String getToken() {
        return getPreferences().getString(PREFERENCE_TOKEN, null);
    }

    public static void setToken(String token) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_TOKEN, token);
        if (!editor.commit()) {
            Log.d(tag, "Token save failure");
        } else {
            Log.d(tag, "Token save success");
        }
    }

    public static SharedPreferences getPreferences() {
//        return PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance());
        return BaseApplication.getInstance().getSharedPreferences("remote", Context.MODE_MULTI_PROCESS);
    }

    public static void clear() {
        setToken(null);
        setUserId(null);
        setStudentId(null);
        setImToken("");
        getPreferences().edit().clear().apply();
        MMKVHelper.getInstance().loginOut();
    }

    public static boolean isLogin() {
        return (!TextUtils.isEmpty(getToken()));
    }

    public static String getImgUrl() {
        return Constant.getImageHost();
//        return getPreferences().getString(PREFERENCE_IMG_URL, BuildConfig.DEBUG?"http://syimage.zhongyouie.com/":"http://image.zhongyouie.com/");
    }

    public static void setImgUrl(String str) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_IMG_URL, str);
        if (!editor.commit()) {
            Log.d(tag, "setImgUrl save failure");
        } else {
            Log.d(tag, "setImgUrl save success");
        }
    }

    public static void setImToken(String imToken){
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_IMTOKEN, imToken);
        if (!editor.commit()) {
            Log.d(tag, "setImgUrl save failure");
        } else {
            Log.d(tag, "setImgUrl save success");
        }
    }

    public static String getImToken(){
        return getPreferences().getString(PREFERENCE_IMTOKEN, "");
    }

    public static void setVideoUrl(String str) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_VIDEO_URL, str);
        if (!editor.commit()) {
            Log.d(tag, "setVideoUrl save failure");
        } else {
            Log.d(tag, "setVideoUrl save success");
        }
    }

    public static void setDownloadUrl(String str) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_DOWNLOAD_URL, str);
        if (!editor.commit()) {
            Log.d(tag, "setDownloadUrl save failure");
        } else {
            Log.d(tag, "setDownloadUrl save success");
        }
    }

    public static void setCooperationUrl(String str) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_COOPERATION_URL, str);
        if (!editor.commit()) {
            Log.d(tag, "setCooperationUrl save failure");
        } else {
            Log.d(tag, "setCooperationUrl save success");
        }
    }

    public static String getUUID() {
        return getPreferences().getString(PREFERENCE_UUID, null);
    }

    public static void setUUID(String uuid) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_UUID, uuid);
        if (!editor.commit()) {
            Log.d(tag, "UUID save failure");
        } else {
            Log.d(tag, "UUID save success");
        }
    }

    public static void setWeiXinHead(String weiXinHead) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREFERENCE_WEIXIN_HEAD, weiXinHead);
        if (!editor.commit()) {
            Log.d(tag, "WeiXinHead save failure");
        } else {
            Log.d(tag, "WeiXinHead save success");
        }
    }

    public static String getWeiXinHead(){
        return getPreferences().getString(PREFERENCE_WEIXIN_HEAD, "");
    }

    /**
     * 没有设置完用户信息
     *
     * @return
     */
    public static boolean isUserinfoEmpty() {
        return (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(MMKVHelper.ADDRESS))
                || TextUtils.isEmpty(Preferences.getUserPostType()));
    }

}

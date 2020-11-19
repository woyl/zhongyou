package com.zhongyou.meet.mobile.utils;

import com.tencent.mmkv.MMKV;

import timber.log.Timber;

/**
 * @author golangdorid@gmail.com
 * @date 2020/3/24 8:29 AM.
 * @
 */
public class MMKVHelper {

	public static MMKVHelper helper;
	MMKV mkv;


	/**
	 * 设置别名
	 */
	public static final String KEY_SETALIASSUCCESS = "set_alia_ssuccess";
	public static final String KEY_CHECK_NOTIFY = "check_notify";
	/**
	 * 用户昵称
	 */
	public static final String USERNICKNAME = "userNickName";
	/**
	 * 用户手机号
	 */
	public static final String MOBILE = "mobile";
	/**
	 * 用户地址
	 */
	public static final String ADDRESS = "ADDRESS";
	/**
	 * 用户头像
	 */
	public static final String PHOTO = "photo";
	public static final String PHOTO_OLD = "photo_old";

	/**
	 * 用户的token
	 */
	public static final String TOKEN = "token";
	/**
	 * 用户区域
	 * 西南-成都-中江县西城电器有限公司
	 */
	public static final String AREAINFO = "areainfo";

	/**
	 * 用户城市
	 * 成都
	 */
	public static final String AREANAME = "areaName";

	/**
	 * 用户公司
	 * 中江县西城电器有限公司
	 */
	public static final String CUSTOMNAME = "customName";

	/**
	 * 用户的ID
	 */
	public static final String ID = "id";
	/**
	 * 用户类型
	 * 幼儿园用户
	 */
	public static final String POSTTYPENAME = "postTypeName";
	/**
	 * 微信头像
	 */
	public static final String WEIXINHEAD = "weixinhead";


	/**
	 * 微信昵称
	 */
	public static final String weixinNickName = "WEIXINNICKNAME";

	/**
	 * 用户的幼儿园名称
	 */
	public static final String KEY_UserSchoolName = "USERSCHOOLNAME";

	/**
	 * 麦克风状态
	 */
	public static final String MICROPHONE_STATE = "microphone_state";
	/**
	 * 相机状态
	 */
	public static final String CAMERA_STATE = "Camera_STATE";

	public static final String KEY_MEETING_UID = "MEETING_UID";

	public static final String KEY_meetingQuilty = "meetingQuilty";

	/**美颜开关*/
	public static final String BEAUTIFULL = "beuatiful";

//	/**是否是首次登陆*/
//	public static final String FIRST_LOGIN = "first_login";

	private MMKVHelper() {
		mkv = MMKV.defaultMMKV();
	}

	public static MMKVHelper getInstance() {
		if (helper == null) {
			helper = new MMKVHelper();
		}
		return helper;
	}


	public void saveUserNickName(String userNickName) {
		Timber.e(userNickName);
		mkv.encode(USERNICKNAME, userNickName);
	}

	public void saveMobie(String mobile) {
		mkv.encode(MOBILE, mobile);
	}

	public void saveAddress(String address) {
		mkv.encode(ADDRESS, address);
	}

	public void savePhoto(String photo) {
		mkv.encode(PHOTO, photo);
	}

	public void savePhotoOld(String photoOld){
		mkv.encode(PHOTO_OLD,photoOld);
	}

	public void saveTOKEN(String token) {
		mkv.encode(TOKEN, token);
	}

	public void saveAreainfo(String areainfo) {
		mkv.encode(AREAINFO, areainfo);
	}

	public void saveID(String Id) {
		mkv.encode(ID, Id);
	}

	public void savePostTypeName(String PostTypeName) {
		mkv.encode(POSTTYPENAME, PostTypeName);
	}

	public void saveWeiXinHead(String weixinHead) {
		mkv.encode(WEIXINHEAD, weixinHead);
	}

	public void saveBeaultiful(boolean isOpen) {mkv.encode(BEAUTIFULL,isOpen);}

//	public void saveFirstLogin(String isFirst) {mkv.encode(FIRST_LOGIN,isFirst);}

	public void loginOut() {
		mkv.clearAll();
		mkv.clearMemoryCache();
	}
}

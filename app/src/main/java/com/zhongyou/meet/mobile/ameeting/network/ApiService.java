package com.zhongyou.meet.mobile.ameeting.network;


import com.alibaba.fastjson.JSONObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lozzow on 2016/12/8.
 * <p>
 * 祖传代码……
 * </p>
 */

public interface ApiService {


	//获取Token
	@POST("/osg/app/rongcloud/getToken")
	Observable<JSONObject> getToken(@Body JSONObject jsonObject);

	//创建并加入群组
	@POST("/osg/app/rongcloud/groupCreateAndJoin")
	Observable<JSONObject> groupCreateAndJoin(@Body JSONObject jsonObject);


	//查询群组成员
	@POST("/osg/app/rongcloud/queryGroupUser")
	Observable<JSONObject> queryGroupUser(@Body JSONObject jsonObject);

	//查询用户信息
	@POST("/osg/app/rongcloud/queryUserInfo")
	Observable<JSONObject> queryUserInfo(@Body JSONObject jsonObject);


	//版本检测
	@GET("/osg/app/version/{applicationID}/android/GA/latest")
	Observable<JSONObject> versionCheck(@Path("applicationID") String applicationID, @Query("versionCode") String versioin_code);

	//设备注册
	@POST("/osg/app/device")
	Observable<JSONObject> registerDevice(@Body JSONObject jsonObject);

	//获取用户信息
	@GET("/osg/app/user")
	Observable<JSONObject> getUserInfo();

	//会议状态(我tm也不知道这个接口是干啥的)
	///osg/app/meeting/join/stats
	@POST("/osg/app/meeting/join/stats")
	Observable<JSONObject> meetingJoinStats(@Body JSONObject jsonObject);

	/**
	 * 获取该用户是否是会议管理员
	 * /osg/app/user/meeting/admin
	 */
	@GET("/osg/app/user/meeting/admin")
	Observable<JSONObject> requestMeetingAdmin();

	/**
	 * 获取会议列表 或者会议搜索
	 *
	 * @param type   0=公开会议 1=受邀会议
	 * @param pageNo 页码
	 * @param title  搜索的标题 不是搜索是 可传空字符串
	 */
	@GET("/osg/app/meeting/listPage")
	Observable<JSONObject> getAllMeeting(@Query("type") int type, @Query("pageNo") int pageNo, @Query("title") String title,@Query("isMeeting") String isMeeting);


	/*
	* params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
		params.put("meetingId", meeting.getId());
		params.put("token", joinCode)
	* ;*/

	/**
	 * 判断角色
	 *
	 * @param clientUid UIDUtil.generatorUID(Preferences.getUserId())
	 * @param meetingId
	 * @param token     joinCode
	 */
	@POST("/osg/app/meeting/verify")
	Observable<JSONObject> verifyRole(@Body JSONObject json);

	/**
	 * 加入会议
	 *
	 * @param clientUid UIDUtil.generatorUID(Preferences.getUserId())
	 * @param meetingId
	 * @param token     joinCode
	 */
	@POST("/osg/app/meeting/join")
	Observable<JSONObject> joinMeeting(@Body JSONObject json);

	/**
	 * 获取声网参数
	 *
	 * @param channel 会议ID
	 * @param account UIDUtil.generatorUID(Preferences.getUserId())
	 * @param role    0||1=Publisher  2=Subscriber
	 */
	@GET("/osg/agora/key/osgV2")
	Observable<JSONObject> getAgoraKey(@Query("channel") String channel, @Query("account") String account, @Query("role") String role);

	/**
	 * 获取banner
	 */
	@GET("/osg/app/meeting/getCarouselMeeting")
	Observable<JSONObject> getBanner();

	/**
	 * 上传到七牛云上的时 获取tokoen
	 */
	@GET("/osg/resource/uploadtoken/image")
	Observable<JSONObject> requestQiNiuToken();

	/**
	 * 设置用户信息
	 */
	@PUT("/osg/app/user/expostor/{userId}")
	Observable<JSONObject> setUserInformation(@Path("userId") String userId, @Body JSONObject json);

	/**
	 * 获取所有的用户类型
	 */
	@GET("/osg/app/user/post/type")
	Observable<JSONObject> getAllUserType();

	/**
	 * 获取用户身份
	 */
	@GET("/osg/app/user/post/type")
	Observable<JSONObject> getUserProfile(@Query("parentId") String parentId);

	/**
	 * 快速加入会议
	 */
	@POST("/osg/app/meeting/quickJoin")
	Observable<JSONObject> quickJoin(@Body JSONObject json);

	/**
	 * 获取通知类型数据
	 */
	@GET("/osg/app/noticeMessage/getNoticeMessage")
	Observable<JSONObject> getNoticeMessageTypeByUserId();

	/**
	 * 获取消息详情
	 */
	@GET("/osg/app/noticeMessage/getNoticeMessageByType")
	Observable<JSONObject> getMessageDetailByUserId(@Query("userId") String userId, @Query("pageNo") int pageNo);

	/**
	 * 获取推广页面数据
	 *
	 * @param type 1 会议 2 创客
	 */

	@GET("/osg/app/promotion/getPromotionPageId")
	Observable<JSONObject> getPromotionPageId(@Query("type") int type);

	/**
	 * 会议预定
	 */
	@POST("/osg/app/meetingAdvance/{meetingId}/advance")
	Observable<JSONObject> meetingReserve(@Path("meetingId") String meetingId);

	/**
	 * 日更课程更多
	 */
	@GET("/osg/app/changeLessonByDay/getChangeLessonByDay")
	Observable<JSONObject> getMoreAudioCourse(@Query("pageNo") int pageNo);

	/**
	 * 点击日更课
	 * */
	@GET("/osg/app/changeLessonByDay/clickCourse/{id}")
	Observable<JSONObject> clickCourse(@Path("id") String id);

	/**
	 * 创客栏目及所有系列
	 */
	@GET("/osg/app/column")
	Observable<JSONObject> getMakerColumn();

	/**
	 * 创客栏目返回系列数据
	 */
	@GET("/osg/app/series/getSeriesPageByType/{typeId}")
	Observable<JSONObject> getSeriesPageByType(@Path("typeId") String typeId, @Query("pageNo") int pageNo, @Query("pageSIze") int pageSIze);

	/**
	 * 系列课报名
	 *
	 * @param isSign 1 报名   0取消
	 */
	@GET("/osg/app/series/{typeId}")
	Observable<JSONObject> siginCourse(@Path("typeId") String typeId, @Query("isSign") int isSign);

	/**
	 * 取消会议预定
	 */
	@POST("/osg/app/meetingAdvance/{meetingId}/cancelAdvance")
	Observable<JSONObject> cancelAdvance(@Path("meetingId") String meetingId);

	/**
	 * 获取收藏会议和报名系列
	 */
	@GET("/osg/app/user/getCollectSeries")
	Observable<JSONObject> getCollectMeeting(@Query("userId") String userId);

	/**
	 * 获取收藏会议和直播
	 */
	@GET("/osg/app/user/getCollectMeeting")
	Observable<JSONObject> getCollectMeetingAndLive(@Query("userId") String userId);

	/**
	 * 根据系列返回页面数据
	 */
	@GET("/osg/app/ckPage")
	Observable<JSONObject> getMoreCourse(@Query("pageId") String pageId, @Query("seriesId") String id);

	/**
	 * 获取打卡的问题
	 */
	@GET("/osg/app/recordProblemGrade/getRecordProblemByPageId")
	Observable<JSONObject> getQuestion(@Query("pageId") String pageId);

	/**
	 * 提交打卡问题
	 */
	@POST("osg/app/recordProblemGrade")
	Observable<JSONObject> commitQuestion(@Body JSONObject jsonObject);

	/**
	 * 版本检测
	 *
	 * @param appCode    zyou_online_android
	 * @param clientType 1
	 */
	@POST("osg/app/version/checkVersion")
	Observable<JSONObject> VersionCheck(@Body JSONObject jsonObject);

	/**
	 * 获取手机验证码
	 */
	@POST("osg/app/user/verifyCode")
	Observable<JSONObject> getVerifyCode(@Body JSONObject jsonObject);

	/**
	 * 通过手机号 验证码登陆
	 */
	@POST("/osg/app/user/mobile/auth")
	Observable<JSONObject> loginWithPhone(@Body JSONObject jsonObject);

	/**
	 * 获取未读消息
	 */
	@GET("/osg/app/noticeMessage/getIsExistUnread")
	Observable<JSONObject> getIsExistUnread();

	/**
	 * 获取所有的ppt资料
	 */
	@GET("osg/app/meeting/materials")
	Observable<JSONObject> getAllPPT(@Query("meetingId") String meetingId);


	/**
	 * 单个消息已读
	 * */
	@GET("/osg/app/noticeMessage/updateReadById")
	Observable<JSONObject> readOneMessage(@Query("noticeId") String noticeId);
	/**
	 * 全部消息已读
	 * @param  type 1 会议 2 创客 3系统
	 *
	 * */
	@GET("/osg/app/noticeMessage/isAllRead")
	Observable<JSONObject> readAllMessage();

	@GET("/osg/app/series/{seriesId}/unlock")
	Observable<JSONObject> unlockSeries(@Path("seriesId") String seriesId);

	/**
	 * 新的首页
	 * */
	@GET("osg/app/homePage")
	Observable<JSONObject> getHomePage();

	/**
	 * 删除信息
	 * */
	@DELETE("osg/app/noticeMessage/deleteNotice")
	Observable<JSONObject> deteleItemMessage(@Query("msgId") String msgId);
}
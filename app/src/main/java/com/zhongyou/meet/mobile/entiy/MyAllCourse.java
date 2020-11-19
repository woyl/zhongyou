package com.zhongyou.meet.mobile.entiy;

import java.util.List;

import javax.xml.transform.sax.SAXSource;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/17 10:07 AM.
 * @
 */
public class MyAllCourse {


	private List<SignUpSeriesBean> signUpSeries;
	private List<AuthSeriesBean> authSeries;
	private List<CollectMeetingsBean> collectMeetings;
	private List<CollectLives> collectLives;

	public List<CollectLives> getCollectLives() {
		return collectLives;
	}

	public void setCollectLives(List<CollectLives> collectLives) {
		this.collectLives = collectLives;
	}

	public List<SignUpSeriesBean> getSignUpSeries() {
		return signUpSeries;
	}

	public void setSignUpSeries(List<SignUpSeriesBean> signUpSeries) {
		this.signUpSeries = signUpSeries;
	}

	public List<AuthSeriesBean> getAuthSeries() {
		return authSeries;
	}

	public void setAuthSeries(List<AuthSeriesBean> authSeries) {
		this.authSeries = authSeries;
	}

	public List<CollectMeetingsBean> getCollectMeetings() {
		return collectMeetings;
	}

	public void setCollectMeetings(List<CollectMeetingsBean> collectMeetings) {
		this.collectMeetings = collectMeetings;
	}

	public static class SignUpSeriesBean {
		/**
		 * id : f6a89c45074d4e9aafe88eaa5ec2a75e
		 * createDate : 2020-05-07 13:46:15
		 * updateDate : 2020-09-03 13:58:15
		 * delFlag : 0
		 * name : 35个成长关键期(测试01)
		 * type : 1
		 * typeId : 3c2966ec931548a6bbc504db0fcf148b
		 * typeName : 大师 live
		 * pageId : 294a8bc9ef424160af4ab3465cf456bb
		 * pageName : 0-9岁儿童的35个成长关键期
		 * upTime : 2020-05-07 13:37:29
		 * isUp : 1
		 * isSignUp : 1
		 * pictureURL : http://syimage.zhongyouie.com//系列-封面图.jpg
		 * isAuth : 1
		 * isSign : 1
		 */

		private String id;
		private String createDate;
		private String updateDate;
		private String delFlag;
		private String name;
		private int type;
		private String typeId;
		private String typeName;
		private String pageId;
		private String pageName;
		private String upTime;
		private int isUp;
		private int isSignUp;
		private String pictureURL;
		private int isAuth;
		private int isSign;
		private String studyNum;
		private String videoNum;
		private String lecturerInfo;
		private String seriesIntro;
		private String lecturerId;
		private String lecturerHeadUrl;
		private String studyNumStr;

		public String getStudyNumStr() {
			return studyNumStr;
		}

		public void setStudyNumStr(String studyNumStr) {
			this.studyNumStr = studyNumStr;
		}

		public String getStudyNum() {
			return studyNum;
		}

		public void setStudyNum(String studyNum) {
			this.studyNum = studyNum;
		}

		public String getVideoNum() {
			return videoNum;
		}

		public void setVideoNum(String videoNum) {
			this.videoNum = videoNum;
		}

		public String getLecturerInfo() {
			return lecturerInfo;
		}

		public void setLecturerInfo(String lecturerInfo) {
			this.lecturerInfo = lecturerInfo;
		}

		public String getSeriesIntro() {
			return seriesIntro;
		}

		public void setSeriesIntro(String seriesIntro) {
			this.seriesIntro = seriesIntro;
		}

		public String getLecturerId() {
			return lecturerId;
		}

		public void setLecturerId(String lecturerId) {
			this.lecturerId = lecturerId;
		}

		public String getLecturerHeadUrl() {
			return lecturerHeadUrl;
		}

		public void setLecturerHeadUrl(String lecturerHeadUrl) {
			this.lecturerHeadUrl = lecturerHeadUrl;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}

		public String getDelFlag() {
			return delFlag;
		}

		public void setDelFlag(String delFlag) {
			this.delFlag = delFlag;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getTypeId() {
			return typeId;
		}

		public void setTypeId(String typeId) {
			this.typeId = typeId;
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public String getPageId() {
			return pageId;
		}

		public void setPageId(String pageId) {
			this.pageId = pageId;
		}

		public String getPageName() {
			return pageName;
		}

		public void setPageName(String pageName) {
			this.pageName = pageName;
		}

		public String getUpTime() {
			return upTime;
		}

		public void setUpTime(String upTime) {
			this.upTime = upTime;
		}

		public int getIsUp() {
			return isUp;
		}

		public void setIsUp(int isUp) {
			this.isUp = isUp;
		}

		public int getIsSignUp() {
			return isSignUp;
		}

		public void setIsSignUp(int isSignUp) {
			this.isSignUp = isSignUp;
		}

		public String getPictureURL() {
			return pictureURL;
		}

		public void setPictureURL(String pictureURL) {
			this.pictureURL = pictureURL;
		}

		public int getIsAuth() {
			return isAuth;
		}

		public void setIsAuth(int isAuth) {
			this.isAuth = isAuth;
		}

		public int getIsSign() {
			return isSign;
		}

		public void setIsSign(int isSign) {
			this.isSign = isSign;
		}
	}

	public static class AuthSeriesBean {
		/**
		 * isSignUp : 1
		 * upTime : 2020-05-27 20:02:02
		 * isAuth : 2
		 * isUp : 1
		 * pictureURL : http://syimage.zhongyouie.com//159058093148701.jpg
		 * name : 解锁教育改革
		 * typeName : 大师 live
		 * typeId : 3c2966ec931548a6bbc504db0fcf148b
		 * id : 7feba764e565463fbe8841bdf7f03c41
		 * type : 1
		 * pageId : 8331858a9dbc4745b5cfb6890656b11b
		 * pageName : 解锁教育改革
		 */

		private int isSignUp;
		private String upTime;
		private int isAuth;
		private int isUp;
		private String pictureURL;
		private String name;
		private String typeName;
		private String typeId;
		private String id;
		private int type;
		private String pageId;
		private String pageName;
		private String studyNum;
		private String videoNum;
		private String lecturerInfo;
		private String seriesIntro;
		private String lecturerId;
		private String lecturerHeadUrl;
		private int isSign;
		private String studyNumStr;

		public String getStudyNumStr() {
			return studyNumStr;
		}

		public void setStudyNumStr(String studyNumStr) {
			this.studyNumStr = studyNumStr;
		}

		public int getIsSign() {
			return isSign;
		}

		public void setIsSign(int isSign) {
			this.isSign = isSign;
		}

		public String getStudyNum() {
			return studyNum;
		}

		public void setStudyNum(String studyNum) {
			this.studyNum = studyNum;
		}

		public String getVideoNum() {
			return videoNum;
		}

		public void setVideoNum(String videoNum) {
			this.videoNum = videoNum;
		}

		public String getLecturerInfo() {
			return lecturerInfo;
		}

		public void setLecturerInfo(String lecturerInfo) {
			this.lecturerInfo = lecturerInfo;
		}

		public String getSeriesIntro() {
			return seriesIntro;
		}

		public void setSeriesIntro(String seriesIntro) {
			this.seriesIntro = seriesIntro;
		}

		public String getLecturerId() {
			return lecturerId;
		}

		public void setLecturerId(String lecturerId) {
			this.lecturerId = lecturerId;
		}

		public String getLecturerHeadUrl() {
			return lecturerHeadUrl;
		}

		public void setLecturerHeadUrl(String lecturerHeadUrl) {
			this.lecturerHeadUrl = lecturerHeadUrl;
		}

		public int getIsSignUp() {
			return isSignUp;
		}

		public void setIsSignUp(int isSignUp) {
			this.isSignUp = isSignUp;
		}

		public String getUpTime() {
			return upTime;
		}

		public void setUpTime(String upTime) {
			this.upTime = upTime;
		}

		public int getIsAuth() {
			return isAuth;
		}

		public void setIsAuth(int isAuth) {
			this.isAuth = isAuth;
		}

		public int getIsUp() {
			return isUp;
		}

		public void setIsUp(int isUp) {
			this.isUp = isUp;
		}

		public String getPictureURL() {
			return pictureURL;
		}

		public void setPictureURL(String pictureURL) {
			this.pictureURL = pictureURL;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public String getTypeId() {
			return typeId;
		}

		public void setTypeId(String typeId) {
			this.typeId = typeId;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getPageId() {
			return pageId;
		}

		public void setPageId(String pageId) {
			this.pageId = pageId;
		}

		public String getPageName() {
			return pageName;
		}

		public void setPageName(String pageName) {
			this.pageName = pageName;
		}
	}

	public static class CollectLives {
		/**
		 * id : e76dda0470594c04b464af2c7570129f
		 * createDate : 2020-09-15 09:56:17
		 * updateDate : 2020-09-15 14:48:46
		 * delFlag : 0
		 * title : test
		 * description :
		 * startTime : 2020-09-15 09:56:17
		 * actualStartTime : 2020-09-15 10:01:36
		 * totalParticipants : 11
		 * totalAttendance : 2
		 * hostToken : 952609
		 * participantsToken : 565628
		 * screenshotFrequency : 0
		 * screenshotCompressionRatio : 0
		 * screenshotScrollFrequency : 1000
		 * meetingProcess : 1
		 * approved : 1
		 * createById : 21
		 * duration : 0
		 * type : 0
		 * materialsCnt : 0
		 * userId :
		 * playUrl :
		 * audienceToken : 581017
		 * resolvingPower : 1
		 * isRecord : 0
		 * isToken : 1
		 * isHide : 0
		 * isCollection : 1
		 * isMeeting : 1
		 * isTop : 0
		 * inviteLink : http://mo.zhongyouie.com/invitedMeeting?joinCode=581017&meetingId=e76dda0470594c04b464af2c7570129f&name=test&startTime=2020-09-15+09%3A56%3A17&host=2
		 * isInjectStream : 0
		 */

		private String id;
		private String createDate;
		private String updateDate;
		private String delFlag;
		private String title;
		private String description;
		private String startTime;
		private String actualStartTime;
		private int totalParticipants;
		private int totalAttendance;
		private String hostToken;
		private String participantsToken;
		private int screenshotFrequency;
		private int screenshotCompressionRatio;
		private int screenshotScrollFrequency;
		private int meetingProcess;
		private int approved;
		private String createById;
		private int duration;
		private int type;
		private int materialsCnt;
		private String userId;
		private String playUrl;
		private String audienceToken;
		private int resolvingPower;
		private int isRecord;
		private int isToken;
		private int isHide;
		private int isCollection;
		private int isMeeting;
		private int isTop;
		private String inviteLink;
		private int isInjectStream;
		private String meetingStartTime;
		private String lecturerId;
		private String lecturerName;
		private String lecturerHeadUrl;

		public String getMeetingStartTime() {
			return meetingStartTime;
		}

		public void setMeetingStartTime(String meetingStartTime) {
			this.meetingStartTime = meetingStartTime;
		}

		public String getLecturerId() {
			return lecturerId;
		}

		public void setLecturerId(String lecturerId) {
			this.lecturerId = lecturerId;
		}

		public String getLecturerName() {
			return lecturerName;
		}

		public void setLecturerName(String lecturerName) {
			this.lecturerName = lecturerName;
		}

		public String getLecturerHeadUrl() {
			return lecturerHeadUrl;
		}

		public void setLecturerHeadUrl(String lecturerHeadUrl) {
			this.lecturerHeadUrl = lecturerHeadUrl;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}

		public String getDelFlag() {
			return delFlag;
		}

		public void setDelFlag(String delFlag) {
			this.delFlag = delFlag;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getStartTime() {
			if(startTime != null && startTime.length() > 16)
				return startTime.substring(0, 16);
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getActualStartTime() {
			return actualStartTime;
		}

		public void setActualStartTime(String actualStartTime) {
			this.actualStartTime = actualStartTime;
		}

		public int getTotalParticipants() {
			return totalParticipants;
		}

		public void setTotalParticipants(int totalParticipants) {
			this.totalParticipants = totalParticipants;
		}

		public int getTotalAttendance() {
			return totalAttendance;
		}

		public void setTotalAttendance(int totalAttendance) {
			this.totalAttendance = totalAttendance;
		}

		public String getHostToken() {
			return hostToken;
		}

		public void setHostToken(String hostToken) {
			this.hostToken = hostToken;
		}

		public String getParticipantsToken() {
			return participantsToken;
		}

		public void setParticipantsToken(String participantsToken) {
			this.participantsToken = participantsToken;
		}

		public int getScreenshotFrequency() {
			return screenshotFrequency;
		}

		public void setScreenshotFrequency(int screenshotFrequency) {
			this.screenshotFrequency = screenshotFrequency;
		}

		public int getScreenshotCompressionRatio() {
			return screenshotCompressionRatio;
		}

		public void setScreenshotCompressionRatio(int screenshotCompressionRatio) {
			this.screenshotCompressionRatio = screenshotCompressionRatio;
		}

		public int getScreenshotScrollFrequency() {
			return screenshotScrollFrequency;
		}

		public void setScreenshotScrollFrequency(int screenshotScrollFrequency) {
			this.screenshotScrollFrequency = screenshotScrollFrequency;
		}

		public int getMeetingProcess() {
			return meetingProcess;
		}

		public void setMeetingProcess(int meetingProcess) {
			this.meetingProcess = meetingProcess;
		}

		public int getApproved() {
			return approved;
		}

		public void setApproved(int approved) {
			this.approved = approved;
		}

		public String getCreateById() {
			return createById;
		}

		public void setCreateById(String createById) {
			this.createById = createById;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getMaterialsCnt() {
			return materialsCnt;
		}

		public void setMaterialsCnt(int materialsCnt) {
			this.materialsCnt = materialsCnt;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getPlayUrl() {
			return playUrl;
		}

		public void setPlayUrl(String playUrl) {
			this.playUrl = playUrl;
		}

		public String getAudienceToken() {
			return audienceToken;
		}

		public void setAudienceToken(String audienceToken) {
			this.audienceToken = audienceToken;
		}

		public int getResolvingPower() {
			return resolvingPower;
		}

		public void setResolvingPower(int resolvingPower) {
			this.resolvingPower = resolvingPower;
		}

		public int getIsRecord() {
			return isRecord;
		}

		public void setIsRecord(int isRecord) {
			this.isRecord = isRecord;
		}

		public int getIsToken() {
			return isToken;
		}

		public void setIsToken(int isToken) {
			this.isToken = isToken;
		}

		public int getIsHide() {
			return isHide;
		}

		public void setIsHide(int isHide) {
			this.isHide = isHide;
		}

		public int getIsCollection() {
			return isCollection;
		}

		public void setIsCollection(int isCollection) {
			this.isCollection = isCollection;
		}

		public int getIsMeeting() {
			return isMeeting;
		}

		public void setIsMeeting(int isMeeting) {
			this.isMeeting = isMeeting;
		}

		public int getIsTop() {
			return isTop;
		}

		public void setIsTop(int isTop) {
			this.isTop = isTop;
		}

		public String getInviteLink() {
			return inviteLink;
		}

		public void setInviteLink(String inviteLink) {
			this.inviteLink = inviteLink;
		}

		public int getIsInjectStream() {
			return isInjectStream;
		}

		public void setIsInjectStream(int isInjectStream) {
			this.isInjectStream = isInjectStream;
		}
	}

	public static class CollectMeetingsBean {
		/**
		 * id : e76dda0470594c04b464af2c7570129f
		 * createDate : 2020-09-15 09:56:17
		 * updateDate : 2020-09-15 14:48:46
		 * delFlag : 0
		 * title : test
		 * description :
		 * startTime : 2020-09-15 09:56:17
		 * actualStartTime : 2020-09-15 10:01:36
		 * totalParticipants : 11
		 * totalAttendance : 2
		 * hostToken : 952609
		 * participantsToken : 565628
		 * screenshotFrequency : 0
		 * screenshotCompressionRatio : 0
		 * screenshotScrollFrequency : 1000
		 * meetingProcess : 1
		 * approved : 1
		 * createById : 21
		 * duration : 0
		 * type : 0
		 * materialsCnt : 0
		 * userId :
		 * playUrl :
		 * audienceToken : 581017
		 * resolvingPower : 1
		 * isRecord : 0
		 * isToken : 1
		 * isHide : 0
		 * isCollection : 1
		 * isMeeting : 1
		 * isTop : 0
		 * inviteLink : http://mo.zhongyouie.com/invitedMeeting?joinCode=581017&meetingId=e76dda0470594c04b464af2c7570129f&name=test&startTime=2020-09-15+09%3A56%3A17&host=2
		 * isInjectStream : 0
		 */

		private String id;
		private String createDate;
		private String updateDate;
		private String delFlag;
		private String title;
		private String description;
		private String startTime;
		private String actualStartTime;
		private int totalParticipants;
		private int totalAttendance;
		private String hostToken;
		private String participantsToken;
		private int screenshotFrequency;
		private int screenshotCompressionRatio;
		private int screenshotScrollFrequency;
		private int meetingProcess;
		private int approved;
		private String createById;
		private int duration;
		private int type;
		private int materialsCnt;
		private String userId;
		private String playUrl;
		private String audienceToken;
		private int resolvingPower;
		private int isRecord;
		private int isToken;
		private int isHide;
		private int isCollection;
		private int isMeeting;
		private int isTop;
		private String inviteLink;
		private int isInjectStream;
		private String meetingStartTime;
		private String lecturerId;
		private String lecturerName;
		private String lecturerHeadUrl;

		public String getMeetingStartTime() {
			return meetingStartTime;
		}

		public void setMeetingStartTime(String meetingStartTime) {
			this.meetingStartTime = meetingStartTime;
		}

		public String getLecturerId() {
			return lecturerId;
		}

		public void setLecturerId(String lecturerId) {
			this.lecturerId = lecturerId;
		}

		public String getLecturerName() {
			return lecturerName;
		}

		public void setLecturerName(String lecturerName) {
			this.lecturerName = lecturerName;
		}

		public String getLecturerHeadUrl() {
			return lecturerHeadUrl;
		}

		public void setLecturerHeadUrl(String lecturerHeadUrl) {
			this.lecturerHeadUrl = lecturerHeadUrl;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}

		public String getDelFlag() {
			return delFlag;
		}

		public void setDelFlag(String delFlag) {
			this.delFlag = delFlag;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getActualStartTime() {
			return actualStartTime;
		}

		public void setActualStartTime(String actualStartTime) {
			this.actualStartTime = actualStartTime;
		}

		public int getTotalParticipants() {
			return totalParticipants;
		}

		public void setTotalParticipants(int totalParticipants) {
			this.totalParticipants = totalParticipants;
		}

		public int getTotalAttendance() {
			return totalAttendance;
		}

		public void setTotalAttendance(int totalAttendance) {
			this.totalAttendance = totalAttendance;
		}

		public String getHostToken() {
			return hostToken;
		}

		public void setHostToken(String hostToken) {
			this.hostToken = hostToken;
		}

		public String getParticipantsToken() {
			return participantsToken;
		}

		public void setParticipantsToken(String participantsToken) {
			this.participantsToken = participantsToken;
		}

		public int getScreenshotFrequency() {
			return screenshotFrequency;
		}

		public void setScreenshotFrequency(int screenshotFrequency) {
			this.screenshotFrequency = screenshotFrequency;
		}

		public int getScreenshotCompressionRatio() {
			return screenshotCompressionRatio;
		}

		public void setScreenshotCompressionRatio(int screenshotCompressionRatio) {
			this.screenshotCompressionRatio = screenshotCompressionRatio;
		}

		public int getScreenshotScrollFrequency() {
			return screenshotScrollFrequency;
		}

		public void setScreenshotScrollFrequency(int screenshotScrollFrequency) {
			this.screenshotScrollFrequency = screenshotScrollFrequency;
		}

		public int getMeetingProcess() {
			return meetingProcess;
		}

		public void setMeetingProcess(int meetingProcess) {
			this.meetingProcess = meetingProcess;
		}

		public int getApproved() {
			return approved;
		}

		public void setApproved(int approved) {
			this.approved = approved;
		}

		public String getCreateById() {
			return createById;
		}

		public void setCreateById(String createById) {
			this.createById = createById;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getMaterialsCnt() {
			return materialsCnt;
		}

		public void setMaterialsCnt(int materialsCnt) {
			this.materialsCnt = materialsCnt;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getPlayUrl() {
			return playUrl;
		}

		public void setPlayUrl(String playUrl) {
			this.playUrl = playUrl;
		}

		public String getAudienceToken() {
			return audienceToken;
		}

		public void setAudienceToken(String audienceToken) {
			this.audienceToken = audienceToken;
		}

		public int getResolvingPower() {
			return resolvingPower;
		}

		public void setResolvingPower(int resolvingPower) {
			this.resolvingPower = resolvingPower;
		}

		public int getIsRecord() {
			return isRecord;
		}

		public void setIsRecord(int isRecord) {
			this.isRecord = isRecord;
		}

		public int getIsToken() {
			return isToken;
		}

		public void setIsToken(int isToken) {
			this.isToken = isToken;
		}

		public int getIsHide() {
			return isHide;
		}

		public void setIsHide(int isHide) {
			this.isHide = isHide;
		}

		public int getIsCollection() {
			return isCollection;
		}

		public void setIsCollection(int isCollection) {
			this.isCollection = isCollection;
		}

		public int getIsMeeting() {
			return isMeeting;
		}

		public void setIsMeeting(int isMeeting) {
			this.isMeeting = isMeeting;
		}

		public int getIsTop() {
			return isTop;
		}

		public void setIsTop(int isTop) {
			this.isTop = isTop;
		}

		public String getInviteLink() {
			return inviteLink;
		}

		public void setInviteLink(String inviteLink) {
			this.inviteLink = inviteLink;
		}

		public int getIsInjectStream() {
			return isInjectStream;
		}

		public void setIsInjectStream(int isInjectStream) {
			this.isInjectStream = isInjectStream;
		}
	}
}

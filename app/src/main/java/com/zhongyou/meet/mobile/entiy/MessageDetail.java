package com.zhongyou.meet.mobile.entiy;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/15 10:13 AM.
 * @
 */
public class MessageDetail {

	/**
	 * id : acd571cad1794ad1bbd51049c38b6af6
	 * createDate : 2020-04-15 09:45:18
	 * updateDate : 2020-04-15 09:45:18
	 * delFlag : 0
	 * notice : ddd
	 * type : 1
	 * isPush : 0
	 * noticeType : 1
	 * time : 2020-04-22 12:00:00.0
	 */



	private String id;
	private String createDate;
	private String updateDate;
	private String delFlag;
	private String notice;
	private int type;
	private int isPush;
	private int noticeType;
	private String time;
	private String meetingTitle;
	/**
	 * appNotice :
	 */

	private String appNotice;
	private String pageId;

	private int isAuth;

	public int getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(int isAuth) {
		this.isAuth = isAuth;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getMeetingTitle() {
		return meetingTitle;
	}

	public void setMeetingTitle(String meetingTitle) {
		this.meetingTitle = meetingTitle;
	}

	public String getMeetingStartTime() {
		return meetingStartTime;
	}

	public void setMeetingStartTime(String meetingStartTime) {
		this.meetingStartTime = meetingStartTime;
	}

	private String meetingStartTime;
	/**
	 * meetingId : 5108899214fd4d10824fce5487e24698
	 * isToken : 1
	 * isRecord : 0
	 * resolvingPower : 1
	 * urlId : meetingId更换urlId
	 */

	private String meetingId;
	private String urlId;
	private int isToken;
	private int isRecord;
	private int resolvingPower;
	/**
	 * earlyTime : 2020-09-03 10:38:33.0
	 * title : 云教室邀请提醒: 15分钟后开始
	 * isReader : 0
	 */

	private String earlyTime;
	private String title;
	private int isReader;
	private Seriser series;

	public String getUrlId() {
		return urlId;
	}

	public void setUrlId(String urlId) {
		this.urlId = urlId;
	}

	public Seriser getSeries() {
		return series;
	}

	public void setSeries(Seriser series) {
		this.series = series;
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

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIsPush() {
		return isPush;
	}

	public void setIsPush(int isPush) {
		this.isPush = isPush;
	}

	public int getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(int noticeType) {
		this.noticeType = noticeType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public int getIsToken() {
		return isToken;
	}

	public void setIsToken(int isToken) {
		this.isToken = isToken;
	}

	public int getIsRecord() {
		return isRecord;
	}

	public void setIsRecord(int isRecord) {
		this.isRecord = isRecord;
	}

	public int getResolvingPower() {
		return resolvingPower;
	}

	public void setResolvingPower(int resolvingPower) {
		this.resolvingPower = resolvingPower;
	}

	public String getEarlyTime() {
		return earlyTime;
	}

	public void setEarlyTime(String earlyTime) {
		this.earlyTime = earlyTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIsReader() {
		return isReader;
	}

	public void setIsReader(int isReader) {
		this.isReader = isReader;
	}

	public String getAppNotice() {
		return appNotice;
	}

	public void setAppNotice(String appNotice) {
		this.appNotice = appNotice;
	}
}

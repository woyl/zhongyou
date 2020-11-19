package com.zhongyou.meet.mobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Meeting implements Entity, Parcelable {


	/**
	 * id : 25c73785ad6644fc9e2d3daf57289970
	 * createDate : 2020-04-21 09:07:17
	 * updateDate : 2020-04-21 09:14:08
	 * delFlag : 0
	 * title : 会议测试
	 * startTime : 2020-04-21 09:07:04
	 * actualStartTime : 2020-04-21 09:14:06
	 * totalParticipants : 67
	 * totalAttendance : 1
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
	 * playUrl : http://syimage.zhongyouie.com/osg/meeting/materials/189b21ffe8a842fe9b59de01ade8f688.jpg
	 * audienceToken : 978012
	 * resolvingPower : 1
	 * isRecord : 0
	 * isToken : 1
	 * isHide : 0
	 * isCollection : 0
	 * isMeeting : 1
	 */

	private String id;
	private String createDate;
	private String updateDate;
	private String delFlag;
	private String title;
	private String startTime;
	private String actualStartTime;
	private int totalParticipants;
	private int totalAttendance;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.createDate);
		dest.writeString(this.updateDate);
		dest.writeString(this.delFlag);
		dest.writeString(this.title);
		dest.writeString(this.startTime);
		dest.writeString(this.actualStartTime);
		dest.writeInt(this.totalParticipants);
		dest.writeInt(this.totalAttendance);
		dest.writeInt(this.screenshotFrequency);
		dest.writeInt(this.screenshotCompressionRatio);
		dest.writeInt(this.screenshotScrollFrequency);
		dest.writeInt(this.meetingProcess);
		dest.writeInt(this.approved);
		dest.writeString(this.createById);
		dest.writeInt(this.duration);
		dest.writeInt(this.type);
		dest.writeInt(this.materialsCnt);
		dest.writeString(this.userId);
		dest.writeString(this.playUrl);
		dest.writeString(this.audienceToken);
		dest.writeInt(this.resolvingPower);
		dest.writeInt(this.isRecord);
		dest.writeInt(this.isToken);
		dest.writeInt(this.isHide);
		dest.writeInt(this.isCollection);
		dest.writeInt(this.isMeeting);
	}

	public Meeting() {
	}

	protected Meeting(Parcel in) {
		this.id = in.readString();
		this.createDate = in.readString();
		this.updateDate = in.readString();
		this.delFlag = in.readString();
		this.title = in.readString();
		this.startTime = in.readString();
		this.actualStartTime = in.readString();
		this.totalParticipants = in.readInt();
		this.totalAttendance = in.readInt();
		this.screenshotFrequency = in.readInt();
		this.screenshotCompressionRatio = in.readInt();
		this.screenshotScrollFrequency = in.readInt();
		this.meetingProcess = in.readInt();
		this.approved = in.readInt();
		this.createById = in.readString();
		this.duration = in.readInt();
		this.type = in.readInt();
		this.materialsCnt = in.readInt();
		this.userId = in.readString();
		this.playUrl = in.readString();
		this.audienceToken = in.readString();
		this.resolvingPower = in.readInt();
		this.isRecord = in.readInt();
		this.isToken = in.readInt();
		this.isHide = in.readInt();
		this.isCollection = in.readInt();
		this.isMeeting = in.readInt();
	}

	public static final Parcelable.Creator<Meeting> CREATOR = new Parcelable.Creator<Meeting>() {
		@Override
		public Meeting createFromParcel(Parcel source) {
			return new Meeting(source);
		}

		@Override
		public Meeting[] newArray(int size) {
			return new Meeting[size];
		}
	};
}

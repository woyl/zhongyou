package com.zhongyou.meet.mobile.entiy;

import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2020/3/27 3:41 PM.
 * @
 */
public class MeetingsData {

	/**
	 * errcode : 0
	 * data : {"pageNo":1,"pageSize":10,"totalPage":1,"list":[{"id":"7c49f50a7e974c01bb451863fec4a752","createDate":"2020-04-15 14:44:41","updateDate":"2020-04-15 14:45:20","delFlag":"0","title":"测试会议","startTime":"2020-04-15 14:44:17","totalParticipants":9,"totalAttendance":1,"screenshotFrequency":0,"screenshotCompressionRatio":0,"screenshotScrollFrequency":1000,"meetingProcess":0,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","playUrl":"http://syimage.zhongyouie.com/osg/meeting/materials/1b344f75536e433aa8fe8cfa5576c6d6.jpg","audienceToken":"424571","resolvingPower":1,"isRecord":0,"isToken":1,"isHide":0,"isCollection":0,"isMeeting":1},{"id":"e59679ccb65e4912b4bdedb0ed3a9ef6","createDate":"2020-04-15 16:46:47","updateDate":"2020-04-15 17:31:23","delFlag":"0","title":"123新版本测试","startTime":"2020-04-15 16:46:43","actualStartTime":"2020-04-15 17:25:06","totalParticipants":34,"totalAttendance":2,"screenshotFrequency":0,"screenshotCompressionRatio":0,"screenshotScrollFrequency":1000,"meetingProcess":1,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","playUrl":"","audienceToken":"606897","resolvingPower":1,"isRecord":0,"isToken":1,"isHide":0,"isCollection":0,"isMeeting":1},{"id":"c124723c27e646838b1322fcfc38fef4","createDate":"2020-04-15 18:07:30","updateDate":"2020-04-15 18:08:10","delFlag":"0","title":"123","startTime":"2020-04-15 18:07:26","actualStartTime":"2020-04-15 18:08:09","totalParticipants":123,"totalAttendance":1,"screenshotFrequency":0,"screenshotCompressionRatio":0,"screenshotScrollFrequency":1000,"meetingProcess":1,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","playUrl":"","audienceToken":"832863","resolvingPower":1,"isRecord":0,"isToken":1,"isHide":0,"isCollection":0,"isMeeting":1},{"id":"e3e66f4408cf4d8fb967941b7264ef6b","createDate":"2020-04-15 18:08:45","updateDate":"2020-04-15 18:08:45","delFlag":"0","title":"456","startTime":"2020-04-16 00:00:00","totalParticipants":567,"totalAttendance":0,"screenshotFrequency":0,"screenshotCompressionRatio":0,"screenshotScrollFrequency":1000,"meetingProcess":0,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","playUrl":"","audienceToken":"760993","resolvingPower":1,"isRecord":0,"isToken":1,"isHide":0,"isCollection":0,"isMeeting":1}],"orderBy":"startTime ASC","count":4,"firstResult":0,"maxResults":10}
	 * errmsg : 处理成功
	 */

	private int errcode;
	private DataBean data;
	private String errmsg;

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public static class DataBean {
		/**
		 * pageNo : 1
		 * pageSize : 10
		 * totalPage : 1
		 * list : [{"id":"7c49f50a7e974c01bb451863fec4a752","createDate":"2020-04-15 14:44:41","updateDate":"2020-04-15 14:45:20","delFlag":"0","title":"测试会议","startTime":"2020-04-15 14:44:17","totalParticipants":9,"totalAttendance":1,"screenshotFrequency":0,"screenshotCompressionRatio":0,"screenshotScrollFrequency":1000,"meetingProcess":0,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","playUrl":"http://syimage.zhongyouie.com/osg/meeting/materials/1b344f75536e433aa8fe8cfa5576c6d6.jpg","audienceToken":"424571","resolvingPower":1,"isRecord":0,"isToken":1,"isHide":0,"isCollection":0,"isMeeting":1},{"id":"e59679ccb65e4912b4bdedb0ed3a9ef6","createDate":"2020-04-15 16:46:47","updateDate":"2020-04-15 17:31:23","delFlag":"0","title":"123新版本测试","startTime":"2020-04-15 16:46:43","actualStartTime":"2020-04-15 17:25:06","totalParticipants":34,"totalAttendance":2,"screenshotFrequency":0,"screenshotCompressionRatio":0,"screenshotScrollFrequency":1000,"meetingProcess":1,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","playUrl":"","audienceToken":"606897","resolvingPower":1,"isRecord":0,"isToken":1,"isHide":0,"isCollection":0,"isMeeting":1},{"id":"c124723c27e646838b1322fcfc38fef4","createDate":"2020-04-15 18:07:30","updateDate":"2020-04-15 18:08:10","delFlag":"0","title":"123","startTime":"2020-04-15 18:07:26","actualStartTime":"2020-04-15 18:08:09","totalParticipants":123,"totalAttendance":1,"screenshotFrequency":0,"screenshotCompressionRatio":0,"screenshotScrollFrequency":1000,"meetingProcess":1,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","playUrl":"","audienceToken":"832863","resolvingPower":1,"isRecord":0,"isToken":1,"isHide":0,"isCollection":0,"isMeeting":1},{"id":"e3e66f4408cf4d8fb967941b7264ef6b","createDate":"2020-04-15 18:08:45","updateDate":"2020-04-15 18:08:45","delFlag":"0","title":"456","startTime":"2020-04-16 00:00:00","totalParticipants":567,"totalAttendance":0,"screenshotFrequency":0,"screenshotCompressionRatio":0,"screenshotScrollFrequency":1000,"meetingProcess":0,"approved":1,"createById":"21","duration":0,"type":0,"materialsCnt":0,"userId":"","playUrl":"","audienceToken":"760993","resolvingPower":1,"isRecord":0,"isToken":1,"isHide":0,"isCollection":0,"isMeeting":1}]
		 * orderBy : startTime ASC
		 * count : 4
		 * firstResult : 0
		 * maxResults : 10
		 */

		private int pageNo;
		private int pageSize;
		private int totalPage;
		private String orderBy;
		private int count;
		private int firstResult;
		private int maxResults;
		private List<ListBean> list;

		public int getPageNo() {
			return pageNo;
		}

		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getTotalPage() {
			return totalPage;
		}

		public void setTotalPage(int totalPage) {
			this.totalPage = totalPage;
		}

		public String getOrderBy() {
			return orderBy;
		}

		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public int getFirstResult() {
			return firstResult;
		}

		public void setFirstResult(int firstResult) {
			this.firstResult = firstResult;
		}

		public int getMaxResults() {
			return maxResults;
		}

		public void setMaxResults(int maxResults) {
			this.maxResults = maxResults;
		}

		public List<ListBean> getList() {
			return list;
		}

		public void setList(List<ListBean> list) {
			this.list = list;
		}

		public static class ListBean {
			/**
			 * id : 7c49f50a7e974c01bb451863fec4a752
			 * createDate : 2020-04-15 14:44:41
			 * updateDate : 2020-04-15 14:45:20
			 * delFlag : 0
			 * title : 测试会议
			 * startTime : 2020-04-15 14:44:17
			 * totalParticipants : 9
			 * totalAttendance : 1
			 * screenshotFrequency : 0
			 * screenshotCompressionRatio : 0
			 * screenshotScrollFrequency : 1000
			 * meetingProcess : 0
			 * approved : 1
			 * createById : 21
			 * duration : 0
			 * type : 0
			 * materialsCnt : 0
			 * userId :
			 * playUrl : http://syimage.zhongyouie.com/osg/meeting/materials/1b344f75536e433aa8fe8cfa5576c6d6.jpg
			 * audienceToken : 424571
			 * resolvingPower : 1
			 * isRecord : 0
			 * isToken : 1
			 * isHide : 0
			 * isCollection : 0
			 * isMeeting : 1
			 * actualStartTime : 2020-04-15 17:25:06
			 */

			private String id;
			private String createDate;
			private String updateDate;
			private String delFlag;
			private String title;
			private String startTime;
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
			private String isToken;
			private int isHide;
			private int isCollection;
			private int isMeeting;
			private String actualStartTime;
			private String meetingStartTime;
			private String description;
			private String lecturerHeadUrl;
			private String lecturerName;
			private String lecturerId;
			private String InviteLink;
			private String isInjectStream;

			public String getMeetingStartTime() {
				return meetingStartTime;
			}

			public void setMeetingStartTime(String meetingStartTime) {
				this.meetingStartTime = meetingStartTime;
			}

			public String getDescription() {
				return description;
			}

			public void setDescription(String description) {
				this.description = description;
			}

			public String getLecturerHeadUrl() {
				return lecturerHeadUrl;
			}

			public void setLecturerHeadUrl(String lecturerHeadUrl) {
				this.lecturerHeadUrl = lecturerHeadUrl;
			}

			public String getLecturerName() {
				return lecturerName;
			}

			public void setLecturerName(String lecturerName) {
				this.lecturerName = lecturerName;
			}

			public String getLecturerId() {
				return lecturerId;
			}

			public void setLecturerId(String lecturerId) {
				this.lecturerId = lecturerId;
			}

			public String getInviteLink() {
				return InviteLink;
			}

			public void setInviteLink(String inviteLink) {
				InviteLink = inviteLink;
			}

			public String getIsInjectStream() {
				return isInjectStream;
			}

			public void setIsInjectStream(String isInjectStream) {
				this.isInjectStream = isInjectStream;
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

			public String getStartTime() {
				return startTime;
			}

			public void setStartTime(String startTime) {
				this.startTime = startTime;
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

			public String getIsToken() {
				return isToken;
			}

			public void setIsToken(String isToken) {
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

			public String getActualStartTime() {
				return actualStartTime;
			}

			public void setActualStartTime(String actualStartTime) {
				this.actualStartTime = actualStartTime;
			}
		}
	}
}

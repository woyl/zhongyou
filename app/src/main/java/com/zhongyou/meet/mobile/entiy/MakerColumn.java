package com.zhongyou.meet.mobile.entiy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/16 11:48 AM.
 * @
 */
public class MakerColumn {

	public MakerColumn() {
	}

	/**
	 * columns : [{"id":"00ace02cfea84c69883854ede0c4fbcc","createDate":"2020-04-16 17:35:56","updateDate":"2020-04-16 17:35:56","delFlag":"0","typeName":"22","sort":33},{"id":"04db0f3fc6294294b01580510e42b2ec","createDate":"2020-04-15 09:39:19","updateDate":"2020-04-15 09:39:19","delFlag":"0","typeName":"44","sort":44},{"id":"1ea36a7a6add4473ba13b86f802497cb","createDate":"2020-04-16 17:42:19","updateDate":"2020-04-16 17:42:19","delFlag":"0","typeName":"33","sort":33},{"id":"7af71d859a92493681f93974f66ac361","createDate":"2020-04-15 09:25:10","updateDate":"2020-04-15 09:39:06","delFlag":"0","typeName":"441288","sort":12399},{"id":"84f1c42184a6425198da1cc3ad552f56","createDate":"2020-04-15 09:24:28","updateDate":"2020-04-15 09:24:28","delFlag":"0","typeName":"2","sort":2},{"id":"93cc0dfcf8cf4c4e85b485c246e425c4","createDate":"2020-04-15 09:29:22","updateDate":"2020-04-15 09:29:22","delFlag":"0","typeName":"23","sort":23},{"id":"960e45a1d5bb497aa7bb3420aecf6020","createDate":"2020-04-15 09:24:54","updateDate":"2020-04-15 09:24:54","delFlag":"0","typeName":"8","sort":8},{"id":"9841d65520984364bb02b6b81cf450f3","createDate":"2020-04-15 10:55:23","updateDate":"2020-04-15 10:55:23","delFlag":"0","typeName":"nihao","sort":1},{"id":"9e662bb99cf5491a9433d350698d595e","createDate":"2020-04-15 09:24:50","updateDate":"2020-04-15 09:24:50","delFlag":"0","typeName":"7","sort":7},{"id":"ae446d3a92f24ed8bf0651fe5814a746","createDate":"2020-04-15 10:55:33","updateDate":"2020-04-15 10:55:33","delFlag":"0","typeName":"haodehen","sort":3},{"id":"ca4e1f9c1c214dc5b5f1959a1bce54be","createDate":"2020-04-15 09:29:37","updateDate":"2020-04-15 09:29:37","delFlag":"0","typeName":"111","sort":111},{"id":"d3b1d8323c7e46b798519a857a5f77f8","createDate":"2020-04-15 09:24:20","updateDate":"2020-04-15 09:24:20","delFlag":"0","typeName":"1","sort":1},{"id":"d4e49fb199a34a2a9ebd4085cd543934","createDate":"2020-04-15 09:25:07","updateDate":"2020-04-15 09:25:07","delFlag":"0","typeName":"11","sort":11},{"id":"e1c54f42da194bf89c3c54aaad4f301a","createDate":"2020-04-15 09:24:46","updateDate":"2020-04-15 09:24:46","delFlag":"0","typeName":"6","sort":6}]
	 * series : {"pageNo":1,"pageSize":20,"totalPage":1,"list":[{"id":"string","createDate":"2020-04-15 10:31:34","updateDate":"2020-04-15 10:31:34","delFlag":"0","name":"22","typeId":"0","pageId":"22","pageName":"123123123","isUp":0,"isSignUp":0,"pictureURL":"22","isSign":1,"typeName":"","upTime":"2020-04-15 16:32:59","type":1},{"id":"f50ec8ccd7ca4d139748ef19f3afacf9","createDate":"2020-04-15 16:33:00","updateDate":"2020-04-15 16:33:00","delFlag":"0","name":"3","typeId":"","typeName":"","pageId":"e70333f396314948b87ac3f945cb0276","pageName":"234234234","upTime":"2020-04-15 16:32:59","isUp":1,"isSignUp":1,"pictureURL":"http://syimage.zhongyouie.com//收藏.png","isSign":1},{"id":"ff938c6b80ff4bd0b86561d1aee016aa","createDate":"2020-04-15 16:36:48","updateDate":"2020-04-15 16:36:48","delFlag":"0","name":"33","typeId":"","typeName":"84f1c42184a6425198da1cc3ad552f56","pageId":"e70333f396314948b87ac3f945cb0276","pageName":"234234234","upTime":"2020-04-15 16:36:48","isUp":1,"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//收藏.png","isSign":1},{"id":"1401f804c1bd4c95ab82472cf851f3a0","createDate":"2020-04-16 09:16:59","updateDate":"2020-04-16 09:16:59","delFlag":"0","name":"直播","type":1,"typeId":"7af71d859a92493681f93974f66ac361","typeName":"441288","pageId":"e70333f396314948b87ac3f945cb0276","pageName":"123123","upTime":"2020-04-16 09:16:59","isUp":1,"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//叹号.svg","isSign":1},{"id":"10a276cef1974984a89b25303baf1527","createDate":"2020-04-16 11:11:19","updateDate":"2020-04-16 11:24:11","delFlag":"0","name":"wuw45","type":1,"typeId":"7af71d859a92493681f93974f66ac361","typeName":"441288","pageId":"e70333f396314948b87ac3f945cb0276","pageName":"123123","upTime":"2020-04-16 11:11:19","isUp":1,"isSignUp":1,"pictureURL":"http://syimage.zhongyouie.com//未标题-1.jpg","isSign":1},{"id":"532b5c5f608e4932b6807c3056c4b05d","createDate":"2020-04-16 18:08:40","updateDate":"2020-04-16 18:08:40","delFlag":"0","name":"33","typeId":"00ace02cfea84c69883854ede0c4fbcc","typeName":"22","pageId":"d6150eff76104b20af998fb58eabc676","pageName":"视频","upTime":"2020-04-16 18:08:33","isUp":1,"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//58fd993ecaf8d.jpg","isSign":1},{"id":"d2643ff17e8346e49236831125c752b0","createDate":"2020-04-16 18:10:34","updateDate":"2020-04-16 18:10:34","delFlag":"0","name":"2323","typeId":"04db0f3fc6294294b01580510e42b2ec","typeName":"44","pageId":"d6150eff76104b20af998fb58eabc676","pageName":"视频","upTime":"2020-04-16 18:09:42","isUp":1,"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//58fd993ecaf8d.jpg","isSign":1},{"id":"16bc5bc7cdd340b2b4ca316ee8f97c2f","createDate":"2020-04-13 10:24:47","updateDate":"2020-04-13 10:41:33","delFlag":"0","name":"123123123","typeId":"2","typeName":"录播","pageId":"1231231423542134asd","pageName":"123123123","upTime":"2020-10-01 00:00:00","isUp":1,"isSignUp":0,"pictureURL":"sdfsdfasdasd","isSign":1}],"orderBy":"","count":8,"firstResult":0,"maxResults":20}
	 */



	private SeriesBean series;
	private List<ColumnsBean> columns;

	public SeriesBean getSeries() {
		return series;
	}

	public void setSeries(SeriesBean series) {
		this.series = series;
	}

	public List<ColumnsBean> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnsBean> columns) {
		this.columns = columns;
	}

	public static class SeriesBean {

		public SeriesBean() {
		}

		/**
		 * pageNo : 1
		 * pageSize : 20
		 * totalPage : 1
		 * list : [{"id":"string","createDate":"2020-04-15 10:31:34","updateDate":"2020-04-15 10:31:34","delFlag":"0","name":"22","typeId":"0","pageId":"22","pageName":"123123123","isUp":0,"isSignUp":0,"pictureURL":"22","isSign":1},{"id":"f50ec8ccd7ca4d139748ef19f3afacf9","createDate":"2020-04-15 16:33:00","updateDate":"2020-04-15 16:33:00","delFlag":"0","name":"3","typeId":"","typeName":"","pageId":"e70333f396314948b87ac3f945cb0276","pageName":"234234234","upTime":"2020-04-15 16:32:59","isUp":1,"isSignUp":1,"pictureURL":"http://syimage.zhongyouie.com//收藏.png","isSign":1},{"id":"ff938c6b80ff4bd0b86561d1aee016aa","createDate":"2020-04-15 16:36:48","updateDate":"2020-04-15 16:36:48","delFlag":"0","name":"33","typeId":"","typeName":"84f1c42184a6425198da1cc3ad552f56","pageId":"e70333f396314948b87ac3f945cb0276","pageName":"234234234","upTime":"2020-04-15 16:36:48","isUp":1,"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//收藏.png","isSign":1},{"id":"1401f804c1bd4c95ab82472cf851f3a0","createDate":"2020-04-16 09:16:59","updateDate":"2020-04-16 09:16:59","delFlag":"0","name":"直播","type":1,"typeId":"7af71d859a92493681f93974f66ac361","typeName":"441288","pageId":"e70333f396314948b87ac3f945cb0276","pageName":"123123","upTime":"2020-04-16 09:16:59","isUp":1,"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//叹号.svg","isSign":1},{"id":"10a276cef1974984a89b25303baf1527","createDate":"2020-04-16 11:11:19","updateDate":"2020-04-16 11:24:11","delFlag":"0","name":"wuw45","type":1,"typeId":"7af71d859a92493681f93974f66ac361","typeName":"441288","pageId":"e70333f396314948b87ac3f945cb0276","pageName":"123123","upTime":"2020-04-16 11:11:19","isUp":1,"isSignUp":1,"pictureURL":"http://syimage.zhongyouie.com//未标题-1.jpg","isSign":1},{"id":"532b5c5f608e4932b6807c3056c4b05d","createDate":"2020-04-16 18:08:40","updateDate":"2020-04-16 18:08:40","delFlag":"0","name":"33","typeId":"00ace02cfea84c69883854ede0c4fbcc","typeName":"22","pageId":"d6150eff76104b20af998fb58eabc676","pageName":"视频","upTime":"2020-04-16 18:08:33","isUp":1,"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//58fd993ecaf8d.jpg","isSign":1},{"id":"d2643ff17e8346e49236831125c752b0","createDate":"2020-04-16 18:10:34","updateDate":"2020-04-16 18:10:34","delFlag":"0","name":"2323","typeId":"04db0f3fc6294294b01580510e42b2ec","typeName":"44","pageId":"d6150eff76104b20af998fb58eabc676","pageName":"视频","upTime":"2020-04-16 18:09:42","isUp":1,"isSignUp":0,"pictureURL":"http://syimage.zhongyouie.com//58fd993ecaf8d.jpg","isSign":1},{"id":"16bc5bc7cdd340b2b4ca316ee8f97c2f","createDate":"2020-04-13 10:24:47","updateDate":"2020-04-13 10:41:33","delFlag":"0","name":"123123123","typeId":"2","typeName":"录播","pageId":"1231231423542134asd","pageName":"123123123","upTime":"2020-10-01 00:00:00","isUp":1,"isSignUp":0,"pictureURL":"sdfsdfasdasd","isSign":1}]
		 * orderBy :
		 * count : 8
		 * firstResult : 0
		 * maxResults : 20
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

		public static class ListBean implements Parcelable {
			/**
			 * id : string
			 * createDate : 2020-04-15 10:31:34
			 * updateDate : 2020-04-15 10:31:34
			 * delFlag : 0
			 * name : 22
			 * typeId : 0
			 * pageId : 22
			 * pageName : 123123123
			 * isUp : 0
			 * isSignUp : 0
			 * pictureURL : 22
			 * isSign : 1
			 * typeName :
			 * upTime : 2020-04-15 16:32:59
			 * type : 1
			 */

			private String id;
			private String createDate;
			private String updateDate;
			private String delFlag;
			private String name;
			private String typeId;
			private String pageId;
			private String pageName;
			private int isUp;
			private int isSignUp;
			private String pictureURL;
			private int isSign;
			private String typeName;
			private String upTime;
			private int type;
			private int isAuth;
			private int singUpNum;
			private int clickCount;
			private String seriesIntro;
			private long studyNum;
			private String lecturerId;
			private String lecturerInfo;
			private int sort;
			private long videoNum;
			private String lecturerHeadUrl;
			private String studyNumStr;

			public ListBean() {
			}

			protected ListBean(Parcel in) {
				id = in.readString();
				createDate = in.readString();
				updateDate = in.readString();
				delFlag = in.readString();
				name = in.readString();
				typeId = in.readString();
				pageId = in.readString();
				pageName = in.readString();
				isUp = in.readInt();
				isSignUp = in.readInt();
				pictureURL = in.readString();
				isSign = in.readInt();
				typeName = in.readString();
				upTime = in.readString();
				type = in.readInt();
				isAuth = in.readInt();
				singUpNum = in.readInt();
				clickCount = in.readInt();
				seriesIntro = in.readString();
				studyNum = in.readLong();
				lecturerId = in.readString();
				lecturerInfo = in.readString();
				sort = in.readInt();
				videoNum = in.readLong();
				lecturerHeadUrl = in.readString();
				studyNumStr = in.readString();
			}

			public static final Creator<ListBean> CREATOR = new Creator<ListBean>() {
				@Override
				public ListBean createFromParcel(Parcel in) {
					return new ListBean(in);
				}

				@Override
				public ListBean[] newArray(int size) {
					return new ListBean[size];
				}
			};

			public String getStudyNumStr() {
				return studyNumStr;
			}

			public void setStudyNumStr(String studyNumStr) {
				this.studyNumStr = studyNumStr;
			}

			public int getSingUpNum() {
				return singUpNum;
			}

			public void setSingUpNum(int singUpNum) {
				this.singUpNum = singUpNum;
			}

			public int getClickCount() {
				return clickCount;
			}

			public void setClickCount(int clickCount) {
				this.clickCount = clickCount;
			}

			public String getSeriesIntro() {
				return seriesIntro;
			}

			public void setSeriesIntro(String seriesIntro) {
				this.seriesIntro = seriesIntro;
			}

			public long getStudyNum() {
				return studyNum;
			}

			public void setStudyNum(long studyNum) {
				this.studyNum = studyNum;
			}

			public String getLecturerId() {
				return lecturerId;
			}

			public void setLecturerId(String lecturerId) {
				this.lecturerId = lecturerId;
			}

			public String getLecturerInfo() {
				return lecturerInfo;
			}

			public void setLecturerInfo(String lecturerInfo) {
				this.lecturerInfo = lecturerInfo;
			}

			public int getSort() {
				return sort;
			}

			public void setSort(int sort) {
				this.sort = sort;
			}

			public long getVideoNum() {
				return videoNum;
			}

			public void setVideoNum(long videoNum) {
				this.videoNum = videoNum;
			}

			public String getLecturerHeadUrl() {
				return lecturerHeadUrl;
			}

			public void setLecturerHeadUrl(String lecturerHeadUrl) {
				this.lecturerHeadUrl = lecturerHeadUrl;
			}

			public int getIsAuth() {
				return isAuth;
			}

			public void setIsAuth(int isAuth) {
				this.isAuth = isAuth;
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

			public String getTypeId() {
				return typeId;
			}

			public void setTypeId(String typeId) {
				this.typeId = typeId;
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

			public int getIsSign() {
				return isSign;
			}

			public void setIsSign(int isSign) {
				this.isSign = isSign;
			}

			public String getTypeName() {
				return typeName;
			}

			public void setTypeName(String typeName) {
				this.typeName = typeName;
			}

			public String getUpTime() {
				return upTime;
			}

			public void setUpTime(String upTime) {
				this.upTime = upTime;
			}

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public void writeToParcel(Parcel dest, int flags) {
				dest.writeString(id);
				dest.writeString(createDate);
				dest.writeString(updateDate);
				dest.writeString(delFlag);
				dest.writeString(name);
				dest.writeString(typeId);
				dest.writeString(pageId);
				dest.writeString(pageName);
				dest.writeInt(isUp);
				dest.writeInt(isSignUp);
				dest.writeString(pictureURL);
				dest.writeInt(isSign);
				dest.writeString(typeName);
				dest.writeString(upTime);
				dest.writeInt(type);
				dest.writeInt(isAuth);
				dest.writeInt(singUpNum);
				dest.writeInt(clickCount);
				dest.writeString(seriesIntro);
				dest.writeLong(studyNum);
				dest.writeString(lecturerId);
				dest.writeString(lecturerInfo);
				dest.writeInt(sort);
				dest.writeLong(videoNum);
				dest.writeString(lecturerHeadUrl);
				dest.writeString(studyNumStr);
			}
		}
	}

	public static class ColumnsBean {
		/**
		 * id : 00ace02cfea84c69883854ede0c4fbcc
		 * createDate : 2020-04-16 17:35:56
		 * updateDate : 2020-04-16 17:35:56
		 * delFlag : 0
		 * typeName : 22
		 * sort : 33
		 */

		private String id;
		private String createDate;
		private String updateDate;
		private String delFlag;
		private String typeName;
		private int sort;

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

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}
	}
}
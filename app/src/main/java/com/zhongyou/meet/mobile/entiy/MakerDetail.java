package com.zhongyou.meet.mobile.entiy;

import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/17 2:27 PM.
 * @
 */
public class MakerDetail {

	/**
	 * seriesIntro :
	 * lecturerHeadUrl :
	 * studyNum : 0
	 * pictureURL : http://syimage.zhongyouie.com//15889292830463221586920544_.pic_hd.jpg
	 * isRecord : 0
	 * lecturerInfo :
	 * videoNum : 6
	 * type : 1
	 * introduceURL : http://syimage.zhongyouie.com//1588934913245WechatIMG174.jpeg
	 * isSign : 0
	 * lecturerId :
	 * subPages : [{"name":"01-A 新高考改革进行时","subPageURL":"http://syimage.zhongyouie.com//1590580756329cover.png","sort":0,"type":3,"subPageId":"dfb35ea1c7f44609a1fe75acab79b2bd"},{"name":"01-1 如何有效开发孩子的视觉能力？","subPageURL":"http://syimage.zhongyouie.com//1588928945682banner-02.png","sort":1,"type":3,"subPageId":"9d257aba533f4b86b056565d28bb8898"},{"name":"01-2 如何有效开发孩子的视觉能力？","subPageURL":"http://syimage.zhongyouie.com//1588936138351banner-02.png","sort":2,"type":3,"subPageId":"e4201131d29e48199a186a22b5da359d"},{"name":"发现潜能，成就人生01","subPageURL":"http://syimage.zhongyouie.com//1591164953202发现潜能2.jpg","sort":3,"type":3,"subPageId":"a316ece62b244a3491fc0935acf23032"},{"name":"脑科学与儿童学习变革 01","subPageURL":"http://syimage.zhongyouie.com//1591165111405脑科学与儿童学习变革2.jpg","sort":4,"type":3,"subPageId":"213ba0cc04744ecda3bc4df2d7673fa6"},{"name":"11-C如何帮孩子培养好习惯？","subPageURL":"http://syimage.zhongyouie.com//1591863260552微信图片_20200525143220.jpg","sort":5,"type":3,"subPageId":"547ee7e80f464416a0885ee28e838ad6"},{"name":"测试","subPageURL":"http://syimage.zhongyouie.com//15923792315283211586920543_.pic_hd.jpg","sort":6,"type":2,"subPageId":"ded40cde89894211a1ce8e9ea9eb3516"}]
	 * name : 0-9岁儿童的35个成长关键期
	 * id : 294a8bc9ef424160af4ab3465cf456bb
	 * isNeedRecord : 0
	 * introduction : 1.cron表达式特殊字符意义示意表 * 匹配所有的值。如:*在分钟的字段域里表示 每分钟 指定几个可选值。如:“MON,WED,FRI”在星期域里表示“星期一、星期三、星期五” 指定增量。如:“0/1...
	 */

	private String seriesIntro;
	private String lecturerHeadUrl;
	private int studyNum;
	private String studyNumStr;
	private String pictureURL;
	private int isRecord;
	private String lecturerInfo;
	private int videoNum;
	private int type;
	private String introduceURL;
	private int isSign;
	private String lecturerId;
	private String name;
	private String id;
	private int isNeedRecord;
	private String introduction;
	private int isAuth;
	private String headUrl;
	private List<SubPagesBean> subPages;

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getStudyNumStr() {
		return studyNumStr;
	}

	public void setStudyNumStr(String studyNumStr) {
		this.studyNumStr = studyNumStr;
	}

	public int getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(int isAuth) {
		this.isAuth = isAuth;
	}

	public String getSeriesIntro() {
		return seriesIntro;
	}

	public void setSeriesIntro(String seriesIntro) {
		this.seriesIntro = seriesIntro;
	}

	public String getLecturerHeadUrl() {
		return lecturerHeadUrl;
	}

	public void setLecturerHeadUrl(String lecturerHeadUrl) {
		this.lecturerHeadUrl = lecturerHeadUrl;
	}

	public int getStudyNum() {
		return studyNum;
	}

	public void setStudyNum(int studyNum) {
		this.studyNum = studyNum;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public int getIsRecord() {
		return isRecord;
	}

	public void setIsRecord(int isRecord) {
		this.isRecord = isRecord;
	}

	public String getLecturerInfo() {
		return lecturerInfo;
	}

	public void setLecturerInfo(String lecturerInfo) {
		this.lecturerInfo = lecturerInfo;
	}

	public int getVideoNum() {
		return videoNum;
	}

	public void setVideoNum(int videoNum) {
		this.videoNum = videoNum;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getIntroduceURL() {
		return introduceURL;
	}

	public void setIntroduceURL(String introduceURL) {
		this.introduceURL = introduceURL;
	}

	public int getIsSign() {
		return isSign;
	}

	public void setIsSign(int isSign) {
		this.isSign = isSign;
	}

	public String getLecturerId() {
		return lecturerId;
	}

	public void setLecturerId(String lecturerId) {
		this.lecturerId = lecturerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIsNeedRecord() {
		return isNeedRecord;
	}

	public void setIsNeedRecord(int isNeedRecord) {
		this.isNeedRecord = isNeedRecord;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public List<SubPagesBean> getSubPages() {
		return subPages;
	}

	public void setSubPages(List<SubPagesBean> subPages) {
		this.subPages = subPages;
	}

	public static class SubPagesBean {
		/**
		 * name : 01-A 新高考改革进行时
		 * subPageURL : http://syimage.zhongyouie.com//1590580756329cover.png
		 * sort : 0
		 * type : 3
		 * subPageId : dfb35ea1c7f44609a1fe75acab79b2bd
		 * resourceTime -> 00:16:09
		 */

		private String name;
		private String subPageURL;
		private int sort;
		private int type;
		private String subPageId;
		private String resourceTime;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSubPageURL() {
			return subPageURL;
		}

		public void setSubPageURL(String subPageURL) {
			this.subPageURL = subPageURL;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getSubPageId() {
			return subPageId;
		}

		public void setSubPageId(String subPageId) {
			this.subPageId = subPageId;
		}

		public String getResourceTime() {
			return resourceTime;
		}

		public void setResourceTime(String resourceTime) {
			this.resourceTime = resourceTime;
		}
	}
}

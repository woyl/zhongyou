package com.zhongyou.meet.mobile.entiy;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/17 4:34 PM.
 * @
 */
public class Question {

	/**
	 * id : 111fe278630344b190368ab0b2ffa2d2
	 * createDate : 2020-04-15 14:12:15
	 * updateDate : 2020-04-15 14:12:15
	 * delFlag : 0
	 * name : 问题561
	 * sort : 1
	 * pageId : 7a35c86e552949959d9c9aecd47ec340
	 */

	private String id;
	private String createDate;
	private String updateDate;
	private String delFlag;
	private String name;
	private int sort;
	private String pageId;
	private double score;

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
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

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
}

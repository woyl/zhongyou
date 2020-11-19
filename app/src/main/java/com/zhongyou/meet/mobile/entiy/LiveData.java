package com.zhongyou.meet.mobile.entiy;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/17 5:46 PM.
 * @
 */
public class LiveData {

	/**
	 * name : 测试1
	 * isRecord : 0
	 * isLiveRecord : 1
	 * type : 2
	 * introduceURL : askludhakljsdhkjalhsbd
	 * liveId : 3216545341
	 * parentId : e70333f396314948b87ac3f945cb0277
	 */

	private String name;
	private int isRecord;
	private int isLiveRecord;
	private int type;
	private String introduceURL;
	private String liveId;
	private String parentId;
	private String id;
	private int isToken;
	private String topUrl ;

	private int isNeedRecord;

	public int getIsNeedRecord() {
		return isNeedRecord;
	}

	public void setIsNeedRecord(int isNeedRecord) {
		this.isNeedRecord = isNeedRecord;
	}

	public String getTopUrl() {
		return topUrl;
	}

	public void setTopUrl(String topUrl) {
		this.topUrl = topUrl;
	}

	public int getIsToken() {
		return isToken;
	}

	public void setIsToken(int isToken) {
		this.isToken = isToken;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsRecord() {
		return isRecord;
	}

	public void setIsRecord(int isRecord) {
		this.isRecord = isRecord;
	}

	public int getIsLiveRecord() {
		return isLiveRecord;
	}

	public void setIsLiveRecord(int isLiveRecord) {
		this.isLiveRecord = isLiveRecord;
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

	public String getLiveId() {
		return liveId;
	}

	public void setLiveId(String liveId) {
		this.liveId = liveId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}

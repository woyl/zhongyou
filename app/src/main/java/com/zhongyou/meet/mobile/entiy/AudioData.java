package com.zhongyou.meet.mobile.entiy;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/17 3:18 PM.
 * @
 */
public class AudioData {

	/**
	 * resourceURL : 123
	 * name : 音频
	 * isRecord : 1
	 * id : 7a35c86e552949959d9c9aecd47ec340
	 * type : 4
	 * title :
	 * introduceURL : 123
	 * resourceTime :
	 */

	private String resourceURL;
	private String name;
	private int isRecord;
	private String id;
	private int type;
	private String title;
	private String introduceURL;
	private String resourceTime;
	private int isNeedRecord;

	private String pictureURL;

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public int getIsNeedRecord() {
		return isNeedRecord;
	}

	public void setIsNeedRecord(int isNeedRecord) {
		this.isNeedRecord = isNeedRecord;
	}

	public String getResourceURL() {
		return resourceURL;
	}

	public void setResourceURL(String resourceURL) {
		this.resourceURL = resourceURL;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntroduceURL() {
		return introduceURL;
	}

	public void setIntroduceURL(String introduceURL) {
		this.introduceURL = introduceURL;
	}

	public String getResourceTime() {
		return resourceTime;
	}

	public void setResourceTime(String resourceTime) {
		this.resourceTime = resourceTime;
	}
}

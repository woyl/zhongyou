package com.zhongyou.meet.mobile.entiy;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/16 9:57 AM.
 * @
 */
public class MoreAudio {

	/**
	 * date : 2020-04
	 * list : [{"onLineTime":"2020-04-03 12:00:00.0","belongTime":"2020-04-12","vioceURL":"vioceURL","name":"sdasda","comentURL":"comentURL","anchorName":"zhuboming","totalDate":"12.25"},{"onLineTime":"2020-04-15 15:44:29.0","belongTime":"2020-04-11","vioceURL":"","name":"aaa","comentURL":"","anchorName":"zhuboming","totalDate":""}]
	 */

	/**
	 * onLineTime : 2020-04-03 12:00:00.0
	 * belongTime : 2020-04-12
	 * vioceURL : vioceURL
	 * name : sdasda
	 * comentURL : comentURL
	 * anchorName : zhuboming
	 * totalDate : 12.25
	 */
	private String date;
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	private String url;
	private int type;//0 闲置状态 1 播放状态 2 暂停状态 3 加载中
	private long currentDuration;
	private boolean isExpanded;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getCurrentDuration() {
		return currentDuration;
	}

	public void setCurrentDuration(long currentDuration) {
		this.currentDuration = currentDuration;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean expanded) {
		isExpanded = expanded;
	}

	private String onLineTime;
	private String belongTime;
	private String vioceURL;
	private String name;
	private String comentURL;
	private String anchorName;
	private String totalDate;

	private int totalTime;

	private String id;
	private String smallUrl;

	public String getSmallUrl() {
		return smallUrl;
	}

	public void setSmallUrl(String smallUrl) {
		this.smallUrl = smallUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	public String getOnLineTime() {
		return onLineTime;
	}

	public void setOnLineTime(String onLineTime) {
		this.onLineTime = onLineTime;
	}

	public String getBelongTime() {
		return belongTime;
	}

	public void setBelongTime(String belongTime) {
		this.belongTime = belongTime;
	}

	public String getVioceURL() {
		return vioceURL;
	}

	public void setVioceURL(String vioceURL) {
		this.vioceURL = vioceURL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComentURL() {
		return comentURL;
	}

	public void setComentURL(String comentURL) {
		this.comentURL = comentURL;
	}

	public String getAnchorName() {
		return anchorName;
	}

	public void setAnchorName(String anchorName) {
		this.anchorName = anchorName;
	}

	public String getTotalDate() {
		return totalDate;
	}

	public void setTotalDate(String totalDate) {
		this.totalDate = totalDate;
	}
}

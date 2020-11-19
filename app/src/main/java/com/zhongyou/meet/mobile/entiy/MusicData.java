package com.zhongyou.meet.mobile.entiy;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/8 3:32 PM.
 * @
 */
public class MusicData implements MultiItemEntity {
	private String url;
	private int type;//0 闲置状态 1 播放状态 2 暂停状态 3 加载中
	private long currentDuration;
	private boolean isExpanded;
	private int itemType;

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean expanded) {
		isExpanded = expanded;
	}

	public long getCurrentDuration() {
		return currentDuration;
	}

	public void setCurrentDuration(long currentDuration) {
		this.currentDuration = currentDuration;
	}

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

	@Override
	public int getItemType() {
		return itemType;
	}
}

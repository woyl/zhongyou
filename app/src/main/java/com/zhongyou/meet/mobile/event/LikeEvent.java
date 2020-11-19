package com.zhongyou.meet.mobile.event;

/**
 * @author golangdorid@gmail.com
 * @date 2020/5/27 10:29 AM.
 * @
 */
public class LikeEvent {
	private boolean isLike;

	public LikeEvent(boolean isLike) {
		this.isLike = isLike;
	}

	public boolean isLike() {
		return isLike;
	}

	public void setLike(boolean like) {
		isLike = like;
	}
}

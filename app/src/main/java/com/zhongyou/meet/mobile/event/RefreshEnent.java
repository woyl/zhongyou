package com.zhongyou.meet.mobile.event;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/16 1:23 PM.
 * @
 */
public  class  RefreshEnent {
	private boolean isRefresh;


	public <T> RefreshEnent(boolean isRefresh) {
		this.isRefresh = isRefresh;

	}

	public boolean isRefresh() {
		return isRefresh;
	}

	public void setRefresh(boolean refresh) {
		isRefresh = refresh;
	}
}

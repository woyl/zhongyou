package com.zhongyou.meet.mobile.event;

/**
 * @author golangdorid@gmail.com
 * @date 2019-10-30 13:36.
 */
public class SocketStatus {
	private boolean isConnected;

	public SocketStatus(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(boolean connected) {
		isConnected = connected;
	}
}

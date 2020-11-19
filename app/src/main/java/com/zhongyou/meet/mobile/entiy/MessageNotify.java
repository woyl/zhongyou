package com.zhongyou.meet.mobile.entiy;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/15 9:41 AM.
 * @
 */
public class MessageNotify {

	/**
	 * number : 1
	 * time : 2020-04-22 12:00:00
	 * type : 3
	 * notice : aaa
	 */

	private int number;
	private String time;
	private int type;
	private String notice;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}
}

package com.zhongyou.meet.mobile.entiy;


import com.contrarywind.interfaces.IPickerViewData;
import com.lljjcoder.bean.CustomCityData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/14 2:37 PM.
 * @
 */
public class UserProfile implements IPickerViewData{


	/**
	 * id : 1114
	 * remarks :
	 * createDate : 2019-04-02 17:07:53
	 * updateDate : 2019-04-02 17:07:53
	 * delFlag : 0
	 * name : 家庭用户
	 * priority : 1
	 */
	private boolean isSelect;

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	private String id;
	private String remarks;
	private String createDate;
	private String updateDate;
	private String delFlag;
	private String name;
	private int priority;

	private List<UserProfile> list = new ArrayList();

	public List<UserProfile> getList() {
		return list;
	}

	public void setList(List<UserProfile> list) {
		this.list = list;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "UserProfile{" +
				"id='" + id + '\'' +
				", remarks='" + remarks + '\'' +
				", createDate='" + createDate + '\'' +
				", updateDate='" + updateDate + '\'' +
				", delFlag='" + delFlag + '\'' +
				", name='" + name + '\'' +
				", priority=" + priority +
				'}';
	}


	@Override
	public String getPickerViewText() {
		return name;
	}



}

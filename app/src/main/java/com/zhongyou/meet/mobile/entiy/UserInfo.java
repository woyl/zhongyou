package com.zhongyou.meet.mobile.entiy;

/**
 * @author golangdorid@gmail.com
 * @date 2020/3/23 9:00 PM.
 * @
 */
public class UserInfo {


	/**
	 * errcode : 0
	 * data : {"device":{"id":"aebc4f41f5ec44e0a1d3aba3ac171702","remarks":"","createDate":"2019-10-16 18:02:46","updateDate":"2020-03-16 13:37:07","delFlag":"0","uuid":"048dc3367b9c4348ba9bab51b35c12f3","androidId":"e5b2b099f1ab4cf5","manufacturer":"Amlogic","name":"Amlogic","model":"S905X_TM_ZHONGYOU","version":"","sdkVersion":23,"screenDensity":"width:1920,height:1080,density:1.5,densityDpi:240","display":"p212-userdebug 6.0.1 MHC19J 20170413 test-keys","finger":"Amlogic/p212/p212:6.0.1/MHC19J/20170413:userdebug/test-keys","socketId":"/sales#XBDkj7i5m4p8D_j2ABZy","regIp":"101.206.170.133","appVersion":"pad_2.3.0_33566720","aliDeviceUUID":"false","imei":"","cpuSerial":"210a8200f4ecc52f45162180fa99db64","androidDeviceId":"","buildSerial":"0123456789abcdef","country":"","province":"","city":"","district":"","beforeOpenDate":"2020-03-16 10:54:28","openDate":"2020-03-16 13:37:06","source":1,"openCount":806,"internalSpace":"1806mb","internalFreeSpace":"1219mb","sdSpace":"12157mb","sdFreeSpace":"2446mb"},"wechat":{"id":"72d5450b37044e5bbc9dd7c761a893ab","createDate":"2019-10-17 09:53:01","updateDate":"2020-03-05 12:21:31","delFlag":"0","officialAccounts":9,"subscribe":0,"openid":"ofGM21p6oRcqvFFoSxJyqwCpkT9s","sex":1,"headimgurl":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJLVjoerrpBqEZ6fq0OJlafd9ArC79GJrteNJVWdib3OUx1GbbRYGvR6387vhpA7YMTreuia1DbKABw/132","city":"","province":"","country":"Chile","unionid":"o9JUSwrjpdp27f5bC_6c1CTrggxY","language":"zh_CN","userId":"30dfd8680e0c4c8abc02277b9d64a041","source":0},"user":{"id":"42d07174b4b24fa1bf7cc83b11824de4","createDate":"2019-10-28 13:18:23","updateDate":"2020-03-09 14:34:22","delFlag":"0","name":"luo-iphone","mobile":"18582496944","address":"成都","photo":"http://syimage.zhongyouie.com/osg/user/expostor/photo/cf4c443ec0f54ab6bf99972babe42135.jpg","signature":"","token":"bbba382f927d4c4a88d531fbc8f91764","type":1,"rank":0,"source":9,"registerIp":"223.104.9.200","lastLoginIp":"223.104.9.200","lastLoginTime":"2020-03-23 20:03:23","shopPhoto":"","areaId":"e461cfe711414062a135c6b1d98fbb3f","areaInfo":"西南-成都-三台县北坝镇奇运电器经营部","areaName":"成都","customId":"e676eb29df6b11e9b62f00163e1428d9","customName":"三台县北坝镇奇运电器经营部","auditStatus":1,"postTypeId":"1114","postTypeName":"家庭用户","gridId":"","gridName":"","unionid":"o9JUSwrjpdp27f5bC_6c1CTrggxY"}}
	 * errmsg : 处理成功
	 */

	private int errcode;
	private DataBean data;
	private String errmsg;

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public static class DataBean {
		/**
		 * device : {"id":"aebc4f41f5ec44e0a1d3aba3ac171702","remarks":"","createDate":"2019-10-16 18:02:46","updateDate":"2020-03-16 13:37:07","delFlag":"0","uuid":"048dc3367b9c4348ba9bab51b35c12f3","androidId":"e5b2b099f1ab4cf5","manufacturer":"Amlogic","name":"Amlogic","model":"S905X_TM_ZHONGYOU","version":"","sdkVersion":23,"screenDensity":"width:1920,height:1080,density:1.5,densityDpi:240","display":"p212-userdebug 6.0.1 MHC19J 20170413 test-keys","finger":"Amlogic/p212/p212:6.0.1/MHC19J/20170413:userdebug/test-keys","socketId":"/sales#XBDkj7i5m4p8D_j2ABZy","regIp":"101.206.170.133","appVersion":"pad_2.3.0_33566720","aliDeviceUUID":"false","imei":"","cpuSerial":"210a8200f4ecc52f45162180fa99db64","androidDeviceId":"","buildSerial":"0123456789abcdef","country":"","province":"","city":"","district":"","beforeOpenDate":"2020-03-16 10:54:28","openDate":"2020-03-16 13:37:06","source":1,"openCount":806,"internalSpace":"1806mb","internalFreeSpace":"1219mb","sdSpace":"12157mb","sdFreeSpace":"2446mb"}
		 * wechat : {"id":"72d5450b37044e5bbc9dd7c761a893ab","createDate":"2019-10-17 09:53:01","updateDate":"2020-03-05 12:21:31","delFlag":"0","officialAccounts":9,"subscribe":0,"openid":"ofGM21p6oRcqvFFoSxJyqwCpkT9s","sex":1,"headimgurl":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJLVjoerrpBqEZ6fq0OJlafd9ArC79GJrteNJVWdib3OUx1GbbRYGvR6387vhpA7YMTreuia1DbKABw/132","city":"","province":"","country":"Chile","unionid":"o9JUSwrjpdp27f5bC_6c1CTrggxY","language":"zh_CN","userId":"30dfd8680e0c4c8abc02277b9d64a041","source":0}
		 * user : {"id":"42d07174b4b24fa1bf7cc83b11824de4","createDate":"2019-10-28 13:18:23","updateDate":"2020-03-09 14:34:22","delFlag":"0","name":"luo-iphone","mobile":"18582496944","address":"成都","photo":"http://syimage.zhongyouie.com/osg/user/expostor/photo/cf4c443ec0f54ab6bf99972babe42135.jpg","signature":"","token":"bbba382f927d4c4a88d531fbc8f91764","type":1,"rank":0,"source":9,"registerIp":"223.104.9.200","lastLoginIp":"223.104.9.200","lastLoginTime":"2020-03-23 20:03:23","shopPhoto":"","areaId":"e461cfe711414062a135c6b1d98fbb3f","areaInfo":"西南-成都-三台县北坝镇奇运电器经营部","areaName":"成都","customId":"e676eb29df6b11e9b62f00163e1428d9","customName":"三台县北坝镇奇运电器经营部","auditStatus":1,"postTypeId":"1114","postTypeName":"家庭用户","gridId":"","gridName":"","unionid":"o9JUSwrjpdp27f5bC_6c1CTrggxY"}
		 */

		private DeviceBean device;
		private WechatBean wechat;
		public UserBean user;

		public DeviceBean getDevice() {
			return device;
		}

		public void setDevice(DeviceBean device) {
			this.device = device;
		}

		public WechatBean getWechat() {
			return wechat;
		}

		public void setWechat(WechatBean wechat) {
			this.wechat = wechat;
		}

		public UserBean getUser() {
			return user;
		}

		public void setUser(UserBean user) {
			this.user = user;
		}

		public static class DeviceBean {
			/**
			 * id : aebc4f41f5ec44e0a1d3aba3ac171702
			 * remarks :
			 * createDate : 2019-10-16 18:02:46
			 * updateDate : 2020-03-16 13:37:07
			 * delFlag : 0
			 * uuid : 048dc3367b9c4348ba9bab51b35c12f3
			 * androidId : e5b2b099f1ab4cf5
			 * manufacturer : Amlogic
			 * name : Amlogic
			 * model : S905X_TM_ZHONGYOU
			 * version :
			 * sdkVersion : 23
			 * screenDensity : width:1920,height:1080,density:1.5,densityDpi:240
			 * display : p212-userdebug 6.0.1 MHC19J 20170413 test-keys
			 * finger : Amlogic/p212/p212:6.0.1/MHC19J/20170413:userdebug/test-keys
			 * socketId : /sales#XBDkj7i5m4p8D_j2ABZy
			 * regIp : 101.206.170.133
			 * appVersion : pad_2.3.0_33566720
			 * aliDeviceUUID : false
			 * imei :
			 * cpuSerial : 210a8200f4ecc52f45162180fa99db64
			 * androidDeviceId :
			 * buildSerial : 0123456789abcdef
			 * country :
			 * province :
			 * city :
			 * district :
			 * beforeOpenDate : 2020-03-16 10:54:28
			 * openDate : 2020-03-16 13:37:06
			 * source : 1
			 * openCount : 806
			 * internalSpace : 1806mb
			 * internalFreeSpace : 1219mb
			 * sdSpace : 12157mb
			 * sdFreeSpace : 2446mb
			 */

			private String id;
			private String remarks;
			private String createDate;
			private String updateDate;
			private String delFlag;
			private String uuid;
			private String androidId;
			private String manufacturer;
			private String name;
			private String model;
			private String version;
			private int sdkVersion;
			private String screenDensity;
			private String display;
			private String finger;
			private String socketId;
			private String regIp;
			private String appVersion;
			private String aliDeviceUUID;
			private String imei;
			private String cpuSerial;
			private String androidDeviceId;
			private String buildSerial;
			private String country;
			private String province;
			private String city;
			private String district;
			private String beforeOpenDate;
			private String openDate;
			private int source;
			private int openCount;
			private String internalSpace;
			private String internalFreeSpace;
			private String sdSpace;
			private String sdFreeSpace;

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

			public String getUuid() {
				return uuid;
			}

			public void setUuid(String uuid) {
				this.uuid = uuid;
			}

			public String getAndroidId() {
				return androidId;
			}

			public void setAndroidId(String androidId) {
				this.androidId = androidId;
			}

			public String getManufacturer() {
				return manufacturer;
			}

			public void setManufacturer(String manufacturer) {
				this.manufacturer = manufacturer;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getModel() {
				return model;
			}

			public void setModel(String model) {
				this.model = model;
			}

			public String getVersion() {
				return version;
			}

			public void setVersion(String version) {
				this.version = version;
			}

			public int getSdkVersion() {
				return sdkVersion;
			}

			public void setSdkVersion(int sdkVersion) {
				this.sdkVersion = sdkVersion;
			}

			public String getScreenDensity() {
				return screenDensity;
			}

			public void setScreenDensity(String screenDensity) {
				this.screenDensity = screenDensity;
			}

			public String getDisplay() {
				return display;
			}

			public void setDisplay(String display) {
				this.display = display;
			}

			public String getFinger() {
				return finger;
			}

			public void setFinger(String finger) {
				this.finger = finger;
			}

			public String getSocketId() {
				return socketId;
			}

			public void setSocketId(String socketId) {
				this.socketId = socketId;
			}

			public String getRegIp() {
				return regIp;
			}

			public void setRegIp(String regIp) {
				this.regIp = regIp;
			}

			public String getAppVersion() {
				return appVersion;
			}

			public void setAppVersion(String appVersion) {
				this.appVersion = appVersion;
			}

			public String getAliDeviceUUID() {
				return aliDeviceUUID;
			}

			public void setAliDeviceUUID(String aliDeviceUUID) {
				this.aliDeviceUUID = aliDeviceUUID;
			}

			public String getImei() {
				return imei;
			}

			public void setImei(String imei) {
				this.imei = imei;
			}

			public String getCpuSerial() {
				return cpuSerial;
			}

			public void setCpuSerial(String cpuSerial) {
				this.cpuSerial = cpuSerial;
			}

			public String getAndroidDeviceId() {
				return androidDeviceId;
			}

			public void setAndroidDeviceId(String androidDeviceId) {
				this.androidDeviceId = androidDeviceId;
			}

			public String getBuildSerial() {
				return buildSerial;
			}

			public void setBuildSerial(String buildSerial) {
				this.buildSerial = buildSerial;
			}

			public String getCountry() {
				return country;
			}

			public void setCountry(String country) {
				this.country = country;
			}

			public String getProvince() {
				return province;
			}

			public void setProvince(String province) {
				this.province = province;
			}

			public String getCity() {
				return city;
			}

			public void setCity(String city) {
				this.city = city;
			}

			public String getDistrict() {
				return district;
			}

			public void setDistrict(String district) {
				this.district = district;
			}

			public String getBeforeOpenDate() {
				return beforeOpenDate;
			}

			public void setBeforeOpenDate(String beforeOpenDate) {
				this.beforeOpenDate = beforeOpenDate;
			}

			public String getOpenDate() {
				return openDate;
			}

			public void setOpenDate(String openDate) {
				this.openDate = openDate;
			}

			public int getSource() {
				return source;
			}

			public void setSource(int source) {
				this.source = source;
			}

			public int getOpenCount() {
				return openCount;
			}

			public void setOpenCount(int openCount) {
				this.openCount = openCount;
			}

			public String getInternalSpace() {
				return internalSpace;
			}

			public void setInternalSpace(String internalSpace) {
				this.internalSpace = internalSpace;
			}

			public String getInternalFreeSpace() {
				return internalFreeSpace;
			}

			public void setInternalFreeSpace(String internalFreeSpace) {
				this.internalFreeSpace = internalFreeSpace;
			}

			public String getSdSpace() {
				return sdSpace;
			}

			public void setSdSpace(String sdSpace) {
				this.sdSpace = sdSpace;
			}

			public String getSdFreeSpace() {
				return sdFreeSpace;
			}

			public void setSdFreeSpace(String sdFreeSpace) {
				this.sdFreeSpace = sdFreeSpace;
			}
		}

		public static class WechatBean {
			/**
			 * id : 72d5450b37044e5bbc9dd7c761a893ab
			 * createDate : 2019-10-17 09:53:01
			 * updateDate : 2020-03-05 12:21:31
			 * delFlag : 0
			 * officialAccounts : 9
			 * subscribe : 0
			 * openid : ofGM21p6oRcqvFFoSxJyqwCpkT9s
			 * sex : 1
			 * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJLVjoerrpBqEZ6fq0OJlafd9ArC79GJrteNJVWdib3OUx1GbbRYGvR6387vhpA7YMTreuia1DbKABw/132
			 * city :
			 * province :
			 * country : Chile
			 * unionid : o9JUSwrjpdp27f5bC_6c1CTrggxY
			 * language : zh_CN
			 * userId : 30dfd8680e0c4c8abc02277b9d64a041
			 * source : 0
			 */

			private String id;
			private String createDate;
			private String updateDate;
			private String delFlag;
			private int officialAccounts;
			private int subscribe;
			private String openid;
			private int sex;
			private String headimgurl;
			private String city;
			private String province;
			private String country;
			private String unionid;
			private String language;
			private String userId;
			private int source;

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

			public int getOfficialAccounts() {
				return officialAccounts;
			}

			public void setOfficialAccounts(int officialAccounts) {
				this.officialAccounts = officialAccounts;
			}

			public int getSubscribe() {
				return subscribe;
			}

			public void setSubscribe(int subscribe) {
				this.subscribe = subscribe;
			}

			public String getOpenid() {
				return openid;
			}

			public void setOpenid(String openid) {
				this.openid = openid;
			}

			public int getSex() {
				return sex;
			}

			public void setSex(int sex) {
				this.sex = sex;
			}

			public String getHeadimgurl() {
				return headimgurl;
			}

			public void setHeadimgurl(String headimgurl) {
				this.headimgurl = headimgurl;
			}

			public String getCity() {
				return city;
			}

			public void setCity(String city) {
				this.city = city;
			}

			public String getProvince() {
				return province;
			}

			public void setProvince(String province) {
				this.province = province;
			}

			public String getCountry() {
				return country;
			}

			public void setCountry(String country) {
				this.country = country;
			}

			public String getUnionid() {
				return unionid;
			}

			public void setUnionid(String unionid) {
				this.unionid = unionid;
			}

			public String getLanguage() {
				return language;
			}

			public void setLanguage(String language) {
				this.language = language;
			}

			public String getUserId() {
				return userId;
			}

			public void setUserId(String userId) {
				this.userId = userId;
			}

			public int getSource() {
				return source;
			}

			public void setSource(int source) {
				this.source = source;
			}
		}

		public static class UserBean {
			/**
			 * id : 42d07174b4b24fa1bf7cc83b11824de4
			 * createDate : 2019-10-28 13:18:23
			 * updateDate : 2020-03-09 14:34:22
			 * delFlag : 0
			 * name : luo-iphone
			 * mobile : 18582496944
			 * address : 成都
			 * photo : http://syimage.zhongyouie.com/osg/user/expostor/photo/cf4c443ec0f54ab6bf99972babe42135.jpg
			 * signature :
			 * token : bbba382f927d4c4a88d531fbc8f91764
			 * type : 1
			 * rank : 0
			 * source : 9
			 * registerIp : 223.104.9.200
			 * lastLoginIp : 223.104.9.200
			 * lastLoginTime : 2020-03-23 20:03:23
			 * shopPhoto :
			 * areaId : e461cfe711414062a135c6b1d98fbb3f
			 * areaInfo : 西南-成都-三台县北坝镇奇运电器经营部
			 * areaName : 成都
			 * customId : e676eb29df6b11e9b62f00163e1428d9
			 * customName : 三台县北坝镇奇运电器经营部
			 * auditStatus : 1
			 * postTypeId : 1114
			 * postTypeName : 家庭用户
			 * gridId :
			 * gridName :
			 * unionid : o9JUSwrjpdp27f5bC_6c1CTrggxY
			 */

			private String id;
			private String createDate;
			private String updateDate;
			private String delFlag;
			private String name;
			private String mobile;
			private String address;
			private String photo;
			private String signature;
			private String token;
			private int type;
			private int rank;
			private int source;
			private String registerIp;
			private String lastLoginIp;
			private String lastLoginTime;
			private String shopPhoto;
			private String areaId;
			private String areaInfo;
			private String areaName;
			private String customId;
			private String customName;
			private int auditStatus;
			private String postTypeId;
			private String postTypeName;
			private String gridId;
			private String gridName;
			private String unionid;

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

			public String getMobile() {
				return mobile;
			}

			public void setMobile(String mobile) {
				this.mobile = mobile;
			}

			public String getAddress() {
				return address;
			}

			public void setAddress(String address) {
				this.address = address;
			}

			public String getPhoto() {
				return photo;
			}

			public void setPhoto(String photo) {
				this.photo = photo;
			}

			public String getSignature() {
				return signature;
			}

			public void setSignature(String signature) {
				this.signature = signature;
			}

			public String getToken() {
				return token;
			}

			public void setToken(String token) {
				this.token = token;
			}

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}

			public int getRank() {
				return rank;
			}

			public void setRank(int rank) {
				this.rank = rank;
			}

			public int getSource() {
				return source;
			}

			public void setSource(int source) {
				this.source = source;
			}

			public String getRegisterIp() {
				return registerIp;
			}

			public void setRegisterIp(String registerIp) {
				this.registerIp = registerIp;
			}

			public String getLastLoginIp() {
				return lastLoginIp;
			}

			public void setLastLoginIp(String lastLoginIp) {
				this.lastLoginIp = lastLoginIp;
			}

			public String getLastLoginTime() {
				return lastLoginTime;
			}

			public void setLastLoginTime(String lastLoginTime) {
				this.lastLoginTime = lastLoginTime;
			}

			public String getShopPhoto() {
				return shopPhoto;
			}

			public void setShopPhoto(String shopPhoto) {
				this.shopPhoto = shopPhoto;
			}

			public String getAreaId() {
				return areaId;
			}

			public void setAreaId(String areaId) {
				this.areaId = areaId;
			}

			public String getAreaInfo() {
				return areaInfo;
			}

			public void setAreaInfo(String areaInfo) {
				this.areaInfo = areaInfo;
			}

			public String getAreaName() {
				return areaName;
			}

			public void setAreaName(String areaName) {
				this.areaName = areaName;
			}

			public String getCustomId() {
				return customId;
			}

			public void setCustomId(String customId) {
				this.customId = customId;
			}

			public String getCustomName() {
				return customName;
			}

			public void setCustomName(String customName) {
				this.customName = customName;
			}

			public int getAuditStatus() {
				return auditStatus;
			}

			public void setAuditStatus(int auditStatus) {
				this.auditStatus = auditStatus;
			}

			public String getPostTypeId() {
				return postTypeId;
			}

			public void setPostTypeId(String postTypeId) {
				this.postTypeId = postTypeId;
			}

			public String getPostTypeName() {
				return postTypeName;
			}

			public void setPostTypeName(String postTypeName) {
				this.postTypeName = postTypeName;
			}

			public String getGridId() {
				return gridId;
			}

			public void setGridId(String gridId) {
				this.gridId = gridId;
			}

			public String getGridName() {
				return gridName;
			}

			public void setGridName(String gridName) {
				this.gridName = gridName;
			}

			public String getUnionid() {
				return unionid;
			}

			public void setUnionid(String unionid) {
				this.unionid = unionid;
			}
		}
	}
}

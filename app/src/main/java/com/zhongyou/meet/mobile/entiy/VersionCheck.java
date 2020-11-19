package com.zhongyou.meet.mobile.entiy;

/**
 * @author golangdorid@gmail.com
 * @date 2020/3/24 9:23 AM.
 * @
 */
public class VersionCheck {

	/**
	 * errcode : 0
	 * data : {"id":"e7f465dff4ad4be9b4b9edadcedbc0ab","remarks":"\u201c中幼在线\u201d是中幼国际教育最新研发的会议培训系统，支持多方会议、一对多互动培训、一对多直播互动功能。","channelId":"149e22584baa4e37baeef4013c7aae84","name":"中幼在线(Android手机)","pkgname":"com.zhongyou.meet.mobile","major":2,"minor":3,"patch":2,"versionDesc":"2.3.2","importance":1,"changelog":"版本更新","versionCode":33566722,"minVersionCode":33566722,"url":"http://qiniu.zhongyouie.cn/dz/app/Guide-Mobile__release_2.3.2_33566722_20200323.apk","size":"26.41MB","deviceUuid":"","deviceSource":0,"defaultSource":1,"associatedResource":0,"displayOnPc":0,"appType":1,"imageUrl":"https://qiniu.zhongyouie.cn/dz/app/logo/bc1d87b9-695c-4cbc-abbe-82f1e4c3fd20.png","chipModel":"","memory":"","resolutionRatio":"","androidVersion":"","checkTvBalanceSpaceSize":0,"channelGroup":0,"extType":0,"vip":0,"jumpFlag":0,"jumpParamId":"","qrCodeUrl":"","version":"2.3.2"}
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
		 * id : e7f465dff4ad4be9b4b9edadcedbc0ab
		 * remarks : “中幼在线”是中幼国际教育最新研发的会议培训系统，支持多方会议、一对多互动培训、一对多直播互动功能。
		 * channelId : 149e22584baa4e37baeef4013c7aae84
		 * name : 中幼在线(Android手机)
		 * pkgname : com.zhongyou.meet.mobile
		 * major : 2
		 * minor : 3
		 * patch : 2
		 * versionDesc : 2.3.2
		 * importance : 1
		 * changelog : 版本更新
		 * versionCode : 33566722
		 * minVersionCode : 33566722
		 * url : http://qiniu.zhongyouie.cn/dz/app/Guide-Mobile__release_2.3.2_33566722_20200323.apk
		 * size : 26.41MB
		 * deviceUuid :
		 * deviceSource : 0
		 * defaultSource : 1
		 * associatedResource : 0
		 * displayOnPc : 0
		 * appType : 1
		 * imageUrl : https://qiniu.zhongyouie.cn/dz/app/logo/bc1d87b9-695c-4cbc-abbe-82f1e4c3fd20.png
		 * chipModel :
		 * memory :
		 * resolutionRatio :
		 * androidVersion :
		 * checkTvBalanceSpaceSize : 0
		 * channelGroup : 0
		 * extType : 0
		 * vip : 0
		 * jumpFlag : 0
		 * jumpParamId :
		 * qrCodeUrl :
		 * version : 2.3.2
		 */

		private String id;
		private String remarks;
		private String channelId;
		private String name;
		private String pkgname;
		private int major;
		private int minor;
		private int patch;
		private String versionDesc;
		private int importance;
		private String changelog;
		private int versionCode;
		private int minVersionCode;
		private String url;
		private String size;
		private String deviceUuid;
		private int deviceSource;
		private int defaultSource;
		private int associatedResource;
		private int displayOnPc;
		private int appType;
		private String imageUrl;
		private String chipModel;
		private String memory;
		private String resolutionRatio;
		private String androidVersion;
		private int checkTvBalanceSpaceSize;
		private int channelGroup;
		private int extType;
		private int vip;
		private int jumpFlag;
		private String jumpParamId;
		private String qrCodeUrl;
		private String version;

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

		public String getChannelId() {
			return channelId;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPkgname() {
			return pkgname;
		}

		public void setPkgname(String pkgname) {
			this.pkgname = pkgname;
		}

		public int getMajor() {
			return major;
		}

		public void setMajor(int major) {
			this.major = major;
		}

		public int getMinor() {
			return minor;
		}

		public void setMinor(int minor) {
			this.minor = minor;
		}

		public int getPatch() {
			return patch;
		}

		public void setPatch(int patch) {
			this.patch = patch;
		}

		public String getVersionDesc() {
			return versionDesc;
		}

		public void setVersionDesc(String versionDesc) {
			this.versionDesc = versionDesc;
		}

		public int getImportance() {
			return importance;
		}

		public void setImportance(int importance) {
			this.importance = importance;
		}

		public String getChangelog() {
			return changelog;
		}

		public void setChangelog(String changelog) {
			this.changelog = changelog;
		}

		public int getVersionCode() {
			return versionCode;
		}

		public void setVersionCode(int versionCode) {
			this.versionCode = versionCode;
		}

		public int getMinVersionCode() {
			return minVersionCode;
		}

		public void setMinVersionCode(int minVersionCode) {
			this.minVersionCode = minVersionCode;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}

		public String getDeviceUuid() {
			return deviceUuid;
		}

		public void setDeviceUuid(String deviceUuid) {
			this.deviceUuid = deviceUuid;
		}

		public int getDeviceSource() {
			return deviceSource;
		}

		public void setDeviceSource(int deviceSource) {
			this.deviceSource = deviceSource;
		}

		public int getDefaultSource() {
			return defaultSource;
		}

		public void setDefaultSource(int defaultSource) {
			this.defaultSource = defaultSource;
		}

		public int getAssociatedResource() {
			return associatedResource;
		}

		public void setAssociatedResource(int associatedResource) {
			this.associatedResource = associatedResource;
		}

		public int getDisplayOnPc() {
			return displayOnPc;
		}

		public void setDisplayOnPc(int displayOnPc) {
			this.displayOnPc = displayOnPc;
		}

		public int getAppType() {
			return appType;
		}

		public void setAppType(int appType) {
			this.appType = appType;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getChipModel() {
			return chipModel;
		}

		public void setChipModel(String chipModel) {
			this.chipModel = chipModel;
		}

		public String getMemory() {
			return memory;
		}

		public void setMemory(String memory) {
			this.memory = memory;
		}

		public String getResolutionRatio() {
			return resolutionRatio;
		}

		public void setResolutionRatio(String resolutionRatio) {
			this.resolutionRatio = resolutionRatio;
		}

		public String getAndroidVersion() {
			return androidVersion;
		}

		public void setAndroidVersion(String androidVersion) {
			this.androidVersion = androidVersion;
		}

		public int getCheckTvBalanceSpaceSize() {
			return checkTvBalanceSpaceSize;
		}

		public void setCheckTvBalanceSpaceSize(int checkTvBalanceSpaceSize) {
			this.checkTvBalanceSpaceSize = checkTvBalanceSpaceSize;
		}

		public int getChannelGroup() {
			return channelGroup;
		}

		public void setChannelGroup(int channelGroup) {
			this.channelGroup = channelGroup;
		}

		public int getExtType() {
			return extType;
		}

		public void setExtType(int extType) {
			this.extType = extType;
		}

		public int getVip() {
			return vip;
		}

		public void setVip(int vip) {
			this.vip = vip;
		}

		public int getJumpFlag() {
			return jumpFlag;
		}

		public void setJumpFlag(int jumpFlag) {
			this.jumpFlag = jumpFlag;
		}

		public String getJumpParamId() {
			return jumpParamId;
		}

		public void setJumpParamId(String jumpParamId) {
			this.jumpParamId = jumpParamId;
		}

		public String getQrCodeUrl() {
			return qrCodeUrl;
		}

		public void setQrCodeUrl(String qrCodeUrl) {
			this.qrCodeUrl = qrCodeUrl;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}
}

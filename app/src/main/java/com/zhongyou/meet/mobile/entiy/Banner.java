package com.zhongyou.meet.mobile.entiy;

import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2020/3/27 4:34 PM.
 * @
 */
public class Banner {
	/**
	 * errcode : 0
	 * data : [{"pictureUrl":"","isRecord":"0","id":"25b87239ca554a99b4cb688dc8da56fb"},{"pictureUrl":"","isRecord":"0","id":"c653ff88e404492483be39e59cd19938"},{"pictureUrl":"http://syimage.zhongyouie.com/osg/meeting/materials/ebf0fc73bfaf45eb91e87e1d73b730fe.png","isRecord":"0","id":"c6f959474fe143599e3690efdbb6297f"},{"pictureUrl":"http://syimage.zhongyouie.com/osg/meeting/materials/a265d601eb9242e9804d9350d75668ec.png","isRecord":"0","id":"e9dbc87720704d1f9e8995ef97b791b9"},{"pictureUrl":"http://syimage.zhongyouie.com/osg/meeting/materials/6b356d5a02b4438696ec03d801c2cbda.png","isRecord":"0","id":"29e099d0eb2341a7a589bb2d5e314c59"},{"pictureUrl":"http://syimage.zhongyouie.com/osg/meeting/materials/081da5104af04429abde5f113ebfcaa1.png","isRecord":"0","id":"e61a4ab58d1849a9a1fc624842ef05e1"},{"pictureUrl":"","isRecord":"0","id":"8048b7116ac84b039b8791c0a7017e1b"},{"pictureUrl":"","isRecord":"0","id":"a31b4880b1dc49f7a10956d9f91cdb04"},{"pictureUrl":"Fl2XEbA02j4h0FrEcIjbMQ8YmobX","isRecord":"0","id":"983534d7e54449b2a065f0adae8fa26f"},{"pictureUrl":"Fl2XEbA02j4h0FrEcIjbMQ8YmobX","isRecord":"0","id":"ec878b80e3734a2aa95b7fda76518eca"},{"pictureUrl":"","isRecord":"0","id":"09f35f0579054130bee15e79d803cea6"},{"pictureUrl":"","isRecord":"0","id":"4c65009c08be4e42a972c3b04c14ae9f"},{"pictureUrl":"","isRecord":"0","id":"5de2fbcf5627438e9dc27501cf30de04"},{"pictureUrl":"","isRecord":"0","id":"2163b40b4022414e94fb86e3ad7a346a"},{"pictureUrl":"","isRecord":"0","id":"59739a22777648febc9dabf8757b53e6"}]
	 * errmsg : 处理成功
	 */

	private int errcode;
	private String errmsg;
	private List<DataBean> data;

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * pictureUrl :
		 * isRecord : 0
		 * id : 25b87239ca554a99b4cb688dc8da56fb
		 */

		private String pictureUrl;
		private String isRecord;
		private String id;

		public String getPictureUrl() {
			return pictureUrl;
		}

		public void setPictureUrl(String pictureUrl) {
			this.pictureUrl = pictureUrl;
		}

		public String getIsRecord() {
			return isRecord;
		}

		public void setIsRecord(String isRecord) {
			this.isRecord = isRecord;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
}

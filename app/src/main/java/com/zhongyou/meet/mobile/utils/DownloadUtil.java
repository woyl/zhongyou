package com.zhongyou.meet.mobile.utils;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * @author golangdorid@gmail.com
 * @date 2020/5/9 4:33 PM.
 * @
 */
public class DownloadUtil {
	private static DownloadUtil downloadUtil;
	private final OkHttpClient okHttpClient;
	private Call mCall;
	private int mProgress;
	private String destFileDir, destFileName;

	public static DownloadUtil get() {
		if (downloadUtil == null) {
			downloadUtil = new DownloadUtil();
		}
		return downloadUtil;
	}

	private DownloadUtil() {
		okHttpClient = new OkHttpClient();
	}

	public void cancleDownLoad() {
		Timber.e("cancleDownLoad---->%s", "cancleDownLoad");
		if (mCall != null) {
			mCall.cancel();
			if (mProgress < 100) {
				File file = new File(destFileDir, destFileName);
				if (file.exists()) {
					boolean delete = file.delete();
					Timber.e("文件删除成功---->%s", delete);
				}
			}
		}
	}

	/**
	 * @param url          下载连接
	 * @param destFileDir  下载的文件储存目录
	 * @param destFileName 下载文件名称
	 * @param listener     下载监听
	 */
	public void download(Activity activity, final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {
		Request request = new Request.Builder().url(url).build();
		this.destFileDir = destFileDir;
		this.destFileName = destFileName;
		mCall = okHttpClient.newCall(request);

		File nomedia = new File(destFileDir + "/.nomedia");
		if (!nomedia.exists()) {
			try {
				boolean noMediaFile = nomedia.createNewFile();
				Timber.e("创建noMediaFile成功---->%s", noMediaFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mCall.enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				// 下载失败监听回调
				if (activity != null) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							deleteFile(destFileDir, destFileName);
							listener.onDownloadFailed(e);
						}
					});
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

				InputStream is = null;
				byte[] buf = new byte[2048];
				int len = 0;
				FileOutputStream fos = null;
				// 储存下载文件的目录
				File dir = new File(destFileDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(dir, destFileName);
				try {
					is = response.body().byteStream();
					long total = response.body().contentLength();
					fos = new FileOutputStream(file);
					long sum = 0;
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
						sum += len;
						mProgress = (int) (sum * 1.0f / total * 100);
						// 下载中更新进度条
						if (activity != null) {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									listener.onDownloading(mProgress);
								}
							});
						}


					}
					fos.flush();
					// 下载完成
					if (activity != null) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								listener.onDownloadSuccess(file);
							}
						});
					}

				} catch (Exception e) {
					if (activity != null) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								deleteFile(destFileDir, destFileName);
								listener.onDownloadFailed(e);
							}
						});
					}

				} finally {
					try {
						if (is != null)
							is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (fos != null)
							fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void deleteFile(String destFileDir, String destFileName) {
		File file = new File(destFileDir, destFileName);
		if (file.exists()) {
			boolean delete = file.delete();
			Timber.e("文件删除成功deleteFile---->%s", delete);
		}
	}

	public interface OnDownloadListener {
		/**
		 * @param file 下载成功后的文件
		 */
		void onDownloadSuccess(File file);

		/**
		 * @param progress 下载进度
		 */
		void onDownloading(int progress);

		/**
		 * @param e 下载异常信息
		 */
		void onDownloadFailed(Exception e);
	}

}

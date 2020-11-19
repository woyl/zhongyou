package com.zhongyou.meet.mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.Target;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.maning.mndialoglibrary.MProgressDialog;
import com.ycbjie.ycupdatelib.UpdateUtils;

import java.io.File;


/**
 * @author golangdorid@gmail.com
 * @date 2020/5/11 9:22 AM.
 * @
 */
public class DownLoadUtils {

	public static String fileName;

	private FileListener mFileListener;

	public void setFileListener(FileListener fileListener) {
		this.mFileListener = fileListener;
	}

	public interface FileListener {
		void isExists(boolean isExists, File file);

		void makeDirsSuccess(boolean makeDirsSuccess);

		void onCompressFileSuccess(File file);

		void onCompressFileError(Throwable e);

		void onDownLoading(int progress);

		void onDownloadFailed(Exception e);

		default void onDownLoadSuccess(File file) {

		}
	}


	public static void downloadFile(Activity activity, String resourceUrl, String fileName, CircleProgress circleProgress, FileListener fileListener) {
		File file = new File(getPath(activity), "compress/" + fileName);
		if (file.exists()) {
			if (activity.isFinishing()) {
				return;
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				if (activity.isDestroyed()) {
					return;
				}
			}
			fileListener.isExists(true, file);
			return;
		}
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showLoaddingDialog(activity);
			}
		});
		DownloadUtil.get().download(activity,resourceUrl, getPath(activity), fileName, new DownloadUtil.OnDownloadListener() {
			@Override
			public void onDownloadSuccess(File file) {

				fileListener.onDownLoadSuccess(file);

				/*try {
					File fs = new File(getPath(activity), "compress/");
					if (!fs.exists()) {
						boolean mkdirs = fs.mkdirs();
						if (!mkdirs) {
							if (activity.isFinishing()) {
								return;
							}
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
								if (activity.isDestroyed()) {
									return;
								}
							}
							fileListener.makeDirsSuccess(false);
							return;
						}
					}
					if (activity == null) {
						return;
					}

					Luban.with(activity)
							.load(file)
							.ignoreBy(100)
							.setTargetDir(fs.getPath())
							.setRenameListener(new OnRenameListener() {
								@Override
								public String rename(String filePath) {
									return fileName;
								}
							}).filter(new CompressionPredicate() {
						@Override
						public boolean apply(String path) {
							return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
						}
					}).setCompressListener(new OnCompressListener() {
						@Override
						public void onStart() {

						}

						@Override
						public void onSuccess(File file) {
							dismissDialog(activity);
							if (activity.isFinishing()) {
								return;
							}
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
								if (activity.isDestroyed()) {
									return;
								}
							}
							fileListener.onCompressFileSuccess(file);
						}

						@Override
						public void onError(Throwable e) {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									dismissDialog(activity);
								}
							});

							if (activity.isFinishing()) {
								return;
							}
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
								if (activity.isDestroyed()) {
									return;
								}
							}
							fileListener.onCompressFileError(e);
						}
					}).launch();
				} catch (Exception e) {
					e.printStackTrace();
				}*/
			}

			@Override
			public void onDownloading(int progress) {

				if (activity.isFinishing()) {
					return;
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					if (activity.isDestroyed()) {
						return;
					}
				}
				if (circleProgress != null) {
					fileListener.onDownLoading(progress);
				}

			}

			@Override
			public void onDownloadFailed(Exception e) {
				if (activity.isFinishing()) {
					return;
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					if (activity.isDestroyed()) {
						return;
					}
				}
				if (activity == null) {
					return;
				}
				dismissDialog(activity);
				fileListener.onDownloadFailed(e);
			}
		});
	}

	private static void showLoaddingDialog(Activity activity) {
		MProgressDialog.showProgress(activity, "加载中...");
	}

	private static void dismissDialog(Activity activity) {
		MProgressDialog.dismissProgress();
	}

	private static String getPath(Context context) {
		String path = UpdateUtils.getFilePath(context) + "Luban/image/";
		File file = new File(path);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return path;
			}
		}
		return path;
	}


}

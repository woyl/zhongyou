package com.zhongyou.meet.mobile.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.maning.mndialoglibrary.MToast;
import com.ycbjie.ycupdatelib.UpdateUtils;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;

import timber.log.Timber;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/20 4:55 PM.
 * @
 */
public class FileUtils {

	public static void addBitmapToAlbum(Context context, Bitmap bitmap, String displayName, String mimeType, Bitmap.CompressFormat compressFormat) {
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
		values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
		} else {
			values.put(MediaStore.MediaColumns.DATA, Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_DCIM + File.separator + displayName);
		}

		Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		if (uri == null) {
			MToast.makeTextShort(context, "图片已存在 请到相册中查看");
			return;
		}

		if (uri != null) {
			try {
				OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
				if (outputStream != null) {
					boolean compress = bitmap.compress(compressFormat, 100, outputStream);
					if (compress) {
						MToast.makeTextShort(context, "保存成功 请到相册中查看");
					} else {
						MToast.makeTextShort(context, "保存失败");
					}
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				MToast.makeTextShort(context, "保存失败");
			}
		} else {
			Timber.e("图片可能已经存在 uri==null");
		}
	}


	public static String getFileNameByUri(Context context, Uri uri) {
		String fileName = "";
		if (context.getContentResolver() != null) {
			Cursor query = context.getContentResolver().query(uri, null, null, null, null);
			if (query != null && query.getCount() > 0) {
				query.moveToFirst();
				fileName = query.getString(query.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
				query.close();
			}
			return fileName;
		}
		return fileName;
	}


	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// 如果下面还有文件
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}


	public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				if (file.isDirectory()) {// 如果下面还有文件
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFolderFile(files[i].getAbsolutePath(), true);
					}
				}
				if (deleteThisPath) {
					if (!file.isDirectory()) {// 如果是文件，删除
						file.delete();
					} else {// 目录
						if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
							file.delete();
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getPath(Context context) {
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

package com.zhongyou.meet.mobile.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import timber.log.Timber;

/**
 * @author golangdorid@gmail.com
 * @date 2020/5/7 1:29 PM.
 * @
 */
public class TBSFileView extends FrameLayout implements TbsReaderView.ReaderCallback {

	private TbsReaderView mTbsReaderView;

	private String TAG = "TBSFileView";

	public TBSFileView(@NonNull Context context) {
		super(context);
		mTbsReaderView = new TbsReaderView(context, this);
	}

	public TBSFileView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		mTbsReaderView = new TbsReaderView(context, this);
		this.addView(mTbsReaderView, new FrameLayout.LayoutParams(-1, -1));
	}

	public TBSFileView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mTbsReaderView = new TbsReaderView(context, this);
		this.addView(mTbsReaderView, new FrameLayout.LayoutParams(-1, -1));
	}


	public void displayFile(File mFile) {
		if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
			//增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
			String bsReaderTemp = "/storage/emulated/0/TbsReaderTemp";
			File bsReaderTempFile = new File(bsReaderTemp);

			if (!bsReaderTempFile.exists()) {
				Logger.d( "准备创建/storage/emulated/0/TbsReaderTemp！！");
				boolean mkdir = bsReaderTempFile.mkdir();
				if (!mkdir) {
					Logger.d( "创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
				}
			}

			//加载文件
			Bundle localBundle = new Bundle();
			localBundle.putString("filePath", mFile.toString());
			localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");
			boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
			if (bool) {
				this.mTbsReaderView.openFile(localBundle);
			}
		} else {
			Timber.e("文件路径无效---->");

		}

	}

	/***
	 * 获取文件类型
	 *
	 * @param paramString
	 * @return
	 */
	private String getFileType(String paramString) {
		String str = "";

		if (TextUtils.isEmpty(paramString)) {
			Logger.d("paramString---->null");
			return str;
		}
		Logger.d("paramString:" + paramString);
		int i = paramString.lastIndexOf('.');
		if (i <= -1) {
			Logger.d( "i <= -1");
			return str;
		}
		str = paramString.substring(i + 1);
		Logger.d("paramString.substring(i + 1)------>" + str);
		return str;
	}

	@Override
	public void onCallBackAction(Integer integer, Object o, Object o1) {

	}

	public void onStopDisplay() {
		if (mTbsReaderView != null) {
			mTbsReaderView.onStop();
		}
	}

}

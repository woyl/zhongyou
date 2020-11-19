package com.zhongyou.meet.mobile.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.ycbjie.ycupdatelib.UpdateUtils;
import com.zhihu.matisse.engine.ImageEngine;
import com.zhongyou.meet.mobile.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/4 2:49 PM.
 * @
 */
public class MatisseGlideEngine implements ImageEngine {

	@Override
	public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
		/*RequestOptions options = new RequestOptions()
				.centerCrop()
				.placeholder(placeholder)//这里可自己添加占位图
				.override(resize, resize);*/
		List<Uri> list = new ArrayList<>();
		list.add(uri);
		withRx(context, list, imageView);


/*		Glide.with(context)
				.asBitmap()  // some .jpeg files are actually gif
				.load(uri)
				.apply(options)
				.into(imageView);*/
	}

	@Override
	public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
		/*RequestOptions options = new RequestOptions()
				.centerCrop()
				.placeholder(placeholder)//这里可自己添加占位图
				.override(resize, resize);
		Glide.with(context)
				.asGif()  // some .jpeg files are actually gif
				.load(uri)
				.apply(options)
				.into(imageView);*/

		List<Uri> list = new ArrayList<>();
		list.add(uri);
		withRx(context, list, imageView);
	}


	@Override
	public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
		/*RequestOptions options = new RequestOptions()
				.centerCrop()
				.override(resizeX, resizeY)
				.priority(Priority.LOW);*/
		List<Uri> list = new ArrayList<>();
		list.add(uri);
		compressImageView(context, list, imageView, resizeX, resizeY);

	}

	@Override
	public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
		/*RequestOptions options = new RequestOptions()
				.centerCrop()

				.override(resizeX, resizeY);
		Glide.with(context)
				.asBitmap() // some .jpeg files are actually gif
				.load(uri)
				.apply(options)
				.into(imageView);*/
		List<Uri> list = new ArrayList<>();
		list.add(uri);
		compressImageView(context, list, imageView, resizeX, resizeY);
	}

	@Override
	public boolean supportAnimatedGif() {
		return false;
	}

	private String getPath(Context context) {
		String path = UpdateUtils.getFilePath(context) + "/Luban/image/";
		File file = new File(path);
		if (file.mkdirs()) {
			return path;
		}
		return path;
	}

	//缩略图的时候使用
	private <T> void withRx(Context context, final List<T> photos, ImageView imageView) {
		for (T photo : photos) {
			Timber.e("photo---->%s", ((Uri) photo).getPath());
		}
		Luban.with(context)
				.load(photos)
				.ignoreBy(100)
				.setTargetDir(getPath(context))
				.filter(new CompressionPredicate() {
					@Override
					public boolean apply(String path) {
						return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".mp4"));
					}
				})
				.setCompressListener(new OnCompressListener() {
					@Override
					public void onStart() {
						// TODO 压缩开始前调用，可以在方法内启动 loading UI
					}

					@Override
					public void onSuccess(File file) {
						// TODO 压缩成功后调用，返回压缩后的图片文件
						Glide.with(context)
								.asBitmap()
								.priority(Priority.LOW)
								.centerCrop()
								.placeholder(R.drawable.icon_meeting)
								.error(R.drawable.ico_tx)// some .jpeg files are actually gif
								.load("content://media" + file.getAbsolutePath())
								.into(imageView);
					}

					@Override
					public void onError(Throwable e) {
						// TODO 当压缩过程出现问题时调用
					}
				}).launch();
	}

	//展示大图的时候使用
	private <T> void compressImageView(Context context, final List<T> photos, ImageView imageView, int resizeX, int resizeY) {
		Luban.with(context)
				.load(photos)
				.ignoreBy(100)
				.setTargetDir(getPath(context))
				.filter(new CompressionPredicate() {
					@Override
					public boolean apply(String path) {
						return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".mp4"));
					}
				})
				.setCompressListener(new OnCompressListener() {
					@Override
					public void onStart() {
						// TODO 压缩开始前调用，可以在方法内启动 loading UI
					}

					@Override
					public void onSuccess(File file) {
						// TODO 压缩成功后调用，返回压缩后的图片文件

						RequestOptions options = new RequestOptions()
								.centerCrop()
								.placeholder(R.drawable.icon_meeting)
								.error(R.drawable.load_error)
								.override(resizeX, resizeY)
								.priority(Priority.LOW);

						Glide.with(context)
								.asBitmap()
								.apply(options)
								.load("content://media" + file.getAbsolutePath())
								.into(imageView);
					}

					@Override
					public void onError(Throwable e) {
						// TODO 当压缩过程出现问题时调用
					}
				}).launch();
	}
}

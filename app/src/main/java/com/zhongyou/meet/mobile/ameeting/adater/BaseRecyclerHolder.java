package com.zhongyou.meet.mobile.ameeting.adater;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.zhongyou.meet.mobile.BuildConfig;

import java.io.File;
import java.util.Objects;


/**
 * 万能的RecyclerView的ViewHolder
 * Created by 南尘 on 16-7-30.
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

	private SparseArray<View> views;
	private Context context;

	private BaseRecyclerHolder(Context context, View itemView) {
		super(itemView);
		this.context = context;
		//指定一个初始为8
		views = new SparseArray<>(8);
	}

	/**
	 * 取得一个RecyclerHolder对象
	 *
	 * @param context  上下文
	 * @param itemView 子项
	 * @return 返回一个RecyclerHolder对象
	 */
	public static BaseRecyclerHolder getRecyclerHolder(Context context, View itemView) {
		return new BaseRecyclerHolder(context, itemView);
	}

	public SparseArray<View> getViews() {
		return this.views;
	}

	/**
	 * 通过view的id获取对应的控件，如果没有则加入views中
	 *
	 * @param viewId 控件的id
	 * @return 返回一个控件
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = itemView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 设置字符串
	 */
	public BaseRecyclerHolder setText(int viewId, String text) {
		TextView tv = getView(viewId);
		if (tv == null) {
			if (BuildConfig.DEBUG) {
				throw new IllegalArgumentException("viewId == null");
			} else {
				return this;
			}

		}
		tv.setText(text);
		return this;
	}

	/**
	 * 设置图片
	 */
	public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
		ImageView iv = getView(viewId);
		if (iv == null) {
			if (BuildConfig.DEBUG) {
				throw new IllegalArgumentException("viewId == null");
			} else {
				return this;
			}

		}
		iv.setImageResource(drawableId);
		return this;
	}


	/**
	 * 设置图片
	 */
	public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap) {
		ImageView iv = getView(viewId);
		if (iv == null) {
			if (BuildConfig.DEBUG) {
				throw new IllegalArgumentException("viewId == null");
			} else {
				return this;
			}

		}
		iv.setImageBitmap(bitmap);
		return this;
	}

	/**
	 * 设置图片
	 */
	public BaseRecyclerHolder setImageByUrl(int viewId, String url) {

		Glide.with(context).load(url).into((ImageView) getView(viewId));
		//        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
		//        ImageLoader.getInstance().displayImage(url, (ImageView) getView(viewId));
		return this;
	}

	public BaseRecyclerHolder setImageByUrl(int viewId, String url, int placeHolder, int error) {

		Glide.with(context).load(url).placeholder(placeHolder).error(error).into((ImageView) getView(viewId));
		//        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
		//        ImageLoader.getInstance().displayImage(url, (ImageView) getView(viewId));
		return this;
	}

	public BaseRecyclerHolder setImageByUrlWithCorner(int viewId, String url, int placeHolder, int error, int radius) {
		RoundedCorners roundedCorners = new RoundedCorners(radius);
		RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
		Glide.with(context).load(url).apply(options).placeholder(placeHolder).error(error).into((ImageView) getView(viewId));
		return this;
	}

	public BaseRecyclerHolder setImageByUrlWithCornerAny(int viewId, Object url, int placeHolder, int error, int radius) {
		RoundedCorners roundedCorners = new RoundedCorners(radius);
		RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
		Glide.with(context).load(url).apply(options).placeholder(placeHolder).error(error).into((ImageView) getView(viewId));
		return this;
	}

	/**
	 * 设置图片
	 */
	public BaseRecyclerHolder setImageByUrl(int viewId, File url) {
		Glide.with(context).load(url).into((ImageView) getView(viewId));
		//        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
		//        ImageLoader.getInstance().displayImage(url, (ImageView) getView(viewId));
		return this;
	}
/*
    public void setImageByUrl(String url, final int viewId) {

        final ImageView imageView = getView(viewId);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context.getApplicationContext()).load(url)into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(resource);
            }
        });
    }*/

}

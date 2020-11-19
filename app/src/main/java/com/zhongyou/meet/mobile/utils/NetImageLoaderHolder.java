package com.zhongyou.meet.mobile.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.zhongyou.meet.mobile.R;

/**
 * @author golangdorid@gmail.com
 * @date 2020/3/27 12:05 PM.
 * @
 */
public class NetImageLoaderHolder extends Holder<String> {
	private ImageView ivPost;
	private Context mContext;

	public NetImageLoaderHolder(View itemView, Context context) {
		super(itemView);
		this.mContext = context;
	}

	@Override
	protected void initView(View itemView) {
		ivPost = itemView.findViewById(R.id.ivPost);
	}

	@Override
	public void updateUI(String data) {
//		RequestOptions options = new RequestOptions().transform(new RoundTransform(ArmsUtils.dip2px(mContext,3)));
		Glide.with(mContext).asBitmap()
//				.apply(options)
				.load(data)
				.into(ivPost);
	}
}

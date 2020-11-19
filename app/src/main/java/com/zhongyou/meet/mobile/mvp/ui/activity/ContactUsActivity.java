package com.zhongyou.meet.mobile.mvp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.adorkable.iosdialog.AlertDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.maning.mndialoglibrary.MToast;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.utils.FileUtils;
import com.zhongyou.meet.mobile.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/20/2020 16:26
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public class ContactUsActivity extends BaseActivity {


	@BindView(R.id.weChat)
	ImageView weChat;
	@BindView(R.id.gongZhongHao)
	ImageView gongZhongHao;
	private AlertDialog mSignalDialog;
	@BindView(R.id.phoneNumber)
	TextView phoneNumber;

	@BindView(R.id.wchat_num)
	AppCompatTextView wchat_num;

	@Override
	public void setupActivityComponent(@NonNull AppComponent appComponent) {

	}

	@Override
	public int initView(@Nullable Bundle savedInstanceState) {
		return R.layout.activity_contact_us; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
	}

	@Override
	public void initData(@Nullable Bundle savedInstanceState) {
		setTitle("联系我们");
	}


	@OnClick({R.id.weChat, R.id.gongZhongHao, R.id.phoneNumber,R.id.wchat_num})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.weChat:
				try {
					Glide.with(ContactUsActivity.this)
							.asBitmap()
							.centerCrop()
							.load(R.drawable.xiaozhuli)
							.into(new SimpleTarget<Bitmap>() {
								@Override
								public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
									showDialog(resource, "小助理微信.png");
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
					MToast.makeTextShort(ContactUsActivity.this, "保存失败");
				}
				break;
			case R.id.gongZhongHao:
				try {
					Glide.with(ContactUsActivity.this)
							.asBitmap()
							.centerCrop()
							.load(R.drawable.gongzhonghao)
							.into(new SimpleTarget<Bitmap>() {
								@Override
								public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
									showDialog(resource, "公众号.png");
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
					MToast.makeTextShort(ContactUsActivity.this, "保存失败");
				}
				break;
			case R.id.phoneNumber:
				Activity activity;
				RxPermissions permissions = new RxPermissions(this);
				permissions.request(Manifest.permission.CALL_PHONE).subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean aBoolean) throws Exception {
						if (aBoolean) {
							Intent intent = new Intent(Intent.ACTION_DIAL);
							Uri data = Uri.parse("tel:" + "4008620628");
							intent.setData(data);
							startActivity(intent);
						}
					}
				});
				break;
			case R.id.wchat_num:
				ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				if (clipboardManager == null) {
					ToastUtils.showToast("获取剪切板出错");
					return;
				}
				if (!TextUtils.isEmpty(wchat_num.getText().toString())) {
					ClipData clipData = ClipData.newPlainText("wechat",wchat_num.getText().toString());
					clipboardManager.setPrimaryClip(clipData);
					ToastUtils.showToast("复制成功 请去微信添加好友");
				} else {
					ToastUtils.showToast("复制微信失败");
				}
				break;
		}
	}

	public void showDialog(Bitmap bitmap, String displayName) {
		mSignalDialog = new AlertDialog(this)
				.builder()
				.setTitle("保存到相册？")
				.setNegativeButton("取消", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mSignalDialog.dismiss();
					}
				})
				.setPositiveButton("保存", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						FileUtils.addBitmapToAlbum(ContactUsActivity.this, bitmap, displayName, "image/png", Bitmap.CompressFormat.PNG);
						mSignalDialog.dismiss();
					}
				});
		mSignalDialog.show();
	}

	@Override
	protected void onDestroy() {
		if (mSignalDialog != null) {
			mSignalDialog.dismiss();
		}
		super.onDestroy();

	}
}

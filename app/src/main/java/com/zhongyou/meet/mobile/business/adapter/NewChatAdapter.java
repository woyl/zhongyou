package com.zhongyou.meet.mobile.business.adapter;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.persistence.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2019-10-28 17:42.
 */
public class NewChatAdapter extends BaseMultiItemQuickAdapter<ChatMesData.PageDataEntity, BaseViewHolder> {

	public NewChatAdapter(List<ChatMesData.PageDataEntity> data) {
		super(data);
		addItemType(2, R.layout.item_center_big);
		addItemType(1, R.layout.item_right);
		addItemType(0, R.layout.item_left);

	}

	@SuppressLint("HandlerLeak")
	private android.os.Handler handler = new android.os.Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			((View) msg.obj).setVisibility(View.GONE);

		}
	};


	@Override
	protected void convert(BaseViewHolder helper, ChatMesData.PageDataEntity item) {

		if (item.getMsgType() == 2) {
			helper.itemView.setVisibility(View.GONE);
			return;
		}
		if (helper.getItemViewType() == 2) {
			Log.e("convertView", "{" + item.getType() + "} -\t- " + item.getContent());
			if (item.getType() == 0) {
				if (item.getUserId().equals(Preferences.getUserId())) {
					helper.setText(R.id.tv_center, "你撤回了一条消息");
					if (System.currentTimeMillis() - item.getReplyTimestamp() < 20000) {
						helper.getView(R.id.tv_edit).setVisibility(View.VISIBLE);
						Message msg = new Message();
						msg.obj = (TextView) helper.getView(R.id.tv_edit);
						handler.sendMessageDelayed(msg, 20000);
						helper.addOnClickListener(R.id.tv_edit);
					} else {
						((TextView) helper.getView(R.id.tv_edit)).setVisibility(View.GONE);
					}

				} else {
					((TextView) helper.getView(R.id.tv_center)).setText(item.getUserName() + "  撤回了一条消息");
					((TextView) helper.getView(R.id.tv_edit)).setVisibility(View.GONE);
				}
			} else if (item.getType() == 1) {
				((TextView) helper.getView(R.id.tv_center)).setText("你撤回了一条消息  " + item.getReplyTime());
				((TextView) helper.getView(R.id.tv_edit)).setVisibility(View.GONE);
			}

		}
		if (helper.getItemViewType() == 1 || helper.getItemViewType() == 0) {
			if (item.getType() != 1) {
				((TextView) helper.getView(R.id.tv_content)).setVisibility(View.VISIBLE);
				((ImageView) helper.getView(R.id.img_arrow)).setVisibility(View.VISIBLE);
				((ImageView) helper.getView(R.id.img_pic)).setVisibility(View.GONE);
				((TextView) helper.getView(R.id.tv_content)).setText(item.getContent());
			} else {
				helper.getView(R.id.img_pic).setVisibility(View.VISIBLE);
				((TextView) helper.getView(R.id.tv_content)).setVisibility(View.GONE);
				((ImageView) helper.getView(R.id.img_arrow)).setVisibility(View.GONE);
			}
			if (null == item.getUserName()) {
				((TextView) helper.getView(R.id.tv_name)).setText("");
			} else {
				((TextView) helper.getView(R.id.tv_name)).setText(item.getUserName());
			}
			Glide.with(mContext)
					.load(item.getUserLogo())
					.skipMemoryCache(false)
					.error(R.drawable.ico_face)
					.placeholder(R.drawable.ico_face)
					.into((ImageView) helper.getView(R.id.mIvHead));

		}
		if (helper.getItemViewType() == 1) {
			if (item.getLocalState() == 0) {
				helper.getView(R.id.send_bar).setVisibility(View.GONE);
				helper.getView(R.id.send_sate).setVisibility(View.GONE);
				helper.getView(R.id.send_err).setVisibility(View.GONE);
			} else if (item.getLocalState() == 1) {
				helper.getView(R.id.send_bar).setVisibility(View.VISIBLE);
				helper.getView(R.id.send_sate).setVisibility(View.GONE);
				helper.getView(R.id.send_err).setVisibility(View.GONE);
			} else if (item.getLocalState() == 2) {
				helper.getView(R.id.send_bar).setVisibility(View.GONE);
				helper.getView(R.id.send_sate).setVisibility(View.VISIBLE);
				helper.getView(R.id.send_err).setVisibility(View.VISIBLE);
				helper.addOnClickListener(R.id.send_sate);
			}
		}

		if (helper.getItemViewType() == 0) {
			helper.addOnLongClickListener(R.id.mIvHead);

		}

		if (helper.getItemViewType() == 1 || helper.getItemViewType() == 0) {
			if (item.getType() == 1) {
				helper.getView(R.id.img_pic).setVisibility(View.VISIBLE);

				Glide.with(mContext).load(item.getContent())
						.skipMemoryCache(false)
						.centerCrop()
						.error(R.drawable.load_error)
						.placeholder(R.drawable.loading)
						.into((ImageView) helper.getView(R.id.img_pic));

				((TextView) helper.getView(R.id.tv_content)).setVisibility(View.GONE);
				((ImageView) helper.getView(R.id.img_arrow)).setVisibility(View.GONE);
				((ImageView) helper.getView(R.id.img_pic)).setVisibility(View.VISIBLE);


				helper.getView(R.id.img_pic).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ImageView view = helper.getView(R.id.img_pic);
						if (mOnItemClickListener != null) {
							// 每次点击 都清空集合 不然会导致重复
							mImagePathLists.clear();
							mImageViewList.clear();
							Logger.e(item.getContent());
							if (item.getContent().startsWith("null") || !item.getContent().startsWith("http")) {
								return;
							}
							//遍历所有的当前adapter的所有数据，如果是图片类型  就添加进入集合
							for (int i = 0; i < getData().size(); i++) {
								if (getData().get(i).getType() == 1 && getData().get(i).getMsgType() != 2) {
									if (!getData().get(i).getContent().startsWith("null")) {
										createImages(getData().get(i).getContent(), view);
										mImagePathLists.add(getData().get(i).getContent());
									}
								}
							}
							Logger.e(mImagePathLists.toString());
							mOnItemClickListener.onItemClick(helper.getPosition(), helper.getView(R.id.img_pic), mImageViewList, mImagePathLists);
						}
					}
				});


			} else {
				helper.getView(R.id.img_pic).setVisibility(View.GONE);
			}

			helper.addOnLongClickListener(R.id.tv_content)
					.addOnLongClickListener(R.id.img_pic);
		}

	}


	public interface onItemClickListener {
		void onItemClick(int position, View view, List<ImageView> imageViewList, List<String> imagePathList);
	}

	public onItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(onItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	private List<ImageView> mImageViewList = new ArrayList<>();
	private List<String> mImagePathLists = new ArrayList<>();

	private void createImages(String conent, ImageView view) {
		Rect bounds = new Rect();
		view.getGlobalVisibleRect(bounds);
		mImageViewList.add(view);
	}


}




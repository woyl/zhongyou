package com.zhongyou.meet.mobile.ameeting.adater;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.SpliteGridLayoutHelper;
import com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper;
import com.alibaba.fastjson.JSON;
import com.lljjcoder.style.citylist.CConfig;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.AudienceVideo;
import com.zhongyou.meet.mobile.utils.DisplayUtil;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.view.MyGridLayoutHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class NewAudienceVideoAdapter extends DelegateAdapter.Adapter<NewAudienceVideoAdapter.AudienceVideoViewHolder> {

	private Context context;
	private ArrayList<AudienceVideo> audienceVideos = new ArrayList<AudienceVideo>();
	private AudienceVideoViewHolder mAudienceVideoViewHolder;
	private int height, width;

	public NewAudienceVideoAdapter(Context context, LayoutHelper layoutHelper) {
		this.context = context;
		this.mLayoutHelper = layoutHelper;
		height = DisplayUtil.dip2px(context, 70);
		width = DisplayUtil.dip2px(context, 114);

	}

	public NewAudienceVideoAdapter(Context context, LayoutHelper layoutHelper, ArrayList<AudienceVideo> audienceVideos) {
		this(context, layoutHelper);
		this.audienceVideos = audienceVideos;
	}

	public void setLayoutHelper(LayoutHelper layoutHelper) {
		this.mLayoutHelper = layoutHelper;
	}

	public synchronized void insertItem(AudienceVideo audienceVideo) {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).getUid() == audienceVideo.getUid()) {
				return;
			}
		}
		if (audienceVideo.isBroadcaster()) {
			this.audienceVideos.add(0, audienceVideo);
			notifyDataSetChanged();
		} else {
			this.audienceVideos.add(audienceVideo);
			notifyDataSetChanged();
		}
	}

	@IntDef({VISIBLE, INVISIBLE, GONE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Visibility {
	}

	public void setVisibility(@Visibility int visibility) {
		for (AudienceVideo audienceVideo : this.audienceVideos) {
			if (visibility == VISIBLE) {
				if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
					audienceVideo.getSurfaceView().setVisibility(VISIBLE);
				}

			} else {
				if (audienceVideo != null && audienceVideo.getSurfaceView() != null) {
					audienceVideo.getSurfaceView().setVisibility(GONE);
				}
			}
		}
	}

	public void changeLists(ArrayList<AudienceVideo> audienceVideos) {
		this.audienceVideos = audienceVideos;
	}

	public synchronized void insertItem(int position, AudienceVideo audienceVideo) {
		if (audienceVideo.isBroadcaster()) {
			this.audienceVideos.add(0, audienceVideo);
			notifyDataSetChanged();
		} else {
			this.audienceVideos.add(position, audienceVideo);
			notifyDataSetChanged();
		}
	}

	public synchronized void insetChairMan(int position, AudienceVideo audienceVideo) {
		this.audienceVideos.add(position, audienceVideo);
		notifyItemInserted(audienceVideos.size());
	}

	/*public void changItemHeight(SizeUtils sizeUtils){
		if (mAudienceVideoViewHolder!=null){
		}
	}*/

	public synchronized int getDataSize() {
		if (audienceVideos != null) {
			return audienceVideos.size();
		}
		return 0;
	}

	public synchronized ArrayList<AudienceVideo> getAudienceVideoLists() {
		return audienceVideos;
	}

	public synchronized void deleteItem(int uid) {
		Iterator<AudienceVideo> iterator = audienceVideos.iterator();
		while (iterator.hasNext()) {
			AudienceVideo audienceVideo = iterator.next();
			if (audienceVideo.getUid() == uid) {

				if (null != audienceVideo.getSurfaceView()) {
					audienceVideo.getSurfaceView().setZOrderMediaOverlay(false);
					audienceVideo.getSurfaceView().setZOrderOnTop(false);
					stripSurfaceView(audienceVideo.getSurfaceView());
				}
				Log.e("deleteItem", "deleteItem: " + iterator.toString());
//                Log.v("delete", "1--" + audienceVideos.size() + "--position--" + position);

				iterator.remove();
//                Log.v("delete", "2--" + audienceVideos.size());
				notifyDataSetChanged();
			}
		}
	}


	public synchronized void deleteItemById(int uid) {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).getUid() == uid) {
				AudienceVideo remove = audienceVideos.remove(i);
				notifyItemRemoved(i);
				break;
			}
		}
	}

	public synchronized boolean isHaveChairMan() {
		try {
			for (int i = 0; i < audienceVideos.size(); i++) {
				if (audienceVideos.get(i).isBroadcaster()) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}


	public synchronized int getPositionById(int uid) {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).getUid() == uid) {
				return i;

			}
		}
		return -1;
	}


	public int getChairManPosition() {
		for (int i = 0; i < audienceVideos.size(); i++) {
			if (audienceVideos.get(i).isBroadcaster()) {
				return i;
			}
		}
		return -1;
	}

	public synchronized void removeItem(int position) {
		if (position >= audienceVideos.size()) {
			return;
		}
		audienceVideos.remove(position);
		notifyItemRemoved(position);

	}
	public synchronized void removeChairman(int position) {
		if (position >= audienceVideos.size()) {
			return;
		}
		audienceVideos.remove(position);
		notifyDataSetChanged();

	}


	public void setVolumeByUid(int uid, int volume) {
		for (AudienceVideo audienceVideo : audienceVideos) {
			if (audienceVideo.getUid() == uid) {
//                Log.v("update_volume", "1--" + audienceVideo.toString());
				audienceVideo.setVolume(volume);
				int position = audienceVideos.indexOf(audienceVideo);
//                Log.v("update_volume", "2--" + audienceVideo.toString() + "--position--" + position);
				notifyItemChanged(position, 1);
			} else {
//                Log.v("update_volume", "不是更新它");
			}
		}
	}

	public void setMutedStatusByUid(int uid, boolean muted) {
		for (AudienceVideo audienceVideo : audienceVideos) {
			if (audienceVideo.getUid() == uid) {
//                Log.v("update_mute", "1--" + audienceVideo.toString());
				audienceVideo.setMuted(muted);
				int position = audienceVideos.indexOf(audienceVideo);
//                Log.v("update_mute", "2--" + audienceVideo.toString() + "--position--" + position);
				notifyItemChanged(position, 1);
			} else {
//                Log.v("update_mute", "不是更新它");
			}
		}
	}

	@Override
	public AudienceVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.item_meeting_audience_video, parent, false);
		mAudienceVideoViewHolder = new AudienceVideoViewHolder(view);
		return mAudienceVideoViewHolder;

	}


	public void setItemSize(int height, int width) {
		this.height = height;
		this.width = width;
//		notifyDataSetChanged();
	}

	public AudienceVideoViewHolder getHolder() {
		return mAudienceVideoViewHolder;
	}




	@Override
	public void onBindViewHolder(@NonNull final AudienceVideoViewHolder holder, final int position) {

		ViewGroup.LayoutParams mLayoutParams = holder.itemView.getLayoutParams();
		mLayoutParams.width = DisplayUtil.dip2px(context, 80);
		mLayoutParams.height = DisplayUtil.dip2px(context, 60);

		/*if (mLayoutHelper instanceof MyGridLayoutHelper) {
			switch (getItemCount()) {
				case 0:
				case 1:
				case 2:
				case 3:
					mLayoutParams.width = DisplayUtil.dip2px(context, 240);
					mLayoutParams.height = getHeight(context) / 3;
					break;
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					mLayoutParams.width = DisplayUtil.dip2px(context, 80);
					mLayoutParams.height = DisplayUtil.dip2px(context, 60);
					break;
			}
		}*/

		if (mLayoutHelper instanceof StaggeredGridLayoutHelper || mLayoutHelper instanceof SpliteGridLayoutHelper) {
			switch (getItemCount()) {
				case 0:
					break;
				case 1:
					mLayoutParams.width = getWidth(context);
					mLayoutParams.height = getHeight(context);
					break;
				case 2:
					mLayoutParams.width = getWidth(context) / 2;
					mLayoutParams.height = getHeight(context);
					break;
				case 3:
				case 4:
					mLayoutParams.width = getWidth(context) / 2;
					mLayoutParams.height = getHeight(context) / 2;
					break;
				case 5:
				case 6:
					mLayoutParams.width = getWidth(context) / 2;
					mLayoutParams.height = getHeight(context) / 3;
					break;
				case 7:
				case 8:
					mLayoutParams.width = getWidth(context) / 4;
					mLayoutParams.height = getHeight(context) / 2;
					break;

			}

		}


		holder.itemView.setLayoutParams(mLayoutParams);


		//Logger.e("width    "+holder.itemView.getLayoutParams().width+"-----height    "+holder.itemView.getLayoutParams().height);
		if (position == 8) {
			ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
			layoutParams.width = 0;
			layoutParams.height = 0;
			holder.itemView.setLayoutParams(layoutParams);
		}

		final AudienceVideo audienceVideo = audienceVideos.get(position);
//		Logger.e(audienceVideo.toString());
		if (audienceVideo.getSurfaceView() == null && !audienceVideo.isBroadcaster()) {
			holder.itemView.setVisibility(VISIBLE);
			holder.nameText.setText(audienceVideo.getName() + "-" + position);
			holder.nameText.setTextColor(context.getResources().getColor(R.color.black));
			holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.red));
		} else if (audienceVideo.isBroadcaster() && audienceVideo.getSurfaceView() == null) {
			holder.itemView.setVisibility(VISIBLE);
			holder.chairManHint.setVisibility(GONE);
			holder.nameText.setText(audienceVideo.getName());
			holder.labelText.setVisibility(VISIBLE);

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onItemClick(recyclerView, v, position);
					}
				}
			});
		} else {
			holder.chairManHint.setVisibility(GONE);
			holder.itemView.setVisibility(VISIBLE);


			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onItemClick(recyclerView, v, position);
					}
				}
			});


			holder.nameText.setText(TextUtils.isEmpty(audienceVideo.getName()) ? (TextUtils.isEmpty(audienceVideo.getUname()) ? "" : audienceVideo.getUname()) : audienceVideo.getName());


			if (audienceVideo.getUid() == MMKV.defaultMMKV().decodeInt(MMKVHelper.KEY_MEETING_UID) && !audienceVideo.isBroadcaster()) {
				holder.nameText.setText(MMKV.defaultMMKV().decodeString(MMKVHelper.USERNICKNAME));
			}


			if (BuildConfig.DEBUG) {
				holder.nameText.setVisibility(VISIBLE);
				if (audienceVideo.isBroadcaster()) {
					holder.nameText.setVisibility(GONE);
				}
			} else {
				holder.nameText.setVisibility(VISIBLE);
				if (audienceVideo.isBroadcaster()) {
					holder.nameText.setVisibility(GONE);
				}
			}

			if (audienceVideo.isBroadcaster()) {
				holder.labelText.setVisibility(VISIBLE);
			} else {
				holder.labelText.setVisibility(GONE);
			}

			if (audienceVideo.isMuted() || audienceVideo.getVolume() <= 100) {
				holder.volumeImage.setVisibility(GONE);
			} else {
				holder.volumeImage.setVisibility(VISIBLE);
			}

			if (audienceVideo.getVideoStatus() == 1) {
				holder.videoStatus.setText("对方网络不好 视频卡顿");
				holder.videoStatus.setVisibility(View.VISIBLE);
			} else if (audienceVideo.getVideoStatus() == 2) {
				holder.videoStatus.setText("对方网络不好 视频播放失败");
				holder.videoStatus.setVisibility(View.VISIBLE);
			} else {
				holder.videoStatus.setVisibility(GONE);
			}

			if (holder.videoLayout.getChildCount() > 0) {
				holder.videoLayout.removeAllViews();
			}
			final SurfaceView surfaceView = audienceVideo.getSurfaceView();
			surfaceView.setZOrderMediaOverlay(true);
			stripSurfaceView(surfaceView);
			holder.videoLayout.addView(surfaceView, new VirtualLayoutManager.LayoutParams(VirtualLayoutManager.LayoutParams.MATCH_PARENT, VirtualLayoutManager.LayoutParams.MATCH_PARENT));
			/*if (mOnDataChangeListener!=null){
				mOnDataChangeListener.onDataChange(audienceVideo.getUid());
			}*/

		}


	}

	@Override
	public void onBindViewHolder(@NonNull AudienceVideoViewHolder holder, int position, @NonNull List<Object> payloads) {
		if (payloads.isEmpty()) {
			onBindViewHolder(holder, position);
		} else {
			final AudienceVideo audienceVideo = audienceVideos.get(position);

			if (audienceVideo.isMuted() || audienceVideo.getVolume() <= 100) {
				holder.volumeImage.setVisibility(GONE);
			} else {
				holder.volumeImage.setVisibility(VISIBLE);
			}

		}
	}

	private void stripSurfaceView(SurfaceView view) {
		if (view == null) {
			return;
		}
		ViewParent parent = view.getParent();
		if (parent != null) {
			((FrameLayout) parent).removeView(view);
		}
	}


	@Override
	public int getItemCount() {
		if (audienceVideos != null) {
			if (audienceVideos.size() > 8) {
				return 8;
			} else {
				return audienceVideos.size();
			}

		}
		return 0;
	}

	private LayoutHelper mLayoutHelper;

	@Override
	public LayoutHelper onCreateLayoutHelper() {
		return mLayoutHelper;
	}

	public class AudienceVideoViewHolder extends RecyclerView.ViewHolder {

		private TextView labelText, nameText, chairManHint, videoStatus;
		private FrameLayout videoLayout;
		private ImageView volumeImage;

		public AudienceVideoViewHolder(View itemView) {
			super(itemView);
			videoLayout = itemView.findViewById(R.id.audience_video_layout);
			labelText = itemView.findViewById(R.id.label);
			chairManHint = itemView.findViewById(R.id.chairManHint);
			nameText = itemView.findViewById(R.id.name);
			volumeImage = itemView.findViewById(R.id.volume_status);
			videoStatus = itemView.findViewById(R.id.videoStatus);

		}
	}


	/**
	 * 定义一个点击事件接口回调
	 */

	public interface onDoubleClickListener {
		void onDoubleClick(View parent, View view, int position);
	}

	public interface OnItemClickListener {
		void onItemClick(RecyclerView parent, View view, int position);
	}

	public interface OnItemLongClickListener {
		boolean onItemLongClick(RecyclerView parent, View view, int position);
	}

	private OnItemClickListener listener;//点击事件监听器
	private OnItemLongClickListener longClickListener;//长按监听器
	private onDoubleClickListener mDoubleClickListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
		this.longClickListener = longClickListener;
	}

	public void setOnDoucleClickListener(onDoubleClickListener onDoucleClickListener) {
		this.mDoubleClickListener = onDoucleClickListener;
	}

	private RecyclerView recyclerView;

	//在RecyclerView提供数据的时候调用
	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		this.recyclerView = recyclerView;
	}

	@Override
	public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
		super.onDetachedFromRecyclerView(recyclerView);
		this.recyclerView = null;
	}

	/**
	 * @return 屏幕宽度 in pixel
	 */
	public static int getWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

	/**
	 * @return 屏幕高度 in pixel
	 */
	public static int getHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}


	public interface onDataChangeListener {
		void onDataChange(int uid);
	}

	public onDataChangeListener mOnDataChangeListener;

	public void setOnDataChangeListener(onDataChangeListener onDataChangeListener) {
		this.mOnDataChangeListener = onDataChangeListener;
	}
}



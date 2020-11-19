package com.zhongyou.meet.mobile.ameeting.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.AudienceVideo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author luopan@centerm.com
 * @date 2019-11-22 17:41.
 */
public class VideoAdapter extends DelegateAdapter.Adapter<RecyclerView.ViewHolder> {


	private Context mContext;
	private List<AudienceVideo> mAudienceVideos;
	private LayoutHelper mLayoutHelper;

	private final int CHAIRMANTYPE = 0x000;
	private final int ATTDENTEETYPE = 0x001;

	public VideoAdapter(Context context, List<AudienceVideo> audienceVideos, LayoutHelper layoutHelper) {
		mContext = context;
		mAudienceVideos = audienceVideos;
		mLayoutHelper = layoutHelper;
	}


	public void insertItem(AudienceVideo video) {
		mAudienceVideos.add( video);
		notifyDataSetChanged();
	}

	public void chageData(List<AudienceVideo> audienceVideos){
		this.mAudienceVideos=audienceVideos;
		notifyDataSetChanged();
	}

	public VideoAdapter(Context context, List<AudienceVideo> audienceVideos) {
		mContext = context;
		mAudienceVideos = audienceVideos;
	}

	@Override
	public LayoutHelper onCreateLayoutHelper() {
		return mLayoutHelper;
	}


	public void setLayoutHelper(LayoutHelper layoutHelper) {
		this.mLayoutHelper = layoutHelper;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		if (i == CHAIRMANTYPE) {
			return new ChairmanViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_chairman, null));
		} else if (i == ATTDENTEETYPE) {
			return new AttdenteeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_attendee, null));
		} else {
			return null;
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
		if (viewHolder instanceof ChairmanViewHolder) {
			setChairManData((ChairmanViewHolder) viewHolder, i);
		} else if (viewHolder instanceof AttdenteeViewHolder) {
			setAttdenteeData((AttdenteeViewHolder) viewHolder, i);
		}
	}

	private void setAttdenteeData(AttdenteeViewHolder holder, int i) {
		holder.name.setText(mAudienceVideos.get(i).getName());

		if (mAudienceVideos.get(i).isBroadcaster()) {
			holder.label.setVisibility(View.VISIBLE);
		} else {
			holder.label.setVisibility(View.GONE);
		}

		if (mAudienceVideos.get(i).isMuted() || mAudienceVideos.get(i).getVolume() <= 100) {
			holder.volumeStatus.setVisibility(View.GONE);
		} else {
			holder.volumeStatus.setVisibility(View.VISIBLE);
		}

		FrameLayout frameLayout = holder.audienceVideoLayout;
		if (frameLayout.getChildCount() > 0) {
			frameLayout.removeAllViews();
		}

		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(500, 300);
		frameLayout.setLayoutParams(layoutParams);
		final SurfaceView surfaceView = mAudienceVideos.get(i).getSurfaceView();
		surfaceView.setLayoutParams(layoutParams);

		stripSurfaceView(surfaceView);
		frameLayout.addView(surfaceView);
	}

	private void setChairManData(ChairmanViewHolder viewHolder, int i) {
		FrameLayout view = viewHolder.chairmanVideoFrame;


		SurfaceView surfaceView = mAudienceVideos.get(i).getSurfaceView();

		stripSurfaceView(surfaceView);
		view.addView(surfaceView);
	}


	private void stripSurfaceView(SurfaceView view) {
		ViewParent parent = view.getParent();
		if (parent != null) {
			((FrameLayout) parent).removeView(view);
		}
	}


	@Override
	public int getItemCount() {
		return mAudienceVideos.size();
	}


	@Override
	public int getItemViewType(int position) {
		if (mAudienceVideos.get(position).isBroadcaster()) {
			return CHAIRMANTYPE;
		} else {
			return ATTDENTEETYPE;
		}
	}

	public class ChairmanViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.chairmanVideoFrame)
		FrameLayout chairmanVideoFrame;

		public ChairmanViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

	}

	public class AttdenteeViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.label)
		TextView label;
		@BindView(R.id.name)
		TextView name;
		@BindView(R.id.volume_status)
		ImageView volumeStatus;
		@BindView(R.id.audience_video_layout)
		FrameLayout audienceVideoLayout;

		public AttdenteeViewHolder(@NonNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}

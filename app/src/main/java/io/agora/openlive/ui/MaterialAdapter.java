package io.agora.openlive.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhongyou.meet.mobile.entities.Material;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.utils.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * Created by whatisjava on 17-10-18.
 */

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.ViewHolder> {

	private Context mContext;
	private ArrayList<Material> materials;
	private OnClickListener onClickListener;
	private int IMG = 0x000;
	private int VIDEO = 0x001;

	public MaterialAdapter(Context context, ArrayList<Material> materials) {
		this.mContext = context;
		this.materials = materials;
	}

	public void addData(ArrayList<Material> materials) {
		this.materials.addAll(materials);
		notifyItemRangeInserted(this.materials.size(), materials.size());
	}

	public void cleanData() {
		this.materials.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		if (materials.size()<=0){
			return IMG;
		}
		if (materials.get(position).getType().equals("1")){
			return VIDEO;
		}else {
			return IMG;
		}
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType==IMG){
			View view = LayoutInflater.from(mContext).inflate(R.layout.item_material, parent, false);
			return new ViewHolder(view);
		}else {
			View view = LayoutInflater.from(mContext).inflate(R.layout.item_material_video, parent, false);
			return new ViewHolder(view);
		}

	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
		Log.e("onBindViewHolder", "onBindViewHolder: "+materials.get(position).toString() );


		Material material = materials.get(position);


		if (material.getType().equals("1")){//视频
			String imageUrl = ImageHelper.videoFrameUrl(material.getMeetingMaterialsPublishList().get(0).getUrl()
					, AutoSizeUtils.dp2px(mContext,180)
					,AutoSizeUtils.dp2px(mContext,200));
			Picasso.with(mContext).load(imageUrl).error(R.drawable.default_img).placeholder(R.drawable.loading).into(viewHolder.imageView);
		}else {
			if (material.getMeetingMaterialsPublishList().size()<=0){
				Picasso.with(mContext).load(R.drawable.item_forum_img_error).into(viewHolder.imageView);
			}else {
				String imageUrl = ImageHelper.getThumb(material.getMeetingMaterialsPublishList().get(0).getUrl());
				Picasso.with(mContext).load(imageUrl).into(viewHolder.imageView);
			}

		}
		viewHolder.nameText.setText(material.getName());
		viewHolder.countText.setText(String.format("%d张", material.getMeetingMaterialsPublishList().size()));
		viewHolder.uploadTimeText.setText(String.format("%s上传", material.getCreateDate()));

		if (onClickListener != null) {
			viewHolder.itemView.setOnClickListener(v -> {
				int layoutPos = viewHolder.getLayoutPosition();
				onClickListener.onPreviewButtonClick(v, material, layoutPos);
			});
		}

	}

	@Override
	public int getItemCount() {
		return materials != null ? materials.size() : 0;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		View itemView;
		ImageView imageView;
		TextView nameText, countText, uploadTimeText;
		FrameLayout coverLayout;

		public ViewHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
			coverLayout = itemView.findViewById(R.id.cover_layout);
			imageView = itemView.findViewById(R.id.image);
			nameText = itemView.findViewById(R.id.name);
			countText = itemView.findViewById(R.id.count);
			uploadTimeText = itemView.findViewById(R.id.upload_time);
		}
	}

	public interface OnClickListener {

		void onPreviewButtonClick(View v, Material material, int position);

	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

}

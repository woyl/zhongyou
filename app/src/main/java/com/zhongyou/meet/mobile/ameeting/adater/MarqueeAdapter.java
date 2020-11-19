package com.zhongyou.meet.mobile.ameeting.adater;

import android.content.Context;
import android.widget.TextView;

import com.xj.marqueeview.base.CommonAdapter;
import com.xj.marqueeview.base.ViewHolder;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entiy.RecomandData;

import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/3 3:34 PM.
 * @
 */
public class MarqueeAdapter extends CommonAdapter<RecomandData.MeetingAdvanceBean> {

	public MarqueeAdapter(Context context, List<RecomandData.MeetingAdvanceBean> datas) {
		super(context, R.layout.item_marquee, datas);
	}

	@Override
	protected void convert(ViewHolder viewHolder, RecomandData.MeetingAdvanceBean item, int position) {
		TextView content = viewHolder.getView(R.id.content);
		content.setSelected(true);
		content.setText(item.getComment().trim());
		viewHolder.getView(R.id.addCollet).setOnClickListener(v -> {
			if (mAddCollectListener != null) {
				mAddCollectListener.onCollectClick(position);
			}
		});
	}

	public interface AddCollectListener {
		void onCollectClick(int position);
	}

	public AddCollectListener mAddCollectListener;

	public void setOnAddCollectListener(AddCollectListener addCollectListener) {
		this.mAddCollectListener = addCollectListener;
	}
}

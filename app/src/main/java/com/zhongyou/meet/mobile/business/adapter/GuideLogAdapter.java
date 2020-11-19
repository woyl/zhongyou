package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.RecordData;
import com.zhongyou.meet.mobile.utils.TimeUtil;
import com.zhongyou.meet.mobile.utils.helper.ImageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wufan on 2017/8/3.
 */

public class GuideLogAdapter extends BaseRecyclerAdapter<RecordData.PageDataEntity> {


    public enum ITEM_TYPE {
        TODAY_TOP,
        TODAY_END,
        TODAY,
        HISTORY_TOP,
        HISTORY_END,
        HISTORY
    }

    public GuideLogAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        RecordData.PageDataEntity bean = mData.get(position);
        String time = bean.getOrderTime();

        if (TimeUtil.isToday(time)) {
            if (position == 0) {
                return ITEM_TYPE.TODAY_TOP.ordinal();
            } else if (position == getItemCount() - 1 || !TimeUtil.isToday(mData.get(position + 1).getOrderTime())) {

                return ITEM_TYPE.TODAY_END.ordinal();
            } else {
                return ITEM_TYPE.TODAY.ordinal();
            }

        } else {
            if (position == 0 || TimeUtil.isToday(mData.get(position - 1).getOrderTime())) {
                //第一个,或者上一个是今天
                return ITEM_TYPE.HISTORY_TOP.ordinal();
            } else if (position == getItemCount() - 1) {
                return ITEM_TYPE.HISTORY_END.ordinal();
            } else {
                return ITEM_TYPE.HISTORY.ordinal();
            }

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == ITEM_TYPE.TODAY_TOP.ordinal()) {
//            return new ViewHolder(mInflater.inflate(R.layout.guide_log_new_item, parent, false));
//        } else if (viewType == ITEM_TYPE.TODAY_END.ordinal()) {
//            return new ViewHolder(mInflater.inflate(R.layout.guide_log_new_item, parent, false));
//        } else if (viewType == ITEM_TYPE.TODAY.ordinal()) {
//            return new ViewHolder(mInflater.inflate(R.layout.guide_log_new_item, parent, false));
//        } else if (viewType == ITEM_TYPE.HISTORY_TOP.ordinal()) {
//            return new ViewHolder(mInflater.inflate(R.layout.guide_log_new_item, parent, false));
//        } else if (viewType == ITEM_TYPE.HISTORY_END.ordinal()) {
//            return new ViewHolder(mInflater.inflate(R.layout.guide_log_new_item, parent, false));
//        } else {
//            return new ViewHolder(mInflater.inflate(R.layout.guide_log_new_item, parent, false));
//        }

        return new ViewHolder(mInflater.inflate(R.layout.guide_log_new_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecordData.PageDataEntity bean = mData.get(position);
        String time = bean.getOrderTime();
        ViewHolder holder = (ViewHolder) viewHolder;

        if (getItemViewType(position) == ITEM_TYPE.HISTORY_TOP.ordinal()) {
            //显示历史记录
            holder.mTvHistory.setVisibility(View.VISIBLE);
        } else {
            holder.mTvHistory.setVisibility(View.GONE);
        }

        ImageHelper.loadImageDpId(bean.getPhoto(), R.dimen.my_px_120, R.dimen.my_px_120, holder.mIvHead);
        holder.mTvName.setText(bean.getAddress() + " " + bean.getName());


        if (TimeUtil.isToday(time)) {
            holder.mTvTime.setText("今天" + " " + TimeUtil.getHM(time));
        } else {
            holder.mTvTime.setText(TimeUtil.getMonthDayHM(time));
        }

        if (bean.getRatingStar() == 0) {
            holder.mTvNoReview.setVisibility(View.VISIBLE);
            holder.mLayoutStar.setVisibility(View.GONE);
        } else {
            holder.mTvNoReview.setVisibility(View.GONE);
            holder.mLayoutStar.setVisibility(View.VISIBLE);
            if (bean.getRatingStar() > 1) {
                holder.mIvStar2.setImageResource(R.mipmap.ic_star_good_record);
            } else {
                holder.mIvStar2.setImageResource(R.mipmap.ic_star_ungood_record);
            }
            if (bean.getRatingStar() > 2) {
                holder.mIvStar3.setImageResource(R.mipmap.ic_star_good_record);
            } else {
                holder.mIvStar3.setImageResource(R.mipmap.ic_star_ungood_record);
            }
            if (bean.getRatingStar() > 3) {
                holder.mIvStar4.setImageResource(R.mipmap.ic_star_good_record);
            } else {
                holder.mIvStar4.setImageResource(R.mipmap.ic_star_ungood_record);
            }
            if (bean.getRatingStar() > 4) {
                holder.mIvStar5.setImageResource(R.mipmap.ic_star_good_record);
            } else {
                holder.mIvStar5.setImageResource(R.mipmap.ic_star_ungood_record);
            }
        }

        //status (integer, optional): 状态， 0：未应答（30s未接听），1：接听，2：拒绝, 8：对方挂断 9：通话结束挂断 ,
        if (bean.getStatus() == 0 || bean.getStatus() == 8 || bean.getStatus() == 2) {
            if (bean.getStatus() == 0) {
                //0：未应答（30s未接听）
                holder.mTvCallDuration.setText("未接听");
            } else if (bean.getStatus() == 8) {
                // 8：对方挂断
                holder.mTvCallDuration.setText("未接通");
            } else if (bean.getStatus() == 2) {
                //2：拒绝
                holder.mTvCallDuration.setText("已拒绝");
            }
            holder.mTvCallDuration.setTextColor(mContext.getResources().getColor(R.color.text_red_ff6482));
            holder.mTvCallDuration.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_uncall, 0, 0, 0);
            holder.mTvCallDuration.getPaint().setFakeBoldText(false);
            holder.mTvNoReview.setVisibility(View.GONE);
        } else {
            //1：接听 9：通话结束挂断 ,
            holder.mTvCallDuration.setText(" " + String.format("%02d", bean.getMinuteInterval()) + ":" + String.format("%02d", bean.getSecondInterval()));
            holder.mTvCallDuration.setTextColor(mContext.getResources().getColor(R.color.text_black_434343));
            holder.mTvCallDuration.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_call, 0, 0, 0);
            holder.mTvCallDuration.getPaint().setFakeBoldText(true);
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mTvHistory)
        TextView mTvHistory;
        @BindView(R.id.mIvHead)
        CircleImageView mIvHead;
        @BindView(R.id.mTvName)
        TextView mTvName;
        @BindView(R.id.mTvTime)
        TextView mTvTime;
        @BindView(R.id.mTvNoReview)
        TextView mTvNoReview;
        @BindView(R.id.mIvStar1)
        ImageView mIvStar1;
        @BindView(R.id.mIvStar2)
        ImageView mIvStar2;
        @BindView(R.id.mIvStar3)
        ImageView mIvStar3;
        @BindView(R.id.mIvStar4)
        ImageView mIvStar4;
        @BindView(R.id.mIvStar5)
        ImageView mIvStar5;
        @BindView(R.id.mLayoutStar)
        LinearLayout mLayoutStar;
        @BindView(R.id.mTvCallDuration)
        TextView mTvCallDuration;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }


}

package com.zhongyou.meet.mobile.business.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.UserInfoActivity;
import com.zhongyou.meet.mobile.business.ReplyReviewActivity;
import com.zhongyou.meet.mobile.business.SettingActivity;
import com.zhongyou.meet.mobile.entities.RankInfo;
import com.zhongyou.meet.mobile.entities.RecordData;
import com.zhongyou.meet.mobile.event.ReplyReviewEvent;
import com.zhongyou.meet.mobile.event.UserUpdateEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.ImageUtils;
import com.zhongyou.meet.mobile.utils.Logger;
import com.zhongyou.meet.mobile.utils.StringUtils;
import com.zhongyou.meet.mobile.utils.TimeUtil;
import com.zhongyou.meet.mobile.utils.helper.ImageHelper;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by wufan on 2017/8/16.
 */

public class ReviewAdapter extends BaseRecyclerAdapter<RecordData.PageDataEntity> implements View.OnClickListener {

    RankInfo mRankInfo;

    private RecordData.PageDataEntity bean;
    private Context mContext;

    public enum ITEM_TYPE {
        ME,
        EMPTY,
        REVIEW
    }


    public ReviewAdapter(Context context) {
        super(context);
        this.mContext = context;
        subscription = RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof ReplyReviewEvent) {
                    ReplyReviewEvent event = (ReplyReviewEvent) o;
                    RecordData.PageDataEntity changeBean = event.getBean();
                    if (bean != null && changeBean != null && changeBean.getId().equals(bean.getId())) {
                        bean.setReplyRating(changeBean.getReplyRating());
                        notifyDataSetChanged();
                    } else if (o instanceof UserUpdateEvent) {
                        notifyDataSetChanged();
                    }

                }
            }
        });
    }


    private Subscription subscription;

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }

    public void setRankInfo(RankInfo rankInfo) {
        mRankInfo = rankInfo;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE.ME.ordinal();
        } else if ( super.getItemCount() == 0) {
            return ITEM_TYPE.EMPTY.ordinal();
        } else {
            return ITEM_TYPE.REVIEW.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        //如果数据为空+2,多个me和空.
        return super.getItemCount() == 0 ? super.getItemCount() + 2 : super.getItemCount() + 1;
    }

    private int getRealPosotion(int position) {
        return super.getItemCount() == 0 ? position - 2 : position - 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ME.ordinal()) {
            return new MeViewHolder(mInflater.inflate(R.layout.me_item, parent, false), this);
        } else if (viewType == ITEM_TYPE.REVIEW.ordinal()) {
            return new ViewHolder(mInflater.inflate(R.layout.review_item, parent, false), this);
        } else {
            return new EmptyViewHolder(mInflater.inflate(R.layout.empty_item, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Logger.i(TAG, "position " + position);

        if (getItemViewType(position) == ITEM_TYPE.ME.ordinal()) {
            MeViewHolder meHolder = (MeViewHolder) viewHolder;
            if(!TextUtils.isEmpty(Preferences.getUserPhoto())){
                ImageHelper.loadImageDpIdRound(Preferences.getUserPhoto(), R.dimen.my_px_460, R.dimen.my_px_426, meHolder.mIvHead);
                ImageHelper.loadImageDpIdBlur(Preferences.getUserPhoto(), R.dimen.my_px_1080, R.dimen.my_px_530, meHolder.mIvBack);
            }

            if (Preferences.getUserRank() == 0) {
                meHolder.mIvGold.setVisibility(View.GONE);
            } else {
                meHolder.mIvGold.setVisibility(View.VISIBLE);
                Bitmap srcBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_my_gold_medal);
                Bitmap disBitmap = ImageUtils.toRoundCorner(srcBitmap, (int) mContext.getResources().getDimension(R.dimen.my_px_28),
                        ImageUtils.CORNER_TOP_RIGHT);
                meHolder.mIvGold.setImageBitmap(disBitmap);
                srcBitmap.recycle();
            }

            meHolder.mTvName.setText(Preferences.getUserName());
            meHolder.mTvAddress.setText(Preferences.getAreaName());
            if (mRankInfo != null) {
                meHolder.mTvStar.setText(mRankInfo.getStar());
                //TODO 分数规则半颗星.
                float star = Float.parseFloat(mRankInfo.getStar());
                ArrayList<ImageView> views = new ArrayList<>();
                views.add(meHolder.mIvStar1);
                views.add(meHolder.mIvStar2);
                views.add(meHolder.mIvStar3);
                views.add(meHolder.mIvStar4);
                views.add(meHolder.mIvStar5);

                for (int i = 0; i < views.size(); i++) {
                    ImageView ivStar = views.get(i);
                    if (star > 0.5 + i) {
                        ivStar.setImageResource(R.mipmap.ic_star_title);
                    } else if (star > i) {
                        ivStar.setImageResource(R.mipmap.ic_star_title_half);
                    } else {
                        ivStar.setImageResource(R.mipmap.ic_star_ungood);
                    }
                }


                if (mRankInfo.getRatingFrequency() == 0 || mRankInfo.getServiceFrequency() == 0) {
                    meHolder.mTvReviewRate.setText("评价率 -");
                } else {
                    String percentStr = StringUtils.percent(mRankInfo.getRatingFrequency(), mRankInfo.getServiceFrequency());
                    meHolder.mTvReviewRate.setText("评价率 " + percentStr);
                }
                meHolder.mTvReviewCount.setText("连线" + mRankInfo.getServiceFrequency() + "次 评价" + mRankInfo.getRatingFrequency() + "次");

            }
        } else if (getItemViewType(position) == ITEM_TYPE.REVIEW.ordinal()) {
            RecordData.PageDataEntity bean = mData.get(getRealPosotion(position));
            String time = bean.getOrderTime();

            System.out.println("time " + time);
            ViewHolder holder = (ViewHolder) viewHolder;

            if (position == getItemCount() - 1) {
                //最后一条隐藏分界线
                holder.mViewDivider.setVisibility(View.GONE);
            } else {
                holder.mViewDivider.setVisibility(View.VISIBLE);
            }


            ImageHelper.loadImageDpId(bean.getPhoto(), R.dimen.my_px_100, R.dimen.my_px_100, holder.mIvHead);
            holder.mTvName.setText(bean.getAddress() + " " + bean.getName());

            if (TimeUtil.isToday(time)) {
                holder.mTvTime.setText("今天" + " " + TimeUtil.getHM(time));
            } else {
                holder.mTvTime.setText(TimeUtil.getyyyyMMddHHmm(time));
            }

            //后台对评价过滤了的.一定有星星,也就是都是接听了的.有通话时间
            holder.mTvCallDuration.setText(" " + String.format("%02d", bean.getMinuteInterval()) + ":" + String.format("%02d", bean.getSecondInterval()));
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

            if (TextUtils.isEmpty(bean.getRatingContent())) {
                holder.mTvReviewContent.setText("还未发表文字评价!");
                holder.mTvReviewContent.setTextColor(mContext.getResources().getColor(R.color.text_gray_909090));
                holder.mTvMeReplyContent.setVisibility(View.GONE);
                holder.mIvMeReplyContentArrows.setVisibility(View.GONE);
                holder.mTvReply.setVisibility(View.GONE);
                holder.mIvReplyArray.setVisibility(View.GONE);
            } else {
                holder.mTvReviewContent.setText(bean.getRatingContent());
                holder.mTvReviewContent.setTextColor(mContext.getResources().getColor(R.color.text_black_434343));
                if (TextUtils.isEmpty(bean.getReplyRating())) {
                    holder.mTvMeReplyContent.setVisibility(View.GONE);
                    holder.mIvMeReplyContentArrows.setVisibility(View.GONE);
                    holder.mTvReply.setVisibility(View.VISIBLE);
                    holder.mIvReplyArray.setVisibility(View.VISIBLE);

                } else {
                    holder.mTvMeReplyContent.setVisibility(View.VISIBLE);
                    holder.mIvMeReplyContentArrows.setVisibility(View.VISIBLE);
                    holder.mTvMeReplyContent.setText(bean.getReplyRating());
                    holder.mTvReply.setVisibility(View.GONE);
                    holder.mIvReplyArray.setVisibility(View.GONE);
                }
            }

            holder.mTvReply.setTag(R.id.tag_bean, bean);
            holder.mTvReply.setTag(R.id.tag_position, position);
        }


    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mIvHead)
        CircleImageView mIvHead;
        @BindView(R.id.mTvName)
        TextView mTvName;
        @BindView(R.id.mTvTime)
        TextView mTvTime;
        @BindView(R.id.mTvCallDuration)
        TextView mTvCallDuration;
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
        @BindView(R.id.mTvReviewContent)
        TextView mTvReviewContent;
        @BindView(R.id.mIvMeReplyContentArrows)
        ImageView mIvMeReplyContentArrows;
        @BindView(R.id.mTvMeReplyContent)
        TextView mTvMeReplyContent;
        @BindView(R.id.mIvReplyArray)
        ImageView mIvReplyArray;
        @BindView(R.id.mTvReply)
        TextView mTvReply;
        @BindView(R.id.mViewDivider)
        View mViewDivider;

        ViewHolder(View view, View.OnClickListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            mTvReply.setOnClickListener(listener);
        }


    }


    static class MeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mIvBack)
        ImageView mIvBack;
        @BindView(R.id.mIvHead)
        ImageView mIvHead;
        @BindView(R.id.mIvGold)
        ImageView mIvGold;
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
        @BindView(R.id.mTvStar)
        TextView mTvStar;
        @BindView(R.id.mLayoutStar)
        LinearLayout mLayoutStar;
        @BindView(R.id.mTvReviewRate)
        TextView mTvReviewRate;
        @BindView(R.id.mTvReviewCount)
        TextView mTvReviewCount;
        @BindView(R.id.mTvName)
        TextView mTvName;
        @BindView(R.id.mTvAddress)
        TextView mTvAddress;
        @BindView(R.id.mIvSetting)
        ImageView mIvSetting;

        MeViewHolder(View view, View.OnClickListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            mIvHead.setOnClickListener(listener);
            mIvSetting.setOnClickListener(listener);

        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {


        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mIvHead:
                boolean isUserAuthByHEZY = Preferences.getUserAuditStatus() == 1;
                UserInfoActivity.actionStart(mContext, false, isUserAuthByHEZY);
                break;
            case R.id.mIvSetting:
                mContext.startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.mTvReply:
                bean = (RecordData.PageDataEntity) v.getTag(R.id.tag_bean);
//                ToastUtils.showToast("点击");
                ReplyReviewActivity.actionStart(mContext, bean);
                ZYAgent.onEvent(mContext, "用户评论,点击回复");
                break;
            default:
                break;
        }
    }
}

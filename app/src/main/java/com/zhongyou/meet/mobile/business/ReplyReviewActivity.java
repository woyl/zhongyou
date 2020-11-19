package com.zhongyou.meet.mobile.business;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.databinding.ReplyReviewActivityBinding;
import com.zhongyou.meet.mobile.entities.RecordData;
import com.zhongyou.meet.mobile.entities.base.BaseErrorBean;
import com.zhongyou.meet.mobile.event.ReplyReviewEvent;
import com.zhongyou.meet.mobile.utils.Logger;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.StringCheckUtil;
import com.zhongyou.meet.mobile.utils.TimeUtil;
import com.zhongyou.meet.mobile.utils.helper.ImageHelper;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;

/**
 * Created by wufan on 2017/8/16.
 */

public class ReplyReviewActivity extends BaseDataBindingActivity<ReplyReviewActivityBinding> {

    private final static String PARAM_BEAN = "param_bean";
    private RecordData.PageDataEntity bean;

    @Override
    public String getStatisticsTag() {
        return "回复评论";
    }

    public static void actionStart(Context context, RecordData.PageDataEntity bean) {
        Intent intent = new Intent(context, ReplyReviewActivity.class);
        intent.putExtra(PARAM_BEAN, bean);
        context.startActivity(intent);
    }

    @Override
    protected void initExtraIntent() {
        bean = getIntent().getParcelableExtra(PARAM_BEAN);
    }

    @Override
    protected int initContentView() {
        return R.layout.reply_review_activity;
    }

    @Override
    protected void initView() {
        setReviewUI();
    }


    @Override
    protected void initListener() {
        mBinding.mIvLeft.setOnClickListener(this);
        mBinding.mTvRight.setOnClickListener(this);
        
        mBinding.mEtReplyContent.addTextChangedListener(mTextWatcher);
    }

    private void setReviewUI(){
        String time = bean.getOrderTime();
        ReplyReviewActivityBinding holder = mBinding;


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

        holder.mTvReviewContent.setText(bean.getRatingContent());


    }


    @Override
    protected void normalOnClick(View v) {
        switch (v.getId()) {
            case R.id.mIvLeft:
                finish();
                break;
            case R.id.mTvRight:
                ZYAgent.onEvent(mContext,"评论回复");
                if(mBinding.mEtReplyContent.getText().toString().trim().length() == 0){
                    showToast("请先输入回复");
                    return;
                }
                requestReplayComment();
                break;
        }
    }

    private TextWatcher mTextWatcher =new TextWatcher() {
        private int editStart;
        private int editEnd;
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mBinding.mEtReplyContent.getSelectionStart();
            editEnd = mBinding.mEtReplyContent.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
            mBinding.mEtReplyContent.removeTextChangedListener(mTextWatcher);

            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            Logger.d(TAG,"StringCheckUtil"+ StringCheckUtil.calculateLength(s.toString()) );
            while (StringCheckUtil.calculateLength(s.toString()) > Constant.REPLY_REVIEW_MAX) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
                Logger.d(TAG,"while"+s.toString());
                Logger.d(TAG,"while"+StringCheckUtil.calculateLength(s.toString()));
            }
            // mEtNickname.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
            mBinding.mEtReplyContent.setSelection(editStart);

            // 恢复监听器
            mBinding.mEtReplyContent.addTextChangedListener(mTextWatcher);

            setLeftCount();



        }
    };

    /**
     * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
     */
    private void setLeftCount() {
        mBinding.mTvTextNum.setText(getInputCount()+"/"+Constant.REPLY_REVIEW_MAX);
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private long getInputCount() {
        return StringCheckUtil.calculateLength(mBinding.mEtReplyContent.getText().toString());
    }


    private void requestReplayComment(){
        final String replayContent = mBinding.mEtReplyContent.getText().toString().trim();
        ApiClient.getInstance().requestReplayComment(this, bean.getId(), replayContent, new OkHttpCallback<BaseErrorBean>() {
            @Override
            public void onSuccess(BaseErrorBean entity) {
                showToast("回复成功");
                bean.setReplyRating(replayContent);
                RxBus.sendMessage(new ReplyReviewEvent(bean));
                finish();
            }
        });
    }
}

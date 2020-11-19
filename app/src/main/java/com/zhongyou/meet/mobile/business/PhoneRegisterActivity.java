package com.zhongyou.meet.mobile.business;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.UserInfoActivity;
import com.zhongyou.meet.mobile.databinding.PhoneRegistLayoutBinding;
import com.zhongyou.meet.mobile.entities.LoginWithPhoneNumber;
import com.zhongyou.meet.mobile.entities.User;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.entities.base.BaseErrorBean;
import com.zhongyou.meet.mobile.event.UserUpdateEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.Login.LoginHelper;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 手机绑定页面
 *
 * @author Dongce
 * create time: 2018/10/22
 */
public class PhoneRegisterActivity extends BaseDataBindingActivity<PhoneRegistLayoutBinding> {

    private Subscription subscription;
    private CountDownTimer countDownTimer;
    private final int SHOW_VERIFICATION_BTN = 1;
    private final int HIDE_VERIFICATION_BTN = 2;
    private final int SHOW_LOGIN_BTN = 3;
    private final int HIDE_LOGIN_BTN = 4;
    private NotificationHandler notificationHandler = new NotificationHandler();

    @Override
    public String getStatisticsTag() {
        return "手机号登录";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int initContentView() {
        return R.layout.phone_regist_layout;
    }

    @Override
    protected void initView() {
        subscription = RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof UserUpdateEvent) {
                    setUserUI();
                }
            }
        });

        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.btnPhoneRequestVerificationCode.setText(millisUntilFinished / 1000 + "S 后重新获取 ");
            }

            @Override
            public void onFinish() {
                notificationHandler.sendEmptyMessage(SHOW_VERIFICATION_BTN);
                mBinding.btnPhoneRequestVerificationCode.setText("获取验证码");
            }
        };

    }

    @Override
    protected void initListener() {
        mBinding.btnPhoneRequestVerificationCode.setOnClickListener(this);
        mBinding.btnPhoneLogin.setOnClickListener(this);
    }

    private void setUserUI() {
    }

    @Override
    protected void normalOnClick(View v) {

        final String phoneNumber = mBinding.edtPhoneNumber.getText().toString().trim();

        switch (v.getId()) {

            case R.id.btn_phone_request_verification_code:

                if (TextUtils.isEmpty(phoneNumber)) {
                    showToast("当前手机号不能为空");
                    return;
                }
                if (phoneNumber.equals(Preferences.getUserMobile())) {
                    showToast("当前手机号已设置成功");
//                    return;
                }

                countDownTimer.start();
                notificationHandler.sendEmptyMessage(HIDE_VERIFICATION_BTN);

                apiClient.requestVerifyCode(this, phoneNumber, new OkHttpCallback<BaseErrorBean>() {
                    @Override
                    public void onSuccess(BaseErrorBean entity) {

                    }

                    @Override
                    public void onFailure(int errorCode, BaseException exception) {
                        super.onFailure(errorCode, exception);
                        String errorMsg = "错误信息：" + exception.getMessage();
                        ZYAgent.onEvent(PhoneRegisterActivity.this, "获取验证码失败，错误码：" + errorCode + "，" + errorMsg);
                        countDownTimer.cancel();
                        notificationHandler.sendEmptyMessage(SHOW_VERIFICATION_BTN);
                        mBinding.btnPhoneRequestVerificationCode.setText("获取验证码");
                        ToastUtils.showToast(errorMsg);
                    }
                });

                break;

            case R.id.btn_phone_login:

                if (TextUtils.isEmpty(phoneNumber)) {
                    showToast("当前手机号不能为空");
                    return;
                }

                final String verificationCode = mBinding.edtPhoneVerificationCode.getText().toString().trim();
                if (TextUtils.isEmpty(verificationCode)) {
                    showToast("验证码不能为空");
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("mobile", phoneNumber);
                params.put("verifyCode", verificationCode);

                apiClient.requestLoginWithPhoneNumber(this, params, loginWithPhoneNumberCallback);
                break;
        }
    }

    private OkHttpCallback<BaseBean<LoginWithPhoneNumber>> loginWithPhoneNumberCallback = new OkHttpCallback<BaseBean<LoginWithPhoneNumber>>() {

        @Override
        public void onStart() {
            super.onStart();
            notificationHandler.sendEmptyMessage(HIDE_LOGIN_BTN);
        }

        @Override
        public void onSuccess(BaseBean<LoginWithPhoneNumber> entity) {

            if (entity.getData() == null) {
                return;
            }
            if (entity.getErrcode() != 0) {
                String errMsg = "用户登录请求失败";
                ToastUtils.showToast(errMsg);
                ZYAgent.onEvent(PhoneRegisterActivity.this, errMsg + "，错误码：" + entity.getErrcode());
                return;
            }
            LoginWithPhoneNumber loginWithPhoneNumber = entity.getData();
            User user = loginWithPhoneNumber.getUser();
            LoginHelper.savaUser(user);

            if (loginWithPhoneNumber.getAuthPass()) {
                //已经认证过，是海尔员工
                startActivity(new Intent(PhoneRegisterActivity.this, HomeActivity.class));
            } else {
                //未认证过，不是海尔员工
                boolean isUserAuthByHEZY = Preferences.getUserAuditStatus() == 1;
                UserInfoActivity.actionStart(PhoneRegisterActivity.this, true, isUserAuthByHEZY);
            }
            finish();
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            String errorMsg = "错误信息：" + exception.getMessage();
            ZYAgent.onEvent(PhoneRegisterActivity.this, "错误码：" + errorCode + "，" + errorMsg);
            ToastUtils.showToast(errorMsg);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            notificationHandler.sendEmptyMessage(SHOW_LOGIN_BTN);
        }
    };

    private class NotificationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SHOW_VERIFICATION_BTN:
                    mBinding.btnPhoneRequestVerificationCode.setTextColor(getResources().getColor(R.color.white));
                    mBinding.btnPhoneRequestVerificationCode.setClickable(true);
                    mBinding.btnPhoneRequestVerificationCode.setBackground(getResources().getDrawable(R.drawable.btn_verification_fillet_bg_normal));
                    break;
                case HIDE_VERIFICATION_BTN:
                    mBinding.btnPhoneRequestVerificationCode.setTextColor(getResources().getColor(R.color.purple_B07EDA));
                    mBinding.btnPhoneRequestVerificationCode.setClickable(false);
                    mBinding.btnPhoneRequestVerificationCode.setBackground(getResources().getDrawable(R.drawable.btn_verification_fillet_bg_pressed));
                    break;
                case SHOW_LOGIN_BTN:
                    mBinding.btnPhoneLogin.setClickable(true);
                    mBinding.btnPhoneLogin.setBackground(getResources().getDrawable(R.drawable.btn_phone_login_fillet_bg_normal));
                    break;
                case HIDE_LOGIN_BTN:
                    mBinding.btnPhoneLogin.setClickable(false);
                    mBinding.btnPhoneLogin.setBackground(getResources().getDrawable(R.drawable.btn_phone_login_fillet_bg_pressed));
                    break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }
}

package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.maning.mndialoglibrary.MToast;
import com.tencent.mmkv.MMKV;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerHolder;
import com.zhongyou.meet.mobile.ameeting.adater.BaseRecyclerViewAdapter;
import com.zhongyou.meet.mobile.ameeting.network.RxSchedulersHelper;
import com.zhongyou.meet.mobile.ameeting.network.RxSubscriber;
import com.zhongyou.meet.mobile.core.JoinMeetingDialog;
import com.zhongyou.meet.mobile.entities.Agora;
import com.zhongyou.meet.mobile.entiy.MeetingJoin;
import com.zhongyou.meet.mobile.entiy.MeetingsData;
import com.zhongyou.meet.mobile.entiy.MessageDetail;
import com.zhongyou.meet.mobile.mvp.contract.MessageDetailContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.utils.UIDUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.agora.openlive.ui.MeetingInitActivity;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/04/2020 21:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class MessageDetailPresenter extends BasePresenter<MessageDetailContract.Model, MessageDetailContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private AlertDialog mPhoneStatusDialog;
    private Dialog dialog;
    List<MessageDetail> dataLists = new ArrayList<>();

    @Inject
    public MessageDetailPresenter(MessageDetailContract.Model model, MessageDetailContract.View rootView) {
        super(model, rootView);
    }

    public void getMessageDetail(String type, int page) {
        if (mModel != null) {
            mModel.getMessageDetail(MMKV.defaultMMKV().decodeString(MMKVHelper.ID), type, page)
                    .compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(new RxSubscriber<JSONObject>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            addDispose(d);
                        }

                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") == 0) {
                                int totalPage = jsonObject.getJSONObject("data").getInteger("totalPage");
                                mRootView.canLoadMore(totalPage);
                                if (page == 1) {
                                    dataLists.clear();
                                }
                                List<MessageDetail> data = JSON.parseArray(jsonObject.getJSONObject("data").getJSONArray("list").toJSONString(), MessageDetail.class);
                                dataLists.addAll(data);
                                if (mRootView != null) {
                                    mRootView.getMessageDetailComplete(dataLists);
                                }
                            } else {
                                MToast.makeTextShort(mRootView.getActivity(), jsonObject.getString("errmsg"));
                            }
                        }
                    });
        }
    }


    private void initDialog(String meetingId, int resolvingPower, int isToken, int isRecord) {
        View view = View.inflate(mRootView.getActivity(), R.layout.dialog_meeting_input_code, null);

        final EditText codeEdit = view.findViewById(R.id.code);

        View audienceConfirm = view.findViewById(R.id.audienceConfirm);
        try {
            audienceConfirm.setVisibility(isToken == 1.0d ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            audienceConfirm.setVisibility(View.GONE);
        }
        String code = MMKV.defaultMMKV().decodeString(meetingId);
        if (TextUtils.isEmpty(code)) {
            codeEdit.setText("");
        } else {
            codeEdit.setText(code);
            codeEdit.setSelection(code.length());
        }
        if (audienceConfirm.getVisibility() == View.VISIBLE) {
            view.findViewById(R.id.confirm).setBackground(mRootView.getActivity().getResources().getDrawable(R.drawable.btn_shap_gray));
        } else {
            view.findViewById(R.id.confirm).setBackground(mRootView.getActivity().getResources().getDrawable(R.drawable.btn_shap_blue));
        }
        //需要录制
        if (isRecord == 1) {
            Constant.isNeedRecord = true;
        } else {
            Constant.isNeedRecord = false;
        }
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (!TextUtils.isEmpty(codeEdit.getText())) {
                    JSONObject params = new JSONObject();
                    params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
                    params.put("meetingId", meetingId);
                    params.put("token", codeEdit.getText().toString().trim());
                    verifyRole(params, meetingId, isRecord, resolvingPower, codeEdit.getText().toString().trim());
                } else {
                    codeEdit.setError("会议加入码不能为空");
                }
            }
        });
        audienceConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                JSONObject params = new JSONObject();
                params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
                params.put("meetingId", meetingId);
                params.put("token", "");
                verifyRole(params, meetingId, isRecord, resolvingPower, "");
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = new Dialog(mRootView.getActivity(), R.style.CustomDialog);
        dialog.setContentView(view);
        dialog.show();
    }

    private void verifyRole(JSONObject params, String id, int isRecord, int resolvingPower, String code) {
        if (mModel != null) {
            mModel.verifyRole(params).compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") == 0) {

                                JSONObject params = new JSONObject();
                                params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
                                params.put("meetingId", id);
                                params.put("token", code);
                                joinMeeting(params, id, code);
                            } else {
                                if (mRootView != null) {
                                    mRootView.showMessage(jsonObject.getString("errmsg"));
                                }
                            }
                        }
                    });
        }
    }

    public void detele(String msgId,int position) {
        if (mModel != null) {
            mModel.deteleItem(msgId).compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") == 0) {
                                if (mRootView != null) {
                                    mRootView.deleteSuccess(true,position);
                                }
                            } else {
                                if (mRootView != null) {
                                    mRootView.showMessage(jsonObject.getString("errmsg"));
                                }

                            }
                        }
                    });
        }
    }

    public void joinMeeting(JSONObject jsonObject, String id, String code) {

        if (mModel != null) {
            mModel.joinMeeting(jsonObject).compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") == 0) {

                                MeetingJoin meetingJoin = JSON.parseObject(jsonObject.toJSONString(), MeetingJoin.class);
                                if (meetingJoin == null || meetingJoin.getData() == null || TextUtils.isEmpty(meetingJoin.getData().getMeeting().getId())) {
                                    mRootView.showMessage("出错了 请重试");
                                    return;
                                }

                                if (meetingJoin.getData().getMeeting().getIsRecord() == 1) {
                                    Constant.isNeedRecord = true;
                                } else {
                                    Constant.isNeedRecord = false;
                                }
                                MMKV.defaultMMKV().encode(id, code);
                                int mJoinRole = meetingJoin.getData().getRole();
                                String role = "Subscriber";
                                if (mJoinRole == 0) {
                                    role = "Publisher";
                                } else if (mJoinRole == 1) {
                                    role = "Publisher";
                                } else if (mJoinRole == 2) {
                                    role = "Subscriber";
                                }
                                getAgoraKey(meetingJoin.getData().getMeeting().getId(), UIDUtil.generatorUID(Preferences.getUserId()), role, mJoinRole, meetingJoin);
                                MMKV.defaultMMKV().encode(MMKVHelper.KEY_meetingQuilty, meetingJoin.getData().getMeeting().getResolvingPower());

                            } else {
                                if (mRootView != null) {
                                    mRootView.showMessage(jsonObject.getString("errmsg"));
                                }
                            }
                        }
                    });
        }


    }

    public void getAgoraKey(String channel, String account, String role, int joinrole, MeetingJoin meetingJoin) {
        if (mModel != null) {
            mModel.getAgoraKey(channel, account, role).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .compose(RxSchedulersHelper.io_main())
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") == 0) {
                                if (mRootView != null) {
                                    Agora agora = new Agora();
                                    agora.setAppID(jsonObject.getJSONObject("data").getString("appID"));
                                    agora.setIsTest(jsonObject.getJSONObject("data").getString("isTest"));
                                    agora.setSignalingKey(jsonObject.getJSONObject("data").getString("signalingKey"));
                                    agora.setToken(jsonObject.getJSONObject("data").getString("token"));

                                    Intent intent = new Intent(mRootView.getActivity(), MeetingInitActivity.class);
                                    intent.putExtra("agora", agora);
                                    intent.putExtra("meeting", meetingJoin.getData());
                                    intent.putExtra("role", joinrole);
                                    intent.putExtra("isMaker", meetingJoin.getData().getMeeting().getIsMeeting() != 1);
                                    mRootView.launchActivity(intent);
                                }
                            } else {
                                if (mRootView != null) {
                                    mRootView.showMessage(jsonObject.getString("errmsg"));
                                }
                            }
                        }
                    });
        }
    }

    private void showPhoneStatus(int i, final MessageDetail meeting) {
        mPhoneStatusDialog = new AlertDialog(mRootView.getActivity())
                .builder()
                .setTitle("提示")
                .setMsg(i == 1 ? "检测到摄像头关闭 是否打开？" : "检测到麦克风关闭 是否打开？")
                .setNegativeButton("进入会议", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPhoneStatusDialog.dismiss();
                        try {
                            if (Double.parseDouble(String.valueOf(meeting.getIsToken())) == 1.0D) {//需要加入码的
                                if (mRootView.getActivity() != null) {
                                    JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), meeting.getMeetingId());
                                }

                            } else {
                                if (mRootView.getActivity() != null) {
                                    JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) mRootView.getActivity(), meeting.getMeetingId());
                                }
                            }
                        } catch (Exception e) {
                            if (mRootView.getActivity() != null) {
                                JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), meeting.getMeetingId());
                            }
                        }
                    }
                })
                .setPositiveButton("去设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPhoneStatusDialog.dismiss();
                        mRootView.launchActivity(new Intent(mRootView.getActivity(), MeetingSettingActivity.class));
                    }
                });
        mPhoneStatusDialog.show();
    }

    public void readAllMessage(String type) {
        mModel.readAllMessage(type).compose(RxSchedulersHelper.io_main())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new RxSubscriber<JSONObject>() {
                    @Override
                    public void _onNext(JSONObject jsonObject) {
                        if (jsonObject.getInteger("errcode") == 0) {
                            mRootView.readAllMessageResult(true);
                        } else {
                            mRootView.readAllMessageResult(false);
                        }
                    }
                });

    }

    public void readOneMessage(String id, int position, MessageDetail item) {
        mModel.readOneMessage(id).compose(RxSchedulersHelper.io_main())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new RxSubscriber<JSONObject>() {
                    @Override
                    public void _onNext(JSONObject jsonObject) {
                        if (jsonObject.getInteger("errcode") == 0) {
                            mRootView.readOneMessageResult(true, position, item);
                        } else {
                            mRootView.readOneMessageResult(false, position, item);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtils.showToast(e.getMessage());
                    }

                    @Override
                    public void _onError(int code, String msg) {
                        super._onError(code, msg);
                        ToastUtils.showToast(msg);
                    }
                });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
        if (mPhoneStatusDialog != null) {
            mPhoneStatusDialog.dismiss();
        }
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}

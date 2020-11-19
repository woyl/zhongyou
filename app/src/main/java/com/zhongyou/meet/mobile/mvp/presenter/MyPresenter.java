package com.zhongyou.meet.mobile.mvp.presenter;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adorkable.iosdialog.AlertDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;
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
import com.zhongyou.meet.mobile.entiy.MyAllCourse;
import com.zhongyou.meet.mobile.mvp.contract.MyContract;
import com.zhongyou.meet.mobile.mvp.ui.activity.MakerDetailActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.MeetingSettingActivity;
import com.zhongyou.meet.mobile.mvp.ui.activity.NewMakerDetailActivity;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.MMKVHelper;
import com.zhongyou.meet.mobile.utils.SpUtil;
import com.zhongyou.meet.mobile.utils.UIDUtil;

import java.util.List;

import javax.inject.Inject;

import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.MeetingInitActivity;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import timber.log.Timber;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 04/03/2020 23:02
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
public class MyPresenter extends BasePresenter<MyContract.Model, MyContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private Dialog dialog;
    private AlertDialog mPhoneStatusDialog;

    @Inject
    public MyPresenter(MyContract.Model model, MyContract.View rootView) {
        super(model, rootView);
    }


    public void getCollectMeeting() {
        if (mModel != null) {
            mModel.getCollectMeeting(MMKV.defaultMMKV().decodeString(MMKVHelper.ID))
                    .compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(new RxSubscriber<JSONObject>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            addDispose(d);
                        }

                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            mRootView.hideLoading();
                            if (jsonObject.getInteger("errcode") == 0) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                MyAllCourse myAllCourse = JSON.parseObject(data.toJSONString(), MyAllCourse.class);
                                mRootView.getDataSuccess(myAllCourse);
                            } else {
                                mRootView.showMessage(jsonObject.getString("errmsg"));
                            }
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                            mRootView.hideLoading();
                        }

                        @Override
                        public void _onError(int code, String msg) {
                            super._onError(code, msg);
                            mRootView.hideLoading();
                        }
                    });
        }
    }

    public BaseRecyclerViewAdapter<MyAllCourse.CollectMeetingsBean> initCollectMeetingAdapter(List<MyAllCourse.CollectMeetingsBean> dataList) {
        return new BaseRecyclerViewAdapter<MyAllCourse.CollectMeetingsBean>(mRootView.getActivity(), dataList, R.layout.item_meeting) {
            @Override
            public void convert(BaseRecyclerHolder holder, MyAllCourse.CollectMeetingsBean item, int position, boolean isScrolling) {
                holder.setText(R.id.title, item.getTitle());
                holder.setText(R.id.begin_time, item.getStartTime().substring(0, 16));
                TextView state = holder.getView(R.id.meeting_state);
                LikeButton collectButton = holder.getView(R.id.collectButton);
                collectButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        unCollection(dataList, item.getId(), position);
                    }
                });
                if (item.getIsCollection() == 0) {
                    collectButton.setLiked(false);
                } else {
                    collectButton.setLiked(true);
                }
                if (item.getMeetingProcess() == 1) {
                    state.setBackgroundResource(R.drawable.bg_meeting_state_new);
                    state.setText("进行中");
                } else {
                    state.setBackgroundResource(R.drawable.bg_meeting_state_a_new);
                    state.setText("未开始");
                }
                if (position % 2 == 0) {
                    ((ImageView) holder.getView(R.id.imageView)).setImageDrawable(mRootView.getActivity().getResources().getDrawable(R.mipmap.bg_meeting_item_a));
                } else {
                    ((ImageView) holder.getView(R.id.imageView)).setImageDrawable(mRootView.getActivity().getResources().getDrawable(R.mipmap.bg_meeting_item_b));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.CAMERA_STATE, true)) {
                            showPhoneStatus(1, item, item.getIsToken());
                            return;
                        }
                        if (!MMKV.defaultMMKV().decodeBool(MMKVHelper.MICROPHONE_STATE, true)) {
                            showPhoneStatus(2, item, item.getIsToken());
                            return;
                        }
                        try {
                            if (Double.parseDouble(String.valueOf(item.getIsToken())) == 1.0D) {//需要加入码的
                                if (mRootView.getActivity() != null) {
                                    JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), item.getId());
                                }

                            } else {
                                if (mRootView.getActivity() != null) {
                                    JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) mRootView.getActivity(), item.getId());
                                }
                            }
                        } catch (Exception e) {
                            if (mRootView.getActivity() != null) {
                                JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), item.getId());
                            }
                        }
                    }
                });
            }
        };
    }

    private void unCollection(List<MyAllCourse.CollectMeetingsBean> dataList, String id, int position) {
        if (mModel != null) {
            mModel.cancelAdvance(id).compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") != 0) {
                                mRootView.showMessage(jsonObject.getString("errmsg"));
                                if (mRootView != null) {
                                    mRootView.cancelAdvance(false, dataList, id, position);
                                }
                            } else {
                                if (mRootView != null) {
                                    mRootView.cancelAdvance(true, dataList, id, position);
                                }
                            }
                        }

                        @Override
                        public void _onError(int code, String msg) {
                            super._onError(code, msg);
                            if (mRootView != null) {
                                mRootView.cancelAdvance(false, dataList, id, position);
                            }
                        }
                    });
        }
    }


    public BaseRecyclerViewAdapter<MyAllCourse.SignUpSeriesBean> initCollectCourseAdapter(List<MyAllCourse.SignUpSeriesBean> dataList) {
        Timber.e("dataList---->%s", JSON.toJSONString(dataList));
        return new BaseRecyclerViewAdapter<MyAllCourse.SignUpSeriesBean>(mRootView.getActivity(), dataList, R.layout.item_sigin) {
            @Override
            public void convert(BaseRecyclerHolder holder, MyAllCourse.SignUpSeriesBean item, int position, boolean isScrolling) {
                holder.setText(R.id.name, item.getName());
//				holder.setText(R.id.begin_time, "———" + item.getName() + "———");
                holder.getView(R.id.item_time).setVisibility(View.INVISIBLE);
                holder.getView(R.id.meeting_state).setVisibility(View.GONE);
                holder.setImageByUrl(R.id.imageView, item.getPictureURL());

                LikeButton collectButton = holder.getView(R.id.collectButton);
                collectButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        Timber.e("dataList---->%s", JSON.toJSONString(dataList));
                        unSignCourse(item.getId(), dataList, position);
                    }
                });
                if (item.getIsSign() == 0) {
                    collectButton.setLiked(false);
                } else {
                    collectButton.setLiked(true);
                }
//				if (position % 2 == 0) {
//					((ImageView) holder.getView(R.id.imageView)).setImageDrawable(mRootView.getActivity().getResources().getDrawable(R.mipmap.bg_meeting_item_a));
//				} else {
//					((ImageView) holder.getView(R.id.imageView)).setImageDrawable(mRootView.getActivity().getResources().getDrawable(R.mipmap.bg_meeting_item_b));
//				}
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRootView.launchActivity(new Intent(mRootView.getActivity(), NewMakerDetailActivity.class)
                                .putExtra("pageId", dataList.get(position).getPageId())
                                .putExtra("seriesId", dataList.get(position).getId())
                                .putExtra("url", item.getPictureURL())
                                .putExtra("isAuth", item.getIsAuth())
                                .putExtra("isSignUp", item.getIsSignUp()));
                    }
                });
            }
        };
    }

    private void unSignCourse(String id, List<MyAllCourse.SignUpSeriesBean> dataList, int position) {
        if (mModel != null) {
            mModel.siginCourse(id, 0).compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") != 0) {
                                mRootView.showMessage(jsonObject.getString("errmsg"));
                                mRootView.unSignCourse(false, dataList, id, position);
                            } else {
                                mRootView.unSignCourse(true, dataList, id, position);
                            }
                        }

                        @Override
                        public void _onError(int code, String msg) {
                            super._onError(code, msg);
                            mRootView.unSignCourse(false, dataList, id, position);
                        }
                    });
        }
    }


    public void initDialog(MyAllCourse.CollectMeetingsBean model, int isToken) {
        View view = View.inflate(mRootView.getActivity(), R.layout.dialog_meeting_input_code, null);
        View audienceConfirm = view.findViewById(R.id.audienceConfirm);
        audienceConfirm.setVisibility(isToken == 1 ? View.GONE : View.VISIBLE);

        final EditText codeEdit = view.findViewById(R.id.code);
        if (!SpUtil.getString(model.getId(), "").equals("")) {
            codeEdit.setText(SpUtil.getString(model.getId(), ""));
        } else {
            codeEdit.setText("");
        }
        String code = MMKV.defaultMMKV().decodeString(model.getId());
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

        MMKV.defaultMMKV().encode(MMKVHelper.KEY_meetingQuilty, model.getResolvingPower());
        //需要录制
        if (model.getIsRecord() == 1) {
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
                    params.put("meetingId", model.getId());
                    params.put("token", codeEdit.getText().toString().trim());
                    verfyRole(params, model.getId(), model.getIsRecord(), model.getResolvingPower(), codeEdit.getText().toString().trim());
                } else {
                    codeEdit.setError("加入码不能为空");
                }
            }
        });
        audienceConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                params.put("clientUid", UIDUtil.generatorUID(Preferences.getUserId()));
                params.put("meetingId", model.getId());
                params.put("token", "");
                verfyRole(params, model.getId(), model.getIsRecord(), model.getResolvingPower(), "");
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


    public void showPhoneStatus(int i, final MyAllCourse.CollectMeetingsBean meeting, int isToken) {
        mPhoneStatusDialog = new AlertDialog(mRootView.getActivity())
                .builder()
                .setTitle("提示")
                .setMsg(i == 1 ? "检测到摄像头关闭 是否打开？" : "检测到麦克风关闭 是否打开？")
                .setNegativeButton("进入会议", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPhoneStatusDialog.dismiss();
                        try {
                            if (Double.parseDouble(String.valueOf(isToken)) == 1.0D) {//需要加入码的
                                if (mRootView.getActivity() != null) {
                                    JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), meeting.getId());
                                }

                            } else {
                                if (mRootView.getActivity() != null) {
                                    JoinMeetingDialog.INSTANCE.showNoCodeDialog((AppCompatActivity) mRootView.getActivity(), meeting.getId());
                                }
                            }
                        } catch (Exception e) {
                            if (mRootView.getActivity() != null) {
                                JoinMeetingDialog.INSTANCE.showNeedCodeDialog((AppCompatActivity) mRootView.getActivity(), meeting.getId());
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

    private void verfyRole(JSONObject params, String id, int isRecord, int resolvingPower, String code) {
        if (mModel != null) {
            mModel.verfyRole(params).compose(RxSchedulersHelper.io_main())
                    .compose(RxSchedulersHelper.io_main())
                    .subscribe(new RxSubscriber<JSONObject>() {
                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") == 0) {
                                int profileIndex = 0;
                                if (resolvingPower == 1.0) {
                                    profileIndex = 0;
                                } else if (resolvingPower == 2.0) {
                                    profileIndex = 1;
                                } else if (resolvingPower == 3.0) {
                                    profileIndex = 2;
                                }

                                //需要录制
                                if (isRecord == 1) {
                                    Constant.isNeedRecord = true;
                                } else {
                                    Constant.isNeedRecord = false;
                                }


                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mRootView.getActivity());
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
                                editor.apply();

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

    /**
     * 获取未读消息
     */
    public void getUnReadMessage() {
        if (mModel != null) {
            mModel.getUnReadMessageCount().compose(RxSchedulersHelper.io_main())
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .subscribe(new RxSubscriber<JSONObject>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            addDispose(d);
                        }

                        @Override
                        public void _onNext(JSONObject jsonObject) {
                            if (jsonObject.getInteger("errcode") == 0) {
                                Integer data = jsonObject.getInteger("data");
                                if (data == 0) {
                                    mRootView.showRedDot(false, data);
                                } else {
                                    mRootView.showRedDot(true, data);
                                }
                            } else {
                                mRootView.showRedDot(false, 0);
                            }
                        }

                        @Override
                        public void _onError(int code, String msg) {
                            super._onError(code, msg);
                            mRootView.showRedDot(false, 0);
                        }
                    });
        }
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

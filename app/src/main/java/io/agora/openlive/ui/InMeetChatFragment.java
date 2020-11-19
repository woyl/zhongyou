package io.agora.openlive.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.jess.arms.utils.RxBus;
import com.orhanobut.logger.Logger;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.BaseFragment;
import com.zhongyou.meet.mobile.entities.Bucket;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.event.ForumRevokeEvent;
import com.zhongyou.meet.mobile.event.ForumSendEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.ToastUtils;
import com.zhongyou.meet.mobile.view.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class InMeetChatFragment extends BaseFragment implements InMeetingAdapter.onItemClickInt{

    private TextView tvSend;
    private EditText editText;
    private RecyclerView recyclerViewChat;
    private ProgressBar proBar;
    private int mTotalPage = -1;
    private int mPageNo = 2;
    private String mMeetingId = "";
    private List<ChatMesData.PageDataEntity> dataChat = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private InMeetingAdapter adapter;
    private static int delayTime = 2000;
    private Subscription subscription;
    private boolean onCreate = true;
    private int charMessgageWhat;
    private ChatMesData.PageDataEntity mPageDataEntity;

    @Override
    public String getStatisticsTag() {
        return "会议中聊天";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inmeeting_chat, null, false);
        initView(view);
        initRecy();
        requestRecord(1+"",10+"");
        subscription = RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof ForumSendEvent) {
                    Logger.e(JSON.toJSONString(((ForumSendEvent)o)));
                    if(mMeetingId != null){
                        if(!((ForumSendEvent) o).getEntity().getMeetingId().equals(mMeetingId)){
                            return;
                        }
                    }
                    if (((ForumSendEvent) o).getEntity().getUserId().equals(Preferences.getUserId())) {
                        for(int i=dataChat.size()-1; i>=0;i--){
                            Log.v("forumsendevent9090","进入循环=="+i);
                            Log.v("forumsendevent9090","进入循环=="+dataChat.get(i).getReplyTimestamp());
                            Log.v("forumsendevent9090","((ForumSendEvent) o).getEntity().getReplyTimestamp()=="+((ForumSendEvent) o).getEntity().getReplyTimestamp());
                            if(dataChat.get(i).getTs() == ((ForumSendEvent) o).getEntity().getTs()){

                                Log.v("forumsendevent9090","拥有13=="+handler.hasMessages(i));
//                            Log.v("forumsendevent9090","拥有13=="+handler.hasMessages(13,temp));
                                if(handler.hasMessages(i)){
                                    Log.v("forumsendevent9090","拥有13=="+dataChat.get(i).getContent());
                                    handler.removeMessages(i);
                                }
                                dataChat.set(i,((ForumSendEvent) o).getEntity());
                                Log.v("forumsendevent9090","退出循环=="+i);
                                Message msg = new Message();
                                msg.arg1 = i;
                                msg.what=12;

                                handler.sendMessageDelayed(msg,10);
                                break;
                            }
                        }


                    } else {
                        dataChat.add(((ForumSendEvent) o).getEntity());
                        handler.sendEmptyMessageDelayed(16,0);

                    }

                } else if (o instanceof ForumRevokeEvent) {
//                    requestRecordOnlyLast(true);
                    if(mMeetingId != null){
                        if(!((ForumRevokeEvent) o).getEntity().getMeetingId().equals(mMeetingId)){
                            return;
                        }
                    }
                    for(int i=0; i<dataChat.size();i++){
                        if(dataChat.get(i).getId().equals(((ForumRevokeEvent) o).getEntity().getId())){
                            dataChat.get(i).setMsgType(1);
                            Message msg = new Message();
                            msg.arg1 = i;
                            msg.what=12;

                            handler.sendMessageDelayed(msg,10);
                            if(progressDialog !=null &&progressDialog.isShowing()){
                                if(handler.hasMessages(14)){
                                    handler.removeMessages(14);
                                }
                                progressDialog.dismiss();
                            }
                            break;
                        }

                    }

                }

            }
        });
        return view;
    }
    public static InMeetChatFragment newInstance(String id) {
        InMeetChatFragment fragment = new InMeetChatFragment();
        fragment.mMeetingId = id;
        return fragment;
    }
    private void initView(View view){
        tvSend = view.findViewById(R.id.tv_send);
        editText = (EditText)view.findViewById(R.id.edit);
        recyclerViewChat = (RecyclerView)view.findViewById(R.id.recy_chat);
        proBar = (ProgressBar) view.findViewById(R.id.progressbar);

        proBar.setVisibility(View.GONE);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editText.getText().toString())){
                    ToastUtils.showToast("发送内容不能为空");
                    return;
                }
                mPageDataEntity = new ChatMesData.PageDataEntity();
                long ts = System.currentTimeMillis();
                Logger.e(Preferences.getUserName());
                mPageDataEntity.setContent(editText.getText().toString());
                mPageDataEntity.setId("");
                mPageDataEntity.setMsgType(0);
                mPageDataEntity.setTs(ts);
                mPageDataEntity.setType(0);
                mPageDataEntity.setUserName(Preferences.getUserName());
                mPageDataEntity.setUserId(Preferences.getUserId());
                mPageDataEntity.setUserLogo(Preferences.getUserPhoto());
                mPageDataEntity.setLocalState(1);
                Message msg = new Message();
                charMessgageWhat = dataChat.size();
                msg.what = charMessgageWhat;
//                msg.arg1 = dataChat.size();
                msg.obj = mPageDataEntity;
                handler.sendMessageDelayed(msg,delayTime);
                dataChat.add((ChatMesData.PageDataEntity) mPageDataEntity);
                initLastData(dataChat,true);
                sendAction(ts,editText.getText().toString());
            }
        });


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    if(TextUtils.isEmpty(editText.getText().toString())){
                        ToastUtils.showToast("发送内容不能为空");
                        return true;
                    }
                    Logger.e("onEditorAction: "+Preferences.getUserName() );
                    mPageDataEntity = new ChatMesData.PageDataEntity();
                    long ts = System.currentTimeMillis();
                    mPageDataEntity.setContent(editText.getText().toString());
                    mPageDataEntity.setId("");
                    mPageDataEntity.setMsgType(0);
                    mPageDataEntity.setTs(ts);
                    mPageDataEntity.setType(0);
                    mPageDataEntity.setUserName(Preferences.getUserName());
                    mPageDataEntity.setUserId(Preferences.getUserId());
                    mPageDataEntity.setUserLogo(Preferences.getUserPhoto());
                    mPageDataEntity.setLocalState(1);
                    Message msg = new Message();
                    charMessgageWhat = dataChat.size();
                    msg.what = charMessgageWhat;
//                msg.arg1 = dataChat.size();
                    msg.obj = mPageDataEntity;
                    handler.sendMessageDelayed(msg,delayTime);
                    dataChat.add((ChatMesData.PageDataEntity) mPageDataEntity);
                    initLastData(dataChat,true);
                    sendAction(ts,editText.getText().toString());
                }
                return false;
            }
        });

    }
    private void sendAction(long ts, String content) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("meetingId", mMeetingId);
        params.put("ts", ts);
        params.put("content", content);
        params.put("type", 0);
        editText.setText("");
        ApiClient.getInstance().expostorPostChatMessage(TAG, expostorStatsCallback, params);
    }
    private void initLastData(List<ChatMesData.PageDataEntity> data, boolean last) {
        if(adapter == null){
            return;
        }
        adapter.notifyItemChanged(data.size() - 1);
        if (last) {
            if (handler.hasMessages(10)) {
                handler.removeMessages(10);
            }
            handler.sendEmptyMessageDelayed(10, 100);
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 11) {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//                params.bottomMargin = (int)getResources().getDimension(R.dimen.my_px_500);
//                recyclerViewChat.setLayoutParams(params);
//                recyclerViewInput.setVisibility(View.VISIBLE);
                recyclerViewChat.scrollToPosition(dataChat.size() - 1);
            } else if (msg.what == 10){
                recyclerViewChat.scrollToPosition(dataChat.size() - 1);
            }else if(msg.what == 12){
//                dataChat.add((ChatMesData.PageDataEntity) msg.obj);
//                initLastData(dataChat,true);
                if (adapter!=null){
                    adapter.notifyItemChanged(msg.arg1);
                }
            }else if(msg.what==14){
                if(progressDialog !=null && progressDialog.isShowing()){
                    ToastUtils.showToast("网络出现问题");
                    progressDialog.dismiss();
                }

            }else if(msg.what == 16){
                initLastData(dataChat, true);
            }/*else if(msg.obj instanceof ChatMesData.PageDataEntity&&msg.what!=17){
                ((ChatMesData.PageDataEntity)msg.obj).setLocalState(2);
                dataChat.set(msg.what,((ChatMesData.PageDataEntity)msg.obj));
                if (adapter!=null){
                    adapter.notifyItemChanged(msg.what);
                }

            }else if (msg.obj instanceof ChatMesData.PageDataEntity&&msg.what==17){
                ((ChatMesData.PageDataEntity)msg.obj).setLocalState(0);
                dataChat.set(charMessgageWhat,((ChatMesData.PageDataEntity)msg.obj));
                if (adapter!=null){
                    adapter.notifyItemChanged(charMessgageWhat);
                }

            }*/
        }
    };

    private OkHttpCallback expostorStatsCallback = new OkHttpCallback<Bucket<ChatMesData.PageDataEntity>>() {

        @Override
        public void onSuccess(Bucket<ChatMesData.PageDataEntity> entity) {
//            ToastUtils.showToast("提交成功");

            if (mPageDataEntity!=null){
                Message message=new Message();
                mPageDataEntity.setLocalState(0);
                message.obj=mPageDataEntity;
                message.what=17;
                handler.sendMessage(message);
            }

        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            if(progressDialog !=null &&progressDialog.isShowing()){
                if(handler.hasMessages(14)){
                    handler.removeMessages(14);
                }
                progressDialog.dismiss();
            }
            ToastUtils.showToast(exception.getMessage());
        }
    };
    Dialog progressDialog;
    private void showRevokeDialog(){
        progressDialog = new Dialog(getActivity(),R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog_revoke);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    private void requestRecord(String pageNo, String pageSize) {
        ApiClient.getInstance().getChatMessages(this, mMeetingId, pageNo, pageSize, new OkHttpCallback<BaseBean<ChatMesData>>() {
            @Override
            public void onSuccess(BaseBean<ChatMesData> entity) {
                mTotalPage = entity.getData().getTotalPage();
                if (dataChat.size() == 0) {
                    dataChat = entity.getData().getPageData();
                    for(int i=0; i<dataChat.size();i++){
                        if(dataChat.get(i).getMsgType()==2){
                            dataChat.remove(i);
                        }
                    }
                    initData(dataChat, true);
                    ApiClient.getInstance().postViewLog(mMeetingId,this,expostorStatsCallback);
                } else {
                    for(int i=0; i<entity.getData().getPageData().size();i++){
                        if(entity.getData().getPageData().get(i).getMsgType()==2){
                            entity.getData().getPageData().remove(i);
                        }
                    }
                    dataChat.addAll(0, entity.getData().getPageData());
                    proBar.setVisibility(View.GONE);
                    initData(dataChat, false);
//                    dataChat.addAll();
                }


            }

            @Override
            public void onFinish() {
                super.onFinish();
//                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void initRecy(){
        mLayoutManager = new LinearLayoutManager(getActivity()); // 解决快速长按焦点丢失问题.
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewChat.addItemDecoration(new SpaceItemDecoration(0, 0, 0, (int) (getResources().getDimension(R.dimen.my_px_36))));
        recyclerViewChat.setLayoutManager(mLayoutManager);
        recyclerViewChat.setFocusable(false);

        recyclerViewChat.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItemPosition;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == 0
                        && !((mPageNo - 1) == mTotalPage) && proBar.getVisibility() == View.GONE) {
                    Log.v("onscrolllistener", "进入停止状态==" + lastVisibleItemPosition);
                    proBar.setVisibility(View.VISIBLE);
                    requestRecord(false);
//                    requestLiveVideoListNext();
                    if (mPageNo != -1 && mTotalPage != -1 && !(mPageNo == mTotalPage)) {
//                        requestRecord(String.valueOf(mPageNo + 1), "20");
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (onCreate) {
                    onCreate = false;
                } else {
                    lastVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                    Log.v("onscrolllistener", "lastvisibleitemp==" + lastVisibleItemPosition);
                    if (lastVisibleItemPosition == 0) {
//                        requestRecord(false);
                    }
                }


            }
        });
    }
    private void requestRecord(boolean last) {
        requestRecord(mPageNo + "", "10");
        mPageNo = mPageNo + 1;
    }
    private void initData(List<ChatMesData.PageDataEntity> data, boolean last) {
        adapter = new InMeetingAdapter(getActivity(), data, this);
        recyclerViewChat.setAdapter(adapter);

//        if (last) {
//            if (handler.hasMessages(10)) {
//                handler.removeMessages(10);
//            }
//            handler.sendEmptyMessageDelayed(10, 100);
//        }
    }

    @Override
    public void onItemClick(int pos) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}

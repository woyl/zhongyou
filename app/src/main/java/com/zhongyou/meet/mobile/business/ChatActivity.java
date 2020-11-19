/*
package com.zhongyou.meet.mobile.business;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongyou.meet.mobile.R;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

*/
/**
 * @author Dongce
 *----------------------
 *fixed by luopan
 *   toolbar的颜色
 *----------------------
 * *//*

public class ChatActivity extends BasicActivity implements TakePhoto.TakeResultListener, InvokeListener {
    private Button btn;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private LinearLayout llBack;
    private TextView tvTitle, tvNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_chat);

//
        llBack = (LinearLayout)this.findViewById(R.id.ll_back);
        tvTitle = (TextView)this.findViewById(R.id.tv_title) ;
        tvNum = (TextView)this.findViewById(R.id.tv_num) ;
        tvTitle.setText(getIntent().getStringExtra("title"));
        tvNum.setText(getIntent().getIntExtra("num",0)+"人");

        llBack.setOnClickListener(view -> finish());
        initFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState, outPersistentState);
//        getTakePhoto();
    }


    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }
    private void initFragment(){
        ChatFragment fragment = ChatFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.rl_content, fragment).show(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
    @Override
    public String getStatisticsTag() {
        return "聊天室";
    }

    @Override
    public void takeSuccess(TResult result) {

    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
*/

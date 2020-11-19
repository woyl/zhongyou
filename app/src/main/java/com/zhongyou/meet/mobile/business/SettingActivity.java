package com.zhongyou.meet.mobile.business;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.RxBus;
import com.zhongyou.meet.mobile.ApiClient;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.entities.Version;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.event.ResolutionChangeEvent;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.ApkUtil;
import com.zhongyou.meet.mobile.utils.Common;
import com.zhongyou.meet.mobile.utils.OkHttpCallback;
import com.zhongyou.meet.mobile.utils.Utils;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;

import io.agora.openlive.model.ConstantApp;
import io.agora.openlive.ui.MainActivity;

/**
 * Created by whatisjava on 17-9-5.
 */

public class SettingActivity extends BasicActivity {

    private SharedPreferences pref;
    private TextView mNewVersionFlag;

    @Override
    public String getStatisticsTag() {
        return "设置";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        pref = PreferenceManager.getDefaultSharedPreferences(this);

        // 设置标题和返回键
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.title)).setText(getStatisticsTag().toString());

        findViewById(R.id.layout_ver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,VersionActivity.class));
            }
        });


        findViewById(R.id.layout_fbl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });


        ((TextView) findViewById(R.id.label_version_num)).setText("V"+BuildConfig.VERSION_NAME);
        mNewVersionFlag = findViewById(R.id.label_version_new);

        findViewById(R.id.layout_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.clear();
                AppManager.getAppManager().startActivity(WXEntryActivity.class);
                finish();
            }
        });

        findViewById(R.id.layout_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.deleteFileByPath(MainActivity.getRoot(SettingActivity.this))){
                    Utils.showSimpleAlertDialog(SettingActivity.this, "所有文件已删除");
                } else{
                    Utils.showSimpleAlertDialog(SettingActivity.this, "文件删除失败");
                }
            }
        });

        versionCheck();

        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
        ((TextView) findViewById(R.id.resolution_text)).setText(resolutionText(prefIndex));
    }

    private void versionCheck() {
        ApiClient.getInstance().versionCheck(this, new OkHttpCallback<BaseBean<Version>>() {
            @Override
            public void onSuccess(BaseBean<Version> entity) {
                Version version = entity.getData();

               /* if (version.getImportance() != 1 &&version.getImportance()!=0) {
                    mNewVersionFlag.setVisibility(View.VISIBLE);
                } else {
                    mNewVersionFlag.setVisibility(View.GONE);
                }*/

               if (version!=null){
                   try {
                       if (ApkUtil.compareVersion(version.getVersionDesc(),BuildConfig.VERSION_NAME)>0){
                           mNewVersionFlag.setVisibility(View.VISIBLE);
                       }else {
                           mNewVersionFlag.setVisibility(View.GONE);
                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
            }

            @Override
            public void onFailure(int errorCode, BaseException exception) {
                super.onFailure(errorCode, exception);
                Toast.makeText(mContext, "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    String[] items = new String[]{"流畅（480x320）", "标准（640x480）", "高清（1280x720）"};

    private void showSelectDialog() {
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("分辨率设置");
        builder.setSingleChoiceItems(items, prefIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, which);
                editor.apply();

                ResolutionChangeEvent resolutionChangeEvent = new ResolutionChangeEvent();
                resolutionChangeEvent.setResolution(pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX));
                RxBus.sendMessage(resolutionChangeEvent);

                dialog.dismiss();
                ((TextView) findViewById(R.id.resolution_text)).setText(resolutionText(which));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String resolutionText(int which) {
        switch (which) {
            case 0:
                return "流畅";
            case 1:
                return "标准（推荐）";
            case 2:
                return "高清";
            default:
                return "标准（推荐）";
        }
    }

}

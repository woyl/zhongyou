package com.zhongyou.meet.mobile.ameeting.network;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.zhongyou.meet.mobile.R;


/**
 * Created by lichuanbei on 2016/12/5.
 */

public class LoadingDialog {
    private static AlertDialog mAlertDialog;
    private static boolean isShow = true;

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static AlertDialog createLoadingDialog(Context context, String msg) {
        // 首先得到整个View
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog_view, null);

        ImageView  imageView = (ImageView) view.findViewById(R.id.iv_quan);

        Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        imageView.startAnimation(operatingAnim);
        // 页面中显示文本
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        // 显示文本
        tvMessage.setText(msg);
        mAlertDialog = new AlertDialog.Builder(context, R.style.dialog).create();
        mAlertDialog.show();
        mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(mAlertDialog!=null)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mAlertDialog.isShowing()) {
                        return isShow;
                    }
                }
                return false;
            }
        });
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(true);
        mAlertDialog.setContentView(view);
        return mAlertDialog;
    }

    /**
     * 显示Dialog
     */
    public static void showDialog(Context context) {
        //每次重建靠谱
        mAlertDialog = LoadingDialog.createLoadingDialog(context, "Loading");
    }

    /**
     * 关闭Dialog
     */
    public static void closeDialog() {
        if (mAlertDialog != null) {
            if (mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
            mAlertDialog = null;
        }
    }
}

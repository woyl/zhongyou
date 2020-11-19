package com.zaaach.citypicker.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by meixi on 2016/11/26. 16:16
 * 邮箱 1085220040@qq.com
 */
public class MyShareUtil {

    private static SharedPreferences sp;
    private static Context mContext;
    // preferences.edit().clear().commit();  清空数据
    private static void initContext(Context context) {
        if (mContext == null){
            mContext = context;
            sp= mContext.getSharedPreferences("lg", 0);
        }

    }

    public static String getSharedString(Context context, String key){
        initContext(context);
        String s = sp.getString(key,"");
        return s;
    }
}

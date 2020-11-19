package com.zhongyou.meet.mobile.utils;

import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

/**
 * title 数据存储工具类
 * author heyan
 * time 2020/11/18 16:50
 * desc 方便以后统一储存入口
 */
public class DataUtil {

    private static final String DATA_MMKV = "data_mmkv";

    public static MMKV getMMKV() {
        return MMKV.mmkvWithID(DATA_MMKV);
    }

    /**
     * 获取数据
     */
    public static String getStr(String key) {
        if (TextUtils.isEmpty(key)) return null;
        return getMMKV().decodeString(key);
    }

    /**
     * 保存数据
     */
    public static void putStr(String key, String value) {
        if (TextUtils.isEmpty(key)) return;
        if (TextUtils.isEmpty(value)) {
            getMMKV().remove(key);
        } else {
            getMMKV().encode(key, value);
        }
    }


//    public static boolean getBool(String key) {
//        if (TextUtils.isEmpty(key)) return false;
//        return getMMKV().decodeBool(key);
//    }
//
//    public static void putBool(String key, boolean value) {
//        if (TextUtils.isEmpty(key)) return;
//        if (TextUtils.isEmpty(value)) {
//            getMMKV().remove(key);
//        } else {
//            getMMKV().encode(key, value);
//        }
//    }


    /**
     * 清除所有数据，慎用
     */
    @Deprecated
    public static void clear(){

    }
}

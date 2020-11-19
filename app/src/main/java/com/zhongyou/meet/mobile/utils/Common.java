package com.zhongyou.meet.mobile.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class Common {

    public static Long getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2="";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                if(str2.indexOf("MemTotal") != -1) {
                    str2 = str2.substring(str2.indexOf(":") + 1).trim();
                    str2 = str2.substring(0, str2.indexOf(" ")).trim();
                    return Long.parseLong(str2) * 1024;
                }
            }
        } catch (IOException e) {
        }
        return Long.parseLong("0");
    }

    /**
     * 获取可用内存
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    /**
     * 获取CPU信息
     * @return
     */
    public static String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2="";
        String[] cpuInfo={"",""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }

    /**
     * 格式化数字
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        String suffix = null;
        float fSize=0;
        if (size >= 1024) {
            suffix = "KB";
            fSize=size / 1024;
            if (fSize >= 1024) {
                suffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                suffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = size;
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    /**
     * 删除目录下所有文件
     * @param path
     * @return
     */
    public static boolean deleteFileByPath(String path) {
        File file = new File(path);
        if(file.isFile())
            file.delete();

        if(file.isDirectory()) {
            for(String subfile : file.list()) {
                deleteFileByPath(String.format(Locale.CHINA, "%s%s/", path, subfile));
            }

            if(file.list().length == 0)
                file.delete();
        }

        return true;
    }

}

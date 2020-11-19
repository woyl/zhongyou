package com.zhongyou.meet.mobile.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.utils.statistics.ZYAgent;
import com.yunos.baseservice.impl.YunOSApiImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by whatisjava on 16-12-2.
 * 在程序安装后第一次运行时生成一个ID，该方式和设备唯一标识不一样，不同的应用程序会产生不同的ID，
 * 同一个程序重新安装也会不同。可以说是用来标识每一份应用程序的唯一ID（即Installtion ID）
 * ，可以用来跟踪应用的安装数量等（其实就是UUID）。
 */

public class Installation {
    public static final String TAG = "DeviceUUID";
    private static String deviceUUID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String id(Context context) {
        if (deviceUUID == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File externalStorageFile = new File(Environment.getExternalStorageDirectory(), INSTALLATION);
                try {
                    if (!externalStorageFile.exists()) {
                        writeInstallationFile(context, externalStorageFile);
                    }
                    deviceUUID = readInstallationFile(externalStorageFile);

                } catch (Exception e) {
                    ZYAgent.onEvent(context, "external device uuid get failed " + e.getMessage());
                }
            } else {
                File internalStorageFile = new File(context.getFilesDir(), INSTALLATION);
                try {
                    if (!internalStorageFile.exists()) {
                        writeInstallationFile(context, internalStorageFile);
                    }
                    deviceUUID = readInstallationFile(internalStorageFile);

                } catch (Exception e) {

                    ZYAgent.onEvent(context, "internal device uuid get failed " + e.getMessage());
                }
            }
        }

        return deviceUUID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        String devicesUUID = new String(bytes);
        Preferences.setUUID(deviceUUID);
        return devicesUUID;
    }

    private static void writeInstallationFile(Context context, File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        YunOSApiImpl api = new YunOSApiImpl(context);
        String deviceUUID = api.getCloudUUID();
        if (TextUtils.isEmpty(deviceUUID) || "false".equals(deviceUUID)) {
            deviceUUID = UUID.randomUUID().toString().replace("-", "");
        }
        Preferences.setUUID(deviceUUID);
        out.write(deviceUUID.getBytes());
        out.close();
    }

    public static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {

            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    if (str.contains("Serial")) {
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        cpuAddress = strCPU.trim();

                        break;
                    }
                } else {

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuAddress;
    }

}

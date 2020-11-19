package com.zhongyou.meet.mobile.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Utils {

	/**
	 * 获取ApiKey
	 *
	 * @param context
	 * @param metaKey
	 * @return
	 */
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return apiKey;
	}

	/**
	 * 验证手机号码格式是否正确
	 *
	 * @param phone
	 * @return
	 */
	public static boolean isMobile(String phone) {
		String pattern = "^1([3456789])\\d{9}$";
		return Pattern.matches(pattern, phone);
	}

	/**
	 * JSON对象转map
	 *
	 * @param json
	 * @return
	 */
	public static Map<String, String> jsonToMap(JSONObject json) {
		Map<String, String> map = new HashMap<>();
		Iterator<String> keyIter = json.keys();
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			try {
				Object obj = json.get(key);
				if (obj == null)
					map.put(key, "");
				else
					map.put(key, String.valueOf(obj));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * Map转JSON
	 *
	 * @param map
	 * @return
	 */
	public static JSONObject mapToJson(Map<String, String> map) {
		JSONObject json = new JSONObject();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			try {
				json.put(key, map.get(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 进行SHA1加密
	 *
	 * @param str
	 * @return
	 */
	public static String sha1(String str) {
		MessageDigest sha = null;
		try {
			sha = MessageDigest.getInstance("SHA");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}

		try {
			byte[] byteArray = str.getBytes("UTF-8");
			byte[] md5Bytes = sha.digest(byteArray);
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 显示一个简单提示
	 *
	 * @param context
	 * @param info
	 */
	public static void showSimpleAlertDialog(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出一个提示窗口
	 *
	 * @param context
	 * @param title
	 * @param desc
	 * @param button1
	 * @param button2
	 * @param onClickListener1
	 * @param onClickListener2
	 */
	public static void showAlertDialog(Context context, String title, String desc, String button1, String button2, final View.OnClickListener onClickListener1, final View.OnClickListener onClickListener2) {
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(desc)
				.setPositiveButton(button1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onClickListener1.onClick(null);
					}
				})
				.setNegativeButton(button2, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (onClickListener2 != null)
							onClickListener2.onClick(null);
					}
				})
				.show();
	}

	/**
	 * 弹出一个输入框
	 *
	 * @param context
	 * @param title
	 * @param onClickListener
	 */
	public static void showInputDialog(Context context, String title, final View.OnClickListener onClickListener) {
		showInputDialog(context, title, "", onClickListener);
	}

	/**
	 * 弹出一个输入框
	 *
	 * @param context
	 * @param title
	 * @param defaultValue
	 * @param onClickListener
	 */
	public static void showInputDialog(Context context, String title, String defaultValue, final View.OnClickListener onClickListener) {
		final EditText edit = new EditText(context);
		edit.setSingleLine(true);
		if (!defaultValue.equals(""))
			edit.setText(defaultValue);
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(edit)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onClickListener.onClick(edit);
					}
				})
				.setNegativeButton("取消", null)
				.show();
	}

	/**
	 * 弹出一个输入框
	 *
	 * @param context
	 * @param title
	 * @param defaultValue
	 * @param onClickListener
	 * @param onNeutralClickListener
	 */
	public static void showInputDialog(Context context, String title, String defaultValue, final View.OnClickListener onClickListener, final View.OnClickListener onNeutralClickListener) {
		final EditText edit = new EditText(context);
		edit.setSingleLine(true);
		if (!defaultValue.equals(""))
			edit.setText(defaultValue);
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(edit)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onClickListener.onClick(edit);
					}
				})
				.setNeutralButton("旁听", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (onNeutralClickListener != null)
							onNeutralClickListener.onClick(edit);
					}
				})
				.setNegativeButton("取消", null)
				.show();
	}

	/**
	 * 获取网络状态是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean getNetWorkState(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) return true;
		else return false;
	}

	/**
	 * 通过是否支持童话功能来判断当前设置是否为Pad设备
	 *
	 * @param activity
	 * @return
	 */
	public static boolean isPad(Activity activity) {
		TelephonyManager telephony = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取当前时间
	 *
	 * @return
	 */
	public static String getDateTime() {
		return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date());
	}

	/**
	 * 将格式化时间转换为长整型
	 *
	 * @param time
	 * @return
	 */
	public static Long getDateTime(String time) {
		return getDateTime(time, "yyyy-MM-dd hh:mm:ss");
	}

	/**
	 * 将格式化时间转换为长整型
	 *
	 * @param time
	 * @param format
	 * @return
	 */
	public static Long getDateTime(String time, String format) {
		try {
			Date date = (new SimpleDateFormat(format)).parse(time);
			return date.getTime();
		} catch (ParseException e) {
			return new Long(0);
		}
	}

	/**
	 * 权限检查
	 *
	 * @param context
	 * @param permission
	 * @return
	 */
	public static boolean checkSelfPermission(Context context, String permission) {
		if (android.os.Build.VERSION.SDK_INT >= 23) {
			if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 300);
				return false;
			}
			return true;
		} else {
			return true;
		}
	}

	/**
	 * /**
	 * 记录日志
	 *
	 * @param format
	 * @param args
	 */
	public synchronized static void wlog(String format, Object... args) {
		StackTraceElement[] eles = Thread.currentThread().getStackTrace();
		wlog(eles[3].getClassName(), eles[3].getMethodName(), format, args);
	}


	/**
	 * 写入日志到文件
	 *
	 * @param level
	 * @param info
	 */
	public synchronized static void wlog2file(String level, String info) {
		StringBuffer sb = new StringBuffer();
		sb.append("Level : ").append(level).append("\r\n");
		sb.append("Time : ").append(getDateTime()).append("\r\n");
		sb.append(info);
		sb.append("\r\n\r\n");

		try {
			String file = String.format(Locale.CHINA, "%s/Android/Log", Environment.getExternalStorageDirectory().getAbsolutePath());
			if (!new File(file).exists())
				new File(file).mkdirs();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			file = String.format(Locale.CHINA, "%s/%s.txt", file, sdf.format(new Date()));
			FileWriter fw = new FileWriter(file, true);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断qq是否可用
	 *
	 * @param context
	 * @return
	 */
	/**
	 * 判断 用户是否安装QQ客户端
	 */
	public static boolean isQQClientAvailable(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *判断是否安装新浪微博
	 **/
	public static boolean isSinaInstalled(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.sina.weibo")) {
					return true;
				}
			}
		}

		return false;
	}


	/**
	 * 检测是否安装支付宝
	 *
	 * @param context
	 * @return
	 */
	public static boolean isAliPayInstalled(Context context) {
		Uri uri = Uri.parse("alipays://platformapi/startApp");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		ComponentName componentName = intent.resolveActivity(context.getPackageManager());
		return componentName != null;
	}


}

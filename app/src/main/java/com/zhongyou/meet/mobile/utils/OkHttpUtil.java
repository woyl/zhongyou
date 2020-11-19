package com.zhongyou.meet.mobile.utils;

/**
 * Created by whatisjava on 17-1-3.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.BaseException;
import com.zhongyou.meet.mobile.BuildConfig;
import com.zhongyou.meet.mobile.Constant;
import com.zhongyou.meet.mobile.entities.base.BaseBean;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.wxapi.WXEntryActivity;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtil {
	private static OkHttpUtil okHttpUtil;
	private static OkHttpClient okHttpClient;
	private Handler mHandler;

	public static Gson getGson() {
		return gson;
	}

	private static Gson gson;
	private Context mContext;

	private OkHttpUtil() {
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

		clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
		clientBuilder.readTimeout(10, TimeUnit.SECONDS);
		clientBuilder.writeTimeout(30, TimeUnit.SECONDS);

		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		clientBuilder.addNetworkInterceptor(interceptor);

		okHttpClient = clientBuilder.build();

		mHandler = new Handler(Looper.getMainLooper());
		mContext = BaseApplication.getInstance();
	}

	public static OkHttpUtil getInstance() {
		if (okHttpUtil == null) {
			synchronized (OkHttpUtil.class) {
				if (okHttpUtil == null) {
					okHttpUtil = new OkHttpUtil();
					gson = new Gson();
				}
			}
		}
		return okHttpUtil;
	}

	private Call call;
	private int count = 0;


	private void request(final Request request, final OkHttpCallback callback) {

		if (!NetUtils.isNetworkConnected(mContext)) {
			callbackFailure(-5, callback, new BaseException("当前无网络连接 请检查网络"));
			return;
		}

		callback.onStart();

		call = okHttpClient.newCall(request);
		if (call.isExecuted() || call.isCanceled()) {
			return;
		}
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callbackFailure(-1, callback, new BaseException(e.getMessage(), e));
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.code() == 502) {
					BaseException exception = new BaseException("服务器502,请稍后重试");
					callbackFailure(-2, callback, exception);
					return;
				}
				if (response.body() != null) {
					String resString = response.body().string();
					Log.v("api response", resString);
					if (!TextUtils.isEmpty(resString)) {
						try {
							BaseBean baseBean = gson.fromJson(resString, BaseBean.class);
							if (baseBean.isSuccess()) {
								Object object = gson.fromJson(resString, callback.mType);
								callbackSuccess(object, callback);
							} else if (baseBean.isTokenError()) {
								if (count != 0) {
									return;
								}
								count++;
								mContext.sendBroadcast(new Intent(BuildConfig.APPLICATION_ID + Constant.RELOGIN_ACTION).putExtra("active", false));
								Preferences.clear();
								callbackFailure(baseBean.getErrcode(), callback, new BaseException(baseBean.getErrmsg()));
								mContext.startActivity(new Intent(mContext, WXEntryActivity.class).putExtra("isBadToken", true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

							} else {
								if (response.code() == 400) {
									callbackFailure(-2, callback, new BaseException(baseBean.getErrmsg()));
								} else if (baseBean.getErrcode() == 5001 || baseBean.getErrcode() == 9010) {
									callbackFailure(response.code(), callback, new BaseException(baseBean.getErrmsg()));
								} else {
									callbackFailure(response.code(), callback, new BaseException(baseBean.getErrmsg()));

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							callbackFailure(response.code(), callback, new BaseException(e.getMessage()));

						}
					} else {
						BaseException exception = new BaseException("服务器返回数据异常 请稍后再试");
						callbackFailure(-2, callback, exception);
//                        ToastUtils.showToast("服务器返回数据异常 请稍后再试");
					}

				} else {
					BaseException exception = new BaseException("服务器返回数据异常 请稍后再试");
//                    ToastUtils.showToast("服务器返回数据异常 请稍后再试");
					callbackFailure(-2, callback, exception);
				}
			}
		});
	}

	private void callbackSuccess(final Object o, final OkHttpCallback callback) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				callback.onSuccess(o);
				callback.onFinish();
			}
		});
	}

	private void callbackFailure(final int code, final OkHttpCallback callback, final BaseException e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (code == -5) {
					if (e != null && e.getMessage() != null) {
						ToastUtils.showToast(e.getMessage());
					}
					return;
				}
				callback.onFailure(code, e);
				callback.onFinish();
			}
		});
	}

	private void runOnUiThread(Runnable task) {
		mHandler.post(task);
	}


	public void get(String url, Map<String, String> headers, Map<String, String> params, OkHttpCallback callback) {
		Request request = buildRequest(jointUrl(url, params), headers, null, null, HttpMethodType.GET);
		request(request, callback);
	}


	public void get(String url, Map<String, String> headers, Map<String, String> params, OkHttpCallback callback, Object tag) {
		Request request = buildRequest(jointUrl(url, params), headers, null, null, HttpMethodType.GET, tag);
		request(request, callback);
	}

	public void post(String url, Map<String, String> headers, Map<String, String> params, OkHttpCallback callback) {
		Request request = buildRequest(url, headers, params, null, HttpMethodType.POST);
		request(request, callback);
	}

	public void postJson(String url, Map<String, String> headers, String jsonStr, OkHttpCallback callback, Object tag) {
		Request request = buildRequest(url, headers, null, jsonStr, HttpMethodType.POST_JSON);
		request(request, callback);
	}

	public void put(String url, Map<String, String> headers, Map<String, String> params, OkHttpCallback callback) {
		Request request = buildRequest(url, headers, params, null, HttpMethodType.PUT);
		request(request, callback);
	}

	public void delete(String url, Map<String, String> headers, Map<String, String> params, OkHttpCallback callback) {
		Request request = buildRequest(url, headers, params, null, HttpMethodType.DELETE);
		request(request, callback);
	}

	//-----------------------------------------  get请求 -----------------------------------------

	/**
	 * get请求
	 */
	public void get(String url, Object tag, OkHttpCallback callback) {
		Request request = buildRequest(url, null, null, null, HttpMethodType.GET, tag);
		request(request, callback);
	}

	public void putJson(String url, Map<String, String> headers, String jsonStr, OkHttpCallback callback, Object tag) {
		Request request = buildRequest(url, headers, null, jsonStr, HttpMethodType.PUT_JSON, tag);
		request(request, callback);
	}

	/**
	 * get请求_使用map设置请求
	 */
	public void get(String url, Map<String, String> params, Object tag, OkHttpCallback callback) {
		get(generateUrlString(url, params), tag, callback);
	}


	/**
	 * 用来拼接get请求的url地址,做了URLENCODE
	 *
	 * @param url    请求的链接
	 * @param params 各种参数
	 * @return 拼接完成的链接
	 */
	public static String generateUrlString(String url, Map<String, String> params) {
		if (params != null && params.size() > 0) {
			Uri uri = Uri.parse(url);
			Uri.Builder b = uri.buildUpon();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				b.appendQueryParameter(entry.getKey(), entry.getValue());
			}
			return b.build().toString();
		}
		return url;
	}

	private Request buildRequest(String url, Map<String, String> headers, Map<String, String> params, String jsonStr, HttpMethodType type) {
		return buildRequest(url, headers, params, jsonStr, type, null);
	}

	private Request buildRequest(String url, Map<String, String> headers, Map<String, String> params, String jsonStr, HttpMethodType type, Object tag) {
		Request.Builder builder = new Request.Builder();
		addHeader(headers, builder);
		builder.url(url);
		if (tag != null) {
			builder.tag(tag);
		}
		if (type == HttpMethodType.GET) {
			builder.get();
		} else if (type == HttpMethodType.POST) {
			builder.post(buildRequestBody(params));
		} else if (type == HttpMethodType.POST_JSON) {
			builder.post(buildRequestBody(jsonStr));
		} else if (type == HttpMethodType.POST_FILE) {
			builder.post(buildMultipartRequestBody(params));
		} else if (type == HttpMethodType.PUT) {
			builder.put(buildRequestBody(params));
		} else if (type == HttpMethodType.PUT_JSON) {
			builder.put(buildRequestBody(jsonStr));
		} else if (type == HttpMethodType.DELETE) {
			builder.delete(buildRequestBody(params));
		} else if (type == HttpMethodType.DELETE_JSON) {
			builder.delete(buildRequestBody(gson.toJson(params)));
		}
		return builder.build();
	}

	public static String jointUrl(String url, Map<String, String> params) {
		if (params != null && params.size() > 0) {
			Uri uri = Uri.parse(url);
			Uri.Builder b = uri.buildUpon();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				b.appendQueryParameter(entry.getKey(), entry.getValue());
			}
			return b.build().toString();
		}
		return url;
	}

	private Request.Builder addHeader(Map<String, String> headers, Request.Builder builder) {
		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				builder.addHeader(entry.getKey(), entry.getValue());
			}
			return builder;
		}
		return builder;
	}

	/**
	 * 通过Map的键值对构建请求对象的body
	 *
	 * @param params Map
	 * @return RequestBody
	 */
	private RequestBody buildRequestBody(Map<String, String> params) {
		FormBody.Builder builder = new FormBody.Builder();
		if (params != null) {
			for (Map.Entry<String, String> entity : params.entrySet()) {
				if (entity == null || entity.getValue() == null) {
					continue;
				}
				builder.add(entity.getKey(), entity.getValue());
			}
		}
		return builder.build();
	}

	/**
	 * 通过json字符串创建请求对象的body
	 *
	 * @param jsonString String
	 * @return RequestBody
	 */
	private RequestBody buildRequestBody(String jsonString) {
		if (!TextUtils.isEmpty(jsonString)) {
			return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
		} else {
			return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{}");
		}
	}

	private MultipartBody buildMultipartRequestBody(Map<String, String> params) {
		MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		if (params != null) {
			for (Map.Entry<String, String> entity : params.entrySet()) {
				multipartBuilder.addFormDataPart(entity.getKey(), entity.getValue());
			}
		}
		return multipartBuilder.build();
	}

	/**
	 * 这个枚举用于指明是哪一种提交方式
	 */
	private enum HttpMethodType {
		GET,
		POST,
		POST_JSON,
		POST_FILE,
		PUT,
		PUT_JSON,
		DELETE,
		DELETE_JSON
	}

	/**
	 * 根据Tag取消请求
	 *
	 * @param tag
	 */
	public void cancelTag(Object tag) {
		for (Call call : okHttpClient.dispatcher().queuedCalls()) {
			if (tag.equals(call.request().tag())) {
				call.cancel();
			}
		}
		for (Call call : okHttpClient.dispatcher().runningCalls()) {
			if (tag.equals(call.request().tag())) {
				call.cancel();
			}
		}
	}

}

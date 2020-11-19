package com.zhongyou.meet.mobile.ameeting.whiteboard;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by buhe on 2018/8/16.
 */

public class DemoAPI {

	static final MediaType JSON
			= MediaType.parse("application/json; charset=utf-8");
	public static final String sdkToken = "NETLESSSDK_YWs9VE04V0tEdFNhakVqVnY2NiZub25jZT0xNTg5Nzk2ODk1ODA0MDAmcm9sZT0wJnNpZz05OGJmYTA0MmQyMTg3MWQ2NjhhNzM3MjljMzYyNjBkMjUxM2M4YTU1NTVmNzMxMTQ0Y2ExZDEzNWZjOTZkYTVm";
	private static final String host = "https://cloudcapiv4.herewhite.com";
	private String demoUUID = "";
	private String demoRoomToken = "";

	String getDemoUUID() {
		return demoUUID;
	}

	private OkHttpClient client = new OkHttpClient();
	private Gson gson = new Gson();

	boolean hasDemoInfo() {
		return demoUUID.length() > 0 && demoRoomToken.length() > 0;
	}

	boolean validateToken() {
		return hasDemoInfo() || sdkToken.length() > 100;
	}

	public interface Result {
		void success(String uuid, String roomToken);

		void fail(String message);
	}

	public void getNewRoom(final Result result) {

		/*if (hasDemoInfo()) {
			result.success(demoUUID, demoRoomToken);
			return;
		}*/

		createRoom(100, new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				result.fail("网络请求错误：" + e.toString());
			}

			@Override
			public void onResponse(Call call, Response response) {
				try {
					if (response.code() == 200) {
						JsonObject room = gson.fromJson(response.body().string(), JsonObject.class);
						String uuid = room.getAsJsonObject("msg").getAsJsonObject("room").get("uuid").getAsString();
						String roomToken = room.getAsJsonObject("msg").get("roomToken").getAsString();
						result.success(uuid, roomToken);
					} else {
						assert response.body() != null;
						result.fail("创建房间失败：" + response.body().string());
					}
				} catch (Throwable e) {
					result.fail("网络请求错误：" + e.toString());
				}
			}
		});
	}

	private void createRoom(int limit, Callback callback) {

		Map<String, Object> roomSpec = new HashMap<>();
		roomSpec.put("name", "Android test room");
		roomSpec.put("limit", limit);
		roomSpec.put("mode", "historied");

		RequestBody body = RequestBody.create(JSON, gson.toJson(roomSpec));

		Request request = new Request.Builder()
				.url(host + "/room")
				.addHeader("token", sdkToken)
				.post(body)
				.build();

		Call call = client.newCall(request);
		call.enqueue(callback);
	}

	public void getRoomToken(final String uuid, final Result result) {

		/*if (uuid.equals(demoUUID)) {
			result.success(demoUUID, demoRoomToken);
			return;
		}
*/
		Map<String, Object> roomSpec = new HashMap<>();

		RequestBody body = RequestBody.create(JSON, gson.toJson(roomSpec));
		Request request = new Request.Builder()
				.url(host + "/room/join?uuid=" + uuid)
				.addHeader("token", sdkToken)
				.post(body)
				.build();
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				result.fail("网络请求错误：" + e.toString());
			}

			@Override
			public void onResponse(Call call, Response response) {
				try {
					if (response.code() == 200) {
						assert response.body() != null;
						JsonObject room = gson.fromJson(response.body().string(), JsonObject.class);
						String roomToken = room.getAsJsonObject("msg").get("roomToken").getAsString();
						result.success(uuid, roomToken);
					} else {
						assert response.body() != null;
						result.fail("获取房间 token 失败：" + response.body().string());
					}
				} catch (Throwable e) {
					result.fail("网络请求错误：" + e.toString());
				}
			}
		});
	}
}

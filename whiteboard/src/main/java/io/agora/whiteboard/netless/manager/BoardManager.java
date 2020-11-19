package io.agora.whiteboard.netless.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.herewhite.sdk.Converter;
import com.herewhite.sdk.ConverterCallbacks;
import com.herewhite.sdk.Room;
import com.herewhite.sdk.RoomCallbacks;
import com.herewhite.sdk.RoomParams;
import com.herewhite.sdk.WhiteSdk;
import com.herewhite.sdk.domain.ConversionInfo;
import com.herewhite.sdk.domain.ConvertException;
import com.herewhite.sdk.domain.ConvertedFiles;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.RoomPhase;
import com.herewhite.sdk.domain.RoomState;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.Scene;
import com.herewhite.sdk.domain.SceneState;


import io.agora.whiteboard.netless.annotation.Appliance;
import io.agora.whiteboard.netless.listener.BoardEventListener;

public class BoardManager extends NetlessManager<Room> implements RoomCallbacks {
	final String EVENT_NAME = "WhiteCommandCustomEvent";

	final String SCENE_DIR = "/dir";
	final String ROOM_INFO = "room info";
	final String ROOM_ACTION = "room action";

	private final LogManager log = new LogManager(this.getClass().getSimpleName());

	private String appliance;
	private int[] strokeColor;
	private Boolean disableDeviceInputs;
	private Boolean disableCameraTransform;
	private Boolean writable;
	private RoomParams mRoomParams;

	private Handler handler = new Handler(Looper.getMainLooper());
	private BoardEventListener listener;

	public void setListener(BoardEventListener listener) {
		this.listener = listener;
	}

	public void init(WhiteSdk sdk, RoomParams params) {
		log.e("init");
		this.mRoomParams = params;
		sdk.joinRoom(params, this, promise);
	}

	public void setAppliance(@Appliance String appliance) {
		if (t != null) {
			MemberState state = new MemberState();
			state.setCurrentApplianceName(appliance);
			t.setMemberState(state);
		} else {
			this.appliance = appliance;
		}
	}

	public String getAppliance() {
		if (t != null) {
			return t.getMemberState().getCurrentApplianceName();
		}
		return null;
	}

	public void setStrokeColor(int[] color) {
		if (t != null) {
			MemberState state = new MemberState();
			state.setStrokeColor(color);
			t.setMemberState(state);
		} else {
			this.strokeColor = color;
		}
	}

	public int[] getStrokeColor() {
		if (t != null) {
			t.getMemberState().getStrokeColor();
		}
		return null;
	}

	public void setSceneIndex(int index) {
		if (t != null && !isDisableDeviceInputs()) {
			t.setSceneIndex(index, new Promise<Boolean>() {
				@Override
				public void then(Boolean aBoolean) {
				}

				@Override
				public void catchEx(SDKError t) {
				}
			});
		}
	}

	public int getSceneCount() {
		if (t != null) {
			return t.getScenes().length;
		}
		return 0;
	}

	public void pptPreviousStep() {
		if (t != null && !isDisableDeviceInputs()) {
			t.pptPreviousStep();
		}
	}

	public void pptNextStep() {
		Scene[] scenes = t.getScenes();
		Log.e("pptNextStep", "pptNextStep: " + new Gson().toJson(scenes));
		if (t != null && !isDisableDeviceInputs()) {
			t.pptNextStep();
		}
	}

	public void getRoomPhase(Promise<RoomPhase> promise) {
		if (t != null) {
			t.getRoomPhase(promise);
		} else {
			if (promise != null) {
				promise.then(RoomPhase.disconnected);
			}
		}
	}

	public void refreshViewSize() {
		if (t != null) {
			t.refreshViewSize();
		}
	}

	public void addEmptyScenes() {
		if (t != null) {
			Log.e("addEmptyScenes", "addEmptyScenes: " + t.getSceneState().getIndex());
			t.putScenes(SCENE_DIR, new Scene[]{
					new Scene("page" + (t.getSceneState().getIndex() + 1))}, t.getSceneState().getIndex() + 1);

			Log.e("addEmptyScenes", "addEmptyScenes  111111: " + t.getSceneState().getIndex());
			t.setScenePath(SCENE_DIR + "/page" + (t.getSceneState().getIndex() + 1));
		}
	}

	public void putScenes(Scene[] scenes, int index) {
		if (t != null) {
			t.putScenes(SCENE_DIR, scenes, index);
			t.setScenePath(SCENE_DIR + "/page" + index);
		}
	}

	public void disableDeviceInputs(boolean disabled) {
		if (t != null) {
			t.disableDeviceInputs(disabled);
		}
		disableDeviceInputs = disabled;
	}

	public void disableDeviceInputsTemporary(boolean disabled) {
		if (t != null) {
			t.disableDeviceInputs(disabled);
		}
	}

	public boolean isDisableDeviceInputs() {
		return disableDeviceInputs == null ? false : disableDeviceInputs;
	}

	public void disableCameraTransform(boolean disabled) {
		if (t != null) {
			t.disableCameraTransform(disabled);
		}
		disableCameraTransform = disabled;
	}

	public boolean isDisableCameraTransform() {
		return disableCameraTransform == null ? false : disableCameraTransform;
	}

	public void setWritable(boolean writable) {
		if (t != null) {
			t.setWritable(writable, new Promise<Boolean>() {
				@Override
				public void then(Boolean aBoolean) {
				}

				@Override
				public void catchEx(SDKError t) {
				}
			});
		}
		this.writable = writable;
	}

	public void disconnect() {
		if (t != null) {
			t.disconnect();
		}
	}

	@Override
	public void onPhaseChanged(RoomPhase phase) {
		if (listener != null) {
			handler.post(() -> listener.onRoomPhaseChanged(phase));
		}
	}

	@Override
	public void onBeingAbleToCommitChange(boolean isAbleToCommit) {

	}

	@Override
	public void onDisconnectWithError(Exception e) {

	}

	@Override
	public void onKickedWithReason(String reason) {

	}

	@Override
	public void onRoomStateChanged(RoomState modifyState) {
		Log.e("onRoomStateChanged", "onRoomStateChanged: " + new Gson().toJson(modifyState));
		if (listener != null) {
			MemberState memberState = modifyState.getMemberState();
			if (memberState != null) {
				handler.post(() -> listener.onMemberStateChanged(memberState));
			}
			SceneState sceneState = modifyState.getSceneState();
			if (sceneState != null) {
				handler.post(() -> listener.onSceneStateChanged(sceneState));
			}
		}
	}

	@Override
	public void onCatchErrorWhenAppendFrame(long userId, Exception error) {

	}

	@Override
	void onSuccess(Room room) {
		log.i("onSuccess");
		if (appliance != null) {
			setAppliance(appliance);
		}
		if (strokeColor != null) {
			setStrokeColor(strokeColor);
		}
		if (disableDeviceInputs != null) {
			disableDeviceInputs(disableDeviceInputs);
		}
		if (disableCameraTransform != null) {
			disableCameraTransform(disableCameraTransform);
		}
		if (writable != null) {
			setWritable(writable);
		}
		if (listener != null) {
			listener.onSceneStateChanged(room.getSceneState());
		}
		if (mNotFoundListener != null) {
			mNotFoundListener.onRoomJoinSuccess(mRoomParams);
		}

	}

	@Override
	void onFail(SDKError error) {
		log.e("onFail %s", error.toString());

		if (error.toString().trim().contains("not found")) {
			if (mNotFoundListener != null) {
				mNotFoundListener.noFoundRoom();
			}
		}

	}

	public interface NotFoundListener {
		void noFoundRoom();

		void onRoomJoinSuccess(RoomParams roomParams);
	}

	protected NotFoundListener mNotFoundListener;

	public void setNotFoundListener(NotFoundListener notFoundListener) {
		mNotFoundListener = notFoundListener;
	}
}

package com.zhongyou.meet.mobile.ameeting;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.herewhite.sdk.Converter;
import com.herewhite.sdk.ConverterCallbacks;
import com.herewhite.sdk.RoomParams;
import com.herewhite.sdk.WhiteSdk;
import com.herewhite.sdk.WhiteSdkConfiguration;
import com.herewhite.sdk.WhiteboardView;
import com.herewhite.sdk.domain.Appliance;
import com.herewhite.sdk.domain.ConversionInfo;
import com.herewhite.sdk.domain.ConvertException;
import com.herewhite.sdk.domain.ConvertedFiles;
import com.herewhite.sdk.domain.DeviceType;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.RoomPhase;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.SceneState;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.ameeting.whiteboard.ApplianceView;
import com.zhongyou.meet.mobile.ameeting.whiteboard.BaseFragment;
import com.zhongyou.meet.mobile.ameeting.whiteboard.ColorPicker;
import com.zhongyou.meet.mobile.ameeting.whiteboard.ColorUtil;
import com.zhongyou.meet.mobile.ameeting.whiteboard.PageControlView;
import com.zhongyou.meet.mobile.ameeting.whiteboard.ToastManager;


import butterknife.BindView;
import butterknife.OnTouch;
import io.agora.whiteboard.netless.listener.BoardEventListener;
import io.agora.whiteboard.netless.manager.BoardManager;
import timber.log.Timber;

public class WhiteBoardFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, PageControlView.PageControlListener, BoardEventListener {

	@BindView(R.id.white_board_view)
	protected WhiteboardView white_board_view;
	@BindView(R.id.appliance_view)
	protected ApplianceView appliance_view;
	@BindView(R.id.color_select_view)
	protected ColorPicker color_select_view;
	@BindView(R.id.page_control_view)
	protected PageControlView page_control_view;
	@BindView(R.id.pb_loading)
	protected ProgressBar pb_loading;

	private WhiteSdk whiteSdk;
	private BoardManager boardManager = new BoardManager();

	@Override
	protected int getLayoutResId() {
		return R.layout.fragment_white_board;
	}

	@Override
	protected void initData() {
		WhiteSdkConfiguration configuration = new WhiteSdkConfiguration(DeviceType.touch, 10, 0.1);
		whiteSdk = new WhiteSdk(white_board_view, context, configuration);
		boardManager.setListener(this);
	}

	@Override
	protected void initView() {
		white_board_view.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
			boardManager.refreshViewSize();
		});
		appliance_view.setVisibility(boardManager.isDisableDeviceInputs() ? View.GONE : View.VISIBLE);
		appliance_view.setOnCheckedChangeListener(this);
		color_select_view.setChangedListener(color -> {
			appliance_view.check(appliance_view.getApplianceId(boardManager.getAppliance()));
			boardManager.setStrokeColor(ColorUtil.colorToArray(color));
		});
		page_control_view.setListener(this);


	}

	public BoardManager getBoardManager() {
		return boardManager;
	}

	public void initBoardWithRoomToken(String uuid, String roomToken) {
		if (TextUtils.isEmpty(uuid) || TextUtils.isEmpty(roomToken)) return;
		boardManager.getRoomPhase(new Promise<RoomPhase>() {
			@Override
			public void then(RoomPhase phase) {
				if (phase != RoomPhase.connected) {
					pb_loading.setVisibility(View.VISIBLE);
					RoomParams params = new RoomParams(uuid, roomToken);
					boardManager.init(whiteSdk, params);
				}
			}

			@Override
			public void catchEx(SDKError t) {
				ToastManager.showShort(t.getMessage());
			}
		});
	}

	public void disableDeviceInputs(boolean disabled) {
		if (disabled != boardManager.isDisableDeviceInputs()) {
			ToastManager.showShort(disabled ? R.string.revoke_board : R.string.authorize_board);
		}
		if (appliance_view != null) {
			appliance_view.setVisibility(disabled ? View.GONE : View.VISIBLE);
		}
		boardManager.disableDeviceInputs(disabled);
	}

	public void disableCameraTransform(boolean disabled) {
		if (disabled != boardManager.isDisableCameraTransform()) {
			if (disabled) {
				ToastManager.showShort(R.string.follow_tips);
				boardManager.disableDeviceInputsTemporary(true);
			} else {
				boardManager.disableDeviceInputsTemporary(boardManager.isDisableDeviceInputs());
			}
		}
		boardManager.disableCameraTransform(disabled);
	}

	public void setWritable(boolean writable) {
		boardManager.setWritable(writable);
	}

	public void convertPPTX(String roomToken, String pptxPath) {
		//不支持 wps 文件，且 wps 转换为 pptx 后的文件不保证能成功解析
//		boardManager.dynamicConvert(roomToken, pptxPath);
		Converter c = new Converter(roomToken);
		c.startConvertTask("https://white-cn-edge-doc-convert.oss-cn-hangzhou.aliyuncs.com/-1/1.pptx", Converter.ConvertType.Dynamic, new ConverterCallbacks() {
			@Override
			public void onFailure(ConvertException e) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pb_loading.setVisibility(View.GONE);
						Toast.makeText(getActivity(),"加载PPT错误",Toast.LENGTH_LONG).show();
						Log.e("dynamicConvert", "onFailure: "+e.getMessage() );
					}
				});

			}

			@Override
			public void onFinish(ConvertedFiles ppt, ConversionInfo convertInfo) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						boardManager.putScenes( ppt.getScenes(), 0);
						pb_loading.setVisibility(View.GONE);
					}
				});

			}

			@Override
			public void onProgress(Double progress, ConversionInfo convertInfo) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pb_loading.setVisibility(View.VISIBLE);
					}
				});

			}
		});
	}

	public void releaseBoard() {
		boardManager.disconnect();
	}

	@OnTouch(R.id.white_board_view)
	boolean onTouch(View view, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			white_board_view.requestFocus();
			if (boardManager.isDisableCameraTransform() && !boardManager.isDisableDeviceInputs()) {
				ToastManager.showShort(R.string.follow_tips);
			}
		}
		return false;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		color_select_view.setVisibility(View.GONE);
		switch (checkedId) {
			case R.id.tool_selector:
				boardManager.setAppliance(Appliance.SELECTOR);
				break;
			case R.id.tool_pencil:
				boardManager.setAppliance(Appliance.PENCIL);
				break;
			case R.id.tool_text:
				boardManager.setAppliance(Appliance.TEXT);
				break;
			case R.id.tool_eraser:
				boardManager.setAppliance(Appliance.ERASER);
				break;
			case R.id.tool_color:
				color_select_view.setVisibility(View.VISIBLE);
				break;
		}
	}

	@Override
	public void toStart() {
		boardManager.setSceneIndex(0);
	}

	@Override
	public void toPrevious() {
		boardManager.pptPreviousStep();
	}

	@Override
	public void toNext() {
		boardManager.pptNextStep();
	}

	@Override
	public void toEnd() {
		boardManager.setSceneIndex(boardManager.getSceneCount() - 1);
	}

	@Override
	public void toAdd() {
		boardManager.addEmptyScenes();
	}

	@Override
	public void onRoomPhaseChanged(RoomPhase phase) {
		pb_loading.setVisibility(phase == RoomPhase.connected ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onSceneStateChanged(SceneState state) {
		Timber.e("onSceneStateChanged---->%s", JSON.toJSONString(state));
		page_control_view.setPageIndex(state.getIndex(), state.getScenes().length);
	}

	@Override
	public void onMemberStateChanged(MemberState state) {
		appliance_view.check(appliance_view.getApplianceId(state.getCurrentApplianceName()));
	}


}

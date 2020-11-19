package com.zhongyou.meet.mobile.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.orhanobut.logger.Logger;
import com.zhongyou.meet.mobile.BaseApplication;
import com.zhongyou.meet.mobile.R;

import androidx.annotation.RequiresApi;
import io.agora.openlive.model.EngineConfig;
import io.agora.openlive.model.WorkerThread;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import me.jessyan.autosize.utils.AutoSizeUtils;

import static android.content.Context.WINDOW_SERVICE;
import static com.tencent.bugly.beta.tinker.TinkerManager.getApplication;

/**
 * Author : Ziwen Lan
 * Date : 2020/5/7
 * Time : 17:07
 * Introduction : 仿微信语音通话悬浮弹窗
 * 内部管理实现悬浮窗功能
 * 实现拖动时判断左右方向自动粘边效果
 * 实现粘边处圆角转直角、非粘边时直角转圆角效果
 */
public class FloatingView extends View {
	private final String TAG = FloatingView.class.getSimpleName();
	/**
	 * 默认宽高与当前View实际宽高
	 */
	private int mDefaultWidth, mDefaultHeight;

	/**
	 * 当前View绘制相关
	 */
	private Direction mDirection = Direction.right;
	private int mOrientation;
	private int mWidthPixels;
	/**
	 * 悬浮窗管理相关
	 */
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayoutParams;
	private boolean mIsShow;
	private SurfaceView mLocalBroadcasterSurfaceView;
	private int x;
	private int y;

	public FloatingView(Context context) {
		super(context);
		init();
	}

	protected RtcEngine rtcEngine() {
		return worker().getRtcEngine();
	}

	protected final WorkerThread worker() {
		return BaseApplication.getInstance().getWorkerThread();
	}

	protected final EngineConfig config() {
		return worker().getEngineConfig();
	}

	private void init() {
		//悬浮窗管理相关
		setBackgroundColor(getResources().getColor(R.color.red));
		mWindowManager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
		mLayoutParams = new WindowManager.LayoutParams();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		}

		//当前View绘制相关
		mPaint = new Paint();
		mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
		mLayoutParams.format = PixelFormat.RGBA_8888;
		mDefaultHeight = AutoSizeUtils.pt2px(getContext(), 50);
		mDefaultWidth = AutoSizeUtils.pt2px(getContext(), 50);
		mLayoutParams.flags = mLayoutParams.FLAG_NOT_TOUCH_MODAL | mLayoutParams.FLAG_NOT_FOCUSABLE | mLayoutParams.FLAG_FULLSCREEN
				| mLayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mLayoutParams.width = mDefaultWidth;
		mLayoutParams.height = mDefaultHeight;
		//记录当前屏幕方向和屏幕宽度
		recordScreenWidth();


	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		setMeasuredDimension(AutoSizeUtils.pt2px(getContext(), 50), AutoSizeUtils.pt2px(getContext(), 50));
	}

	private Paint mPaint;
	private PorterDuffXfermode mPorterDuffXfermode;


	/**
	 * 计算宽高
	 */
	private int measureSize(int defaultSize, int measureSpec) {
		int result = defaultSize;

		return result;
	}

	/**
	 * 记录当前屏幕方向和屏幕宽度
	 */
	private void recordScreenWidth() {
		mOrientation = getResources().getConfiguration().orientation;
		DisplayMetrics outMetrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(outMetrics);
		mWidthPixels = outMetrics.widthPixels;
	}

	/**
	 * 判定所处方向
	 */
	private void handleDirection(int x, int y) {
		if (x > (mWidthPixels / 2)) {
			mDirection = Direction.right;
			mLayoutParams.x = mWidthPixels - getMeasuredWidth();
		} else {
			mDirection = Direction.left;
			mLayoutParams.x = 0;
		}
	}

	/**
	 * show
	 *
	 * @param role
	 * @param uid
	 */
	@SuppressLint("ClickableViewAccessibility")
	public void show(int role, int uid, String appId) {
		if (!mIsShow) {
			BaseApplication.getInstance().initWorkerThread(appId);
			mLocalBroadcasterSurfaceView = RtcEngine.CreateRendererView(BaseApplication.getInstance());
			rtcEngine().setupLocalVideo(new VideoCanvas(mLocalBroadcasterSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
			mLocalBroadcasterSurfaceView.setZOrderOnTop(false);
			mLocalBroadcasterSurfaceView.setZOrderMediaOverlay(false);
			worker().preview(true, mLocalBroadcasterSurfaceView, uid);

			if (mLayoutParams.x == 0 && mLayoutParams.y == 0 && mDirection == Direction.right) {
				mLayoutParams.x = mWidthPixels - mDefaultWidth;
				mLayoutParams.y = 0;
			}
			if (mDirection == Direction.move) {
				handleDirection(mLayoutParams.x, mLayoutParams.y);
			}
			mWindowManager.addView(mLocalBroadcasterSurfaceView, mLayoutParams);
			mIsShow = true;

			mLocalBroadcasterSurfaceView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (mWindowManager != null) {
						if (getResources().getConfiguration().orientation != mOrientation) {
							//屏幕方向翻转了，重新获取并记录屏幕宽度
							recordScreenWidth();
						}
						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								x = (int) event.getRawX();
								y = (int) event.getRawY();
								break;
							case MotionEvent.ACTION_MOVE:
								int nowX = (int) event.getRawX();
								int nowY = (int) event.getRawY();
								int movedX = nowX - x;
								int movedY = nowY - y;
								x = nowX;
								y = nowY;
								mLayoutParams.x = mLayoutParams.x + movedX;
								mLayoutParams.y = mLayoutParams.y + movedY;
								if (mLayoutParams.x < 0) {
									mLayoutParams.x = 0;
								}
								if (mLayoutParams.y < 0) {
									mLayoutParams.y = 0;
								}
								if (mDirection != Direction.move) {
									mDirection = Direction.move;
									invalidate();
								}
								mWindowManager.updateViewLayout(FloatingView.this, mLayoutParams);
								break;
							case MotionEvent.ACTION_UP:
								handleDirection((int) event.getRawX(), (int) event.getRawY());
								invalidate();
								mWindowManager.updateViewLayout(FloatingView.this, mLayoutParams);
								break;
							default:
								break;
						}
					}
					return true;
				}
			});
		}
	}




	/**
	 * 调整悬浮窗位置
	 * 根据提供坐标自动判断粘边
	 */
	public void updateViewLayout(int x, int y) {
		if (mIsShow) {
			handleDirection(x, y);
			invalidate();
			mLayoutParams.y = y;
			mWindowManager.updateViewLayout(this, mLayoutParams);
		}
	}

	/**
	 * dismiss
	 */
	public void dismiss() {
		if (mIsShow) {
			try {
				mWindowManager.removeView(mLocalBroadcasterSurfaceView);
			} catch (Exception e) {

			} finally {
				mIsShow = false;
			}
		}
	}

	/**
	 * 方向
	 */
	public enum Direction {
		/**
		 * 左、右、移动
		 */
		left,
		right,
		move
	}
}

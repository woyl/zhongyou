package com.zhongyou.meet.mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author luopan@centerm.com
 * @date 2020-03-05 14:00.
 */
public class KeyBoardUtils {
	public KeyBoardUtils() {
		throw new UnsupportedOperationException("u can't instantiate me...");
	}

	/**
	 * 避免输入法面板遮挡
	 * <p>在manifest.xml中activity中设置</p>
	 * <p>android:windowSoftInputMode="stateVisible|adjustResize"</p>
	 */

	/**
	 * 动态隐藏软键盘
	 *
	 * @param activity activity
	 */
	public static void hideSoftInput(Activity activity) {
		View view = activity.getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
			//            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); // 同样可行

		}
	}

	public static void hideSoftInput(Activity activity, boolean noView) {
		InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanger.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		//        inputmanger.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); // 同样可行

	}


	/**
	 * 动态隐藏软键盘
	 *
	 * @param context 上下文
	 * @param edit    输入框
	 */
	public static void hideSoftInput(Context context, EditText edit) {
		edit.clearFocus();
		InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanger.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}

	/**
	 * 显示键盘
	 * <p>
	 * (没有用)
	 *
	 * @param view
	 * @return
	 */
	public static boolean showSoftInput(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 动态显示软键盘
	 *
	 * @param context 上下文
	 * @param edit    输入框
	 */
	public static void showSoftInput(Context context, EditText edit) {
		edit.setFocusable(true);
		edit.setFocusableInTouchMode(true);
		edit.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(edit, 0);
	}

	/**
	 * 切换键盘显示与否状态
	 *
	 * @param context 上下文
	 */
	public static void toggleSoftInput(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 同样效果

	}

	/**
	 * 判断键盘是否显示
	 *
	 * @param context 上下文
	 * @return {@code true}: 显示<br>{@code false}: 不显示
	 */
	public static boolean isShowSoftInput(Context context) {
		return ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).isActive();
	}

	/**
	 * 点击屏幕空白区域隐藏软键盘（方法1）
	 * <p>
	 * <p>在onTouch中处理，未获焦点则隐藏</p>
	 * <p>
	 * <p>参照以下注释代码</p>
	 */
	public static void clickBlankArea2HideSoftInput0() {

        /*
        @Override
        public boolean onTouchEvent (MotionEvent event){
            if (null != this.getCurrentFocus()) {
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
            return super.onTouchEvent(event);
        }
        */
	}

	/**
	 * 点击屏幕空白区域隐藏软键盘（方法2）
	 * <p>
	 * <p>根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘</p>
	 * <p>
	 * <p>需重写dispatchTouchEvent</p>
	 * <p>
	 * <p>参照以下注释代码</p>
	 */
	public static void clickBlankArea2HideSoftInput1() {

        /*
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    hideKeyboard(v.getWindowToken());
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
        private boolean isShouldHideKeyboard(View v, MotionEvent event) {
            if (v != null && (v instanceof EditText)) {
                int[] l = {0, 0};
                v.getLocationInWindow(l);
                int left = l[0],
                        top = l[1],
                        bottom = top + v.getHeight(),
                        right = left + v.getWidth();
                return !(event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom);
            }
            return false;
        }
        // 获取InputMethodManager，隐藏软键盘
        private void hideKeyboard(IBinder token) {
            if (token != null) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        */
	}

}

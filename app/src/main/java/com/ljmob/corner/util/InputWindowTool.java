package com.ljmob.corner.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class InputWindowTool {
	/**
	 * 打开输入法键盘
	 * 
	 * @param activity
	 */
	public static void OpenInputWindow(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity
				.getApplication()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 打开输入法键盘，焦点注册到指定View
	 * 
	 * @param view
	 * @param activity
	 */
	public static void OpenInputWindow(View view, Activity activity) {
		view.setFocusable(true);
		view.requestFocus();
		if (activity.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			InputMethodManager inputManager = (InputMethodManager) activity
					.getApplication().getSystemService(
							Context.INPUT_METHOD_SERVICE);
			inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 影藏输入法键盘
	 * 
	 * @param activity
	 */
	public static void hideInputWindow(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity
				.getApplication()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}

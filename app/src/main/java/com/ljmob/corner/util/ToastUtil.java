package com.ljmob.corner.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Toast框工具类
 * 
 * @author YANGBANG
 * 
 */
public class ToastUtil {
	private static Handler handler;
	private static String info;
	private static Toast lastToast;

	public static void show(final Context context, String info) {
		ToastUtil.info = info;
		if (handler == null) {
			handler = new Handler(context.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {
					if (lastToast == null) {
						lastToast = Toast.makeText(context, ToastUtil.info,
								Toast.LENGTH_LONG);
					}
					lastToast.setText(ToastUtil.info);
					lastToast.show();
					super.handleMessage(msg);
				}
			};
		}
		handler.sendEmptyMessage(0);
	}

	public static void show(Context context, int info) {
		ToastUtil.show(context, context.getString(info));
	}
}

package com.ljmob.corner.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ljmob.corner.R;

public class DetailHintUtil {
	// private Context context;
	//
	// public DetailHintUtil(Context context) {
	// this.context = context;
	// }

	/**
	 * 显示异常或错误提示信息
	 * 
	 * @param jsonString
	 *            json字符串
	 * @param statusCode
	 *            状态码
	 */
	public static void showToast(Context context, String jsonString,
			int statusCode) {
		if (statusCode == 401 || statusCode == 400) {
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				String detail = jsonObject.getString("detail");
				ToastUtil.show(context, detail);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			ToastUtil.show(context, R.string.toast_log_servce_exception);
		}
	}

}

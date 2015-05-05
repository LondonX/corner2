package com.ljmob.corner.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager.BadTokenException;

public class DialogUtil {
	private Context context;
	private ProgressDialog dialog;
	private boolean isShowDialog = true;

	public DialogUtil(Context context, int message) {
		this.context = context;
		dialog = new ProgressDialog(context);
		dialog.setMessage(context.getString(message));
		dialog.setCancelable(false);
	}

	public void setMsg(int message) {
		if (dialog != null) {
			dialog.setMessage(context.getString(message));
		}
	}
	public void setMsg(String message) {
		if (dialog != null) {
			dialog.setMessage(message);
		}
	}

	public void setCanShowDialog(boolean isShowDialog) {
		this.isShowDialog = isShowDialog;
	}

	/**
	 * 关闭对话框
	 */
	public void closeDialog() {
		if (dialog.isShowing() && dialog != null) {
			try {
				dialog.dismiss();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 显示对话框
	 */
	public void showDialog() {
		if (dialog != null && isShowDialog && !dialog.isShowing()) {
			try {
				dialog.show();
			} catch (BadTokenException e) {
				e.printStackTrace();
			}
		}
	}

}

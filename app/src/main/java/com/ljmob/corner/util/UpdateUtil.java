package com.ljmob.corner.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.ljmob.corner.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

public class UpdateUtil implements Runnable, Callback {
	public static final int NEW_VER_NON = 0;
	public static final int NEW_VER_IGN = 1;
	public static final int NEW_VER_AVL = 2;
	public static final int NEW_VER_DIA = 3;

	private String updateApi = "/api/v1/apps?type=last&platform=android";
	private String downloadApi = "/apps?type=apk&lastest=true";
	private SharedPreferences preferences;
	private Activity activity;
	private MyApplication application;

	private UpdateListener updateListener;
	private Handler handler;

	public UpdateUtil(Activity activity) {
		this.activity = activity;
		application = (MyApplication) activity.getApplicationContext();
		preferences = application.preferences;
		handler = new Handler(this);

		new Thread(this).start();
	}

	public void setUpdateListener(UpdateListener updateListener) {
		this.updateListener = updateListener;
	}

	@Override
	public void run() {
		String url = UrlStaticUtil.root_photo + updateApi;
		try {
			String jString = HttpRequestTools.doGet(
					activity.getApplicationContext(), url, application.token);
			JSONObject jsonObject = new JSONObject(jString);
			Message msg = new Message();
			msg.obj = jsonObject;
			handler.sendMessage(msg);
			String newVer = jsonObject.getString("version");
			if (newVer.equals(preferences.getString("newVersion", "")) == false) {
				Editor editor = preferences.edit();
				editor.putString("newVersion", newVer);
				editor.apply();
			}
			if (newVer.equals(preferences.getString("diaVersion", "none")) == false
					&& newVer.equals(getCurrentVersion(activity)) == false) {
				Message msg2 = new Message();
				msg2.what = NEW_VER_DIA;
				msg2.obj = jsonObject;
				handler.sendMessage(msg2);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		final JSONObject jsonObject = (JSONObject) msg.obj;
		if (msg.what == NEW_VER_DIA) {
			showUpdateDialog(jsonObject);
			return false;
		}
		if (updateListener != null) {
			updateListener.onGetVersionInfo(jsonObject);
		}
		return false;
	}

	public void showUpdateDialog(final JSONObject jsonObject) {
		try {
			String desc = jsonObject.getString("description");
			String url = UrlStaticUtil.root_photo + downloadApi;
			final Uri uri = Uri.parse(url);
			AlertDialog.Builder builder = new Builder(activity);
			builder.setTitle(R.string.dialog_update_title);
			builder.setMessage(desc + "\n\n"
					+ activity.getString(R.string.dialog_update_content));
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.dialog_update_yes,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent it = new Intent(Intent.ACTION_VIEW, uri);
							activity.startActivity(it);
						}
					});
			builder.setNegativeButton(R.string.dialog_update_no,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Editor editor = preferences.edit();
							try {
								editor.putString("diaVersion",
										jsonObject.getString("version"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
							editor.apply();
						}
					});
			builder.create().show();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void cheakUpdate(Activity activity) {
		new UpdateUtil(activity);
	}

	public static int newVerStatus(Context context) {
		int status = 0;
		SharedPreferences preferences = ((MyApplication) context
				.getApplicationContext()).preferences;
		String newVer = preferences.getString("newVersion", "none");
		String ignVer = preferences.getString("ignoreVersion", "-1");
		if (newVer.equals("none")) {
			return 0;
		}
		String currentVer = getCurrentVersion(context);
		if (newVer.equals(currentVer)) {
			status = NEW_VER_NON;
			Editor editor = preferences.edit();
			editor.putString("ignoreVersion", currentVer);
			editor.apply();
		} else if (newVer.equals(ignVer)) {
			status = NEW_VER_IGN;
		} else {
			status = NEW_VER_AVL;
		}
		return status;
	}

	public static String getCurrentVersion(Context context) {
		String currentVer = "";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			currentVer = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return currentVer;
	}

	public interface UpdateListener {
		public void onGetVersionInfo(JSONObject jsonObject);
	}
}

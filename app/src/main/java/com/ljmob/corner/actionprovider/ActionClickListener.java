package com.ljmob.corner.actionprovider;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import com.ljmob.corner.CollectActivity;
import com.ljmob.corner.FeedbackActivity;
import com.ljmob.corner.FocusActivity;
import com.ljmob.corner.LoginActivity;
import com.ljmob.corner.MsgActivity;
import com.ljmob.corner.R;
import com.ljmob.corner.SettingsActivity;
import com.ljmob.corner.UserActivity;

public class ActionClickListener implements OnMenuItemClickListener {
	private Context context;
	private int tag;

	public ActionClickListener(Context context, int tag) {
		super();
		this.context = context;
		this.tag = tag;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (tag) {
		case R.string.menu_login_qq:
			Intent loginQQIntent = new Intent(context, LoginActivity.class);
			loginQQIntent.putExtra(LoginActivity.LOG_METHOD, LoginActivity.METHOD_QQ);
			context.startActivity(loginQQIntent);
			// Intent intent = new Intent(context, MapActivity.class);
			// context.startActivity(intent);
			break;
		case R.string.menu_login_weixin:
			Intent loginWeixinIntent = new Intent(context, LoginActivity.class);
			loginWeixinIntent.putExtra(LoginActivity.LOG_METHOD,
					LoginActivity.METHOD_WX);
			context.startActivity(loginWeixinIntent);
			break;
		case R.string.menu_login_weibo:
			Intent loginWeiboIntent = new Intent(context, LoginActivity.class);
			loginWeiboIntent.putExtra(LoginActivity.LOG_METHOD,
					LoginActivity.METHOD_WB);
			context.startActivity(loginWeiboIntent);
			break;
		case Action.TAG_USER:
			Intent userIntent = new Intent(context, UserActivity.class);
			context.startActivity(userIntent);
			break;
		case R.string.menu_msg:
			Intent msgIntent = new Intent(context, MsgActivity.class);
			context.startActivity(msgIntent);
			break;
		case R.string.menu_collect:
			Intent collectIntent = new Intent(context, CollectActivity.class);
			context.startActivity(collectIntent);
			break;
		case R.string.menu_topic_focused:
			Intent focusIntent = new Intent(context, FocusActivity.class);
			context.startActivity(focusIntent);
			break;
		case R.string.menu_feedback:
			Intent feedIntent = new Intent(context, FeedbackActivity.class);
			context.startActivity(feedIntent);
			break;
		case R.string.menu_settings:
			Intent settingsIntent = new Intent(context, SettingsActivity.class);
			context.startActivity(settingsIntent);
			break;
		}
		return false;
	}
}
package com.ljmob.corner.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

import com.ljmob.corner.LoginActivity;
import com.ljmob.corner.R;

public class SelectLoginWay extends Builder {
    private final int QQ = 1;
    private final int WECHAT = 2;
    private final int WEIBO = 3;
    private Context context;
    private MyApplication application;

    public SelectLoginWay(Context context) {
        super(context);
        this.context = context;
        this.application = (MyApplication) context.getApplicationContext();

    }

    /**
     * 是否为登陆状态
     *
     * @return true为登陆，false为未登录
     */
    public boolean isLogin() {
        if (application.preferences.getString("token", "").equals("")) {
            showDialog();
            return false;
        } else {
            return true;
        }
    }

    private void showDialog() {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_login,
                null);
        LinearLayout dialog_login_linearQQ = (LinearLayout) view
                .findViewById(R.id.dialog_login_linearQQ);
        LinearLayout dialog_login_linearWechat = (LinearLayout) view
                .findViewById(R.id.dialog_login_linearWechat);
        LinearLayout dialog_login_linearWeibo = (LinearLayout) view
                .findViewById(R.id.dialog_login_linearWeibo);
        builder.setContentView(view);
        dialog_login_linearQQ.setOnClickListener(new MyLoginOnClickListener(QQ,
                builder));
        dialog_login_linearWechat
                .setOnClickListener(new MyLoginOnClickListener(WECHAT, builder));
        dialog_login_linearWeibo.setOnClickListener(new MyLoginOnClickListener(
                WEIBO, builder));
        builder.show();

    }

    private class MyLoginOnClickListener implements OnClickListener {
        int loginWay = 0;
        Dialog builder;

        public MyLoginOnClickListener(int loginWay, Dialog builder) {
            this.loginWay = loginWay;
            this.builder = builder;
        }

        @Override
        public void onClick(View v) {
            if (builder != null && builder.isShowing()) {
                builder.dismiss();
            }
            switch (loginWay) {
                case 1:// QQ
                    Intent loginQQIntent = new Intent(context, LoginActivity.class);
                    loginQQIntent.putExtra(LoginActivity.LOG_METHOD,
                            LoginActivity.METHOD_QQ);
                    context.startActivity(loginQQIntent);
                    break;
                case 2:// WEIXIN
                    Intent loginWeixinIntent = new Intent(context,
                            LoginActivity.class);
                    loginWeixinIntent.putExtra(LoginActivity.LOG_METHOD,
                            LoginActivity.METHOD_WX);
                    context.startActivity(loginWeixinIntent);
                    break;
                case 3:// WEIBO
                    Intent loginWeiboIntent = new Intent(context,
                            LoginActivity.class);
                    loginWeiboIntent.putExtra(LoginActivity.LOG_METHOD,
                            LoginActivity.METHOD_WB);
                    context.startActivity(loginWeiboIntent);
                    break;

                default:
                    break;
            }
        }

    }

    // private void showDialog() {
    // CharSequence items[] = { "QQ登录", "微信登录", "微博登录" };
    // this.setTitle("您还未登录，请选择一种登录方式");
    // this.setItems(items, new DialogInterface.OnClickListener() {
    //
    // @Override
    // public void onClick(DialogInterface dialog, int which) {
    // switch (which) {
    // case 0:// QQ登录
    // Intent loginQQIntent = new Intent(context,
    // LoginActivity.class);
    // loginQQIntent.putExtra(LoginActivity.LOG_METHOD,
    // LoginActivity.QQ);
    // context.startActivity(loginQQIntent);
    // break;
    // case 1:// 微信登录
    // Intent loginWeixinIntent = new Intent(context,
    // LoginActivity.class);
    // loginWeixinIntent.putExtra(LoginActivity.LOG_METHOD,
    // LoginActivity.WX);
    // context.startActivity(loginWeixinIntent);
    // break;
    // case 2:// 微博登录
    // Intent loginWeiboIntent = new Intent(context,
    // LoginActivity.class);
    // loginWeiboIntent.putExtra(LoginActivity.LOG_METHOD,
    // LoginActivity.WB);
    // context.startActivity(loginWeiboIntent);
    // break;
    //
    // default:
    // break;
    // }
    // }
    // });
    // this.create().show();
    // }

}

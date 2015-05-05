package com.ljmob.corner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ljmob.corner.util.AuthTool;
import com.ljmob.corner.util.MyApplication;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends ActionBarActivity implements OnClickListener,
        PlatformActionListener, Handler.Callback {
    public static final String LOG_METHOD = "LOG_METHOD";
    public static final int METHOD_QQ = 0;
    public static final int METHOD_WX = 1;
    public static final int METHOD_WB = 2;
    public static final int HANDLER_ERROR = 0;
    public static final int HANDLER_LOG_AUTH = 1;
    public static final int HANDLER_LOG_SUCCESS = 2;

    private int logMethod;

    private View activity_log_pb0;
    private View activity_log_tvRetry;
    private TextView activity_log_tvStatus;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logMethod = getIntent().getIntExtra("LOG_METHOD", -1);
        initToolBar();
        activity_log_pb0 = findViewById(R.id.activity_log_pb0);
        activity_log_tvRetry = findViewById(R.id.activity_log_tvRetry);
        activity_log_tvStatus = (TextView) findViewById(R.id.activity_log_tvStatus);
        activity_log_tvRetry.setOnClickListener(this);
        handler = new Handler(this);
        doLogin();
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_log_tvRetry:
                doLogin();
                break;
            default:
                break;
        }
    }

    private void doLogin() {
        activity_log_pb0.setVisibility(View.VISIBLE);
        activity_log_tvRetry.setVisibility(View.GONE);
        activity_log_tvStatus.setText(R.string.str_log_ing);
        switch (logMethod) {
            case METHOD_QQ:
                Platform qq = ShareSDK.getPlatform(this, QQ.NAME);
                qq.SSOSetting(false);
                qq.setPlatformActionListener(this);
                qq.authorize();
                break;
            case METHOD_WX:
                Platform wechat = ShareSDK.getPlatform(this, Wechat.NAME);
                wechat.SSOSetting(false);
                wechat.setPlatformActionListener(this);
                wechat.authorize();
                break;
            case METHOD_WB:
                Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
                weibo.SSOSetting(false);
                weibo.setPlatformActionListener(this);
                weibo.authorize();
                break;
            default:
                break;
        }
    }

    private void loginAuth(Platform platform) {
        activity_log_tvStatus.setText(R.string.str_log_auth);
        final String refType;
        if (platform.getName() == QQ.NAME) {
            refType = AuthTool.REF_TYPE_QQ;
        } else if (platform.getName() == Wechat.NAME) {
            refType = AuthTool.REF_TYPE_WX;
        } else if (platform.getName() == SinaWeibo.NAME) {
            refType = AuthTool.REF_TYPE_WB;
        } else {
            refType = null;
        }
        final PlatformDb db = platform.getDb();
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (db.isValid() == false) {
                    handler.sendEmptyMessage(HANDLER_ERROR);
                    return;
                }
                AuthTool authTool = new AuthTool(LoginActivity.this, refType, //context, ref_type
                        db.getUserId(),//ref_account
                        db.getUserName(),//nickname
                        db.getUserGender() == "m" ? AuthTool.SEX_M : AuthTool.SEX_F,//sex
                        db.getUserIcon(),//avatar
                        db.getUserIcon(),//max_avatar
                        "",//signature
                        db.getToken());//token
                authTool.startAuth();
                handler.sendEmptyMessage(HANDLER_LOG_SUCCESS);
            }
        }).start();
    }

    private void loginSuccess() {
        activity_log_pb0.setVisibility(View.GONE);
        activity_log_tvRetry.setVisibility(View.GONE);
        activity_log_tvStatus.setText(R.string.str_log_success);
        ((MyApplication) getApplication()).isLoginSucceed = true;
        autoExit();
    }

    private void loginFaild() {
        activity_log_pb0.setVisibility(View.GONE);
        activity_log_tvRetry.setVisibility(View.VISIBLE);
        activity_log_tvStatus.setText(R.string.str_log_fail);
    }


    private void autoExit() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    LoginActivity.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
        Message msg = new Message();
        msg.what = HANDLER_LOG_AUTH;
        msg.obj = platform;
        handler.sendMessage(msg);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        handler.sendEmptyMessage(HANDLER_ERROR);
    }

    @Override
    public void onCancel(Platform platform, int i) {
        handler.sendEmptyMessage(HANDLER_ERROR);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_ERROR:
                loginFaild();
                break;
            case HANDLER_LOG_AUTH:
                Platform platform = (Platform) msg.obj;
                loginAuth(platform);
                break;
            case HANDLER_LOG_SUCCESS:
                loginSuccess();
                break;
        }
        return false;
    }
}

package com.ljmob.corner;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ljmob.corner.cache.FileCache;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.UserInfo;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.AuthTool;
import com.ljmob.corner.util.AvatarSelectTool;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.GetFileSizeUtil;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.PushSwitchTool;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UpdateUtil;
import com.ljmob.corner.util.UpdateUtil.UpdateListener;
import com.ljmob.corner.util.UrlStaticUtil;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.proguard.C.e;

public class SettingsActivity extends ActionBarActivity implements OnClickListener,
        OnResponseResult, UpdateListener, CompoundButton.OnCheckedChangeListener {
    private final int CLEAR_CACHE = 1;// 清理缓存
    private final int EXIT_LOGIN = 2;// 退出登录
    private TextView activity_settings_tvExit;// 退出登录
    private RelativeLayout activity_settings_rlClear;// 清除缓存
    private TextView activity_settings_tvSize;// 缓存大小
    private RelativeLayout activity_settings_rlInfo;// 个人信息
    private ImageView activity_settings_imgInfoHead;// 头像
    private TextView activity_settings_tvCurrent;
    private RelativeLayout activity_settings_rlUpdate;
    private RelativeLayout activity_settings_rlRate;// 打分
    private RelativeLayout activity_settings_rlMsg;// 消息推送Relative
    private RelativeLayout activity_settings_rlReddot;// 消息推送Relative
    private Switch activity_settings_swMsg;// 消息推送
    private Switch activity_settings_swDot;// 消息小圆点
    private MyApplication application;
    private ServiceImpl serviceImpl;
    private ImageLoader imageLoader;
    private ProgressDialog progDialog;
    private UpdateUtil updateUtil;
    private PushSwitchTool pushSwitchTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initToolBar();
        application = (MyApplication) getApplication();
        serviceImpl = FactoryService.getIService(this);
        imageLoader = new ImageLoader(this);
        pushSwitchTool = new PushSwitchTool(this);
        initView();
    }

    private void getUserInfo() {
        serviceImpl.getUserInfo(UrlStaticUtil
                        .getUserInfo(application.preferences.getInt("id", -1)),
                Constants.USER_INFO);
    }

    private void initView() {
        activity_settings_tvExit = (TextView) this
                .findViewById(R.id.activity_settings_tvExit);
        activity_settings_rlInfo = (RelativeLayout) this
                .findViewById(R.id.activity_settings_rlInfo);
        activity_settings_imgInfoHead = (ImageView) this
                .findViewById(R.id.activity_settings_imgInfoHead);
        activity_settings_rlClear = (RelativeLayout) this
                .findViewById(R.id.activity_settings_rlClear);
        activity_settings_rlUpdate = (RelativeLayout) this
                .findViewById(R.id.activity_settings_rlUpdate);
        activity_settings_rlRate = (RelativeLayout) this
                .findViewById(R.id.activity_settings_rlRate);
        activity_settings_rlMsg = (RelativeLayout) this
                .findViewById(R.id.activity_settings_rlMsg);
        activity_settings_rlReddot = (RelativeLayout) this
                .findViewById(R.id.activity_settings_rlReddot);
        activity_settings_tvSize = (TextView) this
                .findViewById(R.id.activity_settings_tvSize);
        activity_settings_tvCurrent = (TextView) this
                .findViewById(R.id.activity_settings_tvCurrent);
        activity_settings_swMsg = (Switch) this
                .findViewById(R.id.activity_settings_swMsg);
        activity_settings_swDot = (Switch) this
                .findViewById(R.id.activity_settings_swDot);

        activity_settings_swMsg.setChecked(application.preferences.getBoolean(
                "UmengPushIsEnabled", true) ? true : false);
        activity_settings_swDot.setChecked(application.preferences.getBoolean(
                "isNewsDot", true) ? true : false);
        activity_settings_tvSize.setVisibility(View.INVISIBLE);
        imageLoader.DisplayImage(UrlStaticUtil.root_photo
                        + application.preferences.getString("avatar", ""),
                activity_settings_imgInfoHead, true, ImageLoader.L_PICTURE);
        activity_settings_tvExit.setOnClickListener(this);
        activity_settings_rlInfo.setOnClickListener(this);
        activity_settings_rlClear.setOnClickListener(this);
        activity_settings_swMsg.setOnCheckedChangeListener(this);
        activity_settings_swDot.setOnCheckedChangeListener(this);
        activity_settings_rlMsg.setOnClickListener(this);
        activity_settings_rlReddot.setOnClickListener(this);
        activity_settings_rlRate.setOnClickListener(this);
        if (application.preferences.getString("token", "").length() == 0) {
            activity_settings_tvExit.setVisibility(View.GONE);
        }
        setCacheSize();
        setUpdate();
    }

    private void gotoMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show(this, R.string.toast_error_market);
        }
    }

    private void setUpdate() {
        activity_settings_tvCurrent.setText(getString(R.string.str_ver_current)
                + UpdateUtil.getCurrentVersion(this));
        activity_settings_rlUpdate.setOnClickListener(this);
    }

    private void setCacheSize() {
        try {
            float size = GetFileSizeUtil.getInstance().getFileSize(
                    new File(new FileCache(this).getSaveFilePath()))
                    / (1000 * 1000);
            String strSize = String.valueOf(size);
            if (strSize.length() >= 6) {
                strSize = strSize.substring(0, 6);
            }
            activity_settings_tvSize.setText(strSize + "MB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在清理缓存...");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 显示清除缓存提示对话框
     */
    public void showPromptDialog(String msg, final int type) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type == CLEAR_CACHE) {
                    showProgressDialog();
                    AvatarSelectTool.deleteDirectory(new FileCache(
                            SettingsActivity.this).getSaveFilePath());
                    dissmissProgressDialog();
                    setCacheSize();
                } else if (type == EXIT_LOGIN) {
                    Editor editor = application.preferences.edit();
                    editor.remove("id");
                    editor.remove("ref_type");
                    editor.remove("ref_account");
                    editor.remove("nickname");
                    editor.remove("sex");
                    editor.remove("avatar");
                    editor.remove("max_avatar");
                    editor.remove("signature");
                    editor.remove("token");
                    editor.apply();
                    sendBroadcast(new Intent(AuthTool.ACTION_CH_USER));
                    SettingsActivity.this.finish();
                    ToastUtil.show(SettingsActivity.this,
                            R.string.toast_ok_logout);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            PushAgent mPushAgent = PushAgent
                                    .getInstance(SettingsActivity.this);
                            try {
                                mPushAgent.removeAlias(application.preferences
                                                .getString("Alias", ""),
                                        ALIAS_TYPE.WEIXIN);
                            } catch (e e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.activity_settings);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        if (application.isUpdataUserInfo || application.isLoginSucceed) {
            application.isUpdataUserInfo = false;
            application.isLoginSucceed = false;
            getUserInfo();
        }
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_settings_tvExit:
                showPromptDialog("你确定退出登录？", EXIT_LOGIN);
                break;
            case R.id.activity_settings_rlInfo:
                if (new SelectLoginWay(this).isLogin()) {
                    Intent intent = new Intent(this, EditInfoActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.activity_settings_rlClear:
                showPromptDialog("确定清理缓存？" + "清理后若有异常，请重新启动该软件", CLEAR_CACHE);
                break;
            case R.id.activity_settings_rlUpdate:
                updateUtil = new UpdateUtil(this);
                updateUtil.setUpdateListener(this);
                break;
            case R.id.activity_settings_rlRate:
                gotoMarket();
                break;
            case R.id.activity_settings_rlMsg:// 消息推送开关Relative
                activity_settings_swMsg.setChecked(activity_settings_swMsg
                        .isChecked() ? false : true);
                break;

            case R.id.activity_settings_rlReddot:// 消息小圆点Relative
                activity_settings_swDot.setChecked(activity_settings_swDot
                        .isChecked() ? false : true);
                break;

            default:
                break;
        }
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.USER_INFO) {
                UserInfo userInfo = JsonTools.getUserInfo(result);
                Editor editor = application.preferences.edit();
                editor.putString("nickname", userInfo.user_info.nickname);
                editor.putString("sex", userInfo.user_info.sex);
                editor.putString("signature", userInfo.user_info.signature);
                editor.putString("avatar", userInfo.user_info.avatar);
                editor.apply();
                imageLoader.DisplayImage(UrlStaticUtil.root_photo
                                + userInfo.user_info.avatar,
                        activity_settings_imgInfoHead, true, ImageLoader.L_PICTURE);
                activity_settings_tvExit.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onGetVersionInfo(JSONObject jsonObject) {
        if (UpdateUtil.newVerStatus(this) == UpdateUtil.NEW_VER_NON) {
            ToastUtil.show(this, R.string.toast_ver_none);
        } else {
            updateUtil.showUpdateDialog(jsonObject);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.activity_settings_swMsg:// 消息推送开关
                pushSwitchTool.switchPush(activity_settings_swMsg);
                break;
            case R.id.activity_settings_swDot:// 消息小圆点开关
                application.preferences
                        .edit()
                        .putBoolean("isNewsDot",
                                activity_settings_swDot.isChecked()).commit();
                ToastUtil
                        .show(this,
                                activity_settings_swDot.isChecked() ? R.string.toast_str_dot_enabled
                                        : R.string.toast_str_dot_disabled);
                break;
        }
    }
}

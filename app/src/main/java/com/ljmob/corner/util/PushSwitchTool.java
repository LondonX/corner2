package com.ljmob.corner.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Switch;

import com.ljmob.corner.R;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

public class PushSwitchTool {
    protected static final String TAG = PushSwitchTool.class.getSimpleName();
    private Context context;
    private PushAgent mPushAgent;
    private Handler handler = new Handler();
    private Switch switchBtn;
    private DialogUtil dialogUtil;
    private MyApplication application;

    public PushSwitchTool(Context context) {
        this.context = context;
        this.dialogUtil = new DialogUtil(context, R.string.dialog_load);
        application = (MyApplication) context.getApplicationContext();
    }

    public void switchPush(Switch switchBtn) {
        this.mPushAgent = PushAgent.getInstance(context);
        this.switchBtn = switchBtn;
        String info = String.format("enabled:%s  isRegistered:%s",
                mPushAgent.isEnabled(), mPushAgent.isRegistered());
        Log.i(TAG, "switch Push:" + info);
        if (switchBtn.isChecked()) {
            if (mPushAgent.isEnabled() == false) {
                dialogUtil.setMsg(R.string.dialog_enable_push);
                dialogUtil.showDialog();
                mPushAgent.enable(mRegisterCallback);
            }
        } else {
            if (mPushAgent.isEnabled()) {
                dialogUtil.setMsg(R.string.dialog_disable_push);
                dialogUtil.showDialog();
                mPushAgent.disable(mUnregisterCallback);
            }
        }
    }

    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

        @Override
        public void onRegistered(String registrationId) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    updateStatus();
                }
            });
        }
    };

    private IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {

        @Override
        public void onUnregistered(String registrationId) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    updateStatus();
                }
            });
        }
    };

    private void updateStatus() {
        boolean umengPushIsEnabled = mPushAgent.isEnabled();
        if (umengPushIsEnabled) {
            if (switchBtn.isChecked() == false) {
                switchBtn.setChecked(true);
            }
        } else {
            if (switchBtn.isChecked()) {
                switchBtn.setChecked(false);
            }
        }
        ToastUtil.show(context,
                umengPushIsEnabled ? R.string.toast_str_push_enabled
                        : R.string.toast_str_push_disabled);
        dialogUtil.closeDialog();
        application.preferences
                .edit()
                .putBoolean("UmengPushIsEnabled",
                        umengPushIsEnabled ? true : false).commit();
    }
}

package com.ljmob.corner.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ljmob.corner.FavorDetailActivity;
import com.ljmob.corner.R;
import com.ljmob.corner.ReadTopicActivity;
import com.ljmob.corner.UserActivity;
import com.ljmob.corner.map.util.MixtureLocatioin;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;

public class MyApplication extends Application {
    public String token = "";
    public List<String> listString;
    public JSONArray sortArray;
    public MixtureLocatioin mixtureLocatioin;
    public boolean isListMode = false;
    public boolean isAddtopicSucceed = false;
    public SharedPreferences preferences;
    public boolean isUpdataUserInfo = false;
    public boolean isLoginSucceed = false;
    private PushAgent mPushAgent;

    @Override
    public void onCreate() {
        preferences = getSharedPreferences("corner.shared", MODE_PRIVATE);
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        setUmengPush();
        ShareSDK.initSDK(this);
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        ShareSDK.stopSDK(this);
        super.onTerminate();
    }

    private void setUmengPush() {
        /**
         * 该Handler是在IntentService中被调用，故 1.
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK 2.
         * IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
         * 或者可以直接启动Service
         * */
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context,
                                              final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        UTrack.getInstance(getApplicationContext())
                                .trackMsgClick(msg);
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                context);
                        RemoteViews myNotificationView = new RemoteViews(
                                context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title,
                                msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text,
                                msg.text);
                        myNotificationView.setImageViewBitmap(
                                R.id.notification_large_icon,
                                getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(
                                R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        // Log.i("custom", msg.custom+"****");
                        // setNewFansNum(msg.custom);
                        builder.setContent(myNotificationView);
                        Notification mNotification = builder.build();
                        // 由于Android
                        // v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                        mNotification.contentView = myNotificationView;
                        return mNotification;
                    default:
                        // 默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                // Toast.makeText(context, msg.custom,
                // Toast.LENGTH_LONG).show();
                Log.i("YANGBANG", msg.custom);
                Intent[] intents = getActionIntents(context, msg.custom);
                if (intents != null) {
                    startActivities(intents);
                }
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    /**
     * 取得需要打开的activity
     *
     * @param context
     * @param custom  自定义内容
     * @return
     */
    private Intent[] getActionIntents(Context context, String custom) {
        int len = 0;
        Intent[] intents = null;
        if (isMainActivityRun()) {
            len = 1;
            intents = new Intent[len];
        } else {
            len = 2;
            intents = new Intent[len];
            intents[0] = Intent.makeRestartActivityTask(new ComponentName(
                    context, com.ljmob.corner.MainActivity.class));
        }

        try {
            JSONObject jsonObject = new JSONObject(custom);
            String type = jsonObject.getString("type");
            if (type != null) {
                if (type.equals("coupon")) {
                    intents[len - 1] = new Intent(context,
                            FavorDetailActivity.class);
                } else if (type.equals("post")) {
                    intents[len - 1] = new Intent(context,
                            ReadTopicActivity.class);
                } else if (type.equals("user")) {
                    intents[len - 1] = new Intent(context, UserActivity.class);
                }
                intents[len - 1].putExtra("id", jsonObject.getInt("id"));
                intents[len - 1].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                ToastUtil.show(context, R.string.toast_error_push);
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.show(context, R.string.toast_error_push);
            return null;
        }
        return intents;
    }

    /**
     * 是否有activity运行
     *
     * @return
     */
    private boolean isMainActivityRun() {
        ActivityManager am = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isMainActivityRunning = false;
        String MY_PKG_NAME = "com.ljmob.corner";
        for (RunningTaskInfo info : list) {
            if (info.numRunning >= 1
                    && (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity
                    .getPackageName().equals(MY_PKG_NAME))) {
                isMainActivityRunning = true;
                Log.i("numRunning", "numRunningActivity-->" + info.numRunning
                        + "");
                break;
            }
        }
        return isMainActivityRunning;
    }

    // private void setNewFansNum(String custom) {
    // if (custom != null && !custom.equals("")) {
    // try {
    // JSONObject jsonObject = new JSONObject(custom);
    // String type = jsonObject.getString("type");
    // if (type != null) {
    // if (type.equals("coupon")) {
    // } else if (type.equals("post")) {
    // } else if (type.equals("user")) {
    // preferences.edit().putInt("newFansNum",
    // preferences.getInt("newFansNum", 0) + 1);
    // }
    // }
    // } catch (JSONException e) {
    // e.printStackTrace();
    // }
    // }
    // }

}

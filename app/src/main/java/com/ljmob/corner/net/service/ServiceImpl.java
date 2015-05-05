package com.ljmob.corner.net.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.ljmob.corner.R;
import com.ljmob.corner.util.DialogUtil;
import com.ljmob.corner.util.HttpRequestTools;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.ToastUtil;

/**
 * 网络服务接口实现类
 *
 * @author YANGBANG
 */
public class ServiceImpl implements IService, Callback {
    private Context context;
    private MyApplication application;
    private OnResponseResult responseResult;
    private DialogUtil dialogUtil;
    private Handler handler;
    private Message message;
    private List<Object> responseData;

    public ServiceImpl(Context context) {
        this.context = context;
        this.application = (MyApplication) context.getApplicationContext();
        this.responseResult = (OnResponseResult) context;
        this.handler = new Handler(this);
        this.dialogUtil = new DialogUtil(context, R.string.dialog_load);
        this.responseData = new ArrayList<Object>();
    }

    public DialogUtil getDialog() {
        return dialogUtil;
    }

    private synchronized void doGet(final String url, final int tag) {
        dialogUtil.showDialog();
        new Thread(new Runnable() {

            @Override
            public void run() {
                String response = HttpRequestTools.doGet(context, url,
                        application.preferences.getString("token", ""));
                message = handler.obtainMessage();
                message.what = tag;
                if (responseData != null && responseData.size() > 0) {
                    responseData.clear();
                }
                responseData.add(response);
                responseData.add(HttpRequestTools.statusCode);
                message.obj = responseData;
                message.sendToTarget();
            }
        }).start();
    }

    private synchronized void doPost(final String url,
                                     final Map<String, Object> params, final Map<String, File> files,
                                     final int tag) {
        dialogUtil.showDialog();
        new Thread(new Runnable() {

            @Override
            public void run() {
                String response = HttpRequestTools.doPost(context, url, params,
                        files, application.preferences.getString("token", ""));
                message = handler.obtainMessage();
                message.what = tag;
                if (responseData != null && responseData.size() > 0) {
                    responseData.clear();
                }
                responseData.add(response);
                responseData.add(HttpRequestTools.statusCode);
                message.obj = responseData;
                message.sendToTarget();
            }
        }).start();
    }

    private synchronized void doPut(final String url,
                                    final Map<String, Object> params, final Map<String, File> files,
                                    final int tag) {
        dialogUtil.showDialog();
        new Thread(new Runnable() {

            @Override
            public void run() {
                String response = HttpRequestTools.doPut(context, url, params,
                        files, application.preferences.getString("token", ""));
                message = handler.obtainMessage();
                message.what = tag;
                if (responseData != null && responseData.size() > 0) {
                    responseData.clear();
                }
                responseData.add(response);
                responseData.add(HttpRequestTools.statusCode);
                message.obj = responseData;
                message.sendToTarget();
            }
        }).start();
    }

    private synchronized void doDelete(final String url, final int tag) {
        dialogUtil.showDialog();
        new Thread(new Runnable() {

            @Override
            public void run() {
                String response = HttpRequestTools.doDelete(context, url,
                        application.preferences.getString("token", ""));
                message = handler.obtainMessage();
                message.what = tag;
                if (responseData != null && responseData.size() > 0) {
                    responseData.clear();
                }
                responseData.add(response);
                responseData.add(HttpRequestTools.statusCode);
                message.obj = responseData;
                message.sendToTarget();
            }
        }).start();

    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized boolean handleMessage(Message msg) {
        List<Object> response = (List<Object>) msg.obj;
        Log.i("**********", "*****************");
        if (response != null) {
            responseResult.onResult((String) response.get(0), msg.what,
                    (Integer) response.get(1));
            dialogUtil.closeDialog();
        } else {
            dialogUtil.closeDialog();
            ToastUtil.show(context, R.string.toast_log_net_exception);
        }
        return false;
    }

    /**
     * 请求响应接口
     *
     * @author YANGBANG
     */
    public interface OnResponseResult {
        /**
         * 服务器响应接口
         *
         * @param result 结果String
         * @param tag    唯一标识 标签 Constant类中取得
         * @param status 状态码
         */
        public void onResult(String result, int tag, int status);
    }

    @Override
    public void getHotDeals(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getTopic(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getShopDetail(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getCato(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getShopComment(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getReleaseShopComment(String url, Map<String, Object> params,
                                      Map<String, File> files, int tag) {
        doPost(url, params, files, tag);
    }

    @Override
    public void getCouponDetail(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getReadList(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getTopicDetail(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getSortShop(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getAddTopic(String url, Map<String, Object> params,
                            Map<String, File> files, int tag) {
        doPost(url, params, files, tag);

    }

    @Override
    public void getReplyTopic(String url, Map<String, Object> params,
                              Map<String, File> files, int tag) {

        doPost(url, params, files, tag);
    }

    @Override
    public void getClickPraise(String url, Map<String, Object> params,
                               Map<String, File> files, int tag) {

        doPut(url, params, files, tag);
    }

    @Override
    public void getNearSort(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getRestsSort(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getAddMerchantCollect(String url, Map<String, Object> params,
                                      Map<String, File> files, int tag) {
        doPost(url, params, files, tag);
    }

    @Override
    public void getQueryMerchantCollect(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getAddCouponsCollect(String url, Map<String, Object> params,
                                     Map<String, File> files, int tag) {
        doPost(url, params, files, tag);
    }

    @Override
    public void getQueryCouponsCollect(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getAddAttentionTopic(String url, Map<String, Object> params,
                                     Map<String, File> files, int tag) {
        doPost(url, params, files, tag);
    }

    @Override
    public void getQueryAttentionTopic(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getRemoveAttentionTopic(String url, int tag) {
        doDelete(url, tag);
    }

    @Override
    public void getRemoveMerchantCollect(String url, int tag) {
        doDelete(url, tag);
    }

    @Override
    public void getRemoveCouponsCollect(String url, int tag) {
        doDelete(url, tag);
    }

    @Override
    public void getQueryMyTopic(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getQueryMyReply(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getReplyFloor(String url, Map<String, Object> params,
                              Map<String, File> files, int tag) {
        doPost(url, params, files, tag);
    }

    @Override
    public void getTopicFloor(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getFeedBack(String url, Map<String, Object> params,
                            Map<String, File> files, int tag) {
        doPost(url, params, files, tag);
    }

    @Override
    public void getUserInfo(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getSignIn(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getOtherUserInfo(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getOtherUserTpoic(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getAddUserAttention(String url, Map<String, Object> params,
                                    Map<String, File> files, int tag) {
        doPut(url, params, files, tag);
    }

    @Override
    public void getRemoveUserAttention(String url, int tag) {
        doDelete(url, tag);
    }

    @Override
    public void getQueryMyAttention(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getQueryMyFans(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getSearchShop(String url, Map<String, Object> params,
                              Map<String, File> files, int tag) {
        doPost(url, params, files, tag);
    }

    @Override
    public void getEditUserInfo(String url, Map<String, Object> params,
                                Map<String, File> files, int tag) {
        doPut(url, params, files, tag);
    }

    @Override
    public void getStatisticses(String url, Map<String, Object> params,
                                Map<String, File> files, int tag) {
        doPost(url, params, files, tag);
    }

    @Override
    public void getMoreShop(String url, int tag) {
        doGet(url, tag);
    }

    @Override
    public void getMessageCentre(String url, int tag) {
        doGet(url, tag);
    }
}

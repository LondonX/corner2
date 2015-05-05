package com.ljmob.corner.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.widget.ImageView;

import com.ljmob.corner.cache.ImageLoader;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthTool {
    public static final String ACTION_CH_USER = "com.ljmob.corner.ACTION_CH_USER";
    public static final String REF_TYPE_QQ = "qq";
    public static final String REF_TYPE_WX = "wechat";
    public static final String REF_TYPE_WB = "sinablog";
    public static final String SEX_M = "male";
    public static final String SEX_F = "female";

    public String URL = "auth/login";

    public String ref_type;
    public String ref_account;
    public String nickname;
    public String sex;
    public String avatar;
    public String max_avatar;
    public String signature;
    public String token;

    private Context context;
    private MyApplication application;

    public AuthTool(Context context, String ref_type, String ref_account,
                    String nickname, String sex, String avatar, String max_avatar,
                    String signature, String token) {
        super();
        URL = UrlStaticUtil.root_url + URL;
        this.context = context;
        this.ref_type = ref_type;
        this.ref_account = ref_account;
        this.nickname = nickname;
        this.sex = sex;
        this.avatar = avatar;
        this.max_avatar = max_avatar;
        this.signature = signature;
        this.token = token;
        this.application = (MyApplication) context.getApplicationContext();
    }

    public String startAuth() {
        String retString = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ref_type", ref_type);
        params.put("ref_account", ref_account);
        params.put("nickname", nickname);
        params.put("sex", sex);
        params.put("avatar", avatar);
        params.put("max_avatar", max_avatar);
        params.put("signature", signature);
        retString = HttpRequestTools.doPost(context, URL, params, null, token);
        updateUserInfo(retString);
        return retString;
    }

    private void updateUserInfo(String retString) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(retString);
            jsonObject = jsonObject.getJSONObject("user");
            Editor editor = application.preferences.edit();
            editor.putInt("id", jsonObject.getInt("id"));
            editor.putString("ref_type", ref_type);
            editor.putString("ref_account", ref_account);
            editor.putString("nickname", jsonObject.getString("nickname"));
            editor.putString("sex", jsonObject.getString("sex"));
            editor.putString("avatar", jsonObject.getString("avatar"));
            editor.putString("max_avatar", jsonObject.getString("max_avatar"));
            editor.putString("signature", jsonObject.getString("signature"));
            editor.putString("token", token);
            PushAgent mPushAgent = PushAgent.getInstance(context);

            // 下载头像
            new ImageLoader(context).DisplayImage(UrlStaticUtil.root_photo
                            + jsonObject.getString("avatar"), new ImageView(context),
                    true, ImageLoader.M_PICTURE);

            mPushAgent
                    .addAlias(jsonObject.getInt("id") + "", ALIAS_TYPE.WEIXIN);
            editor.putString("Alias", jsonObject.getInt("id") + "");
            editor.apply();
            context.sendBroadcast(new Intent(ACTION_CH_USER));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

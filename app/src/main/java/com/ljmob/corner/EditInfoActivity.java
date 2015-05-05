package com.ljmob.corner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.AvatarSelectTool;
import com.ljmob.corner.util.BitmapTool;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;

public class EditInfoActivity extends ActionBarActivity implements OnClickListener,
        TextWatcher, OnResponseResult {
    private ImageView activity_editinfo_imgHead;// 头像
    private EditText activity_editinfo_etName;
    private LinearLayout activity_editinfo_linearSexMen;
    private RadioButton activity_editinfo_rbSexMen;// 男
    private ImageView activity_editinfo_imgSexMen;// 男
    private LinearLayout activity_editinfo_linearSexFemal;
    private RadioButton activity_editinfo_rbSexFemal;// 女
    private ImageView activity_editinfo_imgSexFemal;// 女
    private ImageView activity_editinfo_imgRelatQQ;// 关联账号
    private ImageView activity_editinfo_imgRelatWeibo;
    private ImageView activity_editinfo_imgRelatWeixin;
    private EditText activity_editinfo_etSign;// 个人签名
    private TextView activity_editinfo_tvMax;// 字数限制
    private ImageLoader imageLoader;
    private ServiceImpl serviceImpl;
    private MyApplication application;
    private SharedPreferences preferences;
    private AvatarSelectTool avatarSelect;
    private boolean isEditChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editinfo);
        application = (MyApplication) getApplication();
        imageLoader = new ImageLoader(this);
        serviceImpl = FactoryService.getIService(this);
        preferences = application.preferences;
        initToolBar();
        initView();
        setUserInfo();
    }

    private void submitUserInfo() {
        String nickname = activity_editinfo_etName.getText().toString();
        String sex = "male";
        if (activity_editinfo_rbSexMen.isChecked()) {
            sex = "male";
        } else {
            sex = "female";
        }
        if (!nickname.equals("")) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("nickname", nickname);
            params.put("signature", activity_editinfo_etSign.getText()
                    .toString());
            params.put("sex", sex);
            Map<String, File> files = new HashMap<String, File>();
            if (avatarSelect != null) {
                files.put("avatar", avatarSelect.getTempImgFile());
            } else {
                params.put("avatar", "");
            }
            serviceImpl.getEditUserInfo(UrlStaticUtil.getEditUserInfo(),
                    params, files, Constants.EDIT_USER_INFO);
        } else {
            ToastUtil.show(this, R.string.toast_error_name);
        }
    }

    private void initView() {
        activity_editinfo_imgHead = (ImageView) this
                .findViewById(R.id.activity_editinfo_imgHead);
        activity_editinfo_etName = (EditText) this
                .findViewById(R.id.activity_editinfo_etName);
        activity_editinfo_imgRelatQQ = (ImageView) this
                .findViewById(R.id.activity_editinfo_imgRelatQQ);
        activity_editinfo_imgRelatWeibo = (ImageView) this
                .findViewById(R.id.activity_editinfo_imgRelatWeibo);
        activity_editinfo_imgRelatWeixin = (ImageView) this
                .findViewById(R.id.activity_editinfo_imgRelatWeixin);
        activity_editinfo_etSign = (EditText) this
                .findViewById(R.id.activity_editinfo_etSign);
        activity_editinfo_tvMax = (TextView) this
                .findViewById(R.id.activity_editinfo_tvMax);
        activity_editinfo_rbSexMen = (RadioButton) this
                .findViewById(R.id.activity_editinfo_rbSexMen);
        activity_editinfo_rbSexFemal = (RadioButton) this
                .findViewById(R.id.activity_editinfo_rbSexFemal);
        activity_editinfo_linearSexMen = (LinearLayout) this
                .findViewById(R.id.activity_editinfo_linearSexMen);
        activity_editinfo_linearSexFemal = (LinearLayout) this
                .findViewById(R.id.activity_editinfo_linearSexFemal);

        activity_editinfo_linearSexMen.setOnClickListener(this);
        activity_editinfo_linearSexFemal.setOnClickListener(this);
        activity_editinfo_rbSexMen.setOnClickListener(this);
        activity_editinfo_rbSexFemal.setOnClickListener(this);
        activity_editinfo_imgHead.setOnClickListener(this);
    }

    private void setUserInfo() {
        imageLoader.DisplayImage(
                UrlStaticUtil.root_photo + preferences.getString("avatar", ""),
                activity_editinfo_imgHead, true, ImageLoader.L_PICTURE);
        activity_editinfo_etName.setText(preferences.getString("nickname", ""));
        activity_editinfo_etSign
                .setText(preferences.getString("signature", ""));
        String sex = preferences.getString("sex", "");
        if (sex.equals("male")) {
            activity_editinfo_rbSexMen.setChecked(true);
            activity_editinfo_rbSexFemal.setChecked(false);
        } else {
            activity_editinfo_rbSexMen.setChecked(false);
            activity_editinfo_rbSexFemal.setChecked(true);
        }
        activity_editinfo_tvMax.setText(activity_editinfo_etSign.length()
                + "/18");
        activity_editinfo_etSign.addTextChangedListener(this);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_editinfo);
    }

    // 当内容发送改变时候调用
    public void showSaveDialog() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("内容已发生改变，是否放弃本次修改？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditInfoActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create();
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (isEditChanged) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.menu_ok_ok) {
            Log.i("YANGBANG", "提交信息");
            submitUserInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_editinfo_linearSexMen:
                activity_editinfo_rbSexMen.setChecked(true);
                activity_editinfo_rbSexFemal.setChecked(false);
                isEditChanged = true;
                break;
            case R.id.activity_editinfo_linearSexFemal:
                activity_editinfo_rbSexMen.setChecked(false);
                activity_editinfo_rbSexFemal.setChecked(true);
                isEditChanged = true;
                break;
            case R.id.activity_editinfo_rbSexMen:
                activity_editinfo_rbSexMen.setChecked(true);
                activity_editinfo_rbSexFemal.setChecked(false);
                isEditChanged = true;
                break;
            case R.id.activity_editinfo_rbSexFemal:
                activity_editinfo_rbSexMen.setChecked(false);
                activity_editinfo_rbSexFemal.setChecked(true);
                isEditChanged = true;
                break;
            case R.id.activity_editinfo_imgHead:
                avatarSelect = new AvatarSelectTool(this);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK
                && requestCode == AvatarSelectTool.CROP_COMPLETE_CODE) {
            if (avatarSelect == null) {
                Toast.makeText(this, "图片剪辑发生错误，请重试。", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            Bitmap bmp = null;
            bmp = BitmapFactory.decodeFile(avatarSelect.getTempImgFile()
                    .getAbsolutePath());
            if (bmp != null) {
                bmp = BitmapTool.getRoundCornerBitmap(bmp, bmp.getWidth() / 2);
                avatarSelect.setAvatar(bmp);
                activity_editinfo_imgHead.setImageBitmap(bmp);
                isEditChanged = true;
                // saveToService();
            } else {
                avatarSelect.setAvatar(null);
            }

        }
        if (resultCode == RESULT_CANCELED) {
            avatarSelect.getTempImgFile().delete();
            avatarSelect.setAvatar(null);
        }

        if (requestCode == AvatarSelectTool.AVATAR_CAMERA_CROP_CODE)// 调用裁剪
        {
            avatarSelect.startPhotoZoom(Uri.fromFile(avatarSelect
                    .getTempImgFile()));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (avatarSelect != null) {
            AvatarSelectTool.deleteDirectory(avatarSelect.getFilePath());
        }
        super.onDestroy();
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.EDIT_USER_INFO) {
                ToastUtil.show(this, R.string.toast_ok_editinfo);
                application.isUpdataUserInfo = true;
                this.finish();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isEditChanged = true;
        int len = s.length();
        activity_editinfo_tvMax.setText(len + "/18");
        if (len >= 19) {
            activity_editinfo_etSign.setText(activity_editinfo_etSign.getText()
                    .toString().substring(0, 18));
            activity_editinfo_etSign.setSelection(activity_editinfo_etSign
                    .length());
        }
        Log.i("YANGBANG", "s=" + s + ",start=" + start + ",before=" + before
                + ",count=" + count);

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

}

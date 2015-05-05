package com.ljmob.corner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ljmob.corner.adapter.DialogAdapter;
import com.ljmob.corner.adapter.EmojiAdapter;
import com.ljmob.corner.cache.FileCache;
import com.ljmob.corner.entity.DCIMPhoto;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.AvatarSelectTool;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.InputWindowTool;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;

public class AddTopicActivity extends Activity implements OnClickListener,
        OnFocusChangeListener, OnItemClickListener, OnResponseResult,
        DialogAdapter.OnDialogItemClickListener {
    private static final int REQUEST_CODE_PICK_IMAGE = 123;
    private static final int REQUEST_CODE_CAMERA_IMAGE = 321;
    private EditText activity_addtopic_etTitle;
    private EditText activity_addtopic_etContent;
    private GridView activity_addtopic_gridEmoji;

    private ServiceImpl serviceImpl;
    private MyApplication application;
    private ArrayList<DCIMPhoto> photos;

    private LinearLayout activity_addtopic_linearImgRoot;
    private int tag = 0;
    private Map<Integer, View> imgViews = new HashMap<Integer, View>();
    private File tempDir;
    private File tempFile;
    private InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtopic);
        application = (MyApplication) getApplication();
        inputManager = (InputMethodManager) getApplication().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        initView();
        serviceImpl = new ServiceImpl(this);
        photos = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        File file = new File(new FileCache(this).getSaveFilePath()
                + "/TempImage");
        if (file.exists()) {
            AvatarSelectTool.deleteDirectory(file.getAbsolutePath());
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bitmap bm = null;
            String path;
            File tempFile;
            DCIMPhoto photo = null;
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                try {
                    Uri originalUri = data.getData(); // 获得图片的uri
                    String[] proj = {MediaStore.Images.Media.DATA};
                    ContentResolver contentResolver = this.getContentResolver();
                    Cursor cursor = contentResolver.query(originalUri, proj,
                            null, null, null);
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    path = cursor.getString(column_index);
                    bm = decodeFile(new File(path));
                    File file = new File(new FileCache(this).getSaveFilePath()
                            + "/TempImage");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    tempFile = new File(file.getAbsolutePath(), "temp_img"
                            + SystemClock.currentThreadTimeMillis() + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(
                            tempFile);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    photo = new DCIMPhoto();
                    photo.index = tag;
                    photo.bitmap = bm;
                    photo.path = path;
                    photo.imgFile = tempFile;
                    photos.add(photo);

                } catch (Exception e) {
                    Log.e("TAG", e.toString());
                }
            } else if (requestCode == REQUEST_CODE_CAMERA_IMAGE) {
                photo = new DCIMPhoto();
                photo.index = tag;
                // photo.bitmap = BitmapTool.read(new FileInputStream(
                // AddTopicActivity.this.tempFile), Config.ARGB_8888);
                photo.bitmap = decodeFile(AddTopicActivity.this.tempFile);
                photo.path = AddTopicActivity.this.tempFile.getAbsolutePath();
                photo.imgFile = AddTopicActivity.this.tempFile;
                photos.add(photo);
            }
            if (photo == null) {
                ToastUtil.show(this, R.string.toast_img_error);
                return;
            }
            Log.i("photos.size", photos.size() + "");
            View addTopicImgView = LayoutInflater.from(this).inflate(
                    R.layout.item_addtopic, null);
            ImageView griditem_addtopic_img = (ImageView) addTopicImgView
                    .findViewById(R.id.griditem_addtopic_img);
            ImageView griditem_addtopic_imgDel = (ImageView) addTopicImgView
                    .findViewById(R.id.griditem_addtopic_imgDel);
            griditem_addtopic_img.setImageBitmap(photo.bitmap);
            griditem_addtopic_imgDel.setTag(tag);
            addTopicImgView.setTag(photo);
            imgViews.put(tag, addTopicImgView);
            griditem_addtopic_imgDel
                    .setOnClickListener(new MyOnDelImgClickListener());
            activity_addtopic_linearImgRoot.addView(addTopicImgView);
            tag++;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        findViewById(R.id.activity_addtopic_imgClose).setOnClickListener(this);
        findViewById(R.id.activity_addtopic_imgPic).setOnClickListener(this);
        findViewById(R.id.activity_addtopic_imgSound).setOnClickListener(this);
        findViewById(R.id.activity_addtopic_imgEmoji).setOnClickListener(this);
        findViewById(R.id.activity_addtopic_tvCommit).setOnClickListener(this);
        activity_addtopic_etTitle = (EditText) findViewById(R.id.activity_addtopic_etTitle);
        activity_addtopic_etContent = (EditText) findViewById(R.id.activity_addtopic_etContent);
        activity_addtopic_gridEmoji = (GridView) findViewById(R.id.activity_addtopic_gridEmoji);
        activity_addtopic_linearImgRoot = (LinearLayout) findViewById(R.id.activity_addtopic_linearImgRoot);
        activity_addtopic_gridEmoji.setOnItemClickListener(this);
        activity_addtopic_linearImgRoot.setOnClickListener(this);
        activity_addtopic_etTitle.setOnClickListener(this);
        activity_addtopic_etContent.setOnClickListener(this);
        activity_addtopic_etTitle.setOnFocusChangeListener(this);
        activity_addtopic_etContent.setOnFocusChangeListener(this);
    }

    private void addTopic() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("subject", activity_addtopic_etTitle.getText().toString());
        params.put("content", activity_addtopic_etContent.getText().toString());
        params.put("photo_size", photos.size());
        Map<String, File> files = new HashMap<String, File>();
        for (int i = 0; i < photos.size(); i++) {
            files.put("photo" + i, photos.get(i).imgFile);
        }
        serviceImpl.getDialog().setMsg(R.string.dialog_send_topic);
        serviceImpl.getAddTopic(UrlStaticUtil.getAddTopic(), params, files,
                Constants.ADD_TOPIC);
    }

    private void showSelectDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(this);
        dialog.titleColorRes(R.color.red);
        dialog.accentColorRes(R.color.red);
        dialog.title(R.string.dialog_add_chose_pic_title);
        String strItem[] = {getString(R.string.dialog_add_chose_camera),
                getString(R.string.dialog_add_chose_album)};
        DialogAdapter adapter = new DialogAdapter(this, strItem);
        dialog.adapter(adapter);
        adapter.setOnDialogItemClickListener(this);
        dialog.show();
    }

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private Bitmap decodeFile(File f) {
        try {
            // 解码图片长度
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 240;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // 解码缩小倍数后的图片
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, o2);
            return bm;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDialogListClick(int index) {
        {
            switch (index) {
                case 0:
                    if (tempDir == null) {
                        tempDir = new File(new FileCache(AddTopicActivity.this)
                                .getSaveFilePath() + "/TempImage");
                    }
                    if (tempDir.exists() == false) {
                        tempDir.mkdirs();
                    }
                    tempFile = new File(tempDir.getAbsoluteFile()
                            + File.separator + "temp_"
                            + System.currentTimeMillis() + ".jpg");
                    // 调用相机
                    Intent intentCamera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE, null);
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(tempFile));
                    startActivityForResult(intentCamera,
                            REQUEST_CODE_CAMERA_IMAGE);
                    break;
                case 1:
                    Intent intentPhoto = new Intent(Intent.ACTION_PICK);
                    intentPhoto.setDataAndType(
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(intentPhoto, REQUEST_CODE_PICK_IMAGE);
                    break;

                default:
                    break;
            }
        }
    }

    private class MyOnDelImgClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Log.i("tag:...", (Integer) v.getTag() + "");
            activity_addtopic_linearImgRoot.removeView(imgViews.get((Integer) v
                    .getTag()));
            if (photos.size() >= 0) {
                photos.clear();
            }
            for (int i = 0; i < activity_addtopic_linearImgRoot.getChildCount(); i++) {
                photos.add((DCIMPhoto) imgViews.get(i).getTag());
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_addtopic_imgClose:
                onBackPressed();
                break;
            case R.id.activity_addtopic_imgPic:
                showSelectDialog();
                break;
            case R.id.activity_addtopic_imgSound:
                break;
            case R.id.activity_addtopic_etTitle:
                if (activity_addtopic_gridEmoji.getVisibility() == View.VISIBLE) {
                    activity_addtopic_gridEmoji.setVisibility(View.GONE);
                }
            case R.id.activity_addtopic_etContent:
                if (activity_addtopic_gridEmoji.getVisibility() == View.VISIBLE) {
                    activity_addtopic_gridEmoji.setVisibility(View.GONE);
                }
                break;
            case R.id.activity_addtopic_imgEmoji:
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                activity_addtopic_gridEmoji.setVisibility(View.VISIBLE);
                activity_addtopic_gridEmoji.setAdapter(new EmojiAdapter(this));
                break;
            case R.id.activity_addtopic_tvCommit:// 发帖
                Log.i("photos.seze", photos.size() + "");
                if (new SelectLoginWay(this).isLogin()) {
                    if (activity_addtopic_etTitle.getText().toString().length() > 0) {
                        addTopic();
                    } else {
                        ToastUtil.show(this, R.string.toast_error_title);
                    }
                }
                break;
            case R.id.activity_addtopic_linearImgRoot:
                if (activity_addtopic_linearImgRoot.getChildCount() == 0) {
                    InputWindowTool.OpenInputWindow(activity_addtopic_etContent,
                            AddTopicActivity.this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.ADD_TOPIC) {
                ToastUtil.show(this, R.string.toast_ok_addtopic);
                application.isAddtopicSucceed = true;
                AddTopicActivity.this.finish();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        EmojiAdapter adapter = (EmojiAdapter) parent.getAdapter();
        String emoji = (String) adapter.getItem(position);
        if (activity_addtopic_etTitle.isFocused()) {
            activity_addtopic_etTitle.setText(activity_addtopic_etTitle
                    .getText().toString() + emoji);
            activity_addtopic_etTitle.setSelection(activity_addtopic_etTitle
                    .length());
        } else if (activity_addtopic_etContent.isFocused()) {
            activity_addtopic_etContent.setText(activity_addtopic_etContent
                    .getText().toString() + emoji);
            activity_addtopic_etContent
                    .setSelection(activity_addtopic_etContent.length());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (activity_addtopic_gridEmoji.getVisibility() == View.VISIBLE) {
                activity_addtopic_gridEmoji.setVisibility(View.GONE);
            }
        }
    }

    // 当内容发送改变时候调用
    public void showSaveDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.dialog_add_exit_title);
        builder.content(R.string.dialog_add_exit_content);
        builder.accentColorRes(R.color.red);
        builder.positiveText(R.string.dialog_update_yes);
        builder.negativeText(R.string.dialog_update_no);
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                AddTopicActivity.this.finish();
                super.onPositive(dialog);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        showSaveDialog();
    }
}

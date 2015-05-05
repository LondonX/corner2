package com.ljmob.corner.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;

import com.ljmob.corner.cache.FileCache;

import java.io.File;

/**
 * 头像选择工具类
 *
 * @author YANGBANG
 */
public class AvatarSelectTool {
    public static final int AVATAR_CAMERA_CROP_CODE = 2;
    public static final int CROP_COMPLETE_CODE = 111;
    private File file;
    private File tempImgFile;
    private int crop = 180;// 裁剪长宽为180像素的图片
    private Bitmap bitAvatar;
    private Activity activity;
    private FileCache fileCache;

    public AvatarSelectTool(Activity activity) {
        this.activity = activity;
        fileCache = new FileCache(activity);
        mkdir();
        showSelectDialog();
    }

    /**
     * 取得文件夹路径
     *
     * @return
     */
    public String getFilePath() {
        // if (hasSDCard()) {
        // return Environment.getExternalStorageDirectory().getAbsolutePath()
        // + "/NobunagaNoYabou/imgAvatar";
        // } else {
        // return Environment.getDataDirectory().getAbsolutePath()
        // + "/data/NobunagaNoYabou/imgAvatar";
        // }
        return fileCache.getSaveFilePath() + "/TempAvatar";
    }

    private void mkdir() {
        file = new File(getFilePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        tempImgFile = new File(file.getAbsoluteFile(), "temp_pic_"
                + SystemClock.currentThreadTimeMillis() + ".jpg");
    }

    private void showSelectDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("选择头像");
        String strItem[] = {"相机", "相册"};
        dialog.setItems(strItem, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                switch (arg1) {
                    case 0:
                        // 调用相机
                        Intent intentCamera = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE, null);
                        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(tempImgFile));
                        activity.startActivityForResult(intentCamera,
                                AVATAR_CAMERA_CROP_CODE);
                        break;
                    case 1:
                        Intent intentPhoto = new Intent(Intent.ACTION_PICK);
                        intentPhoto.setDataAndType(
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                                "image/*");
                        intentPhoto.putExtra("output", Uri.fromFile(tempImgFile));
                        intentPhoto.putExtra("crop", "true");
                        intentPhoto.putExtra("aspectX", 1);// 裁剪框比例
                        intentPhoto.putExtra("aspectY", 1);
                        intentPhoto.putExtra("outputX", crop);// 输出图片大小
                        intentPhoto.putExtra("outputY", crop);
                        intentPhoto.putExtra("outputFormat", "JPEG");
                        activity.startActivityForResult(intentPhoto,
                                CROP_COMPLETE_CODE);
                        break;

                    default:
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }

    /**
     * 取得头像
     *
     * @return bitmap头像
     */
    public Bitmap getAvatar() {
        return bitAvatar;
    }

    /**
     * 设置头像
     *
     * @param bitmap
     */
    public void setAvatar(Bitmap bitmap) {
        bitAvatar = bitmap;
    }

    /**
     * 取得临时图片文件
     *
     * @return 临时图片文件
     */
    public File getTempImgFile() {
        return tempImgFile;
    }

    /**
     * 相机拍完照后进行照片裁剪
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", crop);
        intent.putExtra("outputY", crop);
        intent.putExtra("output", Uri.fromFile(tempImgFile));
        intent.putExtra("outputFormat", "JPEG");
        activity.startActivityForResult(intent, CROP_COMPLETE_CODE);
    }

    /**
     * 删除所有子目录图片
     *
     * @param filePath
     * @return
     */
    public static boolean deleteDirectory(String filePath) {
        if (null == filePath) {
            return false;
        }

        File file = new File(filePath);

        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] list = file.listFiles();

            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    deleteDirectory(list[i].getAbsolutePath());
                } else {
                    list[i].delete();
                }
            }
        }
        return true;
    }

}

package com.ljmob.corner.entity;

import android.app.Activity;
import android.view.View;

import com.ljmob.corner.cache.FileCache;

public class Shareable {
    private String text;
    private Activity activity;
    private View shareableView;
    private String imgFilePath;
    private String imgUrl;
    private String targetUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.ljmob.corner";

    public Shareable(Activity activity) {
        this.activity = activity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getImgFilePath() {
        return imgFilePath;
    }

    public void setImgFilePath(String imgFilePath) {
        this.imgFilePath = imgFilePath;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        this.setImgFilePath(new FileCache(activity).getFile(imgUrl).getAbsolutePath());
    }

    public View getShareableView() {
        return shareableView;
    }

    public void setShareableView(View shareableView) {
        this.shareableView = shareableView;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}

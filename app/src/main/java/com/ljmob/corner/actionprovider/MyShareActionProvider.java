package com.ljmob.corner.actionprovider;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.ShareActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

import com.ljmob.corner.R;
import com.ljmob.corner.entity.Shareable;
import com.ljmob.corner.util.BitmapTool;
import com.ljmob.corner.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class MyShareActionProvider extends ShareActionProvider {
    private Context context;
    private ArrayList<Action> actions;
    private Shareable shareable;

    public MyShareActionProvider(Context context) {
        super(context);
        this.context = context;
        actions = new ArrayList<Action>();
        Action action0 = new Action();
        action0.title = R.string.share_qq;
        action0.tag = R.string.share_qq;
        action0.icon = R.drawable.ic_menu_qq;
        actions.add(action0);
        Action action1 = new Action();
        action1.title = R.string.share_weixin;
        action1.tag = R.string.share_weixin;
        action1.icon = R.drawable.ic_menu_weixin;
        actions.add(action1);
        Action action2 = new Action();
        action2.title = R.string.share_weibo;
        action2.tag = R.string.share_weibo;
        action2.icon = R.drawable.ic_menu_weibo;
        actions.add(action2);
        Action action3 = new Action();
        action3.title = R.string.share_qzone;
        action3.tag = R.string.share_qzone;
        action3.icon = R.drawable.ic_menu_qzone;
        actions.add(action3);
        Action action4 = new Action();
        action4.title = R.string.share_weixin_scene;
        action4.tag = R.string.share_weixin_scene;
        action4.icon = R.drawable.ic_menu_moments;
        actions.add(action4);
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    public void setShareable(Shareable shareable) {
        this.shareable = shareable;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        for (Action action : actions) {
            MenuItem item;
            if (action.title == -1) {
                item = subMenu.add(action.titleString);
            } else {
                item = subMenu.add(action.title);
            }
            if (action.icon != -1) {
                action.iconDrawable = (BitmapDrawable) context.getResources()
                        .getDrawable(action.icon);
                action.iconDrawable.setColorFilter(context.getResources()
                        .getColor(R.color.gray_dark), Mode.SRC_IN);
            }
            if (action.iconDrawable != null) {
                item.setIcon(action.iconDrawable);
            }
            item.setOnMenuItemClickListener(new OnShareMenuItemClickListener(
                    action.tag));
        }
    }

    private class OnShareMenuItemClickListener implements
            OnMenuItemClickListener {
        private int tag;

        public OnShareMenuItemClickListener(int tag) {
            this.tag = tag;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ToastUtil.show(context, R.string.toast_sharing);
            if (shareable == null) {
                return false;
            }
            switch (tag) {
                case R.string.share_qq:
                    share(QQ.NAME, shareable);
                    break;
                case R.string.share_qzone:
                    share(QZone.NAME, shareable);
                    break;
                case R.string.share_weixin:
                    share(Wechat.NAME, shareable);
                    break;
                case R.string.share_weixin_scene:
                    share(WechatMoments.NAME, shareable);
                    break;
                case R.string.share_weibo:
                    share(SinaWeibo.NAME, shareable);
                    break;
                default:
                    break;
            }
            return false;
        }

        private void share(String platformName, Shareable shareable) {
            File imgFile = BitmapTool.saveView2File(shareable.getShareableView());
            OnekeyShare oks = new OnekeyShare();
            oks.setTitle(context.getString(R.string.share_title));
            oks.setText(shareable.getText()+"\n"+shareable.getTargetUrl());
            oks.setImagePath(imgFile.getAbsolutePath());
            oks.setTitleUrl(shareable.getTargetUrl());
            oks.setPlatform(platformName);
            oks.show(context);
        }
    }
}

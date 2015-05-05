package com.ljmob.corner.actionprovider;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.UrlStaticUtil;

public class MainActionProvider extends ActionProvider {
    private ArrayList<Action> actions;
    private boolean isLoged;
    private Context context;
    private SharedPreferences preferences;
    private ImageLoader imageLoader;

    public MainActionProvider(Context context) {
        super(context);
        this.context = context;
        imageLoader = new ImageLoader(context);
        preferences = ((MyApplication) context.getApplicationContext()).preferences;
        actions = new ArrayList<Action>();
        Action action0 = new Action();
        action0.title = R.string.menu_login_qq;
        action0.tag = R.string.menu_login_qq;
        action0.icon = R.drawable.ic_menu_qq;
        actions.add(action0);
        Action action1 = new Action();
        action1.title = R.string.menu_login_weixin;
        action1.tag = R.string.menu_login_weixin;
        action1.icon = R.drawable.ic_menu_weixin;
        actions.add(action1);
        Action action2 = new Action();
        action2.title = R.string.menu_login_weibo;
        action2.tag = R.string.menu_login_weibo;
        action2.icon = R.drawable.ic_menu_weibo;
        actions.add(action2);
        Action action3 = new Action();
        action3.titleString = context.getString(R.string.test);
        action3.tag = Action.TAG_USER;
        action3.icon = R.drawable.ic_menu_head_default;
        actions.add(action3);
        Action action4 = new Action();
        action4.title = R.string.menu_msg;
        action4.tag = R.string.menu_msg;
        action4.icon = R.drawable.ic_menu_msg;
        actions.add(action4);
        Action action5 = new Action();
        action5.title = R.string.menu_collect;
        action5.tag = R.string.menu_collect;
        action5.icon = R.drawable.ic_menu_collect;
        actions.add(action5);
        Action action6 = new Action();
        action6.title = R.string.menu_topic_focused;
        action6.tag = R.string.menu_topic_focused;
        action6.icon = R.drawable.ic_menu_focus;
        actions.add(action6);
        Action action7 = new Action();
        action7.title = R.string.menu_feedback;
        action7.tag = R.string.menu_feedback;
        action7.icon = R.drawable.ic_menu_feedback;
        actions.add(action7);
        Action action8 = new Action();
        action8.title = R.string.menu_settings;
        action8.tag = R.string.menu_settings;
        action8.icon = R.drawable.ic_menu_setting;
        actions.add(action8);

        logStateChange();
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    public void logStateChange() {
        String account = preferences.getString("ref_account", null);
        isLoged = account != null;
        Action userAction = actions.get(3);
        if (isLoged) {
            imageLoader.DisplayImage(
                    UrlStaticUtil.root_photo
                            + preferences.getString("avatar", ""),
                    new ImageView(context), true, ImageLoader.M_PICTURE);
            userAction.iconDrawable = new BitmapDrawable(
                    context.getResources(), imageLoader.getBitmap());
            userAction.icon = -1;
        } else {
            userAction.icon = R.drawable.ic_menu_head_default;
            if (userAction.iconDrawable != null
                    && userAction.iconDrawable.getBitmap() != null) {
                userAction.iconDrawable.getBitmap().recycle();
            }
            userAction.iconDrawable = null;
        }
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        logStateChange();
        for (int index = 0; index < actions.size(); index++) {
            if (isLoged && index < 3) {
                continue;
            } else if (isLoged == false && index > 2 && index < 7) {
                continue;
            }
            Action action = actions.get(index);
            if (index == 3) {
                action.titleString = preferences.getString("nickname", "");
            }
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
            item.setOnMenuItemClickListener(new ActionClickListener(context,
                    action.tag));
        }
    }
}

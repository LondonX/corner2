package com.ljmob.corner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lanjing.galleryView.ImageGalleryActivity;
import com.ljmob.corner.adapter.TopicAdapter;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Topic;
import com.ljmob.corner.entity.UserInfo;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;

public class OthersUserInfoActivity extends ActionBarActivity implements
        OnClickListener, OnItemClickListener, OnResponseResult {
    private ListView activity_otheruser_lv;

    private View headView;
    private ImageView head_user_imgHead;// 用户头像
    private TextView head_user_tvName;
    private ImageView head_user_imgSex;
    private TextView head_user_tvLv;// 用户等级
    private ProgressBar head_user_pbExp;// 经验条
    private TextView head_user_tvFocus;// 关注
    private TextView head_user_tvSign;// 签名

    private TopicAdapter topicAdapter;
    private ServiceImpl serviceImpl;
    private ImageLoader imageLoader;
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otheruser);
        initToolBar();
        user_id = getIntent().getIntExtra("user_id", -1);
        serviceImpl = FactoryService.getIService(this);
        imageLoader = new ImageLoader(this);
        initView();
        getUserInfo();
    }

    private void getUserTopic() {
        serviceImpl.getOtherUserTpoic(UrlStaticUtil.getOtherUserTpoic(user_id),
                Constants.OTHER_USER_TOPIC);
    }

    private void getUserInfo() {
        serviceImpl.getOtherUserInfo(UrlStaticUtil.getUserInfo(user_id),
                Constants.USER_INFO);
    }

    /**
     * 添加关注
     */
    private void getAddUserAttention() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", user_id);
        serviceImpl.getAddUserAttention(UrlStaticUtil.getAddUserAttention(),
                params, new HashMap<String, File>(),
                Constants.ADD_USER_ATTENTION);
    }

    /**
     * 移除关注
     */
    private void getRemoveUserAttention() {
        serviceImpl.getRemoveUserAttention(
                UrlStaticUtil.getRemoveUserAttention(user_id),
                Constants.REMOVE_USER_ATTENTION);
    }

    private void setHeaderUserInfo(String jsonString) {
        UserInfo userInfo = JsonTools.getUserInfo(jsonString);
        if (userInfo == null || userInfo.user_info == null) {
            return;
        }
        imageLoader.DisplayImage(UrlStaticUtil.root_photo
                + userInfo.user_info.avatar, head_user_imgHead, true, ImageLoader.L_PICTURE);
        head_user_imgHead.setTag(UrlStaticUtil.root_photo
                + userInfo.user_info.max_avatar);
        head_user_tvName.setText(userInfo.user_info.nickname);
        if (userInfo.user_info.sex.equals("male")) {
            head_user_imgSex.setImageResource(R.drawable.img_sex_man);
        } else {
            head_user_imgSex.setImageResource(R.drawable.img_sex_femal);
        }
        head_user_tvLv.setText("Lv" + userInfo.user_info.rank);
        head_user_pbExp.setMax(userInfo.max_progress);
        head_user_pbExp.setProgress(userInfo.user_info.progress);
        head_user_tvSign.setText(userInfo.user_info.signature);
        if (userInfo.is_attention == 0) {
            head_user_tvFocus.setText("关注");
        } else if (userInfo.is_attention == 1) {
            head_user_tvFocus.setText("取消关注");
        }
        getUserTopic();
    }

    private void initView() {
        activity_otheruser_lv = (ListView) this
                .findViewById(R.id.activity_otheruser_lv);
        headView = LayoutInflater.from(this).inflate(R.layout.head_user, null);
        head_user_imgHead = (ImageView) headView
                .findViewById(R.id.head_user_imgHead);
        head_user_tvName = (TextView) headView
                .findViewById(R.id.head_user_tvName);
        head_user_imgSex = (ImageView) headView
                .findViewById(R.id.head_user_imgSex);
        head_user_tvLv = (TextView) headView.findViewById(R.id.head_user_tvLv);
        head_user_pbExp = (ProgressBar) headView
                .findViewById(R.id.head_user_pbExp);
        head_user_tvFocus = (TextView) headView
                .findViewById(R.id.head_user_tvFocus);
        head_user_tvSign = (TextView) headView
                .findViewById(R.id.head_user_tvSign);
        activity_otheruser_lv.addHeaderView(headView);
        head_user_tvFocus.setOnClickListener(this);
        head_user_imgHead.setOnClickListener(this);
        activity_otheruser_lv.setOnItemClickListener(this);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.activity_otheruser);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.USER_INFO) {
                setHeaderUserInfo(result);
            } else if (tag == Constants.OTHER_USER_TOPIC) {
                topicAdapter = new TopicAdapter(this,
                        JsonTools.getTopicList(result),
                        TopicAdapter.TOPIC_MY_CENTER);
                activity_otheruser_lv.setAdapter(topicAdapter);
            } else if (tag == Constants.ADD_USER_ATTENTION) {
                ToastUtil.show(this, R.string.toast_ok_focus);
                head_user_tvFocus.setText("取消关注");
            } else if (tag == Constants.REMOVE_USER_ATTENTION) {
                ToastUtil.show(this, R.string.toast_cancel_focus);
                head_user_tvFocus.setText("关注");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_user_tvFocus:// 关注
                if (new SelectLoginWay(this).isLogin()) {
                    if (head_user_tvFocus.getText().toString().equals("关注")) {
                        getAddUserAttention();
                    } else if (head_user_tvFocus.getText().toString()
                            .equals("取消关注")) {
                        getRemoveUserAttention();
                    }
                }
                break;
            case R.id.head_user_imgHead:
                Intent intent = new Intent(OthersUserInfoActivity.this,
                        ImageGalleryActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                urls.add((String) head_user_imgHead.getTag() + "");
                intent.putExtra(ImageGalleryActivity.EXTRA_URLS, urls);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TopicAdapter topicAdapter = null;
        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                    .getAdapter();
            topicAdapter = (TopicAdapter) listAdapter.getWrappedAdapter();
        } else {
            topicAdapter = (TopicAdapter) parent.getAdapter();
        }
        Topic topic = (Topic) topicAdapter.getItem(position - 1);
        Intent intent = new Intent(this, ReadTopicActivity.class);
        intent.putExtra("id", topic.id);
        startActivity(intent);
    }

}

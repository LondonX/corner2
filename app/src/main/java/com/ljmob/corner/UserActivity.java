package com.ljmob.corner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lanjing.galleryView.ImageGalleryActivity;
import com.ljmob.corner.adapter.MainPagerAdapter;
import com.ljmob.corner.adapter.MyReplyAdapter;
import com.ljmob.corner.adapter.TopicAdapter;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Reply;
import com.ljmob.corner.entity.Topic;
import com.ljmob.corner.entity.UserInfo;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;

public class UserActivity extends ActionBarActivity implements OnClickListener,
        OnItemClickListener, OnPageChangeListener, OnResponseResult {
    private ImageView activity_user_imgHead;// 头像
    private TextView activity_user_tvName;
    private ImageView activity_user_imgSex;
    private TextView activity_user_tvLv;// 等级
    private ProgressBar activity_user_pbExp;// 经验条
    private TextView activity_user_tvSign;// 签名
    private TextView activity_user_tvAttent;// 关注
    private TextView activity_user_tvFollower;// 粉丝
    private TextView activity_user_tvSignIn;// 签到
    private TextView activity_user_tvTopic;// 见闻
    private TextView activity_user_tvReply;// 回复
    private View listitem_topics_dotTopic;
    private View listitem_topics_dotReply;
    private ViewPager activity_user_pager;
    private List<View> views = new ArrayList<View>();
    private ListView topicListView;// 主题列表
    private ListView replyListView;// 回复列表
    private MainPagerAdapter pagerAdapter;
    private TopicAdapter topicAdapter;
    private MyReplyAdapter replyAdapter;
    private List<Topic> topicList = new ArrayList<Topic>();
    private List<Reply> replyList = new ArrayList<Reply>();
    private ServiceImpl serviceImpl;
    private ImageLoader imageLoader;
    private MyApplication application;
    private SharedPreferences preferences;
    private boolean isUpdataInfo = false;
    private boolean isFirstInto = true;
    private boolean isUpdateDot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initToolBar();
        serviceImpl = FactoryService.getIService(this);
        imageLoader = new ImageLoader(this);
        application = (MyApplication) getApplication();
        preferences = application.preferences;
        initView();
        initPage();
        getUserInfo();
    }

    @Override
    protected void onRestart() {
        if (application.isUpdataUserInfo) {
            application.isUpdataUserInfo = false;
            isUpdataInfo = true;
            getUserInfo();
        }
        if (isUpdateDot) {
            isUpdateDot = false;
            serviceImpl.getQueryMyTopic(UrlStaticUtil.getQueryMyTopic(),
                    Constants.QUERY_MY_TOPIC);
        }
        super.onRestart();
    }

    /**
     * 签到
     */
    private void getSignIn() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("asd", "sdgsdg");
        serviceImpl.getSignIn(UrlStaticUtil.getSignIn(), Constants.SIGN_IN);
    }

    private void getUserInfo() {
        serviceImpl.getUserInfo(
                UrlStaticUtil.getUserInfo(preferences.getInt("id", -1)),
                Constants.USER_INFO);
    }

    private void initView() {
        activity_user_imgHead = (ImageView) this
                .findViewById(R.id.activity_user_imgHead);
        activity_user_tvName = (TextView) this
                .findViewById(R.id.activity_user_tvName);
        activity_user_imgSex = (ImageView) this
                .findViewById(R.id.activity_user_imgSex);
        activity_user_tvLv = (TextView) this
                .findViewById(R.id.activity_user_tvLv);
        activity_user_pbExp = (ProgressBar) this
                .findViewById(R.id.activity_user_pbExp);
        activity_user_tvSign = (TextView) this
                .findViewById(R.id.activity_user_tvSign);
        activity_user_tvAttent = (TextView) this
                .findViewById(R.id.activity_user_tvAttent);
        activity_user_tvFollower = (TextView) this
                .findViewById(R.id.activity_user_tvFollower);
        activity_user_tvSignIn = (TextView) this
                .findViewById(R.id.activity_user_tvSignIn);
        activity_user_tvTopic = (TextView) this
                .findViewById(R.id.activity_user_tvTopic);
        activity_user_tvReply = (TextView) this
                .findViewById(R.id.activity_user_tvReply);
        listitem_topics_dotTopic = this
                .findViewById(R.id.listitem_topics_dotTopic);
        listitem_topics_dotReply = this
                .findViewById(R.id.listitem_topics_dotReply);
        activity_user_pager = (ViewPager) this
                .findViewById(R.id.activity_user_pager);
        listitem_topics_dotTopic.setVisibility(View.GONE);
        listitem_topics_dotReply.setVisibility(View.GONE);
        activity_user_pager.setOnPageChangeListener(this);
        activity_user_tvAttent.setOnClickListener(this);
        activity_user_tvFollower.setOnClickListener(this);
        activity_user_tvTopic.setOnClickListener(this);
        activity_user_tvReply.setOnClickListener(this);
        activity_user_tvSignIn.setOnClickListener(this);
        activity_user_imgHead.setOnClickListener(this);
    }

    private void setHeadUserInfo(String jsonString) {
        UserInfo userInfo = JsonTools.getUserInfo(jsonString);
        activity_user_imgHead.setTag(UrlStaticUtil.root_photo
                + userInfo.user_info.max_avatar);
        imageLoader.DisplayImage(UrlStaticUtil.root_photo
                        + userInfo.user_info.avatar, activity_user_imgHead, true,
                ImageLoader.L_PICTURE);
        activity_user_tvName.setText(userInfo.user_info.nickname);
        if (userInfo.user_info.sex.equals("male")) {
            activity_user_imgSex.setImageResource(R.drawable.img_sex_man);
        } else {
            activity_user_imgSex.setImageResource(R.drawable.img_sex_femal);
        }
        activity_user_tvSign.setText(userInfo.user_info.signature);
        if (isUpdataInfo) {
            isUpdataInfo = false;
            Editor editor = application.preferences.edit();
            editor.putString("nickname", userInfo.user_info.nickname);
            editor.putString("sex", userInfo.user_info.sex);
            editor.putString("signature", userInfo.user_info.signature);
            editor.putString("avatar", userInfo.user_info.avatar);
            editor.apply();
        }
        activity_user_tvLv.setText("Lv" + userInfo.user_info.rank);
        activity_user_pbExp.setMax(userInfo.max_progress);
        activity_user_pbExp.setProgress(userInfo.user_info.progress);
        activity_user_tvAttent.setText(userInfo.user_info.follow_num + "关注");
        // activity_user_tvFollower
        // .setText(userInfo.user_info.fans_num + "粉丝");
        String newFollower = "";
        if (userInfo.user_info.new_fans_num > 0) {
            newFollower = "+" + userInfo.user_info.new_fans_num;
        }
        String text = userInfo.user_info.fans_num + "粉丝 " + newFollower;
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.red)),
                text.indexOf(newFollower), text.indexOf(newFollower)
                        + newFollower.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        activity_user_tvFollower.setText(spannable);
        if (userInfo.user_info.is_report == 1) {
            activity_user_tvSignIn.setText("已签到");
            activity_user_tvSignIn.setClickable(false);
        } else {
            activity_user_tvSignIn.setText("签到");
            activity_user_tvSignIn.setClickable(true);
        }
        if (isFirstInto) {
            isFirstInto = false;
            selectPage(0);
        }
    }

    // 初始化ViewPage
    private void initPage() {
        topicListView = new ListView(this);
        replyListView = new ListView(this);
        Drawable drawable = new ColorDrawable();
        topicListView.setSelector(drawable);
        topicListView.setDivider(drawable);
        replyListView.setSelector(drawable);
        replyListView.setDivider(drawable);
        topicListView.setTag(0);
        replyListView.setTag(1);
        views.add(topicListView);
        views.add(replyListView);
        pagerAdapter = new MainPagerAdapter(views);
        activity_user_pager.setAdapter(pagerAdapter);
        topicListView.setOnItemClickListener(this);
        replyListView.setOnItemClickListener(this);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_user);
    }

    private void selectPage(int index) {
        activity_user_pager.setCurrentItem(index);
        if (index == 0) {
            activity_user_tvTopic.setEnabled(false);
            activity_user_tvReply.setEnabled(true);
        } else {
            activity_user_tvTopic.setEnabled(true);
            activity_user_tvReply.setEnabled(false);
        }
        ListView lv = (ListView) views.get(index);
        if (lv.getAdapter() == null) {
            switch (index) {
                case 0:
                    serviceImpl.getQueryMyTopic(UrlStaticUtil.getQueryMyTopic(),
                            Constants.QUERY_MY_TOPIC);
                    break;
                case 1:
                    // replyAdapter = new ReplyAdapter(this, replyList);
                    // lv.setAdapter(replyAdapter);
                    serviceImpl.getQueryMyReply(UrlStaticUtil.getQueryMyReply(),
                            Constants.QUERY_MY_REPLY);
                    break;

                default:
                    break;
            }
        }
    }

    private void setUpdateDot(List<Topic> topics) {
        if (topics != null) {
            for (Topic topic : topics) {
                if (topic.is_update == 1) {
                    listitem_topics_dotTopic.setVisibility(View.VISIBLE);
                } else {
                    listitem_topics_dotTopic.setVisibility(View.GONE);
                }
            }
        } else {
            listitem_topics_dotTopic.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.menu_edit) {
            Intent intent = new Intent(this, EditInfoActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_user_tvAttent:
                Intent attentionIntent = new Intent(this, RelationActivity.class);
                attentionIntent.putExtra("isAttention", true);
                startActivity(attentionIntent);
                break;
            case R.id.activity_user_tvFollower:
                // application.preferences.edit().putInt("newFansNum", 0).commit();
                Intent fansIntent = new Intent(this, RelationActivity.class);
                fansIntent.putExtra("isAttention", false);
                startActivity(fansIntent);
                break;
            case R.id.activity_user_tvTopic:
                selectPage(0);
                break;
            case R.id.activity_user_tvReply:
                selectPage(1);
                break;
            case R.id.activity_user_tvSignIn:// 签到
                getSignIn();
                break;
            case R.id.activity_user_imgHead:
                Intent intent = new Intent(UserActivity.this,
                        ImageGalleryActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                urls.add((String) activity_user_imgHead.getTag() + "");
                intent.putExtra(ImageGalleryActivity.EXTRA_URLS, urls);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        selectPage(arg0);
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.QUERY_MY_TOPIC) {
                List<Topic> topics = JsonTools.getTopicList(result);
                if (topicAdapter == null) {
                    topicAdapter = new TopicAdapter(this, topics,
                            TopicAdapter.TOPIC_MY_CENTER);
                    topicListView.setAdapter(topicAdapter);
                    if (application.preferences.getBoolean("isNewsDot", true)) {
                        setUpdateDot(topics);
                    }
                } else {
                    if (application.preferences.getBoolean("isNewsDot", true)) {
                        setUpdateDot(topics);
                    }
                }
            } else if (tag == Constants.QUERY_MY_REPLY) {
                replyAdapter = new MyReplyAdapter(this,
                        JsonTools.getReplyList(result));
                replyListView.setAdapter(replyAdapter);
            } else if (tag == Constants.SIGN_IN) {
                ToastUtil.show(this, R.string.toast_end_sign);
                activity_user_tvSignIn.setText(R.string.str_signed);
                activity_user_tvSignIn.setClickable(false);
                getUserInfo();
            } else if (tag == Constants.USER_INFO) {
                setHeadUserInfo(result);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent(this, ReadTopicActivity.class);
        if ((Integer) parent.getTag() == 0) {
            TopicAdapter topicAdapter = (TopicAdapter) parent.getAdapter();
            Topic topic = (Topic) topicAdapter.getItem(position);
            intent.putExtra("id", topic.id);
            isUpdateDot = true;
        } else if ((Integer) parent.getTag() == 1) {
            MyReplyAdapter replyAdapter = (MyReplyAdapter) parent.getAdapter();
            Reply reply = (Reply) replyAdapter.getItem(position);
            intent.putExtra("id", reply.post_id);
            if (reply.po_type.equals("bulletin")) {
                intent.putExtra("isOfficial", true);
            }
        }
        startActivity(intent);
    }
}

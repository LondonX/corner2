package com.ljmob.corner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lanjing.galleryView.ImageGalleryActivity;
import com.ljmob.corner.adapter.ReadTopicAdapter;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Floor;
import com.ljmob.corner.entity.Photo;
import com.ljmob.corner.entity.Topic;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.InputWindowTool;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.AdjustImageView;
import com.ljmob.corner.view.CustomListView;
import com.ljmob.corner.view.CustomListView.OnLoadMoreListener;

public class ReadTopicActivity extends ActionBarActivity implements OnClickListener,
        OnItemClickListener, OnResponseResult, OnTouchListener {
    private CustomListView activity_topic_lv;
    private LinearLayout activity_topic_linearPraiseAndComm;// 赞和评论linea
    private LinearLayout activity_topic_linearPraise;// 赞
    private ImageView activity_topic_imgPraiseLinear;// 赞
    private TextView activity_topic_tvPraiseLinear;// 赞数
    private LinearLayout activity_topic_linearComm;// 评论
    private ImageView activity_topic_imgCommLinear;// 评论
    private TextView activity_topic_tvCommLinear;// 评论数
    private LinearLayout activity_topic_linearOptions;// 评论linea底部框
    private ImageView activity_topic_imgHead;// 头像
    private EditText activity_topic_etComment;// 输入框
    private ImageView activity_topic_imgEmoji;// Emoji
    private TextView activity_topic_btnSend;// 发送

    private View headerView;
    private RelativeLayout listhead_topic_rlTopic;
    private View listhead_topic_div0;
    private ImageView listhead_topic_imgHeadPoster;
    private TextView listhead_topic_tvNamePoster;
    private ImageView listhead_topic_imgSex;
    private TextView listhead_topic_tvLv;// 等级
    private TextView listhead_topic_tvTime;// 时间
    private TextView listhead_topic_tvReadTitle;// 标题
    private TextView listhead_topic_tvReadTime;
    private TextView listhead_topic_tvContent;// 内容
    private LinearLayout listhead_topic_linearPics;// add图片linear
    private TextView listhead_topic_textOC;// 赞过的人名
    private TextView listhead_topic_tvCount;// 等多少人赞过
    private LinearLayout listhead_topic_linearHeadOC;// 等多少人赞linear
    private View listhead_topic_gap0;

    private ReadTopicAdapter adapter;
    private List<Floor> floorList = new ArrayList<Floor>();
    private MenuItem focusItem;
    private MenuItem focusCancelItem;

    private boolean isOfficial = false;// 是否为公告贴
    private ServiceImpl serviceImpl;
    private int id;
    private int page = 1;
    private int pre_page = 20;

    private int newFloorsSize = 0;
    private int current_page_size = 0;// 当前页码长度

    private ImageLoader imageLoader;
    private boolean isReplyFloor = false;// false回复楼层,true为回复帖子
    private Floor floor;
    private MyApplication application;
    private MyBroadcastReceiver receiver;
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> desc;
    private boolean isExternalComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        application = (MyApplication) getApplication();
        imageLoader = new ImageLoader(this);
        serviceImpl = new ServiceImpl(this);
        id = getIntent().getIntExtra("id", id);
        isOfficial = getIntent().getBooleanExtra("isOfficial", false);
        isExternalComment = getIntent().getBooleanExtra("isExternalComment",
                false);
        Log.i("id", id + "");
        initView();
        initToolBar();
        registerBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        if (application.isLoginSucceed) {
            application.isLoginSucceed = false;
            imageLoader.DisplayImage(UrlStaticUtil.root_photo
                            + application.preferences.getString("avatar", ""),
                    activity_topic_imgHead, true, ImageLoader.L_PICTURE);
        }
        super.onRestart();
    }

    private void registerBroadcastReceiver() {
        receiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("ReplyFloor");
        registerReceiver(receiver, intentFilter);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_topic);
    }

    private void getTopicDetail(int page, int pre_page) {
        // current_page = page;
        serviceImpl.getTopicDetail(
                UrlStaticUtil.getTopicDetail(id, page, pre_page),
                Constants.TOPIC_DETAIL);
    }

    /**
     * 回复帖子
     */
    private void replyTopicById() {
        String content = activity_topic_etComment.getText().toString();
        if (!content.equals("")) {
            serviceImpl.getDialog().setCanShowDialog(true);
            serviceImpl.getDialog().setMsg(R.string.dialog_send_comm);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("post_id", id);
            params.put("content", content);
            params.put("photos", "");
            Map<String, File> files = new HashMap<String, File>();
            serviceImpl.getReplyTopic(UrlStaticUtil.getReplyTopic(id), params,
                    files, Constants.REPLY_TOPIC);
        } else {
            ToastUtil.show(this, R.string.toast_error_content);
        }
    }

    /**
     * 回复楼层
     */
    private void replyFloor() {
        String content = activity_topic_etComment.getText().toString();
        if (!content.equals("")) {
            serviceImpl.getDialog().setCanShowDialog(true);
            serviceImpl.getDialog().setMsg(R.string.dialog_reply_comm);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("post_id", id);
            params.put("row", floor.row);
            params.put("content", content);
            params.put("user_nickname", floor.user_nickname);
            Map<String, File> files = new HashMap<String, File>();
            serviceImpl.getReplyFloor(UrlStaticUtil.getReplyFloor(id), params,
                    files, Constants.REPLY_FLOOR);
        } else {
            ToastUtil.show(this, R.string.toast_error_reply);
        }
    }

    public void clickPraise() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        Map<String, File> files = new HashMap<String, File>();
        serviceImpl.getClickPraise(UrlStaticUtil.getClickPraise(), params,
                files, Constants.CLICK_PRAISE);
    }

    private void initView() {
        activity_topic_lv = (CustomListView) this
                .findViewById(R.id.activity_topic_lv);
        activity_topic_lv.setCanRefresh(false);// 不允许下拉刷新
        // activity_topic_lv.setAutoLoadMore(false);// 设置为手动加载更多
        activity_topic_linearPraiseAndComm = (LinearLayout) this
                .findViewById(R.id.activity_topic_linearPraiseAndComm);
        activity_topic_linearPraise = (LinearLayout) this
                .findViewById(R.id.activity_topic_linearPraise);
        activity_topic_imgPraiseLinear = (ImageView) this
                .findViewById(R.id.activity_topic_imgPraiseLinear);
        activity_topic_tvPraiseLinear = (TextView) this
                .findViewById(R.id.activity_topic_tvPraiseLinear);
        activity_topic_linearComm = (LinearLayout) this
                .findViewById(R.id.activity_topic_linearComm);
        activity_topic_imgCommLinear = (ImageView) this
                .findViewById(R.id.activity_topic_imgCommLinear);
        activity_topic_tvCommLinear = (TextView) this
                .findViewById(R.id.activity_topic_tvCommLinear);
        activity_topic_linearOptions = (LinearLayout) this
                .findViewById(R.id.activity_topic_linearOptions);
        activity_topic_imgHead = (ImageView) this
                .findViewById(R.id.activity_topic_imgHead);
        activity_topic_etComment = (EditText) this
                .findViewById(R.id.activity_topic_etComment);
        activity_topic_imgEmoji = (ImageView) this
                .findViewById(R.id.activity_topic_imgEmoji);
        activity_topic_btnSend = (TextView) this
                .findViewById(R.id.activity_topic_btnSend);
        activity_topic_linearOptions.setVisibility(View.GONE);
        // listHead
        headerView = LayoutInflater.from(this).inflate(R.layout.head_topic,
                null);
        listhead_topic_rlTopic = (RelativeLayout) headerView
                .findViewById(R.id.listhead_topic_rlTopic);
        listhead_topic_div0 = headerView.findViewById(R.id.listhead_topic_div0);
        listhead_topic_imgHeadPoster = (ImageView) headerView
                .findViewById(R.id.listhead_topic_imgHeadPoster);
        listhead_topic_tvNamePoster = (TextView) headerView
                .findViewById(R.id.listhead_topic_tvNamePoster);
        listhead_topic_imgSex = (ImageView) headerView
                .findViewById(R.id.listhead_topic_imgSex);
        listhead_topic_tvLv = (TextView) headerView
                .findViewById(R.id.listhead_topic_tvLv);
        listhead_topic_tvTime = (TextView) headerView
                .findViewById(R.id.listhead_topic_tvTime);
        listhead_topic_tvReadTitle = (TextView) headerView
                .findViewById(R.id.listhead_topic_tvReadTitle);
        listhead_topic_tvReadTime = (TextView) headerView
                .findViewById(R.id.listhead_topic_tvReadTime);
        listhead_topic_tvContent = (TextView) headerView
                .findViewById(R.id.listhead_topic_tvContent);
        listhead_topic_linearPics = (LinearLayout) headerView
                .findViewById(R.id.listhead_topic_linearPics);
        listhead_topic_textOC = (TextView) headerView
                .findViewById(R.id.listhead_topic_textOC);
        listhead_topic_tvCount = (TextView) headerView
                .findViewById(R.id.listhead_topic_tvCount);
        listhead_topic_linearHeadOC = (LinearLayout) headerView
                .findViewById(R.id.listhead_topic_linearHeadOC);
        listhead_topic_gap0 = headerView.findViewById(R.id.listhead_topic_gap0);
        listhead_topic_tvCount.setVisibility(View.GONE);
        // listhead_topic_linearHeadOC.setVisibility(View.GONE);
        listhead_topic_gap0.setVisibility(View.GONE);
        if (isOfficial) {
            listhead_topic_rlTopic.setVisibility(View.GONE);
            listhead_topic_div0.setVisibility(View.GONE);
        }
        activity_topic_lv.addHeaderView(headerView, null, false);
        activity_topic_linearPraise.setOnClickListener(this);
        activity_topic_linearComm.setOnClickListener(this);
        activity_topic_btnSend.setOnClickListener(this);
        // activity_topic_lv.setOnItemClickListener(this);
        activity_topic_lv.setOnLoadListener(new MyOnLoadMoreListener());
        activity_topic_lv.setOnTouchListener(this);

        listhead_topic_imgHeadPoster.setOnClickListener(this);
        activity_topic_imgHead.setOnClickListener(this);
        imageLoader.DisplayImage(UrlStaticUtil.root_photo
                        + application.preferences.getString("avatar", ""),
                activity_topic_imgHead, true, ImageLoader.L_PICTURE);
    }

    private void setHeaderData(Topic topic) {
        if (!isOfficial) {
            imageLoader.DisplayImage(UrlStaticUtil.root_photo
                            + topic.user_avatar, listhead_topic_imgHeadPoster, true,
                    ImageLoader.L_PICTURE);
            listhead_topic_tvNamePoster.setText(topic.user_nickname);
            if (topic.user_sex.equals("male")) {
                listhead_topic_imgSex.setImageResource(R.drawable.img_sex_man);
            } else {
                listhead_topic_imgSex
                        .setImageResource(R.drawable.img_sex_femal);
            }
            listhead_topic_tvLv.setText("Lv" + topic.user_rank);
            listhead_topic_tvTime.setText(topic.create_time);
            listhead_topic_imgHeadPoster.setTag(topic.user_id);
        }
        listhead_topic_tvReadTitle.setText(topic.subject);
        // listhead_topic_tvReadTime.setText(topic.create_time);
        if (topic.content != null && !topic.equals("")) {
            listhead_topic_tvContent.setVisibility(View.VISIBLE);
            listhead_topic_tvContent.setText(Html.fromHtml(topic.content));
        } else {
            listhead_topic_tvContent.setVisibility(View.GONE);
        }
        listhead_topic_textOC.setText(Html.fromHtml(topic.praise_user));
        List<Photo> photoList = topic.photos;
        if (listhead_topic_linearPics.getChildCount() > 0) {
            listhead_topic_linearPics.removeAllViews();
        }
        for (int i = 0; i < photoList.size(); i++) {
            View item_topic_content = LayoutInflater.from(this).inflate(
                    R.layout.item_topic_content, null);
            // ImageView imageView = new ImageView(this);
            AdjustImageView imageView = (AdjustImageView) item_topic_content
                    .findViewById(R.id.item_tc_img);
            urls.add(UrlStaticUtil.root_photo + photoList.get(i).img_url);
            imageView.setTag(i);
            imageView.setOnClickListener(new MyOnPicClickListener());
            imageLoader.DisplayImage(
                    UrlStaticUtil.root_photo + photoList.get(i).img_url,
                    imageView, false, ImageLoader.L_PICTURE);
            listhead_topic_linearPics.addView(item_topic_content);
        }
        activity_topic_tvPraiseLinear.setText(topic.praise_num + "");
        activity_topic_tvCommLinear.setText(topic.comment_num + "");
        // listhead_topic_textOC.setText("");//某某人赞过
        listhead_topic_tvCount.setText("等" + topic.praise_num + "人赞过");// 等多少人赞过
    }

    private class MyOnLoadMoreListener implements OnLoadMoreListener {

        @Override
        public void onLoadMore() {
            loadMore();
        }
    }

    private void loadMore() {
        serviceImpl.getDialog().setCanShowDialog(false);
        page++;
        // if (current_page_size < pre_page) {
        // page--;
        // }
        Log.i("page", page + "");
        getTopicDetail(page, pre_page);
    }

    /**
     * 加载数据
     *
     * @param tempList
     */
    private void loadData(final List<Floor> tempList) {
        if (adapter != null) {
            if (tempList.size() == 0) {
                ToastUtil
                        .show(ReadTopicActivity.this, R.string.toast_end_topic);
                page--;
                activity_topic_lv.onLoadMoreComplete();
                activity_topic_lv.setAutoLoadMore(false);
            } else {
                adapter.loadData(tempList);
                activity_topic_lv.onLoadMoreComplete();
                if (newFloorsSize < pre_page) {
                    page--;
                }
            }
            // if (isCurrentPageNotComplete) {
            // isCurrentPageNotComplete = false;
            // page--;
            // }
        }
    }

    private class MyOnPicClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ReadTopicActivity.this,
                    ImageGalleryActivity.class);
            intent.putExtra(ImageGalleryActivity.EXTRA_URLS, urls);
            intent.putExtra(ImageGalleryActivity.EXTRA_INDEX,
                    (Integer) v.getTag());
            startActivity(intent);
        }

    }

    /**
     * 显示回复帖子输入窗口
     */
    private void showReplyTopicWindow() {
        activity_topic_etComment.setHint("来说两句吧");
        activity_topic_linearPraiseAndComm.setVisibility(View.GONE);
        activity_topic_linearOptions.setVisibility(View.VISIBLE);
        InputWindowTool.OpenInputWindow(activity_topic_etComment,
                ReadTopicActivity.this);
        isReplyFloor = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_focus, menu);
        focusItem = menu.findItem(R.id.menu_focus);
        focusCancelItem = menu.findItem(R.id.menu_focus_cancel);
        getTopicDetail(page, pre_page);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.menu_focus) {
            if (new SelectLoginWay(this).isLogin()) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("post_id", id);
                serviceImpl.getAddAttentionTopic(
                        UrlStaticUtil.getAddAttentionTopic(), params,
                        new HashMap<String, File>(),
                        Constants.ADD_ATTENTION_TOPIC);
            }

        } else if (item.getItemId() == R.id.menu_focus_cancel) {
            if (new SelectLoginWay(this).isLogin()) {
                serviceImpl.getRemoveAttentionTopic(
                        UrlStaticUtil.getRemoveAttentionTopic(id),
                        Constants.REMOVE_ATTENTION_TOPIC);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_topic_linearPraise:// 赞linear
                if (new SelectLoginWay(this).isLogin()) {
                    clickPraise();
                }
                break;
            case R.id.activity_topic_linearComm:// 评论linear
                if (new SelectLoginWay(this).isLogin()) {
                    showReplyTopicWindow();
                }
                break;
            case R.id.activity_topic_btnSend:// 发送
                if (new SelectLoginWay(this).isLogin()) {
                    if (isReplyFloor) {
                        replyFloor();
                    } else {
                        replyTopicById();
                    }
                }

                break;
            case R.id.listhead_topic_imgHeadPoster:
                Intent intent = new Intent();
                int id = (Integer) listhead_topic_imgHeadPoster.getTag();
                if (id == application.preferences.getInt("id", -1)) {
                    intent.setClass(this, UserActivity.class);
                } else {
                    intent.setClass(this, OthersUserInfoActivity.class);
                    intent.putExtra("user_id", id);
                }
                startActivity(intent);
                break;

            case R.id.activity_topic_imgHead:
                Intent intent2 = new Intent(this, UserActivity.class);
                startActivity(intent2);
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        isReplyFloor = true;
        ReadTopicAdapter readTopicAdapter = null;
        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                    .getAdapter();
            readTopicAdapter = (ReadTopicAdapter) listAdapter
                    .getWrappedAdapter();
        } else {
            readTopicAdapter = (ReadTopicAdapter) parent.getAdapter();
        }
        floor = (Floor) readTopicAdapter.getItem(position - 2);
        activity_topic_linearPraiseAndComm.setVisibility(View.GONE);
        activity_topic_linearOptions.setVisibility(View.VISIBLE);
        activity_topic_etComment.setFocusable(true);
        activity_topic_etComment.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getApplication()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        activity_topic_etComment.setHint("回复 " + floor.user_nickname);

    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            switch (tag) {
                case Constants.TOPIC_DETAIL:
                    JSONObject jsonObjectTopic = null;
                    JSONArray jsonArrayTopic = null;
                    try {
                        jsonObjectTopic = new JSONObject(result);
                        jsonArrayTopic = jsonObjectTopic
                                .getJSONArray("comment_arr");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        return;
                    }
                    if (adapter == null) {
                        try {
                            setHeaderData(JsonTools.getTopic(jsonObjectTopic
                                    .getString("post_info").toString()));
                            if (jsonObjectTopic.getInt("is_attention") == 1) {
                                focusItem.setVisible(false);
                                focusCancelItem.setVisible(true);
                            }
                            if (jsonObjectTopic.getInt("is_praise") == 1) {
                                activity_topic_imgPraiseLinear
                                        .setColorFilter(getResources().getColor(
                                                R.color.red));
                                activity_topic_tvPraiseLinear
                                        .setTextColor(getResources().getColor(
                                                R.color.red));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<Floor> floors = new ArrayList<Floor>();
                        floors = JsonTools.getFloorList(jsonArrayTopic.toString());
                        if (isOfficial) {
                            adapter = new ReadTopicAdapter(ReadTopicActivity.this,
                                    floors, 0);
                        } else {
                            adapter = new ReadTopicAdapter(ReadTopicActivity.this,
                                    floors, 1);
                        }
                        activity_topic_lv.setAdapter(adapter);
                        current_page_size = floors.size();
                        newFloorsSize = floors.size();
                        // 第一次加载不满指定条数，代表第一页没加载完，page需要减1
                        if (floors.size() < pre_page) {
                            page--;
                        }
                        if (isExternalComment) {// 如果从外部评论按钮点击进来的,这直接显示输入窗口
                            showReplyTopicWindow();
                        }
                    } else {
                        List<Floor> tempList;
                        List<Floor> floors = JsonTools.getFloorList(jsonArrayTopic
                                .toString());
                        newFloorsSize = floors.size();
                        if (floors != null) {
                            if (current_page_size == pre_page) {
                                loadData(floors);
                            } else {
                                if (current_page_size == floors.size()) {
                                    loadData(new ArrayList<Floor>());
                                } else {
                                    tempList = floors.subList(current_page_size,
                                            floors.size());
                                    loadData(tempList);
                                }
                            }
                        } else {
                            loadData(new ArrayList<Floor>());
                        }
                        if (floors.size() > 0) {
                            current_page_size = floors.size();
                            // current_row = floors.get(floors.size() - 1).row;
                        }
                    }
                    break;

                case Constants.REPLY_TOPIC:
                    ToastUtil.show(this, R.string.toast_ok_comm);
                    // 关闭输入法
                    InputWindowTool.hideInputWindow(ReadTopicActivity.this);
                    activity_topic_lv.setLoadTextViewContent("数据有更新，请加载");
                    // activity_topic_lv.setCanLoadMore(true);
                    try {

                        JSONObject jsonObject = new JSONObject(result);
                        activity_topic_tvCommLinear.setText(jsonObject
                                .getInt("comment_num") + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    activity_topic_etComment.setText("");
                    // 如果当前页码长度不等于指定页码长度，说明之前页码没有加载完，page需要减1
                    // if (current_page_size != pre_page) {
                    // page--;
                    // }
                    loadMore();
                    break;

                case Constants.REPLY_FLOOR:
                    ToastUtil.show(this, "评论成功");
                    activity_topic_etComment.setText("");
                    // 关闭输入法
                    InputWindowTool.hideInputWindow(ReadTopicActivity.this);
                    serviceImpl.getTopicFloor(
                            UrlStaticUtil.getTopicFloor(id, floor.row),
                            Constants.TOPIC_FLOOR);
                    try {

                        JSONObject jsonObject = new JSONObject(result);
                        activity_topic_tvCommLinear.setText(jsonObject
                                .getInt("comment_num") + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TOPIC_FLOOR:
                    Floor floor = new Floor();
                    floor = JsonTools.getFloor(result);
                    adapter.refreshFloor(floor, floor.row);
                    break;

                case Constants.CLICK_PRAISE:
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("is_praise") == 0) {
                            ToastUtil.show(this, "取消赞");
                            activity_topic_imgPraiseLinear
                                    .setColorFilter(getResources().getColor(
                                            R.color.black));
                            activity_topic_tvPraiseLinear
                                    .setTextColor(getResources().getColor(
                                            R.color.black));
                            listhead_topic_textOC.setText(Html.fromHtml(jsonObject
                                    .getString("praise_user")));
                        } else {
                            ToastUtil.show(this, "赞");
                            activity_topic_imgPraiseLinear
                                    .setColorFilter(getResources().getColor(
                                            R.color.red));
                            activity_topic_tvPraiseLinear
                                    .setTextColor(getResources().getColor(
                                            R.color.red));
                            listhead_topic_textOC.setText(Html.fromHtml(jsonObject
                                    .getString("praise_user")));
                        }
                        activity_topic_tvPraiseLinear.setText(jsonObject
                                .getString("praise_num") + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.ADD_ATTENTION_TOPIC:
                    ToastUtil.show(this, "已关注");
                    focusItem.setVisible(false);
                    focusCancelItem.setVisible(true);
                    break;
                case Constants.REMOVE_ATTENTION_TOPIC:
                    ToastUtil.show(this, "取消关注");
                    focusItem.setVisible(true);
                    focusCancelItem.setVisible(false);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (activity_topic_linearPraiseAndComm.getVisibility() == View.GONE) {
                activity_topic_linearPraiseAndComm.setVisibility(View.VISIBLE);
                activity_topic_linearOptions.setVisibility(View.GONE);
                // 关闭输入法
                InputWindowTool.hideInputWindow(ReadTopicActivity.this);
            }
        }
        return false;
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("ReplyFloor")) {
                isReplyFloor = true;
                floor = (Floor) intent.getSerializableExtra("floor");
                activity_topic_linearPraiseAndComm.setVisibility(View.GONE);
                activity_topic_linearOptions.setVisibility(View.VISIBLE);
                InputWindowTool.OpenInputWindow(activity_topic_etComment,
                        ReadTopicActivity.this);
                activity_topic_etComment.setHint("回复 " + floor.user_nickname);
            }
        }

    }

}

package com.ljmob.corner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.corner.actionprovider.MainActionProvider;
import com.ljmob.corner.adapter.CatoGridAdapter;
import com.ljmob.corner.adapter.FavorableShopAdapter;
import com.ljmob.corner.adapter.TopicAdapter;
import com.ljmob.corner.adapter.ViewGroupPagerAdapter;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Cato;
import com.ljmob.corner.entity.FavorableShop;
import com.ljmob.corner.entity.Photo;
import com.ljmob.corner.entity.Topic;
import com.ljmob.corner.map.util.MixtureLocatioin;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.AuthTool;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MacUtil;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.UpdateUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.SwipeRefreshLayoutOverlay;
import com.ljmob.corner.view.SwipeRefreshLayoutOverlay.OnRefreshListener;
import com.ljmob.corner.view.UnscrollableGridView;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener,
        OnItemClickListener, OnClickListener, AnimationListener,
        OnResponseResult {
    private LinearLayout listhead_topics_root0;// topicHeadRoot
    private LinearLayout activity_topics_rlFitShop;// 查看更多linear
    private LinearLayout activity_topics_linearNewTopic0;// recomment
    private LinearLayout activity_topics_linearNewTopic1;// recomment
    private TextView activity_topics_tvTitle0;
    private ImageView activity_topics_imgPic0;
    private TextView activity_topics_tvTitle1;
    private ImageView activity_topics_imgPic1;
    private DrawerLayout activity_main_drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView hotDealsFooterTextView;
    private TextView topicsFooterTextView;
    private View topicsHeadView;
    private View activity_main_flAddTopic;
    private View activity_main_tvFavor;
    private View activity_main_tvTopic;
    private View activity_main_rlFavor;
    private View activity_main_rlTopic;
    private View activity_main_viewFavor;
    private View activity_main_viewTopic;
    private View activity_main_linearTab;
    private View hotDealsFooterView;
    private View topicsFooterView;

    private Animation animEditIn;
    private Animation animEditOut;
    private Animation animTabIn;
    private Animation animTabOut;
    private Animation animToolbarIn;
    private Animation animToolbarOut;

    private Toolbar tb;
    private LinearLayout activity_main_linearDrawer;// 添加分类item linear
    private ViewPager activity_main_pager;
    private List<View> listViews;
    private List<SwipeRefreshLayoutOverlay> swipes;

    private List<FavorableShop> shops = new ArrayList<FavorableShop>();// 热门优惠
    private List<Topic> topics = new ArrayList<Topic>();// 普通帖子
    private List<Topic> bulletins = new ArrayList<Topic>();// 公告贴

    private View emptyHeadView;
    private View listitem_cato;
    private TextView listitem_cato_tvCato;
    private UnscrollableGridView listitem_cato_gvCato;

    private ListView hotDealsListView;
    private SwipeRefreshLayoutOverlay hotDealsSwipe;
    private ListView topicListView;
    private SwipeRefreshLayoutOverlay topicSwipe;
    private FavorableShopAdapter favorableShopAdapter;
    private TopicAdapter topicAdapter;

    private MainActionProvider actionProvider;
    private MainActivityReceiver receiver;

    private CatoGridAdapter gridAdapter;
    private List<Cato> catos = new ArrayList<Cato>();
    private ServiceImpl serviceImpl;
    private ImageLoader imageLoader;
    private int topic_page = 1;
    private int topic_pre_page = 10;
    private int topic_refreshType = 0;
    private int hot_page = 1;
    private int hot_pre_page = 10;
    private int hot_refreshType = 0;
    private Topic topic[] = new Topic[2];
    private MyApplication application;
    private int toolBarHeight;
    private boolean isUiShowing;
    private MacUtil macUtil;
    private PushAgent mPushAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = (MyApplication) getApplication();
        application.mixtureLocatioin = new MixtureLocatioin(this);
        application.mixtureLocatioin.removeListener20Back();
        macUtil = new MacUtil(this);
        new UpdateUtil(this);
        initViews();
        initToolBar();
        imageLoader = new ImageLoader(this);
        receiver = new MainActivityReceiver();
        registerReceiver(receiver, new IntentFilter(AuthTool.ACTION_CH_USER));
        serviceImpl = FactoryService.getIService(this);
        getNetHotDeals(hot_page, hot_pre_page);
        if (savedInstanceState == null) {
            selectPage(0);
        }
        initUmengPush();
    }

    @Override
    protected void onResume() {
        if (application.isAddtopicSucceed) {
            application.isAddtopicSucceed = false;
            topic_refreshType = 0;
            topic_page = 1;
            getNetTopic(topic_page, topic_pre_page);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        if (application.mixtureLocatioin != null) {
            application.mixtureLocatioin.stopLocation();
        }
        super.onDestroy();
    }

    private void initUmengPush() {
        if (application.preferences.getBoolean("UmengPushIsEnabled", true)) {
            mPushAgent = PushAgent.getInstance(this);
            if (mPushAgent.isEnabled() == false
                    && UmengRegistrar.isRegistered(this) == false) {
                mPushAgent.enable();
            }
            String info = String.format("enabled:%s  isRegistered:%s",
                    mPushAgent.isEnabled(), mPushAgent.isRegistered());
            Log.i("YANGBANG", "switch Push:" + info);
            Log.i("device_token", UmengRegistrar.getRegistrationId(this) + "");
        }
    }

    private void getNetHotDeals(int page, int pre_page) {
        serviceImpl.getHotDeals(UrlStaticUtil.getHotDealsUrl(page, pre_page),
                Constants.HOT_DEALS);
    }

    private void getNetTopic(int page, int pre_page) {
        serviceImpl.getTopic(UrlStaticUtil.getTopic(page, pre_page),
                Constants.TOPIC);
    }

    private void getNetCato() {
        serviceImpl.getCato(UrlStaticUtil.getCato(), Constants.CATO);
    }

    private void initCatoView(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            listitem_cato = LayoutInflater.from(this).inflate(
                    R.layout.item_cato, null);
            listitem_cato_tvCato = (TextView) listitem_cato
                    .findViewById(R.id.listitem_cato_tvCato);
            listitem_cato_gvCato = (UnscrollableGridView) listitem_cato
                    .findViewById(R.id.listitem_cato_gvCato);
            activity_main_linearDrawer.addView(listitem_cato);
            try {
                JSONObject typeItem = jsonArray.getJSONObject(i);
                listitem_cato_tvCato.setText(typeItem.getString("type"));
                catos = JsonTools.getCatoList(typeItem.getJSONArray("item")
                        .toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            gridAdapter = new CatoGridAdapter(this, catos);
            if (i == 1) {
                listitem_cato_gvCato.setTag(i - 1);
            } else if (i == 2) {
                listitem_cato_gvCato.setTag(i - 1);
            } else {
                listitem_cato_gvCato.setTag(i);
            }
            listitem_cato_gvCato.setAdapter(gridAdapter);
            listitem_cato_gvCato.setOnItemClickListener(this);
        }
        getStatisticses();
    }

    /**
     * 统计用户
     */
    private void getStatisticses() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ip", macUtil.getIp());
        params.put("mac_addr", macUtil.getMac());
        params.put("app_version", UpdateUtil.getCurrentVersion(this));
        params.put("os_version", Build.VERSION.SDK_INT);
        params.put("platform", "android");
        params.put("device_token", UmengRegistrar.getRegistrationId(this) + "");
        serviceImpl.getStatisticses(UrlStaticUtil.getStatisticses(), params,
                new HashMap<String, File>(), Constants.STATISTICSES);
    }

    private void initViews() {
        activity_main_linearDrawer = (LinearLayout) this
                .findViewById(R.id.activity_main_linearDrawer);
        tb = (Toolbar) this
                .findViewById(R.id.toolbar_root);

        activity_main_drawer = (DrawerLayout) findViewById(R.id.activity_main_root);
        activity_main_pager = (ViewPager) findViewById(R.id.activity_main_pager);
        activity_main_flAddTopic = findViewById(R.id.activity_main_flAddTopic);
        activity_main_tvFavor = findViewById(R.id.activity_main_tvFavor);
        activity_main_tvTopic = findViewById(R.id.activity_main_tvTopic);
        activity_main_rlFavor = findViewById(R.id.activity_main_rlFavor);
        activity_main_rlTopic = findViewById(R.id.activity_main_rlTopic);
        activity_main_viewFavor = findViewById(R.id.activity_main_viewFavor);
        activity_main_viewTopic = findViewById(R.id.activity_main_viewTopic);
        activity_main_linearTab = findViewById(R.id.activity_main_linearTab);

        activity_main_flAddTopic.setOnClickListener(this);
        activity_main_rlFavor.setOnClickListener(this);
        activity_main_rlTopic.setOnClickListener(this);

        animEditIn = AnimationUtils.loadAnimation(this, R.anim.btn_in);
        animEditOut = AnimationUtils.loadAnimation(this, R.anim.btn_out);
        animEditIn.setAnimationListener(this);
        animEditOut.setAnimationListener(this);

        topicsHeadView = getLayoutInflater()
                .inflate(R.layout.head_topics, null);
        hotDealsFooterView = getLayoutInflater().inflate(R.layout.footer_more,
                null);
        topicsFooterView = getLayoutInflater().inflate(R.layout.footer_more,
                null);
        hotDealsFooterTextView = (TextView) hotDealsFooterView
                .findViewById(R.id.footer_more_tvStatus);
        topicsFooterTextView = (TextView) topicsFooterView
                .findViewById(R.id.footer_more_tvStatus);

        listhead_topics_root0 = (LinearLayout) topicsHeadView
                .findViewById(R.id.listhead_topics_root0);
        activity_topics_rlFitShop = (LinearLayout) topicsHeadView
                .findViewById(R.id.activity_topics_rlFitShop);
        activity_topics_linearNewTopic0 = (LinearLayout) topicsHeadView
                .findViewById(R.id.activity_topics_linearNewTopic0);
        activity_topics_linearNewTopic1 = (LinearLayout) topicsHeadView
                .findViewById(R.id.activity_topics_linearNewTopic1);
        activity_topics_tvTitle0 = (TextView) topicsHeadView
                .findViewById(R.id.activity_topics_tvTitle0);
        activity_topics_imgPic0 = (ImageView) topicsHeadView
                .findViewById(R.id.activity_topics_imgPic0);
        activity_topics_tvTitle1 = (TextView) topicsHeadView
                .findViewById(R.id.activity_topics_tvTitle1);
        activity_topics_imgPic1 = (ImageView) topicsHeadView
                .findViewById(R.id.activity_topics_imgPic1);
        activity_topics_rlFitShop.setOnClickListener(this);
        activity_topics_linearNewTopic0.setOnClickListener(this);
        activity_topics_linearNewTopic1.setOnClickListener(this);
        listViews = new ArrayList<View>();
        swipes = new ArrayList<SwipeRefreshLayoutOverlay>();
        hotDealsSwipe = (SwipeRefreshLayoutOverlay) getLayoutInflater()
                .inflate(R.layout.item_swipe, null);
        topicSwipe = (SwipeRefreshLayoutOverlay) getLayoutInflater().inflate(
                R.layout.item_swipe, null);
        hotDealsListView = (ListView) hotDealsSwipe
                .findViewById(R.id.item_swipe_lv);
        topicListView = (ListView) topicSwipe.findViewById(R.id.item_swipe_lv);
        ColorDrawable drawable = new ColorDrawable(Color.TRANSPARENT);
        hotDealsListView.setDivider(drawable);
        hotDealsListView.setSelector(drawable);
        topicListView.setDivider(drawable);
        topicListView.setSelector(drawable);

        hotDealsSwipe.setColorSchemeResources(R.color.red, R.color.material_deep_teal_500,
                R.color.material_deep_teal_200, R.color.gray_div);
        topicSwipe.setColorSchemeResources(R.color.material_deep_teal_200, R.color.gray_div,
                R.color.red, R.color.material_deep_teal_500);

        emptyHeadView = getLayoutInflater().inflate(R.layout.head_view, null);
        hotDealsListView.addHeaderView(emptyHeadView);
        topicListView.addHeaderView(emptyHeadView);
        hotDealsListView.addFooterView(hotDealsFooterView);
        topicListView.addFooterView(topicsFooterView);

        hotDealsListView.setOnItemClickListener(this);
        topicListView.setOnItemClickListener(this);
        hotDealsSwipe.setOnRefreshListener(new SwipeRefreshListener(0));
        topicSwipe.setOnRefreshListener(new SwipeRefreshListener(1));
        hotDealsListView.setOnScrollListener(new LondonXScrollListener(0));
        topicListView.setOnScrollListener(new LondonXScrollListener(1));

        swipes.add(hotDealsSwipe);
        swipes.add(topicSwipe);
        listViews.add(hotDealsListView);
        listViews.add(topicListView);

        activity_main_pager.setAdapter(new ViewGroupPagerAdapter(swipes));
        activity_main_pager.setOnPageChangeListener(this);
    }

    private void initToolBar() {
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, activity_main_drawer,
                tb, 0, 0);
        activity_main_drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        toolBarHeight = getResources().getDimensionPixelSize(R.dimen
                .abc_action_bar_default_height_material);
    }

    private void setHeaderInfo(List<Topic> bulletins) {
        for (int i = 0; i < bulletins.size(); i++) {
            if (i == 0) {
                activity_topics_tvTitle0.setText(bulletins.get(i).subject);
                topic[0] = bulletins.get(i);
                List<Photo> photos = bulletins.get(0).photos;
                if (photos != null && photos.size() > 0) {
                    imageLoader.DisplayImage(
                            UrlStaticUtil.root_photo + photos.get(0).img_url,
                            activity_topics_imgPic0, false, ImageLoader.L_PICTURE);
                }
            } else if (i == 1) {
                activity_topics_tvTitle1.setText(bulletins.get(i).subject);
                topic[1] = bulletins.get(i);
                List<Photo> photos = bulletins.get(1).photos;
                if (photos != null && photos.size() > 0) {
                    imageLoader.DisplayImage(
                            UrlStaticUtil.root_photo + photos.get(0).img_url,
                            activity_topics_imgPic1, false, ImageLoader.L_PICTURE);
                }
            }
        }
        if (bulletins.size() == 0) {
            listhead_topics_root0.setVisibility(View.GONE);
        } else if (bulletins.size() == 1) {
            listhead_topics_root0.setVisibility(View.VISIBLE);
            activity_topics_linearNewTopic0.setVisibility(View.VISIBLE);
            activity_topics_linearNewTopic1.setVisibility(View.GONE);

        } else {
            listhead_topics_root0.setVisibility(View.VISIBLE);
            activity_topics_linearNewTopic0.setVisibility(View.VISIBLE);
            activity_topics_linearNewTopic1.setVisibility(View.VISIBLE);

        }
        // activity_topics_imgPic0

    }

    private void selectPage(int index) {
        if (activity_main_pager.getCurrentItem() != index) {
            activity_main_pager.setCurrentItem(index, true);
        }
        switch (index) {
            case 0:
                if (activity_main_flAddTopic.getVisibility() == View.VISIBLE) {
                    activity_main_flAddTopic.startAnimation(animEditOut);
                }
                activity_main_rlFavor.setEnabled(false);
                activity_main_rlTopic.setEnabled(true);
                activity_main_tvFavor.setAlpha(1.0f);
                activity_main_tvTopic.setAlpha(0.8f);
                activity_main_tvTopic.setAlpha(0.8f);
                activity_main_viewFavor.setVisibility(View.VISIBLE);
                activity_main_viewTopic.setVisibility(View.INVISIBLE);
                break;
            case 1:
                activity_main_flAddTopic.startAnimation(animEditIn);
                activity_main_rlFavor.setEnabled(true);
                activity_main_rlTopic.setEnabled(false);
                activity_main_tvFavor.setAlpha(0.8f);
                activity_main_tvTopic.setAlpha(1.0f);
                activity_main_viewFavor.setVisibility(View.INVISIBLE);
                activity_main_viewTopic.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
        showUi();
    }

    /**
     * topic刷新或加载数据
     *
     * @param tempList
     */
    private void loadDataFromTopic(final List<Topic> tempList) {
        if (topic_refreshType == 0) {
            if (topicAdapter != null) {
                topicAdapter.loadData(tempList, 0);
            }
        } else {
            if (topicAdapter != null) {
                if (tempList.size() == 0) {
                    topicsFooterView.setTag(LondonXScrollListener.nextPageEnd);
                    topicsFooterTextView.setText(R.string.str_loading_end);
                } else {
                    topicAdapter.loadData(tempList, 1);
                }
            }
        }

    }

    /**
     * 热门优惠刷新或加载数据
     *
     * @param tempList
     */
    private void loadDataFromHot(final List<FavorableShop> tempList) {
        if (hot_refreshType == 0) {
            if (favorableShopAdapter != null) {
                favorableShopAdapter.loadData(tempList, 0);
            }
        } else {
            if (favorableShopAdapter != null) {
                if (tempList.size() == 0) {
                    hotDealsFooterView
                            .setTag(LondonXScrollListener.nextPageEnd);
                    hotDealsFooterTextView.setText(R.string.str_loading_end);
                } else {
                    favorableShopAdapter.loadData(tempList, 1);
                }
            }
        }

    }

    /**
     * 设置数据
     *
     * @param index page下标
     */
    private void setData(int index) {
        ListView lv = (ListView) listViews.get(index);
        if (lv.getAdapter() == null) {
            serviceImpl.getDialog().setCanShowDialog(true);
            switch (index) {
                case 0:
                    favorableShopAdapter = new FavorableShopAdapter(this, shops);
                    lv.setAdapter(favorableShopAdapter);
                    getNetCato();// 热门优惠加载完成之后再加载侧滑菜单
                    break;

                case 1:
                    lv.addHeaderView(topicsHeadView, null, false);
                    topicAdapter = new TopicAdapter(this, topics,
                            TopicAdapter.TOPIC_ZJ);
                    lv.setAdapter(topicAdapter);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 请求网络数据
     *
     * @param index page下标
     */
    private void requestData(int index) {
        if (index == 0
                && ((ListView) listViews.get(index)).getAdapter() == null) {
            getNetHotDeals(hot_page, hot_pre_page);
        } else if (index == 1
                && ((ListView) listViews.get(index)).getAdapter() == null) {
            getNetTopic(topic_page, topic_pre_page);
        }
        selectPage(index);
    }

    private void refresh(int index) {
        serviceImpl.getDialog().setCanShowDialog(false);
        switch (index) {
            case 0:
                hot_refreshType = 0;
                hot_page = 1;
                getNetHotDeals(hot_page, hot_pre_page);
                hotDealsFooterTextView.setText(R.string.str_loading);
                hotDealsFooterView.setTag(LondonXScrollListener.nextPageAvailable);
                break;
            case 1:
                topic_refreshType = 0;
                topic_page = 1;
                getNetTopic(topic_page, topic_pre_page);
                topicsFooterTextView.setText(R.string.str_loading);
                topicsFooterView.setTag(LondonXScrollListener.nextPageAvailable);
                break;

            default:
                break;
        }
    }

    private void loadMore(int index) {
        serviceImpl.getDialog().setCanShowDialog(false);
        switch (index) {
            case 0:
                hot_refreshType = 1;
                hot_page++;
                getNetHotDeals(hot_page, hot_pre_page);
                break;
            case 1:
                topic_refreshType = 1;
                topic_page++;
                getNetTopic(topic_page, topic_pre_page);
                break;

            default:
                break;
        }
    }

    private void hideUi() {
        SwipeRefreshLayoutOverlay swipe = activity_main_pager.getCurrentItem() == 0 ? hotDealsSwipe
                : topicSwipe;
        if (isUiShowing == false || swipe.isRefreshing()) {
            return;
        }
        isUiShowing = false;
        if (animTabOut == null) {
            animTabOut = new TranslateAnimation(0, 0, 0, -toolBarHeight * 2);
            animTabOut.setAnimationListener(this);
            animTabOut.setDuration(400);
            animToolbarOut = new TranslateAnimation(0, 0, 0, -toolBarHeight);
            animToolbarOut.setAnimationListener(this);
            animToolbarOut.setDuration(400);
        }
        tb.startAnimation(animToolbarOut);
        activity_main_linearTab.startAnimation(animTabOut);
        if (activity_main_pager.getCurrentItem() == 1) {
            activity_main_flAddTopic.startAnimation(animEditOut);
        }
    }

    private void showUi() {
        if (isUiShowing) {
            return;
        }
        isUiShowing = true;
        if (animTabIn == null) {
            animTabIn = new TranslateAnimation(0, 0, -toolBarHeight * 2, 0);
            animTabIn.setAnimationListener(this);
            animTabIn.setDuration(200);
            animToolbarIn = new TranslateAnimation(0, 0, -toolBarHeight, 0);
            animToolbarIn.setAnimationListener(this);
            animToolbarIn.setDuration(200);
        }
        tb.startAnimation(animToolbarIn);
        activity_main_linearTab.startAnimation(animTabIn);
        if (activity_main_pager.getCurrentItem() == 1) {
            activity_main_flAddTopic.startAnimation(animEditIn);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.menu_main_search) {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isUiShowing == false) {
            showUi();
        } else {
            super.onBackPressed();
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
        requestData(arg0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (parent.getId() == R.id.listitem_cato_gvCato) {
            CatoGridAdapter adapter = (CatoGridAdapter) parent.getAdapter();
            Cato cato = (Cato) adapter.getItem(position);
            Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
            mapIntent.putExtra("belong_kinds", cato.keyword);
            mapIntent.putExtra("tag", (Integer) parent.getTag());
            if ((Integer) parent.getTag() == 0) {
                mapIntent.putExtra("type", "belong_kinds");
            } else {
                mapIntent.putExtra("type", "district");
            }
            startActivity(mapIntent);
        } else if (activity_main_pager.getCurrentItem() == 0) {
            FavorableShopAdapter adapter = null;
            if (parent.getAdapter() instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                        .getAdapter();
                adapter = (FavorableShopAdapter) listAdapter
                        .getWrappedAdapter();
                Log.i("YANGBANG", "HeaderViewListAdapter");
            } else {
                adapter = (FavorableShopAdapter) parent.getAdapter();
            }
            FavorableShop shop = null;
            try {
                shop = (FavorableShop) adapter.getItem(position - 1);
            } catch (IndexOutOfBoundsException ex) {
                return;
            }
            Intent intent = new Intent(this, FavorDetailActivity.class);
            intent.putExtra("id", shop.id);
            intent.putExtra("title", shop.subject);
            startActivity(intent);
        } else if (activity_main_pager.getCurrentItem() == 1) {
            TopicAdapter topicAdapter = null;
            if (parent.getAdapter() instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                        .getAdapter();
                topicAdapter = (TopicAdapter) listAdapter.getWrappedAdapter();
                Log.i("YANGBANG", "HeaderViewListAdapter");
            } else {
                topicAdapter = (TopicAdapter) parent.getAdapter();
            }
            Topic topic = null;
            try {
                topic = (Topic) topicAdapter.getItem(position - 2);
            } catch (IndexOutOfBoundsException e) {
                return;
            }
            Intent topicIntent = new Intent(this, ReadTopicActivity.class);
            // topicIntent.putExtra("topic", topic);
            topicIntent.putExtra("id", topic.id);
            topicIntent.putExtra("isOfficial", false);
            startActivity(topicIntent);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_flAddTopic:
                if (new SelectLoginWay(this).isLogin()) {
                    Intent addTopicIntent = new Intent(this, AddTopicActivity.class);
                    startActivity(addTopicIntent);
                }
                break;
            case R.id.activity_main_rlFavor:
                requestData(0);
                break;
            case R.id.activity_main_rlTopic:
                requestData(1);
                break;
            case R.id.activity_topics_rlFitShop:
                Intent readIntent = new Intent(this, ReadActivity.class);
                startActivity(readIntent);
                break;
            case R.id.activity_topics_linearNewTopic0:
                Intent intentBulletin0 = new Intent(MainActivity.this,
                        ReadTopicActivity.class);
                intentBulletin0.putExtra("topic", topic[0]);
                intentBulletin0.putExtra("id", topic[0].id);
                intentBulletin0.putExtra("isOfficial", true);
                startActivity(intentBulletin0);
                break;
            case R.id.activity_topics_linearNewTopic1:
                Intent intentBulletin1 = new Intent(MainActivity.this,
                        ReadTopicActivity.class);
                intentBulletin1.putExtra("topic", topic[1]);
                intentBulletin1.putExtra("id", topic[1].id);
                intentBulletin1.putExtra("isOfficial", true);
                startActivity(intentBulletin1);
                break;

            default:
                break;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (animation.equals(animEditIn)) {
            activity_main_flAddTopic.setVisibility(View.VISIBLE);
        } else if (animation.equals(animToolbarIn)) {
            getSupportActionBar().show();
        } else if (animation.equals(animTabIn)) {
            activity_main_linearTab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation.equals(animEditOut)) {
            activity_main_flAddTopic.setVisibility(View.INVISIBLE);
        } else if (animation.equals(animTabOut)) {
            activity_main_linearTab.setVisibility(View.INVISIBLE);
        } else if (animation.equals(animToolbarOut)) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.HOT_DEALS) {
                if (favorableShopAdapter == null) {
                    shops = JsonTools.getFavorableShopList(result);
                    setData(0);
                } else {
                    loadDataFromHot(JsonTools.getFavorableShopList(result));
                }
                hotDealsSwipe.setRefreshing(false);
            } else if (tag == Constants.TOPIC) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    topics = JsonTools.getTopicList(jsonObject.getJSONArray(
                            "general").toString());
                    if (topicAdapter == null) {
                        bulletins = JsonTools.getTopicList(jsonObject
                                .getJSONArray("bulletin").toString());
                        setHeaderInfo(bulletins);
                        setData(1);
                    } else {
                        if (topic_refreshType == 0) {
                            bulletins = JsonTools.getTopicList(jsonObject
                                    .getJSONArray("bulletin").toString());
                            setHeaderInfo(bulletins);
                        }
                        loadDataFromTopic(topics);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                topicSwipe.setRefreshing(false);
            } else if (tag == Constants.CATO) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    application.sortArray = jsonArray;
                    initCatoView(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (tag == Constants.CLICK_PRAISE) {
//				topicAdapter.updatePraiseNum(result);
            } else if (tag == Constants.STATISTICSES) {
                Log.i("statisticses", "统计成功！");
            }
        }

    }

    private class SwipeRefreshListener implements OnRefreshListener {
        private int index;

        public SwipeRefreshListener(int index) {
            super();
            this.index = index;
        }

        @Override
        public void onRefresh() {
            refresh(index);
        }
    }

    private class LondonXScrollListener implements OnScrollListener {
        public static final int nextPageError = 0;
        public static final int nextPageAvailable = 1;
        public static final int nextPageEnd = 2;
        private int index;
        private int firstIndex = -1;

        public LondonXScrollListener(int index) {
            super();
            this.index = index;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE
                    && view.getLastVisiblePosition() == view.getCount() - 1) {
                View footerView = index == 0 ? hotDealsFooterView
                        : topicsFooterView;
                if (footerView != null) {
                    Object tag = footerView.getTag();
                    if (tag == null) {
                        footerView.setTag(nextPageAvailable);
                    }
                } else {
                    return;
                }
                if (footerView.getTag().equals(nextPageError)) {
                    loadMore(index);
                } else if (footerView.getTag().equals(nextPageAvailable)) {
                    loadMore(index);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (firstIndex == -1) {
                firstIndex = firstVisibleItem;
                return;
            }
            if (firstIndex < firstVisibleItem) {
                hideUi();
            } else if (firstIndex > firstVisibleItem) {
                showUi();
            }
            firstIndex = firstVisibleItem;
        }
    }

    private class MainActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.equals(AuthTool.ACTION_CH_USER)) {
                actionProvider.logStateChange();
            }
        }
    }

}

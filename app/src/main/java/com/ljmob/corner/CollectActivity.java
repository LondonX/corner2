package com.ljmob.corner;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.corner.adapter.CollectFavorableAdapter;
import com.ljmob.corner.adapter.MainPagerAdapter;
import com.ljmob.corner.adapter.MerchantAdapter;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.CollectFavorable;
import com.ljmob.corner.entity.Shop;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.UrlStaticUtil;

public class CollectActivity extends ActionBarActivity implements OnItemClickListener,
        OnPageChangeListener, OnResponseResult, OnClickListener {
    private ArrayList<TextView> tabs;
    private ArrayList<View> tabIndicators;
    private ViewPager activity_collect_pager;
    private List<Shop> shopList = new ArrayList<Shop>();
    private List<CollectFavorable> collectFavorableList = new ArrayList<CollectFavorable>();
    private List<View> listViews = new ArrayList<View>();

    private ServiceImpl serviceImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ImageLoader imageLoader = new ImageLoader(this);
        serviceImpl = FactoryService.getIService(this);
        initView();
        initToolBar();
        selectPage(0);
    }

    @Override
    protected void onRestart() {
        if (activity_collect_pager.getCurrentItem() == 0) {
            QueryMerchant();// 查询收藏商品
        } else {
            QueryCoupons();// 查询收藏优惠券
        }
        super.onRestart();
    }

    private void QueryMerchant() {
        serviceImpl.getQueryMerchantCollect(
                UrlStaticUtil.getQueryMerchantCollect(),
                Constants.QUERY_MERCHANT_COLLECT);
    }

    private void QueryCoupons() {
        serviceImpl.getQueryCouponsCollect(
                UrlStaticUtil.getQueryCouponsCollect(),
                Constants.QUERY_COUPONS_COLLECT);
    }

    private void initView() {
        activity_collect_pager = (ViewPager) findViewById(R.id.activity_collect_pager);
        ListView merchantListView = new ListView(this);
        ListView favorableListView = new ListView(this);
        merchantListView.setSelector(R.drawable.selector_btn_default);
        favorableListView.setSelector(R.drawable.selector_btn_default);
        listViews.add(merchantListView);
        listViews.add(favorableListView);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(listViews);
        activity_collect_pager.setAdapter(mainPagerAdapter);
        activity_collect_pager.setOnPageChangeListener(this);
        merchantListView.setOnItemClickListener(this);
        favorableListView.setOnItemClickListener(this);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setHomeButtonEnabled(true);
        tabs = new ArrayList<>();
        tabIndicators = new ArrayList<>();
        tabIndicators = new ArrayList<>();
        tabs.add((TextView) findViewById(R.id.activity_collect_tvTab0));
        tabs.add((TextView) findViewById(R.id.activity_collect_tvTab1));
        tabIndicators.add(findViewById(R.id.activity_main_view0));
        tabIndicators.add(findViewById(R.id.activity_main_view1));
        ab.setTitle(R.string.activity_collect);
        for (TextView tab : tabs) {
            tab.setOnClickListener(this);
        }
    }

    private void selectPage(int index) {
        switch (index) {
            case 0:
                tabs.get(1).setEnabled(true);
                tabs.get(1).setAlpha(0.8f);
                tabIndicators.get(1).setVisibility(View.INVISIBLE);
                break;
            case 1:
                tabs.get(0).setEnabled(true);
                tabs.get(0).setAlpha(0.8f);
                tabIndicators.get(0).setVisibility(View.INVISIBLE);
                break;
        }
        tabs.get(index).setEnabled(false);
        tabs.get(index).setAlpha(1.0f);
        tabIndicators.get(index).setVisibility(View.VISIBLE);
        if (activity_collect_pager.getCurrentItem() != index) {
            activity_collect_pager.setCurrentItem(index);
        }
        ListView lv = (ListView) listViews.get(index);
        if (lv.getAdapter() == null) {
            switch (index) {
                case 0:
                    QueryMerchant();// 查询收藏商品
                    break;
                case 1:
                    QueryCoupons();// 查询收藏优惠券
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (activity_collect_pager.getCurrentItem() == 0) {
            MerchantAdapter merchantAdapter = (MerchantAdapter) parent
                    .getAdapter();
            Shop shop = (Shop) merchantAdapter.getItem(position);
            Intent merchantIntent = new Intent(this, ShopActivity.class);
            merchantIntent.putExtra("id", shop.id);
            startActivity(merchantIntent);
        } else if (activity_collect_pager.getCurrentItem() == 1) {
            CollectFavorableAdapter collectFavorableAdapter = (CollectFavorableAdapter) parent
                    .getAdapter();
            CollectFavorable collectFavorable = (CollectFavorable) collectFavorableAdapter
                    .getItem(position);
            Intent collectFavorableIntent = new Intent(this,
                    FavorDetailActivity.class);
            collectFavorableIntent.putExtra("id", collectFavorable.id);
            startActivity(collectFavorableIntent);
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
        if (tag == Constants.QUERY_MERCHANT_COLLECT) {
            MerchantAdapter merchantAdapter = new MerchantAdapter(this,
                    JsonTools.getShopList(result));
            ((ListView) listViews.get(0)).setAdapter(merchantAdapter);
        } else if (tag == Constants.QUERY_COUPONS_COLLECT) {
            CollectFavorableAdapter collectFavorableAdapter = new CollectFavorableAdapter(this,
                    JsonTools.getCollectFavorableList(result));
            ((ListView) listViews.get(1)).setAdapter(collectFavorableAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        selectPage(tabs.indexOf(v));
    }
}

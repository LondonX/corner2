package com.ljmob.corner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;

import com.ljmob.corner.adapter.MerchantAdapter;
import com.ljmob.corner.entity.Shop;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MacUtil;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.CustomListView;
import com.ljmob.corner.view.CustomListView.OnLoadMoreListener;
import com.ljmob.corner.view.UnscrollableGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener,
        OnResponseResult, OnItemClickListener {
    private SearchView searchView;
    private LinearLayout activity_search_linearHot;
    private UnscrollableGridView activity_search_gvHot;
    private CustomListView activity_search_lvResult;
    private View head_search;

    private List<Shop> shopList = new ArrayList<Shop>();
    private MerchantAdapter merchantAdapter;
    private ServiceImpl serviceImpl;
    private int page = 1;
    private int pre_page = 10;
    private String searchString = "";
    private boolean isLoad = false;
    private MacUtil macUtil;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        application = (MyApplication) getApplication();
        macUtil = new MacUtil(this);
        initView();
        initToolBar();
        serviceImpl = FactoryService.getIService(this);
        serviceImpl.getDialog().setMsg(R.string.dialog_search);
        serviceImpl.getDialog().setCanShowDialog(false);
    }

    private void getSearchResult(String searchString, int page, int pre_page) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ip", macUtil.getIp());
        params.put("mac_addr", macUtil.getMac());
        params.put("longitude", application.mixtureLocatioin.getGeoLng());// 经度
        params.put("latitude", application.mixtureLocatioin.getGeoLat());// 纬度
//		params.put("my_lng", application.mixtureLocatioin.getGeoLng());// 经度
//		params.put("my_lat", application.mixtureLocatioin.getGeoLat());// 纬度
        serviceImpl.getSearchShop(
                UrlStaticUtil.getSearchShop(searchString, page, pre_page),
                params, new HashMap<String, File>(), Constants.SEARCH_SHOP);
    }

    private void initView() {
        activity_search_linearHot = (LinearLayout) this
                .findViewById(R.id.activity_search_linearHot);
        activity_search_gvHot = (UnscrollableGridView) this
                .findViewById(R.id.activity_search_gvHot);
        activity_search_lvResult = (CustomListView) this
                .findViewById(R.id.activity_search_lvResult);
        head_search = LayoutInflater.from(this).inflate(R.layout.head_search,
                null);
        activity_search_linearHot.setVisibility(View.GONE);
        activity_search_lvResult.setCanRefresh(false);
        activity_search_lvResult.setCanLoadMore(false);
        activity_search_lvResult.removeHeaderView(activity_search_lvResult
                .getHeadView());
        activity_search_lvResult.setOnItemClickListener(this);
        activity_search_lvResult.setOnLoadListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                isLoad = true;
                page++;
                // serviceImpl.getDialog().setCanShowDialog(false);
                getSearchResult(searchString, page, pre_page);
            }
        });
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.activity_search);
        ab.setDisplayShowHomeEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void LoadData(List<Shop> tempList, boolean isLoad) {
        if (merchantAdapter != null && tempList != null) {
            if (isLoad) {
                if (tempList.size() == 0) {
                    ToastUtil.show(this, R.string.toast_end_load);
                    activity_search_lvResult.onLoadMoreComplete();
                    activity_search_lvResult.setCanLoadMore(false);
                } else {
                    merchantAdapter.LoadData(tempList, isLoad);
                    activity_search_lvResult.onLoadMoreComplete();
                }
            } else {
                merchantAdapter.LoadData(tempList, isLoad);
                if (tempList.size() < 10) {
                    activity_search_lvResult.setCanLoadMore(false);
                }
            }
            if (tempList.size() >= 10) {
                activity_search_lvResult.setCanLoadMore(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.str_search_hint));
        searchView.setFocusable(true);
        searchView.requestFocus();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) getApplication()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                // inputManager.toggleSoftInput(0,
                // InputMethodManager.HIDE_NOT_ALWAYS);
                inputManager.showSoftInput(searchView,
                        InputMethodManager.SHOW_FORCED);
            }
        }, 500);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.replace(" ", "");
        if (newText != null && newText.length() > 0) {
            if (activity_search_lvResult.getHeaderViewsCount() > 0) {
                activity_search_lvResult.removeHeaderView(head_search);
            }
            activity_search_lvResult.addHeaderView(head_search);
            Log.i("newText", newText);
            // serviceImpl.getDialog().setCanShowDialog(true);
            isLoad = false;
            page = 1;
            // activity_search_lvResult.setCanLoadMore(true);
            searchString = newText;
            getSearchResult(newText, page, pre_page);
        }
        return false;
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            shopList = JsonTools.getShopList(result);
            if (shopList == null) {
                shopList = new ArrayList<Shop>();
            }
            if (activity_search_lvResult.getHeaderViewsCount() > 0) {
                activity_search_lvResult.removeHeaderView(head_search);
            }
            if (merchantAdapter == null) {
                merchantAdapter = new MerchantAdapter(this, shopList);
                activity_search_lvResult.setAdapter(merchantAdapter);
                if (shopList.size() >= 10) {
                    activity_search_lvResult.setCanLoadMore(true);
                }
            } else {
                LoadData(shopList, isLoad);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        MerchantAdapter adapter = null;
        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                    .getAdapter();
            adapter = (MerchantAdapter) listAdapter.getWrappedAdapter();
        } else {
            adapter = (MerchantAdapter) parent.getAdapter();
        }
        Shop shop = (Shop) adapter.getItem(position);
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra("id", shop.id);
        startActivity(intent);
    }
}

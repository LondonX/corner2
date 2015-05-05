package com.ljmob.corner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;

import com.ljmob.corner.adapter.MerchantAdapter;
import com.ljmob.corner.entity.Shop;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.CustomListView;
import com.ljmob.corner.view.CustomListView.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class FitShopActivity extends ActionBarActivity implements OnItemClickListener,
        OnResponseResult {
    private CustomListView activity_fitshop_lv;
    private MerchantAdapter merchantAdapter;
    private List<Shop> shopList = new ArrayList<Shop>();
    private ServiceImpl serviceImpl;
    private String belong_mer;
    private int page = 1;
    private int pre_page = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitshop);
        initToolBar();
        initView();
        belong_mer = getIntent().getStringExtra("belong_mer");
        serviceImpl = FactoryService.getIService(this);
        serviceImpl.getDialog().setMsg(R.string.dialog_load_shop);
        getMoreShop(page, pre_page);
    }

    private void getMoreShop(int page, int pre_page) {
        serviceImpl.getMoreShop(
                UrlStaticUtil.getMoreShop(belong_mer, page, pre_page),
                Constants.MORE_SHOP);
    }

    private void initToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_moreshop);
    }

    private void initView() {
        activity_fitshop_lv = (CustomListView) this
                .findViewById(R.id.activity_fitshop_lv);
        activity_fitshop_lv.setCanRefresh(false);
        activity_fitshop_lv.setOnItemClickListener(this);
        activity_fitshop_lv.setOnLoadListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                page++;
                getMoreShop(page, pre_page);
            }
        });
    }

    private void loadData(List<Shop> tempList) {
        if (tempList != null) {
            if (merchantAdapter != null) {
                if (tempList.size() == 0) {
                    ToastUtil.show(this, R.string.toast_end_shop);
                    activity_fitshop_lv.onLoadMoreComplete();
                    activity_fitshop_lv.setCanLoadMore(false);
                } else {
                    merchantAdapter.LoadData(tempList, true);
                    activity_fitshop_lv.onLoadMoreComplete();
                }
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
        MerchantAdapter adapter = null;
        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                    .getAdapter();
            adapter = (MerchantAdapter) listAdapter.getWrappedAdapter();
        } else {
            adapter = (MerchantAdapter) parent.getAdapter();
        }
        Shop shop = (Shop) adapter.getItem(position - 1);
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra("id", shop.id);
        startActivity(intent);
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.MORE_SHOP) {
                shopList = JsonTools.getShopList(result);
                if (merchantAdapter == null) {
                    merchantAdapter = new MerchantAdapter(this, shopList);
                    activity_fitshop_lv.setAdapter(merchantAdapter);
                    serviceImpl.getDialog().setCanShowDialog(false);
                } else {
                    loadData(shopList);
                }
            }
        }
    }
}

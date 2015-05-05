package com.ljmob.corner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanjing.galleryView.ImageGalleryActivity;
import com.ljmob.corner.actionprovider.MyShareActionProvider;
import com.ljmob.corner.adapter.FitShopAdapter;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Shareable;
import com.ljmob.corner.entity.Shop;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.CustomListView;
import com.ljmob.corner.view.CustomListView.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠详情
 *
 * @author YANGBANG
 */
public class FavorDetailActivity extends ActionBarActivity implements OnClickListener,
        OnItemClickListener, OnResponseResult {
    private Toolbar tb;
    private ImageView listhead_fd_imgFavor;// 头部图片
    private TextView listhead_fd_tvTime;// 时间
    private TextView listhead_fd_tvDetail;// 细节描述
    private TextView listhead_fd_textUseage;// 使用方法
    private TextView listhead_fd_textFitShop;// 适用商户字样
    private View headerView;
    private CustomListView activity_fd_lv;
    private FitShopAdapter adapter;
    private List<Shop> shopList = new ArrayList<Shop>();
    private ServiceImpl serviceImpl;
    private int id;
    private String title;
    private ImageLoader imageLoader;
    private String img_url = "";

    private MenuItem menu_collect;
    private MenuItem menu_Cancel_collect;
    private MyShareActionProvider shareActionProvider;

    private int page = 1;
    private int pre_page = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favordetail);
        initView();
        initToolBar();
        imageLoader = new ImageLoader(this);
        id = getIntent().getIntExtra("id", -1);
        title = getIntent().getStringExtra("title");
        serviceImpl = new ServiceImpl(this);
    }

    private void initToolBar() {
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_fd);
    }

    private void getCoupon(int page, int pre_page) {
        serviceImpl.getCouponDetail(
                UrlStaticUtil.getCouponDetail(id, page, pre_page),
                Constants.COUPON_DETAIL);
    }

    private void initView() {
        headerView = LayoutInflater.from(this).inflate(
                R.layout.head_favordetail, null);
        tb = (Toolbar) findViewById(R.id.toolbar_root);
        listhead_fd_imgFavor = (ImageView) headerView
                .findViewById(R.id.listhead_fd_imgFavor);
        listhead_fd_tvTime = (TextView) headerView
                .findViewById(R.id.listhead_fd_tvTime);
        listhead_fd_tvDetail = (TextView) headerView
                .findViewById(R.id.listhead_fd_tvDetail);
        listhead_fd_textUseage = (TextView) headerView
                .findViewById(R.id.listhead_fd_textUseage);
        listhead_fd_textFitShop = (TextView) headerView
                .findViewById(R.id.listhead_fd_textFitShop);
        activity_fd_lv = (CustomListView) this
                .findViewById(R.id.activity_fd_lv);
        activity_fd_lv.setCanRefresh(false);// 设置不能下拉刷新
        activity_fd_lv.addHeaderView(headerView, null, false);
        activity_fd_lv.setOnItemClickListener(this);
        listhead_fd_imgFavor.setOnClickListener(this);
        activity_fd_lv.setOnLoadListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                page++;
                getCoupon(page, pre_page);
            }
        });
    }

    private void setCouponDetail(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (menu_collect != null && menu_Cancel_collect != null) {
                if (jsonObject.getInt("is_favorite") == 0) {
                    menu_collect.setVisible(true);
                    menu_Cancel_collect.setVisible(false);
                } else if (jsonObject.getInt("is_favorite") == 1) {
                    menu_collect.setVisible(false);
                    menu_Cancel_collect.setVisible(true);
                }
            }
            JSONObject header = jsonObject.getJSONObject("coupon");
            img_url = UrlStaticUtil.root_photo + header.getString("max_poster");
            imageLoader.DisplayImage(
                    UrlStaticUtil.root_photo + header.getString("max_poster"),
                    listhead_fd_imgFavor, false, ImageLoader.X_PICTURE);
            listhead_fd_tvDetail.setText(Html.fromHtml(header
                    .getString("content")));

            String useage = header.getString("use_method");
            if (useage.startsWith("http")) {
                useage = "<a href=" + useage + ">" + useage + "</a>";
                listhead_fd_textUseage.setAutoLinkMask(Linkify.WEB_URLS);
            }
            listhead_fd_textUseage.setText(Html.fromHtml(useage));
            listhead_fd_tvTime.setText(header.getString("end_date"));
            JSONArray list = jsonObject.getJSONArray("merchants");
            shopList = JsonTools.getShopList(list.toString());
            adapter = new FitShopAdapter(this, shopList);
            activity_fd_lv.setAdapter(adapter);
            if (shopList.size() == 0) {
                activity_fd_lv.setCanLoadMore(false);
                listhead_fd_textFitShop.setVisibility(View.GONE);
            } else if (shopList.size() < pre_page) {
                activity_fd_lv.setCanLoadMore(false);
            }

            Shareable shareable = new Shareable(this);
            shareable.setImgUrl(UrlStaticUtil.root_photo
                    + header.getString("max_poster"));

            String shareText = getString(R.string.share_text1);
            shareText = shareText.replace("<FAVOR>", title);

            shareable.setShareableView(activity_fd_lv);
            shareable.setText(shareText);
            shareActionProvider.setShareable(shareable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadData(List<Shop> tempList) {
        if (tempList != null && tempList.size() == 0) {
            ToastUtil.show(this, R.string.toast_end_load);
            page--;
            activity_fd_lv.onLoadMoreComplete();
            activity_fd_lv.setCanLoadMore(false);
        } else {
            adapter.loadDada(tempList);
            activity_fd_lv.onLoadMoreComplete();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_collect, menu);
        menu_collect = menu.findItem(R.id.menu_collect);
        menu_Cancel_collect = menu.findItem(R.id.menu_collect_cancel);
        MenuItem shareItem = menu.findItem(
                R.id.menu_share);
        shareActionProvider = (MyShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        getCoupon(page, pre_page);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.menu_collect) {
            if (new SelectLoginWay(this).isLogin()) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("coupon_id", id);
                Map<String, File> files = new HashMap<String, File>();
                serviceImpl.getAddCouponsCollect(
                        UrlStaticUtil.getAddCouponsCollect(), params, files,
                        Constants.ADD_COUPONS_COLLECT);
            }
        } else if (item.getItemId() == R.id.menu_collect_cancel) {
            if (new SelectLoginWay(this).isLogin()) {
                serviceImpl.getRemoveCouponsCollect(
                        UrlStaticUtil.getRemoveCouponsCollect(id),
                        Constants.REMOVE_COUPONS_COLLECT);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listhead_fd_textUseage:
                ToastUtil.show(this, R.string.toast_str_useage);
                break;
            case R.id.listhead_fd_imgFavor:
                Intent intent = new Intent(this, ImageGalleryActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                urls.add(img_url);
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
        FitShopAdapter fitShopAdapter = null;
        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                    .getAdapter();
            fitShopAdapter = (FitShopAdapter) listAdapter.getWrappedAdapter();
        } else {
            fitShopAdapter = (FitShopAdapter) parent.getAdapter();
        }
        Shop shop = (Shop) fitShopAdapter.getItem(position - 2);
        Intent intent = new Intent(FavorDetailActivity.this, ShopActivity.class);
        intent.putExtra("id", shop.id);
        startActivity(intent);
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.COUPON_DETAIL) {
                if (adapter == null) {
                    setCouponDetail(result);
                    serviceImpl.getDialog().setCanShowDialog(false);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray array = jsonObject.getJSONArray("merchants");
                        loadData(JsonTools.getShopList(array.toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (tag == Constants.ADD_COUPONS_COLLECT) {
                ToastUtil.show(this, R.string.toast_ok_collect);
                if (menu_collect != null && menu_Cancel_collect != null) {
                    menu_collect.setVisible(false);
                    menu_Cancel_collect.setVisible(true);
                }
            } else if (tag == Constants.REMOVE_COUPONS_COLLECT) {
                ToastUtil.show(this, R.string.toast_cancel_collect);
                if (menu_collect != null && menu_Cancel_collect != null) {
                    menu_collect.setVisible(true);
                    menu_Cancel_collect.setVisible(false);
                }
            }
        }
    }

}

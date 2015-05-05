package com.ljmob.corner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ljmob.corner.adapter.MerchantAdapter;
import com.ljmob.corner.adapter.SortPopupAdapter;
import com.ljmob.corner.amap.MainMapActivity;
import com.ljmob.corner.entity.KeyWord;
import com.ljmob.corner.entity.Poi;
import com.ljmob.corner.entity.Shop;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.AreaUtil;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.CustomListView;
import com.ljmob.corner.view.CustomListView.OnLoadMoreListener;
import com.ljmob.corner.view.SortPopupWindow;

public class MapActivity extends ActionBarActivity implements OnClickListener,
        OnItemClickListener, OnResponseResult {
    private final int LWZJ = 100;// 离我最近
    private final int PJZH = 101;// 评价最好
    private final int ZXFB = 102;// 最新发布
    private int sort_Type;// 排序类型
    private LinearLayout activity_map_linearTabs;
    private RelativeLayout activity_map_rlTab0;// 美食
    private TextView activity_map_tvTab0;
    private ImageView activity_map_imgTab0;
    private RelativeLayout activity_map_rlTab1;// 商区
    private TextView activity_map_tvTab1;
    private ImageView activity_map_imgTab1;
    private RelativeLayout activity_map_rlTab2;// 排序
    private TextView activity_map_tvTab2;
    private ImageView activity_map_imgTab2;
    private CustomListView activity_map_lv;
    private MerchantAdapter adapter;
    private List<Shop> shopList = new ArrayList<Shop>();
    private ListView listView;
    private View contextView;
    private SortPopupWindow popupWindows[] = new SortPopupWindow[2];
    private int tabIndex = 1;
    private List<Poi> poiList = new ArrayList<Poi>();
    private List<Poi> areaList = new ArrayList<Poi>();
    private List<Poi> rankList = new ArrayList<Poi>();
    private List<Poi> publicList = new ArrayList<Poi>();
    private String belong_kinds = "";// 所属类型
    private String belong_area = "";// 所属商圈
    private String type = "";// 类型：区域或者美食服装等
    private ServiceImpl serviceImpl;
    private MyApplication application;
    private int page = 1;
    private int pre_page = 10;
    private boolean isLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        application = (MyApplication) getApplication();
        // application.mixtureLocatioin = new MixtureLocatioin(this);
        belong_kinds = getIntent().getStringExtra("belong_kinds");
        type = getIntent().getStringExtra("type");
        tabIndex = getIntent().getIntExtra("tag", -1);
        serviceImpl = FactoryService.getIService(this);
        initToolBar();
        initView();
        initData();
        getSortShop(type, belong_kinds, page, pre_page);
        setTabSelect(tabIndex);
        setTabText();
    }

    private void setTabText() {
        switch (tabIndex) {
            case 0:
                activity_map_tvTab0.setText(belong_kinds);
                break;
            case 1:
                activity_map_tvTab1.setText(belong_kinds);
                break;
            case 2:
                activity_map_tvTab2.setText(belong_kinds);
                break;

            default:
                break;
        }
    }

    /**
     * 取得子分类商铺
     */
    private void getSortShop(String type, String belong_kinds, int page,
                             int pre_page) {
        serviceImpl.getSortShop(UrlStaticUtil.getSortShop(type, belong_kinds,
                page, pre_page, application.mixtureLocatioin.getGeoLat(),
                application.mixtureLocatioin.getGeoLng()), Constants.SORT_SHOP);
    }

    /**
     * 取得离我最近列表
     */
    private void getNearSort(int page, int pre_page) {
        serviceImpl.getNearSort(UrlStaticUtil.getNearSort(belong_kinds,
                        belong_area, application.mixtureLocatioin.getGeoLat(),
                        application.mixtureLocatioin.getGeoLng(), page, pre_page),
                Constants.NEAR_SORT);
    }

    /**
     * @param sort 如果是按评分排序,则传入值score,如果是最新发布传入值updated_at
     */
    private void getRestsSort(String sort, int page, int pre_page) {
        serviceImpl
                .getRestsSort(UrlStaticUtil.getRestsSort(belong_kinds,
                                belong_area, sort, page, pre_page,
                                application.mixtureLocatioin.getGeoLat(),
                                application.mixtureLocatioin.getGeoLng()),
                        Constants.RESTS_SORT);
    }

    private void initView() {
        activity_map_linearTabs = (LinearLayout) this
                .findViewById(R.id.activity_map_linearTabs);
        activity_map_rlTab0 = (RelativeLayout) this
                .findViewById(R.id.activity_map_rlTab0);
        activity_map_rlTab1 = (RelativeLayout) this
                .findViewById(R.id.activity_map_rlTab1);
        activity_map_rlTab2 = (RelativeLayout) this
                .findViewById(R.id.activity_map_rlTab2);
        activity_map_tvTab0 = (TextView) this
                .findViewById(R.id.activity_map_tvTab0);
        activity_map_tvTab1 = (TextView) this
                .findViewById(R.id.activity_map_tvTab1);
        activity_map_tvTab2 = (TextView) this
                .findViewById(R.id.activity_map_tvTab2);
        activity_map_imgTab0 = (ImageView) this
                .findViewById(R.id.activity_map_imgTab0);
        activity_map_imgTab1 = (ImageView) this
                .findViewById(R.id.activity_map_imgTab1);
        activity_map_imgTab2 = (ImageView) this
                .findViewById(R.id.activity_map_imgTab2);
        activity_map_lv = (CustomListView) this
                .findViewById(R.id.activity_map_lv);
        activity_map_rlTab0.setOnClickListener(this);
        activity_map_rlTab1.setOnClickListener(this);
        activity_map_rlTab2.setOnClickListener(this);
        activity_map_lv.setOnItemClickListener(this);
        activity_map_lv.setOnLoadListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                serviceImpl.getDialog().setCanShowDialog(false);
                isLoad = true;
                page++;
                if (tabIndex == 0) {
                    getSortShop("belong_kinds", belong_kinds, page, pre_page);
                } else if (tabIndex == 1) {
                    getSortShop("district", belong_kinds, page, pre_page);
//					getSortShop("belong_area", belong_kinds, page, pre_page);
                } else if (tabIndex == 2) {
                    if (sort_Type == LWZJ) {
                        getNearSort(page, pre_page);
                    } else if (sort_Type == PJZH) {
                        getRestsSort("score", page, pre_page);
                    } else if (sort_Type == ZXFB) {
                        getRestsSort("updated_at", page, pre_page);
                    }
                }
            }
        });
    }

    private void initData() {
//		poiList = JsonTools.getPoiList(application.sortArray.toString());
//		poiList = poiList.subList(0, 2);
        AreaUtil areaUtil = new AreaUtil();
        poiList = areaUtil.getGwmsList();
        areaList = areaUtil.getAreaList();
        Poi poi = new Poi();
        poi.type = "排序";
        poi.item = new ArrayList<KeyWord>();
        KeyWord keyWord0 = new KeyWord();
        keyWord0.name = "离我最近";
        keyWord0.keyword = "离我最近";
        poi.item.add(keyWord0);

        KeyWord keyWord1 = new KeyWord();
        keyWord1.name = "评价最好";
        keyWord1.keyword = "评价最好";
        poi.item.add(keyWord1);

        KeyWord keyWord2 = new KeyWord();
        keyWord2.name = "最新发布";
        keyWord2.keyword = "最新发布";
        poi.item.add(keyWord2);

        rankList.add(poi);
    }

    /**
     * 初始化弹出窗体ListView
     *
     * @param tag 0代表1级ListView，1代表2级ListView
     */
    private void initPopupListView(int tag, int index,
                                   boolean isShowRightArrows, List<Poi> poiList) {
        // List<SortPopUp> sortPopUpList = new ArrayList<SortPopUp>();
        SortPopupAdapter popupAdapter;
        listView = new ListView(this);
        listView.setTag(tag);
        listView.setSelector(new ColorDrawable(0x11000000));
        popupAdapter = new SortPopupAdapter(this, poiList, tag, index,
                isShowRightArrows);
        listView.setAdapter(popupAdapter);
        listView.setOnItemClickListener(this);
    }

    private void LoadData(List<Shop> tempList, boolean isLoad) {
        if (tempList != null) {
            if (isLoad) {
                if (tempList.size() == 0) {
                    ToastUtil.show(this, R.string.toast_end_load);
                    activity_map_lv.onLoadMoreComplete();
                    activity_map_lv.setCanLoadMore(false);
                } else {
                    adapter.LoadData(tempList, isLoad);
                    activity_map_lv.onLoadMoreComplete();
                }
            } else {
                adapter.LoadData(tempList, isLoad);
                if (tempList.size() < pre_page) {
                    activity_map_lv.setCanLoadMore(false);
                }
            }
            if (tempList.size() >= pre_page) {
                activity_map_lv.setCanLoadMore(true);
            }
        } else {
            ToastUtil.show(this, "加载异常");
            activity_map_lv.onLoadMoreComplete();
            activity_map_lv.setCanLoadMore(false);
        }
    }

    private void setTabSelect(int index) {
        switch (index) {
            case 0:
                activity_map_imgTab0.setColorFilter(getResources().getColor(
                        R.color.red));
                activity_map_imgTab1.clearColorFilter();
                activity_map_imgTab2.clearColorFilter();
                break;
            case 1:
                activity_map_imgTab0.clearColorFilter();
                activity_map_imgTab1.setColorFilter(getResources().getColor(
                        R.color.red));
                activity_map_imgTab2.clearColorFilter();
                break;
            case 2:
                activity_map_imgTab0.clearColorFilter();
                activity_map_imgTab1.clearColorFilter();
                activity_map_imgTab2.setColorFilter(getResources().getColor(
                        R.color.red));
                break;

            default:
                break;
        }
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        switch (item.getItemId()) {
            case R.id.menu_map:
                if (adapter == null) {
                    break;
                }
                Intent intent = new Intent(this, MainMapActivity.class);
                intent.putExtra("shopList", (Serializable) adapter.getShopList());
                startActivity(intent);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_map_rlTab0:
                tabIndex = 0;
                initPopupListView(0, 0, true, poiList);
                publicList = poiList;
                contextView = activity_map_rlTab1;
                popupWindows[0] = new SortPopupWindow(this, listView);
                popupWindows[0].showAsDropDown(activity_map_rlTab0);
                break;
            case R.id.activity_map_rlTab1:
                tabIndex = 1;
                initPopupListView(0, 0, true, areaList);
                publicList = areaList;
                contextView = activity_map_rlTab2;
                popupWindows[0] = new SortPopupWindow(this, listView);
                popupWindows[0].showAsDropDown(activity_map_rlTab0);
                break;
            case R.id.activity_map_rlTab2:
                tabIndex = 2;
                initPopupListView(1, 0, false, rankList);
                publicList = rankList;
                contextView = activity_map_rlTab1;
                popupWindows[0] = new SortPopupWindow(this, listView);
                popupWindows[0].showAsDropDown(v);
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (parent.getId() == R.id.activity_map_lv) {
            MerchantAdapter merchantAdapter = null;
            if (parent.getAdapter() instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                        .getAdapter();
                merchantAdapter = (MerchantAdapter) listAdapter
                        .getWrappedAdapter();
            } else {
                merchantAdapter = (MerchantAdapter) parent.getAdapter();
            }
            Intent intent = new Intent(MapActivity.this, ShopActivity.class);
            Shop shop = (Shop) merchantAdapter.getItem(position - 1);
            intent.putExtra("id", shop.id);
            startActivity(intent);
        } else if ((Integer) parent.getTag() == 0) {
            initPopupListView(1, position, true, publicList);
            popupWindows[1] = new SortPopupWindow(this, listView);
            // popupWindows[1].showAsDropDown(contextView);
            popupWindows[1].showAsDropDown(activity_map_rlTab0, getResources()
                    .getDisplayMetrics().widthPixels / 2, 0);
            // ((ImageView) view.findViewById(R.id.listitem_pop_imgArrow))
            // .setColorFilter(getResources().getColor(R.color.red));
            // ((ImageView) view.findViewById(R.id.listitem_pop_imgArrowLeft))
            // .setColorFilter(getResources().getColor(R.color.red));
        } else {
            page = 1;
            serviceImpl.getDialog().setCanShowDialog(true);
            isLoad = false;
            SortPopupAdapter adapter = (SortPopupAdapter) parent.getAdapter();
            KeyWord keyWord = ((KeyWord) adapter.getItem(position));
            // ToastUtil.show(this, keyWord.name);
            for (int i = 0; i < popupWindows.length; i++) {
                if (popupWindows[i] != null && popupWindows[i].isShowing()) {
                    popupWindows[i].dismiss();
                }
            }
            setTabSelect(tabIndex);
            switch (tabIndex) {
                case 0:
                    activity_map_tvTab0.setText(keyWord.keyword);
                    belong_kinds = keyWord.keyword;
                    getSortShop("belong_kinds", keyWord.keyword, page, pre_page);
                    break;
                case 1:
                    activity_map_tvTab1.setText(keyWord.keyword);
                    belong_area = keyWord.keyword;
                    getSortShop("district", keyWord.keyword, page, pre_page);
                    break;
                case 2:
                    activity_map_tvTab2.setText(keyWord.keyword);
                    if (position == 0) {// 离我最近
                        sort_Type = LWZJ;
                        getNearSort(page, pre_page);
                    } else if (position == 1) {// 评价最好
                        sort_Type = PJZH;
                        getRestsSort("score", page, pre_page);
                    } else if (position == 2) {// 最新发布
                        sort_Type = ZXFB;
                        getRestsSort("updated_at", page, pre_page);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            Log.i("page", "page-->" + page);
            shopList = JsonTools.getShopList(result);
            if (shopList == null || shopList.size() == 0) {
                ToastUtil.show(this, R.string.toast_error_searchshop);
                return;
            }
            if (adapter == null) {
                adapter = new MerchantAdapter(this,
                        JsonTools.getShopList(result));
                activity_map_lv.setAdapter(adapter);
                if (shopList.size() < pre_page) {
                    activity_map_lv.setCanLoadMore(false);
                }
            } else {
                LoadData(shopList, isLoad);
            }
        }
    }
}

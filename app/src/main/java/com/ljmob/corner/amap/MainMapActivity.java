package com.ljmob.corner.amap;

import android.annotation.SuppressLint;
import android.app.ActionBar.OnNavigationListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.ljmob.corner.R;
import com.ljmob.corner.adapter.SortPopupAdapter;
import com.ljmob.corner.entity.KeyWord;
import com.ljmob.corner.entity.Poi;
import com.ljmob.corner.entity.Shop;
import com.ljmob.corner.map.util.BluePoint;
import com.ljmob.corner.map.util.MixtureLocatioin;
import com.ljmob.corner.map.util.PoiSearchUtil;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.AreaUtil;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.HttpRequestTools;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MarkerTool;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.SortPopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainMapActivity extends ActionBarActivity implements OnItemClickListener,
        OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener,
        OnMapLoadedListener, InfoWindowAdapter, OnCameraChangeListener,
        OnNavigationListener, OnMapClickListener, OnResponseResult, Callback {
    private MyApplication application;
    private ListView listView;
    private View contextView;
    public MapView mapView;
    public AMap aMap;
    public AMapLocation location;
    public PoiSearchUtil poiSearchUtil;
    private BluePoint bluePoint;
    public MixtureLocatioin mixtureLocatioin;

    public ArrayAdapter<String> aAdapter;

    public List<LatLng> latLngs = new ArrayList<LatLng>();
    private SortPopupWindow popupWindows[] = new SortPopupWindow[2];
    private List<Poi> poiList = new ArrayList<Poi>();
    private ServiceImpl serviceImpl;
    private List<Shop> shopList = new ArrayList<Shop>();
    private List<Marker> markerList = new ArrayList<Marker>();
    private Handler handler;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        initToolBar();
        application = (MyApplication) getApplication();
        handler = new Handler(this);
        if (application.mixtureLocatioin != null) {
            application.mixtureLocatioin.requestLocationListener();
        } else {
            application.mixtureLocatioin = new MixtureLocatioin(this);
        }
        mapView = (MapView) this.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        init();
        serviceImpl = FactoryService.getIService(this);
        mixtureLocatioin = application.mixtureLocatioin;
        // getSortShop("belong_kinds",
        // getIntent().getStringExtra("belong_kinds"),
        // 1, 100);
        if (addMarkersToMap((List<Shop>) getIntent().getSerializableExtra(
                "shopList")) == false) {
            ToastUtil.show(this, "没有找到对应商户");
        }
        // addMarkersToMap();
        initData();
    }

    private void initToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_map);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
            // 移动地图，所有marker自适应显示。LatLngBounds与地图边缘10像素的填充区域
            // LatLngBounds bounds = new LatLngBounds.Builder().include(
            // new LatLng(22.299234, 114.175689)).build();
            // aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

            // 移动到指定位置，并设置当前缩放程度
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    22.299234, 114.175689), 11));
            bluePoint = new BluePoint(this, aMap);
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

    private void getConvertCoord(final Marker marker) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String response = HttpRequestTools
                        .doGet(MainMapActivity.this,
                                "http://restapi.amap.com/v3/assistant/coordinate/convert?locations="
                                        + marker.getPosition().longitude
                                        + ","
                                        + marker.getPosition().longitude
                                        + "&coordsys=gps&key=433e784e22699494b574f62ca063e1e5",
                                "");
                Message message = new Message();
                message.what = 1;
                List<Object> objects = new ArrayList<Object>();
                objects.add(response);
                objects.add(marker);
                message.obj = objects;
                handler.sendMessage(message);
            }
        }).start();
    }

    private boolean addMarkersToMap(List<Shop> shopList) {
        boolean isExistMarker = false;
        aMap.clear();
        int i = 0;
        for (Shop shop : shopList) {
            if (shop.latitude != null && !shop.latitude.equals("")) {
                i++;
                Log.i("MarkerNum",
                        "MarkerNum-->" + i + "个,纬度："
                                + Double.valueOf(shop.latitude) + ",经度："
                                + Double.valueOf(shop.longitude));
                MarkerOptions options = new MarkerOptions();
                try {
                    options.position(new LatLng(Double.valueOf(shop.latitude),
                            Double.valueOf(shop.longitude)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                options.title(shop.name).snippet(shop.address);
                options.draggable(true);
                int icon = MarkerTool.getMarkerIcon(shop.belong_kinds);
                if (icon != 0) {
                    options.icon(BitmapDescriptorFactory.fromResource(icon));
                } else {
                    options.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.zongheshichang));
                }
                aMap.addMarker(options);
                if (isExistMarker == false) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(Double.valueOf(shop.latitude), Double
                                    .valueOf(shop.longitude)), 12));
                }
                isExistMarker = true;
            }
        }
        return isExistMarker;
    }

    private void setUpMap() {
        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        aMap.setOnMapClickListener(this);
    }

    private void initData() {
//		poiList = JsonTools.getPoiList(application.sortArray.toString());
//		poiList = poiList.subList(0, 2);
        AreaUtil areaUtil = new AreaUtil();
        poiList = areaUtil.getGwmsList();
    }

    /**
     * 初始化弹出窗体ListView
     *
     * @param tag 0代表1级ListView，1代表2级ListView
     */
    private void initPopupListView(int tag, int index, boolean isShowRightArrows) {
        SortPopupAdapter popupAdapter;
        listView = new ListView(this);
        listView.setTag(tag);
        listView.setSelector(new ColorDrawable(0x11000000));
        popupAdapter = new SortPopupAdapter(this, poiList, tag, index,
                isShowRightArrows);
        listView.setAdapter(popupAdapter);
        listView.setOnItemClickListener(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        // bluePoint.registerSensorListener();
        // Log.i("YANGBANG", "Marker的角度随手机方向变化而变化已启用");
        // Log.i("YANGBANG", "Marker的角度随手机方向变化而变化已启用");
        // Log.i("YANGBANG", "Marker的角度随手机方向变化而变化已启用");
        // Log.i("YANGBANG", "Marker的角度随手机方向变化而变化已启用");
        // Log.i("YANGBANG", "Marker的角度随手机方向变化而变化已启用");
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        // bluePoint.deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        application.mixtureLocatioin.stopLocation();
        mapView.onDestroy();
        mixtureLocatioin.stopLocation();
    }

    public static void setSearchViewOnClickListener(View v,
                                                    OnClickListener listener) {
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = group.getChildAt(i);
                if (child instanceof LinearLayout
                        || child instanceof RelativeLayout) {
                    setSearchViewOnClickListener(child, listener);
                }

                if (child instanceof TextView) {
                    TextView text = (TextView) child;
                    text.setFocusable(false);
                }
                child.setOnClickListener(listener);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        switch (item.getItemId()) {
            case R.id.menu_more_map_mode:
                initPopupListView(0, 0, false);
                if (contextView == null) {
                    contextView = findViewById(R.id.activity_main_map_viewAnchor);
                }
                popupWindows[0] = new SortPopupWindow(this, listView);
                popupWindows[0].showAsDropDown(contextView, getResources()
                        .getDisplayMetrics().widthPixels * 2 / 3, 0);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 我们可以看到，actonbar的用法跟选项菜单是一样的
    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_mode, menu);
        poiSearchUtil = new PoiSearchUtil(MainMapActivity.this, aMap);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if ((Integer) parent.getTag() == 0) {
            initPopupListView(1, position, true);
            popupWindows[1] = new SortPopupWindow(this, listView);
            popupWindows[1].showAsDropDown(contextView, 0, 0);
            // popupWindows[1].showAsDropDown(contextView, getResources()
            // .getDisplayMetrics().widthPixels * 1 / 3, 0);
        } else {
            SortPopupAdapter adapter = (SortPopupAdapter) parent.getAdapter();
            KeyWord keyWord = (KeyWord) adapter.getItem(position);
            for (int i = 0; i < popupWindows.length; i++) {
                if (popupWindows[i].isShowing()) {
                    popupWindows[i].dismiss();
                }
            }
            getSortShop("belong_kinds", keyWord.keyword, 1, 1000);
            // poiSearchUtil.doSearchQuery(keyWord.keyword, keyWord.code,
            // mixtureLocatioin.getCity());
        }
    }

    @Override
    public void onMapClick(LatLng arg0) {
        List<Marker> markers = aMap.getMapScreenMarkers();
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {
    }

    @Override
    public View getInfoContents(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
    }

    @Override
    public void onMarkerDragEnd(Marker arg0) {
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        arg0.showInfoWindow();
        return false;
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.SORT_SHOP) {
                shopList = JsonTools.getShopList(result);
                if (addMarkersToMap(shopList) == false) {
                    ToastUtil.show(this, "没有找到对应商户");
                }
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                List<Object> response = (List<Object>) msg.obj;
                try {
                    JSONObject jsonObject = new JSONObject((String) response.get(0));
                    if (jsonObject.getInt("status") == 1) {
                        Marker marker = (Marker) response.get(1);
                        String locations = jsonObject.getString("locations");
                        double lon = Double.valueOf(locations.substring(0,
                                locations.indexOf(",")));
                        double lat = Double.valueOf(locations.substring(
                                locations.indexOf(",") + 1, locations.length()));
                        marker.setPosition(new LatLng(lat, lon));
                        // startURI(marker);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
        return false;
    }

}

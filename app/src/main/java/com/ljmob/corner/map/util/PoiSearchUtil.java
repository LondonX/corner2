package com.ljmob.corner.map.util;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.ljmob.corner.R;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.SoftWareDetectionUtil;
import com.ljmob.corner.util.ToastUtil;

public class PoiSearchUtil implements InfoWindowAdapter, OnPoiSearchListener,
		OnClickListener {
	private AMap aMap;
	private String keyWord = "";// 要输入的poi搜索关键字
	private ProgressDialog progDialog = null;// 搜索时进度条
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	private Context context;
	private MyApplication application;

	public PoiSearchUtil(Context context, AMap aMap) {
		this.context = context;
		this.aMap = aMap;
		setUpMap();
		application = (MyApplication) context.getApplicationContext();
	}

	/**
	 * 设置页面监听
	 */
	private void setUpMap() {
		aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog(String msg) {
		if (progDialog == null)
			progDialog = new ProgressDialog(context);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage(msg);
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery(String deepType, String centreLocation) {
		showProgressDialog("正在搜索:\n" + deepType);// 显示进度框
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, deepType, centreLocation);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(100);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		poiSearch = new PoiSearch(context, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	/**
	 * 搜索查询
	 * 
	 * @param queryString
	 *            查询关键字
	 * @param queryType
	 *            查询类型，如：餐饮、美食
	 * @param areaString
	 *            搜索区域，空字符串代表全国
	 */
	public void doSearchQuery(String queryString, String queryType,
			String areaString) {
		showProgressDialog("正在搜索:\n" + queryString);// 显示进度框
		currentPage = 0;
		query = new PoiSearch.Query(queryString, queryType, areaString);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(100);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		poiSearch = new PoiSearch(context, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = context.getString(R.string.str_city_recommand);
		for (int i = 0; i < cities.size(); i++) {
			infomation += context.getString(R.string.str_city_name)
					+ cities.get(i).getCityName()
					+ context.getString(R.string.str_city_code)
					+ cities.get(i).getCityCode()
					+ context.getString(R.string.str_city_adcode)
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(context, infomation);

	}

	/**
	 * 通过URI方式调起高德地图app
	 */
	public void startURI(Marker marker) {
		if (SoftWareDetectionUtil.getAppIn(context)) {
			Intent intent = new Intent(
					"android.intent.action.VIEW",
					android.net.Uri.parse("androidamap://viewMap?sourceApplication="
							+ SoftWareDetectionUtil.getApplicationName(context)
							+ "&poiname="
							+ marker.getTitle()
							+ "&lat="
							+ marker.getPosition().latitude
							+ "&lon="
							+ marker.getPosition().longitude + "&dev=0"));
			intent.setPackage("com.autonavi.minimap");
			context.startActivity(intent);
		} else {
			String url = "http://mo.amap.com/?dev=0&q="
					+ marker.getPosition().latitude + ","
					+ marker.getPosition().longitude + "&name="
					+ marker.getTitle();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			context.startActivity(intent);
		}

	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					List<String> listString = new ArrayList<String>();
					poiResult = result;
					// 取得搜索到的poiitems有多少页
					List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					for (PoiItem item : poiItems) {
						Log.i("YBYB", item.getTitle());
						listString.add(item.getTitle());
					}
					application.listString = listString;
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						// List<PoiItem> poiTempItems=new ArrayList<PoiItem>();
						// poiTempItems.add(poiItems.get(0));
						// poiItems.clear();
						// poiItems.add(poiTempItems.get(0));
						if (application.isListMode == false) {
							aMap.clear();// 清理之前的图标
							BluePointSet.setBluePoint(aMap);
							PoiOverlay poiOverlay = new PoiOverlay(aMap,
									poiItems);
							poiOverlay.removeFromMap();
							poiOverlay.addToMap();
							poiOverlay.zoomToSpan();
						} else {
							context.sendBroadcast(new Intent("upDateList"));
						}
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						ToastUtil.show(context, R.string.no_result);
					}
				}
			} else {
				ToastUtil.show(context, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(context, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(context, R.string.error_key);
		} else {
			ToastUtil.show(context, context.getString(R.string.error_other)
					+ rCode);
		}
	}

	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}

	@Override
	public View getInfoWindow(final Marker marker) {
		View view = new InfoWindow(context, marker).getView();
		// view.setBackgroundColor(Color.GRAY);
		TextView view_ms_tvNavigator = (TextView) view
				.findViewById(R.id.view_ms_tvNavigator);
		// 调起第三方地图app
		view_ms_tvNavigator.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startURI(marker);
			}
		});
		return view;
	}

}

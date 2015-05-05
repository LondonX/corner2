package com.ljmob.corner.amap;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.OnNavigationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.ljmob.corner.R;
import com.ljmob.corner.map.util.BluePoint;
import com.ljmob.corner.map.util.InfoWindow;
import com.ljmob.corner.map.util.MixtureLocatioin;
import com.ljmob.corner.map.util.PoiSearchUtil;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.ToastUtil;

public class GaodeMapFragment extends Fragment implements
		OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener,
		OnMapLoadedListener, InfoWindowAdapter, OnCameraChangeListener,
		OnNavigationListener, OnMapClickListener {

	public MapView mapView;
	public AMap aMap;
	public AMapLocation location;
	public PoiSearchUtil poiSearchUtil;
	private BluePoint bluePoint;
	public MixtureLocatioin mixtureLocatioin;
	private MyApplication application;

	public ArrayAdapter<String> aAdapter;

	public List<LatLng> latLngs = new ArrayList<LatLng>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("GaodeMapFragment", "onActivityCreated");
		mapView = (MapView) getView().findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		application = (MyApplication) getActivity().getApplication();
		init();
		setUpMap();
		bluePoint = new BluePoint(getActivity(), aMap);
		mixtureLocatioin = application.mixtureLocatioin;
		addMarkersToMap();
	}

	private void initLatLng() {
		latLngs.add(new LatLng(31.05, 121.76));// 南汇
		latLngs.add(new LatLng(31.19, 121.70));// 川沙
		latLngs.add(new LatLng(31.41, 121.48));// 宝山
		latLngs.add(new LatLng(31.22, 121.48));// 上海
		latLngs.add(new LatLng(30.92, 121.46));// 奉贤
		latLngs.add(new LatLng(31.73, 121.40));// 崇明
		latLngs.add(new LatLng(31.00, 121.24));// 松江
		latLngs.add(new LatLng(31.40, 121.24));// 嘉定
		latLngs.add(new LatLng(30.89, 121.16));// 金山
		latLngs.add(new LatLng(31.15, 121.10));// 青浦
	}

	private void addMarkersToMap() {
		initLatLng();
		int i = 0;
		for (LatLng latLng : latLngs) {
			i++;
			MarkerOptions options = new MarkerOptions();
			options.position(latLng);
			options.title("上海" + i).snippet("地址：" + i);
			options.draggable(true);
			options.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.baobao));
			aMap.addMarker(options);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// return super.onCreateView(inflater, container, savedInstanceState);
		Log.i("GaodeMapFragment", "onCreateView");
		return inflater.inflate(R.layout.gaode_map_layout, container, false);
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		// if (aMap == null) {
		// aMap = ((SupportMapFragment) getSupportFragmentManager()
		// .findFragmentById(R.id.map)).getMap();
		// }
		if (aMap == null) {
			aMap = mapView.getMap();
		}
	}

	private void setUpMap() {
		aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		aMap.setOnMapClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		// mAMapLocManager.destroy();
		mixtureLocatioin.stopLocation();
		Log.i("GaodeMapFragment", "onDestroy");
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		Log.i("GaodeMapFragment", "onPause");

	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		Log.i("GaodeMapFragment", "onResume");

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
		Log.i("GaodeMapFragment", "onSaveInstanceState");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i("GaodeMapFragment", "onStart");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("GaodeMapFragment", "onStop");
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
		return new InfoWindow(getActivity(), arg0).getView();
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
		return false;
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

}

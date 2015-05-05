package com.ljmob.corner.map.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.ljmob.corner.R;
import com.ljmob.corner.util.ToastUtil;

public class MixtureLocatioin implements AMapLocationListener, Runnable {
	private LocationManagerProxy aMapLocManager = null;
	// private TextView myLocation;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();
	private Context context;
	private ProgressDialog progDialog;
	private double geoLat;// 纬度
	private double geoLng;// 进度
	private String cityCode;// 城市编码
	private String desc;// 具体位置描述
	private String accuracy;// 精度
	private String provider;// 定位方式
	private String time;// 定位时间
	private String province;// 省
	private String city;// 市
	private String district;// (区)县
	private String adCode;// 区域编码

	public MixtureLocatioin(Context context) {
		this.context = context;
		// aMapLocManager = LocationManagerProxy.getInstance(context);
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		requestLocationListener();
		// aMapLocManager.requestLocationUpdates(
		// LocationProviderProxy.AMapNetwork, 2000, 10, this);
		// handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		// showProgressDialog();
	}

	/**
	 * 请求位置更新监听
	 */
	public void requestLocationListener() {
		aMapLocManager = LocationManagerProxy.getInstance(context);
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
	}

	public void removeListener20Back() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				stopLocation();
			}
		}, 20000);
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(context);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在定位...");
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
	 * 销毁定位
	 */
	public void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			// dissmissProgressDialog();
			ToastUtil.show(context, R.string.toast_error_location);
			// myLocation.setText("12秒内还没有定位成功，停止定位");
			stopLocation();// 销毁掉定位
		}
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			// dissmissProgressDialog();
			this.aMapLocation = location;// 判断超时机制
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
					+ "\n精    度    :" + location.getAccuracy() + "米"
					+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
					+ AMapUtil.convertToTime(location.getTime()) + "\n城市编码:"
					+ cityCode + "\n位置描述:" + desc + "\n省:"
					+ location.getProvince() + "\n市:" + location.getCity()
					+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
					.getAdCode());
			setGeoLat(geoLat);
			setGeoLng(geoLng);
			setCityCode(cityCode);
			setDesc(desc);
			setAccuracy(location.getAccuracy() + "米");
			setProvider(location.getProvider());
			setTime(AMapUtil.convertToTime(location.getTime()));
			setProvince(location.getProvince());
			setCity(location.getCity());
			setDistrict(location.getDistrict());
			setAdCode(location.getAdCode());
			// Log.i("Location", "定位成功：" + "longitude=" + getGeoLng()
			// + " ,latitude=" + getGeoLat());
			// ((MyApplication)
			// context.getApplicationContext()).mixtureLocatioin = this;
			// myLocation.setText(str);
		}
	}

	/**
	 * 取得纬度
	 * 
	 * @return
	 */
	public double getGeoLat() {
		return geoLat;
	}

	public void setGeoLat(double geoLat) {
		this.geoLat = geoLat;
	}

	/**
	 * 取得经度
	 * 
	 * @return
	 */
	public double getGeoLng() {
		return geoLng;
	}

	public void setGeoLng(double geoLng) {
		this.geoLng = geoLng;
	}

	/**
	 * 城市编码
	 * 
	 * @return
	 */
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	/**
	 * 具体位置描述
	 * 
	 * @return
	 */
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 精度
	 * 
	 * @return
	 */
	public String getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * 定位方式
	 * 
	 * @return
	 */
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * 定位时间
	 * 
	 * @return
	 */
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * 省
	 * 
	 * @return
	 */
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * 市
	 * 
	 * @return
	 */
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * (区)县
	 * 
	 * @return
	 */
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	/**
	 * 区域编码
	 * 
	 * @return
	 */
	public String getAdCode() {
		return adCode;
	}

	public void setAdCode(String adCode) {
		this.adCode = adCode;
	}

}

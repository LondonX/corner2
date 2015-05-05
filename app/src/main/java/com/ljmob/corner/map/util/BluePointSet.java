package com.ljmob.corner.map.util;

import android.graphics.Color;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.ljmob.corner.R;

public class BluePointSet {
	public static void setBluePoint(AMap aMap) {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		// myLocationStyle.myLocationIcon(BitmapDescriptorFactory
		// .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_mark));// 设置小蓝点的图标
//		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.strokeColor(Color.argb(100, 255, 102, 102));// 设置圆形的边框颜色
		// myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));//
		// 设置圆形的填充颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 255, 102, 102));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
	}
}

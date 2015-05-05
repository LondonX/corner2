package com.ljmob.corner.map.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.model.Marker;
import com.ljmob.corner.R;

public class InfoWindow {
	private Context context;
	private View infoWindowView;
	private TextView view_ms_tvName;
	private TextView view_ms_tvContent;
	private TextView view_ms_tvNavigator;
	private Marker marker;

	public InfoWindow(Context context, Marker marker) {
		this.context = context;
		this.marker = marker;
		initViews();
		setInfo();
	}

	private void initViews() {
		infoWindowView = LayoutInflater.from(context).inflate(
				R.layout.view_map_stamp, null);
		view_ms_tvName = (TextView) infoWindowView
				.findViewById(R.id.view_ms_tvName);
		view_ms_tvContent = (TextView) infoWindowView
				.findViewById(R.id.view_ms_tvContent);
		view_ms_tvNavigator = (TextView) infoWindowView
				.findViewById(R.id.view_ms_tvNavigator);
	}

	private void setInfo() {
		if (marker != null) {
			view_ms_tvName.setText(marker.getTitle());
			view_ms_tvContent.setText(marker.getSnippet());
		}
	}

	public View getView() {
		return infoWindowView;
	}
}

package com.ljmob.corner.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.ljmob.corner.R;

public class MarkerTool {
	private static Map<String, Integer> map = getIconMap();

	public static int getMarkerIcon(String name) {
		Log.i("YANGBANG", "Name-->" + name);
		int icon = 0;
		for (String key : map.keySet()) {
			if (key.equals(name)) {
				return map.get(key);
			}
		}
		return icon;
	}

	private static Map<String, Integer> getIconMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("包包", R.drawable.baobao);
		map.put("护肤品", R.drawable.hufupin);
		map.put("服装", R.drawable.fuzhuang);
		map.put("鞋子", R.drawable.xiezi);
		map.put("手表", R.drawable.shoubiao);
		map.put("数码", R.drawable.shumachanpin);
		map.put("珠宝", R.drawable.zhubaoshoushi);
		map.put("综合", R.drawable.zongheshichang);
		return map;
	}
}

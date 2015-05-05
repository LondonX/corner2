package com.ljmob.corner.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MainPagerAdapter extends PagerAdapter {
	private List<View> views;
	private String[] indicators;

	public MainPagerAdapter(List<View> views) {
		this(views, new String[] {});
	}

	public MainPagerAdapter(List<View> views, String[] indicators) {
		super();
		this.views = views;
		this.indicators = indicators;
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if (indicators.length == 0) {
			return "";
		}
		return indicators[position];
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View retView = views.get(position);
		container.addView(retView);
		return retView;
	}
}

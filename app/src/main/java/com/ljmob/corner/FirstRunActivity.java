package com.ljmob.corner;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.corner.adapter.MainPagerAdapter;
import com.ljmob.corner.util.MyApplication;

public class FirstRunActivity extends Activity implements OnPageChangeListener,
		OnTouchListener {
	private MainPagerAdapter adapter;
	private ViewPager activity_firstrun_pager;
	private View activity_firstrun_viewIndicator0;
	private View activity_firstrun_viewIndicator1;
	private View activity_firstrun_viewIndicator2;
	private List<ImageView> imgViews;
	private List<View> views;

	private int lastValue = 0;
	private int currentIndex = 0;
	private float lastPointX = -1;

	private MyApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (MyApplication) getApplication();
		boolean isFirstRun = application.preferences.getBoolean("isFirstRun",
				true);
		if (isFirstRun == false) {
			startActivity(new Intent(this, MainActivity.class));
			this.finish();
			return;
		}
		setContentView(R.layout.activity_firstrun);
		initView();
		views = new ArrayList<View>();
		imgViews = new ArrayList<ImageView>();
		for (int pageIndex = 0; pageIndex < 3; pageIndex++) {
			View page = getLayoutInflater().inflate(R.layout.page_firstrun,
					null);
			TextView tv = (TextView) page.findViewById(R.id.page_firstrun_tv);
			// Typeface tf = Typeface.createFromAsset(getAssets(),
			// "font1.ttf");// 根据路径得到Typeface
			// tv.setTypeface(tf);// 设置字体
			switch (pageIndex) {
			case 0:
				imgViews.add((ImageView) findViewById(R.id.activity_firstrun_img0));
				imgViews.get(pageIndex).setImageResource(
						R.drawable.img_welcome_0);
				tv.setText(R.string.welcome_0);
				break;
			case 1:
				imgViews.add((ImageView) findViewById(R.id.activity_firstrun_img1));
				imgViews.get(pageIndex).setImageResource(
						R.drawable.img_welcome_1);
				imgViews.get(pageIndex).setAlpha(0f);
				tv.setText(R.string.welcome_1);
				break;
			case 2:
				imgViews.add((ImageView) findViewById(R.id.activity_firstrun_img2));
				imgViews.get(pageIndex).setImageResource(
						R.drawable.img_welcome_2);
				imgViews.get(pageIndex).setAlpha(0f);
				tv.setText(R.string.welcome_2);
				break;
			default:
				break;
			}
			views.add(page);
		}
		adapter = new MainPagerAdapter(views);
		activity_firstrun_pager.setAdapter(adapter);
		activity_firstrun_pager.setOnPageChangeListener(this);
		activity_firstrun_pager.setOnTouchListener(this);
		selectPage(0);
	}

	@Override
	protected void onDestroy() {
		Editor editor = application.preferences.edit();
		editor.putBoolean("isFirstRun", false);
		editor.apply();
		super.onDestroy();
	}

	private void initView() {
		activity_firstrun_pager = (ViewPager) findViewById(R.id.activity_firstrun_pager);
		activity_firstrun_viewIndicator0 = findViewById(R.id.activity_firstrun_viewIndicator0);
		activity_firstrun_viewIndicator1 = findViewById(R.id.activity_firstrun_viewIndicator1);
		activity_firstrun_viewIndicator2 = findViewById(R.id.activity_firstrun_viewIndicator2);
	}

	private void selectPage(int index) {
		switch (index) {
		case 0:
			activity_firstrun_viewIndicator0.setEnabled(false);
			activity_firstrun_viewIndicator1.setEnabled(true);
			activity_firstrun_viewIndicator2.setEnabled(true);
			break;
		case 1:
			activity_firstrun_viewIndicator0.setEnabled(true);
			activity_firstrun_viewIndicator1.setEnabled(false);
			activity_firstrun_viewIndicator2.setEnabled(true);
			break;
		case 2:
			activity_firstrun_viewIndicator0.setEnabled(true);
			activity_firstrun_viewIndicator1.setEnabled(true);
			activity_firstrun_viewIndicator2.setEnabled(false);
			break;

		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
			currentIndex = activity_firstrun_pager.getCurrentItem();
			imgViews.get(currentIndex).setAlpha(1.0f);
			lastValue = 0;
			int nextIndex = currentIndex + 1;
			if (nextIndex < views.size()) {
				imgViews.get(nextIndex).setAlpha(0.0f);
			}
		}
	}

	@Override
	public void onPageScrolled(int arg0, float offSetPersent, int offSetPixel) {
		if (offSetPersent == 0 || offSetPersent == 1) {
			currentIndex = activity_firstrun_pager.getCurrentItem();
			Log.i("LondonX", "currentIndex:" + currentIndex);
			return;
		}
		if (lastValue == 0) {
			lastValue = offSetPixel;
			return;
		}
		boolean isR2L = lastValue - offSetPixel < 0;
		lastValue = offSetPixel;
		ImageView curImg = imgViews.get(currentIndex);
		ImageView frontImg = null;
		try {
			frontImg = imgViews.get(currentIndex + 1);
		} catch (IndexOutOfBoundsException e) {
		}
		if (curImg.getAlpha() != 1) {
			curImg.setAlpha(offSetPersent);
			Log.i("LondonX", "curImg img" + (currentIndex) + ".alpha:"
					+ offSetPersent);
		} else if (frontImg != null && frontImg.getAlpha() != 0) {
			frontImg.setAlpha(offSetPersent);
			Log.i("LondonX", "frontImg img" + (currentIndex + 1) + ".alpha:"
					+ offSetPersent);
		} else if (isR2L) {// 向左滑动
			if (frontImg != null) {
				frontImg.setAlpha(offSetPersent);
				Log.i("LondonX", "frontImg img" + (currentIndex + 1)
						+ ".alpha:" + offSetPersent);
			}
		} else {// 向右滑动
			curImg.setAlpha(offSetPersent);
			Log.i("LondonX", "curImg img" + (currentIndex) + ".alpha:"
					+ offSetPersent);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		lastValue = 0;
		int previousIndex = arg0 - 1;
		imgViews.get(previousIndex < 0 ? 0 : previousIndex).setAlpha(1.0f);
		selectPage(arg0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (currentIndex == views.size() - 1) {
			if (lastPointX == -1) {
				lastPointX = event.getX(0);
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (event.getX(0) < lastPointX) {
					startActivity(new Intent(this, MainActivity.class));
					this.finish();
				}
				lastPointX = -1;
			}
		}
		return false;
	}
}

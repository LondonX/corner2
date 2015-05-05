package com.ljmob.corner.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;

public class SortPopupWindow extends PopupWindow {
	private Context context;
	private ListView listView;

	public SortPopupWindow(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
		initPopWindow();
	}

	private void initPopWindow() {
		ColorDrawable cd = new ColorDrawable(0xFFFFFFFF);
		this.setBackgroundDrawable(cd);
		this.setOutsideTouchable(true);
		this.setFocusable(true);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setWidth((context.getResources().getDisplayMetrics().widthPixels) / 2);
		this.setContentView(listView);
	}

}

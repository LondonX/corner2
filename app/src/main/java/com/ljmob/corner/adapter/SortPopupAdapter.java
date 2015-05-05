package com.ljmob.corner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.entity.KeyWord;
import com.ljmob.corner.entity.Poi;
import com.ljmob.corner.util.ToastUtil;

public class SortPopupAdapter extends BaseAdapter {
	private Context context;
	private List<Poi> poiList = new ArrayList<Poi>();
	private int tag = 0;
	private int index = 0;
	private boolean isShowRightArrows = true;

	public SortPopupAdapter(Context context, List<Poi> poiList, int tag,
			int index, boolean isShowRightArrows) {
		this.context = context;
		this.poiList = poiList;
		this.tag = tag;
		this.index = index;
		this.isShowRightArrows = isShowRightArrows;
		if (poiList.size() == 0) {
			ToastUtil.show(context, R.string.toast_error_searchshop);
		}
	}

	@Override
	public int getCount() {
		if (tag == 0) {
			return poiList.size();
		}
		return poiList.get(index).item.size();
	}

	@Override
	public Object getItem(int position) {
		if (tag == 0) {
			return poiList.get(position);
		}
		return poiList.get(index).item.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (tag == 0) {
			return poiList.get(position).id;
		}
		return poiList.get(index).item.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_popmenu, null);
			holder.listitem_pop_tv = (TextView) convertView
					.findViewById(R.id.listitem_pop_tv);
			holder.listitem_pop_imgArrow = (ImageView) convertView
					.findViewById(R.id.listitem_pop_imgArrow);
			holder.listitem_pop_imgArrowLeft = (ImageView) convertView
					.findViewById(R.id.listitem_pop_imgArrowLeft);
			if (isShowRightArrows) {
				holder.listitem_pop_imgArrow.setVisibility(View.VISIBLE);
				holder.listitem_pop_imgArrowLeft.setVisibility(View.GONE);
			} else {
				holder.listitem_pop_imgArrow.setVisibility(View.GONE);
				holder.listitem_pop_imgArrowLeft.setVisibility(View.VISIBLE);
			}
			if (tag == 1) {
				holder.listitem_pop_imgArrow.setVisibility(View.GONE);
				holder.listitem_pop_imgArrowLeft.setVisibility(View.GONE);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (tag == 0) {
			Poi poi = poiList.get(position);
			holder.listitem_pop_tv.setText(poi.type);
		} else {
			KeyWord keyWord = poiList.get(index).item.get(position);
			holder.listitem_pop_tv.setText(keyWord.keyword);
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView listitem_pop_tv;// name
		private ImageView listitem_pop_imgArrow;// 右箭头
		private ImageView listitem_pop_imgArrowLeft;// 左箭头
	}

}

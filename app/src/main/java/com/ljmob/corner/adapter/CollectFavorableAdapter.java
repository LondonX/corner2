package com.ljmob.corner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.entity.CollectFavorable;

/**
 * 优惠收藏适配器
 * 
 * @author YANGBANG
 * 
 */
public class CollectFavorableAdapter extends BaseAdapter {
	private Context context;
	private List<CollectFavorable> collectFavorableList = new ArrayList<CollectFavorable>();

	public CollectFavorableAdapter(Context context,
			List<CollectFavorable> collectFavorableList) {
		this.context = context;
		if (collectFavorableList != null) {
			this.collectFavorableList = collectFavorableList;
		}
	}

	@Override
	public int getCount() {
		return collectFavorableList.size();
	}

	@Override
	public Object getItem(int position) {
		return collectFavorableList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return collectFavorableList.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_favorable, null);
			holder.linearitem_fav_tvType = (TextView) convertView
					.findViewById(R.id.linearitem_fav_tvType);
			holder.linearitem_fav_tvContent = (TextView) convertView
					.findViewById(R.id.linearitem_fav_tvContent);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CollectFavorable collectFavorable = collectFavorableList.get(position);
		holder.linearitem_fav_tvContent.setText(collectFavorable.subject);
		holder.linearitem_fav_tvType.setText(collectFavorable.discount);
		return convertView;
	}

	private class ViewHolder {
		private TextView linearitem_fav_tvType;// 类型
		private TextView linearitem_fav_tvContent;// 内容
	}

}

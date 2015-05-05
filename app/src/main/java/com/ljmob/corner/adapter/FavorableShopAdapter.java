package com.ljmob.corner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.FavorableShop;
import com.ljmob.corner.util.UrlStaticUtil;

public class FavorableShopAdapter extends BaseAdapter {
	private Context context;
	private List<FavorableShop> favorables = new ArrayList<FavorableShop>();
	private ArrayList<Boolean> isAnimFlags;
	private ImageLoader imageLoader;

	public FavorableShopAdapter(Context context, List<FavorableShop> favorables) {
		super();
		this.context = context;
		if (favorables != null) {
			this.favorables = favorables;
		}
		imageLoader = new ImageLoader(context);
		isAnimFlags = new ArrayList<Boolean>();
		for (int i = 0; i < this.favorables.size(); i++) {
			isAnimFlags.add(false);
		}
	}

	@Override
	public int getCount() {
		return favorables.size();
	}

	@Override
	public Object getItem(int position) {
		return favorables.get(position);
	}

	@Override
	public long getItemId(int position) {
		return favorables.get(position).id;
	}

	/**
	 * 加载数据
	 * 
	 * @param tempList
	 *            数据列表
	 * @param type
	 *            0代表下拉刷新、1代表上拉加载
	 */
	public void loadData(List<FavorableShop> tempList, final int type) {
		if (tempList == null) {
			return;
		}
		int count = tempList.size();
		if (type == 0) {
			favorables = tempList;
			if (!isAnimFlags.isEmpty()) {
				isAnimFlags.clear();
			}
			for (int i = 0; i < count; i++) {
				isAnimFlags.add(i < 2);
			}
		} else {
			favorables.addAll(tempList);
			for (int i = 0; i < count; i++) {
				isAnimFlags.add(false);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_favorableshop, null);
			holder = new Holder();
			holder.listitem_fs_img = (ImageView) convertView
					.findViewById(R.id.listitem_fs_img);
			holder.listitem_fs_tvDiscount = (TextView) convertView
					.findViewById(R.id.listitem_fs_tvDiscount);
			holder.listitem_fs_tvTitle = (TextView) convertView
					.findViewById(R.id.listitem_fs_tvTitle);
			holder.listitem_fs_tvDetail = (TextView) convertView
					.findViewById(R.id.listitem_fs_tvDetail);
			convertView.setTag(holder);
		}
		holder = (Holder) convertView.getTag();
		FavorableShop favorable = favorables.get(position);
		imageLoader.DisplayImage(UrlStaticUtil.root_photo + favorable.photo,
				holder.listitem_fs_img, false,ImageLoader.L_PICTURE);
		if (favorable.discount == null || favorable.discount.equals("")) {
			holder.listitem_fs_tvDiscount.setVisibility(View.GONE);
		} else {
			holder.listitem_fs_tvDiscount.setVisibility(View.VISIBLE);
			holder.listitem_fs_tvDiscount.setText(favorable.discount);
		}
		holder.listitem_fs_tvTitle.setText(favorable.subject);
		holder.listitem_fs_tvDetail.setText(Html
				.fromHtml(favorable.description));
		// holder.listitem_fs_tvDiscount.setText("6折");
		// holder.listitem_fs_tvTitle.setText("Blanche Wallet");
		// holder.listitem_fs_tvDetail
		// .setText("2014新款简约时尚裸色真皮牛皮女式装卸式提把手提包。原价：368元 现价220元");
		if (isAnimFlags.get(position) == false) {
			convertView.startAnimation(AnimationUtils.loadAnimation(context,
					R.anim.item_in));
			isAnimFlags.set(position, true);
		}
		return convertView;
	}

	private class Holder {
		public ImageView listitem_fs_img;
		public TextView listitem_fs_tvDiscount;
		public TextView listitem_fs_tvTitle;
		public TextView listitem_fs_tvDetail;
	}
}

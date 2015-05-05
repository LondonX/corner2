package com.ljmob.corner.adapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Shop;
import com.ljmob.corner.util.UrlStaticUtil;

public class MerchantAdapter extends BaseAdapter {
	private Context context;
	private List<Shop> shopList = new ArrayList<Shop>();
	private ImageLoader imageLoader;

	public MerchantAdapter(Context context, List<Shop> shopList) {
		this.context = context;
		if (shopList != null) {
			this.shopList = shopList;
		}
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return shopList.size();
	}

	@Override
	public Object getItem(int position) {
		return shopList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return shopList.get(position).id;
	}

	public void LoadData(List<Shop> tempList, boolean isLoad) {
		if (isLoad) {
			this.shopList.addAll(tempList);
		} else {
			if (tempList != null) {
				this.shopList = tempList;
			}
		}
		notifyDataSetChanged();
	}

	public List<Shop> getShopList() {
		return shopList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_shop_simple, null);
			hold.listitem_shop_simple_img = (ImageView) convertView
					.findViewById(R.id.listitem_shop_simple_img);
			hold.listitem_shop_simple_tvName = (TextView) convertView
					.findViewById(R.id.listitem_shop_simple_tvName);
			hold.listitem_shop_simple_tvFlag = (TextView) convertView
					.findViewById(R.id.listitem_shop_simple_tvFlag);
			hold.listitem_shop_simple_tvDesc = (TextView) convertView
					.findViewById(R.id.listitem_shop_simple_tvDesc);
			hold.listitem_shop_simple_tvTag0 = (TextView) convertView
					.findViewById(R.id.listitem_shop_simple_tvTag0);
			hold.listitem_shop_simple_tvTag1 = (TextView) convertView
					.findViewById(R.id.listitem_shop_simple_tvTag1);
			hold.listitem_shop_simple_tvDistance = (TextView) convertView
					.findViewById(R.id.listitem_shop_simple_tvDistance);
			hold.listitem_shop_simple_tvAvg = (TextView) convertView
					.findViewById(R.id.listitem_shop_simple_tvAvg);
			hold.listitem_shop_simple_rbRate = (RatingBar) convertView
					.findViewById(R.id.listitem_shop_simple_rbRate);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		Shop shop = shopList.get(position);
		imageLoader.DisplayImage(UrlStaticUtil.root_photo + shop.logo_url,
				hold.listitem_shop_simple_img, false,ImageLoader.L_PICTURE);
		hold.listitem_shop_simple_tvName.setText(shop.name);
		hold.listitem_shop_simple_tvDesc.setText(shop.descption);
		hold.listitem_shop_simple_tvTag0.setText(shop.district);
		hold.listitem_shop_simple_tvTag1.setText(shop.belong_kinds);
		if (shop.activity_type == null || shop.activity_type.equals("")) {
			hold.listitem_shop_simple_tvFlag.setVisibility(View.GONE);
		} else {
			hold.listitem_shop_simple_tvFlag.setVisibility(View.VISIBLE);
			hold.listitem_shop_simple_tvFlag.setText(shop.activity_type);
		}
		if (shop.distance > 1000) {
			hold.listitem_shop_simple_tvDistance.setVisibility(View.VISIBLE);
			BigDecimal b = new BigDecimal(Float.valueOf(shop.distance) / 1000);
			float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			hold.listitem_shop_simple_tvDistance.setText(f1 + "km");
		} else if (shop.distance > 0) {
			hold.listitem_shop_simple_tvDistance.setVisibility(View.VISIBLE);
			hold.listitem_shop_simple_tvDistance.setText(shop.distance + "m");
		} else {
			hold.listitem_shop_simple_tvDistance.setVisibility(View.INVISIBLE);
		}
		hold.listitem_shop_simple_tvAvg.setText(shop.money + "HKD/人");
		hold.listitem_shop_simple_rbRate.setRating(shop.score);
		return convertView;
	}

	private class ViewHold {
		private ImageView listitem_shop_simple_img;// 图片
		private TextView listitem_shop_simple_tvName;// 名字、标题
		private TextView listitem_shop_simple_tvFlag;// 优惠文字
		private TextView listitem_shop_simple_tvDesc;// 描述
		private TextView listitem_shop_simple_tvTag0;// 标签0
		private TextView listitem_shop_simple_tvTag1;// 标签1
		private TextView listitem_shop_simple_tvDistance;// 距离
		private TextView listitem_shop_simple_tvAvg;// 评价消费
		private RatingBar listitem_shop_simple_rbRate;// 评分
	}

}

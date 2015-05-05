package com.ljmob.corner.adapter;

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

public class FitShopAdapter extends BaseAdapter {
	private Context context;
	private List<Shop> shopList = new ArrayList<Shop>();
	private ImageLoader imageLoader;

	public FitShopAdapter(Context context, List<Shop> shopList) {
		this.context = context;
		this.shopList = shopList;
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

	/**
	 * 加载数据
	 * 
	 * @param tempList
	 *            新数据列表
	 */
	public void loadDada(List<Shop> tempList) {
		this.shopList.addAll(tempList);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_fitshop, null);
			hold.linearitem_fitshop_img = (ImageView) convertView
					.findViewById(R.id.linearitem_fitshop_img);
			hold.linearitem_fitshop_tvName = (TextView) convertView
					.findViewById(R.id.linearitem_fitshop_tvName);
			hold.activity_shop_rbRate = (RatingBar) convertView
					.findViewById(R.id.activity_shop_rbRate);
			hold.linearitem_fitshop_tvLocation = (TextView) convertView
					.findViewById(R.id.linearitem_fitshop_tvLocation);
			hold.linearitem_fitshop_tvPhone = (TextView) convertView
					.findViewById(R.id.linearitem_fitshop_tvPhone);
			hold.linearitem_fitshop_tvDistance = (TextView) convertView
					.findViewById(R.id.linearitem_fitshop_tvDistance);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		Shop shop = shopList.get(position);
		imageLoader.DisplayImage(UrlStaticUtil.root_photo + shop.logo_url,
				hold.linearitem_fitshop_img, false,ImageLoader.L_PICTURE);
		hold.linearitem_fitshop_tvName.setText(shop.name);
		hold.activity_shop_rbRate.setRating(shop.score);
		hold.linearitem_fitshop_tvLocation.setText("地址：" + shop.address);
		hold.linearitem_fitshop_tvPhone.setText("电话：" + shop.telephone);
		return convertView;
	}

	private class ViewHold {
		private ImageView linearitem_fitshop_img;// 图片
		private TextView linearitem_fitshop_tvName;// 名字
		private RatingBar activity_shop_rbRate;// 等级条
		private TextView linearitem_fitshop_tvLocation;// 位置
		private TextView linearitem_fitshop_tvPhone;// 电话
		private TextView linearitem_fitshop_tvDistance;// 距离
	}
}

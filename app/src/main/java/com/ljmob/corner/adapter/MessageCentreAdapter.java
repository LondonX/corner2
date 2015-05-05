package com.ljmob.corner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Favorable;
import com.ljmob.corner.util.UrlStaticUtil;

public class MessageCentreAdapter extends BaseAdapter {
	private Context context;
	private List<Favorable> favorableList = new ArrayList<Favorable>();
	private ImageLoader imageLoader;

	public MessageCentreAdapter(Context context, List<Favorable> favorableList) {
		this.context = context;
		this.imageLoader = new ImageLoader(context);
		if (favorableList != null) {
			this.favorableList = favorableList;
		}
	}

	@Override
	public int getCount() {
		return favorableList.size();
	}

	@Override
	public Object getItem(int position) {
		return favorableList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return favorableList.get(position).coupon_id;
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
			hold.listitem_shop_simple_linear = (LinearLayout) convertView
					.findViewById(R.id.listitem_shop_simple_linear);
			hold.listitem_shop_simple_linear.setVisibility(View.GONE);
			hold.listitem_shop_simple_tvDesc.setVisibility(View.VISIBLE);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		Favorable favorable = favorableList.get(position);
		imageLoader.DisplayImage(UrlStaticUtil.root_photo + favorable.logo_url,
				hold.listitem_shop_simple_img, false,ImageLoader.L_PICTURE);
		hold.listitem_shop_simple_tvName.setText(favorable.name);
		hold.listitem_shop_simple_tvDesc.setText(favorable.subject);
		hold.listitem_shop_simple_tvTag0.setText(favorable.belong_area);
		hold.listitem_shop_simple_tvTag1.setText(favorable.belong_kinds);
		if (favorable.activity_type != null
				&& !favorable.activity_type.equals("")) {
			hold.listitem_shop_simple_tvFlag.setVisibility(View.VISIBLE);
			hold.listitem_shop_simple_tvFlag.setText(favorable.activity_type);
		} else {
			hold.listitem_shop_simple_tvFlag.setVisibility(View.GONE);
		}
		hold.listitem_shop_simple_tvDistance.setText(favorable.end_date);
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
		private LinearLayout listitem_shop_simple_linear;//
	}

}

package com.ljmob.corner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.ShopComment;
import com.ljmob.corner.util.UrlStaticUtil;

public class ShopCommentAdapter extends BaseAdapter {
	private Context context;
	private List<ShopComment> shopCommentList = new ArrayList<ShopComment>();
	private ImageLoader imageLoader;

	public ShopCommentAdapter(Context context, List<ShopComment> shopCommentList) {
		this.context = context;
		this.shopCommentList = shopCommentList;
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return shopCommentList.size();
	}

	@Override
	public Object getItem(int position) {
		return shopCommentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return shopCommentList.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_othercomments, null);
			hold.linearitem_oc_imgHead = (ImageView) convertView
					.findViewById(R.id.linearitem_oc_imgHead);
			hold.linearitem_oc_tvName = (TextView) convertView
					.findViewById(R.id.linearitem_oc_tvName);
			hold.linearitem_oc_rbMyRate = (RatingBar) convertView
					.findViewById(R.id.linearitem_oc_rbMyRate);
			hold.linearitem_oc_tvFloor = (TextView) convertView
					.findViewById(R.id.linearitem_oc_tvFloor);
			hold.linearitem_oc_etComment = (EditText) convertView
					.findViewById(R.id.linearitem_oc_etComment);
			hold.linearitem_oc_imgPic0 = (ImageView) convertView
					.findViewById(R.id.linearitem_oc_imgPic0);
			hold.linearitem_oc_imgPic1 = (ImageView) convertView
					.findViewById(R.id.linearitem_oc_imgPic1);
			hold.linearitem_oc_imgPic2 = (ImageView) convertView
					.findViewById(R.id.linearitem_oc_imgPic2);
			hold.linearitem_oc_tvDate = (TextView) convertView
					.findViewById(R.id.linearitem_oc_tvDate);
			hold.linearitem_oc_linearPic = (LinearLayout) convertView
					.findViewById(R.id.linearitem_oc_linearPic);
			hold.linearitem_oc_linearPic.setVisibility(View.GONE);
			hold.linearitem_oc_imgPic0.setVisibility(View.GONE);
			hold.linearitem_oc_imgPic1.setVisibility(View.GONE);
			hold.linearitem_oc_imgPic2.setVisibility(View.GONE);
			hold.linearitem_oc_etComment.setEnabled(false);
			hold.linearitem_oc_tvFloor.setVisibility(View.GONE);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		ShopComment shopComment = shopCommentList.get(position);
		imageLoader.DisplayImage(UrlStaticUtil.root_photo
				+ shopComment.user_avatar, hold.linearitem_oc_imgHead, true,
				ImageLoader.L_PICTURE);
		hold.linearitem_oc_tvName.setText(shopComment.user_nickname);
		// hold.linearitem_oc_tvFloor.setText(shopComment.row + "楼");
		hold.linearitem_oc_etComment.setText(shopComment.content);
		hold.linearitem_oc_rbMyRate.setRating(shopComment.score);
		hold.linearitem_oc_tvDate.setText(shopComment.comment_time);
		for (int i = 0; i < shopComment.photos.size(); i++) {
			hold.linearitem_oc_linearPic.setVisibility(View.VISIBLE);
			if (i == 0) {
				hold.linearitem_oc_imgPic0.setVisibility(View.VISIBLE);
				imageLoader.DisplayImage(shopComment.photos.get(i).img_url,
						hold.linearitem_oc_imgPic0, false,
						ImageLoader.L_PICTURE);
			} else if (i == 1) {
				hold.linearitem_oc_imgPic1.setVisibility(View.VISIBLE);
				imageLoader.DisplayImage(shopComment.photos.get(i).img_url,
						hold.linearitem_oc_imgPic1, false,
						ImageLoader.L_PICTURE);
			} else if (i == 2) {
				hold.linearitem_oc_imgPic2.setVisibility(View.VISIBLE);
				imageLoader.DisplayImage(shopComment.photos.get(i).img_url,
						hold.linearitem_oc_imgPic2, false,
						ImageLoader.L_PICTURE);
			}
		}
		return convertView;
	}

	private class ViewHold {
		private ImageView linearitem_oc_imgHead;// 头像
		private TextView linearitem_oc_tvName;
		private RatingBar linearitem_oc_rbMyRate;// 等级条
		private TextView linearitem_oc_tvFloor;// 楼数
		private EditText linearitem_oc_etComment;// 评论内容
		private ImageView linearitem_oc_imgPic0;// 图片
		private ImageView linearitem_oc_imgPic1;
		private ImageView linearitem_oc_imgPic2;
		private TextView linearitem_oc_tvDate;// 评论时间
		private LinearLayout linearitem_oc_linearPic;// 图片linear
	}

}

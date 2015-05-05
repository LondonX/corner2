package com.ljmob.corner.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.ShopComment;
import com.ljmob.corner.util.UrlStaticUtil;

public class ShopCommentView {
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

	private View view;
	private Context context;
	private ImageLoader imageLoader;
	private ShopComment shopComment;

	public ShopCommentView(Context context, ShopComment shopComment) {
		this.context = context;
		this.shopComment = shopComment;
		this.imageLoader = new ImageLoader(context);
		initView();
		setData();
	}

	private void setData() {
		imageLoader.DisplayImage(UrlStaticUtil.root_photo
				+ shopComment.user_avatar, linearitem_oc_imgHead, true,
				ImageLoader.L_PICTURE);
		linearitem_oc_tvName.setText(shopComment.user_nickname);
		// linearitem_oc_tvFloor.setText(shopComment.row + "楼");
		linearitem_oc_etComment.setText(shopComment.content);
		linearitem_oc_rbMyRate.setRating(shopComment.score);
		linearitem_oc_tvDate.setText(shopComment.comment_time);
		for (int i = 0; i < shopComment.photos.size(); i++) {
			this.linearitem_oc_linearPic.setVisibility(View.VISIBLE);
			if (i == 0) {
				this.linearitem_oc_imgPic0.setVisibility(View.VISIBLE);
				imageLoader.DisplayImage(UrlStaticUtil.root_photo
						+ shopComment.photos.get(i).img_url,
						linearitem_oc_imgPic0, false, ImageLoader.L_PICTURE);
			} else if (i == 1) {
				this.linearitem_oc_imgPic1.setVisibility(View.VISIBLE);
				imageLoader.DisplayImage(UrlStaticUtil.root_photo
						+ shopComment.photos.get(i).img_url,
						linearitem_oc_imgPic1, false, ImageLoader.L_PICTURE);
			} else if (i == 2) {
				this.linearitem_oc_imgPic2.setVisibility(View.VISIBLE);
				imageLoader.DisplayImage(UrlStaticUtil.root_photo
						+ shopComment.photos.get(i).img_url,
						linearitem_oc_imgPic2, false, ImageLoader.L_PICTURE);
			}
		}
	}

	private void initView() {
		this.view = LayoutInflater.from(context).inflate(
				R.layout.item_othercomments, null);
		this.linearitem_oc_linearPic = (LinearLayout) view
				.findViewById(R.id.linearitem_oc_linearPic);
		this.linearitem_oc_imgHead = (ImageView) view
				.findViewById(R.id.linearitem_oc_imgHead);
		this.linearitem_oc_tvName = (TextView) view
				.findViewById(R.id.linearitem_oc_tvName);
		this.linearitem_oc_rbMyRate = (RatingBar) view
				.findViewById(R.id.linearitem_oc_rbMyRate);
		this.linearitem_oc_tvFloor = (TextView) view
				.findViewById(R.id.linearitem_oc_tvFloor);
		this.linearitem_oc_etComment = (EditText) view
				.findViewById(R.id.linearitem_oc_etComment);
		this.linearitem_oc_imgPic0 = (ImageView) view
				.findViewById(R.id.linearitem_oc_imgPic0);
		this.linearitem_oc_imgPic1 = (ImageView) view
				.findViewById(R.id.linearitem_oc_imgPic1);
		this.linearitem_oc_imgPic2 = (ImageView) view
				.findViewById(R.id.linearitem_oc_imgPic2);
		this.linearitem_oc_tvDate = (TextView) view
				.findViewById(R.id.linearitem_oc_tvDate);
		this.linearitem_oc_etComment.setEnabled(false);
		this.linearitem_oc_linearPic.setVisibility(View.GONE);
		this.linearitem_oc_imgPic0.setVisibility(View.GONE);
		this.linearitem_oc_imgPic1.setVisibility(View.GONE);
		this.linearitem_oc_imgPic2.setVisibility(View.GONE);
		this.linearitem_oc_tvFloor.setVisibility(View.GONE);
		// this.linearitem_oc_rbMyRate.setStepSize(0.5f);// 设置步长
		// this.linearitem_oc_rbMyRate.setRating(3.5f);
	}

	public View getView() {
		return view;
	}

}

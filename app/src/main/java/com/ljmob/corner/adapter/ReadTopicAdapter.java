package com.ljmob.corner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.corner.OthersUserInfoActivity;
import com.ljmob.corner.R;
import com.ljmob.corner.UserActivity;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Floor;
import com.ljmob.corner.entity.ReplyFloor;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.ReplyCommentView;

public class ReadTopicAdapter extends BaseAdapter {
	private Context context;
	private List<Floor> floorList = new ArrayList<Floor>();
	private int topicType = 0;
	private ImageLoader imageLoader;
	private MyApplication application;

	/**
	 * 阅读帖子内页（包括公告贴和普通帖）
	 * 
	 * @param context
	 * @param readTopicList
	 * @param topicType
	 *            0代表公告贴、1代普通帖
	 */
	public ReadTopicAdapter(Context context, List<Floor> floorList,
			int topicType) {
		this.context = context;
		this.application = (MyApplication) context.getApplicationContext();
		if (floorList != null) {
			this.floorList = floorList;
		}
		this.topicType = topicType;
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return floorList.size();
	}

	@Override
	public Object getItem(int position) {
		return floorList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return floorList.get(position).id;
	}

	/**
	 * 加载数据
	 * 
	 * @param tempList
	 *            数据列表
	 * @param type
	 *            0代表下拉刷新、1代表上拉加载
	 */
	public void loadData(List<Floor> tempList) {
		floorList.addAll(tempList);
		notifyDataSetChanged();
	}

	/**
	 * 刷新楼层
	 * 
	 * @param tempList
	 */
	public void refreshFloor(Floor floor, int row) {
		floorList.set(row - 2, floor);
		// this.floorList = tempList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_topiccomments, null);
			hold.linearitem_tc_imgHead = (ImageView) convertView
					.findViewById(R.id.linearitem_tc_imgHead);
			hold.linearitem_tc_tvName = (TextView) convertView
					.findViewById(R.id.linearitem_tc_tvName);
			hold.activity_topic_imgSex = (ImageView) convertView
					.findViewById(R.id.activity_topic_imgSex);
			hold.linearitem_tc_tvTime = (TextView) convertView
					.findViewById(R.id.linearitem_tc_tvTime);
			hold.linearitem_tc_tvFloor = (TextView) convertView
					.findViewById(R.id.linearitem_tc_tvFloor);
			hold.linearitem_tc_etComment = (EditText) convertView
					.findViewById(R.id.linearitem_tc_etComment);
			hold.linearitem_tc_btnReply = (ImageButton) convertView
					.findViewById(R.id.linearitem_tc_btnReply);
			hold.linearitem_tc_linearReply = (LinearLayout) convertView
					.findViewById(R.id.linearitem_tc_linearReply);
			hold.linearitem_tc_tvMore = (TextView) convertView
					.findViewById(R.id.linearitem_tc_tvMore);
			hold.linearitem_tc_tvLv = (TextView) convertView
					.findViewById(R.id.linearitem_tc_tvLv);
			hold.linearitem_tc_div0 = convertView
					.findViewById(R.id.linearitem_tc_div0);
			hold.linearitem_tc_tvMore.setVisibility(View.GONE);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		Floor floor = floorList.get(position);
		imageLoader.DisplayImage(UrlStaticUtil.root_photo + floor.user_avatar,
				hold.linearitem_tc_imgHead, true,ImageLoader.L_PICTURE);
		hold.linearitem_tc_tvName.setText(floor.user_nickname);
		if (floor.user_sex.equals("male")) {
			hold.activity_topic_imgSex.setImageResource(R.drawable.img_sex_man);
		} else {
			hold.activity_topic_imgSex
					.setImageResource(R.drawable.img_sex_femal);
		}
		hold.linearitem_tc_tvTime.setText(floor.comment_time);
		hold.linearitem_tc_tvFloor.setText(floor.row + "楼");
		hold.linearitem_tc_etComment.setText(floor.content);
		hold.linearitem_tc_tvLv.setText("Lv"+floor.user_rank);
		List<ReplyFloor> replyFloorList = floor.comment_item;
		if (replyFloorList.size() == 0) {
			hold.linearitem_tc_div0.setVisibility(View.GONE);
		} else {
			hold.linearitem_tc_div0.setVisibility(View.VISIBLE);
		}
		if (hold.linearitem_tc_linearReply != null
				&& hold.linearitem_tc_linearReply.getChildCount() > 0) {
			hold.linearitem_tc_linearReply.removeAllViews();
		}
		for (int i = 0; i < replyFloorList.size(); i++) {
			ReplyFloor replyFloor = replyFloorList.get(i);
			hold.linearitem_tc_linearReply.addView(new ReplyCommentView(
					context, replyFloor).getView());
		}
		hold.linearitem_tc_imgHead.setOnClickListener(new MyOnImgClickListener(
				floor));
		hold.linearitem_tc_btnReply
				.setOnClickListener(new MyReplyFloorClickListener(floor));
		return convertView;
	}

	private class MyOnImgClickListener implements OnClickListener {
		Floor floor;

		public MyOnImgClickListener(Floor floor) {
			this.floor = floor;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			if (floor.user_id == application.preferences.getInt("id", -1)) {
				intent.setClass(context, UserActivity.class);
			} else {
				intent.setClass(context, OthersUserInfoActivity.class);
				intent.putExtra("user_id", floor.user_id);
			}
			context.startActivity(intent);
		}
	}

	private class MyReplyFloorClickListener implements OnClickListener {
		Floor floor;

		public MyReplyFloorClickListener(Floor floor) {
			this.floor = floor;
		}

		@Override
		public void onClick(View v) {
			if (new SelectLoginWay(context).isLogin()) {
				Intent intent = new Intent("ReplyFloor");
				intent.putExtra("floor", floor);
				context.sendBroadcast(intent);
			}
		}

	}

	private class ViewHold {
		private ImageView linearitem_tc_imgHead;
		private TextView linearitem_tc_tvName;
		private ImageView activity_topic_imgSex;
		private TextView linearitem_tc_tvTime;
		private TextView linearitem_tc_tvFloor;// 楼数
		private EditText linearitem_tc_etComment;// 评论内容
		private ImageButton linearitem_tc_btnReply;// 回复评论按钮
		private LinearLayout linearitem_tc_linearReply;// 评论子项linear
		private TextView linearitem_tc_tvMore;// 更多评论
		private TextView linearitem_tc_tvLv;// 等级
		private View linearitem_tc_div0;
	}

}

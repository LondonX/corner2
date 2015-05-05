package com.ljmob.corner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.entity.Reply;
import com.ljmob.corner.entity.ReplyItem;
import com.ljmob.corner.view.MyReplyView;

public class MyReplyAdapter extends BaseAdapter {
	private Context context;
	private List<Reply> replyList = new ArrayList<Reply>();

	public MyReplyAdapter(Context context, List<Reply> replyList) {
		this.context = context;
		if (replyList != null) {
			this.replyList = replyList;
		}
	}

	@Override
	public int getCount() {
		return replyList.size();
	}

	@Override
	public Object getItem(int position) {
		return replyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return replyList.get(position).post_id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_reply, null);
			hold.listitem_reply_tvTitle = (TextView) convertView
					.findViewById(R.id.listitem_reply_tvTitle);
			hold.listitem_topics_dot = (View) convertView
					.findViewById(R.id.listitem_topics_dot);
			hold.listitem_reply_tvExpand = (TextView) convertView
					.findViewById(R.id.listitem_reply_tvExpand);
			hold.listitem_reply_linearOtherReply = (LinearLayout) convertView
					.findViewById(R.id.listitem_reply_linearOtherReply);
			hold.listitem_reply_tvExpand.setVisibility(View.GONE);
			hold.listitem_topics_dot.setVisibility(View.GONE);
			hold.listitem_reply_tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		Reply reply = replyList.get(position);
		hold.listitem_reply_tvTitle.setText("原帖：" + reply.post_subject);
		List<ReplyItem> replyItemList = reply.item;
		if (hold.listitem_reply_linearOtherReply.getChildCount() > 0) {
			hold.listitem_reply_linearOtherReply.removeAllViews();
		}
		for (int i = 0; i < replyItemList.size(); i++) {
			ReplyItem replyItem = replyItemList.get(i);
			MyReplyView myReplyView = new MyReplyView(context, replyItem);
			hold.listitem_reply_linearOtherReply.addView(myReplyView.getView());
		}

		return convertView;
	}

	private class ViewHold {
		private TextView listitem_reply_tvTitle;
		private View listitem_topics_dot;// 最新小圆点
		private TextView listitem_reply_tvExpand;// 查看全部
		private LinearLayout listitem_reply_linearOtherReply;// 添加评论
	}

}

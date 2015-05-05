package com.ljmob.corner.view;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.entity.ReplyItem;

public class MyReplyView {
	private Context context;
	private View view;
	private TextView linearitem_reply_tvContent;// 回复者和内容
	private ImageView linearitem_reply_imgReply;
	private TextView linearitem_reply_tvTime;
	private ReplyItem replyItem;

	public MyReplyView(Context context, ReplyItem replyItem) {
		this.context = context;
		this.replyItem = replyItem;
		initView();
	}

	private void initView() {
		view = LayoutInflater.from(context).inflate(R.layout.item_reply, null);
		linearitem_reply_tvContent = (TextView) view
				.findViewById(R.id.linearitem_reply_tvContent);
		linearitem_reply_imgReply = (ImageView) view
				.findViewById(R.id.linearitem_reply_imgReply);
		linearitem_reply_tvTime = (TextView) view
				.findViewById(R.id.linearitem_reply_tvTime);

		// linearitem_reply_tvContent.setText(Html.fromHtml("<font color=#000000>"
		// + replyName + ":</font>" + replyContent));
		if (replyItem.comment_user_nickname != null) {
			linearitem_reply_tvContent
					.setText(getSpannedString("回复 "
							+ replyItem.comment_user_nickname + "：",
							replyItem.content));
		} else {
			linearitem_reply_tvContent.setText(getSpannedString("回复：",
					replyItem.content));
		}
		linearitem_reply_tvContent.setMovementMethod(LinkMovementMethod
				.getInstance());
		linearitem_reply_tvTime.setText(replyItem.comment_time);
		linearitem_reply_imgReply.setVisibility(View.GONE);
	}

	private SpannableStringBuilder getSpannedString(String replyName,
			String replyContent) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		int len = 0;
		int tempLen = 0;
		builder.append(replyName);
		len = builder.length();
		builder.setSpan(new ForegroundColorSpan(0xff000000), 0, len,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// builder.setSpan(new URLSpan("tel:15116488084"), 0, len,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.append(replyContent);
		tempLen = len;
		len = builder.length();
		builder.setSpan(new ForegroundColorSpan(context.getResources()
				.getColor(R.color.gray_light)), tempLen, len,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}

	public View getView() {
		return view;
	}
}

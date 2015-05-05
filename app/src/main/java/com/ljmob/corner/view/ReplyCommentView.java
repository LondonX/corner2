package com.ljmob.corner.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.entity.ReplyFloor;
import com.ljmob.corner.util.ToastUtil;

/**
 * 回复评论view
 * 
 * @author YANGBANG
 * 
 */
public class ReplyCommentView implements OnClickListener {
	private Context context;
	private View view;
	private TextView linearitem_reply_tvContent;// 回复者和内容
	private ImageView linearitem_reply_imgReply;
	private TextView linearitem_reply_tvTime;
	private ReplyFloor replyFloor;

	public ReplyCommentView(Context context, ReplyFloor replyFloor) {
		this.context = context;
		this.replyFloor = replyFloor;
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
		if (replyFloor.comment_user_nickname != null) {
			linearitem_reply_tvContent.setText(getSpannedString(
					replyFloor.user_nickname, "回复 "
							+ replyFloor.comment_user_nickname + "："
							+ replyFloor.content));
		} else {
			linearitem_reply_tvContent.setText(getSpannedString(
					replyFloor.user_nickname, replyFloor.content));
		}
		linearitem_reply_tvContent.setMovementMethod(LinkMovementMethod
				.getInstance());
		linearitem_reply_tvTime.setText(replyFloor.comment_time);
		linearitem_reply_imgReply.setVisibility(View.GONE);
		linearitem_reply_imgReply.setOnClickListener(this);
	}

	private SpannableStringBuilder getSpannedString(String replyName,
			String replyContent) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		int len = 0;
		int tempLen = 0;
		builder.append(replyName + ":");
		len = builder.length();
//		builder.setSpan(new ForegroundColorSpan(0xff000000), 0, len,
//				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// builder.setSpan(new URLSpan("tel:15116488084"), 0, len,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.append(replyContent);
		tempLen = len;
		len = builder.length();
		builder.setSpan(new ForegroundColorSpan(Color.BLACK), tempLen, len,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}

	public View getView() {
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linearitem_reply_imgReply:// 回复
			ToastUtil.show(context, R.string.toast_reply);
			break;

		default:
			break;
		}
	}

}

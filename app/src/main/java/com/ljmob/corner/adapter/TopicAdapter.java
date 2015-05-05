package com.ljmob.corner.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ljmob.corner.OthersUserInfoActivity;
import com.ljmob.corner.R;
import com.ljmob.corner.ReadTopicActivity;
import com.ljmob.corner.UserActivity;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Topic;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.HttpRequestTools;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.UrlStaticUtil;

public class TopicAdapter extends BaseAdapter implements Callback {
	public static final int TOPIC_ZJ = 0;
	public static final int TOPIC_NOTICE_MORE = 1;
	public static final int TOPIC_MY_CENTER = 2;
	public static final int TOPIC_FOCUS = 3;
	private Context context;
	private List<Topic> topicList = new ArrayList<Topic>();
	private ArrayList<Boolean> isAnimFlags;
	private int itemType = 0;// 0代表转角、1代表阅读更多官方贴、2代表个人中心主题、3代表关注的帖子
	private ImageLoader imageLoader;
	private int clickIndex = 0;
	private MyApplication application;
	private Handler handler;

	/**
	 * 
	 * @param context
	 * @param newses
	 * @param itemType
	 *            0代表见闻、1代表阅读更多见闻、2代表个人中心主题、3代表关注的帖子
	 */
	public TopicAdapter(Context context, List<Topic> topicList, int itemType) {
		super();
		this.context = context;
		application = (MyApplication) context.getApplicationContext();
		handler = new Handler(this);
		if (topicList != null) {
			this.topicList = topicList;
		} else {
			this.topicList = new ArrayList<Topic>();
		}
		this.itemType = itemType;
		this.imageLoader = new ImageLoader(context);
		this.isAnimFlags = new ArrayList<Boolean>();
		for (int i = 0; i < this.topicList.size(); i++) {
			this.isAnimFlags.add(false);
			this.topicList.get(i).index = i;
		}
	}

	@Override
	public int getCount() {
		return topicList.size();
	}

	@Override
	public Object getItem(int position) {
		return topicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return topicList.get(position).id;
	}

	/**
	 * 加载数据
	 * 
	 * @param tempList
	 *            数据列表
	 * @param type
	 *            0代表下拉刷新、1代表上拉加载
	 */
	public void loadData(List<Topic> tempList, final int type) {
		if (type == 0) {
			topicList = tempList;
			if (!isAnimFlags.isEmpty()) {
				isAnimFlags.clear();
			}
			for (int i = 0; i < tempList.size(); i++) {
				isAnimFlags.add(i < 2);
				topicList.get(i).index = i;
			}
		} else {
			int laseIndex = topicList.size();
			topicList.addAll(tempList);
			for (int i = 0; i < tempList.size(); i++) {
				topicList.get(laseIndex + i).index = laseIndex + i;
				isAnimFlags.add(false);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHold hold = null;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					com.ljmob.corner.R.layout.item_topics, null);
			hold.listitem_topics_linearCard = (RelativeLayout) convertView
					.findViewById(R.id.listitem_topics_linearCard);
			hold.listitem_topics_imgHeadPoster = (ImageView) convertView
					.findViewById(R.id.listitem_topics_imgHeadPoster);
			hold.listitem_topics_tvNamePoster = (TextView) convertView
					.findViewById(R.id.listitem_topics_tvNamePoster);
			hold.listitem_topics_imgSex = (ImageView) convertView
					.findViewById(R.id.listitem_topics_imgSex);
			hold.listitem_topics_tvLv = (TextView) convertView
					.findViewById(R.id.listitem_topics_tvLv);
			hold.listitem_topics_tvTime = (TextView) convertView
					.findViewById(R.id.listitem_topics_tvTime);
			hold.listitem_topics_tvTitle = (TextView) convertView
					.findViewById(R.id.listitem_topics_tvTitle);
			hold.listitem_topics_tvTimeUser = (TextView) convertView
					.findViewById(R.id.listitem_topics_tvTimeUser);
			hold.listitem_topics_dot = (View) convertView
					.findViewById(R.id.listitem_topics_dot);
			hold.listitem_topics_tvContentPreview = (TextView) convertView
					.findViewById(R.id.listitem_topics_tvContentPreview);
			hold.linearitem_topic_linearPic = (LinearLayout) convertView
					.findViewById(R.id.linearitem_topic_linearPic);
			hold.linearitem_topic_imgPic0 = (ImageView) convertView
					.findViewById(R.id.linearitem_topic_imgPic0);
			hold.linearitem_topic_imgPic1 = (ImageView) convertView
					.findViewById(R.id.linearitem_topic_imgPic1);
			hold.linearitem_topic_imgPic2 = (ImageView) convertView
					.findViewById(R.id.linearitem_topic_imgPic2);
			hold.linearitem_topic_linearPraise = (LinearLayout) convertView
					.findViewById(R.id.linearitem_topic_linearPraise);
			hold.linearitem_topic_imgPraise = (ImageView) convertView
					.findViewById(R.id.linearitem_topic_imgPraise);
			hold.linearitem_topic_tvPraise = (TextView) convertView
					.findViewById(R.id.linearitem_topic_tvPraise);
			hold.linearitem_topic_linearComments = (LinearLayout) convertView
					.findViewById(R.id.linearitem_topic_linearComments);
			hold.linearitem_topic_imgComments = (ImageView) convertView
					.findViewById(R.id.linearitem_topic_imgComments);
			hold.linearitem_topic_tvComments = (TextView) convertView
					.findViewById(R.id.linearitem_topic_tvComments);
			hold.listitem_topics_imgDelete = (ImageView) convertView
					.findViewById(R.id.listitem_topics_imgDelete);
			hold.listitem_topics_div0 = (View) convertView
					.findViewById(R.id.listitem_topics_div0);
			hold.linearitem_topic_linearCommIndicator = (LinearLayout) convertView
					.findViewById(R.id.linearitem_topic_linearCommIndicator);
			hold.linearitem_topic_rlOptions = (RelativeLayout) convertView
					.findViewById(R.id.linearitem_topic_rlOptions);
			hold.linearitem_topic_tvCommIndicator = (TextView) convertView
					.findViewById(R.id.linearitem_topic_tvCommIndicator);
			hold.listitem_topics_tvTime1 = (TextView) convertView
					.findViewById(R.id.listitem_topics_tvTime1);
			hold.listitem_topics_dot.setVisibility(View.GONE);
			hold.listitem_topics_imgDelete.setVisibility(View.GONE);
			hold.linearitem_topic_linearPic.setVisibility(View.GONE);
			hold.linearitem_topic_imgPic0.setVisibility(View.GONE);
			hold.linearitem_topic_imgPic1.setVisibility(View.GONE);
			hold.linearitem_topic_imgPic2.setVisibility(View.GONE);
			hold.listitem_topics_tvTime1.setVisibility(View.GONE);
			hold.listitem_topics_div0.setVisibility(View.GONE);
			switch (itemType) {
			case TOPIC_ZJ:// 转角帖
				hold.listitem_topics_linearCard.setVisibility(View.VISIBLE);
				hold.listitem_topics_div0.setVisibility(View.VISIBLE);
				break;
			case TOPIC_NOTICE_MORE:// 更多公告贴
				hold.listitem_topics_linearCard.setVisibility(View.GONE);
				break;
			case TOPIC_MY_CENTER:// 个人中心贴
				hold.listitem_topics_linearCard.setVisibility(View.GONE);
				hold.listitem_topics_imgDelete.setVisibility(View.VISIBLE);
				hold.listitem_topics_tvTime1.setVisibility(View.VISIBLE);
				break;
			case TOPIC_FOCUS:// 关注贴
				hold.listitem_topics_linearCard.setVisibility(View.GONE);
				hold.listitem_topics_tvContentPreview.setVisibility(View.GONE);
				hold.listitem_topics_tvTimeUser.setVisibility(View.GONE);
				hold.linearitem_topic_tvPraise.setVisibility(View.GONE);
				hold.linearitem_topic_linearCommIndicator
						.setVisibility(View.VISIBLE);
				hold.linearitem_topic_rlOptions.setVisibility(View.GONE);
				break;

			default:
				break;
			}
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}

		Topic topic = topicList.get(position);
		switch (itemType) {
		case TOPIC_ZJ:// 转角帖
			hold.listitem_topics_tvNamePoster.setText(topic.user_nickname);
			if (topic.content != null && !topic.content.equals("")) {
				hold.listitem_topics_tvContentPreview
						.setVisibility(View.VISIBLE);
				hold.listitem_topics_tvContentPreview.setText(Html
						.fromHtml(topic.content));
				// hold.listitem_topics_tvContentPreview.setText(Html.fromHtml(
				// topic.content, imageGetter, null));
			} else {
				hold.listitem_topics_tvContentPreview.setVisibility(View.GONE);
			}
			hold.listitem_topics_tvTimeUser.setText(Html
					.fromHtml(topic.praise_user));
			hold.linearitem_topic_tvPraise.setText("" + topic.praise_num);
			if (topic.user_sex != null && topic.user_sex.equals("male")) {
				hold.listitem_topics_imgSex
						.setImageResource(R.drawable.img_sex_man);
			} else {
				hold.listitem_topics_imgSex
						.setImageResource(R.drawable.img_sex_femal);
			}
			imageLoader.DisplayImage(UrlStaticUtil.root_photo
					+ topic.user_avatar, hold.listitem_topics_imgHeadPoster,
					true, ImageLoader.L_PICTURE);
			hold.listitem_topics_imgHeadPoster
					.setOnClickListener(new MyOnClickUserInfoListener(topic));
			break;
		case TOPIC_NOTICE_MORE:// 更多公告贴
			if (topic.content != null && !topic.content.equals("")) {
				hold.listitem_topics_tvContentPreview
						.setVisibility(View.VISIBLE);
				hold.listitem_topics_tvContentPreview.setText(Html
						.fromHtml(topic.content));
			} else {
				hold.listitem_topics_tvContentPreview.setVisibility(View.GONE);
			}
			hold.listitem_topics_tvTimeUser.setText(Html
					.fromHtml(topic.praise_user));
			hold.linearitem_topic_tvPraise.setText("" + topic.praise_num);
			break;
		case TOPIC_MY_CENTER:// 个人中心贴
			if (topic.content != null && !topic.content.equals("")) {
				hold.listitem_topics_tvContentPreview
						.setVisibility(View.VISIBLE);
				hold.listitem_topics_tvContentPreview.setText(Html
						.fromHtml(topic.content));
			} else {
				hold.listitem_topics_tvContentPreview.setVisibility(View.GONE);
			}
			hold.listitem_topics_tvTimeUser.setText(Html
					.fromHtml(topic.praise_user));
			hold.linearitem_topic_tvPraise.setText("" + topic.praise_num);
			break;
		case TOPIC_FOCUS:// 关注贴

			break;

		default:
			break;
		}
		if (topic.is_praise == 0) {
			hold.linearitem_topic_imgPraise.setColorFilter(Color.BLACK);
			hold.linearitem_topic_tvPraise.setTextColor(Color.BLACK);
		} else if (topic.is_praise == 1) {
			hold.linearitem_topic_imgPraise.setColorFilter(context
					.getResources().getColor(R.color.red));
			hold.linearitem_topic_tvPraise.setTextColor(context.getResources()
					.getColor(R.color.red));
		}
		hold.linearitem_topic_linearPraise
				.setOnClickListener(new MyPraiseOnClickListener(topic));
		hold.linearitem_topic_linearComments
				.setOnClickListener(new MyCommentOnClickListener(topic));
		hold.listitem_topics_tvTitle.setText(topic.subject);
		hold.linearitem_topic_tvComments.setText(topic.comment_num + "");
		hold.linearitem_topic_tvCommIndicator.setText(topic.comment_num + "");
		hold.listitem_topics_tvTime.setText(topic.last_comments_time);
		hold.listitem_topics_tvTime1.setText(topic.create_time);
		hold.listitem_topics_tvLv.setText("Lv" + topic.user_rank);
		if (topic.photos != null && topic.photos.size() > 0) {
			for (int i = 0; i < topic.photos.size(); i++) {
				hold.linearitem_topic_linearPic.setVisibility(View.VISIBLE);
				if (i == 0) {
					hold.linearitem_topic_imgPic0.setVisibility(View.VISIBLE);
					imageLoader.DisplayImage(UrlStaticUtil.root_photo
							+ topic.photos.get(i).img_url,
							hold.linearitem_topic_imgPic0, false,
							ImageLoader.L_PICTURE);
				}
				if (i == 1) {
					hold.linearitem_topic_imgPic1.setVisibility(View.VISIBLE);
					imageLoader.DisplayImage(UrlStaticUtil.root_photo
							+ topic.photos.get(i).img_url,
							hold.linearitem_topic_imgPic1, false,
							ImageLoader.L_PICTURE);
				}
				if (i == 2) {
					hold.linearitem_topic_imgPic2.setVisibility(View.VISIBLE);
					imageLoader.DisplayImage(UrlStaticUtil.root_photo
							+ topic.photos.get(i).img_url,
							hold.linearitem_topic_imgPic2, false,
							ImageLoader.L_PICTURE);
				}
			}
			if (topic.photos.size() == 1) {
				hold.linearitem_topic_imgPic0.setVisibility(View.VISIBLE);
				hold.linearitem_topic_imgPic1.setVisibility(View.GONE);
				hold.linearitem_topic_imgPic2.setVisibility(View.GONE);
			} else if (topic.photos.size() == 2) {
				hold.linearitem_topic_imgPic0.setVisibility(View.VISIBLE);
				hold.linearitem_topic_imgPic1.setVisibility(View.VISIBLE);
				hold.linearitem_topic_imgPic2.setVisibility(View.GONE);
			} else {
				hold.linearitem_topic_imgPic0.setVisibility(View.VISIBLE);
				hold.linearitem_topic_imgPic1.setVisibility(View.VISIBLE);
				hold.linearitem_topic_imgPic2.setVisibility(View.VISIBLE);
			}
		} else {
			hold.linearitem_topic_linearPic.setVisibility(View.GONE);
		}
		if (isAnimFlags.get(position) == false) {
			convertView.startAnimation(AnimationUtils.loadAnimation(context,
					R.anim.item_in));
			isAnimFlags.set(position, true);
		}
		return convertView;
	}

	private class ViewHold {
		RelativeLayout listitem_topics_linearCard;// 用户信息relative
		ImageView listitem_topics_imgHeadPoster;// 头像
		TextView listitem_topics_tvNamePoster;// 姓名
		ImageView listitem_topics_imgSex;// 性别
		TextView listitem_topics_tvLv;// 等级
		TextView listitem_topics_tvTime;// 时间
		TextView listitem_topics_tvTitle;//
		TextView listitem_topics_tvTimeUser;
		View listitem_topics_dot;// 点
		TextView listitem_topics_tvContentPreview;// 内容
		LinearLayout linearitem_topic_linearPic;
		ImageView linearitem_topic_imgPic0;
		ImageView linearitem_topic_imgPic1;
		ImageView linearitem_topic_imgPic2;
		LinearLayout linearitem_topic_linearPraise;// 赞linear
		ImageView linearitem_topic_imgPraise;// 赞图标
		TextView linearitem_topic_tvPraise;// 赞
		LinearLayout linearitem_topic_linearComments;// 评论linear
		ImageView linearitem_topic_imgComments;// 评论图标
		TextView linearitem_topic_tvComments;// 评论次数
		ImageView listitem_topics_imgDelete;// 取消关注
		View listitem_topics_div0;// 分割线
		LinearLayout linearitem_topic_linearCommIndicator;
		RelativeLayout linearitem_topic_rlOptions;
		TextView linearitem_topic_tvCommIndicator;
		TextView listitem_topics_tvTime1;// 右侧时间
	}

	private class MyOnClickUserInfoListener implements OnClickListener {
		private Topic topic;

		public MyOnClickUserInfoListener(Topic topic) {
			this.topic = topic;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			if (topic.user_id == application.preferences.getInt("id", -1)) {
				intent.setClass(context, UserActivity.class);
			} else {
				intent.setClass(context, OthersUserInfoActivity.class);
				intent.putExtra("user_id", topic.user_id);
			}
			context.startActivity(intent);
		}
	}

	private class MyCommentOnClickListener implements OnClickListener {
		Topic topic;

		public MyCommentOnClickListener(Topic topic) {
			this.topic = topic;
		}

		@Override
		public void onClick(View v) {
			if (new SelectLoginWay(context).isLogin()) {
				Intent intent = new Intent(context, ReadTopicActivity.class);
				intent.putExtra("id", topic.id);
				intent.putExtra("isExternalComment", true);
				if (itemType == 1) {
					intent.putExtra("isOfficial", true);
				}
				context.startActivity(intent);
			}
		}

	}

	private class MyPraiseOnClickListener implements OnClickListener {
		private Topic topic;

		public MyPraiseOnClickListener(Topic topic) {
			this.topic = topic;
		}

		@Override
		public void onClick(View v) {
			if (new SelectLoginWay(context).isLogin()) {
				clickIndex = topic.index;
				praise(topic);
			}
		}

	}

	/**
	 * 外层帖子列表点赞
	 * 
	 * @param topic
	 */
	private void praise(final Topic topic) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("id", topic.id);
				Map<String, File> files = new HashMap<String, File>();
				String response = HttpRequestTools.doPut(context,
						UrlStaticUtil.getClickPraise(), params, files,
						application.preferences.getString("token", ""));
				Message message = new Message();
				message.what = Constants.CLICK_PRAISE;
				message.obj = response;
				handler.sendMessage(message);
			}
		}).start();
	}

	/**
	 * 更新赞数
	 * 
	 * @param result
	 *            返回结果Response
	 */
	private void updatePraiseNum(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			topicList.get(clickIndex).praise_num = jsonObject
					.getInt("praise_num");
			topicList.get(clickIndex).praise_user = jsonObject
					.getString("praise_user");
			topicList.get(clickIndex).is_praise = jsonObject
					.getInt("is_praise");
			notifyDataSetChanged();
			Log.i("赞数：", topicList.get(clickIndex).praise_num + "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Constants.CLICK_PRAISE:
			String reponse = (String) msg.obj;
			if (reponse != null && !reponse.equals("")) {
				updatePraiseNum(reponse);
			}
			break;

		default:
			break;
		}
		return false;
	}

}

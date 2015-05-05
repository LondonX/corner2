package com.ljmob.corner.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Relation;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.UrlStaticUtil;

public class RelationAdapter extends BaseAdapter {
	private Context context;
	private List<Relation> relationList = new ArrayList<Relation>();
	private int tag = 0;
	private ImageLoader imageLoader;
	private ServiceImpl serviceImpl;

	/**
	 * 
	 * @param context
	 *            上下文环境
	 * @param relationList
	 *            关系列表
	 * @param tag
	 *            标识0代表我关注的人，1代表我的粉丝
	 */
	public RelationAdapter(Context context, List<Relation> relationList, int tag) {
		this.context = context;
		if (relationList != null) {
			this.relationList = relationList;
		}
		this.tag = tag;
		this.imageLoader = new ImageLoader(context);
		this.serviceImpl = FactoryService.getIService(context);
	}

	@Override
	public int getCount() {
		return relationList.size();
	}

	@Override
	public Object getItem(int position) {
		return relationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return relationList.get(position).user_id;
	}

	/**
	 * 刷新列表
	 * 
	 * @param tempList
	 */
	public void refreshList(List<Relation> tempList) {
		this.relationList = tempList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_relation, null);
			hold.listitem_relation_imgHead = (ImageView) convertView
					.findViewById(R.id.listitem_relation_imgHead);
			hold.listitem_relation_tvName = (TextView) convertView
					.findViewById(R.id.listitem_relation_tvName);
			hold.listitem_relation_tvSign = (TextView) convertView
					.findViewById(R.id.listitem_relation_tvSign);
			hold.listitem_relation_imgSex = (ImageView) convertView
					.findViewById(R.id.listitem_relation_imgSex);
			hold.listitem_relation_tvLv = (TextView) convertView
					.findViewById(R.id.listitem_relation_tvLv);
			hold.listitem_relation_btnOption = (ImageButton) convertView
					.findViewById(R.id.listitem_relation_btnOption);
			if (tag == 0) {
				hold.listitem_relation_btnOption
						.setImageResource(R.drawable.img_delete);
			} else if (tag == 1) {
				hold.listitem_relation_btnOption
						.setImageResource(R.drawable.img_add);
			}
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		Relation relation = relationList.get(position);
		imageLoader.DisplayImage(UrlStaticUtil.root_photo + relation.avatar,
				hold.listitem_relation_imgHead, true,ImageLoader.L_PICTURE);
		hold.listitem_relation_tvName.setText(relation.nickname);
		hold.listitem_relation_tvSign.setText(relation.signature);
		hold.listitem_relation_tvLv.setText("Lv" + relation.rank);
		if (relation.sex.equals("male")) {
			hold.listitem_relation_imgSex
					.setImageResource(R.drawable.img_sex_man);
		} else {
			hold.listitem_relation_imgSex
					.setImageResource(R.drawable.img_sex_femal);
		}
		if (tag == 0) {
			hold.listitem_relation_btnOption
					.setOnClickListener(new MyOnClickListener(relation, 0));
		} else if (tag == 1) {
			hold.listitem_relation_btnOption
					.setOnClickListener(new MyOnClickListener(relation, 1));
		}

		return convertView;
	}

	private class MyOnClickListener implements OnClickListener {
		private Relation relation;
		private int tag = 0;

		/**
		 * 
		 * @param relation
		 * @param tag
		 *            标签0代表我关注的人、1代表我的粉丝
		 */
		public MyOnClickListener(Relation relation, int tag) {
			this.relation = relation;
			this.tag = tag;
		}

		@Override
		public void onClick(View v) {
			if (tag == 0) {
				serviceImpl.getRemoveUserAttention(UrlStaticUtil
						.getRemoveUserAttention(this.relation.user_id),
						Constants.REMOVE_USER_ATTENTION);
				serviceImpl.getDialog().setMsg(R.string.dialog_cancel_focus);
			} else if (tag == 1) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("id", this.relation.user_id);
				serviceImpl.getAddUserAttention(
						UrlStaticUtil.getAddUserAttention(), params,
						new HashMap<String, File>(),
						Constants.ADD_USER_ATTENTION);
				serviceImpl.getDialog().setMsg(R.string.dialog_add_focus);
			}
		}
	}

	private class ViewHold {
		private ImageView listitem_relation_imgHead;// 头像
		private TextView listitem_relation_tvName;
		private TextView listitem_relation_tvSign;
		private ImageView listitem_relation_imgSex;
		private TextView listitem_relation_tvLv;// 等级
		private ImageButton listitem_relation_btnOption;// 添加或移除
	}

}

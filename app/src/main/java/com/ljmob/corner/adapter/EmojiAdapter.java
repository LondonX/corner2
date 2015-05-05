package com.ljmob.corner.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.util.EmojiUtil;

public class EmojiAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> emoji;

	public EmojiAdapter(Context context) {
		this.context = context;
		this.emoji = EmojiUtil.getEmojis(context);
	}

	@Override
	public int getCount() {
		return emoji.size();
	}

	@Override
	public Object getItem(int position) {
		return emoji.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_emoji, null);
			holder.item_emoji_tvEmoji = (TextView) convertView
					.findViewById(R.id.item_emoji_tvEmoji);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.item_emoji_tvEmoji.setText(emoji.get(position));
		return convertView;
	}

	private class ViewHolder {
		private TextView item_emoji_tvEmoji;
	}
}

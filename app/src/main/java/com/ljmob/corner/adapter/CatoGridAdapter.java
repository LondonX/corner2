package com.ljmob.corner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Cato;
import com.ljmob.corner.util.UrlStaticUtil;

public class CatoGridAdapter extends BaseAdapter {
	private Context context;
	private List<Cato> catoList = new ArrayList<Cato>();
	private ImageLoader imageLoader;

	public CatoGridAdapter(Context context, List<Cato> catoList) {
		this.context = context;
		if (catoList != null) {
			this.catoList = catoList;
		}
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return catoList.size();
	}

	@Override
	public Object getItem(int position) {
		return catoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return catoList.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		parent.setFocusable(true);
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.griditem_cato, null);
			hold.griditem_cato_imgLabel = (ImageView) convertView
					.findViewById(R.id.griditem_cato_imgLabel);
			hold.griditem_cato_tvLabel = (TextView) convertView
					.findViewById(R.id.griditem_cato_tvLabel);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		Cato cato = catoList.get(position);
		if (cato.name != null && !cato.name.equals("")) {
			hold.griditem_cato_tvLabel.setText(Html.fromHtml(cato.name));
		} else {
			hold.griditem_cato_tvLabel.setVisibility(View.GONE);
		}
		imageLoader.DisplayImage(UrlStaticUtil.root_photo + cato.icon,
				hold.griditem_cato_imgLabel, false,ImageLoader.L_PICTURE);
		return convertView;
	}

	private class ViewHold {
		private ImageView griditem_cato_imgLabel;
		private TextView griditem_cato_tvLabel;
	}

}

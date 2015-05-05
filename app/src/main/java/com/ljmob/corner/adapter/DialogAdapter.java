package com.ljmob.corner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ljmob.corner.R;


/**
 * Simple adapter example for custom items in the dialog
 */
public class DialogAdapter extends BaseAdapter implements View.OnClickListener {

    private Toast mToast;
    private final Context mContext;
    private final CharSequence[] mItems;
    private OnDialogItemClickListener onDialogItemClickListener;

    public DialogAdapter(Context context, String[] items) {
        this.mContext = context;
        this.mItems = items;
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        this.onDialogItemClickListener = onDialogItemClickListener;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public CharSequence getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_list, parent, false);
        }
        TextView view = (TextView) convertView.findViewById(R.id.item_dialog_list_btn0);
        view.setOnClickListener(this);
        view.setText(mItems[position]);
        view.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
        if (onDialogItemClickListener != null) {
            onDialogItemClickListener.onDialogListClick(index);
        }
    }

    public interface OnDialogItemClickListener {
        public void onDialogListClick(int index);
    }
}
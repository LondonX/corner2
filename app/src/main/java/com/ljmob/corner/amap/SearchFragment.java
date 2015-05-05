package com.ljmob.corner.amap;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ljmob.corner.R;
import com.ljmob.corner.util.MyApplication;

public class SearchFragment extends ListFragment implements OnClickListener {
	// String[] cities = { "Shenzhen", "Beijing", "Shanghai", "Guangzhou",
	// "Wuhan", "Tianjing", "Changsha", "Xi'an", "Chongqing", "Guilin", };
	private List<String> listString;
	private OnHeadlineSelectedListener mCallback;
	private LinearLayout lin_page;
	private Button btn_search_prev;// 上一页
	private Button btn_search_next_page;// 下一页
	private MyApplication application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("YANGBANG", "SearchActivity----onCreate");
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		application = (MyApplication) getActivity().getApplication();
		initView();
	}

	private void initView() {
		lin_page = (LinearLayout) getView().findViewById(R.id.lin_page);
		btn_search_prev = (Button) getView().findViewById(R.id.btn_search_prev);
		btn_search_next_page = (Button) getView().findViewById(
				R.id.btn_search_next_page);
		btn_search_prev.setOnClickListener(this);
		btn_search_next_page.setOnClickListener(this);
		setFooterIsShow();
	}

	public void setFooterIsShow() {
		if (application.isListMode) {
			lin_page.setVisibility(View.VISIBLE);
			mCallback.onFristPage();
//			setList(application.listString);
		} else {
			lin_page.setVisibility(View.GONE);
		}
	}

	public void setList(List<String> listString) {
		this.listString = listString;
		if (listString != null) {
			this.setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, listString));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// return super.onCreateView(inflater, container, savedInstanceState);
		Log.i("YANGBANG", "SearchActivity----onCreateView");
		return inflater.inflate(R.layout.activity_searchmap, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(getActivity(), listString.get(position),
				Toast.LENGTH_SHORT).show();
		application.isListMode = false;
		mCallback.onArticleSelected(listString.get(position));
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("YANGBANG", "SearchActivity----onPause");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("YANGBANG", "SearchActivity----onResume");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("YANGBANG", "SearchActivity----onStop");
	}

	@Override
	public void onDestroy() {
		Log.i("YANGBANG", "SearchActivity----onDestroy");
		super.onDestroy();
		Log.i("YANGBANG", "SearchActivity----onDestroy");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i("YANGBANG", "SearchActivity----onStart");
	}

	// Container Activity must implement this interface
	public interface OnHeadlineSelectedListener {
		public void onArticleSelected(String locationString);

		public void onNextPage();

		public void onPrev();
		
		public void onFristPage();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnHeadlineSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search_prev:// 上一页
			mCallback.onPrev();
			break;
		case R.id.btn_search_next_page:// 下一页
			mCallback.onNextPage();
			break;

		default:
			break;
		}
	}

}

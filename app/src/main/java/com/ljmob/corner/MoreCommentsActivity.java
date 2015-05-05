package com.ljmob.corner;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.corner.adapter.ShopCommentAdapter;
import com.ljmob.corner.entity.ShopComment;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.UrlStaticUtil;

public class MoreCommentsActivity extends ActionBarActivity implements OnResponseResult {
	private LinearLayout activity_mc_linearHead;// 评论数linear
	private TextView activity_mc_tvCount;// 评论数
	private ListView activity_mc_lvComments;
	private ShopCommentAdapter adapter;
	private List<ShopComment> commentList = new ArrayList<ShopComment>();
	private ServiceImpl serviceImpl;
	private int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_morecomments);
		initToolBar();
		initView();
		id = getIntent().getIntExtra("id", -1);
		serviceImpl = new ServiceImpl(this);
		serviceImpl.getShopComment(UrlStaticUtil.getShopComment(id),
				Constants.SHOP_COMMENT);
	}

	private void initView() {
		activity_mc_linearHead = (LinearLayout) this
				.findViewById(R.id.activity_mc_linearHead);
		activity_mc_tvCount = (TextView) this
				.findViewById(R.id.activity_mc_tvCount);
		activity_mc_lvComments = (ListView) this
				.findViewById(R.id.activity_mc_lvComments);
		activity_mc_linearHead.setVisibility(View.GONE);
	}

	private void initToolBar() {
        Toolbar tb=(Toolbar)findViewById(R.id.toolbar_root);
        setSupportActionBar(tb);
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setTitle(R.string.activity_mc);
	}

	private void setShopCommentList(String jsonString) {
		try {
			JSONArray shopCommentItem = new JSONArray(jsonString);
			commentList = JsonTools.getShopCommentList(shopCommentItem
					.toString());
			adapter = new ShopCommentAdapter(this, commentList);
			activity_mc_lvComments.setAdapter(adapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResult(String result, int tag, int status) {
		if (tag == Constants.SHOP_COMMENT) {
			setShopCommentList(result);
		}
	}
}

package com.ljmob.corner;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;

import com.ljmob.corner.adapter.TopicAdapter;
import com.ljmob.corner.entity.Topic;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.CustomListView;
import com.ljmob.corner.view.CustomListView.OnLoadMoreListener;
import com.ljmob.corner.view.CustomListView.OnRefreshListener;

public class ReadActivity extends ActionBarActivity implements OnItemClickListener,
        OnResponseResult {
    private CustomListView activity_reads_lv;
    private List<Topic> topicList = new ArrayList<Topic>();
    private TopicAdapter adapter;
    private ServiceImpl serviceImpl;
    private int page = 1;
    private int pre_page = 10;
    private int refreshType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reads);
        initToolBar();
        initView();
        serviceImpl = new ServiceImpl(this);
        getReadList(page, pre_page);
    }

    private void getReadList(int page, int pre_page) {
        serviceImpl.getReadList(UrlStaticUtil.getReadList(page, pre_page),
                Constants.READ_LIST);
    }

    private void setReadList(String jsonString) {
        try {
            JSONArray readList = new JSONArray(jsonString);
            topicList = JsonTools.getTopicList(readList.toString());
            if (topicList != null && topicList.size() < pre_page) {
                activity_reads_lv.setCanLoadMore(false);
            }
            adapter = new TopicAdapter(this, topicList,
                    TopicAdapter.TOPIC_NOTICE_MORE);
            activity_reads_lv.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        activity_reads_lv = (CustomListView) this
                .findViewById(R.id.activity_reads_lv);
        activity_reads_lv.setOnItemClickListener(this);
        activity_reads_lv.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshType = 0;
                page = 1;
                getReadList(page, pre_page);
            }
        });
        activity_reads_lv.setOnLoadListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                refreshType = 1;
                page++;
                getReadList(page, pre_page);
            }
        });
    }

    /**
     * 刷新或加载数据
     *
     * @param tempList
     */
    private void loadData(final List<Topic> tempList) {
        if (refreshType == 0) {
            if (adapter != null) {
                adapter.loadData(tempList, 0);
            }
            activity_reads_lv.onRefreshComplete();
            if (tempList.size() < pre_page) {
                activity_reads_lv.setCanLoadMore(false);
            } else {
                activity_reads_lv.setCanLoadMore(true);
            }
        } else {
            if (adapter != null) {
                if (tempList.size() == 0) {
                    ToastUtil.show(ReadActivity.this, R.string.toast_end_load);
                    activity_reads_lv.onLoadMoreComplete();
                    activity_reads_lv.setCanLoadMore(false);
                } else {
                    adapter.loadData(tempList, 1);
                    adapter.notifyDataSetChanged();
                    activity_reads_lv.onLoadMoreComplete();
                }
            }
        }
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_reads);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        TopicAdapter topicAdapter = null;
        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                    .getAdapter();
            topicAdapter = (TopicAdapter) listAdapter.getWrappedAdapter();
        } else {
            topicAdapter = (TopicAdapter) parent.getAdapter();
        }
        Topic topic = (Topic) topicAdapter.getItem(position - 1);
        Intent intent = new Intent(this, ReadTopicActivity.class);
        intent.putExtra("topic", topic);
        intent.putExtra("id", topic.id);
        intent.putExtra("isOfficial", true);
        startActivity(intent);
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (tag == Constants.READ_LIST) {
            if (adapter == null) {
                setReadList(result);
            } else {
                loadData(JsonTools.getTopicList(result));
            }
        }
    }

}

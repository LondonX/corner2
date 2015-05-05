package com.ljmob.corner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ljmob.corner.adapter.TopicAdapter;
import com.ljmob.corner.entity.Topic;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.UrlStaticUtil;

/**
 * 关注帖子activity
 *
 * @author YANGBANG
 */
public class FocusActivity extends ActionBarActivity implements OnItemClickListener,
        OnResponseResult {
    private ListView activity_focus_lv;
    private TopicAdapter topicAdapter;

    private ServiceImpl serviceImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        serviceImpl = FactoryService.getIService(this);
        initView();
        initToolBar();
        serviceImpl.getQueryAttentionTopic(
                UrlStaticUtil.getQueryAttentionTopic(),
                Constants.QUERY_ATTENTION_TOPIC);
    }

    @Override
    protected void onRestart() {
        serviceImpl.getQueryAttentionTopic(
                UrlStaticUtil.getQueryAttentionTopic(),
                Constants.QUERY_ATTENTION_TOPIC);
        super.onRestart();
    }

    private void initView() {
        activity_focus_lv = (ListView) this
                .findViewById(R.id.activity_focus_lv);
        activity_focus_lv.setOnItemClickListener(this);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_focus);
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
        if (tag == Constants.QUERY_ATTENTION_TOPIC) {
            topicAdapter = new TopicAdapter(this,
                    JsonTools.getTopicList(result), TopicAdapter.TOPIC_FOCUS);
            activity_focus_lv.setAdapter(topicAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TopicAdapter adapter = (TopicAdapter) parent.getAdapter();
        Topic topic = (Topic) adapter.getItem(position);
        Intent intent = new Intent(this, ReadTopicActivity.class);
        intent.putExtra("id", topic.id);
        if (topic.po_type.equals("bulletin")) {
            intent.putExtra("isOfficial", true);
        }
        startActivity(intent);
    }

}

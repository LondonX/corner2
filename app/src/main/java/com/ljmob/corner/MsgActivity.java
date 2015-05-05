package com.ljmob.corner;

import java.util.List;

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

import com.ljmob.corner.adapter.MessageCentreAdapter;
import com.ljmob.corner.entity.Favorable;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.CustomListView;
import com.ljmob.corner.view.CustomListView.OnRefreshListener;

public class MsgActivity extends ActionBarActivity implements OnItemClickListener,
        OnResponseResult {
    private CustomListView activity_msg_lv;
    private ServiceImpl serviceImpl;
    private MessageCentreAdapter messageCentreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        initToolBar();
        initViews();
        serviceImpl = FactoryService.getIService(this);
        getMessageCentre();
    }

    private void getMessageCentre() {
        serviceImpl.getMessageCentre(UrlStaticUtil.getMessageCentre(),
                Constants.MESSAGE_CENTRE);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(R.string.activity_msg);
    }

    private void initViews() {
        activity_msg_lv = (CustomListView) this
                .findViewById(R.id.activity_msg_lv);
        activity_msg_lv.setCanLoadMore(false);
        activity_msg_lv.setOnItemClickListener(this);
        activity_msg_lv.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                getMessageCentre();
            }
        });
    }

    private void LoadData(List<Favorable> tempList) {
        if (messageCentreAdapter != null && tempList != null) {
            messageCentreAdapter.notifyDataSetChanged();
            this.activity_msg_lv.onRefreshComplete();
        } else {
            this.activity_msg_lv.onRefreshComplete();
            ToastUtil.show(this, R.string.toast_error_refresh);
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
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        MessageCentreAdapter adapter = null;
        if (parent.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) parent
                    .getAdapter();
            adapter = (MessageCentreAdapter) listAdapter.getWrappedAdapter();
        } else {
            adapter = (MessageCentreAdapter) parent.getAdapter();
        }
        Favorable favorable = (Favorable) adapter.getItem(position - 1);
        Intent intent = new Intent(MsgActivity.this, FavorDetailActivity.class);
        intent.putExtra("id", favorable.coupon_id);
        startActivity(intent);
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.MESSAGE_CENTRE) {
                if (messageCentreAdapter == null) {
                    messageCentreAdapter = new MessageCentreAdapter(this,
                            JsonTools.getFavorableList(result));
                    this.activity_msg_lv.setAdapter(messageCentreAdapter);
                } else {
                    LoadData(JsonTools.getFavorableList(result));
                }
            }
        }
    }

}

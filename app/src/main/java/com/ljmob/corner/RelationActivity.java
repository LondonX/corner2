package com.ljmob.corner;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.corner.adapter.MainPagerAdapter;
import com.ljmob.corner.adapter.RelationAdapter;
import com.ljmob.corner.entity.Relation;
import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;

public class RelationActivity extends ActionBarActivity implements OnPageChangeListener,
        OnItemClickListener, OnResponseResult, OnClickListener {
    private ArrayList<TextView> tabs;
    private ArrayList<View> tabIndicators;
    private ArrayList<View> lvs;
    private ListView attentionListView;
    private ListView fansListView;
    private ViewPager activity_relation_pager;
    private RelationAdapter attentionAdapter;
    private RelationAdapter fansAdapter;
    private List<Relation> relationList = new ArrayList<Relation>();
    private MainPagerAdapter pagerAdapter;
    private ServiceImpl serviceImpl;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);
        application = (MyApplication) getApplication();
        initView();
        serviceImpl = FactoryService.getIService(this);
        initToolBar();
        // if (savedInstanceState == null) {
        // selectPage(0);
        // }
    }

    /**
     * 我关注的人
     */
    private void getMyAttention() {
        serviceImpl.getQueryMyAttention(UrlStaticUtil.getQueryMyAttention(),
                Constants.QUERY_MY_ATTENTION);
    }

    /**
     * 我的粉丝
     */
    private void getMyFans() {
        serviceImpl.getQueryMyFans(UrlStaticUtil.getQueryMyFans(),
                Constants.QUERY_MY_FANS);
    }

    private void initView() {
        activity_relation_pager = (ViewPager) findViewById(R.id.activity_relation_pager);

        lvs = new ArrayList<View>();
        attentionListView = new ListView(this);
        attentionListView.setOnItemClickListener(this);
        lvs.add(attentionListView);
        fansListView = new ListView(this);
        fansListView.setOnItemClickListener(this);
        lvs.add(fansListView);
        pagerAdapter = new MainPagerAdapter(lvs);
        activity_relation_pager.setAdapter(pagerAdapter);
        activity_relation_pager.setOnPageChangeListener(this);
        attentionListView.setOnItemClickListener(this);
        fansListView.setOnItemClickListener(this);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle(R.string.activity_relation);
        tabs = new ArrayList<>();
        tabIndicators = new ArrayList<>();
        TextView tab0 = (TextView) findViewById(R.id.activity_relation_tvAttent);
        TextView tab1 = (TextView) findViewById(R.id.activity_relation_tvFollower);
        tab0.setOnClickListener(this);
        tab1.setOnClickListener(this);
        tabs.add(tab0);
        tabs.add(tab1);
        tabIndicators.add(findViewById(R.id.activity_main_viewAttent));
        tabIndicators.add(findViewById(R.id.activity_main_viewFollower));
        if (getIntent().getBooleanExtra("isAttention", true)) {
            selectPage(0);
        } else {
            selectPage(1);
        }
    }

    private void selectPage(int index) {
        switch (index) {
            case 0:
                tabs.get(1).setEnabled(true);
                tabs.get(1).setAlpha(0.8f);
                tabIndicators.get(1).setVisibility(View.INVISIBLE);
                break;
            case 1:
                tabs.get(0).setEnabled(true);
                tabs.get(0).setAlpha(0.8f);
                tabIndicators.get(0).setVisibility(View.INVISIBLE);
                break;
        }
        tabs.get(index).setEnabled(false);
        tabs.get(index).setAlpha(1.0f);
        tabIndicators.get(index).setVisibility(View.VISIBLE);
        if (activity_relation_pager.getCurrentItem() != index) {
            activity_relation_pager.setCurrentItem(index);
        }
        setPageData(index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    private void setPageData(int index) {
        ListView lv = (ListView) lvs.get(index);
        if (lv.getAdapter() == null) {
            switch (index) {
                case 0:
                    getMyAttention();
                    break;
                case 1:
                    getMyFans();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        selectPage(arg0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        RelationAdapter relationAdapter = (RelationAdapter) parent.getAdapter();
        Relation relation = (Relation) relationAdapter.getItem(position);
        Intent intent = new Intent(this, OthersUserInfoActivity.class);
        intent.putExtra("user_id", relation.user_id);
        startActivity(intent);
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.QUERY_MY_ATTENTION) {
                if (attentionAdapter == null) {
                    attentionAdapter = new RelationAdapter(this,
                            JsonTools.getRelationList(result), 0);
                    attentionListView.setAdapter(attentionAdapter);
                } else {
                    attentionAdapter.refreshList(JsonTools
                            .getRelationList(result));
                }
            } else if (tag == Constants.QUERY_MY_FANS) {
                if (fansAdapter == null) {
                    fansAdapter = new RelationAdapter(this,
                            JsonTools.getRelationList(result), 1);
                    fansListView.setAdapter(fansAdapter);
                } else {
                    fansAdapter.refreshList(JsonTools.getRelationList(result));
                }
                application.isUpdataUserInfo = true;
            } else if (tag == Constants.ADD_USER_ATTENTION) {
                serviceImpl.getDialog().setCanShowDialog(false);
                getMyAttention();
                ToastUtil.show(this, R.string.toast_str_focused);
                application.isUpdataUserInfo = true;
            } else if (tag == Constants.REMOVE_USER_ATTENTION) {
                serviceImpl.getDialog().setCanShowDialog(false);
                getMyAttention();
                ToastUtil.show(this, R.string.toast_str_unfocused);
                application.isUpdataUserInfo = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_relation_tvAttent:
                selectPage(0);
                break;
            case R.id.activity_relation_tvFollower:
                selectPage(1);
                break;

            default:
                break;
        }
    }
}

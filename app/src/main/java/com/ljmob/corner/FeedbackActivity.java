package com.ljmob.corner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.ljmob.corner.net.service.FactoryService;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;

/**
 * 意见反馈
 *
 * @author YANGBANG
 */
public class FeedbackActivity extends ActionBarActivity implements OnClickListener,
        OnResponseResult {
    private EditText activity_feedback_tvContact;
    private EditText activity_feedback_tvFeedback;
    private ServiceImpl serviceImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initToolBar();
        initView();
        serviceImpl = FactoryService.getIService(this);
    }

    private void getFeedBack() {
        String contact = activity_feedback_tvContact.getText().toString();
        String content = activity_feedback_tvFeedback.getText().toString();
        if (contact.equals("") || content.equals("")) {
            ToastUtil.show(this, R.string.toast_error_nullfeedback);
        } else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("contact", contact);
            params.put("content", content);
            serviceImpl.getFeedBack(UrlStaticUtil.getFeedBack(), params,
                    new HashMap<String, File>(), Constants.FEED_BACK);
        }

    }

    private void initView() {
        activity_feedback_tvContact = (EditText) this
                .findViewById(R.id.activity_feedback_tvContact);
        activity_feedback_tvFeedback = (EditText) this
                .findViewById(R.id.activity_feedback_tvFeedback);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.activity_feedback);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.menu_ok_ok) {
            getFeedBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (status == 200 || status == 201) {
            if (tag == Constants.FEED_BACK) {
                ToastUtil.show(this, R.string.toast_ok_feedback);
                activity_feedback_tvContact.setText("");
                activity_feedback_tvFeedback.setText("");
            }
        }
    }

}

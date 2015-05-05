package com.lanjing.galleryView;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lanjing.galleryView.PhotoViewAttacher.OnPhotoTapListener;
import com.lanjing.galleryView.adapter.GalleryPagerAdapter;
import com.lanjing.galleryView.view.HackyViewPager;
import com.ljmob.corner.R;

public class ImageGalleryActivity extends ActionBarActivity implements
        OnPageChangeListener, OnPhotoTapListener {
    public static final String EXTRA_URLS = "EXTRA_URLS";
    public static final String EXTRA_DESCS = "EXTRA_DESCS";
    public static final String EXTRA_INDEX = "EXTRA_INDEX";
    private ViewPager pager;
    private ScrollView sv;
    private TextView tvDesc;
    private ArrayList<String> urls;
    private ArrayList<String> desc;
    private int currentIndex;
    private GalleryPagerAdapter adapter;
    // private List<TripItem> tripItemsList;
    private float dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        dp = getResources().getDisplayMetrics().widthPixels / 320f;
        sv = (ScrollView) findViewById(R.id.activity_gallery_sv);
        tvDesc = (TextView) findViewById(R.id.activity_gallery_sv_tvDesc);
        pager = (HackyViewPager) findViewById(R.id.activity_gallery_pager);
        urls = getIntent().getStringArrayListExtra(EXTRA_URLS);
        desc = getIntent().getStringArrayListExtra(EXTRA_DESCS);
        currentIndex = getIntent().getIntExtra(EXTRA_INDEX, 0);
        initToolBar();
        adapter = new GalleryPagerAdapter(this, urls, this);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
        pager.setCurrentItem(currentIndex);
        setDesciption(0);
    }

    private void initToolBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_root));
        ActionBar ab = getSupportActionBar();
        ab.setTitle(getString(R.string.activity_gallery) + " "
                + (currentIndex + 1) + "/" + urls.size());
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDesciption(int i) {
        if (desc != null && desc.get(i) != null
                && desc.get(i).equals("") == false) {
            sv.setVisibility(View.VISIBLE);
            tvDesc.setText(desc.get(i));
            tvDesc.setTextColor(Color.WHITE);
            tvDesc.setPadding((int) (12 * dp), 0, (int) (12 * dp), 0);
        } else {
            sv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        getSupportActionBar().setTitle(
                getString(R.string.activity_gallery) + " " + (arg0 + 1) + "/"
                        + urls.size());
        setDesciption(arg0);
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        if (getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
            if (tvDesc.getText() != null && tvDesc.getText().length() != 0) {
                sv.setVisibility(View.GONE);
            } else {
                sv.setVisibility(View.GONE);
            }
        } else {
            getSupportActionBar().show();
            if (tvDesc.getText() != null && tvDesc.getText().length() != 0) {
                sv.setVisibility(View.VISIBLE);
            } else {
                sv.setVisibility(View.GONE);
            }
        }
    }
}

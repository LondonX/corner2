package com.ljmob.corner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ljmob.corner.actionprovider.MyShareActionProvider;
import com.ljmob.corner.cache.ImageLoader;
import com.ljmob.corner.entity.Shareable;
import com.ljmob.corner.entity.ShopComment;
import com.ljmob.corner.net.service.ServiceImpl;
import com.ljmob.corner.net.service.ServiceImpl.OnResponseResult;
import com.ljmob.corner.util.Constants;
import com.ljmob.corner.util.JsonTools;
import com.ljmob.corner.util.MyApplication;
import com.ljmob.corner.util.SelectLoginWay;
import com.ljmob.corner.util.SoftWareDetectionUtil;
import com.ljmob.corner.util.ToastUtil;
import com.ljmob.corner.util.UrlStaticUtil;
import com.ljmob.corner.view.ShopCommentView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopActivity extends ActionBarActivity implements OnClickListener,
        OnResponseResult {
    private int id;
    private ScrollView activity_shop_root;
    private LinearLayout activity_shop_linearLocation;
    private LinearLayout activity_shop_linearPhone;
    private LinearLayout activity_shop_linearRoot;
    private ImageView activity_shop_imgShop;// 图片
    private TextView activity_shop_tvShop;// 商店
    private RatingBar activity_shop_rbRate;// 星级条
    private TextView activity_shop_tvAvg;// 平均
    private TextView activity_shop_tvDetails;// 说明
    private ImageView activity_shop_imgLocation;// 定位按钮
    private TextView activity_shop_tvLocation;// 位置
    private ImageView activity_shop_imgPhone;// 电话按钮
    private TextView activity_shop_tvPhone;// 电话号码
    private View activity_shop_gap0;// 阴影View
    private LinearLayout activity_shop_linearFavorable;// 优惠信息linear
    private TextView activity_shop_textFavorable;// 优惠信息tv
    private TextView activity_shop_tvCount;// 总评论数
    private LinearLayout activity_shop_linearHeadOC;// 更多评论linear
    private LinearLayout activity_shop_linearOtherComment;// 其他评论linear
    private TextView activity_shop_tvMoreShop;// 查看更多分店

    private LinearLayout activity_shop_linearMyComment;//
    private ImageView activity_shop_imgMyHead;// 用户本人头像
    private TextView activity_shop_tvMyName;// 用户名称
    private RatingBar activity_shop_rbMyRate;// 评分
    private TextView activity_shop_tvUpload;// 上传图片
    private EditText activity_shop_etMyComment;// 评论内容
    private TextView activity_shop_btnCommit;// 评论发布按钮
    private ServiceImpl serviceImpl;
    private ImageLoader imageLoader;
    private List<ShopComment> shopCommentList = new ArrayList<ShopComment>();
    private String max_logo_url;
    private MyApplication application;

    private MenuItem menu_collect;
    private MenuItem menu_Cancel_collect;
    private MyShareActionProvider shareActionProvider;
    private boolean canShare = false;
    private String shopImgUrl;
    private JSONObject merchant;
    private Shareable shareable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        application = (MyApplication) getApplication();
        imageLoader = new ImageLoader(this);
        initToolBar();
        initViews();
        id = getIntent().getIntExtra("id", -1);
        serviceImpl = new ServiceImpl(this);
        serviceImpl.getShopDetail(UrlStaticUtil.getShopDetail(id),
                Constants.SHOP_DETAIL);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUserInfo();
    }

    private void initToolBar() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_root);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.activity_shop);
    }

    private void initViews() {
        activity_shop_root = (ScrollView) this
                .findViewById(R.id.activity_shop_svRoot);
        activity_shop_linearLocation = (LinearLayout) this
                .findViewById(R.id.activity_shop_linearLocation);
        activity_shop_linearPhone = (LinearLayout) this
                .findViewById(R.id.activity_shop_linearPhone);
        activity_shop_linearRoot = (LinearLayout) this
                .findViewById(R.id.activity_shop_linearRoot);

        activity_shop_tvMoreShop = (TextView) this
                .findViewById(R.id.activity_shop_tvMoreShop);
        activity_shop_linearOtherComment = (LinearLayout) this
                .findViewById(R.id.activity_shop_linearOtherComment);
        activity_shop_linearHeadOC = (LinearLayout) this
                .findViewById(R.id.activity_shop_linearHeadOC);
        activity_shop_imgShop = (ImageView) this
                .findViewById(R.id.activity_shop_imgShop);
        activity_shop_tvShop = (TextView) this
                .findViewById(R.id.activity_shop_tvShop);
        activity_shop_rbRate = (RatingBar) this
                .findViewById(R.id.activity_shop_rbRate);
        activity_shop_tvAvg = (TextView) this
                .findViewById(R.id.activity_shop_tvAvg);
        // 暂时GONE掉平均消费
        activity_shop_tvAvg.setVisibility(View.GONE);
        activity_shop_tvDetails = (TextView) this
                .findViewById(R.id.activity_shop_tvDetails);
        activity_shop_imgLocation = (ImageView) this
                .findViewById(R.id.activity_shop_imgLocation);
        activity_shop_tvLocation = (TextView) this
                .findViewById(R.id.activity_shop_tvLocation);
        activity_shop_imgPhone = (ImageView) this
                .findViewById(R.id.activity_shop_imgPhone);
        activity_shop_tvPhone = (TextView) this
                .findViewById(R.id.activity_shop_tvPhone);
        activity_shop_gap0 = this.findViewById(R.id.activity_shop_gap0);
        activity_shop_linearFavorable = (LinearLayout) this
                .findViewById(R.id.activity_shop_linearFavorable);
        activity_shop_textFavorable = (TextView) this
                .findViewById(R.id.activity_shop_textFavorable);
        activity_shop_tvCount = (TextView) this
                .findViewById(R.id.activity_shop_tvCount);

        activity_shop_linearMyComment = (LinearLayout) this
                .findViewById(R.id.activity_shop_linearMyComment);
        activity_shop_imgMyHead = (ImageView) this
                .findViewById(R.id.activity_shop_imgMyHead);
        activity_shop_tvMyName = (TextView) this
                .findViewById(R.id.activity_shop_tvMyName);
        activity_shop_rbMyRate = (RatingBar) this
                .findViewById(R.id.activity_shop_rbMyRate);
        activity_shop_tvUpload = (TextView) this
                .findViewById(R.id.activity_shop_tvUpload);
        activity_shop_etMyComment = (EditText) this
                .findViewById(R.id.activity_shop_etMyComment);
        activity_shop_btnCommit = (TextView) this
                .findViewById(R.id.activity_shop_btnCommit);
        activity_shop_tvUpload.setVisibility(View.GONE);
        activity_shop_linearLocation.setOnClickListener(this);
        activity_shop_linearPhone.setOnClickListener(this);
        activity_shop_btnCommit.setOnClickListener(this);
        activity_shop_tvMoreShop.setOnClickListener(this);

        activity_shop_linearFavorable.setOnClickListener(this);
        activity_shop_linearHeadOC.setOnClickListener(this);
        setUserInfo();
    }

    private void setUserInfo() {
        if (application.preferences.getString("token", "").length() > 0) {
            // activity_shop_linearMyComment.setVisibility(View.VISIBLE);
            imageLoader.DisplayImage(UrlStaticUtil.root_photo
                            + application.preferences.getString("avatar", ""),
                    activity_shop_imgMyHead, true, ImageLoader.L_PICTURE);
            activity_shop_tvMyName.setText(application.preferences.getString(
                    "nickname", ""));
        } else {
            // activity_shop_linearMyComment.setVisibility(View.GONE);
            activity_shop_tvMyName.setText("佚名");
        }
    }

    /**
     * 设置商户信息
     *
     * @param jsonString
     */
    private void setShopInfo(String jsonString) {
        String shopName = "";
        String location = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (menu_collect != null && menu_Cancel_collect != null) {
                if (jsonObject.getInt("is_favorite") == 0) {
                    menu_collect.setVisible(true);
                    menu_Cancel_collect.setVisible(false);
                } else if (jsonObject.getInt("is_favorite") == 1) {
                    menu_collect.setVisible(false);
                    menu_Cancel_collect.setVisible(true);
                }
            }
            merchant = jsonObject.getJSONObject("merchant");
            shopImgUrl = UrlStaticUtil.root_photo
                    + merchant.getString("logo_url");
            imageLoader.DisplayImage(shopImgUrl, activity_shop_imgShop, false,
                    ImageLoader.L_PICTURE);
            max_logo_url = merchant.getString("max_logo_url");
            shopName = merchant.getString("name");
            activity_shop_tvShop.setText(shopName);
            activity_shop_rbRate.setRating(merchant.getInt("score"));
            // activity_shop_tvAvg.setText("消费$:" + merchant.getInt("money"));
            // activity_shop_tvAvg.setText("消费HKD" + merchant.getInt("money")
            // + "/每人");
            if (merchant.getString("hours") != null
                    && !merchant.getString("hours").equals("")) {
                activity_shop_tvAvg.setVisibility(View.VISIBLE);
                activity_shop_tvAvg.setText("营业时间："
                        + merchant.getString("hours"));
            } else {
                activity_shop_tvAvg.setVisibility(View.GONE);
            }
            activity_shop_tvDetails.setText(merchant.getString("descption"));
            location = merchant.getString("address");
            activity_shop_tvLocation.setText(location);
            activity_shop_tvPhone.setText(merchant.getString("telephone"));
            JSONArray coupons = jsonObject.getJSONArray("coupons");
            for (int i = 0; i < coupons.length(); i++) {
                JSONObject item = coupons.getJSONObject(i);
                LinearLayout view = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.item_favorable, null);
                TextView linearitem_fav_tvContent = (TextView) view
                        .findViewById(R.id.linearitem_fav_tvContent);
                TextView linearitem_fav_tvType = (TextView) view
                        .findViewById(R.id.linearitem_fav_tvType);
                linearitem_fav_tvType.setText(item.getString("discount"));
                linearitem_fav_tvContent.setText(Html.fromHtml(item
                        .getString("content").replace("\r", "")
                        .replace("\n", "").replace("\t", "")
                        .replace("&nbsp;", "")));
                view.setTag(item.getInt("id"));
                view.setOnClickListener(new MyOnClickListener(item.getInt("id")));
                activity_shop_linearFavorable.addView(view);
            }
            if (coupons.length() == 0) {
                activity_shop_gap0.setVisibility(View.GONE);
                activity_shop_linearFavorable.setVisibility(View.GONE);
            }
            canShare = true;
            String shareText = getString(R.string.share_text0);
            shareText = shareText.replace("<SHOP_LOCATION>", location).replace(
                    "<SHOP_NAME>", shopName);
            shareable = new Shareable(this);
            shareable.setText(shareText);
            shareable.setImgUrl(shopImgUrl);
            shareActionProvider.setShareable(shareable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serviceImpl.getShopComment(UrlStaticUtil.getShopComment(id),
                Constants.SHOP_COMMENT);
    }

    /**
     * 设置评论数据
     *
     * @param jsonString
     */
    private void setShopComment(String jsonString) {
        try {
            JSONArray commentItem = new JSONArray(jsonString);
            shopCommentList = JsonTools.getShopCommentList(commentItem
                    .toString());
            activity_shop_tvCount.setText("(" + shopCommentList.size() + ")");
            if (shopCommentList.size() > 2) {
                shopCommentList = shopCommentList.subList(0, 2);
            }
            int childCount = activity_shop_linearOtherComment.getChildCount();
            if (childCount > 1) {
                activity_shop_linearOtherComment.removeViews(1, childCount - 1);
            }
            for (int i = 0; i < shopCommentList.size(); i++) {
                ShopCommentView shopCommentView = new ShopCommentView(this,
                        shopCommentList.get(i));
                activity_shop_linearOtherComment.addView(shopCommentView
                        .getView());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (shareable != null) {
            shareable.setShareableView(activity_shop_linearRoot);
        }
    }

    /**
     * 发表评论
     */
    private void releaseComment() {
        if (activity_shop_etMyComment.getText().toString().length() > 0) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("merchant_id", id);
            params.put("content", activity_shop_etMyComment.getText()
                    .toString());
            params.put("user_nickname", "Silence");
            params.put("score", (int) activity_shop_rbMyRate.getRating());
            params.put("photos", "");
            serviceImpl.getReleaseShopComment(
                    UrlStaticUtil.getReleaseShopComment(id), params, null,
                    Constants.RELEASE_SHOP_COMMENT);
        } else {
            ToastUtil.show(this, R.string.toast_error_comm);
        }
    }

    private class MyOnClickListener implements OnClickListener {
        int id;

        public MyOnClickListener(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            Intent favorIntent = new Intent(ShopActivity.this,
                    FavorDetailActivity.class);
            favorIntent.putExtra("id", this.id);
            favorIntent.putExtra("max_logo_url", max_logo_url);
            favorIntent.putExtra("title", activity_shop_tvShop.getText().toString());
            startActivity(favorIntent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_collect, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_share);
        shareActionProvider = (MyShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        menu_collect = menu.findItem(R.id.menu_collect);
        menu_Cancel_collect = menu.findItem(R.id.menu_collect_cancel);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.menu_collect) {
            if (new SelectLoginWay(this).isLogin()) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("merchant_id", id);
                Map<String, File> files = new HashMap<String, File>();
                serviceImpl.getAddMerchantCollect(
                        UrlStaticUtil.getAddMerchantCollect(), params, files,
                        Constants.ADD_MERCHANT_COLLECT);
            }
        } else if (item.getItemId() == R.id.menu_collect_cancel) {
            if (new SelectLoginWay(this).isLogin()) {
                serviceImpl.getRemoveMerchantCollect(
                        UrlStaticUtil.getRemoveMerchantCollect(id),
                        Constants.REMOVE_MERCHANT_COLLECT);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (merchant == null) {
            ToastUtil.show(this, R.string.toast_error_merchant);
            return;
        }
        switch (v.getId()) {
            case R.id.activity_shop_tvMoreShop:// 查看更多分店
                Intent intentMore = new Intent(this, FitShopActivity.class);
                try {
                    intentMore.putExtra("belong_mer",
                            merchant.getString("belong_mer"));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                startActivity(intentMore);
                break;
            case R.id.activity_shop_linearHeadOC:
                Intent moreCommentsIntent = new Intent(this,
                        MoreCommentsActivity.class);
                moreCommentsIntent.putExtra("id", id);
                startActivity(moreCommentsIntent);
                break;
            case R.id.activity_shop_linearFavorable:
                // Intent favorIntent = new Intent(this, FavorDetailActivity.class);
                // startActivity(favorIntent);
                break;
            case R.id.activity_shop_btnCommit:// 发布评论
                if (new SelectLoginWay(this).isLogin()) {
                    releaseComment();
                }
                break;
            case R.id.activity_shop_linearLocation:
                if (SoftWareDetectionUtil.getAppIn(ShopActivity.this)) {
                    Intent intent = null;
                    try {
                        intent = new Intent(
                                "android.intent.action.VIEW",
                                android.net.Uri.parse("androidamap://viewMap?sourceApplication="
                                        + SoftWareDetectionUtil
                                        .getApplicationName(ShopActivity.this)
                                        + "&poiname="
                                        + merchant.getString("address")
                                        + "&lat="
                                        + merchant.getString("latitude")
                                        + "&lon="
                                        + merchant.getString("longitude")
                                        + "&dev=0"));
                        intent.setPackage("com.autonavi.minimap");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                } else {
                    String url = null;
                    try {
                        url = "http://mo.amap.com/?dev=0&q="
                                + merchant.getString("latitude") + ","
                                + merchant.getString("longitude") + "&name="
                                + merchant.getString("address");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
                break;
            case R.id.activity_shop_linearPhone:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + activity_shop_tvPhone.getText().toString()));
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onResult(String result, int tag, int status) {
        if (tag == Constants.SHOP_DETAIL) {
            setShopInfo(result);
        } else if (tag == Constants.SHOP_COMMENT) {
            setShopComment(result);
        } else if (tag == Constants.RELEASE_SHOP_COMMENT) {
            activity_shop_etMyComment.setText("");
            activity_shop_rbMyRate.setRating(0);
            ToastUtil.show(this, R.string.toast_ok_commit);
            serviceImpl.getShopComment(UrlStaticUtil.getShopComment(id),
                    Constants.SHOP_COMMENT);
        } else if (tag == Constants.ADD_MERCHANT_COLLECT) {
            ToastUtil.show(this, R.string.toast_ok_collect);
            if (menu_collect != null && menu_Cancel_collect != null) {
                menu_collect.setVisible(false);
                menu_Cancel_collect.setVisible(true);
            }
        } else if (tag == Constants.REMOVE_MERCHANT_COLLECT) {
            ToastUtil.show(this, R.string.toast_cancel_collect);
            if (menu_collect != null && menu_Cancel_collect != null) {
                menu_collect.setVisible(true);
                menu_Cancel_collect.setVisible(false);
            }
        }
    }
}

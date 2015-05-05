package com.ljmob.corner.util;

public class UrlStaticUtil {
    // public static final String root_photo = "http://192.168.1.5:3000";
    // public static final String root_url = "http://192.168.1.5:3000/api/v1/";

    public static final String root_photo = "http://corner.lafina.cn";
    public static final String root_url = "http://corner.lafina.cn/api/v1/";

    /**
     * 热门优惠url
     *
     * @return url
     */
    public static String getHotDealsUrl(int page, int pre_page) {
        return root_url + "advertisements?page=" + page + "&pre_page="
                + pre_page;
    }

    /**
     * 取得见闻下帖子url
     *
     * @return
     */
    public static String getTopic(int page, int pre_page) {
        return root_url + "posts?page=" + page + "&pre_page=" + pre_page;
    }

    /**
     * 取得商户详情
     *
     * @param id 商户id
     * @return
     */
    public static String getShopDetail(int id) {
        return root_url + "merchant/" + id;
    }

    /**
     * 取得分类卡图url
     *
     * @return
     */
    public static String getCato() {
        return root_url + "kinds";
    }

    /**
     * 取得商户评论url
     *
     * @param id 商户id
     * @return
     */
    public static String getShopComment(int id) {
        return root_url + "merchant/" + id + "/comments";
    }

    /**
     * 发布评论url
     *
     * @param id 商户id
     * @return
     */
    public static String getReleaseShopComment(int id) {
        return root_url + "merchant/" + id + "/comments";
    }

    /**
     * 优惠券详情
     *
     * @param id
     * @return
     */
    public static String getCouponDetail(int id, int page, int pre_page) {
        return root_url + "coupons/" + id + "?page=" + page + "&pre_page="
                + pre_page;
    }

    /**
     * 公告贴列表
     *
     * @param page     页码
     * @param pre_page 每页显示几条
     * @return
     */
    public static String getReadList(int page, int pre_page) {
        return root_url + "posts/type?po_type=bulletin&page=" + page
                + "&pre_page=" + pre_page;
    }

    /**
     * 帖子内页（包括普通帖和公告贴）
     *
     * @param id 帖子id
     * @return
     */
    public static String getTopicDetail(int id, int page, int pre_page) {
        return root_url + "post/" + id + "?page=" + page + "&pre_page="
                + pre_page;
    }

    /**
     * 分类商铺
     *
     * @param type         大分类
     * @param belong_kinds 所属子类型，如包包、首饰、麻辣烫等
     * @param page
     * @param pre_page
     * @param my_lat       纬度
     * @param my_lng       经度
     * @return
     */
    public static String getSortShop(String type, String belong_kinds,
                                     int page, int pre_page, double my_lat, double my_lng) {
        return root_url + "merchants/type?" + type + "=" + belong_kinds
                + "&page=" + page + "&pre_page=" + pre_page + "&my_lat="
                + my_lat + "&my_lng=" + my_lng;
    }

    /**
     * 添加帖子
     *
     * @return
     */
    public static String getAddTopic() {
        return root_url + "posts";
    }

    /**
     * 回复帖子
     *
     * @param id 帖子id
     * @return
     */
    public static String getReplyTopic(int id) {
        return root_url + "post/" + id + "/comments";
    }

    /**
     * 点赞
     *
     * @return
     */
    public static String getClickPraise() {
        return root_url + "posts/click_praise";
    }

    /**
     * 离我最近
     *
     * @param belong_kinds 所属类型
     * @param belong_area  所属商圈
     * @return
     */
    public static String getNearSort(String belong_kinds, String belong_area,
                                     double my_lat, double my_lng, int page, int pre_page) {
        return root_url + "merchants/type?belong_kinds=" + belong_kinds
                + "&belong_area=" + belong_area + "&sort=distance" + "&my_lat="
                + my_lat + "&my_lng=" + my_lng + "&page=" + page + "&pre_page="
                + pre_page;
    }

    /**
     * 评价最好和最新发布
     *
     * @param belong_kinds 所属类型
     * @param belong_area  所属商圈
     * @param sort         如果是按评分排序,则传入值score,如果是最新发布传入值updated_at
     * @return
     */
    public static String getRestsSort(String belong_kinds, String belong_area,
                                      String sort, int page, int pre_page, double my_lat, double my_lng) {
        return root_url + "merchants/type?belong_kinds=" + belong_kinds
                + "&belong_area=" + belong_area + "&sort=" + sort + "&page="
                + page + "&pre_page=" + pre_page + "&my_lat=" + my_lat
                + "&my_lng=" + my_lng;
    }

    /**
     * 添加商户收藏
     *
     * @return
     */
    public static String getAddMerchantCollect() {
        return root_url + "auth/favorite/merchants";
    }

    /**
     * 移除商户收藏
     *
     * @param merchant_id 商户id
     * @return
     */
    public static String getRemoveMerchantCollect(int merchant_id) {
        return root_url + "auth/favorite/merchants?merchant_id=" + merchant_id;
    }

    /**
     * 查询商户收藏
     *
     * @return
     */
    public static String getQueryMerchantCollect() {
        return root_url + "auth/favorite/merchants";
    }

    /**
     * 添加优惠券收藏
     *
     * @return
     */
    public static String getAddCouponsCollect() {
        return root_url + "auth/favorite/coupons";
    }

    /**
     * 移除优惠券收藏
     *
     * @param coupon_id 优惠券id
     * @return
     */
    public static String getRemoveCouponsCollect(int coupon_id) {
        return root_url + "auth/favorite/coupons?coupon_id=" + coupon_id;
    }

    /**
     * 查询优惠券收藏
     *
     * @return
     */
    public static String getQueryCouponsCollect() {
        return root_url + "auth/favorite/coupons";
    }

    /**
     * 添加关注帖子
     *
     * @return
     */
    public static String getAddAttentionTopic() {
        return root_url + "auth/attentions";
    }

    /**
     * 移除关注帖子
     *
     * @return
     */
    public static String getRemoveAttentionTopic(int topic_id) {
        return root_url + "auth/attentions?post_id=" + topic_id;
    }

    /**
     * 查询关注的帖子
     *
     * @return
     */
    public static String getQueryAttentionTopic() {
        return root_url + "auth/attentions";
    }

    /**
     * 查询我的帖子
     *
     * @return
     */
    public static String getQueryMyTopic() {
        return root_url + "auth/posts";
    }

    /**
     * 查询我的回复
     *
     * @return
     */
    public static String getQueryMyReply() {
        return root_url + "auth/comments";
    }

    /**
     * 回复楼层
     *
     * @param post_id
     * @return
     */
    public static String getReplyFloor(int post_id) {
        return root_url + "post/" + post_id + "/comments";
    }

    /**
     * 取得帖子某一楼层
     *
     * @param post_id 帖子id
     * @param row     楼层
     * @return
     */
    public static String getTopicFloor(int post_id, int row) {
        return root_url + "post/" + post_id + "/comments/" + row;
    }

    /**
     * 取得意见反馈
     *
     * @return
     */
    public static String getFeedBack() {
        return root_url + "advices";
    }

    /**
     * 取得个人信息
     *
     * @return
     */
    public static String getUserInfo(int user_id) {
        return root_url + "auth?id=" + user_id;
    }

    /**
     * 签到
     *
     * @return
     */
    public static String getSignIn() {
        return root_url + "auth/report";
    }

    // /**
    // * 取得其他用户信息
    // *
    // * @param user_id
    // * 用户id
    // * @return
    // */
    // public static String getOtherUserInfo(int user_id) {
    // return root_url + "auth?user_id=" + user_id;
    // }

    /**
     * 取得其他用户帖子
     *
     * @param user_id 用户id
     * @return
     */
    public static String getOtherUserTpoic(int user_id) {
        return root_url + "auth/posts?id=" + user_id;
    }

    /**
     * 添加用户关注
     *
     * @return
     */
    public static String getAddUserAttention() {
        return root_url + "auth/follow";
    }

    /**
     * 移除用户关注
     *
     * @return
     */
    public static String getRemoveUserAttention(int user_id) {
        return root_url + "auth/follow?follow_userid=" + user_id;
    }

    /**
     * 查询我所有关注的人
     *
     * @return
     */
    public static String getQueryMyAttention() {
        return root_url + "auth/follow";
    }

    /**
     * 查询我的所有粉丝
     *
     * @return
     */
    public static String getQueryMyFans() {
        return root_url + "auth/fans";
    }

    /**
     * 搜索商户
     *
     * @return
     */
    public static String getSearchShop(String searchString, int page,
                                       int pre_page) {
        return root_url + "merchants/tag?condition=" + searchString + "&page="
                + page + "&pre_page=" + pre_page;
    }

    /**
     * 编辑用户信息
     *
     * @return
     */
    public static String getEditUserInfo() {
        return root_url + "auth/profile";
    }

    /**
     * 取得统计 统计用户登陆软件次数，系统版本等
     *
     * @return
     */
    public static String getStatisticses() {
        return root_url + "statisticses";
    }

    /**
     * 取得更多商户
     *
     * @param belong_mer 品牌名称
     * @param page       页码
     * @param pre_page   一页显示多少条
     * @return
     */
    public static String getMoreShop(String belong_mer, int page, int pre_page) {
        return root_url + "merchants/list?belong_mer=" + belong_mer + "&page="
                + page + "&pre_page=" + pre_page;
    }

    /**
     * 取得消息中心
     *
     * @return
     */
    public static String getMessageCentre() {
        return root_url + "auth/message";
    }

}

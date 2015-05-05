package com.ljmob.corner.net.service;

import java.io.File;
import java.util.Map;

public interface IService {
	/**
	 * 取得热门优惠
	 * 
	 * @param url
	 *            请求地址
	 * @param tag
	 *            唯一标识 Constant中取得
	 */
	public void getHotDeals(String url, int tag);

	public void getTopic(String url, int tag);

	public void getShopDetail(String url, int tag);

	public void getCato(String url, int tag);

	public void getShopComment(String url, int tag);

	public void getReleaseShopComment(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getCouponDetail(String url, int tag);

	public void getReadList(String url, int tag);

	public void getTopicDetail(String url, int tag);

	public void getSortShop(String url, int tag);

	public void getAddTopic(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getReplyTopic(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getClickPraise(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getNearSort(String url, int tag);

	public void getRestsSort(String url, int tag);

	public void getAddMerchantCollect(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getQueryMerchantCollect(String url, int tag);

	public void getAddCouponsCollect(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getQueryCouponsCollect(String url, int tag);

	public void getAddAttentionTopic(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getQueryAttentionTopic(String url, int tag);

	public void getRemoveAttentionTopic(String url, int tag);

	public void getRemoveMerchantCollect(String url, int tag);

	public void getRemoveCouponsCollect(String url, int tag);

	public void getQueryMyTopic(String url, int tag);

	public void getQueryMyReply(String url, int tag);

	public void getReplyFloor(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getTopicFloor(String url, int tag);

	public void getFeedBack(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getUserInfo(String url, int tag);

	public void getSignIn(String url, int tag);

	public void getOtherUserInfo(String url, int tag);

	public void getOtherUserTpoic(String url, int tag);

	public void getAddUserAttention(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getRemoveUserAttention(String url, int tag);

	public void getQueryMyAttention(String url, int tag);

	public void getQueryMyFans(String url, int tag);

	public void getSearchShop(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getEditUserInfo(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getStatisticses(String url, Map<String, Object> params,
			Map<String, File> files, int tag);

	public void getMoreShop(String url, int tag);

	public void getMessageCentre(String url, int tag);

}

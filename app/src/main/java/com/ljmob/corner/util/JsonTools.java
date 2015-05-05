package com.ljmob.corner.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.corner.entity.Bulletin;
import com.ljmob.corner.entity.Cato;
import com.ljmob.corner.entity.CollectFavorable;
import com.ljmob.corner.entity.Favorable;
import com.ljmob.corner.entity.FavorableShop;
import com.ljmob.corner.entity.Floor;
import com.ljmob.corner.entity.Poi;
import com.ljmob.corner.entity.Relation;
import com.ljmob.corner.entity.Reply;
import com.ljmob.corner.entity.Shop;
import com.ljmob.corner.entity.ShopComment;
import com.ljmob.corner.entity.Topic;
import com.ljmob.corner.entity.UserInfo;

public class JsonTools {

	/**
	 * 取得Poi兴趣点对象列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Poi> getPoiList(String jsonString) {
		List<Poi> list = new ArrayList<Poi>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Poi>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Poi>();
		}

	}

	/**
	 * 取得FavorableShop热门优惠对象列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<FavorableShop> getFavorableShopList(String jsonString) {
		List<FavorableShop> list = new ArrayList<FavorableShop>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<FavorableShop>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FavorableShop>();
		}

	}

	/**
	 * 取得Topic帖子列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Topic> getTopicList(String jsonString) {
		List<Topic> list = new ArrayList<Topic>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Topic>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Topic>();
		}
	}

	/**
	 * 取得Bulletin帖子列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Bulletin> getBulletinList(String jsonString) {
		List<Bulletin> list = new ArrayList<Bulletin>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Bulletin>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Bulletin>();
		}
	}

	/**
	 * 取得Cato分类
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Cato> getCatoList(String jsonString) {
		List<Cato> list = new ArrayList<Cato>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Cato>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Cato>();
		}
	}

	/**
	 * 取得ShopComment商户评论
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<ShopComment> getShopCommentList(String jsonString) {
		List<ShopComment> list = new ArrayList<ShopComment>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<ShopComment>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ShopComment>();
		}
	}

	/**
	 * 取得Shop商户列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Shop> getShopList(String jsonString) {
		List<Shop> list = new ArrayList<Shop>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Shop>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Shop>();
		}
	}

	/**
	 * 取得帖子Floor楼层列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Floor> getFloorList(String jsonString) {
		List<Floor> list = new ArrayList<Floor>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Floor>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Floor>();
		}
	}

	/**
	 * 取得帖子楼层实体
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Floor getFloor(String jsonString) {
		Gson gson = new Gson();
		Floor floor = new Floor();
		Type listType = new TypeToken<Floor>() {
		}.getType();
		try {
			floor = gson.fromJson(jsonString, listType);
			return floor;
		} catch (Exception e) {
			e.printStackTrace();
			return floor;
		}
	}

	/**
	 * 取得CollectFavorable优惠价列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<CollectFavorable> getCollectFavorableList(
			String jsonString) {
		List<CollectFavorable> list = new ArrayList<CollectFavorable>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<CollectFavorable>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<CollectFavorable>();
		}
	}

	/**
	 * 取得MyReply列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Reply> getReplyList(String jsonString) {
		List<Reply> list = new ArrayList<Reply>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Reply>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Reply>();
		}
	}

	/**
	 * 取得Relation关系粉丝列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Relation> getRelationList(String jsonString) {
		List<Relation> list = new ArrayList<Relation>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Relation>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Relation>();
		}
	}

	/**
	 * 取得消息中心优惠列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Favorable> getFavorableList(String jsonString) {
		List<Favorable> list = new ArrayList<Favorable>();
		Gson gson = new Gson();
		Type listType = new TypeToken<List<Favorable>>() {
		}.getType();
		try {
			list = gson.fromJson(jsonString, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Favorable>();
		}
	}

	/**
	 * 取得topic帖子
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Topic getTopic(String jsonString) {
		Topic topic = new Topic();
		Gson gson = new Gson();
		Type type = new TypeToken<Topic>() {
		}.getType();
		try {
			topic = gson.fromJson(jsonString, type);
			return topic;
		} catch (Exception e) {
			e.printStackTrace();
			return topic;
		}
	}

	/**
	 * 取得UserInfo个人信息
	 * 
	 * @param jsonString
	 * @return
	 */
	public static UserInfo getUserInfo(String jsonString) {
		UserInfo userInfo = new UserInfo();
		Gson gson = new Gson();
		Type type = new TypeToken<UserInfo>() {
		}.getType();
		try {
			userInfo = gson.fromJson(jsonString, type);
			return userInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return userInfo;
		}
	}
}

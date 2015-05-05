package com.ljmob.corner.entity;

import java.util.List;

/**
 * 商户评论实体类
 * 
 * @author YANGBANG
 * 
 */
public class ShopComment {
	public int id;
	public int row;
	public int score;
	public int user_id;// 用户id
	public int user_rank;// 星级
	public String user_nickname;// 用户呢称
	public String user_avatar;// 用户头像
	public String user_sex;
	public String content;// 评论内容
	public String comment_user_nickname;// 评论用户呢称
	public String comment_time;// 评论时间
	public List<Photo> photos;// 图片

	/**
	 * 图片
	 * 
	 * @author YANGBANG
	 * 
	 */
	public class Photo {
		public int id;
		public int position;
		public String img_url;
	}

}

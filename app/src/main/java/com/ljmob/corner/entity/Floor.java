package com.ljmob.corner.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子内页 楼层实体类
 * 
 * @author YANGBANG
 * 
 */
public class Floor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6941234897170809543L;
	public int id;
	public int user_id;
	public String user_avatar;// 头像
	public String user_nickname;
	public String user_sex;
	public String user_rank;
	public int row;// 楼数
	public String title;
	public String content;// 评论内容
	public int score;
	public int column;
	public String comment_user_nickname;// 被评论用户昵称
	public String comment_time;
	public List<Photo> photos;// 图片列表
	public List<ReplyFloor> comment_item;// 评论子项 对某层楼进行回复

	@Override
	public String toString() {
		return "content:" + content + ",comment_user_nickname:"
				+ comment_user_nickname + ",row:" + row;
	}

}

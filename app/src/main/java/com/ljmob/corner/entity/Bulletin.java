package com.ljmob.corner.entity;

/**
 * 公告贴实体类
 * 
 * @author YANGBANG
 * 
 */
public class Bulletin {
	public int id;
	public String subject;
	public String content;
	public int praise_num;
	public int comment_num;
	public String po_type;// 帖子类型
	public int is_update;
	public String last_comments_time;// 最后评论时间
	public int user_id;
	public String created_at;
	public String updated_at;
	public String status;
}

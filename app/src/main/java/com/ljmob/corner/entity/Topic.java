package com.ljmob.corner.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子实体类（包括见闻、见闻更多阅读、个人中心主题）
 * 
 * @author YANGBANG
 * 
 */
public class Topic implements Serializable {
	private static final long serialVersionUID = 0x2df91985;
	public int index;// 下标
	public int id;
	public int user_id;// 用户id
	public String user_avatar;// 头像
	public String user_nickname;
	public String user_sex;
	public int user_rank;// 等级
	public String time;
	public String subject;
	public String content;
	public List<Photo> photos;// 图片
	public int praise_num;// 赞数
	public int comment_num;// 评论数
	public int is_praise;// 是否赞过
	public boolean isComment;// 是否评论过
	public boolean isFocus;// 是否关注
	public String po_type;// 帖子类型
	public String praise_user;// 赞过的用户
	public int is_update;
	public String last_comments_time;// 最后评论时间
	public String create_time;// 创建时间
	public String updated_at;// 更新时间
	public String status;// 状态

}

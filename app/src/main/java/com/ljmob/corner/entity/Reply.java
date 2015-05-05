package com.ljmob.corner.entity;

import java.util.List;

/**
 * 个人中心回复项
 * 
 * @author YANGBANG
 * 
 */
public class Reply {
	public int post_id;// 原帖id
	public String post_subject;// 原帖主题
	public String po_type;// 帖子类型 bulletin为公告贴、general普通帖
	public List<ReplyItem> item;// 评论项

}

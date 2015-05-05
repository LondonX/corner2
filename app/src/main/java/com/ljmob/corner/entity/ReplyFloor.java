package com.ljmob.corner.entity;

import java.io.Serializable;

/**
 * 回复楼层实体类
 * 
 * @author YANGBANG
 * 
 */
public class ReplyFloor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2517151087826470491L;
	public int id;
	public int comment_user_id;
	public String user_nickname;
	public String comment_user_nickname;
	public String content;
	public String comment_time;
}

package com.ljmob.corner.entity;

/**
 * 关系实体类，关注与被关注的人
 * 
 * @author YANGBANG
 * 
 */
public class Relation {
	public int user_id;
	public String avatar;
	public String max_avatar;
	public String nickname;
	public String sex;
	public int rank;// 等级
	public String signature;// 签名
	public boolean isAttention;// true为关注、false为粉丝
}

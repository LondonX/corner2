package com.ljmob.corner.entity;

/**
 * 用户实体类
 * 
 * @author YANGBANG
 * 
 */
public class UserInfo {
	public int is_attention;// 是否关注0代表没关注、1代表关注
	public int is_self;// 是否为本人 0代表不是本人、1代表本人
	public int progress;// 当前经验值
	public int max_progress;// 最大经验值
	public Info user_info;// 用户信息

	public class Info {
		public int id;
		public String nickname;
		public String status;
		public String avatar;
		public String max_avatar;
		public String sex;
		public int rank;// 等级
		public int progress;// 经验
		public int follow_num;// 我关注人的数量
		public int fans_num;// 粉丝数量
		public int new_fans_num;// 新增粉丝数量
		public int is_report;
		public int posts_num;
		public String signature;// 签名
	}

}

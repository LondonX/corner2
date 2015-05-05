package com.ljmob.corner.entity;

import java.io.Serializable;

/**
 * 主页热门优惠实体类
 * 
 * @author YANGBANG
 * 
 */
public class FavorableShop implements Serializable {
	private static final long serialVersionUID = 0x2D3057A9;
	// public int id;
	// public String pic_url;
	// public String name;
	// public String details;
	// public String discount;

	public int id;
	public String photo;
	public String discount;// 折扣
	public String subject;// 标题
	public String description;// 描述
}

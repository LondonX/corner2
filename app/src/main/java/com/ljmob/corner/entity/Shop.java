package com.ljmob.corner.entity;

import java.io.Serializable;

/**
 * 商户类
 * 
 * @author YANGBANG
 * 
 */
public class Shop implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String logo_url;
	public String max_logo_url;
	public String name;
	public int score;// 星级
	public int money;
	public String info;
	public String descption;// 描述
	public String activity_type;//
	public String address;// 位置
	public String telephone;// 电话
	public String hours;// 营业时间
	public String longitude;// 经度
	public String latitude;// 纬度
	public String longitude_amap;// 经度
	public String latitude_amap;// 纬度
//	public String belong_area;// 标签1
	public String belong_kinds;// 标签2
	public String district;// 区域
	public String belong_mer;// 品牌名称

	public String expend;// 最低消费
	public String area;// 区域
	public String product_name;// 商品名称
	public int distance;// 距离m
}

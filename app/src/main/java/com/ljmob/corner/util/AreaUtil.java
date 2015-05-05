package com.ljmob.corner.util;

import java.util.ArrayList;
import java.util.List;

import com.ljmob.corner.entity.KeyWord;
import com.ljmob.corner.entity.Poi;

public class AreaUtil {
	List<Poi> areaList = new ArrayList<Poi>();
	List<Poi> gwmsList = new ArrayList<Poi>();
	String[][] xg = {
			{ "中环", "湾仔", "铜锣湾", "西湾河", "北角", "上环", "赤柱", "山顶", "金钟", "西营盘",
					"筲箕湾", "大坑", "小西湾", "浅水湾", "数码港", "跑马地", "炮台山", "香港仔",
					"西环", "薄扶林", "天后", "半山", "鸭脷洲", "鰂鱼涌", "太古", "柴湾" },
			{ "尖沙咀", "旺角", "黄大仙", "大坑东", "石硖尾", "油麻地", "西隧", "深水埗", "九龙湾",
					"土瓜湾", "红磡", "黄埔", "慈云山", "钻石山", "启德", "牛池湾", "大角咀", "佐敦",
					"长沙湾", "鲤鱼门", "太子", "观塘", "油塘", "茶果岭", "蓝田", "飞鹅山", "秀茂坪",
					"牛头角", "九龙城", "荔枝角", "九龙塘", "美孚" },
			{ "荃湾", "沙田", "屯门", "元朗", "马湾", "赤鱲角", "大围", "南丫岛", "东涌", "西贡",
					"长洲", "天水围", "葵涌", "上水", "粉岭", "大埔", "清水湾", "将军澳", "大屿山",
					"葵青", "青衣", "马鞍山" } };

	String[][] gwms = {
			{ "包包", "护肤品", "服装", "鞋子", "手表", "数码", "珠宝", "综合", "便利店", "家居",
					"母婴", "医药", "更多" },
			{ "茶餐厅", "西餐", "海鲜", "日式", "面包甜点", "咖啡厅", "酒吧", "自助餐", "火锅", "烧烤",
					"其他" } };

	public AreaUtil() {
		initData();
	}

	/**
	 * 取得香港18区
	 * 
	 * @return 区域列表
	 */
	public List<Poi> getAreaList() {
		return areaList;
	}

	public List<Poi> getGwmsList() {
		return gwmsList;
	}

	public void initData() {
		for (int i = 0; i < xg.length; i++) {
			Poi poi0 = new Poi();
			poi0.item = new ArrayList<KeyWord>();
			switch (i) {
			case 0:
				poi0.type = "香港岛";
				break;
			case 1:
				poi0.type = "九龙";
				break;
			case 2:
				poi0.type = "新界及离岛";
				break;

			default:
				break;
			}
			for (int j = 0; j < xg[i].length; j++) {
				KeyWord keyWord = new KeyWord();
				keyWord.name = xg[i][j];
				keyWord.keyword = xg[i][j];
				poi0.item.add(keyWord);
			}
			areaList.add(poi0);
		}

		for (int i = 0; i < gwms.length; i++) {
			Poi poi0 = new Poi();
			poi0.item = new ArrayList<KeyWord>();
			switch (i) {
			case 0:
				poi0.type = "购物";
				break;
			case 1:
				poi0.type = "美食";
				break;
			default:
				break;
			}
			for (int j = 0; j < gwms[i].length; j++) {
				KeyWord keyWord = new KeyWord();
				keyWord.name = gwms[i][j];
				keyWord.keyword = gwms[i][j];
				poi0.item.add(keyWord);
			}
			gwmsList.add(poi0);
		}

	}
}

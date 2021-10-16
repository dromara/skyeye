/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class AssetArticlesUseEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
    	TITLE("title", "{\"name\": \"标题\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"12\"}"),
    	ODD_NUMBER("oddNumber", "{\"name\": \"单号\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	USER_NAME("userName", "{\"name\": \"责任人\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	GOODS("goods", "{\"name\": \"用品\", \"orderBy\": \"4\", \"showType\": \"5\", \"proportion\": \"12\"}"),
    	REMARK("remark", "{\"name\": \"相关描述\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"相关附件\", \"orderBy\": \"6\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
        private String nameCode;
        private String str;
		
        Entity(String nameCode, String str){
            this.nameCode = nameCode;
            this.str = str;
        }
	
		public static Map<String, Object> getClockInState(Map<String, Object> map){
        	Map<String, Object> bean = new HashMap<>();
            for (Entity q : Entity.values()){
				Map<String, Object> jObject = JSONUtil.toBean(q.getStr(), null);
            	jObject.put("value", map.containsKey(q.getNameCode()) ? map.get(q.getNameCode()) : "");
            	if("5".equals(jObject.get("showType").toString())){//展示为表格,特殊处理
            		jObject.put("headerTitle", "[{ \"field\": \"typeName\", \"title\": \"用品类别\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"articleName\", \"title\": \"用品名称\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"specificationsName\", \"title\": \"规格(单位)\", \"align\": \"left\", \"width\": 200},"
            				+ "{ \"field\": \"applyUseNum\", \"title\": \"领用数量\", \"align\": \"left\", \"width\": 140},"
            				+ "{ \"field\": \"actualUseNum\", \"title\": \"实发数量\", \"align\": \"left\", \"width\": 140},"
            				+ "{ \"field\": \"stateName\", \"title\": \"状态\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"remark\", \"title\": \"备注\", \"align\": \"left\", \"width\": 150}]");
            	}
            	bean.put(q.getNameCode(), jObject);
            }
            return bean;
        }
        
        public String getNameCode() {
			return nameCode;
		}

		public String getStr() {
			return str;
		}
    }
	
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: ProFileEntity
 * @Description: 项目文档审批对象类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/10 21:17
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProFileEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
		title("title", "{\"name\": \"名称\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		typeName("typeName", "{\"name\": \"所属分类\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		projectName("projectName", "{\"name\": \"所属项目\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		createName("userName", "{\"name\": \"创建人\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		content("content", "{\"name\": \"内容\", \"orderBy\": \"5\", \"showType\": \"3\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"附件\", \"orderBy\": \"6\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
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

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ProWorkloadEntity
 * @Description: 项目工作量提交到工作流的实体类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/8 13:18
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProWorkloadEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
    	TITLE("title", "{\"name\": \"主题\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"12\"}"),
    	WRITEPEOPLE("userName", "{\"name\": \"填报人\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	WORKLOADTYPE("workLoadType", "{\"name\": \"日期类型\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	PROJECTNAME("projectName", "{\"name\": \"所属项目\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	PROJECTNUMBER("projectNumber", "{\"name\": \"项目编号\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	TASKS("tasks", "{\"name\": \"任务\", \"orderBy\": \"6\", \"showType\": \"5\", \"proportion\": \"12\"}"),
    	ALLWORKLOAD("allworkload", "{\"name\": \"合计\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"12\"}"),
    	REMARK("remark", "{\"name\": \"附加描述\", \"orderBy\": \"8\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"附件\", \"orderBy\": \"9\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
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
            		List<Map<String, Object>> items = map.containsKey(q.getNameCode()) ? (ArrayList<Map<String, Object>>) map.get(q.getNameCode()) : new ArrayList<>();
            		Map<String, Object> item = items.get(0);
            		jObject.put("headerTitle", "[{ \"field\": \"taskContent\", \"title\": \"填报人相关任务\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"executePeople\", \"title\": \"任务执行人\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"monAmount\", \"title\": \"周一(" + item.get("monTime").toString() + ")\", \"align\": \"left\", \"width\": 200},"
            				+ "{ \"field\": \"tuesAmount\", \"title\": \"周二(" + item.get("tuesTime").toString() + ")\", \"align\": \"left\", \"width\": 200},"
            				+ "{ \"field\": \"wedAmount\", \"title\": \"周三(" + item.get("wedTime").toString() + ")\", \"align\": \"left\", \"width\": 200},"
            				+ "{ \"field\": \"thurAmount\", \"title\": \"周四(" + item.get("thurTime").toString() + ")\", \"align\": \"left\", \"width\": 200},"
            				+ "{ \"field\": \"friAmount\", \"title\": \"周五(" + item.get("friTime").toString() + ")\", \"align\": \"left\", \"width\": 200},"
            				+ "{ \"field\": \"satuAmount\", \"title\": \"周六(" + item.get("satuTime").toString() + ")\", \"align\": \"left\", \"width\": 200},"
            				+ "{ \"field\": \"sunAmount\", \"title\": \"周日(" + item.get("sunTime").toString() + ")\", \"align\": \"left\", \"width\": 200},"
            				+ "{ \"field\": \"allWorkLoad\", \"title\": \"小计\", \"align\": \"left\", \"width\": 70}]");
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

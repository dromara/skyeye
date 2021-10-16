/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: ProCostExpenseEntity
 * @Description: 项目费用报销对象类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/11 22:41
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProCostExpenseEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
    	TITLE("title", "{\"name\": \"主题\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"12\"}"),
    	WRITEPEOPLE("userName", "{\"name\": \"报销人\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	WORKLOADTYPE("departmentName", "{\"name\": \"所属部门\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	PROJECTNAME("projectName", "{\"name\": \"所属项目\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	PROJECTNUMBER("reimbursementTime", "{\"name\": \"报销日期\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	TASKS("purposes", "{\"name\": \"任务\", \"orderBy\": \"6\", \"showType\": \"5\", \"proportion\": \"12\"}"),
    	ALLWORKLOAD("allPrice", "{\"name\": \"合计\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"附件\", \"orderBy\": \"8\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
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
            		jObject.put("headerTitle", "[{ \"field\": \"expenseTypeId\", \"title\": \"支出分类\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"purposeContent\", \"title\": \"用途说明\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"price\", \"title\": \"费用金额\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"opposUnit\", \"title\": \"对方单位\", \"align\": \"left\", \"width\": 150},"
            				+ "{ \"field\": \"experiencedPerson\", \"title\": \"经手人\", \"align\": \"left\", \"width\": 150}]");
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

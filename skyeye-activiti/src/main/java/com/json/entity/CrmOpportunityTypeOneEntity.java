/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: CrmOpportunityTypeOneEntity
 * @Description: 商机审核一阶段对象类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 16:53
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class CrmOpportunityTypeOneEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
		CUSTOMERID("customerId", "{\"name\": \"客户名称\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		INDUSTRYID("industryId", "{\"name\": \"所属行业\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CITY("city", "{\"name\": \"所在城市\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		DETAILADDRESS("detailAddress", "{\"name\": \"详细地址\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		TITLE("title", "{\"name\": \"商机名称\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		FROMID("fromId", "{\"name\": \"商机来源\", \"orderBy\": \"6\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		ESTIMATEPRICE("estimatePrice", "{\"name\": \"预成交金\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		ESTIMATEENDTIME("estimateEndTime", "{\"name\": \"预结单日\", \"orderBy\": \"8\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CONTACTS("contacts", "{\"name\": \"联系人\", \"orderBy\": \"9\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		DEPARTMENT("department", "{\"name\": \"部门\", \"orderBy\": \"10\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		JOB("job", "{\"name\": \"职务\", \"orderBy\": \"11\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		WORKPHONE("workPhone", "{\"name\": \"办公电话\", \"orderBy\": \"12\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		MOBILEPHONE("mobilePhone", "{\"name\": \"移动电话\", \"orderBy\": \"13\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		EMAIL("email", "{\"name\": \"邮件\", \"orderBy\": \"14\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		QQ("qq", "{\"name\": \"QQ\", \"orderBy\": \"15\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		BUSINESSNEED("businessNeed", "{\"name\": \"主要业务\", \"orderBy\": \"16\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"附件资料\", \"orderBy\": \"17\", \"showType\": \"2\", \"proportion\": \"12\"}"),
		SUBDEPARTMENTS("subDepartments", "{\"name\": \"所属部门\", \"orderBy\": \"18\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		RESPONSID("responsId", "{\"name\": \"负责人\", \"orderBy\": \"19\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		PARTID("partId", "{\"name\": \"参与人\", \"orderBy\": \"20\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		FOLLOWID("followId", "{\"name\": \"关注人\", \"orderBy\": \"21\", \"showType\": \"1\", \"proportion\": \"6\"}");
		
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

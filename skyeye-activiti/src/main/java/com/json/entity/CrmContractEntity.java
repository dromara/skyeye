/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: CrmContractEntity
 * @Description: 客户合同提交到工作流中的对象
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 15:43
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class CrmContractEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
		CUSTOMERID("customerId", "{\"name\": \"客户\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CITY("city", "{\"name\": \"所在城市\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		DETAILADDRESS("detailAddress", "{\"name\": \"详细地址\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		TITLE("title", "{\"name\": \"合同名称\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		NUM("num", "{\"name\": \"合同编号\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		PRICE("price", "{\"name\": \"合同金额\", \"orderBy\": \"6\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		SIGNINGTIME("signingTime", "{\"name\": \"签约日期\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		EFFECTTIME("effectTime", "{\"name\": \"生效日期\", \"orderBy\": \"8\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		SERVICEENDTIME("serviceEndTime", "{\"name\": \"服务期至\", \"orderBy\": \"9\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CONTACTS("contacts", "{\"name\": \"联系人\", \"orderBy\": \"10\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		WORKPHONE("workPhone", "{\"name\": \"固定电话\", \"orderBy\": \"11\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		MOBILEPHONE("mobilePhone", "{\"name\": \"移动电话\", \"orderBy\": \"12\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		QQ("qq", "{\"name\": \"QQ号\", \"orderBy\": \"13\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		EMAIL("email", "{\"name\": \"邮箱\", \"orderBy\": \"14\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		DEPARTMENTID("departmentId", "{\"name\": \"合同所属部门\", \"orderBy\": \"15\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		RELATIONUSERID("relationUserId", "{\"name\": \"合同关联人员\", \"orderBy\": \"16\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		STATE("stateName", "{\"name\": \"状态\", \"orderBy\": \"17\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		USERNAME("userName", "{\"name\": \"创建人\", \"orderBy\": \"18\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		TECHNICALTERMS("technicalTerms", "{\"name\": \"主要技术条款\", \"orderBy\": \"19\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		BUSINESSTERMS("businessTerms", "{\"name\": \"主要商务条款\", \"orderBy\": \"20\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"相关附件\", \"orderBy\": \"21\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
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

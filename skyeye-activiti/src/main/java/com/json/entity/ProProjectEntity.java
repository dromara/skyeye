/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: ProProjectEntity
 * @Description: 项目立项申请提交到工作流的对象
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/5 20:29
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProProjectEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
		PROJECTNAME("projectName", "{\"name\": \"项目名称\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		TYPENAME("typeName", "{\"name\": \"项目分类\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		PROJECTNUMBER("projectNumber", "{\"name\": \"项目编号\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		DEPARTMENTNAME("departmentName", "{\"name\": \"所属部门\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		STARTTIME("startTime", "{\"name\": \"计划开始时间\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		ENDTIME("endTime", "{\"name\": \"计划完成时间\", \"orderBy\": \"6\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CUSTOMERNAME("customerName", "{\"name\": \"客户名称\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CONTRACTNAME("contractName", "{\"name\": \"关联合同\", \"orderBy\": \"8\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CONTACTNAME("contactName", "{\"name\": \"对方联系人\", \"orderBy\": \"9\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		TELPHONE("telphone", "{\"name\": \"固定电话\", \"orderBy\": \"10\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		MOBILE("mobile", "{\"name\": \"移动电话\", \"orderBy\": \"11\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		QQ("qq", "{\"name\": \"QQ号\", \"orderBy\": \"12\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		MAIL("mail", "{\"name\": \"邮箱\", \"orderBy\": \"13\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		ESTIMATEDWORKLOAD("estimatedWorkload", "{\"name\": \"预估工作量\", \"orderBy\": \"14\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		ESTIMATEDCOST("estimatedCost", "{\"name\": \"预估成本费用\", \"orderBy\": \"15\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		STATENAME("stateName", "{\"name\": \"状态\", \"orderBy\": \"16\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		BUSINESSCONTENT("businessContent", "{\"name\": \"需求和目标\", \"orderBy\": \"17\", \"showType\": \"3\", \"proportion\": \"12\"}"),
		BUSINESSENCLOSUREINFO("businessEnclosureInfo", "{\"name\": \"相关附件\", \"orderBy\": \"18\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
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

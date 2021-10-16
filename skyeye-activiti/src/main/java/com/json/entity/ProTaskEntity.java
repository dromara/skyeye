/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: ProTaskEntity
 * @Description: 项目任务实体类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 20:12
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ProTaskEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
		FATHERORSON("fatherOrSon", "{\"name\": \"任务类型\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		TASKNAME("taskName", "{\"name\": \"名称\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		TASKTYPENAME("taskTypeName", "{\"name\": \"所属分类\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		PROJECTNAME("projectName", "{\"name\": \"所属项目\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		DEPARTMENTS("departments", "{\"name\": \"所属部门\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CREATEUSERNAME("userName", "{\"name\": \"创建人\", \"orderBy\": \"6\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		CREATETIME("createTime", "{\"name\": \"创建时间\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		STARTTIME("startTime", "{\"name\": \"开始时间\", \"orderBy\": \"8\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		ENDTIME("endTime", "{\"name\": \"结束时间\", \"orderBy\": \"9\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		PERFORMID("performId", "{\"name\": \"执行人\", \"orderBy\": \"10\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		ESTIMATEDWORKLOAD("estimatedWorkload", "{\"name\": \"预估工作量\", \"orderBy\": \"11\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		TASKINSTRUCTIONS("taskInstructions", "{\"name\": \"任务说明\", \"orderBy\": \"12\", \"showType\": \"3\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"附件\", \"orderBy\": \"13\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
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

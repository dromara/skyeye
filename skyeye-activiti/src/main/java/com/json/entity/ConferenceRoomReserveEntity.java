/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: ConferenceRoomReserveEntity
 * @Description: 会议室预定实体类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 16:47
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ConferenceRoomReserveEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
    	TITLE("title", "{\"name\": \"标题\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"12\"}"),
    	ODD_NUMBER("oddNumber", "{\"name\": \"单号\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	USER_NAME("userName", "{\"name\": \"申请人\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	RESERVESTATENAME("reserveStateName", "{\"name\": \"预定状态\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	CONFERENCEROOM("conferenceRoom", "{\"name\": \"会议室\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	STARTTIME("startTime", "{\"name\": \"开始时间\", \"orderBy\": \"6\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	ENDTIME("endTime", "{\"name\": \"结束时间\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"6\"}"),
    	RESERVEREASON("reserveReason", "{\"name\": \"使用事由\", \"orderBy\": \"8\", \"showType\": \"1\", \"proportion\": \"12\"}"),
    	REMARK("remark", "{\"name\": \"备注\", \"orderBy\": \"9\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"相关附件\", \"orderBy\": \"10\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
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

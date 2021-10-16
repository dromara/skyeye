/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class VehicleUseEntity {

	public static Map<String, Object> transform(Map<String, Object> map) {
		return Entity.getClockInState(map);
	}
	
	public static enum Entity {
		TITLE("title", "{\"name\": \"标题\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		ODD_NUMBER("oddNumber", "{\"name\": \"单号\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		USER_NAME("userName", "{\"name\": \"责任人\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		PASSENGERNUM("passengerNum", "{\"name\": \"乘车人数\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		PASSENGERINFO("passengerInfo", "{\"name\": \"乘车人信息\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		ISSELFDRIVE("isSelfDrive", "{\"name\": \"是否自驾\", \"orderBy\": \"6\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		DRIVERNAME("driverName", "{\"name\": \"驾驶人信息\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		DEPARTURETIME("departureTime", "{\"name\": \"用车时间段\", \"orderBy\": \"8\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		DESIGNATEDVEHICLEINFO("designatedVehicleInfo", "{\"name\": \"指定车辆\", \"orderBy\": \"9\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		TIMEANDPLACEOFTRAVEL("timeAndPlaceOfTravel", "{\"name\": \"乘车时间地点\", \"orderBy\": \"10\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		VEHICLESTATENAME("vehicleStateName", "{\"name\": \"用车状态\", \"orderBy\": \"11\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		VEHICLEINFO("vehicleInfo", "{\"name\": \"实际用车\", \"orderBy\": \"12\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		VEHICLEIMG("vehicleImg", "{\"name\": \"车图片\", \"orderBy\": \"13\", \"showType\": \"4\", \"proportion\": \"12\"}"),
		FUELCONSUMPTION("fuelConsumption", "{\"name\": \"用车油耗(L)\", \"orderBy\": \"14\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		VEHICLECOST("vehicleCost", "{\"name\": \"用车费用(元)\", \"orderBy\": \"15\", \"showType\": \"1\", \"proportion\": \"6\"}"),
		REASONSFORUSINGCAR("reasonsForUsingCar", "{\"name\": \"用车事由\", \"orderBy\": \"16\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		REMARK("remark", "{\"name\": \"相关描述\", \"orderBy\": \"17\", \"showType\": \"1\", \"proportion\": \"12\"}"),
		ENCLOSUREINFO("enclosureInfo", "{\"name\": \"相关附件\", \"orderBy\": \"18\", \"showType\": \"2\", \"proportion\": \"12\"}");
		
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

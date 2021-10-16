/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolConstants {
	
	// 是或否的枚举类
    public static enum YesORNo {
    	YES("1", "是"),
        NO("2", "否");
		
        private String value;
        private String name;
		
        YesORNo(String value, String name){
            this.value = value;
            this.name = name;
        }
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public static List<Map> getList(){
        	List<Map> list = new ArrayList<>();
            for (YesORNo q : YesORNo.values()){
            	Map item = new HashMap<>();
            	item.put("dictKey", q.getValue());
            	item.put("dictValue", q.getName());
            	list.add(item);
            }
            return list;
        }
	
        public static String getValueByName(String name){
            for (YesORNo q : YesORNo.values()){
                if(q.getName().equals(name)){
                    return q.getValue();
                }
            }
            return "";
        }
        
        public static String getNameByValue(String value){
            for (YesORNo q : YesORNo.values()){
                if(q.getValue().equals(value)){
                    return q.getName();
                }
            }
            return "";
        }

		public String getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
    }

}

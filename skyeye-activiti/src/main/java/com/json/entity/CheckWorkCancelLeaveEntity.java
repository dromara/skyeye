/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @ClassName: CheckWorkCancelLeaveEntity
 * @Description: 构造销假申请提交到工作流的对象
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 11:50
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class CheckWorkCancelLeaveEntity {

    public static Map<String, Object> transform(Map<String, Object> map) {
        return Entity.getClockInState(map);
    }

    public static enum Entity {
        TITLE("title", "{\"name\": \"标题\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"12\"}"),
        ODD_NUMBER("oddNumber", "{\"name\": \"单号\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"6\"}"),
        USER_NAME("userName", "{\"name\": \"申请人\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"6\"}"),
        GOODS("cancelLeaveDay", "{\"name\": \"日期\", \"orderBy\": \"4\", \"showType\": \"5\", \"proportion\": \"12\"}"),
        REMARK("remark", "{\"name\": \"相关描述\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"12\"}"),
        ENCLOSUREINFO("enclosureInfo", "{\"name\": \"相关附件\", \"orderBy\": \"6\", \"showType\": \"2\", \"proportion\": \"12\"}");

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
                    jObject.put("headerTitle", "[{ \"field\": \"timeName\", \"title\": \"班次\", \"align\": \"left\", \"width\": 150},"
                            + "{ \"field\": \"timeStartTime\", \"title\": \"班次打卡时间(上班)\", \"align\": \"left\", \"width\": 150},"
                            + "{ \"field\": \"timeEndTime\", \"title\": \"班次打卡时间(上班)\", \"align\": \"left\", \"width\": 150},"
                            + "{ \"field\": \"cancelDay\", \"title\": \"销假日期\", \"align\": \"center\", \"width\": 100},"
                            + "{ \"field\": \"cancelStartTime\", \"title\": \"开始时间\", \"align\": \"left\", \"width\": 100},"
                            + "{ \"field\": \"cancelEndTime\", \"title\": \"结束时间\", \"align\": \"left\", \"width\": 100},"
                            + "{ \"field\": \"cancelHour\", \"title\": \"销假工时\", \"align\": \"left\", \"width\": 100},"
                            + "{ \"field\": \"stateName\", \"title\": \"状态\", \"align\": \"left\", \"width\": 120},"
                            + "{ \"field\": \"remark\", \"title\": \"备注\", \"align\": \"left\", \"width\": 150}]");
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

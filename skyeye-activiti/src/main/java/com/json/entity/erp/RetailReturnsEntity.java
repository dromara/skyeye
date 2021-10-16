/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.json.entity.erp;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: PurchaseOrderEntity
 * @Description: 零售退货单提交到工作流的实体类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/19 22:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class RetailReturnsEntity {

    public static Map<String, Object> transform(Map<String, Object> map) {
        return Entity.getClockInState(map);
    }

    public static enum Entity {
        SUPPLIER_NAME("supplierName", "{\"name\": \"会员\", \"orderBy\": \"1\", \"showType\": \"1\", \"proportion\": \"3\"}"),
        OPER_TIME("operTime", "{\"name\": \"单据日期\", \"orderBy\": \"2\", \"showType\": \"1\", \"proportion\": \"3\"}"),
        ACCOUNT_NAME("accountName", "{\"name\": \"结算账户\", \"orderBy\": \"3\", \"showType\": \"1\", \"proportion\": \"3\"}"),
        PAY_TYPE("payType", "{\"name\": \"退款类型\", \"orderBy\": \"4\", \"showType\": \"1\", \"proportion\": \"3\"}"),
        DEFAULT_NUMBER("defaultNumber", "{\"name\": \"单据编号\", \"orderBy\": \"5\", \"showType\": \"1\", \"proportion\": \"6\"}"),
        OPER_PERSON_NAME("operPersonName", "{\"name\": \"操作人\", \"orderBy\": \"6\", \"showType\": \"1\", \"proportion\": \"6\"}"),
        CREATE_TIME("createTime", "{\"name\": \"操作日期\", \"orderBy\": \"7\", \"showType\": \"1\", \"proportion\": \"6\"}"),
        ITEMS("items", "{\"name\": \"列表项\", \"orderBy\": \"8\", \"showType\": \"5\", \"proportion\": \"12\"}"),
        TOTAL_PRICE("totalPrice", "{\"name\": \"共计金额\", \"orderBy\": \"9\", \"showType\": \"1\", \"proportion\": \"4\"}"),
        CHANGE_AMOUNT("changeAmount", "{\"name\": \"退款金额\", \"orderBy\": \"10\", \"showType\": \"1\", \"proportion\": \"4\"}"),
        DISCOUNT("giveChange", "{\"name\": \"折损扣费\", \"orderBy\": \"11\", \"showType\": \"1\", \"proportion\": \"4\"}"),
        REMARK("remark", "{\"name\": \"备注\", \"orderBy\": \"12\", \"showType\": \"1\", \"proportion\": \"12\"}");

        private String nameCode;
        private String str;

        Entity(String nameCode, String str){
            this.nameCode = nameCode;
            this.str = str;
        }

        public static Map<String, Object> getClockInState(Map<String, Object> map){
            Map<String, Object> bean = new HashMap<>();
            Map<String, Object> jObject;
            for (Entity q : Entity.values()){
                jObject = JSONUtil.toBean(q.getStr(), null);
                jObject.put("value", map.containsKey(q.getNameCode()) ? map.get(q.getNameCode()) : "");
                if("5".equals(jObject.get("showType").toString())){//展示为表格,特殊处理
                    getItemListHeaderTitle(jObject);
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

    private static void getItemListHeaderTitle(Map<String, Object> jObject) {
        jObject.put("headerTitle", "[{ \"field\": \"deportName\", \"title\": \"仓库\", \"align\": \"left\", \"width\": 150},"
                + "{ \"field\": \"materialNames\", \"title\": \"产品(型号)\", \"align\": \"left\", \"width\": 70},"
                + "{ \"field\": \"unitName\", \"title\": \"单位\", \"align\": \"left\", \"width\": 70},"
                + "{ \"field\": \"operNumber\", \"title\": \"数量\", \"align\": \"left\", \"width\": 80},"
                + "{ \"field\": \"unitPrice\", \"title\": \"单价\", \"align\": \"left\", \"width\": 80},"
                + "{ \"field\": \"allPrice\", \"title\": \"金额\", \"align\": \"left\", \"width\": 80},"
                + "{ \"field\": \"remark\", \"title\": \"备注\", \"align\": \"left\", \"width\": 100}]");
    }

}

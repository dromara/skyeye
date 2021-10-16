/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

import com.skyeye.common.util.DateUtil;

import java.util.Arrays;
import java.util.Locale;

/**
 * @ClassName: AdminAssistantConstants
 * @Description: 行政办公相关常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/17 21:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AdminAssistantConstants {

    public static enum AdminAssistantType {
        ASSET_ARTICLES_ODD_NUMBER_TO_USE("用品领用单", "YPLY", 1),
        ASSET_ARTICLES_ODD_NUMBER_TO_PURCHASE("用品采购单", "YPCG", 2),
        ASSET_ODD_NUMBER_TO_USE("资产领用单", "ZCLY", 3),
        ASSET_ODD_NUMBER_TO_PURCHASE("资产采购单", "ZCCG", 4),
        ASSET_ODD_NUMBER_TO_RETURN("资产归还单", "ZCGH", 5),
        LICENCE_BORROW_ODD_NUMBER_TO_USE("证照借用单", "ZZJY", 6),
        LICENCE_REVERT_ODD_NUMBER_TO_USE("证照归还单", "ZZGH", 7),
        SEAL_BORROW_ODD_NUMBER_TO_USE("印章借用单", "YZJY", 8),
        SEAL_REVERT_ODD_NUMBER_TO_USE("印章归还单", "YZGH", 9),
        VEHICLE_ODD_NUMBER_TO_USE("用车申请单", "YCSQ", 10),
        CONFERENCE_ROOM_RESERVE_ODD_NUMBER("会议室预定单", "HYSYD", 11);

        private String title;
        private String orderNum;
        private Integer type;

        AdminAssistantType(String title, String orderNum, Integer type){
            this.title = title;
            this.orderNum = orderNum;
            this.type = type;
        }

        public String getOrderNumberByType(int type) {
            AdminAssistantType adminAssistantType = Arrays.stream(AdminAssistantType.values())
                .filter(bean -> bean.getType() == type).findFirst().orElse(null);
            if (adminAssistantType == null) {
                return "";
            } else {
                return String.format(Locale.ROOT, "%s%s", adminAssistantType.getOrderNum(),
                        DateUtil.getTimeStrAndToString());
            }
        }

        public String getTitle() {
            return title;
        }

        public String getOrderNum() {
            return String.format(Locale.ROOT, "%s%s", this.orderNum, DateUtil.getTimeStrAndToString());
        }

        public Integer getType() {
            return type;
        }
    }

}

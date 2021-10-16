/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

import java.util.Arrays;

/**
 * @ClassName: OrganizationConstants
 * @Description: 组织管理常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/30 20:01
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class OrganizationConstants {

    /**
     * 企业部门-加班结算类型
     */
    public static enum OvertimeSettlementType {
        SINGLE_SALARY_SETTLEMENT("单倍薪资结算", 1),
        ONE_POINT_FIVE_SALARY_SETTLEMENT("1.5倍薪资结算", 2),
        DOUBLE_SALARY_SETTLEMENT("双倍薪资结算", 3),
        COMPENSATORY_LEAVE_SETTLEMENT("补休结算", 6);

        private String title;
        private Integer type;

        OvertimeSettlementType(String title, Integer type){
            this.title = title;
            this.type = type;
        }

        public static String getTitleByType(int type) {
            OvertimeSettlementType overtimeSettlementType = Arrays.stream(OvertimeSettlementType.values())
                    .filter(bean -> bean.getType() == type).findFirst().orElse(null);
            if (overtimeSettlementType == null) {
                return "";
            } else {
                return overtimeSettlementType.getTitle();
            }
        }

        public String getTitle() {
            return title;
        }

        public Integer getType() {
            return type;
        }
    }
    
}

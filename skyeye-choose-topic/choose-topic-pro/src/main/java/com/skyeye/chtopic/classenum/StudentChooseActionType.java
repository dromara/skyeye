/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 学生选题/选导操作历史类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StudentChooseActionType implements SkyeyeEnumClass {

    CHOOSE_TOPIC(1, "选择课题", true, false),
    CANCEL_TOPIC(2, "取消选题", true, false),
    CHOOSE_TEACHER(3, "选择导师", true, false),
    CANCEL_TEACHER(4, "退选导师", true, false),
    CHANGE_TEACHER(5, "更换导师", true, false),
    TEACHER_AGREE(6, "导师同意", true, false),
    TEACHER_REJECT(7, "导师拒绝", true, false),
    AUTO_REJECT(8, "容量满自动拒绝", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getNameByKey(Integer key) {
        if (key == null) {
            return "未知操作";
        }
        for (StudentChooseActionType item : values()) {
            if (item.getKey().equals(key)) {
                return item.getValue();
            }
        }
        return "未知操作";
    }

    public static String getTeacherReviewNameByKey(Integer key) {
        if (key == null) {
            return "未知操作";
        }
        switch (key) {
            case 6:
                return "同意";
            case 7:
                return "拒绝";
            case 8:
                return "自动拒绝";
            default:
                return getNameByKey(key);
        }
    }
}

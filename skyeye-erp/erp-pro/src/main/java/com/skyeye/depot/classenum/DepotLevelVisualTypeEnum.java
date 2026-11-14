/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: DepotLevelVisualTypeEnum
 * @Description: 仓库级别的 3D 可视化类型（名称可自由命名，类型决定 3D/导航语义）
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/13
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DepotLevelVisualTypeEnum implements SkyeyeEnumClass {

    ZONE(1, "分区/库区", true, false),
    AISLE(2, "巷道/通道", true, false),
    RACK(3, "排/货架", true, false),
    LEVEL(4, "层", true, false),
    BIN(5, "位/格口", true, false),
    DOCK(6, "月台/出入口", true, false),
    /** 原「电梯/楼梯/升降」拆分：历史数据 visualType=7 按电梯处理 */
    ELEVATOR(7, "电梯", true, false),
    CUSTOM(8, "自定义", true, true),
    STAIRS(9, "楼梯", true, false),
    LIFT(10, "升降机", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AutoDemandRoleStateEnum
 * @Description: 需求积分分配-角色工作状态
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/17
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoDemandRoleStateEnum implements SkyeyeEnumClass {

    WAIT("wait", "未开始", "orange", true, true),
    PROGRESS("progress", "进行中", "blue", true, false),
    FINISH("finish", "已完成", "green", true, false);

    private String key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}

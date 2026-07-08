/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheduling.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: SchedulePublishState
 * @Description: 排班发布状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2026/7/8
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SchedulePublishState implements SkyeyeEnumClass {

    DRAFT(1, "未发布", true, true),
    PUBLISHED(2, "已发布", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

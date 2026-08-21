/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.report;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName: ReportPersistProperties
 * @Description: 是否把大屏写入本地 report_page
 */
@ConfigurationProperties(prefix = "skill.report")
public class ReportPersistProperties {

    /** 默认开启：executeSkill 成功后落本地 report_page */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

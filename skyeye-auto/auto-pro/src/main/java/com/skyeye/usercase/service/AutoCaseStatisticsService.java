/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AutoCaseStatisticsService
 * @Description: 用例管理统计服务接口层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoCaseStatisticsService {

    /**
     * 用例总览卡片：用例总数、步骤总数、空用例数、平均步骤数
     */
    void queryOverviewAutoCase(InputObject inputObject, OutputObject outputObject);

    /**
     * 用例新增日度趋势
     */
    void queryAutoCaseTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 按模块统计用例数量
     */
    void queryAutoCaseStatsByModule(InputObject inputObject, OutputObject outputObject);

}

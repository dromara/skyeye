/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AutoDemandStatisticsService
 * @Description: 需求管理统计服务接口层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoDemandStatisticsService {

    /**
     * 需求总览卡片：总数及各状态数量
     */
    void queryOverviewAutoDemand(InputObject inputObject, OutputObject outputObject);

    /**
     * 需求新增日度趋势
     */
    void queryAutoDemandTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 按状态统计需求数量
     */
    void queryDemandStatsByState(InputObject inputObject, OutputObject outputObject);

}

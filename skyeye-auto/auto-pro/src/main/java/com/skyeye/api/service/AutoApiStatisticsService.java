/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.api.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AutoApiStatisticsService
 * @Description: 接口管理统计服务接口层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoApiStatisticsService {

    /**
     * 接口总览卡片：接口总数、已归属模块数、未归属模块数、已被步骤引用数
     */
    void queryOverviewAutoApi(InputObject inputObject, OutputObject outputObject);

    /**
     * 接口新增日度趋势
     */
    void queryAutoApiTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 按创建人统计接口数量
     */
    void queryApiStatsByCreator(InputObject inputObject, OutputObject outputObject);

}

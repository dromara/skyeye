/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.module.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AutoModuleStatisticsService
 * @Description: 项目模块统计服务接口层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoModuleStatisticsService {

    /**
     * 模块总览卡片：模块总数、一级模块数、用例总数、空模块数
     */
    void queryOverviewAutoModule(InputObject inputObject, OutputObject outputObject);

    /**
     * 模块新增日度趋势
     */
    void queryAutoModuleTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 按模块统计用例数量
     */
    void queryCaseStatsByModule(InputObject inputObject, OutputObject outputObject);

}

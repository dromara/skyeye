/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.schedule.entity.AutoScheduleTask;

import java.util.List;
import java.util.Map;

/**
 * @Description: 自动化定时任务数据层
 */
public interface AutoScheduleTaskDao extends SkyeyeBaseMapper<AutoScheduleTask> {

    List<Map<String, Object>> queryAutoScheduleTaskList(CommonPageInfo pageInfo);

}

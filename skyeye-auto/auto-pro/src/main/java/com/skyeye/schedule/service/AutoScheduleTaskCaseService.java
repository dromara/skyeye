/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.schedule.entity.AutoScheduleTaskCase;

import java.util.List;

/**
 * @Description: 定时任务与用例关联服务
 */
public interface AutoScheduleTaskCaseService extends SkyeyeBusinessService<AutoScheduleTaskCase> {

    void deleteByParentId(String parentId);

    List<String> selectByParentId(String parentId);

    void saveList(String parentId, List<String> caseIds);

}

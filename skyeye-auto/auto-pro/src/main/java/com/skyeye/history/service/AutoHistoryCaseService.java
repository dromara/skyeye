/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.history.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.history.entity.AutoHistoryCase;

import java.util.List;

/**
 * @ClassName: AutoHistoryCaseService
 * @Description: 用例执行历史服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/16 20:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoHistoryCaseService extends SkyeyeBusinessService<AutoHistoryCase> {

    /**
     * 判断指定用例是否在执行中
     *
     * @param caseId
     * @return true：存在执行中的用例，false：不存在执行中的用例
     */
    Boolean checkUserCaseRuning(String caseId);

    void finishAutoCaseHistoryById(InputObject inputObject, OutputObject outputObject);

    void finishAutoCaseHistoryById(String id, Integer result);

    /**
     * 定时任务执行详情：分页查询关联的用例执行历史
     */
    void queryScheduleTaskHistoryCaseList(InputObject inputObject, OutputObject outputObject);

    /**
     * 删除定时任务执行记录下关联的用例执行历史
     */
    void deleteByScheduleTaskHistoryIds(List<String> scheduleTaskHistoryIds);
}

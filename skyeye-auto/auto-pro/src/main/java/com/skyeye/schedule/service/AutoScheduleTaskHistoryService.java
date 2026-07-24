/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.schedule.entity.AutoScheduleTaskHistory;

/**
 * @ClassName: AutoScheduleTaskHistoryService
 * @Description: 定时任务执行记录服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2026/7/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoScheduleTaskHistoryService extends SkyeyeBusinessService<AutoScheduleTaskHistory> {

    /**
     * 是否存在执行中的定时任务记录
     */
    Boolean checkScheduleTaskRuning(String scheduleTaskId);

    /**
     * 根据定时任务id删除执行记录
     */
    void deleteByScheduleTaskId(String scheduleTaskId);

    /**
     * 根据id回写执行结束结果
     */
    void finishAutoScheduleTaskHistoryById(String id, Integer result, Integer totalNum,
                                           Integer successNum, Integer failNum, Double successRate);

}

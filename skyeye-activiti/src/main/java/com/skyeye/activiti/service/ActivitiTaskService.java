/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.Map;

/**
 * @ClassName: ActivitiTaskService
 * @Description: 工作流用户任务相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActivitiTaskService {

    void queryUserAgencyTasksListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

    void queryStartProcessNotSubByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

    void queryMyHistoryTaskByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

    void queryApprovalTasksHistoryByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception;

    void queryAllComplateProcessList(InputObject inputObject, OutputObject outputObject) throws Exception;

    void queryAllConductProcessList(InputObject inputObject, OutputObject outputObject) throws Exception;

    void querySubFormMationByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception;

    void querySubFormMationByTaskId(InputObject inputObject, OutputObject outputObject) throws Exception;

    /**
     * 获取当前任务节点填写的表单数据
     *
     * @param taskId 任务id
     * @return 当前任务节点填写的表单数据
     */
    Map<String, Object> getCurrentTaskParamsByTaskId(String taskId);

    void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) throws Exception;

}

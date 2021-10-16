/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 *
 * @ClassName: ProTaskService
 * @Description: 项目任务管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 20:11
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProTaskService {

    public void queryProTaskList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertProTaskMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProTaskMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProTaskMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProTaskMation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteProTaskMationById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProTaskProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProTaskToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProTaskToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProTaskMationInProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyProTaskList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProTaskToExecutionBegin(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProTaskToExecutionOver(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProTaskToExecutionClose(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryProTaskInExecution(InputObject inputObject, OutputObject outputObject) throws Exception;

}

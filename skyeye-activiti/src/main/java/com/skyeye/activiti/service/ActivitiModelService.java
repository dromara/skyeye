/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.Map;

public interface ActivitiModelService {

	public void insertNewActivitiModel(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToStartProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryUserAgencyTasksListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryReleaseActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editApprovalActivitiTaskListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryStartProcessNotSubByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryMyHistoryTaskByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertSyncUserListMationToAct(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void querySubFormMationByTaskId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryApprovalTasksHistoryByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllComplateProcessList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryAllConductProcessList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProcessToHangUp(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void updateProcessToActivation(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertDSFormProcess(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToStartProcessByMap(Map<String, Object> map, Map<String, Object> user, String id) throws Exception;

	public void querySubFormMationByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProcessInstanceWithDraw(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editProcessInstancePicToRefresh(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editDsFormContentToRevokeByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 设置该流程是否可以编辑
	 *
	 * @param map 参数
	 * @param processInstanceId 流程id
	 * @param userId 当前登陆人id
	 */
	public void setWhetherEditByProcessInstanceId(Map<String, Object> map, String processInstanceId, String userId) throws Exception;

	/**
	 * 判断是否提交到工作流
	 *
	 * @param bean 单据信息
	 * @return
	 * @throws Exception
	 */
	public String judgeSubmitActiviti(Map<String, Object> bean) throws Exception;

    void copyModelByModelId(InputObject inputObject, OutputObject outputObject) throws Exception;
}

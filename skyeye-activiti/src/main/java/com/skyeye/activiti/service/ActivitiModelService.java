/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: ActivitiModelService
 * @Description: 工作流模型操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 21:37
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActivitiModelService {

	public void insertNewActivitiModel(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void queryActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryReleaseActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void editApprovalActivitiTaskListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception;

	/**
	 * 删除指定流程在redis中的缓存信息
	 *
	 * @param processInstanceId 流程id
	 */
	void deleteProcessInRedisMation(String processInstanceId);

	/**
	 * 获取指定任务节点的审批信息
	 *
	 * @param approvedId 审批人id
	 * @param approvedName 审批人名字
	 * @param opinion 审批意见
	 * @param flag 该节点是否审批通过，true:通过，false:不通过
	 * @param task 任务
	 * @return
	 */
	List<Map<String, Object>> getUpLeaveList(String approvedId, String approvedName, String opinion, boolean flag, Task task);

	public void editActivitiModelToStartProcessByMap(Map<String, Object> map, Map<String, Object> user, String id, String approvalId) throws Exception;

	/**
	 * 流程图高亮显示
	 *
	 * @param processInstanceId
	 * @throws Exception
	 */
	void queryProHighLighted(String processInstanceId) throws Exception;

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

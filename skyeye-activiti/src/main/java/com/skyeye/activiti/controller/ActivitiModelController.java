/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.activiti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class ActivitiModelController {
	
	@Autowired
	private ActivitiModelService activitiModelService;
	
    /**
	 * 
	     * @Title: insertNewActivitiModel
	     * @Description: 新建一个空模型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/insertNewActivitiModel")
	@ResponseBody
	public void insertNewActivitiModel(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.insertNewActivitiModel(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryActivitiModelList
	     * @Description: 获取所有模型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryActivitiModelList")
	@ResponseBody
	public void queryActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryActivitiModelList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActivitiModelToDeploy
	     * @Description: 发布模型为流程定义
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/editActivitiModelToDeploy")
	@ResponseBody
	public void editActivitiModelToDeploy(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.editActivitiModelToDeploy(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActivitiModelToStartProcess
	     * @Description: 启动流程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/editActivitiModelToStartProcess")
	@ResponseBody
	public void editActivitiModelToStartProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.editActivitiModelToStartProcess(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActivitiModelToRun
	     * @Description: 提交任务
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/editActivitiModelToRun")
	@ResponseBody
	public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.editActivitiModelToRun(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteActivitiModelById
	     * @Description: 删除模型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/deleteActivitiModelById")
	@ResponseBody
	public void deleteActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.deleteActivitiModelById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteReleasedActivitiModelById
	     * @Description: 取消发布
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/deleteReleasedActivitiModelById")
	@ResponseBody
	public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.deleteReleasedActivitiModelById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserAgencyTasksListByUserId
	     * @Description: 获取用户待办任务
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryUserAgencyTasksListByUserId")
	@ResponseBody
	public void queryUserAgencyTasksListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryUserAgencyTasksListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryReleaseActivitiModelList
	     * @Description: 获取已发布模型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryReleaseActivitiModelList")
	@ResponseBody
	public void queryReleaseActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryReleaseActivitiModelList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editApprovalActivitiTaskListByUserId
	     * @Description: 导出model的xml文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/editApprovalActivitiTaskListByUserId")
	@ResponseBody
	public void editApprovalActivitiTaskListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.editApprovalActivitiTaskListByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserListToActiviti
	     * @Description: 获取人员选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryUserListToActiviti")
	@ResponseBody
	public void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryUserListToActiviti(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryUserGroupListToActiviti
	     * @Description: 获取组人员选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryUserGroupListToActiviti")
	@ResponseBody
	public void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryUserGroupListToActiviti(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryStartProcessNotSubByUserId
	     * @Description: 获取我启动的流程
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryStartProcessNotSubByUserId")
	@ResponseBody
	public void queryStartProcessNotSubByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryStartProcessNotSubByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMyHistoryTaskByUserId
	     * @Description: 获取我的历史任务
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryMyHistoryTaskByUserId")
	@ResponseBody
	public void queryMyHistoryTaskByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryMyHistoryTaskByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSyncUserListMationToAct
	     * @Description: 用户以及用户组信息同步到act表中
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/insertSyncUserListMationToAct")
	@ResponseBody
	public void insertSyncUserListMationToAct(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.insertSyncUserListMationToAct(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySubFormMationByTaskId
	     * @Description: 获取用户提交的表单信息根据taskid
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/querySubFormMationByTaskId")
	@ResponseBody
	public void querySubFormMationByTaskId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.querySubFormMationByTaskId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryApprovalTasksHistoryByProcessInstanceId
	     * @Description: 获取历史审批列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryApprovalTasksHistoryByProcessInstanceId")
	@ResponseBody
	public void queryApprovalTasksHistoryByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryApprovalTasksHistoryByProcessInstanceId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllComplateProcessList
	     * @Description: 获取所有已完成的流程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryAllComplateProcessList")
	@ResponseBody
	public void queryAllComplateProcessList(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryAllComplateProcessList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllConductProcessList
	     * @Description: 获取所有待办的流程信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryAllConductProcessList")
	@ResponseBody
	public void queryAllConductProcessList(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryAllConductProcessList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateProcessToHangUp
	     * @Description: 流程挂起
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/updateProcessToHangUp")
	@ResponseBody
	public void updateProcessToHangUp(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.updateProcessToHangUp(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateProcessToActivation
	     * @Description: 流程激活
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/updateProcessToActivation")
	@ResponseBody
	public void updateProcessToActivation(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.updateProcessToActivation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDSFormProcess
	     * @Description: 提交审批
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/insertDSFormProcess")
	@ResponseBody
	public void insertDSFormProcess(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.insertDSFormProcess(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySubFormMationByProcessInstanceId
	     * @Description: 获取用户提交的表单信息根据流程id
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/querySubFormMationByProcessInstanceId")
	@ResponseBody
	public void querySubFormMationByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.querySubFormMationByProcessInstanceId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editProcessInstanceWithDraw
	     * @Description: 流程撤回
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/editProcessInstanceWithDraw")
	@ResponseBody
	public void editProcessInstanceWithDraw(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.editProcessInstanceWithDraw(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editProcessInstancePicToRefresh
	     * @Description: 刷新流程图
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/editProcessInstancePicToRefresh")
	@ResponseBody
	public void editProcessInstancePicToRefresh(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.editProcessInstancePicToRefresh(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormContentToRevokeByProcessInstanceId
	     * @Description: 动态表单提交项进行撤销操作
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/editDsFormContentToRevokeByProcessInstanceId")
	@ResponseBody
	public void editDsFormContentToRevokeByProcessInstanceId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.editDsFormContentToRevokeByProcessInstanceId(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: copyModelByModelId
	 * @Description: 流程模型拷贝
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/copyModelByModelId")
	@ResponseBody
	public void copyModelByModelId(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.copyModelByModelId(inputObject, outputObject);
	}

	
}

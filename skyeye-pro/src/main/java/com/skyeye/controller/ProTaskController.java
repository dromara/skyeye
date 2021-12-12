/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ProTaskService;

/**
 *
 * @ClassName: ProTaskController
 * @Description: 项目任务管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 20:09
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Controller
public class ProTaskController {

    @Autowired
    private ProTaskService proTaskService;

    /**
     *
     * @Title: queryProTaskList
     * @Description: 获取任务管理表列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProTaskController/queryProTaskList")
    @ResponseBody
    public void queryProTaskList(InputObject inputObject, OutputObject outputObject) throws Exception {
        proTaskService.queryProTaskList(inputObject, outputObject);
    }
    
	/**
    *
    * @Title: insertProTaskMation
    * @Description: 新增任务管理
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/insertProTaskMation")
	@ResponseBody
	public void insertProTaskMation(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.insertProTaskMation(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryProTaskMationToDetails
    * @Description: 任务详情
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/queryProTaskMationToDetails")
	@ResponseBody
	public void queryProTaskMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.queryProTaskMationToDetails(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryProTaskMationToEdit
    * @Description: 获取任务信息用以编辑
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/queryProTaskMationToEdit")
	@ResponseBody
	public void queryProTaskMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.queryProTaskMationToEdit(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: editProTaskMation
    * @Description: 编辑任务信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/editProTaskMation")
	@ResponseBody
	public void editProTaskMation(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.editProTaskMation(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: deleteProTaskMationById
    * @Description: 删除任务
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/deleteProTaskMationById")
	@ResponseBody
	public void deleteProTaskMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.deleteProTaskMationById(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: editProTaskProcessToRevoke
    * @Description: 撤销任务审批申请
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/editProTaskProcessToRevoke")
	@ResponseBody
	public void editProTaskProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.editProTaskProcessToRevoke(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: editProTaskToApprovalById
    * @Description: 根据任务Id提交审批
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/editProTaskToApprovalById")
	@ResponseBody
	public void editProTaskToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.editProTaskToApprovalById(inputObject, outputObject);
	}
	
	/**
     * 
     * @Title: updateProTaskToCancellation
     * @Description: 作废任务
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
	@RequestMapping("/post/ProTaskController/updateProTaskToCancellation")
	@ResponseBody
	public void updateProTaskToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.updateProTaskToCancellation(inputObject, outputObject);
	}

	/**
     * 
     * @Title: queryMyProTaskList
     * @Description: 获取我的任务列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
	@RequestMapping("/post/ProTaskController/queryMyProTaskList")
	@ResponseBody
	public void queryMyProTaskList(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.queryMyProTaskList(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: updateProTaskToExecutionBegin
    * @Description: 任务开始执行
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/updateProTaskToExecutionBegin")
	@ResponseBody
	public void updateProTaskToExecutionBegin(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.updateProTaskToExecutionBegin(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: updateProTaskToExecutionOver
    * @Description: 任务执行完成
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/updateProTaskToExecutionOver")
	@ResponseBody
	public void updateProTaskToExecutionOver(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.updateProTaskToExecutionOver(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: updateProTaskToExecutionClose
    * @Description: 任务关闭
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/updateProTaskToExecutionClose")
	@ResponseBody
	public void updateProTaskToExecutionClose(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.updateProTaskToExecutionClose(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryProTaskInExecution
    * @Description: 登录人的执行中的任务
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProTaskController/queryProTaskInExecution")
	@ResponseBody
	public void queryProTaskInExecution(InputObject inputObject, OutputObject outputObject) throws Exception {
	   proTaskService.queryProTaskInExecution(inputObject, outputObject);
	}
 
}

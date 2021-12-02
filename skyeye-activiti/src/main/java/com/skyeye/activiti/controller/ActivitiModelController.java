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

/**
 *
 * @ClassName: ActivitiModelController
 * @Description: 工作流模型操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 21:37
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
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

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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @Title: queryReleasedActivitiModelList
	     * @Description: 获取已经发布的模型
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/queryReleasedActivitiModelList")
	@ResponseBody
	public void queryReleasedActivitiModelList(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.queryReleasedActivitiModelList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteReleasedActivitiModelById
	     * @Description: 取消发布
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActivitiModelController/deleteReleasedActivitiModelById")
	@ResponseBody
	public void deleteReleasedActivitiModelById(InputObject inputObject, OutputObject outputObject) throws Exception{
		activitiModelService.deleteReleasedActivitiModelById(inputObject, outputObject);
	}
	
}

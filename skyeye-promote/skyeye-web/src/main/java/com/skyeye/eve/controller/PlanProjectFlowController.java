/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.PlanProjectFlowService;

@Controller
public class PlanProjectFlowController {
	
	@Autowired
	private PlanProjectFlowService planProjectFlowService;
	
	/**
	 * 
	     * @Title: queryPlanProjectFlowList
	     * @Description: 获取项目规划-项目流程图表列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectFlowController/queryPlanProjectFlowList")
	@ResponseBody
	public void queryPlanProjectFlowList(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectFlowService.queryPlanProjectFlowList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertPlanProjectFlowMation
	     * @Description: 添加项目规划-项目流程图表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectFlowController/insertPlanProjectFlowMation")
	@ResponseBody
	public void insertPlanProjectFlowMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectFlowService.insertPlanProjectFlowMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deletePlanProjectFlowMationById
	     * @Description: 删除项目规划-项目流程图表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectFlowController/deletePlanProjectFlowMationById")
	@ResponseBody
	public void deletePlanProjectFlowMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectFlowService.deletePlanProjectFlowMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryPlanProjectFlowMationToEditById
	     * @Description: 编辑项目规划-项目流程图表信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectFlowController/queryPlanProjectFlowMationToEditById")
	@ResponseBody
	public void queryPlanProjectFlowMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectFlowService.queryPlanProjectFlowMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editPlanProjectFlowMationById
	     * @Description: 编辑项目规划-项目流程图表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectFlowController/editPlanProjectFlowMationById")
	@ResponseBody
	public void editPlanProjectFlowMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectFlowService.editPlanProjectFlowMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryPlanProjectFlowJsonContentMationById
	     * @Description: 获取项目流程图内容进行设计
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectFlowController/queryPlanProjectFlowJsonContentMationById")
	@ResponseBody
	public void queryPlanProjectFlowJsonContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectFlowService.queryPlanProjectFlowJsonContentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editPlanProjectFlowJsonContentMationById
	     * @Description: 设计项目流程图
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectFlowController/editPlanProjectFlowJsonContentMationById")
	@ResponseBody
	public void editPlanProjectFlowJsonContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectFlowService.editPlanProjectFlowJsonContentMationById(inputObject, outputObject);
	}
	
}

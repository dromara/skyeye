package com.skyeye.planproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.planproject.service.PlanProjectService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class PlanProjectController {
	
	@Autowired
	private PlanProjectService planProjectService;
	
	/**
	 * 
	     * @Title: queryPlanProjectList
	     * @Description: 获取项目规划-项目表列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectController/queryPlanProjectList")
	@ResponseBody
	public void queryPlanProjectList(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectService.queryPlanProjectList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertPlanProjectMation
	     * @Description: 添加项目规划-项目表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectController/insertPlanProjectMation")
	@ResponseBody
	public void insertPlanProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectService.insertPlanProjectMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deletePlanProjectMationById
	     * @Description: 删除项目规划-项目表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectController/deletePlanProjectMationById")
	@ResponseBody
	public void deletePlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectService.deletePlanProjectMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryPlanProjectMationToEditById
	     * @Description: 编辑项目规划-项目表信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectController/queryPlanProjectMationToEditById")
	@ResponseBody
	public void queryPlanProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectService.queryPlanProjectMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editPlanProjectMationById
	     * @Description: 编辑项目规划-项目表信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PlanProjectController/editPlanProjectMationById")
	@ResponseBody
	public void editPlanProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		planProjectService.editPlanProjectMationById(inputObject, outputObject);
	}
	
}

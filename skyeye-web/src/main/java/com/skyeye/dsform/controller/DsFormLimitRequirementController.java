package com.skyeye.dsform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.dsform.service.DsFormLimitRequirementService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class DsFormLimitRequirementController {
	
	@Autowired
	private DsFormLimitRequirementService dsFormLimitRequirementService;
	
	/**
	 * 
	     * @Title: queryDsFormLimitRequirementList
	     * @Description: 获取动态表单条件限制类型列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormLimitRequirementController/queryDsFormLimitRequirementList")
	@ResponseBody
	public void queryDsFormLimitRequirementList(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormLimitRequirementService.queryDsFormLimitRequirementList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDsFormLimitRequirementMation
	     * @Description: 添加动态表单条件限制类型信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormLimitRequirementController/insertDsFormLimitRequirementMation")
	@ResponseBody
	public void insertDsFormLimitRequirementMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormLimitRequirementService.insertDsFormLimitRequirementMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteDsFormLimitRequirementMationById
	     * @Description: 删除动态表单条件限制类型信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormLimitRequirementController/deleteDsFormLimitRequirementMationById")
	@ResponseBody
	public void deleteDsFormLimitRequirementMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormLimitRequirementService.deleteDsFormLimitRequirementMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDsFormLimitRequirementMationToEditById
	     * @Description: 编辑动态表单条件限制类型信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormLimitRequirementController/queryDsFormLimitRequirementMationToEditById")
	@ResponseBody
	public void queryDsFormLimitRequirementMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormLimitRequirementService.queryDsFormLimitRequirementMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormLimitRequirementMationById
	     * @Description: 编辑动态表单条件限制类型信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormLimitRequirementController/editDsFormLimitRequirementMationById")
	@ResponseBody
	public void editDsFormLimitRequirementMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormLimitRequirementService.editDsFormLimitRequirementMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDsFormLimitRequirementMationToShow
	     * @Description: 获取动态表单内容供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormLimitRequirementController/queryDsFormLimitRequirementMationToShow")
	@ResponseBody
	public void queryDsFormLimitRequirementMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormLimitRequirementService.queryDsFormLimitRequirementMationToShow(inputObject, outputObject);
	}
	
}

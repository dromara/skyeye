package com.skyeye.exexplain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.exexplain.service.ExExplainToCodeModelService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class ExExplainToCodeModelController {
	
	@Autowired
	private ExExplainToCodeModelService exExplainToCodeModelService;
	
	/**
	 * 
	     * @Title: insertExExplainToCodeModelMation
	     * @Description: 添加代码生成器说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToCodeModelController/insertExExplainToCodeModelMation")
	@ResponseBody
	public void insertExExplainToCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToCodeModelService.insertExExplainToCodeModelMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainToCodeModelMation
	     * @Description: 编辑代码生成器说明信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToCodeModelController/queryExExplainToCodeModelMation")
	@ResponseBody
	public void queryExExplainToCodeModelMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToCodeModelService.queryExExplainToCodeModelMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editExExplainToCodeModelMationById
	     * @Description: 编辑代码生成器说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToCodeModelController/editExExplainToCodeModelMationById")
	@ResponseBody
	public void editExExplainToCodeModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToCodeModelService.editExExplainToCodeModelMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainToCodeModelMationToShow
	     * @Description: 获取代码生成器说明信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToCodeModelController/queryExExplainToCodeModelMationToShow")
	@ResponseBody
	public void queryExExplainToCodeModelMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToCodeModelService.queryExExplainToCodeModelMationToShow(inputObject, outputObject);
	}
	
}

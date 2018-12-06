package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.ExExplainToDsFormContentService;

@Controller
public class ExExplainToDsFormContentController {
	
	@Autowired
	private ExExplainToDsFormContentService exExplainToDsFormContentService;
	
	/**
	 * 
	     * @Title: insertExExplainToDsFormContentMation
	     * @Description: 添加动态表单内容项说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToDsFormContentController/insertExExplainToDsFormContentMation")
	@ResponseBody
	public void insertExExplainToDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToDsFormContentService.insertExExplainToDsFormContentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainToDsFormContentMation
	     * @Description: 编辑动态表单内容项说明信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToDsFormContentController/queryExExplainToDsFormContentMation")
	@ResponseBody
	public void queryExExplainToDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToDsFormContentService.queryExExplainToDsFormContentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editExExplainToDsFormContentMationById
	     * @Description: 编辑动态表单内容项说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToDsFormContentController/editExExplainToDsFormContentMationById")
	@ResponseBody
	public void editExExplainToDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToDsFormContentService.editExExplainToDsFormContentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainToDsFormContentMationToShow
	     * @Description: 获取动态表单内容项说明信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToDsFormContentController/queryExExplainToDsFormContentMationToShow")
	@ResponseBody
	public void queryExExplainToDsFormContentMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToDsFormContentService.queryExExplainToDsFormContentMationToShow(inputObject, outputObject);
	}
	
}

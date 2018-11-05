package com.skyeye.dsform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.dsform.service.DsFormContentService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class DsFormContentController {
	
	@Autowired
	private DsFormContentService dsFormContentService;
	
	/**
	 * 
	     * @Title: queryDsFormContentList
	     * @Description: 获取动态表单内容列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormContentController/queryDsFormContentList")
	@ResponseBody
	public void queryDsFormContentList(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormContentService.queryDsFormContentList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDsFormContentMation
	     * @Description: 添加动态表单内容信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormContentController/insertDsFormContentMation")
	@ResponseBody
	public void insertDsFormContentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormContentService.insertDsFormContentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteDsFormContentMationById
	     * @Description: 删除动态表单内容信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormContentController/deleteDsFormContentMationById")
	@ResponseBody
	public void deleteDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormContentService.deleteDsFormContentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDsFormContentMationToEditById
	     * @Description: 编辑动态表单内容信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormContentController/queryDsFormContentMationToEditById")
	@ResponseBody
	public void queryDsFormContentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormContentService.queryDsFormContentMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormContentMationById
	     * @Description: 编辑动态表单内容信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormContentController/editDsFormContentMationById")
	@ResponseBody
	public void editDsFormContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormContentService.editDsFormContentMationById(inputObject, outputObject);
	}
	
}

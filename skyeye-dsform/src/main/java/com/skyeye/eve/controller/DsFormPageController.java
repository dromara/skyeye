/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.DsFormPageService;

@Controller
public class DsFormPageController {

	@Autowired
	private DsFormPageService dsFormPageService;
	
	/**
	 * 
	     * @Title: queryDsFormPageList
	     * @Description: 获取动态表单页面表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/queryDsFormPageList")
	@ResponseBody
	public void queryDsFormPageList(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.queryDsFormPageList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertDsFormPageMation
	     * @Description: 新增动态表单页面
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/insertDsFormPageMation")
	@ResponseBody
	public void insertDsFormPageMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.insertDsFormPageMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertDsFormPageContent
	     * @Description: 新增控件到表单页面
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/insertDsFormPageContent")
	@ResponseBody
	public void insertDsFormPageContent(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.insertDsFormPageContent(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectFormPageContentByPageId
	     * @Description: 查看某个动态表单中的表单控件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/selectFormPageContentByPageId")
	@ResponseBody
	public void selectFormPageContentByPageId(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.selectFormPageContentByPageId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteDsFormPageById
	     * @Description: 删除动态表单页面
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/deleteDsFormPageById")
	@ResponseBody
	public void deleteDsFormPageById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.deleteDsFormPageById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: selectDsFormPageById
	     * @Description: 通过id查找对应的动态表单
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/selectDsFormPageById")
	@ResponseBody
	public void selectDsFormPageById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.selectDsFormPageById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormPageMationById
	     * @Description: 通过id编辑对应的动态表单
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/editDsFormPageMationById")
	@ResponseBody
	public void editDsFormPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.editDsFormPageMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllDsFormPageContent
	     * @Description: 获取表单内所有控件用于编辑调整
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/queryAllDsFormPageContent")
	@ResponseBody
	public void queryAllDsFormPageContent(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.queryAllDsFormPageContent(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormPageContentByPageId
	     * @Description: 编辑表单内容中的控件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/editDsFormPageContentByPageId")
	@ResponseBody
	public void editDsFormPageContentByPageId(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.editDsFormPageContentByPageId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryInterfaceIsTrueOrNot
	     * @Description: 验证接口是否正确
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/queryInterfaceIsTrueOrNot")
	@ResponseBody
	public void queryInterfaceIsTrueOrNot(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.queryInterfaceIsTrueOrNot(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryInterfaceValue
	     * @Description: 获取接口中的值
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/queryInterfaceValue")
	@ResponseBody
	public void queryInterfaceValue(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.queryInterfaceValue(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDsFormContentListByPageId
	     * @Description: 获取redis中的动态表单页
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/DsFormPageController/queryDsFormContentListByPageId")
	@ResponseBody
	public void queryDsFormContentListByPageId(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.queryDsFormContentListByPageId(inputObject, outputObject);
	}

	/**
	 * 根据code获取动态表单信息
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@RequestMapping("/post/DsFormPageController/queryDsFormContentListByCode")
	@ResponseBody
	public void queryDsFormContentListByCode(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.queryDsFormContentListByCode(inputObject, outputObject);
	}

	/**
	 * 保存动态表单信息
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@RequestMapping("/post/DsFormPageController/saveDsFormDataList")
	@ResponseBody
	public void saveDsFormDataList(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.saveDsFormDataList(inputObject, outputObject);
	}

	/**
	 * 根据objectId获取动态表单信息
	 *
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception
	 */
	@RequestMapping("/post/DsFormPageController/queryDsFormDataListByObjectId")
	@ResponseBody
	public void queryDsFormDataListByObjectId(InputObject inputObject, OutputObject outputObject) throws Exception{
		dsFormPageService.queryDsFormDataListByObjectId(inputObject, outputObject);
	}

}

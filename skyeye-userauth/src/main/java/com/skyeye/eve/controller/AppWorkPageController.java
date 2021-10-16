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
import com.skyeye.eve.service.AppWorkPageService;

@Controller
public class AppWorkPageController {

	@Autowired
	private AppWorkPageService appWorkPageService;
	
	/**
	 * 
	     * @Title: queryAppWorkPageList
	     * @Description: 获取手机端菜单目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/queryAppWorkPageList")
	@ResponseBody
	public void queryLightAppList(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.queryAppWorkPageList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertAppWorkPageMation
	     * @Description: 新增手机端菜单目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/insertAppWorkPageMation")
	@ResponseBody
	public void insertAppWorkPageMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.insertAppWorkPageMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAppWorkPageListById
	     * @Description: 根据目录id获取菜单列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/queryAppWorkPageListById")
	@ResponseBody
	public void queryAppWorkPageListById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.queryAppWorkPageListById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertAppWorkPageMationById
	     * @Description: 新增手机端菜单
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/insertAppWorkPageMationById")
	@ResponseBody
	public void insertAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.insertAppWorkPageMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAppWorkPageMationById
	     * @Description: 获取菜单信息进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/queryAppWorkPageMationById")
	@ResponseBody
	public void queryAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.queryAppWorkPageMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageMationById
	     * @Description: 保存编辑后的菜单信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkPageMationById")
	@ResponseBody
	public void editAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkPageMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteAppWorkPageMationById
	     * @Description: 删除菜单
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/deleteAppWorkPageMationById")
	@ResponseBody
	public void deleteAppWorkPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.deleteAppWorkPageMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageSortTopById
	     * @Description: 菜单上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkPageSortTopById")
	@ResponseBody
	public void editAppWorkPageSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkPageSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageSortLowerById
	     * @Description: 菜单下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkPageSortLowerById")
	@ResponseBody
	public void editAppWorkPageSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkPageSortLowerById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageUpById
	     * @Description: 菜单上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkPageUpById")
	@ResponseBody
	public void editAppWorkPageUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkPageUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageDownById
	     * @Description: 菜单下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkPageDownById")
	@ResponseBody
	public void editAppWorkPageDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkPageDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageTitleById
	     * @Description: 编辑菜单目录名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkPageTitleById")
	@ResponseBody
	public void editAppWorkPageTitleById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkPageTitleById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteAppWorkPageById
	     * @Description: 删除菜单目录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/deleteAppWorkPageById")
	@ResponseBody
	public void deleteAppWorkPageById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.deleteAppWorkPageById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkUpById
	     * @Description: 目录上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkUpById")
	@ResponseBody
	public void editAppWorkUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkDownById
	     * @Description: 目录下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkDownById")
	@ResponseBody
	public void editAppWorkDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkSortTopById
	     * @Description: 目录上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkSortTopById")
	@ResponseBody
	public void editAppWorkSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkSortLowerById
	     * @Description: 目录下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageController/editAppWorkSortLowerById")
	@ResponseBody
	public void editAppWorkSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageService.editAppWorkSortLowerById(inputObject, outputObject);
	}
	
}

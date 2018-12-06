package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.RmGroupService;

@Controller
public class RmGroupController {
	
	@Autowired
	private RmGroupService rmGroupService;
	
	/**
	 * 
	     * @Title: queryRmGroupList
	     * @Description: 获取小程序分组列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupController/queryRmGroupList")
	@ResponseBody
	public void queryRmGroupList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupService.queryRmGroupList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertRmGroupMation
	     * @Description: 添加小程序分组
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupController/insertRmGroupMation")
	@ResponseBody
	public void insertRmGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupService.insertRmGroupMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteRmGroupById
	     * @Description: 删除小程序分组信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupController/deleteRmGroupById")
	@ResponseBody
	public void deleteRmGroupById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupService.deleteRmGroupById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmGroupMationToEditById
	     * @Description: 编辑小程序分组信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupController/queryRmGroupMationToEditById")
	@ResponseBody
	public void queryRmGroupMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupService.queryRmGroupMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmGroupMationById
	     * @Description: 编辑小程序分组信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupController/editRmGroupMationById")
	@ResponseBody
	public void editRmGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupService.editRmGroupMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmGroupSortTopById
	     * @Description: 小程序分组展示顺序上移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupController/editRmGroupSortTopById")
	@ResponseBody
	public void editRmGroupSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupService.editRmGroupSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmGroupSortLowerById
	     * @Description: 小程序分组展示顺序下移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupController/editRmGroupSortLowerById")
	@ResponseBody
	public void editRmGroupSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupService.editRmGroupSortLowerById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmGroupAllList
	     * @Description: 获取所有小程序分组根据小程序分类ID
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupController/queryRmGroupAllList")
	@ResponseBody
	public void queryRmGroupAllList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupService.queryRmGroupAllList(inputObject, outputObject);
	}
	
}

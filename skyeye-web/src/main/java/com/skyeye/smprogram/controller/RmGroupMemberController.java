package com.skyeye.smprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.smprogram.service.RmGroupMemberService;

@Controller
public class RmGroupMemberController {
	
	@Autowired
	private RmGroupMemberService rmGroupMemberService;
	
	/**
	 * 
	     * @Title: queryRmGroupMemberList
	     * @Description: 获取小程序组件列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupMemberController/queryRmGroupMemberList")
	@ResponseBody
	public void queryRmGroupMemberList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupMemberService.queryRmGroupMemberList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertRmGroupMemberMation
	     * @Description: 添加小程序组件
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupMemberController/insertRmGroupMemberMation")
	@ResponseBody
	public void insertRmGroupMemberMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupMemberService.insertRmGroupMemberMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmGroupMemberSortTopById
	     * @Description: 小程序组件展示顺序上移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupMemberController/editRmGroupMemberSortTopById")
	@ResponseBody
	public void editRmGroupMemberSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupMemberService.editRmGroupMemberSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmGroupMemberSortLowerById
	     * @Description: 小程序组件展示顺序下移
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupMemberController/editRmGroupMemberSortLowerById")
	@ResponseBody
	public void editRmGroupMemberSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupMemberService.editRmGroupMemberSortLowerById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteRmGroupMemberById
	     * @Description: 删除小程序组件信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupMemberController/deleteRmGroupMemberById")
	@ResponseBody
	public void deleteRmGroupMemberById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupMemberService.deleteRmGroupMemberById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmGroupMemberMationToEditById
	     * @Description: 编辑小程序组件信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupMemberController/queryRmGroupMemberMationToEditById")
	@ResponseBody
	public void queryRmGroupMemberMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupMemberService.queryRmGroupMemberMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmGroupMemberMationById
	     * @Description: 编辑小程序组件信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupMemberController/editRmGroupMemberMationById")
	@ResponseBody
	public void editRmGroupMemberMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupMemberService.editRmGroupMemberMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmGroupMemberAndPropertyMationById
	     * @Description: 编辑小程序组件和标签属性的绑定信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmGroupMemberController/editRmGroupMemberAndPropertyMationById")
	@ResponseBody
	public void editRmGroupMemberAndPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmGroupMemberService.editRmGroupMemberAndPropertyMationById(inputObject, outputObject);
	}
	
}

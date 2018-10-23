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
	
}

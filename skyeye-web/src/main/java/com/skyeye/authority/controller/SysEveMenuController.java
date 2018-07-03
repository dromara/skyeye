package com.skyeye.authority.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.authority.service.SysEveMenuService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class SysEveMenuController {
	
	@Autowired
	private SysEveMenuService sysEveMenuService;
	
	/**
	 * 
	     * @Title: querySysMenuList
	     * @Description: 获取菜单列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveMenuController/querySysMenuList")
	@ResponseBody
	public void querySysMenuList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveMenuService.querySysMenuList(inputObject, outputObject);
	}
	
}

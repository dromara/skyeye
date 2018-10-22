package com.skyeye.smprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.smprogram.service.RmTypeService;

@Controller
public class RmTypeController {
	
	@Autowired
	private RmTypeService rmTypeService;
	
	/**
	 * 
	     * @Title: querySysMenuList
	     * @Description: 获取小程序分类列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmTypeController/queryRmTypeList")
	@ResponseBody
	public void queryRmTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmTypeService.queryRmTypeList(inputObject, outputObject);
	}
	
}

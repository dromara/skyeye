package com.skyeye.authority.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.authority.service.SysEveIconService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class SysEveIconController {
	
	@Autowired
	private SysEveIconService sysEveIconService;
	
	/**
	 * 
	     * @Title: querySysIconList
	     * @Description: 获取ICON列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveIconController/querySysIconList")
	@ResponseBody
	public void querySysIconList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveIconService.querySysIconList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysIconMation
	     * @Description: 添加ICON信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveIconController/insertSysIconMation")
	@ResponseBody
	public void insertSysIconMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveIconService.insertSysIconMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysIconMationById
	     * @Description: 删除ICON信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveIconController/deleteSysIconMationById")
	@ResponseBody
	public void deleteSysIconMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveIconService.deleteSysIconMationById(inputObject, outputObject);
	}
	
}
